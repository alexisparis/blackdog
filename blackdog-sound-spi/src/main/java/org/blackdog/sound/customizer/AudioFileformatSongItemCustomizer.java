/*
 * blackdog sound spi : define player based on java sound spi
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
package org.blackdog.sound.customizer;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.log4j.Logger;
import org.blackdog.sound.BlackdogAudioSystem;
import org.blackdog.sound.InputStreamProvider;
import org.blackdog.type.SongItem;
import org.blackdog.type.customizer.SongItemCustomizer;
import org.blackdog.report.TagsUpdateReport;
import org.blackdog.type.annotation.AudioFileFormatTags;
import org.blackdog.type.base.AudioDuration;

/**
 *
 * customizer that complete the information of a SongItem
 *  according to the AudioFileFormat found for that item.
 *
 * @author alexis
 */
public class AudioFileformatSongItemCustomizer implements SongItemCustomizer
{
    /** logger */
    private transient Logger logger = Logger.getLogger(AudioFileformatSongItemCustomizer.class);
    
    /** cache for AudioFileformatTags */
    private Map<Class, AudioFileFormatTags> affTags = new WeakHashMap<Class, AudioFileFormatTags>();
    
    /** file where to store properties */
    private File   properties = null;
    
    /** Creates a new instance of AudioFileformatSongItemCustomizer */
    public AudioFileformatSongItemCustomizer()
    {
//	this.properties = new File("/home/alexis/audioFileFormat.properties");
//	
//	try
//	{
//	    this.properties.createNewFile();
//	}
//	catch(IOException e)
//	{
//	    e.printStackTrace();
//	}
    }
    
    /** return the AudioFileFormatTags to use for the given SongItem
     *	@param item a SonggItem
     *	@return a AudioFileFormatTags or null if not found
     */
    private AudioFileFormatTags getAudioFileFormatTags(SongItem item)
    {
	AudioFileFormatTags tags = null;
	
	if ( item != null )
	{
	    if ( this.affTags.containsKey(item.getClass()) )
	    {
		tags = this.affTags.get(item.getClass());
	    }
	    else
	    {
		tags = (AudioFileFormatTags)item.getClass().getAnnotation(AudioFileFormatTags.class);
		
		this.affTags.put(item.getClass(), tags);
	    }
	}
	
	return tags;
    }

    /** complete the information of the given SongItem
     *	@param report a TagsUpdateReport
     *	@param item a SongItem
     */
    public void customize(TagsUpdateReport report, final SongItem item)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("caling customize(" + report + ", " + item + ")");
	}
	
	if ( item != null )
	{
	    /* asking BlackdogAudioSystem to return the AudioFileFormat for this kind of item */
	    // TODO : complete extension for optimization
	    AudioFileFormat format = null;
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("trying to get audio file format for item " + item);
	    }
	    try
	    {
		format = BlackdogAudioSystem.getAudioFileFormat(new InputStreamProvider()
		{
		    public InputStream createStream()
		    {
			InputStream stream = null;

			try
			{
			    stream = item.createInputStream();
			}
			catch(IOException e)
			{
			    logger.error("unable to open input stream for item " + item);
			}

			return stream;
		    }
		}, null);
	    }
	    catch(UnsupportedAudioFileException e)
	    {
		logger.error("unable to get audio file format for " + item, e);
	    }
	    catch(IOException e)
	    {
		logger.error("error reading input stream while trying to get audio file format for " + item, e);
	    }
	    catch(Exception e)
	    {
		logger.error("got error while trying to get audio file format for " + item, e);
	    }
	    
	    if ( format == null )
	    {
		logger.info("unable to get AudioFileformat for item : " + item);
	    }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("got audio file format : " + format);
		}
		
		Iterator<Map.Entry<String, Object>> entries = format.properties().entrySet().iterator();
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("affichage des propriétés pour " + item.getName() + ", type=" + format.getType() + ", bytes length=" + format.getByteLength() +
				 ", frame length=" + format.getFrameLength() + ", channels=" + format.getFormat().getChannels() + 
				 ", encoding=" + format.getFormat().getEncoding() + ", frame rate=" + format.getFormat().getFrameRate() + 
				 ", frame size=" + format.getFormat().getFrameSize() + ", sample rate=" + format.getFormat().getSampleRate() + 
				 ", sample size in bits=" + format.getFormat().getSampleSizeInBits());
		    
		    while(entries.hasNext())
		    {
			Map.Entry<String, Object> currentEntry = entries.next();

			logger.debug("\t" + currentEntry.getKey() + " --> " + currentEntry.getValue());
		    }
		    
		    entries = format.properties().entrySet().iterator();
		}
		
		Properties props = null;
		
