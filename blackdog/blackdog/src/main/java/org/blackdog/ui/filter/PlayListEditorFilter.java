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
package org.blackdog.ui.filter;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.table.TableModel;
import org.apache.log4j.Logger;
import org.blackdog.type.SongItem;
import org.blackdog.ui.FilterListModel;
import org.blackdog.ui.list.AlbumList;
import org.blackdog.ui.list.ArtistList;
import org.jdesktop.swingx.decorator.Filter;
import org.siberia.base.LangUtilities;
import org.siberia.ui.swing.table.filter.AbstractFilter;
import org.siberia.ui.swing.table.model.SibTypeListTableModel;

/**
 *
 * Filter that use the string critera enter by user and
 *  use the artist and album given
 *
 * @author alexis
 */
public class PlayListEditorFilter extends AbstractFilter
{   
    /** logger */
    private static Logger logger = Logger.getLogger(PlayListEditorFilter.class.getName());
    
    /** name of the property that is related to the input */
    public static final String    PROPERTY_INPUT   = "input";
    
    /** the current input used to filter data */
    private String                input            = null;
    
    /** the input element */
    private String[]              processedInput   = null;
    
    /** list for authors */
    private ArtistList            artistList       = null;
    
    /** list for albums */
    private AlbumList             albumList        = null;
    
    /** artist */
    private Set<String>           artists          = null;
    
    /** album */
    private Set<String>           albums           = null;
    
    /** accept all items on the model */
    private boolean               acceptAll        = false;
    
    /**
     * Creates a new instance of PlayListEditorFilter */
    public PlayListEditorFilter(PlayListEditorFilter oldFilter, TableModel model,
			        ArtistList artistList, AlbumList albumList)
    {   super(model, 4000);
	
	if ( oldFilter != null )
	{
	    this.setArtists(oldFilter.getArtists());
	    oldFilter.setArtists(null);
	    if ( this.getArtists() != null )
	    {
		this.getArtists().clear();
	    }

	    this.setAlbums(oldFilter.getAlbums());
	    oldFilter.setAlbums(null);
	    if ( this.getAlbums() != null )
	    {
		this.getAlbums().clear();
	    }
	}
        
	this.artistList = artistList;
	this.albumList  = albumList;
    }
    
    /** return the input
     *  @return a String
     */
    public String getInput()
    {
        return this.input;
    }
    
    /** set the filtered String
     *  @param input the input used to filter the table
     */
    public void setInput(String input)
    {   
        if ( ! LangUtilities.equals(this.getInput(), input) )
        {
            String oldInput = this.getInput();
            
            this.input = input;

            if ( this.input == null || this.input.trim().length() == 0 )
            {   this.processedInput = null; }
            else
            {
                StringTokenizer tokenizer = new StringTokenizer(this.input.trim(), " ");
                this.processedInput = new String[tokenizer.countTokens()];

                for(int i = 0; i < this.processedInput.length; i++)
                {   String token = tokenizer.nextToken();
                    if ( token == null || token.trim().length() == 0 )
                    {   this.processedInput[i] = null; }
                    else
                    {   this.processedInput[i] = token.trim().toLowerCase(); }
                }
            }
            
            this.firePropertyChange(PROPERTY_INPUT, oldInput, this.getInput());
        }
    }
    
    /** return the artists
     *  @return a Set of Strings
     */
    private Set<String> getArtists()
    {
        return this.artists;
    }
    
    /** initialize the artists
     *  @param artists the artists
     */
    private void setArtists(Set<String> artists)
    {
        this.artists = artists;
    }
    
    /** return the albums
     *  @return a Set of Strings
     */
    private Set<String> getAlbums()
    {
        return this.albums;
    }
    
    /** initialize the album
     *  @param artist the album
     */
    private void setAlbums(Set<String> albums)
    {
        this.albums = albums;
    }
    
    /** method to override to return a custom size
     *  @return the size or null to let default behaviour
     */
    protected Integer getSizeImpl()
    {
        Integer result = super.getSizeImpl();
        
	if ( this.acceptAll )
	{   result = (this.getTableModel() == null ? 0 : this.getTableModel().getRowCount()); }
	
//        if ( this.input == null || this.input.trim().length() == 0 )
//        {   result = (this.getTableModel() == null ? 0 : this.getTableModel().getRowCount()); }
        
        return result;
    }

