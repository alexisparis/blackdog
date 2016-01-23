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

import org.blackdog.report.TagsUpdateReport;

import org.blackdog.type.SongItem;

import org.siberia.type.SibType;

import java.beans.PropertyVetoException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 *
 * Define an abstract SongItem factory
 *
 * @author alexis
 */
public abstract class AbstractSongItemFactory
    implements SongItemFactory
{
    /** Creates a new instance of AbstractSongItemFactory */
    public AbstractSongItemFactory(  )
    {
    }

    /**
     * create a new SongItem according to the given url
     *
     * @param url an URL
     *        @param report a TagsUpdateReport
     * @return a SongItem
     * @exception SongItemCreationException if error occured when trying to create the SongItem
     */
    public SongItem createSongItem( URL url, TagsUpdateReport report )
                            throws SongItemCreationException
    {
        if ( url == null )
        {
            throw new SongItemCreationException( new IllegalArgumentException( "url cannot be null" ), url, this );
        }

        SongItem item = this.createNewSongItem( url, report );

        if ( item == null )
        {
            throw new SongItemCreationException( new Exception( "the SongItem returned by method createNewSongItem was null" ),
                                                 url, this );
        }

        try
        {
            String simpleName = null;

            if ( item instanceof SibType )
            {
                String extension = null;

                String file = url.getFile(  );

                if ( file != null )
                {
                    /** try to create a significant name according to the file name but without extension */
                    simpleName = file;

                    int lastSlashPosition = simpleName.lastIndexOf( '/' );

                    if ( lastSlashPosition != -1 )
                    {
                        simpleName = simpleName.substring( lastSlashPosition + 1 );
                    }

                    int lastPointIndex = simpleName.lastIndexOf( '.' );

                    if ( lastPointIndex != -1 )
                    {
                        extension = simpleName.substring( lastPointIndex + 1 );

                        simpleName = simpleName.substring( 0, lastPointIndex );
                    }

                    try
                    {
                        Charset charset = Charset.forName( "UTF-8" );
                        simpleName = new String( simpleName.getBytes(  ),
                                                 charset );
                    } catch ( Exception e )
                    {
                        e.printStackTrace(  );
                    }
                }

                if ( extension == null )
                {
                    extension = "unknown";
                }

                if ( simpleName != null )
                {
                    simpleName = simpleName.replaceAll( "%20", " " );

                    /* set the name */
                    ( (SibType) item ).setName( simpleName );
                    item.setExt( extension );
                }

                ( (SibType) item ).setNameCouldChange( false );
            }
        } catch ( PropertyVetoException ex )
        {
            throw new SongItemCreationException( ex, url, this );
        }

        /** if title is not set, then use the url to set a custom title */
        String title = item.getTitle(  );

        if ( ( title == null ) || ( title.trim(  ).length(  ) == 0 ) )
        {
            String customTitle = null;

            try
            {
                customTitle = url.toURI(  ).getPath(  );

                if ( customTitle != null )
                {
                    int lastPointIndex = customTitle.lastIndexOf( '.' );

                    if ( lastPointIndex != -1 )
                    {
                        customTitle = customTitle.substring( 0, lastPointIndex );

                        // TODO --> even on a win os ??
                        int lastSeparatorIndex = customTitle.lastIndexOf( '/' );

                        if ( lastSeparatorIndex != -1 )
                        {
                            customTitle = customTitle.substring( lastSeparatorIndex + 1 );
                        }
                    }
                }
            } catch ( URISyntaxException ex )
            {
                ex.printStackTrace(  );
            }

            if ( customTitle != null )
            {
                try
                {
                    item.setTitle( customTitle );
                } catch ( PropertyVetoException e )
                {
                    e.printStackTrace(  );
                }
            }
        }

        return item;
    }

    /** create the correct type of SongItem according to url
     *  the url should not be used to configure the new SongItem in this method but should only
     *  be used to determine the kind of SongItem to create.
     *  @param url the url that will be used by createSongItem to configure the returned SongItem
     *        @param report a TagsUpdateReport
     *  @return a SongItem
     */
    protected abstract SongItem _createNewSongItem( URL url, TagsUpdateReport report );

    /** create the correct type of SongItem according to url
     *  the url should not be used to configure the new SongItem in this method but should only
     *  be used to determine the kind of SongItem to create.
     *  @param url the url that will be used by createSongItem to configure the returned SongItem
     *        @param report a TagsUpdateReport
     *  @return a SongItem
     */
    protected final SongItem createNewSongItem( URL url, TagsUpdateReport report )
    {
        SongItem item = this._createNewSongItem( url, report );

        try
        {
            item.setValue( url );
        } catch ( PropertyVetoException e )
        {
            report.addErrorLog( item, "unable to set the value '" + url + "' of the SongItem", e );
        }

        this.completeInformation( item, report );

        return item;
    }

    /** complete initialization of a SongItem<br>
     *        @param item a SongItem
     *        @param report a TagsUpdateReport
     */
    protected void completeInformation( SongItem item, TagsUpdateReport report )
    {
        /* to override */
    }
}
