/* =============================================================================
 * Siberia bars
 * =============================================================================
 *
 * Project Lead:  Alexis Paris
 *
 * (C) Copyright 2007, by Alexis Paris.
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
package org.siberia.bar.factory;

import org.apache.log4j.Logger;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.xml.bind.JAXBException;
import org.siberia.bar.customizer.BarCustomizer;
import org.siberia.bar.customizer.DefaultPopupMenuCustomizer;
import org.siberia.bar.customizer.PopupMenuCustomizer;
import org.siberia.bar.i18n.I18NResources;
import org.siberia.bar.provider.BarProvider;
import org.siberia.bar.customizer.DefaultMenuBarCustomizer;
import org.siberia.bar.customizer.DefaultToolBarCustomizer;
import org.siberia.bar.customizer.MenuBarCustomizer;
import org.siberia.bar.customizer.ToolBarCustomizer;
import org.siberia.bar.xml.JAXBBarLoader;
import org.siberia.xml.schema.bar.ButtonType;
import org.siberia.xml.schema.bar.CheckType;
import org.siberia.xml.schema.bar.SeparatorSizedElement;
import org.siberia.xml.schema.bar.ActionableElement;
import org.siberia.xml.schema.bar.ActionableShortcutedElement;
import org.siberia.xml.schema.bar.CheckMenuItem;
import org.siberia.xml.schema.bar.ComboType;
import org.siberia.xml.schema.bar.MenuType;
import org.siberia.xml.schema.bar.MenuItem;
import org.siberia.xml.schema.bar.Menubar;
import org.siberia.xml.schema.bar.OrderedElement;
import org.siberia.xml.schema.bar.ParameterType;
import org.siberia.xml.schema.bar.SeparatorElement;
import org.siberia.xml.schema.bar.Toolbar;
import org.siberia.xml.schema.bar.TypeMenu;
import java.lang.ref.SoftReference;
import javax.swing.JSeparator;

/**
 *
 * Factory that is able to create bars according to xml declarations<br>
 *  by 'bar', we means :<br>
 *  <ul>
 *	<li>javax.swing.JMenuBar</li>
 *	<li>javax.swing.JToolBar</li>
 *	<li>javax.swing.JPopupMenu</li>
 *  </ul>
 *
 *  this class provides the main folowing methods :
 *
 *  <ul>
 *	<ul><b>For JMenuBars</b> :
 *	    <li>configure(JMenuBar menubar, BarProvider provider, MenuBarCustomizer customizer, boolean removeAll) which configure an existing JMenuBar with the xml bar definitions provided by the BarProvider</li>
 *	    <li>createMenuBar(BarProvider provider, MenuBarCustomizer customizer) which create a JMenuBar according to the xml bar definitions provided by the BarProvider</li>
 *	</ul>
 *	<ul><b>For JToolBars</b> :
 *	    <li>configure(JToolBar toolbar, boolean removeAll) which configure an existing JToolBar with the xml bar definitions provided by the BarProvider</li>
 *	    <li>createToolBar() which create a JToolBar according to the xml bar definitions provided by the BarProvider</li>
 *	</ul>
 *	<ul><b>For JPopupMenus</b> :
 *	    <li>createContextMenu() which creates a JPopupMenu according to the xml bar definitions provided by the current BarProvider</li>
 *	</ul>
 *
 *  </ul>
 *
 * @author alexis
 */
public class BarFactory
{   
    /** logger */
    private              Logger                       logger                       = Logger.getLogger(BarFactory.class);
    
    /** indicates if the resulting main menu bar should be written for debug purposes */
    private static final boolean                      DEBUG_MENUBAR                = false;
    
    /** comparator on OrderedElement */
    private static       SoftReference<Comparator>    ORDERED_ITEM_COMPARATOR_REF  = new SoftReference(null);
    
    /** comparator on PriorityElement */
    private static       SoftReference<Comparator>    PRIORITY_ITEM_COMPARATOR_REF = new SoftReference(null);
    
    /** reference to JAXBBarLoader */
    private              SoftReference<JAXBBarLoader> jaxbeRef                     = null;
    
    /** menubar customizer */
    private              MenuBarCustomizer            defaultMenuCustomizer        = new DefaultMenuBarCustomizer();
    
