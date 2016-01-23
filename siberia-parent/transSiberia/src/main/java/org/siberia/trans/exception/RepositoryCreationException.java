/* 
 * TransSiberia : siberia plugin allowing to update siberia pplications and download new plugins
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
package org.siberia.trans.exception;

/**
 *
 * Exception throwed when error occurred when creating a SiberiaRepository
 *
 * @author alexis
 */
public class RepositoryCreationException extends Exception
{
    /** Creates a new instance of RepositoryException
     *  @param repository a String representing a Repository
     */
    public RepositoryCreationException(String repository)
    {   this(repository, null); }
    
    /** Creates a new instance of RepositoryException
     *  @param repository a String representing a Repository
     *  @param e a Throwable
     */
    public RepositoryCreationException(String repository, Throwable e)
    {   super("unable to create SiberiaRepository for " + repository, e); }
    
}
