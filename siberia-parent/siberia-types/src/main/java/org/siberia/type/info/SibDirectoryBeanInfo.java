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
package org.siberia.type.info;

import java.beans.PropertyDescriptor;
import java.util.ResourceBundle;
import org.siberia.base.file.Directory;
import org.siberia.type.SibDirectory;
import org.siberia.type.SibURL;

/**
 *
 * BeanInfo for SibDirectory
 *
 * @author alexis
 */
public class SibDirectoryBeanInfo extends BaseTypeBeanInfo
{
    /** Creates a new instance of SibDirectoryBeanInfo */
    public SibDirectoryBeanInfo()
    {   this(SibDirectory.class); }
    
    /** Creates a new instance of SibDirectoryBeanInfo */
    protected SibDirectoryBeanInfo(Class c)
    {   super(c, Directory.class); }
    
    /** method that is called after building the PropertyDescriptor according to annotation information
     *  Overwrite this method to modify the information of the PropertyDescriptor
     *  @param descriptor a PropertyDescriptor
     */
    @Override
    protected void postProcessProperty(PropertyDescriptor descriptor)
    {   
	super.postProcessProperty(descriptor);
	
	if ( SibDirectory.PROPERTY_VALUE.equals(descriptor.getName()) )
	{   
	    ResourceBundle rb = ResourceBundle.getBundle(SibDirectoryBeanInfo.class.getName());
	    
	    descriptor.setDisplayName(rb.getString("directory.displayName"));
	    descriptor.setShortDescription(rb.getString("directory.description"));

	    if ( descriptor instanceof ExtendedPropertyDescriptor )
	    {
		((ExtendedPropertyDescriptor)descriptor).setSupportGrouping(false);
	    }
	}
    }
    
}
