/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.property;

import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;

/**
 *
 * @author alexis
 */
public class ClassUtils
{
    
    /** Creates a new instance of ClassUtils */
    private ClassUtils()
    {	}
    
    /** return a class according to its name
     *	@param classname the siberia name of a class or int, float, etc..
     *	@return the corresponding class
     */
    public static Class getClass(String classname) throws ResourceException
    {
	Class result = null;
	
	if ( classname != null )
	{
	    if ( classname.equalsIgnoreCase("double") )
	    {
		result = double.class;
	    }
	    else if ( classname.equalsIgnoreCase("float") )
	    {
		result = float.class;
	    }
	    else if ( classname.equalsIgnoreCase("int") )
	    {
		result = int.class;
	    }
	    else if ( classname.equalsIgnoreCase("integer") )
	    {
		result = int.class;
	    }
	    else if ( classname.equalsIgnoreCase("long") )
	    {
		result = long.class;
	    }
	    else if ( classname.equalsIgnoreCase("short") )
	    {
		result = short.class;
	    }
	    
	    if( result == null )
	    {
		result = ResourceLoader.getInstance().getClass(classname);
	    }
	}
	
	return result;
    }
    
}
