/* 
 * TransSiberia : siberia plugin allowing to update siberia pplications and download new plugins
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
package org.siberia.trans.type.plugin;

import java.beans.PropertyVetoException;
import java.net.URL;
import org.siberia.trans.exception.InvalidPluginDeclaration;
import org.siberia.utilities.security.check.CheckSum;

/**
 *
 * @author alexis
 */
public class VirtualPluginBuild extends PluginBuild
{
    /* url of the build */
    private URL url = null;
    
    /** Creates a new instance of VirtualPluginBuild
     *	@param url the complete url of the build
     */
    public VirtualPluginBuild(URL url)
    {
	super();
	
	this.url = url;
	
	/** no check for virtual build */
	this.setCheckType(CheckSum.NONE);
	this.setVersion(Version.UNKNOWN_VERSION);
	
	/* set name according to url */
	if ( this.getURL() != null )
	{
	    String file = this.getURL().getFile();
	    
	    if ( file != null )
	    {
		int lastSlashIndex = file.lastIndexOf('/');
		
		if ( lastSlashIndex > -1 )
		{
		    file = file.substring(lastSlashIndex + 1);
		}
		
		try
		{   
		    this.setName(file);
		}
		catch(PropertyVetoException e)
		{
		    e.printStackTrace();
		}
	    }
	}
    }
    
    /** return the url where the build can be downloaded
     *	@return an url
     */
    public URL getURL()
    {
	return this.url;
    }

    @Override
    public String toString()
    {
	return "virtual build from " + this.url;
    }
    
    /** return the local filename to use when downloading a build
     *	@return a String ('siberia-types-0.0.1.zip')
     */
    @Override
    public String getLocalArchiveSimplename() throws InvalidPluginDeclaration
    {
	String result = null;
	
	if ( this.getURL() != null )
	{
	    result = this.getURL().getFile();
	    
	    int lastSlashIndex = result.lastIndexOf('/');
	    
	    if ( lastSlashIndex > -1 )
	    {
		result = result.substring(lastSlashIndex + 1);
	    }
	}
	
	return result;
    }

    @Override
    public boolean equals(Object t)
    {
	boolean result = false;
	
	if ( t instanceof VirtualPluginBuild )
	{
	    URL other = ((VirtualPluginBuild)t).getURL();
	    
	    if ( this.getURL() == null )
	    {
		if ( other == null )
		{
		    result = true;
		}
	    }
	    else
	    {
		result = this.getURL().equals(other);
	    }
	}
	
	return result;
    }

    public int hashCode()
    {
	return (this.getURL() == null ? 0 : this.getURL().hashCode());
    }
    
}
