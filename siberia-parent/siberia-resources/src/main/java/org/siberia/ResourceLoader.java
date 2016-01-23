/* 
 * Siberia resources : siberia plugin to facilitate resource loading
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
package org.siberia;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import org.apache.log4j.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.java.plugin.PluginClassLoader;
import org.java.plugin.registry.PluginDescriptor;
import org.siberia.exception.PluginAlreadyDeclaredException;
import org.siberia.exception.PluginDeclarationException;
import org.siberia.exception.ResourceException;

/**
 *
 *  Entity that is able to load resource file.<br>
 *  It provides functionnalities to  : <br><ul>
 *  <li>create an icon located in org/atom3/rc/img if <br>
 *  the path is not specified or everywhere if the path is specified.</li>
 *  <li>get the xml persistence declaration of an ColdType</li>
 *  <li>get any kind of resource as file</li>
 *  </ul>
 *
 *  Another functionnality is to get a file that is contains in a directory that is marked as a resource directory<br>
 *  in a plugin descriptor :<br>
 *  a plugin is able to declare many resource paths.<br>
 *  ResourceLoader is able to find a file by giving it the String "atom3;2::menu/main.xml". <br>
 *  In this case, it means that ResourceLoader have to search the resource path with id 2 declared in plugin descriptor of<br>
 *  plugin 'atom3', then the resource is to be located at {path}/menu/main.xml
 *
 * @author alexis
 */
public class ResourceLoader
{       
    /* the singleton */
    private static final  ResourceLoader            instance           = new ResourceLoader();
    
    /** logger */
    public  static Logger                           logger             = Logger.getLogger(ResourceLoader.class);
    
    /** map linking plugin id and their related PluginRcDeclaration */
    private        Map<String, PluginRcDeclaration> pluginDeclarations = null;
    
    /** tells if the resource loader has been initialized */
    private        boolean                          initialized        = false;
    
    /** name of the application */
    private        String                           applicationName    = null;
    
    /** id of the application */
    private        String                           applicationId      = null;
    
    /** operating system */
    private        OperatingSystem                  os                 = null;
    
    /** indicate if the debug mode is activated */
    private        boolean                          debugActivated     = false;
    
    /** Creates a new instance of IconLoader */
    private ResourceLoader()
    {   /* do nothing */ }
    
    /** singleton getter **/
    public static ResourceLoader getInstance()
    {   return instance; }
    
    /** return true if debug is activated
     *	@return a boolean
     */
    public boolean isDebugEnabled()
    {
	return this.debugActivated;
    }
    
    /** indicate if debug is activated
     *	@param enabled a boolean
     */
    public void setDebugEnabled(boolean enabled)
    {
	this.debugActivated = enabled;
    }
    
    /** return the detected operating system
     *	@return a OperatingSystem
     */
    public synchronized OperatingSystem getOperatingSystem()
    {
	if ( this.os == null )
	{
	    String osName = System.getProperty("os.name");
	    osName = osName.toLowerCase();
	    
	    if (osName.contains("windows"))
	    {
		this.os = OperatingSystem.WINDOWS;
		
		if ( osName.contains("vista") )
		{
		    this.os = OperatingSystem.WINDOWS_VISTA;
		}
	    }
	    else if (osName.contains("sunos"))
	    {
		this.os = OperatingSystem.SOLARIS;
	    }
	    else if ( (osName.contains("mac") && osName.contains("os") && osName.contains("x")) ||
		      osName.contains("mac os x") )
	    {
		this.os = OperatingSystem.MACOSX;
	    }
	    else
	    {
		/* default linux */
		this.os = OperatingSystem.LINUX;
	    }
	}
	
	return this.os;
    }
    
    /** return the name of the application
     *  @return the name of the application
     */
    public String getApplicationName()
    {   return this.applicationName; }
    
    /** initialize the name of the application
     *  @param name the name of the application
     */
    public void setApplicationName(String name)
    {   this.applicationName = name; }
    
