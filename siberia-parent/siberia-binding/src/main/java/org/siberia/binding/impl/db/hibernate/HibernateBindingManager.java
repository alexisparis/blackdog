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
package org.siberia.binding.impl.db.hibernate;

import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.InitializeCollectionEvent;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreLoadEvent;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.type.Type;
import org.siberia.TypeInformationProvider;
import org.siberia.binding.constraint.DataBaseBindingConstraint;
import org.siberia.binding.exception.DatabaseConfigurationException;
import org.siberia.binding.exception.SaveException;
import org.siberia.binding.impl.db.*;
import org.siberia.binding.transaction.Transaction;
import org.siberia.type.SibType;
import org.siberia.ResourceLoader;

/**
 *
 * DataBaseBindingManager based on hibernate framework
 *
 * @author alexis
 */
public class HibernateBindingManager extends AbstractDataBaseBindingManager<HibernateTransactionHandler>
{
    /** property hibernate dialect */
    public static final  String         PROPERTY_DIALECT                 = "hibernate.dialect";
    
    /** property hibernate hbm2ddl auto */
    public static final  String         PROPERTY_HBM2DDL_AUTO            = "hibernate.hbm2ddl.auto";
    
    /** property factory */
    public static final  String         PROPERTY_FACTORY_CLASS           = "hibernate.jdbc.factory_class";// "transaction.factory_class";
    
    /** session context class */
    public static final  String         PROPERTY_SESSION_CONTEXT_CLASS   = "hibernate.current_session_context_class";
    
    /** isolation */
    public static final  String         PROPERTY_ISOLATION               = "hibernate.connection.isolation";
    
    /** connection release mode */
    public static final  String         PROPERTY_CONNECTION_RELEASE_MODE = "hibernate.connection.release_mode";
    
    /** batch size */
    public static final  String         PROPERTY_BATCH_SIZE              = "hibernate.jdbc.batch_size";
    
    /** auto commit property */
    public static final  String         PROPERTY_AUTO_COMMIT             = "hibernate.connection.autocommit";
    
    /** auto close session */
    public static final  String         PROPERTY_AUTO_CLOSE_SESSION      = "hibernate.transaction.auto_close_session";
    
    /** show sql */
    public static final  String         PROPERTY_SHOW_SQL                = "hibernate.show_sql";
    
    /** format sql */
    public static final  String         PROPERTY_FORMAT_SQL              = "hibernate.format_sql";
    
    /** logger */
    public               Logger         logger                      = Logger.getLogger(HibernateBindingManager.class);
    
    /** session factory */
    private              SessionFactory sessionFactory              = null;
    
