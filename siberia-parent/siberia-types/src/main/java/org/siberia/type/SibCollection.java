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
package org.siberia.type;

import java.beans.PropertyVetoException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.siberia.SiberiaTypesPlugin;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;
import org.siberia.type.annotation.bean.ConfigurationItem;
import org.siberia.type.event.ContentChangeEvent;
import org.siberia.type.event.ContentClearedEvent;

/**
 *
 * Abstract class for all classes that a collection of SibType instances<br>
 * This class is configurable and if it is configurable, then, the meta flags can be modified
 *
 * @author alexis
 */
@Bean(  name="SibCollection",
        internationalizationRef="org.siberia.rc.i18n.type.SibCollection",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public abstract class SibCollection extends SibConfigurable
                                    implements Collection
{   
    /** property names */
    public static final String PROPERTY_EDIT_AUTHORIZATION     = "edit_auth";
    public static final String PROPERTY_CREATE_AUTHORIZATION   = "create_auth";
    public static final String PROPERTY_REMOVE_AUTHORIZATION   = "remove_auth";
    public static final String PROPERTY_CONTENT                = "content";
    public static final String PROPERTY_CONTENT_AS_CHILD       = "contentAsChild";
    public static final String PROPERTY_SUBCLASSES_ACCEPTATION = "acceptSubClasses";
    
    /** indicates if edit is allowed */
    @BeanProperty(name=PROPERTY_EDIT_AUTHORIZATION,
                  internationalizationRef="org.siberia.rc.i18n.property.SibCollection_edit_auth",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setEditAuthorization",
                  writeMethodParametersClass={boolean.class},
                  readMethodName="isEditAuthorized",
                  readMethodParametersClass={}
                 )
    @ConfigurationItem
    private   boolean   couldEdit            = true;
    
    /** indicates if create is allowed */
    @BeanProperty(name=PROPERTY_CREATE_AUTHORIZATION,
                  internationalizationRef="org.siberia.rc.i18n.property.SibCollection_create_auth",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setCreateAuthorization",
                  writeMethodParametersClass={boolean.class},
                  readMethodName="isCreateAuthorized",
                  readMethodParametersClass={}
                 )
    @ConfigurationItem
    private   boolean   couldCreate          = true;
    
    /** indicates if remove is allowed */
    @BeanProperty(name=PROPERTY_REMOVE_AUTHORIZATION,
                  internationalizationRef="org.siberia.rc.i18n.property.SibCollection_remove_auth",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setRemoveAuthorization",
                  writeMethodParametersClass={boolean.class},
                  readMethodName="isRemoveAuthorized",
                  readMethodParametersClass={}
                 )
    @ConfigurationItem
    private   boolean   couldRemove          = true;
    
    /** indicates if edit is allowed */
    @BeanProperty(name=PROPERTY_CONTENT_AS_CHILD,
                  internationalizationRef="org.siberia.rc.i18n.property.SibCollection_contentAsChild",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setContentItemAsChild",
                  writeMethodParametersClass={boolean.class},
                  readMethodName="isContentItemAsChild",
                  readMethodParametersClass={}
                 )
    @ConfigurationItem
    private   boolean   contentItemIsChild    = true;
    
    /** superclass of allowed instantiations */
    private   Class     contentType           = SibType.class; 
    
    /** true to indicate that this list supports all classes that have the allowed class as super class
     *  if false, that means that this list only accept instance of allowed class and not instance of a class that extends allowed class
     */
    private   boolean   acceptSubClassesItem  = true;
    
    /** Collection owned by this instance */
    protected Collection collection           = null;
    
    /** logger */
    private transient Logger logger           = Logger.getLogger(SibCollection.class);
    
    /** create a new instance of SibCollection that could contains SibType<br>
     *  Items can be edited, added and removed.
     */
    public SibCollection()
    {   super();
        
        this.collection = this.initCollection();
    }
    
    public Collection get()
    {   return this; }
    
    /** return the collection
     *	@return a Collection
     */
    protected Collection getCollection()
    {
	return this.collection;
    }
    
    protected void setCollection(Collection collection)
    {
	this.clear();
	
	ContentChangeEvent evt = null;
	
	this.collection = collection;
	
	this.addAll(collection, true);
	
//	for(int i = 0; i < this.getChildrenCount(); i++)
//	{
//	    SibType type = this.getChildAt(i);
//
//	    this.removeChildElement(type);
//	}
//	
//	if ( this.isContentItemAsChild() )
//	{
//	    if ( collection != null )
//	    {
//		Iterator it = collection.iterator();
//		while(it.hasNext())
//		{
//		    Object o = it.next();
//
//		    if ( o instanceof SibType )
//		    {
//			((SibType)o).setParent(this);
//		    }
//		}
//	    }
//	}
    }
    
    /** return true if content is considered as child
     *  @return a boolean
     */
    public boolean isContentItemIsChild()
    {   return this.isContentItemAsChild(); }
    
    /** indicates if content item as to be considered as children
     *  @param contentItemAsChild true to indicate that the item contained by this collection have to be considered as child
     *          of this collection
     */
    public void setContentItemIsChild(boolean contentItemAsChild) throws PropertyVetoException
    {
	this.setContentItemAsChild(contentItemAsChild);
    }
    
    /** return true if content is considered as child
     *  @return a boolean
     */
    public boolean isContentItemAsChild()
    {   return this.contentItemIsChild; }
    
    /** indicates if content item as to be considered as children
     *  @param contentItemAsChild true to indicate that the item contained by this collection have to be considered as child
     *          of this collection
     */
    public void setContentItemAsChild(boolean contentItemAsChild) throws PropertyVetoException
    {
        // has changed ??
        if ( contentItemAsChild != this.isContentItemAsChild() )
        {   
            this.fireVetoableChange(PROPERTY_CONTENT_AS_CHILD, ! contentItemAsChild, contentItemAsChild);
            
            this.checkReadOnlyProperty(PROPERTY_CONTENT_AS_CHILD, ! contentItemAsChild, contentItemAsChild);
            
            /* sure the change will be apply */
            this.contentItemIsChild = contentItemAsChild;
            
            if ( this.isContentItemAsChild() )
            {   
		List<SibType> copy = new ArrayList<SibType>(this);
		this.addChildElements(copy);
//		/** add all existing item to child list */
//                Iterator items = this.iterator();
//                while(items.hasNext())
//                {   SibType current = (SibType)items.next();
//                    
//                    if ( current != null )
//                    {   this.addChildElement(current); }
//                }
            }
            else
            {   
		this.removeChildElements(this);
//		/* remove all children that represents a SibType appearing in the collection */
//                for(int i = 0; i < this.getChildrenCount(); i++)
//                {   SibType current = this.getChildAt(i);
//                    
//                    if ( current != null )
//                    {   /* if it appears in the collection ... */
//                        if ( this.contains(current) )
//                        {   this.removeChildElement(current); }
//                    }
//                }
            }
            
            /** warn all listener that the change is applied */
            this.firePropertyChange(PROPERTY_CONTENT_AS_CHILD, ! this.isContentItemAsChild(), this.isContentItemAsChild());
        }
    }
    
    /** return the number of children for this object
     *  @return the number of children for this object
     */
    @Override
    public int getChildrenCount()
    {   if ( this.isContentItemAsChild() )
	{
            return super.getChildrenCount();
	}
        else
	{
            return 0;
	}
    }

    /** return the allowed type of items that could be contained by the collection
     *  @return the allowed type of items that could be contained by the collection
     */
    public Class getAllowedClass()
    {   return this.contentType; }

    /** initialize the allowed type of items that could be contained by the collection
     *  @param cl the allowed type of items that could be contained by the collection
     */    
    public void setAllowedClass(Class cl)
    {   this.contentType = cl; }
    
    /** tell if the item can be added to the collection
     *  @param c a Class
     *  @return true if the item can be added to the collection
     */
    public boolean itemAllowed(Class c)
    {   
        boolean result = false;
        
        if ( this.getAllowedClass() != null && c != null )
        {   
            if ( this.isAcceptingSubClassesItem() )
            {   
                result = this.getAllowedClass().isAssignableFrom(c);
            }
            else
            {
                result = this.getAllowedClass().equals(c);
            }
        }
        return result;
    }
    
    /** tell if the item can be added to the collection
     *  @param item 
     *  @return true if the item can be added to the collection
     */
    public boolean itemAllowed(Object item)
    {   
        boolean result = false;
        
        if ( this.getAllowedClass() != null && item != null )
        {   
            result = this.itemAllowed(item.getClass());
        }
        return result;
    }
    
    /** indicate if the list accept sub classes item of the allowed class
     *  if false, that means that this list only accept instance of allowed class and not instance of a class that extends allowed class
     *  @return a boolean
     */
    public boolean isAcceptingSubClassesItem()
    {
        return this.acceptSubClassesItem;
    }
    
    /** set if the list accept sub classes item of the allowed class
     *  if false, that means that this list only accept instance of allowed class and not instance of a class that extends allowed class
     *  @param acceptSubClasses a boolean
     */
    public void setAcceptSubClassesItem(boolean acceptSubClasses)
    {
        if ( acceptSubClasses != this.isAcceptingSubClassesItem() )
        {
            this.acceptSubClassesItem = acceptSubClasses;
            
            this.firePropertyChange(PROPERTY_SUBCLASSES_ACCEPTATION, ! this.isAcceptingSubClassesItem(), this.isAcceptingSubClassesItem());
        }
    }
    
    /** initialize the collection */
    protected abstract Collection initCollection();

    @Override
    public boolean isLeaf()
    {   
	boolean leaf = super.isLeaf();
        
        if ( leaf && this.isContentItemAsChild() && this.size() > 0 )
        {   leaf = false; }
        
        return leaf;
    }
    
    /** ########################################################################
     *  ###################### Collection implementation #######################
     *  ######################################################################## */
        
    /** indicate if the list contains some elements
     *  @return true if the list contains some elements
     */
    public boolean isEmpty()
    {   if ( this.collection == null )
        {   return true; }
        return (this.collection.isEmpty());
    }
    
    /** return the size of the collection
     *  @return the size of the collection
     */
    public int size()
    {   if ( this.collection == null )   
            return 0;
        return this.collection.size();
    }
    
    /** indicates if the collection contains the given element
     *  @param o an object
     *  @return true if the collection contains the given element
     */
    public boolean contains(Object o)
    {   if ( this.collection == null )
            return false;
        return this.collection.contains(o);
    }
    
    /** return an iterator over the element of the collection
     *  @return an iterator over the element of the collection
     */
    public Iterator iterator()
    {   if ( this.collection == null )
            return Collections.EMPTY_LIST.iterator();
        return this.collection.iterator();
    }
    
    /** return an array with all element of the collection
     *  @return an array with all element of the collection
     */
    public Object[] toArray()
    {   if ( this.collection == null )
            return new Object[]{};
        return this.collection.toArray();
    }
    
    /** return an array with all element of the collection
     *  @return an array with all element of the collection
     */
    public Object[] toArray(Object[] a)
    {   
	if ( this.collection == null )
	{
            return new Object[]{};
	}
	
        return this.collection.toArray(a);
    }
    
    /** add a new element 
     *  @param o the element to add
     *  @return true if the collection was modified
     */
    public boolean add(Object o)
    {
	return this.add(o, true);
    }
    
    /** add a new element 
     *  @param o the element to add
     *	@param fireContentChanged true to fire a ContentChangedEvent if the item was successfully added
     *  @return true if the collection was modified
     */
    protected boolean add(Object o, boolean fireContentChanged)
    {
	return this.add(o, fireContentChanged, this.isContentItemAsChild());
    }
    
    /** add a new element 
     *  @param o the element to add
     *	@param fireContentChanged true to fire a ContentChangedEvent if the item was successfully added
     *	@param addItemAsChild true to add this item as a child
     *  @return true if the collection was modified
     */
    protected boolean add(Object o, boolean fireContentChanged, boolean addItemAsChild)
    {   
	boolean result = false;
	
	if ( o != null )
        {   if( this.itemAllowed(o))
            {   if ( o instanceof SibType )
                {   SibType item = (SibType)o;
                    
//                    synchronized(item)
                    {   if ( this.collection == null )
                        {   this.collection = this.initCollection(); }

                        int position = this.collection.size();
                        
                        result = this.collection.add(item);
			
			if ( result )
			{
			    if ( fireContentChanged )
			    {
				this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CONTENT, ContentChangeEvent.ADD, position, item));
			    }

			    if ( addItemAsChild )
			    {
				this.addChildElement(item);
			    }
			}
                    }
                }
            }
        }
        return result;
    }
    
    /** add a new element called from addAll
     *  @param o the element to add
     *  @return true if the collection was modified
     */
    protected boolean addCommingFromAddAll(Object o)
    {
	return this.add(o, false, false);
    }
    
    /** add a collection
     *  @param c a collection to add
     *  @return true if the collection was modified
     */
    public boolean addAll(Collection c)
    {
	return this.addAll(c, false);
    }
    
    /** add a collection
     *  @param c a collection to add
     *	@param collectionAlreadyUpdated true to indicate that items shalml not be added in the list
     *		consider that all items were successfully added.
     *  @return true if the collection was modified
     */
    private boolean addAll(Collection c, boolean collectionAlreadyUpdated)
    {   
	boolean modified = false;
	
	if ( c != null && c.size() > 0 )
        {   
	    if ( this.collection == null )
	    {	
                this.collection = this.initCollection();
	    }
	    
	    Iterator it = c.iterator();
	    
	    List<SibType> itemsAdded = new ArrayList<SibType>(c.size());
	    
	    Integer firstAddIndex = null;
	    
	    while(it.hasNext())
	    {
		Object o = it.next();
		
		if ( o instanceof SibType )
		{
		    if ( collectionAlreadyUpdated || this.addCommingFromAddAll(o) )
		    {
			itemsAdded.add( (SibType)o );

			if ( firstAddIndex == null )
			{
			    firstAddIndex = this.size() - 1;
			}
		    }
		}
	    }
	    
	    if ( itemsAdded.size() > 0 )
	    {
		modified = true;
		
		int[] positions = new int[itemsAdded.size()];
		
		for(int i = 0; i < positions.length; i++)
		{
		    positions[i] = firstAddIndex.intValue() + i;
		}
		
                this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CONTENT, ContentChangeEvent.ADD, positions, 
                                    (SibType[])itemsAdded.toArray(new SibType[itemsAdded.size()])));
		
		/** add as children if isContentAsChild is set */
		if ( this.isContentItemAsChild() )
		{
		    this.addChildElements(itemsAdded);
		}
	    }
	    