    /** return the id of the application
     *  @return the id of the application
     */
    public String getApplicationId()
    {   return this.applicationId; }
    
    /** initialize the id of the application
     *  @param name the id of the application
     */
    public void setApplicationId(String id)
    {   this.applicationId = id; }
    
    /** return the path to the application directory where files specific to the application 
     *  can be created
     *  @return a String representing the path to a directory
     */
    public String getApplicationHomeDirectory()
    {   
        if ( this.applicationName == null )
            throw new RuntimeException("application name not set");
        
        return System.getProperty("user.home") +
               java.io.File.separator + "." + this.applicationName;
    }
    
    /** method that return the ClassLoader associated with a plugin
     *  @param id the id of a plugin
     *  @return a ClassLoader
     */
    public ClassLoader getPluginClassLoader(String id)
    {   ClassLoader cl = null;
        if ( this.pluginDeclarations != null )
        {   logger.debug("searching for the class loader of plugin '" + id + "'");
            
            PluginRcDeclaration decl = this.pluginDeclarations.get(id);
            if ( decl != null )
            {   cl = decl.getClassLoader(); }
        }
        return cl;
    }
    
    /** method that return the ClassLoader associated with the application plugin
     *  @return a ClassLoader
     */
    public ClassLoader getApplicationPluginClassLoader()
    {   return this.getPluginClassLoader(this.getApplicationId()); }
    
    /** method that return the ClassLoader associated with a class represented asplugin
     *  @param pluginClassName the name of a class
     *  @return a ClassLoader
     */
    public ClassLoader getPluginClassLoaderForClassName(String pluginClassName)
    {   ClassLoader classLoader = null;
        if ( pluginClassName != null )
        {   /* create PluginClass to determine plugin id */
            PluginClass cl = new PluginClass(pluginClassName);
            
            classLoader = this.getPluginClassLoader(cl.getPlugin());
        }
        return classLoader;
    }
    
    /** declare a new plugin
     *  @param id the id of the plugin
     *  @param classLoader a ClassLoader linked with the plugin
     *  @param dirs an array of String representing the resource directories declared by the plugin
     */
    public void declarePlugin(String id, ClassLoader classLoader, String... dirs)
            throws PluginDeclarationException, PluginAlreadyDeclaredException
    {   if ( pluginDeclarations != null )
        {   if ( pluginDeclarations.containsKey(id) )
                throw new PluginAlreadyDeclaredException(id);
        }
        else
        {   pluginDeclarations = new HashMap<String, PluginRcDeclaration>(); }
        
        try
        {   pluginDeclarations.put(id, new PluginRcDeclaration(classLoader, dirs));
            
            if ( ! initialized )
                initialized = true;
        }
        catch(Exception e)
        {   throw new PluginDeclarationException(id, e); }
    }
    
    /** method that checked if the loader has been initialized, if not,
     *  then a RuntimeException is throwed
     */
    private void checkInitialization()
    {   if ( ! initialized )
            throw new RuntimeException("ResourceLoader is not initialized");
    }
    
    /* #########################################################################
     * ############################ Class loader ###############################
     * ######################################################################### */
    
    /** return a Class
     *  @param completePath a string representing a class in siberia.<br/>
     *          it can be "siberia::org.siberia.Action" or "org.siberia.Action" but it will be less efficient
     *  @return a Class or null if not found
     *
     *  @exception ResourceException if the Class was not found
     */
    public Class getClass(String completePath) throws ResourceException
    {   PluginClass cl = null;
        Class c = null;
        try
        {   cl = new PluginClass(completePath);
            
            c = this.getClass(cl.getClassName(), cl.getPlugin());
        }
        catch(IllegalArgumentException e)
        {   logger.debug("unable to find class for '" + completePath + "'", e);
            throw new ResourceException(completePath);
        }
        
        return c; 
    }
    
    /** return a Class
     *  @param className the complete name of the class
     *  @return a Class or null if not found
     *
     *  Calling this methods will cause to search for the class in all ClassLoader of the application
     *
     *  @exception ResourceException if the Class was not found
     */
    public Class loadClass(String className) throws ResourceException
    {   return this.getClass(className, null); }
    
