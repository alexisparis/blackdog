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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import org.siberia.parser.ParseException;
import org.siberia.parser.NoParserException;
import org.siberia.parser.Parser;
import org.siberia.properties.action.ApplyAction;
import org.siberia.properties.action.CancelAction;
import org.siberia.properties.action.DependAction;
import org.siberia.properties.action.ValidateAction;
import org.siberia.xml.schema.properties.DependsOnType;
import org.siberia.xml.schema.properties.ObjectFactory;
import org.siberia.xml.schema.properties.PropertyContainer;
import org.siberia.xml.schema.properties.PropertyType;
import org.siberia.xml.schema.properties.PropertyType.AppliedValue;
import org.siberia.ResourceLoader;
import org.siberia.properties.util.PropertiesProvider;
import org.siberia.properties.exception.PropertiesException;
import java.beans.PropertyVetoException;

/**
 *
 * Wrapper for xml Property element.
 * supports PropertyChangeListener and all functionnalities of ColdType.
 *
 * @author alexis
 */
public class XmlProperty extends XmlPropertyContainer implements PropertyChangeListener
{
    /** name of properties */
    public static final String PROP_EDITABILITY  = "editability";
    public static final String PROP_VISILITY     = "visibility";
    public static final String PROP_VALUE        = "value";
    
    /** logger */
    private transient Logger     logger         = Logger.getLogger(XmlProperty.class);
    
    /** xml property node */
    private PropertyType         innerProperty  = null;
    
    /** apply action */
    private ApplyAction          applyAction    = null;
    
    /** depending action */
    private DependAction         dependAction   = null;
    
    /** valide action */
    private ValidateAction       validateAction = null;
    
    /** valide action */
    private CancelAction         cancelAction   = null;
    
    /** value at initialisation */
    private String               initValue      = null;
    
    /** set of depending ColdXmlProperties */
    private Set<XmlProperty>     depends        = null;
    
    /** indicates if the property is visible */
    private boolean              visible        = true;
    
    /** indicates if the property is editable */
    private boolean              editable       = true;
    
    /** old value of the property */
    private String               oldValue       = null;
    
    /** Creates a new instance of ColdXmlProperty
     *  @param xmlProperty a PropertyType node
     */
    public XmlProperty(PropertyType xmlProperty)
    {   super();
        
        if ( xmlProperty == null )
            throw new IllegalArgumentException("Property must not be null");
        
        this.innerProperty = xmlProperty;
        
        if ( xmlProperty != null )
        {   
            /* initialize old value */
            this.oldValue = this.getCurrentValue();
            
            String applyAction = xmlProperty.getApplyAction();
            if ( applyAction != null )
            {   try
                {   Class c          = ResourceLoader.getInstance().getClass(applyAction);
                    Object action = c.newInstance();
                    if ( action instanceof ApplyAction )
                        this.applyAction = (ApplyAction)action;
                    else
                        logger.error("the applyAction tag on property (id=" + xmlProperty.getId() + ") " +
                                            "have to represent a class implementing " + ApplyAction.class + ".");
                }
                catch(Exception e)
                {   logger.error("unable to create an instance from class '" + applyAction + "'"); }
            }
            String dependAction = xmlProperty.getDependAction();
            if ( dependAction != null )
            {   try
                {   Class c          = ResourceLoader.getInstance().getClass(dependAction);
                    Object action = c.newInstance();
                    if ( action instanceof DependAction )
                        this.dependAction = (DependAction)action;
                    else
                        logger.error("the dependAction tag on property (id=" + xmlProperty.getId() + ") " +
                                            "must representing a class implementing " + DependAction.class + ".");
                }
                catch(Exception e)
                {   logger.error("unable to create an instance from class '" + dependAction + "'", e); }
            }
            String validateAction = xmlProperty.getValidateAction();
            if ( validateAction != null )
            {   try
                {   Class c          = ResourceLoader.getInstance().getClass(validateAction);
                    Object action = c.newInstance();
                    if ( action instanceof ValidateAction )
                        this.validateAction = (ValidateAction)action;
                    else
                        logger.error("the validateAction tag on property (id=" + xmlProperty.getId() + ") " +
                                            "have to represent a class implementing " + ValidateAction.class + ".");
                }
                catch(Exception e)
                {   logger.error("unable to create an instance from class '" + validateAction + "'"); }
            }
            String cancelAction = xmlProperty.getCancelAction();
            if ( cancelAction != null )
            {   try
                {   Class c          = ResourceLoader.getInstance().getClass(cancelAction);
                    Object action = c.newInstance();
                    if ( action instanceof CancelAction )
                        this.cancelAction = (CancelAction)action;
                    else
                        logger.error("the cancelAction tag on property (id=" + xmlProperty.getId() + ") " +
                                            "have to represent a class implementing " + CancelAction.class + ".");
                }
                catch(Exception e)
                {   logger.error("unable to create an instance from class '" + cancelAction + "'"); }
            }
        }
    }
    
