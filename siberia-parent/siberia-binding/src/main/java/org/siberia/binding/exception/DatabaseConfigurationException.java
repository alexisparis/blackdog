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

import org.siberia.binding.DataBaseBindingManager;

/**
 *
 * Exceptino throwed when the configuration of DataBaseBindingManager failed
 *
 * @author alexis
 */
public class DatabaseConfigurationException extends Exception
{
    
    /** Creates a new instance of DatabaseConfigurationException
     *  @param manager the DataBaseBindingManager which configuration failed
     *  @param nested the nested throwable
     */
    public DatabaseConfigurationException(DataBaseBindingManager manager, Throwable nested)
    {   super("unable to configure database for manager " + manager, nested); }
    
    
    /** Creates a new instance of DatabaseConfigurationException
     *  @param manager the DataBaseBindingManager which configuration failed
     */
    public DatabaseConfigurationException(DataBaseBindingManager manager)
    {   this(manager, null); }
    
}
