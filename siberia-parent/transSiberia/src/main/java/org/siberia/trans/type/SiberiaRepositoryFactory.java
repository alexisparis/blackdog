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
package org.siberia.trans.type;

import java.beans.PropertyVetoException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;
import org.siberia.trans.exception.InvalidRepositoryException;
import org.siberia.trans.exception.RepositoryCreationException;
import org.siberia.trans.type.repository.*;
import org.siberia.xml.schema.pluginarch.Repository;

/**
 *
 * Factory for remote repositories
 *
 * @author alexis
 */
public class SiberiaRepositoryFactory
{
    /** logger */
    private static final Logger logger = Logger.getLogger(SiberiaRepositoryFactory.class);
    
    /** Creates a new instance of SiberiaRepositoryFactory */
    public SiberiaRepositoryFactory()
    {   }
    
    /** method that create a repository according to a String
     *  @param repository a String representing a Repository
     *  @return a SiberiaRepository
     *
     *  @throw RepositoryException when error occurred when creating the repository descriptor
     *	@exception InvalidRepositoryException if the repository is invalid
     */
    public static SiberiaRepository create(String repository) throws RepositoryCreationException,
								     InvalidRepositoryException
    {
        URL url = null;
        try
        {   url = new URL(repository); }
        catch(MalformedURLException e)
        {   throw new RepositoryCreationException(repository, e); }
        
        return create(url);
    }
    
    /** method that create a repository according to a String
     *  @param urlRepository an URL
     *  @return a SiberiaRepository
     *
     *  @throw RepositoryException when error occurred when creating the repository descriptor
     *	@exception InvalidRepositoryException if the repository is invalid
     */
    public static SiberiaRepository create(URL urlRepository) throws RepositoryCreationException,
								     InvalidRepositoryException
    {
        SiberiaRepository rep = create(null, urlRepository);
        
        if ( rep != null )
        {
            String name = null;
	    
	    name = RepositoryUtilities.getName(rep);
            
            try
            {   
                rep.setName(name);
            }
            catch (PropertyVetoException ex)
            {
                ex.printStackTrace();
            }
        }
        
        return rep;
    }
    
    /** method that create a repository according to a String
     *  @param name the name of the repository
     *  @param repository a String representing a Repository
     *  @return a SiberiaRepository
     *
     *  @throw RepositoryException when error occurred when creating the repository descriptor
     */
    public static SiberiaRepository create(String name, String repository) throws RepositoryCreationException
    {   URL url = null;
        try
        {   url = new URL(repository); }
        catch(MalformedURLException e)
        {   throw new RepositoryCreationException(repository, e); }
        
        return create(name, url);
    }
    
    /** method that create a repository according to a String
     *  @param name the name of the repository
     *  @param urlRepository an URL
     *  @return a SiberiaRepository
     *
     *  @throw RepositoryException when error occurred when creating the repository descriptor
     */
    public static SiberiaRepository create(String name, URL urlRepository) throws RepositoryCreationException
    {   SiberiaRepository repository = null;
        if ( urlRepository != null )
        {   if ( urlRepository.getProtocol().equals("http") )
            {   
                HttpSiberiaRepository rep = new HttpSiberiaRepository();
                
                try
                {
                    rep.setURL(urlRepository);
                }
                catch (PropertyVetoException ex)
                {
                    ex.printStackTrace();
                }
                
                repository = rep;
            }
	    else if ( urlRepository.getProtocol().equals("file") )
            {   
                DefaultSiberiaRepository rep = new DefaultSiberiaRepository();
                
                try
                {
                    rep.setURL(urlRepository);
                }
                catch (PropertyVetoException ex)
                {
                    ex.printStackTrace();
                }
                
                repository = rep;
            }
            
            if ( repository != null )
            {
                /* just created --> no PropertyVetoException */
                try
                {   repository.setName(name); }
                catch (PropertyVetoException ex)
                {   ex.printStackTrace(); }
            }
        }
        
        if ( repository == null )
        {
	    logger.error("unable to create repository with " + (urlRepository == null ? null : urlRepository.toString()));
        }
        
        return repository;
    }
}
