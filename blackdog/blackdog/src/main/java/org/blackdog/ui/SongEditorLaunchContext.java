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
package org.blackdog.ui;

import org.blackdog.type.PlayList;
import org.siberia.editor.launch.DefaultEditorLaunchContext;
import org.siberia.type.SibType;

/**
 *
 * Specific EditorLaunchContext that indicate the playlist of the item
 *
 * @author alexis
 */
public class SongEditorLaunchContext extends DefaultEditorLaunchContext
{
    /** define the playlist where the song item appear */
    private PlayList playList = null;
    
    /** Creates a new instance of SongEditorLaunchContext
     *  @param item the item to edit
     *  @param playlist the playlist where the item appear
     */
    public SongEditorLaunchContext(SibType item, PlayList playlist)
    {
        super(item);
        
        this.playList = playlist;
    }
    
    /** return the playlist related to thie given item
     *  @return a PlayList
     */
    public PlayList getPlayList()
    {
        return this.playList;
    }
    
}
