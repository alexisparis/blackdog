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
package org.blackdog.ui;

import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.JXTable;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 * runnable that change the filter on the playlist editor
 *
 * @author alexis
 */
public class PlayListFilterRunnable implements Runnable
{
    /** logger */
    private Logger              logger      = Logger.getLogger(PlayListFilterRunnable.class.getName());

    /** input */
    private Filter              filter      = null;

    /** playlist editor panel */
    private PlayListEditorPanel panel       = null;

    public PlayListFilterRunnable(PlayListEditorPanel panel, Filter filter)
    {
	if ( panel == null )
	{   throw new IllegalArgumentException("table could not be null"); }

	this.panel  = panel;
	this.filter = filter;
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
    public void run()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling run of " + this.getClass().getSimpleName());
	}

	final FilterPipeline pipe = new FilterPipeline(new Filter[]{this.filter});

	if ( logger.isDebugEnabled() )
	{
	    logger.debug("thread interrupted ? " + Thread.currentThread().isInterrupted());
	}

	if ( ! Thread.currentThread().isInterrupted() )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("current filter continue and is about to wait");
	    }

	    try
	    {   
		Thread.sleep(300);
	    }
	    catch (InterruptedException ex)
	    {   
		return;
	    }

	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("current filter has finished waiting");
	    }

	    if ( ! Thread.currentThread().isInterrupted() )
	    {
		Runnable runnable = new Runnable()
		{
		    public void run()
		    {   
			logger.debug("current filter runnable : EDT runnable launched");
			if ( ! Thread.currentThread().isInterrupted() )
			{   
			    logger.debug("current filter runnable : before applying filter");
			    panel.getTablePanel().getTable().setFilters(pipe);
			    
			    panel.valueAdjustingManually = true;
			    panel.getArtistList().getSelectionModel().setValueIsAdjusting(false);
			    panel.getAlbumList().getSelectionModel().setValueIsAdjusting(false);
			    panel.valueAdjustingManually = false;
			    
			    /** if there is a selection in artists or albums
			     *	except 'all' and if there is no result, then force panel to launch filter again
			     *
			     *	this solve the following problem :
			     *	when an artist and an album are selected,
			     *	if the user select another artist, a new search will began with the new artist
			     *	but with the old album, but this album could be removed after the search, thus
			     *	the selection in album goes to 'all items' but no song are displayed in the table
			     *	therefore, if I encounter this problem, I force updateFilter to be called again
			     */
			    if ( panel.getTablePanel().getTable().getRowCount() == 0 &&
				 panel.getAlbumList().getSpecificSelectedItems().size() == 0 )
			    {
				panel.updateFilter();
			    }
			
			    logger.debug("current filter runnable : after applying filter");
			}
			else
			{   logger.debug("filter runnable interrupted before applying filter"); }
		    }
		};

		if ( ! Thread.currentThread().isInterrupted() )
		{   
		    if ( SwingUtilities.isEventDispatchThread() )
		    {   runnable.run(); }
		    else
		    {   SwingUtilities.invokeLater(runnable); }
		}
	    }
	    else
	    {   logger.debug("filter runnable interrupted before launching inner runnable"); }
	}
	else
	{   logger.debug("filter runnable interrupted before waiting"); }
    }
}