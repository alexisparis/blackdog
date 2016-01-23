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

import java.io.PrintStream;
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
 * Abstract implementation of PlayableSession that keep in memory the item played
 *
 * @author alexis
 */
public abstract class HistoricPlayableSession extends AbstractPlayableSession implements PlayableSession
{
    /** logger */
    private transient Logger logger = Logger.getLogger( HistoricPlayableSession.class );

    /** list of WeakReference on Playable already played */
    private List<WeakReference<Playable>> played = null;

    /** position in the played list
     *        -1 if we are at the top
     */
    private int playedPosition = -1;

    /**
     *  Creates a new instance of AbstractPlayableSession */
    public HistoricPlayableSession(  )
    {
        this( null );
    }

    /**
     *  Creates a new instance of AbstractPlayableSession
     *        @param initialPlayable the initial playable of the session
     */
    public HistoricPlayableSession( Playable initialPlayable )
    {
        this( initialPlayable, null );
    }

    /**
     * Creates a new instance of AbstractPlayableSession
     *        @param initialPlayable the initial playable of the session
     *        @param initialListSize the initial size of the list of played playable
     */
    public HistoricPlayableSession( Playable initialPlayable, Integer initialListSize )
    {
        super( initialPlayable );

        if ( initialListSize == null )
        {
            this.played = new ArrayList<WeakReference<Playable>>(  );
        } else
        {
            this.played = new ArrayList<WeakReference<Playable>>( initialListSize );
        }
	
	if ( initialPlayable != null )
	{
	    this.played.add(new WeakReference<Playable>(initialPlayable));
	}
    }
    
    /** print the state of the historic to System.out */
    void printHistoricStates()
    {
	printHistoricStates(System.out);
    }
    
    /** print the state of the historic
     *	@param stream a PrintStream
     */
    void printHistoricStates(PrintStream stream)
    {
	stream.println("================");
	if ( this.played == null )
	{
	    stream.println("no items in the historic (position = " + this.playedPosition + ")");
	}
	else
	{
	    for(int i = 0; i < this.played.size(); i++)
	    {
		WeakReference<Playable> ref = this.played.get(i);
		stream.println("\ti=" + i + " --> " + (ref == null ? null : ref.get()));
	    }
	    stream.println("(position = " + this.playedPosition + ")");
	}
	stream.println("================");
    }

