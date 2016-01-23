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

import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

class MPlayerErrorReader extends Thread
{
    /** logger */
    private Logger logger = Logger.getLogger(MPlayerErrorReader.class);
    
    private BlackdogMPlayer player;
    private BufferedReader in;
    
    MPlayerErrorReader(BlackdogMPlayer player, Process process)
    {
	this.setName("mplayer error reader");
	this.player = player;
	in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    }
    
    @Override
    public void run()
    {
	String line = null;
	try
	{
	    while ((line = in.readLine()) != null)
	    {
		if( Thread.currentThread().isInterrupted() )
		{
		    break;
		}
		
		logger.error(line);
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
