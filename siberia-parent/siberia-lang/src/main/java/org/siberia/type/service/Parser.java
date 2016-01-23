/* 
 * Siberia lang : java language utilities
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
package org.siberia.type.service;

import java.util.List;
import java.util.Map;
import org.siberia.type.lang.SibImport;

/**
 *
 * @author alexis
 */
public interface Parser
{
    
    /* return a mapping of the instantiation found in the text
     *  @param text the String to parse
     *  @return a Map<String, Class>
     */
    public Map<String, Class> parse(String text);
    
    /** returns a list of all unresolved symbols in the gieven text 
     *  @param text a String
     *  @return a list of String representing the unresolved symbols used in the text
     */
    public List<String> getUnresolvedSymbols(String text);
    
    /** returns all the imports used by the script
     *  @return a list of ColdImport
     */
    public List<SibImport> getImports();
    
    /** indicates to the parser that last results are not good because context changed */
    public void isNowInconsistent();
    
}
