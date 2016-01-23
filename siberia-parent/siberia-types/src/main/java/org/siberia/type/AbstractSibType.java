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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.swing.event.EventListenerList;
import org.siberia.base.LangUtilities;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.UnmergeableProperty;
import org.apache.log4j.Logger;
import org.siberia.type.annotation.bean.BeanProperty;
import org.siberia.type.annotation.bean.ConfigurationItem;
import org.siberia.type.event.ContentChangeEvent;
import org.siberia.type.event.ContentClearedEvent;
import org.siberia.type.event.HierarchicalPropertyChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeListener;


/**
 * base class for all object displayed in the application
 *
 *  @author alexis
 */
@Bean(  name="AbstractSibType",
        internationalizationRef="org.siberia.rc.i18n.type.AbstractSibType",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public abstract class AbstractSibType implements SibType
{   
    /** logger */
    private static      Logger                      logger               = Logger.getLogger(AbstractSibType.class);
    
    /** list of listener */
    private   transient EventListenerList           listeners            = null;
    
    /** propertyChange support */
    private   transient PropertyChangeSupport       propertySupport      = new PropertyChangeSupport(this);
    
    /** vetoableChange support */
    private   transient VetoableChangeSupport       vetoableSupport      = new VetoableChangeSupport(this);
    
    /* parent of the instance */
    private   transient SibType                     parent               = null;
    
    /* String name of the instance */
    @BeanProperty(name=SibType.PROPERTY_NAME,
                  internationalizationRef="org.siberia.rc.i18n.property.AbstractSibType_name",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setName",
                  writeMethodParametersClass={String.class},
                  readMethodName="getName",
                  readMethodParametersClass={}
                 )
    @UnmergeableProperty
    private   String                      name                 = null;
    
    /* child elements
     *	this list can contains different objects which are equals but are not the same objects
     */
    protected ArrayList<SibType>         children             = null;
    
    /** indicates if the name could be modified */
    @BeanProperty(name=SibType.PROPERTY_NAME_COULD_CHANGE,
                  internationalizationRef="org.siberia.rc.i18n.property.AbstractSibType_nameCouldChange",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setNameCouldChange",
                  writeMethodParametersClass={boolean.class},
                  readMethodName="nameCouldChange",
                  readMethodParametersClass={}
                 )
    @ConfigurationItem
    private   boolean                     nameCouldChange      = true;
    
    /** read only */
    @BeanProperty(name=SibType.PROPERTY_READ_ONLY,
                  internationalizationRef="org.siberia.rc.i18n.property.AbstractSibType_readOnly",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setReadOnly",
                  writeMethodParametersClass={boolean.class},
                  readMethodName="isReadOnly",
                  readMethodParametersClass={}
                 )
    @ConfigurationItem
    private   boolean                     readOnly             = false;
    
    /** is moveable */
    
    /** read only */
    @BeanProperty(name=SibType.PROPERTY_MOVEABLE,
                  internationalizationRef="org.siberia.rc.i18n.property.AbstractSibType_moveable",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setMoveable",
                  writeMethodParametersClass={boolean.class},
                  readMethodName="canBeMoved",
                  readMethodParametersClass={}
                 )
    @ConfigurationItem
    private   boolean                     moveable             = true;
    
    /** id */
    @BeanProperty(name=SibType.PROPERTY_ID,
                  internationalizationRef="org.siberia.rc.i18n.property.AbstractSibType_id",
                  expert=true,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setId",
                  writeMethodParametersClass={long.class},
                  readMethodName="getId",
                  readMethodParametersClass={}
                 )
    @ConfigurationItem
    @UnmergeableProperty
    private   long                        id                   = -1;
    
    /** read only */
    @BeanProperty(name=SibType.PROPERTY_REMOVABLE,
                  internationalizationRef="org.siberia.rc.i18n.property.AbstractSibType_removable",
                  expert=true,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setRemovable",
                  writeMethodParametersClass={boolean.class},
                  readMethodName="isRemovable",
                  readMethodParametersClass={}
                 )
    @ConfigurationItem
    private   boolean                     removable           = true;
    
    /** create a new SibType without parent */
    public AbstractSibType()
    {   }
    
    /** tell if the type is considered as leaf
     *  @return true if the element must be considered as leaf
     */
    public boolean isLeaf()
    {   
	boolean containChild = false;
        if ( this.children != null && this.children.size() > 0 )
	{
            containChild = true;
	}
        return ! containChild;
    }
    
    /** return true if the type is read only
     *  @return true if the type is read only
     */
    public boolean isReadOnly()
    {   return this.readOnly; }
    
    /** set if the type is read only
     *  @param readOnly true if the type is read only
     */
    public void setReadOnly(boolean readOnly)
    {   
        boolean oldValue = this.isReadOnly();
        
        this.readOnly = readOnly;
        
        this.firePropertyChange(PROPERTY_READ_ONLY, oldValue, this.isReadOnly());
    }
    
    /** return true if the type can be removed
     *  @return true if the type can be removed
     */
    public boolean isRemovable()
    {   return this.removable; }
    
    /** set if the type can be removed
     *  @param removable true if the type can be removed
     */
    public void setRemovable(boolean removable) throws PropertyVetoException
    {
        if ( removable != this.isRemovable() )
        {   
            this.fireVetoableChange(PROPERTY_REMOVABLE, this.isRemovable(), removable);

            PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, PROPERTY_REMOVABLE, ! removable, removable);

            this.checkReadOnlyProperty(PROPERTY_REMOVABLE, ! removable, removable);

            this.removable = removable;

            this.firePropertyChange(changeEvent);
        }
    }
    
    /** method that throw a PropertyVetoException if we try to change the value of the property when 
     *  the instance is read-only
     *  @param propertyName the name of the property
     *  @param oldValue the old value of the property
     *  @param newValue the new value of the property
     */
    protected void checkReadOnlyProperty(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException
    {   this.checkReadOnlyProperty(new PropertyChangeEvent(this, propertyName, oldValue, newValue)); }
    
    /** method that throw a PropertyVetoException if we try to change the value of the property when 
     *  the instance is read-only
     *  @param event a PropertyChangeEvent
     */
    protected void checkReadOnlyProperty(PropertyChangeEvent event) throws PropertyVetoException
    {   if ( event != null && this.isReadOnly() )
        {   boolean valueChanged = false;
            if ( event.getOldValue() == null )
            {   if ( event.getNewValue() != null )
                    valueChanged = true;
            }
            else
            {   if ( event.getNewValue() == null )
                    valueChanged = true;
                else
                {   valueChanged = ! event.getOldValue().equals(event.getNewValue()); }
            }
            
            if ( valueChanged )
            {   throw new PropertyVetoException("instance is read-only", event); }
        }
    }
    
    /** return the id of the object
     *  @return a long
     */
    public long getId()
    {   return this.id; }
    
    /** initialize the id of the object
     *  @param id a long
     */
    public void setId(long id) throws PropertyVetoException
    {   if ( id != this.getId() )
        {   
            this.fireVetoableChange(PROPERTY_ID, this.getId(), id);

            long oldId = this.getId();

            PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, PROPERTY_ID, oldId, id);

            this.checkReadOnlyProperty(PROPERTY_ID, oldId, id);

            this.id = id;

            this.firePropertyChange(changeEvent);
        }
    }

    /*  is the object valid ?<br>
     *  to be overwritten
     *  @return true if the object is valid
     */
    public boolean isValid()
    {   return true; }

    /* display instance caracteristics
     *  @param maxLength the maximum length for the string representation or -1 if unlimited
     *  @return a String representation of the element
     */
    public String toString(int maxLength)
    {   StringBuffer toReturn = new StringBuffer();
        
        if ( this.getName() != null )
        {   toReturn.append(this.getName()); }
        
//        toReturn.append(" : [");
//        
//        if ( this.children != null )
//        {
//            synchronized(this.children)
//            {
//                for(int i = 0; i < this.children.size(); i++)
//                {   SibType current = this.children.get(i);
//                    if ( current != null )
//                    {
//                        toReturn.append( current.toString() );
//                        
//                        if ( toReturn.length() > maxLength && maxLength > 0 )
//                        {   break; }
//                        
//                        if ( i < this.children.size() - 1 )
//                        {   toReturn.append(", "); }
//                    }
//                }
//            }
//        }
//        
//        toReturn.append("]");
        
        if (maxLength != -1) return toReturn.substring(0, maxLength);
        return toReturn.toString();
    }
    
    public String toString()
    {   return this.toString(-1); }

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {   return this.toString(); }
    
    public Object clone()
    {   SibType newType = null;
        try
        {   newType = this.getClass().newInstance(); }
        catch(Exception e)
        {   e.printStackTrace();
            return null;
        }
        
        try
        {   newType.setNameCouldChange(this.nameCouldChange()); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        try
        {   newType.setMoveable(this.canBeMoved()); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        throw new UnsupportedOperationException("operation non complete : child non clon�");
        
//        return newType;
    }
    
    public boolean equals(Object t)
    {   
        boolean result = false;
        
        if ( t != null )
        {   if ( t instanceof SibType )
            {   SibType other = (SibType)t;
                
                if ( this.getClass().equals(other.getClass()) )
                {                   
                    result = LangUtilities.equals(this.getName(), other.getName());
                }
            }
        }
        
        return result;
    }
    
    public int hashCode()
    {   if ( this.getName() == null ) return 0;
        else                          return this.getName().hashCode();
    }
    
    /** return the system hashcode of the item
     *	@return an integer
     */
    public int getIdentityHashCode()
    {
	return System.identityHashCode(this);
    }
    
    /* ############################################################
     * ################## Hierarchic information ##################
     * ############################################################ */

    /** return the parent of the object
     *  @return the parent of the SibType
     */
    public final SibType getParent()
    {   return this.parent; }
    
    /** return the path to this instance
     *  @return an array of SibType ( the last elements are the direct parent of this instance and this) or
     *          null if it does not have a father
     */
    public final SibType[] getPath()
    {   List<SibType> list = new ArrayList<SibType>();
        SibType current = this;
        list.add(current);
        while(current.getParent() != null)
        {   list.add(0, current.getParent());
            current = current.getParent();
        }
        return (SibType[])list.toArray(new SibType[]{});
    }
    
    /** return the number of children for this object
     *  @return the number of children for this object
     */
    public int getChildrenCount()
    {   return (this.children == null ? 0 : this.children.size()); }
    
    /* return true if this instance contains the given instance as child
     *  @param child an SibType instance
     * @return true if this instance contains the given instance as child
     */
    public boolean containsChild(SibType child)
    {   boolean found = false;
        if ( this.children != null )
        {   found = this.children.contains(child); }
        return found;
    }
    
    /** return the child placed at the given position or null if not fund
     *  @param index
     *  @return an instanceof of SibType
     */
    public final SibType getChildAt(int index)
    {   SibType elt = null;
        if ( this.children != null )
        {   if ( index >= 0 && index < this.children.size() )
                elt = this.children.get(index);
        }
        return elt;
    }
    
    /** method that return the level of this instance in a hierarchy.<br>
     *  example : <br>
     *      if we have three instances : a, b, c.
     *          a is the parent of b, b is the parent of c and a has no parent.
     *  then calling this method on a will return 0.<br>
     *  then calling this method on b will return 1.<br>
     *  then calling this method on c will return 2.<br>
     *  @return the level of the item in its hierarchy ( 0 if the item has no parent )
     */
    public int getLevel()
    {   int level = 0;
        if ( this.getParent() != null )
        {   level += this.getParent().getLevel() + 1; }
        
        return level;
    }
    
    /** returns the index where the item reference appears in the children list
     *	@return the index where the item reference appears in the children list
     *		or -1 if it does not appears
     */
    public int indexOfChildReference(SibType item)
    {
	int result = -1;
	
	if ( this.children != null )
	{
	    for(int i = 0; i < this.children.size(); i++)
	    {
		if ( item == this.children.get(i) )
		{
		    result = i;
		    break;
		}
	    }
	}
	
	return result;
    }
    
    /** initialize the parent of the object
     *  @param parent the parent of the SibType
     */
    public final void setParent(SibType parent)
    {
	this.setParent(parent, true, true);
    }
    
    /** initialize the parent of the object
     *  @param parent the parent of the SibType
     *	@param removeFromOldParent true to indicate that this should be removed from the child list of its old parent
     *	@param addToNewParent true to indicate that this should be added to the child list of its new parent
     */
    public final void setParent(SibType parent, boolean removeFromOldParent, boolean addToNewParent)
    {   
        if ( parent != this.getParent() )
        {
            if ( this.parent != null && removeFromOldParent )
            {   this.parent.removeChildElement(this); }
            
            this.parent = parent;
            
            if ( this.parent != null && addToNewParent )
            {   this.parent.addChildElement(this); }
        }
    }
    
    /*  add child element.
     *  @param item an instance of SibType
     */
    public final void addChildElement(SibType item)
    {
	this.addChildElement(item, true);
    }
    
    /*  add child element.
     *  @param item an instance of SibType
     *	@param fireEvent true to fire a ContentChangeEvent if adding type succeed
     *
     *	@return the position where the items as been added or negative if not added
     */
    protected int addChildElement(SibType item, boolean fireEvent)
    {   
	int position = -1;
	
        if ( this.children == null )
        {   this.children = new ArrayList<SibType>(); }
        
        if ( item != null )
        {
	    /** if we have two items a & b that are equals
	     *	it is allowed to add them as content of a list
	     *	if we test contains, then when adding the second item, the parent of b will not be set
	     *	and therefore, the HierarchicalPropertyChange system will not work and the list including
	     *	b will never be warned when a modification occur on b, therefore, we allow 
	     *	to have a & b as child of an AbstractSibType
	     */
            if ( this.indexOfChildReference(item) < 0 && this.children.add(item) )
            {   
		position = this.children.size() - 1;
		
                item.setParent(this, true, false);
                
		if ( fireEvent )
		{
		    this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CHILDREN, 
								   ContentChangeEvent.ADD, this.getChildrenCount() - 1, item));
		}
            }
        }
	
	return position;
    }
    
    /*  add child elements.
     *  @param items a list of items
     */
    protected void addChildElements(List<SibType> items)
    {
	if ( items != null && items.size() > 0 )
	{
	    ListIterator<SibType> it = items.listIterator();
	    
	    Integer       firstPosition = null;
	    List<SibType> addedItems = new ArrayList<SibType>(items.size());
	    
	    while(it.hasNext())
	    {
		SibType current = it.next();
		
		int position = this.addChildElement(current, false);
		
		if ( position > -1 )
		{
		    if ( firstPosition == null )
		    {
			firstPosition = position;
		    }
		    addedItems.add(current);
		}
	    }
	    
	    int[] positions = new int[addedItems.size()];
	    
	    for(int i = 0; i < positions.length; i++)
	    {
		positions[i] = firstPosition + i;
	    }
	    
	    this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CHILDREN,
							   ContentChangeEvent.ADD,
							   positions, (SibType[])addedItems.toArray(new SibType[addedItems.size()])));
	}
    }
    
    /** remove an element from the children list
     *	@param item the child to remove
     */
    public final void removeChildElement(SibType item)
    {
	this.removeChildElement(item, true);
    }
    
    /** remove an element from the children list
     *	@param item the child to remove
     *	@param fireEvent true to fire a ContentChangeEvent if adding type succeed
     *
     *	@return the position where the items as been added or negative if not added
     */
    public final int removeChildElement(SibType item, boolean fireEvent)
    {   
	int position = -1;
	
	if ( this.children != null && item != null )
        {   
            int index = this.indexOfChildReference(item);
            if ( index >= 0 && index < this.children.size() && this.children.remove(index) != null )
            {   
		position = index;
		
                item.setParent(null, false, true);
                
		if ( fireEvent )
		{
		    this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CHILDREN, 
								   ContentChangeEvent.REMOVE, index, item));
		}
            }
        }
	
	return position;
    }
    
    /*  remove child elements.
     *  @param items a collection of SibType
     */
    protected void removeChildElements(Collection<SibType> items)
    {
	if ( items != null && items.size() > 0 && this.children != null && this.children.size() > 0 )
	{
	    Map<Integer, SibType> map = new HashMap<Integer, SibType>(items.size());
	    
	    Iterator<SibType> it = items.iterator();
	    while(it.hasNext())
	    {
		SibType currentItem = it.next();
		
		int position = this.indexOfChildReference(currentItem);
		
		if ( position >= 0 && position < this.children.size() )
		{
		    map.put(position, currentItem);
		}
	    }
	    
	    List<Integer> indexList = new ArrayList<Integer>(map.keySet());
	    
	    Collections.sort(indexList);
	    
	    int currentIndexInList = indexList.size() - 1;
	    while(currentIndexInList >= 0)
	    {
		int currentPosition = indexList.get(currentIndexInList);
		
		SibType currentItem = map.get(currentPosition);
		
		int position = this.removeChildElement(currentItem, false);

		if ( position < 0 )
		{
		    // could be ingored since we delete the current entry in indexList
//		    /* the item has not been removed */
//		    map.remove(currentPosition);
		    
		    /* remove current item in indexList and continue */
		    indexList.remove(currentIndexInList);
		}
		
		currentIndexInList --;
	    }
	    
	    if ( indexList.size() > 0 ) // efficient removals
	    {
		int indexListSize = indexList.size();
		
		if ( this.children == null || this.children.size() == 0 )
		{
		    /** optimization --> avoid to create two arrays */
		    this.firePropertyChange(new ContentClearedEvent(this, PROPERTY_CHILDREN, 
								     (SibType[])new ArrayList<SibType>(map.values()).toArray(new SibType[map.size()]) ));
		}
		else
		{
		    /* finally create the two arrays */
		    int[]     positions    = new int[indexListSize];
		    SibType[] removedItems = new SibType[indexListSize];

		    for(int i = 0; i < indexListSize; i++)
		    {
			int pos = indexList.get(indexListSize - i - 1);
			positions[i] = pos;
			removedItems[i] = map.get(pos);
		    }

		    indexList.clear();

		    this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CHILDREN,
								   ContentChangeEvent.REMOVE,
								   positions,
								   removedItems));
		}
	    }
	    
	    map.clear();
	}
    }
    
    /* insert child element
     *  @param index the index where to insert the new element
     *  @param item an instance of SibType
     */
    public final void insertChildElement(int index, SibType item)
    {   
        if ( item != null )
        {
            if ( this.children == null )
            {   this.children = new ArrayList<SibType>(); }

            if ( index >= 0 && index < this.children.size() )
            {   
		int refIndex = this.indexOfChildReference(item);
		
		if ( refIndex != index )
		{
		    /** delete if it already exists */
		    if ( refIndex >= 0 )
		    {   this.removeChildElement(item); }

		    this.children.add(index, item);

		    item.setParent(this, true, false);

		    this.firePropertyChange(new ContentChangeEvent(this, PROPERTY_CHILDREN, 
								   ContentChangeEvent.ADD, index, item));
		}
            }
        }
    }
    
    public Iterator<SibType> children()
    {   return (this.children == null ? Collections.EMPTY_LIST.iterator() : this.children.iterator()); }
    
    /** return true if the instance has object for ancestor
     *  @return true if the instance has object for ancestor
     **/
    public final boolean hasForAncestor(SibType object)
    {   if (this.getParent() == null) return false;
        
        SibType current = this.getParent();
        
        while(current != null)
        {   if(current == object) return true;
            current = current.getParent();
        }
        return false;
    }
    
    /** return true if the instance has object for ancestor
     *  @return true if the instance has object for ancestor
     **/
    public final boolean hasForAncestor(Class classObject)
    {   if (this.getParent() == null) return false;
        
        SibType current = this.getParent();
        
        while(current != null)
        {   if(classObject.isAssignableFrom(current.getClass())) return true;
            current = current.getParent();
        }
        return false;
    }
    
    /** return the first parent instance of class classObject
     *  @param classObject a Class
     *  @return the first parent instance of class classObject
     **/
    public final SibType getFirstAncestorOfType(Class classObject)
    {   if (this.getParent() == null) return null;
        
        SibType current = this.getParent();
        
        while(current != null)
        {   if(classObject.isAssignableFrom(current.getClass())) return current;
            current = current.getParent();
        }
        return null;
    }
    
    /** return the position of the given element in the children list of the current object
     *  @param child an Object
     *  @return the index in the list or -1 if not fund
     */
    public final int indexOfChild(Object child)
    {   return (this.children == null ? -1 : this.children.indexOf(child)); }
    
    /** return the index of this instance in its parent children container
     *  or -1 if it was not fund
     *  @return the index of this instance in its parent children container
     **/
    public final int getIndexInParent()
    {   
        if ( this.getParent() == null ) return -1;
        
        Iterator brothers = this.getParent().children();
        int index = 0;
        while(brothers.hasNext())
        {   if (brothers.next() == this) return index;
            index ++;
        }
        return -1;
    }
    
    /** return the String value of an owned element of class SibType
     *  @param code the name of an element
     *  @return the value as a String for that code
     */
    public final String getChildAsValue(String code)
    {   
        if ( code == null ) return null;
        
        if ( code.equals(PROPERTY_NAME) )
            return this.getName();
        else
        {   SibType concernedInstance = this.getChildNamed( code );
            if ( concernedInstance == null )
                return null;
            else
                return concernedInstance.valueAsString();
        }
    }
    
    /** return an existing child of the current SibType called 'name'
     *  @param name the name of an element
     *  @return an SibType named 'name'
     */
    public final SibType getChildNamed(String name)
    {   SibType current = null;
        SibType value   = null;
        for (Iterator<SibType> children = this.children(); children.hasNext(); )
        {   current = children.next();
            value   = current.getChildNamed(name);

            if ( value != null)
                return value;
        }
        return null;
    }
    
    /* #######################################################################
     * ################### HtmlPrintable implementation ######################
     * ####################################################################### */
    
    /** return an html representation for this object
     *  @return an html representation for this object
     */
    public final String toHtml()
    {   return "<html>" + this.getHtmlContentAndDesc() + "</html>"; }
    
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContentAndDesc()
    {   return "<b>" + this.getName() + "</b> <i><code>" +
               this.getClass().getName() + "</code></i> : <br>" +
               this.getHtmlContent();
    }
        
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   StringBuffer toReturn = new StringBuffer("<ul>");
        Iterator children = this.children();
        while(children.hasNext())
        {   toReturn.append( "<li>" + ((SibType)children.next()).getHtmlContentAndDesc() + "</li>"); }
        toReturn.append("</ul>");
        
        return toReturn.toString();
    }
    
    /* #######################################################################
     * ###################### Namable implementation #########################
     * ####################################################################### */

    public String getName()
    {   return this.name; }     
    
    public void setName(String value) throws PropertyVetoException
    {   
	boolean equals = false;
	
	if ( value == null )
	{
	    if ( this.getName() == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    equals = value.equals(this.getName());
	}
	
	if ( ! equals )
	{
	    this.fireVetoableChange(PROPERTY_NAME, this.name, value);

	    String tmpName = this.name;

	    PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, PROPERTY_NAME, tmpName, value);

	    /** if the name is could not be changed, then throw a PropertyVetoException */
	    if ( ! this.nameCouldChange() )
	    {   throw new PropertyVetoException("the name could not be changed", changeEvent); }

	    this.checkReadOnlyProperty(PROPERTY_NAME, tmpName, value);

	    this.name = value;

	    this.firePropertyChange(changeEvent);
	}
    }
    
    public boolean nameCouldChange()
    {   return this.nameCouldChange; }
    
    public boolean getNameCouldChange()
    {   return this.nameCouldChange(); }
    
    public void setNameCouldChange(boolean couldChange) throws PropertyVetoException
    {   
	if ( couldChange != this.nameCouldChange() )
	{
	    this.fireVetoableChange(PROPERTY_NAME_COULD_CHANGE, ! this.nameCouldChange(), this.nameCouldChange());

	    this.checkReadOnlyProperty(PROPERTY_NAME_COULD_CHANGE, this.nameCouldChange(), couldChange);

	    this.nameCouldChange = couldChange;

	    this.firePropertyChange(PROPERTY_NAME_COULD_CHANGE, ! couldChange, couldChange);
	}
    }
    
    /* #########################################################################
     * ######################## Moveable implementation ########################
     * ######################################################################### */
    
    public boolean canBeMoved()
    {   return this.moveable; }
    
    public boolean isMoveable()
    {   return this.canBeMoved(); }
    
    public void setMoveable(boolean moveable) throws PropertyVetoException
    {   
	if ( moveable != this.canBeMoved() ) 
	{
	    this.fireVetoableChange(PROPERTY_MOVEABLE, ! this.canBeMoved(), this.canBeMoved());

	    this.checkReadOnlyProperty(PROPERTY_MOVEABLE, this.canBeMoved(), moveable);

	    this.moveable = moveable;

	    this.firePropertyChange(PROPERTY_MOVEABLE, ! moveable, moveable);
	}
    }
    
    /* ############################################################
     * ################ Property change management ################
     * ############################################################ */
    
    /** indicates to all user that a property changed
     *  @param propertyName the name of the property
     *  @param oldValue the old value of the property
     *  @param newValue the new value of the property
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {   PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        this.firePropertyChange(event);
    }
    
    /** indicates to all user that a property changed
     *  @param e an instance of PropertyChangeEvent
     */
    public void firePropertyChange(PropertyChangeEvent e)
    {   
	if ( this.propertySupport != null )
	{
	    this.propertySupport.firePropertyChange(e);
	}
        
        /** warn HierarchicalPropertyChangeListeners */
        this.fireHierarchicalPropertyChangeEvent(new HierarchicalPropertyChangeEvent(e));
    }
    
    /** add a new listener to the change of the object
     *  @param listener an instance of PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {   
	if ( this.propertySupport == null )
	{
	    this.propertySupport = new PropertyChangeSupport(this);
	}
	this.propertySupport.addPropertyChangeListener(listener);
    }
    
    /** add a new listener to the change of the object
     *  @param propertyName the name of the property
     *  @param listener an instance of PropertyChangeListener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {   
	if ( this.propertySupport == null )
	{
	    this.propertySupport = new PropertyChangeSupport(this);
	}
	this.propertySupport.addPropertyChangeListener(propertyName, listener);
    }
    
    /** remove a listener
     *  @param listener an instance of PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {   
	if ( this.propertySupport == null )
	{
	    this.propertySupport = new PropertyChangeSupport(this);
	}
	this.propertySupport.removePropertyChangeListener(listener);
    }
    
    /** remove a listener
     *  @param propertyName the name of the property
     *  @param listener an instance of PropertyChangeListener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {   
	if ( this.propertySupport == null )
	{
	    this.propertySupport = new PropertyChangeSupport(this);
	}
	this.propertySupport.removePropertyChangeListener(propertyName, listener);
    }
    
    /** clear the list of propertyChangeListener for this object */
    public void clearPropertyChangeListenerList()
    {   this.propertySupport = new PropertyChangeSupport(this); }
    
    /* ############################################################
     * ################ Property change management ################
     * ############################################################ */

    /**
     * Report a vetoable property update to any registered listeners.  If
     * anyone vetos the change, then fire a new event reverting everyone to 
     * the old value and then rethrow the PropertyVetoException.
     * <p>
     * No event is fired if old and new are equal and non-null.
     *
     * @param propertyName  The programmatic name of the property
     *		that is about to change..
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     * @exception PropertyVetoException if the recipient wishes the property
     *              change to be rolled back.
     */
    public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException
    {   
	if ( this.vetoableSupport != null )
	{
	    this.vetoableSupport.fireVetoableChange(propertyName, oldValue, newValue);
	}
    }

    /**
     * Fire a vetoable property update to any registered listeners.  If
     * anyone vetos the change, then fire a new event reverting everyone to 
     * the old value and then rethrow the PropertyVetoException.
     * <p>
     * No event is fired if old and new are equal and non-null.
     *
     * @param evt  The PropertyChangeEvent to be fired.
     * @exception PropertyVetoException if the recipient wishes the property
     *              change to be rolled back.
     */
    public void fireVetoableChange(PropertyChangeEvent evt) throws PropertyVetoException
    {   
	if ( this.vetoableSupport == null )
	{
	    this.vetoableSupport = new VetoableChangeSupport(this);
	}
	this.vetoableSupport.fireVetoableChange(evt);
    }

    /**
     * Add a VetoableListener to the listener list.
     * The listener is registered for all properties.
     * The same listener object may be added more than once, and will be called
     * as many times as it is added.
     * If <code>listener</code> is null, no exception is thrown and no action
     * is taken.
     *
     * @param listener  The VetoableChangeListener to be added
     */

    public void addVetoableChangeListener(VetoableChangeListener listener)
    {   
	if ( this.vetoableSupport == null )
	{
	    this.vetoableSupport = new VetoableChangeSupport(this);
	}
	this.vetoableSupport.addVetoableChangeListener(listener);
    }

    /**
     * Remove a VetoableChangeListener from the listener list.
     * This removes a VetoableChangeListener that was registered
     * for all properties.
     * If <code>listener</code> was added more than once to the same event
     * source, it will be notified one less time after being removed.
     * If <code>listener</code> is null, or was never added, no exception is
     * thrown and no action is taken.
     *
     * @param listener  The VetoableChangeListener to be removed
     */
    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {   
	if ( this.vetoableSupport == null )
	{
	    this.vetoableSupport = new VetoableChangeSupport(this);
	}
	this.vetoableSupport.removeVetoableChangeListener(listener);
    }

    /**
     * Add a VetoableChangeListener for a specific property.  The listener
     * will be invoked only when a call on fireVetoableChange names that
     * specific property.
     * The same listener object may be added more than once.  For each
     * property,  the listener will be invoked the number of times it was added
     * for that property.
     * If <code>propertyName</code> or <code>listener</code> is null, no
     * exception is thrown and no action is taken.
     *
     * @param propertyName  The name of the property to listen on.
     * @param listener  The VetoableChangeListener to be added
     */

    public void addVetoableChangeListener(String propertyName, VetoableChangeListener listener)
    {   
	if ( this.vetoableSupport == null )
	{
	    this.vetoableSupport = new VetoableChangeSupport(this);
	}
	this.vetoableSupport.addVetoableChangeListener(propertyName, listener);
    }

    /**
     * Remove a VetoableChangeListener for a specific property.
     * If <code>listener</code> was added more than once to the same event
     * source for the specified property, it will be notified one less time
     * after being removed.
     * If <code>propertyName</code> is null, no exception is thrown and no
     * action is taken.
     * If <code>listener</code> is null, or was never added for the specified
     * property, no exception is thrown and no action is taken.
     *
     * @param propertyName  The name of the property that was listened on.
     * @param listener  The VetoableChangeListener to be removed
     */

    public void removeVetoableChangeListener(String propertyName, VetoableChangeListener listener)
    {   
	if ( this.vetoableSupport == null )
	{
	    this.vetoableSupport = new VetoableChangeSupport(this);
	}
	this.vetoableSupport.removeVetoableChangeListener(propertyName, listener);
    }
    
    /* ############################################################
     * ########### HierarchicalPropertyChange management ##########
     * ############################################################ */
    
    /** fire a new HierarchicalPropertyChangeEvent
     *  @param event a HierarchicalPropertyChangeEvent
     */
    public void fireHierarchicalPropertyChangeEvent(HierarchicalPropertyChangeEvent event)
    {
        if ( event != null )
        {
            if ( this.listeners != null )
            {   HierarchicalPropertyChangeListener[] list = 
                                    (HierarchicalPropertyChangeListener[])this.listeners.getListeners(HierarchicalPropertyChangeListener.class);

                if ( list != null )
                {   for(int i = 0; i < list.length; i++)
                    {   HierarchicalPropertyChangeListener current = list[i];
			
                        if ( current != null )
                        {   current.propertyChange(event); }
                    }
                }
            }

            /* tell parent that a HierarchicalPropertyChangeEvent has been processed */
            SibType parent = this.getParent();
            if ( parent != null )
            {   /* modify event */
                event.setCurrentSource(parent);
                
                /* and force parent to warn its own HierarchicalPropertyChangeEvent */
		
                parent.fireHierarchicalPropertyChangeEvent(event);
            }
        }
    }
    
    /** add a new HierarchicalPropertyChangeListener
     *  @param listener a HierarchicalPropertyChangeListener
     */
    public void addHierarchicalPropertyChangeListener(HierarchicalPropertyChangeListener listener)
    {
        if ( listener != null )
        {   if ( this.listeners == null )
                this.listeners = new EventListenerList();
            
            this.listeners.add(HierarchicalPropertyChangeListener.class, listener);
        }
    }
    
    /** remove a new HierarchicalPropertyChangeListener
     *  @param listener a HierarchicalPropertyChangeListener
     */
    public void removeHierarchicalPropertyChangeListener(HierarchicalPropertyChangeListener listener)
    {   if ( listener != null )
        {   if ( this.listeners != null )
            {   this.listeners.remove(HierarchicalPropertyChangeListener.class, listener); }
        }
    }
}
