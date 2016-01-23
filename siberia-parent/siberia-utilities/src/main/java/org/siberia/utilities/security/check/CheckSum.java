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
/*
 * CheckSum.java
 *
 * Created on 11 ao�t 2006, 03:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.siberia.utilities.security.check;

import java.io.File;

/**
 *
 * @author alexis
 */
public enum CheckSum
{
    NONE    (""     , "none", null),
    MD5_SUM (".md5" , "md5" , SimpleCheckMethod.MD5_METHOD),
    SHA1_SUM(".sha1", "sha1", SimpleCheckMethod.SHA1_METHOD);
    
    /** extension of file that contains the signature ( example : '.sha1') */
    private String      extension = null;
    
    /** check method */
    private CheckMethod method    = null;
    
    /** abbreviation */
    private String      shortName = null;
    
    CheckSum(String extension, String shortName, CheckMethod method)
    {   this.extension = extension;
        this.method    = method;
        this.shortName = shortName;
    }
    
    /** return the extension file containing the hash sequence
     *  @return the extension file containing the hash sequence
     */
    public String extension()
    {   return this.extension; }
    
    /** return the short name of the Checksum method
     *  @return the short name of the Checksum method
     */
    public String shortName()
    {   return this.shortName; }
    
    /** return the label of the Checksum method
     *  @return the label of the Checksum method
     */
    public String label()
    {   return this.shortName(); }
    
    /** return the method
     *  @return the method
     */
    public CheckMethod method()
    {   return this.method; }
    
    /** check if the sum is correct according to the parameters
     *  @param file the file to check
     *  @param sumFile the file that contains the checkSum
     *  @return true if the check is valid
     */
    public boolean isValid(File file, File sumFile)
    {   boolean valid = false;
        if ( this.method() == null )
            valid = true;
        else
        {   valid = this.method().isValid(file, sumFile); }
        
        return valid;
    }
    
    /** return the checksum related to the given abbreviation
     *  @param abbrev
     *  @return an instanceof CheckSum or NONE if not found
     */
    public static CheckSum getCheckSumForAbbreviation(String abbrev)
    {   CheckSum check = NONE;
        
        if ( abbrev != null )
        {
            for (CheckSum p : CheckSum.values())
            {   if ( p.shortName().equals(abbrev) )
                {   check = p;
                    break;
                }
            }
        }
        return check;
    }
}
