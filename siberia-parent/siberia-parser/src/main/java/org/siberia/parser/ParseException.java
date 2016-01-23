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
package org.siberia.parser;


/**
 *
 * Define an exception that could be throwed if an error occurred during parsing a property value
 *
 * @author alexis
 */
public class ParseException extends Exception
{
    
    /** Creates a new instance of ParseException
     *  @param value the value we were trying to parse
     *  @param parser the parser used to parse the value
     */
    public ParseException(String value, Parser parser)
    {   super("Could not parse '" + value + "' with " + parser); }
    
}
