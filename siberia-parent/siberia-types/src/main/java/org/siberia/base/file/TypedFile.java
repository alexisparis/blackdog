/* 
 * Siberia types : siberia plugin defining structures managed by siberia platform
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
package org.siberia.base.file;

import java.io.File;

/**
 *
 * Extension to File which allow to choose if a TypedFile
 *  could only represent a File or a Directory or whatever
 *
 * @author alexis
 */
public abstract class TypedFile extends File
{
    /** kind of file */
    private FileType type = FileType.FILE;
    
    /** Creates a new instance of TypedFile
     *  @param path
     */
    public TypedFile(String path)
    {   super(path); }

    /** return the FileType
     *  @return a FileType
     */
    public FileType getType()
    {   return type; }

    /** initialize the FileType
     *  @param type a FileType
     */
    public void setType(FileType type)
    {   if ( type == null )
            throw new IllegalArgumentException("illegal FileType");
        this.type = type;
    }
    
}
