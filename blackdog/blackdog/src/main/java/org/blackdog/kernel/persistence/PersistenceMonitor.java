/*
 * blackdog : audio player / manager
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.kernel.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alexis
 */
public class PersistenceMonitor extends Thread
{
    /** list of Runnable to launch */
    private List<Runnable>                  tasks   = new ArrayList<Runnable>();
    
    /** list of PersistenceFuture */
    private List<AbstractPersistenceFuture> futures = new ArrayList<AbstractPersistenceFuture>();
    
    /** Creates a new instance of PersistenceMonitor */
    public PersistenceMonitor()
    {	
	super();
	
	this.setName("Blackdog persistence monitor");
    }
    
    /** add a new Task
     *	@param task a Runnable
     *	@return a PersistenceFuture
     */
    public PersistenceFuture addTask(Runnable task)
    {
	AbstractPersistenceFuture future = null;
	
	if ( task != null )
	{
	    synchronized(this.tasks)
	    {
		this.tasks.add(task);
		
		future = new AbstractPersistenceFuture();
		this.futures.add(future);
	    }
	}
	
	return future;
    }
    
    public void run()
    {
	while(true) // never die
	{
	    Runnable		      runnable = null;
	    AbstractPersistenceFuture future   = null;
	    
	    synchronized(this.tasks)
	    {
		int size = this.tasks.size();
		if ( size > 0 )
		{
		    runnable = this.tasks.remove(0);
		    future   = this.futures.remove(0);
		}
	    }
		    
	    if ( runnable != null )
	    {
		future.setStatus(AbstractPersistenceFuture.FutureStatus.STARTED);
		runnable.run();
		future.setStatus(AbstractPersistenceFuture.FutureStatus.DONE);
	    }
	    else
	    {
		try
		{
		    this.sleep(100);
		}
		catch (InterruptedException ex)
		{
		    ex.printStackTrace();
		}
	    }
	}
    }
}
