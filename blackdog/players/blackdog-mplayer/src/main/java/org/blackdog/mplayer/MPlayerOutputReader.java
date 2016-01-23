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
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author alexis
 */
public class MPlayerOutputReader extends Thread
{
    protected BufferedReader in;
    
    protected int length;
    protected int time;
    
    /* player */
    BlackdogMPlayer player = null;
    
    /** Creates a new instance of MPlayerOutputReader */
    public MPlayerOutputReader(BlackdogMPlayer player, Process process)
    {
	this.setName("mplayer output reader");
	this.player = player;
	this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }
    
    protected void read(String line)
    {
	// Read progress
	// MPlayer bug: Duration still inaccurate with mp3 VBR files! Flac duration bug
	if (line.matches(".*ANS_TIME_POSITION.*"))
	{
	    time = (int) (Float.parseFloat(line.substring(line.indexOf("=") + 1)) * 1000.0);
	    player.updatePosition(time);
	}
	
	// End
	if (line.matches(".*\\x2e\\x2e\\x2e.*\\(.*\\x20.*\\).*"))
	{
	    player.notifyItemFullyRead(player.getItem(), this.time);
	}
	
	// Read length
	if (line.matches(".*ANS_LENGTH.*")) {
		// Length still inaccurate with mp3 VBR files!
		length = (int) (Float.parseFloat(line.substring(line.indexOf("=") + 1)) * 1000.0);
//		handler.setCurrentDuration(length);
//		handler.setDuration(audioFile.getDuration() * 1000);
	}

//	// MPlayer bug: Workaround (for audio files) for "mute bug" [1868482] 
//	if (handler.isMute() && length > 0 && length - time < 1000) {
//		logger.debug(LogCategories.PLAYER, "MPlayer 'mute bug' workaround applied");
//		handler.next(true);
//		interrupt();
//	}
    }
    
    @Override
    public final void run()
    {
	String line = null;
	try
	{
	    while ((line = in.readLine()) != null && !isInterrupted())
	    {
		if( Thread.currentThread().isInterrupted() )
		{
		    break;
		}
		
		read(line);
	    }
	}
	catch (final IOException e)
	{
	    player.notifyError(e);
	}
	finally
	{
	    try
	    {
		in.close();
	    }
	    catch(Exception e)
	    {
		e.printStackTrace();
	    }
	}
    }
    
}
