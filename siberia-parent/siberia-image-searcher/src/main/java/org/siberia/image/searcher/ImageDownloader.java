/* 
 * Siberia image searcher : siberia plugin defining image searchers
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
package org.siberia.image.searcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 *
 * Object that allow to download an url to temp directory
 *
 * @author alexis
 */
public class ImageDownloader
{
    private static final int DEFAULT_BUFFER_SIZE = 512*1024;
    
    /** logger */
    private Logger logger = Logger.getLogger(ImageDownloader.class);
    
    /** Creates a new instance of ImageDownloader */
    public ImageDownloader()
    {	}
    
    /** copy the url content to a temp directory
     *	@param url an URL
     *	@return a File that is marked as deleted on jvm exit
     */
    public File downloadURLContent(URL url) throws IOException
    {
	File f = null;
	
	if ( url != null )
	{
	    int position = -1;
	    
	    String file = url.getFile();
	    String extension = null;
	    
	    if ( file != null )
	    {
		position = file.lastIndexOf('/');
		
		if ( position != -1 )
		{
		    file = file.substring(position + 1);
		}
	    }
	    
	    if ( file != null )
	    {
		position = file.lastIndexOf('.');
		
		if ( position != -1 )
		{
		    extension = file.substring(position + 1);
		    file = file.substring(0, position);
		}
	    }
	    
	    if ( file == null )
	    {
		file = "temp_siberia_img";
	    }
	    if ( extension == null )
	    {
		extension = "";
	    }
	    
	    f = File.createTempFile(file, extension);
	    
	    f.deleteOnExit();
	    
	    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
	    
	    InputStream  input  = null;
	    OutputStream output = null;
	    
	    try
	    {
		output = new FileOutputStream(f);
		
		int nbLecture;
		while( (nbLecture = input.read(buffer)) != -1 )
		{
		    output.write(buffer, 0, nbLecture);
		}
	    }
	    finally
	    {   
		try
		{   input.close(); }
		catch(Exception e)
		{   e.printStackTrace(); }
		try
		{   output.close(); }
		catch(Exception e)
		{   e.printStackTrace(); }
	    }
	}
	    
	return f;
    }
}
