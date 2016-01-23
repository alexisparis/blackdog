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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * TaskStatus that manage others TaskStatus and that is related to a category
 *
 * @author alexis
 */
public class CategoryTaskStatusContainer extends TaskStatusContainer
{
    /* category */
    private String category = null;
    
    /** Creates a new instance of CategoryTaskStatusContainer
     *  @param category a String representing the category of a status
     */
    public CategoryTaskStatusContainer(String category)
    {   super();
        
        this.category = category;
    }

    /**
     * initialize a label representing the status of the task
     * 
     * 
     * @param label a label representing the status of the task
     */
    public void setLabel(String label)
    {   String prefix = this.category;
        if ( prefix != null )
        {   if ( prefix.trim().length() > 0 )
                prefix = prefix + " : ";
        }
        super.setLabel((prefix == null ? "" : "<html><b>prefix</b>") + label + (prefix == null ? "" : "</html>"));
    }
}
