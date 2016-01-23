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

import org.apache.log4j.Logger;

import org.blackdog.type.Playable;
import org.blackdog.type.base.RepeatMode;

import org.siberia.type.AbstractSibType;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Abstract implementation of PlayableSession
 *
 * @author alexis
 */
public abstract class AbstractPlayableSession extends AbstractSibType implements PlayableSession
{
    /** logger */
    private transient Logger logger = Logger.getLogger( AbstractPlayableSession.class );

    /** current playable */
    private Playable currentPlayable = null;

    /**
     *  Creates a new instance of AbstractPlayableSession */
    public AbstractPlayableSession(  )
    {
        this( null );
    }

    /**
     *  Creates a new instance of AbstractPlayableSession
     *        @param initialPlayable the initial playable of the session
     */
    public AbstractPlayableSession( Playable initialPlayable )
    {
        this.setCurrentPlayable( initialPlayable );
    }

    /** get current playable
     *        @return the current playable or null if no playable is available
     */
    public Playable getCurrentPlayable(  )
    {
        return this.currentPlayable;
    }

    /** set the current playable
     *        @param playable the new playable to set as current playable
     */
    public void setCurrentPlayable( Playable playable )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling setCurrentPlayable(" + playable + ")");
	}
	
        if ( playable != this.getCurrentPlayable(  ) )
        {
            Playable oldPlayable = this.getCurrentPlayable(  );

            if ( this.logger.isDebugEnabled(  ) )
            {
                this.logger.debug( "setting current playable : " + ( 
                                                                       ( playable == null ) ? null : playable.getName(  )
                                                                    ) );
            }

            this.currentPlayable = playable;

            this.firePropertyChange( PROPERTY_CURRENT_PLAYABLE,
                                     oldPlayable,
                                     this.getCurrentPlayable(  ) );
        }
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling setCurrentPlayable(" + playable + ")");
	}
    }

    /** provide a random integer in the range [a, b]
     *        @param a an integer
     *        @param b an integer
     *        @return an integer
     */
    protected int getRandomNumber( int a, int b )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getRandomNumber(" + a + ", " + b + ")");
	}
        double random = Math.random(  ) * ( Math.abs( a - b ) + 1 );

        int result = (int) ( Math.min( a, b ) + random );
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of getRandomNumber(" + a + ", " + b + ") return " + result);
	}

        return result;
    }

    /**
     * called to free the provider from it context
     *  this method is called when the provider will not be used anymore
     */
    public void dispose(  )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling dispose()");
	}
        this.currentPlayable = null;
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling dispose()");
	}
    }
}
