package org.blackdog.player.impl;

/**
 * BasicPlayer.
 *
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */
import java.io.*;
import javazoom.jl.decoder.*;
import javax.sound.sampled.*;

import java.util.*;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Control;
import org.apache.log4j.Logger;
import org.blackdog.player.AbstractPlayer;
import org.blackdog.player.PlayerStatus;
import org.blackdog.player.annotation.PlayerCaracteristics;
import org.blackdog.player.impl.reader.BlackdogAudioSystem;
import org.blackdog.type.Playable;

/**
 * Java sound based player
 * 
 * BasicPlayer implements basics features of a player. The playback is done
 * with a thread.
 * BasicPlayer is the result of jlGui 0.5 from JavaZOOM and BaseAudioStream
 * from Matthias Pfisterer JavaSound examples.
 *
 * @author	E.B from JavaZOOM
 *
 * Homepage : http://www.javazoom.net
 *
 */
@PlayerCaracteristics(extensions={"wav", "mp3", "ogg"})

public class SpiPlayer extends AbstractPlayer
{
    /** logger */
    private static Logger logger = Logger.getLogger(SpiPlayer.class.getName());
    
    private static final int EXTERNAL_BUFFER_SIZE = 4000 * 4;
    
    private Thread m_thread = null;
    private Object m_dataSource;
    private AudioInputStream m_audioInputStream;
    private AudioFileFormat m_audioFileFormat;
    private SourceDataLine m_line;
    private FloatControl m_gainControl;
    private FloatControl m_panControl;
    
    /**
     * These variables are used to distinguish stopped, paused, playing states.
     * We need them to control Thread.
     */
    private long doSeek = -1;
    private File _file = null;
    private JSPlayerListener m_bpl = null;
    
    /** ExecutorService */
    private ExecutorService executorService     = null;
    
    /** the current future */
    private Future          currentFuture       = null;
    
    /** length of the Playable in ms */
    private double          playableLength      = AudioSystem.NOT_SPECIFIED;
    
    /** bytes length of the playable */
    private long            playableBytesLength = -1;
    
    /**
     * Constructs a Basic Player.
     */
    public SpiPlayer()
    {   }

