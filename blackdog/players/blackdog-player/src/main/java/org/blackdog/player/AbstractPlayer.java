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
import java.beans.PropertyChangeSupport;
import javax.swing.event.EventListenerList;
import org.blackdog.player.event.PlayerListener;
import org.blackdog.type.Playable;
import org.blackdog.type.SongItem;

/**
 *
 * Abstract implementation of Player
 *
 * @author alexis
 */
public abstract class AbstractPlayer implements Player
{
    /** PropertyChangeSupport */
    private PropertyChangeSupport support          = new PropertyChangeSupport(this);
    
    /** percentage played */
    private double                percentagePlayed = 0.0d;
    
    /** player status */
    private PlayerStatus          status           = PlayerStatus.STOPPED;
    
    /** item associated with the player */
    private Playable              item             = null;
    
    /** event listener list */
    private EventListenerList     listeners        = new EventListenerList();
    
    /** volume gain */
    private double                volumeGain       = 0.5d;
    
    /** Creates a new instance of AbstractAudioPlayer */
    public AbstractPlayer()
    {   }
    
    /** ask player if it accepts to be used<br>
     *	this method can be overriden if a player is platform specific
     *	@return true if the player accept to be use
     */
    public boolean acceptToBeUsed()
    {
	return true;
    }
    
    /** dispose the player */
    public void dispose()
    {
	if ( ! PlayerStatus.STOPPED.equals(this.getPlayerStatus()) )
	{
	    this.stop();
	}
    }
    
    /** configure the player */
    public void configure()
    {
	
    }
    
    /** return the item currently associated with the player
     *  @return a Playable
     */
    public Playable getItem()
    {   return this.item; }
    
    /** initialize the item currently associated with the player
     *  @param item a Playable
     */
    public void setItem(Playable item)
    {   if ( item != this.getItem() )
        {   Playable oldItem = this.getItem();
            
            this.item = item;
            
            this.firePropertyChangeEvent(PROPERTY_ITEM_PLAYED, oldItem, this.getItem());
	    
	    this.setPosition(0d);
        }
    }
    
    /** ########################################################################
     *  ########## methods that force the player to play, pause or stop ########
     *  ######################################################################## */
    
    /**
     * Plays an audio sample from the beginning
     */
    public void play()
    {   
        this.playImpl();
        
        this.setPlayerStatus(PlayerStatus.PLAYING);
    }
    
    /**
     * resume
     */
    public void resume()
    {
	this.resumeImpl();
	
	this.setPlayerStatus(PlayerStatus.PLAYING);
    }
    
    /**
     * resume
     */
    public abstract void resumeImpl();
    
    /**
     * Plays an audio sample from the beginning. do not care about the status of the player
     */
    public abstract void playImpl();
    
    /**
     * Plays an audio sample at a given percentage position
     *  @param position a position in percentage of the current audio sample length.<br/>
     *      if position is 0, then the player start the audio sample at the beginning. if position is 98, then the player<br/>
     *      will only process the end of the audio sample.
     */
    public void playAt(double position)
    {   
        this.playAtImpl(position);
        
        this.setPlayerStatus(PlayerStatus.PLAYING);
    }
    
    /**
     * Plays an audio sample at a given percentage position. do not care about the status of the player
     *  @param position a position in percentage of the current audio sample length.<br/>
     *      if position is 0, then the player start the audio sample at the beginning. if position is 98, then the player<br/>
     *      will only process the end of the audio sample.
     */
    public abstract void playAtImpl(double position);
    
    /**
     * Stop temporarly or start at the current position the current audio sample
     */
    public void pause()
    {   
        this.pauseImpl();
        
	PlayerStatus status = this.getPlayerStatus();
	if ( status != null && PlayerStatus.PLAYING.equals(status) )
	{
	    this.setPlayerStatus(PlayerStatus.PAUSED);
	}
    }
    
    /**
     * Stop temporarly or start at the current position the current audio sample. do not care about the status of the player
     */
    public abstract void pauseImpl();
    
    /**
     * stop this player. Any audio currently playing is stopped
     * immediately.
     *
     * Another call to play will restart the same player
     */
    public void stop()
    {   
        this.stopImpl();
        
        this.setPlayerStatus(PlayerStatus.STOPPED);
	
	this.setPosition(0);
    }
    
    /**
     * stop this player. Any audio currently playing is stopped
     * immediately.
     * Do not care about the status of the player
     *
     * Another call to play will restart the same player
     */
    public abstract void stopImpl();
    
    /** ########################################################################
     *  ##### methods that give information about the status of the player #####
     *  ######################################################################## */
    
    /**
     * Returns true if the player is in action
     *
     * @return	true if the player is currently processing an audio sample.
     *          false if the player has no current audio sample, or if it stopped or paused.
     */
    public boolean isPlaying()
    {   return this.status.equals(PlayerStatus.PLAYING); }
    
    /** indicate the status of the player
     *  @param status a PlayerStatus
     */
    public void setPlayerStatus(PlayerStatus status)
    {   if ( status == null )
        {   throw new IllegalArgumentException("status '" + status + "' is an invalid status player"); }
        
        if ( ! this.getPlayerStatus().equals(status) )
        {   PlayerStatus oldstatus = this.getPlayerStatus();
            
            this.status = status;
            
            this.firePropertyChangeEvent(PROPERTY_STATUS, oldstatus, this.getPlayerStatus());
        }
    }
    