    /** initialize the id of the object
     *  @param id a long
     */
    public void setId(long id) throws PropertyVetoException
    {   throw new UnsupportedOperationException(); }
    
    /** indicates if the value seems to be changed
     *  @return true if the value of the property has changed
     */
    private boolean isValueModified()
    {
        boolean result = true;
        
        String currentValue = this.getCurrentValue();
        if ( currentValue == null )
        {
            if ( this.oldValue != null )
            {
                result = false;
            }
        }
        else
        {
            if ( this.oldValue != null )
            {
                result = ! currentValue.equals(this.oldValue);
            }
        }
        
        return result;
    }
    
    /** method which allow to restore the old value for all properties */
    public void restoreOldValues()
    {   
        if ( this.isValueModified() && this.cancelAction != null )
        {   
            /** call action */
            this.cancelAction.modificationCanceledOn(this, this.getOldParsedValue(), this.getCurrentParsedValue());
        }
        
        if ( this.oldValue != null )
        {   try
            {   this.setValue(this.oldValue); }
            catch (PropertiesException ex)
            {   ex.printStackTrace(); }
        }
    }
    
    /** method which indicate that all values are confirmed */
    public void confirmValues()
    {   
        if ( this.isValueModified() && this.validateAction != null )
        {   
            /** call action */
            this.validateAction.modificationConfirmedOn(this, this.getOldParsedValue(), this.getCurrentParsedValue());
        }
        
        this.oldValue = this.getCurrentValue();
    }
    
