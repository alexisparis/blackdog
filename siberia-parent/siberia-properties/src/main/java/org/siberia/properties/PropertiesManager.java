/* 
 * Siberia properties : siberia plugin defining system properties
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
package org.siberia.properties;

import java.io.File;
import java.lang.ref.SoftReference;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;
import org.siberia.env.SiberExtension;
import org.siberia.xml.JAXBPropertiesLoader;
import org.siberia.xml.schema.properties.Properties;
import org.siberia.properties.util.PropertiesMerger;
import org.siberia.properties.util.PropertiesProvider;
import org.siberia.env.PluginResources;

/**
 *
 * Defines static methods to store properties context and associates to them an id.
 * This class is also responsible for the merge of Properties and for the storage of the result.
 * It provide also methods to save the properties after changes.
 * 
 * @author alexis
 */
public class PropertiesManager
{
    /** id of the extension defining a Properties */
    private static final String PROPERTIES_EXTENSION_ID = "Properties";
    
    /** id of the general properties if exists */
    public static final String GENERAL_PROPERTIES = "main";
    
    /** map that links properties_id and descriptor */
    private static Map<String, PropertiesContext> contexts = null;
    
    /** logger */
    public static  Logger                          logger   = Logger.getLogger(PropertiesManager.class);
    
    /** soft reference to JAXBPropertiesLoader */
    private static SoftReference<JAXBPropertiesLoader> ref = null;
    
    /** Creates a new instance of PropertiesFinder */
    private PropertiesManager()
    {   /* do not used directly PropertiesManager */ }
    
    /** return the context for a given id
     *  @param id the id of a properties
     *  @return a PropertiesContext or null if not found
     */
    private static PropertiesContext getContext(String id)
    {   PropertiesContext context = null;
        
        if ( contexts != null )
        {   context = contexts.get(id); }
        
        return context;
    }
    
    /** method that returns an instance of JAXBPropertiesLoader
     *  @return an JAXBPropertiesLoader
     */
    private static JAXBPropertiesLoader getJAXBLoader()
    {   JAXBPropertiesLoader loader = null;
        if ( ref != null )
            loader = ref.get();
        if ( loader == null )
        {   loader = new JAXBPropertiesLoader();
            
            ref = new SoftReference<JAXBPropertiesLoader>(loader);
        }
        
        return loader;
    }
    
    /** method that saved a Properties
     *  @param id the id of the properties
     *  @param properties an xml Properties declaration
     */
    public static void saveProperties(String id, Properties properties)
    {
        PropertiesContext context = contexts.get(id);
        if ( context != null )
        {   try
            {   getJAXBLoader().saveProperties(properties, new File(context.getFileName()));
                logger.info("saving properties (id='" + id + "') in file " + context.getFileName());
            }
            catch(Exception e)
            {   logger.error("unable to save properties for id=" + id, e); }
        }
        else
        {   logger.error("unable to save properties for null id"); }
    }
    
    
    /** methods that initialized all properties declared in siberia plugins */
    public static void initialize()
    {   Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(PROPERTIES_EXTENSION_ID);
        
        Map<String, Set<Properties>> propertiesDeclarations =
                            new HashMap<String, Set<Properties>>();
        
        if ( extensions != null )
        {   Iterator<SiberExtension> it = extensions.iterator();
            while(it.hasNext())
            {   SiberExtension extension = it.next();
                
                String id   = extension.getStringParameterValue("id");
                String path = extension.getStringParameterValue("path");
                
                if ( id != null && path != null )
                {   
                    try
                    {   URL url = ResourceLoader.getInstance().getRcResource(path);

                        Properties prop = getJAXBLoader().loadProperties(url.openStream());

                        Set<Properties> set = propertiesDeclarations.get(id);

                        if ( set == null )
                        {   set = new HashSet<Properties>();
                            propertiesDeclarations.put(id, set);
                        }

                        set.add(prop);
                    }
                    catch(ResourceException re)
                    {   logger.error("unable to find resource : " + path, re); }
                    catch(JAXBException jaxbe)
                    {   logger.error("unable to marshal properties resource : " + path, jaxbe); }
                    catch(Exception e)
                    {   logger.error("error occured when opening properties declaration (id=" + id + ", path=" + path + ")", e); }
                }
                else
                {   if ( id == null )
                    {   logger.error("invalid declaration of properties (id not specified) --> ignored"); }
                    if ( path == null )
                    {   logger.error("invalid declaration of properties (path not specified) --> ignored"); }
                }
            }
        }
        
        /** register all properties */
        Iterator<String> ids = propertiesDeclarations.keySet().iterator();
        while(ids.hasNext())
        {   String currentId = ids.next();
            
            Set<Properties> currentProperties = propertiesDeclarations.get(currentId);
            
            Properties merged = new PropertiesMerger(currentProperties).merge();
            
            System.out.println("registering properties for id=" + currentId);
            PropertiesManager.register(currentId, merged);
        }
    }
    
