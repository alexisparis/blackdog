/* 
 * Siberia components : siberia plugin for graphical components
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
package org.siberia.ui.action;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import org.apache.log4j.Logger;
import org.siberia.utilities.util.Parameter;
import org.siberia.type.SibType;

/**
 *
 * Action that is related to an Object
 *
 * the reference to the type is a soft reference
 *
 * @author alexis
 */
public abstract class TypeReferenceAction<E extends SibType> extends GenericAction
                                                             implements TypeAction<E>
{
    /** logger */
    private        Logger                         logger         = Logger.getLogger(TypeReferenceAction.class);
    
    /** map linking index and instances related to this action */
    private        Map<Integer, SoftReference<E>> instanceRefMap = null;
    
    /** parameters wich modify the way to create the associated type */
    private        List<Parameter>                parameters     = null;
    
    /** Creates a new instance of Action */
    public TypeReferenceAction()
    {   super(); }
    
    /** Creates a new instance of Action
     *  @param name the name of the action
     */
    public TypeReferenceAction(String name)
    {   super(name); }
    
    /** Creates a new instance of Action
     *  @param name the name of the action
     *  @param icon an icon
     */
    public TypeReferenceAction(String name, Icon icon)
    {   super(name, icon); }
    
    /** Creates a new instance of Action
     *  @param name the name of the action
     *  @param icon an icon
     *  @param desc a description
     */
    public TypeReferenceAction(String name, Icon icon, String desc)
    {   super(name, icon, desc); }
    
    /** process parameters
     *  @param list a List containing Parameters
     */
    public synchronized void setParameters(List<Parameter> list)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setParameters(List)");
	    
	    if ( list == null || list.size() == 0 )
	    {
		logger.debug("calling setParameters with no parameters");
	    }
	    else
	    {
		StringBuffer buffer = new StringBuffer();
		
		int size = list.size();
		for(int i = 0; i < size; i++)
		{
		    Parameter param = list.get(i);
		    
		    buffer.append( (param == null) ? "null" : "[name=" + param.getName() + ", value=" + param.getValue() + "]" );
		    
		    if ( i < size - 1 )
		    {
			buffer.append(", ");
		    }
		}
	    
		logger.debug("calling setParameters with {" + buffer + "}");
	    }
	}
	
	this.parameters = list;
        
        /* broke the reference to force the call to method 
         * createType the next time it have to be used
         */
        this.instanceRefMap = null;
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setParameters(List)");
	}
    }

    /** return the Objects related to this action
     *  @return the Objects related to this action
     */
    public final synchronized List<E> getTypes()
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering getTypes()");
	}
	List<E> items = new ArrayList<E>();
        
        int index = 0;
        while(true)
        {   E currentItem = null;
            
            if ( this.instanceRefMap != null )
            {   SoftReference<E> ref = this.instanceRefMap.get(index);
                
                if ( ref != null )
                {   currentItem = ref.get(); }
            }
            
            if ( currentItem == null )
            {   try
                {   currentItem = this.createType(this.parameters, index); }
                catch(NoItemAnymoreException e)
                {   break; }
                
                if ( this.instanceRefMap == null )
                {   this.instanceRefMap = new HashMap<Integer, SoftReference<E>>(); }
                
                this.instanceRefMap.put(index, new SoftReference<E>(currentItem));
            }
            
            items.add(currentItem);
            
            index++;
        }
	if ( logger.isDebugEnabled() )
	{
	    if ( items == null || items.size() == 0 )
	    {
		logger.debug("getTypes() returns no items");
	    }
	    else
	    {
		StringBuffer buffer = new StringBuffer();
		
		int size = items.size();
		for(int i = 0; i < size; i++)
		{
		    SibType current = items.get(i);
		    
		    buffer.append( (current == null ? "null" : current.getName()) );
		    
		    if ( i < size - 1 )
		    {
			buffer.append(", ");
		    }
		}
		
		logger.debug("getTypes return {" + buffer + "}");
	    }
	    
	    logger.debug("exiting getTypes()");
	}
        
        return items;
    }

    /** initialize the instance related to this action
     *  @param instance the instance related to this action
     */
    public final void setTypes(E... o)
    {   /* do nothing */ }
    
    /** method which create the type if the reference has been broken
     *  this method is called by the getType when reference is broken
     *  @param list a list of Parameter
     *  @param index the index of the item to be created
     *  @return an instanceof E
     *
     *  @exception NoItemAnymoreException when no item will be created for greater or equal index
     */
    protected abstract E createType(List<Parameter> list, int index) throws NoItemAnymoreException;
    
    /** exception that can be thrown when the implementation of createType at a given index returns no item anymore */
    public static class NoItemAnymoreException extends Exception
    {
        
    }
}
