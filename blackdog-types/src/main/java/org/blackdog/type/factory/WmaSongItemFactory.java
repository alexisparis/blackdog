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
package org.blackdog.type.factory;

import org.apache.log4j.Logger;

import org.blackdog.report.TagsUpdateReport;

import org.blackdog.type.SongItem;

import java.net.URL;
import org.blackdog.type.WmaSongItem;

/**
 *
 * Define a song item factory for wma files
 *
 * @author alexis
 */
public class WmaSongItemFactory extends DefaultTaggedSongItemFactory
{
    /** logger */
    private transient Logger logger = Logger.getLogger( WmaSongItemFactory.class );

    /** Creates a new instance of WmaSongItemFactory */
    public WmaSongItemFactory(  )
    {	}

    /** create the correct type of SongItem according to url
     *  the url should not be used to configure the new SongItem in this method but should only
     *  be used to determine the kind of SongItem to create.
     *  @param url the url that will be used by createSongItem to configure the returned SongItem
     *        @param report a TagsUpdateReport
     *  @return a SongItem
     */
    protected SongItem _createNewSongItem( URL url, TagsUpdateReport report )
    {
        return new WmaSongItem();
    }
}
