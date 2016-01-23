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
import java.beans.PropertyChangeSupport;
import org.apache.log4j.Logger;

/**
 *
 * Abstract implementation fo TaskStatus
 *
 * @author alexis
 */
public class AbstractTaskStatus implements TaskStatus
{
    /** logger */
    private Logger                logger  = Logger.getLogger(AbstractTaskStatus.class);
    
    /** property change event support */
    private PropertyChangeSupport support = null;
    
    /** label */
    private String                label   = null;
    
    /** completed value */
    private float                 value   = 0;
    
    /** name */
    private String                name    = null;
    
    /** Creates a new instance of AbstractTaskStatus */
    public AbstractTaskStatus()
    {
	this(null);
    }
    
    /** Creates a new instance of AbstractTaskStatus */
    public AbstractTaskStatus(String name)
    {   this(name, ""); }
    
    /** Creates a new instance of AbstractTaskStatus
     *  @param label the initial label
     */
    public AbstractTaskStatus(String name, String label)
    {   this.setLabel(label);
	this.name = name;
    }
    
    /** return the name of the status
     *	@return a String
     */
    public String getName()
    {
	return this.name;
    }
    
    /** return an initialized PropertyChangeSupport
     *  @return a PropertyChangeSupport
     */
    protected synchronized PropertyChangeSupport getSupport()
    {   if ( this.support == null )
            this.support = new PropertyChangeSupport(this);
        return this.support;
    }
    
    /** indicates if the related task is completed
     *  @return true if the related task is completed
     */
    public boolean isCompleted()
    {   return this.value >= 100; }
    
    /** indicates that it has completed */
    public void complete()
    {   this.setPercentageCompleted(100f); }

    /**
     * initialize a label representing the status of the task
     * 
     * @param label a label representing the status of the task
     */
    public void setLabel(String label)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("setting label '" + label + "' for " + this);
	}
	
	boolean hasChanged = false;
        if ( this.label == null )
        {   if ( label != null )
                hasChanged = true;
        }
        else
        {   if ( ! this.label.equals(label) )
                hasChanged = true;
        }
        
        if ( hasChanged )
        {   String oldLabel = this.label;
            this.label = label;
            
            this.getSupport().firePropertyChange(TaskStatus.PROPERTY_LABEL, oldLabel, this.label);
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of setting label '" + label + "' for " + this);
	}
    }

    /**
     * return a label representing the status of the task
     * 
     * @return a label representing the status of the task
     */
    public String getLabel()
    {   return this.label; }
    
    /** initialize the percentage of work that the task has completed
     *  @param value the percentage of work that the task has completed ( [O, 100] )
     */
    public void setPercentageCompleted(int value)
    {   this.setPercentageCompleted( (float)value ); }

    /**
     * initialize the percentage of work that the task has completed
     * 
     * @param proposedValue the percentage of work that the task has completed ( [O, 100] )
     */
    public void setPercentageCompleted(float proposedValue)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("setting progression '" + proposedValue + "' for " + this);
	}
        // value is in [0, 100]
        float value = proposedValue;
        if ( value < 0 )
            value = 0;
        if ( value > 100 )
            value = 100;
        
        if ( this.value != value )
        {   float oldValue = this.value;
            this.value = value;
            
            this.getSupport().firePropertyChange(TaskStatus.PROPERTY_COMPLETED, oldValue, this.value);
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of setting progression '" + proposedValue + "' for " + this);
	}
    }

    /**
     * return the percentage of work that the task has completed
     * 
     * @return the percentage of work that the task has completed ( [O, 100] )
     */
    public float getPercentageCompleted()
    {   return this.value; }

    /**
     * remove a PropertyListener over label and work
     * 
     * @param listener an instance of PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {   if ( listener != null )
        {   if ( this.support != null )
            {   this.getSupport().removePropertyChangeListener(listener); }
        }
    }

    /**
     * add a PropertyListener over label and work
     * 
     * @param listener an instance of PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {   if ( listener != null )
        {   this.getSupport().addPropertyChangeListener(listener); }
    }
    
    /** return a representation of the TaskStatus
     *  @return a String
     */
    public String toString()
    {   
	return this.getClass().getSimpleName() + " name=" + this.getName() + " hash=" + this.hashCode() + 
		    " label=" + this.getLabel() + " progress=" + this.getPercentageCompleted();
    }
    
}
