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
package org.siberia.type.task;

import org.siberia.type.AbstractSibType;
import org.siberia.type.SibString;
import org.siberia.type.annotation.bean.BeanProperty;
import org.siberia.type.annotation.bean.UnmergeableProperty;
import org.siberia.type.annotation.bean.Bean;

/**
 *
 * represents a task that can be managed by the siberia platform
 *
 * @author alexis
 */
@Bean(  name="SibTask",
        internationalizationRef="org.siberia.rc.i18n.type.task.SibTask",
        expert=true,
        hidden=true,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public abstract class SibTask extends AbstractSibType implements Runnable
{
    /** property progression */
    public static final String PROPERTY_PROGRESSION = "org.siberia.type.task.SibTask.progression";
    
    /** property deterministic */
    public static final String PROPERTY_DETERMINISTIC = "org.siberia.type.task.SibTask.deterministic";
    
    /** property stoppable */
    public static final String PROPERTY_STOPPABLE     = "org.siberia.type.task.SibTask.stoppable";
    
    /** property status */
    public static final String PROPERTY_STATUS        = "org.siberia.type.task.SibTask.status";
    
    /** max value of the progression value */
    public static final int PROGRESSION_MAX_VALUE = 100;
    
    /** progression */
    @BeanProperty(name=PROPERTY_PROGRESSION,
                  internationalizationRef="org.siberia.rc.i18n.property.SibTask_progression",
                  expert=true,
                  hidden=true,
                  preferred=false,
                  bound=true,
                  constrained=false,
                  writeMethodName="setProgression",
                  writeMethodParametersClass={int.class},
                  readMethodName="getProgression",
                  readMethodParametersClass={}
                 )
    @UnmergeableProperty
    private int progression = 0;
    
    /** indicate if this task is deterministic */
    @BeanProperty(name=PROPERTY_DETERMINISTIC,
                  internationalizationRef="org.siberia.rc.i18n.property.SibTask_deterministic",
                  expert=true,
                  hidden=true,
                  preferred=false,
                  bound=true,
                  constrained=false,
                  writeMethodName="setDeterministic",
                  writeMethodParametersClass={boolean.class},
                  readMethodName="isDeterministic",
                  readMethodParametersClass={}
                 )
    @UnmergeableProperty
    private boolean deterministic = true;
    
    /** indicate if this task is deterministic */
    @BeanProperty(name=PROPERTY_STOPPABLE,
                  internationalizationRef="org.siberia.rc.i18n.property.SibTask_stoppable",
                  expert=true,
                  hidden=true,
                  preferred=false,
                  bound=true,
                  constrained=false,
                  writeMethodName="setStoppable",
                  writeMethodParametersClass={boolean.class},
                  readMethodName="isStoppable",
                  readMethodParametersClass={}
                 )
    @UnmergeableProperty
    private boolean stoppable = true;
    
    /** status of this task */
    @BeanProperty(name=PROPERTY_STATUS,
                  internationalizationRef="org.siberia.rc.i18n.property.SibTask_status",
                  expert=true,
                  hidden=true,
                  preferred=false,
                  bound=true,
                  constrained=false,
                  writeMethodName="setStatus",
                  writeMethodParametersClass={SibTaskStatus.class},
                  readMethodName="getStatus",
                  readMethodParametersClass={}
                 )
    @UnmergeableProperty
    private SibTaskStatus status = SibTaskStatus.IDLE;
    
    /** Creates a new instance of SibTask */
    public SibTask()
    {	}
    
    /** return the progression fo the task
     *	@return the progression of the task
     */
    public int getProgression()
    {
	return this.progression;
    }
    
    /** set the progression fo the task
     *	@param progression the progression of the task
     */
    public void setProgression(int progression)
    {
	int _progress = progression;
	
	if ( _progress < 0 )
	{
	    _progress = 0;
	}
	else if ( _progress > PROGRESSION_MAX_VALUE )
	{
	    _progress = PROGRESSION_MAX_VALUE;
	}
	
	if ( this.getProgression() != _progress )
	{
	    int old = this.getProgression();
	    
	    this.progression = _progress;
	    
	    this.firePropertyChange(PROPERTY_PROGRESSION, old, this.getProgression());
	}
    }
    
    /** return true if this task is deterministic
     *	@return a boolean
     */
    public boolean isDeterministic()
    {
	return this.deterministic;
    }
    
    /** indicate if this task is deterministic
     *	@param deterministic true to indicate that this task is deterministic
     */
    public void setDeterministic(boolean deterministic)
    {
	if ( this.isDeterministic() != deterministic )
	{
	    this.deterministic = deterministic;
	    
	    this.firePropertyChange(PROPERTY_DETERMINISTIC, ! this.isDeterministic(), this.isDeterministic());
	}
    }
    
    /** return true if this task is stoppable
     *	@return a boolean
     */
    public boolean isStoppable()
    {
	return this.stoppable;
    }
    
    /** indicate if this task is stoppable
     *	@param stoppable true to indicate that this task is stoppable
     */
    public void setStoppable(boolean stoppable)
    {
	if ( this.isStoppable() != stoppable )
	{
	    this.stoppable = stoppable;
	    
	    this.firePropertyChange(PROPERTY_STOPPABLE, ! this.isStoppable(), this.isStoppable());
	}
    }
    
    /** return the status of this task
     *	@return a SibTaskStatus
     */
    public SibTaskStatus getStatus()
    {
	return this.status;
    }
    
    /** set the status of this task
     *	@param status a SibTaskStatus
     */
    public void setStatus(SibTaskStatus status)
    {
	if ( status == null )
	{
	    throw new IllegalArgumentException("the status of a task could not be null");
	}
	
	if ( ! status.equals(this.getStatus()) )
	{
	    SibTaskStatus oldStatus = this.getStatus();
	    
	    this.status = status;
	    
	    this.firePropertyChange(PROPERTY_STATUS, oldStatus, this.getStatus());
	}
    }
    
}
