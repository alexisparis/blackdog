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
package org.siberia.utilities.security.check;

import java.io.File;
import java.io.InputStream;

/**
 *
 * Defines a CheckMethod
 *
 * @author alexis
 */
public interface CheckMethod
{
    /** check if the sum is correct according to the parameters
     *  @param file the file to check
     *  @param sumFile the file that contains the checkSum
     *  @return true if the check is valid
     */
    public boolean isValid(File file, File sumFile);
    
    /** return a String representing the hash sequence for the given file
     *  @param fileContent the content of the file to check
     *	@return a String representing the hash sequence of the given file
     */
    public String getHashSequence(InputStream fileContent);
}
