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
package org.siberia.utilities.lang;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * Class that allow to convert number (radix 2..16)
 *
 * @author alexis
 */
public class NumberConversion
{   
    /** Creates a new instance of NumberConversion */
    private NumberConversion()
    {   /* do nothing */ }
    
    /** m�thode that return an integer related to the given char
     *  @param c a char. if it is 'a' or 'A' then 10 is returned. it return -1 if the value is not<br/>
     *          in the range ['0', '9'] U ['a', 'z'] U ['A', 'Z']
     *  @return an integer
     */
    protected static int getValue(char c)
    {   int value = -1;
        if ( c >= '0' && c <= '9' )
            value = c - 48;
        else if ( c >= 'a' && c <= 'z' )
            value = c - 97 + 10;
        else if ( c >= 'A' && c <= 'Z' )
            value = c - 65 + 10;
        return value;
    }
    
    /** m�thode a char that represent the integer given
     *      if int is in [0,  
     *  @param value an integer
     *  @return a char or null if value is less than 0 or greater than 35
     */
    protected static Character getCharacter(long value)
    {   Character val = null;
        if ( value >= 0 )
        {   if ( value <= 9 )
                val = new Character((char)(value + 48));
            else if ( value <= 35 )
                val = new Character((char)(value - 10 + 65));
        }
        return val;
    }
    
    /** method that check the base
     *  @param base
     *
     *  @exception IllegalArgumentException if base in less than 2 or greater than 36
     */
    private static void checkBase(int base)
    {
        if ( base < 2 || base > 36 )
            throw new IllegalArgumentException("base have to be in the range [2, 36]");
    }
    
    /** convert the given String to classic base
     *  @param converted a String converted in base base
     *  @param base the radix used
     *  @return the converted number in 10-base
     *
     *  @exception IllegalArgumentException if base in less than 2 or greater than 36
     */
    public static long convert(String converted, int base)
    {   
        checkBase(base);
        
        long number = 0;
        if ( converted != null )
        {   if ( converted.length() >= 1 )
            {   for(int i = converted.length() - 1; i >= 0; i--)
                {   int t = getValue(converted.charAt(i));
                    if ( t >= 0 )
                        number += t * Math.pow(base, converted.length() - i - 1);
                }
            }
        }
        return number;
    }
    
    /** convert the number into the given base
     *  @param number the number to convert
     *  @param base the radix to use
     *  @return a String representing the number in the wanted base 
     *
     *  @exception IllegalArgumentException if base in less than 2 or greater than 36
     */
    public static String convert(long number, int base)
    {   
        checkBase(base);
        
        StringBuffer buffer = new StringBuffer();
        if ( number >= 0 )
        {   long r = 0;
            long q = number;
            
            while(q != 0)
            {   r = q % base;
                
                Character c = getCharacter(r);
                if ( c == null )
                    throw new RuntimeException("j'ai merde");
                buffer.insert(0, c);
                q = (q - r) / base;
            }
            if ( buffer.length() == 0 )
                buffer.append("0");
        }
        return buffer.toString();
    }
    
    /** return the integer representing the given hexadecimal
     *  @param hexa an hexadecimal
     *  @return a long
     */
    public static long getDecimalValue(String hexa)
    {   return NumberConversion.convert(hexa, 16); }
    
    /** return the hexadecimal representing the given integer
     *  @param number a long
     *  @return an hexadecimal
     */
    public static String getHexaValue(long number)
    {   return NumberConversion.convert(number, 16); }
}