    @Override
    protected void filter()
    {	
	this.filterVersion2();
    }

    protected void filterVersion2()
    {	
        if ( logger.isDebugEnabled() )
        {
            logger.debug("calling filter");
            logger.debug("filter criterions : ");
            if ( this.processedInput == null )
            {
                logger.debug("\t" + null);
            }
            else
            {
                for(int i = 0; i < this.processedInput.length; i++)
                {
                    logger.debug("\t" + this.processedInput[i] + "#");
                }
            }
        }
        
        int index = -1;
	
	this.acceptAll = false;
	
	Set<String> selectedArtists = this.artistList.getSpecificSelectedItems();
	Set<String> selectedAlbums  = this.albumList.getSpecificSelectedItems();

        TableModel model = this.getTableModel();
	
	String[] entries = new String[model.getColumnCount()];
	
	this.getRedirectionMap().clear();
	
	boolean unknownArtistSelected = artistList.isUnknownSelected();
	boolean unknownAlbumSelected  = albumList.isUnknownSelected();
	
	for(int i = 0; i < model.getRowCount(); i++)
	{
	    boolean itemConveyToCritera = true;
	    
	    if ( (this.processedInput != null && this.processedInput.length > 0) )
	    {
		itemConveyToCritera = false;
		
		for(int j = 0; j < model.getColumnCount(); j++)
		{   Object current = model.getValueAt(i, j);
		    String value = String.valueOf(current);
		    if ( value == null || value.length() == 0 )
		    {   entries[j] = null; }
		    else
		    {   entries[j] = value.trim().toLowerCase(); }
		}

		for(int k = 0; k < this.processedInput.length; k++)
		{
		    String currentToken = this.processedInput[k];

		    boolean found = false;

		    for(int h = 0; h < entries.length && ! itemConveyToCritera; h++)
		    {
			String currentEntry = entries[h];
			
			if ( currentEntry != null && currentEntry.indexOf(currentToken) != -1 )
			{
			    found = true;
			    break;
			}
		    }
		    
		    if ( found )
		    {
			if ( k >= this.processedInput.length - 1 )
			{
			    itemConveyToCritera = true;
			}
		    }
		    else
		    {
			break;
		    }
		}
	    }
	    
	    boolean viewItem = false;
	    
	    if ( model instanceof SibTypeListTableModel )
	    {
		Object o = ((SibTypeListTableModel)model).getItem(i);

		if ( o instanceof SongItem )
		{
		    SongItem song = (SongItem)o;
		    
		    if ( itemConveyToCritera )
		    {
			/** add auteur of item */
			if ( this.artists == null )
			{
			    this.artists = new HashSet<String>(200);
			}
			
			this.artists.add(song.getArtist());
			
			/* if there are no selection made on author excluding all authors ... */
			if ( selectedArtists.size() == 0 && ! unknownArtistSelected )
			{
			    if ( this.albums == null )
			    {
				this.albums = new HashSet<String>(200);
			    }
			    
			    this.albums.add(song.getAlbum());
			    
			    /* no albums selected except all albums ... */
			    if ( selectedAlbums.size() == 0 && ! unknownAlbumSelected )
			    {
				viewItem = true;
			    }
			    else
			    {
				if ( selectedAlbums.contains(song.getAlbum()) || 
				     (unknownAlbumSelected && (song.getAlbum() == null || song.getAlbum().length() == 0) ) )
				{
				    viewItem = true;
				}
			    }
			}
			else
			{
			    /* if the author of the song is among the selected authors ... */
			    if ( selectedArtists.contains(song.getArtist()) ||
				 (unknownArtistSelected && (song.getArtist() == null || song.getArtist().length() ==0) ) )
			    {
				if ( this.albums == null )
				{
				    this.albums = new HashSet<String>(200);
				}
				
				this.albums.add(song.getAlbum());
				
				    
				if ( selectedAlbums.size() == 0 && ! this.albumList.isUnknownSelected() )
				{
				    viewItem = true;
				}
				else
				{
				    if ( selectedAlbums.contains(song.getAlbum()) ||
					 (unknownAlbumSelected && (song.getAlbum() == null || song.getAlbum().length() == 0) ) )
				    {
					viewItem = true;
				    }
				}
			    }
			}
		    }
		}
	    }
	    
	    if ( viewItem )
	    {
		/* feed map */
		index++;
		this.getRedirectionMap().put(index, i);
	    }
	}
			    
	this.artistList.getSelectionModel().setValueIsAdjusting(true);
	this.albumList.getSelectionModel().setValueIsAdjusting(true);
	
	this.artistList.getModel().clear();
	this.albumList.getModel().clear();
	
	this.artistList.getModel().addAll(this.artists);
	this.albumList.getModel().addAll(this.albums);
	
	this.artistList.setSelectedItems(selectedArtists, unknownArtistSelected);
	this.albumList.setSelectedItems(selectedAlbums, unknownAlbumSelected);
    }

