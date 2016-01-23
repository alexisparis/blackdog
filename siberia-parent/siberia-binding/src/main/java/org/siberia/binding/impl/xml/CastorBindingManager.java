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
//package org.siberia.binding.impl.xml;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.PrintWriter;
//import java.lang.ref.SoftReference;
//import java.net.URL;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//import org.apache.log4j.Logger;
//import org.exolab.castor.mapping.Mapping;
//import org.exolab.castor.mapping.MappingException;
//import org.exolab.castor.xml.Marshaller;
//import org.exolab.castor.xml.Unmarshaller;
//import org.siberia.ClassElement;
//import org.siberia.TypeInformationProvider;
//import org.siberia.binding.exception.LoadException;
//import org.siberia.binding.exception.SaveException;
//import org.siberia.binding.impl.XmlBindingManager;
//import org.siberia.env.PluginResources;
//import org.siberia.env.SiberExtension;
//import org.siberia.ResourceLoader;
//import org.siberia.exception.ResourceException;
//import org.siberia.type.SibType;
//import org.xml.sax.InputSource;
//
///**
// *
// * XmlBindingManager based on castor API
// *
// * @author alexis
// */
//public class CastorBindingManager extends XmlBindingManager
//{
//    /** extension point id of castor xml declarations */
//    private static final String CASTOR_DECL_EXTENSION_ID = "castor-declaration";
//    
//    /** logger */
//    public static  Logger logger = Logger.getLogger(CastorBindingManager.class);
//    
//    /** mapping reference */
//    private SoftReference<Mapping> mappingRef = new SoftReference<Mapping>(null);
//    
//    /** Creates a new instance of CastorBindingManager */
//    public CastorBindingManager()
//    {   }
//    
//    /** methods to save the instance in a File
//     *  @param type an Object
//     *  @param file an existing File
//     *
//     *  @exception SaveException if errors occured
//     */
//    protected void store(Object type, File file) throws SaveException
//    {
//        if ( type != null && file != null )
//        {   
//            try
//            {   FileWriter writer = new FileWriter(file);
//                
//                Marshaller marshaller = new Marshaller(writer);
//                marshaller.setMapping(this.getMapping());
//                marshaller.marshal(type);
//            }
//            catch (Exception ex)
//            {   throw new SaveException(ex); }
//        }
//        else
//        {   if ( type == null )
//            {   throw new SaveException(new NullPointerException()); }
//            if ( file == null )
//            {   throw new SaveException(new IOException()); }
//        }
//    }
//    
//    /** methods to save the instance in a File
//     *  @param type an Object
//     *  @param file an existing File
//     *
//     *  @exception LoadException if errors occured
//     */
//    protected Object load(File file) throws LoadException
//    {   Object type = null;
//        
//        try
//        {   FileReader reader = new FileReader(file);
//            Unmarshaller unmarshaller = new Unmarshaller(this.getMapping());
//            type = unmarshaller.unmarshal(reader);
//        }
//        catch(Exception e)
//        {   throw new LoadException(e); }
//        
//        return type;
//    }
//    
//    /** add a new Mapping declaration to the given mapping
//     *  @param mapping a Mapping
//     *  @param element a ClassElement
//     */
//    private void addMapping(Mapping mapping, ClassElement element)
//    {   if ( mapping != null && element != null )
//        {   String className = (String)element.getInformation("classname");
//            String rcPath    = (String)element.getInformation("castorMappingPath");
//            
//            if ( className != null && rcPath != null )
//            {   this.addMapping(mapping, className, rcPath); }
//            
//            /* process sub ClassElements */
//            Set<ClassElement> set = element.getSubClasses();
//            
//            if ( set != null )
//            {   Iterator<ClassElement> it = set.iterator();
//                
//                while(it.hasNext())
//                {   this.addMapping(mapping, it.next()); }
//            }
//        }
//    }
//    
//    /** add a new Mapping declaration to the given mapping
//     *  @param mapping a Mapping
//     *  @param className the name of the class related to the next mapping
//     *  @param mappingPath the plugin path to the mapping declaration
//     */
//    private void addMapping(Mapping mapping, String className, String mappingPath)
//    {   if ( mapping != null )
//        {   InputSource source = null;
//            InputStream stream = null;
//                    
//            try
//            {   URL url = ResourceLoader.getInstance().getRcResource(mappingPath);
//                stream = url.openStream();
//
//                source = new InputSource(stream);
//                mapping.loadMapping(source);
//                logger.info("mapping loaded for class '" + className + "'");
//            }
//            catch(ResourceException e)
//            {   logger.error("unable to find castor declaration resource '" + mappingPath + "'", e); }
//            catch(IOException e)
//            {   logger.error(e.getMessage(), e); }
//            catch(MappingException e)
//            {   logger.error(e.getMessage(), e); }
//            finally
//            {   if ( stream != null )
//                {   try
//                    {   stream.close(); }
//                    catch(IOException e)
//                    {   e.printStackTrace(); }
//                }
//            }
//        }
//    }
//    
//    /** method that returns the mapping to use
//     *  @return a Mapping
//     */
//    private Mapping getMapping()
//    {   Mapping mapping = this.mappingRef.get();
//        
//        if ( mapping == null )
//        {
//            Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(CASTOR_DECL_EXTENSION_ID);
//            
//            /* map contains class name representation as key and path of the xml castor declaration as value */
//            TypeInformationProvider provider = TypeInformationProvider.getInstance();
//            
//            /** we create a tree structure associated to SibType. this tree structure represents the inheritance relationship
//             *  between declared SibType classes.
//             *
//             *  each class element in the tree would be associated with their castor mapping file if it exists.
//             *
//             *  So, we are able to add each mapping in the order of inheritance.
//             *
//             *  In fact, if both SibType classes A and B, with B which inherits from A, the castor
//             *  mapping declaration for B could include this related with class A.
//             *  Consequently, the mapping of class A have to be loaded before mapping for class B.
//             */
//            ClassElement rootElement = provider.getRootClassElement();
//            
//            /* set of ClassLoaders to use in the mapping */
//            Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
//
//            if ( extensions != null )
//            {   Iterator<SiberExtension> it = extensions.iterator();
//                while(it.hasNext())
//                {   SiberExtension extension = it.next();
//
//                    String path = extension.getStringParameterValue("path");
//                    String c    = extension.getStringParameterValue("class");
//
//                    if ( path != null && c != null )
//                    {   
//                        /* feed ClassLoader set */
//                        ClassLoader classLoader = ResourceLoader.getInstance().getPluginClassLoaderForClassName(c);
//                        
//                        if ( classLoader != null )
//                        {   
//                            try
//                            {   Class cl = ResourceLoader.getInstance().getClass(c);
//                                
//                                rootElement.addInformation(cl, "castorMappingPath", path);
//                                rootElement.addInformation(cl, "classname", c);
//                            }
//                            catch(ResourceException e)
//                            {   logger.error("unable to load class '" + c + "'");
//                                continue;
//                            }
//                            
//                            classLoaders.add(classLoader);
//                            logger.info("adding mapping '" + path + "' for class='" + c + "'");
//                        }
//                        else
//                        {   logger.error("cannot consider declaration '" + path + " for class='" + c + "' --> no classloader found"); }
//                    }
//                    else
//                    {   if ( path == null )
//                        {   logger.error("invalid declaration of castor xml path (path not specified) --> ignored"); }
//                        if ( c == null )
//                        {   logger.error("invalid declaration of castor xml path for class '" + c + "' --> ignored"); }
//                    }
//                }
//            }
//            
//            /* create the mapping with set declarationPathSet */
//            ClassLoader typeCL = ResourceLoader.getInstance().getPluginClassLoader("siberia-types");
//            
//            mapping = new Mapping();//typeCL);
//            mapping.setAllowRedefinitions(true);
//            
//            PrintWriter writer = new PrintWriter(System.err);
//            mapping.setLogWriter(writer);
//                                
//            /* add all mappings information */
//            this.addMapping(mapping, rootElement);
//            
//            /* create the new reference */
//            this.mappingRef = new SoftReference(mapping);
//        }
//        
//        return mapping;
//    }
//    
//    public class MyClassLoader extends ClassLoader
//    {
//        /** sub classLoaders */
//        private Set<ClassLoader> subClassloaders = null;
//        
//        /** create a new MyClassLoader
//         *  @param subClassLoaders a set of ClassLoader
//         */
//        public MyClassLoader(Set<ClassLoader> subClassLoaders)
//        {   super();
//            
//            this.subClassloaders = subClassLoaders;
//        }
//
//        /**
//         * Loads the class with the specified <a href="#name">binary name</a>.  The
//         * default implementation of this method searches for classes in the
//         * following order:
//         *
//         * <p><ol>
//         *
//         *   <li><p> Invoke {@link #findLoadedClass(String)} to check if the class
//         *   has already been loaded.  </p></li>
//         *
//         *   <li><p> Invoke the {@link #loadClass(String) <tt>loadClass</tt>} method
//         *   on the parent class loader.  If the parent is <tt>null</tt> the class
//         *   loader built-in to the virtual machine is used, instead.  </p></li>
//         *
//         *   <li><p> Invoke the {@link #findClass(String)} method to find the
//         *   class.  </p></li>
//         *
//         * </ol>
//         *
//         * <p> If the class was found using the above steps, and the
//         * <tt>resolve</tt> flag is true, this method will then invoke the {@link
//         * #resolveClass(Class)} method on the resulting <tt>Class</tt> object.
//         *
//         * <p> Subclasses of <tt>ClassLoader</tt> are encouraged to override {@link
//         * #findClass(String)}, rather than this method.  </p>
//         *
//         * @param  name
//         *         The <a href="#name">binary name</a> of the class
//         *
//         * @param  resolve
//         *         If <tt>true</tt> then resolve the class
//         *
//         * @return  The resulting <tt>Class</tt> object
//         *
//         * @throws  ClassNotFoundException
//         *          If the class could not be found
//         */
//        protected synchronized Class<?> loadClass(String name, boolean resolve)
//            throws ClassNotFoundException
//        {   Class c = null;
//            
//            if ( this.subClassloaders != null )
//            {   Iterator<ClassLoader> it = this.subClassloaders.iterator();
//                
//                while(it.hasNext())
//                {   ClassLoader current = it.next();
//                    
//                    if ( current != null )
//                    {   
////                        c = current.loadClass()
//                    }
//                }
//            }
//            
//            return c;
//        }
//    }
//}
