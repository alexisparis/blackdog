/* 
 * Siberia components : siberia plugin for graphical components
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
package org.siberia.ui.bar;

import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.bar.customizer.MenuBarCustomizer;
import org.siberia.bar.customizer.PopupMenuCustomizer;
import org.siberia.bar.customizer.ToolBarCustomizer;
import org.siberia.bar.factory.BarFactory;
import org.siberia.bar.provider.BarProvider;
import org.siberia.env.PluginResources;
import org.siberia.env.PluginResourcesEvent;
import org.siberia.env.PluginResourcesListener;
import org.siberia.env.SiberExtension;
import org.siberia.exception.ResourceException;
import org.siberia.ui.action.TypeAction;
import org.siberia.ui.bar.customizer.PluginMenuBarCustomizer;
import org.siberia.ui.bar.customizer.PluginPopupMenuCustomizer;
import org.siberia.ui.bar.customizer.PluginToolBarCustomizer;
import org.siberia.utilities.cache.GenericCache2;
import org.siberia.utilities.util.ClassMap;
import org.siberia.utilities.util.IteratorGroup;

/**
 *
 * singleton that is able to create Toolbar, Menubar and context menus
 * in a Siberia application
 *
 * @author alexis
 */
public class PluginBarFactory extends BarFactory implements PluginResourcesListener
{
    /** singleton */
    private static PluginBarFactory factory = new PluginBarFactory();
    
    static
    {
	PluginResources.getInstance().addPluginResourcesListener(factory);
    }
    
    /** logger */
    private Logger           logger = Logger.getLogger(PluginBarFactory.class);
    
    /** cache */
    private ContextMenuCache cache  = null;
    
    /** return the singleton PluginBarFactory
     *	@return a PluginBarFactory
     */
    public static PluginBarFactory getInstance()
    {
	return factory;
    }
    
    public void updateComponentTreeUI()
    {
	if ( this.cache != null )
	{
	    /* clear cache to force recreation fo all context menu instead of calling updateComponentTreeUI on all */
	    this.cache.clear();
	}
    }
    
    /** create a new PluginBarFactory */
    private PluginBarFactory()
    {
	super();

	ToolBarCustomizer   toolbarCustomizer = new PluginToolBarCustomizer();
	MenuBarCustomizer   menubarCustomizer = new PluginMenuBarCustomizer();
	PopupMenuCustomizer popupCustomizer   = new PluginPopupMenuCustomizer();
	
	this.setDefaultToolBarCustomizer(toolbarCustomizer);
	this.setDefaultMenuBarCustomizer(menubarCustomizer);
	this.setDefaultPopupMenuCustomizer(popupCustomizer);
    }
    
    /** return a JPopupMenu for the application system tray item
     *	@return a JPopupMenu
     */
    public JPopupMenu createSystemTrayPopupMenu()
    {
	return this.createContextMenu(new PluginBarProvider(null, PluginBarProvider.BarKind.SYSTRAY));
    }

    /** return the cache ( create it if it does not exists )
     *  @return an instanceof ContextMenuCache
     */
    private ContextMenuCache getCache()
    {
	if (cache == null)
	{
	    cache = new ContextMenuCache();
	}

	return cache;
    }

    /** return the base class to consider for the given objects
     *  @param o an array of Objects
     *  @return a Class or null if the given array in null or empty
     */
    public Class getBaseClass(Object[] o)
    {
	Class c = null;

	if (o != null && o.length > 0)
	{
	    for (int i = 0; i < o.length; i++)
	    {
		Object current = o[i];

		if (current != null)
		{
		    if (c == null)
		    {
			c = current.getClass();
		    }
		    else
		    {
			Class currentClass = current.getClass();

			while (!c.isAssignableFrom(currentClass))
			{
			    c = c.getSuperclass();
			}
		    }

		    /** if c is Object.class --> c will not be modified anymore, so break */
		    if (c.equals(Object.class))
		    {
			break;
		    }
		}
	    }
	}

	return c;
    }

