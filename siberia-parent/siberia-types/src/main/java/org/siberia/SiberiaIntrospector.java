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
package org.siberia;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.siberia.base.collection.CollectionIntersector;
import org.siberia.type.info.AnnotationBasedBeanInfo;
import org.siberia.type.info.BeanInfoCategory;
import org.siberia.type.info.ConfigurationBeanInfo;
import org.siberia.type.info.ExtendedPropertyDescriptor;
import org.siberia.type.info.SiberiaBeanInfo;
import org.apache.log4j.Logger;
import org.siberia.type.Namable;
import org.siberia.type.SibType;

/**
 *
 * Intropector for Siberia platform
 *
 * @author alexis
 */
public class SiberiaIntrospector
{
    /** logger */
    public static Logger logger = Logger.getLogger(SiberiaIntrospector.class);
    
    /** cache for BeanInfo */
    private Map<BeanInfoContext, BeanInfo> beanInfoCache = new WeakHashMap<BeanInfoContext, BeanInfo>();
    
    /** Creates a new instance of SiberiaIntrospector */
    public SiberiaIntrospector()
    {   }
        
    /** methods which is called to determine if a PropertyDescriptor should be read-only<br>
     *  example : this method is responsible for the non-editability of the property name if the type has nameCouldChange = false
     *
     *  @param bean an instance of SibType
     *  @param property a PropertyDescriptor
     *
     *  @return true if we must consider the write method
     */
    protected boolean considerPropertyDescriptorWriteMethod(SibType bean, PropertyDescriptor property)
    {
        boolean result = true;
        if ( property != null && bean != null )
        {   if ( property.getName().equals(Namable.PROPERTY_NAME) )
            {   if ( ! bean.nameCouldChange() )
                {   result = false; }
            }
            if ( result && ! property.getName().equals(SibType.PROPERTY_READ_ONLY) )
            {   if ( bean.isReadOnly() )
                {   result = false; }
            }
        }
        
        return result;
    }
    
