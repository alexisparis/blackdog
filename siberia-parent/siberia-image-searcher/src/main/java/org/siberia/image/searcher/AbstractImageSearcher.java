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

import java.util.Locale;
import javax.swing.event.EventListenerList;
import org.siberia.image.searcher.event.ImageFoundEvent;
import org.siberia.image.searcher.event.ImageSearcherEvent;
import org.siberia.image.searcher.event.ImageSearcherListener;

/**
 *
 * Abstract implementation of an ImageSearcher
 *
 * @author alexis
 */
public abstract class AbstractImageSearcher implements ImageSearcher
{   
    /** number of maximum link retrieved */
    private int               maxLinkRetrieved = 10;
    
    /** type d'images recherches */
    private ImageSize         imageSize        = ImageSize.MEDIUM;
    
    /** locale (null to use the default locale) */
    private Locale            locale           = null;
    
    /** event listener list */
    private EventListenerList listeners        = new EventListenerList();
    
    /** tableau de criteres de recherche */
    private String[]          criterions       = null;
    
    /** Creates a new instance of AbstractImageSearcher */
    public AbstractImageSearcher()
    {	}
    
    /** set the criterions to use for the search
     *	@param criterions an array of String
     */
    public void setCriterions(String[] criterions)
    {
	this.criterions = criterions;
    }
    
    /** return the criterions to use for the search
     *	@return an array of String
     */
    public String[] getCriterions()
    {
	return this.criterions;
    }
    
    /** return the maximum number of image links retrieved
     *  @return an integer
     */
    public int getMaximumLinksRetrieved()
    {   return maxLinkRetrieved; }
    
    /** set the maximum number of image links retrieved
     *  @param maxLinkRetrieved un entier
     */
    public void setMaximumLinksRetrieved(int maxLinkRetrieved)
    {
        int _maxLinkRetrieved = maxLinkRetrieved;
        
        if ( _maxLinkRetrieved <= 0 || _maxLinkRetrieved > 20 )
        {
            _maxLinkRetrieved = 20;
        }
        
        this.maxLinkRetrieved = _maxLinkRetrieved;
    }
    
    /** return the image size wanted for this search
     *  @return a ImageSize
     */
    public ImageSize getImageSize()
    {   return imageSize; }
    
    /** initialize the image size wanted for this search
     *  @param imageSize a ImageSize
     *
     *  @exception IllegalArgumentException if imageSize is null
     */
    public void setImageSize(ImageSize imageSize)
    {
        if ( imageSize == null )
        {
            throw new IllegalArgumentException("imageSize could not be null");
        }
        this.imageSize = imageSize;
    }
    
    /** return the locale used for the search
     *  @return a Locale or null if the research must consider the default locale
     */
    public Locale getLocale()
    {   return locale; }
    
    /** initialize the locale used for the search
     *  @param locale a Locale or null if the research must consider the default locale
     */
    public void setLocale(Locale locale)
    {   this.locale = locale; }
    
    /**
	 * add ImageSearcherListener
	 * 
	 * 
	 * @param listener a ImageSearcherListener
	 */
    public void addImageDownloadedListener(ImageSearcherListener listener)
    {
        if ( listener != null )
        {
            this.listeners.add(ImageSearcherListener.class, listener);
        }
    }
    
    /**
	 * add ImageSearcherListener
	 * 
	 * 
	 * @param listener a ImageSearcherListener
	 */
    public void removeImageDownloadedListener(ImageSearcherListener listener)
    {
        if ( listener != null )
        {
            this.listeners.remove(ImageSearcherListener.class, listener);
        }
    }
    
    /** indicate to all listeners that an image link was found
     *	@param event an ImageFoundEvent 
     */
    protected void fireImageFound(ImageFoundEvent event)
    {
	if ( this.listeners != null )
	{
	    ImageSearcherListener[] listeners = (ImageSearcherListener[])this.listeners.getListeners(ImageSearcherListener.class);
	    
	    if ( listeners != null )
	    {
		for(int i = 0; i < listeners.length; i++)
		{
		    ImageSearcherListener listener = listeners[i];
		    
		    if ( listener != null )
		    {
			listener.imageFound(event);
		    }
		}
	    }
	}
    }
    
    /** indicate to all listeners that the search has began
     *	@param event an ImageSearcherEvent
     */
    protected void fireSearchHasBegan(ImageSearcherEvent event)
    {
	if ( this.listeners != null )
	{
	    ImageSearcherListener[] listeners = (ImageSearcherListener[])this.listeners.getListeners(ImageSearcherListener.class);
	    
	    if ( listeners != null )
	    {
		for(int i = 0; i < listeners.length; i++)
		{
		    ImageSearcherListener listener = listeners[i];
		    
		    if ( listener != null )
		    {
			listener.searchHasBegan(event);
		    }
		}
	    }
	}
    }
    
    /** indicate to all listeners that the search has began
     *	@param event an ImageSearcherEvent
     */
    protected void fireSearchFinished(ImageSearcherEvent event)
    {
	if ( this.listeners != null )
	{
	    ImageSearcherListener[] listeners = (ImageSearcherListener[])this.listeners.getListeners(ImageSearcherListener.class);
	    
	    if ( listeners != null )
	    {
		for(int i = 0; i < listeners.length; i++)
		{
		    ImageSearcherListener listener = listeners[i];
		    
		    if ( listener != null )
		    {
			listener.searchFinished(event);
		    }
		}
	    }
	}
    }
    
}
