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
public abstract class AbstractSibTask extends SibTask
{   
    /** Creates a new instance of SibTask */
    public AbstractSibTask()
    {	}

    /**
     * When an object implementing interface <code>Runnable</code> is used 
     * to create a thread, starting the thread causes the object's 
     * <code>run</code> method to be called in that separately executing 
     * thread. 
     * <p>
     * The general contract of the method <code>run</code> is that it may 
     * take any action whatsoever.
     * 
     * @see java.lang.Thread#run()
     */
    public final void run()
    {
	this.setStatus(SibTaskStatus.RUNNING);
	
	if ( ! this.getStatus().equals(SibTaskStatus.STOPPED) )
	{
	    this._run();
	}
	
	if ( ! this.getStatus().equals(SibTaskStatus.STOPPED) )
	{
	    this.setStatus(SibTaskStatus.FINISHED);
	}
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used 
     * to create a thread, starting the thread causes the object's 
     * <code>run</code> method to be called in that separately executing 
     * thread. 
     * <p>
     * The general contract of the method <code>run</code> is that it may 
     * take any action whatsoever.
     * 
     * @see java.lang.Thread#run()
     */
    public abstract void _run();
    
}
