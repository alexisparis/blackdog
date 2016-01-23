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

import org.blackdog.type.base.AudioDuration;

import java.beans.PropertyVetoException;
import java.util.Date;

/**
 *
 * Property of something that has a duration
 *
 *
 * @author alexis
 */
public interface TimeBasedItem
{
    /** property duration */
    public static final String PROPERTY_DURATION = "org.blackdog.type.TimeBasedItem.duration";

    /** property duration verified */
    public static final String PROPERTY_DURATION_VERIFIED = "org.blackdog.type.TimeBasedItem.durationVerified";

    /** property creation date */
    public static final String PROPERTY_CREATION_DATE = "org.blackdog.type.TimeBasedItem.creationDate";

    /** set the duration for this item
     *        @param duration an AudioDuration
     */
    public void setDuration( AudioDuration duration )
                     throws PropertyVetoException;

    /** return the duration for this item
     *        @return an AudioDuration or null if the duration is not specified
     */
    public AudioDuration getDuration(  );

    /** indicate if the duration is verified
     *        @param verified true if the duration is verified
     */
    public void setDurationVerified( boolean verified )
                             throws PropertyVetoException;

    /** indicate if the duration is verified
     *        @return true if the duration is verified
     */
    public boolean isDurationVerified(  );

    /** return a Date that represents the date of creation of this item
     *        @return a Date
     */
    public Date getCreationDate(  );

    /** initialize the Date that represents the date when the item was created
     *        @param date a Date
     */
    public void setCreationDate( Date date )
                         throws PropertyVetoException;
}
