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
package org.blackdog.lyrics.action;

import org.apache.log4j.Logger;
import org.blackdog.lyrics.type.LeoLyrics;
import org.blackdog.lyrics.type.Lyrics;

/**
 *
 * Action that try to retrieve song lyrics according to leo's lyrics web site database.
 *
 * @author alexis
 */
public class LeoLyricsEditingAction extends AbstractLyricsEditingAction
{
    /** logger */
    private Logger logger = Logger.getLogger(LeoLyricsEditingAction.class);
    
    /** Creates a new instance of LeoLyricsEditingAction */
    public LeoLyricsEditingAction()
    {	}

    /**
     * create the lyrics which will be linked to the SongItem
     * 
     * @return a Lyrics object
     */
    protected Lyrics createLyrics()
    {
	return new LeoLyrics();
    }
    
}
