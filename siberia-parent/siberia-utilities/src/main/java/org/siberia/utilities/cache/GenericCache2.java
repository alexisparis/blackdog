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
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.siberia.SiberiaUtilitiesPlugin;
import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;
import org.apache.log4j.Logger;



/**
 *
 * Generic class for cache system implementation
 *
 * @author alexis
 */
public abstract class GenericCache2<T, U> implements CacheEventListener
{
    /** logger */
    private static final Logger logger = Logger.getLogger(GenericCache2.class);
    
    /** cache manager */
    private static CacheManager manager = null;
    
    /** inner cache */
    private Cache cache = null;
    
    /** maximum size of the map */
    private int maxSize = 16;
    
    /** create a new pool */
    public GenericCache2()
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
	    
	    this.cache.getCacheEventNotificationService().registerListener(this);
        }
        catch(Exception e)
        {   e.printStackTrace(); }
    }
    
    /** return the CacheManager
     *  @return a CacheManager
     */
    private static CacheManager getManager()
    {   if ( manager == null )
        {   URL url = null;
	    try
            {
                ClassLoader loader = ResourceLoader.getInstance().getPluginClassLoader(SiberiaUtilitiesPlugin.PLUGIN_ID);

                url = ResourceLoader.getInstance().getResource("ehcache.xml", loader);
            }
            catch(Exception e)
            {   
		logger.error(e.getMessage());
	    }
	    
	    if ( url != null )
	    {
                manager = CacheManager.create(url);
	    }
	    else
	    {
		manager = CacheManager.create();
	    }
        }
        
        return manager;
    }
    
    /** clear all cached item */
    public void clear()
    {
	if ( this.cache != null )
	{
	    try
	    {
		this.cache.removeAll();
	    }
	    catch (IllegalStateException ex)
	    {
		ex.printStackTrace();
	    }
	    catch (CacheException ex)
	    {
		ex.printStackTrace();
	    }
	}
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
    {   this.cache.put(new Element(t, u)); }//(Serializable)t, (Serializable)u)); }
    
    /** return the object related to the given object
     *  @param key an instance of T
     *  @return an instance of T
     */
    public U get(T key)
    {           
        Element elt = this.cache.get(key);// (Serializable) key );
        U value = (U)(elt == null ? null : elt.getValue());
        
        if ( elt == null )
        {   
            value = this.create(key);
            
            if ( value != null )
                this.store(key, (U)value);
        }
//        else
//            System.out.println("l'objet est en cache");
        
        return (U)value;
    }
    
    /** method that create an elemet according to the key
     *  @param key the corresponding key
     *  @param parameters others parameters
     *  @return the object that has benn created
     */
    public abstract U create(T key, Object... parameters);
    
    /* #########################################################################
     * ################## CacheEventListener implementation ####################
     * ######################################################################### */

    /**
     * Give the replicator a chance to cleanup and free resources when no longer needed
     */
    public void dispose()
    {
	logger.debug("calling dispose");
	// to overwrite for special needs
    }

    /**
     * Called immediately after an element has been put into the cache and the element already
     * existed in the cache. This is thus an update.
     * <p/>
     * The {@link net.sf.ehcache.Cache#put(net.sf.ehcache.Element)} method
     * will block until this method returns.
     * <p/>
     * Implementers may wish to have access to the Element's fields, including value, so the
     * element is provided. Implementers should be careful not to modify the element. The
     * effect of any modifications is undefined.
     * 
     * @param cache   the cache emitting the notification
     * @param element the element which was just put into the cache.
     */
    public void notifyElementUpdated(final Ehcache cache, final Element element) throws CacheException
    {
	logger.debug("calling notifyElementUpdated(key=" + element.getObjectKey() + ", value=" + element.getObjectValue() + ")");
	// to overwrite for special needs
    }

    /**
     * Called immediately after an attempt to remove an element. The remove method will block until
     * this method returns.
     * <p/>
     * This notification is received regardless of whether the cache had an element matching
     * the removal key or not. If an element was removed, the element is passed to this method,
     * otherwise a synthetic element, with only the key set is passed in.
     * <p/>
     * This notification is not called for the following special cases:
     * <ol>
     * <li>removeAll was called. See {@link #notifyRemoveAll(net.sf.ehcache.Ehcache)}
     * <li>An element was evicted from the cache.
     * See {@link #notifyElementEvicted(net.sf.ehcache.Ehcache, net.sf.ehcache.Element)}
     * </ol>
     * 
     * @param cache   the cache emitting the notification
     * @param element the element just deleted, or a synthetic element with just the key set if
     *                no element was removed.
     */
    public void notifyElementRemoved(final Ehcache cache, final Element element) throws CacheException
    {
	logger.debug("calling notifyElementRemoved(key=" + element.getObjectKey() + ", value=" + element.getObjectValue() + ")");
	// to overwrite for special needs
    }

    /**
     * Called immediately after an element has been put into the cache. The
     * {@link net.sf.ehcache.Cache#put(net.sf.ehcache.Element)} method
     * will block until this method returns.
     * <p/>
     * Implementers may wish to have access to the Element's fields, including value, so the
     * element is provided. Implementers should be careful not to modify the element. The
     * effect of any modifications is undefined.
     * 
     * @param cache   the cache emitting the notification
     * @param element the element which was just put into the cache.
     */
    public void notifyElementPut(final Ehcache cache, final Element element) throws CacheException
    {
	logger.debug("calling notifyElementPut(key=" + element.getObjectKey() + ", value=" + element.getObjectValue() + ")");
	// to overwrite for special needs
    }

    /**
     * Called immediately after an element is <i>found</i> to be expired. The
     * {@link net.sf.ehcache.Cache#remove(Object)} method will block until this method returns.
     * <p/>
     * As the {@link Element} has been expired, only what was the key of the element is known.
     * <p/>
     * Elements are checked for expiry in ehcache at the following times:
     * <ul>
     * <li>When a get request is made
     * <li>When an element is spooled to the diskStore in accordance with a MemoryStore
     * eviction policy
     * <li>In the DiskStore when the expiry thread runs, which by default is
     * {@link net.sf.ehcache.Cache#DEFAULT_EXPIRY_THREAD_INTERVAL_SECONDS}
     * </ul>
     * If an element is found to be expired, it is deleted and this method is notified.
     * 
     * @param cache   the cache emitting the notification
     * @param element the element that has just expired
     *                <p/>
     *                Deadlock Warning: expiry will often come from the <code>DiskStore</code>
     *                expiry thread. It holds a lock to the DiskStorea the time the
     *                notification is sent. If the implementation of this method calls into a
     *                synchronized <code>Cache</code> method and that subsequently calls into
     *                DiskStore a deadlock will result. Accordingly implementers of this method
     *                should not call back into Cache.
     */
    public void notifyElementExpired(final Ehcache cache, final Element element)
    {
	logger.debug("calling notifyElementExpired(key=" + element.getObjectKey() + ", value=" + element.getObjectValue() + ")");
	// to overwrite for special needs
    }

    /**
     * Called immediately after an element is evicted from the cache. Evicted in this sense
     * means evicted from one store and not moved to another, so that it exists nowhere in the
     * local cache.
     * <p/>
     * In a sense the Element has been <i>removed</i> from the cache, but it is different,
     * thus the separate notification.
     * 
     * @param cache   the cache emitting the notification
     * @param element the element that has just been evicted
     */
    public void notifyElementEvicted(final Ehcache cache, final Element element)
    {
	logger.debug("calling notifyElementEvicted(key=" + element.getObjectKey() + ", value=" + element.getObjectValue() + ")");
	// to overwrite for special needs
    }

    /**
     * Called during {@link net.sf.ehcache.Ehcache#removeAll()} to indicate that the all
     * elements have been removed from the cache in a bulk operation. The usual
     * {@link #notifyElementRemoved(net.sf.ehcache.Ehcache, net.sf.ehcache.Element)}
     * is not called.
     * <p/>
     * This notification exists because clearing a cache is a special case. It is often
     * not practical to serially process notifications where potentially millions of elements
     * have been bulk deleted.
     * 
     * @param cache the cache emitting the notification
     */
    public void notifyRemoveAll(final Ehcache cache)
    {
	logger.debug("calling notifyRemoveAll");
	// to overwrite for special needs
    }

    /**
     * Creates a clone of this listener. This method will only be called by ehcache before a
     * cache is initialized.
     * <p/>
     * This may not be possible for listeners after they have been initialized. Implementations
     * should throw CloneNotSupportedException if they do not support clone.
     *
     * @return a clone
     * @throws CloneNotSupportedException if the listener could not be cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
	throw new CloneNotSupportedException();
    }
    
}
