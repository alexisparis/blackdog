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
package org.siberia.base;

/**
 *
 * Define static methods to simplify the manipulations of Objects of packaqge java.lang
 *
 * @author alexis
 */
public class LangUtilities
{
    
    /** Creates a new instance of LangUtilities */
    private LangUtilities()
    {   }
    
    /** returns true fi==if the two Strings are equals
     *  @param s1 a String
     *  @param s2 a String
     *  @return true if the strings are equals
     */
    public static boolean equals(String s1, String s2)
    {   boolean result = false;
        
        if ( s1 == null )
        {   if ( s2 == null )
            {   result = true; }
        }
        else
        {   result = s1.equals(s2); }
        
        return result;
    }
    
    /** returns true fi==if the two Numbers are equals
     *  @param n1 a String
     *  @param n2 a String
     *  @return true if the numbers are equals
     */
    public static boolean equals(Number n1, Number n2)
    {   boolean result = false;
        
        if ( n1 == null )
        {   if ( n2 == null )
            {   result = true; }
        }
        else
        {   result = n1.doubleValue() == n2.doubleValue(); }
        
        return result;
    }
    
    /** compare two strings
     *  @param n1 a String
     *  @param n2 a String
     *  @return 0 if n1 == n2, > 0 if n1 is higher than n2, < 0 else
     */
    public static int compare(String n1, String n2)
    {   
	int result = 0;
    
	/** compare names */
	if ( n1 == null )
	{
	    if ( n2 != null )
	    {
		result = -1;
	    }
	}
	else
	{
	    if ( n2 == null )
	    {
		result = 1;
	    }
	    else
	    {
		result = n1.compareTo(n2);
	    }
	}
	
	return result;
    }
    
}
