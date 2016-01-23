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
package org.blackdog;

import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;
import org.blackdog.action.impl.LaunchScanAction;
import org.blackdog.kernel.MusikKernelResources;
import org.siberia.ResourceLoader;
import org.siberia.properties.PropertiesManager;
import org.siberia.ui.UserInterface;

/**
 *
 * @author alexis
 */
public class BlackdogApplication extends org.siberia.SiberiaGUIApplication
{
    /** Creates a new instance of BlackdogApplication */
    public BlackdogApplication()
    {   this("sibermusik"); }
    
    /** Creates a new instance of BlackdogApplication
     *  @param id the id of the plugin
     */
    public BlackdogApplication(String id)
    {   super(id);  }
    
    /** called to initialize properties
     *	@param mainPropertiesExists true to indicate that the main properties file already exists
     */
    @Override
    protected void initializeProperties(boolean mainPropertiesExists)
    {
	super.initializeProperties(mainPropertiesExists);
	
	if ( ! mainPropertiesExists )
	{
	    /** change the default look and feel */
	    String lafName = "Plastic 3D";
	    
	    try
	    {
		PropertiesManager.setGeneralProperty(UserInterface.LOOK_AND_FEEL_PROPERTY_ID, lafName);
	    }
	    catch (Exception ex)
	    {
		logger.error("could not initialize look and feel with '" + lafName + "'", ex);
	    }
	    
	    MusikKernelResources.getInstance().forceAudioEntaggerInitialization();
	}
	
	/** always initialize the player when properties have been initialized
	 *  this will fix player if it is not initialized
	 */
	MusikKernelResources.getInstance().initializePlayer();
	
	/** always initialize the image searcher when properties have been initialized
	 *  this will fix image searcher if it is not initialized
	 */
	MusikKernelResources.getInstance().initializeCoverSearchImageSearcher();
    }
    
    /** start platform */
    @Override
    protected void start()
    {   
	super.start();
	
	/** initailize WizardPreparator */
	Image backgroundSummaryImage = null;
	try
	{
	    backgroundSummaryImage = ResourceLoader.getInstance().getImageNamed(BlackdogPlugin.PLUGIN_ID + ";1::img/wizard_summary_background.png");
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
	org.siberia.ui.awl.WizardPreparator.setSummaryImage(backgroundSummaryImage);
	org.siberia.ui.awl.WizardPreparator.setSummaryVisible(true);
    }
    
    /** method that is called when the system has successfully launch the application.
     *	all initialization are made and even the splash is invisible
     */
    @Override
    public void applicationStarted()
    {
	super.applicationStarted();
		
	if ( MusikKernelResources.getInstance().getLibraryScanProperties().getScannedDirectories().size() == 0 )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    /* launch scan action to force user to add at most one scanned directory */
		    LaunchScanAction scanAction = new LaunchScanAction(true);

		    scanAction.actionPerformed(new ActionEvent(UserInterface.getInstance().getFrame(),
							       -1, null));
		}
	    };
	    
	    /** ensure to do action in EDT */
	    if ( SwingUtilities.isEventDispatchThread() )
	    {
		runnable.run();
	    }
	    else
	    {
		SwingUtilities.invokeLater(runnable);
	    }
	}
    }
    
}
