/* 
 * Siberia gui : siberia plugin defining basics of graphical application 
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

package org.siberia.ui;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;
import org.siberia.GraphicalResources;
import org.siberia.ResourceLoader;
import org.siberia.editor.Editor;
import org.siberia.editor.agreement.SupportAgreement;
import org.siberia.editor.support.EditorSupport;
import org.siberia.env.PluginResources;
import org.siberia.env.SiberExtension;
import org.siberia.eventsystem.EventConfigurationEngine;
import org.siberia.eventsystem.item.EventSystemItem;
import org.siberia.exception.ResourceException;
import org.siberia.kernel.Kernel;
import org.siberia.kernel.KernelDependant;
import org.siberia.kernel.closure.ClosureRefusedReport;
import org.siberia.kernel.closure.ClosureRefusedReportItem;
import org.siberia.kernel.editor.EditorLauncher;
import org.siberia.kernel.resource.ColdResource;
import org.siberia.TypeInformationProvider;
import org.siberia.properties.PropertiesManager;
import org.siberia.type.SibType;
import org.siberia.ui.bar.PluginBarFactory;
import org.siberia.ui.bar.PluginBarProvider;
import org.siberia.ui.editor.EditorDialog;
import org.siberia.ui.swix.ExtendedSwingEngine;
import org.java.plugin.registry.Extension.Parameter;
import org.siberia.PluginClass;
//import org.siberia.utilities.util.Parameter;
import org.swixml.SwingEngine;
import javax.swing.Icon;
import org.siberia.SiberiaApplication;
import org.siberia.SiberiaGUIApplication;
import org.siberia.utilities.task.TaskStatus;
import org.siberia.editor.launch.EditorLaunchContext;
import org.siberia.editor.launch.EditorLaunchInformation;
import org.siberia.ui.IconCache;
import org.siberia.properties.PropertiesManager.PropertiesNotRegisteredException;


/**
 *
 *  Class which is the main frame of the software
 *
 *  @author alexis
 */
public class UserInterface implements EditorLauncher
{
    /** amount of time unit for laf registration */
    private static final int    LAF_REGISTRATION_TIME_UNIT             = 30;
    
    /** amount of time unit for configuration of the graphic engine */
    private static final int    GRAPHIC_ENGINE_CONFIGURATION_TIME_UNIT = 10;
    
    /** amount of time unit for creating gui */
    private static final int    GUI_CREATION_TIME_UNIT                 = 20;
    
    /** amount of time unit for registration of EditorSupport */
    private static final int    EDITOR_SUPPORT_REGISTRATION_TIME_UNIT  = 20;
    
    /** amount of time unit for gui configuration */
    private static final int    GUI_CONFIGURATION_TIME_UNIT            = 20;
    
    
    /** path of the xml graphical interface definition **/
    private static final String GUI_DEFINITION_PATH                    = "interface.xml";
    
    /** id in the event system */
    public static final  String EVENT_SYSTEM_ID                        = "ui";
    
    /** id of the property which is related to the look and feel to apply */
    public static final  String LOOK_AND_FEEL_PROPERTY_ID              = "frame.main.lookandfeel";
    
    /** singleton */
    private static UserInterface                 instance           = new UserInterface();
    
    /** logger */
    private        Logger                        logger             = Logger.getLogger(UserInterface.class);
    
    /** map containing the graphical components declared by xml declaration */
    private        Map<String, Component>        components         = null;
    
    /** set of support for editors */
    public         Set<EditorSupport>            editorSupports     = null;
    
    /** working list of Editor supports */
    private        List<EditorSupport>           workingSupportList = null;
    
    /** reference to the frame */
    private        JFrame                        frame              = null;
    
    /** container build from graphic engine */
    private        Container                     container          = null;
    
    /** swing engine */
    private        SwingEngine                   engine             = null;
    
    /** map linking editor and their relative information */
//    private        Map<Editor
    
    /** build a new main frame **/
    public UserInterface()
    {   }
    
    public static UserInterface getInstance()
    {   if ( instance == null )
        {
            instance = new UserInterface();
        }
        
        return instance;
    }
    
