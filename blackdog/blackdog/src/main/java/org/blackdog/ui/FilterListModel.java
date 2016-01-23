/*
 * blackdog : audio player / manager
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.siberia.base.collection.SortedList;

/**
 * define a model of a list that is used to filter selection
 *
 * @author alexis
 */
public class FilterListModel extends AbstractListModel
{
    /** logger */
    private Logger       logger          = Logger.getLogger(FilterListModel.class.getName());
    
    /** list of String */
    private List<String> items           = new SortedList<String>();

    /** item 'all' */
    private String       itemAll         = null;

    /** item 'unknown' */
    private String       itemUnknown     = null;
    
    /** true if unknown item has to be considered in the list */
    private boolean      considerUnknown = false;

    /** create a new FilterListModel
     *  @param labelAll the label to use for 'all' item
     *  @param labelUnknown the label to use for 'all' item
     */
    public FilterListModel(String labelAll, String labelUnknown)
    {
	this.itemAll     = labelAll;
	this.itemUnknown = labelUnknown;
    }
    
    /** return true if the model consider unknwon item
     *	@return a boolean
     */
    public boolean getConsiderUnknown()
    {
	return this.considerUnknown;
    }
    
    /** return the label to use for all items
     *	@return a String representing the label to use for all items
     */
    public String getLabelAllItems()
    {
	return this.itemAll;
    }
    
    /** return the label to use for 'unknown' items
     *	@return a String representing the label to use for 'unknown' items
     */
    public String getLabelUnknownItems()
    {
	return this.itemUnknown;
    }

    /**
     * Returns the length of the list.
     * @return the length of the list
     */
    public int getSize()
    {   
	return this.items.size() + 1 + (this.considerUnknown ? 1 : 0);
    }
    
    /** return the index of the given items
     *	@param item a String
     */
    public int indexOf(String item)
    {
	int result = -1;
	
	if ( item != null )
	{
	    if ( this.getLabelAllItems().equals(item) )
	    {
		result = 0;
	    }
	    else
	    {
		if ( this.considerUnknown )
		{
		    if ( this.getLabelUnknownItems().equals(item) )
		    {
			result = 1;
		    }
		    else
		    {
			int intResult = this.items.indexOf(item);
			if ( intResult >= 0 )
			{
			    result = intResult + 2;
			}
		    }
		}
		else if ( this.items != null )
		{
		    int intResult = this.items.indexOf(item);
		    
		    if ( intResult >= 0 )
		    {
			result = intResult + 1;
		    }
		}
	    }
	}
	
	return result;
    }
    
    /** add all items of the given collection
     *	@param collection a Collection of String
     */
    public void addAll(Collection<String> collection)
    {
	if ( collection != null && collection.size() > 0 )
	{
	    int size = this.getSize();
	    
	    Iterator<String> it = collection.iterator();
	    
	    while(it.hasNext())
	    {
		this.addItem(it.next(), (size > 1));
	    }
	    
	    if ( size == 1 )
	    {
		this.fireIntervalAdded(this, 1, this.getSize() - 1);
	    }
	}
    }

    /** add a new item
     *  @param item the new item to add
     *  @return true if the item was succesfully added
     */
    public boolean addItem(String item)
    {
	return this.addItem(item, true);
    }

    /** add a new item
     *  @param item the new item to add
     *	@param fireEvent true to enable fire event
     *  @return true if the item was succesfully added
     */
    protected boolean addItem(String item, boolean fireEvent)
    {
	boolean result = false;

	String processedItem = (item == null ? null : item);
	
	if ( item == null || item.length() == 0 )
	{
	    if ( ! this.considerUnknown )
	    {
		this.considerUnknown = true;
		result = true;
		
		if ( fireEvent )
		{
		    Runnable run = new Runnable()
		    {
			public void run()
			{
			    fireIntervalAdded(FilterListModel.this, 1, 1);
			}
		    };


		    if ( SwingUtilities.isEventDispatchThread() )
		    {
			run.run();
		    }
		    else
		    {
			SwingUtilities.invokeLater(run);
		    }
		}
	    }
	}
	else
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("#############");
		logger.debug("trying to add '" + processedItem + "'");
	    }

	    result = this.items.add(processedItem);
	    
	    if ( result && fireEvent )
	    {
		final int index = this.items.indexOf(processedItem);
		
		if ( index >= 0 && index < this.getSize() )
		{
		    Runnable run = new Runnable()
		    {
			public void run()
			{
			    fireIntervalAdded(FilterListModel.this, index, index);
			}
		    };


		    if ( SwingUtilities.isEventDispatchThread() )
		    {
			run.run();
		    }
		    else
		    {
			SwingUtilities.invokeLater(run);
		    }
		}
	    }
	}

	return result;
    }

    /** remove a item
     *  @param item the new item to add
     *  @return true if the item was succesfully added
     */
    public boolean removeItem(final String item)
    {
	boolean result = false;
	
	String processed = (item == null ? null : item);

	if ( processed == null || processed.length() == 0 )
	{
	    if ( this.considerUnknown )
	    {
		this.considerUnknown = false;
		
		result = true;
		
		Runnable run = new Runnable()
		{
		    public void run()
		    {
			fireIntervalRemoved(FilterListModel.this, 1, 1);
		    }
		};

		if ( SwingUtilities.isEventDispatchThread() )
		{
		    run.run();
		}
		else
		{
		    SwingUtilities.invokeLater(run);
		}
	    }
	}
	else
	{
	    synchronized(this.items)
	    {
		final int index = this.items.indexOf(item);
		
		if ( index > -1 && index < this.items.size() )
		{
		    result = this.items.remove(item);

		    if ( result )
		    {
			Runnable run = new Runnable()
			{
			    public void run()
			    {
				fireIntervalRemoved(FilterListModel.this, index, index);
			    }
			};


			if ( SwingUtilities.isEventDispatchThread() )
			{
			    run.run();
			}
			else
			{
			    SwingUtilities.invokeLater(run);
			}
		    }
		}
	    }
	}

	return result;
    }

    /** cleared the content of the list */
    public void clear()
    {
	synchronized(this.items)
	{
	    final int size = this.items.size();

	    this.items.clear();
	    
	    boolean hasConsideredUnknown = this.considerUnknown;
	    
	    this.considerUnknown = false;
	    
	    if ( size > 0 || hasConsideredUnknown )
	    {
		Runnable run = new Runnable()
		{
		    public void run()
		    {
			fireIntervalRemoved(FilterListModel.this, 1, 1 + size);
		    }
		};

		if ( SwingUtilities.isEventDispatchThread() )
		{
		    run.run();
		}
		else
		{
		    SwingUtilities.invokeLater(run);
		}
	    }
	}
    }

    /** Returns the value at the specified index.  
     *  @param index the requested index
     *  @return the value at <code>index</code>
     */
    public String getElementAt(int index)
    {
	String item = null;
	
	if ( index == 0 )
	{
	    item = this.getLabelAllItems();
	}
	else
	{
	    if ( this.considerUnknown )
	    {
		if ( index == 1 )
		{
		    item = this.getLabelUnknownItems();
		}
		else
		{
		    item = this.items.get(index - 2);
		}
	    }
	    else
	    {
		item = this.items.get(index - 1);
	    }
	    
	}
	    
	return item;
    }
}