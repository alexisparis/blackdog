/* 
 * Siberia properties : siberia plugin defining system properties
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.properties.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import bsh.EvalError;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.siberia.xml.JAXBPropertiesLoader;
import org.siberia.xml.schema.properties.CategoryType;
import org.siberia.xml.schema.properties.DependsOnType;
import org.siberia.xml.schema.properties.ObjectFactory;
import org.siberia.xml.schema.properties.Properties;
import org.siberia.xml.schema.properties.PropertyContainer;
import org.siberia.xml.schema.properties.PropertyType;
import org.siberia.xml.schema.properties.ExtendedRangeType;
import org.siberia.xml.schema.properties.RefClassType;
import org.siberia.xml.schema.properties.RangeType;
import org.siberia.xml.schema.properties.Values;

/**
 *
 * Object that is able to create a single xml Properties from several Properties.
 *
 * This object is able to udpdate existing properties by calling update method.
 *
 * @author alexis
 */
public class PropertiesMerger
{
    /** logger */
    private static final Logger logger = Logger.getLogger(PropertiesMerger.class);
    
    /** list of properties to merge */
    private List<Properties>                 properties              = null;

    /** ordered elements comparator */
    private Comparator                       comparator              = null;

    /** current id */
    private int                              currentId               = 1;

    /** map between property path and List of dependsOn related to this path */
    private Map<String, List<DependsOnType>> storedDependsOn         = null;
    
    /** map used when update mode : it link property representation and value */
    private Map<String, String>              currentValues           = null;
    
    /** indicates if the merge of attribute is activated */
    private boolean                          mergeAttributeActivated = true;
    
    /** bsh interpreter */
    private bsh.Interpreter                  interpreter             = null;

    /** create a new PropertiesMerger */
    public PropertiesMerger()
    {   this(null);
        
        this.mergeAttributeActivated = false;
    }

    /** create a new PropertiesMerger
     *  @param set a set of xml properties
     */
    public PropertiesMerger(Set<Properties> set)
    {   if ( set != null )
        {   this.properties = new ArrayList<Properties>(set);
                    
            /* sort the properties according to their priority */
            Collections.sort(this.properties, new Comparator()
            {
                public int compare(Object o1, Object o2)
                {   int result = 0;
                    if ( o1 instanceof Properties )
                    {   if ( o2 instanceof Properties )
                        {   Properties properties1 = (Properties)o1;
                            Properties properties2 = (Properties)o2;

                            result = properties1.getPriority() - properties2.getPriority();
                        }
                        else
                        {   result = 1; }
                    }
                    else
                    {   if ( o2 instanceof Properties )
                        {   result = -1; }
                    }

                    return result;
                }
            });
        }
        else
        {   this.properties = Collections.EMPTY_LIST; }
    }

    /** return the resulting merged properties xml structure
     *  @return the resulting merged properties xml structure
     */
    public Properties merge()
    {   
        Properties current = null;
        if ( properties != null )
        {   
            if ( properties.size() == 1 )
            {   current = properties.get(0); }
            else if ( properties.size() > 1 )
            {   /* start with the first last two ones and go back */
                current = this.properties.get(properties.size() - 1);

                for(int i = properties.size() - 2; i >= 0; i--)
                {   current = this.merge(current, properties.get(i)); }
            }
        }

        if ( current != null )
            this.sortProperties(current);

        /* check validity and provide messages to correct structure
         * should probably be called on every Properties before merging
         */

        // not very efficient but additional functionalities will be easier
        // besides, this merge is to be done one time at the start of the platform ( for the main property file )

        /* assign id only */
        this.traverse(current, null, "", false, true, false, false, false, false);

        /* ids are assignated --> store dependsOn */
        this.traverse(current, null, "", false, false, false, true, false, false);

        /* complete dependsOn */
        this.traverse(current, null, "", false, false, false, false, true, false);

        /* display the alone dependsOn */
        if ( this.storedDependsOn != null )
        {   Iterator<String> it = this.storedDependsOn.keySet().iterator();
            while(it.hasNext())
            {   String path = it.next();
                int count = 0;

                List<DependsOnType> list = this.storedDependsOn.get(path);
                if ( list != null )
                {   if ( list.size() > 0 )
                    {   Iterator<DependsOnType> it2 = list.iterator();
                        while(it2.hasNext())
                        {   DependsOnType d = it2.next();
                            if ( d != null )
                            {   count ++; }
                        }
                    }
                }

                if ( count > 0 )
                    logger.error(count + " DependsOn for path " + path + " could not be completed");
            }
        }

        /* translate entities */
        this.traverse(current, null, "", false, false, false, false, false, true);

        /* post process entities */
        this.traverse(current, null, "", false, false, true, false, false, false);

        /* check */
        this.traverse(current, null, "", true, false, false, false, false, false);

        return current;
    }

