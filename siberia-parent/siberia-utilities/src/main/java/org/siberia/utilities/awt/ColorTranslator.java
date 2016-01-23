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
package org.siberia.utilities.awt;

import java.util.*;

import java.awt.Color;
import org.siberia.utilities.lang.NumberConversion;

/**
 * Singleton to build relations between java.awt.Color class and
 * the color String representation used in ColdType package 
 * and kernel package.
 *  
 * example :         "#FF0000"   -->   java.awt.Color.RED
 *
 * @author alexis
 */
public class ColorTranslator
{
    /** Creates a new instance of ColorTranslator */
    private ColorTranslator()
    {   }
    
    /** return a color according to the given html color representation
     *  @param col an html color representation
     *  @return a Color
     */
    public static Color getColorFor(String col)
    {   long[] colors = getRGBComponents(col);
        return new Color((int)colors[0], (int)colors[1], (int)colors[2]);
    }
    
    /** return an html color according to the given color
     *  @param col a Color
     *  @return an html color representation
     */
    public static String getStringRepresentationOf(Color col)
    {   StringBuffer buf = new StringBuffer(7);
        buf.append('#');
        buf.append(getHexaValue(col.getRed()));
        buf.append(getHexaValue(col.getGreen()));
        buf.append(getHexaValue(col.getBlue()));
        return buf.toString();
    }
    
    private static long[] getRGBComponents(String col)
    {   long[] RGB = new long[]{0, 0, 0};
        if ( col != null )
        {   if ( col.length() == 7 )
            {   if ( col.startsWith("#") )
                {   String red = col.substring(1, 3);
                    RGB[0] = getDecimalValue(red);
                    String green = col.substring(3, 5);
                    RGB[1] = getDecimalValue(green);
                    String blue = col.substring(5, 7);
                    RGB[2] = getDecimalValue(blue);
                }
            }
        }
        return RGB;
    }
    
    /** return the integer representing the given hexadecimal
     *  @param hexa an hexadecimal
     *  @return an integer
     */
    public static long getDecimalValue(String hexa)
    {   long result = NumberConversion.getDecimalValue(hexa);
        if ( result > 255 )
            result = 255;
        return result;
    }
    
    /** return the hexadecimal representing the given integer
     *  @param number an integer
     *  @return an hexadecimal
     */
    public static String getHexaValue(int number)
    {   String result = NumberConversion.getHexaValue(number);
        result = result.trim();
        if ( result.length() > 2 )
            result = "FF";
        else if ( result.length() == 1 )
            result = "0" + result;
        else if ( result.length() == 0 )
            result = "00";
        return result;
    }
}
