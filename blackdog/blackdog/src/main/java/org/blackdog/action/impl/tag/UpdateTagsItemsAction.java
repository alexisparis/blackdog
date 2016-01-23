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
package org.blackdog.action.impl.tag;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.blackdog.kernel.MusikKernelResources;
import org.blackdog.report.ReportManager;
import org.blackdog.type.customizer.SongItemCustomizer;
import org.siberia.type.SibList;
import org.siberia.type.SibType;
import org.siberia.type.SibCollection;
import org.siberia.ui.action.AbstractSingleTypeAction;
import org.siberia.ui.swing.dialog.ExtendedSwingWorker;
import org.siberia.ui.swing.dialog.SwingWorkerDialog;
import org.blackdog.type.TaggedSongItem;
import org.blackdog.report.TagsUpdateReport;

/**
 *
 * Action that remove items
 *
 *  if one of the related items could not be removed, then the action is disabled for all items
 *
 * @author alexis
 */
public class UpdateTagsItemsAction<E extends TaggedSongItem> extends AbstractSingleTypeAction<E>
{
    /** logger */
    private Logger logger = Logger.getLogger(UpdateTagsItemsAction.class);
    
    /** Creates a new instance of TypeEditingAction */
    public UpdateTagsItemsAction()
    {   super(); }
    
    /** method called when the action has to be performed
     *  @param e an ActionEvent
     */
    public void actionPerformed(ActionEvent e)
    {   final List<E> items = this.getTypes();
	if ( items != null )
	{
	    final SwingWorkerDialog dialog = new SwingWorkerDialog(this.getWindow(e), false);
	    
	    SwingWorker worker = new ExtendedSwingWorker()
	    {
		TagsUpdateReport report = new TagsUpdateReport();
		
		protected Object doInBackground() throws Exception
		{
		    float partTimePerItem = 100.0f / items.size();
		    
		    
		    for(int i = 0; i < items.size(); i++)
		    {
			if ( dialog != null && ! dialog.isVisible() )
			{
			    break;
			}
			
			TaggedSongItem current = items.get(i);
			
			if ( current != null )
			{
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("before setting all song caracteristics to null");
			    }
			    
			    try
			    {
				current.setInTagsRefreshProcess(true);
				
				/* clear item attributes */
				try
				{
				    current.setAlbum(null);
				    current.setArtist(null);
				    current.setAuthor(null);
				    current.setCategory(null);
				    current.setComment(null);
				    current.setLeadArtist(null);
				    current.setLyrics(null);
				    current.setYear(null);
				    current.setTrackNumber(null);
				    current.setTitle(null);
				}
				catch(PropertyVetoException ex)
				{
				    ex.printStackTrace();
				}
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("after setting all song caracteristics to null");
				}

				if ( logger.isDebugEnabled() )
				{
				    logger.debug("before asking song customizers");
				}
    //			    current.updateItemsTags(report);

				List<SongItemCustomizer> customizers = MusikKernelResources.getInstance().getSongItemCustomizers();

				/** ask to all SongItem customizer to finish item initialization */
				if ( customizers != null )
				{
				    ListIterator<SongItemCustomizer> it = customizers.listIterator();

				    while(it.hasNext())
				    {
					SongItemCustomizer currentCustomizer = it.next();

					try
					{
					    if ( currentCustomizer != null )
					    {
						if ( logger.isDebugEnabled() )
						{
						    logger.debug("asking customizer " + currentCustomizer + " to customize " + current);
						}

						currentCustomizer.customize(report, current);
					    }
					}
					catch(Exception e)
					{
					    logger.error("error while asking customizer " + currentCustomizer + " to customize " + current, e);
					}
				    }
				}
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("after asking song customizers");
				}
			    }
			    finally
			    {
				current.setInTagsRefreshProcess(false);
			    }
			}
			
			this.setProgress( (int) ( (i + 1) * partTimePerItem ) );
		    }
		    
		    this.setProgress(100);
		    
		    return null;
		}
		
		@Override
		protected void done()
		{
		    super.done();
		    
		    new ReportManager().processReport(report);
		}
	    };
	    
	    dialog.setWorker(worker);
		
	    ResourceBundle rb = ResourceBundle.getBundle(UpdateTagsItemsAction.class.getName());

	    dialog.getLabel().setText(rb.getString("dialog.label"));
	    dialog.setTitle(rb.getString("dialog.title"));

	    dialog.display();
	}
	else
	{   new Exception("et merde").printStackTrace(); }
    }
    
    /** set the type related to this action
     *  @return a SibType
     */
    @Override
    public void setTypes(E... types)
    {   super.setTypes(types);
	
	boolean enabled = true;
	
	if ( types == null || types.length == 0 )
	{   enabled = false; }
	else
	{
	    for(int i = 0; i < types.length; i++)
	    {
		TaggedSongItem item = types[i];
		
		if ( item != null )
		{
		    
		    
		}
	    }
	}
	
	this.setEnabled(enabled);
    }
    
}