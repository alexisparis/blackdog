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
import org.apache.log4j.Logger;

/**
 *
 * TaskStatus that manage others TaskStatus
 *
 * @author alexis
 */
public class TaskStatusContainer extends AbstractTaskStatus implements PropertyChangeListener
{
    /** logger */
    private Logger                       logger          = Logger.getLogger(AbstractTaskStatus.class);
    
    /** vector of sub TaskStatus */
    private Vector<TaskStatusDescriptor> subStatus       = null;
    
    /** index of the current task being performed and not completed (0 if not started) */
    private int                          currentIndex    = 0;
    
    /** number of unit attached to the container */
    private int                          totalNbTimeUnit = 0;
    
    /** Creates a new instance of TaskStatusContainer */
    public TaskStatusContainer()
    {   
	this(null);
    }
    
    /** Creates a new instance of TaskStatusContainer */
    public TaskStatusContainer(String name)
    {   
	super(name);
    }
    
    /** append a new TaskStatus
     *  @param status a TaskStatus
     *  @param nbTimeUnit an integer
     */
    public void append(TaskStatus status, int nbTimeUnit)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling append(" + status + ", " + nbTimeUnit + ") on " + this);
	}
	if ( status != null )
        {   /** initialize label */
            boolean initLabel = false;
            if ( this.subStatus == null )
            {   this.subStatus = new Vector<TaskStatusDescriptor>();
                initLabel = true;
            }
            this.subStatus.add(new TaskStatusDescriptor(status, nbTimeUnit));
            status.addPropertyChangeListener(this);
            this.totalNbTimeUnit += nbTimeUnit;
            
            if ( initLabel )
            {   this.setLabel(status.getLabel()); }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of append(" + status + ", " + nbTimeUnit + ")");
	}
    }
    
    /** indicates if the given TaskStatus is associated to the current Task
     *  @param status a TaskStatus
     *  @return true if the given TaskStatus is associated to the current Task
     */
    private synchronized boolean isCurrentExecutingTask(TaskStatus status)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling isCurrentExecutingTask(" + status + ") on " + this);
	}
	
        boolean yes = false;
        if ( status != null )
        {   if ( this.subStatus != null )
            {   if ( this.currentIndex >= 0 && this.currentIndex < this.subStatus.size() )
                {   if ( this.subStatus.get(this.currentIndex).status == status )
                    {   yes = true; }
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("isCurrentExecutingTask(" + status + ") returns " + yes);
	}
	
        return yes;
    }

    /**
     * This method gets called when a bound property is changed.
     * 
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("property change(" + evt.getPropertyName() + ", " + evt.getOldValue() + ", " + evt.getNewValue() + ") on " + this);
	    
	    logger.debug("displaying all tasks status : ");
	    
	    if ( this.subStatus != null )
	    {
		for(int i = 0; i < this.subStatus.size(); i++)
		{
		    TaskStatusDescriptor current = this.subStatus.get(i);
		    
		    logger.debug("\tstatus at (" + i + ") : " + current.status + " with nbunit=" + current.nbTimeUnit);
		}
	    }
	}
	
	if ( evt.getSource() instanceof TaskStatus )
        {   TaskStatus source =(TaskStatus)evt.getSource();
            
            if ( source == this )
            {   this.removePropertyChangeListener(this);
                
                if ( this.subStatus != null )
                {   Iterator<TaskStatusDescriptor> it = this.subStatus.iterator();
                    while(it.hasNext())
                    {   it.next().status.removePropertyChangeListener(this); }
                    
                    this.subStatus.clear();
                    this.subStatus = null;
                }
            }
            else
            {
                if ( evt.getPropertyName().equals(TaskStatus.PROPERTY_LABEL) )
                {   
		    if ( this.isCurrentExecutingTask(source) )
                    {   
			
			this.setLabel(source.getLabel());
		    }
                }
                else if ( evt.getPropertyName().equals(TaskStatus.PROPERTY_COMPLETED) )
                {   
                    if ( this.isCurrentExecutingTask(source) )
                    {   if ( evt.getNewValue() instanceof Number )
                        {   
                            
                            /** receive a PropertyChangeEvent that is not from me, so sub status is not null ! */
                            if ( this.currentIndex >= this.subStatus.size() )
                                this.complete();
                            else
                            {
                                /* what is the new value of the container ? */
                                int nbUnitPassed = 0;
                                for(int i = 0; i <= this.currentIndex; i++)
                                {   if ( i != this.currentIndex )
                                        nbUnitPassed += this.subStatus.get(i).nbTimeUnit;
                                    else
                                        nbUnitPassed += ((Number)evt.getNewValue()).doubleValue() *
                                                            this.subStatus.get(i).nbTimeUnit / 100;
                                }
                                float nm = (float)((((float)nbUnitPassed) / this.totalNbTimeUnit) * 100);
                                this.setPercentageCompleted( nm );
                            }
                            if ( ((Number)evt.getNewValue()).intValue() >= 100f )
                            {   /** next task */
                                this.currentIndex ++;
				
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("current task is finished --> go to next");
				}
                                
                                /** set to the container the label of the new sub status */
                                if ( this.subStatus != null && this.currentIndex >= 0 && this.currentIndex < this.subStatus.size() )
                                {   TaskStatusDescriptor newStatusDesc = this.subStatus.get(this.currentIndex);
                                    if ( newStatusDesc != null )
                                    {   TaskStatus newStatus = newStatusDesc.status;
                                        if ( newStatus != null )
                                        {   this.setLabel(newStatus.getLabel()); }
                                    }
                                }
                            }
                        }
                    }
                    else
                    {   throw new RuntimeException("task launched but another task is in execution"); }
                }
            }
        }
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of property change(" + evt.getPropertyName() + ", " + evt.getOldValue() + ", " + evt.getNewValue() + ") on " + this);
	}
    }
    
    /** class that link a TaskStatus with its standard length in standard time unit */
    private class TaskStatusDescriptor
    {
        TaskStatus status     = null;
        
        int        nbTimeUnit = 0;
        
        /** create a new TaskStatusDescriptor
         *  @param status a TaskStatus
         *  @param nbTimeUnit an integer
         */
        public TaskStatusDescriptor(TaskStatus status, int nbTimeUnit)
        {   if ( status == null )
                throw new IllegalArgumentException("status must be non null");
            if ( nbTimeUnit < 0 )
                throw new IllegalArgumentException("nbTimeUnit must be positive");
                
            this.status = status;
            this.nbTimeUnit = nbTimeUnit;
        }
    }
    
}
