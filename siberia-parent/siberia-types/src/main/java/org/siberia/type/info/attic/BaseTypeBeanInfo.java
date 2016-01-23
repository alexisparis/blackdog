/* 
 * Siberia types : siberia plugin defining structures managed by siberia platform
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
package org.siberia.type.info.attic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.siberia.type.SibBaseType;
import org.siberia.type.annotation.bean.BeanProperty;
import org.siberia.type.info.*;

/**
 *
 * Abstract BeanInfo for Classes that manages a basic sun class instance like String, Integer or other
 *
 * @author alexis
 */
public abstract class BaseTypeBeanInfo extends DefaultTypeBeanInfo
{
    /** class caracteristics of the class */
    private Class baseClass = null;
    
    /** Creates a new instance of BaseTypeBeanInfo
     *  @param beanClass the class related with this bean info
     *  @param cl the base java type caracteristics of the class
     */
    public BaseTypeBeanInfo(Class beanClass, Class cl)
    {   super(beanClass);
        
        if ( cl == null )
            throw new IllegalArgumentException("base java class cannot be null");
        this.baseClass = cl;
    }
    
    /** return the method to use as write method for a Property
     *  @param field a Field
     *  @param annotation the BeanProperty annotation
     */
    protected Method getWriteMethod(Field field, BeanProperty annotation)
    {   Method m = null;
        
        if ( annotation != null )
        {   if ( annotation.name().equals(SibBaseType.PROPERTY_VALUE) )
            {   
                Class c = this.getRelatedClass();
                while(c != null && ! c.equals(Object.class) )
                {
                    try
                    {   m = c.getDeclaredMethod("setValue", this.baseClass);
                        break;
                    }
                    catch(Exception e)
                    {   }
                    
                    c = c.getSuperclass();
                }
            }
        }
        
        if ( m == null )
            m = super.getWriteMethod(field, annotation);
        
        return m;
    }
    
    /** return the method to use as read method for a Property
     *  @param field a Field
     *  @param annotation the BeanProperty annotation
     */
    protected Method getReadMethod(Field field, BeanProperty annotation)
    {   Method m = null;
        
        if ( annotation != null )
        {   if ( annotation.name().equals(SibBaseType.PROPERTY_VALUE) )
            {   
                Class c = this.getRelatedClass();
                while( c != null && ! c.equals(Object.class) )
                {
                    try
                    {   m = c.getDeclaredMethod("getValueImpl", (Class[])null);
                        break;
                    }
                    catch(Exception e)
                    {   }
                    
                    c = c.getSuperclass();
                }
            }
        }
        
        if ( m == null )
            m = super.getReadMethod(field, annotation);
        
        return m;
    }
    
}