//mp3.padding=false
//mp3.framesize.bytes=413
//mp3.vbr.scale=77
//mp3.vbr=true
//mp3.crc=false
//mp3.version.encoding=MPEG1L3
//mp3.original=true
//mp3.bitrate.nominal.bps=197000
//mp3.header.pos=4096
//mp3.version.mpeg=1
//mp3.id3tag.disc=1/2
//mp3.id3tag.v2=java.io.ByteArrayInputStream@5d8440
//mp3.id3tag.orchestra=Carl Smith
//mp3.id3tag.encoded=LAME 3.97
//mp3.id3tag.grouping=Indie
//mp3.version.layer=3
//mp3.id3tag.publisher=Bear Family
//mp3.id3tag.v2.version=3
//mp3.mode=1
//mp3.frequency.hz=44100
//mp3.id3tag.genre=Hard Rock
//mp3.channels=2
//mp3.id3tag.composer=Bryant
//mp3.id3tag.track=1
//mp3.framerate.fps=38.28125
//mp3.copyright=false
//mp3.id3tag.length=290000
//	
//album=Untitled
//author=Korn
//date=2007
//comment=-] MST [-
//title=intro
//copyright=Octone Records LLC
//	
//ogg.bitrate.nominal.bps=192003
//ogg.frequency.hz=44100
//ogg.version=0
//ogg.comment.track=-52
//ogg.comment.genre=Classic Rock
//ogg.comment.encodedby=Xiph.Org libVorbis I 20020717
//ogg.comment.ext.2=MUSICBRAINZ_SORTNAME\=The Dandy Warhols
//ogg.comment.ext.1=Year\=2003
//ogg.channels=2
//ogg.serial=27538
		
		if ( this.properties != null )
		{
		    try
		    {
		    FileInputStream stream = new FileInputStream(this.properties);
		    props = new Properties();

		    props.load(stream);

		    stream.close();
		    }
		    catch(Exception e)
		    {
			e.printStackTrace();
		    }
		}
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("asking audio file format for " + item);
		}
		AudioFileFormatTags tags = getAudioFileFormatTags(item);
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("audio file format for " + item + " --> " + tags);
		}
		
		//System.out.println("tags for " + item.getName() + " --> " + tags + " " + (tags == null ? null : tags.audioLengthTags().length));
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("try to compute duration");
		}
		Long duration = this.getTimeLengthEstimation(format, (tags == null ? (String[])null : tags.audioLengthTags()));
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("duration computed is " + duration);
		}
		
