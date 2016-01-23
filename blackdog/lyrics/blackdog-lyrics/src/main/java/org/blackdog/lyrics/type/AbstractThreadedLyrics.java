/*
 * blackdog lyrics : define editor and systems to get lyrics for a song
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
package org.blackdog.lyrics.type;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.siberia.type.annotation.bean.Bean;

/**
 *
 * Default representation fo lyrics.
 *
 *  this class provide mecanism to retrieve information in its own thread.
 *  just implements _retrieve() methods without considering multi-threading problems.
 *
 * @author alexis
 */
@Bean(  name="lyrics",
        internationalizationRef="org.blackdog.rc.i18n.type.AbstractThreadedLyrics",
        expert=false,
        hidden=true,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public abstract class AbstractThreadedLyrics extends AbstractLyrics
{   
    /** executor service */
    private ExecutorService       service          = Executors.newSingleThreadExecutor();
    
    /** current future */
    private WeakReference<Future> currentFutureRef = null;
    
    /**
     * Creates a new instance of AbstractThreadedLyrics
     */
    public AbstractThreadedLyrics()
    {	}
    
    /** indicate to the lyrics to try to retrieve content about its current SongItem */
    public final synchronized void retrieve()
    {
	if ( this.currentFutureRef != null )
	{
	    Future future = this.currentFutureRef.get();
	    
	    if ( future != null && ! future.isDone() )
	    {
		future.cancel(true);
	    }
	}
	
	Runnable runnable = new Runnable()
	{
	    public void run()
	    {
		_retrieve();
	    }
	};
	
	this.currentFutureRef = new WeakReference<Future>(this.service.submit(runnable));
    }
    
    /** retrieve the content according to the current song item caracteristics */
    protected abstract void _retrieve();
    
}
