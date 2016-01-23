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

import org.siberia.type.SibURL;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 *
 * Abstract implementation of a song
 *
 * @author alexis
 */
@Bean(
name = "song item",
	internationalizationRef = "org.blackdog.rc.i18n.type.AbstractSongItem",
	expert = false,
	hidden = true,
	preferred = true,
	propertiesClassLimit = Object.class,
	methodsClassLimit = Object.class
	)
	public abstract class AbstractSongItem extends AudioItem implements SongItem
{
    /** Creates a new instance of AudioItem */
    public AbstractSongItem(  )
    {
    }
    
    /** album */
    @BeanProperty(
    name = PROPERTY_ALBUM, internationalizationRef = "org.blackdog.rc.i18n.property.AbstractSongItem_album", expert = false, hidden = false, preferred = true, bound = true, constrained = true, writeMethodName = "setAlbum", writeMethodParametersClass =
    {
	String.class}
    , readMethodName = "getAlbum", readMethodParametersClass =
    {
    }
    
    )
    private String album = null;
    
    /** artist */
    @BeanProperty(
    name = PROPERTY_ARTIST, internationalizationRef = "org.blackdog.rc.i18n.property.AbstractSongItem_artist", expert = false, hidden = false, preferred = true, bound = true, constrained = true, writeMethodName = "setArtist", writeMethodParametersClass =
    {
	String.class}
    , readMethodName = "getArtist", readMethodParametersClass =
    {
    }
    
    )
    private String artist = null;
    
    /** title */
    @BeanProperty(
    name = PROPERTY_TITLE, internationalizationRef = "org.blackdog.rc.i18n.property.AbstractSongItem_title", expert = false, hidden = false, preferred = true, bound = true, constrained = true, writeMethodName = "setTitle", writeMethodParametersClass =
    {
	String.class}
    , readMethodName = "getTitle", readMethodParametersClass =
    {
    }
    
    )
    private String title = null;
    
    /** author */
    @BeanProperty(
    name = PROPERTY_AUTHOR, internationalizationRef = "org.blackdog.rc.i18n.property.AbstractSongItem_author", expert = false, hidden = false, preferred = true, bound = true, constrained = true, writeMethodName = "setAuthor", writeMethodParametersClass =
    {
	String.class}
    , readMethodName = "getAuthor", readMethodParametersClass =
    {
    }
    
    )
    private String author = null;
    
    /** lead artist */
    @BeanProperty(
    name = PROPERTY_LEAD_ARTIST, internationalizationRef = "org.blackdog.rc.i18n.property.AbstractSongItem_leadArtist", expert = false, hidden = false, preferred = true, bound = true, constrained = true, writeMethodName = "setLeadArtist", writeMethodParametersClass =
    {
	String.class}
    , readMethodName = "getLeadArtist", readMethodParametersClass =
    {
    }
    
    )
    private String leadArtist = null;
    
    /** comment */
    @BeanProperty(
    name = PROPERTY_COMMENT, internationalizationRef = "org.blackdog.rc.i18n.property.AbstractSongItem_comment", expert = false, hidden = false, preferred = true, bound = true, constrained = true, writeMethodName = "setComment", writeMethodParametersClass =
    {
	String.class}
    , readMethodName = "getComment", readMethodParametersClass =
    {
    }
    
    )
    private String comment = null;
    
    /** lyrics */
    @BeanProperty(
    name = PROPERTY_LYRICS, internationalizationRef = "org.blackdog.rc.i18n.property.AbstractSongItem_lyrics", expert = false, hidden = true, preferred = true, bound = true, constrained = true, writeMethodName = "setLyrics", writeMethodParametersClass =
    {
	String.class}
    , readMethodName = "getLyrics", readMethodParametersClass =
    {
    }
    
    )
    private String lyrics = null;
    
    /** track number */
    @BeanProperty(
    name = PROPERTY_TRACK_NUMBER, internationalizationRef = "org.blackdog.rc.i18n.property.AbstractSongItem_trackNumber", expert = false, hidden = false, preferred = true, bound = true, constrained = true, writeMethodName = "setTrackNumber", writeMethodParametersClass =
    {
	Integer.class}
    , readMethodName = "getTrackNumber", readMethodParametersClass =
    {
    }
    
    )
    private Integer track = null;
    