//		System.out.println("duration : " + duration);
		
		if ( duration != null && duration > 0 )
		{
		    try
		    {
			AudioDuration date = new AudioDuration(duration);
			item.setDuration(date);

			if ( logger.isDebugEnabled() )
			{
			    logger.debug("setting duration=" + duration + " for " + item.getName());
			}
		    }
		    catch(PropertyVetoException e)
		    {
			logger.warn("unable to change the duration of " + item, e);
		    }
		}
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("try to get bytes length");
		}
		Integer bytesLength = this.getAudioBytesLengthEstimation(format, (tags == null ? (String[])null : tags.audioBytesLengthTags()));
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("bytes length is " + bytesLength);
		}
		if ( bytesLength != null && bytesLength.intValue() > 0 )
		{
		    item.setAudioBytesLength(bytesLength.intValue());
		}
		
		if ( this.properties != null )
		{
		    try
		    {

		    FileOutputStream output = new FileOutputStream(this.properties);

		    props.save(output, "toto");

		    output.close();
		    }
		    catch(Exception e)
		    {
			e.printStackTrace();
		    }
		}
		
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of customize(" + report + ", " + item + ")");
	}
    }
    
    /** return an estimation of the audio bytes length 
     *	@param format an AudioFileFormat
     *	@param audioBytesLengthTags an array of String representing the name of tags that are related to audio bytes length
     *	@return an estimation of the audio bytes length
     */
    private Integer getAudioBytesLengthEstimation(AudioFileFormat format, String[] audioBytesLengthTags)
    {
	Integer bytesLength = null;
	
	if ( format != null )
	{
	    int byteslength = -1;
	    
	    Map properties = format.properties();
	    
	    if (properties != null)
	    {
		if ( audioBytesLengthTags != null )
		{
		    for(int i = 0; i < audioBytesLengthTags.length; i++)
		    {
			String current = audioBytesLengthTags[i];
			
			if ( current != null )
			{
			    Object o = properties.get(current);
			    
			    if ( o instanceof String )
			    {
				try
				{
				    bytesLength = Integer.parseInt( (String)o );
				}
				catch(NumberFormatException e)
				{
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("unable to parse '" + o + "' as audio length for tag '" + current + "'");
				    }
				}
			    }
			}
			
			if ( bytesLength != null && bytesLength.intValue() > 0 )
			{
			    break;
			}
		    }
		}
	    }
	}
	
	return bytesLength;
    }

    /**
     * Try to compute time length in milliseconds.
     *  @param properties
     *	@param audioLengthTags an array of String representing the name of tags that are related to audio length
     *  @return
     */
    public long getTimeLengthEstimation(AudioFileFormat format, String[] audioLengthTags)
    {
	Long milliseconds = null;
	
	if ( format != null )
	{
	    int byteslength = -1;
	    
	    Map properties = format.properties();
	    
	    if (properties != null)
	    {
		if ( audioLengthTags != null )
		{
		    for(int i = 0; i < audioLengthTags.length; i++)
		    {
			String current = audioLengthTags[i];
			
			if ( current != null )
			{
			    Object o = properties.get(current);
			    
			    if ( o instanceof String )
			    {
				try
				{
				    milliseconds = Long.parseLong( (String)o );
				}
				catch(NumberFormatException e)
				{
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("unable to parse '" + o + "' as audio length for tag '" + current + "'");
				    }
				}
			    }
			}
			
			if ( milliseconds != null && milliseconds > 0 )
			{
			    break;
			}
		    }
		}
		
		if ( milliseconds == null || milliseconds < 0 )
		{
		    if (properties.containsKey("audio.length.bytes"))
		    {
			byteslength = ((Integer) properties.get("audio.length.bytes")).intValue();
		    }
		    if (properties.containsKey("duration"))
		    {
			milliseconds = (long) ((((Long) properties.get("duration")).longValue()) / 1000.0);
		    }
		    else
		    {
			// Try to compute duration
			int bitspersample = -1;
			int channels = -1;
			float samplerate = -1.0f;
			int framesize = -1;
			if (properties.containsKey("audio.samplesize.bits"))
			{
			    bitspersample = ((Integer) properties.get("audio.samplesize.bits")).intValue();
			}
			if (properties.containsKey("audio.channels"))
			{
			    channels = ((Integer) properties.get("audio.channels")).intValue();
			}
			if (properties.containsKey("audio.samplerate.hz"))
			{
			    samplerate = ((Float) properties.get("audio.samplerate.hz")).floatValue();
			}
			if (properties.containsKey("audio.framesize.bytes"))
			{
			    framesize = ((Integer) properties.get("audio.framesize.bytes")).intValue();
			}

			if ( bitspersample == -1 )
			{
			    bitspersample = format.getFormat().getSampleSizeInBits();
			}
			if ( channels == -1 )
			{
			    channels = format.getFormat().getChannels();
			}
			if ( samplerate == -1.0f )
			{
			    samplerate = format.getFormat().getSampleRate();
			}
			if ( framesize == -1 )
			{
			    framesize = format.getFormat().getFrameSize();
			}

			if (bitspersample > 0)
			{
			    milliseconds = (long) (1000.0f * byteslength / (samplerate * channels * (bitspersample / 8)));
			}
			else
			{
			    milliseconds = (long) (1000.0f * byteslength / (samplerate * framesize));
			}
		    }
		}
	    }
	}
        return milliseconds;
    }
    
}
