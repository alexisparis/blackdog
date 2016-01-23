/*
 * blackdog types : define kind of items maanged by blackdog
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
package org.blackdog.entagger;

import org.blackdog.report.TagsUpdateReport;

import org.blackdog.type.TaggedSongItem;

/**
 *
 * define an object able to update the tags of an audio file
 *
 * @author alexis
 */
public interface AudioEntagger
{
    /** refresh the file tags of an TaggedSongItem according to its properties
     *        @param songItem a TaggedSongItem
     *        @param report a TagsUpdateReport
     */
    public void updateFileTags( TaggedSongItem songItem, TagsUpdateReport report );

    /** returns true if the entagger known entagging one of the given properties<br>
     *        this method is used to determine if a change of some properties have to provoke
     *        a file tags update<br>
     *        @param properties an array of String representing property name
     *        @return true if the entagger known entagging one of the given properties
     */
    public boolean isSupportingOneOfProperties( String... properties );
}
