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
package org.siberia.env;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.EventListenerList;
import org.apache.log4j.Logger;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.boot.ApplicationPlugin;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ManifestProcessingException;
import org.java.plugin.registry.PluginAttribute;
import org.java.plugin.registry.PluginDescriptor;
import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;

/**
 *
 * Singleton allowing to get some information about plugin extension-points
 * and environment.
 *
 * @author alexis
 */
public class PluginResources
{   
    /** logger */
    private Logger                            logger                 = Logger.getLogger(PluginResources.class);
    
    /** application plugin */
    private PluginManager                     manager                = null;
    
    /** resources path */
    private Map<String, Set<String>>          resourcePaths          = null;
    
    /** singleton */
    private static PluginResources            singleton              = null;
    
    /** event listener list */
    private EventListenerList                 listeners              = new EventListenerList();
    
    /** initialize the singleton with the given PluginManager
     *  @param pluginManager the pluginManager of the application
     */
    public static synchronized void initialize(PluginManager manager)
    {   getInstance().manager = manager; }
    
    /** return the singleton
     *  @return an instance of PluginResources
     */
    public static synchronized PluginResources getInstance()
    {   if ( singleton == null )
        {   singleton = new PluginResources(); }
        
        return singleton;
    }
    
    /** Creates a new instance of PluginContext */
    private PluginResources()
    {   }
    
    /** ########################################################################
     *  ######################## Plugin related methods ########################
     *  ######################################################################## */
    
    /** return the ids of all loaded plugin
     *  @return an Iterator over all plugin ids
     */
    public Iterator<String> pluginIds()
    {   
        Collection collec = this.manager.getRegistry().getPluginDescriptors();
        Iterator it = collec.iterator();
        Set<String> ids = new HashSet<String>(collec.size());
        
        while(it.hasNext())
        {   Object current = it.next();
            
            if ( current instanceof PluginDescriptor )
            {   ids.add( ((PluginDescriptor)current).getId() ); }
        }
        
        return ids.iterator();
    }
    
    /** return true if the manager has already a plugin with the given id
     *	@param id the id of a plugin
     *	@return true if the manager has already a plugin with the given id
     */
    public boolean containsPluginWithId(String id)
    {
	boolean result = false;
	
        Collection collec = this.manager.getRegistry().getPluginDescriptors();
        Iterator it = collec.iterator();
        
        while(it.hasNext())
        {   Object current = it.next();
            
            if ( current instanceof PluginDescriptor )
            {   
		result = ((PluginDescriptor)current).getId().equals(id);
		
		if ( result )
		{
		    break;
		}
	    }
        }
        
        return result;
	
    }
    
    /** return the location where are stored the plugins
     *	@return a File representing a directory
     */
    public File getPluginsDirectory()
    {
	File f = null;
	
	Collection collec = this.manager.getRegistry().getPluginDescriptors();
	if ( collec != null )
	{
	    Iterator it = collec.iterator();
	    while ( it.hasNext() && f == null )
	    {
		Object o = it.next();
		
		if ( o instanceof PluginDescriptor )
		{
		    URL url = ((PluginDescriptor)o).getLocation();
		    
		    if ( url != null && url.getProtocol().equals("jar") )
		    {
			String path = url.getPath();
			int jarIndex = path.lastIndexOf('!');
			
			if ( jarIndex > -1 )
			{
			    // 5 --> don't take file:
			    path = path.substring(5, jarIndex);
//			    file:/mnt/projects/offshore/plugins/siberia-docking-components-0.0.1.zip!/plugin.xml
			    System.out.println("path : " + path);

			    f = new File(path);
			    if ( f != null )
			    {
				f = f.getParentFile();
			    }
			}
		    }
		    else if ( url != null && url.getProtocol().equals("file") )
		    {
			f = new File(url.getPath());
			if ( f != null )
			{
			    f = f.getParentFile();
			}
		    }
		}
	    }
	}
	
	return f;
    }
    
