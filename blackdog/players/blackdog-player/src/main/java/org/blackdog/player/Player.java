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
package org.blackdog.player;

import java.beans.PropertyChangeListener;
import org.blackdog.player.event.PlayerListener;
import org.blackdog.type.Playable;

/**
 *
 * @author alexis
 */
public interface Player
{
    /** properties name */
    public static final String PROPERTY_PERCENTAGE_PLAYED = "percentagePlayed";
    
    public static final String PROPERTY_STATUS            = "status";
    
    public static final String PROPERTY_ITEM_PLAYED       = "itemPlayed";
    
    public static final String PROPERTY_VOLUME_GAIN       = "volumeGain";
    
    /** ask player if it accepts to be used<br>
     *	this method can be overriden if a player is platform specific
     *	@return true if the player accept to be use
     */
    public boolean acceptToBeUsed();
    
    /** return the item currently associated with the player
     *  @return a Playable
     */
    public Playable getItem();
    
    /** initialize the item currently associated with the player
     *  @param item a Playable
     */
    public void setItem(Playable item);
    
    /**
     * resume
     */
    public void resume();
    
    /** dispose the player */
    public void dispose();
    
    /** configure the player */
    public void configure();
    
    /**
     * Plays an audio sample from the beginning
     */
    public void play();
    
    /**
     * Plays an audio sample at a given percentage position
     *  @param position a position in percentage of the current audio sample length.<br/>
     *      if position is 0, then the player start the audio sample at the beginning. if position is 98, then the player<br/>
     *      will only process the end of the audio sample.
     */
    public void playAt(double position);
    
    /**
     * Stop temporarly or start at the current position the current audio sample
     */
    public void pause();
    
    /**
     * stop this player. Any audio currently playing is stopped
     * immediately.
     *
     * Another call to play will restart the same player
     */
    public void stop();
    
    /** indicate the status of the player
     *  @param status a PlayerStatus
     */
    public void setPlayerStatus(PlayerStatus status);
    
    /** return the status of the player
     *  @return a PlayerStatus
     */
    public PlayerStatus getPlayerStatus();
    
    /**
     * Returns true if the player is in action
     *
     * @return	true if the player is currently processing an audio sample.
     *          false if the player has no current audio sample, or if it stopped or paused.
     */
    public boolean isPlaying();
    
    /**
     * Returns the completed status of this player.
     *
     * @return	true if all available MPEG audio frames have been
     *			decoded, or false otherwise.
     */
    public boolean isComplete();
    
    /**
     * Retrieves the position in the percentage [0, 1000] of the current audio sample being played
     *  @return a double representing a percentage in the range [0, 1000]
     */
    public double getPosition();
    
    /** add a new PropertyChangeListener
     *  @param listener a PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);
    
    /** add a new PropertyChangeListener
     *  @param propertyName the name of a property
     *  @param listener a PropertyChangeListener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
    
    /** return the volume gain
     *	@return a double that represents the volume gain
     */
    public double getVolumeGain();
    
    /** initialize the volume gain
     *	@param gain a double that represents the volume gain
     */
    public void setVolumeGain(double gain);
    
    /** remove a new PropertyChangeListener
     *  @param listener a PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);
    
    /** remove a new PropertyChangeListener
     *  @param propertyName the name of a property
     *  @param listener a PropertyChangeListener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
    
    /** add a new PlayerListener
     *  @param listener a PlayerListener
     */
    public void addPlayerListener(PlayerListener listener);
    
    /** remove a PlayerListener
     *  @param listener a PlayerListener
     */
    public void removePlayerListener(PlayerListener listener);
}