    /** return the resulting merged properties
     *  @param master the master properties
     *  @param servant the servant properties
     *  @return a Properties or null if there was nothing to merge
     */
    private Properties merge(Properties master, Properties servant)
    {   if ( master == null )
        {   if ( servant == null )
                return null;
            else
                return servant;
        }
        else if ( servant == null )
            return master;

        List masterItems = master.getCategory();
        List servantItems = servant.getCategory();

        this.merge(masterItems, servantItems);

        return master;
    }

    /** merge list of elements (Category or Property)
     *  @param master the master list of element containing Category or Property
     *  @param servant the servant list of element containing Category or Property
     */
    private void merge(List master, List servant)
    {   if ( master != null )
        {
            if ( servant != null )
            {
                /* merge two non null menubars fill the structure of master with servant elements */
                for(int i = 0; i < servant.size(); i++)
                {   Object current = servant.get(i);
                    if ( current instanceof RefClassType )
                    {   RefClassType refclass = (RefClassType)current;
                        if ( refclass != null )
                        {   /* search for a category name category.getRepr in the master direct categories */
                            boolean merged = false;
                            for(int j = 0; j < master.size(); j++)
                            {   Object c = master.get(j);
                                if ( c instanceof RefClassType )
                                {   RefClassType ref = (RefClassType)c;
                                    if ( ref != null )
                                    {   if ( ref.getRepr().equals(refclass.getRepr()) && ref.getClass() == refclass.getClass() )
                                        {   this.merge(ref, refclass);
                                            merged = true;
                                            break;
                                        }
                                    }
                                }
                            }

                            if ( ! merged )
                            {   /* add the current servant category to the list of items of the master */
                                master.add(refclass);
                            }
                        }
                    }
                }
            }
        }
    }

    /** merge two categories
     *  @param master the master RefClassType 
     *  @param servant the servant RefClassType 
     */
    private void merge(RefClassType master, RefClassType servant)
    {   
        if ( master != null && servant != null )
        {   
            if ( master instanceof CategoryType && servant instanceof CategoryType )
                this.merge((CategoryType)master, (CategoryType)servant);
            else if ( master instanceof PropertyType && servant instanceof PropertyType )
                this.merge((PropertyType)master, (PropertyType)servant);
        }
    }

    /** merge attributes of two PropertyContainer */
    private void mergeAttributes(PropertyContainer o1, PropertyContainer o2)
    {   if ( o1 != null && o2 != null && this.mergeAttributeActivated )
        {   if ( o1.getIcon() == null )
                o1.setIcon(o2.getIcon());
            if ( o1.getLabel() == null )
                o1.setLabel(o2.getLabel());
            if ( o1.getDescription() == null )
                o1.setDescription(o2.getDescription());
        }

    }

    /** merge two categories
     *  @param master the master Category 
     *  @param servant the servant Category 
     */
    private void merge(CategoryType master, CategoryType servant)
    {   if ( master != null && servant != null )
        {   this.merge(master.getPropertyAndCategory(), servant.getPropertyAndCategory());

            this.mergeAttributes(master, servant);
        }
    }

    /** merge two properties
     *  @param master the master Property
     *  @param servant the servant Property
     */
    private void merge(PropertyType master, PropertyType servant)
    {   if ( master != null && servant != null )
        {   /* merge attributes */
            this.mergeAttributes(master, servant);
        }
    }

    /** sort the given list recursively
     *  @param item an object representing a Properties, Category or Property
     */
    private void sortProperties(Object item)
    {   if ( item != null )
        {   if ( item instanceof List )
            {
                /* sort sublist */
                Iterator it = ((List)item).iterator();
                while(it.hasNext())
                {   Object current = it.next();
                    {   this.sortProperties( current ); }
                }
                if ( this.comparator == null )
                    this.comparator = new PropertyContainerComparator();
                Collections.sort((List)item, this.comparator);
            }
            else if( item instanceof Properties )
            {   this.sortProperties( ((Properties)item).getCategory() ); }
            else if( item instanceof CategoryType )
            {   this.sortProperties( ((CategoryType)item).getPropertyAndCategory() ); }
        }
    }

