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
//package org.siberia.binding.impl.db.jdo.jpox;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.Enumeration;
//import org.jpox.exceptions.ClassNotResolvedException;
//import org.siberia.ResourceLoader;
//import org.siberia.exception.ResourceException;
//
///**
// *
// * @author alexis
// */
//public class SiberiaClassLoaderResolver extends org.jpox.JDOClassLoaderResolver
//{   
//
//    /**
//     * JDO's Class Loading mechanism (Spec 1.0.1 Chapter 12.5).
//     * Try 3 loaders, starting with user-supplied loader, then try
//     * the current thread's loader, and finally try the PM context
//     * loader. This method does not initialize the class
//     * @param name Name of the Class to be loaded
//     * @param primary primary ClassLoader to use (or null)
//     * @return The class given the name, using the required loader.
//     * @throws ClassNotResolvedException if the class can't be found in the classpath
//     */
//    public Class classForName(String name, ClassLoader primary)
//    {   //System.out.println("calling classForName with " + name);
//        return this.classForName(name, primary, true); }
//    
//    /**
//     * JDO's Class Loading mechanism (Spec 1.0.1 Chapter 12.5)
//     * @param name Name of the Class to be loaded
//     * @param primary the primary ClassLoader to use (or null)
//     * @param initialize whether to initialize the class or not.
//     * @return The Class given the name, using the specified ClassLoader
//     * @throws ClassNotResolvedException if the class can't be found in the classpath
//     */
//    @Override
//    public Class classForName(String name, ClassLoader primary, boolean initialize)
//    {   
////	System.out.println("resolving class : " + name);
//        Class c = null;
//        ClassNotResolvedException exception = null;
//
//
//	
//
//        try
//        {   c = super.classForName(name, primary, initialize); }
//        catch(ClassNotResolvedException e)
//        {   exception = e; }
//
//        if ( c == null )
//        {   try
//            {   c = this.getClass().getClassLoader().loadClass(name); }
//            catch(ClassNotFoundException e)
//            {   exception = new ClassNotResolvedException(e.getMessage()); }
//        }
//        
//        if ( c == null )
//        {   try
//            {   c = ResourceLoader.getInstance().loadClass(name); }
//            catch(ResourceException e)
//            {   exception = new ClassNotResolvedException(e.getMessage()); }
//        }
//        
//        if ( c == null )
//        {   throw exception; }
//	
////	System.out.println("\treturning " + c);
//
//        return c;
//    }
//    
//    
//
//    /**
//     * Finds all the resources with the given name.
//     * @param resourceName the resource name. If <code>resourceName</code> starts with "/", remove it before searching.
//     * @param primary the primary ClassLoader to use (or null)
//     * @return An enumeration of URL objects for the resource. If no resources could be found, the enumeration will be empty. 
//     * Resources that the class loader doesn't have access to will not be in the enumeration.
//     * @throws IOException If I/O errors occur
//     * @see ClassLoader#getResources(java.lang.String)
//     */
//    public Enumeration getResources(String resourceName, ClassLoader primary) throws IOException
//    {   Enumeration e = super.getResources(resourceName, primary);
//        
////        System.out.println("calling getResources(" + resourceName + ", classLoader)");
//        
//        return e;
//    }
//    
//    /**
//     * Finds the resource with the given name.
//     * @param resourceName the path to resource name relative to the classloader root path. If <code>resourceName</code> starts with "/", remove it.   
//     * @param primary the primary ClassLoader to use (or null)
//     * @return A URL object for reading the resource, or null if the resource could not be found or the invoker doesn't have adequate privileges to get the resource. 
//     * @throws IOException If I/O errors occur
//     * @see ClassLoader#getResource(java.lang.String)
//     */
//    public URL getResource(String resourceName, ClassLoader primary)
//    {   URL e = super.getResource(resourceName, primary);
//        
////        System.out.println("calling getResource(" + resourceName + ", classLoader)");
//        
//        return e;
//    }
//}
