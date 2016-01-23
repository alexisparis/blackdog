/* 
 * Siberia utilities : siberia plugin providing severall utilities classes
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
package org.siberia.utilities.cache;

import java.io.Serializable;
import java.net.URL;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.siberia.SiberiaUtilitiesPlugin;
import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;



/**
 *
 * Generic class for cache system implementation
 *
 * @author alexis
 */
public abstract class GenericCache<T, U>
{
    /** cache manager */
    private static CacheManager manager = null;
    
    /** inner cache */
    private Cache cache = null;
    
    /** maximum size of the map */
    private int maxSize = 16;
    
    /** create a new pool */
    public GenericCache()
    {   //Use the cache manager to create the default cache
        
        try
        {   
            String[] names = getManager().getCacheNames();
            String name = null;
            while(true)
            {   name = String.valueOf(Math.random() * 1000);
                
                for(int i = 0; i < names.length; i++)
                {   if ( names[i].equals(name) )
                        continue;
                }
                
                break;
            }
            
            this.cache = new Cache(name, maxSize, true, false, 220, 220);
//                                name, maxSize,
//                                MemoryStoreEvictionPolicy.LFU, true, false,
//                                60, 30, false, 0);
            
            getManager().addCache(this.cache);
        }
        catch(Exception e)
        {   e.printStackTrace(); }
    }
    
    /** return the CacheManager
     *  @return a CacheManager
     */
    private static CacheManager getManager()
    {   if ( manager == null )
        {   try
            {
                ClassLoader loader = ResourceLoader.getInstance().getPluginClassLoader(SiberiaUtilitiesPlugin.PLUGIN_ID);

                URL url = ResourceLoader.getInstance().getResource("ehcache.xml", loader);
                manager = CacheManager.create(url);
            }
            catch(Exception e)
            {   e.printStackTrace(); }
        }
        
        return manager;
    }
    
    /** initialize the maximum size of the cache
     *  @param size the new maximum size of the cache
     */
    public void setMaximumSize(int size)
    {   if ( size > 0 )
            this.maxSize = size;
    }
    
    /** add an element
     *  @param t an instance of T
     *  @param u an instance of U
     */
    public void store(T t, U u)
    {   this.cache.put(new Element((Serializable)t, (Serializable)u)); }
    
    /** return the object related to the given object
     *  @param key an instance of T
     *  @return an instance of T
     */
    public U get(T key)
    {           
        Element elt = this.cache.get( (Serializable) key );
        U value = (U)(elt == null ? null : elt.getValue());
        
        if ( elt == null )
        {   
            value = this.create(key);
            System.out.println("l'objet doit etre cree " + key.getClass());
            
            if ( value != null )
                this.store(key, (U)value);
        }
        else
            System.out.println("l'objet est en cache");
        
        return (U)value;
    }
    
    /** method that create an elemet according to the key
     *  @param key the corresponding key
     *  @param parameters others parameters
     *  @return the object that has benn created
     */
    public abstract U create(T key, Object... parameters);
    
    /** return a unique id representing the given object
     *  @param key an instance of T
     *  @return an id
     */
//    public abstract String getId(T key);
    
}
