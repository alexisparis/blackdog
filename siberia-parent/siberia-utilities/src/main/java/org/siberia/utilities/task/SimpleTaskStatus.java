/* 
 * Siberia utilities : siberia plugin providing severall utilities classes
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
package org.siberia.utilities.task;

/**
 *
 * Simple TaskStatus
 *
 * @author alexis
 */
public class SimpleTaskStatus extends AbstractTaskStatus
{   
    /** Creates a new instance of TaskStatus */
    public SimpleTaskStatus()
    {   this(""); }
    
    /** Creates a new instance of TaskStatus */
    public SimpleTaskStatus(String name)
    {   this(name, ""); }
    
    /** Creates a new instance of AbstractTaskStatus
     *  @param label the initial label
     */
    public SimpleTaskStatus(String name, String label)
    {   super(name, label); }
    
}
