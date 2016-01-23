/* 
 * Siberia parser : siberia plugin defining object parsers
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
package org.siberia.parser.impl;

import org.siberia.parser.ParseException;
import org.siberia.parser.Parser;

/**
 *
 * Conversion methods for Boolean
 *
 * @author alexis
 */
public class BooleanParser implements Parser
{
    /** parse a String and return an object that implements Comparable
     *  @param value a String to parse
     *  @return an Object that is Comparable
     *
     *  @throws ParseException if error occurred during parsing
     */
    public Comparable parse(String value) throws ParseException
    {   
        Boolean bApply = Boolean.parseBoolean(value);
        if ( ! bApply.booleanValue() )
        {   boolean ok = true;
            if ( value == null )
                ok = false;
            else
            {   if ( ! value.trim().equalsIgnoreCase("false") )
                {   ok = false; }
            }
            
            if ( ! ok )
                throw new ParseException(value, this);
        }
        return bApply;
    }
    
}
