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
package org.siberia.image.searcher.event;

import java.net.URL;
import java.util.EventObject;
import org.siberia.image.searcher.ImageSearcher;

/**
 * 
 * Event that indicate that an image has been found for an ImageSearcher
 * 
 * @author Alexis
 */
public class ImageFoundEvent extends ImageSearcherEvent
{
    /** url */
    private URL   url  = null;
    
    /** index of the image in the searcher */
    private int   index = -1;
    
    /**
     * Creates a new instance of ImageFoundEvent
     * 
     * @param source an ImageSearcher
     */
    public ImageFoundEvent(ImageSearcher searcher, URL url, int index)
    {
        super(searcher);
        
        this.url = url;
        this.index = index;
    }
    
    /** return the url of the image
     *  @return the url of the image
     */
    public URL getURL()
    {
        return this.url;
    }
    
    /** return the index of the image
     *  @return an integer
     */
    public int getImageIndex()
    {
        return this.index;
    }
}
