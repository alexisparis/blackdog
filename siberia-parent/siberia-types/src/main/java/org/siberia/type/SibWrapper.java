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
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.siberia.type.event.HierarchicalPropertyChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeListener;

/**
 *
 * Define a Wrapper for SibType instances
 *
 * @author alexis
 */
public class SibWrapper implements SibType, PropertyChangeListener
{
    /** wrapped SibType */
    private SibType[] innerInstances;
    
    /** wrapped listener */
    private Set<WrapListener> wrappedListeners = null;
    
    /** map telling if a PropertyChangeListener already exists when a new type is wrapped */
    private Map<PropertyChangeListener, Boolean> listenersInitialization =
                        new HashMap<PropertyChangeListener, Boolean>();
    
    /** map telling if a VetoableChangeListener already exists when a new type is wrapped */
    private Map<VetoableChangeListener, Boolean> vetoListenersInitialization =
                        new HashMap<VetoableChangeListener, Boolean>();
    
    /** Creates a new instance of SibWrapper */
    public SibWrapper()
    {   }
    
    public Object clone()
    {   throw new UnsupportedOperationException(); }
    
    /** wrap entities
     *  @param entities an array of SibType
     */
    public void wrap(SibType... entities)
    {   
	SibType[] old = this.innerInstances;
        
        /** remove listener that were added by adding PropertyChangeListeners on the wrapper */
        if ( this.innerInstances != null )
        {   
	    for(int i = 0; i < this.innerInstances.length; i++)
	    {
		SibType current = this.innerInstances[i];
		
		if ( current != null )
		{
		    current.removePropertyChangeListener(this);
		}
	    }
	}
        
        this.innerInstances = entities;
        
        /** add listener that were added by adding PropertyChangeListeners on the wrapper */
        if ( this.innerInstances != null )
        {   
	    for(int i = 0; i < this.innerInstances.length; i++)
	    {
		SibType current = this.innerInstances[i];
		if ( current != null )
		{
		    current.addPropertyChangeListener(this);
		}
	    }
	}
        
        if ( this.wrappedListeners != null )
        {   synchronized(this.wrappedListeners)
            {   Iterator<WrapListener> it = this.wrappedListeners.iterator();
                while(it.hasNext())
		{
                    it.next().entityWrappedChanged(this, old, this.innerInstances);
		}   
            }
        }
    }
    
    /** return the wrapped SibTypes
     *  @return the wrapped SibTypes
     */
    public SibType[] getWrappedTypes()
    {   return this.innerInstances; }
    
    /** ########################################################################
     *  ######################## SibType implementation ########################
     *  ######################################################################## */
    
    /** tell if the type is considered as leaf
     *  @return true if the element must be considered as leaf
     */
    public boolean isLeaf()
    {   
	boolean result = true;
	
	/** if we find a wrapped item that is not leaf, then we return false */
	if ( this.innerInstances != null )
	{
	    for(int i = 0; i < this.innerInstances.length; i++)
	    {
		SibType current = this.innerInstances[i];
		
		if ( current != null )
		{
		    if ( ! current.isLeaf() )
		    {
			result = false;
			break;
		    }
		}
	    }
	}
        return result;
    }
    
    /** return true if the type is read only
     *  @return true if the type is read only
     */
    public boolean isReadOnly()
    {   
	return true;
    }
    
    /** set if the type is read only
     *  @param readOnly true if the type is read only
     */
    public void setReadOnly(boolean readOnly) throws PropertyVetoException
    {   
	throw new UnsupportedOperationException("cannot call setReadOnly on a " + this.getClass());
    }
    
    /** return true if the type can be removed
     *  @return true if the type can be removed
     */
    public boolean isRemovable()
    {   return false; }
    
    /** set if the type can be removed
     *  @param removable true if the type can be removed
     */
    public void setRemovable(boolean removable) throws PropertyVetoException
    {   throw new UnsupportedOperationException("wrapper cannot be removed"); }

    /*  is the object valid ?<br>
     *  to be overwritten
     *  @return true if the object is valid
     */
    public boolean isValid()
    {   
	boolean valid = true;
	
	/** if one wrapped items is not valid, then the wrapper becomes not valid */
	if ( this.innerInstances != null )
	{
	    for(int i = 0; i < this.innerInstances.length; i++)
	    {
		SibType current = this.innerInstances[i];
		
		if ( current != null && ! current.isValid() )
		{
		    valid = false;
		    break;
		}
	    }
	}
	
        return valid;
    }

