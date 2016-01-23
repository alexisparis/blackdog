/* 
 * Siberia editor : siberia plugin defining editor framework
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.editor.launch;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * Represents the information about an editor currently in use in the platform
 *
 * @author alexis
 */
public class EditorLaunchInformation
{
    /** date of launch */
    private Calendar launchDate = null;
    
    /** Creates a new instance of EditorInformation */
    public EditorLaunchInformation()
    {   this(null); }
    
    /** Creates a new instance of EditorInformation
     *  @param launchDate a calendar representing the date when the editor was first launched
     */
    public EditorLaunchInformation(Calendar launchDate)
    {   this.launchDate = (launchDate == null ? new GregorianCalendar() : launchDate); }
    
    /** return a calendar representing the date when the editor was first launched
     *  @return a calendar representing the date when the editor was first launched
     */
    public Calendar getLaunchingDate()
    {   return this.launchDate; }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(this.getClass());
        buffer.append(" ");
        DateFormat format = new SimpleDateFormat("dd/MM/yy' at 'HH:mm:ss.SSS");
        
        buffer.append(format.format(this.launchDate.getTime()));
        
        return buffer.toString();
    }

}
