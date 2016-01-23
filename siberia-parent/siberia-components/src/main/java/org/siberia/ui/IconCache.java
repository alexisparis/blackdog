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
package org.siberia.ui;

import java.beans.BeanInfo;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;
import org.siberia.type.SibType;
import org.siberia.type.annotation.bean.BeanConstants;
import org.siberia.utilities.cache.GenericCache2;
import org.siberia.TypeInformationProvider;

/**
 *
 * @author alexis
 */
public class IconCache extends GenericCache2<IconCache.IconKey, Icon>
{
    /** singleton */
    private static IconCache singleton = new IconCache();
    
    /* logger */
    private        Logger    logger    = Logger.getLogger(IconCache.class);
    
    /** Creates a new instance of IconCache */
    private IconCache()
    {   }
    
    /** return the cache for icon
     *  @return an IconCache
     */
    public static IconCache getInstance()
    {
        return singleton;
    }
    
    /**
     * method that create an elemet according to the key
     * 
     * @param key the corresponding key
     * @param parameters others parameters
     * @return the object that has benn created
     */
    public Icon create(IconKey key, Object... parameters)
    {
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("entering create(IconKey, Object...)");
	    this.logger.debug("calling create with a IconKey : " + key);
	}
	
        Icon icon = null;

        if ( key == null )
	{
	    // provide traces !!??
	}
	else
        {
            Object rcPath = null;
	    
	    try
	    {	
		rcPath = TypeInformationProvider.getInformation(
			ResourceLoader.getInstance().getClass(key.getRelatedClassname()), key.getIconType() );
	    }
	    catch (ResourceException ex)
	    {	
		this.logger.error("got resource exception while trying to get icon from key : " + key, ex);
	    }
	    
            if ( rcPath instanceof String )
            {   
                try
                {
                    icon = ResourceLoader.getInstance().getIconNamed( (String)rcPath );
                }
                catch (ResourceException ex)
                {   
		    this.logger.error("got resource exception while trying to get icon named '" + rcPath + "'", ex);
		}
            }

        }
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("create returns " + icon);
	    this.logger.debug("exiting create(IconKey, Object...)");
	}

        return icon;
    }

    /**
     * return the object related to the given object
     *  a 16*16 COLOR Icon
     * 
     * @param o an Object
     * @return an instance of Icon
     */
    public Icon get(Object o)
    {
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("entering get(Object)");
	}
        Icon icon = this.get(o, BeanConstants.BEAN_PLUGIN_ICON_COLOR_16);
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("exiting get(Object)");
	}
	return icon;
    }

    /**
     * return the object related to the given object
     * 
     * @param o an Object
     * @param type an icon type
     * @return an instance of Icon
     */
    public Icon get(Object o, String type)
    {
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("entering get(Object, String)");
	}
        Icon icon = (o == null ? null : get(o.getClass(), type));
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("exiting get(Object, String)");
	}
	
	return icon;
    }

    /**
     * return the object related to the given object
     * 
     * @param c a Class
     * @param type an icon type
     * @return an instance of Icon
     */
    public Icon get(Class c, String type)
    {
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("entering get(Class, String)");
	}
        this.checkIconType(type);
        
        Icon retValue;
        
        retValue = this.get( new IconKey(c, type) );
	if ( this.logger.isDebugEnabled() )
	{
	    this.logger.debug("exiting get(Class, String)");
	}
        return retValue;
    }
    
    /** method that check the type of icon
     *  @param icon the kind of icon
     */
    private void checkIconType(String type)
    {
        boolean checked = true;
        
        if ( type == null )
        {
            checked = false;
        }
        else
        {
            if ( ! BeanConstants.BEAN_PLUGIN_ICON_COLOR_16.equals(type) &&
                 ! BeanConstants.BEAN_PLUGIN_ICON_COLOR_32.equals(type) &&
                 ! BeanConstants.BEAN_PLUGIN_ICON_MONO_16.equals(type) &&
                 ! BeanConstants.BEAN_PLUGIN_ICON_MONO_32.equals(type) )
            {
                checked = false;
            }
        }
        
        if ( ! checked )
        {
            throw new IllegalArgumentException("illegal icon type '" + type + "'");
        }
        
    }
    
    /** icon information */
    public static class IconKey implements Serializable
    {
        /** class */
        private String    classname = null;
        
        /** icon type */
        private String    iconType  = null;
        
        /** create a new IconKey
         *  @param c a Class
         *  @param type the icon type
         */
        public IconKey(Class c, String type)
        {
	    String pluginId = null;
	    
	    /** optimization : if SibType, no need to search in the ClassLoaders of the applciation */
	    if ( SibType.class.isAssignableFrom(c) )
	    {
		pluginId = TypeInformationProvider.getPluginDeclaring(c);
	    }
	    
	    /* not found if SibType, ask ResourceLoader */
	    if ( pluginId == null )
	    {
		pluginId = ResourceLoader.getInstance().getPluginIdWhichDeclare(c);
	    }
	    
            this.classname = (pluginId == null ? "" : pluginId + "::") + c.getName();
            this.iconType = type;
        }
        
        /** return the name of a Class
         *  @return the name of a Class
         */
        public String getRelatedClassname()
        {
            return this.classname;
        }
        
        /** return the kind of icon to provide
         *  @param return a String
         */
        public String getIconType()
        {
            return this.iconType;
        }

        public int hashCode()
        {
            return (this.getIconType() == null ? 0 : this.getIconType().hashCode());
        }

        public boolean equals(Object obj)
        {
            boolean result = true;
            
            if ( obj instanceof IconKey )
            {
                IconKey other = (IconKey)obj;
                
                if ( this.getRelatedClassname() == null )
                {
                    if ( other.getRelatedClassname() != null )
                    {
                        result = false;
                    }
                }
                else
                {
                    result = this.getRelatedClassname().equals(other.getRelatedClassname());
                }
                
                if ( result )
                {
                    if ( this.getIconType() == null )
                    {
                        if ( other.getIconType() != null )
                        {
                            result = false;
                        }
                    }
                    else
                    {
                        result = this.getIconType().equals(other.getIconType());
                    }
                }
            }
            else
            {
                result = false;
            }
            
            return result;
        }

	@Override
	public String toString()
	{
	    return "IconKey type=" + this.getIconType() + ", class=" + this.getRelatedClassname();
	}

    }
}
