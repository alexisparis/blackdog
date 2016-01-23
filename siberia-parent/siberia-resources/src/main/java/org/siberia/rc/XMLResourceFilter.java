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
 * Resource filter for xml files
 * 
 * @author alexis
 */
@Deprecated
public class XMLResourceFilter extends ResourceFilter
{
    
    /** Creates a new instance of XMLResourceFilter */
    public XMLResourceFilter()
    {   this(null, null); }
    
    /** Creates a new instance of ResourceFilter
     *  @param suffixes an array of String
     *  @param filenames an array of String
     *  @param extensions an array of String
     */
    public XMLResourceFilter(String[] suffixes, String[] filenames)
    {   this.setDirectorySuffix(suffixes);
        this.setFileNames(filenames);
        this.setExtensions(new String[]{"xml"});
    }
    
}