    /** add a depending action
     *  @param depend a ColdXmlProperty
     */
    public void addDependingProperty(XmlProperty depend)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("adding depending property '" + (depend == null ? null : depend.getRepr()) + "' on '" +
			 this.getRepr() + "'");
	}
	
	if ( depend != null )
        {   
            if ( this.depends == null )
                this.depends = new HashSet<XmlProperty>();
            this.depends.add(depend);
            
            depend.addPropertyChangeListener(this);
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of adding depending property '" + (depend == null ? null : depend.getRepr()) + "' on '" +
			 this.getRepr() + "'");
	}
    }
    
    /** return the property node
     *  @return a Property
     */
    public PropertyType getInnerProperty()
    {   return this.innerProperty; }
    
    /** return an instance of PropertyContainer
     *  @return an instance of PropertyContainer
     */
    public PropertyContainer getPropertyContainer()
    {   return this.getInnerProperty(); }
    
    /** return the code of the renderer to use for this property
     *  @return the code of the renderer to use for this property
     */
    public String getRendererCode()
    {   String code = null;
        if ( this.getInnerProperty() != null )
        {   code = this.getInnerProperty().getRenderer(); }
        return code;
    }
    
    /** return the code of the editor to use for this property
     *  @return the code of the editor to use for this property
     */
    public String getEditorCode()
    {   String code = null;
        if ( this.getInnerProperty() != null )
        {   code = this.getInnerProperty().getEditor(); }
        return code;
    }
    
    /** return true if this property depends on other properties
     *  @return true if this property depends on other properties
     */
    public boolean isDependingOnOthersProperties()
    {   boolean depend = false;
        if ( this.getInnerProperty() != null )
        {   if ( this.getInnerProperty().getDependsOn() != null )
            {   if ( this.getInnerProperty().getDependsOn().size() > 0 )
                    depend = true;
            }
        }
        return depend;
    }
    
    /** return a list of integer representing the id of the properties this property depends on
     *  @return a list of integer
     */
    public List<Long> getMasterPropertiesId()
    {   List<Long> ids = null;
        if ( this.getInnerProperty() != null )
        {   if ( this.getInnerProperty().getDependsOn() != null )
            {   ids = new ArrayList<Long>(this.getInnerProperty().getDependsOn().size());
                Iterator depends = this.getInnerProperty().getDependsOn().iterator();
                while(depends.hasNext())
                {   Object current = depends.next();
                    if ( current instanceof DependsOnType )
                    {   try
                        {   ids.add( Long.parseLong(((DependsOnType)current).getRepr()) ); }
                        catch(NumberFormatException e)
                        {   logger.error("unable to parse the dependsOn elemetn with rep = '" +
                                                ((DependsOnType)current).getRepr() + "'");
                        }
                    }
                }
            }
        }
        if ( ids == null )
            ids = Collections.EMPTY_LIST;
        return ids;
    }
    
    /** return true if property is visible
     *  @return true if property is visible
     */
    public boolean isVisible()
    {   return this.visible; }
    
    /** initialize visibility
     *  @param visible true if property is visible
     */
    public void setVisible(boolean visible)
    {   if ( visible != this.isVisible() )
        {   this.visible = visible;
            this.firePropertyChange(PROP_VISILITY, ! visible, visible);
        }
    }
    
    /** return the id of the property
     *  @return the id of the property or -1 if no specified
     */
    public long getId()
    {   long result = -1;
        if ( this.innerProperty != null )
            result = this.innerProperty.getId();
        return result;
    }
    
    /** return the representation of the property
     *  @return the representation of the property or null if no specified
     */
    public String getRepr()
    {   String result = null;
        if ( this.innerProperty != null )
            result = this.innerProperty.getRepr();
        return result;
    }
    
    /** return the nature of the element
     *  @return the nature of the element
     */ 
    public String getNature()
    {   String nature = null;
        if ( this.innerProperty != null )
        {   nature = this.innerProperty.getNature().value(); }
        return nature;
    }
    
    /** return the editor code of the element
     *  @return the editor code of the element
     */ 
    public String getEditor()
    {   String code = null;
        if ( this.innerProperty != null )
        {   code = this.innerProperty.getEditor(); }
        return code;
    }
    
    /** return the renderer code of the element
     *  @return the renderer code of the element
     */ 
    public String getRenderer()
    {   String code = null;
        if ( this.innerProperty != null )
        {   code = this.innerProperty.getRenderer(); }
        return code;
    }
    
    /** return true if the property should be editable
     *  @return true if the property should be editable
     */
    public boolean isEditable()
    {   return this.editable; }
    
    /** initialize the editability of the property
     *  @param editable true if the property should be editable
     */
    public void setEditable(boolean editable)
    {   if ( editable != this.isEditable() )
        {   this.editable = editable;
            this.firePropertyChange(PROP_EDITABILITY, ! editable, editable);
        }
    }
    
    /** return the default value as String
     *  @return the default value as String
     */
    public String getDefaultValue()
    {   String result = null;
        if ( this.innerProperty != null )
            result = this.innerProperty.getDefault();
        return result;
    }
    
    /** return the current value node
     *  @return a value
     */
    private AppliedValue getCurrentNodeValue()
    {   AppliedValue value = null;
        if ( this.innerProperty != null )
        {   if ( this.innerProperty.getAppliedValue() != null )
            {   value = this.innerProperty.getAppliedValue(); }
            else
            {   /* force creation */
                value = new ObjectFactory().createPropertyTypeAppliedValue();
                value.setValue(this.getDefaultValue());
                this.innerProperty.setAppliedValue(value);
            }
        }
        return value;
    }
    
    /** return the current value
     *  @return a String representing the current value
     */
    public String getCurrentValue()
    {   String result = null;
        AppliedValue applied = this.getCurrentNodeValue();
        if ( applied != null )
            result = applied.getValue();
        return result;
    }
    
    /** return the current value parsed
     *  @return an Object representing the value of the property
     */
    public Object getCurrentParsedValue()
    {           
        Parser parser = PropertiesProvider.getParserRegistry().
                            getParser(this.getNature());
        Object o = null;
        try
        {   o = parser.parse(this.getCurrentValue()); }
        catch(Exception e)
        {   logger.error("error when parsing value '" + this.getCurrentValue() + "' with parser : " + parser); }
        
        return o;
    }
    
    /** return the old value parsed
     *  @return an Object representing the old value of the property
     */
    public Object getOldParsedValue()
    {           
        Parser parser = PropertiesProvider.getParserRegistry().
                            getParser(this.getNature());
        Object o = null;
        try
        {   o = parser.parse(this.oldValue); }
        catch(Exception e)
        {   logger.error("error when parsing value '" + this.oldValue + "' with parser : " + parser); }
        
        return o;
    }
    
    /** initialize the value
     *  @param value the value to apply
     *  @return false if it failed
     */
    public boolean setValue(String value) throws PropertiesException
    {   boolean ok = false;
        if ( value != null )
        {   AppliedValue applied = this.getCurrentNodeValue();
            if ( applied != null )
            {   boolean fire = false;
                
                String oldValue = applied.getValue();
                
                if ( oldValue == null )
                {   if ( value != null )
                        fire = true;
                }
                else
                {   if ( ! oldValue.equals(value) )
                        fire = true;
                }
                
                Parser parser = PropertiesProvider.getParserRegistry().
                                    getParser(this.getNature());
                Object oldObject = null;
                Object newObject = null;
                if ( parser != null )
                {   try
                    {   oldObject = parser.parse(oldValue); }
                    catch(Exception e)
                    {   logger.error("error when parsing '" + oldValue +
                                            "' with parser " + parser, e);
                    }
                    try
                    {   newObject = parser.parse(value); }
                    catch(Exception e)
                    {   logger.error("error when parsing '" + value +
                                            "' with parser " + parser, e);
                    }
                }
                else
                    throw new PropertiesException(new NoParserException(this.getNature()));
                
                if ( fire )
                {   
                    applied.setValue(value);
                    this.firePropertyChange(PROP_VALUE, oldObject, newObject);
                    ok = true;
                }
            }
        }
        return ok;
    }
    
    /** indicates to all user that a property changed
     *  @param e an instance of PropertyChangeEvent
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {   this.firePropertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue)); }
    
    /** indicates to all user that a property changed
     *  @param e an instance of PropertyChangeEvent
     */
    public void firePropertyChange(PropertyChangeEvent e)
    {   if ( this.applyAction != null )
            this.applyAction.applyChange(e.getPropertyName(), this);
        
        super.firePropertyChange(e);
    }
    
    /** method that update the state of the current property according to the states of the properties it depends */
    public void updateState()
    {   if ( this.depends != null )
        {   Iterator<XmlProperty> it = this.depends.iterator();
            while(it.hasNext())
            {   XmlProperty current = it.next();
                
                current.updateState();
                
                String currentValue = current.getCurrentValue();
                
                Parser parser = PropertiesProvider.getParserRegistry().
                                    getParser(current.getNature());
                
                Object o = null;
                
                if ( parser != null )
                {
                    try
                    {   o = parser.parse(currentValue); }
                    catch (ParseException ex)
                    {   ex.printStackTrace(); }
                }
                
                /** we have to force the change of some properties to be sure, that
                 *  properties are compatible with themselves
                 */
                current.firePropertyChange(PROP_VALUE, null, o);
                current.firePropertyChange(PROP_EDITABILITY, ! current.isEditable(), current.isEditable());
                current.firePropertyChange(PROP_VISILITY, ! current.isVisible(), current.isVisible());
            }
        }
        
    }
    
    /* #########################################################################
     * ################ PropertyChangeListener implementation ##################
     * ######################################################################### */
    
    public void propertyChange(PropertyChangeEvent evt)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug(this.getRepr() + " detect a modification from " + evt.getSource() + " of kind " +
			 evt.getSource().getClass());
	}
	if ( evt.getSource() instanceof XmlProperty )
        {   if ( this.dependAction != null )
            {   this.dependAction.dependingPropertyChange(evt.getPropertyName(), this, (XmlProperty)evt.getSource(),
                                                          this.depends, evt.getOldValue(), evt.getNewValue());
            }
        }
    }

    /** do not consider name to see if ColdXmlProperty are equals
     *  prefer compare their xml property
     */
    public boolean equals(Object t)
    {   boolean retValue = false;
        if ( t instanceof XmlProperty )
        {   retValue = this.innerProperty == ((XmlProperty) t).innerProperty; }
        
        return retValue;
    }
    
}