    /** traverses the structure recursively
     *  @param item the current item
     *  @param parentItem the parent of the current item
     *  @param path the current path of the parent
     *  @param check true if check must be called on every element
     *  @param assignId true if assignId must be called on every element
     *  @param postProcess true if check must be called on every element
     *  @param storeDependsOn true if dependsOn have to be stored
     *  @param completeDependsOn true if dependsOn have to be completed by id
     *  @param translateClassProperty indicates if references of class properties should be translated
     */
    private void traverse(Object item, Object parentItem, String path, boolean check, boolean assignId, boolean postProcess,
                                                    boolean storeDependsOn, boolean completeDependsOn,
                                                    boolean translateClassProperty)
    {   this.traverse(item, parentItem, path, check, assignId, postProcess,
                      storeDependsOn, completeDependsOn,
                      translateClassProperty, false, false, false, false);
    }

    /** traverses the structure recursively
     *  @param item the current item
     *  @param parentItem the parent of the current item
     *  @param path the current path of the parent
     *  @param check true if check must be called on every element
     *  @param assignId true if assignId must be called on every element
     *  @param postProcess true if check must be called on every element
     *  @param storeDependsOn true if dependsOn have to be stored
     *  @param completeDependsOn true if dependsOn have to be completed by id
     *  @param translateClassProperty indicates if references of class properties should be translated
     *  @param storeCurrentValue true to store the representation and value of property
     *  @param considerManual true to consider the manual property when storing values
     *  @param deleteNonManualProperty true to remove non manual properties from the item
     *  @param updateValueWithCurrentValues indicates if value of property have to be updated with those value contained
     *          by currentValues map
     */
    private void traverse(Object item, Object parentItem, String path, boolean check, boolean assignId, boolean postProcess,
                                                    boolean storeDependsOn, boolean completeDependsOn,
                                                    boolean translateClassProperty, boolean storeCurrentValue,
                                                    boolean considerManual, boolean deleteNonManualProperty,
                                                    boolean updateValueWithCurrentValues)
    {   if ( item != null )
        {   
            if ( check )
                this.checkValidity(item);
            if ( assignId )
                this.assignId(item);
            if ( postProcess )
                this.postProcess(item);
            if ( storeDependsOn )
                this.storeDependsOn(item);
            if ( translateClassProperty )
                this.translate(item);

            if ( item instanceof Properties )
            {   Properties properties = (Properties)item;

                List<CategoryType> items = properties.getCategory();
                if ( items != null )
                {   for(int i = 0; i < items.size(); i++)
                    {   CategoryType category = items.get(i);
                        this.traverse(category, item, "/", check, assignId, postProcess, storeDependsOn,
                                      completeDependsOn, translateClassProperty, storeCurrentValue,
                                      considerManual, deleteNonManualProperty, updateValueWithCurrentValues);
                        
                        if ( deleteNonManualProperty )
                        {   if ( category.getPropertyAndCategory().size() == 0 )
                            {
                                if ( properties.getCategory().remove(category) )
                                {   i--; }
                            }
                        }
                    }
                }
            }
            if ( item instanceof CategoryType )
            {   CategoryType category = (CategoryType)item;
                
                List<PropertyContainer> items = category.getPropertyAndCategory();
                if ( items != null )
                {   for(int i = 0; i < items.size(); i++)
                    {   PropertyContainer currentContainer = items.get(i);
                        
                        boolean removed = false;
                        
                        if ( ! removed )
                        {   this.traverse(currentContainer, item, path + category.getRepr() + "/", check, assignId, postProcess, 
                                      storeDependsOn, completeDependsOn, translateClassProperty, storeCurrentValue,
                                      considerManual, deleteNonManualProperty, updateValueWithCurrentValues);
                        }
                        
                        if ( currentContainer instanceof CategoryType )
                        {   CategoryType subCategory = (CategoryType)currentContainer;
                            if ( deleteNonManualProperty && subCategory.getPropertyAndCategory().size() == 0 )
                            {   removed = category.getPropertyAndCategory().remove(currentContainer); }
                        }
                        else if ( currentContainer instanceof PropertyType )
                        {   PropertyType property = (PropertyType)currentContainer;
                            if ( deleteNonManualProperty && ! property.isManual() )
                            {   removed = category.getPropertyAndCategory().remove(currentContainer); }
                        }
                        
                        if ( removed )
                        {   i--; }
                    }
                }
            }
            if ( item instanceof PropertyType )
            {   PropertyType property = (PropertyType)item;

                if ( completeDependsOn )
                    this.completeDependsOn(property, path + property.getRepr());

                List dependsList = property.getDependsOn();
                if ( dependsList != null )
                {   Iterator it = dependsList.iterator();
                    while(it.hasNext())
                        this.traverse(it.next(), item, null, check, assignId, postProcess, storeDependsOn, completeDependsOn,
                                      translateClassProperty);
                }

                /** store the value if necessary */
                boolean storeValue = storeCurrentValue;
                if ( storeValue )
                {   if ( property.isManual() && ! considerManual )
                        storeValue = false;
                }

                if ( storeValue )
                {   if ( this.currentValues == null )
                    {   this.currentValues = new HashMap<String, String>(); }

                    this.currentValues.put(property.getRepr(), property.getAppliedValue().getValue());
                }

                if ( updateValueWithCurrentValues && this.currentValues != null )
                {   String value = this.currentValues.get(property.getRepr());

                    if ( value != null )
                    {   /* should test if the value we try to apply is valid */
                        if ( PropertyValueValidator.isValid(value, property) )
                        {   property.getAppliedValue().setValue(value); }
                        else
                        {   logger.warn("Impossible to set old value '" + value + "' to property='" + 
                                        property.getRepr() + "' because it is invalid");
                        }
                    }
                }

                this.traverse(property.getPattern(), item, null, check, assignId, postProcess, false, false,
                              translateClassProperty);
                this.traverse(property.getRange(), item, null, check, assignId, postProcess, false, false,
                              translateClassProperty);
                this.traverse(property.getValues(), item, null, check, assignId, postProcess, false, false,
                              translateClassProperty);
            }
            if ( item instanceof Values )
            {   Values values = (Values)item;

                List v = values.getValue();
                if ( v != null )
                {   Iterator it = v.iterator();
                    while(it.hasNext())
                        this.traverse(it.next(), item, null, check, assignId, postProcess, false, false,
                                      translateClassProperty);
                }
            }
            if ( item instanceof RangeType )
            {   RangeType range = (RangeType)item;

                this.traverse(range.getMaximum(), item, null, check, assignId, postProcess, false, false,
                              translateClassProperty);
                this.traverse(range.getMinimum(), item, null, check, assignId, postProcess, false, false,
                              translateClassProperty);

                if ( item instanceof ExtendedRangeType )
                {   ExtendedRangeType cRange = (ExtendedRangeType)item;

                    List v = cRange.getExcluded();
                    if ( v != null )
                    {   Iterator it = v.iterator();
                        while(it.hasNext())
                            this.traverse(it.next(), item, null, check, assignId, postProcess, false, false,
                                          translateClassProperty);
                    }
                }
            }
        }
    }

