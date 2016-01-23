/* 
 * Siberia properties : siberia plugin defining system properties
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBException;
import org.siberia.xml.schema.properties.Properties;


/**
 *
 * Class that allow marshalling and unmarshalling for properties
 *
 * @author alexis
 */
public class JAXBPropertiesLoader extends org.siberia.utilities.xml.JAXBLoader
{       
    /** create a new JAXBPropertiesLoader */
    public JAXBPropertiesLoader()
    {   super(JAXBPropertiesLoader.class.getClassLoader()); }
    
    /* #########################################################################
     * ####################### Properties unmarshaller #########################
     * ######################################################################### */

    /** load a Properties from an InputStream
     *  @param stream an InputStream
     *  @return a Properties
     *
     *  @throw JAXBException
     */
    public Properties loadProperties(InputStream stream) throws JAXBException
    {   Object result = unmarshal(Properties.class, stream);
        
        if ( result instanceof Properties )
            return (Properties)result;
        return null;
    }

    /** save a Properties in a file
     *  @param properties a Properties
     *  @param file a File
     *
     *  @throw JAXBException, IOException
     */
    public void saveProperties(Properties properties, File file) throws JAXBException, IOException
    {   marshal(Properties.class, properties, file); }

}