    /* display instance caracteristics
     *  @param maxLength the maximum length for the string representation or -1 if unlimited
     *  @return a String representation of the element
     */
    public String toString(int maxLength)
    {   
	String result = null;
	
	if ( this.innerInstances == null || this.innerInstances.length == 0 )
	{
	    result = "null";
	}
	else
	{
	    StringBuffer buffer = new StringBuffer();
	    
	    for(int i = 0; i < this.innerInstances.length; i++)
	    {
		SibType current = this.innerInstances[i];
		
		if ( current == null )
		{
		    buffer.append("null");
		}
		else
		{
		    buffer.append(current.toString());
		}
		
		if ( i < this.innerInstances.length - 1 )
		{
		    buffer.append(", ");
		}
	    }
	    
	    result = buffer.toString();
	}
	
	if ( result != null && maxLength > -1 )
	{
	    result = result.substring(0, Math.min(result.length(), maxLength));
	}
	
        return result;
    }
    
    public String toString()
    {   
	return this.toString(-1);
    }

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {
	String result = null;
	
	if ( this.innerInstances == null || this.innerInstances.length == 0 )
	{
	    result = "null";
	}
	else
	{
	    StringBuffer buffer = new StringBuffer();
	    
	    for(int i = 0; i < this.innerInstances.length; i++)
	    {
		SibType current = this.innerInstances[i];
		
		if ( current == null )
		{
		    buffer.append("null");
		}
		else
		{
		    buffer.append(current.valueAsString());
		}
		
		if ( i < this.innerInstances.length - 1 )
		{
		    buffer.append(", ");
		}
	    }
	    
	    result = buffer.toString();
	}
	
        return result;
    }
    
    /* ############################################################
     * ################## Hierarchic information ##################
     * ############################################################ */
    
    /** initialize the parent of the object
     *  @param parent the parent of the SibType
     */
    public void setParent(SibType parent)
    {   
	if ( this.innerInstances != null )
	{
	    for(int i = 0; i < this.innerInstances.length; i++)
	    {
		SibType current = this.innerInstances[i];
		
		if ( current != null )
		{
		    current.setParent(parent);
		}
	    }
	}
    }
    
    /** initialize the parent of the object
     *  @param parent the parent of the SibType
     *	@param removeFromOldParent true to indicate that this should be removed from the child list of its old parent
     *	@param addToNewParent true to indicate that this should be added to the child list of its new parent
     */
    public void setParent(SibType parent, boolean removeFromOldParent, boolean addToNewParent)
    {   
	if ( this.innerInstances != null )
	{
	    for(int i = 0; i < this.innerInstances.length; i++)
	    {
		SibType current = this.innerInstances[i];
		
		if ( current != null )
		{
		    current.setParent(parent, removeFromOldParent, addToNewParent);
		}
	    }
	}
    }

    /** return the parent of the object
     *  @return the parent of the SibType
     */
    public SibType getParent()
    {   
	return null;
    }
    
    /** return the path to this instance
     *  @return an array of SibType ( the last elements are the direct parent of this instance and this) or
     *          null if it does not have a father
     */
    public SibType[] getPath()
    {   
	return null;
    }
    
    /** return the number of children for this object
     *  @return the number of children for this object
     */
    public int getChildrenCount()
    {   
	return 0;
    }
    
    /** method that return the level of this instance in a hierarchy.<br>
     *  example : <br>
     *      if we have three instances : a, b, c.
     *          a is the parent of b, b is the parent of c.
     *  then calling this method on a will return 0.<br>
     *  then calling this method on b will return 1.<br>
     *  then calling this method on c will return 2.<br>
     *  @return the level of the item in its hierarchy ( 0 if the item has no parent )
     */
    public int getLevel()
    {   
	return 0;
    }
    
    /* return true if this instance contains the given instance as child
     *  @param child an SibType instance
     * @return true if this instance contains the given instance as child
     */
    public boolean containsChild(SibType child)
    {   
	return false;
    }
    
    /** return the child placed at the given position or null if not fund
     *  @param index
     *  @return an instanceof of SibType
     */
    public SibType getChildAt(int index)
    {   
	return null;
    }
    
    /* add child element
     *  @param item an instance of SibType
     */
    public void addChildElement(SibType item)
    {   
	throw new UnsupportedOperationException("canot call addChildElement on a " + this.getClass());
    }
    
    /* insert child element
     *  @param index the index where to insert the new element
     *  @param item an instance of SibType
     */
    public void insertChildElement(int index, SibType item)
    {   
	throw new UnsupportedOperationException("canot call insertChildElement on a " + this.getClass());
    }
    
    /** remove an element from the children list **/
    public void removeChildElement(SibType item)
    {   
	throw new UnsupportedOperationException("canot call removeChildElement on a " + this.getClass());
    }
    
    public Iterator<SibType> children()
    {   
	return Collections.EMPTY_LIST.iterator();
    }
    
    /** return true if the instance has object for ancestor
     *  @return true if the instance has object for ancestor
     **/
    public boolean hasForAncestor(SibType object)
    {   
	return false;
    }
    
