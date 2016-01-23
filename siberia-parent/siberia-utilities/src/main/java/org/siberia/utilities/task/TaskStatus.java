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

import java.beans.PropertyChangeListener;

/**
 *
 * define the progression status of a task
 *
 * @author alexis
 */
public interface TaskStatus
{
    /** properties */
    public static final String PROPERTY_LABEL     = "label";
    public static final String PROPERTY_COMPLETED = "completed";
    
    /** return the name of the status
     *	@return a String
     */
    public String getName();
    
    /** return a label representing the status of the task
     *  @return a label representing the status of the task
     */
    public String getLabel();
    
    /** initialize a label representing the status of the task
     *  @param label a label representing the status of the task
     */
    public void setLabel(String label);
    
    /** initialize the percentage of work that the task has completed
     *  @param value the percentage of work that the task has completed ( [O, 100] )
     */
    public void setPercentageCompleted(float value);
    
    /** initialize the percentage of work that the task has completed
     *  @param value the percentage of work that the task has completed ( [O, 100] )
     */
    public void setPercentageCompleted(int value);
    
    /** return the percentage of work that the task has completed
     *  @return the percentage of work that the task has completed ( [O, 100] )
     */
    public float getPercentageCompleted();
    
    /** indicates if the related task is completed
     *  @return true if the related task is completed
     */
    public boolean isCompleted();
    
    /** indicates that it has completed */
    public void complete();
    
    /** add a PropertyListener over label and work
     *  @param listener an instance of PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);
    
    /** remove a PropertyListener over label and work
     *  @param listener an instance of PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);
    
}
