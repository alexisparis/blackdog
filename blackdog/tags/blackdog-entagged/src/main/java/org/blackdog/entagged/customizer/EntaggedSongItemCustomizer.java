/*
 * blackdog entagged : define audio customizer and entagger based on entagged
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
package org.blackdog.entagged.customizer;

import entagged.audioformats.AudioFile;
import entagged.audioformats.Tag;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.generic.TagTextField;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.blackdog.type.AudioCategory;
import org.blackdog.type.SongItem;
import org.blackdog.type.base.AudioDuration;
import org.blackdog.type.customizer.SongItemCustomizer;
import org.blackdog.report.TagsUpdateReport;

/**
 *
 * customizer that complete the information of a SongItem
 *  according to entagged libraries
 *
 * @author alexis
 */
public class EntaggedSongItemCustomizer implements SongItemCustomizer
{
    /** logger */
    private Logger logger = Logger.getLogger(EntaggedSongItemCustomizer.class);
    
    /**
     * Creates a new instance of EntaggedSongItemCustomizer
     */
    public EntaggedSongItemCustomizer()
    {
	
    }
    
    /** complete the information of the given SongItem
     *	@param report a TagsUpdateReport
     *	@param item a SongItem
     */
    public void customize(TagsUpdateReport report, final SongItem item)
    {
	if ( item != null )
	{
	    URL url = item.getValue();
	    
	    if ( url == null )
	    {
		report.addDebugLog(item, "no url related to this item --> could not load AudioFile");
	    }
	    else
	    {
		try
		{
		    File f = new File(url.toURI());
		    
		    item.setAudioBytesLength(f.length());
		    
		    AudioFile file = entagged.audioformats.AudioFileIO.read(f);
		    
		    try
		    {
			item.setBitrate(new Integer(file.getBitrate()));
		    }
		    catch(Exception e)
		    {
			String message = "could not set the bitrate for " + item;
			logger.error(message, e);
			report.addErrorLog(item, message, e);
		    }
		    
		    if ( file != null )
		    {
			// duration
			if ( item.getDuration() == null || item.getDuration().getTimeInMilli() <= 0 )
			{
			    float length = file.getLength();
			    
			    if ( length <= 0 )
			    {
				length = file.getPreciseLength();
			    }
			    
			    if ( length > 0 )
			    {
				try
				{
				    item.setDuration(new AudioDuration((long)(length * 1000)));
				}
				catch(PropertyVetoException e)
				{
				    String message = "could not change the duration to '" + length + " seconds' for " + url;
				    logger.error(message, e);
				    report.addErrorLog(item, message, e);
				}
			    }
			}
			
			Tag tag = file.getTag();
			
			if ( tag == null )
			{
			    report.addDebugLog(item, "no tag discovered from " + url);
			}
			else
			{
			    // album
			    if ( item.getAlbum() == null || item.getAlbum().length() == 0 )
			    {
				if ( tag.getFirstAlbum() != null )
				{
				    try
				    {
					item.setAlbum(tag.getFirstAlbum());
				    }
				    catch(PropertyVetoException e)
				    {
					String message = "could not change the album to '" + tag.getFirstAlbum() + "' for " + url;
					logger.error(message, e);
					report.addErrorLog(item, message, e);
				    }
				}
			    }
			    // artist
			    if ( item.getArtist() == null || item.getArtist().length() == 0 )
			    {
				if ( tag.getFirstArtist() != null )
				{
				    try
				    {
					item.setArtist(tag.getFirstArtist());
				    }
				    catch(PropertyVetoException e)
				    {
					String message = "could not change the artist to '" + tag.getFirstArtist() + "' for " + url;
					logger.error(message, e);
					report.addErrorLog(item, message, e);
				    }
				}
			    }
			    // comment
			    if ( item.getComment() == null || item.getComment().length() == 0 )
			    {
				if ( tag.getFirstComment() != null )
				{
				    try
				    {
					item.setComment(tag.getFirstComment());
				    }
				    catch(PropertyVetoException e)
				    {
					String message = "could not change the comment to '" + tag.getFirstComment() + "' for " + url;
					logger.error(message, e);
					report.addErrorLog(item, message, e);
				    }
				}
			    }
			    // genre
			    if ( true )//item.getCategory() == null )
			    {
				if ( tag.getFirstGenre() != null )
				{
				    AudioCategory category = AudioCategory.getAudioCategoryFor(tag.getFirstGenre());
				    
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("get audio category '" + category + "' for '" + tag.getFirstGenre() + "'");
				    }
				    
				    try
				    {
					item.setCategory(category);
				    }
				    catch(PropertyVetoException e)
				    {
					String message = "could not change the category to '" + tag.getFirstGenre() + "' for " + url;
					logger.error(message, e);
					report.addErrorLog(item, message, e);
				    }
				}
			    }
			    // title
			    if ( item.getTitle() == null || item.getTitle().length() == 0 )
			    {
				if ( tag.getFirstTitle() != null )
				{
				    try
				    {
					item.setTitle(tag.getFirstTitle());
				    }
				    catch(PropertyVetoException e)
				    {
					String message = "could not change the title to '" + tag.getFirstTitle() + "' for " + url;
					logger.error(message, e);
					report.addErrorLog(item, message, e);
				    }
				}
			    }
			    // title
			    if ( item.getTrackNumber() == null || item.getTrackNumber().intValue() == 0 )
			    {
				if ( tag.getFirstTrack() != null )
				{
				    try
				    {
					String track = tag.getFirstTrack();
					if ( track != null && track.trim().length() > 0 )
					{
					    int slashIndex = track.indexOf('/');
					    if ( slashIndex > -1 )
					    {
						track = track.substring(0, slashIndex);
					    }
					    
					    item.setTrackNumber(Integer.parseInt(track));
					}
				    }
				    catch(PropertyVetoException e)
				    {
					String message = "could not change the track number to '" + tag.getFirstTrack() + "' for " + url;
					logger.error(message, e);
					report.addErrorLog(item, message, e);
				    }
				    catch(NumberFormatException e)
				    {
					String message = "could not parse '" + tag.getFirstTrack() + "' as a Track number for " + url;
					logger.error(message, e);
					report.addErrorLog(item, message, e);
				    }
				}
			    }
			    // date
			    if ( item.getYear() == null || item.getYear().intValue() == 0 )
			    {
				if ( tag.getFirstYear() != null )
				{
				    try
				    {
					String year = tag.getFirstYear();
					if ( year != null && year.trim().length() > 0 )
					{
					    item.setYear(Integer.parseInt(year));
					}
				    }
				    catch(PropertyVetoException e)
				    {
					String message = "could not change the year of release to '" + tag.getFirstYear() + "' for " + url;
					logger.error(message, e);
					report.addErrorLog(item, message, e);
				    }
				    catch(NumberFormatException e)
				    {
					String message = "could not parse '" + tag.getFirstYear() + "' as a year of release for " + url;
					logger.error(message, e);
					report.addErrorLog(item, message, e);
				    }
				}
			    }
			}
		    }
		}
		catch(URISyntaxException e)
		{
		    String message = "cannot create uri from " + url;
		    logger.error(message);
		    report.addErrorLog(item, message, e);
		}
		catch(CannotReadException e)
		{
		    String message = "cannot read tag from " + url;
		    logger.error(message);
		    report.addErrorLog(item, message, e);
		}
	    }
	}
    }
    
}
