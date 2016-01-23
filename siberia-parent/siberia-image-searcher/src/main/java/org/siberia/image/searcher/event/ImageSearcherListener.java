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

import java.util.EventListener;

/**
 *
 * Listener about image search
 *
 * @author Alexis
 */
public interface ImageSearcherListener extends EventListener
{
    /**
     * called when an ImageSearcher provide a new image link
     * 
     * @param event an ImageFoundEvent
     */
    public void imageFound(ImageFoundEvent event);
    
    /**
     * called when the search begin
     * 
     * @param event an ImageSearcherEvent
     */
    public void searchHasBegan(ImageSearcherEvent event);
    
    /**
     * called when the search is finished
     * 
     * @param event an ImageSearcherEvent
     */
    public void searchFinished(ImageSearcherEvent event);
}
