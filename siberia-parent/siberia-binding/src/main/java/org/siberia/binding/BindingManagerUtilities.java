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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.binding.exception.BindingManagerDisabledException;
import org.siberia.binding.exception.IllegalBindingManagerException;
import org.siberia.binding.exception.NoBindingManagerFoundException;
import org.siberia.binding.impl.AbstractFileBindingManager;
import org.siberia.env.PluginResources;
import org.siberia.env.SiberExtension;
import org.siberia.exception.ResourceException;

/**
 *
 * Provide some information about BindingManagers
 *
 * @author alexis
 */
public class BindingManagerUtilities
{
    /** logger */
    private static       Logger                           logger                           = Logger.getLogger(BindingManagerUtilities.class);
    
    /** code of the XStream BindingManager */
    private static final String                           XSTREAM_CODE                     = "xstream";
    
    /** code of the JPox BindingManager */
    private static final String                           JPOX_CODE                        = "jpox";
    
    /** code of the hibernate BindingManager */
    private static final String                           HIBERNATE_CODE                   = "hibernate";
    
    /** code of the default BindingManager for file */
    public  static final String                           DEFAULT_FILE_BINDING_MANAGER     = XSTREAM_CODE;
    
    /** code of the default BindingManager for database */
    public  static final String                           DEFAULT_DATABASE_BINDING_MANAGER = HIBERNATE_CODE;
    
    /** map containing as key a kind of BindingManager and as value a List containing the code of every BindingManager of that kind */
    private static       WeakHashMap<Class, List<String>> managers                         = new WeakHashMap<Class, List<String>>();
    
    /** Creates a new instance of BindingManagerUtilities */
    private BindingManagerUtilities()
    {   }
    
    /** return the code of the default file BindingManager
     *  @return a String
     */
    public static String getDefaultFileBindingManagerCode()
    {   return DEFAULT_FILE_BINDING_MANAGER; }
    
    /** return the code of the default database BindingManager
     *  @return a String
     */
    public static String getDefaultDatabaseBindingManagerCode()
    {   return DEFAULT_DATABASE_BINDING_MANAGER; }
    
    /** returns the BindingManager associated with the given code
     *  @param code the code of a registered BindingManager
     *  @return a BindingManager
     *
     *  @exception BindingManagerDisabledException if the BindingManager is declared disabled
     *  @exception NoBindingManagerFoundException if no BindingManager is registered under the given code
     */
    private static BindingManager getBindingManager(String code) throws BindingManagerDisabledException, NoBindingManagerFoundException
    {   BindingManager manager = null;
        
        logger.debug("calling getBindingManager with code='" + code + "'");
        
        if ( code != null )
        {   Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(BindingManager.BINDING_MANAGER_EXTENSION_ID);
            
            if ( extensions != null )
            {   Iterator<SiberExtension> it = extensions .iterator();
                
                logger.debug("loop over binding manager declarations");
                
                while(it.hasNext())
                {   SiberExtension current = it.next();
                    
                    String currentCode = current.getStringParameterValue(BindingManager.ATTR_CODE);
                            
                    logger.debug("current binding manager declaration '" + currentCode + "'");
                    
                    if ( code.equals(currentCode) )
                    {   
                        logger.debug("declaration for binding manager '" + code + "' found");
                        
                        /* check if the BindingManager is enabled */
                        Boolean enabled = current.getBooleanParameterValue(BindingManager.ATTR_ENABLED);
                        
                        logger.debug("getting enabled='" + enabled + "' for bindingManager '" + code + "'");
                        
                        if ( enabled == null || ! enabled.booleanValue() )
                        {   logger.debug("binding manager '" + code + "' is disabled");
                            throw new BindingManagerDisabledException(code);
                        }
                        
                        /** try to load the class */
                        Class c = getBindingManagerClass(current);
                        
                        /** instantiate class */
                        if ( c != null )
                        {   
                            try
                            {   manager = (BindingManager)c.newInstance();
                                logger.debug("instantiation of binding manager '" + c + "' --> ok");
                            }
                            catch(Exception e)
                            {   logger.error("unable to instantiate binding manager class '" + c + "'", e); }
                            
                            if ( manager != null )
                            {   break; }
                        }
                    }
                }
            }
        }
        
        if ( manager == null )
        {   throw new NoBindingManagerFoundException(code); }
        
        return manager;
    }
    
