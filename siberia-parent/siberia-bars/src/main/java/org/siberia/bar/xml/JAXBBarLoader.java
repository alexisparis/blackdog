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
package org.siberia.bar.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
import org.siberia.xml.schema.bar.Toolbar;
import org.siberia.xml.schema.bar.Menubar;
import org.siberia.xml.schema.bar.TypeMenu;


/**
 *
 * Class that allow marshalling and unmarshalling file related to existing siberia xsd files
 *
 * @author alexis
 */
public class JAXBBarLoader
{   
    /** logger */
    private Logger      logger      = Logger.getLogger(JAXBBarLoader.class);
    
    /** ClassLoader to use */
    private ClassLoader classLoader = null;
    
    /** create a new JAXBBarLoader */
    public JAXBBarLoader()
    {   this(JAXBBarLoader.class.getClassLoader()); }
    
    /** create a new JAXBBarLoader */
    public JAXBBarLoader(ClassLoader classLoader)
    {   
	this.classLoader = classLoader;
    }
    
    /* #########################################################################
     * ######################### Menu unmarshaller #############################
     * ######################################################################### */

    public Menubar loadMenubar(InputStream stream) throws JAXBException
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering loadMenuBar(InputStream)");
	}
	Object result = this.unmarshal(Menubar.class, stream);
        Menubar bar = null;
        if ( result instanceof Menubar )
            bar = (Menubar)result;
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting loadMenuBar(InputStream)");
	}
        return bar;
    }

    public Toolbar loadToolbar(InputStream stream) throws JAXBException
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering loadToolBar(InputStream)");
	}
	Object result = this.unmarshal(Toolbar.class, stream);
        Toolbar bar = null;
        if ( result instanceof Toolbar )
            bar = (Toolbar)result;
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting loadToolBar(InputStream)");
	}
        return bar;
    }

    public TypeMenu loadTypeMenu(InputStream stream) throws JAXBException
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering loadTypeMenu(InputStream)");
	}
	Object result = this.unmarshal(TypeMenu.class, stream);
        TypeMenu menu = null;
        if ( result instanceof TypeMenu )
            menu = (TypeMenu)result;
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting loadTypeMenu(InputStream)");
	}
        return menu;
    }

    public void saveMenubar(Menubar bar, File file) throws JAXBException, IOException
    {   this.marshal(Menubar.class, bar, file); }

    public void saveToolbar(Toolbar tool, File file) throws JAXBException, IOException
    {   this.marshal(Toolbar.class, tool, file); }

    public void saveTypeMenu(TypeMenu menu, File file) throws JAXBException, IOException
    {   this.marshal(TypeMenu.class, menu, file); }
    
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

}
