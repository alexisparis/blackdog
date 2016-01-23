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
 * Interface for all object that are configurable<br>
 * That means that their inner caracteristics that change their behaviour can be modified
 *
 * @author alexis
 */
public interface Configurable
{
    /** tell if the type can be configured
     *  @return true if the type can be configured
     */
    public boolean isConfigurable(); 
 
    /** tell if the type can be configured
     *  @param config true if the type can be configured
     */   
    public void setConfigurable(boolean config) throws PropertyVetoException;
    
}
