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
package org.blackdog.type.info;

import org.blackdog.type.MpPlusSongItem;

/**
 *
 *  BeanInfo for MpPlusSongItem.
 *  This kind of BeanInfo allow to deal with annotations
 *
 * @author alexis
 */
public class MpPlusSongItemBeanInfo extends DefaultSongItemBeanInfo
{
    /** create a new MpPlusSongItemBeanInfo */
    public MpPlusSongItemBeanInfo(  )
    {
        this( MpPlusSongItem.class );
    }

    /** create a new MpPlusSongItemBeanInfo */
    protected MpPlusSongItemBeanInfo( Class c )
    {
        super( c );
    }
}
