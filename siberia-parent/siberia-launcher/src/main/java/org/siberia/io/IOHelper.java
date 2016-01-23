/* =============================================================================
 * Siberia launcher
 * =============================================================================
 *
 * Project Lead:  Alexis Paris
 *
 * (C) Copyright 2008, by Alexis Paris.
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
package org.siberia.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * basic io methods
 *
 * @author alexis
 */
public class IOHelper
{
    
    /** delete the given file
     *	@param f the File to delete
     */
    public static void delete(File f) throws IOException
    {
	if ( f != null )
	{
	    if ( f.isFile() )
	    {
		f.delete();
	    }
	    else
	    {
		/* directory --> make sure that it is empty when trying to delete it */
		File[] files = f.listFiles();
		
		if ( files != null )
		{
		    for(int i = 0; i < files.length; i++)
		    {
			File current = files[i];
			
			delete(current);
		    }
		}
		
		f.delete();
	    }
	}
    }
    
    /** copy a file to another file
     *	@param origin the origin file or directory
     *	@param destination the destination file or directory
     */
    public static void copy(File origin, File destination) throws IOException
    {
	if ( origin != null && destination != null && origin.exists() )
	{
	    /* make sure that the destination file exists */
	    if ( destination.exists() )
	    {
		if ( (origin.isFile() && ! destination.isFile()) ||
		     ( ! origin.isFile() && destination.isFile()) )
		{
		    /* delete destination */
		    delete(destination);
		}
	    }
	    
	    if ( ! destination.exists() )
	    {
		if ( origin.isDirectory() )
		{
		    destination.mkdirs();
		}
		else // file
		{
		    File parent = destination.getParentFile();
		    
		    if ( ! parent.exists() )
		    {
			parent.mkdirs();
		    }
		    
		    destination.createNewFile();
		}
	    }
	    
	    /** destination file exists and its type is correct according to the type of origin */
	    if ( origin.isFile() )
	    {
		/** copy from file to file */
		FileInputStream in = null;
		FileOutputStream out = null;
		
		in  = new FileInputStream(origin);
		out = new FileOutputStream(destination);
		
		try
		{
		    byte[] buffer = new byte[512*1024];
		    int nbLecture;
		    
		    while ( (nbLecture = in.read(buffer)) != -1 )
		    {
			out.write(buffer, 0, nbLecture);
		    }
		}
		finally
		{
		    if ( in != null )
		    {
			try
			{   in.close(); }
			catch(IOException e)
			{   e.printStackTrace(); }
		    }
		    if ( out != null )
		    {
			try
			{   out.close(); }
			catch(IOException e)
			{   e.printStackTrace(); }
		    }
		}
	    }
	    else // diretory
	    {
		/* copy each file of origin to destination dir */
		File[] files = origin.listFiles();
		
		if ( files != null )
		{
		    for(int i = 0; i < files.length; i++)
		    {
			File current = files[i];
			
			File subFile = new File(destination, current.getName());
			
			copy(current, subFile);
		    }
		}
	    }
	}
    }
}