    /** return true if the local main properties file seems to exists
     *	@return true if the local main properties file seems to exists
     */
    public static boolean localMainPropertiesFileExists()
    {
	return localPropertiesFileExists(GENERAL_PROPERTIES);
    }
    
    /** return true if the local properties file linked to 'id' seems to exists
     *	@param id the id of the properties
     *	@return true if the local properties file linked to 'id' seems to exists
     */
    public static boolean localPropertiesFileExists(String id)
    {
	boolean exists = false;
	
	File f = new File(ResourceLoader.getInstance().getApplicationHomeDirectory() +
                          File.separator + id + ".xml");
	
	exists = f.exists();
	
	return exists;
    }
    
    /** register a context of Properties
     *  @param id the id of the properties
     *  @param properties an xml declaration of properties
     */
    private static void register(String id, Properties properties)
    {   if ( properties != null && id != null )
        {   if ( contexts == null )
                contexts = new HashMap<String, PropertiesContext>();
            
            PropertiesContext context = contexts.get(id);
            
            Properties toSave = properties;
            
            if ( context == null )
            {   context = new PropertiesContext(ResourceLoader.getInstance().getApplicationHomeDirectory() +
                                                File.separator + id + ".xml");
                contexts.put(id, context);
                logger.info("registering properties " + id + " for context path " + context.getFileName());
                
                /* make sure that the properties file is created, if it exists, then we have to merge
                 * the required structure with the one already declared that could have differents applied values.
                 * if the properties odes not exist, then the given properties is simply saved in application directory
                 */
                File f = new File(context.getFileName());
                
                Properties oldProperties = null;

                if ( f.exists() )
                {   try
                    {   
                        oldProperties = getJAXBLoader().loadProperties(new FileInputStream(f));
                    }
                    catch(Exception e)
                    {   logger.error("unable to use existing properties for id=" + id, e); }
                }

                toSave = new PropertiesMerger().update(properties, oldProperties);
                
                try
                {   getJAXBLoader().saveProperties(toSave, f); }
                catch(Exception e)
                {   logger.error("unable to save properties for context id=" + id, e); }
            }
        }
        else
        {   logger.error("unable to register properties id=" + id + " properties=" + properties); }
    }
    
    /** return the Object representing the property for the given set of properties declared in a file
     *  properties stream an InputStream ( for example, a FileInputStream pointed out an xml declaration of properties)
     *  @param id the id of a registered properties context
     *  @param propertyNames an array of name's properties
     *  @return returns an object which type depends on the nature of the resulting properties or null if not found
     */
    public static Object getProperty(String id, String propertyName)
    {   Object result = null;
        
        if ( contexts != null )
        {   PropertiesContext context = contexts.get(id);
            
            if ( context != null )
            {   
                try
                {   result = PropertiesProvider.getProperty
                                        (new FileInputStream(context.getFileName()), propertyName);
                }
                catch(Exception e)
                {   logger.error("unable to get property " + propertyName + " from properties context " + id); }
            }
        }
        
        return result;
    }
    
    /** return the Objects representing the property for the given set of properties declared in a file
     *  properties stream an InputStream ( for example, a FileInputStream pointed out an xml declaration of properties)
     *  @param propertyNames an array of name's properties
     *  @return returns an array of object which type depends on their declared nature
     */
    public static Object[] getProperties(String id, String... propertyNames)
    {   Object[] results = null;
        
        if ( contexts != null )
        {
            PropertiesContext context = contexts.get(id);
            if ( context != null )
            {   try
                {   results = PropertiesProvider.getProperties
                                        (new FileInputStream(context.getFileName()), propertyNames);
                }
                catch(Exception e)
                {   logger.error("unable to get property " + propertyNames + " from properties context " + id);

                    results = new Object[propertyNames.length];

                    for(int i = 0; i < results.length; i++)
                    {   results[i] = null; }
                }
            }
        }
        
        return results;
    }
    
    /** return an xml Properties
     *  @param id the id of registered properties
     *  @return returns a Properties
     */
    public static Properties getPropertiesById(String id)
    {   Properties properties = null;
        
        if ( contexts != null )
        {   PropertiesContext context = contexts.get(id);
            
            if ( context != null )
            {   try
                {   properties = getJAXBLoader().loadProperties(new FileInputStream(context.getFileName())); }
                catch(Exception e)
                {   logger.error("unable to get properties for id=" + id, e); }
            }
        }
        
        return properties;
    }
    
