/* 
 * Siberia utilities : siberia plugin providing severall utilities classes
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
package org.siberia.utilities.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.apache.log4j.Logger;
import org.siberia.utilities.task.TaskStatus;

/**
 *
 * provide some static methods to make file manipulation easier
 *
 * @author alexis
 */
public class IOUtilities
{
    /** default buffer size when copy is wanted */
    private static final int DEFAULT_BUFFER_SIZE = 512*1024;
    
    /** logger */
    private static Logger logger = Logger.getLogger(IOUtilities.class);
    
    /* copy the content of a directory to another
     *  @param sourceLocation a directory
     *  @param targetLocation a directory ( if it does not exists, it is created )
     */
    public static void copyDirectory(File sourceLocation , File targetLocation) throws IOException
    {   copyDirectory(sourceLocation, targetLocation, null); }
    
    /** return the approximative count of bytes represented by the given url
     *	@param url an URL
     *	@return the approximative count of bytes represented by the given url
     */
    public static int bytesAvailable(URL url) throws IOException
    {
	int count = 0;
	
	if ( url != null )
	{
	    InputStream stream = url.openStream();
	    
	    try
	    {
		count = stream.available();
	    }
	    finally
	    {
		if ( stream != null )
		{
		    stream.close();
		}
	    }
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("bytes available from " + url + " : " + count);
	}
	
	return count;
    }
    
