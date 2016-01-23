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
//package org.siberia.binding.impl.db.attic;
//
//import java.util.Properties;
//import org.siberia.binding.exception.DatabaseConfigurationException;
//import org.siberia.binding.exception.LoadException;
//import org.siberia.binding.exception.SaveException;
//import org.siberia.binding.DataBaseBindingManager;
//import org.siberia.type.SibType;
//
///**
// *
// * DataBaseBindingManager based on ibatis framework
// *
// * @author alexis
// */
//public class IbatisBindingManager implements DataBaseBindingManager
//{
////    /** extension point id of hibernate xml declarations */
////    private static final String         IBATIS_DECL_EXTENSION_ID    = "ibatis-declaration";
////    
////    /** logger */
////    public static        Logger         logger                      = Logger.getLogger(IbatisBindingManager.class);
////    
////    private              SqlMapClient   clientMap                   = null;
////
////    static {
////        
////    }
//    
//    /** Creates a new instance of HibernateBindingManager */
//    public IbatisBindingManager()
//    {
////        this.clientMap = new SqlMapClientImpl(null);
//    }
//    
//    /** set the database properties
//     *  @param properties a Properties
//     */
//    public void setProperties(Properties properties) throws DatabaseConfigurationException
//    {   }
//    
//    /**
//     * ask to load a stored object
//     * 
//     * @return type an Object
//     * @exception LoadException if errors occured
//     */
//    public Object load() throws LoadException
//    {   //this.getSessionFactory().openSession().
//        return null;
//    }
//
//    /**
//     * save an instance of SibType
//     * 
//     * @param type an Object
//     * @exception SaveException if errors occured
//     */
//    public void store(Object type) throws SaveException
//    {
//        // do nothing
//    }
//    
//    /** method that returns the configuration to use
//     *  @return a Configuration
//     */
////    private Configuration createConfiguration()
////    {   
////        Configuration cfg = new Configuration();
////        
////        Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(HIBERNATE_DECL_EXTENSION_ID);
////
////        if ( extensions != null )
////        {   Iterator<SiberExtension> it = extensions.iterator();
////            while(it.hasNext())
////            {   SiberExtension extension = it.next();
////
////                String path = extension.getStringParameterValue("path");
////                String c    = extension.getStringParameterValue("class");
////
////                if ( path != null && c != null )
////                {   
////                    try
////                    {   
////                        cfg = cfg.addURL(ResourceLoader.getInstance().getRcResource(path));
////                    }
////                    catch(ResourceException e)
////                    {   logger.error("unable to load hibernate declaration at '" + path + "'");
////                        continue;
////                    }
////                    logger.info("adding mapping '" + path + "' for class='" + c + "'");
////                }
////                else
////                {   if ( path == null )
////                    {   logger.error("invalid declaration of hibernate xml path (path not specified) --> ignored"); }
////                    if ( c == null )
////                    {   logger.error("invalid declaration of hibernate xml path for class '" + c + "' --> ignored"); }
////                }
////            }
////        }
////        
////        /** configure Configuration properties */
////         
////        cfg = cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
////        cfg = cfg.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
////        cfg = cfg.setProperty("hibernate.connection.username", "sa");
////        cfg = cfg.setProperty("hibernate.connection.password", "");
////        cfg = cfg.setProperty("hibernate.show_sql", "true");
////        cfg = cfg.setProperty("hibernate.format_sql", "true");
////        cfg = cfg.setProperty("hibernate.hbm2ddl.auto", "create");
////        cfg = cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb:/home/alexis/.Blackdog/database/");
//////        cfg = cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb://localhost/mydatabase");
//////        cfg = cfg.setProperty("", "");
//////        cfg = cfg.setProperty("", "");
//////        cfg = cfg.setProperty("", "");
////        
////        return cfg;
////    }
//    
//}
