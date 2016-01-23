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
package org.blackdog.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import org.blackdog.player.AbstractPlayer;
import org.blackdog.sound.javazoom.BasicPlayer2;
import org.blackdog.type.AudioItem;
import org.blackdog.type.Playable;
import org.blackdog.type.SongItem;
import org.blackdog.type.TimeBasedItem;
import org.blackdog.type.base.AudioDuration;

/**
 *
 * @author alexis
 */
public class BlackdogBasicPlayer extends AbstractPlayer
{
    /** basic player from javazoom */
    private BasicPlayer2 innerPlayer      = null;
    
    /** length in milli-seconds of the current item or -1 if undefined */
    private long         audioLength      = -1;
    
    /** bytes length of the item played */
    private long         bytesLength      = -1;
    
    /** last micro-seconds */
    private long         lastMicroSeconds = -1;
    
    /** Creates a new instance of BlackdogBasicPlayer */
    public BlackdogBasicPlayer()
    {
	this.innerPlayer = new BasicPlayer2();
	
	this.innerPlayer.addBasicPlayerListener(new BasicPlayerListener()
	{
	    public void opened(Object stream, Map properties)
	    {
		
	    }
	    
	    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties)
	    {
    //		System.err.println("progress : bytesread=" + bytesread + ", microseconds=" + microseconds);
		processProgress(bytesread, microseconds, pcmdata, properties);
		
		lastMicroSeconds = microseconds;
	    }
	    
	    public void setController(BasicController controller)
	    {
		new Exception("setting controller : " + controller).printStackTrace();
	    }
	    
	    public void stateUpdated(BasicPlayerEvent event)
	    {
		if ( event != null )
		{
		    if ( event.getCode() == BasicPlayerEvent.EOM )
		    {
			if ( getItem() != null )
			{
			    fireSongItemFullyRead(getItem(), (lastMicroSeconds <= 0 ? -1 : lastMicroSeconds / 1000));
			}
		    }
		}
		System.err.println("received event : " + event);
	    }
	});
    }
    
    /** dispose the player */
    public void dispose()
    {
	super.dispose();
	
	if ( this.innerPlayer != null )
	{
	    try
	    {
		this.innerPlayer.stop();
	    }
	    catch (BasicPlayerException ex)
	    {
		ex.printStackTrace();
	    }
	}
    }

    /** initialize the item currently associated with the player
     *  @param item a Playable
     */
    @Override
    public void setItem(final Playable item)
    {
	this.stop();
	
	super.setItem(item);
	
	if ( item == null )
	{
	    this.audioLength = -1;
	    this.bytesLength = -1;
	}
	else
	{
	    if ( item instanceof TimeBasedItem )
	    {
		AudioDuration duration = ((TimeBasedItem)item).getDuration();
		
		if ( duration != null )
		{
		    this.audioLength = duration.getTimeInMilli();
		}
	    }
	    if ( item instanceof SongItem )
	    {
		this.bytesLength = ((SongItem)item).getAudioBytesLength();
	    }
	    
	    try
	    {
		String extension = item.getExtension();
		
		this.innerPlayer.open(new InputStreamProvider()
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
			    e.printStackTrace();
			}
			
			return stream;
		    }
		}, extension);
	    }
	    catch(BasicPlayerException e)
	    {
		this.error(e);
		e.printStackTrace();
	    }
	    
	    if ( this.audioLength <= 0 )
	    {
		this.audioLength = -1;
	    }
	    if ( this.bytesLength <= 0 )
	    {
		this.bytesLength = -1;
	    }
	}
    }
    
    /** apply the new gain on the audio system to really change the volume
     *	@param newValue a double
     */
    protected void updateVolumeGain(double newValue)
    {
	if ( this.innerPlayer != null )
	{
	    try
	    {
		this.innerPlayer.setGain(newValue);
	    }
	    catch(BasicPlayerException e)
	    {
		e.printStackTrace();
	    }
	}
    }

    /**
     * stop this player. Any audio currently playing is stopped
     * immediately.
     * Do not care about the status of the player
     * 
     * Another call to play will restart the same player
     */
    public void stopImpl()
    {
	try
	{
	    this.innerPlayer.stop();
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     * Plays an audio sample from the beginning. do not care about the status of the player
     */
    public void playImpl()
    {
	try
	{
	    
	    System.out.println("status : " + this.getPlayerStatus());
	    this.innerPlayer.play();
	}
	catch(Exception e)
	{
	    this.error(e);
	    e.printStackTrace();
	}
    }

    /**
     * Stop temporarly or start at the current position the current audio sample. do not care about the status of the player
     */
    public void pauseImpl()
    {
	try
	{
	    this.innerPlayer.pause();
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     * Plays an audio sample at a given percentage position. do not care about the status of the player
     * 
     * @param position a position in percentage of the current audio sample length.<br/>
     *      if position is 0, then the player start the audio sample at the beginning. if position is 98, then the player<br/>
     *      will only process the end of the audio sample.
     */
    public void playAtImpl(double position)
    {
	System.out.println("playAtImpl : " + position);
	if ( this.bytesLength <= 0 )
	{
	    this.setPosition(0);
	}
	else
	{
	    try
	    {
		long seeks = (long)(this.bytesLength * position);
		System.out.println("seeks : " + seeks);
		this.innerPlayer.seek(1000);// seeks );
	    }
	    catch(BasicPlayerException e)
	    {
		e.printStackTrace();
	    }
	}
    }
    
    /**
     * resume
     */
    public void resumeImpl()
    {
	try
	{
	    this.innerPlayer.resume();
	}
	catch(BasicPlayerException e)
	{
	    e.printStackTrace();
	}
    }

    /**
     * Process PROGRESS event.
     * @param bytesread
     * @param microseconds
     * @param pcmdata
     * @param properties
     */
    public void processProgress(int bytesread, long microseconds, byte[] pcmdata, Map properties)
    {
	/* determine the progression for this player */
	if ( this.audioLength <= 0 || microseconds <= 0 )
	{
	    this.setPosition(0);
	}
	else
	{
	    double percentage = ( ( ((double)(microseconds)) / ((double)(this.audioLength * 1000) ) ) * 1000);
	    
	    this.setPosition(percentage);
	}
	
//        //log.debug("Player: Progress (EDT="+SwingUtilities.isEventDispatchThread()+")");
//        int byteslength = -1;
//        long total = -1;
//        // Try to get time from playlist item.
//        if (currentPlaylistItem != null) total = currentPlaylistItem.getLength();
//        // If it fails then try again with JavaSound SPI.
//        if (total <= 0) total = (long) Math.round(getTimeLengthEstimation(audioInfo) / 1000);
//        // If it fails again then it might be stream => Total = -1
//        if (total <= 0) total = -1;
//        if (audioInfo.containsKey("basicplayer.sourcedataline"))
//        {
//            // Spectrum/time analyzer
//            if (ui.getAcAnalyzer() != null) ui.getAcAnalyzer().writeDSP(pcmdata);
//        }
//        if (audioInfo.containsKey("audio.length.bytes"))
//        {
//            byteslength = ((Integer) audioInfo.get("audio.length.bytes")).intValue();
//        }
//        float progress = -1.0f;
//        if ((bytesread > 0) && ((byteslength > 0))) progress = bytesread * 1.0f / byteslength * 1.0f;
//        if (audioInfo.containsKey("audio.type"))
//        {
//            String audioformat = (String) audioInfo.get("audio.type");
//            if (audioformat.equalsIgnoreCase("mp3"))
//            {
//                //if (properties.containsKey("mp3.position.microseconds")) secondsAmount = (long) Math.round(((Long) properties.get("mp3.position.microseconds")).longValue()/1000000);
//                // Shoutcast stream title.
//                if (properties.containsKey("mp3.shoutcast.metadata.StreamTitle"))
//                {
//                    String shoutTitle = ((String) properties.get("mp3.shoutcast.metadata.StreamTitle")).trim();
//                    if (shoutTitle.length() > 0)
//                    {
//                        if (currentPlaylistItem != null)
//                        {
//                            String sTitle = " (" + currentPlaylistItem.getFormattedDisplayName() + ")";
//                            if (!currentPlaylistItem.getFormattedName().equals(shoutTitle + sTitle))
//                            {
//                                currentPlaylistItem.setFormattedDisplayName(shoutTitle + sTitle);
//                                showTitle((shoutTitle + sTitle).toUpperCase());
//                                playlistUI.paintList();
//                            }
//                        }
//                    }
//                }
//                // EqualizerUI
//                if (properties.containsKey("mp3.equalizer")) equalizerUI.setBands((float[]) properties.get("mp3.equalizer"));
//                if (total > 0) secondsAmount = (long) (total * progress);
//                else secondsAmount = -1;
//            }
//            else if (audioformat.equalsIgnoreCase("wave"))
//            {
//                secondsAmount = (long) (total * progress);
//            }
//            else
//            {
//                secondsAmount = (long) Math.round(microseconds / 1000000);
//                equalizerUI.setBands(null);
//            }
//        }
//        else
//        {
//            secondsAmount = (long) Math.round(microseconds / 1000000);
//            equalizerUI.setBands(null);
//        }
//        if (secondsAmount < 0) secondsAmount = (long) Math.round(microseconds / 1000000);
//        /*-- Display elapsed time --*/
//        int secondD = 0, second = 0, minuteD = 0, minute = 0;
//        int seconds = (int) secondsAmount;
//        int minutes = (int) Math.floor(seconds / 60);
//        int hours = (int) Math.floor(minutes / 60);
//        minutes = minutes - hours * 60;
//        seconds = seconds - minutes * 60 - hours * 3600;
//        if (seconds < 10)
//        {
//            secondD = 0;
//            second = seconds;
//        }
//        else
//        {
//            secondD = ((int) seconds / 10);
//            second = ((int) (seconds - (((int) seconds / 10)) * 10));
//        }
//        if (minutes < 10)
//        {
//            minuteD = 0;
//            minute = minutes;
//        }
//        else
//        {
//            minuteD = ((int) minutes / 10);
//            minute = ((int) (minutes - (((int) minutes / 10)) * 10));
//        }
//        ui.getAcMinuteH().setAcText(String.valueOf(minuteD));
//        ui.getAcMinuteL().setAcText(String.valueOf(minute));
//        ui.getAcSecondH().setAcText(String.valueOf(secondD));
//        ui.getAcSecondL().setAcText(String.valueOf(second));
//        // Update PosBar location.
//        if (total != 0)
//        {
//            if (posValueJump == false)
//            {
//                int posValue = ((int) Math.round(secondsAmount * Skin.POSBARMAX / total));
//                ui.getAcPosBar().setValue(posValue);
//            }
//        }
//        else ui.getAcPosBar().setValue(0);
//        long ctime = System.currentTimeMillis();
//        long lctime = lastScrollTime;
//        // Scroll title ?
//        if ((titleScrollLabel != null) && (titleScrollLabel.length > 0))
//        {
//            if (ctime - lctime > SCROLL_PERIOD)
//            {
//                lastScrollTime = ctime;
//                if (scrollRight == true)
//                {
//                    scrollIndex++;
//                    if (scrollIndex >= titleScrollLabel.length)
//                    {
//                        scrollIndex--;
//                        scrollRight = false;
//                    }
//                }
//                else
//                {
//                    scrollIndex--;
//                    if (scrollIndex <= 0)
//                    {
//                        scrollRight = true;
//                    }
//                }
//                // TODO : Improve
//                ui.getAcTitleLabel().setAcText(titleScrollLabel[scrollIndex]);
//            }
//        }
    }
    
}