    /** return true if the instance has object for ancestor
     *  @return true if the instance has object for ancestor
     **/
    public boolean hasForAncestor(Class classObject)
    {   
	return false;
    }
    
    /** return the first parent instance of class classObject
     *  @param classObject a Class
     *  @return the first parent instance of class classObject
     **/
    public SibType getFirstAncestorOfType(Class classObject)
    {   
	return null;
    }
    
    /** return the position of the given element in the children list of the current object
     *  @param child an Object
     *  @return the index in the list or -1 if not fund
     */
    public int indexOfChild(Object child)
    {   
	return -1;
    }
    
    /** returns the index where the item reference appears in the children list
     *	@return the index where the item reference appears in the children list
     *		or -1 if it does not appears
     */
    public int indexOfChildReference(SibType item)
    {   
	return -1;
    }
    
    /** return the index of this instance in its parent children container
     *  or -1 if it was not fund
     *  @return the index of this instance in its parent children container
     **/
    public int getIndexInParent()
    {   
	return -1;
    }
    
    /** return the String value of an owned element of class SibType
     *  @param code the name of an element
     *  @return the value as a String for that code
     */
    public String getChildAsValue(String code)
    {   
	return "null";
    }
    
    /** return an existing child of the current SibType called 'name'
     *  @param name the name of an element
     *  @return an SibType named 'name'
     */
    public SibType getChildNamed(String name)
    {   
	return null;
    }
    