    /** register a new plugin build file<br>
     *	the given file is copied in the plugins directory<br>
     *	if a version of the plugin is found (whatever version), then this existing version is unregistered before installing new version<br>
     *	@param filename the name of the file to create
     *	@param buildFile the file representing a PluginBuild
     *	@param id the id of the new plugin
     *
     *	@return an array of PluginResourceModification representing the action made to add and register this plugin
     */
    public PluginResourceModification[] addPlugin(String filename, File buildFile, String id) throws MalformedURLException
    {
	PluginResourceModification[] modifications = null;
	
	File f = new File(this.getPluginsDirectory(), filename);
	
	InputStream in = null;
	OutputStream out = null;
	
        try
        {   in = new FileInputStream(buildFile);
            out = new FileOutputStream(f);

            byte[] buffer = new byte[2*1024];

            File parent = f.getParentFile();
            if ( ! parent.exists() )
	    {	parent.mkdirs(); }
            if ( ! f.exists() )
	    {	f.createNewFile(); }

	    int bytesReadCount = 0;
            int nbLecture;
            while( (nbLecture = in.read(buffer)) != -1 )
            {   
		out.write(buffer, 0, nbLecture);
		
		bytesReadCount += nbLecture;
	    }
	    
	    int index = 0;
	    
	    /* map collecting plugin location */
	    Map<String, URL> pluginsURL = new HashMap<String, URL>();
	    
	    Collection<String> unregisteredPlugins = null;
	    
	    Iterator<PluginDescriptor> allDescriptors = this.manager.getRegistry().getPluginDescriptors().iterator();
	    while(allDescriptors.hasNext())
	    {
		PluginDescriptor desc = allDescriptors.next();
		
		if ( desc != null )
		{
		    pluginsURL.put(desc.getId(), desc.getLocation());
		}
	    }
	    
	    if ( id != null )
	    {
		if ( this.containsPluginWithId(id) )
		{
		    unregisteredPlugins = this.manager.getRegistry().unregister(new String[]{id});

		    modifications = new PluginResourceModification[2];

		    modifications[index++] = new PluginResourceModification(PluginResourceModification.Kind.REMOVE, id);
		}
		else
		{
		    modifications = new PluginResourceModification[1];
		}

		try
		{
		    PluginManager.PluginLocation location = org.java.plugin.standard.StandardPluginLocation.create(f);
		    this.manager.publishPlugins(new PluginManager.PluginLocation[]{location});
		}
		catch (ManifestProcessingException ex)
		{
		    ex.printStackTrace();
		}
		this.manager.activatePlugin(id);

		/** activate the plugins that were unregister during modifications */
		if ( unregisteredPlugins != null )
		{
		    Iterator<String> ids = unregisteredPlugins.iterator();

		    Map<String, PluginManager.PluginLocation> locations = new HashMap<String, PluginManager.PluginLocation>();

		    while(ids.hasNext())
		    {
			String cid = ids.next();

			int indexAt = cid.indexOf('@');

			if ( indexAt != -1 )
			{
			    cid = cid.substring(0, indexAt);
			}

			if ( ! cid.equals(id) )
			{
			    URL url = pluginsURL.get(cid);

			    if ( url != null )
			    {
				//jar:file:/mnt/projects/offshore/plugins/siberia-binding-0.0.1.zip!/plugin.xml
				String urlToString = url.toString();
				urlToString = urlToString.substring(9, urlToString.length() - 12);
				locations.put(cid, org.java.plugin.standard.StandardPluginLocation.create(new File(urlToString)));
			    }
			}
		    }

		    /** register locations */
		    List<PluginManager.PluginLocation> locationsList = new ArrayList<PluginManager.PluginLocation>(locations.values());
		    this.manager.publishPlugins( (PluginManager.PluginLocation[])locationsList.toArray(new PluginManager.PluginLocation[locationsList.size()]) );

		    ids = locations.keySet().iterator();

		    while(ids.hasNext())
		    {
			String cid = ids.next();

			System.out.println("plugin unregistered : " + cid);

			if (cid != null )
			{
			    Plugin plugin = this.manager.getPlugin(cid);

			    if ( plugin != null )
			    {
				PluginDescriptor descriptor = plugin.getDescriptor();

				if ( descriptor != null )
				{
				    if ( ! this.manager.isPluginActivated(descriptor) )
				    {
					this.manager.activatePlugin(cid);
				    }
				}
			    }
			}
		    }
		}
	    
		modifications[index++] = new PluginResourceModification(PluginResourceModification.Kind.ADD, id);
	    }
        }
        catch( Exception e )
        {   e.printStackTrace(); }
        finally
        {   try
            {   in.close(); }
            catch(Exception e)
            {   e.printStackTrace(); }
            try
            {   out.close(); }
            catch(Exception e)
            {   e.printStackTrace(); }
        }
	
	return modifications;
    }
    
