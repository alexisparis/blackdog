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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 *
 * @author alexis
 */
public class ExtendedPropertyDescriptor extends PropertyDescriptor
{
    /** mergeable */
    private boolean supportGrouping = true;
    
    /**
     * This constructor takes the name of a simple property, and Method
     * objects for reading and writing the property.
     *
     * @param propertyName The programmatic name of the property.
     * @param readMethod The method used for reading the property value.
     *		May be null if the property is write-only.
     * @param writeMethod The method used for writing the property value.
     *		May be null if the property is read-only.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public ExtendedPropertyDescriptor(String propertyName, Method readMethod, Method writeMethod) throws IntrospectionException
    {	
	super(propertyName, readMethod, writeMethod);
    }

    /** indicate if this property descriptor supports grouping
     *	@return a boolean
     */
    public boolean isSupportGrouping()
    {	return supportGrouping; }

    /** indicate if this property descriptor supports grouping
     *	@param supportGrouping true to indicate that this property supports grouping
     */
    public void setSupportGrouping(boolean supportGrouping)
    {
	this.supportGrouping = supportGrouping;
    }
}
