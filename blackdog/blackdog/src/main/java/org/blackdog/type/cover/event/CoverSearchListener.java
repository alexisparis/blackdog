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
package org.blackdog.type.cover.event;

import java.util.EventListener;

/**
 *
 * Define methods to listen to cover search.
 *
 * same as a ListDataListener
 *
 * @author alexis
 */
public interface CoverSearchListener extends EventListener
{         
    /* 
     * indicate that a new url has been found
     *  @param e a CoverSearchItemEvent
     */
    public void imageAdded(CoverSearchItemEvent e);
          
    /**
     * indicate that all images have been removed
     * 
     * @param e a CoverSearchItemEvent
     */
    public void imagesRemoved(CoverSearchItemEvent e);
    
    /** indicate that the search is finished
     *	@param event a CoverSearchEvent
     */
    public void searchFinished(CoverSearchEvent event);
    
    /** indicate that the search has started
     *	@param event a CoverSearchEvent
     */
    public void searchStarted(CoverSearchEvent event);
}
