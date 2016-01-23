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
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.Reader;
//import java.io.Writer;
//import java.lang.ref.SoftReference;
//import java.util.Iterator;
//import java.util.List;
//import org.siberia.kernel.Kernel;
//import org.siberia.type.AbstractSibType;
//import org.siberia.exception.ResourceException;
//import org.siberia.ResourceLoader;
//import org.siberia.env.TypeServicesProvider;
//import org.exolab.castor.mapping.Mapping;
//import org.exolab.castor.mapping.MappingException;
//import org.exolab.castor.xml.MarshalException;
//import org.exolab.castor.xml.Marshaller;
//import org.exolab.castor.xml.Unmarshaller;
//import org.exolab.castor.xml.ValidationException;
//import org.xml.sax.InputSource;
//
///**
// *
// * @author alexis
// */
//public class TypePersistenceManager implements TypeServicesProvider.StatusListener
//{
//    /** singleton */
//    private static final TypePersistenceManager instance = new TypePersistenceManager();
//    
//    /** indicates if type changed since last main mapping generation */
//    private boolean                mappingDegenerated = false;
//    
//    /** castor mapping that summarize all persistence mappings of all SibType */
//    private SoftReference<Mapping> mappingRef         = new SoftReference(null);
//    
//    private TypePersistenceManager()
//    {   Kernel.getInstance().getPluginContext().getTypeServicesProvider().
//                            addInvalidationListener(this); }
//    
//    /** return a TypePersistenceManager
//     *  @return an instanceof TypePersistenceManager
//     */
//    public static TypePersistenceManager getInstance()
//    {   return instance; }
//    
//    /** create a new Mapping according to the loaded SibType */
//    private void createMapping()
//    {   List<Class> c = Kernel.getInstance().getPluginContext().getTypeServicesProvider()
//                            .getSubClassFor(AbstractSibType.class, true);
//        
//        if ( c != null )
//        {   Mapping mapping = new Mapping();
//            InputSource source = null;
//            Iterator<Class> it = c.iterator();
//            Class current = null;
//            while(it.hasNext())
//            {   InputStream stream = null;
//                try
//                {   current = it.next();
//                    stream = this.getPersistenceDeclarationForClass(current);
//                    source = new InputSource(stream);
//                    System.out.println("file : " + stream);
//                    mapping.loadMapping(source);
//                    System.out.println("\tloading mapping for " + current.getName());
//                }
//                catch(ResourceException e)
//                {   /*e.printStackTrace(); /*USE LOGGER INSTEAD */ }
//                catch(IOException e)
//                {   e.printStackTrace(); }
//                catch(MappingException e)
//                {   e.printStackTrace(); }
//                finally
//                {   if ( stream != null )
//                    {   try
//                        {   stream.close(); }
//                        catch(IOException e)
//                        {   e.printStackTrace(); }
//                    }
//                }
//            }
//            
//            this.mappingRef = new SoftReference(mapping);
//            
//            this.mappingDegenerated = false;
//        }
//    }
//    
//    /** return an file representing the persistence description of the given ColdClass
//     *  @param cl a Class that inherits from SibType
//     *  @return the xml declaration of the class or null if the does not inherit from SibType
//     *
//     *  @exception throws ColdResourceException if resource could not be found
//     */
//    public InputStream getPersistenceDeclarationForClass(Class cl) throws ResourceException
//    {   InputStream stream = null;
//        try
//        {   stream = ResourceLoader.getInstance().getRcResource("persistence" + File.separator + cl.getSimpleName() + ".xml").openStream(); }
//        catch(IOException e)
//        {   throw new ResourceException("persistence declaration for " + cl + " could not be found"); }
//        return stream;
//    }
//    
//    /* #########################################################################
//     * ############################# MARSHALLING ###############################
//     * ######################################################################### */
//    
//    /** marshall an SibType item
//     *  @param item an SibType instance
//     *  @param stream an OutputStream
//     *
//     *  @exception <ul><li>FileNotFoundException</li>
//                       <li>MappingException</li>
//                       <li>IOException</li>
//                       <li>MarshalException</li>
//                       <li>ValidationException</li></ul>
//     */
//    public void marshall(AbstractSibType item, OutputStream stream) throws MappingException,
//                                                                     IOException,
//                                                                     MarshalException,
//                                                                     ValidationException
//    {   if ( stream != null )
//        {   this.marshall(item, new OutputStreamWriter(stream)); }
//    }
//    
//    /** marshall an SibType item
//     *  @param item an SibType instance
//     *  @param writer a writer
//     *
//     *  @exception <ul><li>FileNotFoundException</li>
//                       <li>MappingException</li>
//                       <li>IOException</li>
//                       <li>MarshalException</li>
//                       <li>ValidationException</li></ul>
//     */
//    public void marshall(AbstractSibType item, Writer writer) throws MappingException,
//                                                               IOException,
//                                                               MarshalException,
//                                                               ValidationException
//    {
//        if ( item != null && writer != null )
//        {   boolean generateMapping = this.mappingDegenerated;
//            if ( ! generateMapping )
//            {   if ( this.mappingRef == null )
//                    generateMapping = true;
//                else if ( this.mappingRef.get() == null )
//                    generateMapping = true;
//            }
//
//            if ( generateMapping )
//                this.createMapping();
//
//            Marshaller marshaller = new Marshaller(writer);
//            marshaller.setMapping(this.mappingRef.get());
//            marshaller.marshal(item);
//        }
//    }
//    
//    /** marshall an SibType item
//     *  @param item an SibType instance
//     *  @param file the file where to marshall item
//     *
//     *  @exception <ul><li>FileNotFoundException</li>
//                       <li>MappingException</li>
//                       <li>IOException</li>
//                       <li>MarshalException</li>
//                       <li>ValidationException</li></ul>
//     */
//    public void marshall(AbstractSibType item, File file) throws FileNotFoundException,
//                                                           MappingException,
//                                                           IOException,
//                                                           MarshalException,
//                                                           ValidationException
//    {
//        if ( file != null )
//        {   /* create the file if it does not exist */
//            if ( ! file.exists() )
//                file.createNewFile();
//            
//            this.marshall(item, new OutputStreamWriter(new FileOutputStream(file)));
//        }
//    }
//    
//    /* #########################################################################
//     * ############################ UNMARSHALLING ##############################
//     * ######################################################################### */
//    
//    /** unmarshall an SibType item
//     *  @param stream an InputStream
//     *  @return the unmarshalled type
//     *
//     *  @exception <ul><li>FileNotFoundException</li>
//                       <li>MappingException</li>
//                       <li>IOException</li>
//                       <li>MarshalException</li>
//                       <li>ValidationException</li></ul>
//     */
//    public AbstractSibType unmarshall(InputStream stream) throws MappingException,
//                                                           IOException,
//                                                           MarshalException,
//                                                           ValidationException
//    {   if ( stream != null )
//        {   return this.unmarshall(new InputStreamReader(stream)); }
//        return null;
//    }
//    
//    /** unmarshall an SibType item
//     *  @param reader a Reader
//     *  @return the unmarshalled type
//     *
//     *  @exception <ul><li>FileNotFoundException</li>
//                       <li>MappingException</li>
//                       <li>IOException</li>
//                       <li>MarshalException</li>
//                       <li>ValidationException</li></ul>
//     */
//    public AbstractSibType unmarshall(Reader reader) throws MappingException,
//                                                      IOException,
//                                                      MarshalException,
//                                                      ValidationException
//    {
//        if ( reader != null )
//        {   boolean generateMapping = this.mappingDegenerated;
//            if ( ! generateMapping )
//            {   if ( this.mappingRef == null )
//                    generateMapping = true;
//                else if ( this.mappingRef.get() == null )
//                    generateMapping = true;
//            }
//
//            if ( generateMapping )
//                this.createMapping();
//
//            Unmarshaller unmarshaller = new Unmarshaller(this.mappingRef.get());
//            Object o = unmarshaller.unmarshal(reader);
//            if ( o instanceof AbstractSibType )
//                return (AbstractSibType)o;
//        }
//        return null;
//    }
//    
//    /** unmarshall an SibType item
//     *  @param file the file where to marshall item
//     *  @return the unmarshalled type
//     *
//     *  @exception <ul><li>FileNotFoundException</li>
//                       <li>MappingException</li>
//                       <li>IOException</li>
//                       <li>MarshalException</li>
//                       <li>ValidationException</li></ul>
//     */
//    public AbstractSibType unmarshall(File file) throws FileNotFoundException,
//                                                           MappingException,
//                                                           IOException,
//                                                           MarshalException,
//                                                           ValidationException
//    {
//        if ( file != null )
//        {   /* create the file if it does not exist */
//            if ( file.exists() )
//            {   return this.unmarshall(new InputStreamReader(new FileInputStream(file))); }
//        }
//        return null;
//    }
//    
//    /* #########################################################################
//     * ############# TypeInfoProvider.StatusListener implementation ############
//     * ######################################################################### */
//    
//    /** indicate that the status of the provider has changed
//     *  @param propertyName the name of the property
//     */
//    public void statusChanged(String propertyName)
//    {   if( propertyName != null )
//        {   if ( propertyName.equals(TypeServicesProvider.INVALIDATION) )
//                this.mappingDegenerated = true;
//        }
//    }
//    
//}
