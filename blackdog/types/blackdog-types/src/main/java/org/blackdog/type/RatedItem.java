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

import org.blackdog.type.base.Rate;

import java.beans.PropertyVetoException;

/**
 *
 * define an object that has the hability to be rated from 0 to 10
 *
 * @author alexis
 */
public interface RatedItem
{
    /** name of the property rate */
    public static final String PROPERTY_RATE = "org.blackdog.type.RatedItem.rate";

    /** return the rate of this item
     *        @return a Rate or null if the rate is undefined
     */
    public Rate getRate(  );

    /** initialize the rate of this item
     *        @param rate a Rate or null to indicate that the rate of this item is undefined
     *
     *        @exception PropertyVetoException if the value change is not accepted
     */
    public void setRate( Rate rate )
                 throws PropertyVetoException;
}
