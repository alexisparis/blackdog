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
package org.siberia.ui.swing.table;

import java.util.Enumeration;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * Wrapper for TableColumnModel
 *
 * @author alexis
 */
public class TableColumnModelWrapper implements TableColumnModel,
						TableColumnModelListener,
						ListSelectionModel,
						ListSelectionListener
{
    /** wrapped column model */
    private TableColumnModel  wrapped   = null;
    
    /** event listener list */
    private EventListenerList listeners = new EventListenerList();
    
    /** Creates a new instance of TableColumnModelWrapper
     *	@param wrapped the wrapped TableColumnModel
     */
    public TableColumnModelWrapper(TableColumnModel wrapped)
    {
	this.wrapped = wrapped;
	
	this.wrapped.addColumnModelListener(this);
	
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    ((ListSelectionModel)this.wrapped).addListSelectionListener(this);
	}
    }

    /**
     * Moves the column and its header at <code>columnIndex</code> to
     * <code>newIndex</code>.  The old column at <code>columnIndex</code>
     * will now be found at <code>newIndex</code>.  The column that used
     * to be at <code>newIndex</code> is shifted left or right
     * to make room.  This will not move any columns if
     * <code>columnIndex</code> equals <code>newIndex</code>.  This method 
     * posts a <code>columnMoved</code> event to its listeners.
     * 
     * @param columnIndex                     the index of column to be moved
     * @param newIndex                        index of the column's new location
     * @exception IllegalArgumentException      if <code>columnIndex</code> or 
     *                                          <code>newIndex</code>
     *                                          are not in the valid range
     */
    public void moveColumn(int columnIndex, int newIndex)
    {
//	new Exception("move column").printStackTrace();
	this.wrapped.moveColumn(columnIndex, newIndex);
	
//	BasicTableHeaderUI
		
//	javax.swing.plaf.basic.BasicTableHeaderUI$MouseInputHandler.setDraggedDistance(.java:252)
//        javax.swing.plaf.basic.BasicTableHeaderUI$MouseInputHandler.mouseDragged(.java:221)

    }

    /**
     * 
     * Returns the number of columns in the model.
     * 
     * @return the number of columns in the model
     */
    public int getColumnCount()
    {
	return this.wrapped.getColumnCount();
    }

    /**
     * 
     * Returns the width between the cells in each column. 
     * 
     * @return the margin, in pixels, between the cells
     */
    public int getColumnMargin()
    {
	return this.wrapped.getColumnMargin();
    }

    /**
     * Returns true if columns may be selected.
     * 
     * @return true if columns may be selected
     * @see #setColumnSelectionAllowed
     */
    public boolean getColumnSelectionAllowed()
    {
	return this.wrapped.getColumnSelectionAllowed();
    }

    /**
     * 
     * Returns an <code>Enumeration</code> of all the columns in the model.
     * 
     * @return an <code>Enumeration</code> of all the columns in the model
     */
    public Enumeration<TableColumn> getColumns()
    {
	return this.wrapped.getColumns();
    }

    /**
     * Returns the number of selected columns.
     * 
     * @return the number of selected columns; or 0 if no columns are selected
     */
    public int getSelectedColumnCount()
    {
	return this.wrapped.getSelectedColumnCount();
    }

    /**
     * Returns an array of indicies of all selected columns.
     * 
     * @return an array of integers containing the indicies of all
     * 		selected columns; or an empty array if nothing is selected
     */
    public int[] getSelectedColumns()
    {
	return this.wrapped.getSelectedColumns();
    }

    /**
     * Returns the current selection model.
     * 
     * @return a <code>ListSelectionModel</code> object
     * @see #setSelectionModel
     */
    public ListSelectionModel getSelectionModel()
    {
	return this.wrapped.getSelectionModel();
    }

    /**
     * 
     * Returns the total width of all the columns. 
     * 
     * @return the total computed width of all columns
     */
    public int getTotalColumnWidth()
    {
	return this.wrapped.getTotalColumnWidth();
    }

    /**
     * Returns the index of the first column in the table
     * whose identifier is equal to <code>identifier</code>,
     * when compared using <code>equals</code>.
     * 
     * @param columnIdentifier        the identifier object
     * @return the index of the first table column
     *                  whose identifier is equal to <code>identifier</code>
     * @exception IllegalArgumentException      if <code>identifier</code>
     * 				is <code>null</code>, or no
     * 				<code>TableColumn</code> has this
     * 				<code>identifier</code>
     * @see #getColumn
     */
    public int getColumnIndex(Object columnIdentifier)
    {
	return this.wrapped.getColumnIndex(columnIdentifier);
    }

    /**
     * Sets the <code>TableColumn</code>'s column margin to
     * <code>newMargin</code>.  This method posts
     * a <code>columnMarginChanged</code> event to its listeners.
     * 
     * @param newMargin       the width, in pixels, of the new column margins
     * @see #getColumnMargin
     */
    public void setColumnMargin(int newMargin)
    {
	this.wrapped.setColumnMargin(newMargin);
    }

    /**
     * Returns the <code>TableColumn</code> object for the column at
     * <code>columnIndex</code>.
     * 
     * @param columnIndex     the index of the desired column
     * @return the <code>TableColumn</code> object for
     * 				the column at <code>columnIndex</code>
     */
    public TableColumn getColumn(int columnIndex)
    {
	return this.wrapped.getColumn(columnIndex);
    }

    /**
     * Returns the index of the column that lies on the 
     * horizontal point, <code>xPosition</code>;
     * or -1 if it lies outside the any of the column's bounds.
     * 
     * In keeping with Swing's separable model architecture, a
     * TableColumnModel does not know how the table columns actually appear on
     * screen.  The visual presentation of the columns is the responsibility
     * of the view/controller object using this model (typically JTable).  The
     * view/controller need not display the columns sequentially from left to
     * right.  For example, columns could be displayed from right to left to
     * accomodate a locale preference or some columns might be hidden at the
     * request of the user.  Because the model does not know how the columns
     * are laid out on screen, the given <code>xPosition</code> should not be
     * considered to be a coordinate in 2D graphics space.  Instead, it should
     * be considered to be a width from the start of the first column in the
     * model.  If the column index for a given X coordinate in 2D space is
     * required, <code>JTable.columnAtPoint</code> can be used instead.
     * 
     * @return the index of the column; or -1 if no column is found
     * @see javax.swing.JTable#columnAtPoint
     */
    public int getColumnIndexAtX(int xPosition)
    {
	return this.wrapped.getColumnIndexAtX(xPosition);
    }

    /**
     * Sets the selection model.
     * 
     * @param newModel  a <code>ListSelectionModel</code> object
     * @see #getSelectionModel
     */
    public void setSelectionModel(ListSelectionModel newModel)
    {
	this.wrapped.setSelectionModel(newModel);
    }

    /**
     * Removes a listener for table column model events.
     * 
     * @param x  a <code>TableColumnModelListener</code> object
     */
    public void removeColumnModelListener(TableColumnModelListener x)
    {
	this.listeners.remove(TableColumnModelListener.class, x);
    }

    /**
     * Adds a listener for table column model events.
     * 
     * @param x  a <code>TableColumnModelListener</code> object
     */
    public void addColumnModelListener(TableColumnModelListener x)
    {
	this.listeners.add(TableColumnModelListener.class, x);
    }

    /**
     * Sets whether the columns in this model may be selected.
     * 
     * @param flag   true if columns may be selected; otherwise false
     * @see #getColumnSelectionAllowed
     */
    public void setColumnSelectionAllowed(boolean flag)
    {
	this.wrapped.setColumnSelectionAllowed(flag);
    }

    /**
     *  Deletes the <code>TableColumn</code> <code>column</code> from the 
     *  <code>tableColumns</code> array.  This method will do nothing if 
     *  <code>column</code> is not in the table's column list.
     *  This method posts a <code>columnRemoved</code>
     *  event to its listeners.
     * 
     * @param column          the <code>TableColumn</code> to be removed
     * @see #addColumn
     */
    public void removeColumn(TableColumn column)
    {
	this.wrapped.removeColumn(column);
    }

    /**
     *  Appends <code>aColumn</code> to the end of the
     *  <code>tableColumns</code> array.
     *  This method posts a <code>columnAdded</code>
     *  event to its listeners.
     * 
     * @param aColumn         the <code>TableColumn</code> to be added
     * @see #removeColumn
     */
    public void addColumn(TableColumn aColumn)
    {
	this.wrapped.addColumn(aColumn);
    }
    
    /* #########################################################################
     * ################ TableColumnModelListener implementation ################
     * ######################################################################### */
    
    /** Tells listeners that a column was added to the model. */
    public void columnAdded(TableColumnModelEvent e)
    {
	TableColumnModelEvent newEvt = new TableColumnModelEvent(this, e.getFromIndex(), e.getToIndex());
	
	/* fire event to listeners */
	TableColumnModelListener[] array = (TableColumnModelListener[])this.listeners.getListeners(TableColumnModelListener.class);
	if ( array != null )
	{
	    for(int i = 0; i < array.length; i++)
	    {
		TableColumnModelListener listener = array[i];
		
		if ( listener != null )
		{
		    listener.columnAdded(newEvt);
		}
	    }
	}
    }

    /** Tells listeners that a column was removed from the model. */
    public void columnRemoved(TableColumnModelEvent e)
    {
	TableColumnModelEvent newEvt = new TableColumnModelEvent(this, e.getFromIndex(), e.getToIndex());
	
	/* fire event to listeners */
	TableColumnModelListener[] array = (TableColumnModelListener[])this.listeners.getListeners(TableColumnModelListener.class);
	if ( array != null )
	{
	    for(int i = 0; i < array.length; i++)
	    {
		TableColumnModelListener listener = array[i];
		
		if ( listener != null )
		{
		    listener.columnRemoved(newEvt);
		}
	    }
	}
    }

    /** Tells listeners that a column was repositioned. */
    public void columnMoved(TableColumnModelEvent e)
    {
	TableColumnModelEvent newEvt = new TableColumnModelEvent(this, e.getFromIndex(), e.getToIndex());
	
	/* fire event to listeners */
	TableColumnModelListener[] array = (TableColumnModelListener[])this.listeners.getListeners(TableColumnModelListener.class);
	if ( array != null )
	{
	    for(int i = 0; i < array.length; i++)
	    {
		TableColumnModelListener listener = array[i];
		
		if ( listener != null )
		{
		    listener.columnMoved(newEvt);
		}
	    }
	}
    }

    /** Tells listeners that a column was moved due to a margin change. */
    public void columnMarginChanged(ChangeEvent e)
    {
	ChangeEvent newEvt = new ChangeEvent(this);
	
	/* fire event to listeners */
	TableColumnModelListener[] array = (TableColumnModelListener[])this.listeners.getListeners(TableColumnModelListener.class);
	if ( array != null )
	{
	    for(int i = 0; i < array.length; i++)
	    {
		TableColumnModelListener listener = array[i];
		
		if ( listener != null )
		{
		    listener.columnMarginChanged(newEvt);
		}
	    }
	}
    }

    /**
     * Tells listeners that the selection model of the
     * TableColumnModel changed.
     */
    public void columnSelectionChanged(ListSelectionEvent e)
    {
	/* fire event to listeners */
	TableColumnModelListener[] array = (TableColumnModelListener[])this.listeners.getListeners(TableColumnModelListener.class);
	if ( array != null )
	{
	    for(int i = 0; i < array.length; i++)
	    {
		TableColumnModelListener listener = array[i];
		
		if ( listener != null )
		{
		    listener.columnSelectionChanged(e);
		}
	    }
	}
    }
    
    /* #########################################################################
     * ################### ListSelectionModel implementation ###################
     * ######################################################################### */
    
      /** 
       * Called whenever the value of the selection changes.
       * @param e the event that characterizes the change.
       */
      public void valueChanged(ListSelectionEvent e)
      {
	  if ( e.getSource() == this.wrapped )
	  {
	      ListSelectionEvent newEvt = new ListSelectionEvent(this, e.getFirstIndex(), e.getLastIndex(), e.getValueIsAdjusting());
	      
	    
	      /* fire event to listeners */
	      ListSelectionListener[] array = (ListSelectionListener[])this.listeners.getListeners(ListSelectionListener.class);
	      if ( array != null )
	      {
	  	  for(int i = 0; i < array.length; i++)
		  {
		      ListSelectionListener listener = array[i];

		      if ( listener != null )
		      {
			  listener.valueChanged(newEvt);
		      }
		  }
	      }
	  }
      }
    
    /* #########################################################################
     * ################### ListSelectionModel implementation ###################
     * ######################################################################### */
    
    // fix to be able to wrap SwingX column model which have to implements ListSelectionListener
    
    /** 
     * Changes the selection to be between {@code index0} and {@code index1}
     * inclusive. {@code index0} doesn't have to be less than or equal to
     * {@code index1}.
     * <p>
     * In {@code SINGLE_SELECTION} selection mode, only the second index
     * is used.
     * <p>
     * If this represents a change to the current selection, then each
     * {@code ListSelectionListener} is notified of the change.
     * 
     * @param index0 one end of the interval.
     * @param index1 other end of the interval
     * @see #addListSelectionListener
     */
    public void setSelectionInterval(int index0, int index1)
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    ((ListSelectionModel)this.wrapped).setSelectionInterval(index0, index1);
	}
    }


    /** 
     * Changes the selection to be the set union of the current selection
     * and the indices between {@code index0} and {@code index1} inclusive.
     * {@code index0} doesn't have to be less than or equal to {@code index1}.
     * <p>
     * In {@code SINGLE_SELECTION} selection mode, this is equivalent
     * to calling {@code setSelectionInterval}, and only the second index
     * is used. In {@code SINGLE_INTERVAL_SELECTION} selection mode, this
     * method behaves like {@code setSelectionInterval}, unless the given
     * interval is immediately adjacent to or overlaps the existing selection,
     * and can therefore be used to grow the selection.
     * <p>
     * If this represents a change to the current selection, then each
     * {@code ListSelectionListener} is notified of the change.
     * 
     * @param index0 one end of the interval.
     * @param index1 other end of the interval
     * @see #addListSelectionListener
     * @see #setSelectionInterval
     */
    public void addSelectionInterval(int index0, int index1)
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    ((ListSelectionModel)this.wrapped).addSelectionInterval(index0, index1);
	}
    }


    /** 
     * Changes the selection to be the set difference of the current selection
     * and the indices between {@code index0} and {@code index1} inclusive.
     * {@code index0} doesn't have to be less than or equal to {@code index1}.
     * <p>
     * In {@code SINGLE_INTERVAL_SELECTION} selection mode, if the removal
     * would produce two disjoint selections, the removal is extended through
     * the greater end of the selection. For example, if the selection is
     * {@code 0-10} and you supply indices {@code 5,6} (in any order) the
     * resulting selection is {@code 0-4}.
     * <p>
     * If this represents a change to the current selection, then each
     * {@code ListSelectionListener} is notified of the change.
     * 
     * @param index0 one end of the interval.
     * @param index1 other end of the interval
     * @see #addListSelectionListener
     */
    public void removeSelectionInterval(int index0, int index1)
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    ((ListSelectionModel)this.wrapped).removeSelectionInterval(index0, index1);
	}
    }


    /**
     * Returns the first selected index or -1 if the selection is empty.
     */
    public int getMinSelectionIndex()
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    return ((ListSelectionModel)this.wrapped).getMinSelectionIndex();
	}
	else
	{
	    return -1;
	}
    }


    /**
     * Returns the last selected index or -1 if the selection is empty.
     */
    public int getMaxSelectionIndex()
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    return ((ListSelectionModel)this.wrapped).getMaxSelectionIndex();
	}
	else
	{
	    return -1;
	}
    }


    /** 
     * Returns true if the specified index is selected.
     */
    public boolean isSelectedIndex(int index)
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    return ((ListSelectionModel)this.wrapped).isSelectedIndex(index);
	}
	else
	{
	    return false;
	}
    }

    
    /**
     * Return the first index argument from the most recent call to 
     * setSelectionInterval(), addSelectionInterval() or removeSelectionInterval().
     * The most recent index0 is considered the "anchor" and the most recent
     * index1 is considered the "lead".  Some interfaces display these
     * indices specially, e.g. Windows95 displays the lead index with a 
     * dotted yellow outline.
     * 
     * @see #getLeadSelectionIndex
     * @see #setSelectionInterval
     * @see #addSelectionInterval
     */
    public int getAnchorSelectionIndex()
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    return ((ListSelectionModel)this.wrapped).getAnchorSelectionIndex();
	}
	else
	{
	    return -1;
	}
    }


    /**
     * Set the anchor selection index. 
     * 
     * @see #getAnchorSelectionIndex
     */
    public void setAnchorSelectionIndex(int index)
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    ((ListSelectionModel)this.wrapped).setAnchorSelectionIndex(index);
	}
    }


    /**
     * Return the second index argument from the most recent call to 
     * setSelectionInterval(), addSelectionInterval() or removeSelectionInterval().
     * 
     * @see #getAnchorSelectionIndex
     * @see #setSelectionInterval
     * @see #addSelectionInterval
     */
    public int getLeadSelectionIndex()
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    return ((ListSelectionModel)this.wrapped).getLeadSelectionIndex();
	}
	else
	{
	    return -1;
	}
    }

    /**
     * Set the lead selection index. 
     * 
     * @see #getLeadSelectionIndex
     */
    public void setLeadSelectionIndex(int index)
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    ((ListSelectionModel)this.wrapped).setLeadSelectionIndex(index);
	}
    }

    /**
     * Change the selection to the empty set.  If this represents
     * a change to the current selection then notify each ListSelectionListener.
     * 
     * @see #addListSelectionListener
     */
    public void clearSelection()
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    ((ListSelectionModel)this.wrapped).clearSelection();
	}
    }

    /**
     * Returns true if no indices are selected.
     */
    public boolean isSelectionEmpty()
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    return ((ListSelectionModel)this.wrapped).isSelectionEmpty();
	}
	else
	{
	    return true;
	}
    }
    
    /** 
     * Insert length indices beginning before/after index.  This is typically 
     * called to sync the selection model with a corresponding change
     * in the data model.
     */
    public void insertIndexInterval(int index, int length, boolean before)
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    ((ListSelectionModel)this.wrapped).insertIndexInterval(index, length, before);
	}
    }

    /** 
     * Remove the indices in the interval index0,index1 (inclusive) from
     * the selection model.  This is typically called to sync the selection
     * model width a corresponding change in the data model.
     */
    public void removeIndexInterval(int index0, int index1)
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    ((ListSelectionModel)this.wrapped).removeIndexInterval(index0, index1);
	}
    }

    /**
     * Sets the {@code valueIsAdjusting} property, which indicates whether
     * or not upcoming selection changes should be considered part of a single
     * change. The value of this property is used to initialize the
     * {@code valueIsAdjusting} property of the {@code ListSelectionEvent}s that
     * are generated.
     * <p>
     * For example, if the selection is being updated in response to a user
     * drag, this property can be set to {@code true} when the drag is initiated
     * and set to {@code false} when the drag is finished. During the drag,
     * listeners receive events with a {@code valueIsAdjusting} property
     * set to {@code true}. At the end of the drag, when the change is
     * finalized, listeners receive an event with the value set to {@code false}.
     * Listeners can use this pattern if they wish to update only when a change
     * has been finalized.
     * <p>
     * Setting this property to {@code true} begins a series of changes that
     * is to be considered part of a single change. When the property is changed
     * back to {@code false}, an event is sent out characterizing the entire
     * selection change (if there was one), with the event's
     * {@code valueIsAdjusting} property set to {@code false}.
     * 
     * @param valueIsAdjusting the new value of the property
     * @see #getValueIsAdjusting
     * @see javax.swing.event.ListSelectionEvent#getValueIsAdjusting
     */
    public void setValueIsAdjusting(boolean valueIsAdjusting)
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    ((ListSelectionModel)this.wrapped).setValueIsAdjusting(valueIsAdjusting);
	}
    }

    /**
     * Returns {@code true} if the selection is undergoing a series of changes.
     *
     * @return true if the selection is undergoing a series of changes
     * @see #setValueIsAdjusting
     */
    public boolean getValueIsAdjusting()
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    return ((ListSelectionModel)this.wrapped).getValueIsAdjusting();
	}
	else
	{
	    return false;
	}
    }

    /**
     * Sets the selection mode. The following list describes the accepted
     * selection modes:
     * <ul>
     * <li>{@code ListSelectionModel.SINGLE_SELECTION} -
     *   Only one list index can be selected at a time. In this mode,
     *   {@code setSelectionInterval} and {@code addSelectionInterval} are
     *   equivalent, both replacing the current selection with the index
     *   represented by the second argument (the "lead").
     * <li>{@code ListSelectionModel.SINGLE_INTERVAL_SELECTION} -
     *   Only one contiguous interval can be selected at a time.
     *   In this mode, {@code addSelectionInterval} behaves like
     *   {@code setSelectionInterval} (replacing the current selection),
     *   unless the given interval is immediately adjacent to or overlaps
     *   the existing selection, and can therefore be used to grow it.
     * <li>{@code ListSelectionModel.MULTIPLE_INTERVAL_SELECTION} -
     *   In this mode, there's no restriction on what can be selected.
     * </ul>
     * 
     * @see #getSelectionMode
     * @throws IllegalArgumentException if the selection mode isn't
     *         one of those allowed
     */
    public void setSelectionMode(int selectionMode)
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    ((ListSelectionModel)this.wrapped).setSelectionMode(selectionMode);
	}
    }

    /**
     * Returns the current selection mode.
     *
     * @return the current selection mode
     * @see #setSelectionMode
     */
    public int getSelectionMode()
    {
	if ( this.wrapped instanceof ListSelectionModel )
	{
	    return ((ListSelectionModel)this.wrapped).getSelectionMode();
	}
	else
	{
	    return ListSelectionModel.SINGLE_SELECTION;
	}
    }

    /**
     * Add a listener to the list that's notified each time a change
     * to the selection occurs.
     * 
     * @param x the ListSelectionListener
     * @see #removeListSelectionListener
     * @see #setSelectionInterval
     * @see #addSelectionInterval
     * @see #removeSelectionInterval
     * @see #clearSelection
     * @see #insertIndexInterval
     * @see #removeIndexInterval
     */  
    public void addListSelectionListener(ListSelectionListener x)
    {
	this.listeners.add(ListSelectionListener.class, x);
    }

    /**
     * Remove a listener from the list that's notified each time a 
     * change to the selection occurs.
     * 
     * @param x the ListSelectionListener
     * @see #addListSelectionListener
     */  
    public void removeListSelectionListener(ListSelectionListener x)
    {
	this.listeners.remove(ListSelectionListener.class, x);
    }
}
