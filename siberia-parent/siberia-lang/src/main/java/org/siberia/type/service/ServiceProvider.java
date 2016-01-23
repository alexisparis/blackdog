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
import org.siberia.type.SibList;
import org.siberia.type.lang.SibImport;

/**
 *
 * @author alexis
 */
public interface ServiceProvider extends Runnable, Task
{
    /** indicates if the provider is currently running
     *  @return true if the provider is running
     */
    public boolean isRunning();
    
    /** stop the provider in its current task */
    public void stop();
    
    /** indicates to the provider that its current task should reinit */
    public void shouldReinit();
    
    /** configure the research
     *  @param input the criterion to use in the research
     *  @param list the list to fill
     */
    public void configureResearch(String input, SibList list);
    
    /** tell the result of the parsing to the service provider
     *  @param map a map<String, Class>
     */
    public void setParserResult(Map<String, Class> map);
    
    /** set the import used
     *  @param imports a list of SibImport
     */
    public void setImports(List<SibImport> imports);
    
    /** returns a list of package name that contains a class named className
     *  @param className a name of a class
     *  @return a list of string representing the name of packages that contains a class named className
     */
    public List<String> getPackagesNameContainingClass(String className);
    
}
