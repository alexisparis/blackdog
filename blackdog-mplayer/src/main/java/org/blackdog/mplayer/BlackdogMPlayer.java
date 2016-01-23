/*
 * blackdog mplayer : define a player based on MPlayer
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
package org.blackdog.mplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.blackdog.BlackdogMPlayerPlugin;
import org.blackdog.player.AbstractPlayer;
import org.blackdog.type.AudioItem;
import org.blackdog.type.Playable;
import org.blackdog.type.TimeBasedItem;
import org.blackdog.type.base.AudioDuration;
import org.siberia.OperatingSystem;
import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;
import org.siberia.utilities.io.IOUtilities;

/**
 *
 * Blackdog player based on Mplayer.
 *
 *  Inspired from the mplayer of aTunes.
 *
 * @author alexis
 */
public class BlackdogMPlayer extends AbstractPlayer
{   
    /** windows mplayer version */
    private static final String MPLAYER_VERSION = "";
    
    /**
     * Command to be executed on Linux systems to launch mplayer. Mplayer should
     * be in $PATH
     */
    private static final String LINUX_COMMAND = "mplayer";

    /**
     * Command to be executed on Windows systems to launch mplayer. Mplayer is
     * in "win_tools" dir, inside aTunes package
     */
    private static       String WIN_COMMAND = null;

    /**
     * Command to be executed on Mac systems to launch mplayer.
     */
    private static final String MACOS_COMMAND = "mplayer -ao macosx";

    /**
     * Command to be executed on Solaris systems to launch mplayer. Note the
     * workaround with the options - Java6 on Solaris Express appears to require
     * these options added separately.
     */
    private static final String SOLARIS_COMMAND = "mplayer";
    private static final String SOLARISOPTAO = "-ao";
    private static final String SOLARISOPTTYPE = "sun";

    // Arguments for mplayer

    /**
     * Argument to not display more information than needed.
     */
    private static final String QUIET = "-quiet";

    /**
     * Argument to control mplayer through commands
     */
    private static final String SLAVE = "-slave";

    /**
     * Argument to pass mplayer a play list
     */
    private static final String PLAYLIST = "-playlist";

    /**
     * Arguments to filter audio output
     */
    private static final String AUDIO_FILTER = "-af";
    private static final String VOLUME_NORM = "volnorm";
    private static final String KARAOKE = "karaoke";
    private static final String EQUALIZER = "equalizer=";
    
    /** logger */
    private Logger             logger            = Logger.getLogger(BlackdogMPlayer.class);
	
    /** mplayer process */
    private Process               mPlayerProcess = null;
    
    /** MPlayerOutputReader */
    private MPlayerOutputReader   outputReader   = null;
    
    /* MPlayerErrorReader */
    private MPlayerErrorReader	  errorReader    = null;
    
    /** position thread */
    private MPlayerPositionThread positionThread = null;
    
    /** length in milli-seconds of the current item or -1 if undefined */
    private long                  audioLength    = -1;
    
    /** Creates a new instance of BlackdogMPlayer */
    public BlackdogMPlayer()
    {	}
    