    /** return a Class
     *  @param className the complete name of the class
     *  @param id a plugin id, if id is null, then the search will be done on all plugins and therefore<br/>
     *          it could be time costly
     *  @return a Class or null if not found
     *
     *  @exception ResourceException if the Class was not found
     */
    public Class getClass(String className, String id) throws ResourceException
    {   Class c = null;
        checkInitialization();
        
        if ( id != null )
        {   PluginRcDeclaration decl = this.pluginDeclarations.get(id);
            if ( decl != null )
            {   try
                {   ClassLoader cl = decl.getClassLoader();
                    if ( cl != null )
                    {   c = cl.loadClass(className); }
                }
                catch(ClassNotFoundException e)
                {   /* search in all plugin */ }
            }
        }
        else
        {   try
            {   c = Class.forName(className); }
            catch (ClassNotFoundException ex)
            {   /*ex.printStackTrace();*/ }
        }
        
        if ( c == null )
        {   Iterator<String> plugins = this.pluginDeclarations.keySet().iterator();
            while(plugins.hasNext())
            {   String current = plugins.next();
                
                /* no need to search with the classLoader we try earlier */
                if ( ! current.equals(id) )
                {   PluginRcDeclaration decl = this.pluginDeclarations.get(current);
                    if ( decl != null )
                    {   try
                        {   ClassLoader cl = decl.getClassLoader();
                            if ( cl != null )
                            {   c = cl.loadClass(className);
                                if ( c != null )
                                    break;
                            }
                        }
                        catch(ClassNotFoundException e)
                        {   /* search in all plugin */ }
                    }
                }
            }
        }
        
        if ( c == null )
            throw new ResourceException(className);
        
        return c;
    }
    
    /** return the id of the plugin that declared the given class
     *  @param c a Class
     *  @return a String
     */
    public String getPluginIdWhichDeclare(Class c)
    {   
        String pluginId = null;
        
        if ( c != null )
        {   ClassLoader loader = c.getClassLoader();
            
            if ( loader instanceof PluginClassLoader )
            {   PluginDescriptor desc = ((PluginClassLoader)loader).getPluginDescriptor();
                if ( desc != null )
                {   pluginId = desc.getId(); }
            }
        }
        
        return pluginId;
    }
    
    /* #########################################################################
     * ############################ Image loader ###############################
     * ######################################################################### */
    
    /** return an image placed anywhere in a rc directory of a plugin.<br>
     *  this methods search in declared rc directories of every plugin if no plugin id is specified in the path.<br>
     *  @param shortPath the path where to find the resource<br> this path should be "atom3;1::img/<iconName>.png"<br>
     *          but the most efficient and sure method to get resource is to prefix the name of the resource with the plugin id<br>
     *          like this "atom3::img/<iconName>.png" . in fact, the set of resources is reduced.
     *          If a plugin declare several resource directories, then, you can find a resource by giving the index of declaration of
     *          the considered resource directory : <br/>
     *  @return an URL
     *
     *  @exception throws ColdResourceException if resource could not be found
     */
    public Image getImageNamed(String shortPath) throws ResourceException
    {   URL url = this.getRcResource(shortPath == null ? null : shortPath);
        
        if ( url != null )
        {   return new ImageIcon(url).getImage(); }
        else
            throw new ResourceException(shortPath);
    }
    
    /* #########################################################################
     * ############################ Icon loader ################################
     * ######################################################################### */
    
    /** return an icon placed anywhere in a rc directory of a plugin.<br>
     *  this methods search in declared rc directories of every plugin if no plugin id is specified in the path.<br>
     *  @param shortPath the path where to find the resource<br> this path should be "atom3;1::img/<iconName>.png"<br>
     *          but the most efficient and sure method to get resource is to prefix the name of the resource with the plugin id<br>
     *          like this "atom3::img/<iconName>.png" . in fact, the set of resources is reduced.
     *          If a plugin declare several resource directories, then, you can find a resource by giving the index of declaration of
     *          the considered resource directory : <br/>
     *  @return an URL
     *
     *  @exception throws ColdResourceException if resource could not be found
     */
    public Icon getIconNamed(String shortPath) throws ResourceException
    {   URL url = this.getRcResource(shortPath == null ? null : shortPath);
        return this.createIcon(url);
    }
    