    /** method that allow to post process the PropertyDescriptor of a bean according to a bean instance
     *  @param bean a SibType
     *  @param info a BeanInfo
     */
    protected void postProcessBeanInfo(SibType bean, BeanInfo info)
    {   if ( info != null && bean != null )
        {   PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            if ( descriptors != null )
            {   for(int i = 0; i < descriptors.length; i++)
                {   PropertyDescriptor currentDescriptor = descriptors[i];
                    
                    if ( ! considerPropertyDescriptorWriteMethod(bean, currentDescriptor) )
                    {   if ( currentDescriptor != null )
                        {   
                            try
                            {   currentDescriptor.setWriteMethod(null); }
                            catch (IntrospectionException ex)
                            {   ex.printStackTrace(); }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Introspect on a Java Bean and learn about all its properties, exposed
     * methods, and events.
     * <p>
     * If the BeanInfo class for a Java Bean has been previously Introspected
     * then the BeanInfo class is retrieved from the BeanInfo cache.
     *
     * This methods returns a Bean with all basics descriptors and those expert too
     *  and search for ressources as plugin resources
     *
     * @param bean a SibType.
     * @param category a BeanInfoCategory
     * @param expert true to consider expert descriptors too
     * @return  A BeanInfo object describing the target bean.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     * @see #flushCaches
     * @see #flushFromCaches
     */
    public BeanInfo getBeanInfo(SibType bean)
    {   BeanInfo info = (bean == null ? null : getBeanInfo(bean.getClass()));
        
        this.postProcessBeanInfo(bean, info);
        
        return info;
    }
    
    /**
     * Introspect on a Java Bean and learn about all its properties, exposed
     * methods, and events.
     * <p>
     * If the BeanInfo class for a Java Bean has been previously Introspected
     * then the BeanInfo class is retrieved from the BeanInfo cache.
     *
     * This methods returns a Bean with expert descriptors too
     *  and search for ressources as plugin resources
     *
     * @param bean a SibType.
     * @param category a BeanInfoCategory
     * @param expert true to consider expert descriptors too
     * @return  A BeanInfo object describing the target bean.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     * @see #flushCaches
     * @see #flushFromCaches
     */
    public BeanInfo getBeanInfo(SibType bean, BeanInfoCategory category)
    {   BeanInfo info = (bean == null ? null : getBeanInfo(bean.getClass(), category));
        
        this.postProcessBeanInfo(bean, info);
        
        return info;
    }
    
    /**
     * Introspect on a Java Bean and learn about all its properties, exposed
     * methods, and events.
     * <p>
     * If the BeanInfo class for a Java Bean has been previously Introspected
     * then the BeanInfo class is retrieved from the BeanInfo cache.
     *
     * @param bean a SibType.
     * @param category a BeanInfoCategory
     * @param expert true to consider expert descriptors too
     * @param pluginSearch true if resources have to be found as plugin resources
     * @return  A BeanInfo object describing the target bean.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     * @see #flushCaches
     * @see #flushFromCaches
     */
    public BeanInfo getBeanInfo(SibType bean, BeanInfoCategory category, boolean expert, boolean pluginSearch)
    {   BeanInfo info = (bean == null ? null : getBeanInfo(bean.getClass(), category, expert, pluginSearch));
        
        this.postProcessBeanInfo(bean, info);
        
        return info;
    }
    
    /* #########################################################################
     * ###################### get bean info for classes ########################
     * ######################################################################### */
    
    /**
     * Introspect on a Java Bean and learn about all its properties, exposed
     * methods, and events.
     * <p>
     * If the BeanInfo class for a Java Bean has been previously Introspected
     * then the BeanInfo class is retrieved from the BeanInfo cache.
     *
     * This methods returns a Bean with all basics descriptors and those expert too
     *  and search for ressources as plugin resources
     *
     * @param beanClass  The bean class to be analyzed.
     * @param category a BeanInfoCategory
     * @param expert true to consider expert descriptors too
     * @return  A BeanInfo object describing the target bean.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     * @see #flushCaches
     * @see #flushFromCaches
     */
    public BeanInfo getBeanInfo(Class<?> beanClass)
    {   return getBeanInfo(beanClass, BeanInfoCategory.BASICS); }
    
    /**
     * Introspect on a Java Bean and learn about all its properties, exposed
     * methods, and events.
     * <p>
     * If the BeanInfo class for a Java Bean has been previously Introspected
     * then the BeanInfo class is retrieved from the BeanInfo cache.
     *
     * This methods returns a Bean with expert descriptors too
     *  and search for ressources as plugin resources
     *
     * @param beanClass  The bean class to be analyzed.
     * @param category a BeanInfoCategory
     * @param expert true to consider expert descriptors too
     * @return  A BeanInfo object describing the target bean.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     * @see #flushCaches
     * @see #flushFromCaches
     */
    public BeanInfo getBeanInfo(Class<?> beanClass, BeanInfoCategory category)
    {   return getBeanInfo(beanClass, category, true, true); }
    
    /**
     * Introspect on a Java Bean and learn about all its properties, exposed
     * methods, and events.
     * <p>
     * If the BeanInfo class for a Java Bean has been previously Introspected
     * then the BeanInfo class is retrieved from the BeanInfo cache.
     *
     * @param beanClass  The bean class to be analyzed.
     * @param category a BeanInfoCategory
     * @param expert true to consider expert descriptors too
     * @param pluginSearch true if resources have to be found as plugin resources
     * @return  A BeanInfo object describing the target bean.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     * @see #flushCaches
     * @see #flushFromCaches
     */
    public BeanInfo getBeanInfo(Class<?> beanClass, BeanInfoCategory category, boolean expert, boolean pluginSearch)
    {   /* search in a directory called info in the directory where the class is declared */
	
	BeanInfoContext context = new BeanInfoContext();
	context.relatedClass = beanClass;
	context.category = category;
	context.expert = expert;
	context.pluginSearch = pluginSearch;
	
        BeanInfo info = beanInfoCache.get(context);
	
	if ( info == null )
	{
	    if ( beanClass != null )
	    {   ClassLoader classLoader = beanClass.getClassLoader();

		StringBuffer beanInfoClassName = new StringBuffer();

		beanInfoClassName.append(beanClass.getPackage().getName());
		beanInfoClassName.append(".info.");
		beanInfoClassName.append(beanClass.getSimpleName());
		beanInfoClassName.append("BeanInfo");

		try
		{   Class beanInfoClass = Class.forName(beanInfoClassName.toString(), true, classLoader);

		    Object o = beanInfoClass.newInstance();

		    if ( o instanceof BeanInfo )
			info = (BeanInfo)o;
		}
		catch(Exception e)
		{   
		    if ( logger.isDebugEnabled() )
		    {   logger.debug("Impossible de recuperer le BeanInfo '" + beanInfoClassName.toString() + "'"); }
		}
	    }

	    if ( info == null )
	    {   AnnotationBasedBeanInfo sibInfo = new AnnotationBasedBeanInfo();
		sibInfo.setRelatedClass(beanClass);

		info = sibInfo;
	    }

	    if ( info instanceof SiberiaBeanInfo )
	    {   ((SiberiaBeanInfo)info).considerExpertDescriptor(expert);
		((SiberiaBeanInfo)info).setPluginContextActivated(pluginSearch);
	    }

	    if ( info instanceof ConfigurationBeanInfo )
	    {   ((ConfigurationBeanInfo)info).setBeanInfoCategory(category); }
	    
	    /* feed cache */
	    beanInfoCache.put(context, info);
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("context not in cache --> creating new BeanInfo for " + context);
	    }
	}
	else
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("retrieving BeanInfo for " + context + " on cache");
	    }
	}
        
        return info;
    }
    
    /** create an intersection of PropertyDescriptor according to a set of BeanInfo<br>
     *	the list of PropertyDescriptor returned will be the merge of all PropertyDescriptors of the given bean
     *	where PropertyDescriptor instance of ExtendedPropertyDescriptor which does not support grouping
     *	will be evicted
     *	@param beans a list of BeanInfo
     *	@return a List of PropertyDescriptors
     */
    public List<PropertyDescriptor> extractGroupSupportingPropertyDescriptors(List<BeanInfo> beans)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering extractGroupSupportingPropertyDescriptors with " + (beans == null ? 0 : beans.size()) + " bean infos");
	}
	
	List<PropertyDescriptor> descriptors = null;
	
	if ( beans != null )
	{
	    Map<String, PropertyDescriptor> map                   = new HashMap<String, PropertyDescriptor>();
	    
	    List<Set<String>>               propertiesPerBeanInfo = new ArrayList<Set<String>>();
	    
	    for(int i = 0; i < beans.size(); i++)
	    {
		BeanInfo currentBeanInfo = beans.get(i);
		
		if ( currentBeanInfo != null )
		{
		    if ( logger.isDebugEnabled() )
		    {
			Class c = null;
			
			if ( currentBeanInfo.getBeanDescriptor() != null )
			{
			    c = currentBeanInfo.getBeanDescriptor().getBeanClass();
			}
			logger.debug("considering bean info : " + currentBeanInfo + " for class : " + c);
		    }
		    
		    PropertyDescriptor[] descs = currentBeanInfo.getPropertyDescriptors();
		    
		    if ( descs != null )
		    {
			Set<String> currentBeanInfoProperties = new HashSet<String>();
			
			for(int j = 0; j < descs.length; j++)
			{
			    PropertyDescriptor currentDesc = descs[j];
			    
			    if ( currentDesc != null )
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("considering property : " + currentDesc.getName() + " (property type=" + currentDesc.getPropertyType() + ")");
				}
				
				if ( currentDesc instanceof ExtendedPropertyDescriptor )
				{
				    if ( ! ((ExtendedPropertyDescriptor)currentDesc).isSupportGrouping() )
				    {
					/* log */
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("avoiding property descriptor " + currentDesc.getName() + " that does not support group");
					}
					continue;
				    }
				}
			    
				/** add the property */
				if ( ! map.containsKey(currentDesc.getName()) )
				{
				    map.put(currentDesc.getName(), currentDesc);
				}
				currentBeanInfoProperties.add(currentDesc.getName());
			    }
			}
			
			propertiesPerBeanInfo.add(currentBeanInfoProperties);
		    }
		}
	    }
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("affichage de map : ");
		Iterator<Map.Entry<String, PropertyDescriptor>> entries = map.entrySet().iterator();
		while(entries.hasNext())
		{
		    Map.Entry<String, PropertyDescriptor> currentEntry = entries.next();
		    logger.debug("entry : " + currentEntry.getKey() + " --> " + currentEntry.getValue());
		}
		
