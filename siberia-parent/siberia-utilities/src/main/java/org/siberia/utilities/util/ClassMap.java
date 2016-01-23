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
package org.siberia.utilities.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Map that contains classes as key.<br>
 *
 *  If we try to get the value for an Object o : <br>
 *      <ul><li>o is a Class : then, we will try to find if o appears in the map,<br>
 *              or if one of its interfaces appears, then look at the super class, etc...</li>
 *          <li>o is not a Class, then we will work with the Class of the object</li></ul>
 *
 * @author alexis
 */
public class ClassMap<T extends Object> extends HashMap<Class, T>
{
    
    /** Creates a new instance of ClassMap */
    public ClassMap()
    {   super(); }
    
    /** Creates a new instance of ClassMap
     *  @param initialCapacity the initial capacity
     */
    public ClassMap(int initialCapacity)
    {   super(initialCapacity); }
    
    public Object search0(Object object)
    {   return this.search(object); }
    
    public T search(Object object)
    {   T result = null;
        
        if ( object != null )
        {   if ( object instanceof Class )
            {
                Class c = (Class)object;

                while( c != null && ! c.equals(Object.class) )
                {   result = super.get(c);
                    if ( result != null )
                        break;

                    for(int i = 0; i < c.getInterfaces().length; i++)
                    {   Class interf = c.getInterfaces()[i];
                        result = super.get(interf);
                        if ( result != null )
                            break;
                    }

                    if ( result != null )
                        break;

                    c = c.getSuperclass();
                }
            }
            else
            {   result = this.search(object.getClass()); }
        }
        
        return result;
    }
    
    /** return a List of <T> for a given class or object.
     *  @param object a Class or an Object
     *  @return a List containing T
     */
    public List<T> getRecursively(Object object)
    {   return this.getRecursively(object, false); }
    
    /** return a List of <T> for a given class or object.
     *  @param object a Class or an Object
     *  @param unique true if the element of the returned list are to appear once
     *  @return a List containing T
     */
    public List<T> getRecursively(Object object, boolean unique)
    {   List<T> list = null;
        
        if ( object != null )
        {   if ( object instanceof Class )
            {   Class c = (Class)object;
                T result = null;
                
                while( c != null && ! c.equals(Object.class) )
                {   result = super.get(c);
                    if ( result != null )
                    {   /** add to the set */
                        if ( list == null )
                            list = new ArrayList<T>();
                        if ( ( ! unique ) || ( ! list.contains(result) ) )
                            list.add(result);
                    }

                    for(int i = 0; i < c.getInterfaces().length; i++)
                    {   Class interf = c.getInterfaces()[i];
                        result = super.get(interf);
                        if ( result != null )
                        {   /** add to the set */
                            if ( list == null )
                                list = new ArrayList<T>();
                            if ( ( ! unique ) || ( ! list.contains(result) ) )
                                list.add(result);
                        }
                    }

                    c = c.getSuperclass();
                }
            }
            else
            {   list = this.getRecursively(object.getClass(), unique); }
        }
        
        if ( list == null )
            list = Collections.EMPTY_LIST;
        
//        if ( list != null )
//        {   System.out.println("???????? list contains for(" + object + ")");
//            
//            for(int i = 0; i < list.size(); i++)
//            {   System.out.println("???????? \t" + list.get(i)); }
//        }
//        else
//        {
//            System.out.println("???????? list is null");
//        }
        
        
        return list;
    }
    
    /** static method to add ClassMap functionnalities to an existing Map
     *  return the value of the object key
     *  @param map a Map
     *  @param object an Object
     *  @return the value of the Objet in the given Map
     */
    public static Object get(Map map, Object object)
    {   Object result = null;
        
        if ( object != null && map != null )
        {   if ( object instanceof Class )
            {
                Class c = (Class)object;

                while( ! c.equals(Object.class) )
                {   result = map.get(c);
                    if ( result != null )
                        break;

                    for(int i = 0; i < c.getInterfaces().length; i++)
                    {   Class interf = c.getInterfaces()[i];
                        result = map.get(interf);
                        if ( result != null )
                            break;
                    }

                    if ( result != null )
                        break;

                    c = c.getSuperclass();
                }
            }
            else
            {   result = ClassMap.get(map, object.getClass()); }
        }
        
        return result;
    }
    
}
