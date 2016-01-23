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
package org.siberia.trans.handler;

import org.siberia.trans.PluginGraph;
import org.siberia.trans.TransSiberia;
import org.siberia.trans.exception.DownloadAbortedException;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.utilities.task.TaskStatus;

/**
 *
 * Define an object able to modulate TransSiberia behaviour during download process
 *
 * @author alexis
 */
public interface DownloadHandler
{
    /** indicate the beginning of plugins graph registration */
    public void pluginRegistrationBeginned();
    
    /** indicate the plugins graph registration has ended */
    public void pluginRegistrationEnded();
    
    /** indicate that the download of the given build has begun
     *	@param build the build taht is currently being downloaded
     *	@param partialStatus a TaskStatus
     */
    public void buildDownloadBegan(PluginBuild build, TaskStatus partialStatus);
    
    /** indicate that the download of the given build is finished
     *	@param build the build taht is currently being downloaded
     *	@param partialStatus a TaskStatus
     *	@param hasNext true if there are other Build to download
     */
    public void buildDownloadFinished(PluginBuild build, TaskStatus partialStatus, boolean hasNext);
    
    /** set the global TaskStatus representing the download task
     *	@param status a TaskStatus
     */
    public void setGlobalTaskStatus(TaskStatus status);
    
    /** set the number of build to download
     *	@param count the number of build to download
     */
    public void setNumberOfBuildsToDownload(int count);
    
    /** ask for license acceptation
     *	@param transSiberia the TransSiberia currently being used
     *	@param build the build that ask for license acceptation
     *	@return true if the license is accepted, false else
     */
    public boolean confirmLicense(TransSiberia transSiberia, PluginBuild build);
    
    /** handle an error during registration phase
     *	@param graph the PluginGraph
     *	@param message the message of the error
     *	@param throwable the throwable which gives the StackTrace
     *
     *	@return true to try again
     *	
     *	@exception DownloadAbortedException if the error provoke the download stop
     */
    public boolean handleErrorOnRegistration(PluginGraph graph, String message, Throwable throwable) throws DownloadAbortedException;
    
    /** handle an error on download
     *	@param graph the PluginGraph
     *	@param message the messageof the error
     *	@param throwable the throwable which gives the StackTrace
     *
     *	@return true to try again
     *	
     *	@exception DownloadAbortedException if the error provoke the download stop
     */
    public boolean handleErrorOnDownload(PluginGraph graph, String message, Throwable throwable) throws DownloadAbortedException;
}