    /** year */
    @BeanProperty(
    name = PROPERTY_YEAR_RELEASED, internationalizationRef = "org.blackdog.rc.i18n.property.AbstractSongItem_year", expert = false, hidden = false, preferred = true, bound = true, constrained = true, writeMethodName = "setYear", writeMethodParametersClass =
    {
	Integer.class}
    , readMethodName = "getYear", readMethodParametersClass =
    {
    }
    
    )
    private Integer year = null;
    
    /** extension */
    @BeanProperty(
    name = PROPERTY_EXTENSION, internationalizationRef = "org.blackdog.rc.i18n.property.AbstractSongItem_extension", expert = true, hidden = true, preferred = true, bound = true, constrained = true, writeMethodName = "setExt", writeMethodParametersClass =
    {
	String.class}
    , readMethodName = "getExt", readMethodParametersClass =
    {
    }
    
    )
    private String extension = null;
    
    /** bitrate */
    @BeanProperty(
	    name = PROPERTY_BITRATE, 
	    internationalizationRef = "org.blackdog.rc.i18n.property.AbstractSongItem_bitrate", 
	    expert = true, 
	    hidden = true, 
	    preferred = true, 
	    bound = true, 
	    constrained = true, 
	    writeMethodName = "setBitrate", 
	    writeMethodParametersClass ={Integer.class}, 
	    readMethodName = "getBitrate", 
	    readMethodParametersClass =
    {
    }
    
    )
    private Integer bitRate = null;
    
    /** set the extension for the file
     *        @param extension a String representing the extension (example : 'mp3', 'ogg', ...)
     */
    public void setExt( String extension )
    {
	if ( ! org.siberia.base.LangUtilities.equals( this.getExtension(  ),
		extension ) )
	{
//	    new Exception("settting extension : " + extension).printStackTrace();
	    String oldExtension = this.getExtension(  );
	    
	    this.extension = extension;
	    
	    this.firePropertyChange( PROPERTY_EXTENSION,
		    oldExtension,
		    this.getExtension(  ) );
	}
    }
    
    /** return the extension for the file
     *        @return a String representing the extension (example : 'mp3', 'ogg', ...)
     */
    public String getExt(  )
    {
	return this.extension;
    }
    
    /** return the album of the item
     *  @return the album
     */
    public String getAlbum(  )
    {
	return this.album;
    }
    
    /** initialize the album of the item
     *  @param album the name of the album
     *
     *  @throws PropertyVetoException
     */
    public void setAlbum( String album )
    throws PropertyVetoException
    {
	if ( ! org.siberia.base.LangUtilities.equals( this.getAlbum(  ),
		album ) )
	{
	    this.fireVetoableChange( PROPERTY_ALBUM,
		    this.getAlbum(  ),
		    album );
	    
	    this.checkReadOnlyProperty( PROPERTY_ALBUM,
		    this.getAlbum(  ),
		    album );
	    
	    String oldAlbum = this.getAlbum(  );
	    
	    this.album = album;
	    
	    this.albumChanged(  );
	    
	    this.firePropertyChange( PROPERTY_ALBUM,
		    oldAlbum,
		    this.getAlbum(  ) );
	}
    }
    
    /** method called when property album has been changed */
    protected void albumChanged(  )
    {
	/* to override */
    }
    
    /** return the artist of the item
     *  @return the artist
     */
    public String getArtist(  )
    {
	return this.artist;
    }
    
    /** initialize the artist of the item
     *  @param artist the name of the artist
     *
     *  @throws PropertyVetoException
     */
    public void setArtist( String artist )
    throws PropertyVetoException
    {
	if ( ! org.siberia.base.LangUtilities.equals( this.getArtist(  ),
		artist ) )
	{
	    this.fireVetoableChange( PROPERTY_ARTIST,
		    this.getArtist(  ),
		    artist );
	    
	    this.checkReadOnlyProperty( PROPERTY_ARTIST,
		    this.getArtist(  ),
		    artist );
	    
	    String oldArtist = this.getArtist(  );
	    
	    this.artist = artist;
	    
	    this.artistChanged(  );
	    
	    this.firePropertyChange( PROPERTY_ARTIST,
		    oldArtist,
		    this.getArtist(  ) );
	}
    }
    
    /** method called when property artist has been changed */
    protected void artistChanged(  )
    {
	/* to override */
    }
    
    /** return the title of the item
     *  @return the title
     */
    public String getTitle(  )
    {
	return this.title;
    }
    
