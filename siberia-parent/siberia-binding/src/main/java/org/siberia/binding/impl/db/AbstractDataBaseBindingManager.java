/* 
 * Siberia binding : siberia plugin defining persistence services
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
package org.siberia.binding.impl.db;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.binding.constraint.DataBaseBindingConstraint;
import org.siberia.binding.exception.DatabaseConfigurationException;
import org.siberia.binding.exception.DatabaseConfigurationNotFoundException;
import org.siberia.binding.DataBaseBindingManager;
import org.siberia.binding.exception.LoadException;
import org.siberia.binding.exception.SaveException;
import org.siberia.binding.transaction.Transaction;

/**
 *
 * Abstract DataBaseBindingManager.
 *
 * if connection url contains "{application_dir}", then this pattern is replaced by :<br>
 *  
 *  {application directory} + file.separator + "database"
 *
 * @author alexis
 */
public abstract class AbstractDataBaseBindingManager<T extends Transaction> implements DataBaseBindingManager<T, DataBaseBindingConstraint>
{
    /** logger */
    private static Logger      logger            = Logger.getLogger(AbstractDataBaseBindingManager.class);
    
    /** list of ignored property */
    private        Set<String> ignoredProperties = null;
    
    /** list of database properties */
    private        Properties  properties        = null;
    
    /** Creates a new instance of AbstractDataBaseBindingManager */
    public AbstractDataBaseBindingManager() throws DatabaseConfigurationException
    {
        Properties properties = null;
        try
        {   properties = DBUtilities.getDataBaseParameters(); }
        catch(DatabaseConfigurationNotFoundException e)
        {   throw new DatabaseConfigurationException(this, e); }
        
        /** considered all properties as ignored */
        if ( properties != null )
        {   Enumeration en = properties.propertyNames();
            
            while(en.hasMoreElements())
            {   Object current = en.nextElement();
                
                if ( current instanceof String )
                {   if ( this.ignoredProperties == null )
                    {   this.ignoredProperties = new HashSet<String>(properties.size()); }
                    
                    this.ignoredProperties.add( (String)current );
                }
            }
        }
	
	/** for property database url, if the value contains {applicationDirectory}
	 *  then it is replaced by the path of the application directory (for example {user.dir}/.{application name}/database
	 */
	String value = properties.getProperty(DataBaseBindingManager.PROPERTY_CONNECTION_URL);
	
	if ( value != null )
	{
	    String applicationDirMotif = "{application_dir}";
	    int applicationDirIndex = value.indexOf(applicationDirMotif);
	    
	    if ( applicationDirIndex != -1 )
	    {
		if ( logger.isInfoEnabled() )
		{
		    logger.info("initial url connection contains '" + applicationDirMotif + "' --> replace it");
		}
		StringBuffer buffer = new StringBuffer(100);
		buffer.append(value);
		
		buffer.delete(applicationDirIndex, applicationDirIndex + applicationDirMotif.length());
		
		buffer.insert(applicationDirIndex, ResourceLoader.getInstance().getApplicationHomeDirectory() + 
			      System.getProperty("file.separator") + "database");
		
		if ( logger.isInfoEnabled() )
		{
		    logger.info("new connection url is : " + buffer.toString());
		}
		
		properties.setProperty(DataBaseBindingManager.PROPERTY_CONNECTION_URL, buffer.toString());
	    }
	}
        
        this.setProperties(properties);
    }

    /** method that dispose the binding manager */
    public final void dispose()
    {   this.shutdown(); }
    
    /** ########################################################################
     *  ###################### properties management ###########################
     *  ######################################################################## */
    
    /** launch the report of unused property */
    protected void reportIgnoredProperties()
    {   
        /** warn for ignored properties */
        if ( this.ignoredProperties != null )
        {   Iterator<String> it = this.ignoredProperties.iterator();
            
            while(it.hasNext())
            {   String current = it.next();
                
                logger.warn("unable to process database property '" + current + "' --> ignored");
            }
        }
    }
    
    /** initialize the properties
     *  @param properties a Properties
     */
    public void setProperties(Properties properties)
    {   this.properties = properties; }
    
    /** return the properties
     *  @return a Properties
     */
    public Properties getProperties()
    {   return this.properties; }
    