    /** returns a JPopupMenu related to given Objects
     *  @param o an array of Objects
     *  @return a JPopupMenu or null if no popupmenu could be created
     */
    public JPopupMenu getContextMenuForItem(Object[] o)
    {
	JPopupMenu contextMenu = null;

	/** we have to find the appropriated class. for example, if we get two object of kind SibURL and SibString we must 
	 *  consider SibBaseType.class.
	 *  if we get SibInteger and java.util.Calendar, we consider Object.class.
	 */
	Class c = this.getBaseClass(o);

	if (o != null && o.length > 0)
	{
	    for (int i = 0; i < o.length; i++)
	    {
		Object current = o[i];

		if (current != null)
		{
		    if (c == null)
		    {
			c = current.getClass();
		    }
		    else
		    {
			Class currentClass = current.getClass();

			while (!c.isAssignableFrom(currentClass))
			{

			}
		    }

		    /** if c is Object.class --> c will not be modified anymore, so break */
		    if (c.equals(Object.class))
		    {
			break;
		    }
		}
	    }
	}

	if (c != null)
	{
	    contextMenu = (o == null ? null : getContextMenuForClass(c));

	    if (contextMenu != null)
	    {
		/** prepare the context menu by using the given object */
		applyObject(contextMenu, o);
	    }
	}

	return contextMenu;
    }

    /** methods that allow to set the type of item
     *  @param component a Component
     *  @param o an array of Objects
     */
    private void applyObject(Component component, final Object[] o)
    {
	if (component != null )
	{
	    if (component instanceof Container)
	    {
		for (int i = 0; i < ((Container) component).getComponentCount(); i++)
		{
		    /** apply object recursively */
		    applyObject(((Container) component).getComponent(i), o);
		}
	    }
	    if ( component instanceof JMenu )
	    {
		for (int i = 0; i < ((JMenu) component).getMenuComponentCount(); i++)
		{
		    /** apply object recursively */
		    applyObject(((JMenu) component).getMenuComponent(i), o);
		}
	    }

	    /* if the component contains an action, then it could be an action extending TypeAction ... */
	    if (component instanceof AbstractButton)
	    {
		Action action = ((AbstractButton) component).getAction();

		/** try to invoke setTypes method on the action
		 *  must use reflection because the methods use generic
		 */
		if (action instanceof TypeAction)
		{
		    Object array = null;
		    if ( o != null && o.length > 0 )
		    {
			array = Array.newInstance(getBaseClass(o), o.length);

			for (int i = 0; i < o.length; i++)
			{
			    Array.set(array, i, o[i]);
			}
		    }

		    Method[] ms = action.getClass().getMethods();

		    for (int i = 0; i < ms.length; i++)
		    {
			Method c = ms[i];
			if (c.getName().equals("setTypes"))
			{
			    Class[] parameters = c.getParameterTypes();

			    if (parameters != null && parameters.length == 1)
			    {
				try
				{
				    c.invoke(action, array);
				    break;
				}
				catch (IllegalArgumentException ex)
				{
				    ex.printStackTrace();
				}
				catch (InvocationTargetException ex)
				{
				    ex.printStackTrace();
				}
				catch (IllegalAccessException ex)
				{
				    ex.printStackTrace();
				}
			    }
			}
		    }
		}
	    }
	}
    }

    /** returns a JPopupMenu related to the given Class
     *  @param c a Class
     *  @return a JPopupMenu or null if no popupmenu could be created
     */
    public JPopupMenu getContextMenuForClass(Class c)
    {   /* search in the cache if it already exists */
	return getCache().get(c);
    }

    /* #########################################################################
     * ################# PluginResourcesListener implementation ################
     * ######################################################################### */
    /** indicate to a listener that a modification has been made in the plugin context
     *	@param evt a PluginResourcesEvent describing the modification
     */
    public void pluginContextChanged(PluginResourcesEvent evt)
    {
	if (evt.getPhase().equals(PluginResourcesEvent.Phase._7))
	{
	    /* clear cache */
	    if (this.cache != null)
	    {
		this.cache.clear();
	    }
	}
    }

