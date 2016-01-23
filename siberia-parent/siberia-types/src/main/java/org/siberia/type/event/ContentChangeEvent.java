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
import java.util.Arrays;
import java.util.Comparator;
import org.apache.log4j.Logger;
import org.siberia.type.SibType;

/** 
 *  describe an event that concerns the content of the collection
 *  This class extends PropertyChangeEvent
 *
 *  if this event is an add event, then the position are sorted from the lower position to higher.
 *  else if it is a remove event, then the position are sorted from the higher position to lower :
 *
 *  if this event is related to a list that was like this before remove actions : 
 *	[ "a", "b", "c", "d", "e", "f"]
 *
 *	if we wanted to remove items "e", "a", "b", then
 *	    this event will have positions set to :
 *		[  4,  1,   0]
 *	    and objects like this :
 *		["e", "b", "a"]
 *
 *	4 was the position of item "e" before remove.
 *	1 was the position of item "b" before remove.
 *	0 was the position of item "a" before remove.
 *
 *  IMPORTANT : the position array could be filled with -1 which indicate that this event concern a collection
 *  that does not support indexing like Set for example.
 *
 * @author alexis
 */
public class ContentChangeEvent extends PropertyChangeEvent
{
    /** logger */
    private static Logger logger = Logger.getLogger(ContentChangeEvent.class.getName());
    
    /** change status constants */
    public static final int ADD    = 0;
    public static final int REMOVE = 1;
    
    /** the items concern by the event */
    private   SibType[] objects        = null;

    /* the positions of the concerned items */
    private   int[]       position       = null;

    /** tell if the objects where ADDED or removed */
    private   int         mode           = ADD;

    /** true if this event describe a clear of the collection */
    protected boolean     hasBeenCleared = false;

    /** create a new instance of ContentChangeEvent
     *  @param source the source of the event
     *  @param propertyName the name of the property
     *  @param mode the way the collection changed : one of the static objects declared in SibCollection : <br>
     *                  <ul><li>ADD</li><li>REMOVE</li></ul>
     *  @param positions position occupied by the item in the list
     *  @param objects the objects concerns by the event
     */
    public ContentChangeEvent(Object source, String propertyName, int mode, int[] position, SibType... objects)
    {   super(source, propertyName, null, null);
        this.setMode(mode);
        this.setPosition(position);
        this.setObject(objects);
    }

    /** create a new instance of ContentChangeEvent
     *  @param source the source of the event
     *  @param propertyName the name of the property
     *  @param mode the way the collection changed : one of the static objects declared in SibCollection : <br>
     *                  <ul><li>ADD</li><li>REMOVE</li></ul>
     *  @param position position occupied by the item in the list
     *  @param object the object concerns by the event
     */
    public ContentChangeEvent(Object source, String propertyName, int mode, int position, SibType object)
    {   this(source, propertyName, mode, new int[]{position}, object); }

    /** create a new instance of ContentChangeEvent
     *  @param source the source of the event
     *  @param propertyName the name of the property
     *  @param mode the way the collection changed : one of the static objects declared in SibCollection : <br>
     *                  <ul><li>ADD</li><li>REMOVE</li></ul>
     *  @param objects the objects concerns by the event
     */
    public ContentChangeEvent(Object source, String propertyName, int mode, SibType... objects)
    {   this(source, propertyName, mode, null, objects); }

    /** create a new instance of ContentChangeEvent that tell that the collection has been cleared
     *  @param source the source of the event
     *  @param propertyName the name of the property
     */
    protected ContentChangeEvent(Object source, String propertyName)
    {   this(source, propertyName, REMOVE, -1, null);

        this.hasBeenCleared = true;
    }

    /** return the objects concern by the vent
     *  @return an array of SibType instances
     */
    public SibType[] getObject()
    {   return objects; }

    /** set the objects concern by the vent
     *  @param objects an array of SibType instances
     */
    public void setObject(SibType... objects)
    {   this.objects = objects; }

    /** return the positions occupied by the objects concern by the event
     *  @return an array of null if positions were not specified
     */
    public int[] getPosition()
    {   return position; }

    /** set the positions occupied by the objects concern by the event
     *  @param positions an array of integers
     */
    public void setPosition(int... positions)
    {   
        logger.debug("calling setPosition");
        
        if ( positions != null )
        {   if ( positions.length == 0 || (positions.length == 1 && positions[0] == -1) )
                this.position = null;
            else
                this.position = positions;
        }
        else
        {   this.position = null; }
        
//        if ( this.getMode() == ADD && this.position != null && this.position.length > 1 )
//        {   
//            /** sort the position array in descending order */
//            Arrays.sort(this.getPosition());
//
//            int length = (int)this.getPosition().length / 2;
//
//            for(int i = 0; i < length; i++)
//            {   int a = this.getPosition()[i];
//                int reverseIndex = this.getPosition().length - 1 - i;
//                this.getPosition()[i] = this.getPosition()[reverseIndex];
//                this.getPosition()[reverseIndex] = a;
//            }
//        }
    }

    /** return the way the collection changed
     *  @return one of the static objects declared in SibCollection : <br>
     *                  <ul><li>ADD</li><li>REMOVE</li></ul>
     */
    public int getMode()
    {   return mode; }

    /** initialize the way the collection changed
     *  @param mode one of the static objects declared in SibCollection : <br>
     *                  <ul><li>ADD</li><li>REMOVE</li></ul>
     */
    public void setMode(int mode)
    {   if ( mode == ADD || mode == REMOVE )
            this.mode = mode;
    }

    /** return true if the event indicates that the collection has been cleared
     *  @return true if the event indicates that the collection has been cleared
     */
    public boolean contentCleared()
    {   return this.hasBeenCleared; }
}
