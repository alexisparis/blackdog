/*
 * FileBarProvider.java
 *
 * Created on 2 février 2008, 23:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.siberia.bar.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author alexis
 */
public class FileBarProvider implements BarProvider
{
    /** list of InputStream */
    private List<InputStream> streams = null;
    
    /** Creates a new instance of FileBarProvider
     *	@param files an array of files
     */
    public FileBarProvider(File... files) throws FileNotFoundException
    {
	if ( files != null && files.length > 0 )
	{
	    this.streams = new ArrayList<InputStream>(files.length);
	    
	    for(int i = 0; i < files.length; i++)
	    {
		File current = files[i];
		
		if ( current == null )
		{
		    this.streams.add(null);
		}
		else
		{
		    this.streams.add(new FileInputStream(current));
		}
	    }
	}
    }

    /**
     * return an iterator over input streams that represent bar definitions to consider during bar creation
     * 
     * @return an Iterator over InputStream representing bar definitions
     */
    public Iterator<InputStream> getBarInputStreams()
    {
	Iterator<InputStream> it = null;
	
	if ( streams != null )
	{
	    it = streams.iterator();
	}
	
	if ( it == null )
	{
	    it = new Iterator<InputStream>()
	    {
		public boolean hasNext()
		{   return false; }
		
		public InputStream next()
		{   return null; }
		
		public void remove()
		{   }
	    };
	}
	
	return it;
    }
    
}
