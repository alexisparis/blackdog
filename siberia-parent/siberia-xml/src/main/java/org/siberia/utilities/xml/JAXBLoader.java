/* 
 * Siberia xml : siberia plugin to provide utilities for xml format
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
package org.siberia.utilities.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.siberia.utilities.xml.exception.SchemaNotFoundException;
import org.siberia.utilities.xml.exception.SchemaValidationNotSupportedException;
import org.xml.sax.SAXException;


/**
 *
 * Class that allow marshalling and unmarshalling file related to existing siberia xsd files
 *
 * @author alexis
 */
public class JAXBLoader
{       
    /** ClassLoader to use */
    private ClassLoader classLoader = null;
    
    /** create a new JAXBLoader
     *  @param classLoader a ClassLoader
     */
    public JAXBLoader(ClassLoader classLoader)
    {   if ( classLoader == null )
            throw new IllegalArgumentException("invalid ClassLoader");
        this.classLoader = classLoader;
    }
    
    /** method that close an InputStream 
     *  @param stream an InputStream
     */
    protected void closeStream(InputStream stream)
    {   if ( stream != null )
        {   try
            {   stream.close(); }
            catch(IOException e)
            {   e.printStackTrace(); }
        }
    }
    
    /** method that return the url of the xsd file that declare the given class
     *  this method is used to be able to check the validity of an xml file
     *  @param jaxbClass a class
     *  @return the url of an xsd file or null
     */
    protected URL getXsdUrlForClass(Class jaxbClass)
    {
        return null;
    }
    
    /** method that allow to check if the given stream is valid according to xsd structure
     *  @param jaxbClass the jaxb class to marshall/unmarshall
     *  @param stream an InputStream
     *  @return true if the given stream is valid according to xsd structure
     */
    public boolean checkValidity(Class jaxbClass, InputStream stream) throws SchemaValidationNotSupportedException
    {
        return this.checkValidity(jaxbClass, stream, this.classLoader);
    }
    
    /** method that allow to check if the given stream is valid according to xsd structure
     *  @param jaxbClass the jaxb class to marshall/unmarshall
     *  @param stream an InputStream
     *  @param classLoader a ClassLoader
     *  @return true if the given stream is valid according to xsd structure
     */
    public boolean checkValidity(Class jaxbClass, InputStream stream, ClassLoader classLoader) throws SchemaValidationNotSupportedException
    {
        if ( jaxbClass == null )
        {
            throw new IllegalArgumentException("jaxbClass must be non null");
        }
        if ( stream == null )
        {
            throw new IllegalArgumentException("stream must be non null");
        }
        
        JAXBContext jc = createContext(jaxbClass, classLoader);
        
        boolean result = true;
        
        try
        {   
            //Unmarshaller u = jc.createUnmarshaller();
            
            Schema schema = null;
            
            SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            
            URL url = this.getXsdUrlForClass(jaxbClass);
            
            if ( url == null )
            {
                throw new SchemaNotFoundException("schema '" + url + "' could not be found");
            }
            else
            {
                try
                {
                    schema = sf.newSchema(url);
                }
                catch (SAXException ex)
                {
                    ex.printStackTrace();
                    throw new SchemaNotFoundException("schema '" + url + "' could not be found");
                }

                Validator validator = schema.newValidator();
                
                System.out.println("validator is : " + validator.getClass());
                validator.validate(new javax.xml.transform.stream.StreamSource(stream));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            result = false;
        }
        
        return result;
    }
    
    /* #########################################################################
     * ############################# Generic ###################################
     * ######################################################################### */
    
    /** unmarshal
     *  @param jaxbClass the class to unmarshal
     *  @param stream an InputStream
     *  @return an object
     *
     *  @throw JAXBException
     */
    public Object unmarshal(Class jaxbClass, InputStream stream) throws JAXBException
    {   return unmarshal(jaxbClass, stream, this.classLoader); }

    /** unmarshal
     *  @param jaxbClass the class to unmarshal
     *  @param stream an InputStream, the stream is always closed when finished or failed
     *  @param classLoader a ClassLoader
     *  @return an object
     *
     *  @throw JAXBException
     */
    private Object unmarshal(Class jaxbClass, InputStream stream, ClassLoader classLoader) throws JAXBException
    {   
        JAXBContext jc = createContext(jaxbClass, classLoader);
        
        Unmarshaller u = jc.createUnmarshaller();
        
        Object o = null;
        
        try
        {   o = u.unmarshal(stream); }
        catch(JAXBException e)
        {   throw e; }
        finally
        {   closeStream(stream); }
        
        return o;
    }

    /** marshal
     *  @param jaxbClass the class to unmarshal
     *  @param o the object to marshal
     *  @param file a File where to marshal
     *
     *  @throw JAXBException, IOException
     */
    public void marshal(Class jaxbClass, Object o, File file) throws JAXBException, IOException
    {   marshal(jaxbClass, o, file, this.classLoader); }

    /** marshal
     *  @param jaxbClass the class to unmarshal
     *  @param o the object to marshal
     *  @param file a File where to marshal
     *  @param classLoader a ClassLoader
     *
     *  @throw JAXBException, IOException
     */
    private void marshal(Class jaxbClass, Object o, File file, ClassLoader classLoader) throws JAXBException, IOException
    {   if ( file != null )
        {   if ( ! file.getParentFile().exists() )
            file.getParentFile().mkdirs();
            
            if ( ! file.exists() )
                file.createNewFile();
        }
        JAXBContext jc = createContext( jaxbClass, classLoader );
        Marshaller u = jc.createMarshaller();
        u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        u.setProperty(Marshaller.JAXB_ENCODING        , "ISO-8859-1");
        
        u.marshal(o, new FileWriter(file));
    }
    
    /* create a given context
     *  @param jaxbClass the class to create the context
     *  @return an instance of JAXBContext
     */
    protected JAXBContext createContext(Class jaxbClass)
    {   return createContext(jaxbClass, this.classLoader); }
    
    /* create a given context
     *  @param jaxbClass the class to create the context
     *  @param loader a ClassLoader
     *  @return an instance of JAXBContext
     */
    protected JAXBContext createContext(Class jaxbClass, ClassLoader loader)
    {   JAXBContext context = null;
        try
        {   
            //new com.sun.xml.internal.bind.v2.ContextFactory()
            context = JAXBContext.newInstance(jaxbClass.getPackage().getName(), loader);
        }
        catch(JAXBException e)
        {   e.printStackTrace(); }
        
        return context;
    }

}
