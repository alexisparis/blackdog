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
 * class representing a named String
 *
 *  @author alexis
 */
@Bean(  name="SibString",
        internationalizationRef="org.siberia.rc.i18n.type.SibString",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
      
public class SibString extends SibBaseType
{
    /** propert string */
    public static final String PROPERTY_VALUE = "string";
    
    /** type of the base value */
    @BeanProperty(name=SibString.PROPERTY_VALUE,
                  internationalizationRef="org.siberia.rc.i18n.property.SibString_value",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setValue",
                  writeMethodParametersClass={String.class},
                  readMethodName="getValue",
                  readMethodParametersClass={}
                 )
    private String value = null;
    
    /** create a new instanceof SibString */
    public SibString()
    {   super(); }

    /** return the value of the instance
     *  @return an instance of String
     */
    protected String getObject()
    {   return this.value; }

    /** return the value of the instance
     *  @return an instance of String
     */
    public String getValue()
    {   return this.getObject(); }
    
    /** initialize the object of the type
     *  @param o an Object
     */
    protected void setObject(Object o) throws PropertyVetoException
    {   if ( o instanceof String )
        {   this.setValue( (String)o ); }
        else
        {   throw new IllegalArgumentException("waiting for a String"); }
    }

    /** initialize the value of the instance
     *  @param value 
     */
    public void setValue(String value) throws PropertyVetoException
    {   
	if ( ! this.equalsValue(value) )
	{
	    this.fireVetoableChange(PROPERTY_VALUE, this.value, value);

	    String oldValue = this.value;

	    this.checkReadOnlyProperty(PROPERTY_VALUE, this.getObject(), value);

	    this.value = value;

	    this.firePropertyChange(PROPERTY_VALUE, oldValue, this.value);
	}
    }
        
}



