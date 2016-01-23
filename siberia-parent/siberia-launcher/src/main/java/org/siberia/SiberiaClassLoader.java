/* =============================================================================
 * Siberia launcher
 * =============================================================================
 *
 * Project Lead:  Alexis Paris
 *
 * (C) Copyright 2008, by Alexis Paris.
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
package org.siberia;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Iterator;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.PluginDescriptor;

/**
 *
 * ClassLoader that allow to load classes declared in plugins
 * 
 * @author alexis
 */
public class SiberiaClassLoader extends URLClassLoader
{
    /** plugin manager */
    private PluginManager manager = null;
    
    /** Creates a new instance of SiberiaClassLoader */
    public SiberiaClassLoader(PluginManager manager, ClassLoader parent)
    {   super(new URL[]{}, parent);
        
        if ( manager == null )
        {   throw new IllegalArgumentException("plugin manager could not be null"); }
        
        this.manager = manager;
    }

    /**
     * Loads the class with the specified <a href="#name">binary name</a>.  The
     * default implementation of this method searches for classes in the
     * following order:
     * 
     * <p><ol>
     * 
     *   <li><p> Invoke {@link #findLoadedClass(String)} to check if the class
     *   has already been loaded.  </p></li>
     * 
     *   <li><p> Invoke the {@link #loadClass(String) <tt>loadClass</tt>} method
     *   on the parent class loader.  If the parent is <tt>null</tt> the class
     *   loader built-in to the virtual machine is used, instead.  </p></li>
     * 
     *   <li><p> Invoke the {@link #findClass(String)} method to find the
     *   class.  </p></li>
     * 
     * </ol>
     * 
     * <p> If the class was found using the above steps, and the
     * <tt>resolve</tt> flag is true, this method will then invoke the {@link
     * #resolveClass(Class)} method on the resulting <tt>Class</tt> object.
     * 
     * <p> Subclasses of <tt>ClassLoader</tt> are encouraged to override {@link
     * #findClass(String)}, rather than this method.  </p>
     * 
     * @param name
     *         The <a href="#name">binary name</a> of the class
     * @param resolve
     *         If <tt>true</tt> then resolve the class
     * @return The resulting <tt>Class</tt> object
     * @throws ClassNotFoundException
     *          If the class could not be found
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
    {
        Class retValue = null;
        
        ClassNotFoundException exception = null;
        
        try
        {   retValue = super.loadClass(name, resolve); }
        catch (ClassNotFoundException ex)
        {   exception = ex; }
        
        if ( retValue == null )
        {   /* try to find it in the PluginClassLoader */
            Collection collec = this.manager.getRegistry().getPluginDescriptors();
            if ( collec != null )
            {   Iterator it = collec.iterator();
                
                while(it.hasNext())
                {   Object current = it.next();
                    
                    if ( current instanceof PluginDescriptor )
                    {   PluginDescriptor descriptor = (PluginDescriptor)current;
                        
                        ClassLoader pluginCl = this.manager.getPluginClassLoader(descriptor);
                        if ( pluginCl != null )
                        {
                            try
                            {   retValue = pluginCl.loadClass(name);
                                break;
                            }
                            catch(ClassNotFoundException e)
                            {   if ( exception == null )
                                {   exception = e; }
                            }
                        }
                    }
                }
            }
        }
        
        if ( exception != null && retValue == null )
        {   throw exception; }
        
        return retValue;
    }
    
}