    /** assign an id to the element
     *  @param item an Object
     */
    private void assignId(Object item)
    {   if ( item instanceof PropertyType )
        {   ((PropertyType)item).setId(this.currentId ++); }
    }

    /** translate the element
     *  @param item an Object
     */
    private void translate(Object item)
    {   if ( item instanceof RefClassType )
        {   RefClassType type = (RefClassType)item;

            if ( type.isRefClass() )
            {   type.setRefClass(false);

                /* use reflect to get the property */
                String translated = this.translateString(type.getRepr());
                if ( translated != null )
                    type.setRepr(translated);
                else
                    this.translate(type);
            }
//            else
//            {   if ( type.getRepr().indexOf('.') != -1 )
//                {   type.setRepr("{" + type.getRepr() + "}"); }
//            }
        }
        if ( item instanceof PropertyContainer )
        {   PropertyContainer p = (PropertyContainer)item;

            String s = this.translateString(p.getLabel());
            if ( s != null )
                p.setLabel(s);
            s = this.translateString(p.getIcon());
            if ( s != null )
                p.setIcon(this.translateString(p.getIcon()));
            s = this.translateString(p.getDescription());
            if ( s != null )
                p.setDescription(this.translateString(p.getDescription()));
        }
    }
    
    /** return the interpreter linked to this merger
     *  @return an interpreter
     */
    private synchronized bsh.Interpreter getInterpreter()
    {
        if ( this.interpreter == null )
        {
            this.interpreter = new bsh.Interpreter();
        }
        
        return this.interpreter;
    }