    /** configuration */
    private              Configuration  configuration               = null;
    
    
    /** Creates a new instance of HibernateBindingManager */
    public HibernateBindingManager() throws DatabaseConfigurationException
    {
        this.configuration = this.createConfiguration();
	
//	org.hibernate.event.PreLoadEventListener preLoadListener = new org.hibernate.event.PreLoadEventListener()
//	{
//	    public void onPreLoad(PreLoadEvent event)
//	    {
//		System.err.println("############# onPreLoad");
//		System.err.println("############# onPreLoad");
//		System.err.println("############# onPreLoad");
//		System.err.println("############# onPreLoad");
//		System.err.println("############# onPreLoad");
//	    }
//	};
//	configuration.setListener("pre-load", preLoadListener);
//	
//	org.hibernate.event.PostLoadEventListener postLoadListener = new org.hibernate.event.PostLoadEventListener()
//	{
//	    public void onPostLoad(PostLoadEvent event)
//	    {
//		System.err.println("############# onPostLoad");
//		System.err.println("############# onPostLoad");
//		System.err.println("############# onPostLoad");
//		System.err.println("############# onPostLoad");
//		System.err.println("############# onPostLoad");
//	    }
//	};
//	configuration.setListener("post-load", postLoadListener);
//	
//	org.hibernate.event.InitializeCollectionEventListener collecListener = new org.hibernate.event.InitializeCollectionEventListener()
//	{
//	    public void onInitializeCollection(InitializeCollectionEvent event) throws HibernateException
//	    {
//		System.err.println("############# onInitializeCollection");
//		System.err.println("############# onInitializeCollection");
//		System.err.println("############# onInitializeCollection");
//		System.err.println("############# onInitializeCollection");
//		System.err.println("############# onInitializeCollection");
//	    }
//	};
//	configuration.setListener("load-collection", collecListener);
	
	
	//	public void setListeners(String type, Object[] listeners) {
//                        "auto-flush"
//                        "merge"
//                        "create"
//                        "create-onflush"
//                        "delete"
//                        "dirty-check"
//                        "evict"
//                        "flush"
//                        "flush-entity"
//			  "load"
//                        "load-collection"
//                        "lock"
//                        "refresh"
//                        "replicate"
//                        "save-update"
//                        "save"
//                        "update"
//                        "pre-load"
//                        "pre-update"
//                        "pre-delete"
//                        "pre-insert"
//                        "post-load"
//                        "post-update"
//                        "post-delete"
//                        "post-insert"
//                        "post-commit-update"
//                        "post-commit-delete"
//                        "post-commit-insert"
	/////////////////////
//        configuration.EventListeners().
	
//	configuration = configuration.setInterceptor(new EmptyInterceptor()
//	{
//	    public void afterTransactionBegin(org.hibernate.Transaction tx)
//	    {
//		System.err.println("interceptor :: afterTransactionBegin");
//		super.afterTransactionBegin(tx);
//	    }
//	    public void afterTransactionCompletion(org.hibernate.Transaction tx)
//	    {
//		System.err.println("interceptor :: afterTransactionCompletion");
//		super.afterTransactionCompletion(tx);
//	    }
//	    public void beforeTransactionCompletion(org.hibernate.Transaction tx)
//	    {
//		System.err.println("interceptor :: beforeTransactionCompletion");
//		super.beforeTransactionCompletion(tx);
//	    }
//	    public int[] findDirty(Object entity, Serializable id, Object[]
//		    currentState, Object[] previousState, String[] propertyNames, Type[] types)
//	    {
//		System.err.println("interceptor :: findDirty");
//		return super.findDirty(entity, id, currentState, previousState, propertyNames, types);
//	    }
//	    public Object getEntity(String entityName, Serializable id) throws CallbackException
//	    {
//		System.err.println("interceptor :: getEntity");
//		return super.getEntity(entityName, id);
//	    }
//	    public String getEntityName(Object object) throws CallbackException
//	    {
//		System.err.println("interceptor :: getEntityName");
//		return super.getEntityName(object);
//	    }
//	    public Object instantiate(String entityName, EntityMode entityMode, Serializable id) throws CallbackException
//	    {
//		System.err.println("interceptor :: instantiate");
//		return super.instantiate(entityName, entityMode, id);
//	    }
//	    public Boolean isTransient(Object entity)
//	    {
//		System.err.println("interceptor :: isTransient");
//		return super.isTransient(entity);
//	    }
//	    public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException
//	    {
//		System.err.println("interceptor :: onCollectionRecreate");
//		super.onCollectionRecreate(collection, key);
//	    }
//	    public void onCollectionRemove(Object collection, Serializable key) throws CallbackException
//	    {
//		System.err.println("interceptor :: onCollectionRemove");
//		super.onCollectionRemove(collection, key);
//	    }
//	    public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException
//	    {
//		System.err.println("interceptor :: onCollectionUpdate");
//		super.onCollectionUpdate(collection, key);
//	    }
//	    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException
//	    {
//		System.err.println("interceptor :: onDelete");
//		super.onDelete(entity, id, state, propertyNames, types);
//	    }
//	    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, 
//					Object[] previousState, String[] propertyNames, 
//					Type[] types) throws CallbackException
//	    {
//		System.err.println("interceptor :: onFlushDirty");
//		return super.onFlushDirty(entity, id, currentState, 
//					previousState, propertyNames, 
//					types);
//	    }
//	    public boolean onLoad(Object entity, Serializable id, Object[] state, 
//				  String[] propertyNames, Type[] types) throws CallbackException
//	    {
//		System.err.println("interceptor :: onLoad");
//		return super.onLoad(entity, id, state, propertyNames, types);
//	    }
//	    public String onPrepareStatement(String sql)
//	    {
//		System.err.println("interceptor :: onPrepareStatement");
//		return super.onPrepareStatement(sql);
//	    }
//	    public boolean onSave(Object entity, Serializable id, Object[] state,
//				  String[] propertyNames, Type[] types) throws CallbackException
//	    {
//		System.err.println("interceptor :: onSave");
//		return super.onSave(entity, id, state, propertyNames, types);
//	    }
//	    public void postFlush(Iterator entities) throws CallbackException
//	    {
//		System.err.println("interceptor :: postflush");
//		super.postFlush(entities);
//	    }
//	    public void preFlush(Iterator entities) throws CallbackException
//	    {
//		System.err.println("interceptor :: preFlush");
//		super.preFlush(entities);
//	    }
//	});
        
        try
        {   
	    /* Create the SessionFactory */
            this.sessionFactory = this.configuration.buildSessionFactory();
        }
        catch (Exception ex)
        {   // Make sure you log the exception, as it might be swallowed
	    logger.error("Initial SessionFactory creation failed." + ex);
	    ex.printStackTrace();
        }
	
//	Hibernate.initialize(null);
//	ThreadLocalSessionContext
//	org.hibernate.impl.SessionFactoryImpl
	
//	Iterator it = configuration.getTableMappings();
//	System.out.println("table mappings : ");
//	while(it.hasNext())
//	{
//	    Object current = it.next();
//	    System.out.println("\t" + current + " of kind : " + current.getClass());
//	    
//	    if ( current instanceof org.hibernate.mapping.Table )
//	    {
//		org.hibernate.mapping.Table table = (org.hibernate.mapping.Table)current;
////		configuration.get
////		String sqlDel = table.sqlDropString(null, table.getCatalog(), table.getSchema());
////		System.out.println("toDelete : " + sqlDel);
//	    }
//	}
	
//	org.hibernate.mapping.Table(ABSTRACT_TAGGED_SONG_ITEM) of kind : class org.hibernate.mapping.Table
//        org.hibernate.mapping.Table(AUDIO_ITEM) of kind : class org.hibernate.mapping.Table
//        org.hibernate.mapping.Table(DEFAULT_SONG_ITEM) of kind : class org.hibernate.mapping.Table
//        org.hibernate.mapping.Table(IGNORED_DIRECTORIES) of kind : class org.hibernate.mapping.Table
//        org.hibernate.mapping.Table(MP3_SONG_ITEM) of kind : class org.hibernate.mapping.Table
//        org.hibernate.mapping.Table(PLAYLIST) of kind : class org.hibernate.mapping.Table
//        org.hibernate.mapping.Table(PLAYLIST_ITEMS) of kind : class org.hibernate.mapping.Table
//        org.hibernate.mapping.Table(RADIO_ITEM) of kind : class org.hibernate.mapping.Table
//        org.hibernate.mapping.Table(SCANNABLE_PLAYLIST) of kind : class org.hibernate.mapping.Table
//        org.hibernate.mapping.Table(SCANNED_DIRECTORIES) of kind : class org.hibernate.mapping.Table
//        org.hibernate.mapping.Table(SONG_ITEM) of kind : class org.hibernate.mapping.Table

	this.ensureTableCreations();
    }
    
