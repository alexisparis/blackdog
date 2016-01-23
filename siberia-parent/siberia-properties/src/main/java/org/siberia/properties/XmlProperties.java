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

import org.apache.log4j.Logger;
import org.siberia.type.AbstractSibType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JToggleButton;
import org.siberia.xml.schema.properties.CategoryType;
import org.siberia.xml.schema.properties.Properties;

/**
 *
 * Object that acts as a container for Properties
 *
 * @author alexis
 */
public class XmlProperties extends AbstractSibType
{
    /** logger */
    private Logger                  logger            = Logger.getLogger(XmlProperties.class);
    
    /** ordered list of root categories */
    private List<XmlCategory>       categories        = null;
    
    /** inner properties */
    private Properties              innerProperties   = null;
    
    /** Collection of properties that depends on other properties */
    private Collection<XmlProperty> dependsProperties = null;
    
    /** Creates a new instance of ColdXmlProperties
     *  @param properties xml properties
     */
    public XmlProperties(Properties properties)
    {   
        super();
        
        this.innerProperties = properties;
        
        this.createCategories();
    }
    
    /** return an ordered iterator over direct contained categories
     *  @return an Iterator
     */
    public Iterator<XmlCategory> categories()
    {   Collection<XmlCategory> collec = this.categories;
        
        if ( collec == null )
            collec = Collections.EMPTY_LIST;
        
        return collec.iterator();
    }
    
    /** create all sub categories for the given xml properties */
    private void createCategories()
    {   
        /** set of ColdXmlProperty */
        Map<Number, XmlProperty> allProperties     = new HashMap<Number, XmlProperty>();
        
        /** collection of Property needed by properties in dependsProperties list */
        Collection<XmlProperty>   masterProperties  = null;

        JToggleButton firstButton = null;

        if ( this.innerProperties != null )
        {   
            /** add the rootCategories */
            if( this.innerProperties.getCategory() != null )
            {   
                Iterator it = this.innerProperties.getCategory().iterator();
                while(it.hasNext())
                {   Object current = it.next();
                    if ( current instanceof CategoryType )
                    {   XmlCategory category = new XmlCategory( (CategoryType)current );
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("registering properties for category : " + category.getLabel());
			}
                        
                        if ( this.categories == null )
                            this.categories = new ArrayList<XmlCategory>();
                        this.categories.add(category);

                        Iterator<XmlProperty> recursiveProperties = category.recursiveProperties().iterator();
                        if ( recursiveProperties != null )
                        {   while(recursiveProperties.hasNext())
                            {   XmlProperty currentProperty = recursiveProperties.next();
                                if ( currentProperty != null )
                                {   if ( allProperties == null )
                                        allProperties = new HashMap<Number, XmlProperty>();
                                    allProperties.put(currentProperty.getId(), currentProperty);
				    
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("registering property '" + currentProperty.getRepr() + 
						     "' for id=" + currentProperty.getId());
				    }

                                    if ( currentProperty.isDependingOnOthersProperties() )
                                    {   if ( this.dependsProperties == null )
                                            this.dependsProperties = new ArrayList<XmlProperty>();
                                        this.dependsProperties.add(currentProperty);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("all properties size : " + (allProperties == null ? 0 : allProperties.size()));
	    
	    if ( allProperties != null )
	    {
		logger.debug("displaying all properties map : ");

		Iterator<Map.Entry<Number, XmlProperty>> entries = allProperties.entrySet().iterator();

		while(entries.hasNext())
		{
		    Map.Entry<Number, XmlProperty> currentEntry = entries.next();
		    
		    logger.debug("\t" + currentEntry.getKey() + " --> " + (currentEntry.getValue() == null ? null : currentEntry.getValue().getRepr()));
		}
	    }
	}

        /** add depending properties */
        if ( this.dependsProperties != null )
        {   Iterator<XmlProperty> it = this.dependsProperties.iterator();
            while(it.hasNext())
            {   XmlProperty current = it.next();
                if ( current != null )
                {   List<Long> depIds = current.getMasterPropertiesId();
                    if ( depIds != null )
                    {   Iterator<Long> ids = depIds.iterator();
                        while(ids.hasNext())
                        {   
			    Long id = ids.next();
			    
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("searching for property with id=" + id);
			    }
			    
                            XmlProperty master = allProperties.get(id);
                            
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("trying to add " + (master == null ? null : "'" + master.getRepr() + "'(" + id + ")") + " as depending property of '" +
					     current.getRepr() + "'(" + current.getId() + ") --> id is : " + id);
			    }
			    if ( master == null )
			    {
				logger.warn("trying to add null as depending property of '" + current.getRepr() + "'(" + current.getId() + ") --> id is : " + id);
			    }
			    
                            if ( masterProperties == null )
			    {
                                masterProperties = new HashSet<XmlProperty>();
			    }
                            
                            masterProperties.add(master);

                            current.addDependingProperty(master);
                        }
                    }
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("all properties size after dependencies assignation : " + (allProperties == null ? 0 : allProperties.size()));
	}
    }
    
    /** update the state of all items contains by this properties */
    public void updateStates()
    {   if ( this.dependsProperties != null )
        {   Iterator<XmlProperty> it = this.dependsProperties.iterator();
            while(it.hasNext())
            {   it.next().updateState(); }
        }
    }
    
    /** method which allow to restore the old value for all properties */
    public void restoreOldValues()
    {   Iterator<XmlCategory> it = this.categories();
        
        while(it.hasNext())
        {   XmlCategory current = it.next();
            
            if ( current != null )
            {   current.restoreOldValues(); }
        }
    }
    
    /** method which indicate that all values are confirmed */
    public void confirmValues()
    {   Iterator<XmlCategory> it = this.categories();
        
        while(it.hasNext())
        {   XmlCategory current = it.next();
            
            if ( current != null )
            {   current.confirmValues(); }
        }
    }
    
}
