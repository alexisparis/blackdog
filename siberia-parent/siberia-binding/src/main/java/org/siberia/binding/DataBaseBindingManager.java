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
package org.siberia.binding;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.siberia.binding.constraint.DataBaseBindingConstraint;
import org.siberia.binding.exception.DatabaseConfigurationException;
import org.siberia.binding.exception.SaveException;
import org.siberia.binding.transaction.Transaction;

/**
 *
 * Describe the services provided by an entity that is able to bind SibTypes instances in databases
 *
 * @author alexis
 */
public interface DataBaseBindingManager<T extends Transaction, C extends DataBaseBindingConstraint> extends BindingManager
{
    /** option names */
    public static final String PROPERTY_CONNECTION_DRIVER_NAME = "connectionDriverName";
    public static final String PROPERTY_CONNECTION_URL         = "connectionURL";
    public static final String PROPERTY_CONNECTION_USERNAME    = "connectionUserName";
    public static final String PROPERTY_CONNECTION_PASSWORD    = "connectionPassword";
    
    /** set the database properties
     *  @param properties a Properties
     */
    public void setProperties(Properties properties) throws DatabaseConfigurationException;
    
    /** drop all databases tables */
    public void dropAllTables();
    
    /** create all databases tables */
    public void createAllTables();
    
    /** shutdown manager
     *  close all resources opened by the manager including database connections
     */
    public void shutdown();
    
    /** create a new Transaction
     *	@return a new T
     */
    public T createTransaction() throws Exception;
    
    /** return true if the item exists
     *	@param object the object to check
     *	@return true if the object exists
     */
    public boolean existsInsideTransaction(Object object) throws Exception;
    
    /** return true if the item exists
     *	@param transaction a Transaction
     *	@param object the object to check
     *	@return true if the object exists
     */
    public boolean exists(T transaction, Object object) throws Exception;

    /**
     * save objects in database
     * 
     *  @param types an array of Object
     *  @exception Exception if errors occured
     */
    public void storeInsideTransaction(Object... types) throws Exception;

    /**
     * save objects in database
     * 
     *	@param transaction a Transaction or null to create a new Transaction
     *  @param types an array of Object
     */
    public void store(T transaction, Object... types) throws Exception;

    /**
     * refresh the status of an object stored in database
     * 
     *  @param type an Object
     */
    public void refresh(Object type) throws Exception;

    /**
     * refresh the status of an object stored in database
     * 
     *  @param transaction a Transaction
     *  @param type an Object
     */
    public void refresh(T transaction, Object type) throws Exception;

    /**
     * delete objects in database
     * 
     *  @param types an array of Object
     */
    public void deleteInsideTransaction(Object... types) throws Exception;

    /**
     * delete objects in database
     * 
     *  @param transaction a Transaction
     *  @param types an array of Object
     */
    public void delete(T transaction, Object... types) throws Exception;
    
    /** load objects
     *	@param kind a Class that represents the kind of item to load
     *  @param constraints an array of BindingConstraint that allow to filter objects to load
     *	@return  list of Objects resulting from the loading process
     */
    public List loadInsideTransaction(Class kind, C... constraints) throws Exception;
    
    /** load objects
     *  @param transaction a Transaction
     *	@param kind a Class that represents the kind of item to load
     *  @param constraints an array of BindingConstraint that allow to filter objects to load
     *	@return a list of Objects resulting from the loading process
     */
    public List load(T transaction, Class kind, C... constraints) throws Exception;
    
    /** return a Collection of items that convey to given critera
     *  @param typeClass the kind of item to load
     *  @param subclasses whether to include instances of subclasses
     */
    public int getItemsCount(Class typeClass, boolean subClasses) throws Exception;
    
    /** return a Collection of items that convey to given critera
     *  @param transaction a Transaction
     *  @param typeClass the kind of item to load
     *  @param subclasses whether to include instances of subclasses
     */
    public int getItemsCount(T transaction, Class typeClass, boolean subClasses) throws Exception;
    
}
