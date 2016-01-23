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
import org.siberia.type.annotation.bean.Bean;

import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.Date;

/**
 *
 * define a song
 *
 * @author alexis
 */
@Bean( 
        name = "song item", internationalizationRef = "org.blackdog.rc.i18n.type.SongItem", expert = false, hidden = true, preferred = true, propertiesClassLimit = Object.class, methodsClassLimit = Object.class
     )
public interface SongItem
    extends SibType,
            CategorizedItem,
            Playable,
            TimeBasedItem
{
    /** property song title */
    public static final String PROPERTY_TITLE = "title";

    /** property extension */
    public static final String PROPERTY_EXTENSION = "extension";

    /** property name */
    public static final String PROPERTY_ARTIST = "artist";

    /** album name */
    public static final String PROPERTY_ALBUM = "album";

    /** author */
    public static final String PROPERTY_AUTHOR = "author";

    /** lead artist */
    public static final String PROPERTY_LEAD_ARTIST = "leadArtist";

    /** comment */
    public static final String PROPERTY_COMMENT = "comment";

    /** lyrics */
    public static final String PROPERTY_LYRICS = "lyrics";

    /** track number */
    public static final String PROPERTY_TRACK_NUMBER = "trackNumber";

    /** year released */
    public static final String PROPERTY_YEAR_RELEASED = "yearReleased";

    /** bitrate */
    public static final String PROPERTY_BITRATE       = "bitrate";

    /** set the url of the song item
     *  @param url an URL
     */
    public void setValue( URL url )
                  throws PropertyVetoException;

    /** return the url
     *  @return an URL
     */
    public URL getValue(  );

    /** set the extension for the file
     *        @param extension a String representing the extension (example : 'mp3', 'ogg', ...)
     */
    public void setExt( String extension );

    /** return the extension for the file
     *        @return a String representing the extension (example : 'mp3', 'ogg', ...)
     */
    public String getExt(  );

    /** return the title of the item
     *  @return the title
     */
    public String getTitle(  );

    /** initialize the title of the item
     *  @param artist the title
     *
     *  @throws PropertyVetoException
     */
    public void setTitle( String title )
                  throws PropertyVetoException;

    /** return the artist of the item
     *  @return the artist
     */
    public String getArtist(  );

    /** initialize the artist of the item
     *  @param artist the name of the artist
     *
     *  @throws PropertyVetoException
     */
    public void setArtist( String artist )
                   throws PropertyVetoException;

    /** return the album of the item
     *  @return the album
     */
    public String getAlbum(  );

    /** initialize the album of the item
     *  @param album the name of the album
     *
     *  @throws PropertyVetoException
     */
    public void setAlbum( String album )
                  throws PropertyVetoException;

    /** return the author of the item
     *  @return the author
     */
    public String getAuthor(  );

    /** initialize the author of the item
     *  @param author the name of the author
     *
     *  @throws PropertyVetoException
     */
    public void setAuthor( String author )
                   throws PropertyVetoException;

    /** return the lead artist of the item
     *  @return the lead artist
     */
    public String getLeadArtist(  );

    /** initialize the lead artist of the item
     *  @param artist the lead artist
     *
     *  @throws PropertyVetoException
     */
    public void setLeadArtist( String artist )
                       throws PropertyVetoException;

    /** return the comment associated with the item
     *  @return the comment
     */
    public String getComment(  );

    /** initialize the comment of the item
     *  @param comment the comment
     *
     *  @throws PropertyVetoException
     */
    public void setComment( String comment )
                    throws PropertyVetoException;

    /** return the lyrics in a html format associated of the item
     *  @return the lyrics in an html format
     */
    public String getLyrics(  );

    /** initialize the lyrics in an html format of the item
     *  @param lyrics the lyrics in an html format
     *
     *  @throws PropertyVetoException
     */
    public void setLyrics( String lyrics )
                   throws PropertyVetoException;

    /** return the track number of the item in its album
     *  @return the track number of the item in its album
     */
    public Integer getTrackNumber(  );

    /** initialize the track number of the item in its album
     *  @param trackNumber the track number of the item in its album
     *
     *  @throws PropertyVetoException
     */
    public void setTrackNumber( Integer trackNumber )
                        throws PropertyVetoException;

    /** return the release year of this item
     *  @return the release year of this item
     */
    public Integer getYear(  );

    /** initialize the release year of this item
     *  @param year the release year of this item
     *
     *  @throws PropertyVetoException
     */
    public void setYear( Integer year )
                 throws PropertyVetoException;

    public long getAudioBytesLength(  );

    public void setAudioBytesLength( long audioBytesLength );
    
    /** set the bitrate for this item
     *	@param bitRate
     */
    public void setBitrate(Integer bitRate) throws PropertyVetoException;
    
    /** return the bitrate for this item
     *	@return bitRate
     */
    public Integer getBitrate();
}