    /** initialize the item currently associated with the player
     *  @param item a Playable
     */
    @Override
    public void setItem(Playable item)
    {
	new Exception("setItem").printStackTrace();
	
        super.setItem(item);
        
        try
        {   
            this.setDataSource(item == (InputStream)null ? null : item.createInputStream());
	    
	    m_audioInputStream.available();
	    
	    if ( item == null )
	    {
		this.playableLength = 0;
		this.playableBytesLength = -1;
	    }
	    else
	    {
		this.playableLength = AudioSystem.NOT_SPECIFIED;
		this.playableBytesLength = item.getBytesLength();
		
		/** try to dtermine playableLength according to format */
		int   frameLength = this.getAudioFileFormat().getFrameLength();
		float frameRate   = this.getAudioFormat().getFrameRate();
		
		if ( frameLength != AudioSystem.NOT_SPECIFIED && frameRate != AudioSystem.NOT_SPECIFIED )
		{
		    this.playableLength = frameLength * frameRate;
		}
		
		if ( this.playableLength < 0 )
		{
		    this.playableLength = AudioSystem.NOT_SPECIFIED;
		}
		
		if ( this.playableLength == AudioSystem.NOT_SPECIFIED )
		{
		    if ( "mp3".equals(item.getExtension()) )
		    {
			InputStream stream = null;
			try
			{
			    stream = item.createInputStream();
			    Bitstream m_bitstream = new Bitstream(stream);
			    Header m_header = m_bitstream.readFrame();

			    int mediaLength = (int)stream.available();

			    if (mediaLength != AudioSystem.NOT_SPECIFIED && mediaLength > 0)
			    {
			       this.playableLength = Math.round(m_header.total_ms(mediaLength));
			    }
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			}
			finally
			{
			    if ( stream != null )
			    {
				try
				{
				    stream.close();
				}
				catch(IOException e)
				{
				    e.printStackTrace();
				}
			    }
			}
		    }
//		    else if ( "ogg".equals(item.getExtension()) )
//		    {
//
//		    }
		    else
		    {
			
		    }
		}
		
		if ( this.playableLength < 0 )
		{
		    this.playableLength = AudioSystem.NOT_SPECIFIED;
		}
	    }
        }
        catch (UnsupportedAudioFileException ex)
        {   ex.printStackTrace(); }
        catch (IOException ex)
        {   ex.printStackTrace(); }
        catch (LineUnavailableException ex)
        {   ex.printStackTrace(); }
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
        /** stop current runnable if exists */
        this.waitUntilEndOfLastRunnable(true);
        
        SourceDataLine line = this.m_line;
        if ( line != null )
        {
            line.flush();
            line.close();
        }
        
        InputStream stream = this.m_audioInputStream;
        if ( stream != null )
        {
            try
            {
                stream.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Plays an audio sample from the beginning. do not care about the status of the player
     */
    public void playImpl()
    {
        logger.debug("calling playImpl");
        
        if ( this.getPlayerStatus().equals(PlayerStatus.PAUSED) )
        {   logger.debug("calling resumePlayback");
            this.resumePlayback();
        }
        else
        {   logger.debug("calling startPlayback");
            this.startPlayback();
        }
    }
    
    /**
     * Stop temporarly or start at the current position the current audio sample. do not care about the status of the player
     */
    public void pauseImpl()
    {
        this.pausePlayback();
    }
    
    /**
     * Plays an audio sample at a given percentage position. do not care about the status of the player
     *  @param position a position in percentage of the current audio sample length.<br/>
     *      if position is 0, then the player start the audio sample at the beginning. if position is 98, then the player<br/>
     *      will only process the end of the audio sample.
     */
    public void playAtImpl(double position)
    {
        // TODO
    }
    
    /**
     * Sets the data source as an InputStream
     */
    public void setDataSource(InputStream inputStream) throws UnsupportedAudioFileException, LineUnavailableException, IOException
    {
        logger.debug("setDataSource(" + inputStream + ")");
        if (inputStream != null)
        {
            this.m_dataSource = inputStream;
            initAudioInputStream();
        }
    }
    
    /**
     * Inits Audio ressources from the data source.<br>
     * - AudioInputStream <br>
     * - AudioFileFormat
     */
    private void initAudioInputStream() throws UnsupportedAudioFileException, LineUnavailableException, IOException
    { 
        logger.debug("initAudioInputStream");
        if ( this.m_dataSource instanceof InputStream )
        {
            initAudioInputStream( (InputStream)this.m_dataSource );
        }
    }
    
    /**
     * Inits Audio ressources from InputStream.
     */
    private void initAudioInputStream(InputStream inputStream) throws UnsupportedAudioFileException, IOException
    {
        logger.debug("initAudioInputStream(" + inputStream + ")");
        this.m_audioInputStream = BlackdogAudioSystem.getAudioInputStream(inputStream);
        this.m_audioFileFormat  = BlackdogAudioSystem.getAudioFileFormat(inputStream);
    }
    
    /* #########################################################################
     * ################### methods that manage playback ########################
     * ######################################################################### */
    
    /**
     * Pauses the playback.<br>
     *
     * Player Status = PAUSED.
     */
    public void pausePlayback()
    {
        logger.debug("pause playback");
        SourceDataLine line = this.m_line;
        
        if (line != null)
        {
            logger.debug("flusing line");
            m_line.flush();
            logger.debug("line flushed");
            
            logger.debug("stopping line");
            m_line.stop();
            logger.debug("line stopped");
        }
    }
    
    /**
     * Resumes the playback.<br>
     *
     * Player Status = PLAYING.
     */
    public void resumePlayback()
    {
        logger.debug("resume playback");
        
        SourceDataLine line = this.m_line;
        
        if (line != null)
        {
            logger.debug("starting line");
            m_line.start();
            logger.debug("line started");
        }
    }
    
    /* #########################################################################
     * ####################### Audio line management ###########################
     * ######################################################################### */
    
    /**
     * Inits Audio ressources from AudioSystem.<br>
     * DateSource must be present.
     */
    protected void initLine() throws LineUnavailableException
    {
        logger.debug("initLine");
            
        if (m_line == null)
        {
            createLine();
            openLine();
        }
        else
        {   
            AudioFormat lineAudioFormat = m_line.getFormat();
            AudioFormat audioInputStreamFormat = m_audioInputStream == null ? null : m_audioInputStream.getFormat();
            if ( ! lineAudioFormat.equals(audioInputStreamFormat) )
            {   
                // TODO
                
                openLine();
            }
        }
    }
    
    /**
     * Inits a DateLine.<br>
     *
     * We check if the line supports Volume and Pan controls.
     *
     * From the AudioInputStream, i.e. from the sound file, we
     * fetch information about the format of the audio data. These
     * information include the sampling frequency, the number of
     * channels and the size of the samples. There information
     * are needed to ask JavaSound for a suitable output line
     * for this audio file.
     * Furthermore, we have to give JavaSound a hint about how
     * big the internal buffer for the line should be. Here,
     * we say AudioSystem.NOT_SPECIFIED, signaling that we don't
     * care about the exact size. JavaSound will use some default
     * value for the buffer size.
     */
    private void createLine() throws LineUnavailableException
    {
        logger.debug("creatingLine");
            
        // TODO : comment
        
        if (m_line == null)
        {
            AudioFormat sourceFormat = m_audioInputStream.getFormat();
            
            AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    sourceFormat.getSampleRate(),
                    16,
                    sourceFormat.getChannels(),
                    sourceFormat.getChannels() * 2,
                    sourceFormat.getSampleRate(),
                    false);
            
            m_audioInputStream = BlackdogAudioSystem.getAudioInputStream(targetFormat, m_audioInputStream);
            
            AudioFormat audioFormat = m_audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
            m_line = (SourceDataLine) AudioSystem.getLine(info);
            
            /*-- Display supported controls --*/
            Control[] c = m_line.getControls();
            for (int p = 0; p < c.length; p++)
            {
                logger.debug("controls : " + c[p].toString());
            }
            /*-- Is Gain Control supported ? --*/
            if (m_line.isControlSupported(FloatControl.Type.MASTER_GAIN))
            {
                m_gainControl = (FloatControl) m_line.getControl(FloatControl.Type.MASTER_GAIN);
                logger.debug("Master Gain Control : [" + m_gainControl.getMinimum() + "," + m_gainControl.getMaximum() + "];precision=" + m_gainControl.getPrecision());
            }
            
            /*-- Is Pan control supported ? --*/
            if (m_line.isControlSupported(FloatControl.Type.PAN))
            {
                m_panControl = (FloatControl) m_line.getControl(FloatControl.Type.PAN);
		
                logger.debug("Pan Control : [" + m_panControl.getMinimum() + "," + m_panControl.getMaximum() + "];precision=" + m_panControl.getPrecision());
            }
        }
    }
    
    /**
     * Opens the line.
     */
    private void openLine() throws LineUnavailableException
    {
        logger.debug("openLine");
        
        if (m_line != null)
        {
            AudioFormat audioFormat = m_audioInputStream.getFormat();
            logger.debug("opening line");
            this.m_line.open(audioFormat, m_line.getBufferSize());
            logger.debug("line opened");
        }
    }
    
    /* #########################################################################
     * ######################## Thread related methods #########################
     * ######################################################################### */
    
    /** return the ExecutorService
     *  @return the ExecutorService
     */
    private synchronized ExecutorService getExecutorService()
    {
        ExecutorService service = this.executorService;
        
        if ( service == null )
        {
            service = Executors.newSingleThreadExecutor();
            this.executorService = service;
        }
        
        return service;
    }
    
    /** method that launch a new runnable on the executor service and that update the last future
     *  this methods does not check if the last runnable submitted is done
     *  @param runnable a Runnable
     */
    private void launchRunnable(Runnable runnable)
    {
        if ( runnable == null )
        {
            throw new IllegalArgumentException("runnable could not be null");
        }
        
        ExecutorService service = this.getExecutorService();
        
        this.currentFuture = service.submit(runnable);
    }
    
    /** method that returns when the last runnable submitted on the ExecutorService is done
     *  @param forceToExit true to indicate to the last task to finished
     */
    private void waitUntilEndOfLastRunnable(boolean forceToExit)
    {
        Future future = this.currentFuture;
        if ( future != null )
        {
            if ( ! future.isDone() )
            {
                try
                {
                    if ( forceToExit )
                    {
                        future.cancel(true);
                    }
                    
                    /* wait for the future to be finished */
                    future.get();
                }
                catch(Exception e)
                {
                    logger.warn("error when waiting for the last task to finished", e);
                }
            }
        }
    }
    
  //////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////
    
    /**
     * Starts playback.
     */
    public void startPlayback()
    {
        logger.debug("startPlayBack");
        
        logger.info("active thread count : " + Thread.activeCount());
        
        logger.debug("player status : " + this.getPlayerStatus());
        
        if ( this.getPlayerStatus().equals(PlayerStatus.PAUSED) )
        {
            this.resumePlayback();
        }
        else if ( this.getPlayerStatus().equals(PlayerStatus.STOPPED) )
        {
            synchronized(this)
            {
                /** wait for last task to finished */
                this.waitUntilEndOfLastRunnable(true);

                try
                {
                    logger.debug("initializing line for new playback start");
                    initLine();
                }
                catch (Exception e)
                {
                    e.printStackTrace();

                    if ( e instanceof LineUnavailableException )
                    {
                        this.error(e);
                    }

                    logger.error("init line failed", e);

                    return;
                }

                logger.debug("creating new thread");

                this.launchRunnable(new PlayerRunnable());

                SourceDataLine line = this.m_line;
                if ( line != null )
                {
                    line.start();
                }
            }
        }
    }
    
    /** player runnable */
    private class PlayerRunnable implements Runnable
    {
        /** name of the runnable */
        private String name = null;
        
        /** create a new PlayerRunnable */
        public PlayerRunnable()
        {
            this(null);
        }
        
        /** create a new PlayerRunnable
         *  @param name the name of the runnable
         */
        public PlayerRunnable(String name)
        {
            this.name = name;
        }
        
        /**
         * Main loop.
         *
         * Player Status == STOPPED => End of Thread + Freeing Audio Ressources.<br>
         * Player Status == PLAYING => Audio stream data sent to Audio line.<br>
         * Player Status == PAUSED => Waiting for another status.
         */
        public void run()
        {
            logger.debug("runnable started");
            
            try
            {
                //if (m_audioInputStream.markSupported()) m_audioInputStream.mark(m_audioFileFormat.getByteLength());
                //else trace(1,getClass().getName(), "Mark not supported");
                int nBytesRead = 1;
                int nBytesCursor = 0;
                byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
		
		int frameSize = getAudioFormat().getFrameSize();
		
		System.out.println("sample rate         : " + getAudioFormat().getSampleRate());
		System.out.println("sample size in bits : " + getAudioFormat().getSampleSizeInBits());
		System.out.println("frame rate          : " + getAudioFormat().getFrameRate());
		System.out.println("frame size          : " + getAudioFormat().getFrameSize());
		
		Map<String, Object> map = null;
		
		System.out.println("audio format properties : ");
		map = getAudioFormat().properties();
		Iterator it = map.keySet().iterator();
		while(it.hasNext())
		{
		    Object current = it.next();
		    
		    System.out.println("\t" + current + " --> " + map.get(current));
		}
		System.out.println("audio file format properties : ");
		map = getAudioFileFormat().properties();
		it = map.keySet().iterator();
		while(it.hasNext())
		{
		    Object current = it.next();
		    
		    System.out.println("\t" + current + " --> " + map.get(current));
		}
		System.out.println("audio file format frame length : " + getAudioFileFormat().getFrameLength());
		

                /** until no bytes were read or if the related thread was interupted */
                while ( (nBytesRead != -1) && getPlayerStatus().equals(PlayerStatus.PLAYING) )
                {   
                    if ( Thread.currentThread().isInterrupted() )
                    {
                        throw new InterruptedException();
                    }

                    try
                    {
                        if (doSeek > -1)
                        {
                            // Seek implementation. WAV format only !
                            if ( (getAudioFileFormat() != null) && (getAudioFileFormat().getType().toString().startsWith("WAV")))
                            {
                                if ( (getTotalLengthInMilliSeconds() != AudioSystem.NOT_SPECIFIED) && (getTotalLengthInMilliSeconds() > 0))
                                {
                                    m_line.flush();
                                    m_line.stop();
                                    //m_audioInputStream.reset();
                                    m_audioInputStream.close();
				    
                                    m_audioInputStream = BlackdogAudioSystem.getAudioInputStream(_file);
                                    nBytesCursor = 0;
                                    if (m_audioFileFormat.getByteLength() - doSeek < abData.length)
                                    {
                                        doSeek = m_audioFileFormat.getByteLength() - abData.length;
                                    }
                                    doSeek = doSeek - doSeek % 4;
                                    int toSkip = (int) doSeek;
                                    // skip(...) instead of read(...) runs out of memory ?!
                                    while ( (toSkip > 0) && (nBytesRead > 0))
                                    {
                                        if (toSkip > abData.length)
                                        {
                                            nBytesRead = m_audioInputStream.read(abData, 0, abData.length);
                                        }
                                        else
                                        {
                                            nBytesRead = m_audioInputStream.read(abData, 0, toSkip);
                                        }
                                        toSkip = toSkip - nBytesRead;
                                        nBytesCursor = nBytesCursor + nBytesRead;
                                    }
                                    m_line.start();
                                }
                                else
                                {
                                    logger.error("seek not supported for this input stream : " + m_audioInputStream);
                                }
                            }
                            else
                            {
                                logger.error("seek not supported for this input stream : " + m_audioInputStream);
                            }
                            doSeek = -1;
                        }
                        nBytesRead = m_audioInputStream.read(abData, 0, abData.length);

    //          System.out.println("nBytesRead : " + nBytesRead);

                    }
                    catch(IOException e)
                    {
                        logger.error("io exception", e);
                    }
                    catch(UnsupportedAudioFileException e)
                    {
                        logger.error("unsupported audio file", e);
                    }
                    
                    // Update BasicPlayerListener
                    if (nBytesRead >= 0)
                    {
                        if (m_bpl != null)
                        {
                            m_bpl.updateMediaData(abData);
                        }
			
                        int nBytesWritten = m_line.write(abData, 0, nBytesRead);
                        long nbmilliseconds = m_line.getMicrosecondPosition() / 1000;
			
			/** if getTotalLengthInSeconds() == AudioSystem.NOT_SPECIFIED
			 *  try to fix it according to nBytesWritten, nbMilliseconds and the size of the inputStream
			 */
			if ( getTotalLengthInMilliSeconds() == AudioSystem.NOT_SPECIFIED &&
			     nBytesRead > 0 &&
			     nbmilliseconds > 0 )
			{
			    if ( playableBytesLength == -1 )
			    {
				playableLength = 100000000; // Shit
			    }
			    else
			    {
				System.out.println("playableBytesLength : " + playableBytesLength);
				System.out.println("nBytesRead : " + nBytesRead);
				System.out.println("nbmilliseconds : " + nbmilliseconds);
				System.out.println("mline frame position : " + m_line.getLongFramePosition());
				playableLength = ( ((float)playableBytesLength) / ((float)nBytesRead) ) * nbmilliseconds;
			    }
			}

                        System.err.println("nbmilliseconds : " + nbmilliseconds);
                        System.err.println("length total in milli seconds : " + getTotalLengthInMilliSeconds());

                        nBytesCursor = nBytesCursor + nBytesWritten;
			
			setPosition( Math.min(100d, ( (nbmilliseconds / getTotalLengthInMilliSeconds()) * 100 ) ) );
                        
			if (m_bpl != null)
                        {
//                            int a = (int) Math.round( (float) nBytesCursor / bytesPerSecond);
			    
//                            m_bpl.updateCursor(a , getTotalLengthInMilliSeconds());
//                            m_bpl.updateTime(nbmilliseconds);
                        }
                    }
                    
		    System.out.println("mline frame position : " + m_line.getLongFramePosition());
                    logger.debug("nBytesRead : " + nBytesRead);
                    logger.debug("player status : " + getPlayerStatus());
                }
                
                // end of while
                
                if (m_line != null)
                {
                    try
                    {
                        m_line.drain();
                        m_line.stop();
                        m_line.close();
			
			setPosition(100);

                        fireSongItemFullyRead(getItem());
                    }
                    catch (Exception e)
                    {
                        logger.error("cannot free audio resources", e);
                    }
                    finally
                    {
                        m_line = null;
                    }
                }
                
                logger.debug("thread stopped");
                
                if (m_bpl != null && nBytesRead == -1)
                {
                    m_bpl.updateMediaState("EOM");
                }
            }
            catch(InterruptedException e)
            {
                logger.debug("thread interrupted");
            }
        }
    }
  
  //////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////
    
    /* #########################################################################
     * ########################### Gain control ################################
     * ######################################################################### */
    
    // TODO : comment gain methods
    
    /**
     * Returns true if Gain control is supported.
     */
    public boolean hasGainControl()
    {
        return this.m_gainControl != null;
    }
    
    /**
     * Sets Gain value.
     * Linear scale 0.0  <-->  1.0
     * Threshold Coef. : 1/2 to avoid saturation.
     */
    public void setGain(double fGain)
    {
        if ( this.hasGainControl() )
        {
            double minGainDB = getMinimum();
            double ampGainDB = ( (10.0f / 20.0f) * getMaximum()) - getMinimum();
            double cste = Math.log(10.0) / 20;
            double valueDB = minGainDB + (1 / cste) * Math.log(1 + (Math.exp(cste * ampGainDB) - 1) * fGain);
            //trace(1,getClass().getName(), "Gain : "+valueDB);
            this.m_gainControl.setValue( (float) valueDB);
        }
    }
    
    /**
     * Returns Gain value.
     */
    public float getGain()
    {
        if ( this.hasGainControl() )
        {
            return this.m_gainControl.getValue();
        }
        else
        {
            return 0.0F;
        }
    }
    
    /**
     * Gets max Gain value.
     */
    public float getMaximum()
    {
        if ( this.hasGainControl() )
        {
            return this.m_gainControl.getMaximum();
        }
        else
        {
            return 0.0F;
        }
    }
    
    /**
     * Gets min Gain value.
     */
    public float getMinimum()
    {
        if ( this.hasGainControl() )
        {
            return this.m_gainControl.getMinimum();
        }
        else
        {
            return 0.0F;
        }
    }
    
    /* #########################################################################
     * ############################ Pan control ################################
     * ######################################################################### */
    
    // TODO : comment pan methods
    
    /**
     * Returns true if Pan control is supported.
     */
    public boolean hasPanControl()
    {
        return this.m_panControl != null;
    }
    
    /**
     * Returns Pan precision.
     */
    public float getPrecision()
    {
        if ( this.hasPanControl() )
        {
            return this.m_panControl.getPrecision();
        }
        else
        {
            return 0.0F;
        }
    }
    
    /**
     * Returns Pan value.
     */
    public float getPan()
    {
        if ( this.hasPanControl() )
        {
            return this.m_panControl.getValue();
        }
        else
        {
            return 0.0F;
        }
    }
    
    /**
     * Sets Pan value.
     * Linear scale : -1.0 <--> +1.0
     */
    public void setPan(float fPan)
    {
        if ( this.hasPanControl() )
        {
            //trace(1,getClass().getName(), "Pan : "+fPan);
            this.m_panControl.setValue(fPan);
        }
    }
    
    /* #########################################################################
     * ############################### Seek ####################################
     * ######################################################################### */
    
    // TODO : comment methods
    
    /**
     * Sets Seek value.
     * Linear scale : 0.0 <--> +1.0
     */
    public void setSeek(double seek) throws IOException
    {
        double length = -1;
        if ( ( this.m_audioFileFormat != null) && (this.m_audioFileFormat.getByteLength() != AudioSystem.NOT_SPECIFIED))
        {
            length = (double)this.m_audioFileFormat.getByteLength();
        }
        long newPos = (long) Math.round(seek * length);
        doSeek = newPos;
    }
    
    /* #########################################################################
     * ########################### Audio format ################################
     * ######################################################################### */
    
    // TODO : comment methods
    
    /**
     * Returns source AudioFormat.
     */
    public AudioFormat getAudioFormat()
    {
        // TODO
        if (m_audioFileFormat != null)
        {
            return m_audioFileFormat.getFormat();
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Returns source AudioFileFormat.
     */
    public AudioFileFormat getAudioFileFormat()
    {
        return this.m_audioFileFormat;
    }
    
    /**
     * Returns total length in milli-seconds.
     */
    public double getTotalLengthInMilliSeconds()
    {
	return this.playableLength;
    }
    
    /**
     * Returns bit rate.
     */
    public int getBitRate()
    {
        // TODO
        int bitRate = 0;
        if (getAudioFileFormat() != null)
        {
            int FL = (getAudioFileFormat()).getFrameLength();
            int FS = (getAudioFormat()).getFrameSize();
            float SR = (getAudioFormat()).getSampleRate();
            float FR = (getAudioFormat()).getFrameRate();
            int TL = (getAudioFileFormat()).getByteLength();
            String type = (getAudioFileFormat()).getType().toString();
            String encoding = (getAudioFormat()).getEncoding().toString();
            // Assumes that type includes xBitRate string.
            if ( (type != null) && ( (type.startsWith("MP3")) || (type.startsWith("VORBIS"))))
            {
                // BitRate string appended to type.
                // Another solution ?
                StringTokenizer st = new StringTokenizer(type, "x");
                if (st.hasMoreTokens())
                {
                    st.nextToken();
                    if ( st.hasMoreTokens() )
                    {   String bitRateStr = st.nextToken();
                        bitRate = Math.round( (Integer.parseInt(bitRateStr)));
                    }
                }
            }
            else
            {
                bitRate = Math.round(FS * FR * 8);
            }
            
            logger.debug("Type=" + type + " Encoding=" + encoding + " FL=" + FL + " FS=" + FS + " SR=" + 
                         SR + " FR=" + FR + " TL=" + TL + " :: bitRate=" + bitRate);
        }
        // N/A so computes bitRate for output.
        if ( (bitRate <= 0) && (m_line != null))
        {
            bitRate = Math.round( ( (m_line.getFormat()).getFrameSize()) * ( (m_line.getFormat()).getFrameRate()) * 8);
        }
        
//    System.out.println("bitRate : " + bitRate);
        
        return bitRate;
    }
    
//    /**
//     * Gets an InputStream from File.
//     */
//    protected InputStream openInput(File file) throws IOException
//    {
//        InputStream stream = null;
//        
//        if ( file != null && file.exists() && file.isFile() )
//        {
//            InputStream fileIn = new FileInputStream(file);
//            stream = new BufferedInputStream(fileIn);
//        }
//        return stream;
//    }
}
