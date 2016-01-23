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
package org.blackdog.type;

import org.siberia.type.SibType;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 *
 * @author alexis
 */
public interface Playable
    extends SibType
{
    /** property count of times the item was played */
    public static final String PROPERTY_PLAYED_COUNT = "org.blackdog.type.Playable.playedCount";

    /** property date when the item was last played  */
    public static final String PROPERTY_DATE_LAST_PLAYED = "org.blackdog.type.Playable.dateLastPlayed";

    /** return an InputStream
     *  @return an InputStream
     *
     *  @exception IOException if the creation failed
     */
    public InputStream createInputStream(  )
                                  throws IOException;

    /** return the number of bytes that represents the playable
     *        @return a Long or -1 if this cannot be found
     */
    public long getBytesLength(  );

    /** return the extension of the item
     *  @return a String that does not contains '.' (example : 'mp3', 'ogg', etc..)
     */
    public String getExtension(  );

    /** return the name of the playable item
     *  @return the name
     */
    public String getPlayableName(  );

    /** return the number of play for this item
     *        @return an integer that represents the number of play for this item
     */
    public int getCountPlayed(  );

    /** initialize the number of play for this item
     *        @param playCount an integer that represents the number of play for this item
     */
    public void setCountPlayed( int playCount )
                        throws PropertyVetoException;

    /** return a Date that represents the last time the item was wanted to be played
     *        @return a Date
     */
    public Date getLastTimePlayed(  );

    /** initialize the Date that represents the last time the item was wanted to be played
     *        @param date a Date
     */
    public void setLastTimePlayed( Date date )
                           throws PropertyVetoException;
}
