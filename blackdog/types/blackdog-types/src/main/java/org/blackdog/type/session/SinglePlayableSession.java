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
package org.blackdog.type.session;

import org.blackdog.type.Playable;
import org.blackdog.type.base.RepeatMode;

/**
 *
 * particular session that manage only one Playable
 *
 * @author alexis
 */
public class SinglePlayableSession
    extends AbstractPlayableSession
{
    /** Creates a new instance of SinglePlayableSession
     *        @param playable
     */
    public SinglePlayableSession( Playable playable )
    {
        super( playable );
    }

    /**
     * reinitialize the provider
     *  clear all items that could represents the Playable already played
     */
    public void reinit(  )
    {
        /* do nothing */
    }

    /**
     * go to the previous Playable
     *
     * @param current the current playable
     * @param shuffle true to indicate taht shuffle is enabled
     * @param repeatMode the mode of repeat
     * @return a Playable or null if no playable found anymore
     */
    public void goToPreviousPlayable( Playable current, boolean shuffle, RepeatMode repeatMode )
    {
        this.setCurrentPlayable( current );
    }

    /**
     * go to the next Playable
     *
     * @param current the current playable
     * @param shuffle true to indicate taht shuffle is enabled
     * @param repeatMode the mode of repeat
     */
    public void goToNextPlayable( Playable current, boolean shuffle, RepeatMode repeatMode )
    {
        this.setCurrentPlayable( current );
    }
}
