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

import org.siberia.type.SibType;

/**
 *
 * Define an object that is able to manage playable objects
 * to determine which item to play
 *
 * @author alexis
 */
public interface PlayableSession
    extends SibType
{
    /** property current playable */
    public static final String PROPERTY_CURRENT_PLAYABLE = "currentPlayable";

    /** reinitialize the provider
     *  clear all items that could represents the Playable already played
     */
    public void reinit(  );

    /** called to free the provider from it context
     *  this method is called when the provider will not be used anymore
     */
    public void dispose(  );

    /** get current playable
     *        @return the current playable or null if no playable is available
     */
    public Playable getCurrentPlayable(  );

    /** set the current playable
     *        @param playable the new playable to set as current playable
     */
    public void setCurrentPlayable( Playable playable );

    /** go to the next Playable
     *  @param current the current playable
     *        @param shuffle true to indicate taht shuffle is enabled
     *        @param repeatMode the mode of repeat
     */
    public void goToNextPlayable( Playable current, boolean shuffle, RepeatMode repeatMode );

    /** go to the previous Playable
     *  @param current the current playable
     *        @param shuffle true to indicate taht shuffle is enabled
     *        @param repeatMode the mode of repeat
     *  @return a Playable or null if no playable found anymore
     */
    public void goToPreviousPlayable( Playable current, boolean shuffle, RepeatMode repeatMode );
}
