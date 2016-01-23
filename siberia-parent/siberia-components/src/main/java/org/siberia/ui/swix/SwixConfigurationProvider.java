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

import java.net.URL;
import org.apache.log4j.Logger;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.siberia.env.SiberExtension;
import org.siberia.ResourceLoader;
import org.siberia.env.PluginResources;
import org.siberia.xml.JAXBLoader;
import org.siberia.xml.schema.swix.SwixConfiguration;

/**
 *
 * Entity that provide SwixConfiguration according to boot declaration :<br>
 * It search in platform and software structure if a configuration file for Swix exists. <br>
 * If so, the SwixConfigurationProvider return ths configuration information contained by this file.
 *
 * @author alexis
 */
public class SwixConfigurationProvider
{   
    /** logger */
    private             Logger logger                            = Logger.getLogger(SwixConfigurationProvider.class);
    
    /** graphical components id in Plugin declaration */
    public static final String COMPONENT_DECLARATION_PLUGIN_ID   = "ComponentRegistration";
    
    /** Creates a new instance of ConfigurationProvider */
    public SwixConfigurationProvider()
    {   }
    
    /** return a Set containing SwixConfiguration defined in plugins declaration
     *  @return a Set containing SwixConfiguration
     */
    public Set<SwixConfiguration> getSwixConfigurations()
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering getSwixConfigurations()");
	}
	
        Set<SwixConfiguration> result = null;
        
        Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(COMPONENT_DECLARATION_PLUGIN_ID);
		
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("found " + (extensions == null ? 0 : extensions.size()) + " extensions to '" + COMPONENT_DECLARATION_PLUGIN_ID + "'");
	}
	
        if ( extensions != null )
        {
            Iterator<SiberExtension> it = extensions.iterator();
            while(it.hasNext())
            {   SiberExtension currentExtension = it.next();
                
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("processing extension " + currentExtension);
		}
                if ( currentExtension != null )
                {   /* create a descriptor */
                    String filePath = currentExtension.getStringParameterValue("filepath");
                    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("processing extension filepath" + filePath);
		    }
                    InputStream stream = null;
                    
                    try
                    {   URL u = ResourceLoader.getInstance().getRcResource(filePath);
                        stream = u.openStream();
                        
                        SwixConfiguration config = new JAXBLoader().loadSwix(stream);
                        
                        if ( result == null )
                            result = new HashSet<SwixConfiguration>(extensions.size());
                        
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("adding Swix configuration from " + u);
			}
			
                        result.add(config);
                    }
                    catch(Exception e)
                    {   
			logger.debug("getting exception whil processing swix configuration", e);
                    }
                }
            }
        }
        
        if ( result == null )
	{
            result = Collections.EMPTY_SET;
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting getSwixConfigurations()");
	}
        
        return result;
    }
    
}
