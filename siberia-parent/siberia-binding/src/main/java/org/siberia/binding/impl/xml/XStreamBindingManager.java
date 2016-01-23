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
package org.siberia.binding.impl.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.SoftReference;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.binding.exception.LoadException;
import org.siberia.binding.exception.SaveException;
import org.siberia.binding.impl.xml.XmlBindingManager;
import org.siberia.env.PluginResources;
import org.siberia.env.SiberExtension;
import org.siberia.exception.ResourceException;
import org.siberia.type.SibType;
import com.thoughtworks.xstream.XStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 *
 * XmlBindingManager based on castor API
 *
 * @author alexis
 */
public class XStreamBindingManager extends XmlBindingManager
{
    /** extension point id of castor xml declarations */
    private static final String CASTOR_DECL_EXTENSION_ID = "xstream-declaration";
    
    /** logger */
    public static  Logger logger = Logger.getLogger(XStreamBindingManager.class);
    
    /** mapping reference */
    private SoftReference<XStream> streamRef = new SoftReference<XStream>(null);
    
    /** Creates a new instance of CastorBindingManager */
    public XStreamBindingManager()
    {   }
    
    /** methods to save the instance in a File
     *  @param type an Object
     *  @param file an existing File
     *
     *  @exception SaveException if errors occured
     */
    protected void store(Object type, File file) throws SaveException
    {
        if ( type != null && file != null )
        {   
	    FileWriter writer = null;
            try
            {   
		writer = new FileWriter(file);
		
		System.out.println("type is : " + type);
		if ( type instanceof SibType )
		{
		    System.out.println("type name is : " + ((SibType)type).getName());
		}
		
                XStream stream = this.getStream();
                String xml = stream.toXML(type);
		
		System.out.println("chaine xml : " + xml);
                
                writer.write(xml);
            }
            catch (Exception ex)
            {   throw new SaveException(ex); }
	    finally
	    {
		if ( writer != null )
		{
		    try
		    {	writer.close(); }
		    catch (IOException ex)
		    {	ex.printStackTrace(); }
		}
	    }
        }
        else
        {   if ( type == null )
            {   throw new SaveException(new NullPointerException()); }
            if ( file == null )
            {   throw new SaveException(new IOException()); }
        }
    }
    
    /** methods to save the instance in a File
     *  @param type an Object
     *  @param file an existing File
     *
     *  @exception LoadException if errors occured
     */
    protected Object load(File file) throws LoadException
    {   Object type = null;
        
	new Exception("load from xml").printStackTrace();
        try
        {   XStream stream = this.getStream();
            InputStream is = new FileInputStream(file);
            
            type = stream.fromXML(is);
	    
	    System.out.println("type is : " + (type == null ? null : type.getClass()));
        }
        catch(Exception e)
        {   throw new LoadException(e); }
        
        return type;
    }
    
    /** method that returns the mapping to use
     *  @return a Mapping
     */
    private XStream getStream()
    {   
	XStream stream = this.streamRef.get();
        
        if ( stream == null )
        {   stream = new XStream();
            this.streamRef = new SoftReference<XStream>(stream);
	    
//	    stream.registerConverter(new Converter()
//	    {
//		public boolean canConvert(Class type)
//		{
//		    boolean result = false;
//		    if ( type != null && org.siberia.type.SibType.class.isAssignableFrom(type) )
//		    {
//			result = true;
//		    }
//		    
//		    return result;
//		}
//		
//		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context)
//		{
//		    
//		}
//		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
//		{
//		    
//		}
//	    });
        }
        
        return stream;
    }
}
