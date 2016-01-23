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
//package org.siberia.binding.impl.db.jdo;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Enumeration;
//import java.util.Properties;
//import javax.jdo.Extent;
//import javax.jdo.JDOHelper;
//import javax.jdo.PersistenceManagerFactory;
//import org.apache.log4j.Logger;
//import javax.jdo.PersistenceManager;
//import javax.jdo.Query;
//import javax.jdo.Transaction;
//import org.siberia.binding.exception.DatabaseConfigurationException;
//import org.siberia.binding.exception.SaveException;
//import org.siberia.binding.impl.db.*;
//import org.siberia.binding.impl.db.jdo.jpox.SiberiaPersistenceManagerFactory;
//
///**
// *
// * Abstract implementation of DataBaseBindingManager for JDO
// *
// * @author alexis
// */
//public abstract class JDOBindingManager extends AbstractDataBaseBindingManager
//{
//    /** define if message must provide the state of the objects */
//    private static final boolean MESSAGE_PROVIDE_OBJECTS_STATUS = true;
//    
//    /** property names */
//    protected static final String PROPERTY_NON_TRANSACTION_READ      = "jdo.nonTransactionalRead";
//    
//    protected static final String PROPERTY_NON_TRANSACTION_WRITE     = "jdo.nonTransactionalWrite";
//    
//    protected static final String PROPERTY_PERSISTENCE_MANAGER_CLASS = "jdo.PersistenceManagerFactoryClass";
//    
//    protected static final String PROPERTY_DETACH_ALL_ON_COMMIT      = "jdo.DetachAllOnCommit";
//    
//    /** logger */
//    private static Logger                    logger  = Logger.getLogger(JDOBindingManager.class);
//    
//    /** PersistenceManager */
//    private        PersistenceManager        manager = null;
//    
//    /* PersistenceManagerFactory */
//    private        PersistenceManagerFactory factory = null;
//    
//    /** Creates a new instance of JDOBindingManager */
//    public JDOBindingManager() throws DatabaseConfigurationException
//    {   super();
//        
//        /** initialize with jdo parameters */
//        this.initialize();
//        
//        System.out.println("detach all on commit ? "  + this.factory.getDetachAllOnCommit());
//    }
//    
//    /** initialize bindingManager */
//    protected void initialize()
//    {
//        this.shutdown();
//        
//        Properties convertedProperties = this.convertProperties(this.getProperties());
//        logger.debug("getting " + convertedProperties.size() + " properties to configure BindingManager");
//        
//        this.reportIgnoredProperties();
//        
//        Enumeration en = convertedProperties.keys();
//        while(en.hasMoreElements())
//        {   Object current = en.nextElement();
//            
//            logger.debug("consider database property '" + current + "' --> '" + convertedProperties.get(current) + "'");
//	    
//	    
//        }
//	
////	System.setProperty(PersistenceConfiguration.CLASS_LOADER_RESOLVER_NAME_PROPERTY, "jdo");
//	
////	org.jpox.PersistenceConfiguration
////	org.jpox.plugin.PluginManager
//
//        logger.debug("creating PersistenceManagerFactory");
//        this.factory = JDOHelper.getPersistenceManagerFactory(convertedProperties, getClass().getClassLoader());
//        logger.debug("PersistenceManagerFactory created : " + this.factory);
//
//        logger.debug("creating PersistenceManager");
//        this.manager = this.factory.getPersistenceManager();
//        logger.debug("PersistenceManager created : " + this.manager);
//    }
//    
//    /** return the PersistenceManager
//     *  @return a PersistenceManager
//     */
//    protected PersistenceManager getManager()
//    {   return this.manager; }
//    
//    /** shutdown manager */
//    public void shutdown()
//    {   if ( this.getManager() != null )
//        {   this.getManager().close(); }
//        
//        if ( this.factory != null )
//        {   this.factory.close(); }
//    }
//    
//    /** convert properties specified for jdo
//     *  @param initialProperties
//     *  @return a Properties
//     */
//    protected Properties convertProperties(Properties initialProperties)
//    {   Properties result = null;
//        
//        if ( initialProperties != null )
//        {
//            result = new Properties();
//            Enumeration en = initialProperties.propertyNames();
//            
//            while(en.hasMoreElements())
//            {   Object current = en.nextElement();
//                
//                if ( current instanceof String )
//                {   
//                    boolean processed = true;
//                    
//                    String value = initialProperties.getProperty((String)current);
//                    if ( current.equals(PROPERTY_CONNECTION_DRIVER_NAME) )
//                    {   result.put("javax.jdo.option.ConnectionDriverName", value); }
//                    else if ( current.equals(PROPERTY_CONNECTION_URL) )
//                    {   result.put("javax.jdo.option.ConnectionURL", value); }
//                    else if ( current.equals(PROPERTY_CONNECTION_USERNAME) )
//                    {   result.put("javax.jdo.option.ConnectionUserName", value); }
//                    else if ( current.equals(PROPERTY_CONNECTION_PASSWORD) )
//                    {   result.put("javax.jdo.option.ConnectionPassword", value); }
//                    else if ( current.equals(PROPERTY_NON_TRANSACTION_READ) )
//                    {   result.put("javax.jdo.option.NontransactionalRead", value); }
//                    else if ( current.equals(PROPERTY_NON_TRANSACTION_WRITE) )
//                    {   result.put("javax.jdo.option.NontransactionalWrite", value); }
//                    else if ( current.equals(PROPERTY_PERSISTENCE_MANAGER_CLASS) )
//                    {   result.put("javax.jdo.PersistenceManagerFactoryClass", value); }
//                    else if ( current.equals(PROPERTY_DETACH_ALL_ON_COMMIT) )
//                    {   result.put("javax.jdo.option.DetachAllOnCommit", value); }
//                    
//                    else
//                    {   processed = false; }
//                    
//                    if ( processed )
//                    {   this.setPropertyAsProcessed((String)current); }
//                }
//            }
//        }
//        
//        return result;
//    }
//    
//    /** methods that warn that a Transaction is rollbacked
//     *  @param trasaction the rollbacked transaction
//     *  @param msg the description of the operation in which a Transaction was rollbacked
//     */
//    private void logTransactionRollback(Transaction transaction, String msg)
//    {   logger.error("transaction rollbacked" + (msg == null ? "" : " for action : " + msg)); }
//    
//    /** methods that warn that a Transaction is committed
//     *  @param trasaction the commited transaction
//     *  @param msg the description of the operation in which a Transaction was committed
//     */
//    private void logTransactionCommitted(Transaction transaction, String msg)
//    {   logger.info("transaction committed" + (msg == null ? "" : " for action : " + msg)); }
//    
//    /** format the state of Objects
//     *  @param types an array of Objects
//     *  @return a StringBuffer
//     */
//    private StringBuffer formatObjectsStatus(Object... types)
//    {   StringBuffer buffer = new StringBuffer();
//        
//        if ( types != null )
//        {   buffer.append("[");
//            
//            for(int i = 0; i < types.length; i++)
//            {   Object current = types[i];
//                
//                buffer.append(current + "(kind=" + (current == null ? null : current.getClass()) + ")");
//                
//                if ( i < types.length - 1 )
//                {   buffer.append(", "); }
//            }
//        }
//        buffer.append("]");
//        
//        return buffer;
//    }
//    
//    /** ########################################################################
//     *  ############################ DATA ACCESS ###############################
//     *  ######################################################################## */
//
//    /**
//     * save objects in database
//     * 
//     * @param types an array of Object
//     * @exception SaveException if errors occured
//     */
//    public void store(Object... types) throws SaveException
//    {
//        Transaction tx = this.getManager().currentTransaction();
//        
//        String description = null;
//        if ( MESSAGE_PROVIDE_OBJECTS_STATUS )
//        {   StringBuffer buffer = this.formatObjectsStatus(types);
//            
//            buffer.insert(0, "storing ");
//            
//            description = buffer.toString();
//        }
//        
//        try
//        {   tx.begin();
//
//            this.getManager().makePersistent(types[0]);
//
//            tx.commit();
//            this.logTransactionCommitted(tx, description);
//        }
//        finally
//        {   if (tx.isActive())
//            {   tx.rollback();
//                this.logTransactionRollback(tx, description);
//            }
//        }
//    }
//
//    /**
//     * refresh the status of objects stored in database
//     * @param types an array of Object
//     */
//    public void refresh(Object types)
//    {
//        Transaction tx = this.getManager().currentTransaction();
//        
//        String description = null;
//        if ( MESSAGE_PROVIDE_OBJECTS_STATUS )
//        {   StringBuffer buffer = this.formatObjectsStatus(types);
//            
//            buffer.insert(0, "refreshing ");
//            
//            description = buffer.toString();
//        }
//        
//        try
//        {   tx.begin();
//
//            this.getManager().refresh(types);
//
//            tx.commit();
//            this.logTransactionCommitted(tx, description);
//        }
//        finally
//        {   if (tx.isActive())
//            {   tx.rollback();
//                this.logTransactionRollback(tx, description);
//            }
//        }
//    }
//
//    /**
//     * delete objects in database
//     * 
//     * @param types an array of Object
//     */
//    public void delete(Object... types)
//    {
//        Transaction tx = this.getManager().currentTransaction();
//        
//        String description = null;
//        if ( MESSAGE_PROVIDE_OBJECTS_STATUS )
//        {   StringBuffer buffer = this.formatObjectsStatus(types);
//            
//            buffer.insert(0, "deleting ");
//            
//            description = buffer.toString();
//        }
//        
//        try
//        {   tx.begin();
//
//            this.getManager().deletePersistentAll(types);
//
//            tx.commit();
//            this.logTransactionCommitted(tx, description);
//        }
//        finally
//        {   if (tx.isActive())
//            {   tx.rollback();
//                this.logTransactionRollback(tx, description);
//            }
//        }
//    }
//    
//    /** return the number of items of the given type in database (including element that are related to sub classes )
//     *  @param typeClass the kind of item
//     */
//    public int getItemsCount(Class typeClass)
//    {   return this.getItemsCount(typeClass, true); }
//    
//    /** return a Collection of items that convey to given critera
//     *  @param typeClass the kind of item to load
//     *  @param subclasses whether to include instances of subclasses
//     */
//    public int getItemsCount(Class typeClass, boolean subClasses)
//    {   return this.getItems(typeClass, subClasses).size(); }
//    
//    /** return a Collection of items of the given type (including element that are related to sub classes )
//     *  @param typeClass the kind of item to load
//     *  @return a Collection
//     */
//    public Collection getItems(Class typeClass)
//    {   return this.getItems(typeClass, true); }
//    
//    /** return a Collection of items of the given type
//     *  @param typeClass the kind of item to load
//     *  @param subclasses whether to include instances of subclasses
//     *  @return a Collection
//     */
//    public Collection getItems(Class typeClass, boolean subClasses)
//    {
//        Collection collec = null;
//        
//        Transaction tx = this.getManager().currentTransaction();
//        try
//        {   tx.begin();
//
//            Extent e = this.getManager().getExtent(typeClass, subClasses);
//            Query q = this.getManager().newQuery(e);//,"price < 150.00");
//
//            collec = (Collection)q.execute();
//
//            tx.commit();
//        }
//        finally
//        {   if (tx.isActive())
//            {   tx.rollback(); }
//        }
//        
//        if (collec == null )
//            collec = Collections.emptyList();
//        
//        return collec;
//    }
//    
//}
