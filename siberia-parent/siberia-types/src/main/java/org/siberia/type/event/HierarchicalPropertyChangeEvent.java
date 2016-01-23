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
package org.siberia.type.event;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;
import org.siberia.type.SibType;

/**
 *
 * Extension for the PropertyChangeEvent.
 * This kind of event describe the change of a property that occurs in a SibType hierarchy.
 *
 * It contains the PropertyChangeEvent that describe the local change and a list of SibType representing the parent hierarchy of
 * the objects from which the change came.
 *
 * for example : <br>
 *  consider three SibTypes a, b, c1, c2.<br>
 *  a is the parent of b, b is the parent of c1 and c2.<br>
 *  if the name of c1 changed, then a HierarchicalPropertyChangeEvent event_c1 should be created with source = c1.
 *  then c1 must warn its parent b that a change occured on its state : 
 *  a new HierarchicalPropertyChangeEvent event_b_c1 will be created with source = b. event_b_c1 will have the same PropertyChangeEvent
 *  that event_c1 and the list path of event_b_c1 will be something like that : <br>
 *  [b, c1].<br>
 *
 *  Recursively, b should warn a that a change related to a property of one of its children occured.
 *  a new HierarchicalPropertyChangeEvent event_a_b_c1 will be created with source = a. event_a_b_c1 will have the same PropertyChangeEvent
 *  that event_b_c1 and the list path of event_a_b_c1 will be something like that : <br>
 *  [a, b, c1].<br>
 *
 *  a has no parent. so it won't fire any HierarchicalPropertyChangeEvent.
 *
 *  This kind of event is useful when an object want to be warned when properties changes on a tree of SibType without listening all
 *  entities in the tree.
 *
 *  In the example, we create 3 events... 
 *  To avoid such creations, it is allowed to dynamically change the source of the event.
 *  To use only one event, act as described : <br>
 *  the event will be created by the object which is the source of the PropertyChangeEvent, in our case, object c1.
 *  c1 will warn its own HierarchicalPropertyChangeListener and then will indicate to its parent that its state has changed.
 *  c1 will not used the event it created anymore, then, b could change it and use it to warn all its HierarchicalPropertyChangeListeners
 *  that a property changed on its child c1...
 *  etc..
 *
 * @author alexis
 */
public class HierarchicalPropertyChangeEvent extends EventObject
{
    /** property changed event */
    private PropertyChangeEvent subEvent = null;
    
    /** vector representing the path */
    private Vector<SibType>     path     = null;
    
    /** the source of the event */
    private SibType             source   = null;
    
    /** Creates a new instance of HierarchicalPropertyChangeEvent<br>
     *  this kind of event create a List of Object which represents the path
     *  @param event a PropertyChangeEvent
     */
    public HierarchicalPropertyChangeEvent(PropertyChangeEvent event)
    {   super(event.getSource());
        
        if ( ! (event.getSource() instanceof SibType) )
        {   throw new IllegalArgumentException("source of the event must implements " + SibType.class); }
        
        this.subEvent = event;
        
        this.path = new Vector<SibType>( ((SibType)event.getSource()).getLevel() + 1 );
        
        this.setCurrentSource((SibType)event.getSource());
    }
    
    /** modify the source of the event
     *  @param source a SibType
     */
    public void setCurrentSource(SibType source)
    {   
        if ( source == null )
        {   throw new IllegalArgumentException("null source"); }
        
        SibType old = this.getCurrentSource();
        this.source = source;
        
        if ( this.source != old )
        {   /* insert the new source in the first place of the vector */
            this.path.insertElementAt( source, 0 );
        }
    }
    
    /** return the origin source of the event, that seems the object that is the origin of this event
     *  @return a SibType
     */
    public SibType getOriginSource()
    {   SibType origin = null;
        if ( this.path != null )
        {   origin = (SibType)this.getPropertyChangeEvent().getSource(); }
        
        return origin;
    }

    /** return the source of the event
     *  @return a SibType
     */
    public SibType getCurrentSource()
    {   return this.source; }
    
    /** return the source of the event
     *  @return an Object
     */
    @Override
    public Object getSource()
    {   return this.getCurrentSource(); }
    
    /** return the PropertyChangeEvent encapsulated by this event
     *  @return a PropertyChangeEvent
     */
    public PropertyChangeEvent getPropertyChangeEvent()
    {   return this.subEvent; }
    
    /** return the path of the event
     *  @return an unmodifiable list of SibType
     */
    public List<SibType> getPath()
    {   return Collections.unmodifiableList(this.path); }
    
    /** return the path of the event.
     *  The item at index 0 has no parent, the last item is the object that changed
     *  @return an array representing the path associated with the event
     */
    public SibType[] getArrayPath()
    {   return (SibType[])this.path.toArray(new SibType[]{}); }

    /**
     * Returns a String representation of this EventObject.
     * @return  A a String representation of this EventObject.
     */
    public String toString()
    {   StringBuffer buffer = new StringBuffer();
        
        buffer.append(this.getClass().getName() + "[source=" + this.getCurrentSource().getName() + "] with PropertyChangeEvent=[");
        buffer.append("source=" + this.getPropertyChangeEvent().getSource() + ", " + 
                      "propertyname=" + this.getPropertyChangeEvent().getPropertyName());
        buffer.append(", oldValue=" + this.getPropertyChangeEvent().getOldValue() + " --> newValue=" + this.getPropertyChangeEvent().getNewValue());
        buffer.append("] on path=[");
        
        if ( this.path != null )
        {   for(int i = 0; i < this.path.size(); i++)
            {   SibType current = this.path.get(i);
                
                if ( current == null )
                    buffer.append("null");
                else
                    buffer.append(current.getName());
                
                if ( i < this.path.size() - 1 )
                    buffer.append(", ");
            }
        }
        
        buffer.append("]");
        
        return buffer.toString();
    }
    
}