    /** indicate that a properties has been processed
     *  @param property a Property
     */
    protected void setPropertyAsProcessed(String property)
    {   logger.debug("database property '" + property + "' processed");
        this.ignoredProperties.remove(property);
    }
    
    /** ########################################################################
     *  ######################### data management ##############################
     *  ######################################################################## */
    
    /** return true if the item exists
     *	@param object the object to check
     *	@return true if the object exists
     */
    public boolean existsInsideTransaction(Object object) throws Exception
    {
	boolean exists = false;
	
	T transaction = null;
	
	try
	{
	    transaction = this.createTransaction();
	    
	    exists = this.exists(transaction, object);
	    
	    transaction = null;
	}
	finally
	{
	    if ( transaction != null )
	    {
		try
		{   transaction.rollback(); }
		catch(Exception e)
		{
		    logger.error("error while rollbacking transaction", e);
		}
	    }
	}
	
	return exists;
    }

    /**
     * save objects in database
     * 
     *  @param types an array of Object
     *  @exception SaveException if errors occured
     */
    public void storeInsideTransaction(Object... types) throws Exception
    {
	T transaction = null;
	
	try
	{
	    transaction = this.createTransaction();
	    
	    this.store(transaction, types);
	    
	    transaction.commit();
	    
	    transaction = null;
	}
	finally
	{
	    if ( transaction != null )
	    {
		try
		{   transaction.rollback(); }
		catch(Exception e)
		{
		    logger.error("error while rollbacking transaction", e);
		}
	    }
	}
    }
    
    /** load objects
     *	@param kind a Class that represents the kind of item to load
     *  @param constraints an array of BindingConstraint that allow to filter objects to load
     *	@return  list of Objects resulting from the loading process
     */
    public List loadInsideTransaction(Class kind, DataBaseBindingConstraint... constraints) throws Exception
    {
	List result = null;
	
	T transaction = null;
	
	try
	{
	    transaction = this.createTransaction();
	    
	    result = this.load(transaction, kind, constraints);
	    
	    transaction.commit();
	    
	    transaction = null;
	}
	finally
	{
	    if ( transaction != null )
	    {
		try
		{   transaction.rollback(); }
		catch(Exception e)
		{
		    logger.error("error while rollbacking transaction", e);
		}
	    }
	}
	
	if ( result == null )
	{
	    result = Collections.emptyList();
	}
	
	return result;
    }

    /**
     * refresh the status of an object stored in database
     * 
     *  @param type an Object
     */
    public void refresh(Object type) throws Exception
    {
	T transaction = null;
	
	try
	{
	    transaction = this.createTransaction();
	    
	    this.refresh(transaction, type);
	    
	    transaction.commit();
	    
	    transaction = null;
	}
	finally
	{
	    if ( transaction != null )
	    {
		try
		{   transaction.rollback(); }
		catch(Exception e)
		{
		    logger.error("error while rollbacking transaction", e);
		}
	    }
	}
    }

    /**
     * delete objects in database
     * 
     *  @param types an array of Object
     */
    public void deleteInsideTransaction(Object... types) throws Exception
    {
	T transaction = null;
	
	try
	{
	    transaction = this.createTransaction();
	    
	    this.delete(transaction, types);
	    
	    transaction.commit();
	    
	    transaction = null;
	}
	finally
	{
	    if ( transaction != null )
	    {
		try
		{   transaction.rollback(); }
		catch(Exception e)
		{
		    logger.error("error while rollbacking transaction", e);
		}
	    }
	}
    }
    
    /** return a Collection of items that convey to given critera
     *  @param typeClass the kind of item to load
     *  @param subclasses whether to include instances of subclasses
     */
    public int getItemsCount(Class typeClass, boolean subClasses) throws Exception
    {
	int result = 0;
	
	T transaction = null;
	
	try
	{
	    transaction = this.createTransaction();
	    
	    result = this.getItemsCount(transaction, typeClass, subClasses);
	    
	    transaction.commit();
	    
	    transaction = null;
	}
	finally
	{
	    if ( transaction != null )
	    {
		try
		{   transaction.rollback(); }
		catch(Exception e)
		{
		    logger.error("error while rollbacking transaction", e);
		}
	    }
	}
	
	return result;
    }
    
}
