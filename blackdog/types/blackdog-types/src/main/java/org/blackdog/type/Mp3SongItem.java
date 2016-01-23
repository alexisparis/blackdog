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
package org.blackdog.type;

import org.apache.log4j.Logger;

import org.blackdog.type.annotation.AudioFileFormatTags;

import org.siberia.type.annotation.bean.Bean;

/**
 *
 * MP3 implementation of a song
 *
 * @author alexis
 */
@Bean( 
        name = "Mp3 song item", internationalizationRef = "org.blackdog.rc.i18n.type.Mp3SongItem", expert = false, hidden = true, preferred = true, propertiesClassLimit = Object.class, methodsClassLimit = Object.class
     )
@AudioFileFormatTags( 
        audioLengthTags = 
{
    "mp3.id3tag.length"}
, audioBytesLengthTags = 
{
    "audio.length.bytes"}

     )
public class Mp3SongItem
    extends AbstractTaggedSongItem
{
    /** logger */
    private transient Logger logger = Logger.getLogger( Mp3SongItem.class );

    /** Creates a new instance of Mp3SongItem */
    public Mp3SongItem(  )
    {
    }
}