    /** drop all databases tables */
    public void dropAllTables()
    {
        SchemaExport export = new SchemaExport(this.configuration);
	export.drop(false, true);
	
	List exceptions = export.getExceptions();
	if ( exceptions != null )
	{
	    for(int i = 0; i < exceptions.size(); i++)
	    {
		Object current = exceptions.get(i);
		
		if ( current instanceof Throwable )
		{
		    logger.error("exception occured while droping tables", (Throwable)current);
		}
	    }
	}
    }
    
    /** ensure that all tables are created */
    private void ensureTableCreations()
    {
        SchemaUpdate update = new SchemaUpdate(this.configuration);
        update.execute(true, true);
	
	List exceptions = update.getExceptions();
	if ( exceptions != null )
	{
	    for(int i = 0; i < exceptions.size(); i++)
	    {
		Object current = exceptions.get(i);
		
		if ( current instanceof Throwable )
		{
		    logger.error("exception occured while updating schema", (Throwable)current);
		}
	    }
	}
    }
    
    /** create all databases tables */
    public void createAllTables()
    {
	this.ensureTableCreations();
    }

    /** return the session factory
     *  @return a SessionFactory
     */
    public SessionFactory getSessionFactory()
    {   return this.sessionFactory; }
    
    /** method that returns the configuration to use
     *  @return a Configuration
     */
    private Configuration createConfiguration()
    {   
        Configuration cfg = new Configuration();
	
	List<Class> classes = TypeInformationProvider.getInstance().getSubClassFor(SibType.class, true, true, true, (Class[])null);
	
	if ( classes != null )
	{
	    String suffix = ".hbm.xml";
	    
	    for(int i = 0; i < classes.size(); i++)
	    {
		Class current = classes.get(i);
		
		if ( current != null )
		{
		    String pluginId = TypeInformationProvider.getInstance().getPluginDeclaring(current);
		    
		    if ( pluginId == null )
		    {
			logger.warn("could not find the plugin declaring class " + current);
		    }
		    else
		    {
			StringBuffer buffer = new StringBuffer(current.getName().length() + suffix.length());
			
			buffer.append(current.getName());
			
			/** replace . by / */
			for(int j = 0; j < buffer.length(); j++)
			{
			    char currentChar = buffer.charAt(j);
			    
			    if ( currentChar == '.' )
			    {
				buffer.deleteCharAt(j);
				buffer.insert(j, '/');
			    }
			}
			buffer.append(suffix);
				
			URL url = ResourceLoader.getInstance().getPluginClassLoader(pluginId).getResource(buffer.toString());
			
			if ( url == null )
			{
			    logger.warn("resource " + buffer.toString() + " could not be found --> ignored");
			}
			else
			{
			    logger.info("adding hibernate mapping declaration from " + url);
			    cfg = cfg.addURL(url);
			}
		    }
		}
	    }
	}
        
        /** configure Configuration properties */
	
	Properties convertedProperties = this.convertProperties(this.getProperties());
	
        this.reportIgnoredProperties();
        
        Enumeration en = convertedProperties.keys();
        while(en.hasMoreElements())
        {   Object current = en.nextElement();
            
            logger.debug("consider database property '" + current + "' --> '" + convertedProperties.get(current) + "'");
        }
         
	cfg = cfg.setProperties(convertedProperties);
        
        return cfg;
    }
    