    /** return an Icon with the given url
     *  @param url an URL
     *  @return an Icon or null if failed
     */
    public Icon createIcon(URL url) throws ResourceException
    {   if ( url != null )
        {   return new ImageIcon(url); }
        else
            throw new ResourceException(url.toString());
    }
    
    /* #########################################################################
     * ########################## Generic methods ##############################
     * ######################################################################### */
    
    /** return a resource with a full path
     *  @param the path where to find the resource<br>
     *  if path does not start with /, the search path will be : the location of this class + / + path
     *  else                            the search path will be : one element of the classpath + / + path 
     *  @param classLoader a ClassLoader
     *  @return a URL
     *
     *  @exception throws ColdResourceException if resource could not be found
     **/
    public URL getResource(String path, ClassLoader classLoader) throws ResourceException
    {   URL urlResult = null;
        if ( path != null )
        {   if ( classLoader != null )
            {   urlResult = classLoader.getResource(path); }
            else
            {   this.getClass().getResource(path); }
        }
        
        if ( urlResult == null )
            throw new ResourceException(path);
        
        return urlResult;
    }
    
    /** return a resource placed anywhere in a rc directory of a plugin.<br>
     *  this methods search in declared rc directories of every plugin.<br>
     *  @param shortPath the path where to find the resource<br> this path should be "img/<iconName>.png" or "menu/main.xml"<br>
     *          but the most efficient and sure method to get resource is to prefix the name of the resource with the plugin id<br>
     *          like this "atom3::img/<iconName>.png" or "atom3::menu/main.xml". in fact, the set of resources is reduced.
     *          If a plugin declare several resource directories, then, you can find a resource by giving the index of declaration of
     *          the considered resource directory : <br/>
     *              for example : "atom3;2::menu/main.xml".
     *  @return an URL
     *
     *  @exception throws ColdResourceException if resource could not be found
     **/
    public URL getRcResource(String shortPath) throws ResourceException
    {   
        this.checkInitialization();
        
        URL url = null;
        
        ResourceException exception = null;
        
        if ( shortPath != null )
        {
            PluginResource resource = null;
            try
            {   resource = new PluginResource(shortPath); }
            catch(IllegalArgumentException e)
            {   throw new ResourceException("shortPath"); }
            
            Map<ClassLoader, Set<String>> candidates = new HashMap<ClassLoader, Set<String>>();
            
            if ( resource.getPlugin() != null )
            {   PluginRcDeclaration decl = this.pluginDeclarations.get(resource.getPlugin());
                if ( decl != null )
                {   candidates.put(decl.getClassLoader(), decl.getPathCandidates(resource)); }
            }
            
            Iterator<ClassLoader> loaders = candidates.keySet().iterator();
            while(loaders.hasNext())
            {   ClassLoader cl = loaders.next();
                
                if ( cl != null )
                {   Iterator<String> s = candidates.get(cl).iterator();
                    while(s.hasNext())
                    {   String currentPath = s.next();
                        
                        try
                        {   url = this.getResource(currentPath, cl);
                            break;
                        }
                        catch(ResourceException e)
                        {   if ( exception != null )
                                exception = e;
                        }
                    }
                }
                
                if ( url != null )
                    break;
            }
        }
        
        if ( url == null )
        {   if ( exception == null )
                exception = new ResourceException(shortPath);
            throw exception;
        }
        
        return url;
    }
    
