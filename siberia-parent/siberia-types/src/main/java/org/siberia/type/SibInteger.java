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
 * Representation of an integer
 *
 *  @author alexis
 */
@Bean(  name="SibInteger",
        internationalizationRef="org.siberia.rc.i18n.type.SibInteger",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibInteger extends SibBaseType
{
    /** propert integer */
    public static final String PROPERTY_VALUE = "integer";
    
    /** type of the base value */
    @BeanProperty(name=SibInteger.PROPERTY_VALUE,
                  internationalizationRef="org.siberia.rc.i18n.property.SibInteger_value",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setValue",
                  writeMethodParametersClass={Integer.class},
                  readMethodName="getValue",
                  readMethodParametersClass={}
                 )
    private Integer value = null;
    
    /** Creates a new instance of SibInteger */
    public SibInteger()
    {   super(); }

    /** return the value of the instance
     *  @return an instance of Integer
     */
    protected Integer getObject()
    {   return this.value; }

    /** return the value of the instance
     *  @return an instance of Integer
     */
    public Integer getValue()
    {   return this.getObject(); }
    
    /** initialize the object of the type
     *  @param o an Object
     */
    protected void setObject(Object o) throws PropertyVetoException
    {   if ( o instanceof Integer )
        {   this.setValue( (Integer)o ); }
        else
        {   throw new IllegalArgumentException("waiting for a Integer"); }
    }

    /** initialize the value of the instance
     *  @param value 
     */
    public void setValue(Integer value) throws PropertyVetoException
    {   
	if ( ! this.equalsValue(value) )
	{
	    this.fireVetoableChange(PROPERTY_VALUE, this.value, value);

	    Integer oldValue = this.value;

	    this.checkReadOnlyProperty(PROPERTY_VALUE, this.getObject(), value);

	    this.value = value;

	    this.firePropertyChange(PROPERTY_VALUE, oldValue, this.value);
	}
    }
}
