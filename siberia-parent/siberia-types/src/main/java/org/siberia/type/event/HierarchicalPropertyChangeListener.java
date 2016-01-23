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
package org.siberia.type.event;

import java.util.EventListener;

/**
 *
 * Define the methods that a Class must implements if it wants to listen
 * to property changes on a hierarchy of SibType
 *
 * @author alexis
 */
public interface HierarchicalPropertyChangeListener extends EventListener
{
    /** called when a HierarchicalPropertyChangeEvent was throwed by
     *  an object listener by this one
     *  @param event a HierarchycalPropertyChangeEvent
     */
    public void propertyChange(HierarchicalPropertyChangeEvent event);
    
}
