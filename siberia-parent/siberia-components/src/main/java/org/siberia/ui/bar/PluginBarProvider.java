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

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import org.siberia.bar.provider.BarProvider;
import org.siberia.env.SiberExtension;
import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;
import org.siberia.env.PluginResources;


/**
 *
 * @author alexis
 */
public class PluginBarProvider implements BarProvider
{
    /** logger */
    private Logger logger = Logger.getLogger(PluginBarProvider.class);
    
    /** extension id related to bar */
    public static enum BarKind
    {
	TOOLBAR("toolbar"),
	MENUBAR("menubar"),
	POPUPMENU("contextmenu"),
	SYSTRAY("systray");
	
	private BarKind(String code)
	{
	    this.code = code;
	}
	String code = null;

	/** return the code of the kind of bar which represent the id of the extension point
	 *  @return a String
	 */
	public String getCode()
	{
	    return this.code;
	}
    }
    /* name of the parameter Code */
    static final String PARAMETER_CODE     = "code";
    /* name of the parameter Code */
    static final String PARAMETER_CLASS    = "class";
    /* name of the parameter filepath */
    static final String PARAMETER_FILEPATH = "filepath";
    /** bar id */
    private String  barId   = null;
    /** kind of bar */
    private BarKind barKind = null;

    /** create a new PluginBarProvider
     *	@param barId the id of the bar
     *	@param kind a BarKind
     */
    public PluginBarProvider(String barId, BarKind kind)
    {
	if (kind == null)
	{
	    throw new IllegalArgumentException("kind of bar could not be null");
	}
	
	this.barId = barId;
	this.barKind = kind;
    }

    /** return an iterator over input streams that represent bar definitions to consider during bar creation
     *	@return an Iterator over InputStream representing bar definitions
     */
    public Iterator<InputStream> getBarInputStreams()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering getBarInputStreams()");
	}
	
	Iterator<InputStream> streams = null;

	/** search in extensions points */
	Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(this.barKind.getCode());
	if (extensions != null)
	{
	    Set<InputStream> streamSet = new HashSet<InputStream>();

	    Iterator<SiberExtension> it = extensions.iterator();
	    while (it.hasNext())
	    {
		SiberExtension currentExtension = it.next();

		if (currentExtension != null)
		{   
		    if ( ! this.barKind.equals(BarKind.POPUPMENU) )
		    {
			/* create a descriptor */
			String code = currentExtension.getStringParameterValue(PARAMETER_CODE);

			if ( (code == null && this.barId == null) || code.equals(this.barId))
			{
			    String filePath = currentExtension.getStringParameterValue(PARAMETER_FILEPATH);

			    try
			    {
				InputStream stream = ResourceLoader.getInstance().getRcResource(filePath).openStream();

				if (stream != null)
				{
				    streamSet.add(stream);
				}
			    }
			    catch (Exception e)
			    {
				e.printStackTrace();
			    }
			}
		    }
		}
	    }
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("getBarInputStreams() consider a set of InputStream containing " + streamSet.size());
	    }

	    streams = streamSet.iterator();
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting getBarInputStreams()");
	}

	return streams;
    }
}
