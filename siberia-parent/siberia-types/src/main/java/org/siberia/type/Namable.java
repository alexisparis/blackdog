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

/**
 * Define methods for object that are caracterized by a name
 *
 * @author alexis
 */
public interface Namable
{
    /** property names */
    public static final String PROPERTY_NAME              = "name";
    
    /** property names */
    public static final String PROPERTY_NAME_COULD_CHANGE = "nameCouldChange";
    
    /** return the name of the object
     *  @return a String corresponding to the name
     **/
    public String getName();
    
    /** set the name of the object
     *  @param name the new name of the object
     **/
    public void setName(String name) throws PropertyVetoException;
    
    /** specify if the name could be changed
     *  @return a boolean
     **/
    public boolean nameCouldChange();
    
    /** set the name changeability of the object
     *  @param couldChange true if the name can be changed
     **/
    public void setNameCouldChange(boolean couldChange) throws PropertyVetoException;
}
