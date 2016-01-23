/*
 * blackdog : audio player / manager
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
package org.blackdog.type.session;

import java.lang.ref.WeakReference;
import org.apache.log4j.Logger;
import org.blackdog.type.PlayList;
import org.blackdog.type.Playable;
import org.blackdog.ui.PlayListEditor;
import org.siberia.editor.Editor;
import org.siberia.editor.event.EditorClosureListener;
import org.siberia.editor.exception.ClosingRefusedException;
import org.siberia.ui.swing.table.SibTypeListTable;

/**
 *
 * Playable session applied to a PlayListEditor
 *
 * @author alexis
 */
public class PlayListEditorPlayableSession extends org.blackdog.type.session.PlayListPlayableSession
					   implements EditorClosureListener
{
    /** logger */
    private transient Logger                logger   = Logger.getLogger(PlayListEditorPlayableSession.class);
    
    /** weak reference on the PlayListEditor */
    private WeakReference<PlayListEditor>   editorRef = null;
    
    /** Creates a new instance of PlayListEditorPlayableSession
     *	@param playlist the playlist managed by the given editor
     *	@param editor a PlayListEditor
     */
    public PlayListEditorPlayableSession(PlayList playlist, PlayListEditor editor)
    {	
	super(playlist);
	
	this.setPlayListEditor(editor);
    }
    
    /** set the editor 
     *	@param editor a PlayListEditor
     */
    public void setPlayListEditor(PlayListEditor editor)
    {
	PlayListEditor curEditor = this.editorRef == null ? null : this.editorRef.get();
	
	if ( curEditor != editor )
	{
	    if ( curEditor != null )
	    {
		curEditor.removeEditorClosureListener(this);
	    }
	
	    this.editorRef = new WeakReference<PlayListEditor>(editor);

	    if ( editor != null )
	    {
		editor.addEditorClosureListener(this);
	    }
	}
    }
    
    /** return a SibTypeListTable
     *	@return a SibTypeListTable
     */
    private SibTypeListTable getTable()
    {
	SibTypeListTable table = null;
	
	PlayListEditor editor = this.editorRef.get();
	if ( editor != null )
	{
	    table = editor.getTable();
	}
	
	return table;
    }
    
    /** return the index of the playable for the given table
     *	@param table a SibTypeListTable
     *	@param playable a Playable
     *	@return an integer
     */
    private int getIndexOf(SibTypeListTable table, Playable playable)
    {
	int result = -1;
	
	if ( table != null && playable != null )
	{
	    result = table.getAbsoluteIndexOfItem(playable);
	}
	
	return result;
    }
    
    /** return the item at a given position
     *	@param table a SibTypeListTable
     *	@param index an integer
     *	@return an integer
     */
    private Object getItemAt(SibTypeListTable table, int index)
    {
	Object result = null;
	
	result = table.getItemAtAbsoluteIndex(index);
	
	return result;
    }

    /**
     * return a random playable for this session<br>
     * 
     * @return a random Playable
     */
    @Override
    protected Playable getRandomPlayable()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getRandomPlayable()");
	}
	
	Playable playable = null;
	
	SibTypeListTable t = this.getTable();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("table of editor is : " + t);
	}
	if ( t != null )
	{
	    int randomIndex = this.getRandomNumber(0, t.getAllRowsCount());
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("considering random index : " + randomIndex);
	    }
	    
	    Object o = null;

	    try
	    {
		o = this.getItemAt(t, randomIndex);
	    }
	    catch(Exception e)
	    {
		logger.warn("unable to get the first playable", e);
	    }

	    if ( o instanceof Playable )
	    {
		playable = ((Playable)o);
	    }
	}
	else
	{
	    playable = super.getRandomPlayable();
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getRandomPlayable returns " + (playable == null ? null : playable.getName()));
	}
	
	return playable;
    }

    /**
     * return the last playable for this session<br>
     * 
     * @return the last Playable
     */
    @Override
    protected Playable getLastPlayable()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getLastPlayable()");
	}
	Playable playable = null;
	
	SibTypeListTable t = this.getTable();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("table of editor is : " + t);
	}
	if ( t != null )
	{
	    if ( t.getAllRowsCount() > 0 )
	    {
		Object o = null;
		
		try
		{
		    o = this.getItemAt(t, t.getAllRowsCount() - 1);
		}
		catch(Exception e)
		{
		    logger.warn("unable to get the first playable", e);
		}
		
		if ( o instanceof Playable )
		{
		    playable = ((Playable)o);
		}
	    }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("table does not have any rows");
		}
	    }
	}
	else
	{
	    playable = super.getLastPlayable();
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getLastPlayable returns " + (playable == null ? null : playable.getName()));
	}
	
	return playable;
    }

    /**
     * return the first playable for this session
     * 
     * @return the first Playable
     */
    @Override
    protected Playable getFirstPlayable()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getFirstPlayable()");
	}
	Playable playable = null;
	
	SibTypeListTable t = this.getTable();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("table of editor is : " + t);
	}
	if ( t != null )
	{
	    if ( t.getAllRowsCount() > 0 )
	    {
		Object o = null;
		
		try
		{
		    o = this.getItemAt(t, 0);
		}
		catch(Exception e)
		{
		    logger.warn("unable to get the first playable", e);
		}
		
		if ( o instanceof Playable )
		{
		    playable = ((Playable)o);
		}
	    }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("table does not have any rows");
		}
	    }
	}
	else
	{
	    playable = super.getFirstPlayable();
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getFirstPlayable returns " + (playable == null ? null : playable.getName()));
	}
	
	return playable;
    }

    /**
     * return the previous playable without considering shuffle or repeat mode
     * 
     * @param playable a Playable
     * @return a Playable
     */
    @Override
    protected Playable getPreviousPlayable(Playable playable)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getPreviousPlayable(" + playable + ")");
	}
	Playable result = null;
	
	SibTypeListTable t = this.getTable();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("table of editor is : " + t);
	}
	if ( t != null )
	{
	    int index = this.getIndexOf(t, playable);
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("index of given playable : " + index);
	    }
	    
	    if ( index > 0 && index < t.getAllRowsCount() - 1 )
	    {
		Object o = null;
		
		try
		{
		    o = this.getItemAt(t, index - 1);
		}
		catch(Exception e)
		{
		    logger.warn("unable to get the next playable after " + (playable == null ? null : playable.getName()), e);
		}
		
		if ( o instanceof Playable )
		{
		    result = (Playable)o;
		}
	    }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("index of given playable is invalid : consider firstPlayable");
		}
		
		/* the last playable is out of bounds go to first playable */
		result = this.getFirstPlayable();
	    }
	}
	else
	{
	    result = super.getPreviousPlayable(playable);
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getPreviousPlayable from " + (playable == null ? null : playable.getName()) + " returns " + 
						       (result == null ? null : result.getName()));
	}
	
	return result;
    }

    /**
     * return the next playable without considering shuffle or repeat mode
     * 
     * @param playable a Playable
     * @return a Playable
     */
    @Override
    protected Playable getNextPlayable(Playable playable)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getNextPlayable(" + playable + ")");
	}
	Playable result = null;
	
	SibTypeListTable t = this.getTable();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("table of editor is : " + t);
	}
	if ( t != null )
	{
	    int index = this.getIndexOf(t, playable);
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("index of given playable : " + index);
	    }
	    
	    if ( index >= 0 && index < t.getAllRowsCount() - 1 )
	    {
		Object o = null;
		
		try
		{
		    o = this.getItemAt(t, index + 1);
		}
		catch(Exception e)
		{
		    logger.warn("unable to get the next playable after " + (playable == null ? null : playable.getName()), e);
		}
		
		if ( o instanceof Playable )
		{
		    result = (Playable)o;
		}
	    }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("index of given playable is invalid : consider firstPlayable");
		}
		/* the last playable is out of bounds go to first playable */
		result = this.getFirstPlayable();
	    }
	}
	else
	{
	    result = super.getNextPlayable(playable);
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getNextPlayable from " + (playable == null ? null : playable.getName()) + " returns " + 
						   (result == null ? null : result.getName()));
	}
	
	return result;
    }
    
    /** ########################################################################
     *  ############### EditorClosureListener implementation ###################
     *  ######################################################################## */
    
    /** method called when the editor request to be closed.
     *  this request was accepted
     *  @param editor the editor that requested to be closed
     */
    public void editorRequestedToClose(Editor editor)
    {	}
    
    /** method called when the editor request to be closed
     *  @param editor the editor that requested to be closed
     *  
     *  @exception ClosingRefusedException if the listener refused the editor to be closed
     */
    public void editorRequestingToClose(Editor editor) throws ClosingRefusedException
    {	}
    
    /** method called when the editor is really closed
     *  @param editor the editor that requested to be closed
     *  
     */
    public void editorClosed(Editor editor)
    {	
	if ( editor != null )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("editor was closed --> consider now playlist");
	    }
	    editor.removeEditorClosureListener(this);
	    this.editorRef = new WeakReference<PlayListEditor>(null);
	}
    }
    
}
