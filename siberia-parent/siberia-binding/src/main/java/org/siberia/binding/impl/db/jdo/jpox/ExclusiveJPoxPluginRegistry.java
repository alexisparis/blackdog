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
///*
// * NullPluginRegistry.java
// *
// * Created on 18 avril 2007, 19:57
// *
// * To change this template, choose Tools | Template Manager
// * and open the template in the editor.
// */
//
//package org.siberia.binding.impl.db.jdo.jpox;
//
//import java.io.File;
//import java.io.FilenameFilter;
//import java.io.IOException;
//import java.net.URL;
//import java.util.Enumeration;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//import javax.jdo.JDOFatalException;
//import org.jpox.ClassLoaderResolver;
//import org.jpox.plugin.ConfigurationElement;
//import org.jpox.plugin.Extension;
//import org.jpox.plugin.ExtensionPoint;
//import org.jpox.plugin.NonManagedPluginRegistry;
//import org.jpox.util.JavaUtils;
//
///**
// *
// * Extension to NonManagedPluginRegistry to avoid most of the initialization of PluginManager
// *  
// *  Considered Extensions and Extension points are only those defined by JPox jar
// *
// * @author alexis
// */
//public class ExclusiveJPoxPluginRegistry extends NonManagedPluginRegistry
//{
//    /** ClassLoaderResolver corresponding to the PMF **/
//    private final ClassLoaderResolver clr;
//
//    /** directories that are searched for plugin files **/
//    // use trailing slash!
//    private static final String[] PLUGIN_DIRS = {"/", "/META-INF/plugins/"};
//
//    /** directories that are searched for plugin files **/
//    // use trailing slash!
//    private static final String[] SELF_PLUGIN_DIRS = {"/jpf/"};
//
//    /** names of additional plugin files have to match this regular expression **/
//    private static final String PLUGIN_FILE_REGEX = "plugin(.+)\\.xml$";
//
//    /** filters all accepted plugin file names **/
//    private static final FilenameFilter PLUGIN_FILE_FILTER = new FilenameFilter()
//    {
//        public boolean accept(File dir, String name)
//        {
//            if (JavaUtils.isJRE1_4OrAbove())
//            {
//                // JDK1.4+ method
//                return name.matches("^" + PLUGIN_FILE_REGEX);
//            }
//            else
//            {
//                return name.equals("plugin.xml");
//            }
//        }
//    };
//    
//    /**
//     * Constructor
//     * @param clr the ClassLoaderResolver
//     */
//    public ExclusiveJPoxPluginRegistry(ClassLoaderResolver clr)
//    {   super(clr);
//        
//        this.clr = clr;
//    }
//    
//    public void registerExtensions()
//    {   
//        //use a set to remove any duplicates
//        Set set = new HashSet();
//
//        try
//        {
//            for (int i = 0; i < PLUGIN_DIRS.length; i++)
//            {
//                // First add all plugin.xml...
//                Enumeration paths = clr.getResources(PLUGIN_DIRS[i] + "plugin.xml",null);
//                while (paths.hasMoreElements())
//                {
//                    Object current = paths.nextElement();
//                    
//                    /** register only extensions and extension points defined by JPox */
//		    
//                    if ( current instanceof URL )
//                    {   
////			if ( ((URL)current).getFile().endsWith("jpox-1.1.7.jar!/plugin.xml") ||
////                             ((URL)current).getFile().endsWith("jpox-java5-1.1.7.jar!/plugin.xml") )
////                        {   set.add(current); } 
//			if ( ((URL)current).getFile().endsWith("jpox-1.2.0-beta-1.jar!/plugin.xml") ||
//                             ((URL)current).getFile().endsWith("jpox-java5-1.2.0-beta-1.jar!/plugin.xml") )
//                        {   set.add(current); } 
//                    }
//                    else
//                    {   set.add(current); }
//                }
//            }
//            for (int i = 0; i < SELF_PLUGIN_DIRS.length; i++)
//            {
//                // First add all plugin.xml...
//                Enumeration paths = clr.getResources(SELF_PLUGIN_DIRS[i] + "plugin.xml",null);
//                while (paths.hasMoreElements())
//                {
//                    Object current = paths.nextElement();
//                    
//                    /** register only extensions and extension points defined by Siberia-binding */
//                    if ( current instanceof URL )
//                    {   
//			set.add(current);
//                    }
//                    else
//                    {   set.add(current); }
//                }
//            }
//	    
//	    
//        }
//        catch (IOException e)
//        { //TODO: Localisation?
//            throw new JDOFatalException("Error loading resource", e);
//        }
//
//        //parse the files (Extensions are automatically added to ExtensionPoint)
//        Iterator it = set.iterator();
//        while (it.hasNext())
//        {
//            URL plugin = (URL) it.next();
//            registerExtensionPoints(plugin);
//        }
//
//        it = set.iterator();
//        while (it.hasNext())
//        {
//            URL plugin = (URL) it.next();
//            registerExtensions(plugin);
//        }
//	
////	ExtensionPoint[] points = this.getExtensionPoints();
////	for(int i = 0; i < points.length; i++)
////	{
////	    ExtensionPoint pt = points[i];
////	    
////	    System.out.println("\t" + pt.getId() + " " + pt.getName() + " ");
////	    
////	}
////	
////	ExtensionPoint point = this.getExtensionPoint("org.jpox.classloader_resolver");
////	if ( point != null )
////	{
////	    Extension[] extensions = point.getExtensions();
////	    for(int i = 0; i < extensions.length; i++)
////	    {
////		Extension ext = extensions[i];
////
////		ConfigurationElement[] elts = ext.getConfigurationElements();
////		for(int j = 0; j < elts.length; j++)
////		{
////		    ConfigurationElement elt = elts[j];
////		    System.out.println("elt // " + elt.getAttribute("class-name"));
////		}
////	    }
////	}
//    }
//    
//}
