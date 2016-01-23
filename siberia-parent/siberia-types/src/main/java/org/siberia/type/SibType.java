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
import java.io.Serializable;
import java.util.Iterator;
import org.siberia.type.event.HierarchicalPropertyChangeSource;

/**
 * base interface for all object displayed in the application
 *
 *  @author alexis
 */
public interface SibType extends HierarchicalPropertyChangeSource,
                                 Cloneable,
                                 Namable,
                                 Moveable,
                                 HtmlPrintable,
                                 Serializable
{   
    /** name of the property identity hashcode */
    public static final String PROPERTY_IDENTITY_HASHCODE = "identity-hashcode";
    
    /** property names */
    public static final String PROPERTY_READ_ONLY         = "read-only";
    
    /** children */
    public static final String PROPERTY_CHILDREN          = "children";
    
    /** id */
    public static final String PROPERTY_ID                = "id";
    
    /** id */
    public static final String PROPERTY_REMOVABLE         = "removable";
    
    
    public Object clone();
    
    /** tell if the type is considered as leaf
     *  @return true if the element must be considered as leaf
     */
    public boolean isLeaf();
    
    /** return true if the type is read only
     *  @return true if the type is read only
     */
    public boolean isReadOnly();
    
    /** set if the type is read only
     *  @param readOnly true if the type is read only
     */
    public void setReadOnly(boolean readOnly) throws PropertyVetoException;
    
    /** return true if the type can be removed
     *  @return true if the type can be removed
     */
    public boolean isRemovable();
    
    /** set if the type can be removed
     *  @param removable true if the type can be removed
     */
    public void setRemovable(boolean removable) throws PropertyVetoException;

    /*  is the object valid ?<br>
     *  to be overwritten
     *  @return true if the object is valid
     */
    public boolean isValid();
    
    /** return the id of the object
     *  @return a long
     */
    public long getId();
    
    /** initialize the id of the object
     *  @param id a long
     */
    public void setId(long id) throws PropertyVetoException;

    /* display instance caracteristics
     *  @param maxLength the maximum length for the string representation or -1 if unlimited
     *  @return a String representation of the element
     */
    public String toString(int maxLength);
    
    public String toString();

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString();
    
    /** return the system hashcode of the item
     *	@return an integer
     */
    public int getIdentityHashCode();
    
    /* ############################################################
     * ################## Hierarchic information ##################
     * ############################################################ */
    
    /** initialize the parent of the object
     *  @param parent the parent of the SibType
     */
    public void setParent(SibType parent);
    
    /** initialize the parent of the object
     *  @param parent the parent of the SibType
     *	@param removeFromOldParent true to indicate that this should be removed from the child list of its old parent
     *	@param addToNewParent true to indicate that this should be added to the child list of its new parent
     */
    public void setParent(SibType parent, boolean removeFromOldParent, boolean addToNewParent);

    /** return the parent of the object
     *  @return the parent of the SibType
     */
    public SibType getParent();
    
    /** return the path to this instance
     *  @return an array of SibType ( the last elements are the direct parent of this instance and this) or
     *          null if it does not have a father
     */
    public SibType[] getPath();
    
    /** return the number of children for this object
     *  @return the number of children for this object
     */
    public int getChildrenCount();
    
    /* return true if this instance contains the given instance as child
     *  @param child an SibType instance
     * @return true if this instance contains the given instance as child
     */
    public boolean containsChild(SibType child);
    
    /** return the child placed at the given position or null if not fund
     *  @param index
     *  @return an instanceof of SibType
     */
    public SibType getChildAt(int index);
    
    /* add child element
     *  @param item an instance of SibType
     */
    public void addChildElement(SibType item);
    
    /* insert child element
     *  @param index the index where to insert the new element
     *  @param item an instance of SibType
     */
    public void insertChildElement(int index, SibType item);
    
    /** remove an element from the children list **/
    public void removeChildElement(SibType item);
    
    public Iterator<SibType> children();
    
    /** return true if the instance has object for ancestor
     *  @return true if the instance has object for ancestor
     **/
    public boolean hasForAncestor(SibType object);
    
    /** return true if the instance has object for ancestor
     *  @return true if the instance has object for ancestor
     **/
    public boolean hasForAncestor(Class classObject);
    
    /** return the first parent instance of class classObject
     *  @param classObject a Class
     *  @return the first parent instance of class classObject
     **/
    public SibType getFirstAncestorOfType(Class classObject);
    
    /** return the position of the given element in the children list of the current object
     *  @param child an Object
     *  @return the index in the list or -1 if not fund
     */
    public int indexOfChild(Object child);
    
    /** returns the index where the item reference appears in the children list
     *	@return the index where the item reference appears in the children list
     *		or -1 if it does not appears
     */
    public int indexOfChildReference(SibType item);
    
    /** return the index of this instance in its parent children container
     *  or -1 if it was not fund
     *  @return the index of this instance in its parent children container
     **/
    public int getIndexInParent();
    
    /** return the String value of an owned element of class SibType
     *  @param code the name of an element
     *  @return the value as a String for that code
     */
    public String getChildAsValue(String code);
    
    /** return an existing child of the current SibType called 'name'
     *  @param name the name of an element
     *  @return an SibType named 'name'
     */
    public SibType getChildNamed(String name);
    
    /** method that return the level of this instance in a hierarchy.<br>
     *  example : <br>
     *      if we have three instances : a, b, c.
     *          a is the parent of b, b is the parent of c.
     *  then calling this method on a will return 0.<br>
     *  then calling this method on b will return 1.<br>
     *  then calling this method on c will return 2.<br>
     *  @return the level of the item in its hierarchy ( 0 if the item has no parent )
     */
    public int getLevel();
    
    /* #######################################################################
     * ################### HtmlPrintable implementation ######################
     * ####################################################################### */
    
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContentAndDesc();
        
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent();
    
    /* ############################################################
     * ################ Property change management ################
     * ############################################################ */
    
    /** indicates to all user that a property changed
     *  @param propertyName the name of the property
     *  @param oldValue the old value of the property
     *  @param newValue the new value of the property
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue);
    
    /** indicates to all user that a property changed
     *  @param e an instance of PropertyChangeEvent
     */
    public void firePropertyChange(PropertyChangeEvent e);
    
    /** add a new listener to the change of the object
     *  @param listener an instance of PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);
    
    /** add a new listener to the change of the object
     *  @param propertyName the name of the property
     *  @param listener an instance of PropertyChangeListener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
    
    /** remove a listener
     *  @param listener an instance of PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);
    
    /** remove a listener
     *  @param propertyName the name of the property
     *  @param listener an instance of PropertyChangeListener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
    
    /** clear the list of propertyChangeListener for this object */
    public void clearPropertyChangeListenerList();
    
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
    public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException;


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
    public void fireVetoableChange(PropertyChangeEvent evt) throws PropertyVetoException;

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

    public void addVetoableChangeListener(VetoableChangeListener listener);

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
    public void removeVetoableChangeListener(VetoableChangeListener listener);

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

    public void addVetoableChangeListener(String propertyName, VetoableChangeListener listener);

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

    public void removeVetoableChangeListener(String propertyName, VetoableChangeListener listener);
}
