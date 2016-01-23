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
package org.siberia.type.attic;

import java.util.*;
import org.siberia.type.AbstractSibType;
import org.siberia.type.SibType;
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
public class SibList extends SibCollection<ArrayList<SibType>> implements List
{   
    /** create a new instance of SibList that could contains SibType<br>
     *  Items can be edited, added and removed.
     */
    public SibList()
    {   super(); }
    
    /** initialize the collection */
    protected ArrayList<SibType> initCollection()
    {   return new ArrayList<SibType>(); }
    
    
    /* #########################################################################
     * ####################### LIST IMPLEMENTATION  ############################
     * ######################################################################### */
    
    /** add a collection
     *  @param index
     *  @param c a collection to add
     *  @return true if the collection was modified
     */
    public boolean addAll(int index, Collection c)
    {   if ( c != null )
        {   if ( this.collection == null )
                this.collection = this.initCollection();
            boolean modified = false;
            int i = index;
            for(Iterator it = c.iterator(); it.hasNext();)
            {   Object object = it.next();
                if ( this.itemAllowed(object) )
                {   this.add(i++, object); }
            }   
            /* fire event */
            int[] positions = new int[c.size()];
            for(int j = 0; j < c.size(); j++)
                positions[j] = j + index;
            this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CONTENT, ContentChangeEvent.ADD, positions, 
                                (AbstractSibType[])c.toArray(new AbstractSibType[]{})));
            
            return modified;
        }
        return false;
    }
    
    /** replace the element at position index and return it
     *  @param index
     *  @param object the new object to add to the list
     *  @return the object that were placed at position index before replace operation
     */
    public Object set(int index, Object element)
    {   Object object = null;
        if ( this.collection != null )
        {   object = this.remove(index);
            
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
            object = this.collection.remove(index);
            if ( object instanceof AbstractSibType )
                this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CONTENT, ContentChangeEvent.REMOVE, index, (AbstractSibType)object));
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

    /** add an item at a specified position
     *  @param position an integer
     *  @param object the object to add
     */
    public void add(int position, Object object)
    {   
        if( this.itemAllowed(object) && object instanceof SibType )
        {   SibType item = (SibType)object;
            this.addChildElement(item);
            item.setParent(this);
            if( this.collection == null )
                this.collection = this.initCollection();
            ((List)this.collection).add(position, item);

            this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CONTENT, ContentChangeEvent.ADD, position, item));
        }
    }
    
    /* #########################################################################
     * #################### END OF LIST IMPLEMENTATION  ######################## 
     * ######################################################################### */
}
