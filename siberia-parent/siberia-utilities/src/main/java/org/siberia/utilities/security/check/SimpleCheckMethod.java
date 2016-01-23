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
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;

/**
 *
 * @author alexis
 */
public class SimpleCheckMethod extends AbstractCheckMethod
{
    /** logger */
    private static final Logger logger = Logger.getLogger(SimpleCheckMethod.class);
    
    /** SHA-1 Check METHOD */
    public static final CheckMethod SHA1_METHOD = new SimpleCheckMethod("SHA-1");
    
    /** MD5 Check METHOD */
    public static final CheckMethod MD5_METHOD  = new SimpleCheckMethod("MD5");
    
    /** id of algorithm */
    private String algo     = null;
    
    /** provider of algo */
    private String provider = null;
    
    /** Creates a new instance of SimpleCheckMethod
     *  @param algo "SHA-1" or "MD5", etc..
     *  @param provider the provider of algorithm
     */
    public SimpleCheckMethod(String provider, String algo)
    {   this.algo     = algo;
        this.provider = provider;
    }
    
    /** Creates a new instance of SimpleCheckMethod
     *  @param algos "SHA-1" or "MD5", etc..
     */
    public SimpleCheckMethod(String algo)
    {   this(null, algo); }
    
    /** return a String representing the hash sequence for the given file
     *  @param fileContent the content of the file to check
     *	@return a String representing the hash sequence of the given file
     */
    public String getHashSequence(InputStream fileContent)
    {
	String hash = null;
	
	if ( fileContent != null )
	{
            if ( this.algo != null )
            {   
		try
		{   MessageDigest md = null;
		    if ( this.provider == null )
			md = MessageDigest.getInstance(this.algo);
		    else
			md = MessageDigest.getInstance(this.algo, this.provider);

		    // pas bourrin
		    // Lecture par segment de 0.5Mo 
		    byte buffer[]=new byte[512*1024];
		    int nbLecture;
		    while( (nbLecture = fileContent.read(buffer)) != -1 )
		    {   md.update(buffer, 0, nbLecture); } 

		    byte[] b = Hex.encode(md.digest());
		    
		    hash = new String(b);
		}
		catch (Exception e)
		{   e.printStackTrace(); }
            }
	}
	
	return hash;
    }

    /**
     * check if the sum is correct according to the parameters
     * 
     * 
     * @param file the file to check
     * @param hash the String defining the checksum for the given file
     * @param fileContent the content of the file to check
     * @return true if the check is valid
     */
    protected boolean isValid(String hash, InputStream fileContent, long length)
    {
        boolean okay = false;
        
        if ( hash != null && fileContent != null )
        {
            okay = hash.equals ( this.getHashSequence (fileContent) );
        }
	
	if ( ! okay )
	{
	    logger.warn("checksum failed for algorythm " + algo);
	}
	
        return okay;
    }
    
}