    /** configure the player */
    public void configure()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling configure()");
	}
	super.configure();
	
	/* if the os is windows, then copy mplayer.exe to .blackdog directory */
	OperatingSystem os = ResourceLoader.getInstance().getOperatingSystem();
	if ( OperatingSystem.WINDOWS.equals(os) || OperatingSystem.WINDOWS_VISTA.equals(os) )
	{
	    boolean copyMPlayer = true;
	    
	    String mplayerDir = ResourceLoader.getInstance().getApplicationHomeDirectory() + 
				    System.getProperty("file.separator") + "mplayer";
	    
	    File dir		   = new File(mplayerDir);
	    File mplayerExe	   = new File(dir, "mplayer.exe");
	    File mplayerProperties = new File(dir, "mplayer.properties");
	    
	    WIN_COMMAND = mplayerExe.getAbsolutePath();
	    
	    if ( dir.exists() )
	    {
		/* see if there is a mplayer.exe file in this directory */
		if ( mplayerExe.exists() )
		{
		    /* see if there is a mplayer.properties file */
		    if ( mplayerProperties.exists() )
		    {
			/** load properties and see if the version is good */
			Properties properties = new Properties();
			
			InputStream stream = null;
			
			try
			{
			    stream = new FileInputStream(mplayerProperties);
			    properties.load(stream);
			    
			    String version = properties.getProperty("version");
			    
			    if ( version != null && MPLAYER_VERSION.equals(version) )
			    {
				/** all is good --> no need to copy mplayer.exe */
				copyMPlayer = false;
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
				    logger.error("unable to close properties input stream", e);
				}
			    }
			}
		    }
		    else
		    {
			
		    }
		}
		else
		{
		    
		}
	    }
	    else
	    {
		dir.mkdirs();
	    }
	    
	    if ( copyMPlayer )
	    {
		String rcPath = BlackdogMPlayerPlugin.PLUGIN_ID + ";1::/win/mplayer.exe";
		
		OutputStream outputStream = null;
		InputStream  inputStream  = null;
		
		try
		{
		    URL url = ResourceLoader.getInstance().getRcResource(rcPath);
		    
		    if ( ! mplayerExe.exists() )
		    {
			mplayerExe.createNewFile();
		    }
		    
		    inputStream = url.openStream();
		    
		    IOUtilities.copy(inputStream, mplayerExe);
		    
		    /** and create the properties file with the actual version */
		    if ( ! mplayerProperties.exists() )
		    {
			mplayerProperties.createNewFile();
		    }
		    
		    Properties properties = new Properties();
		    properties.setProperty("version", MPLAYER_VERSION);
		    
		    outputStream = new FileOutputStream(mplayerProperties);
		    properties.store(outputStream, "mplayer blackdog configuration");
		}
		catch(ResourceException e)
		{
		    logger.error("unable to get resource '" + rcPath + "'", e);
		}
		catch(IOException e)
		{
		    logger.error("error while copying mplayer context", e);
		}
		finally
		{
		    if ( outputStream != null )
		    {
			try
			{
			    outputStream.close();
			}
			catch(IOException e)
			{
			    logger.error("unable to close properties output stream", e);
			}
		    }
		    if ( inputStream != null )
		    {
			try
			{
			    inputStream.close();
			}
			catch(IOException e)
			{
			    logger.error("unable to close properties input stream", e);
			}
		    }
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of configure()");
	}
    }

    @Override
    public void dispose()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling dispose()");
	}
	super.dispose();
	
	if ( this.positionThread != null )
	{
	    this.positionThread.interrupt();
	    this.positionThread = null;
	}
	if ( this.mPlayerProcess != null )
	{
	    this.mPlayerProcess.destroy();
	}
	if ( this.outputReader != null )
	{
	    this.outputReader.interrupt();
	    this.outputReader = null;
	}
	if ( this.errorReader != null )
	{
	    this.errorReader.interrupt();
	    this.errorReader = null;
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of dispose()");
	}
    }

    /** initialize the item currently associated with the player
     *  @param item a Playable
     */
    @Override
    public void setItem(final Playable item)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling setItem("+ item + ")");
	}
	super.setItem(item);
	
	if ( this.positionThread == null )
	{
	    this.positionThread = new MPlayerPositionThread(this);
	    this.positionThread.start();
	}
	
	this.audioLength = -1;
	    
	if ( item != null )
	{
	    if ( item instanceof TimeBasedItem )
	    {
		AudioDuration duration = ((TimeBasedItem)item).getDuration();
		
		if ( duration != null )
		{
		    this.audioLength = duration.getTimeInMilli();
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of setItem("+ item + ")");
	}
    }
    
    /** set the position
     *	@param position the position in milli-seconds
     */
    void updatePosition(int milliseconds)
    {
	if ( logger.isDebugEnabled() )
	{
//	    logger.debug("calling updatePosition("+ milliseconds + ")");
	}
	/* determine the progression for this player */
	if ( this.audioLength <= 0 || milliseconds <= 0 )
	{
	    this.setPosition(0);
	}
	else
	{
	    double percentage = ( ((double)(milliseconds)) / ((double)(this.audioLength) ) ) * 1000;
	    
	    this.setPosition(percentage);
	}
	if ( logger.isDebugEnabled() )
	{
//	    logger.debug("end of updatePosition("+ milliseconds + ")");
	}
    }
    
    /** ask player if it accepts to be used<br>
     *	this method can be overriden if a player is platform specific
     *	@return true if the player accept to be use
     */
    @Override
    public boolean acceptToBeUsed()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling acceptToBeUsed()");
	}
	boolean result = super.acceptToBeUsed();
	
	if ( result )
	{
	    OperatingSystem os = ResourceLoader.getInstance().getOperatingSystem();
	    
	    if ( ! OperatingSystem.WINDOWS.equals(os) && ! OperatingSystem.WINDOWS_VISTA.equals(os) )
	    {
		BufferedReader stdInput = null;
		try
		{
		    Process p = new ProcessBuilder(LINUX_COMMAND).start();
		    stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

		    while (stdInput.readLine() != null)
		    {
			// Nothing to do
		    }

		    int code = p.waitFor();
		    if (code != 0)
		    {
			result = false;
		    }
		}
		catch (Exception e)
		{
		    result = false;
		}
		finally
		{
		    if ( stdInput != null )
		    {
			try
			{
			    stdInput.close();
			}
			catch(IOException e)
			{
			    //
			}
		    }
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("acceptToBeUsed() returns " + result);
	}
	
	return result;
    }
    
    /** notify error
     *	@param exception an Exception
     */
    void notifyError(Exception e)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling notifyError(" + e + ")");
	}
	this.error(e);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of notifyError(" + e + ")");
	}
    }
    
    /** notify item fully read
     *	@param
     */
    void notifyItemFullyRead(Playable item, long milliSecondsEstimation)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling notifyItemFullyRead(" + item + ", " + milliSecondsEstimation + ")");
	}
	this.fireSongItemFullyRead(item, milliSecondsEstimation);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of notifyItemFullyRead(" + item + ", " + milliSecondsEstimation + ")");
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
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling stopImpl()");
	}
	this.sendStopCommand();
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of stopImpl()");
	}
    }

    /**
     * resume
     */
    public void resumeImpl()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling resumeImpl()");
	}
	this.sendResumeCommand();
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of resumeImpl()");
	}
    }

    /**
     * Plays an audio sample from the beginning. do not care about the status of the player
     */
    public void playImpl()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling playImpl()");
	}
	try
	{
	    // Send stop command in order to try to avoid two mplayer
	    // instaces are running at the same time
	    sendStopCommand();
	    
	    // Start the play process
	    this.mPlayerProcess = getProcess(this.getItem());
	    
	    if ( this.outputReader != null )
	    {
		this.outputReader.interrupt();
	    }
	    this.outputReader = new MPlayerOutputReader(this, mPlayerProcess);
	    
	    if ( this.errorReader != null )
	    {
		this.errorReader.interrupt();
	    }
	    this.errorReader = new MPlayerErrorReader(this, mPlayerProcess);
	    
	    this.outputReader.start();
	    this.errorReader.start();
	    
	    sendGetDurationCommand();
	}
	catch (Exception e)
	{
	    this.error(e);
	    this.stop();
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of playImpl()");
	}
    }

    /**
     * Stop temporarly or start at the current position the current audio sample. do not care about the status of the player
     */
    public void pauseImpl()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling pauseImpl()");
	}
	this.sendPauseCommand();
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of pauseImpl()");
	}
    }

    /**
     * apply the new gain on the audio system to really change the volume
     * 
     * @param newValue a double
     */
    protected void updateVolumeGain(double newValue)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling updateVolumeGain(" + newValue + ")");
	}
	
	int _newValue = (int)(newValue * 100);
	
	int volume = 0;
	
	if ( _newValue < 0 )
	{
	    volume = 0;
	}
	else if ( _newValue > 100 )
	{
	    volume = 100;
	}
	else
	{
	    volume = _newValue;
	}
	
	this.sendVolumeCommand(volume);
	
	// MPlayer bug: paused, volume change -> starts playing
