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
package org.blackdog.entagged.entagger;

import entagged.audioformats.Tag;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.exceptions.CannotWriteException;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.log4j.Logger;
import org.blackdog.report.TagsUpdateLog;
import org.blackdog.report.TagsUpdateReport;
import entagged.audioformats.AudioFile;
import org.blackdog.entagger.AbstractAudioEntagger;
import org.blackdog.type.TaggedSongItem;

/**
 *
 * @author alexis
 */
public class EntaggedEntagger extends AbstractAudioEntagger
{   
    /** logger */
    private Logger logger = Logger.getLogger(EntaggedEntagger.class);
    
    /** Creates a new instance of EntaggedEntagger */
    public EntaggedEntagger()
    {	}
    
    /** refresh the file tags of an TaggedSongItem according to its properties
     *	@param songItem a TaggedSongItem
     *	@param report a TagsUpdateReport
     */
    public void updateFileTags(TaggedSongItem songItem, TagsUpdateReport report)
    {
	if ( songItem != null )
	{
	    File f = null;
	    
	    try
	    {
		URL url = songItem.getValue();

		f = new File(url.toURI());

		AudioFile audioFile = entagged.audioformats.AudioFileIO.read(f);
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("audio file is " + audioFile);
		}
		
		/** complete tags of the file read */
		Tag tag = audioFile.getTag();
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("tag is " + tag);
		}
		 
		if ( tag == null )
		{
		    logger.error("tag is null for " + songItem.getName());
		    report.addLog(songItem, "no tag for file : " + f, TagsUpdateLog.Status.WARNING);
		}
		else
		{
		    if ( songItem.getAlbum() != null && songItem.getAlbum().trim().length() > 0 )
		    {
			String encoded = new String(songItem.getAlbum().getBytes(), Charset.forName("utf-8"));
			tag.setAlbum(encoded);
		    }
		    tag.setArtist(songItem.getArtist());
		    tag.setComment(songItem.getComment());
//		    tag.setEncoding()
		    tag.setGenre(songItem.getCategory().getPreferredGenreForm());
		    tag.setTitle(songItem.getTitle());
		    if ( songItem.getTrackNumber() != null )
		    {
			tag.setTrack(Integer.toString(songItem.getTrackNumber()));
		    }
		    if ( songItem.getYear() != null )
		    {
			tag.setYear(Integer.toString(songItem.getYear()));
		    }
		    
		    logger.info("tags : writing file " + f);
		    
		    entagged.audioformats.AudioFileIO.write(audioFile);
		    
		    logger.info("tags : file " + f + " written");
		}
	    }
	    catch(URISyntaxException e)
	    {
		String message = "could not create uri with url " + songItem.getValue();
		report.addErrorLog(songItem, message, e);
		logger.error(message, e);
	    }
	    catch(CannotReadException e)
	    {
		String message = "could not read file : " + f;
		report.addErrorLog(songItem, message, e);
		logger.error(message, e);
	    }
	    catch(CannotWriteException e)
	    {
		String message = "could not write file : " + f;
		report.addErrorLog(songItem, message, e);
		logger.error(message, e);
	    }
	}
    }
}
