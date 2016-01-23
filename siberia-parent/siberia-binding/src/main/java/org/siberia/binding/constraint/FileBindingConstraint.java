/* 
 * Siberia binding : siberia plugin defining persistence services
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
package org.siberia.binding.constraint;

import java.io.File;

/**
 *
 * @author alexis
 */
public class FileBindingConstraint implements BindingConstraint
{
    /** file representing the constraint */
    private File constraintFile = null;
    
    /** Creates a new instance of FileBindingConstraint */
    public FileBindingConstraint()
    {
	this(null);
    }
    
    /** Creates a new instance of FileBindingConstraint
     *	@param f a File
     */
    public FileBindingConstraint(File f)
    {	
	this.setFile(f);
    }
    
    /** set the file of the constraint
     *	@param file a File
     */
    public void setFile(File file)
    {
	this.constraintFile = file;
    }
    
    /** ge"t the file of the constraint
     *	@return a File
     */
    public File getFile()
    {
	return this.constraintFile;
    }
    
}