    /** return the status of the player
     *  @return a PlayerStatus
     */
    public PlayerStatus getPlayerStatus()
    {   return this.status; }
    
    /** ########################################################################
     *  ###### methods that give information about the actual position of ######
     *  ####################### the player in the file #########################
     *  ######################################################################## */
    
    /**
     * Returns the completed status of this player.
     *
     * @return	true if all available MPEG audio frames have been
     *			decoded, or false otherwise.
     */
    public boolean isComplete()
    {   return this.getPosition() >= 1000.0d; }
    
    /**
     * Retrieves the position in the percentage [0, 1000] of the current audio sample being played
     *  @return a double representing a percentage in the range [0, 1000]
     */
    public double getPosition()
    {   return this.percentagePlayed; }
    
    /**
     * set the position in the percentage [0, 1000] of the current audio sample being played
     *  @param position a double representing a percentage in the range [0, 1000]
     */
    protected void setPosition(double position)
    {   double correctedPosition = position;
        
        if ( correctedPosition < 0 )
        {   correctedPosition = 0; }
        else if ( correctedPosition > 1000 )
        {   correctedPosition = 1000; }
        
        if ( correctedPosition != this.getPosition() )
        {   
            double oldValue = this.getPosition();
            
            this.percentagePlayed = correctedPosition;
            
            this.firePropertyChangeEvent(PROPERTY_PERCENTAGE_PLAYED, oldValue, this.getPosition());
        }
    }
    
    /** return the volume gain
     *	@return a double that represents the volume gain
     */
    public double getVolumeGain()
    {
	return this.volumeGain;
    }
    
    /** initialize the volume gain
     *	@param gain a double that represents the volume gain
     */
    public void setVolumeGain(double gain)
    {
	double _gain = gain;
	
	if ( _gain < 0 )
	{
	    _gain = 0;
	}
	if ( _gain > 1 )
	{
	    _gain = 1;
	}
	
	if ( _gain != this.getVolumeGain() )
	{
	    double oldGain = this.getVolumeGain();
	    
	    this.volumeGain = _gain;
	    
	    this.updateVolumeGain(_gain);
	    
	    this.firePropertyChangeEvent(PROPERTY_VOLUME_GAIN, oldGain, this.getVolumeGain());
	}
    }
    
    /** apply the new gain on the audio system to really change the volume
     *	@param newValue a double
     */
    protected abstract void updateVolumeGain(double newValue);
    
    /* #########################################################################
     * ################ PropertyChangeListener related methods #################
     * ######################################################################### */
    
    /** fire a PropertyChangeEvent
     *  @param propertyName the name of the property
     *  @param oldValue the old value
     *  @param newValue the new value
     */
    protected void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue)
    {   this.support.firePropertyChange(propertyName, oldValue, newValue); }
    
    /** add a new PropertyChangeListener
     *  @param listener a PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {   if ( listener != null )
        {   this.support.addPropertyChangeListener(listener); }
    }
    
    /** add a new PropertyChangeListener
     *  @param propertyName the name of a property
     *  @param listener a PropertyChangeListener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {   if ( listener != null )
        {   this.support.addPropertyChangeListener(propertyName, listener); }
    }
    
    /** remove a new PropertyChangeListener
     *  @param listener a PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {   if ( listener != null )
        {   this.support.removePropertyChangeListener(listener); }
    }
    
    /** remove a new PropertyChangeListener
     *  @param propertyName the name of a property
     *  @param listener a PropertyChangeListener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {   if ( listener != null )
        {   this.support.removePropertyChangeListener(propertyName, listener); }
    }
    
    /* #########################################################################
     * #################### PlayerListener related methods #####################
     * ######################################################################### */
    
    /** add a new PlayerListener
     *  @param listener a PlayerListener
     */
    public void addPlayerListener(PlayerListener listener)
    {
        this.listeners.add(PlayerListener.class, listener);
    }
    
    /** remove a PlayerListener
     *  @param listener a PlayerListener
     */
    public void removePlayerListener(PlayerListener listener)
    {
        this.listeners.remove(PlayerListener.class, listener);
    }
    
    /** send an error to PlayerListener
     *  @param exception an exception
     */
    protected void error(Exception exception)
    {
        PlayerListener[] list = (PlayerListener[])this.listeners.getListeners(PlayerListener.class);
        if ( list != null )
        {
            for(int i = 0; i < list.length; i++)
            {
                PlayerListener current = list[i];
                
                if ( current != null )
                {
                    current.errorReceived(this, exception);
                }
            }
        }
    }
    
    /** indicate all PlayerListener that the current item has been fully read
     *  @param item a Playable
     *	@param milliSecondsEstimation the estimation of the stream in milli-seconds or -1 if do not know
     */
    protected void fireSongItemFullyRead(Playable item, long milliSecondsEstimation)
    {
        PlayerListener[] list = (PlayerListener[])this.listeners.getListeners(PlayerListener.class);
        if ( list != null )
        {
            for(int i = 0; i < list.length; i++)
            {
                PlayerListener current = list[i];
                
                if ( current != null )
                {
                    current.itemFullyRead(this, item, milliSecondsEstimation);
                }
            }
        }
    }
    
}