    /** install look and feels */
    protected void installLookAndFeels()
    {   /* search for registered look and feel and install them */
        Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(org.siberia.SiberiaLafPlugin.LAF_EXTENSION_ID);
        if ( extensions != null )
        {
            /** set containing the class name of already installed lookandfeels
             *  to avoid installing the same Laf
             */
            Set<String> alreadyInstalledLaf = new HashSet<String>();
            LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
            for(int i = 0; i < infos.length; i++)
            {   LookAndFeelInfo laf = infos[i];
                alreadyInstalledLaf.add(laf.getClassName());
            }
            
            /* get the name of the os */
            String osName = System.getProperty("os.name").toLowerCase();
            
            Iterator<SiberExtension> it = extensions.iterator();
            while(it.hasNext())
            {   SiberExtension extension = it.next();
                
                Parameter paramName    = extension.getParameter("name");
                Parameter paramClass   = extension.getParameter("class");
                Parameter paramOs      = extension.getParameter("osPattern");
                
                if (paramName != null && paramClass != null )
                {   boolean newLafDetected = false;
                    PluginClass c = null;
                    /* we have to check if the look and feel is not already installed */
                    try
                    {   c = new PluginClass(paramClass.valueAsString());
                        
                        newLafDetected = ! alreadyInstalledLaf.contains(c.getClassName());
                    }
                    catch(Exception e)
                    {   logger.error("unable to parse class definition '" + paramClass.valueAsString() + "'"); }
                    
                    if ( newLafDetected )
                    {
                        boolean osCompliant = false;
                        if ( paramOs == null )
                        {   osCompliant = true; }
                        else
                        {   try
                            {   java.util.regex.Pattern pat     = java.util.regex.Pattern.compile(paramOs.valueAsString());
                                java.util.regex.Matcher matcher = pat.matcher(osName);
                                osCompliant = matcher.find();
                            }
                            catch(Exception e)
                            {   logger.info("laf '" + paramName.valueAsString() + "' ignored"); }
                        }

                        if ( osCompliant )
                        {   
                            /* verify that the class can be loaded by this plugin
                             *  I must provide some error traces since any laf class has to be loaded by this plugin
                             */
                            try
                            {   
//                                Class.forName(c.getClassName());
                            
                                /* register it to UIManager
                                 *
                                 * Warning : the class name installed in UIManager is the siberia representation of a Class : 
                                 *
                                 *  example : siberia::org.siberia.....
                                 */
                                UIManager.installLookAndFeel( paramName.valueAsString(), paramClass.valueAsString() );
                                logger.info("installing look and feel " +
                                                    "(name=" + (paramName == null ? "null" : paramName.valueAsString()) + ", " +
                                                    "class=" + (paramClass == null ? "null" : paramClass.valueAsString()) + ")");
                            }
                            catch(Exception e)
                            {   logger.error("class '" + c.getClassName() + "' could not be loaded by this plugin"); }
                        }
                    }
                    else
                    {   logger.info("lookAndFeel '" + paramName.valueAsString() + "' is already installed"); }
                }
                else
                {   logger.error("unable to install look and feel " +
                                        "(name=" + (paramName == null ? "null" : paramName.valueAsString()) + ", " +
                                        "(class=" + (paramClass == null ? "null" : paramClass.valueAsString()) + ")");
                    
                }
            }
        }
    }
    
    /** apply a look and feel
     *  @param lookAndFeel the name of a look and feel
     */
    public void applyLookAndFeelByName(String lookAndFeel)
    {
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
	
        String oldLookAndFeel = null;
	
	if ( lafs != null && UIManager.getLookAndFeel() != null )
	{
	    for(int i = 0; i < lafs.length && oldLookAndFeel == null; i++)
	    {
		UIManager.LookAndFeelInfo info = lafs[i];
		
		if ( info != null )
		{
		    if ( UIManager.getLookAndFeel().getClass().getName().equals(info.getClassName()) )
		    {
			oldLookAndFeel = info.getName();
		    }
		}
	    }
	}
	
        lafs = UIManager.getInstalledLookAndFeels();
        
        boolean applied = false;
        if ( lafs != null )
        {   for(int i = 0; i < lafs.length; i++)
            {   UIManager.LookAndFeelInfo info = lafs[i];
                
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("finding laf '" + info.getName() + "'");
		}
		
                if ( info.getName().equals(lookAndFeel) )
                {   try
                    {   
			this.applyLookAndFeelByClass(info.getClassName());
			applied = true;
		    }
                    catch(Exception e)
                    {   logger.error("unable to set lookandfeel '" + lookAndFeel + "'");
                        try
                        {   this.applyLookAndFeelByName(oldLookAndFeel);
                            PropertiesManager.setGeneralProperty(LOOK_AND_FEEL_PROPERTY_ID, oldLookAndFeel);
                        }
                        catch(Exception ex)
                        {   logger.error("unable to save general properties", ex); }
                    }
                    break;
                }
            }
        }
        