//	if (paused)
//	{
//	    sendPauseCommand();
//	}
	// MPlayer bug: muted, volume change -> demuted
//	if ( muted )
//	{
//	    sendMuteCommand();
//	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of updateVolumeGain(" + newValue + ")");
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
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling playAtImpl(" + position + ")");
	}
	this.sendSeekCommand(position);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling playAtImpl(" + position + ")");
	}
    }

    /**
     * Returns a mplayer process to play an audiofile
     * 
     * @param audioObject
     *            audio object which should be played
     * @return mplayer process
     * @throws IOException
     */
    private Process getProcess(Playable item) throws IOException
    {	
	Process process = null;
	
	URL url = null;
	
	if ( item instanceof AudioItem )
	{
	    url = ((AudioItem)item).getValue();
	}
	
	if ( url != null )
	{
	    ProcessBuilder builder = new ProcessBuilder();

	    List<String> command = new ArrayList<String>(10);

	    /** determine the program command for the operating system */
	    OperatingSystem os = ResourceLoader.getInstance().getOperatingSystem();

	    if ( OperatingSystem.WINDOWS.equals(os) || OperatingSystem.WINDOWS_VISTA.equals(os) )
	    {
		command.add(WIN_COMMAND);
	    }
	    else if ( OperatingSystem.SOLARIS.equals(os) )
	    {
		command.add(SOLARIS_COMMAND);
		command.add(SOLARISOPTAO);
		command.add(SOLARISOPTTYPE);
	    }
	    else if ( OperatingSystem.MACOSX.equals(os) )
	    {
		command.add(MACOS_COMMAND);
	    }
	    else if ( OperatingSystem.LINUX.equals(os) )
	    {
		command.add(LINUX_COMMAND);
	    }

	    command.add(QUIET);
	    command.add(SLAVE);
	    
	    boolean itemCommandAdded = false;
	    
	    if ( "file".equals(url.getProtocol()) )
	    {
		try
		{
		    java.io.File f = new java.io.File(url.toURI());
		    command.add(f.getAbsolutePath());
		    itemCommandAdded = true;
		}
		catch(Exception e)
		{
		    logger.error("got exception while creating file according to url '" + url + "'", e);
		}
	    }
	    
	    if ( ! itemCommandAdded )
	    {
		command.add(url.toString());
	    }

	    command.add(VOLUME_NORM);
	    
    //	// Build equalizer command. Mplayer uses 10 bands
    //	if (audioObject instanceof AudioFile && getEqualizer() != null)
    //		command.add(EQUALIZER + getEqualizer()[0] + ":" + getEqualizer()[1] + ":" + getEqualizer()[2] + ":" + getEqualizer()[3] + ":" + getEqualizer()[4] + ":"
    //				+ getEqualizer()[5] + ":" + getEqualizer()[6] + ":" + getEqualizer()[7] + ":" + getEqualizer()[8] + ":" + getEqualizer()[9]);

	    process = builder.command(command).start();
	}
	
	return process;
    }
    
    /* #########################################################################
     * ######################### mplayer commands ##############################
     * ######################################################################### */
    
    private void sendCommand(String command)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling sentCommand(" + command + ")");
	}
	if ( this.mPlayerProcess != null)
	{
	    PrintStream out = new PrintStream(this.mPlayerProcess.getOutputStream());
	    StringBuilder sb = new StringBuilder();
	    sb.append(command).append('\n');
	    out.print(sb.toString());
	    out.flush();
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of sentCommand(" + command + ")");
	}
    }
    
    private void sendGetDurationCommand()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling sentGetdurationCommand()");
	}
	sendCommand("get_time_length");
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of sentGetdurationCommand()");
	}
    }
    
    void sendGetPositionCommand()
    {
	if ( logger.isDebugEnabled() )
	{
//	    logger.debug("calling sentGetPositioncommand()");
	}
//	if ( ! paused )
	    sendCommand("get_time_pos");
	if ( logger.isDebugEnabled() )
	{
//	    logger.debug("end of sentGetPositioncommand()");
	}
    }
    
    private void sendMuteCommand()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling sentMuteCommand()");
	}
	sendCommand("mute");
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of sentMuteCommand()");
	}
    }
    
    private void sendPauseCommand()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling sentPauseCommand()");
	}
	sendCommand("pause");
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of sentPauseCommand()");
	}
    }
    
    private void sendResumeCommand()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling sentResumeCommand()");
	}
	sendCommand("pause");
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of sentResumeCommand()");
	}
    }
    
    private void sendSeekCommand(double perCent)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling sentSeekCommand(" + perCent + ")");
	}
	String[] strings = new String[]{"seek ", Double.toString(perCent * 100), " 1"};
	
	StringBuilder objStringBuilder = new StringBuilder();

	for (Object element : strings)
		objStringBuilder.append(element);
	
	String command = objStringBuilder.toString();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("sending seek command '" + command + "'");
	}
	
	sendCommand(command);
	
//	if (paused)
//	    sendPauseCommand();
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of sentSeekCommand(" + perCent + ")");
	}
    }
    
    private void sendStopCommand()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling sentStopCommand()");
	}
	sendCommand("quit");
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of sentStopCommand()");
	}
    }
    
    private void sendVolumeCommand(int perCent)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling sentVolumeCommand()");
	}
	String[] strings = new String[]{"volume ", Integer.toString(perCent), " 1"};
	
	StringBuilder objStringBuilder = new StringBuilder();

	for (Object element : strings)
	{
	    objStringBuilder.append(element);
	}
	
	String command = objStringBuilder.toString();
	
	sendCommand(command);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of sentVolumeCommand()");
	}
    }
    
    public void setMute(boolean mute)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling setMute(" + mute + ")");
	}
	sendMuteCommand();
	
	// MPlayer bug: paused, demute, muted -> starts playing
//	if (paused && !mute && muted)
	{
	    sendPauseCommand();
	}
	
//	muted = mute;
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of setMute(" + mute + ")");
	}
    }
    
}