//            List items = new ArrayList(c.size());
//	    
//            for(Iterator it = c.iterator(); it.hasNext();)
//            {   Object object = it.next();
//                boolean added = this.add(object);
//                if ( added && ! modified )
//                {   modified = true;
//                    items.add(object);
//                }
//            }   
//            if ( modified )
//            {   /* fire event */
//                int[] positions = new int[items.size()];
//                int index = 0;
//                for(Iterator it2 = items.iterator(); it2.hasNext();)
//                    positions[index++] = this.indexOfChild(it2.next());
//                this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CONTENT, ContentChangeEvent.ADD, positions, 
//                                    (SibType[])items.toArray(new SibType[items.size()])));
//            }
        }
	
        return modified;
    }
    
    /** remove an element
     *  @param o the object to remove
     *  @return true if the collection was modified
     */
    public boolean remove(Object o)
    {
	return this.remove(o, true);
    }
    
    /** remove an element
     *  @param o the object to remove
     *	@param fireContentChanged true to fire a ContentChangedEvent if the item were successfully removed
     *  @return true if the collection was modified
     */
    protected boolean remove(Object o, boolean fireContentChanged)
    {   
	return this.remove(o , fireContentChanged, this.isContentItemAsChild());
    }
    
    /** remove an element
     *  @param o the object to remove
     *	@param fireContentChanged true to fire a ContentChangedEvent if the item were successfully removed
     *	@param removeItemAsChild true to remove this item as a child
     *  @return true if the collection was modified
     */
    protected boolean remove(Object o, boolean fireContentChanged, boolean removeItemAsChild)
    {   
        boolean result = false;
        
        if ( o != null && this.collection != null )
        {   if ( o instanceof SibType )
            {   SibType item = (SibType)o;
                
                if ( item.isRemovable() )
                {
                    int index = this.getIndexRelatedTo(item);

//                    item.clearPropertyChangeListenerList();

                    result = this.collection.remove(o);
		    
		    if ( result )
		    {
			if ( removeItemAsChild )
			{
			    item.setParent(null);

			    this.removeChildElement(item);
			}

			if ( fireContentChanged )
			{
			    this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CONTENT, ContentChangeEvent.REMOVE, index, item));
			}
		    }
                }
            }
        }
	
        return result;
    }
    
    /** remove a new element called from removeAll
     *  @param o the element to remove
     *  @return true if the collection was modified
     */
    protected boolean removeCommingFromRemoveAll(Object o)
    {
	return this.remove(o, false, false);
    }
    
    /** return an index related to the given item<br>
     *	this method is called from remvoe methods to complete the information of ContentChangeEvent
     *	@param item a SibType
     *	@return the associated index or -1 if we do not know
     */
    protected int getIndexRelatedTo(SibType item)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getIndexRelatedto(" + item + ") on " + this);
	    logger.debug("content item as child : " + this.isContentItemAsChild() + " for " + this);
	}
	
	int result = -1;
	
	if ( this.isContentItemAsChild() )
	{
	    result = this.indexOfChildReference(item);
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getIndexRelatedto(" + item + ") returns " + result);
	}
	
	return result;
    }
    
    /** return true if the collection support items indexing
     *	@return true if the collection support items indexing
     */
    protected boolean isSupportingIndexation()
    {
	return false;
    }
    
    /** remove a collection
     *  @param c a collection to remove
     *  @return true if the collection was modified
     */
    public boolean removeAll(Collection c)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling removeAll with a collection which size is " + (c == null ? 0 : c.size()));
	    logger.debug("collection is " + this + " (identity hashcode=" +
						System.identityHashCode(this) + ")");
	}
	
	boolean modified = false;
	
	if ( c != null && c.size() > 0 && this.collection != null && this.collection.size() > 0 )
        {   
	    /** map used with indexation supporting SibCollection */
	    Map<Integer, SibType> map = null;
	    
	    /** list used in both cases */
	    List<SibType> removedItems = new ArrayList<SibType>(c.size());
	    
	    List<Integer> indexList = null;
	    
	    if ( this.isSupportingIndexation() )
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("collection supports indexation --> feed position map...");
		}
		Iterator<SibType> it = c.iterator();
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("loop on all items to remove to determine their position in the sib collection");
		    logger.debug("collection of item to remove size : " + (c == null ? 0 : c.size()));
		    logger.debug("iterator on collection has next ? " + it.hasNext());
		}
		
		while(it.hasNext())
		{
		    SibType currentItem = it.next();
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("considering item to remove : " + currentItem);
		    }

		    int position = this.getIndexRelatedTo(currentItem);

		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("index of related item : " + currentItem + " is " + position);
		    }
		    if ( position >= 0 && position < this.collection.size() )
		    {
			if ( map == null )
			{
			    map = new HashMap<Integer, SibType>(c.size());
			}
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("putting (" + position + ", " + currentItem + ") in position map");
			}
			map.put(position, currentItem);
			
			if ( indexList == null )
			{
			    indexList = new ArrayList<Integer>(c.size());
			}
			indexList.add(position);
		    }
		    else
		    {
			logger.error("got index " + position + " for item " + currentItem + " but must be in [0, " + this.collection.size() + "[");
		    }
		}
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("end of position map feeding");
		}
	    }
	    else
	    {
		// do nothing
	    }
	    
	    int currentIndexInList  = -1;
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("is supporting indexation ? " + this.isSupportingIndexation());
	    }
	    if ( this.isSupportingIndexation() && indexList != null )
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("getting map keys");
		}