    /** convert properties specified for jdo
     *  @param initialProperties
     *  @return a Properties
     */
    protected Properties convertProperties(Properties initialProperties)
    {   Properties result = null;
        
        if ( initialProperties != null )
        {
            result = new Properties();
            Enumeration en = initialProperties.propertyNames();
            
            while(en.hasMoreElements())
            {   Object current = en.nextElement();
                
                if ( current instanceof String )
                {   
                    boolean processed = true;
		    
                    String value = initialProperties.getProperty((String)current);
                    if ( current.equals(PROPERTY_CONNECTION_DRIVER_NAME) )
		    {   result.put("hibernate.connection.driver_class", value); }
                    else if ( current.equals(PROPERTY_CONNECTION_URL) )
                    {   result.put("hibernate.connection.url", value); }
                    else if ( current.equals(PROPERTY_CONNECTION_USERNAME) )
                    {   result.put("hibernate.connection.username", value); }
                    else if ( current.equals(PROPERTY_CONNECTION_PASSWORD) )
                    {   result.put("hibernate.connection.password", value); }
                    else if ( current.equals(PROPERTY_DIALECT) )
                    {   result.put(PROPERTY_DIALECT, value); }
                    else if ( current.equals(PROPERTY_HBM2DDL_AUTO) )
                    {   result.put(PROPERTY_HBM2DDL_AUTO, value); }
                    else if ( current.equals(PROPERTY_FACTORY_CLASS) )
                    {   result.put(PROPERTY_FACTORY_CLASS, value); }
                    else if ( current.equals(PROPERTY_SESSION_CONTEXT_CLASS) )
                    {   result.put(PROPERTY_SESSION_CONTEXT_CLASS, value); }
                    else if ( current.equals(PROPERTY_ISOLATION) )
                    {   result.put(PROPERTY_ISOLATION, value); }
                    else if ( current.equals(PROPERTY_CONNECTION_RELEASE_MODE) )
                    {   result.put(PROPERTY_CONNECTION_RELEASE_MODE, value); }
                    else if ( current.equals(PROPERTY_BATCH_SIZE) )
                    {   result.put(PROPERTY_BATCH_SIZE, value); }
                    else if ( current.equals(PROPERTY_AUTO_COMMIT) )
                    {   result.put(PROPERTY_AUTO_COMMIT, value); }
                    else if ( current.equals(PROPERTY_AUTO_CLOSE_SESSION) )
                    {   result.put(PROPERTY_AUTO_CLOSE_SESSION, value); }
                    else if ( current.equals(PROPERTY_SHOW_SQL) )
                    {   result.put(PROPERTY_SHOW_SQL, value); }
                    else if ( current.equals(PROPERTY_FORMAT_SQL) )
                    {   result.put(PROPERTY_FORMAT_SQL, value); }
                    else if ( ((String)current).startsWith("hibernate.c3p0") )
                    {   result.put(current, value); }
                    else
                    {   processed = false; }
                    
                    if ( processed )
                    {   this.setPropertyAsProcessed((String)current); }
                }
            }
        }
        
        return result;
    }