    /** return a resource with a full path
     *  @param the path where to find the resource<br>
     *  if path does not start with /, the search path will be : the location of this class + / + path
     *  else                            the search path will be : one element of the classpath + / + path 
     *  @return an URL
     *
     *  @exception throws ColdResourceException if resource could not be found
     **/
    public URL getResource(String path) throws ResourceException
    {   return this.getResource(path, null); }
    
    /** return a resource with a full path
     *  @param the path where to find the resource<br>
     *  if path does not start with /, the search path will be : the location of this class + / + path
     *  else                            the search path will be : one element of the classpath + / + path 
     *  @return an InputStream
     *
     *  @exception throws ColdResourceException if resource could not be found
     **/
    public InputStream getResourceAsStream(String path) throws ResourceException
    {   return this.getResourceAsStream(path, null); }
    
    /** return a resource with a full path
     *  @param the path where to find the resource<br>
     *  if path does not start with /, the search path will be : the location of this class + / + path
     *  else                            the search path will be : one element of the classpath + / + path 
     *  @param classLoader a ClassLoader
     *  @return an InputStream
     *
     *  @exception throws ColdResourceException if resource could not be found
     **/
    private InputStream getResourceAsStream(String path, ClassLoader classLoader) throws ResourceException
    {   URL u = this.getResource(path, classLoader);
        if ( u != null )
        {   try
            {   return u.openStream(); }
            catch(IOException e)
            {   throw new ResourceException("unable to find " + path + " with the given classloader"); }
        }
        else 
            return null;
    }
    
    /** ########################################################################
     *  ############# Method that allow to simplify the creation ###############
     *  ############### of a file in the application directory #################
     *  ######################################################################## */
    
    /** return true if the file exists in the application directory
     *  @param filename the name of the file to create
     *  @return true if the file exists
     */
    public boolean doesApplicationFileExist(String filename)
    {
        File f = new File(this.getApplicationHomeDirectory() + java.io.File.separator + filename);
        
        return f.exists();
    }
    
    /** method that allow to get a file in the application user directory
     *  if the file does not exists, it is created
     *  @param filename the name of the file to create
     *  @return a File
     *
     *  @exception IOException if the file already exists
     */
    public File getApplicationFile(String filename) throws IOException
    {
        return this.getApplicationFile(filename, true);
    }
    
    /** method that allow to get a file in the application user directory
     *  @param filename the name of the file to create
     *  @param create true to create the file if it does not exists
     *  @return a File
     *
     *  @exception IOException if the file already exists
     */
    public File getApplicationFile(String filename, boolean create) throws IOException
    {
        File f = null;
        
        f = new File(this.getApplicationHomeDirectory() + java.io.File.separator + filename);
        
        File parent = f.getParentFile();
        
        if ( ! parent.exists() )
        {
            parent.mkdirs();
        }
        parent = null;
        
        if ( create )
        {
            if ( ! f.createNewFile() )
            {
                f = null;
                throw new IOException("unable to create file '" + f.getAbsolutePath() + "'");
            }
        }
        
        return f;
    }
    
    /** ########################################################################
     *  ########################## i18n methods  ###############################
     *  ######################################################################## */
    
    /** return the ResourceBundle contained by the plugin with the given id
     *  @param pluginId the id of the plugin
     *  @param basename the name of the Resourcebundle
     *  @return a Properties
     *
     *	this method exists cause getResourceBundle
     *
     *  @exception MissingResourceException if resource not found
     */
    public Properties getPluginDeclaration(String pluginId, String basename) throws MissingResourceException
    {
	Properties properties = null;
        
        if ( pluginId == null )
        {
            throw new IllegalArgumentException("pluginId must be non null");
        }
        
        ClassLoader loader = this.getPluginClassLoader(pluginId);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("classloader for plugin '" + pluginId + "' : " + loader + " " +
			 (loader == null ? "" : loader.getClass() ));
	}
        
        if ( loader != null )
        {
	    URL url = loader.getResource(basename + ".properties");
	    if ( url != null )
	    {
		try
		{
		    InputStream stream = url.openStream();

		    properties = new Properties();

		    properties.load(stream);
		}
		catch (IOException ex)
		{
		    properties = null;
		}
	    }
        }
        
