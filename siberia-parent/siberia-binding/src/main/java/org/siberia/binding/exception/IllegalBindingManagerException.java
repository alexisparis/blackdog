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
package org.siberia.binding.exception;

/**
 *
 * Exception found when trying to found a BindingManager type A when searching for a BindingManager of kind B
 *
 * @author alexis
 */
public class IllegalBindingManagerException extends BindingManagerException
{
    
    /** Creates a new instance of IllegalBindingManagerException
     *  @param code the code of a BindingManager
     *  @param managerClassFound the BindingManager class found for the given code
     *  @param requiredType the required kind of BindingManager we were to found
     */
    public IllegalBindingManagerException(String code, Class managerClassFound, Class requiredType)
    {   super("trying to get a '" + requiredType + "' for code '" + code + "' but found '" + managerClassFound); }
    
}