    /** return the Object representing the property for the general context
     *  @param propertyNames an array of name's properties
     *  @return returns an object which type depends on the nature of the resulting properties or null if not found
     */
    public static Object getGeneralProperty(String propertyName)
    {   return getProperty(GENERAL_PROPERTIES, propertyName); }
    
    /** return the Objects representing the property for the general context
     *  @param propertyNames an array of name's properties
     *  @return returns an array of object whioch type depends on their declared nature
     */
    public static Object[] getGeneralProperties(String... propertyNames)
    {   return getProperties(GENERAL_PROPERTIES, propertyNames); }
    
    /** set the value of a property of the general context
     *  @param propertyName the name of the property
     *  @param value the value to set
     *  @boolean return true if the value has been changed
     */
    public static boolean setGeneralProperty(String propertyName, Object value) throws PropertiesManager.PropertiesNotRegisteredException,
                                                                                        PropertiesManager.PropertiesReadException
    {   return setProperty(GENERAL_PROPERTIES, propertyName, value); }
    
    /** set the value of properties of the general context
     *  @param propertyNames the name of the properties
     *  @param values the values to set
     *  @boolean return true if values has been changed
     */
    public static boolean setGeneralProperties(String[] propertyNames, Object[] values) throws PropertiesManager.PropertiesNotRegisteredException,
                                                                                        PropertiesManager.PropertiesReadException
    {   return setProperties(GENERAL_PROPERTIES, propertyNames, values); }
    
    /** set the value of the property
     *  @param id of a property context
     *  @param propertyName the name of the property
     *  @param value the value to set
     *  @boolean return true if the value has been changed
     */
    public static boolean setProperty(String id, String propertyName, Object value) throws PropertiesManager.PropertiesNotRegisteredException,
                                                                                        PropertiesManager.PropertiesReadException
    {   
        boolean succes = false;
        PropertiesContext context = getContext(id);
        
        if ( context != null )
        {   succes = PropertiesProvider.setProperty(context.getFileName(), propertyName, value); }
        
        return succes;
    }
    
    /** set the value of the properties.<br/>
     *  properties are saved only if all values have been applied
     *  @param id of a property context
     *  @param propertyNames the name of the properties
     *  @param values the value to set
     *  @boolean return true if the value has been changed
     */
    public static boolean setProperties(String id, String[] propertyNames, Object[] values)
                        throws PropertiesManager.PropertiesNotRegisteredException,
                               PropertiesManager.PropertiesReadException
    {   return setProperties(id, propertyNames, values, true); }
    
    /** set the value of the properties
     *  @param id of a property context
     *  @param propertyNames the name of the properties
     *  @param values the value to set
     *  @param saveIfAllSucceed indicates if properties must only be saved if all values have been applied
     *  @boolean return true if the value has been changed
     */
    public static boolean setProperties(String id, String[] propertyNames, Object[] values, boolean saveIfAllSucceed)
                        throws PropertiesManager.PropertiesNotRegisteredException,
                               PropertiesManager.PropertiesReadException
    {   
        boolean succes = false;
        PropertiesContext context = getContext(id);
        
        if ( context != null )
        {   succes = PropertiesProvider.setProperties(context.getFileName(), propertyNames, values, saveIfAllSucceed); }
        
        return succes;
    }
    
    
    
    
    /** declaration of a properties context */
    private static class PropertiesContext
    {
        private String            fileName;
        
        public PropertiesContext(String fileName)
        {   this.fileName        = fileName; }

        public String getFileName()
        {   return fileName; }
    }
    
    /** defines an exception that is throwed when properties have not be registered */
    public static class PropertiesNotRegisteredException extends Exception
    {
        public PropertiesNotRegisteredException(int propertiesId)
        {   super("Properties for id : " + propertiesId + " have not been registered."); }
    }
    
    /** defines an exception that is throwed when properties could not be read in the context file */
    public static class PropertiesReadException extends Exception
    {
        public PropertiesReadException(int propertiesId, PropertiesContext context)
        {   super("Properties for id : " + propertiesId + " could not been read from " + context.getFileName()); }
    }
    
    /** defines an exception that is throwed when properties could not be written in the context file */
    public static class PropertiesWriteException extends Exception
    {
        public PropertiesWriteException(int propertiesId, PropertiesContext context)
        {   super("Properties for id : " + propertiesId + " could not been written into " + context.getFileName()); }
    }
    
}