    /**
     * shutdown manager
     *  close all resources opened by the manager including database connections
     */
    public void shutdown()
    {
	if ( this.sessionFactory != null )
	{
	    this.sessionFactory.close();
	    this.sessionFactory = null;
	}
    }
    
    /** ########################################################################
     *  ######################### data management ##############################
     *  ######################################################################## */
    
    /** create a new Transaction
     *	@return a new transaction
     */
    public HibernateTransactionHandler createTransaction() throws Exception
    {
	HibernateTransactionHandler tr = null;
	
	Session                   session     = this.sessionFactory.getCurrentSession();
//	Session                   session     = this.sessionFactory.openSession();
	org.hibernate.Transaction transaction = session.beginTransaction();
	
	tr = new HibernateTransactionHandler(session, transaction);
	
	return tr;
    }
    
    /** return true if the item exists
     *	@param transaction a Transaction
     *	@param object the object to check
     *	@return true if the object exists
     */
    public boolean exists(HibernateTransactionHandler transaction, Object object) throws Exception
    {
	/* not yet implemented */
	return false;
    }
    
    /**
     * save objects in database
     * 
     *	@param transaction a Transaction
     *  @param types an array of Object
     *  @exception SaveException if errors occured
     */
    public void store(HibernateTransactionHandler transaction, Object... types) throws Exception
    {
	if ( types != null )
	{
	    try
	    {
		for(int i = 0; i < types.length; i++)
		{
		    transaction.getSession().saveOrUpdate(types[i]);
		}
	    }
	    catch(Exception e)
	    {
		transaction.setRollbackOnly();
		throw e;
	    }
	}
    }
    
    /** load objects
     *  @param transaction a Transaction
     *	@param kind a Class that represents the kind of item to load
     *  @param constraints an array of BindingConstraint that allow to filter objects to load
     *	@return a list of Objects resulting from the loading process
     */
    public List load(HibernateTransactionHandler transaction, Class kind, DataBaseBindingConstraint... constraints) throws Exception
    {    
	List results = null;
	
	StringBuffer queryBuffer = new StringBuffer();
	queryBuffer.append("from " + kind.getSimpleName());
	
	try
	{
	    Query query = null;
	
	    /** complete query with constraint */
	    if ( constraints != null )
	    {
		boolean whereAdded = false;
		
		for(int i = 0; i < constraints.length; i++)
		{
		    DataBaseBindingConstraint ctr = constraints[i];
		    
		    if ( ctr != null && ctr.isActive() )
		    {
			if ( ! whereAdded )
			{
			    queryBuffer.append((" WHERE"));
			    
			    whereAdded = true;
			}
			
			/** find the hibernate name of the property linked to ctr.getPropertyName() */
			String hibernatePropertyName = ctr.getPropertyName();
			
			queryBuffer.append(" " + hibernatePropertyName + " " + ctr.getRelation().getDatabaseValue() + " ");
			
			Object constraint = ctr.getConstraintValue();
			if ( constraint instanceof String )
			{
			    queryBuffer.append("'" + ((String)constraint) + "'");
			}
			else
			{
			    queryBuffer.append(constraint.toString());
			}
		    }
		}
	    }
	    
	    query = transaction.getSession().createQuery(queryBuffer.toString());
	    results = query.list();
	}
	catch(HibernateException e)
	{
	    transaction.setRollbackOnly();
	    throw e;
	}
	
	return results;
    }

    /**
     * refresh the status of an object stored in database
     * 
     *  @param transaction a Transaction
     *  @param type an Object
     */
    public void refresh(HibernateTransactionHandler transaction, Object type) throws Exception
    {
	
    }

    /**
     * delete objects in database
     * 
     *  @param transaction a Transaction
     *  @param types an array of Object
     */
    public void delete(HibernateTransactionHandler transaction, Object... types) throws Exception
    {
	if ( types != null )
	{
	    for(int i = 0; i < types.length; i++)
	    {
		Object current = types[i];
		
		if ( current != null )
		{
		    try
		    {
			transaction.getSession().delete(current);
		    }
		    catch(HibernateException e)
		    {
			transaction.setRollbackOnly();
			throw e;
		    }
		}
	    }
	}
    }
    
    /** return a Collection of items that convey to given critera
     *  @param transaction a Transaction
     *  @param typeClass the kind of item to load
     *  @param subclasses whether to include instances of subclasses
     */
    public int getItemsCount(HibernateTransactionHandler transaction, Class typeClass, boolean subClasses) throws Exception
    {
	return 0;
    }
    
}