    /** toolbar customizer */
    private              ToolBarCustomizer            defaultToolCustomizer        = new DefaultToolBarCustomizer();
    
    /** popup menu customizer */
    private              PopupMenuCustomizer          defaultPopupMenuCustomizer   = new DefaultPopupMenuCustomizer();
    
    /** Creates a new instance of MenuFactory */
    public BarFactory()
    {   /* do nothing */ }
    
    /** set the default toolbar customizer
     *	@param customizer a ToolBarCustomizer
     */
    public void setDefaultToolBarCustomizer(ToolBarCustomizer customizer)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setDefaultToolBarCustomizer(ToolBarCustomizer)");
	}
	if ( customizer != this.getDefaultToolBarCustomizer() )
	{
	    if ( customizer == null )
	    {   throw new IllegalArgumentException("a non null ToolBarCustomizer must be provided"); }
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("changing the default toolbar customizer of " + this + " from " + this.getDefaultToolBarCustomizer() + " to " + customizer);
	    }
	    
	    this.defaultToolCustomizer = customizer;
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setDefaultToolBarCustomizer(ToolBarCustomizer)");
	}
    }
    
    /** return the default ToolBarCustomizer
     *	@return a ToolBarCustomizer
     */
    public ToolBarCustomizer getDefaultToolBarCustomizer()
    {	return this.defaultToolCustomizer; }
    
    /** set the default menubar customizer
     *	@param customizer a MenuBarCustomizer
     */
    public void setDefaultMenuBarCustomizer(MenuBarCustomizer customizer)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setDefaultMenuBarCustomizer(MenuBarCustomizer)");
	}
	if ( customizer != this.getDefaultMenuBarCustomizer() )
	{
	    if ( customizer == null )
	    {   throw new IllegalArgumentException("a non null MenuBarCustomizer must be provided"); }
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("changing the default menubar customizer of " + this + " from " + this.getDefaultMenuBarCustomizer() + " to " + customizer);
	    }
	    
	    this.defaultMenuCustomizer = customizer;
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setDefaultMenuBarCustomizer(MenuBarCustomizer)");
	}
    }
    
    /** return the default MenuBarCustomizer
     *	@return a MenuBarCustomizer
     */
    public MenuBarCustomizer getDefaultMenuBarCustomizer()
    {	return this.defaultMenuCustomizer; }
    
    /** set the default popup menu customizer
     *	@param customizer a PopupMenuCustomizer
     */
    public void setDefaultPopupMenuCustomizer(PopupMenuCustomizer customizer)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setDefaultPopupMenuCustomizer(PopupMenuCustomizer)");
	}
	if ( customizer != this.getDefaultPopupMenuCustomizer() )
	{
	    if ( customizer == null )
	    {   throw new IllegalArgumentException("a non null PopupMenuCustomizer must be provided"); }
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("changing the default popup menu customizer of " + this + " from " + this.getDefaultPopupMenuCustomizer() + " to " + customizer);
	    }
	    
	    this.defaultPopupMenuCustomizer = customizer;
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setDefaultPopupMenuCustomizer(PopupMenuCustomizer)");
	}
    }
    
    /** return the PopupMenuCustomizer
     *	@return a PopupMenuCustomizer
     */
    public PopupMenuCustomizer getDefaultPopupMenuCustomizer()
    {	return this.defaultPopupMenuCustomizer; }
    
    /** return an instance of JAXBBarLoader
     *  @return an instance of JAXBBarLoader
     */
    private JAXBBarLoader getJAXBLoader()
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering getJAXBLoader()");
	}
	JAXBBarLoader loader = null;
        if ( jaxbeRef != null )
	{
            loader = jaxbeRef.get();
	}
	
        if ( loader == null )
        {   loader = new JAXBBarLoader();
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("creating a new " + JAXBBarLoader.class.getName() + " to load bar definitions");
	    }
            
            jaxbeRef = new SoftReference<JAXBBarLoader>(loader);
        }
	else
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("getting " + JAXBBarLoader.class.getName() + " from cache");
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting getJAXBLoader()");
	}
        
        return loader;        
    }
    
    /* #########################################################################
     * ####################### MenuBar factory methods #########################
     * ######################################################################### */
    
    /** configure given JMenuBar according to the bar declaration provided by BarProvider<br>
     *	this methods remove all items added to the JMenuBar before configuration
     *  @param menubar a JMenuBar
     *	@param provider a BarProvider
     */
    public void configure(JMenuBar menubar, BarProvider provider)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering configure(JMenuBar, BarProvider)");
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling configure for menu bar of kind " + (menubar == null ? null : menubar.getClass()));
	}
	this.configure(menubar, provider, true);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting configure(JMenuBar, BarProvider)");
	}
    }
    
    /** configure given JMenuBar according to the bar declaration provided by BarProvider
     *  @param menubar a JMenuBar
     *	@param provider a BarProvider
     *	@param removeAll true to remove all components of the given menu bar before configuring it
     */
    public void configure(JMenuBar menubar, BarProvider provider, boolean removeAll)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering configure(JMenuBar, BarProvider, boolean)");
	}
	this.configure(menubar, provider, this.getDefaultMenuBarCustomizer(), removeAll);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting configure(JMenuBar, BarProvider, boolean)");
	}
    }
    
    /** configure given JMenuBar according to the bar declaration provided by BarProvider
     *  @param menubar a JMenuBar
     *	@param provider a BarProvider
     *	@param customizer a MenuBarCustomizer
     *	@param removeAll true to remove all components of the given menu bar before configuring it
     */
    public void configure(JMenuBar menubar, BarProvider provider, MenuBarCustomizer customizer, boolean removeAll)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering configure(JMenuBar, BarProvider, MenuBarCustomizer, boolean)");
	}
	
	MenuBarCustomizer customizerUsed = customizer;

	if ( customizerUsed == null )
	{
	    customizerUsed = this.getDefaultMenuBarCustomizer();
	}
	
        if ( menubar != null )
        {   
	    if ( removeAll )
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("removing all components of menubar " + menubar);
		}
		menubar.removeAll();
	    }
            
	    Iterator<InputStream> streams = null;
	    
	    if ( provider == null )
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("no bar provider set");
		}
	    }
	    else
	    {
		streams = provider.getBarInputStreams();
	    }
	    
	    if ( streams == null )
	    {
		//
	    }
	    else
	    {
		List<Menubar> menubars = new ArrayList<Menubar>(10);
		
		try
		{
		    while(streams.hasNext())
		    {
			InputStream currentStream = streams.next();
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("configure JMenuBar process the current InputStream " + currentStream);
			}

			if ( currentStream != null )
			{
			    try
			    {
				Menubar bar = getJAXBLoader().loadMenubar(currentStream);

				if ( customizerUsed.shouldConsider(bar) )
				{   
				    /** register I18n resources */
				    if ( bar != null )
				    {
					org.siberia.xml.schema.bar.I18NResources rcs = bar.getI18NResources();

					if ( rcs != null && rcs.getI18NResource() != null )
					{
					    for(int i = 0; i < rcs.getI18NResource().size(); i++)
					    {
						org.siberia.xml.schema.bar.I18NResource rc = rcs.getI18NResource().get(i);

						if ( rc != null )
						{
						    I18NResources _rc = new I18NResources(rc.getValue(), rc.getPriority());
						    
						    if ( logger.isDebugEnabled() )
						    {
							logger.debug("menu bar customizer has registered i18n resources '" + _rc.getCode() + "' with priority " + _rc.getPriority());
						    }
						    customizerUsed.registerI18NResources(_rc);
						}
					    }
					}
				    }
				    
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("adding " + bar + " to the list of Menubar to merge");
				    }
				    menubars.add(bar);
				}
				else
				{
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("menu bar customizer refuse to consider bar " + bar);
				    }
				}
			    }
			    catch(JAXBException e)
			    {
				logger.error("unable to load menubar from inputStream " + currentStream, e);
			    }
			    finally
			    {
				try
				{
				    currentStream.close();
				}
				catch(IOException e)
				{
				    logger.error("unable to close inputStream " + currentStream);
				}
			    }
			}
		    }
		}
		finally
		{
		    /* ensure that all input streams are closed */
		    if ( streams != null )
		    {
			while(streams.hasNext())
			{
			    InputStream currentStream = streams.next();
			    
			    if ( currentStream != null )
			    {
				try
				{
				    currentStream.close();
				}
				catch(IOException e)
				{
				    logger.error("unable to close input stream " + currentStream);
				}
			    }
			}
		    }
		}
		
		if ( menubars == null )
		{
		    menubars = Collections.emptyList();
		}
		
		if ( logger.isDebugEnabled() )
		{   logger.debug("creating menus according to " + menubars.size() + " menubars"); }
                
                List<JMenu> menus = this.createMenu(menubars, customizerUsed);
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("created " + (menus == null ? 0 : menus.size()) + " JMenu with all bar definitions");
		}
                if ( menus != null )
                {   Iterator<JMenu> it = menus.iterator();
                    while(it.hasNext())
		    {
                        menubar.add(it.next());
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("adding a menu to a JMenuBar");
			}
		    }
                }
		
		customizerUsed.clearI18NResources();
	    }
        }
	
	if ( logger.isDebugEnabled() )
	{   logger.debug("end of configure with JMenuBar : component count = " + (menubar == null ? 0 : menubar.getComponentCount())); }
        
	this.cleanBar(menubar, customizerUsed);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting configure(JMenuBar, BarProvider, MenuBarCustomizer, boolean)");
	}
    }
    
    /** create a new JMenuBar according to bar provider
     *	@param provider a BarProvider
     *  @return a new JMenuBar
     */
    public JMenuBar createMenuBar(BarProvider provider)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createMenuBar(BarProvider)");
	}
	
	JMenuBar bar = new JMenuBar();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createMenuBar(BarProvider)");
	}
        
        return this.createMenuBar(provider, null);
    }
    
    /** create a new JMenuBar according to bar provider
     *	@param provider a BarProvider
     *	@param customizer a MenuBarCustomizer
     *  @return a new JMenuBar
     */
    public JMenuBar createMenuBar(BarProvider provider, MenuBarCustomizer customizer)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createMenuBar(BarProvider, MenuBarCustomizer)");
	}
	JMenuBar bar = new JMenuBar();
        
        this.configure(bar, provider, customizer, true);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createMenuBar(BarProvider, MenuBarCustomizer)");
	}
        
        return bar;
    }
    
    /* #########################################################################
     * ####################### ToolBar factory methods #########################
     * ######################################################################### */
    
    /** create a list of component according to xml declarations and add them to the given toolbar<br>
     *	this methods remove all items added to the JToolBar before configuration
     *  @param toolbar an instance of JToolBar
     *	@param provider a Barprovider
     */
    public void configure(JToolBar toolbar, BarProvider provider)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering configure(JToolBar, BarProvider)");
	}
	this.configure(toolbar, provider, true);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting configure(JToolBar, BarProvider)");
	}
    }
    
    /** create a list of component according to xml declarations and add them to the given toolbar
     *  @param toolbar an instance of JToolBar
     *	@param provider a Barprovider
     *	@param removeAll true to remove all components of the given menu bar before configuring it
     */
    public void configure(JToolBar toolbar, BarProvider provider, boolean removeAll)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering configure(JToolBar, BarProvider, boolean)");
	}
	configure(toolbar, provider, this.getDefaultToolBarCustomizer(), removeAll);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting configure(JToolBar, BarProvider, boolean)");
	}
    }
    
    /** create a list of component according to xml declarations and add them to the given toolbar
     *  @param toolbar an instance of JToolBar
     *	@param provider a Barprovider
     *	@param customizer a ToolBarCustomizer
     *	@param removeAll true to remove all components of the given menu bar before configuring it
     */
    public void configure(JToolBar toolbar, BarProvider provider, ToolBarCustomizer customizer, boolean removeAll)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering configure(JToolBar, BarProvider, ToolBarCustomizer, boolean)");
	}
	    
	ToolBarCustomizer customizerUsed = customizer;

	if ( customizerUsed == null )
	{
	    customizerUsed = this.getDefaultToolBarCustomizer();
	}
	    
        if ( toolbar != null )
        {   
	    if ( removeAll )
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("removing all components of toolbar " + toolbar);
		}
		toolbar.removeAll();
	    }
	    
            List<Toolbar> toolbars = null;
	    
	    Iterator<InputStream> streams = null;
	    
	    if ( provider == null )
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("no bar provider set");
		}
	    }
	    else
	    {
		streams = provider.getBarInputStreams();
	    }
		
	    if ( streams != null )
	    {
		try
		{
		    while(streams.hasNext())
		    {
			InputStream currentStream = streams.next();
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("configure JToolBar process the current InputStream " + currentStream);
			}
			
			if ( currentStream != null )
			{
			    try
			    {
				Toolbar bar = getJAXBLoader().loadToolbar(currentStream);

				boolean consider = customizerUsed.shouldConsider(bar);
			
				if ( consider )
				{   
				
				    /** register I18n resources */
				    if ( bar != null )
				    {
					org.siberia.xml.schema.bar.I18NResources rcs = bar.getI18NResources();

					if ( rcs != null )
					{
					    for(int i = 0; i < rcs.getI18NResource().size(); i++)
					    {
						org.siberia.xml.schema.bar.I18NResource rc = rcs.getI18NResource().get(i);

						if ( rc != null )
						{
						    I18NResources _rc = new I18NResources(rc.getValue(), rc.getPriority());
						    if ( logger.isDebugEnabled() )
						    {
							logger.debug("tool bar customizer has registered i18n resources '" + _rc.getCode() + "' with priority " + _rc.getPriority());
						    }
						    customizerUsed.registerI18NResources(_rc);
						}
					    }
					}
				    }
				    
				    if ( toolbars == null )
				    {
					toolbars = new ArrayList<Toolbar>(10);
				    }
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("adding " + bar + " to the list of Toolbar to merge");
				    }
				    toolbars.add(bar);
				}
			    }
			    catch(JAXBException e)
			    {
				logger.error("unable to load toolbar from inputStream " + currentStream, e);
			    }
			    finally
			    {
				try
				{
				    currentStream.close();
				}
				catch(IOException e)
				{
				    logger.error("unable to close inputStream " + currentStream);
				}
			    }
			}
		    }
		}
		finally
		{
		    /* ensure that all input streams are closed */
		    if ( streams != null )
		    {
			while(streams.hasNext())
			{
			    InputStream currentStream = streams.next();
			    
			    if ( currentStream != null )
			    {
				try
				{
				    currentStream.close();
				}
				catch(IOException e)
				{
				    logger.error("unable to close input stream " + currentStream);
				}
			    }
			}
		    }
		}
	    }
            
	    if ( toolbars == null )
	    {
		toolbars = Collections.emptyList();
	    }
		
	    if ( logger.isDebugEnabled() )
	    {   logger.debug("creating toolbar according to " + toolbars.size() + " toolbars"); }
            
            customizerUsed.feedComponent(toolbar, mergeToolbars(toolbars));
	    
	    customizerUsed.clearI18NResources();
        }
        
	this.cleanBar(toolbar, customizerUsed);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting configure(JToolBar, BarProvider, ToolBarCustomizer, boolean)");
	}
    }
    
    /** create a new JToolBar according to bar provider
     *	@param provider a BarProvider
     *  @return a new JToolBar
     */
    public JToolBar createToolBar(BarProvider provider)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createToolBar(BarProvider)");
	}
	JToolBar bar = this.createToolBar(provider, this.getDefaultToolBarCustomizer());
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createToolBar(BarProvider)");
	}
	
	return bar;
    }
    
    /** create a new JToolBar according to bar provider
     *	@param provider a BarProvider
     *	@param customizer a ToolBarCustomizer
     *  @return a new JToolBar
     */
    public JToolBar createToolBar(BarProvider provider, ToolBarCustomizer customizer)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createToolBar(BarProvider, ToolBarCustomizer)");
	}
	JToolBar bar = new JToolBar();
        
        configure(bar, provider, customizer, true);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createToolBar(BarProvider, ToolBarCustomizer)");
	}
        
        return bar;
    }
    
    /* #########################################################################
     * ######################## context menu creation ##########################
     * ######################################################################### */
    
    /** create a list of JMenuItem according to BarProvider
     *	@param provider a BarProvider
     *  @return a JPopupMenu
     */
    public JPopupMenu createContextMenu(BarProvider provider)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createContextMenu(BarProvider)");
	}
	
	JPopupMenu menu = this.createContextMenu(provider, this.getDefaultPopupMenuCustomizer());
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createContextMenu(BarProvider)");
	}
	
	return menu;
    }
    
    /** create a list of JMenuItem according to BarProvider
     *	@param provider a BarProvider
     *	@param customizer a PopupMenuCustomizer
     *  @return a JPopupMenu
     */
    public JPopupMenu createContextMenu(BarProvider provider, PopupMenuCustomizer customizer)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createContextMenu(BarProvider, PopupMenuCustomizer)");
	}
	JPopupMenu menu = null;
	
	Iterator<InputStream> streams = null;
	
	PopupMenuCustomizer customizerUsed = customizer;
	    
	if ( customizerUsed == null )
	{
	    customizerUsed = this.getDefaultPopupMenuCustomizer();
	}

	if ( provider == null )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("no bar provider set");
	    }
	}
	else
	{
	    streams = provider.getBarInputStreams();
	}

	if ( streams != null )
	{
	    List<TypeMenu> menus = new ArrayList<TypeMenu>(10);

	    try
	    {
		while(streams.hasNext())
		{
		    InputStream currentStream = streams.next();
			
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("createContextMenu process the current InputStream " + currentStream);
		    }

		    if ( currentStream != null )
		    {
			try
			{
			    TypeMenu typeMenu = getJAXBLoader().loadTypeMenu(currentStream);

			    if ( customizerUsed.shouldConsider(typeMenu) )
			    {   
				menus.add(typeMenu);
				
				/** register I18n resources */
				if ( typeMenu != null )
				{
				    org.siberia.xml.schema.bar.I18NResources rcs = typeMenu.getI18NResources();

				    if ( rcs != null )
				    {
					for(int i = 0; i < rcs.getI18NResource().size(); i++)
					{
					    org.siberia.xml.schema.bar.I18NResource rc = rcs.getI18NResource().get(i);

					    if ( rc != null )
					    {
						I18NResources _rc = new I18NResources(rc.getValue(), rc.getPriority());
						if ( logger.isDebugEnabled() )
						{
						    logger.debug("popup menu customizer has registered i18n resources '" + _rc.getCode() + "' with priority " + _rc.getPriority());
						}
						customizerUsed.registerI18NResources(_rc);
					    }
					}
				    }
				}
			    }
			    else
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("popup menu customizer refused TypeMenu " + typeMenu);
				}
			    }
			}
			catch(JAXBException e)
			{
			    logger.error("unable to load menubar from inputStream " + currentStream, e);
			}
			finally
			{
			    try
			    {
				currentStream.close();
			    }
			    catch(IOException e)
			    {
				logger.error("unable to close inputStream " + currentStream);
			    }
			}
		    }
		}
	    }
	    finally
	    {
		/* ensure that all input streams are closed */
		if ( streams != null )
		{
		    while(streams.hasNext())
		    {
			InputStream currentStream = streams.next();

			if ( currentStream != null )
			{
			    try
			    {
				currentStream.close();
			    }
			    catch(IOException e)
			    {
				logger.error("unable to close input stream " + currentStream);
			    }
			}
		    }
		}
	    }

	    if ( menus == null )
	    {
		menus = Collections.emptyList();
	    }
		
	    if ( logger.isDebugEnabled() )
	    {   logger.debug("creating context menu according to " + menus.size() + " type menu"); }

	    TypeMenu typeMenu = new TypeMenuMerger(menus, customizerUsed).mergeType();
	    
	    if ( typeMenu != null )
	    {
		/* sort the type menu */
		sortMenuItem(typeMenu.getItems().getMenuOrItemOrCheck());

		menu = new JPopupMenu();

		customizerUsed.feedComponent(menu, typeMenu.getItems().getMenuOrItemOrCheck());
	    }
	}
        
	this.cleanBar(menu, customizerUsed);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createContextMenu(BarProvider, PopupMenuCustomizer)");
	}
        
        return menu;
    }
    
    /* #########################################################################
     * ########################### Cleaning methods ############################
     * ######################################################################### */
    
    /** method that clean a bar component
     *  @param component a JComponent
     *	@param custromizer a BarCustomizer
     */
    public void cleanBar(JComponent component, BarCustomizer customizer)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering cleanBar with " + component);
	}
	
	if ( customizer != null )
	{
	    if ( customizer.avoidSuccessiveSeparators() )
	    {
		this.mergeSeparators(component);
	    }
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting cleanBar with " + component);
	}
    }
    
    /*  method that merged separators. for example, if two separator are adding successively,
     *  then this methods wil remove one of the two separator
     *  @param component a JComponent
     */
    public void mergeSeparators(JComponent component)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering mergeSeparators(JComponent)");
	}
	
	if ( component != null )
        {   boolean lastIsSeparator = false;
            for(int i = 0; i < component.getComponentCount(); i++)
            {   Component current = component.getComponent(i);
                
                if ( current instanceof JSeparator )
                {
                    if ( lastIsSeparator )
                    {   
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("removing separator from " + component);
			}
			component.remove(current);
                        i--;
                        continue;
                    }
                    else
                    {   lastIsSeparator = true; }
                }
                else
                {   if ( current instanceof JComponent )
                        this.mergeSeparators((JComponent)current);
                    lastIsSeparator = false;
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting mergeSeparators(JComponent)");
	}
    }
    
    /* #########################################################################
     * ########################### General methods #############################
     * ######################################################################### */
    
    /** method that merge Toolbars
     *  @param toolbars a Collection of Toolbars
     *  @return a list containing OrderedElement ( button, check, combo, separator, etc...)
     */
    protected List<OrderedElement> mergeToolbars(List<Toolbar> toolbars)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering mergeToolbars(List<Toolbar>)");
	}
        List<OrderedElement> list = null;
        
        if ( toolbars != null )
        {   
            /* sort PriorityElement of the list */
            Collections.sort(toolbars, getPriorityComparator());
            
            Iterator<Toolbar> it = toolbars.iterator();
            
            while(it.hasNext())
            {   Toolbar tool = it.next();
                
		if ( tool.getItems() != null )
		{
		    if ( list == null )
		    {
			list = new ArrayList<OrderedElement>();
		    }
		    list.addAll( tool.getItems().getButtonOrCheckOrCombo() );
		}
            }
        }
        
            
        if ( list == null )
	{
            list = Collections.EMPTY_LIST;
	}
        else
	{
            Collections.sort(list, getOrderedComparator());
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting mergeToolbars(List<Toolbar>)");
	}
        
        return list;
    }
    
    /** create a list of menu according to xml declarations
     *  @param menubars a List containing Menubar
     *  @return a list of Menu
     */
    protected List<JMenu> createMenu(List<Menubar> menubars)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createMenu(List<Menubar>)");
	}
	List<JMenu> menus = this.createMenu(menubars, this.getDefaultMenuBarCustomizer());
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createMenu(List<Menubar>)");
	}
	
	return menus;
    }
    
    /** create a list of menu according to xml declarations
     *  @param menubars a List containing Menubar
     *	@param customizer a MenuBarCustomizer
     *  @return a list of Menu
     */
    protected List<JMenu> createMenu(List<Menubar> menubars, MenuBarCustomizer customizer)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createMenu(List<Menubar>, MenuBarCustomizer)");
	}
        /* sort the list --> it is a list of PriorityElement */
        if ( menubars != null )
	{
            Collections.sort(menubars, getPriorityComparator());
	}
        
        Menubar bar = null;
	
	MenuBarCustomizer customizerUsed = customizer;
	if ( customizerUsed == null )
	{
	    customizerUsed = this.getDefaultMenuBarCustomizer();
	}
        
        bar = new MenubarMerger(menubars, customizerUsed).merge();
           
        if ( DEBUG_MENUBAR )
        {
            try
            {   
		getJAXBLoader().saveMenubar(bar, new File("src" + File.separator + "menu_1" + ".xml"));
	    }
            catch (JAXBException ex)
            {   
		logger.error("unable to save menu", ex);
	    }
            catch (IOException ex)
            {   
		logger.error("unable to save menu", ex);
	    }
        }
        
        /** sort the root menu of the bar */
        if ( bar != null && bar.getMenus() != null && bar.getMenus().getMenu() != null )
        {   Collections.sort(bar.getMenus().getMenu(), getOrderedComparator()); }                    
        
        List<JMenu> menus = null;
        if (bar != null && bar.getMenus() != null && bar.getMenus().getMenu() != null )
        {   
	    /* do not forget to order list according to order id */
            menus = new ArrayList<JMenu>(bar.getMenus().getMenu().size());
            Iterator it = bar.getMenus().getMenu().iterator();
            while(it.hasNext())
            {   Object current = it.next();
                if ( current instanceof MenuType )
                {   JMenu graphicalMenu = this.createMenu((MenuType)current, customizerUsed);
                    if ( graphicalMenu != null )
                    {   menus.add(graphicalMenu); }
                }
            }
        }
        if ( menus == null )
	{   menus = Collections.EMPTY_LIST; }
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createMenu(List<Menubar>, MenuBarCustomizer)");
	}
	
        return menus;
    }
    
    /** return a JMenu according to a given xml Menu
     *  @param menu an xml menu
     *  @return a JMenu or null if errors occured
     */
    public JMenu createMenu(MenuType menu)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createMenu(MenuType)");
	}
	JMenu jmenu = this.createMenu(menu, this.getDefaultMenuBarCustomizer());
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createMenu(MenuType)");
	}
	return jmenu;
    }
	
    /** return a JMenu according to a given xml Menu
     *  @param menu an xml menu
     *	@param customizer a MenuBarCustomizer
     *  @return a JMenu or null if errors occured
     */
    public JMenu createMenu(MenuType menu, MenuBarCustomizer customizer)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createMenu(MenuType, MenuBarCustomizer)");
	}
	JMenu result = null;
        if ( menu != null )
        {   
	    MenuBarCustomizer customizerUsed = customizer;
	    
	    if ( customizerUsed == null )
	    {
		customizerUsed = this.getDefaultMenuBarCustomizer();
	    }
	    
            /* sort the menu */
            if ( menu.getMenuOrItemOrCheck() != null )
            {   Collections.sort(menu.getMenuOrItemOrCheck(), getOrderedComparator()); }
            
	    result = customizerUsed.createMenu(menu);
	    if ( result != null )
	    {
//		customizerUsed.feedComponent(result, menu.getMenuOrItemOrCheck());
	    }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createMenu(MenuType, MenuBarCustomizer)");
	}
        
        return result;
    }
        
    /** sort the given list recursively
     *  @param  item an object containing AbstractMenuItem (Menubar, Menu, TypeMenu)
     */
    protected void sortMenuItem(Object item)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering sortMenuItem(Object)");
	}
	if ( item != null )
        {   if ( item instanceof List )
            {
                /* sort sublist */
                Iterator it = ((List)item).iterator();
                while(it.hasNext())
                {   Object current = it.next();
//                        if ( current instanceof Menu )
                    {   sortMenuItem( current ); }//((Menu)current).getMenuOrButtonOrCheck() ); }
                }
                
                Collections.sort((List)item, getOrderedComparator());
            }
            else if( item instanceof Menubar )
            {   sortMenuItem( ((Menubar)item).getMenus().getMenu() ); }
            else if( item instanceof MenuType )
            {   sortMenuItem( ((MenuType)item).getMenuOrItemOrCheck() ); }
            else if( item instanceof TypeMenu )
            {   sortMenuItem( ((TypeMenu)item).getItems().getMenuOrItemOrCheck() ); }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting sortMenuItem(Object)");
	}
    }
    
    /* #########################################################################
     * ####################### Junit test methods ##############################
     * ######################################################################### */
    
    /** complete a JToolbar according to an xml Toolbar declaration
     *	created to be used for JUnit test
     *  @param toolbar a JToolBar
     *  @param toolbarXml an xml Toolbar
     */
    protected void feedToolbar(JToolBar toolbar, Toolbar toolbarXml)
    {   this.getDefaultToolBarCustomizer().feedComponent(toolbar, toolbarXml == null ? null : toolbarXml.getItems().getButtonOrCheckOrCombo()); }
    
    /* #########################################################################
     * ######################### Comparators getter ############################
     * ######################################################################### */
    
    /** return a comparator that can be used with ordered items
     *  @return a Comparator for OrderedElement
     */
    private static Comparator getOrderedComparator()
    {   
        Comparator c = ORDERED_ITEM_COMPARATOR_REF.get();
        
        if ( c == null )
        {   c = new OrderedElementComparator();
            ORDERED_ITEM_COMPARATOR_REF = new SoftReference(c);
        }
        
        return c;
    }
    
    /** return a comparator that can be used with items that inherit from PriorityElement
     *  @return a Comparator for PriorityElement
     */
    private static Comparator getPriorityComparator()
    {   
        Comparator c = PRIORITY_ITEM_COMPARATOR_REF.get();
        
        if ( c == null )
        {   c = new PriorityElementComparator();
            PRIORITY_ITEM_COMPARATOR_REF = new SoftReference(c);
        }
        
        return c;
    }
    
}
