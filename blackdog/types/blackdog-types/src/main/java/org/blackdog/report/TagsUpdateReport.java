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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author alexis
 */
public class TagsUpdateReport
{
    /** list of TagsUpdateLog */
    private List<TagsUpdateLog> logs = new ArrayList<TagsUpdateLog>(  );

    /** Creates a new instance of TagsUpdateReport */
    public TagsUpdateReport(  )
    {
    }

    /** return an iterator over TagsUpdateLog
     *        @return an iterator
     */
    public Iterator<TagsUpdateLog> logs(  )
    {
        return this.logs.iterator(  );
    }

    /** return the number of logs contained by the report
     *        @return an integer
     */
    public int getLogsCount(  )
    {
        return this.logs.size(  );
    }

    /** returns true if the report contains a log which status is equals or greater to the given status
     *        @param statusLimit a TagsUpdateLog.Status
     *        @return true if the report contains a log which status is equals or greater to the given status
     */
    public boolean containsLogWithStatusHigherOrEqualsTo( TagsUpdateLog.Status statusLimit )
    {
        return this.containsLogWithStatusHigherOrEqualsTo( null, statusLimit );
    }

    /** returns true if the report contains a log which status is equals or greater to the given status
     *        @param songItem a SongItem to filter for this SongItem or null to consider all logs for all SongItems
     *        @param statusLimit a TagsUpdateLog.Status
     *        @return true if the report contains a log which status is equals or greater to the given status
     */
    public boolean containsLogWithStatusHigherOrEqualsTo( SongItem songItem, TagsUpdateLog.Status statusLimit )
    {
        boolean result = false;

        if ( ( this.logs != null ) && ( statusLimit != null ) )
        {
            for ( int i = 0; i < this.logs.size(  ); i++ )
            {
                TagsUpdateLog currentLog = this.logs.get( i );

                if ( ( currentLog != null ) && ( statusLimit.compareTo( currentLog.getStatus(  ) ) <= 0 ) )
                {
                    if ( ( songItem == null ) || ( songItem == currentLog.getSongItem(  ) ) )
                    {
                        result = true;

                        break;
                    }
                }
            }
        }

        return result;
    }

    /** add a new log
     *        @param log a TagsUpdateLog
     */
    public void addLog( TagsUpdateLog log )
    {
        if ( log != null )
        {
            this.logs.add( log );
        }
    }

    /** add a new debug log
     *        @param songItem the SongItem related to the update action
     *        @param message the message
     */
    public void addDebugLog( SongItem item, String message )
    {
        this.addLog( item, message, TagsUpdateLog.Status.DEBUG );
    }

    /** add a new error log
     *        @param songItem the SongItem related to the update action
     *        @param message the message
     *        @param throwable a Throwable
     */
    public void addErrorLog( SongItem item, String message, Throwable throwable )
    {
        this.addLog( item, message, TagsUpdateLog.Status.ERROR, throwable );
    }

    /** add a new log
     *        @param songItem the SongItem related to the update action
     *        @param message the message
     *        @param status the status of the log
     */
    public void addLog( SongItem item, String message, TagsUpdateLog.Status status )
    {
        this.addLog( item, message, status, null );
    }

    /** add a new log
     *        @param songItem the SongItem related to the update action
     *        @param message the message
     *        @param status the status of the log
     *        @param throwable a Throwable
     */
    public void addLog( SongItem item, String message, TagsUpdateLog.Status status, Throwable throwable )
    {
        TagsUpdateLog log = new TagsUpdateLog( item, message, status, throwable );

        this.addLog( log );
    }
}
