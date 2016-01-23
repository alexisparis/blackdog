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
package org.siberia.ui.swix;

import java.awt.Container;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.siberia.xml.schema.swix.SwixConfiguration;
import org.siberia.xml.schema.swix.ConverterType;
import org.siberia.xml.schema.swix.TagType;
import org.swixml.ConverterLibrary;
import org.swixml.SwingEngine;
import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;

/**
 *
 * Class that extends the functionnalities of the default SwingEngine by<br>
 * providing converter and tag specific to Siberia and current software.<br>
 * It uses SwixConfigurationProvider to get Swix configuration information.
 *
 * @author alexis
 */
public class ExtendedSwingEngine extends SwingEngine
{
    /** tell if converter and tag initialization is done */
    private boolean initialized = false;
    
    /** logger */
    private Logger  logger      = Logger.getLogger(this.getClass());
    
    /** Creates a new instance of ExtendedSwingEngine */
    public ExtendedSwingEngine()
    {   super(); }
    
    /** declared a new tag
     *  @param tag the string value of the tag
     *  @param extSwingClass a Class that extends a Swing component class
     */
    public void registerTag(String tag, Class extSwingClass)
    {   
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("entering registerTag(String, Class)");
	}
	
	logger.info("registering " + extSwingClass + " for tag : '" + tag + "'");
        this.getTaglib().registerTag(tag, extSwingClass);
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("exiting registerTag(String, Class)");
	}
    }
    
    /** unregister a tag
     *  @param tag the string value of the tag
     */
    public void unregisterTag(String tag)
    {   
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("entering unregisterTag(String)");
	}
	logger.info("unregistering tag : '" + tag + "'");
        this.getTaglib().unregisterTag(tag);
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("exiting unregisterTag(String)");
	}
    }
    
    /** declared a new converter
     *  @param objClass the class to convert
     *  @param converter an object implementing org.swixml.Converter
     */
    public void registerConverter(Class objClass, org.swixml.Converter converter)
    {   
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("entering registerConverter(Class, Converter)");
	}
	logger.info("registering converter " + converter + " for class : " + objClass);
        ConverterLibrary.getInstance().register(objClass, converter);
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("exiting registerConverter(Class, Converter)");
	}
    }
    
    /** add a list of Converter
     *  @param converters a list of xml converter
     */
    private void addConverters(List converters)
    {   
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("entering addConverters(List)");
	}
	if ( converters != null )
        {   Iterator it = converters.iterator();
            while(it.hasNext())
            {   Object current = it.next();
                if ( current != null && current instanceof ConverterType )
                {   ConverterType c = (ConverterType)current;
                    
                    Class                objectClass   = null;
                    org.swixml.Converter converterInst = null;
                    
                    try
                    {   objectClass = ResourceLoader.getInstance().getClass(c.getObjClass()); }
                    catch(Exception e)
                    {   logger.error("class " + c.getObjClass() + " could not be found", e);
                        continue;
                    }
                    
                    try
                    {   Class converterClass = ResourceLoader.getInstance().getClass(c.getConverterClass());
                        converterInst = (org.swixml.Converter)converterClass.newInstance();
                    }
                    catch(ResourceException e)
                    {   logger.error("class " + c.getConverterClass() + " could not be found", e);
                        continue;
                    }
                    catch(Exception e)
                    {   logger.error("unable to create a org.swixml.Converter from class " + c.getConverterClass(), e);
                        continue;
                    }
                    
                    this.registerConverter(objectClass, converterInst);
                }
            }
        }
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("exiting addConverters(List)");
	}
    }
    
    /** add a list of Tag
     *  @param tags a list of xml tag
     */
    private void addTags(List tags)
    {   
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("entering addTags(List)");
	}
	if ( tags != null )
        {   Iterator it = tags.iterator();
            while(it.hasNext())
            {   Object current = it.next();
                if ( current != null && current instanceof TagType )
                {   TagType c = (TagType)current;
                    
                    Class  swingClass = null;
                    String tag        = c.getTag();
                    
                    try
                    {   swingClass = ResourceLoader.getInstance().getClass(c.getSwingClass()); }
                    catch(Exception e)
                    {   logger.error("class " + c.getSwingClass() + " could not be found", e);
                        continue;
                    }
                    
                    if ( ! java.awt.Component.class.isAssignableFrom(swingClass) )
                    {   logger.warn("class " + swingClass + " should represent a Swing component but " +
                                            "is not extending java.awt.Component");
                    }
                    
                    this.registerTag(tag, swingClass);
                }
            }
        }
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("exiting addTags(List)");
	}
    }
    
    /** initialize converters and tags */
    private void initialize()
    {   
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("entering initialize()");
	}
        SwixConfigurationProvider provider= new SwixConfigurationProvider();
        Set<SwixConfiguration> set = provider.getSwixConfigurations();
        
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("initializing " + (set == null ? 0 : set.size()) + " swix configurations");
	}
        if ( set != null )
        {   Iterator<SwixConfiguration> it = set.iterator();
            while(it.hasNext())
	    {
		SwixConfiguration config = it.next();
		
		if ( this.logger.isDebugEnabled() )
		{
		    this.logger.debug("initializing swix configuration " + config);
		}
		
                this.initialize(config);
	    }
        }
        
        this.initialized = true;
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("exiting initialize()");
	}
    }
    
    /** initialize converters and tags
     *  @param config a SwixConfiguration
     */
    private void initialize(SwixConfiguration config)
    {   
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("entering initialize(SwixConfiguration)");
	}
	if ( config != null )
        {   if ( config.getConverterGroup() != null )
            {   this.addConverters(config.getConverterGroup().getConverter()); }
            if ( config.getTagGroup() != null )
            {   this.addTags(config.getTagGroup().getTag()); }
        }
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("exiting initialize(SwixConfiguration)");
	}
    }
    
    /**
     * Gets the parsing of the XML file started.
     *
     * @param xml_file <code>File</code> xml-file
     * @return <code>Object</code>- instanced swing object tree root
     */
    public Container render(File xml_file) throws Exception
    {   
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("entering render(File)");
	    this.logger.debug("calling render(File) with " + xml_file);
	}
	if ( ! this.initialized )
        {   /* configure all converters and additive tags */   
            this.initialize();
        }
        
        Container container = super.render(xml_file);
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("render(File) returns " + container);
	    this.logger.debug("exiting render(File)");
	}
	return container;
    }
    
}
