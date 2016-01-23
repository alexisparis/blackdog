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

import org.blackdog.type.CategorizedItem;
import org.blackdog.type.SongItem;

/**
 *
 * Abstract version of an AudioEntagger
 *  that indicate that it is able to entagg file with main properties :
 *  album, artist, category, etc...
 *
 * @author alexis
 */
public abstract class AbstractAudioEntagger
    implements AudioEntagger
{
    /** Creates a new instance of AbstractAudioEntagger */
    public AbstractAudioEntagger(  )
    {
    }

    /** returns true if the entagger known entagging one of the given properties<br>
     *        this method is used to determine if a change of some properties have to provoke
     *        a file tags update<br>
     *        @param properties an array of String representing property name
     *        @return true if the entagger known entagging one of the given properties
     */
    public boolean isSupportingOneOfProperties( String... properties )
    {
        boolean supports = false;

        if ( properties != null )
        {
            for ( int i = 0; ( i < properties.length ) && ! supports; i++ )
            {
                String property = properties[i];

                if ( property != null )
                {
                    /* it must be a property taggable */
                    if ( SongItem.PROPERTY_ALBUM.equals( property ) ||
                             SongItem.PROPERTY_ARTIST.equals( property ) ||
                             SongItem.PROPERTY_COMMENT.equals( property ) ||
                             SongItem.PROPERTY_TITLE.equals( property ) ||
                             SongItem.PROPERTY_TRACK_NUMBER.equals( property ) ||
                             SongItem.PROPERTY_YEAR_RELEASED.equals( property ) ||
                             CategorizedItem.PROPERTY_CATEGORY.equals( property ) )
                    {
                        supports = true;
                    }
                }
            }
        }

        return supports;
    }
}