    /** return the number of item considered as played by the session
     *        @return an integer
     */
    int getPlayedItemCount(  )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getPlayedItemCount()");
	}
        int result = ( ( this.played == null ) ? 0 : this.played.size(  ) );
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of getPlayedItemCount() returns " + result);
	}
	return result;
    }

    /** return the current position
     *        @return an integer or -1 if we are at the top
     */
    int getPosition(  )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getPosition()");
	}
        int result = this.playedPosition;
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of getPosition() returns " + result);
	}
	return result;
    }

    /** return the playable at the given position
     *        @param position an integer
     *        @return a Playable
     */
    protected Playable getPlayedItemAt( int position )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getPlayedItemAt(" + position + ")");
	}
        Playable playable = null;

        if ( this.played != null )
        {
            WeakReference<Playable> ref = this.played.get( position );

            if ( ref == null )
            {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("got a null weak reference at " + position);
		}
	    }
	    else
	    {
                playable = ref.get(  );
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of getPlayedItemAt(" + position + ") returns " + playable);
	}

        return playable;
    }

    /* #########################################################################
     * ########################## PlayableSession ##############################
     * ######################################################################### */

    /**
     * reinitialize the provider
     *  clear all items that could represents the Playable already played
     */
    public void reinit(  )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling reinit()");
	}
        if ( this.played != null )
        {
            this.played.clear(  );
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("clearing list of weak references");
	    }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("reinitializaing position at -1");
	}
        this.playedPosition = -1;
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling reinit()");
	}
    }

    @Override
    public void setCurrentPlayable( Playable playable )
    {
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("calling setCurrentPlayable(" + playable + ")");
	}
	this.setCurrentPlayable(playable, true);
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("end of calling setCurrentPlayable(" + playable + ")");
	}
    }

    public void setCurrentPlayable( Playable playable, boolean manageHistoric )
    {
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("calling setCurrentPlayable(" + playable + ", " + manageHistoric + ")");
	}
        super.setCurrentPlayable( playable );

        /* if the given playable is not null and is not the last item enqueued in the list,
         *  then add it
         */
	if ( manageHistoric && playable != null )
	{
	    if ( playable == null )
	    {
		if ( this.played != null )
		{
		    this.played.clear();
		}
		this.playedPosition = -1;
	    }
	    else if ( this.played != null )
	    {
		if ( this.playedPosition < 0 || this.playedPosition == this.played.size() - 1 )
		{
		    /** check that it is not the same item
		     *  if not add it
		     */
		    Playable lastPlayable = null;

		    if ( this.played.size() > 0 )
		    {
			WeakReference<Playable> lastRef = this.played.get(this.played.size() - 1);
			if ( lastRef != null )
			{
			    lastPlayable = lastRef.get();
			}
		    }
		    if ( lastPlayable != playable )
		    {
			this.played.add(new WeakReference<Playable>(playable));
			if ( this.playedPosition >= 0 )
			{
			    this.playedPosition ++;
			}
		    }
		}
		else
		{
		    this.playedPosition ++;

		    if ( this.played != null )
		    {
			/* replace at position if valid */
			this.played.set(this.playedPosition, new WeakReference<Playable>(playable));

			/** remove all items after playedPosition */
			for(int i = this.playedPosition + 1; i < this.played.size(); )
			{
			    this.played.remove(i);
			}
		    }

		    /** go to the top if so */
		    if ( this.played != null && this.playedPosition >= this.played.size() - 1 )
		    {
			this.playedPosition = -1;
		    }
		}
	    }
	}
	
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("end of calling setCurrentPlayable(" + playable + ", " + manageHistoric + ")");
	}
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
        if ( logger.isDebugEnabled(  ) )
        {
            logger.debug( "calling goToPreviousPlayable(" + current + ", " + shuffle + ", " + repeatMode + ")" );
        }

        Playable previous = null;

        RepeatMode _repeatMode = repeatMode;

        if ( _repeatMode == null )
        {
            _repeatMode = RepeatMode.NONE;
        }

        if ( RepeatMode.CURRENT.equals( _repeatMode ) )
        {
            if ( logger.isDebugEnabled(  ) )
            {
                logger.debug( "repeat mode current --> returning the same playable" );
            }

            previous = current;
        }
	else
        {
            boolean fixPrevToCurrent = false;

            boolean firstPlayable = this.isFirstPlayable( current );
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("is current the first playable ? " + firstPlayable);
	    }

            /* repeat is none or all */

            /* if repeat is none, must determine if we must close the session */
            if ( firstPlayable )
            {
                if ( RepeatMode.NONE.equals( repeatMode ) )
                {
                    fixPrevToCurrent = true;
                }
            }

            if ( logger.isDebugEnabled(  ) )
            {
                logger.debug( "fixPrevToCurrent : " + fixPrevToCurrent );
            }

            if ( ! fixPrevToCurrent )
            {
                if ( logger.isDebugEnabled(  ) )
                {
                    logger.debug( "playedPosition : " + playedPosition );
                }
		
		int _playedPosition = playedPosition;
		if ( _playedPosition < 0 && this.played != null )
		{
		    _playedPosition = this.played.size() - 1;
		}

                if ( ( _playedPosition > 0 ) && ( _playedPosition < this.getPlayedItemCount() ) )
                {
                    if ( logger.isDebugEnabled(  ) )
                    {
                        logger.debug( "getting playable from historic list" );
                    }

                    this.playedPosition = _playedPosition - 1;

                    if ( shuffle )
                    {
                        WeakReference<Playable> ref = this.played.get( this.playedPosition );

                        if ( ref != null )
                        {
                            previous = ref.get(  );
                        }
                    }
		    else
                    {
                        WeakReference<Playable> ref = this.played.get( this.playedPosition );

                        if ( ref != null )
                        {
                            previous = ref.get(  );
                        }
                    }
                }
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("previous is " + previous);
		}

                /* if position <= -1 or previous condition could not find a valid playable */
                if ( previous == null )
                {
                    if ( this.playedPosition > 0 )
                    {
                        if ( logger.isDebugEnabled(  ) )
                        {
                            logger.debug( "forcing playedPosition to -1" );
                        }
			
                        /** force to reinitialize since we should have found an item in historic list but null arrived */
                        this.playedPosition = -1;
			this.played.clear();
                    }
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("played position : " + this.playedPosition);
		    }

                    if ( shuffle )
                    {
                        if ( logger.isDebugEnabled(  ) )
                        {
                            logger.debug( "using random playable" );
                        }

                        previous = this.getRandomPlayable(  );
                    } else
                    {
                        /* first playable but repeat all mode */
                        if ( firstPlayable )
                        {
                            if ( logger.isDebugEnabled(  ) )
                            {
                                logger.debug( "using last playable" );
                            }

                            previous = this.getLastPlayable(  );
                        } else
                        {
                            if ( logger.isDebugEnabled(  ) )
                            {
                                logger.debug( "using previous playable" );
                            }

                            previous = this.getPreviousPlayable( current );
                        }
                    }
		    
		    /* if we found a previous that is not the current
		     *  it must be inserted before the current position
		     */
		    if ( previous != null && previous != current )
		    {
			if ( this.played.size() == 0 )
			{
			    this.playedPosition = -1;
			    this.played.add(new WeakReference<Playable>(previous));
			}
			else
			{
			    /* determine the real index of the current item */
			    int realIndex = this.playedPosition;
			    if ( realIndex < 0 )
			    {
				realIndex = this.played.size() - 1;
			    }
			    
			    this.played.add(realIndex, new WeakReference<Playable>(previous));
			    this.playedPosition = realIndex;
			}
		    }
                }
            }

            if ( fixPrevToCurrent )
            {
                previous = current;
            }
        }

        if ( logger.isDebugEnabled(  ) )
        {
            logger.debug( "final playedPosition : " + playedPosition );
            logger.debug( "end of goToPreviousPlayable(" + current + ", " + shuffle + ", " + repeatMode + ")" );
        }

        this.setCurrentPlayable( previous, false );
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("played position is " + this.playedPosition);
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling goToPreviousPlayable(" + current + ", " + shuffle + ", " + repeatMode + ")" );
	}
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
        if ( logger.isDebugEnabled(  ) )
        {
            logger.debug( "calling goToNextPlayable(" + current + ", " + shuffle + ", " + repeatMode + ")" );
        }

        Playable next = null;

        RepeatMode _repeatMode = repeatMode;

        if ( _repeatMode == null )
        {
            _repeatMode = RepeatMode.NONE;
        }

        if ( RepeatMode.CURRENT.equals( _repeatMode ) )
        {
            if ( logger.isDebugEnabled(  ) )
            {
                logger.debug( "repeat mode current --> returning the same playable" );
            }

            next = current;
        }
	else
        {
            boolean fixNextToNull = false;

            boolean lastPlayable = this.isLastPlayable( current );

            /* repeat is none or all */

            /* if repeat is none, must determine if we must close the session */
            if ( RepeatMode.NONE.equals( _repeatMode ) )
            {
                if ( lastPlayable )
                {
                    fixNextToNull = true;
                }
            }

            if ( logger.isDebugEnabled(  ) )
            {
                logger.debug( "fixNextToNull : " + fixNextToNull );
            }

            if ( ! fixNextToNull )
            {
                if ( logger.isDebugEnabled(  ) )
                {
                    logger.debug( "playedPosition : " + playedPosition );
                }

                if ( ( this.playedPosition > -1 ) && ( this.playedPosition < ( this.getPlayedItemCount() - 1 ) ) )
                {
                    this.playedPosition++;

                    if ( logger.isDebugEnabled(  ) )
                    {
                        logger.debug( "getting playable from historic list" );
                    }

                    if ( shuffle )// whatever the position, if we go next, then 
                    {
			next = this.getRandomPlayable();
//                        WeakReference<Playable> ref = this.played.get( this.playedPosition );
//
//                        if ( ref != null )
//                        {
//                            next = ref.get(  );
//                        }
                    }
		    else
                    {
                        WeakReference<Playable> ref = this.played.get( this.playedPosition );

                        if ( ref != null )
                        {
                            next = ref.get(  );
                        }
                    }
                }

                /* if position <= -1 or previous condition could not find a valid playable */
                if ( next == null )
                {
                    if ( this.playedPosition > -1 )
                    {
                        if ( logger.isDebugEnabled(  ) )
                        {
                            logger.debug( "forcing playedPosition to -1" );
                        }

                        /** force to go to the top */
                        this.playedPosition = -1;
                    }

                    if ( shuffle )
                    {
                        if ( logger.isDebugEnabled(  ) )
                        {
                            logger.debug( "using random playable" );
                        }

                        next = this.getRandomPlayable(  );
                    } else
                    {
                        /* last playable but repeat all mode */
                        if ( lastPlayable )
                        {
                            if ( logger.isDebugEnabled(  ) )
                            {
                                logger.debug( "using first playable" );
                            }

                            next = this.getFirstPlayable(  );
                        } else
                        {
                            if ( logger.isDebugEnabled(  ) )
                            {
                                logger.debug( "using next playable" );
                            }

                            next = this.getNextPlayable( current );
                        }
                    }

//                    if ( ( next != current ) && ( next != null ) )
//                    {
//                        if ( logger.isDebugEnabled(  ) )
//                        {
//                            logger.debug( "adding " + current.getName(  ) + " in historic list" );
//                        }
//
//                        this.played.add( new WeakReference<Playable>( current ) );
//                    }
                }
            }

            if ( fixNextToNull )
            {
                next = null;
            }
        }

        if ( logger.isDebugEnabled(  ) )
        {
            logger.debug( "final playedPosition : " + playedPosition );
            logger.debug( "end of goToNextPlayable(" + current + ", " + shuffle + ", " + repeatMode + ")" );
        }

        this.setCurrentPlayable( next, false );
	
	/** update historic */
	if ( next == null )
	{
	    this.played.clear();
	    this.playedPosition = -1;
	}
	else
	{
	    if ( this.playedPosition < 0 || this.playedPosition == this.played.size() - 1 )
	    {
		/** check that it is not the same item
		 *  if not add it
		 */
		Playable lastPlayable = null;
		
		if ( this.played.size() > 0 )
		{
		    WeakReference<Playable> lastRef = this.played.get(this.played.size() - 1);
		    if ( lastRef != null )
		    {
			lastPlayable = lastRef.get();
		    }
		}
		if ( lastPlayable != next )
		{
		    this.played.add(new WeakReference<Playable>(next));
		    if ( this.playedPosition >= 0 )
		    {
			this.playedPosition ++;
		    }
		}
	    }
	    else
	    {
		/* replace at position if valid */
		this.played.set(this.playedPosition, new WeakReference<Playable>(next));
	    }
		    
	    /** go to the top if so */
	    if ( this.playedPosition >= this.played.size() - 1 )
	    {
		this.playedPosition = -1;
	    }
	}
	
        if ( logger.isDebugEnabled(  ) )
        {
            logger.debug( "end of calling goToNextPlayable(" + current + ", " + shuffle + ", " + repeatMode + ")" );
        }
    }

    /** return true if the given Playable is the last playable<br>
     *        this method is called when repeat is none to determine if the session must close or not
     *        @param playable a Playable
     *        @return true if the given Playable is the last playable
     */
    protected boolean isLastPlayable( Playable playable )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling isLastPlayable(" + playable + ")");
	}
        boolean result = playable == this.getLastPlayable(  );
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling isLastPlayable(" + playable + ") returns " + result);
	}
	return result;
    }

    /** return true if the given Playable is the first playable<br>
     *        this method is called when repeat is none to determine if the session must close or not
     *        @param playable a Playable
     *        @return true if the given Playable is the first playable
     */
    protected boolean isFirstPlayable( Playable playable )
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling isFirstPlayable(" + playable + ")");
	}
        boolean result = playable == this.getFirstPlayable(  );
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling isFirstPlayable(" + playable + ") returns " + result);
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
	    logger.debug("calling dispose");
	}
        this.setCurrentPlayable( null );

        if ( this.played != null )
        {
            this.played.clear(  );
        }

        this.playedPosition = -1;
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling dispose");
	}
    }

    /** return the last playable for this session<br>
     *        @return the last Playable
     */
    protected abstract Playable getLastPlayable(  );

    /** return the next playable without considering shuffle or repeat mode
     *        @param playable a Playable
     *        @return a Playable
     */
    protected abstract Playable getNextPlayable( Playable playable );

    /** return the first playable for this session
     *        @return the first Playable
     */
    protected abstract Playable getFirstPlayable(  );

    /** return the previous playable without considering shuffle or repeat mode
     *        @param playable a Playable
     *        @return a Playable
     */
    protected abstract Playable getPreviousPlayable( Playable playable );

    /** return a random playable for this session<br>
     *        @return a random Playable
     */
    protected abstract Playable getRandomPlayable(  );
}
