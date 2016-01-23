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

/**
 *
 * @author alexis
 */
public interface SiberiaBeanInfo extends ConfigurationBeanInfo
{
    /** indicates if expert information have to be taken into account
     *  @return a boolean
     */
    public boolean considerExpertDescriptor();
    
    /** indicates if expert information have to be taken into account
     *  @param consider a boolean
     */
    public void considerExpertDescriptor(boolean consider);
    
    /** indicates if icon resources have to be find in a plugin context
     *  @param pluginContext
     */
    public void setPluginContextActivated(boolean pluginContext);
    
    /** indicates if icon resources have to be find in a plugin context
     *  @return true if icon resources have to be find in a plugin context
     */
    public boolean isPluginContextActivated();
}
