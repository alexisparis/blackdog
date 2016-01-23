/* 
 * Siberia image searcher : siberia plugin defining image searchers
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
package org.siberia.image.searcher;

/**
 *
 * Define the size of an image
 *
 * @author alexis
 */
public enum ImageSize
{
    WHATEVER("all"),
    ICON("icon"),
    MEDIUM("medium"),
    LARGE("large"),
    HUGE("huge");

    /** label */
    private String label = null;

    private ImageSize(String label)
    {
	this.label = label;
    }
    
    /** return an ImageSize that has the given label
     *	@param label a String
     *	@return an ImageSize
     */
    public static ImageSize getImageSizeWithLabel(String label)
    {
	ImageSize size = null;
	
	if ( label != null )
	{
	    ImageSize[] sizes = ImageSize.values();
	    if ( sizes != null )
	    {
		for(int i = 0; i < sizes.length; i++)
		{
		    ImageSize current = sizes[i];
		    
		    if ( current != null )
		    {
			if ( label.equals(current.label) )
			{
			    size = current;
			    break;
			}
		    }
		}
	    }
	}
	
	if ( size == null )
	{
	    size = ImageSize.MEDIUM;
	}
	
	return size;
    }
}
