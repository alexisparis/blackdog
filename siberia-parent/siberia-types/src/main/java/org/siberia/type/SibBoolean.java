/* 
 * Siberia types : siberia plugin defining structures managed by siberia platform
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
package org.siberia.type;

import java.beans.PropertyVetoException;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;

/**
 *
 * Class that represents a named boolean
 *
 *  @author alexis
 */
@Bean(  name="SibBoolean",
        internationalizationRef="org.siberia.rc.i18n.type.SibBoolean",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibBoolean extends SibBaseType
{   
    /** propert boolean */
    public static final String PROPERTY_VALUE = "boolean";
    
    /** type of the base value */
    @BeanProperty(name=SibBoolean.PROPERTY_VALUE,
                  internationalizationRef="org.siberia.rc.i18n.property.SibBoolean_value",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setValue",
                  writeMethodParametersClass={Boolean.class},
                  readMethodName="getValue",
                  readMethodParametersClass={}
                 )
    private Boolean value = Boolean.FALSE;
    
    /** create a new instance of SibBoolean */
    public SibBoolean()
    {   super(); }

    /** return the value of the instance
     *  @return an instance of Boolean
     */
    protected Boolean getObject()
    {   return this.value; }

    /** return the value of the instance
     *  @return an instance of Boolean
     */
    public Boolean getValue()
    {   return this.getObject(); }
    
    /** initialize the object of the type
     *  @param o an Object
     */
    protected void setObject(Object o) throws PropertyVetoException
    {   if ( o instanceof Boolean )
        {   this.setValue( (Boolean)o ); }
        else
        {   throw new IllegalArgumentException("waiting for a Boolean"); }
    }

    /** initialize the value of the instance
     *  @param value 
     */
    public void setValue(Boolean value) throws PropertyVetoException
    {   
        if ( ! this.equalsValue(value) )
	{
	    this.fireVetoableChange(PROPERTY_VALUE, this.value, value);
        
	    Boolean oldValue = this.value;

	    this.checkReadOnlyProperty(PROPERTY_VALUE, this.getObject(), value);

	    this.value = value;

	    this.firePropertyChange(PROPERTY_VALUE, oldValue, this.value);
	}
    }
    
    /** return true if the boolean is selected
     *  @return true if the boolean is true
     */
    public boolean isSelected()
    {   Boolean b = this.getValue();
        if ( b != null )
            return b.booleanValue();
        return false;
    }
    
    /** select or not the boolean
     *  @param select true if the boolean must be selected
     */
    private void select(boolean select) throws PropertyVetoException
    {   this.setValue(select); }
    
    /** select the boolean */
    public void select() throws PropertyVetoException
    {   this.select(true); }
    
    /** unselect the boolean */
    public void unselect() throws PropertyVetoException
    {   this.select(false); }
}
