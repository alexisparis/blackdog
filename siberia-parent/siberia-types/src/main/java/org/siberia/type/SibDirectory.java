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
import org.siberia.base.file.Directory;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;

/** 
 * Representation of a directory
 *
 *  @author alexis
 */
@Bean(  name="SibDirectory",
        internationalizationRef="org.siberia.rc.i18n.type.SibDirectory",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibDirectory extends SibBaseType
{
    /** propert directory */
    public static final String PROPERTY_VALUE = "directory";
    
    /** type of the base value */
    @BeanProperty(name=SibDirectory.PROPERTY_VALUE,
                  internationalizationRef="org.siberia.rc.i18n.property.SibDirectory_value",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setValue",
                  writeMethodParametersClass={Directory.class},
                  readMethodName="getValue",
                  readMethodParametersClass={}
                 )
    private Directory value = null;
    
    /** Creates a new instance of SibDirectory */
    public SibDirectory()
    {   super(); }

    /** return the value of the instance
     *  @return an instance of File
     */
    protected Directory getObject()
    {   return this.value; }

    /** return the value of the instance
     *  @return an instance of File
     */
    public Directory getValue()
    {   return this.getObject(); }
    
    /** initialize the object of the type
     *  @param o an Object
     */
    protected void setObject(Object o) throws PropertyVetoException
    {   if ( o instanceof Directory )
        {   this.setValue( (Directory)o ); }
        else
        {   throw new IllegalArgumentException("waiting for a Directory"); }
    }

    /** initialize the value of the instance
     *  @param value 
     */
    public void setValue(Directory value) throws PropertyVetoException
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
    
    /** set the value of the directory
     *	@param path the path of the directory
     */
    public void setFilePath(String path) throws PropertyVetoException
    {
	if ( path == null )
	{
	    this.setValue(null);
	}
	else
	{
	    this.setValue(new Directory(path));
	}
    }
    
    /** get the path of the directory
     *	@return the path of the directory
     */
    public String getFilePath()
    {	String path = null;
	
	Directory dir = this.getValue();
	if ( dir != null )
	{
	    path = dir.getAbsolutePath();
	}
	
	return path;
    }
}
