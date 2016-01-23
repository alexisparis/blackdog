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
package org.blackdog.lyrics.action;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.blackdog.lyrics.type.AbstractLyrics;
import org.blackdog.lyrics.type.Lyrics;
import org.blackdog.type.SongItem;
import org.siberia.type.SibType;
import org.siberia.ui.action.impl.TypeEditingAction;

/**
 *
 * abstract action to edit a type.
 *
 * It manages its editability according to the kind of editors installed
 *
 * @author alexis
 */
public abstract class AbstractLyricsEditingAction extends TypeEditingAction<SibType>
{
    /** logger */
    private Logger logger = Logger.getLogger(AbstractLyricsEditingAction.class);
    
    /** Creates a new instance of DefaultTypeEditingAction */
    public AbstractLyricsEditingAction()
    {   }
    
    /** set the type related to this action
     *  @return a SibType
     */
    @Override
    public void setTypes(SibType[] types)
    {   super.setTypes(types);
    
        boolean enabled = false;
        
        List<SibType> items = this.getTypes();
        if ( items != null )
        {   if ( items.size() > 0 )
            {   SibType type = items.get(0);
                
                if ( type instanceof SongItem )
                {   enabled = true; }
            }
        }
        
        this.setEnabled(enabled);
    }
    
    /**
     * create the lyrics which will be linked to the SongItem
     * 
     * @return a Lyrics object
     */
    protected abstract Lyrics createLyrics();
    
    /** returns the items to edit
     *  this methods is only called when the action si to be run and allow to link one list
     *  of items to the action and edit another kind of objects
     *  @return a List of SibType
     */
    public List<SibType> getItemsToEdit()
    {   
	List<SibType> editingItems = null;
        
        List<SibType> superItems = super.getTypes();
	
        if ( superItems != null )
        {   for(int i = 0; i < superItems.size(); i++)
            {   Object current = superItems.get(i);
	        
                if ( current instanceof SongItem )
                {   if ( editingItems == null )
                    {   editingItems = new ArrayList<SibType>(); }
                    
                    Lyrics lyrics = this.createLyrics();
		    
                    lyrics.setSongItem( (SongItem)current );
		    
		    /** initialize the name of the lyrics object */
		    try
		    {
			lyrics.setName( ((SongItem)current).getName() + " lyrics");
		    }
		    catch(PropertyVetoException e)
		    {
			logger.error("unable to change the name of Lyrics object", e);
		    }
		    
		    /** ask the lyrics to retrieve lyrics content */
		    lyrics.retrieve();
		    
                    editingItems.add((SibType)lyrics);
                }
            }
        }
        
        if ( editingItems == null )
        {   editingItems = Collections.emptyList(); }
        
        return editingItems;
    }
    
}
