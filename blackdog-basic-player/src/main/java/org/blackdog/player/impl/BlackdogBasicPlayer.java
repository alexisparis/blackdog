/*
 * BlackdogBasicPlayer.java
 *
 * Created on 18 février 2008, 20:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.blackdog.player.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import org.blackdog.player.AbstractPlayer;
import org.blackdog.player.impl.javazoom.BasicPlayer2;
import org.blackdog.type.Playable;
import org.blackdog.player.annotation.PlayerCaracteristics;

/**
 *
 * @author alexis
 */
@PlayerCaracteristics(extensions={"wav", "mp3", "ogg", "flac"})

public class BlackdogBasicPlayer extends AbstractPlayer
{
    /** basic player from javazoom */
    private BasicPlayer2 innerPlayer = null;
    
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
		System.err.println("progress : bytesread=" + bytesread + ", microseconds=" + microseconds);
		processProgress(bytesread, microseconds, pcmdata, properties);
	    }
	    
	    public void setController(BasicController controller)
	    {
		System.err.println("setting controller : " + controller);
	    }
	    
	    public void stateUpdated(BasicPlayerEvent event)
	    {
		System.err.println("received event : " + event);
	    }
	});
    }

    /** initialize the item currently associated with the player
     *  @param item a Playable
     */
    @Override
    public void setItem(final Playable item)
    {
	this.stop();
	
	super.setItem(item);
	
	if ( item != null )
	{
	    try
	    {
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
		});
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
	    this.innerPlayer.play();
	}
	catch(Exception e)
	{
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
	// not yet implemented
    }

    /**
     * Try to compute time length in milliseconds.
     * @param properties
     * @return
     */
    public long getTimeLengthEstimation(Map properties)
    {
        long milliseconds = -1;
        int byteslength = -1;
        if (properties != null)
        {
            if (properties.containsKey("audio.length.bytes"))
            {
                byteslength = ((Integer) properties.get("audio.length.bytes")).intValue();
            }
            if (properties.containsKey("duration"))
            {
                milliseconds = (int) (((Long) properties.get("duration")).longValue()) / 1000;
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
                if (bitspersample > 0)
                {
                    milliseconds = (int) (1000.0f * byteslength / (samplerate * channels * (bitspersample / 8)));
                }
                else
                {
                    milliseconds = (int) (1000.0f * byteslength / (samplerate * framesize));
                }
            }
        }
        return milliseconds;
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
