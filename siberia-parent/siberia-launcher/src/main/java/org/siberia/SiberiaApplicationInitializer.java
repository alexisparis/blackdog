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
package org.siberia;

import java.io.File;
import java.lang.ref.SoftReference;
import org.java.plugin.boot.Application;
import org.java.plugin.boot.BootErrorHandler;
import org.java.plugin.boot.DefaultApplicationInitializer;
import org.java.plugin.util.ExtendedProperties;
import org.siberia.error.SiberiaBootErrorHandler;
import org.siberia.io.IOHelper;

/**
 *
 * Developped to provide a custom classloader that allow the load classes declared in all plugins.
 *
 *  It is not used yet but... in the future...
 *
 * @author alexis
 */
public class SiberiaApplicationInitializer extends DefaultApplicationInitializer
{
    /** property related to plugins directory */
    public static final String PROPERTY_PLUGIN_DIR        = "siberia.plugins.directory";
    
    /** property related to plugins back up directory */
    public static final String PROPERTY_PLUGIN_BACKUP_DIR = "siberia.plugins.backup.directory";
    
    /** error code which indicate that the application must restart */
    public static final int    ERROR_CODE_RESTART         = 200;
    
    /** indique si la sauvegarde des plugins doit être effectue */
    public static       boolean BACKUP_ENABLED            = true;
    
    /** Creates a new instance of SiberiaApplicationInitializer */
    public SiberiaApplicationInitializer()
    {   super(); }
    
    /**
     * Configures this instance and application environment. The sequence is:
     * <ul>
     *   <li>Configure logging system. There is special code for supporting
     *     <code>Log4j</code> logging system only. All other systems support
     *     come from <code>commons-logging</code> package.</li>
     *   <li>Instantiate and configure {@link PluginsCollector} using
     *     configuration data.</li>
     * </ul>
     * @see org.java.plugin.boot.ApplicationInitializer#configure(
     *      org.java.plugin.util.ExtendedProperties)
     */
    @Override
    public void configure(final ExtendedProperties configuration) throws Exception
    {
        super.configure(configuration);
        
        /** add some useful properties to system to be able to get properties value on plugins classes */
        System.setProperty(PROPERTY_PLUGIN_DIR, configuration.getProperty("org.java.plugin.boot.pluginsRepositories"));
        
        /** add some useful properties to system to be able to get properties value on plugins classes */
        System.setProperty(PROPERTY_PLUGIN_BACKUP_DIR, configuration.getProperty("org.java.plugin.boot.pluginsBackupRepositories"));
    }
    
    public Application initApplication(final BootErrorHandler errorHandler, final String[] args) throws Exception
    {   
	Application app = super.initApplication(errorHandler, args);
	
	SiberiaLauncher.applicationRef = new SoftReference<Application>(app);
	
	if ( errorHandler instanceof SiberiaBootErrorHandler )
	{
	    File backup     = ((SiberiaBootErrorHandler)errorHandler).getBackupPluginsDirectory();
	    File pluginsdir = ((SiberiaBootErrorHandler)errorHandler).getPluginsDirectory();

	    if ( backup != null && pluginsdir != null && BACKUP_ENABLED )
	    {
		System.out.println("recreating plugins backup");
		/* copy the content of backupPluginsDir to pluginsDir */
		/* recreate backup when application start ends */
		IOHelper.delete(backup);
		IOHelper.copy(pluginsdir, backup);
	    }
	}
        
//        if ( app instanceof ApplicationPlugin )
//        {   PluginManager manager = ((ApplicationPlugin)app).getManager();
//            
//            if ( manager != null )
//            {   /** install custom ClassLoader */
//                ClassLoader initialClassLoader = Thread.currentThread().getContextClassLoader();
//                
//                ClassLoader siberiaClassLoader = new SiberiaClassLoader(manager, initialClassLoader);
//
//                Thread.currentThread().setContextClassLoader(siberiaClassLoader);
//            }
//        }
        
        return app;
    }
    
}
