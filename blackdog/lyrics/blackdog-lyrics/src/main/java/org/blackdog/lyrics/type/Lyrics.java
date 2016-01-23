/*
 * blackdog lyrics : define editor and systems to get lyrics for a song
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
package org.blackdog.lyrics.type;

import org.blackdog.type.SongItem;
import org.siberia.type.SibType;

/**
 *
 * Represents a lyrics
 *
 * @author alexis
 */
public interface Lyrics extends SibType
{
    /** property content */
    public static final String PROPERTY_HTML_CONTENT    = "html-content";
    
    /** lyrics provider */
    public static final String PROPERTY_LYRICS_PROVIDER = "lyrics-provider";
    
    /** retrieve status */
    public static final String PROPERTY_RETRIEVED       = "lyrics-retrieving-status";
    
    /** return the SongItem linked to the lyrics
     *  @return an SongItem
     */
    public SongItem getSongItem();
    
    /** initialize the SongItem linked to the lyrics
     *  @param item an SongItem
     */
    public void setSongItem(SongItem item);
    
    /** set the retrieve status of the lyrics
     *	@param status a LyricsRetrievedStatus
     */
    public void setRetrieveStatus(LyricsRetrievedStatus status);
    
    /** return the retrieve status of the lyrics
     *	@return a LyricsRetrievedStatus
     */
    public LyricsRetrievedStatus getRetrieveStatus();

    /** return the html content representing the lyrics
     *  @return a String
     */
    public String getHtmlContent();

    /** initialize the html content representing the lyrics
     *  @param htmlContent a String
     */
    public void setHtmlContent(String htmlContent);
    
    /** indicate to the lyrics to try to retrieve content about its current SongItem */
    public void retrieve();
    
    
}
