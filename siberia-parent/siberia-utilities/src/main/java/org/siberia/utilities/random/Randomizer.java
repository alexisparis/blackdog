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
package org.siberia.utilities.random;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * Class that defines static methods to provide String, int id.
 *
 * @author alexis
 */
public class Randomizer
{
    /** random */
    private static Random random = new Random();
    
    /** Creates a new instance of Randomizer */
    private Randomizer()
    {   /* nothing to do */ }
    
    /** return a randomized integer in [0, Integer.MAX_VALUE]
     *  @param set a set containing String id that are not allowed
     *  @return a randomized integer
     */
    public static int randomInteger(Set<Integer> set) throws RandomException
    {   return randomInteger(0, Integer.MAX_VALUE - 1, set); }
    
    /** return a randomized integer
     *  @param minInclude the minimum
     *  @param maxInclude the maximum
     *  @return a randomized integer
     */
    public static int randomInteger(int minInclude, int maxInclude) throws RandomException
    {   return randomInteger(minInclude, maxInclude, null); }
    
    /** return a randomized integer
     *  @param minInclude the minimum
     *  @param maxInclude the maximum
     *  @param set a set containing Integer id that are not allowed
     *  @return a randomized integer
     */
    public static int randomInteger(int minInclude, int maxInclude, Set<Integer> set) throws RandomException
    {   
        if ( maxInclude == Integer.MAX_VALUE )
            throw new RuntimeException("randomInteger accept a maxInclude in range [0, Integer.MAX_VALUE - 1]");
        
        if ( maxInclude < minInclude )
            throw new RandomException();
        int interval = maxInclude - minInclude + 1;
        int value = random.nextInt( interval );
        value += minInclude;
        
        /* test if value is valid */
        if ( set != null )
        {   if ( set.size() < interval )
            {   if ( set.contains(value) )
                    return randomInteger(minInclude, maxInclude, set);
            }
            else
                throw new RandomException();
        }
        return value;
    }
    
    /** return a randomized char
     *  @return a randomized char
     */
    public static char randomChar() throws RandomException
    {   boolean majuscule = random.nextBoolean();
        int     value     = randomInteger( majuscule ? 65 : 97, majuscule ? 90 : 122 );
        return (char)value;
    }
    
    /** return a randomized String that does not appears in the given set
     *  @param length the length of the returned id
     *  @return an id as A String
     */
    public static String randomString(int length) throws RandomException
    {   return randomString(length, null); }
    
    /** return a randomized String that does not appears in the given set
     *  @param length the length of the returned id
     *  @param set a set containing String id that are not allowed
     *  @return an id as A String
     */
    public static String randomString(int length, Set<String> collection)
                                        throws RandomException
    {   String random = null;
        if ( length > 0 )
        {   char[] buffer = new char[length];
            
            for(int i = 0; i < buffer.length; i++)
            {   buffer[i] = randomChar(); }
            
            random = new String(buffer);
            
            /* test if the generated value is valid */
            if ( collection != null )
            {   
                if ( collection.size() < Math.pow(52, length ) )
                {
                    if ( collection.contains(random) )
                        return randomString(length, collection);
                }
                else
                    random = null;
            }
        }
        if ( random == null )
            throw new RandomException();
        
        return random;
    }
    
    /** defines an exception that could be thrown when generation failed */
    public static class RandomException extends Exception
    {   }
    
}