    /** initialize the title of the item
     *  @param artist the title
     *
     *  @throws PropertyVetoException
     */
    public void setTitle( String title )
    throws PropertyVetoException
    {
	if ( ! org.siberia.base.LangUtilities.equals( this.getTitle(  ),
		title ) )
	{
	    this.fireVetoableChange( PROPERTY_TITLE,
		    this.getTitle(  ),
		    title );
	    
	    this.checkReadOnlyProperty( PROPERTY_TITLE,
		    this.getTitle(  ),
		    title );
	    
	    String oldTitle = this.getTitle(  );
	    
	    this.title = title;
	    
	    this.titleChanged(  );
	    
	    this.firePropertyChange( PROPERTY_TITLE,
		    oldTitle,
		    this.getTitle(  ) );
	}
    }
    
    /** method called when property title has been changed */
    protected void titleChanged(  )
    {
	/* to override */
    }
    
    /** return the author of the item
     *  @return the author
     */
    public String getAuthor(  )
    {
	return this.author;
    }
    
    /** initialize the author of the item
     *  @param author the name of the author
     *
     *  @throws PropertyVetoException
     */
    public void setAuthor( String author )
    throws PropertyVetoException
    {
	if ( ! org.siberia.base.LangUtilities.equals( this.getAuthor(  ),
		author ) )
	{
	    this.fireVetoableChange( PROPERTY_AUTHOR,
		    this.getAuthor(  ),
		    author );
	    
	    this.checkReadOnlyProperty( PROPERTY_AUTHOR,
		    this.getAuthor(  ),
		    author );
	    
	    String oldAuthor = this.getAuthor(  );
	    
	    this.author = author;
	    
	    this.authorChanged(  );
	    
	    this.firePropertyChange( PROPERTY_AUTHOR,
		    oldAuthor,
		    this.getAuthor(  ) );
	}
    }
    
    /** method called when property author has been changed */
    protected void authorChanged(  )
    {
	/* to override */
    }
    
    /** return the lead artist of the item
     *  @return the lead artist
     */
    public String getLeadArtist(  )
    {
	return this.leadArtist;
    }
    
    /** initialize the lead artist of the item
     *  @param artist the lead artist
     *
     *  @throws PropertyVetoException
     */
    public void setLeadArtist( String artist )
    throws PropertyVetoException
    {
	if ( ! org.siberia.base.LangUtilities.equals( this.getLeadArtist(  ),
		artist ) )
	{
	    this.fireVetoableChange( PROPERTY_LEAD_ARTIST,
		    this.getLeadArtist(  ),
		    artist );
	    
	    this.checkReadOnlyProperty( PROPERTY_LEAD_ARTIST,
		    this.getLeadArtist(  ),
		    artist );
	    
	    String oldLeadArtist = this.getLeadArtist(  );
	    
	    this.leadArtist = artist;
	    
	    this.leadArtistChanged(  );
	    
	    this.firePropertyChange( PROPERTY_LEAD_ARTIST,
		    oldLeadArtist,
		    this.getLeadArtist(  ) );
	}
    }
    
    /** method called when property lead artist has been changed */
    protected void leadArtistChanged(  )
    {
	/* to override */
    }
    
    /** return the comment associated with the item
     *  @return the comment
     */
    public String getComment(  )
    {
	return this.comment;
    }
    
    /** initialize the comment of the item
     *  @param comment the comment
     *
     *  @throws PropertyVetoException
     */
    public void setComment( String comment )
    throws PropertyVetoException
    {
	if ( ! org.siberia.base.LangUtilities.equals( this.getComment(  ),
		comment ) )
	{
	    this.fireVetoableChange( PROPERTY_COMMENT,
		    this.getComment(  ),
		    comment );
	    
	    this.checkReadOnlyProperty( PROPERTY_COMMENT,
		    this.getComment(  ),
		    comment );
	    
	    String oldComment = this.getComment(  );
	    
	    this.comment = comment;
	    
	    this.commentChanged(  );
	    
	    this.firePropertyChange( PROPERTY_COMMENT,
		    oldComment,
		    this.getComment(  ) );
	}
    }
    
    /** method called when property comment has been changed */
    protected void commentChanged(  )
    {
	/* to override */
    }
    
    /** return the lyrics in a html format associated of the item
     *  @return the lyrics in an html format
     */
    public String getLyrics(  )
    {
	return this.lyrics;
    }
    
