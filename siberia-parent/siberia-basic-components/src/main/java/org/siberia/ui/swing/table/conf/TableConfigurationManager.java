/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.table.conf;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.ui.swing.table.*;

/**
 *
 * Manager that defines static methods to manage TablePanel configuration
 *
 * @author alexis
 */
public class TableConfigurationManager
{
    /** directory relative to {ResourceLoader.getInstance().getApplicationHomeDirectory()}
     *	where the configuration are saved
     */
    private static final String TABLE_CONFIGURATION_DIR = "table-conf";
    
    /** map that links path of TablePanel configuration file to a Set of TablePanel
     *	which used that configuration
     *  the path is related to the path returned by
     *	{ResourceLoader.getInstance().getApplicationHomeDirectory()} + "/" + {TABLE_CONFIGURATION_DIR} + "/"
     *
     *	so "repositoriesTablePanel.properties" could mean
     *	"/home/alexis/.blackdog/repositoriesTablePanel.properties"
     */
    private static Map<String, Set<WeakReference<TablePanel>>> configurationMap = null;
    
    /** logger */
    private static Logger                                      logger           = Logger.getLogger(TableConfigurationManager.class);
    
    /** Creates a new instance of TableConfigurationManager */
    private TableConfigurationManager()
    {	}
    
    /** register a TablePanel for the given relative properties file path
     *	@param propertiesPath the relative properties file path
     *	@param tablePanel a TablePanel
     */
    public static void register(String propertiesPath, TablePanel tablePanel)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering register(String, TablePanel)");
	    logger.debug("calling register(String, TablePanel) with " + propertiesPath + " and " + tablePanel);
	}
	
	if ( propertiesPath == null )
	{   throw new IllegalArgumentException("propertiesPath should not be null"); }
	if ( tablePanel == null )
	{   throw new IllegalArgumentException("tablePanel should not be null"); }
	
	synchronized(TableConfigurationManager.class)
	{
	    if ( configurationMap == null )
	    {
		configurationMap = new HashMap<String, Set<WeakReference<TablePanel>>>(20);
	    }
	    
	    Set<WeakReference<TablePanel>> panels = configurationMap.get(propertiesPath);
	    
	    if ( panels == null )
	    {
		panels = new HashSet<WeakReference<TablePanel>>(10);
		configurationMap.put(propertiesPath, panels);
	    }
	    
	    panels.add(new WeakReference<TablePanel>(tablePanel));
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting register(String, TablePanel)");
	}
    }
    
    /** unregister a TablePanel for the given relative properties file path
     *	@param propertiesPath the relative properties file path
     *	@param tablePanel a TablePanel
     */
    public static void unregister(String propertiesPath, TablePanel tablePanel)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering unregister(String, TablePanel)");
	    logger.debug("calling unregister(String, TablePanel) with " + propertiesPath + " and " + tablePanel);
	}
	if ( propertiesPath != null && tablePanel != null )
	{
	    if ( configurationMap != null )
	    {
		synchronized(TableConfigurationManager.class)
		{
		    WeakReference<TablePanel> refToRemove = null;
		    
		    Set<WeakReference<TablePanel>> sets = configurationMap.get(propertiesPath);
		    
		    if ( sets != null && sets.size() > 0 )
		    {
			Iterator<WeakReference<TablePanel>> it = sets.iterator();
			
			while(it.hasNext())
			{
			    WeakReference<TablePanel> currentRef = it.next();
			    
			    if ( currentRef != null && currentRef.get() == tablePanel )
			    {
				refToRemove = currentRef;
				break;
			    }
			}
		    }
		    
		    if ( refToRemove == null )
		    {
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("unable to remove reference to tablePanel " + tablePanel + " for properties path " + propertiesPath);
			}
		    }
		    else
		    {	sets.remove(refToRemove); }
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting unregister(String, TablePanel)");
	}
    }
    
    /** return the location to use for the given relative properties file path
     *	@param propertiesPath the relative properties file path
     *	@return a complete path
     */
    private static String completePath(String propertiesPath)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering completePath(String)");
	    logger.debug("calling completePath(String) with " + propertiesPath);
	}
	String result = ResourceLoader.getInstance().getApplicationHomeDirectory() + File.separator +
			       TABLE_CONFIGURATION_DIR + File.separator + propertiesPath;
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("completePath(String) returns " + result);
	    logger.debug("exiting completePath(String)");
	}
	return result;
    }
    
    /** return the configuration for the given path
     *	@param propertiesPath the relative properties file path
     */
    public static TableConfiguration loadConfiguration(String propertiesPath) throws IOException
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering loadConfiguration(String)");
	    logger.debug("calling loadConfiguration(String) with " + propertiesPath);
	}
	TableConfiguration conf = TableConfiguration.loadConfiguration(completePath(propertiesPath));
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("loadConfiguration(String) returns " + conf);
	    logger.debug("exiting loadConfiguration(String)");
	}
	
	return conf;
    }
    
    /** ask manager to save the configuration of the given TablePanel
     *	@param propertiesPath the relative properties file path
     *	@param configuration a TableConfiguration
     *	@param tablePanel a TablePanel
     */
    public static void saveConfiguration(String propertiesPath, TableConfiguration configuration, TablePanel tablePanel) throws IOException
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering saveConfiguration(String, TableConfiguration, TablePanel)");
	    logger.debug("calling saveConfiguration(String, TableConfiguration, TablePanel) with " + propertiesPath + ", " +
			    configuration + ", " + tablePanel);
	}
	if ( propertiesPath != null && tablePanel != null && configuration != null )
	{   
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("before asking configuration to be saved");
	    }
	    configuration.save(completePath(propertiesPath));
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("configuration saved at " + propertiesPath);
	    }
	    
	    /* warn all tablePanel related to propertiesPath (except current TablePanel) that their configuration has changed */
	    synchronized(TableConfigurationManager.class)
	    {
		if ( configurationMap != null )
		{
		    Set<WeakReference<TablePanel>> sets = configurationMap.get(propertiesPath);
		    if ( sets != null && sets.size() > 0 )
		    {
			Iterator<WeakReference<TablePanel>> it = sets.iterator();
			
			while(it.hasNext())
			{
			    WeakReference<TablePanel> currentTablePanelRef = it.next();
			    if ( currentTablePanelRef != null )
			    {
				TablePanel currentTablePanel = currentTablePanelRef.get();
				
				if ( currentTablePanel != tablePanel )
				{
				    /* warn TablePanel */
				    currentTablePanel.loadConfiguration(configuration);
				    
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("asking " + currentTablePanel + " to update configuration");
				    }
				}
			    }
			}
		    }
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting saveConfiguration(String, TableConfiguration, TablePanel)");
	}
    }
}
