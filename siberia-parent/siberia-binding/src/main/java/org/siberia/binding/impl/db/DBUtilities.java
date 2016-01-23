/* 
 * Siberia binding : siberia plugin defining persistence services
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

package org.siberia.binding.impl.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.SiberiaBindingPlugin;
import org.siberia.binding.DataBaseBindingManager;
import org.siberia.binding.constraint.DataBaseBindingConstraint;
import org.siberia.binding.exception.DatabaseConfigurationNotFoundException;
import org.siberia.binding.transaction.Transaction;
import org.siberia.exception.ResourceException;

/**
 *
 * Define generic methods that simplify the use of an instance of DataBaseBindingManager
 *
 * @author alexis
 */
public class DBUtilities
{
    /* logger */
    private static final Logger logger                      = Logger.getLogger(DBUtilities.class);
    
    /** name of the file that contains the database configuration */
    private static final String DB_DECLARATION_FILE_NAME    = "database.properties";
    
    /** path to the resource that contains the database configuration */
    private static final String DB_DECLARATION_PATH         = "database.properties";
    
    /** path to the default resource that contains the database configuration */
    private static final String DEFAULT_DB_DECLARATION_PATH = SiberiaBindingPlugin.PLUGIN_ID + ";1::conf" + File.separator + DB_DECLARATION_FILE_NAME;
    
    /** property substitution key */
    private static Map<String, String> keySubstitutions   = null;
    private static Map<String, String> valueSubstitutions = null;
    
    static
    {
        keySubstitutions = new HashMap<String, String>();
        
        valueSubstitutions = new HashMap<String, String>();
        valueSubstitutions.put("{APPLICATION.DIR}", ResourceLoader.getInstance().getApplicationHomeDirectory());
        valueSubstitutions.put("{HOME.DIR}", System.getProperty("user.dir"));
    }
    
    /** Creates a new instance of DBUtilities */
    private DBUtilities()
    {   }
    
    /** ########################################################################
     *  ######### methods that allow to get the database configuration #########
     *  ######################################################################## */
    
    /** return a Properties containing database properties
     *  @return a Properties
     *
     *  @exception DatabaseConfigurationNotFoundException when configuration could not be found
     */
    public static Properties getDataBaseParameters() throws DatabaseConfigurationNotFoundException
    {
        logger.debug("trying to get database properties from application directory");
        Properties properties = getDataBaseParametersFromHome();
        
        if ( properties == null )
        {   logger.debug("database properties not found in application directory");
            /* try to get the declaration made by application plugin
             *  so, if found, copy the declaration to user directory and retry getDataBaseParametersFromHome
             */
            
            URL url = null;
            try
            {   
                logger.debug("trying to get database properties from application plugin");
                url = ResourceLoader.getInstance().getResource(DB_DECLARATION_PATH, ResourceLoader.getInstance().getApplicationPluginClassLoader());
                
                properties = getDataBaseParameters(url, "application");
            }
            catch(ResourceException e)
            {   logger.info("unable to get '" + DB_DECLARATION_FILE_NAME + "' from application", e); }
            
            if ( properties == null )
            {   logger.debug("unable to get database properties from application plugin");
                /* try to get the default declaration given by this plugin
                 *  so, if found, copy the declaration to user directory and retry getDataBaseParametersFromHome
                 */

                url = null;
                try
                {   
                    logger.debug("trying to get database properties from " + SiberiaBindingPlugin.PLUGIN_ID + " plugin");
                    url = ResourceLoader.getInstance().getRcResource(DEFAULT_DB_DECLARATION_PATH);

                    properties = getDataBaseParameters(url, SiberiaBindingPlugin.PLUGIN_ID);
                }
                catch(ResourceException e)
                {   logger.info("unable to get '" + DB_DECLARATION_FILE_NAME + "' from " + SiberiaBindingPlugin.PLUGIN_ID); }
                
                if ( properties == null )
                {   logger.debug("unable to get database properties from " + SiberiaBindingPlugin.PLUGIN_ID + " plugin"); }
            }
        }
        else
        {   logger.debug("using database properties defined in home directory"); }
            
        if ( properties == null )
        {   throw new DatabaseConfigurationNotFoundException(); }
        
        /** convert parameters */
        convertDataBaseProperties(properties);
        
        return properties;
    }
    