		logger.debug("affichage de propertiesPerBeanInfo : ");
		Iterator<Set<String>> it = propertiesPerBeanInfo.iterator();
		while(it.hasNext())
		{
		    Set<String> sets = it.next();
		    
		    if ( sets == null )
		    {
			logger.debug("set : " + sets);
		    }
		    else
		    {
			StringBuffer buffer = new StringBuffer();
			
			Iterator<String> it2 = sets.iterator();
			
			while(it2.hasNext())
			{
			    buffer.append(it2.next() + ", ");
			}
			logger.debug("set : " + buffer.toString());
		    }
		}
	    }
	    
	    /** create a set which represents the intersection of all set contained by propertiesPerBeanInfo */
	    CollectionIntersector<String> intersector = new CollectionIntersector<String>();
	    Set<String>[]                 sets        = (Set<String>[])propertiesPerBeanInfo.toArray(
							new HashSet[propertiesPerBeanInfo.size()]);
	    Set<String> intersectSet = intersector.intersection( sets );
	    
	    /** modify map to convey to intersection */
	    if ( intersectSet == null || intersectSet.size() == 0 )
	    {
		map.clear();
	    }
	    else
	    {
		Set<String> keyToRemove = new HashSet<String>();
		
		Iterator<String> keys = map.keySet().iterator();
		while(keys.hasNext())
		{
		    String currentKey = keys.next();
		    
		    if ( ! intersectSet.contains(currentKey) )
		    {
			keyToRemove.add(currentKey);
		    }
		}
		
		Iterator<String> toRemoveIt = keyToRemove.iterator();
		
		while(toRemoveIt.hasNext())
		{
		    String toRemove = toRemoveIt.next();
		    
		    map.remove(toRemove);
		}
	    }
	    
	    
	    descriptors = new ArrayList<PropertyDescriptor>(map.values());
	}
	
	if ( descriptors == null )
	{
	    descriptors = Collections.emptyList();
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("extractGroupSupportingPropertyDescriptors returns " + descriptors.size() + " property descriptors");
	}
	
	return descriptors;
    }
    
    /** bean info context descriptor */
    private static class BeanInfoContext
    {
	/** class */
	private Class            relatedClass = null;
	
	/** category */
	private BeanInfoCategory category     = null;
	
	/** expert */
	private boolean          expert       = false;
	
	/** pluginSearch */
	private boolean          pluginSearch = false;

	public String toString()
	{
	    return "BeanInfoContext for class=" + this.relatedClass + " on category=" + this.category + 
			" (expert ? " + this.expert + ", pluginSearch ? " + this.pluginSearch + ")";
	}

	public int hashCode()
	{
	    return (this.relatedClass == null ? 0 : this.relatedClass.hashCode());
	}

	public boolean equals(Object obj)
	{
	    boolean result = false;
	    
	    if ( obj instanceof BeanInfoContext )
	    {
		BeanInfoContext other = (BeanInfoContext)obj;
		
		/** first, test class which is the most discriminant criterion */
		boolean sameClass = false;
		
		if ( this.relatedClass == null )
		{
		    if ( other.relatedClass == null )
		    {
			sameClass = true;
		    }
		}
		else
		{
		    sameClass = this.relatedClass.equals(other.relatedClass);
		}
		
		if ( sameClass && this.pluginSearch == other.pluginSearch )
		{
		    if ( this.expert == other.expert )
		    {
			boolean sameCategory = false;
			
			if ( this.category == null )
			{
			    if ( other.category == null )
			    {
				sameCategory = true;
			    }
			}
			else
			{
			    sameCategory = this.category.equals(other.category);
			}
			
			if ( sameCategory )
			{
			    result = true;
			}
		    }
		}
	    }
	    
	    return result;
	}
    }
    
}
