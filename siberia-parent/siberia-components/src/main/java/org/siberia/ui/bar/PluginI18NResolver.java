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
package org.siberia.ui.bar;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.bar.i18n.I18NResources;
import org.siberia.bar.i18n.I18nResolver;

/**
 *
 * implementation of I18NResolver related to the manner of which 
 *  ressources can be accessed in a Siberia application
 *
 * @author alexis
 */
public class PluginI18NResolver implements I18nResolver
{
    /** logger */
    private Logger logger = Logger.getLogger(PluginI18NResolver.class);
    
    /** Creates a new instance of PluginI18NResolver */
    public PluginI18NResolver()
    {	}
    
    public ResourceBundle getResource(I18NResources i18nResources)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering getResource(I18NResources)");
	    logger.debug("calling getResource(I18NResources) with " + i18nResources);
	}
	
	ResourceBundle rb = null;

	if ( i18nResources != null )
	{
	    try
	    {
		rb = ResourceLoader.getInstance().getResourceBundle(i18nResources.getCode());
	    }
	    catch(MissingResourceException e)
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("unable to access to resource '" + i18nResources.getCode() + "'", e);
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getResource(I18NResources) returns " + rb);
	    logger.debug("exiting getResource(I18NResources)");
	}

	return rb;
    }
    
}