    /** returns the BindingManager associated with the given code
     *  @param code the code of a registered BindingManager
     *  @return a BindingManager
     *
     *  @exception BindingManagerDisabledException if the BindingManager is declared disabled
     *  @exception NoBindingManagerFoundException if no BindingManager is registered under the given code
     *  @exception IllegalBindingManagerException if the BindingManager found was not expected
     */
    public static BindingManager getBindingManager(String code, Class expectedType) throws IllegalBindingManagerException,
                                                                                            NoBindingManagerFoundException,
                                                                                            BindingManagerDisabledException
    {   BindingManager manager = getBindingManager(code);
        
        if ( ! (expectedType.isAssignableFrom(manager.getClass())) )
        {   throw new IllegalBindingManagerException(code, manager.getClass(), expectedType); }
        
        return manager;
    }
    
    /** ########################################################################
     *  ######################### GLOBAL INFORMATION ###########################
     *  ######################################################################## */
    
    /** return the code of FileBindingManager
     *  @return a List containing Strings
     */
    public static List<String> getFileBindingManagers()
    {   return getBindingManagersImplementing(FileBindingManager.class); }
    
    /** return the code of DataBaseBindingManager
     *  @return a List containing Strings
     */
    public static List<String> getDataBaseBindingManagers()
    {   return getBindingManagersImplementing(DataBaseBindingManager.class); }
    
    /** return the List containing code for a given kind of BindingManager
     *  @param bmKind a Class Implementing BindingManager
     *  @return a List<String> that contains the code of every BindingManager implementing bmKind
     */
    public static List<String> getBindingManagersImplementing(Class bmKind)
    {   List<String> result = managers.get(bmKind);
        
        if ( result == null )
        {
            Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(BindingManager.BINDING_MANAGER_EXTENSION_ID);

            if ( extensions != null )
            {   Iterator<SiberExtension> it = extensions .iterator();

                logger.debug("loop over binding manager declarations");

                while(it.hasNext())
                {   SiberExtension current = it.next();

                    String currentCode = current.getStringParameterValue(BindingManager.ATTR_CODE);

                    logger.debug("declaration for binding manager '" + currentCode + "' found when collecting binding manager declaration");

                    /* check if the BindingManager is enabled */
                    Boolean enabled = current.getBooleanParameterValue(BindingManager.ATTR_ENABLED);

                    if ( enabled == null || enabled.booleanValue() )
                    {   /** try to load the class */
                        Class c = getBindingManagerClass(current);

                        if ( bmKind.isAssignableFrom(c) )
                        {   
                            if ( result == null )
                            {   result = new ArrayList<String>(); }
                            
                            result.add(currentCode);
                            logger.debug("adding '" + currentCode + "' as " + bmKind.getName());
                        }
                    }
                    else
                    {   logger.debug("ingoring binding manager '" + currentCode + "' because it is disabled"); }
                }
            }
            
            if ( result == null )
            {   result = Collections.emptyList(); }
            
            managers.put(bmKind, result);
        }
        
        return result;
    }
    
    /** return the BindingManager class contains by the given extension
     *  @param extension a SiberExtension
     *  @return a Class
     */
    private static Class getBindingManagerClass(SiberExtension extension)
    {   Class c = null;
        
        if ( extension != null )
        {   
            String bmCode = extension.getStringParameterValue(BindingManager.ATTR_CODE);
            
            String className = extension.getStringParameterValue(BindingManager.ATTR_CLASS);
            logger.debug("getting className='" + className + "' for bindingManager '" + bmCode + "'");

            /** try to load the class */
            try
            {   c = ResourceLoader.getInstance().getClass(className);
                logger.debug("binding manager class '" + c + "' found");
            }
            catch(ResourceException e)
            {   logger.error("unable to load class '" + className + "'", e); }
        }
        
        return c;
    }
    
}