    /* copy the content of a directory to another
     *  @param sourceLocation a directory
     *  @param targetLocation a directory ( if it does not exists, it is created )
     *  @param filter filter to apply to source location
     */
    public static void copyDirectory(File sourceLocation , File targetLocation, FileFilter filter) throws IOException
    {   
        if ( sourceLocation != null )
        {   if ( sourceLocation.exists() )
            {   if (sourceLocation.isDirectory())
                {   if ( ! targetLocation.exists())
                    {   targetLocation.mkdirs(); }

                    File[] children = sourceLocation.listFiles(filter);
                    for (int i=0; i<children.length; i++)
                    {   copyDirectory(children[i], new File(targetLocation, children[i].getName()), null);
                    }
                }
                else
                {   if ( ! targetLocation.exists() )
                        targetLocation.createNewFile();

                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0)
                    {   out.write(buf, 0, len); }
                    in.close();
                    out.close();
                }
            }
        }
    }
    
    /* delete a file recursively
     *  @param dir a File
     */
    public static boolean delete(File dir)
    {   if (dir.isDirectory())
        {   String[] children = dir.list();
            for (int i=0; i<children.length; i++)
            {   boolean success = delete(new File(dir, children[i]));
                if (!success)
                {   return false; }
            }
        }
    
        return dir.delete();
    }
    
    /** copy a file that is not a directory by giving its url to another file
     *  @param sourceFile the source file
     *  @param destinationFile the file where to copy the source file
     *
     *  @exception IOException
     */
    public static void copy(File sourceFile, File destinationFile) throws IOException
    {
	copy(sourceFile, destinationFile, (TaskStatus)null);
    }
    
    /** copy a file that is not a directory by giving its url to another file
     *  @param sourceFile the source file
     *  @param destinationFile the file where to copy the source file
     *  @param status a TaskStatus. could be null.
     *
     *  @exception IOException
     */
    public static void copy(File sourceFile, File destinationFile, TaskStatus status) throws IOException
    {   copy(sourceFile, destinationFile, status, new byte[DEFAULT_BUFFER_SIZE]); }
    
    /** copy a file that is not a directory by giving its url to another file
     *  @param sourceFile the source file
     *  @param destinationFile the file where to copy the source file
     *  @param buffer the buffer to use
     *
     *  @exception IOException
     */
    public static void copy(File sourceFile, File destinationFile, byte[] buffer) throws IOException
    {
	copy(sourceFile, destinationFile, null, buffer);
    }
    
    /** copy a file that is not a directory by giving its url to another file
     *  @param sourceFile the source file
     *  @param destinationFile the file where to copy the source file
     *  @param status a TaskStatus. could be null.
     *  @param buffer the buffer to use
     *
     *  @exception IOException
     */
    public static void copy(File sourceFile, File destinationFile, TaskStatus status, byte[] buffer) throws IOException
    {   InputStream  in  = null;
        
        if ( sourceFile == null )
            throw new IllegalArgumentException("source file is null");
        
        copy(new FileInputStream(sourceFile), destinationFile, status, buffer);
    }
    
    /** copy a file that is not a directory by giving its url to another file
     *  @param source the source InputStream
     *  @param destinationFile the file where to copy the source file
     *
     *  @exception IOException
     */
    public static void copy(InputStream source, File destinationFile) throws IOException
    {
	copy(source, destinationFile, (TaskStatus)null);
    }
    
    /** copy a file that is not a directory by giving its url to another file
     *  @param source the source InputStream
     *  @param destinationFile the file where to copy the source file
     *  @param status a TaskStatus. could be null.
     *
     *  @exception IOException
     */
    public static void copy(InputStream source, File destinationFile, TaskStatus status) throws IOException
    {   copy(source, destinationFile, status, new byte[DEFAULT_BUFFER_SIZE]); }
    
    /** copy a file that is not a directory by giving its url to another file
     *  @param source the source InputStream
     *  @param destinationFile the file where to copy the source file
     *  @param buffer the buffer to use
     *
     *  @exception IOException
     */
    public static void copy(InputStream source, File destinationFile, byte[] buffer) throws IOException
    {
	copy(source, destinationFile, null, buffer);
    }
    
    /** copy a file that is not a directory by giving its url to another file
     *  @param source the source InputStream
     *  @param destinationFile the file where to copy the source file
     *  @param status a TaskStatus. could be null.
     *  @param buffer the buffer to use
     *
     *  @exception IOException
     */
    public static void copy(InputStream source, File destinationFile, TaskStatus status, byte[] buffer) throws IOException
    {   InputStream  in  = null;
        OutputStream out = null;
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("copy from stream " + source + " to " + destinationFile + " with buffer (" + buffer.length + ")");
	}
        
        if ( destinationFile == null )
        {   throw new IllegalArgumentException("destination file is null"); }
        if ( source == null )
            throw new IllegalArgumentException("source stream is null");
        
        if ( ! destinationFile.exists() )
            throw new FileNotFoundException("destination file does not exists");
        
        try
        {   in = source;
            out = new FileOutputStream(destinationFile);

            byte[] buf = buffer;

            if ( buf == null )
            {   buf = new byte[DEFAULT_BUFFER_SIZE]; }

            File parent = destinationFile.getParentFile();
            if ( ! parent.exists() )
	    {	parent.mkdirs(); }
            if ( ! destinationFile.exists() )
	    {	destinationFile.createNewFile(); }
	    
	    int bytesCount = in.available();

	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("bytes available : " + bytesCount);
	    }
	    
	    int bytesReadCount = 0;
            int nbLecture;
            while( (nbLecture = in.read(buf)) != -1 )
            {   
		out.write(buf, 0, nbLecture);
		
		bytesReadCount += nbLecture;
		
		if ( status != null )
		{
		    float rate = ( ((float)bytesReadCount) / ((float)bytesCount) ) * 100;
		    double completed = Math.max(0.0f, rate);
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("copy completed at " + completed + " %");
			logger.debug("bytes read : " + bytesReadCount);
		    }
		    status.setPercentageCompleted( (float)completed );
		    
//		    try
//		    {
//			Thread.sleep(100);
//		    }
//		    catch(InterruptedException e)
//		    {
//			return;
//		    }
		}
	    } 
	    
	    if ( status != null )
	    {
		status.setPercentageCompleted( 100 );
	    }
        }
        catch( java.io.FileNotFoundException f )
        {   throw f; }
        catch( java.io.IOException e )
        {   throw e; }
        finally
        {   try
            {   in.close(); }
            catch(Exception e)
            {   e.printStackTrace(); }
            try
            {   out.close(); }
            catch(Exception e)
            {   e.printStackTrace(); }
        }
    }
    
}