    /** initialize the lyrics in an html format of the item
     *  @param lyrics the lyrics in an html format
     *
     *  @throws PropertyVetoException
     */
    public void setLyrics( String lyrics )
    throws PropertyVetoException
    {
	if ( ! org.siberia.base.LangUtilities.equals( this.getLyrics(  ),
		lyrics ) )
	{
	    this.fireVetoableChange( PROPERTY_LYRICS,
		    this.getLyrics(  ),
		    lyrics );
	    
	    this.checkReadOnlyProperty( PROPERTY_LYRICS,
		    this.getLyrics(  ),
		    lyrics );
	    
	    String oldLyrics = this.getLyrics(  );
	    
	    this.lyrics = lyrics;
	    
	    this.lyricsChanged(  );
	    
	    this.firePropertyChange( PROPERTY_LYRICS,
		    oldLyrics,
		    this.getLyrics(  ) );
	}
    }
    
    /** method called when property lyrics has been changed */
    protected void lyricsChanged(  )
    {
	/* to override */
    }
    
    /** return the track number of the item in its album
     *  @return the track number of the item in its album
     */
    public Integer getTrackNumber(  )
    {
	return this.track;
    }
    
    /** initialize the track number of the item in its album
     *  @param trackNumber the track number of the item in its album
     *
     *  @throws PropertyVetoException
     */
    public void setTrackNumber( Integer trackNumber )
    throws PropertyVetoException
    {
	if ( ! org.siberia.base.LangUtilities.equals( this.getTrackNumber(  ),
		trackNumber ) )
	{
	    this.fireVetoableChange( PROPERTY_TRACK_NUMBER,
		    this.getTrackNumber(  ),
		    trackNumber );
	    
	    this.checkReadOnlyProperty( PROPERTY_TRACK_NUMBER,
		    this.getTrackNumber(  ),
		    trackNumber );
	    
	    Integer oldTrack = this.getTrackNumber(  );
	    
	    this.track = trackNumber;
	    
	    this.trackNumberChanged(  );
	    
	    this.firePropertyChange( PROPERTY_TRACK_NUMBER,
		    oldTrack,
		    this.getTrackNumber(  ) );
	}
    }
    
    /** method called when property track number has been changed */
    protected void trackNumberChanged(  )
    {
	/* to override */
    }
    
    /** return the release year of this item
     *  @return the release year of this item
     */
    protected Integer getYearReleased(  )
    {
	return this.getYear(  );
    }
    
    /** initialize the release year of this item
     *  @param year the release year of this item
     *
     *  @throws PropertyVetoException
     */
    protected void setYearReleased( Integer year )
    throws PropertyVetoException
    {
	this.setYear( year );
    }
    
    /** return the release year of this item
     *  @return the release year of this item
     */
    public Integer getYear(  )
    {
	return this.year;
    }
    
    /** initialize the release year of this item
     *  @param year the release year of this item
     *
     *  @throws PropertyVetoException
     */
    public void setYear( Integer year )
    throws PropertyVetoException
    {
	if ( ! org.siberia.base.LangUtilities.equals( this.getYear(  ),
		year ) )
	{
	    this.fireVetoableChange( PROPERTY_YEAR_RELEASED,
		    this.getYear(  ),
		    year );
	    
	    this.checkReadOnlyProperty( PROPERTY_YEAR_RELEASED,
		    this.getYear(  ),
		    year );
	    
	    Integer oldYear = this.getYear(  );
	    
	    this.year = year;
	    
	    this.yearChanged(  );
	    
	    this.firePropertyChange( PROPERTY_YEAR_RELEASED,
		    oldYear,
		    this.getYear(  ) );
	}
    }
    
    /** method called when property year has been changed */
    protected void yearChanged(  )
    {
	/* to override */
    }
    
    /** set the bitrate for this item
     *	@param bitRate
     */
    public void setBitrate(Integer bitRate) throws PropertyVetoException
    {
	if ( ! org.siberia.base.LangUtilities.equals( this.getBitrate(), bitRate) )
	{
	    this.fireVetoableChange( PROPERTY_BITRATE, this.getBitrate(), bitRate);
	    
	    this.checkReadOnlyProperty( PROPERTY_BITRATE, this.getBitrate(), bitRate);
	    
	    Integer oldBitRate = this.getBitrate();
	    
	    this.bitRate = bitRate;
	    
	    this.firePropertyChange( PROPERTY_BITRATE, oldBitRate, this.getBitrate() );
	}
    }
    
    /** return the bitrate for this item
     *	@return bitRate
     */
    public Integer getBitrate()
    {
	return this.bitRate;
    }
}
