/* 
 * Siberia resources : siberia plugin to facilitate resource loading
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
package org.siberia;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author alexis
 */
public class PluginRcDeclaration
{   
    /** array of plugin resource directories */
    private String[]    rcDirs      = null;
    
    /** classloader linked with the plugin */
    private ClassLoader classLoader = null;
    
    /** Creates a new instance of PluginRcDeclaration
     *  @param cl the ClassLoader linked with the plugin
     *  @param dir an array of Strign representing the resource directories declared by the plugin
     */
    public PluginRcDeclaration(ClassLoader cl, String... dir)
    {   if ( cl == null )
            throw new IllegalArgumentException("classloader for plugin must be specified");
        this.classLoader = cl;
        this.rcDirs      = dir;
    }
    
    /** return the array of String representing the resources paths
     *	@return an array of String
     */
    public String[] getRcDirs()
    {
	return this.rcDirs;
    }
    
    /** return the directory path declared at index i
     *  @param i index of the resource directory path to search
     *  @return a String representing the path of the resource directory declared with index i<br/>
     *      or null if not found. if non null, the path returned always ends with File separator
     */
    public String getRcDirectoryPath(int index)
    {   
        String dir = null;
        if ( this.rcDirs != null )
        {   if ( index >= 0 && index < this.rcDirs.length )
            {   dir = this.rcDirs[index]; }
        }
        
        if ( dir != null )
        {   if ( ! dir.endsWith(File.separator) )
                dir += File.separator;
        }
        
        return dir;
    }
    
    /** return resource dir count
     *  @return the count of resource directory declared by the plugin
     */
    protected int getRcDirCount()
    {   int count = 0;
        if ( this.rcDirs != null )
            count = this.rcDirs.length;
        return count;
    }
    
    /** return the declared ClassLoader
     *  @return an instance of ClassLoader
     */
    public ClassLoader getClassLoader()
    {   return this.classLoader; }
    
    /** return a Set of String representing candidates paths to get the given resource
     *  @param resource a instance of PluginResource
     *  @return a Set of path wich represent candidates paths to get the given resource
     */
    public Set<String> getPathCandidates(PluginResource resource)
    {   Set<String> candidates = null;
        if ( resource != null )
        {   int rcIndex = resource.getRcDirIndex();
            if ( rcIndex != -1 )
            {   String dirPath = this.getRcDirectoryPath(rcIndex - 1);
                if ( dirPath != null )
                {   if ( candidates == null )
                        candidates = new HashSet<String>(1);
                    candidates.add(dirPath + resource.getPath());
                }
            }
            else
            {   for(int i = 0; i < this.getRcDirCount(); i++)
                {   if ( candidates == null )
                        candidates = new HashSet<String>(this.getRcDirCount());
                    String currentDir = this.getRcDirectoryPath(i);
                    if ( currentDir != null )
                    {   candidates.add(currentDir + resource.getPath()); }
                }
            }
        }
        if ( candidates == null )
            candidates = Collections.EMPTY_SET;
        
        return candidates;
    }
    
}
