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
package org.blackdog.report;

import org.blackdog.type.SongItem;

/**
 *
 * Represents an information in an update tags report
 *
 * @author alexis
 */
public class TagsUpdateLog
{
    /**
     * song item related to this action
     */
    private SongItem songItem = null;

    /** message */
    private String message = null;

    /** log level */
    private Status status = Status.DEBUG;

    /** throwable */
    private Throwable throwable = null;

    /** Creates a new instance of TagsUpdateLog
     *        @param songItem the SongItem related to the update action
     *        @param message the message
     *        @param status the status of the log
     *        @param throwable a Throwable
     */
    public TagsUpdateLog( SongItem item, String message, Status status, Throwable throwable )
    {
        if ( item == null )
        {
            throw new IllegalArgumentException( "a non null item has to be provided" );
        }

        if ( status == null )
        {
            throw new IllegalArgumentException( "a non null status has to be provided" );
        }

        this.songItem = item;
        this.message = message;
        this.status = status;
        this.throwable = throwable;
    }

    /** return a SongItem
     *        @return a SongItem
     */
    public SongItem getSongItem(  )
    {
        return songItem;
    }

    /** return a message
     *        @return a String
     */
    public String getMessage(  )
    {
        return message;
    }

    /** return the status of the log
     *        @return a Status
     */
    public Status getStatus(  )
    {
        return status;
    }

    /** return the throwable of the log
     *        @return a Throwable
     */
    public Throwable getThrowable(  )
    {
        return this.throwable;
    }

    /** define log status */
    public static enum Status
    {DEBUG,
        INFO,
        WARNING,
        ERROR;
    }
}
