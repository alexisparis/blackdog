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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 *
 * @author alexis
 */
public abstract class AbstractCheckMethod implements CheckMethod
{
    
    /** Creates a new instance of AbstractCheckSum */
    public AbstractCheckMethod()
    {   }

    /**
     * check if the sum is correct according to the parameters
     * 
     * @param file the file to check
     * @param sumFile the file that contains the checkSum
     * @return true if the check is valid
     */
    public boolean isValid(File file, File sumFile)
    {
        boolean okay = false;
        
        if ( file != null && sumFile != null )
        {   
            try
            {
                InputStream in = new DataInputStream(new FileInputStream(file));

                /** read the sumFile */
                DataInputStream sumStream = new DataInputStream(new FileInputStream(sumFile));
                byte[] buffer = new byte[(int)sumFile.length()];
                sumStream.readFully(buffer);
                
                String sum = new String(buffer).trim();
                
                int firstSpacePos = sum.indexOf(' ');
                if ( firstSpacePos > 0 )
                {   sum = sum.substring(0, firstSpacePos); }
                
                return this.isValid(sum, in, file.length());
            }
            catch(Exception e)
            {   e.printStackTrace(); }
        }
        return okay;
    }
    
    /** check if the sum is correct according to the parameters
     * 
     * @param file the file to check
     * @param hash the String defining the checksum for the given file
     * @param fileContent the content of the file to check
     * @return true if the check is valid
     */
    protected abstract boolean isValid(String hash, InputStream fileContent, long length);
    
}
