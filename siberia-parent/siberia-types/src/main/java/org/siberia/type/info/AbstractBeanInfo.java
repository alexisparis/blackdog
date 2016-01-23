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
package org.siberia.type.info;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.siberia.type.annotation.bean.BeanProperty;
import org.siberia.type.annotation.bean.ConfigurationItem;

/**
 *
 * Abstract implementation of A SiberiaBeanInfo
 *
 * @author alexis
 */
public abstract class AbstractBeanInfo implements SiberiaBeanInfo
{
    /** category of the BeanInfo */
    private BeanInfoCategory category        = BeanInfoCategory.BASICS;
    
    /** consider expert descriptors */
    private boolean          expertMode      = true;
    
    /** indicates if all resources have to be found thanks to plugin classLoading */
    private boolean          pluginActivated = true;
    
    /** Creates a new instance of AbstractBeanInfo */
    public AbstractBeanInfo()
    {   }
    
    /** return the bean info category
     *  @return a BeanInfoCategory
     */
    public BeanInfoCategory getBeanInfoCategory()
    {   return this.category; }
    
    /** initialize the BeanInfo category
     *  @param category a BeanInfoCategory
     */
    public void setBeanInfoCategory(BeanInfoCategory category)
    {   this.category = category; }
    
    /** indicates if expert information have to be taken into account
     *  @return a boolean
     */
    public boolean considerExpertDescriptor()
    {   return this.expertMode; }
    
    /** indicates if expert information have to be taken into account
     *  @param consider a boolean
     */
    public void considerExpertDescriptor(boolean consider)
    {   this.expertMode = consider; }
    
    /** indicates if icon resources have to be find in a plugin context
     *  @param pluginContext
     */
    public void setPluginContextActivated(boolean pluginContext)
    {   this.pluginActivated = pluginContext; }
    
    /** indicates if icon resources have to be find in a plugin context
     *  @return true if icon resources have to be find in a plugin context
     */
    public boolean isPluginContextActivated()
    {   return this.pluginActivated; }
    
    /** ########################################################################
     *  ############ Methods that deal with the rights to consider #############
     *  ######################################################################## */
    
    /** return true if the given Field should generate a PropertyDescriptor
     *  to overwrite
     *
     *  @param field a Field
     *  @return true if it should generate a PropertyEditor
     */
    protected boolean shouldGenerate(Field field)
    {   return this.shouldGenerate((AccessibleObject)field); }
    
    /** return true if the given Field should generate a MethodDescriptor
     *
     *  @param method a Method
     *  @return true if it should generate a MethodEditor
     */
    protected boolean shouldGenerate(Method method)
    {   return this.shouldGenerate((AccessibleObject)method); }
    
    /** return true if the given Field should generate a MethodDescriptor
     *
     *  @param object a AccessibleObject
     *  @return true if it should generate a MethodEditor
     */
    protected boolean shouldGenerate(AccessibleObject object)
    {   /* check if the field has got an annotation ConfigurationItem */
        boolean result = true;
        if ( object == null )
	{
            result = false;
	}
        else
        {   if ( ! this.getBeanInfoCategory().equals(BeanInfoCategory.ALL) )
            {
                ConfigurationItem annotation = (ConfigurationItem)object.getAnnotation(ConfigurationItem.class);

//                if ( (annotation != null && this.getBeanInfoCategory().equals(BeanInfoCategory.BASICS)) ||
//                     (annotation == null && this.getBeanInfoCategory().equals(BeanInfoCategory.CONFIGURATION)) )
                if ( annotation != null && this.getBeanInfoCategory().equals(BeanInfoCategory.BASICS) )
                {   result = false; }
            }
        }
        return result;
    }
    
    
    
}
