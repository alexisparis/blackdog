/* =============================================================================
 * Siberia launcher
 * =============================================================================
 *
 * Project Lead:  Alexis Paris
 *
 * (C) Copyright 2008, by Alexis Paris.
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
package org.siberia.error;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import org.java.plugin.boot.BootErrorHandlerGui;
import org.java.plugin.registry.IntegrityCheckReport;
import org.siberia.SiberiaApplicationInitializer;
import org.siberia.io.IOHelper;
import org.siberia.splash.ProgressionSplashScreen;

/**
 *
 * Specific BootErrorHandler for siberia applications
 *
 * @author alexis
 */
public class SiberiaBootErrorHandler extends BootErrorHandlerGui
{   
    /** Creates a new instance of SiberiaBootErrorHandler */
    public SiberiaBootErrorHandler()
    {   }
    
    /** returns a File representing the plugins backup directory
     *	this file could not exists
     *	@return a File
     */
    public File getBackupPluginsDirectory()
    {
	return new File(System.getProperty(SiberiaApplicationInitializer.PROPERTY_PLUGIN_BACKUP_DIR));
    }
    
    /** returns a File representing the plugins directory
     *	@return a File
     */
    public File getPluginsDirectory()
    {
	return new File(System.getProperty(SiberiaApplicationInitializer.PROPERTY_PLUGIN_DIR));
    }
    
    /** hide splash screen */
    private void hideSplashScreen()
    {
	ProgressionSplashScreen splash = ProgressionSplashScreen.lastSplash;
	
	if ( splash != null && splash.isVisible() )
	{
	    splash.setVisible(false);
	}
    }
    
    /** ########################################################################
     *  ###################### Error handling methods ##########################
     *  ######################################################################## */

    @Override
    public void handleFatalError(String message)
    {
	this.hideSplashScreen();
	
        super.handleFatalError(message);
	
	/* launch restore process */
	launchRestoreProcess();
    }

    @Override
    public void handleFatalError(String message, Throwable t)
    {
	this.hideSplashScreen();
	
	super.handleFatalError(message, t);
	
	/* launch restore process */
	launchRestoreProcess();
    }

    @Override
    public boolean handleError(String message, Exception e)
    {
	this.hideSplashScreen();
	
        boolean retValue;
        
        retValue = super.handleError(message, e);
	
	if ( ! retValue )
	{   
	    /* launch restore process */
	    launchRestoreProcess();
	}
	
        return retValue;
    }

    @Override
    public boolean handleError(String message, IntegrityCheckReport report)
    {
	this.hideSplashScreen();
	
        boolean retValue;
        
        retValue = super.handleError(message, report);
	
	if ( ! retValue )
	{   
	    /* launch restore process */
	    launchRestoreProcess();
	}
	
        return retValue;
    }
    
    /** ########################################################################
     *  ######################## Plugins management ############################
     *  ######################################################################## */
    
    /** launch restore process method */
    private void launchRestoreProcess()
    {
	File pluginDir = this.getPluginsDirectory();
	File backup    = this.getBackupPluginsDirectory();
	
	if ( pluginDir != null && pluginDir.exists() && backup != null && backup.exists() )
	{
	    /* ask user */
	    ResourceBundle rb = ResourceBundle.getBundle(this.getClass().getName());
	    int answer = JOptionPane.showConfirmDialog(null, rb.getString("restore.backup.message"), rb.getString("restore.backup.title"), JOptionPane.YES_NO_OPTION);
	    
	    if ( answer == JOptionPane.YES_OPTION )
	    {
		try
		{
		    this.restoreLastConfiguration(pluginDir, backup);

		    System.exit(SiberiaApplicationInitializer.ERROR_CODE_RESTART);
		}
		catch(IOException e)
		{   e.printStackTrace(); }
	    }
	    else
	    {
		SiberiaApplicationInitializer.BACKUP_ENABLED = false;
	    }
	}
    }
    
    /** security method which allow to restore last plugin configuration
     *	this method search in the application directory if a back up of plugin configuration exists
     *	and restore it
     *	@param pluginsDir the file representing the directory which contains plugins
     *	@param backupPluginsDir the file representing the directory which contains plugins backup
     */
    private void restoreLastConfiguration(File pluginsDir, File backupPluginsDir) throws IOException
    {
	if ( backupPluginsDir != null && pluginsDir != null &&
	     backupPluginsDir.exists() && pluginsDir.exists() )
	{
	    /** delete the current plugins directory */
	    IOHelper.delete(pluginsDir);
	    
	    /** copy the content of backupPluginsDir to pluginsDir */
	    IOHelper.copy(backupPluginsDir, pluginsDir);
	}	
    }
    
}
