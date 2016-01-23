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

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * Static class which stores parser according to an id or a class
 *
 * @author alexis
 */
public class ParserRegistry
{
//    /** id of extension point defining a parser */
//    private static final String PARSER_PLUGIN_ID  = "Parser";
//    
//    /** name of the attribute that indicate the type of the parser in parser extended point */
//    private static final String PARSER_TYPE_ATTR  = "type";
//    
//    /** name of the attribute that indicate the class of the parser in parser extended point */
//    private static final String PARSER_CLASS_ATTR = "class";
    
    /** logger */
    private static Logger logger = Logger.getLogger(ParserRegistry.class);
    
    /** map for PropertyParser according to id */
    private Map<String, Parser> idParsers    = null;
    
    /** register a new PropertyParser
     *  @param nature a String id associated with the parser
     *  @param parser an implementation of PropertyParser
     */
    public void registerParser(String nature, Parser parser)
    {   if ( parser != null )
        {   if ( idParsers == null )
                idParsers = new HashMap<String, Parser>();
            Parser p = idParsers.get(nature);
            if ( p != null )
                logger.warn("already contains a parser associated with id '" + nature + "', overwritten.");
            idParsers.put(nature, parser);
        }
    }
    
    /** unregister a new PropertyParser
     *  @param nature a String id associated with the parser
     */
    public void unregisterParser(String nature)
    {   if ( idParsers != null )
        {   idParsers.remove(nature); }
    }
    
    /** register a new PropertyParser
     *  @param c a Class
     *  @param parser an implementation of PropertyParser
     */
    public void registerParser(Class c, Parser parser)
    {   if ( parser != null && c != null)
        {   registerParser(c.getName(), parser); }
    }
    
    /** unregister a new PropertyParser
     *  @param c a Class
     */
    public void unregisterParser(Class c)
    {   if ( c != null )
        {   unregisterParser(c.getName()); }
    }
    
    /** return a parser for a given nature of parser
     *  @param c a Class
     *  @return a Parser or null if not found
     */
    public Parser getParser(Class c)
    {   Parser p = null;
        if ( c != null )
        {   p = getParser(c.getName()); }
        return p;
    }
    
    /** return a parser for a given nature of parser
     *  @param nature the id of a parser
     *  @return a Parser or null if not found
     */
    public Parser getParser(String nature)
    {   
        Parser parser = null;
        if ( idParsers != null )
        {   parser = idParsers.get(nature); }
        return parser;
    }
    
}
