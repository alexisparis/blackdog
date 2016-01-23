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
package org.siberia.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.siberia.xml.schema.properties.CategoryType;
import org.siberia.xml.schema.properties.PropertyContainer;
import org.siberia.xml.schema.properties.PropertyType;

/**
 *
 * Wrapper for xml Category element.
 * supports PropertyChangeListener and all functionnalities of SibType.
 *
 * @author alexis
 */
public class XmlCategory extends XmlPropertyContainer
{   
    /** logger */
    private Logger                         logger        = Logger.getLogger(XmlCategory.class);
    
    /** reference to the CategoryType */
    private CategoryType                   innerCategory = null;
    
    /** list of owned items */
    private List<XmlPropertyContainer> items         = null;
    
    /** Creates a new instance of ColdXmlCategory
     *  @param xmlCategory a CategoryType node
     */
    public XmlCategory(CategoryType xmlCategory)
    {   super();
        
        this.innerCategory = xmlCategory;
        
        /* feed the lists of sub categories and of direct properties */
        if ( this.innerCategory != null )
        {   if ( this.innerCategory.getPropertyAndCategory() != null )
            {   Iterator it = this.innerCategory.getPropertyAndCategory().iterator();
                while(it.hasNext())
                {   Object current = it.next();
                    
                    if ( current != null )
                    {   if ( this.items == null )
                                this.items = new ArrayList<XmlPropertyContainer>();
                            
                        
                        if ( current instanceof CategoryType )
                        {   XmlCategory category = new XmlCategory( (CategoryType)current );
                            category.setParent(this);
                            this.items.add( category );
                        }
                        else if ( current instanceof PropertyType )
                        {   XmlProperty property = new XmlProperty( (PropertyType)current );
                            property.setParent(this);
                            this.items.add( property );
                        }
                        else
                        {   logger.warn("element from " + current.getClass() + " could not be added to a ColdXmlProperty."); }
                    }
                }
            }
        }
    }
    
    /** return the categoryType node
     *  @return a categoryType
     */
    public CategoryType getInnerCategory()
    {   return this.innerCategory; }
    
    /** return an instance of PropertyContainer
     *  @return an instance of PropertyContainer
     */
    public PropertyContainer getPropertyContainer()
    {   return this.getInnerCategory(); }

    /** return a list of items
     *  @return a list of items
     */
    public List<XmlPropertyContainer> getItems()
    {   List<XmlPropertyContainer> list = this.items;
        if ( list == null )
            list = Collections.EMPTY_LIST;
        return list;
    }
    
    /** return a list over all properties contained by this category and by its sub categories
     *  @return a list over ColdXmlProperty
     *
     *  A REVOIR
     */
    public List<XmlProperty> recursiveProperties()
    {   List<XmlProperty> list = null;
        if (  this.items != null)
        {   Iterator<XmlPropertyContainer> it = this.items.iterator();
            while(it.hasNext())
            {   XmlPropertyContainer current = it.next();
                if ( list == null )
                    list = new ArrayList<XmlProperty>();
                if ( current instanceof XmlProperty )
                {   list.add( (XmlProperty)current ); }
                else if ( current instanceof XmlCategory )
                {   list.addAll( ((XmlCategory)current).recursiveProperties() ); }
            }
        }
        if ( list == null )
            list = Collections.EMPTY_LIST;
        return list;
    }
    
    /** method which allow to restore the old value for all properties */
    public void restoreOldValues()
    {
        if ( this.items != null )
        {   Iterator<XmlPropertyContainer> it = this.items.iterator();
            while(it.hasNext())
            {   XmlPropertyContainer current = it.next();
                
                if ( current != null )
                {   current.restoreOldValues(); }
            }
        }
    }
    
    /** method which indicate that all values are confirmed */
    public void confirmValues()
    {
        if ( this.items != null )
        {   Iterator<XmlPropertyContainer> it = this.items.iterator();
            while(it.hasNext())
            {   XmlPropertyContainer current = it.next();
                
                if ( current != null )
                {   current.confirmValues(); }
            }
        }
    }
}