    /* #######################################################################
     * ################### HtmlPrintable implementation ######################
     * ####################################################################### */
    
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContentAndDesc()
    {   
	return "null";
    }
        
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   
	return "null";
    }
    
    /** return a html sequence describing the object
     *  @return a html sequence describing the object
     */
    public String toHtml()
    {   
	return "null";
    }
    
    /* #######################################################################
     * ###################### Namable implementation #########################
     * ####################################################################### */
    
    /** return the name of the object
     *  @return a String corresponding to the name
     **/
    public String getName()
    {   
	String result = null;
	
	if ( this.innerInstances != null )
	{
	    StringBuffer buffer = new StringBuffer();
	    
	    for(int i = 0; i < this.innerInstances.length; i++)
	    {
		SibType current = this.innerInstances[i];
		
		if ( current == null )
		{
		    buffer.append("null");
		}
		else
		{
		    buffer.append(current.getName());
		}
		
		if ( i < this.innerInstances.length - 1 )
		{
		    buffer.append(", ");
		}
	    }
	    
	    result = buffer.toString();
	}
	
	return result;
    }
    
    /** set the name of the object
     *  @param name the new name of the object
     **/
    public void setName(String name) throws PropertyVetoException
    {   
	throw new UnsupportedOperationException("cannot call setName on a " + this.getClass());
    }
    
    /** return the id of the object
     *  @return a long
     */
    public long getId()
    {   
	return -1;
    }
    
    /** initialize the id of the object
     *  @param id a long
     */
    public void setId(long id) throws PropertyVetoException
    {   
	throw new UnsupportedOperationException("cannot call setId on a " + this.getClass());
    }
    
    /** specify if the name could be changed
     *  @return a boolean
     **/
    public boolean nameCouldChange()
    {   
	return false;
    }
    
    /** set the name changeability of the object
     *  @param couldChange true if the name can be changed
     **/
    public void setNameCouldChange(boolean couldChange) throws PropertyVetoException
    {   
	throw new UnsupportedOperationException("cannot call setNameCouldChange on a " + this.getClass());
    }
    
    /** return the system hashcode of the item
     *	@return an integer
     */
    public int getIdentityHashCode()
    {
	return -1;
    }
    
    /* #######################################################################
     * ###################### Moveable implementation ########################
     * ####################################################################### */
    
    /** get the moveable hability of the instance
     *  @return boolean true if the object can be moved in a structure
     **/
    public boolean canBeMoved()
    {   
	return false;
    }
    
    /** set the moveable hability of the instance
     *  @param moveable true if the object has the hability to move in a structure
     **/
    public void setMoveable(boolean moveable) throws PropertyVetoException
    {   
	throw new UnsupportedOperationException("cannot call setMoveable on a " + this.getClass());
    }
    
    /** add a new WrapListener
     *  @param listener
     */
    public void addWrapListener(WrapListener listener)
    {   if ( listener != null )
        {   if ( this.wrappedListeners == null )
                this.wrappedListeners = new HashSet<WrapListener>();
            synchronized(this.wrappedListeners)
            {   this.wrappedListeners.add(listener); }
        }
    }
    
    /** remove a new WrapListener
     *  @param listener
     */
    public void removeWrapListener(WrapListener listener)
    {   if ( this.wrappedListeners != null )
        {   synchronized(this.wrappedListeners)
            {   this.wrappedListeners.remove(listener); }
        }
    }
    
    /** define a listener that will be warned when the wrapped entity of a SibWrapper changed */
    public static interface WrapListener
    {
        /** the wrapped entity of a SibWrapper has changed
         *  @param wrapper an instance of SibWrapper
         *  @param oldTypes the old wrapped entities
         *  @param newTypes the new wrapped entities
         */
        public void entityWrappedChanged(SibWrapper wrapper, SibType[] oldTypes, SibType[] newTypes);
        
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
    {   if ( this.listenersInitialization != null )
        {   Iterator<PropertyChangeListener> it = this.listenersInitialization.keySet().iterator();
            while(it.hasNext())
                it.next().propertyChange(e);
        }
    }
    
    /** add a new listener to the change of the object
     *  @param listener an instance of PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {   if ( ! this.listenersInitialization.containsKey(listener) )
        {   this.listenersInitialization.put(listener, false); }
    }
    
    /** add a new listener to the change of the object
     *  @param propertyName the name of the property
     *  @param listener an instance of PropertyChangeListener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {   this.addPropertyChangeListener(listener); }
    
    /** remove a listener
     *  @param listener an instance of PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {   if ( this.listenersInitialization.containsKey(listener) )
        {   this.listenersInitialization.remove(listener); }
    }
    
    /** remove a listener
     *  @param propertyName the name of the property
     *  @param listener an instance of PropertyChangeListener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {   this.removePropertyChangeListener(listener); }
    
    /** clear the list of propertyChangeListener for this object */
    public void clearPropertyChangeListenerList()
    {   
	throw new UnsupportedOperationException("cannot call clearPropertyChangeListenerList on a " + this.getClass());
    }
    
    /** allow the current object to inherit the listeners of its current parent<br>
     *  calling this method is very time costly, so warning
     */  
    public void inheritsPropertyChangeListenersFromItsParent()
    {   // PENDING
        throw new UnsupportedOperationException();
    }
    
    /** add the same listener to all children
     *  @param listener the listener to add
     */
    public void addPropertyChangeListenerToChildren(PropertyChangeListener listener)
    {   // PENDING
        throw new UnsupportedOperationException();
    }
    
    /** remove the same listener to all children
     *  @param listener the listener to remove
     */
    public void removePropertyChangeListenerFromChildren(PropertyChangeListener listener)
    {   // PENDING
        throw new UnsupportedOperationException();
    }

    /**
     * This method gets called when a bound property is changed.
     * 
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {   
	boolean containsSource = false;
	
	if ( this.innerInstances != null )
	{
	    for(int i = 0; i < this.innerInstances.length; i++)
	    {
		SibType current = this.innerInstances[i];
		
		if ( evt.getSource() == current )
		{
		    containsSource = true;
		    break;
		}
	    }
	}
	
	if ( containsSource )
        {   
	    PropertyChangeEvent redirectEvent = new PropertyChangeEvent(this, evt.getPropertyName(),
                                                                        evt.getOldValue(), evt.getNewValue());
            
            this.firePropertyChange(redirectEvent);
        }
    }
    
    /* ############################################################
     * ################ Vetoable change management ################
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
    {   PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        
        this.fireVetoableChange(event);
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
    {   if ( this.vetoListenersInitialization != null )
        {   Iterator<VetoableChangeListener> it = this.vetoListenersInitialization.keySet().iterator();
            while(it.hasNext())
                it.next().vetoableChange(evt);
        }
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
    {   if ( ! this.vetoListenersInitialization.containsKey(listener) )
        {   this.vetoListenersInitialization.put(listener, false); }
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
    {   this.addVetoableChangeListener(listener); }

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
    {   if ( this.vetoListenersInitialization.containsKey(listener) )
        {   this.vetoListenersInitialization.remove(listener); }
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
    {   this.removeVetoableChangeListener(listener); }
    
    /* ############################################################
     * ###### HierarchicalPropertyChangeSource implementation #####
     * ############################################################ */
    
    /** add a new HierarchicalPropertyChangeListener
     *  @param listener a HierarchicalPropertyChangeListener
     */
    public void addHierarchicalPropertyChangeListener(HierarchicalPropertyChangeListener listener)
    {   throw new UnsupportedOperationException(); }
    
    /** remove a new HierarchicalPropertyChangeListener
     *  @param listener a HierarchicalPropertyChangeListener
     */
    public void removeHierarchicalPropertyChangeListener(HierarchicalPropertyChangeListener listener)
    {   throw new UnsupportedOperationException(); }
    
    /** fire a new HierarchicalPropertyChangeEvent
     *  @param event a HierarchicalPropertyChangeEvent
     */
    public void fireHierarchicalPropertyChangeEvent(HierarchicalPropertyChangeEvent event)
    {   throw new UnsupportedOperationException(); }
    
}
