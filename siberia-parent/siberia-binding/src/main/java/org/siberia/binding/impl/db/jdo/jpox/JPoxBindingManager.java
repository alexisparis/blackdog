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
//import java.util.Enumeration;
//import java.util.Properties;
//import org.apache.log4j.Logger;
//import org.siberia.binding.exception.DatabaseConfigurationException;
//import org.siberia.binding.impl.db.jdo.JDOBindingManager;
//
///**
// *
// * DataBaseBindingManager based on jpox framework
// *
// * @author alexis
// */
//public abstract class JPoxBindingManager extends JDOBindingManager
//{
//    /** property names */
//    protected static final String PROPERTY_AUTO_CREATE_SCHEMA   = "jpox.autoCreateSchema";
//    
//    protected static final String PROPERTY_AUTO_CREATE_COLUMNS  = "jpox.autoCreateColumns";
//    
//    protected static final String PROPERTY_AUTO_START_MECHANISM = "jpox.autoStartMechanism";
//    
//    /** logger */
//    private static   final Logger   logger                    = Logger.getLogger(JPoxBindingManager.class);
//    
//    /** Creates a new instance of JPoxBindingManager */
//    public JPoxBindingManager() throws DatabaseConfigurationException
//    {   super(); }
//    
//    /** convert properties specified for jdo
//     *  @param initialProperties
//     *  @return a Properties
//     */
//    @Override
//    protected Properties convertProperties(Properties initialProperties)
//    {   Properties result = super.convertProperties(initialProperties);
//	
//	
//	if ( initialProperties != null )
//	{
//	    if ( result == null )
//	    {   result = new Properties(); }
//	    
//	    Enumeration en = initialProperties.propertyNames();
//	    
//	    while(en.hasMoreElements())
//	    {   Object current = en.nextElement();
//		
//		if ( current instanceof String )
//		{
//		    boolean processed = true;
//		    
//		    String value = initialProperties.getProperty((String)current);
//		    if ( current.equals(PROPERTY_AUTO_CREATE_SCHEMA) )
//		    {   result.put("org.jpox.autoCreateSchema", value); }
//		    if ( current.equals(PROPERTY_AUTO_START_MECHANISM) )
//		    {   result.put("org.jpox.autoStartMechanism", value); }
//		    if ( current.equals(PROPERTY_AUTO_CREATE_COLUMNS) )
//		    {   result.put("org.jpox.autoCreateColumns", value); }
//		    else
//		    {   processed = false; }
//		    
////		    result.put("org.jpox.autoCreateColumns", true);
//		    
//		    
////                    org.jpox.identifier.defaultSchemaName
//		    
//		    if ( processed )
//		    {   this.setPropertyAsProcessed((String)current); }
//		}
//	    }
//	}
//	
//	return result;
//    }
//}