    /** cache for context menu */
    private class ContextMenuCache extends GenericCache2<Class, JPopupMenu>
    {
	private SoftReference<ClassMap<List<String>>> classMapRef = null;

	/** method that create an elemet according to the key
	 *  @param key the corresponding key
	 *  @param parameters others parameters
	 *  @return the object that has been created
	 */
	public JPopupMenu create(Class key, Object... parameters)
	{   
	    JPopupMenu contextMenu = null;
	    final Iterator<String> rcPaths = getContextMenuPaths(key);
	    
	    BarProvider provider = new BarProvider()
	    {
		public Iterator<InputStream> getBarInputStreams()
		{
		    Set<InputStream> streams = new HashSet<InputStream>(10);
		    
		    while(rcPaths.hasNext())
		    {
			String currentPath = rcPaths.next();
			
			if ( currentPath != null )
			{
			    URL url = null;
			    try    
			    {
				url = ResourceLoader.getInstance().getRcResource(currentPath);
				
				streams.add(url.openStream());
			    }
			    catch(ResourceException e)
			    {
				logger.error("unable to get resource '" + currentPath + "'", e);
			    }
			    catch(IOException e)
			    {
				logger.error("unable to open stream from '" + url + "'", e);
			    }
			}
		    }
		    
		    if ( streams == null )
		    {
			streams = Collections.emptySet();
		    }
		    
		    return streams.iterator();
		}
	    };
	    
	    contextMenu = PluginBarFactory.getInstance().createContextMenu(provider);

	    return contextMenu;
	}

	/** return the classmap that contains the resource paths for a given class
	 *  @return a ClassMap
	 */
	private ClassMap<List<String>> getClassMap()
	{
	    ClassMap<List<String>> map = (this.classMapRef == null ? null : this.classMapRef.get());

	    if (map == null)
	    {
		map = new ClassMap<List<String>>();

		/* should initialize a ClassMap and create a new SoftReference */
		Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(PluginBarProvider.BarKind.POPUPMENU.getCode());
		if (extensions != null)
		{
		    Iterator<SiberExtension> it = extensions.iterator();
		    while (it.hasNext())
		    {
			SiberExtension extension = it.next();

			String classRepresentation = extension.getStringParameterValue(PluginBarProvider.PARAMETER_CLASS);
			String filePath = extension.getStringParameterValue(PluginBarProvider.PARAMETER_FILEPATH);

			if (classRepresentation != null)
			{
			    if (filePath != null)
			    {
				try
				{
				    Class c = ResourceLoader.getInstance().getClass(classRepresentation);
				    System.out.println("class for '" + classRepresentation + "' " + c);

				    List<String> list = map.get(c);

				    if (list == null)
				    {
					list = new ArrayList<String>();
					map.put(c, list);
				    }

				    list.add(filePath);
				}
				catch (ResourceException e)
				{
				    logger.error("error when processing a '" + PluginBarProvider.BarKind.POPUPMENU.getCode() +
						 "' extension point", e);
				}
			    }
			    else
			    {
				logger.error("unable to consider extension point '" + PluginBarProvider.BarKind.POPUPMENU.getCode() + "' : " +
					     "filepath parameter is null for '" + classRepresentation + "'");
			    }
			}
			else
			{
			    logger.error("unable to consider extension point '" + PluginBarProvider.BarKind.POPUPMENU.getCode() + "' : " +
					 "class parameter is null for filePath='" + filePath + "'");
			}
		    }
		}

		this.classMapRef = new SoftReference<ClassMap<List<String>>>(map);
	    }

	    return map;
	}

	/** return a list of String representing path to context menu resources declaration
	 *  @param c a Class
	 *  @return an iterator over all 
	 */
	private Iterator<String> getContextMenuPaths(Class c)
	{
	    List<List<String>> list = getClassMap().getRecursively(c, true);
	    if (list == null)
	    {
		return Collections.EMPTY_LIST.iterator();
	    }
	    else
	    {
		return new IteratorGroup<String>((Iterable<String>[]) list.toArray((Iterable<String>[]) new Iterable[]{}));
	    }
	}
    }
}
