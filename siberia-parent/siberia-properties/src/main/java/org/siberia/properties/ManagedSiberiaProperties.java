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

import org.siberia.type.AbstractSibType;
import org.siberia.xml.schema.properties.Properties;
import org.siberia.properties.SiberiaProperties;

/**
 *
 * Cold type linked to a properties context id
 *
 * @author alexis
 */
public class ManagedSiberiaProperties extends SiberiaProperties
{
    /** id of the properties context */
    private String     id         = null;
    
    /** properties */
    private Properties properties = null;
    
    /** Creates a new instance of ManagedSiberiaProperties
     *  @param id the id of the related properties context
     */
    public ManagedSiberiaProperties(String id)
    {   super();
        
        if ( id == null )
            throw new IllegalArgumentException("id must be non null");
        
        this.id = id;
    }
    
    /** refresh */
    public void refresh()
    {
	this.properties = null;
    }
    
    /** returns the xml node Properties related to this properties
     *  @return a Properties
     */
    public Properties getRelatedProperties()
    {   if ( this.properties == null )
        {   this.properties = PropertiesManager.getPropertiesById(this.getPropertiesId()); }
        
        return this.properties;
    }
    
    /** indicates to the object to ave its content */
    public void save()
    {   PropertiesManager.saveProperties( this.getPropertiesId(), this.getRelatedProperties()); }
    
    /** return the id related to this object
     *  @return the id related to this object
     */
    public String getPropertiesId()
    {   return this.id; }
    
}
