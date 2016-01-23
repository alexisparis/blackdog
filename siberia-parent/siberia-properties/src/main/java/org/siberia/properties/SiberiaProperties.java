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
package org.siberia.properties;

import java.beans.PropertyVetoException;
import org.siberia.type.AbstractSibType;
import org.siberia.xml.schema.properties.Properties;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.SiberiaPropertiesPlugin;

/**
 *
 * Type linked to a properties
 *
 * @author alexis
 */
@Bean(  name="properties container",
        internationalizationRef="org.siberia.rc.i18n.type.SiberiaProperties",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public abstract class SiberiaProperties extends AbstractSibType
{   
    /** Creates a new instance of ColdProperties */
    public SiberiaProperties()
    {   super(); }
    
    /** returns the xml node Properties related to this properties
     *  @return a Properties
     */
    public abstract Properties getRelatedProperties();
    
    /** return the id related to this object
     *  @return the id related to this object
     */
    public abstract String getPropertiesId();
    
    /** indicates to the object to save its content */
    public abstract void save();

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {   throw new UnsupportedOperationException("ColdXmlProperty does not support valueAsString"); }
    
}
