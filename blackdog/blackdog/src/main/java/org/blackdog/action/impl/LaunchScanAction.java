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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.blackdog.kernel.MusikKernelResources;
import org.blackdog.scan.AudioScanner;
import org.siberia.type.SibType;
import org.siberia.type.event.HierarchicalPropertyChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeListener;
import org.siberia.ui.UserInterface;
import org.siberia.ui.action.GenericAction;
import org.blackdog.type.scan.ScanProperties;
import org.blackdog.type.scan.ScannedDirectories;
import org.siberia.ui.action.impl.AddingTypeAction;


/**
 *
 * Launch the scan of audio items on the library
 *
 * @author alexis
 */
public class LaunchScanAction extends GenericAction
{
    /** true to ask user when no scanned directories are registered
     *	if false, then the user will be ask to create a new scanned directories
     */
    private boolean askUserWhenNoScannedDir = true;
    
    /** logger */
    private Logger  logger                  = Logger.getLogger(LaunchScanAction.class);
    
    /** Creates a new instance of LaunchScanAction */
    public LaunchScanAction()
    {
	this(true);
    }
    
    /** Creates a new instance of LaunchScanAction
     *	@param askUserWhenNoScannedDir
     */
    public LaunchScanAction(boolean askUserWhenNoScannedDir)
    {   
	this.askUserWhenNoScannedDir = askUserWhenNoScannedDir;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("scan launch wanted");
	}
	
	ResourceBundle rb = ResourceBundle.getBundle(LaunchScanAction.class.getName());

	/* make sure that no scan is actually running */
	if ( MusikKernelResources.getInstance().isScanRunning() )
	{
	    logger.info("another scan seems to be running --> yield");
	    
	    JOptionPane.showMessageDialog(this.getWindow(e), rb.getString("anotherScanRunning.message"),
					  rb.getString("anotherScanRunning.title"), JOptionPane.ERROR_MESSAGE);
	}
	else
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("no other scan seems to be running --> trying to obtain permission");
	    }
		    
	    /* if we change the scan status, then we got the right to scan, else ... */
	    if ( MusikKernelResources.getInstance().setScanRunning(true) )
	    {
		try
		{
		    boolean containsScanDir = false;

		    ScannedDirectories scannedDir = null;
		    /** check if there is at least one scan directory */
		    ScanProperties scanProperties = MusikKernelResources.getInstance().getLibraryScanProperties();
		    if ( scanProperties != null )
		    {
			scannedDir = scanProperties.getScannedDirectories();

			if ( scannedDir != null )
			{
			    containsScanDir = scannedDir.size() > 0;
			}
		    }

		    if ( ! containsScanDir && scannedDir != null )
		    {
			/** warn user */
			int answer = -1;

			if ( this.askUserWhenNoScannedDir )
			{
			    answer = JOptionPane.showConfirmDialog(UserInterface.getInstance().getFrame(),
							  rb.getString("noScanneddir.message"),
							  rb.getString("noScanneddir.title"),
							  JOptionPane.YES_NO_OPTION,
							  JOptionPane.INFORMATION_MESSAGE);
			}
			else
			{
			    answer = JOptionPane.YES_OPTION;
			}

			if ( answer == JOptionPane.YES_OPTION )
			{
			    AddingTypeAction addAction = new AddingTypeAction();
			    addAction.setTypes(new SibType[]{scannedDir});

			    addAction.actionPerformed(e);

			    if ( addAction.getNewlyCreatedItem() != null )
			    {
				containsScanDir = true;
			    }
			}
		    }

		    if ( containsScanDir )
		    {
			final ScanDialog dialog = new ScanDialog(UserInterface.getInstance().getFrame(), true);

			final AudioScanner scanner = new AudioScanner(dialog,
								      MusikKernelResources.getInstance().getAudioResources().getPlayListLibrary());


			if ( logger.isDebugEnabled() )
			{
			    logger.debug("setting scan dialog visible");
			}

			scanner.execute();
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("executing scanner");
			}
			
			/* show dialog box */
			dialog.setVisible(true);
		    }
		    else
		    {
			while(MusikKernelResources.getInstance().isScanRunning())
			{
			    if ( MusikKernelResources.getInstance().setScanRunning(false) )
			    {
				break;
			    }
			}
		    }
		}
		catch(Exception ex)
		{
		    ex.printStackTrace();
		    
		    while(MusikKernelResources.getInstance().isScanRunning())
		    {
			if ( MusikKernelResources.getInstance().setScanRunning(false) )
			{
			    break;
			}
		    }
		}
	    }
	    else
	    {
		logger.info("could not obtain permission from resources to launch scan --> another scan seems to be running");
	    }
	}
    }
    
}
