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
 *
 * interface which defines an object that can be moved in a structure
 *
 * @author alexis
 */
public interface Moveable
{
    /** property names */
    public static final String PROPERTY_MOVEABLE = "moveable";
    
    /** get the moveable hability of the instance
     *  @return boolean true if the object can be moved in a structure
     **/
    public boolean canBeMoved();
    
    /** set the moveable hability of the instance
     *  @param moveable true if the object has the hability to move in a structure
     **/
    public void setMoveable(boolean moveable) throws PropertyVetoException;
    
}
