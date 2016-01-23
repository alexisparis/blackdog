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

import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 *
 * Defines the retrieving status of a Lyrics
 *
 * @author alexis
 */
public enum LyricsRetrievedStatus
{   
    NONE                ("none"),             // lyrics retrieving process has not be started
    CURRENTLY_IN_PROCESS("currentlyInProcess"), // lyrics is actually being process
    RETRIEVED           ("retrieved"),        // lyrics was found and its content is filled
    UNRETRIEVED         ("unretrieved");	     // lyrics was not found
    
    /** code */
    private String code = null;
    
    /**
     * soft reference to the ResourceBundle linked to LyricsRetrievedStatus
     */
    private static SoftReference<ResourceBundle> rbReference = new SoftReference<ResourceBundle>(null);
    
    private LyricsRetrievedStatus(String code)
    {   this.code = code; }
    
    /** return the label
     *  @return the label
     */
    public String label()
    {   
	ResourceBundle rb = this.rbReference.get();
        if ( rb == null )
        {   rb = ResourceBundle.getBundle(LyricsRetrievedStatus.class.getName());
            this.rbReference = new SoftReference<ResourceBundle>(rb);
        }
        
        return rb.getString(this.code);
    }
}
