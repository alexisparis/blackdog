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

/**
 *
 * interface for all semantical object that manage import
 * 
 * @author alexis
 */
public interface ImportManager
{
    /** return a String representing all the imports to add
     *  @return a String representing all the imports to add
     */
    public String getResultImports();
    

    /** set a parser that would be used by the import manager
     *  @param parser an Cold parser
     */
    public void setParser(Parser parser);
    

    /** return a parser that would be used by the import manager
     *  @return an Cold parser
     */
    public Parser getParser();

    /** returns the service provider that can be used by the import manager
     *  @return an instance of Service provider
     */
    public ServiceProvider getServiceProvider();
    
    /** method to add import information
     *  @param instanceName the name of a class
     *  @param packageName the name of the corresponding package
     */
    public void addImportPart(String instanceName, String packageName);
    
    /* reset the current results */
    public void reset();
    
}
