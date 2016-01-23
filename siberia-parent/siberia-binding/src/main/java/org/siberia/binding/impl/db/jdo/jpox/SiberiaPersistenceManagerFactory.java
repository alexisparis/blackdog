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
//package org.siberia.binding.impl.db.jdo.jpox;
//
//import java.lang.ref.WeakReference;
//import java.util.Iterator;
//import java.util.Map;
//import javax.jdo.PersistenceManagerFactory;
//import org.jpox.ClassLoaderResolver;
//import org.jpox.OMFContext;
//import org.jpox.PersistenceConfiguration;
//import org.jpox.PersistenceManagerFactoryImpl;
//
///**
// *
// * @author alexis
// */
//public class SiberiaPersistenceManagerFactory extends PersistenceManagerFactoryImpl
//{
//    
//    /** Creates a new instance of SiberiaPersistenceManagerFactory */
//    public SiberiaPersistenceManagerFactory(Map props)
//    {   
//	super();
//	
//	
////	at org.jpox.OMFContext.<init>(OMFContext.java:113)
////        at org.jpox.AbstractObjectManagerFactory.<init>(AbstractObjectManagerFactory.java:65)
////        at org.jpox.AbstractPersistenceManagerFactory.<init>(AbstractPersistenceManagerFactory.java:77)
////        at org.jpox.PersistenceManagerFactoryImpl.<init>(PersistenceManagerFactoryImpl.java:127)
////        at org.siberia.binding.impl.db.jpox.SiberiaPersistenceManagerFactory.<init>(SiberiaPersistenceManagerFactory.java:22)
////        at org.siberia.binding.impl.db.jpox.SiberiaPersistenceManagerFactory.getPersistenceManagerFactory(SiberiaPersistenceManagerFactory.java:61)
//	
//	
////Caused by org.jpox.exceptions.JPOXUserException: Field "org.siberia.type.SibCollection.collection" is declared as a reference type (interface/Object) 
////but no implementation classes of "org.blackdog.type.SongItem" have been found !
////
////org.jpox.metadata.MetaDataUtils.getImplementationNamesForReferenceField(MetaDataUtils.java:329)
////org.jpox.metadata.AbstractPropertyMetaData.setRelation(AbstractPropertyMetaData.java:2052)
////org.jpox.metadata.AbstractPropertyMetaData.getRelationType(AbstractPropertyMetaData.java:2222)
////org.jpox.store.rdbms.table.ClassTable.addFieldMetaData(ClassTable.java:513)
////org.jpox.store.rdbms.table.ClassTable.manageClass(ClassTable.java:391)
//	
//	Iterator it = props.keySet().iterator();
//	while(it.hasNext())
//	{
//	    Object current = it.next();
//	    System.out.println("\tproperty : " + current + " kind : " + (current == null ? null : current.getClass()));
//	}
//	
//        this.setOptions(props);
//        
//        /** modify PMF */
//        this.omfContext = new SiberiaPMFContext(this);
//        
//        this.freezeConfiguration();
//    }
//    
//    public synchronized static PersistenceManagerFactory getPersistenceManagerFactory(Map props)
//    {   
//	
//	
//	System.out.println("affichage du contenu de la map : ");
//	Iterator key = props.keySet().iterator();
//	while(key.hasNext())
//	{
//	    Object current = key.next();
//	    System.out.println("\t" + current + " --> " + props.get(current));
//	}
//	
////	props.put(PersistenceManagerFactoryImpl.)
//	
////	PersistenceConfiguration pers = this;
////	pers.setPluginRegistryClassName();
//	
//        PersistenceManagerFactory factory = new SiberiaPersistenceManagerFactory(props);
//        
//        return factory;
//    }
//
//    /**
//     * Mutator for the {@link PluginRegistry} class name. If one is provided, it will be used as registry for plug-ins
//     * @return the fully qualified class {@link PluginRegistry}
//     */
//    @Override
//    public String getPluginRegistryClassName()
//    {   return ExclusiveJPoxPluginRegistry.class.getName(); }
//    
//    public static class SiberiaPMFContext extends OMFContext
//    {
//        /** resolver */
//        private WeakReference<ClassLoaderResolver> resolverRef = null;
//        
//        public SiberiaPMFContext(PersistenceConfiguration pmfConfiguration)
//        {   super(pmfConfiguration); }
//        
//        /**
//         * Accessor for a ClassLoaderResolver to use in resolving classes.
//         * @param primaryLoader Loader to use as the primary loader.
//         * @return The ClassLoader resolver
//         */
//        @Override
//        public synchronized ClassLoaderResolver getClassLoaderResolver(ClassLoader primaryLoader)
//        {   
//            ClassLoaderResolver resolver = null;
//            
//            if ( this.resolverRef != null )
//            {   resolver = this.resolverRef.get(); }
//            
//            if ( resolver == null )
//            {   resolver = new SiberiaClassLoaderResolver();
//                this.resolverRef = new WeakReference<ClassLoaderResolver>(resolver);
//            }
//            
//            return resolver;
//        }
//    }
//    
//}