    /** return a Properties containing database properties from an url
     *  @param url the source url
     *  @param sourceName the name of the source (used to provide explicit messages)
     *  @return a Properties or null if failed
     */
    private static Properties getDataBaseParameters(URL url, String sourceName)
    {
        Properties properties = null;
        
        try
        {   File homeDirDbFile = new File(ResourceLoader.getInstance().getApplicationHomeDirectory() + File.separator +
                                          DB_DECLARATION_FILE_NAME);

            File parentFile = homeDirDbFile.getParentFile();
            if ( ! parentFile.exists() )
            {   try
                {   parentFile.mkdirs(); }
                catch(Exception e)
                {   logger.error("unable to create directory '" + parentFile.getAbsolutePath() + "'", e); }
            }
            
            if ( ! homeDirDbFile.exists() )
            {   try
                {   homeDirDbFile.createNewFile(); }
                catch(IOException e)
                {   logger.error("unable to create file '" + homeDirDbFile.getAbsolutePath() + "'", e); }
            }

            if ( homeDirDbFile.exists() )
            {   org.siberia.utilities.io.IOUtilities.copy(url.openStream(), homeDirDbFile);

                properties = getDataBaseParametersFromHome();
            }
        }
        catch(IOException e)
        {   logger.error("unable to copy application database properties to user directory"); }
        
        return properties;
    }
    
    /** return a Properties containing database properties from user directory
     *  @return a Properties
     */
    private static Properties getDataBaseParametersFromHome()
    {   
        Properties properties = new Properties();
        boolean configurationLoaded = false;
        
        /** try to get the file a file named 'DB_DECLARATION_FILE_NAME' in the home directory of the user */
        File f = new File(ResourceLoader.getInstance().getApplicationHomeDirectory() + File.separator +
                          DB_DECLARATION_FILE_NAME);
        
        if ( f.exists() )
        {   logger.debug("using database properties defined in " + f.getAbsolutePath());
            
            configurationLoaded = loadDatabaseProperties(properties, f);
        }
        
        if ( ! configurationLoaded )
        {   properties = null; }
        
        return properties;
    }
    
    /** method that allow to feed the given properties with the properties defined by the given InputStream
     *  @param properties a Properties
     *  @param file a File
     *  @return true if no problem
     */
    private static boolean loadDatabaseProperties(Properties properties, File f)
    {   boolean result = false;
        
        InputStream stream = null;
        
        try
        {   stream = new FileInputStream(f);

            properties.load(stream);
            
            result = true;
        }
        catch (Exception ex)
        {   logger.error("unable to read database properties from " + f.getAbsolutePath(), ex);
            stream = null;
        }
        finally
        {   if ( stream != null )
            {   try
                {   stream.close(); }
                catch(IOException e)
                {   e.printStackTrace(); }
            }
        }
        
        return result;
    }
    
    /** convert properties */
    private static void convertDataBaseProperties(Properties properties)
    {   if ( properties != null )
        {   Enumeration en = properties.propertyNames();
            
            while(en.hasMoreElements())
            {   Object current = en.nextElement();
                
                if ( current instanceof String )
                {   boolean modified = false;
                    
                    String key   = (String)current;
                    String value = properties.getProperty((String)key);
                    
                    // TODO
//                    String keySubstitution
                    
                    
                    if ( modified )
                    {   properties.put(key, value); }
                }
            }
        }
    }
    
    /** make a load all query for a given database manager
     *	for a given kind of item
     *	@param manager a DataBaseBindingManager
     *	@param transaction a Transaction
     *	@param type the kind of items to return
     *	@returns a List of items
     */
    public static List loadAll(DataBaseBindingManager manager, Transaction transaction, Class type) throws Exception
    {
	List results = null;
	
	results = manager.load(transaction, type, (DataBaseBindingConstraint[])null);
	
	if ( results == null )
	{
	    results = Collections.emptyList();
	}
	
	return results;
    }
    
    /** make a load all query for a given database manager
     *	for a given kind of item
     *	@param manager a DataBaseBindingManager
     *	@param type the kind of items to return
     *	@returns a List of items
     */
    public static List loadAll(DataBaseBindingManager manager, Class type) throws Exception
    {
	Transaction transaction = manager.createTransaction();
	
	return loadAll(manager, transaction, type);
    }
    
}
