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
package org.blackdog.type.customizer;

import java.beans.PropertyVetoException;
import org.apache.log4j.Logger;
import org.blackdog.type.SongItem;
import org.blackdog.report.TagsUpdateReport;


/**
 *
 * customizer that must be asked at last to correct illegal property such
 *  as too long song name for example.
 *
 * @author alexis
 */
public class SongItemPropertyCorrecterCustomizer implements SongItemCustomizer
{
    /** logger */
    private Logger logger = Logger.getLogger(SongItemPropertyCorrecterCustomizer.class);
    
    /**
     * Creates a new instance of SongItemPropertyCorrecterCustomizer
     */
    public SongItemPropertyCorrecterCustomizer()
    {    }

    /** complete the information of the given SongItem
     *	@param report a TagsUpdateReport
     *	@param item a SongItem
     */
    public void customize(TagsUpdateReport report, final SongItem item)
    {
	if ( item != null )
	{
	    int stringLimit = 255;
	    
	    String content = null;
	    
	    content = item.getName();
	    
	    if ( content != null && content.length() > stringLimit )
	    {
		report.addErrorLog(item, "the name '" + content + "' length is higher than " + stringLimit + " --> truncation", null);
		try
		{
		    item.setName(content.substring(0, stringLimit));
		}
		catch(PropertyVetoException e)
		{
		    report.addErrorLog(item, "unable to change the name", e);
		}
	    }
	    
	    content = item.getTitle();
	    
	    if ( content != null && content.length() > stringLimit )
	    {
		report.addErrorLog(item, "the title '" + content + "' length is higher than " + stringLimit + " --> truncation", null);
		try
		{
		    item.setTitle(content.substring(0, stringLimit));
		}
		catch(PropertyVetoException e)
		{
		    report.addErrorLog(item, "unable to change the title", e);
		}
	    }
	    
	    content = item.getArtist();
	    
	    if ( content != null && content.length() > stringLimit )
	    {
		report.addErrorLog(item, "the artist '" + content + "' length is higher than " + stringLimit + " --> truncation", null);
		try
		{
		    item.setArtist(content.substring(0, stringLimit));
		}
		catch(PropertyVetoException e)
		{
		    report.addErrorLog(item, "unable to change the artist", e);
		}
	    }
	    
	    content = item.getAlbum();
	    
	    if ( content != null && content.length() > stringLimit )
	    {
		report.addErrorLog(item, "the album '" + content + "' length is higher than " + stringLimit + " --> truncation", null);
		try
		{
		    item.setAlbum(content.substring(0, stringLimit));
		}
		catch(PropertyVetoException e)
		{
		    report.addErrorLog(item, "unable to change the album", e);
		}
	    }
	    
	    content = item.getAuthor();
	    
	    if ( content != null && content.length() > stringLimit )
	    {
		report.addErrorLog(item, "the author '" + content + "' length is higher than " + stringLimit + " --> truncation", null);
		try
		{
		    item.setAuthor(content.substring(0, stringLimit));
		}
		catch(PropertyVetoException e)
		{
		    report.addErrorLog(item, "unable to change the author", e);
		}
	    }
	    
	    content = item.getLeadArtist();
	    
	    if ( content != null && content.length() > stringLimit )
	    {
		report.addErrorLog(item, "the lead artist '" + content + "' length is higher than " + stringLimit + " --> truncation", null);
		try
		{
		    item.setLeadArtist(content.substring(0, stringLimit));
		}
		catch(PropertyVetoException e)
		{
		    report.addErrorLog(item, "unable to change the lead artist", e);
		}
	    }
	    
	    content = item.getComment();
	    
	    if ( content != null && content.length() > stringLimit )
	    {
		report.addErrorLog(item, "the comment '" + content + "' length is higher than " + stringLimit + " --> truncation", null);
		try
		{
		    item.setComment(content.substring(0, stringLimit));
		}
		catch(PropertyVetoException e)
		{
		    report.addErrorLog(item, "unable to change the comment", e);
		}
	    }
	    
	    stringLimit = 500;
	    
	    content = item.getLyrics();
	    
	    if ( content != null && content.length() > stringLimit )
	    {
		report.addErrorLog(item, "the lyrics '" + content + "' length is higher than " + stringLimit + " --> truncation", null);
		try
		{
		    item.setLyrics(content.substring(0, stringLimit));
		}
		catch(PropertyVetoException e)
		{
		    report.addErrorLog(item, "unable to change the lyrics", e);
		}
	    }
	}
    }
}
