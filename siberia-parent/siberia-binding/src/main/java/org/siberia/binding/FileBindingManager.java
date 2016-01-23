/* 
 * Siberia binding : siberia plugin defining persistence services
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
package org.siberia.binding;

import org.siberia.binding.constraint.BindingConstraint;
import org.siberia.binding.constraint.FileBindingConstraint;
import org.siberia.binding.exception.LoadException;
import org.siberia.binding.exception.SaveException;

/**
 *
 * BindingManager that are able to save an Object in a file
 *
 * @author alexis
 */
public interface FileBindingManager<T extends FileBindingConstraint> extends BindingManager
{   
    /** save objects
     *  @param types an array of Objects
     *
     *  @exception SaveException if errors occured
     */
    public void store(Object... type) throws SaveException;
    
    /** load objects
     *  @param constraints an array of BindingConstraint that allow to filter objects to load
     *	@return an array of Objects resulting from the loading process
     *
     *  @exception LoadException if errors occured
     */
    public Object[] load(T... constraints) throws LoadException;
}
