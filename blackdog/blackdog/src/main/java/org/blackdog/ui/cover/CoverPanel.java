/*
 * blackdog : audio player / manager
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.ui.cover;

import java.awt.Image;
import java.net.URL;
import org.jdesktop.swingx.JXImagePanel;

/**
 *
 * panel that is able to display an image
 *
 * @author alexis
 */
public class CoverPanel extends JXImagePanel
{
    /** property url */
    public static final String PROPERTY_URL = "url";
    
    /** url */
    private URL url = null;
    
    /** Creates a new instance of CoverPanel */
    public CoverPanel()
    {	}

    public void setImage(URL url, Image image)
    {
	super.setImage(image);
	
	StringBuffer buffer = new StringBuffer();
	
	if ( url != null )
	{
	    if ( buffer.length() == 0 )
	    {
		buffer.append("<html><center>");
	    }
	    buffer.append(url.toString());
	}
	if ( image != null )
	{
	    if ( buffer.length() == 0 )
	    {
		buffer.append("<html><center>");
	    }
	    else
	    {
		buffer.append("<br>");
	    }
	    buffer.append(image.getWidth(null) + "x" + image.getHeight(null));
	}
	
	if ( buffer.length() > 0 )
	{ 
	    buffer.append("</center></html>");
	}
		
	this.setToolTipText(buffer.toString());
	
	URL old = this.getURL();
	
	boolean sameURL = false;
	
	if ( url == null )
	{
	    if ( old == null )
	    {
		sameURL = true;
	    }
	}
	else
	{
	    if ( old != null )
	    {
		sameURL = url.equals(old);
	    }
	}
	
	this.url = url;
	
	if ( ! sameURL )
	{
	    this.firePropertyChange(PROPERTY_URL, old, this.getURL());
	}
    }
    
    /** return the url linked to this panel
     *	@return an URL
     */
    public URL getURL()
    {
	return this.url;
    }
    
}
