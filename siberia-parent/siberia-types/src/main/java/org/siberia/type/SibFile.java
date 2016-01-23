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
import java.io.File;
import org.siberia.base.file.DefaultFile;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;

/** 
 * Representation of a named file
 *
 *  @author alexis
 */
@Bean(  name="SibFile",
        internationalizationRef="org.siberia.rc.i18n.type.SibFile",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibFile extends SibBaseType
{
    /** propert file */
    public static final String PROPERTY_VALUE = "file";
    
    /** type of the base value */
    @BeanProperty(name=SibFile.PROPERTY_VALUE,
                  internationalizationRef="org.siberia.rc.i18n.property.SibFile_value",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setValue",
                  writeMethodParametersClass={DefaultFile.class},
                  readMethodName="getValue",
                  readMethodParametersClass={}
                 )
    private DefaultFile value = null;
    
    /** Creates a new instance of SibFile */
    public SibFile()
    {   super(); }

    /** return the value of the instance
     *  @return an instance of File
     */
    protected DefaultFile getObject()
    {   return this.value; }

    /** return the value of the instance
     *  @return an instance of File
     */
    public DefaultFile getValue()
    {   return this.getObject(); }
    
    /** initialize the object of the type
     *  @param o an Object
     */
    protected void setObject(Object o) throws PropertyVetoException
    {   if ( o instanceof DefaultFile )
        {   this.setValue( (DefaultFile)o ); }
        else
        {   throw new IllegalArgumentException("waiting for a DefaultFile"); }
    }

    /** initialize the value of the instance
     *  @param value 
     */
    public void setValue(DefaultFile value) throws PropertyVetoException
    {   
        if ( ! this.equalsValue(value) )
	{
	    this.fireVetoableChange(PROPERTY_VALUE, this.value, value);
        
	    File oldValue = this.value;

	    this.checkReadOnlyProperty(PROPERTY_VALUE, this.getObject(), value);

	    this.value = value;

	    this.firePropertyChange(PROPERTY_VALUE, oldValue, this.value);
	}
    }
    
    /** set the value of the file
     *	@param path the path of the file
     */
    public void setFilePath(String path) throws PropertyVetoException
    {
	if ( path == null )
	{
	    this.setValue(null);
	}
	else
	{
	    this.setValue(new DefaultFile(path));
	}
    }
    
    /** get the path of the file
     *	@return the path of the file
     */
    public String getFilePath()
    {	String path = null;
	
	DefaultFile file = this.getValue();
	if ( file != null )
	{
	    path = file.getAbsolutePath();
	}
	
	return path;
    }
}
