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
/*
 * HtmlTagsUpdateReportGenerator.java
 *
 * Created on 3 février 2008, 19:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.blackdog.report.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Iterator;
import org.blackdog.report.TagsUpdateLog;
import org.blackdog.report.TagsUpdateReport;
import org.blackdog.type.SongItem;

/**
 *
 * Html report generator for TagsUpdateReport
 *
 * @author alexis
 */
public class HtmlTagsUpdateReportGenerator implements TagsUpdateReportGenerator
{
    /** Creates a new instance of HtmlTagsUpdateReportGenerator */
    public HtmlTagsUpdateReportGenerator()
    {	}

    /**
     * create a file that represents the report
     * 
     * @param report a TagsUpdateReport
     * @param statusThreshold
     * @return a File
     */
    public File generateReport(TagsUpdateReport report, TagsUpdateLog.Status statusThreshold) throws IOException
    {
	File result = File.createTempFile("tags_update_report", ".html");
	result.deleteOnExit();
	
	if ( report != null )
	{
	    PrintWriter writer = null;
	    
	    try
	    {
		writer = new PrintWriter(result);

		writer.write("<html><h1>scan report " + DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) + "<h1>");
		writer.write("<table border=\"1\">");

		SongItem currentSongItem = null;
		
		Iterator<TagsUpdateLog> it = report.logs();
		
		while(it.hasNext())
		{
		    TagsUpdateLog current = it.next();

		    if ( current != null )
		    {
			if ( statusThreshold == null || current.getStatus().compareTo(statusThreshold) >= 0 )
			{
			    if ( current.getSongItem() != currentSongItem )
			    {
				currentSongItem = current.getSongItem();
				writer.write("<tr><td colspan=\"2\"><b><u>" + (currentSongItem == null ? null : currentSongItem.getValue()) + "</u></b></td></tr>");
			    }
			    
			    /* write message */
			    String status = null;
			    
			    String color = null;
			    
			    if ( TagsUpdateLog.Status.DEBUG.equals(current.getStatus()) )
			    {
				color = "#b2b2b2";
			    }
			    else if ( TagsUpdateLog.Status.WARNING.equals(current.getStatus()) )
			    {
				color = "#fa9504";
			    }
			    else if ( TagsUpdateLog.Status.ERROR.equals(current.getStatus()) )
			    {
				color = "#ff0000";
			    }
			    
			    if ( color == null )
			    {
				status = current.getStatus().toString();
			    }
			    else
			    {
				status = "<font color=\"" + color + "\">" + current.getStatus() + "</font>";
			    }
			    
			    writer.write("<tr><td>" + status + "</td>");
			    
			    writer.write("<td>" + current.getMessage());
			    
			    if ( current.getThrowable() != null )
			    {
				writer.write("<br>");
				
				((Throwable)current.getThrowable()).printStackTrace(writer);
			    }
			    writer.write("</td>");
			    
			    writer.write("</tr>");
			}
		    }
		}
		
		writer.write("</table>");
		writer.write("</html>");
	    }
	    finally
	    {
		if ( writer != null )
		{
		    try
		    {
			writer.close();
		    }
		    catch(Exception ex)
		    {	}
		}
	    }
	}
	
	return result;
    }
    
}
