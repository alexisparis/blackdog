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

import java.beans.PropertyVetoException;

/**
 *
 * @author alexis
 */
public interface CategorizedItem
{
    /** name of the property category */
    public static final String PROPERTY_CATEGORY = "category";

    /** return the category of the item
     *  @return an Item of AudioCategory enumeration
     */
    public AudioCategory getCategory(  );

    /** initialize the category of the item
     *  @param category an Item of AudioCategory enumeration
     *
     *  @throws PropertyVetoException
     */
    public void setCategory( AudioCategory category )
                     throws PropertyVetoException;
}