        if ( applied )
        {   logger.error("unable to set look and feel : " + lookAndFeel);
			
	    try
	    {
		PropertiesManager.setGeneralProperty(LOOK_AND_FEEL_PROPERTY_ID, lookAndFeel);
	    }
	    catch(Exception e)
	    {
		logger.error("unable to register value '" + UIManager.getLookAndFeel().getName() +
			     "' for property '" + LOOK_AND_FEEL_PROPERTY_ID + "'", e);
	    }
	}
    }
    
    /** apply a look and feel.<br>
     *  because this method could throw exception if Look and feel initialization fails, it is recommended
     *  to call the method <code>applyLookAndFeelByName</code>
     *
     *  @param lookAndFeelClass the class of a look and feel
     */
    private void applyLookAndFeelByClass(final String lookAndFeelClass)
    {
	Runnable runnable = new Runnable()
	{
	    public void run()
	    {
		Class c = null;

		try
		{   c = ResourceLoader.getInstance().getClass(lookAndFeelClass);


		    /* if look and feel class name is like siberia::org.siberia... it means that the laf was installed by a plugin
		     * Else, it means that the laf was installed automatically ( example : Metal ).
		     *
		     * in the last case, nevermind the ClassLoader we used.
		     * In the first case, we have to find the ClassLoader related to the laf and initialize "ClassLoader" 
		     *  UIDefaults parameter.
		     */
		    ClassLoader lafClassLoader = null;
		    try
		    {   PluginClass pluginClass = new PluginClass(lookAndFeelClass);

			if ( pluginClass != null )
			{   String pluginId = pluginClass.getPlugin();

			    if ( pluginId != null )
			    {   lafClassLoader = ResourceLoader.getInstance().getPluginClassLoader(pluginId); }
			}
		    }
		    catch(Exception e)
		    {   logger.error("unable to parse class definition '" + lookAndFeelClass + "'"); }

		    /** have to find the ClassLoader related to the plugin that declared the laf
		     *  we try to install
		     */
		    UIManager.getDefaults().put("ClassLoader", lafClassLoader);

	//            c = this.getClass().forName(lookAndFeelClass);

		    logger.info("class laf trouvé");

		    Object o = c.newInstance();

		    if ( o instanceof LookAndFeel )
		    {   UIManager.setLookAndFeel( (LookAndFeel)o );
			if ( getFrame() != null )
			{
			    SwingUtilities.updateComponentTreeUI(getFrame());
			}
			 
			/** refresh all cached context menu */
			PluginBarFactory.getInstance().updateComponentTreeUI();
		    }
		    else
		    {   logger.error("trying to set a Look and feel that is not an instance of javax.swing.LookAndFeel"); }
		}
		catch(ResourceException e)
		{   logger.error("unable to find class " + lookAndFeelClass); }//, e); }
		catch(Exception e)
		{   logger.error("unable to create LookAndFeel " + lookAndFeelClass); }//, e); }
	    }
	};
	
	if ( SwingUtilities.isEventDispatchThread() )
	{
	    runnable.run();
	}
	else
	{
	    SwingUtilities.invokeLater(runnable);
	}
    }
    
    /** install system tray item */
    protected void installSystemTray()
    {
	if ( false )//SystemTray.isSupported() )
	{
	    SystemTray tray = SystemTray.getSystemTray();
	    
	    JPopupMenu popup = PluginBarFactory.getInstance().createSystemTrayPopupMenu();
	    
	    PopupMenu menu = null;
	    
	    if ( popup != null )
	    {
		for(int i = 0; i < popup.getComponentCount(); i++)
		{
		    final Component c = popup.getComponent(i);
		    
		    if ( c instanceof JMenuItem )
		    {
			if ( menu == null )
			{
			    menu = new PopupMenu();
			}
			
			MenuItem item = new MenuItem( ((JMenuItem)c).getText() );
			
			item.addActionListener(new ActionListener()
			{
			    public void actionPerformed(ActionEvent e)
			    {
				Action a = ((JMenuItem)c).getAction();
				if ( a != null )
				{
				    a.actionPerformed(e);
				}
			    }
			});
			
			menu.add(item);
		    }
		    else if ( c instanceof JSeparator )
		    {
			if ( menu == null )
			{
			    menu = new PopupMenu();
			}
			
			menu.addSeparator();
		    }
		}
	    }
	    
	    final TrayIcon trayIcon = new TrayIcon(this.getFrame().getIconImage(),
					     ResourceLoader.getInstance().getApplicationName(),
					     menu);

	    ActionListener actionListener = new ActionListener()
	    {
		public void actionPerformed(ActionEvent e)
		{
		    trayIcon.displayMessage("Action Event",
			    "An Action Event Has Been Performed!",
			    TrayIcon.MessageType.INFO);
		}
	    };

	    trayIcon.setImageAutoSize(true);
	    trayIcon.addActionListener(actionListener);
//	    trayIcon.addMouseListener(mouseListener);

	    try
	    {
		tray.add(trayIcon);
	    }
	    catch (AWTException e)
	    {
		logger.error("unable to install system tray icon", e);
	    }
	}
    }
    
    /** return the frame */
    public Frame getFrame()
    {   return this.frame; }
    
    /** return the component with id 'id' in the user interface declaration
     *  @param id a String
     *  @return an Object representing a graphical component or null if not found
     */
    public Object getComponent(String id)
    {   Object c = null;
        if ( this.components != null )
            c = this.components.get(id);
        return c;
    }
    
    /** configure that listening caracteristics of every component that composed the gui
     *  @param it an Iterator over all components used
     */
    protected void configureGraphicalEventSystem(Iterator it)
    {   if ( it != null )
        {   while(it.hasNext())
            {   Object current = it.next();
                
                if ( current instanceof EventSystemItem )
                    EventConfigurationEngine.configure( (EventSystemItem)current );
            }
        }
    }
    
    /** not call in EDT !!! */
    public void start(final TaskStatus status)
    {
	Runnable run = null;
	
        /** install look and feels */
	run = new Runnable()
	{   public void run()
	    {
		if ( status != null )
		{   status.setLabel( ((SiberiaGUIApplication)SiberiaApplication.instance).getGuiResourcebundle().getString("installLaf")); }
	    }
	};
	if ( SwingUtilities.isEventDispatchThread() )
	{   run.run(); }
	else
	{   SwingUtilities.invokeLater(run); }
	
	run = new Runnable()
	{   public void run() {	installLookAndFeels(); } };
	if ( SwingUtilities.isEventDispatchThread() )
	{   run.run(); }
	else
	{   SwingUtilities.invokeLater(run); }
	
	run = new Runnable()
	{   
	    public void run()
	    {
		if ( status != null )
		{   status.setPercentageCompleted(status.getPercentageCompleted() + LAF_REGISTRATION_TIME_UNIT); }
	    }
	};
	if ( SwingUtilities.isEventDispatchThread() )
	{   run.run(); }
	else
	{   SwingUtilities.invokeLater(run); }
        
	run = new Runnable()
	{   public void run()
	    {
		// 30' will be enough !!; 
		ToolTipManager.sharedInstance().setDismissDelay(30000);
	    }
	};
	if ( SwingUtilities.isEventDispatchThread() )
	{   run.run(); }
	else
	{   SwingUtilities.invokeLater(run); }
        
        try
        {
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("application plugin id : " + Kernel.getInstance().getPluginContext().getApplicationPluginId());
	    }
	    
            ClassLoader loader = ResourceLoader.getInstance().getPluginClassLoader(
                                Kernel.getInstance().getPluginContext().getApplicationPluginId());
            
            final URL url = ResourceLoader.getInstance().getResource(GUI_DEFINITION_PATH,
                                loader);
            
	    run = new Runnable()
	    {   
		public void run()
		{
		    if ( status != null )
		    {   status.setLabel(((SiberiaGUIApplication)SiberiaApplication.instance).getGuiResourcebundle().getString("graphicEngineConfig")); }
		}
	    };
	    if ( SwingUtilities.isEventDispatchThread() )
	    {   run.run(); }
	    else
	    {   SwingUtilities.invokeLater(run); }
	    
	    run = new Runnable()
	    {   public void run() { engine = new ExtendedSwingEngine(); } };
	    if ( SwingUtilities.isEventDispatchThread() )
	    {   run.run(); }
	    else
	    {   SwingUtilities.invokeLater(run); }
	    
	    run = new Runnable()
	    {   
		public void run()
		{
		    if ( status != null )
		    {   status.setPercentageCompleted(status.getPercentageCompleted() + GRAPHIC_ENGINE_CONFIGURATION_TIME_UNIT); }
		    if ( status != null )
		    {   status.setLabel(((SiberiaGUIApplication)SiberiaApplication.instance).getGuiResourcebundle().getString("uiCreation")); }
		}
	    };
	    if ( SwingUtilities.isEventDispatchThread() )
	    {   run.run(); }
	    else
	    {   SwingUtilities.invokeLater(run); }
	    
	    run = new Runnable()
	    {   public void run()
		{
		    try
		    {
			container = engine.render( new File(url.toURI()) );
		    }
		    catch (Exception ex)
		    {
			logger.error("error while building container", ex);
		    }
		}
	    };
	    if ( SwingUtilities.isEventDispatchThread() )
	    {   run.run(); }
	    else
	    {   SwingUtilities.invokeLater(run); }
	    
	    run = new Runnable()
	    {   
		public void run()
		{
		    if ( status != null )
		    {   status.setPercentageCompleted(status.getPercentageCompleted() + GUI_CREATION_TIME_UNIT); }
		}
	    };
	    if ( SwingUtilities.isEventDispatchThread() )
	    {   run.run(); }
	    else
	    {   SwingUtilities.invokeLater(run); }
            
            /** configure listening system for every component that claims for it */
	    run = new Runnable()
	    {   public void run() { configureGraphicalEventSystem(engine.getAllComponentItertor()); } };
	    if ( SwingUtilities.isEventDispatchThread() )
	    {   run.run(); }
	    else
	    {   SwingUtilities.invokeLater(run); }
            
	    run = new Runnable()
	    {   public void run()
		{
		    Map m = engine.getIdMap();

		    if ( m != null )
		    {   Iterator it = m.keySet().iterator();

			if ( status != null )
			{   status.setLabel(((SiberiaGUIApplication)SiberiaApplication.instance).getGuiResourcebundle().getString("editorSupportRegistration")); }

			while(it.hasNext())
			{   Object key   = it.next();
			    Object value = m.get(key);

			    if ( key instanceof String )
			    {   if ( value instanceof Component )
				{   if ( components == null )
					components = new HashMap<String, Component>(m.size());
				    components.put( (String)key, (Component)value );
				}

				if ( value instanceof EditorSupport )
				{   if ( editorSupports == null )
					editorSupports = new HashSet<EditorSupport>();
				    editorSupports.add( (EditorSupport)value );
				}

				/* if component implements KernelDependant, ... */
				if ( value instanceof KernelDependant )
				{   KernelDependant dpt = (KernelDependant)value;

				    Iterator resources = Kernel.getInstance().getResources().resources();

				    while(resources.hasNext())
				    {   Object current = resources.next();
					if ( current instanceof ColdResource )
					{   dpt.declareResource( (ColdResource)current ); }
				    }
				}
			    }
			}

			if ( status != null )
			{   status.setPercentageCompleted(status.getPercentageCompleted() + EDITOR_SUPPORT_REGISTRATION_TIME_UNIT); }
	//                it = m.keySet().iterator();
	//                while(it.hasNext())
	//                {   Object current = it.next();
	//                    if ( current instanceof String )
	//                        engine.forget( (String)current );
	//                }
		    }
		}
	    };
	    if ( SwingUtilities.isEventDispatchThread() )
	    {   run.run(); }
	    else
	    {   SwingUtilities.invokeLater(run); }
	    
            engine = null;
        }
        catch(Exception e)
        {   e.printStackTrace(); }
        
	run = new Runnable()
	{   
	    public void run()
	    {
		/* keep reference to the frame */
		if ( container instanceof JFrame )
		{   frame = (JFrame)container;

		    if ( status != null )
		    {   status.setLabel(((SiberiaGUIApplication)SiberiaApplication.instance).getGuiResourcebundle().getString("uiConfiguration")); }

		    GraphicalResources.getInstance().setMainFrame(frame);

		    /** apply look and feel declared in properties */
		    Object laf = PropertiesManager.getGeneralProperty(LOOK_AND_FEEL_PROPERTY_ID);
		    if ( laf instanceof String )
		    {   applyLookAndFeelByName( (String)laf ); }

		    Object o = PropertiesManager.getGeneralProperty("frame.main.maximized");

		    boolean considerFramePositionning = true;
		    if ( o instanceof Boolean )
		    {   if ( ((Boolean)o).booleanValue() )
			{
			    considerFramePositionning = false;

			    /* call the pack before fixing the extended state */
			    Dimension tailleEcran = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			    frame.setSize(tailleEcran);
			    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
			}
		    }

		    if ( considerFramePositionning )
		    {   try
			{   Number x = (Number)PropertiesManager.getGeneralProperty("frame.main.x");
			    Number y = (Number)PropertiesManager.getGeneralProperty("frame.main.y");

			    frame.setLocation(x.intValue(), y.intValue());
			}
			catch(Exception e)
			{   frame.setLocation(0, 0); }

			try
			{   Number width = (Number)PropertiesManager.getGeneralProperty("frame.main.width");
			    Number height = (Number)PropertiesManager.getGeneralProperty("frame.main.height");

			    frame.setSize(new Dimension(width.intValue(), height.intValue()));
			}
			catch(Exception e)
			{   frame.setSize(800, 600); }
		    }

		    if ( status != null )
		    {   status.setPercentageCompleted(status.getPercentageCompleted() + GUI_CONFIGURATION_TIME_UNIT); }

		    // TODO
		    System.out.println("installing control+TAB control+TAB control+TAB control+TAB control+TAB control+TAB control+TAB ");
		    ((JComponent)frame.getContentPane()).getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
					put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_MASK), 
					    "editors");
		    ((JComponent)frame.getContentPane()).getActionMap().put("editors", new AbstractAction()
		    {
			public void actionPerformed(ActionEvent e)
			{
			    System.out.println("actionPerformed");
			    JPopupMenu menu = new JPopupMenu();
			    menu.add(new JMenuItem("a"));
			    menu.add(new JMenuItem("b"));
			    menu.add(new JMenuItem("c"));

			    menu.setVisible(true);
			}
		    });
		}
	    }
	};
	if ( SwingUtilities.isEventDispatchThread() )
	{   run.run(); }
	else
	{   SwingUtilities.invokeLater(run); }
	    
	    
	run = new Runnable()
	{
	    public void run()
	    {
		/** install system tray if necessary */
		installSystemTray();

		frame.setVisible( true );

		/* modify positionning parameters when exit */
		if ( frame != null )
		{   frame.addWindowListener(new WindowAdapter()
		    {
			/**
			 * Invoked when a window is in the process of being closed.
			 * The close operation can be overridden at this point.
			 */
			public void windowClosing(WindowEvent e)
			{
			    tryToClose();
			}
		    });
		}
	    }
	};
	if ( SwingUtilities.isEventDispatchThread() )
	{   run.run(); }
	else
	{   SwingUtilities.invokeLater(run); }
    }
    
    /** try to close the application */
    public void tryToClose()
    {
	/** ask kernel if it allows application close */
	ClosureRefusedReport report = Kernel.getInstance().checkCloseAllowed();

	if ( report == null || report.isEmpty() )
	{
	    try
	    {
		Object automaticPositioning = PropertiesManager.getGeneralProperty("frame.main.automaticpositioning");

		if ( automaticPositioning instanceof Boolean && ((Boolean)automaticPositioning).booleanValue() )
		{
		    if ( frame.getExtendedState() == Frame.MAXIMIZED_BOTH )
		    {   PropertiesManager.setGeneralProperty("frame.main.maximized", Boolean.TRUE); }
		    else
		    {   /* else, the frame should not be extended */
			PropertiesManager.setGeneralProperties(
					    new String[]{"frame.main.x", "frame.main.y",
					    "frame.main.width", "frame.main.height", "frame.main.maximized"},
					    new Object[]{frame.getLocationOnScreen().x, frame.getLocationOnScreen().y,
					    frame.getSize().width, frame.getSize().height, Boolean.FALSE});
		    }
		}
	    }
	    catch(Exception ex)
	    {   logger.error("unable to fix properties", ex); }

	    try
	    {
		frame.setVisible(false);

		Kernel.getInstance().close(false);
		Kernel.getInstance().setProperlyClosed();

		frame.dispose();

		System.exit(0);
	    }
	    catch(Exception ex)
	    {
		logger.error("errors occured while closing kernel", ex);
	    }
	}
	else
	{
	    ResourceBundle rb = ResourceBundle.getBundle(UserInterface.class.getName());

	    /* show messages */
	    StringBuffer buffer = new StringBuffer();

	    buffer.append("<html>" + rb.getString("close.error.dialog.message"));

	    buffer.append("<ul>");

	    int count = 0;
	    Iterator<ClosureRefusedReportItem> items = report.items();

	    while( items.hasNext() && count < 12 )
	    {
		ClosureRefusedReportItem item = items.next();

		buffer.append("<li>" + item.getMessage() + "</li>");

		count++;
	    }

	    if ( items.hasNext() )
	    {
		buffer.append("<li>...</li>");
	    }

	    buffer.append("<ul>");

	    buffer.append("</html>");

	    JOptionPane.showMessageDialog(UserInterface.getInstance().getFrame(),
					  buffer.toString(),
					  rb.getString("close.error.dialog.title"),
					  JOptionPane.ERROR_MESSAGE);
	}
    }

    /**
     * return the priority of the launcher
     * 
     * @return the priority of the launcher. if this priority is important, the launcher will be asked first to launch editor.
     *      a launcher with priority 0 will be asked to launch the editor if no launcher with a greater priority accept to care about the editor.
     */
    public int getPriority()
    {   return 0; }
    
    /** ask launcher to close all editor related to the given type
     *	@param type a SibType
     */
    public void closeEditorRelatedToInstance(SibType type)
    {
	if ( type != null && this.editorSupports != null )
	{
	    Iterator<EditorSupport> supports = this.editorSupports.iterator();
	    
	    while(supports.hasNext())
	    {
		EditorSupport current = supports.next();
		
		if ( current != null )
		{
		    Editor editor = current.getEditor(type);
		    
		    if ( editor != null )
		    {
			editor.close();
		    }
		}
	    }
	}
    }

    /**
     * ask to launch an Editor according to the given context
     * 
     * @param context an EditorLaunchContext
     * @return true if it has succeeded
     */
    public boolean launch(EditorLaunchContext context)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling launch with context " + context);
	}
	
        EditingErrorReport report = null;
        
        /* search for the best support to edit those items */
        if ( this.editorSupports == null )
        {   
	    logger.error("no EditorSupport regsitered --> the item related to context could not be edited");
	}
	else
	{
	    /* perhaps the item to edit is already edited, in this case, no need to create another editor */
	    Iterator<EditorSupport> _supports = this.editorSupports.iterator();
	    
	    while(_supports.hasNext())
	    {
		EditorSupport _current = _supports.next();
		
		if ( _current != null )
		{
		    if ( _current.isRegistering(context.getItem()) )
		    {
			_current.show(context.getItem());
			return true;
		    }
		}
	    }
	    
            EditorDialog dialog = new EditorDialog(context);
            
            dialog.display();
            
            List<Class> editorClasses = dialog.getEditorClasses();
            
            boolean editorFound = false;
            
            if ( editorClasses == null )
            {
		logger.error("EditorDialog returns no editor classes for context : " + context);
	    }
	    else
	    {
                /* use factory to create the editor */
                List<Editor> edit = new ArrayList<Editor>(editorClasses.size());
                for(int i = 0; i < editorClasses.size(); i++)
                {   Class c = editorClasses.get(i);
		    
		    Editor editor = null;
		    
                    if ( c != null )
                    {
			/** check LaunchedInstancesMaximum */
			Integer maxInstances = Kernel.getInstance().getPluginContext().getLaunchedInstancesMaximumForEditorKind(c);
			
			boolean createNewEditor = true;
			
			if ( maxInstances != null && maxInstances.intValue() > 0 )
			{
			    Set<Editor> registeredEditors = Kernel.getInstance().getEditorRegistry().getEditors(c);
			    
			    if ( registeredEditors != null && registeredEditors.size() >= maxInstances.intValue() )
			    {
				/* take the first editor !! */
				Iterator<Editor> itRegisteredEditors = registeredEditors.iterator();
				
				while(itRegisteredEditors.hasNext())
				{
				    editor = itRegisteredEditors.next();
				    
				    if ( editor != null )
				    {
					createNewEditor = false;
				
					break;
				    }
				}
			    }
			}
			
			if ( createNewEditor )
			{
			    try
			    {   
				/** try to get an Editor of that kind in the cache */
				editor = Kernel.getInstance().getEditorRegistry().getEditorFromCache(c, new EditorLaunchInformation());

				if ( editor == null )
				{   logger.info("building editor of kind " + c);
				    editor = (Editor)c.newInstance();
				}
				else
				{   logger.info("recovering an existing editor from cache"); }

//				edit.add( editor );
				EventConfigurationEngine.configure(editor);
			    }
			    catch(Exception ex)
			    {   ex.printStackTrace(); }
			}
                    }
		    
		    edit.add(editor);
                }
                
                Iterator<Editor> editors = edit.iterator();
                if ( editors == null )
                {
		    logger.error("no editors found for context " + context);
		}
		else
		{
                    final EditorLaunchContext currentContext = context;
                        
                    Editor editor = editors.next();

                    if ( currentContext != null )
                    {   if ( editor != null )
                        {   /* initialize instance-editor relation */
			    
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("before setting instance " + currentContext.getItem() + " for editor " + editor);
			    }
                            editor.setInstance(currentContext.getItem());
			    
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("after setting instance " + currentContext.getItem() + " for editor " + editor);
			    }

                            /* find the best EditorSupport for the current editor */
                            SupportAgreement bestAgreement = null;
			    
			    /** create a new list with all support and sort it */
			    if ( this.workingSupportList == null )
			    {
				this.workingSupportList = new ArrayList<EditorSupport>(this.editorSupports.size());
			    }
			    else
			    {
				this.workingSupportList.clear();
			    }
			    
			    this.workingSupportList.addAll(this.editorSupports);
			    
			    Collections.sort(this.workingSupportList, Collections.reverseOrder(new Comparator<EditorSupport>()
			    {
				public int compare(EditorSupport o1, EditorSupport o2)
				{
				    int result = 0;
				    
				    if ( o1 == null )
				    {
					if ( o2 != null )
					{
					    result = -1;
					}
				    }
				    else
				    {
					if ( o2 == null )
					{
					    result = 1;
					}
					else
					{
					    result = o1.getPriority() - o2.getPriority();
					}
				    }
				    
				    return result;
				}
			    }));
			    
                            Iterator<EditorSupport> it = this.editorSupports.iterator();
			    
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("looping on editor support to find the support with the best agreement");
			    }
			    
			    for(int i = 0; i < this.workingSupportList.size(); i++)
			    {
                                EditorSupport current = this.workingSupportList.get(i);

                                if ( current != null )
                                {   SupportAgreement ag = current.agreeToSupport(editor, currentContext);

				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("editor support " + current + " returns agreement " + ag);
				    }
				    
                                    if ( bestAgreement == null )
                                    {   bestAgreement = ag; }
                                    else
                                    {   if ( ag.compareTo(bestAgreement) > 0 )
					{
                                            bestAgreement = ag;
					}
                                    }
                                }
                            }
			    
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("choosen editor support is " + editor + " for context " + context);
			    }

                            final SupportAgreement finalAgreement = bestAgreement;
                            final Editor           finalEditor    = editor;

                            if ( bestAgreement != null )
                            {   
                                /* add the new editor into the best EditorSupport */
                                Runnable runnable = new Runnable()
                                {
                                    public void run()

                                    {
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("asking editor support " + finalAgreement.getSupport().getClass() + " to register editor " + finalEditor);
					}
					
                                        long startTime = System.currentTimeMillis();
                                        finalAgreement.getSupport().register(finalEditor, currentContext);
                                        logger.info("registering " +
                                                            (finalEditor == null ? "null" : finalEditor.getClass()) +
                                                            " in " + (System.currentTimeMillis() - startTime) +
                                                            " ms");
                                    }
                                };
                                /* causes strange problems when using infonode docking framework !! so forget for the moment */