    /** return the result of translation of the given string
     *  @param initial a String
     *  @return the translated String
     */
    private String translateString(String initial)
    {   String result = null;
        if ( initial != null )
        {
            if ( initial.length() > 0 )
            {   try
                {   Object r = this.getInterpreter().eval(initial);
                    if ( r != null )
                        return r.toString();
                }
                catch(EvalError e)
                {   /* do nothing */
                    logger.debug("unable to eval the string " + initial);
                }
            }
            return initial;
        }
        return result;
    }

    /** store dependsOn
     *  @param item an Object
     */
    private void storeDependsOn(Object item)
    {   if ( item instanceof DependsOnType )
        {   DependsOnType depend = (DependsOnType)item;
            if ( this.storedDependsOn == null )
                this.storedDependsOn=  new HashMap<String, List<DependsOnType>>();
            List<DependsOnType> list = this.storedDependsOn.get(depend.getRepr());

            if ( list == null )
            {   list = new ArrayList<DependsOnType>();
                this.storedDependsOn.put(depend.getRepr(), list);
            }
            list.add(depend);
        }
    }

    /** complete dependsOn by an id
     *  @param item an Object
     *  @param path of the object
     */
    private void completeDependsOn(Object item, String path)
    {   if ( item instanceof PropertyType )
        {   PropertyType property = (PropertyType)item;
            if ( this.storedDependsOn != null )
            {   List<DependsOnType> list = this.storedDependsOn.get(path);
                if ( list != null )
                {   Iterator<DependsOnType> it = list.iterator();
                    while(it.hasNext())
                    {   it.next().setRepr(String.valueOf(property.getId())); }
                }
                this.storedDependsOn.remove(path);
            }
        }
    }

    /** post process element
     *  @param item an Object
     */
    private void postProcess(Object item)
    {   if ( item instanceof PropertyType )
        {   /* create the value */
            PropertyType property = (PropertyType)item;
            if ( property.getAppliedValue() == null )
            {   property.setAppliedValue(new ObjectFactory().createPropertyTypeAppliedValue());
                property.getAppliedValue().setValue(property.getDefault());
            }
        }
    }

    /** provides messages about the validity of the given element
     *  @param item an Object
     */
    private void checkValidity(Object item)
    {   if ( item != null )
        {   
            if ( item instanceof RefClassType )
            {   RefClassType type = (RefClassType)item;
                if ( ! type.isRefClass() )
                {   if ( type.getRepr() != null )
                    {   if ( type.getRepr().indexOf('{') != -1 || type.getRepr().indexOf('}') != -1 )
                        {   logger.error("RefClassType " + type.getRepr() + " of type " + type.getClass() +
                                         " does not have flag 'refClass'='true' --> its Repr cannot contains '{' or '}'.");
                        }
                    }
                    else
                    {   logger.error("element " + item.getClass() + " " + type + " must specify a Repr."); }
                }
            }
        }
    }

    /** return the resulting merged properties xml structure.<br>
     *  WARNING : the two properties requiredProperties & oldProperties could be modified by this method.
     *
     *  @param requiredProperties the Properties that declared all properties needed
     *  @param oldProperties an existing Properties that could miss some items
     *  @return the resulting merged properties xml structure
     */
    public Properties update(Properties requiredProperties, Properties oldProperties)
    {	Properties result = null;
        if ( oldProperties != null )
        {   if ( requiredProperties == null )
            {   result = oldProperties; }
            else
            {   /* real update
                 * we have to modify requiredProperties with the value of oldProperties
                 * and we have to add manual properties in oldProperties into requiredProperties
                 *
                 * first, we traverse the oldProperties, we put non manual property into currentValues map
                 * and we delete all properties that are non manual.
                 * At the end of this procedure, oldProperties should only contains manual properties, its categories
                 * that did not contain any manual property will be relmoved
                 */
                this.traverse(oldProperties, null, null, false, false, false, false,
                              false, false, true, false, true, false);
                
                /* now, we have to apply the old value to the Properties requiredProperties */
                this.traverse(requiredProperties, null, null, false, false, false, false,
                              false, false, false, false, false, true);
                
                /* finally, we can merge the two properties */
                Set<Properties> properties = new HashSet<Properties>(2);
                properties.add(requiredProperties);
                properties.add(oldProperties);
                result = new PropertiesMerger(properties).merge();
            }
        }
        else
        {   result = requiredProperties; }
        
        return result;
    }
    
}
