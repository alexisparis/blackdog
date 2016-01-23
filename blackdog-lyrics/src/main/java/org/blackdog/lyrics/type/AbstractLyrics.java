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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.blackdog.type.SongItem;
import org.siberia.type.AbstractSibType;
import org.siberia.type.annotation.bean.Bean;

/**
 *
 * Abstract representation of a Lyrics for a SongItem.
 *
 * this object manage specific Lyrics properties
 *  and listen to changes on the current SongItem to call retrieve
 *  with the new information of the SongItem
 *
 * @author alexis
 */
@Bean(  name="abstract lyrics",
        internationalizationRef="org.blackdog.rc.i18n.type.AbstractLyrics",
        expert=false,
        hidden=true,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public abstract class AbstractLyrics extends AbstractSibType implements Lyrics, PropertyChangeListener
{
    /** logger */
    private transient Logger        logger         = Logger.getLogger(AbstractLyrics.class);
    
    /** String representing an html content */
    private String                  htmlContent    = null;
    
    /** AudioItem linked to the AbstractLyrics */
    private WeakReference<SongItem> songItem       = new WeakReference<SongItem>(null);
    
    /** retrieving status */
    private LyricsRetrievedStatus   retrieveStatus = LyricsRetrievedStatus.NONE;
    
    /**
     * Creates a new instance of AbstractLyrics
     */
    public AbstractLyrics()
    {   }
    
    /** set the retrieve status of the lyrics
     *	@param status a LyricsRetrievedStatus
     */
    public void setRetrieveStatus(LyricsRetrievedStatus status)
    {
	assert status != null;
	
        LyricsRetrievedStatus oldStatus = this.getRetrieveStatus();
	
	if ( ! status.equals(oldStatus) )
	{
	    this.retrieveStatus = status;

	    this.firePropertyChange(PROPERTY_RETRIEVED, oldStatus, this.getRetrieveStatus());
	}
    }
    
    /** return the retrieve status of the lyrics
     *	@return a LyricsRetrievedStatus
     */
    public LyricsRetrievedStatus getRetrieveStatus()
    {
	return this.retrieveStatus;
    }
    
    /** return the SongItem linked to the lyrics
     *  @return an SongItem
     */
    public SongItem getSongItem()
    {   return (this.songItem == null ? null : this.songItem.get()); }
    
    /** initialize the SongItem linked to the lyrics
     *  @param item an SongItem
     */
    public void setSongItem(SongItem item)
    {   
        if ( this.songItem != item )
        {
	    SongItem old = this.getSongItem();
            if ( old != null )
            {   old.removePropertyChangeListener(this); }
            
            this.songItem = new WeakReference<SongItem>(item);
            
//            if ( item != null )
//            {   item.addPropertyChangeListener(this); }
        }
    }

    /** return the html content representing the lyrics
     *  @return a String
     */
    public String getHtmlContent()
    {   return htmlContent; }

    /** initialize the html content representing the lyrics
     *  @param htmlContent a String
     */
    public void setHtmlContent(String htmlContent)
    {
        String oldValue = this.getHtmlContent();
	
	boolean equals = false;
	
	if ( oldValue == null )
	{
	    if ( htmlContent == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    equals = oldValue.equals(htmlContent);
	}
        
	if ( ! equals )
	{
	    this.htmlContent = htmlContent;

	    this.firePropertyChange(PROPERTY_HTML_CONTENT, oldValue, this.getHtmlContent());
	}
    }
    
    /* #########################################################################
     * ######################## Utilities methods ##############################
     * ######################################################################### */
    
    /** return a list of String representing critera for artist
     *	@return a list of String
     */
    protected List<String> getArtistCriteriums()
    {
	List<String> criteriums = null;
	
	if ( this.getSongItem() != null )
	{
	    String artist = this.getSongItem().getArtist();
	    
	    if ( artist == null || artist.trim().length() == 0 )
	    {
		artist = this.getSongItem().getAuthor();
	    }
	    
	    if ( artist != null && artist.trim().length() > 0 )
	    {
		String[] splitted = artist.split(" ");
		
		if ( splitted != null && splitted.length > 0 )
		{
		    criteriums = new ArrayList<String>(splitted.length);
		    
		    for(int i = 0; i < splitted.length; i++)
		    {
			criteriums.add(splitted[i]);
		    }
		    
		}
	    }
	    
//            title = item.getPlayableName();
//            System.out.println("title : " + title + "#");
//            if ( title != null && title.trim().length() > 0 )
//            {
//                artist = item.getArtist();
//                if ( artist == null || artist.trim().length() == 0 )
//                {   artist = "";
//                    searchArtist = false;
//                }
//            }
	}
	
	if ( criteriums == null )
	{
	    criteriums = Collections.emptyList();
	}
	
	return criteriums;
    }
    
    /** return a list of String representing critera for song name
     *	@return a list of String
     */
    protected List<String> getSongNameCriteriums()
    {
	List<String> criteriums = null;
	
	if ( this.getSongItem() != null )
	{
	    String title = this.getSongItem().getTitle();
	    
	    if ( title == null || title.trim().length() == 0 )
	    {
		title = this.getSongItem().getName();
	    }
	    
	    if ( title != null && title.trim().length() > 0 )
	    {
		String[] splitted = title.split(" ");
		
		if ( splitted != null && splitted.length > 0 )
		{
		    criteriums = new ArrayList<String>(splitted.length);
		    
		    for(int i = 0; i < splitted.length; i++)
		    {
			criteriums.add(splitted[i]);
		    }
		    
		}
	    }
	}
	
	if ( criteriums == null )
	{
	    criteriums = Collections.emptyList();
	}
	
	return criteriums;
    }
    
    /* #########################################################################
     * ################### PropertyChangeListener impl #########################
     * ######################################################################### */
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */

    public void propertyChange(PropertyChangeEvent evt)
    {
        if ( evt.getSource() == this.getSongItem() )
        {   this.retrieve(); }
    }
    
}