//                                    SwingUtilities.invokeLater();
                                runnable.run();

                                bestAgreement.getSupport().register(editor, currentContext);
                                
                                Kernel.getInstance().getEditorRegistry().register(editor, new EditorLaunchInformation());
                            }
                            else
                            {   logger.error("no support found for " + editor.getClass());
                                if ( report == null )
                                    report = new EditingErrorReport();
                                report.addLine("no support found for " + editor.getClass());
                            }
                        }
                        else
                        {   logger.error("no editor found for " + currentContext.getItem().getClass());
                            if ( report == null )
                                report = new EditingErrorReport();
                            report.addLine("no editor found for " + currentContext.getItem().getClass());
                        }
                    }
                    else
                    {   logger.error("trying to edit null");
                        if ( report == null )
                            report = new EditingErrorReport();
                        report.addLine("trying to edit null");
                    }
                }
            }
        }
        
        if ( report != null )
	{
            report.display();
	}
        
        return true;
    }
    
    /** errors editing report */
    public static class EditingErrorReport
    {
        /** content */
        private StringBuffer content     = null;
        
        /** indicates if report has been initialized */
        private boolean      initialized = false;
        
        /** create a new EditingErrorReport */
        public EditingErrorReport()
        {   }
        
        /** initialize report */
        private void initialize()
        {   if ( ! this.initialized )
            {   this.content = new StringBuffer();
                
                this.content.append("<html>Errors occured when editing items : <ul>");
                
            }
        }
        
        /** add an error to the report
         *  @param line
         */
        public void addLine(String line)
        {   if ( line != null )
            {   String trimmed = line.trim();
                if ( trimmed.length() > 0 )
                {
                    if ( ! this.initialized )
                    {   this.initialize(); }
                    
                    this.content.append("<li>" + trimmed + "</li>");
                }
            }
        }
        
        /** show the report */
        public void display()
        {   if ( this.content != null )
            {   String stringContent = this.content.toString().trim();
                
                if ( stringContent.length() > 0 )
                {   if ( ! stringContent.endsWith("</html>") )
                    {   stringContent += "</html>"; }
                    
                    JOptionPane.showMessageDialog(UserInterface.getInstance().getFrame(),
                                        stringContent, "Editing error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
    }
}
