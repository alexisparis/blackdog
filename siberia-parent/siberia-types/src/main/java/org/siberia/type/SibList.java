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

import java.util.*;
import org.apache.log4j.Logger;
import org.siberia.SiberiaTypesPlugin;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.event.ContentChangeEvent;

/**
 *
 *  Class representing a list of SibType<br>
 *  This class is configurable and if it is configurable, then, the meta flags can be modified
 *
 *  @author alexis
 */
@Bean(  name="SibList",
        internationalizationRef="org.siberia.rc.i18n.type.SibList",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibList extends SibCollection implements List
{   
    /** logger */
    private transient Logger logger = Logger.getLogger(SibList.class);
    
    /** preferred capacity of the list
     *	<= 0 means undefined
     */
    private int preferredCapacity = 0;
    
    /** create a new instance of SibList that could contains SibType<br>
     *  Items can be edited, added and removed.
     */
    public SibList()
    {   this(0); }
    
    /** create a new instance of SibList that could contains SibType<br>
     *  Items can be edited, added and removed.
     *	@param preferredCapacity an integer representing the preferred capacity of the list
     */
    public SibList(int preferredCapacity)
    {   super();
	
	this.setPreferredCapacity(preferredCapacity);
    }
    
    /** set the preferred list capacity
     *	@param preferredCapacity an integer representing the preferred capacity of the list
     */
    public void setPreferredCapacity(int preferredCapacity)
    {
	this.preferredCapacity = preferredCapacity;
	
	Collection c = this.collection;
	if ( c instanceof ArrayList )
	{
	    ((ArrayList)c).ensureCapacity(this.preferredCapacity);
	}
    }
    
    /** initialize the collection */
    protected ArrayList initCollection()
    {   
	ArrayList list = null;
	
	if ( this.preferredCapacity > 0 )
	{
	    list = new ArrayList(this.preferredCapacity);
	}
	else
	{
	    list = new ArrayList();
	}
	
	return list;
    }
    
    
    /* #########################################################################
     * ####################### LIST IMPLEMENTATION  ############################
     * ######################################################################### */

    /** add an item at a specified position
     *  @param position an integer
     *  @param object the object to add
     */
    public void add(int position, Object object)
    {
	this.add(position, object, true);
    }

    /** add an item at a specified position
     *  @param position an integer
     *  @param object the object to add
     *	@param fireContentChanged true to fire a ContentChangedEvent if the item was successfully added
     */
    protected void add(int position, Object object, boolean fireContentChanged)
    {
	this.add(position, object, fireContentChanged, this.isContentItemAsChild());
    }

    /** add an item at a specified position
     *  @param position an integer
     *  @param object the object to add
     *	@param fireContentChanged true to fire a ContentChangedEvent if the item was successfully added
     *	@param addItemAsChild true to add this item as a child
     */
    protected boolean add(int position, Object object, boolean fireContentChanged, boolean addItemAsChild)
    {   
	boolean result = false;
	
        if( this.itemAllowed(object) && object instanceof SibType )
        {   SibType item = (SibType)object;
	    
            if( this.collection == null )
	    {
                this.collection = this.initCollection();
	    }
	    
	    int sizeBefore = this.collection.size();
	    
            ((List)this.collection).add(position, item);
	    
	    if ( sizeBefore != this.collection.size() )
	    {
		result = true;
		
		if ( fireContentChanged )
		{
		    this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CONTENT, ContentChangeEvent.ADD, position, item));
		}

		if ( addItemAsChild )
		{
		    this.addChildElement(item);
		    item.setParent(this);
		}
	    }
        }
	
	return result;
    }
    
    /** add a new element called from addAll
     *  @param index
     *  @param o the element to add
     *  @return true if the collection was modified
     */
    protected boolean addCommingFromAddAll(int index, Object o)
    {
	return this.add(index, o, false, false);
    }
    
    /** add a collection
     *  @param index
     *  @param c a collection to add
     *  @return true if the collection was modified
     */
    public boolean addAll(int index, Collection c)
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
	    
	    Integer firstAddIndex = index;
	    
	    int currentIndex = index;
	    
	    while(it.hasNext())
	    {
		Object o = it.next();
		
		if ( o instanceof SibType && this.addCommingFromAddAll(currentIndex, o) )
		{
		    itemsAdded.add( (SibType)o );
		    currentIndex ++;
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
        }
	
        return modified;
    }
    
    /** return an index related to the given item<br>
     *	this method is called from remvoe methods to complete the information of ContentChangeEvent
     *	@param item a SibType
     *	@return the associated index or -1 if we do not know
     */
    @Override
    protected int getIndexRelatedTo(SibType item)
    {
	return this.indexOfByReference(item);
    }
    
    /** return true if the collection support items indexing
     *	@return true if the collection support items indexing
     */
    @Override
    protected boolean isSupportingIndexation()
    {
	return true;
    }
    
    /** replace the element at position index and return it
     *  @param index
     *  @param object the new object to add to the list
     *  @return the object that were placed at position index before replace operation
     */
    public Object set(int index, Object element)
    {   Object object = null;
        if ( this.collection != null && this.collection.size() > 0 )
        {   
	    object = this.remove(index);
            
            this.add(index, element);
        }
        return object;
    }
    
    /** return the last index of the given element
     *  @param o an object
     *  @return the last index of the given element
     */
    public int lastIndexOf(Object o)
    {   int position = -1;
        if ( this.collection != null )
            position = ((List)this.collection).lastIndexOf(o);
        return position;
    }
     
    /** return a list iterator over the list
     *  @return an instance of ListIterator
     */
    public ListIterator listIterator()
    {   if ( this.collection != null )
            return ((List)this.collection).listIterator();
        return Collections.EMPTY_LIST.listIterator();
    }
    
    /** return a list iterator over the list
     *  @param index
     *  @return an instance of ListIterator
     */
    public ListIterator listIterator(int index)
    {   if ( this.collection != null )
            return ((List)this.collection).listIterator(index);
        return Collections.EMPTY_LIST.listIterator(index);
    }
                        
    /** return a sub list of this list
     *  @param fromIndex start index
     *  @param toIndex end index
     *  @return a list
     */
    public List subList(int fromIndex, int toIndex)
    {   if ( this.collection != null )
            return ((List)this.collection).subList(fromIndex, toIndex);
        return Collections.EMPTY_LIST.subList(fromIndex, toIndex);
    }
    
    /** remove the element at the given index
     *  @param index an integer representing an index in the list
     *  @return the removed element
     */
    public Object remove(int index)
    {   Object object = null;
        if ( this.collection != null )
        {   AbstractSibType item = (AbstractSibType)((List)this.collection).get(index);
            if ( item.isRemovable() )
            {
                object = ((List)this.collection).remove(index);
                if ( object instanceof SibType )
		{
		    if ( this.isContentItemAsChild() )
		    {
			this.removeChildElement( (SibType)object );
		    }
		    
                    this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CONTENT, ContentChangeEvent.REMOVE, index, (SibType)object));
		}
            }
        }
        return object;
    }
    
    /** return the object corresponding to the given index
     *  @param index an index
     *  @return the object located at the given index
     */
    public Object get(int index)
    {   if ( this.collection == null )
            return null;
        return ((List)this.collection).get(index);
    }
    
    /** return the index of the given element
     *  @param obj an object
     *  @return the index of the element in the list or -1 if not found
     */
    public int indexOf(Object obj)
    {   if ( this.collection == null )
            return -1;
        return ((List)this.collection).indexOf(obj);
    }
    
    /** return the index of the given element by reference<br>
     *	that means that if the list contains two items a & b that are equals respectively at index
     *	n & m, indexOfByReference(b) will not returns n but m<br>
     *  @param obj an object
     *  @return the index of the element in the list or -1 if not found
     */
    public int indexOfByReference(Object obj)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling indexOfByReference(" + obj + ") on the list " + this + " which size is " + this.size());
	    logger.debug("id hashcode of '" + obj + "'=" + System.identityHashCode(obj));
	    logger.debug(obj + " class : " + (obj == null ? null : obj.getClass()));
	}
	
	int index = -1;
	
	for(int i = 0; i < this.size(); i++)
	{
	    Object item = this.get(i);
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("indexOfByReference consider item at " + i + " which is " + item + "(id hashcode=" + System.identityHashCode(item) + ")");
		logger.debug("item at " + i + " is of class " + (item == null ? null : item.getClass()));
	    }
	    
	    boolean sameRef = item == obj;
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("same reference ? " + sameRef);
	    }
	    
	    if ( sameRef )
	    {
		index = i;
		break;
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("indexOfByReference(" + obj + ") return " + index);
	}
	
	return index;
    }
    
    /* #########################################################################
     * #################### END OF LIST IMPLEMENTATION  ######################## 
     * ######################################################################### */
}
