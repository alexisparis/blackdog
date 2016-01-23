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
import org.siberia.image.searcher.event.ImageSearcherListener;

/**
 *
 * Define an object that is able to collect images
 *  acording to criterions
 *
 * @author alexis
 */
public interface ImageSearcher
{
    /** set the criterions to use for the search
     *	@param criterions an array of String
     */
    public void setCriterions(String[] criterions);
    
    /** return the criterions to use for the search
     *	@return an array of String
     */
    public String[] getCriterions();
    
    /** return the maximum number of image links retrieved
     *  @return an integer
     */
    public int getMaximumLinksRetrieved();
    
    /** set the maximum number of image links retrieved
     *  @param maxLinkRetrieved un entier
     */
    public void setMaximumLinksRetrieved(int maxLinkRetrieved);
    
    /** return the image size wanted for this search
     *  @return a ImageSize
     */
    public ImageSize getImageSize();
    
    /** initialize the image size wanted for this search
     *  @param imageSize a ImageSize
     *
     *  @exception IllegalArgumentException if imageSize is null
     */
    public void setImageSize(ImageSize imageSize);
    
    /** launch search */
    public void search();
    
    /** cancel search */
    public void cancel();
    
    /** return the locale used for the search
     *  @return a Locale or null if the research must consider the default locale
     */
    public Locale getLocale();
    
    /** initialize the locale used for the search
     *  @param locale a Locale or null if the research must consider the default locale
     */
    public void setLocale(Locale locale);
    
    /**
	 * add ImageSearcherListener
	 * 
	 * @param listener a IImageFoundListener
	 */
    public void addImageDownloadedListener(ImageSearcherListener listener);
    
    /**
	 * add ImageSearcherListener
	 * 
	 * @param listener a IImageFoundListener
	 */
    public void removeImageDownloadedListener(ImageSearcherListener listener);
}
