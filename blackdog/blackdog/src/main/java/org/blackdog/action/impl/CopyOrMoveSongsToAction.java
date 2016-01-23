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
package org.blackdog.action.impl;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.blackdog.kernel.MusikKernelResources;
import org.blackdog.type.SongItem;
import org.siberia.ui.action.AbstractSingleTypeAction;
import org.siberia.ui.swing.dialog.ExtendedSwingWorker;
import org.siberia.ui.swing.dialog.SwingWorkerDialog;

/**
 *
 * Action that copy some items to another directory
 *
 * @author alexis
 */
public abstract class CopyOrMoveSongsToAction<E extends SongItem> extends AbstractSingleTypeAction<E>
{
    /** logger */
    private Logger logger  = Logger.getLogger(CopyOrMoveSongsToAction.class);
    
    /** last choosen directory */
    private static File   lastDir = null;
    
    /**
     * Creates a new instance of CopyOrMoveSongsToAction
     */
    public CopyOrMoveSongsToAction()
    {   super(); }
    
    /** return true if the action has to erase the origin file after */
    protected abstract boolean destroySourceFileAfterCopy();
    
    /** method called when the action has to be performed
     *  @param e an ActionEvent
     */
    public void actionPerformed(final ActionEvent e)
    {   final List<E> items = this.getTypes();
	if ( items != null )
	{
	    final ResourceBundle rb = ResourceBundle.getBundle(CopyOrMoveSongsToAction.class.getName());
	    
	    JFileChooser chooser = new JFileChooser(this.lastDir);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setDialogTitle(rb.getString( this.destroySourceFileAfterCopy() ? "filechooser.copy.title" : "filechooser.move.title" ));
	    
	    chooser.setMultiSelectionEnabled(false);
	    
	    int answer = chooser.showOpenDialog(getFrame(e));
	    
	    if ( answer == JFileChooser.APPROVE_OPTION )
	    {
		File _lastDir = chooser.getSelectedFile();
		
		if ( _lastDir != null && _lastDir.isDirectory() )
		{
		    this.lastDir = _lastDir;
		    
		    final SwingWorkerDialog dialog = new SwingWorkerDialog(this.getWindow(e), true);
		    
//		    Dimension dim = dialog.getPreferredSize();
//		    System.out.println("preferred size : " + dim);
//		    if ( dim != null && dim.width < 400 )
//		    {
//			dim = new Dimension(dim);
//			dim.width = 400;
//			dialog.setPreferredSize(dim);
//		    }
		    
		    dialog.setDifferWorkerExecutionEnabled(false);

		    SwingWorker worker = new ExtendedSwingWorker()
		    {
			protected Object doInBackground() throws Exception
			{
			    float partTimePerItem = 100.0f / items.size();
			    
			    for(int i = 0; i < items.size(); i++)
			    {

				SongItem current = items.get(i);

				if ( current == null )
				{
				    logger.info("unable to copy null song item");
				}
				else
				{
				    URL url = current.getValue();
				    
				    if ( url != null )
				    {
					File destination = null;
					
					try
					{
					    File origin = new File(url.toURI());
					    
					    final String originName = origin.getName();
					    
					    Runnable runnable = new Runnable()
					    {
						public void run()
						{
						    dialog.getLabel().setText(originName);
						}
					    };
					    
					    if ( SwingUtilities.isEventDispatchThread() )
					    {
						runnable.run();
					    }
					    else
					    {
						SwingUtilities.invokeLater(runnable);
					    }

					    /* determine the file where to copy this item */
					    destination = new File(lastDir, origin.getName());

					    if ( ! origin.equals(destination) )
					    {
						boolean copy = true;
						
						if ( destination.exists() )
						{
						    StringBuffer message = new StringBuffer(rb.getString("file.alreadyexists.dialog.message"));
						    String title = rb.getString("file.alreadyexists.dialog.title");
						    
						    /* in message, replace {file} by the path name */
						    String pathMotif = "{file}";
						    int fileIndex = message.indexOf(pathMotif);
						    if ( fileIndex != -1 )
						    {
							message.delete(fileIndex, fileIndex + pathMotif.length());
							message.insert(fileIndex, destination.getAbsolutePath());
						    }
						    
						    int answer = JOptionPane.showConfirmDialog(getWindow(e), 
											       message.toString(),
											       title,
											       JOptionPane.YES_NO_OPTION,
											       JOptionPane.WARNING_MESSAGE);
						    
						    if ( answer != JOptionPane.YES_OPTION )
						    {
							copy = false;
						    }
						}
						
						if ( copy )
						{
						    /** ensure that destination is created */
						    File parentDest = destination.getParentFile();
						    
						    if ( ! parentDest.exists() )
						    {
							parentDest.mkdirs();
						    }
						    
						    if ( ! destination.exists() )
						    {
							destination.createNewFile();
						    }
						    
						    org.siberia.utilities.io.IOUtilities.copy(origin, destination);

						    if ( destroySourceFileAfterCopy() )
						    {
							org.siberia.utilities.io.IOUtilities.delete(origin);

							/* also delete the items from the main playlist */
//							MusikKernelResources.getInstance().getAudioResources().getPlayListLibrary().remove(current);

							/** update song item value */
							current.setValue(destination.toURL());
						    }
						}
					    }
					}
					catch(URISyntaxException ex)
					{
					    logger.error("unable to copy", ex);
					}
					catch(IOException ex)
					{ 
					    if ( destination != null )
					    {
						StringBuffer message = new StringBuffer(rb.getString("file.ioexception.dialog.message"));
						String title = rb.getString("file.ioexception.dialog.title");

						/* in message, replace {file} by the path name */
						String pathMotif = "{file}";
						int fileIndex = message.indexOf(pathMotif);
						if ( fileIndex != -1 )
						{
						    message.delete(fileIndex, fileIndex + pathMotif.length());
						    message.insert(fileIndex, destination.getAbsolutePath());
						}

						JOptionPane.showMessageDialog(getWindow(e), 
									      message.toString(),
									      title,
									      JOptionPane.ERROR_MESSAGE);
					    }
					    
					    logger.error("unable to copy", ex);
					}
					catch(Exception ex)
					{
					    logger.error("unable to copy", ex);
					}
				    }
				}

				this.setProgress( (int) ( (i + 1) * partTimePerItem ) );
			    }

			    this.setProgress(100);

			    return null;
			}
		    };
		    
		    dialog.setWorker(worker);

		    String rbLabel = "waitingdialog.copy.label";
		    if ( this.destroySourceFileAfterCopy() )
		    {
			rbLabel = "waitingdialog.move.label";
		    }
		    String rbMessage = "waitingdialog.copy.message";
		    if ( this.destroySourceFileAfterCopy() )
		    {
			rbLabel = "waitingdialog.move.message";
		    }
		    
		    dialog.getLabel().setText(rb.getString(rbLabel));
		    dialog.setTitle(rb.getString(rbMessage));
		    
		    dialog.display();
		}
	    }
	}
    }
    
}