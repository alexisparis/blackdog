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
import org.siberia.binding.exception.LoadException;
import org.siberia.binding.exception.SaveException;
import org.siberia.type.SibType;

/**
 *
 * Describe the services provided by an entity that is able to bind SibTypes instances
 *
 * @author alexis
 */
public interface BindingManager
{
    /** extension point id of BindingManager */
    public static final String BINDING_MANAGER_EXTENSION_ID = "binding-manager";
    
    /** name of the attribute code */
    public static final String ATTR_CODE                    = "code";
    
    /** name of the attribute class */
    public static final String ATTR_CLASS                   = "class";
    
    /** name of the attribute enabled */
    public static final String ATTR_ENABLED                 = "enabled";
    
    /** method that dispose the binding manager */
    public void dispose();
    
}
