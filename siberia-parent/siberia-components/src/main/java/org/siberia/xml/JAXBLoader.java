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
package org.siberia.xml;

import java.io.InputStream;

import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;

import org.siberia.xml.schema.swix.SwixConfiguration;


/**
 *
 * Class that allow marshalling and unmarshalling file related to existing siberia xsd files
 *
 * @author alexis
 */
public class JAXBLoader extends org.siberia.utilities.xml.JAXBLoader
{   
    /** logger */
    private Logger logger = Logger.getLogger(JAXBLoader.class);
    
    /** create a new JAXBBarLoader */
    public JAXBLoader()
    {   super(JAXBLoader.class.getClassLoader()); }   
    
    /* #########################################################################
     * ######################### Swix unmarshaller #############################
     * ######################################################################### */

    public SwixConfiguration loadSwix(InputStream stream) throws JAXBException
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering loadSwix(InputStream)");
	}
	SwixConfiguration config = null;
	
	Object result = unmarshal(SwixConfiguration.class, stream);
        
        if ( result instanceof SwixConfiguration )
	{   config = (SwixConfiguration)result; }
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting loadSwix(InputStream)");
	}
	
        return config;
    }
}
