/*
 * blackdog player : define the interface Player supported by blackdog
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
package org.blackdog.player.event;

import java.util.EventListener;
import javax.sound.sampled.LineUnavailableException;
import org.blackdog.player.Player;
import org.blackdog.type.Playable;

/**
 *
 * Define a listener to interact with player
 *
 * @author alexis
 */
public interface PlayerListener extends EventListener
{
    /** method called to indicate an error to the listener
     *  @param player the player that get the exception
     *  @param exception the exception received
     */
    public void errorReceived(Player player, Exception exception);
    
    /** called when the current SongItem has been fully read
     *  @param player the player that played the SongItem
     *  @param playable the Playable objct that has been fully read
     *	@param milliSecondsEstimation the estimation of the stream in milli-seconds
     */
    public void itemFullyRead(Player player, Playable playable, long milliSecondsEstimation);
}