    protected void filterVersion1()
    {	
        if ( logger.isDebugEnabled() )
        {
            logger.debug("calling filter");
            logger.debug("filter criterions : ");
            if ( this.processedInput == null )
            {
                logger.debug("\t" + null);
            }
            else
            {
                for(int i = 0; i < this.processedInput.length; i++)
                {
                    logger.debug("\t" + this.processedInput[i] + "#");
                }
            }
        }
        
        /* TODO Auto-generated method stub */
        int index = -1;
	
	this.acceptAll = false;

        TableModel model = this.getTableModel();
        
        if ( (model == null || this.input == null || this.input.trim().length() == 0) )//&&
//             (this.artists == null || this.artists.size() == 0) && 
//             (this.albums == null || this.albums.size() == 0) )
        {   
            if ( logger.isDebugEnabled() )
            {
                logger.debug("clearing inner redirection map");
            }
            
	    this.acceptAll = true;
	    
            this.getRedirectionMap().clear();
        }
        else
        {
            String[] entries = new String[model.getColumnCount()];
            
            for(int i = 0; i < model.getRowCount(); i++)
            {	
                int modelIndex = i;
                
                if ( logger.isDebugEnabled() )
                {
                    logger.debug("considering model row " + modelIndex);
                }

                boolean hidden = true;
                
                boolean result = true;
                
                /** apply artist and album criterion */
                if ( model instanceof SibTypeListTableModel )
                {
                    Object o = ((SibTypeListTableModel)model).getItem(i);
                    
                    if ( o instanceof SongItem )
                    {
                        if ( logger.isDebugEnabled() )
                        {
                            logger.debug("the item at " + modelIndex + " is a " + SongItem.class.getSimpleName() + " named " + ((SongItem)o).getName());
                        }
                        
                        SongItem item = (SongItem)o;
                        
                        Set<String> artists = this.getArtists();
                        if ( result && artists != null && artists.size() > 0 )
                        {
                            result = artists.contains(item.getArtist());
                        }
                        
                        if ( result )
                        {
                            Set<String> albums = this.getAlbums();
                            if ( albums != null && albums.size() > 0 )
                            {
                                result = albums.contains(item.getAlbum());
                            }
                        }
                    }
                    
                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug("item named " + ((SongItem)o).getName() + " seems to be " + (result ? "valid":"invalid") + " according to allowed artists and albums");
                    }
                }
                
                if ( result && this.processedInput != null )
                {   
                    for(int j = 0; j < model.getColumnCount(); j++)
                    {   Object current = model.getValueAt(i, j);
                        String value = String.valueOf(current);
                        if ( value == null )
                        {   entries[j] = null; }
                        else
                        {   entries[j] = value.trim().toLowerCase(); }
                    }
                    
                    for(int k = 0; k < this.processedInput.length; k++)
                    {
                        String currentToken = this.processedInput[k];
                        
                        boolean found = false;
                        
                        for(int h = 0; h < entries.length; h++)
                        {
                            if ( entries[h].indexOf(currentToken) != -1 )
                            {
                                found = true;
                                break;
                            }
                        }
                        
                        if ( ! found )
                        {   result = false;
                            break;
                        }
                    }
                    
                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug("item seems to be " + (result ? "valid":"invalid") + " according to criterions");
                    }
                }
   
                if ( logger.isDebugEnabled() )
                {
                    logger.debug( (result ? "accepting" : "rejecting") + " item");
                }
                
                if ( result )
                {   /* feed map */
                    index++;
                    this.getRedirectionMap().put(index, modelIndex);
                }
            }
        }
    }
    
}
