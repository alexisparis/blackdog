package org.blackdog.player.impl;

import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.log4j.Logger;
import org.blackdog.player.AbstractAudioPlayer;
import org.blackdog.player.PlayerStatus;
import org.blackdog.player.annotation.PlayerCaracteristics;

/**
 *
 * @author alexis
 */

@PlayerCaracteristics(extensions={"wav"})

public class WavAudioPlayer extends AbstractAudioPlayer
{
    /** logger */
    private Logger logger = Logger.getLogger(WavAudioPlayer.class);
    
    enum Position
    {   LEFT, RIGHT, NORMAL };
    
    private final int EXTERNAL_BUFFER_SIZE = 4096;//524288; // 128Kb = (128*1024) * 4
 
    private Position curPosition = Position.NORMAL;
    
    /** the current SourceDataLine */
    private SourceDataLine audioLine = null;
    
    /** create a new WavAudioPlayer */
    public WavAudioPlayer()
    {   super(); }

    /**
     * stop this player. Any audio currently playing is stopped
     * immediately.
     * Do not care about the status of the player
     * 
     * Another call to play will restart the same player
     */
    public void stopImpl()
    {   
        Runnable runnable = new Runnable()
        {
            public void run()
            {   logger.debug("trying to stop " + this);
                logger.debug("audio line : " + audioLine);
                if ( audioLine != null )
                {   logger.debug("audio line is open ? " + audioLine.isOpen());
                    if ( audioLine.isOpen() )
                    {   
                        logger.debug("about to close audio line");
                        audioLine.close();
                        logger.debug("audio line closed");
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    /**
     * Stop temporarly or start at the current position the current audio sample. do not care about the status of the player
     */
    public void pauseImpl()
    {   /* do nothing */ }

    /**
     * Plays an audio sample at a given percentage position. do not care about the status of the player
     * 
     * @param position a position in percentage of the current audio sample length.<br/>
     *      if position is 0, then the player start the audio sample at the beginning. if position is 98, then the player<br/>
     *      will only process the end of the audio sample.
     */
    public void playAtImpl(double position)
    {   throw new UnsupportedOperationException("a faire"); }

    /**
     * Plays an audio sample from the beginning. do not care about the status of the player
     */
    public void playImpl()
    {
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                InputStream stream = null;
                
                try
                {   logger.debug("preparing input stream");
                    stream = getItem().createInputStream();
                    logger.debug("input stream prepared");
                }
                catch(IOException e)
                {   e.printStackTrace(); }
 
		AudioInputStream audioInputStream = null;
		try
                {   logger.debug("creating AudioInputStream");
                    audioInputStream = AudioSystem.getAudioInputStream(stream);
                    logger.debug("AudioInputStream created");
                }
                catch (UnsupportedAudioFileException e1)
                {   e1.printStackTrace();
                    return;
		}
                catch (IOException e1)
                {   e1.printStackTrace();
                    return;
		}
 
		AudioFormat format = audioInputStream.getFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
 
		try
                {   audioLine = (SourceDataLine) AudioSystem.getLine(info);
                    audioLine.open(format);
		}
                catch (LineUnavailableException e)
                {   e.printStackTrace();
                    return;
		}
                catch (Exception e)
                {   e.printStackTrace();
                    return;
		}
 
		if (audioLine.isControlSupported(FloatControl.Type.PAN))
                {   FloatControl pan = (FloatControl) audioLine.getControl(FloatControl.Type.PAN);
                    if (curPosition == Position.RIGHT)
                    {   pan.setValue(1.0f); }
                    else if (curPosition == Position.LEFT)
                    {   pan.setValue(-1.0f); }
		} 
 
		audioLine.start();
		int nBytesRead = 0;
		byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
 
		try
                {   while ( nBytesRead != -1 && ! getPlayerStatus().equals(PlayerStatus.STOPPED) )
                    {   if ( getPlayerStatus().equals(PlayerStatus.PLAYING) )
                        {   System.err.println("before read");
                            nBytesRead = audioInputStream.read(abData, 0, abData.length);
                            System.err.println("after read");
                            if (nBytesRead >= 0)
                            {   System.err.println("before write");
                                System.out.println("bytes length : " + abData.length);
                                audioLine.write(abData, 0, nBytesRead);
                                System.err.println("after write");
                            }
                        }
                    }
		}
                catch (IOException e)
                {   e.printStackTrace();
                    return;
		}
                finally
                {   
                    if ( audioLine != null )
                    {   if ( audioLine.isOpen() )
                        {   audioLine.drain(); }
                        audioLine.close();
                    }
		}
            }
        };
        
        new Thread(runnable).start();
    }
}
