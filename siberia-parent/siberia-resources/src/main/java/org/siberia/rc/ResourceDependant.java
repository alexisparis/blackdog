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

/**
 *
 * Interface that describe the relation with resources
 *
 * @author alexis
 */
@Deprecated
public interface ResourceDependant
{
    
    /** return the name of the directories after 'rc' directory that contains the associated resources
     *  @return the name of the directories after 'rc' directory that contains the associated resources
     */
    public String[] getDirectorySuffix();
    
    /** initialize the name of the directories after 'rc' directory that contains the associated resources
     *  @param dirSuffix the name of the directories after 'rc' directory that contains the associated resources
     */
    public void setDirectorySuffix(String... dirSuffix);
    
    /** return the extensions of the file associated with an implementation ( without '.' )
     *  @return the extensions of the file associated with an implementation ( without '.' )
     */
    public String[] getExtensions();
    
    /** set the extensions of the file associated with an implementation ( without '.' )
     *  @param extensions the extensions of the file associated with an implementation ( without '.' )
     */
    public void setExtensions(String... extensions);
    
    /** return the names of the file associated with an implementation ( without extension )
     *  @return the names of the file associated with an implementation ( without extension )
     */
    public String[] getFileNames();
    
    /** set the names of the file associated with an implementation ( without extension )
     *  @param filenames the names of the file associated with an implementation ( without extension )
     */
    public void setFileNames(String... filenames);
    
    /** return all possibilities of files
     *  @return all possibilities of files
     */
    public String[] possibilities();
    
}
