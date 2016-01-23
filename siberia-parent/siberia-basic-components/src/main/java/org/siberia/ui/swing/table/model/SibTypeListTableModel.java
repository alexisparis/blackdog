/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.table.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.siberia.type.SibCollection;
import org.siberia.type.SibList;
import org.siberia.type.SibType;
import org.siberia.type.event.ContentChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeListener;
import org.siberia.ui.swing.error.ErrorHandler;
import org.siberia.ui.swing.error.ErrorOriginator;

/**
 *
 * TableModel that is able to display a SibList in a JTable
 * 
 * @author alexis
 */
public abstract class SibTypeListTableModel<T extends SibType> extends    ErrorOriginatorTableModel
                                                               implements HierarchicalPropertyChangeListener,
								          ObjectsTableModel<T>
{   
    /** logger */
    private static Logger logger = Logger.getLogger(SibTypeListTableModel.class.getName());
    
    /** SibList */
    private SibList list = null;
    
    /** Creates a new instance of SibTypeListTableModel */
    public SibTypeListTableModel()
    {   super(); }
    
    /** set the SibList displayed managed by this model
     *  @param list a sibList
     */
    public synchronized void setList(SibList list)
    {   if ( list != this.list )
        {   
	    SibList old = this.list;
            
            this.list = list;
	    
	    this.managedItemChanged(old, this.list);

            this.fireTableDataChanged();
        }
    }
    
    /** indicate to model that the object it manage has changed
     *	@param oldObject the old object managed by the model
     *	@param newObject the new object managed by the model
     */
    protected void managedItemChanged(SibType oldObject, SibType newObject)
    {
	if ( oldObject != null )
	{   oldObject.removeHierarchicalPropertyChangeListener(this); }

	if ( newObject != null )
	{   newObject.addHierarchicalPropertyChangeListener(this); }
    }
    
    /** return the SibList
     *  @return a SibList
     */
    public SibList getList()
    {   return this.list; }
    
    /** return the item at the given index in the list
     *  @param index an integer
     *  @return T
     */
    public synchronized T getItem(int index)
    {   
	T object = null;
        if ( this.getList() != null )
        {   if ( index >= 0 && index < this.getList().size() )
            {   try
                {   object = (T)this.getList().get(index); }
                catch(ClassCastException e)
                {   }
            }
        }
        return object;
    }
    
    /** return the index of the given item
     *  @param item an item that managed by the model
     *  @return the index of the item in the table or -1 if it does not appear
     */
    public int getIndexOfItem(Object item)
    {
	int result = -1;
	
	if ( this.getList() != null )
	{
	    result = this.getList().indexOfByReference(item);
	}
	
	return result;
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     * 
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount()
    {   return (this.getList() == null ? 0 : this.getList().size()); }
    
    /** method called when an item has been inserted in the list
     *  this method is always called in the EDT
     *  @param startIndex the index of the first item removed from the list
     *  @param endIndex the index of the last item removed from the list
     */
    public void itemsAddedInList(int startIndex, int endIndex)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling itemsAddedInList(" + startIndex + ", " + endIndex + ")");
	}
        
        this.fireTableRowsInserted(startIndex, endIndex);
    }
    
    /** method called when an item has been removed from the list
     *  this method is always called in the EDT
     *  @param startIndex the index of the first item removed from the list
     *  @param endIndex the index of the last item removed from the list
     */
    public void itemsRemovedFromList(int startIndex, int endIndex)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling itemsRemovedFromList(" + startIndex + ", " + endIndex + ")");
	}
        
        this.fireTableRowsDeleted(startIndex, endIndex);
    }
    
    /* #########################################################################
     * ############## HierarchicalPropertyChangeListener methods ###############
     * ######################################################################### */
    
    /** method called when a change is detected on an item contained by the list
     *  this method is always called in the EDT
     *  @param item the object that was modified
     *  @param index the index of the item in the list
     *  @param propertyName the name of the property
     *  @param oldValue the old value of the property
     *  @param newValue the new value of the property
     */
    protected abstract void propertyChangedOnItem(SibType item, int index, String propertyName, Object oldValue, Object newValue);
    
    /** check if the given HierarchicalPropertyChangeEvent has to be taken into account
     *	@param event a HierarchicalPropertyChangeEvent
     *	@return true if the given HierarchicalPropertyChangeEvent has to be taken into account
     */
    protected boolean shouldConsiderEvent(HierarchicalPropertyChangeEvent e)
    {
	return true;
    }

    /**
     * called when a HierarchicalPropertyChangeEvent was throwed by
     *  an object listener by this one
     * 
     * @param event a HierarchycalPropertyChangeEvent
     */
    public final void propertyChange(final HierarchicalPropertyChangeEvent event)
    {   
	if ( this.shouldConsiderEvent(event) )
	{
	    if ( event.getPropertyChangeEvent().getSource() == this.getList() )
	    {   if ( event.getPropertyChangeEvent() instanceof ContentChangeEvent )
		{   ContentChangeEvent contentEvent = (ContentChangeEvent)event.getPropertyChangeEvent();

		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("receiving hierarchical property change " + contentEvent.getPropertyName());
		    }
		    if ( contentEvent.getPropertyName().equals(SibCollection.PROPERTY_CONTENT) )
		    {
			boolean add = contentEvent.getMode() == contentEvent.ADD;
			
			if ( ! add && ((ContentChangeEvent)contentEvent).contentCleared()  )
			{
			    SibType[] typesRemoved = ((ContentChangeEvent)contentEvent).getObject();
			    
			    final int lastRowIndex = (typesRemoved == null ? 0 : typesRemoved.length);
			    
			    Runnable runnable = new Runnable()
			    {   public void run()
				{   itemsRemovedFromList(0, lastRowIndex); }
			    };
			    if ( runnable != null )
			    {
				if ( SwingUtilities.isEventDispatchThread() )
				{   runnable.run(); }
				else
				{   SwingUtilities.invokeLater(runnable); }
			    }
			}
			else
			{
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("type was added ? " + add);
			    }
			    int[] positions = contentEvent.getPosition();
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("positions ? " + positions);
			    }

			    if ( positions != null )
			    {   
				/* ensure positions is sorted */
//				Arrays.sort(positions);

				/* decompose array to find complete interval */
				Integer startIndex  = null;
				Integer endIndex    = null;
				int     diff        = -1;
				Integer lastCurrent = null;

				int i = 0;

				if ( logger.isDebugEnabled() )
				{
				    StringBuffer buffer = new StringBuffer();

				    for(int j = 0; j < positions.length; j++)
				    {
					buffer.append(positions[j] + ",");
				    }
				    
				    logger.debug("positions are " + buffer.toString());
				}
				
				while( i < positions.length )
				{
				    int current = positions[i];

				    /* if no endIndex, then mark it */
				    if ( (add && startIndex == null) || ( (! add) && endIndex == null) )
				    {
					if ( add )
					{
					    startIndex = current;
					}
					else
					{
					    endIndex = current;
					}
				    }
				    else
				    {
					if ( lastCurrent != null )
					{
					    /* compare */
					    diff = Math.abs(lastCurrent.intValue() - current);

					    if ( diff != 1 )
					    {
						if ( add )
						{
						    /* should mark endIndex */
						    endIndex = lastCurrent;
						}
						else
						{
						    /* should mark startIndex */
						    startIndex = lastCurrent;
						}
					    }
					}
				    }

				    if ( i == positions.length - 1 )
				    {
					if ( add )
					{
					    if ( endIndex == null )
					    {
						endIndex = current;
					    }
					}
					else
					{
					    if ( startIndex == null )
					    {
						startIndex = current;
					    }
					}
				    }
				    
				    boolean reloop = false;
				    
				    if ( startIndex != null && endIndex != null )
				    {
					final int _startIndex = startIndex.intValue();
					final int _endIndex   = endIndex.intValue();

					Runnable runnable = null;

					if ( add )
					{   
					    if ( logger.isDebugEnabled() )
					    {
						logger.debug("addItemsInList(" + _startIndex + ", " + _endIndex + ")");
					    }
					    runnable = new Runnable()
					    {   public void run()
						{   itemsAddedInList(_startIndex, _endIndex); }
					    };
					}
					else
					{   
					    if ( logger.isDebugEnabled() )
					    {
						logger.debug("removeItemsInList(" + _startIndex + ", " + _endIndex + ")");
					    }
					    runnable = new Runnable()
					    {   public void run()
						{   itemsRemovedFromList(_startIndex, _endIndex); }
					    };
					}

					if ( runnable != null )
					{
					    if ( SwingUtilities.isEventDispatchThread() )
					    {   runnable.run(); }
					    else
					    {   SwingUtilities.invokeLater(runnable); }
					}
					
					/** if we are on the last item of the list
					 *  and we do an action that include this item, then consider to not reloop
					 */
					if ( i == positions.length - 1 && (current >= startIndex && current <= endIndex ) )
					{
					    reloop = false;
					}
					else
					{
					    reloop = true;
					}

					startIndex = null;
					endIndex   = null;
				    }

				    if ( ! reloop )
				    {
					lastCurrent = current;

					i++;
				    }
				}


    //			    for(int i = 0; i < positions.length; i++)
    //			    {   final int currentIndex = positions[i];
    //				if ( logger.isDebugEnabled() )
    //				{
    //				    logger.debug("position modification : " + currentIndex);
    //				}
    //
    //				if ( add )
    //				{   
    //				    Runnable runnable = new Runnable()
    //				    {   public void run()
    //					{   itemsAddedInList(currentIndex); }
    //				    };
    //
    //				    if ( SwingUtilities.isEventDispatchThread() )
    //				    {   runnable.run(); }
    //				    else
    //				    {   SwingUtilities.invokeLater(runnable); }
    //				}
    //				else
    //				{   
    //				    Runnable runnable = new Runnable()
    //				    {   public void run()
    //					{   itemsRemovedFromList(currentIndex); }
    //				    };
    //
    //				    if ( SwingUtilities.isEventDispatchThread() )
    //				    {   runnable.run(); }
    //				    else
    //				    {   SwingUtilities.invokeLater(runnable); }
    //				}
    //			    }
			    }
			}
		    }
		}
	    }
	    else
	    {   final Object source = event.getPropertyChangeEvent().getSource();
		final int index = this.getIndexOfItem(source);
		
		if ( index >= 0 && index < this.getRowCount() && source instanceof SibType )
		{   /** an object contained by the list changed */

		    Runnable run = new Runnable()
		    {
			public void run()
			{
			    propertyChangedOnItem( (SibType)source, index, event.getPropertyChangeEvent().getPropertyName(), 
						event.getPropertyChangeEvent().getOldValue(),
						event.getPropertyChangeEvent().getNewValue() );
			}
		    };

		    if ( SwingUtilities.isEventDispatchThread() )
		    {   run.run(); }
		    else
		    {   SwingUtilities.invokeLater(run); }

		}
	    }
	}
        
    }
}
