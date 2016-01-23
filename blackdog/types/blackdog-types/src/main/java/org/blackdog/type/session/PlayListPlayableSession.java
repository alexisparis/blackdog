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

import java.lang.ref.WeakReference;
import org.apache.log4j.Logger;
import org.blackdog.type.PlayList;
import org.blackdog.type.Playable;

/**
 *
 * Playable session based on a PlayList
 *
 * @author alexis
 */
public class PlayListPlayableSession extends HistoricPlayableSession
{
    /** logger */
    private transient Logger logger = Logger.getLogger(PlayListPlayableSession.class);
    
    /** play list */
    private WeakReference<PlayList> playlistRef = null;

    /** Creates a new instance of PlayListPlayableSession
     *        @param playlist a PlayList
     */
    public PlayListPlayableSession( PlayList playlist )
    {
        this.playlistRef = new WeakReference<PlayList>(playlist);
    }
    
    /** return the playlist linked to this session
     *	@return a PlayList
     */
    public PlayList getPlayList()
    {
	return this.playlistRef.get();
    }

    /**
     * return a random playable for this session<br>
     *
     * @return a random Playable
     */
    protected Playable getRandomPlayable(  )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getRandomPlayable()");
	}
	
        Playable playable = null;
	
	PlayList playlist = this.getPlayList();
	
	if ( playlist != null )
	{
	    int position = -1;

	    if ( playlist.size(  ) > 0 )
	    {
		position = this.getRandomNumber( 0, playlist.size(  ) - 1 );
	    }
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("considering random position " + position);
	    }

	    if ( ( position >= 0 ) && ( position < playlist.size(  ) ) )
	    {
		Object o = playlist.get( position );

		if ( o instanceof Playable )
		{
		    playable = (Playable) o;
		}
	    }
	    else
	    {
		logger.warn("position outside range of playlist");
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling getRandomPlayable() returns " + playable);
	}

        return playable;
    }

    /**
     * return the last playable for this session<br>
     *
     * @return the last Playable
     */
    protected Playable getLastPlayable(  )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getLastPlayable()");
	}
        Playable playable = null;

	PlayList playlist = this.getPlayList();
	
	if ( playlist != null )
	{
	    if ( playlist.size(  ) > 0 )
	    {
		Object o = playlist.get( playlist.size(  ) - 1 );

		if ( o instanceof Playable )
		{
		    playable = (Playable) o;
		}
	    }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("the size of the playlist is " + playlist.size() + " --> could not provide last");
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling getLastPlayable() returns " + playable);
	}

        return playable;
    }

    /**
     * return the first playable for this session
     *
     * @return the first Playable
     */
    protected Playable getFirstPlayable(  )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getFirstPlayable()");
	}
        Playable playable = null;

	PlayList playlist = this.getPlayList();
	
	if ( playlist != null )
	{
	    if ( playlist.size(  ) > 0 )
	    {
		Object o = playlist.get( 0 );

		if ( o instanceof Playable )
		{
		    playable = (Playable) o;
		}
	    }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("the size of the playlist is " + playlist.size() + " --> could not provide first");
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling getFirstPlayable() returns " + playable);
	}

        return playable;
    }

    /**
     * return the previous playable without considering shuffle or repeat mode
     *
     * @param playable a Playable
     * @return a Playable
     */
    protected Playable getPreviousPlayable( Playable playable )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getPreviousPlayable(" + playable + ")");
	}
        Playable result = null;

	PlayList playlist = this.getPlayList();

	if ( playlist != null )
	{
	    if ( playlist.size(  ) > 0 )
	    {
		int index = playlist.indexOfByReference( playable );

		if ( logger.isDebugEnabled() )
		{
		    logger.debug("index of " + playable + " is " + index);
		}

		if ( ( index >= 1 ) && ( index < playlist.size(  ) ) )
		{
		    Object o = playlist.get( index - 1 );

		    if ( o instanceof Playable )
		    {
			result = (Playable) o;
		    }
		    else
		    {
			logger.error("item at " + (index - 1) + " is not a Playable instance");
		    }
		}
		else
		{
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("could not provide next (index of given item is " + index + ", size of the playlist is " + playlist.size() + ")");
		    }
		}
	    }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("the size of the playlist is " + playlist.size() + " --> could not provide previous");
		}
	    }
	} 
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling getPreviousPlayable(" + playable + ") returns " + result);
	}

        return result;
    }

    /**
     * return the next playable without considering shuffle or repeat mode
     *
     * @param playable a Playable
     * @return a Playable
     */
    protected Playable getNextPlayable( Playable playable )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getNextPlayable(" + playable + ")");
	}
        Playable result = null;

	PlayList playlist = this.getPlayList();
	
	if ( playlist != null )
	{
	    if ( playlist.size(  ) > 0 )
	    {
		int index = playlist.indexOfByReference( playable );

		if ( logger.isDebugEnabled() )
		{
		    logger.debug("index of " + playable + " is " + index);
		}

		if ( ( index >= 0 ) && ( index < ( playlist.size(  ) - 1 ) ) )
		{
		    Object o = playlist.get( index + 1 );

		    if ( o instanceof Playable )
		    {
			result = (Playable) o;
		    }
		    else
		    {
			logger.error("item at " + (index + 1) + " is not a Playable instance");
		    }
		}
		else
		{
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("could not provide next (index of given item is " + index + ", size of the playlist is " + playlist.size() + ")");
		    }
		}
	    }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("the size of the playlist is " + playlist.size() + " --> could not provide next");
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling getNextPlayable(" + playable + ") returns " + result);
	}

        return result;
    }
}
