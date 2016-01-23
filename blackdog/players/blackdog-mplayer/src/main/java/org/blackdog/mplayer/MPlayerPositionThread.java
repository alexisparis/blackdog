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

import org.blackdog.player.PlayerStatus;

class MPlayerPositionThread extends Thread
{
    
    private static final int STEP = 200;
    
    private BlackdogMPlayer handler;
    
    MPlayerPositionThread(BlackdogMPlayer handler)
    {
	this.handler = handler;
	this.setName("mplayer positioner");
    }
    
    @Override
    public void run()
    {
	try
	{
	    while (true)
	    {
		if( Thread.currentThread().isInterrupted() )
		{
		    break;
		}
		
		boolean sendGetPosition = true;
		PlayerStatus status = handler.getPlayerStatus();
		
		if ( status != null && PlayerStatus.PAUSED.equals(status) )
		{
		    sendGetPosition = false;
		}
		
		if ( sendGetPosition )
		{
		    handler.sendGetPositionCommand();
		}
		Thread.sleep(STEP);
	    }
	}
	catch (InterruptedException e)
	{
	    handler.notifyError(e);
	}
    }
}