    /** remove all plugins actually in the plugins directory */
    public void removeAllPlugins()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling removeAllPlugins");
	}
	
	File pluginDir = this.getPluginsDirectory();
	
	File[] files = pluginDir.listFiles();
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("plugin directory contains " + (files == null ? 0 : files.length) + " plugins");
	}
	
	if ( files != null )
	{
	    for(int i = 0; i < files.length; i++)
	    {
		File current = files[i];
		
		if ( current != null )
		{
		    if ( current.delete() )
		    {
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("plugin file : " + current + " deleted from file-system");
			}
		    }
		    else
		    {
			logger.error("unable to delete " + current);
		    }
		}
	    }
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of removeAllPlugins");
	}
    }
    
    /** unregister a plugin<br>
     *	@param id the id of the plugin to remove
     */
    public void removePlugin(String id)
    {
	// TODO
    }
    
    /** return the number of plugin loaded
     *  @return the number of plugin loaded
     */
    public int getPluginCount()
    {   return this.manager.getRegistry().getPluginDescriptors().size(); }
    
    /** ########################################################################
     *  ####################### Resources related methods ######################
     *  ######################################################################## */
    
    /** methods that fill the map of resource paths for a given plugin id
     *  @param id the id of a plugin
     */
    private void fillResourcePath(String id)
    {   if ( id != null )
        {   
            boolean present = true; // optimistic
            if ( this.resourcePaths != null )
            {   if ( ! this.resourcePaths.containsKey(id) )
                    present = false;
            }
            else
                present = false;
            
            if ( ! present )
            {   if ( this.resourcePaths == null )
                    this.resourcePaths = new HashMap<String, Set<String>>();

                PluginDescriptor plugin = this.manager.getRegistry().getPluginDescriptor(id);

                if ( plugin != null )
                {   PluginAttribute attr = ((PluginDescriptor)plugin).getAttribute("resources");

                    Collection subAttrs = attr.getSubAttributes();
                    if ( subAttrs != null )
                    {   Iterator attrIt = subAttrs.iterator();

                        HashSet<String> rcPaths = null;

                        while(attrIt.hasNext())
                        {   Object currentAttr = attrIt.next();

                            if ( currentAttr instanceof PluginAttribute )
                            {   String path = ((PluginAttribute)currentAttr).getValue();
                                if ( path != null )
                                {   if ( ! path.endsWith(File.separator) )
                                        path = path + File.separator;
                                    if ( rcPaths == null )
                                        rcPaths = new HashSet<String>();

                                    rcPaths.add( path );
                                }
                            }
                        }

                        this.resourcePaths.put(id, rcPaths);
                    }
                }
            }
        }
    }
    
    /** methods that return the path declared by plugin with id 'id' with id 'pathId'
     *  @param id the id of a plugin
     *  @param pathId the id of the resource path declared by the plugin
     */
    public String getResourcePath(String id, String pathId)
    {   String path = null;
        if ( id != null )
        {   
            PluginDescriptor plugin = this.manager.getRegistry().getPluginDescriptor(id);

            if ( plugin != null )
            {   PluginAttribute attr = ((PluginDescriptor)plugin).getAttribute("resources");

                PluginAttribute subAttr = attr.getSubAttribute(pathId);
                if ( subAttr != null )
                {   path = subAttr.getValue();
                    
                    if ( ! path.endsWith(File.separator) )
                        path = path + File.separator;
                }
            }
        }
        
        return path;
    }
    
    /** return a set of paths poiting out resources
     *  @param ids id of plugin that declared resources path
     *  @return a set of String
     */
    public Set<String> getResourcePaths(String... ids)
    {   Set<String> paths = null;
        if ( ids != null )
        {   for(int i = 0; i < ids.length; i++)
            {   String currentId = ids[i];
                if ( currentId != null )
                {   this.fillResourcePath(currentId);

                    Set<String> p = this.resourcePaths.get(currentId);

                    if ( p != null )
                    {   if ( paths == null )
                            paths = new HashSet<String>();
                        paths.addAll(p);
                    }
                }
            }
        }
        
        if ( paths == null )
            paths = Collections.EMPTY_SET;
        
        return paths;
    }
    
    /** return a set of paths poiting out resources
     *  @return a set of String
     */
    public Set<String> getResourcePaths()
    {   logger.debug("calling getResourcePaths on every plugin is not efficient. prefer " +
                     "indicating the plugin owner by prefixing resource with " +
                     "'{plugin-id}{;{index of the resource directory in the plugi declaration}}::{name of the resource}'");
        Collection collec = this.manager.getRegistry().getPluginDescriptors();
        String[] ids = null;
        if ( collec != null )
        {   ids = new String[collec.size()];
            
            Iterator it = collec.iterator();
            int index = 0;
            while(it.hasNext())
            {   Object current = it.next();
                
                if ( current instanceof PluginDescriptor )
                {   ids[index++] = ((PluginDescriptor)current).getId(); }
            }
        }
        
        return this.getResourcePaths(ids);
    }
    
    /** return the class loader associated with the plugin which id is 'id'
     *  @param id the id of the plugin
     *  @return a ClassLoader
     */
    public ClassLoader getClassLoader(String id)
    {   ClassLoader loader = null;
        
        try
        {
            PluginDescriptor desc = this.manager.getPlugin(id).getDescriptor();
        
            if ( desc != null )
                loader = this.manager.getPluginClassLoader(desc);
        }
        catch(PluginLifecycleException e)
        {   e.printStackTrace(); }
        
        return loader;
    }
    
    /** return the ClassLoader for a given object
     *  @param object an Object
     *  @return the ClassLoader for a given object
     */
    public ClassLoader getClassLoaderFor(Object object)
    {   ClassLoader cloader = null;
        Plugin plugin = this.manager.getPluginFor(object);
        if ( plugin != null )
        {   cloader = this.manager.getPluginClassLoader(plugin.getDescriptor()); }
        return cloader;
    }
    
    /** ########################################################################
     *  ###################### Extensions related methods ######################
     *  ######################################################################## */
    
    /** return all extensions for an extension-id
     *  @param id the extension id
     *  @return a Set of SiberExtension
     */
    public Set<SiberExtension> getExtensions(String id)
    {   
        Set<SiberExtension> result = new HashSet<SiberExtension>();
            
        Iterator it = this.manager.getRegistry().getPluginDescriptors().iterator();
        while(it.hasNext())
        {   Object current = it.next();
            if ( current != null )
            {   if ( current instanceof PluginDescriptor )
                {   String pluginId = ((PluginDescriptor)current).getId();
                    Iterator it2 = ((PluginDescriptor)current).getExtensions().iterator();

                    while(it2.hasNext())
                    {   Object currentExt = it2.next();
                        if ( currentExt != null && currentExt instanceof Extension )
                        {   Extension ext = (Extension)currentExt;

                            if ( ext.getExtendedPointId().equals(id) )
                            {   result.add(new SiberExtension(ext, pluginId)); }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    /** ########################################################################
     *  ################### PluginResourcesListener methods ####################
     *  ######################################################################## */
    
    /** add a PluginResourcesListener
     *	@param listener a PluginResourcesListener
     */
    public void addPluginResourcesListener(PluginResourcesListener listener)
    {
	this.listeners.add(PluginResourcesListener.class, listener);
    }
    
    /** remove a PluginResourcesListener
     *	@param listener a PluginResourcesListener
     */
    public void removePluginResourcesListener(PluginResourcesListener listener)
    {
	this.listeners.remove(PluginResourcesListener.class, listener);
    }
    
    /** warn PluginResourcesListener with the given modifications
     *	@param source
     *	@param modifications an array of PluginResourceModification
     */
    public void warnPluginResourcesListener(Object source, PluginResourceModification[] modifications)
    {
	this.firePluginResourcesEvent(source, modifications);
    }
    
    /** fire a PluginResourcesEvent
     *	@param source
     *	@param modifications an array of PluginResourceModification
     */
    protected void firePluginResourcesEvent(Object source, PluginResourceModification[] modifications)
    {
	if ( this.listeners != null )
	{
	    Object[] objects = this.listeners.getListeners(PluginResourcesListener.class);
	    
	    if ( objects != null )
	    {
		PluginResourcesEvent.Phase[] phases = PluginResourcesEvent.Phase.values();
		
		PluginResourcesEvent evt = null;
		
		for(int i = 0; i < phases.length; i++)
		{
		    evt = new PluginResourcesEvent(source, phases[i], modifications);
		
		    for(int j = 0; j < objects.length; j++)
		    {
			Object current = objects[j];

			if ( current instanceof PluginResourcesListener )
			{
			    ((PluginResourcesListener)current).pluginContextChanged(evt);
			}
		    }
		}
	    }
	}
    }
    
}
