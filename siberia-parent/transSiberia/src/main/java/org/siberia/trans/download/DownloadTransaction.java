/* 
 * TransSiberia : siberia plugin allowing to update siberia pplications and download new plugins
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
package org.siberia.trans.download;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.siberia.env.PluginResourceModification;
import org.siberia.env.PluginResources;
import org.siberia.trans.PluginGraph;
import org.siberia.trans.exception.InvalidPluginDeclaration;
import org.siberia.trans.type.plugin.PluginBuild;

/**
 *
 * Define the download transaction.
 *
 * Well, at the time, it is not really a Transaction is the sense of jta but in the futute it will
 *  surely be a very transaction.
 *  For now, it just have some transaction related methods such as commit, rollback and setRollbackOnly
 *
 * @author alexis
 */
public class DownloadTransaction
{
    /** executors service that is used to do the job */
    private ExecutorService      executorService     = null;
    
    /** list of Runnable to submit to executor service */
    private List<Callable<File>> tasks               = null;
    
    /** list of future */
    private List<Future<File>>   futures             = null;
    
    /** rollback only */
    private boolean              rollBackOnly        = false;
    
    /** the graph of build */
    private PluginGraph          graph               = null;
    
    /** indicate the number of error when trying to add plugins */
    private int                  addPluginErrorCount = 0;
    
    /**
     * Creates a new instance of DownloadTransaction
     *
     *	@param graph the graph of PluginBuild
     */
    public DownloadTransaction(PluginGraph graph)
    {
	if ( graph == null )
	{
	    throw new IllegalArgumentException("provide a non null graph");
	}
	
	this.graph = graph;
	
	this.executorService = Executors.newSingleThreadExecutor();
	this.tasks           = new ArrayList<Callable<File>>(20);
	this.futures         = new ArrayList<Future<File>>(20);
    }
    
    /** return the graph of plugins
     *	@return a PluginGraph
     */
    public PluginGraph getPluginGraph()
    {
	return this.graph;
    }
    
    /** submit a new task
     *	@param task a Runnable
     */
    public void appendTask(Callable<File> task)
    {
	this.tasks.add(task);
    }
    
    /** begin the transaction */
    public void begin()
    {
	for(int i = 0; i < this.tasks.size(); i++)
	{
	    Callable<File> currentTask = this.tasks.get(i);
	    
	    if ( currentTask != null && ! this.isRollbackOnly() )
	    {
		this.futures.add(this.executorService.submit(currentTask));
	    }
	}
    }
    
    /** return true if the download needs to restart the applciation
     *	@return a boolean
     */
    public boolean isRebootNeeded()
    {
	return true;
    }
    
    /** pre-commit method. called when sure that the transaction is not in rollback only
     */
    protected void preCommit()
    {
	// to override
    }
    
    /** post-commit method. called when sure that the transaction is not in rollback only
     */
    protected void postCommit()
    {
	// to override
    }
    
    /** commit the transaction */
    public void commit() throws IllegalStateException
    {
	if ( this.isRollbackOnly() )
	{
	    throw new IllegalStateException("the transaction is marked as rollbackonly");
	}
	
	this.preCommit();
	
	List<PluginResourceModification> modifications = new ArrayList<PluginResourceModification>(50);
	
	/** copy files to local plugin directory */
	for(int i = 0; i < this.futures.size(); i++)
	{
	    Future<File> currentFuture = this.futures.get(i);
	    if ( currentFuture != null )
	    {
		try
		{
		    File f = currentFuture.get();

		    if ( f != null )
		    {
			PluginBuild build = this.graph.getBuildAt(i);
			
			try
			{
			    PluginResourceModification[] modifs = PluginResources.getInstance().addPlugin(build.getLocalArchiveSimplename(),
													  f,
													  build.getPluginId());
			    
			    if ( modifs != null )
			    {
				for(int j = 0; j < modifs.length; j++)
				{
				    PluginResourceModification currentModif = modifs[j];
				    
				    if ( currentModif != null )
				    {
					modifications.add(currentModif);
				    }
				}
			    }
			}
			catch (Exception ex)
			{
			    this.addPluginErrorCount ++;
			    ex.printStackTrace();
			}
		    }
		}
		catch(Exception e)
		{
		    e.printStackTrace();
		}
	    }
	}
	
	if ( modifications.size() > 0 )
	{
	    PluginResources.getInstance().warnPluginResourcesListener(this,
		    (PluginResourceModification[])modifications.toArray(new PluginResourceModification[modifications.size()]));
	}
	
	this.postCommit();
    }
    
    /** indicate if the transaction is over */
    public boolean isFinished()
    {
	/** see if all future have been processed */
	boolean allFutureProcessed = true;
	for(int i = 0; i < this.futures.size(); i++)
	{
	    Future<File> currentFuture = this.futures.get(i);
	    if ( currentFuture != null )
	    {
		if ( ! currentFuture.isDone() )
		{
		    allFutureProcessed = false;
		}
	    }
	}
	
	return allFutureProcessed || this.executorService.isShutdown() || this.executorService.isTerminated();
    }
    
    /** rollback the transaction */
    public void rollback() throws IllegalStateException
    {
	/* nothing to do */
    }
    
    /** Modify the transaction associated with the current thread such that the only possible outcome of the transaction is to roll back the transaction
     *
     */
    public void setRollbackOnly() throws IllegalStateException
    {
	this.rollBackOnly = true;
	
	if ( this.futures != null )
	{
	    for(int i = 0; i < this.futures.size(); i++)
	    {
		Future future = this.futures.get(i);
		if ( future != null && ! future.isDone() )
		{
		    System.out.println("canceling success ? " + future.cancel(true));
		}
	    }
	}
	
	this.executorService.shutdownNow();
    }
    
    public boolean isRollbackOnly()
    {
	return this.rollBackOnly;
    }
    
    /* Obtain the status of the transaction associated with the current thread.
     *	@return The transaction status. If no transaction is associated with the current thread, this method returns the Status.NoTransaction value.
     */
//    public int getStatus()
//    {
//	return 0;
//    }
}
