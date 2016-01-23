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
package org.siberia.rc;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * Resource filter for all kind of files
 *
 * @author alexis
 */
@Deprecated
public class ResourceFilter implements ResourceDependant
{
    /** list of directory suffixes */
    private String[]               suffix     = null;
    
    /** list of extension */
    private String[]               extensions = null;
    
    /** list of filenames */
    private String[]               filenames  = null;
    
    /** sub filters */
    private Set<ResourceDependant> subFilters = null;
    
    /** Creates a new instance of ResourceFilter */
    public ResourceFilter()
    {   this(null, null, null); }
    
    /** Creates a new instance of ResourceFilter
     *  @param suffixes an array of String
     *  @param filenames an array of String
     *  @param extensions an array of String
     */
    public ResourceFilter(String[] suffixes, String[] filenames, String[] extensions)
    {   this.setDirectorySuffix(suffixes);
        this.setFileNames(filenames);
        this.setExtensions(extensions);
    }
    
    /** add a new subfilter
     *  @param filter a ResourceFilter
     */
    public void addSubFilter(ResourceFilter filter)
    {   if ( filter != null )
        {   if ( this.subFilters == null )
                this.subFilters = new HashSet<ResourceDependant>();
            this.subFilters.add(filter);
        }
    }
    
    /** remove a subfilter
     *  @param filter a ResourceFilter
     */
    public void removeSubFilter(ResourceFilter filter)
    {   if ( filter != null )
        {   if ( this.subFilters != null )
                this.subFilters.remove(filter);
        }
    }
    
    /** return the name of the directories after 'rc' directory that contains the associated resources
     *  @return the name of the directories after 'rc' directory that contains the associated resources
     */
    public String[] getDirectorySuffix()
    {   return this.suffix; }
    
    /** initialize the name of the directories after 'rc' directory that contains the associated resources
     *  @param dirSuffix the name of the directories after 'rc' directory that contains the associated resources
     */
    public void setDirectorySuffix(String... dirSuffix)
    {   this.suffix = dirSuffix; }
    
    /** return the extensions of the file associated with an implementation ( without '.' )
     *  @return the extensions of the file associated with an implementation ( without '.' )
     */
    public String[] getExtensions()
    {   return this.extensions; }
    
    /** set the extensions of the file associated with an implementation ( without '.' )
     *  @param extensions the extensions of the file associated with an implementation ( without '.' )
     */
    public void setExtensions(String... extensions)
    {   this.extensions = extensions; }
    
    /** return the names of the file associated with an implementation ( without extension )
     *  @return the names of the file associated with an implementation ( without extension )
     */
    public String[] getFileNames()
    {   return this.filenames; }
    
    /** set the names of the file associated with an implementation ( without extension )
     *  @param filenames the names of the file associated with an implementation ( without extension )
     */
    public void setFileNames(String... filenames)
    {   this.filenames = filenames; }
    
    /** return all possibilities of files
     *  @return all possibilities of files
     */
    public String[] possibilities()
    {   Set<String> set = new HashSet<String>();
        
        /* if no suffix are supplied, let's consider to look directly into rc directory */
        if ( this.suffix == null )
            set.addAll(this.possibilitiesForSuffix(""));
        else
        {   for(int i = 0; i < this.suffix.length; i++)
            {   String current = this.suffix[i];
                if ( current != null )
                    set.addAll(this.possibilitiesForSuffix(current));
            }
        }
        
        /* add sub filters possibilities */
        if ( this.subFilters != null )
        {   Iterator<ResourceDependant> it = this.subFilters.iterator();
            while(it.hasNext())
            {   String[] t = it.next().possibilities();
                if ( t != null )
                {   for(int i = 0; i < t.length; i++)
                        set.add(t[i]);
                }
            }
        }
        
        return set.toArray(new String[set.size()]);
    }
    
    /** return a set of String with all possibilities for the given suffix
     *  @param suffix a directory suffix
     *  @return a set of String 
     */
    private Set<String> possibilitiesForSuffix(String suffix)
    {   Set<String> set = null;
        if ( suffix != null )
        {   if ( this.filenames != null )
            {   int pos = 0;
                for(int i = 0; i < suffix.length(); i++)
                {   char c = suffix.charAt(i);
                    if ( c != File.separatorChar )
                        break;
                    pos = i;
                }
                String suffixModif = (pos == 0 ? suffix : suffix.substring(pos));
                for(int i = 0; i < this.filenames.length; i++)
                {   if ( set == null )
                        set = new HashSet<String>();
                    set.addAll(this.possiblitiesForSuffixAndFilenames(suffixModif, this.filenames[i]));
                }
            }
        }
        
        if ( set == null )
            set = Collections.EMPTY_SET;
        return set;
    }
    
    /** return a set of String with all possibilities for the given suffix and filename
     *  @param suffix a directory suffix
     *  @param filename a filename
     *  @return a set of String 
     */
    private Set<String> possiblitiesForSuffixAndFilenames(String suffix, String filename)
    {   Set<String> set = null;
        if ( suffix != null )
        {   if ( filename != null )
            {   
                if ( this.extensions != null )
                {   for(int i = 0; i < this.extensions.length; i++)
                    {   String extension = this.extensions[i];
                        if ( extension != null )
                        {   if ( extension.indexOf(File.separatorChar) != -1 )
                            {   /* shit... nevermind... */
                                continue;
                            }
                        }
                        
                        if ( set == null )
                            set = new HashSet<String>();
                        
                        set.add( (suffix.endsWith(File.separator) ? suffix : suffix + File.separator) +
                                 filename +
                                 (extension == null ? "" : "." + extension) );
                    }
                }
            }
        }
        
        if ( set == null )
            set = Collections.EMPTY_SET;
        
        return set;
    }
    
    
    
}
