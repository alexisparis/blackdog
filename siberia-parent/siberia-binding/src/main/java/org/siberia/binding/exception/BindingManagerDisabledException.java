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
 * Exception found when trying to access to a BindingManager that is declared as disabled
 *
 * @author alexis
 */
public class BindingManagerDisabledException extends BindingManagerException
{
    /** Creates a new instance of BindingManagerDisabledException
     *  @param code the code of the BindingManager
     */
    public BindingManagerDisabledException(String code)
    {   super("trying to access to the BindingManager '" + code + "' that is declared disabled"); }
    
}
