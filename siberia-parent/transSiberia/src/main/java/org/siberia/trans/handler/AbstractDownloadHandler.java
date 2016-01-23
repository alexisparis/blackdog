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

import org.apache.log4j.Logger;
import org.siberia.utilities.task.TaskStatus;

/**
 *
 * Abstract implementation of DownloadHandler
 *
 * @author alexis
 */
public abstract class AbstractDownloadHandler implements DownloadHandler
{
    /** logger */
    private static Logger logger = Logger.getLogger(AbstractDownloadHandler.class.getName());
    
    /** global task status */
    private TaskStatus globalTaskStatus = null;
    
    /** Creates a new instance of AbstractDownloadHandler */
    public AbstractDownloadHandler()
    {
    }
    
    /** set the global TaskStatus representing the download task
     *	@param status a TaskStatus
     */
    public void setGlobalTaskStatus(TaskStatus status)
    {
	this.globalTaskStatus = status;
    }
    
    /** get the global TaskStatus representing the download task
     *	@return a TaskStatus
     */
    protected TaskStatus getGlobalTaskStatus()
    {
	return this.globalTaskStatus;
    }
    
}
