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
import java.net.MalformedURLException;
import java.net.URL;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;

/**
 *
 * Representation of a name url
 *
 * @author alexis
 */
@Bean(  name="SibURL",
        internationalizationRef="org.siberia.rc.i18n.type.SibURL",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibURL extends SibBaseType
{
    /** propert url */
    public static final String PROPERTY_VALUE = "url";
    
    /** type of the base value */
    @BeanProperty(name=SibURL.PROPERTY_VALUE,
                  internationalizationRef="org.siberia.rc.i18n.property.SibURL_value",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setValue",
                  writeMethodParametersClass={URL.class},
                  readMethodName="getValue",
                  readMethodParametersClass={}
                 )
    private URL value = null;
    
    /** create a new instanceof SibURL */
    public SibURL()
    {   super(); }
    
    /** set the url
     *  @param url a string representation of an url
     */
    public void setURL(String url) throws PropertyVetoException
    {   URL u = null;
        try
        {   u = new URL(url);
            this.setValue(u);
        }
        catch(MalformedURLException e)
        {   return; }
    }
    
    /** get the url
     *  @return an instance of URL
     */
    public URL getURL()
    {   return this.getValue(); }

    /** return the value of the instance
     *  @return an instance of URL
     */
    protected URL getObject()
    {   return this.value; }

    /** return the value of the instance
     *  @return an instance of URL
     */
    public URL getValue()
    {   return this.getObject(); }
    
    /** initialize the object of the type
     *  @param o an Object
     */
    protected void setObject(Object o) throws PropertyVetoException
    {   if ( o instanceof URL )
        {   this.setValue( (URL)o ); }
        else
        {   throw new IllegalArgumentException("waiting for a URL"); }
    }

    /** initialize the value of the instance
     *  @param value 
     */
    public void setValue(URL value) throws PropertyVetoException
    {   
	if ( ! this.equalsValue(value) )
	{
	    this.fireVetoableChange(PROPERTY_VALUE, this.value, value);

	    URL oldValue = this.value;

	    this.checkReadOnlyProperty(PROPERTY_VALUE, this.getObject(), value);

	    this.value = value;

	    this.firePropertyChange(PROPERTY_VALUE, oldValue, this.value);
	}
    }
}