//		Set<Integer> keys = map.keySet();
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("got map keys");
		    logger.debug("before creating a list with index map keys");
		}
//		indexList = new ArrayList<Integer>(keys.size());
//		
//		Iterator<Integer> itInt = keys.iterator();
//		
//		while(itInt.hasNext())
//		{
//		    Integer currentInt = itInt.next();
//		    if ( logger.isDebugEnabled() )
//		    {
//			logger.debug("adding index " + currentInt + " to index list");
//		    }
//		    indexList.add( currentInt );
//		}
	    
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("list with index map keys created");
		}
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("indexList size");
		    logger.debug("sorting indexList...");
		}
		if ( indexList != null )
		{
		    Collections.sort(indexList);
		}
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("indexList sorted");
		}
		
		currentIndexInList = indexList.size() - 1;
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("currentIndexInList=" + currentIndexInList);
		}
		
		while(currentIndexInList >= 0)
		{
		    int currentPosition = indexList.get(currentIndexInList);

		    SibType currentItem = map.get(currentPosition);

		    int index = this.getIndexRelatedTo(currentItem);

		    if ( this.removeCommingFromRemoveAll(currentItem) )
		    {
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("item " + currentItem + " removed --> added in the list of items removed");
			}
			removedItems.add(currentItem);
		    }
		    else
		    {
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("item " + currentItem + " not removed");
			}
			// could be ignored since we delete the current entry in indexList
    //		    /* the item has not been removed */
    //		    map.remove(currentPosition);

			/* remove current item in indexList and continue */
			indexList.remove(new Integer(index));
		    }

		    currentIndexInList --;
		}
	    }
	    else
	    {
		Iterator<SibType> it = c.iterator();
		while(it.hasNext())
		{
		    SibType currentItem = it.next();

		    if ( this.removeCommingFromRemoveAll(currentItem) )
		    {
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("item " + currentItem + " removed --> added in the list of items removed");
			}
			removedItems.add(currentItem);
		    }
		}
	    }
	    
	    if ( this.isSupportingIndexation() && indexList != null )
	    {
		if ( indexList.size() > 0 ) // efficient removals
		{
		    int indexListSize = indexList.size();

		    if ( this.collection == null || this.collection.size() == 0 )
		    {
			/** optimization --> avoid to create two arrays */
			this.firePropertyChange(new ContentClearedEvent(this, PROPERTY_CONTENT, (SibType[])removedItems.toArray(new SibType[removedItems.size()])));
		    }
		    else
		    {
			/* finally create the two arrays */
			int[]     positions     = new int[indexListSize];
			SibType[] _removedItems = new SibType[indexListSize];

			for(int i = 0; i < indexListSize; i++)
			{
			    int pos = indexList.get(indexListSize - i - 1);
			    positions[i] = pos;
			    _removedItems[i] = map.get(pos);
			}

			indexList.clear();

			this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CONTENT,
								       ContentChangeEvent.REMOVE,
								       positions,
								       _removedItems));
		    }
			
		    if ( this.isContentItemAsChild() )
		    {
			this.removeChildElements(removedItems);
		    }
		}
	    
		map.clear();
	    }
	    else
	    {
		/** fire content event for the inner collection and remove child if necessary */
		if ( removedItems != null && removedItems.size() > 0 )
		{
		    if ( this.collection == null || this.collection.size() == 0 )
		    {
			this.firePropertyChange(new ContentClearedEvent(this, PROPERTY_CONTENT, (SibType[])removedItems.toArray(new SibType[removedItems.size()])));
		    }
		    else
		    {
			int[] positions = new int[removedItems.size()];
			
			Arrays.fill(positions, -1);
			
			this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CONTENT,
								       ContentChangeEvent.REMOVE,
								       positions,
								       (SibType[])removedItems.toArray(new SibType[removedItems.size()]) ));
		    }

		    if ( this.isContentItemAsChild() )
		    {
			this.removeChildElements(removedItems);
		    }
		}
	    }
	}
	
	return modified;
    }
     
     /** indicates if all elements of the given collection appears in the collection
      * @param c a Collection
      * @return true if all elements of the given collection appears in the collection
      */
    public boolean containsAll(Collection c)
    {   if ( c != null && this.collection != null )
        {   return this.collection.containsAll(c); }
        return false;
    }
    
    /** retains all the element contained by the given collection
     *  @param c a collection
     *  @return true if the collection was modified
     */
    public boolean retainAll(Collection c)
    {   if ( c == null )
        {   if ( ! this.isEmpty() )
            {   this.clear();
                return true;
            }
        }
        else if ( c.size() == 0 && ! this.isEmpty())
        {   this.clear();
            return true;
        }
        
        // c contains elements and my collection is perhaps empty
        if ( ! this.isEmpty() )
        {   boolean modified   = false;
            List    items      = null;
            Object  currentElt = null;
            for(Iterator it = this.collection.iterator(); it.hasNext();)
            {   currentElt = it.next();
                if ( c.contains(currentElt) )
                {   
                    // TODO : integrate isRemovable on SibType
                    boolean removed = this.collection.remove(currentElt);
                    if ( removed && ! modified )
                    {   modified = true;
                        if ( items == null )
                            items = new ArrayList();
                        items.add(currentElt);
                    }
                }
            }
            if ( modified )
            {   /* fire event */
                int[] positions = new int[items.size()];
                int index = 0;
                for(Iterator it2 = items.iterator(); it2.hasNext();)
                    positions[index++] = this.indexOfChild(it2.next());
                this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CONTENT, ContentChangeEvent.REMOVE, positions, 
                                    (SibType[])items.toArray(new SibType[]{})));
            }
            
            return modified;
        }
        return false;
    }
    
    /** clear the collection */
    public void clear()
    {   
	this.removeAll(this);
    }
    
    /** ########################################################################
     *  ################### META-FLAGS GETTERS AND SETTERS #####################
     *  ######################################################################## */

    /** indicates if item edition is allowed
     *  @return true if item edition is allowed
     */
    protected boolean isCouldEdit()
    {   return this.isEditAuthorized(); }

    /** tell if item edition is allowed<br>
     *  The list has to be configurable
     *  @param couldEdit true if item edition is allowed
     */
    protected void setCouldEdit(boolean couldEdit) throws PropertyVetoException
    {
	this.setEditAuthorization(couldEdit);
    }

    /** indicates if item edition is allowed
     *  @return true if item edition is allowed
     */
    public boolean isEditAuthorized()
    {   return couldEdit; }

    /** tell if item edition is allowed<br>
     *  The list has to be configurable
     *  @param couldEdit true if item edition is allowed
     */
    public void setEditAuthorization(boolean couldEdit) throws PropertyVetoException
    {   if ( this.isConfigurable() )
        {   
	    if ( couldEdit != this.isEditAuthorized() )
	    {
		this.fireVetoableChange(PROPERTY_EDIT_AUTHORIZATION, this.isEditAuthorized(), couldEdit);

		this.checkReadOnlyProperty(PROPERTY_EDIT_AUTHORIZATION, this.isEditAuthorized(), couldEdit);

		this.couldEdit = couldEdit;

		this.firePropertyChange(PROPERTY_EDIT_AUTHORIZATION, ! this.couldEdit, this.couldEdit);
	    }
        }
    }

    /** indicates if creating item is allowed
     *  @return true if creating item is allowed
     */
    protected boolean isCouldCreate()
    {   return this.isCreateAuthorized(); }

    /** tell if item creation is allowed<br>
     *  The list has to be configurable
     *  @param couldCreate true if item creation is allowed
     */
    protected void setCouldCreate(boolean couldCreate) throws PropertyVetoException
    {
	this.setCreateAuthorization(couldCreate);
    }

    /** indicates if creating item is allowed
     *  @return true if creating item is allowed
     */
    public boolean isCreateAuthorized()
    {   return couldCreate; }

    /** tell if item creation is allowed<br>
     *  The list has to be configurable
     *  @param couldCreate true if item creation is allowed
     */
    public void setCreateAuthorization(boolean couldCreate) throws PropertyVetoException
    {   if ( this.isConfigurable() )
        {   
	    if ( couldCreate != this.isCreateAuthorized() )
	    {
		this.fireVetoableChange(PROPERTY_CREATE_AUTHORIZATION, this.isCreateAuthorized(), couldCreate);

		this.checkReadOnlyProperty(PROPERTY_CREATE_AUTHORIZATION, this.isCreateAuthorized(), couldCreate);

		this.couldCreate = couldCreate;

		this.firePropertyChange(PROPERTY_CREATE_AUTHORIZATION, ! this.couldCreate, this.couldCreate);
	    }
        }
    }

    /** indicates if removing item is allowed
     *  @return true if removing item is allowed
     */
    protected boolean isCouldRemove()
    {   return this.isRemoveAuthorized(); }

    /** tell if removing item is allowed<br>
     *  The list has to be configurable
     *  @param couldRemove true if removing item is allowed
     */
    protected void setCouldRemove(boolean couldRemove) throws PropertyVetoException
    {
	this.setRemoveAuthorization(couldRemove);
    }

    /** indicates if removing item is allowed
     *  @return true if removing item is allowed
     */
    public boolean isRemoveAuthorized()
    {   return couldRemove; }

    /** tell if removing item is allowed<br>
     *  The list has to be configurable
     *  @param couldRemove true if removing item is allowed
     */
    public void setRemoveAuthorization(boolean couldRemove) throws PropertyVetoException
    {   if ( this.isConfigurable() )
        {   
	    if ( couldRemove != this.isRemoveAuthorized() )
	    {
		this.fireVetoableChange(PROPERTY_REMOVE_AUTHORIZATION, this.isRemoveAuthorized(), couldRemove);

		this.checkReadOnlyProperty(PROPERTY_REMOVE_AUTHORIZATION, this.isRemoveAuthorized(), couldRemove);

		this.couldRemove = couldRemove;

		this.firePropertyChange(PROPERTY_REMOVE_AUTHORIZATION, ! this.couldRemove, this.couldRemove);
	    }
        }
    }

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {   StringBuffer value = new StringBuffer();
        if ( this.collection != null )
        {   Iterator it = this.iterator();
            while(it.hasNext())
            {   value.append( ((SibType)it.next()).valueAsString());
                if ( it.hasNext() )
                    value.append(" ,");
            }
        }
        return value.toString();
    }
    
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   return "   allowed items : " + this.getAllowedClass().getName() + "<br>" +
               "   could edit    ? " + this.isEditAuthorized() + "<br>" + 
               "   could create  ? " + this.isCreateAuthorized() + "<br>" + 
               "   could remove  ? " + this.isRemoveAuthorized() + "<br>" + 
               super.getHtmlContent();
    }
    
    public Object clone()
    {   return this.clone(true, true); }
    
    /** clone a SibCollection
     *  @param addItems true to indicate that all sub items of this list should be added to the new collection
     *  @param cloneItems true to indicate that if addItems is true, then, the content of the new
     *      collection will be clones of the items contained by the current list
     */
    public Object clone(boolean addItems, boolean cloneItems)
    {   SibCollection other = (SibCollection)super.clone();
        
        other.setAllowedClass(this.getAllowedClass());
        
        try
        {   other.setEditAuthorization(this.isEditAuthorized()); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        try
        {   other.setCreateAuthorization(this.isCreateAuthorized()); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        try
        {   other.setRemoveAuthorization(this.isRemoveAuthorized()); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        /** clone and add all elements */
        if ( addItems )
        {
            Iterator it = this.iterator();
            Object current = null;
            while(it.hasNext())
            {   current = it.next();
                if ( current != null )
                {   if ( current instanceof SibType )
                    {   
                        if ( cloneItems )
                        {   other.add(((SibType)current).clone()); }
                        else
                        {   other.add(current); }
                    }
                }
            }
        }
        
        return other;
    }
}