        if ( properties == null )
        {
            throw new MissingResourceException(basename, pluginId, null);
        }
        
        return properties;
    }
    
    /** return the ResourceBundle represented by the given resource representation
     *  @param shortPath the path where to find the resource<br> this path should be "img/<iconName>.png" or "menu/main.xml"<br>
     *          but the most efficient and sure method to get resource is to prefix the name of the resource with the plugin id<br>
     *          like this "atom3::img/<iconName>.png" or "atom3::menu/main.xml". in fact, the set of resources is reduced.
     *          If a plugin declare several resource directories, then, you can find a resource by giving the index of declaration of
     *          the considered resource directory : <br/>
     *              for example : "atom3;2::menu/main.properties".
     *  @return a ResourceBundle
     *
     *  @exception MissingResourceException if resource not found
     */
    public ResourceBundle getResourceBundle(String shortPath) throws MissingResourceException
    {
	ResourceBundle rb = null;
	
	try
	{	 
	    PluginResource rc = new PluginResource(shortPath);
	    
	    ClassLoader classLoader = this.getPluginClassLoader(rc.getPlugin());
	    
	    if ( classLoader == null )
	    {
		throw new MissingResourceException(shortPath, shortPath, null);
	    }
	    else
	    {
		String basename = null;
		
		/** try to build the basename that represent the resource */
		PluginRcDeclaration rcDeclaration = this.pluginDeclarations.get(rc.getPlugin());
		
		if ( rcDeclaration != null )
		{   
		    String path = rcDeclaration.getRcDirectoryPath(rc.getRcDirIndex() - 1);
		    
		    if ( path != null && rc.getPath() != null )
		    {			
			StringBuffer buffer = new StringBuffer(path.length() + rc.getPath().length());
			
			buffer.append(path);
			buffer.append(rc.getPath());
			
			/* transform path ...
			 *  - delete extension
			 *  - replace / by .
			 */
			int lastPointIndex = buffer.lastIndexOf(".");
			
			if ( lastPointIndex >= 0 )
			{
			    buffer.delete(lastPointIndex, buffer.length());
			}
			
			for(int index = buffer.length() - 1; index >= 0; index--)
			{
			    char current = buffer.charAt(index);
			    
			    if ( current == '/' )
			    {
				buffer.deleteCharAt(index);
				buffer.insert(index, '.');
			    }
			}
			
			basename = buffer.toString();
		    }
		}
		
		if ( basename == null )
		{
		    throw new MissingResourceException(shortPath, shortPath, null);
		}
		else
		{
		    rb = ResourceBundle.getBundle(basename, Locale.getDefault(), classLoader);
		}
	    }
	}
	catch(IllegalArgumentException e)
	{
	    logger.error("unable to build PluginResource according to '" + shortPath + "'", e);
	    throw new MissingResourceException(shortPath, shortPath, null);
	}
	
	return rb;
    }
    
    /** return the ResourceBundle contained by the plugin with the given id
     *  @param pluginId the id of the plugin
     *  @param basename the name of the Resourcebundle
     *  @return a ResourceBundle
     *
     *  @exception MissingResourceException if resource not found
     */
    public ResourceBundle getResourceBundle(String pluginId, String basename) throws MissingResourceException
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling(" + pluginId + ", " + basename + ")");
	}
	
        ResourceBundle rb = null;
        
        if ( pluginId == null )
        {
            throw new IllegalArgumentException("pluginId must be non null");
        }
        
        ClassLoader loader = this.getPluginClassLoader(pluginId);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("classloader for plugin '" + pluginId + "' : " + loader + " " +
			 (loader == null ? "" : loader.getClass() ));
	}
        
        if ( loader != null )
        {
            rb = ResourceBundle.getBundle(basename, Locale.getDefault(), loader);
        }
        
        if ( rb == null )
        {
            throw new MissingResourceException(basename, pluginId, null);
        }
        
        return rb;
    }
    

}
