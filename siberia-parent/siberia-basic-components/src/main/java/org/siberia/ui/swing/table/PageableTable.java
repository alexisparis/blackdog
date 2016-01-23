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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.table.TableColumnExt;
import org.siberia.ui.swing.error.ErrorEvent;
import org.siberia.ui.swing.error.ErrorHandler;
import org.siberia.ui.swing.error.ErrorOriginator;
import org.siberia.ui.swing.table.controller.TableController;
import org.siberia.ui.swing.transfer.ComponentTransfer;

/**
 *
 * JTable used in an application based on siberia
 *
 * @author alexis
 */
public class PageableTable extends JXTable implements ErrorHandler,
						      ComponentTransfer
{   
    /** logger */
    private static Logger logger = Logger.getLogger(PageableTable.class.getName());
    
    /** property name 'external controller' */
    public static final String    PROPERTY_EXTERNAL_CONTROLLER = "externalController";
    
    /** property name 'maxDisplayedRow' */
    public static final String    PROPERTY_MAX_DISPLAYED_ROWS  = "maxDisplayedRow";
    
    /** property name 'currentPageNumber' */
    public static final String    PROPERTY_CURRENT_PAGE_NUMBER = "currentPageNumber";
    
    /** property name 'pageCount' */
    public static final String    PROPERTY_PAGE_COUNT          = "pageCount";
    
    /** max number of row displayed
     *  if equals to -1, pageable functionnalities are desactivated
     */
    private int                   maxDisplayedRows       = 10;
    
    /** the number of the page currently displayed (from 0 to ?) */
    private int                   currentPageNumber      = -1;
    
    /** page count */
    private int                   pageCount              = 0;
    
    /** wrapped table model */
    private TableModel            wrappedModel           = null;
    
    /** indicate if a modification has been detected on the ListSelectionModel */
    private boolean               selectionModelModified = false;
    
    /** external controller */
    private TableController       controller             = null;
    
    /** transfer action */
    private int                   transferAction         = TransferHandler.MOVE;
    
    /**
     * Creates a new instance of PageableTable
     */
    public PageableTable()
    {   
	super();
	
	/** force to stop editing when tabel lost focus
	 *  without this, when you start to edit the property of an object
	 *  then you modify the property externally, the edition is currently running
	 *  so, the new value is applied but when edition stopped, the old value is reset
	 */
	this.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	
//	this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }
    
    /* #########################################################################
     * ################## ComponentTransfer implementation #################
     * ######################################################################### */
    
    /** return the action that has to be done<br>
     *	TransferHandler.COPY
     *	TransferHandler.COPY_OR_MOVE
     *	TransferHandler.LINK
     *	TransferHandler.MOVE
     *	TransferHandler.NONE
     *	@return an integer representing a transfer action
     */
    public int getTransferAction()
    {
	return this.transferAction;
    }
    
    /** set the action that has to be done<br>
     *	TransferHandler.COPY
     *	TransferHandler.COPY_OR_MOVE
     *	TransferHandler.LINK
     *	TransferHandler.MOVE
     *	TransferHandler.NONE
     *	@param action an integer representing a transfer action
     */
    public void setTransferAction(int action)
    {
	this.transferAction = action;
    }

    private void _updateSubComponentUI(Object componentShell)
    {	
        if ( componentShell != null &&
	     ! (componentShell instanceof Component) &&
	     ! (componentShell instanceof DefaultCellEditor) )
	{
//	    new Exception("could not refresh " + componentShell.getClass()).printStackTrace();
	    
//	    if (component != null) {
//		SwingUtilities.updateComponentTreeUI(component);
//	    }
        }
    }

    /** return the external controller linked to this table
     *	@return a TableController
     */
    public TableController getExternalController()
    {
	return controller;
    }

    /** initialize the external controller linked to this table
     *	@param controller a TableController
     */
    public void setExternalController(TableController controller)
    {
	if ( controller != this.getExternalController() )
	{
	    TableController old = this.getExternalController();
	    
	    this.controller = controller;
	 
	    this.firePropertyChange(PROPERTY_EXTERNAL_CONTROLLER, old, this.getExternalController());
	}
    }
    
    /** convert the given index<br>
     *	the index represents the index of a preferred column in the model<br>
     *	the returns index consider immuable columns like row number that is always the first column if displayed
     *	@param index 
     *	@return an integer
     */
    public int convertViewToView(int index)
    {
	return index;
    }
    
    /** return the number of mouse click that have to be done to start edition on the given row and column
     *	@param row the index of the row
     *	@param column the index of the column
     *	@return the number of click that have to be done to start edition on the given row and column
     */
    protected int getClickCountToStartEditingAt(int row, int column)
    {
	int value = 1;
	
	if ( this.getExternalController() != null )
	{
	    value = this.getExternalController().getClickCountToStartEditingAt(this, row, column);
	}
	
	return value;
    }
    
    @Override
    public boolean editCellAt(int row, int column, EventObject e)
    {
	boolean result = false;
	
	if ( e instanceof MouseEvent )
	{
	    if ( ((MouseEvent)e).getClickCount() >= this.getClickCountToStartEditingAt(row, column) )
	    {
		result = super.editCellAt(row, column, e);
	    }
	}
	else
	{
	    result = super.editCellAt(row, column, e);
	}
	
	return result;
    }
    
    /** print dbug messages on th given PrintStream
     *	@param stream a PrintStream
     */
    public void debug(PrintStream stream)
    {
	stream.println("table is      : " + this);
	stream.println("model is      : " + this.getModel());
	stream.println("column model  : " + this.getColumnModel());
	
	stream.println();
	stream.println("page count    : " + this.getPageCount());
	stream.println("current page  : " + this.getCurrentPage());
	stream.println("max row count : " + this.getMaximumDisplayedRows());
	
	stream.println();
	stream.println("###################################");
	stream.println("column model : ");
	TableColumnModel columnModel = this.getColumnModel();
	for(int i = 0; i < columnModel.getColumnCount(); i++)
	{
	    TableColumn column = columnModel.getColumn(i);
	    stream.println("\ti=" + i + " --> identifier=" + column.getIdentifier() + ", header value=" + column.getHeaderValue() +
			    ", model index=" + column.getModelIndex());
	}
	
	stream.println();
	stream.println("###################################");
	stream.println("columns : ");
	for(int column = 0; column < this.getColumnCount(); column++)
	{
	    stream.println("column at " + column + " --> name=" + this.getColumnName(column) + 
							 ", class=" + this.getColumnClass(column) +
							 ", renderer=" + this.getCellRenderer(0, column).getClass());
	}
	
	stream.println("###################################");
	
	stream.println("columns according to model : ");
	for(int column = 0; column < this.getColumnCount(); column++)
	{
	    stream.println("column at " + column + " --> name=" + this.getModel().getColumnName(column) + 
						     ", class=" + this.getModel().getColumnClass(column));
	}
	stream.println("###################################");
	
	stream.println();
	
	for(int column = 0; column < this.getColumnCount(); column++)
	{
	    stream.println("convertColumnIndexToModel(" + column + ") --> " + this.convertColumnIndexToModel(column));
	}
	
	stream.println();
	
	for(int row = 0; row < this.getRowCount(); row++)
	{
	    stream.println("## row " + row);
	    for(int column = 0; column < this.getColumnCount(); column++)
	    {
		stream.println("\tcolumn=" + column + " --> value=" + this.getValueAt(row, column) +
							       ", editable ? " + this.isCellEditable(row, column));
	    }
	}
    }

    /**
     * indicate that an error has occurred
     * 
     * @param evt an ErrorEvent
     */
    public void handleError(ErrorEvent evt)
    {
	if ( evt != null )
	{
	    /** display a JXErrorDialog */
	    JXErrorPane.showDialog(this, evt.createErrorInfo());
	}
    }

    @Override
    public void setModel(TableModel newModel)
    {
        if ( this.getModel() instanceof ErrorOriginator )
        {   ((ErrorOriginator)this.getModel()).removeErrorHandler(this); }
        
        super.setModel(newModel);
        
        /** compute page number */
        this.updatePagesCountAndPageSelection(true);
        
        if ( this.getModel() instanceof ErrorOriginator )
        {   ((ErrorOriginator)this.getModel()).addErrorHandler(this); }
    }
    
    /* #########################################################################
     * ######################## Pageable methods ###############################
     * ######################################################################### */
    
    /** set the count of page
     *  @param pageCount the count of page
     *
     *  @exception IllegalArgumentException if pageCount is less than 1
     */
    private void setPageCount(int pageCount)
    {   if (pageCount < 1)
        {   throw new IllegalArgumentException("there is always at least one page even if no row is displayed not " + pageCount); }
        
        if ( pageCount != this.getPageCount() )
        {   int oldValue = this.getPageCount();
            
            this.pageCount = pageCount;
            
            this.firePropertyChange(PROPERTY_PAGE_COUNT, oldValue, this.getPageCount());
        }
    }
    
    /** return the count of page
     *  @return the count of page
     */
    public int getPageCount()
    {   return this.pageCount; }
    
    /** compute the page count according to the given tableModel
     *  @param model a TableModel
     *  @return the count of page
     */
    private int computePageCount(TableModel model)
    {   int count = -1;
        int rowCount = super.getRowCount();
            
        if ( rowCount > 0 && this.arePageableFunctionnalitiesActivated() )
        {   count = (int)Math.ceil( ((double)rowCount) / this.getMaximumDisplayedRows() ); }
        
        if ( count <= 0 )
        {   count = 1; }
        
        return count;
    }
    
    /** set the maximum row count displayed
     *  @param maxDisplayedRows the maximum row count that the model can display at a time
     *      
     *  @exception IllegalArgumentException if maxDisplayedRows is &lt; 1 and != -1
     */
    public void setMaximumDisplayedRows(int maxDisplayedRows)
    {   if ( maxDisplayedRows < 1 && maxDisplayedRows != -1 )
        {   throw new IllegalArgumentException("maxDisplayedRow must be greater than 0 or equal to -1"); }
        
        if ( this.getMaximumDisplayedRows() != maxDisplayedRows )
        {   
            int oldValue = this.getMaximumDisplayedRows();
            
            this.maxDisplayedRows = maxDisplayedRows;
            
            /** the content of the model changed completely */
            this.tableChanged(new TableModelEvent(this.getModel()));
            
            this.firePropertyChange(PROPERTY_MAX_DISPLAYED_ROWS, oldValue, this.getMaximumDisplayedRows());
            
            this.updatePagesCountAndPageSelection();
        }
    }
    
    
    /** return the maximum row count displayed
     *  @return the maximum row count that the model can display at a time
     */
    public int getMaximumDisplayedRows()
    {   return this.maxDisplayedRows; }
    
    /** return true if pageable functionnalities are activated
     *  @return a boolean
     */
    public boolean arePageableFunctionnalitiesActivated()
    {   return this.getMaximumDisplayedRows() != -1; }
    
    /** set the current page number
     *  @param pageNumber the number of the page to display<br>
     *      if pageNumber is greater than the page count, then we will try to see the last page.<br>
     *      if pageNumber is less than 0, then we will try to display the content of the first page
     */
    public void setCurrentPage(int pageNumber)
    {   
        /** determine the page to display */
        int newValue = pageNumber;
        if ( newValue < 0 )
        {   newValue = 0; }
        else
        {   
            int pageCount = this.getPageCount();
                    
            if ( newValue >= pageCount )
            {   newValue = pageCount - 1; }
        }
        
	int currentPage = this.getCurrentPage();
        if ( currentPage != newValue )
        {   
            this.currentPageNumber = newValue;
	    
	    /** stop editing if so .. */
	    if ( this.isEditing() )
	    {
		this.removeEditor();
	    }
            
            /** the content of the model changed completely */
            this.tableChanged(new TableModelEvent(this.getModel()));
            
            this.firePropertyChange(PROPERTY_CURRENT_PAGE_NUMBER, currentPage, this.getCurrentPage());
        }
    }
    
    /** return the current page number
     *  @return an integer (from 0 to page count - 1)
     */
    public int getCurrentPage()
    {   return this.currentPageNumber; }
    
    /** go the previous page
     *	@return true if succeed
     */
    public boolean goToPreviousPage()
    {
	boolean result = false;
	
	int currentPage = this.getCurrentPage();
	if ( currentPage > 0 )
	{
	    this.setCurrentPage(currentPage - 1);
	    result = true;
	}
	
	return result;
    }
    
    /** go the next page
     *	@return true if succeed
     */
    public boolean goToNextPage()
    {
	boolean result = false;
	
	int currentPage = this.getCurrentPage();
	if ( currentPage < this.getPageCount() - 1 )
	{
	    this.setCurrentPage(currentPage + 1);
	    result = true;
	}
	
	return result;
    }
    
    /** go the first page
     *	@return true if succeed
     *		false if the selected page was already the first page
     */
    public boolean goToFirstPage()
    {
	boolean result = false;
	
	int currentPage = this.getCurrentPage();
	if ( currentPage != 0 )
	{
	    this.setCurrentPage(0);
	    result = true;
	}
	return result;
    }
    
    /** go the last page
     *	@return true if succeed
     *		false if the selected page was already the last page
     */
    public boolean goToLastPage()
    {
	boolean result = false;
	
	int currentPage = this.getCurrentPage();
	if ( currentPage != this.getPageCount() - 1 )
	{
	    this.setCurrentPage(this.getPageCount() - 1);
	    result = true;
	}
	return result;
    }
    
    /** method that convert the rowIndex of the wrapped model to the wrapping model according to context
     *	if the table display the second page (current page = 1) and maximum row count = 10, then 
     *	convertRowIndexToWrapped(15) --> 5
     *  @param rowIndex
     */
    public int convertRowIndexToWrapping(int rowIndex)
    {   
	if ( this.arePageableFunctionnalitiesActivated() )
	{
	    return (rowIndex % this.getMaximumDisplayedRows());
	}
	else
	{
	    return rowIndex;
	}
    }
    
    /** method that convert the rowIndex of the wrapping model to the wrapped model according to context
     *	if the table display the second page (current page = 1) and maximum row count = 10, then 
     *	convertRowIndexToWrapped(5) --> 15
     *
     *  @param rowIndex
     */
    public int convertRowIndexToWrapped(int rowIndex)
    {   return (this.arePageableFunctionnalitiesActivated() ? this.getCurrentPage() * this.getMaximumDisplayedRows() : 0) + rowIndex; }
    
    /* #########################################################################
     * #################### Overriden TableModel methods #######################
     * ######################################################################### */

    /** 
     *  Sets the FilterPipeline for filtering table rows, maybe null
     *  to remove all previously applied filters. 
     *  
     *  Note: the current "interactive" sortState is preserved (by 
     *  internally copying the old sortKeys to the new pipeline, if any).
     * 
     * @param pipeline the <code>FilterPipeline</code> to use, null removes
     *   all filters.
     */
    @Override
    public void setFilters(FilterPipeline pipeline)
    {
        super.setFilters(pipeline);
        
        this.updatePagesCountAndPageSelection();
    }
    
    /** methods that re-compute the page count and that assign the current page if needed
     */
    protected void updatePagesCountAndPageSelection()
    {   this.updatePagesCountAndPageSelection(false); }
    
    /** update page count */
    protected void updatePagesCount()
    {
        this.setPageCount(this.computePageCount(this.getModel()));
    }
    
    /** methods that re-compute the page count and that assign the current page if needed
     *  @param selectFirstPage true to select the first page otherwise, if the current page is bad according to the number of page
     *      (example : current page is 9 and page count is 4) then select the last page
     */
    protected void updatePagesCountAndPageSelection(boolean selectFirstPage)
    {   
	this.updatePagesCount();
        
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("updatePagesCountAndPageSelection count " + this.getPageCount() + " pages");
	}
     
        if ( selectFirstPage )
        {   this.setCurrentPage(0); }
        else
        {   if ( this.getCurrentPage() >= this.getPageCount() )
            {   this.setCurrentPage(this.getPageCount() - 1); }
        }
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     * 
     * @param rowIndex	the row whose value is to be queried
     * @param columnIndex 	the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {   if ( columnIndex < this.getColumnCount() && rowIndex < this.getRowCount() )
        {
            /** convert index */
            TableModel model = this.getModel();
            if ( model == null )
            {   return null; }
            else
            {   if ( this.arePageableFunctionnalitiesActivated() )
                {   int newRowIndex = this.convertRowIndexToWrapped(rowIndex);
                    if ( newRowIndex >= super.getRowCount() )
                    {   return null; }
                    else
                    {   return super.getValueAt(newRowIndex, columnIndex); }
                }
                else
                {   return super.getValueAt(rowIndex, columnIndex); }
            }
        }
        else
        {   return null; }
    }
    
    /** return the number of item managed by the table excepting pagination
     *  @return the completeNumber of row managed by the table
     */
    public int getAllRowsCount()
    {
        return super.getRowCount();
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
    @Override
    public int getRowCount()
    {   
        int result = 0;
        
        if ( this.getMaximumDisplayedRows() < 1 )
        {   result = super.getRowCount(); }
        else
        {   int allRowsCount = super.getRowCount();
            
            if ( allRowsCount == 0 )
            {   result = 0; }
            else
            {   result = Math.min( (allRowsCount -
                                    ( (this.getCurrentPage()) * this.getMaximumDisplayedRows() )), this.getMaximumDisplayedRows());
            }
        }
        
        return result;
    }

    /**
     *  This empty implementation is provided so users don't have to implement
     *  this method if their data model is not editable.
     * 
     * @param aValue   value to assign to cell
     * @param rowIndex   row of cell
     * @param columnIndex  column of cell
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {   /** convert index */
        int newColumnIndex = columnIndex;

        int newRowIndex = rowIndex;

        if ( this.arePageableFunctionnalitiesActivated() )
        {   newRowIndex = this.convertRowIndexToWrapped(rowIndex); }
	
	
        this.getModel().setValueAt(aValue, convertRowIndexToModel(newRowIndex),
					   convertColumnIndexToModel(newColumnIndex));
	
	
//	System.out.println("table is : " + this);
//	System.out.println("model is : " + this.getModel());
//	System.out.println("index : " + rowIndex);
//	System.out.println("newRowIndex : " + newRowIndex);
//	System.out.println("this.getAllRowsCount() : " + this.getAllRowsCount());
//	
//	System.out.println("converted row index : "  + convertRowIndexToModel(newRowIndex));
//	System.out.println("converted column index : " + convertColumnIndexToModel(newColumnIndex));
//	System.out.println("cell editable : " + isCellEditable(newRowIndex, newColumnIndex));
//	
//        if (newRowIndex < this.getAllRowsCount())
//        {   System.out.println("calling super.setValueAt");
//	    super.setValueAt(aValue, newRowIndex, newColumnIndex);
//	}
    }
    
    /**
     *  Returns false.  This is the default implementation for all cells.
     * 
     * @param rowIndex  the row being queried
     * @param columnIndex the column being queried
     * @return false
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {   boolean editable = false;
	
        /** convert index */
        if ( rowIndex < this.getAllRowsCount() && columnIndex < this.getColumnCount() )
        {   
            
            int newColumnIndex = columnIndex;
            int newRowIndex = rowIndex;

            if ( this.arePageableFunctionnalitiesActivated() )
            {   newRowIndex = this.convertRowIndexToWrapped(rowIndex); }
	    
	    editable = getModel().isCellEditable(convertRowIndexToModel(newRowIndex),
		    convertColumnIndexToModel(columnIndex));
	    if (editable)
	    {
		TableColumnExt tableColumn = getColumnExt(columnIndex);
		if (tableColumn != null)
		{
		    editable = tableColumn.isEditable();
		}
	    }
        }
        
        return editable;
    }
    
    /** ensure that the row related to given coordinates is visible
     *	@param rowIndex
     */
    public void ensureRowVisibility(int rowIndex)
    {
	this.ensureCellVisibility(rowIndex, 0);
    }
    
    /** ensure that the cell related to given coordinates is visible
     *	@param rowIndex
     *	@param columnIndex
     */
    public void ensureCellVisibility(int rowIndex, int columnIndex)
    {
	Rectangle rec = this.getCellRect(rowIndex, columnIndex, true);
	if ( rec != null )
	{
	    this.scrollRectToVisible(rec);
	}
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e)
    {
	super.valueChanged(e);
	
	if ( ! e.getValueIsAdjusting() )
	{
	    this.selectionModelModified = true;
	}
    }

    @Override
    protected void processKeyEvent(final KeyEvent e)
    {
	this.selectionModelModified  = false;
	
	super.processKeyEvent(e);
	
	/** if the last row is selected and there are others page after, then,
	 *  go to the next page and select the first line
	 *
	 *  or in reverse if the first row is selected and there are others page before, then,
	 *  go to the previous page and select the last line
	 */
	    
	if ( /*! e.isConsumed() &&*/ ! this.selectionModelModified && e.getID() == KeyEvent.KEY_PRESSED && this.arePageableFunctionnalitiesActivated() )
	{
	    final int modifiers = e.getModifiers();
	    
	    if ( modifiers == 0 || modifiers == InputEvent.CTRL_MASK )
	    {
		boolean selectLastLine  = false;
		boolean selectFirstLine = false;
		int     rowCount        = getRowCount();

		ListSelectionModel model = getSelectionModel();

		if ( model.isSelectedIndex(0) && modifiers == 0 && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_PAGE_UP) )
		{
		    /* select previous page */
		    selectLastLine = goToPreviousPage();
		}
		else if ( modifiers == InputEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_PAGE_UP )
		{
		    /* select first page */
		    selectFirstLine = goToFirstPage();
		}
		else if ( rowCount > 0 )
		{
		    if ( modifiers == 0 && model.isSelectedIndex(rowCount - 1) &&
			    (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) )
		    {
			/* select next page */
			selectFirstLine = goToNextPage();
		    }
		    else if ( modifiers == InputEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_PAGE_DOWN )
		    {
			/* select last page */
			selectLastLine = goToLastPage();
		    }
		}

		rowCount = this.getRowCount();
		
		if ( selectFirstLine )
		{
		    e.consume();
		    model.setSelectionInterval(0, 0);
		    ensureRowVisibility(0);
		}
		else if ( selectLastLine )
		{
		    e.consume();
		    int lastRowIndex = rowCount - 1;
		    model.setSelectionInterval(lastRowIndex, lastRowIndex);
		    ensureRowVisibility(lastRowIndex);
		}
	    }
	}
    }
    
    /** return the model index related to the given index.
     *  this methods allow to get the index in the table model of the row currently placed at position 'position' in the table
     *  according to pagination and filter pipe
     *  @param position the position in the table
     *  @return the corresponding position in the table model
     *  
     *  @exception IllegalArgumentException if position is higher that the current row count or position less than zero
     */
    public int getModelIndexForTableRowAt(int position)
    {
        if ( position < 0 || position >= this.getRowCount() )
        {   throw new IllegalArgumentException("illegal position : " + position + " should be >= 0 and less than " + this.getRowCount()); }
        
        int result = position;
        
        /** take pagination into account */
        if ( this.arePageableFunctionnalitiesActivated() )
        {
            result = result + (this.getMaximumDisplayedRows() * this.getCurrentPage());
        }
        
        /** consider filter and sorter */
        result = this.convertRowIndexToModel(result);
        
        return result;
    }
    
    /** return the table index related to the given model index.
     *  this methods allow to get the index in the table of the row currently placed at index 'position' in the model
     *  according to pagination and filter pipe
     *  @param position the position in the table model
     *  @return the corresponding position in the table or < 0 if the row is not displayed in the table
     *  
     *  @exception IllegalArgumentException if position is higher that the current row count or position less than zero
     */
    public int getTableIndexForRowModelAt(int position)
    {
        if ( position < 0 || position >= this.getModel().getRowCount() )
        {   throw new IllegalArgumentException("illegal position : should be >= 0 and less than " + this.getModel().getRowCount()); }
        
        int result = position;
        
        /** consider filter and sorter */
        result = this.convertRowIndexToView(result);
        
        if ( result >= 0 )
        {
            /** take pagination into account */
            if ( this.arePageableFunctionnalitiesActivated() )
            {
                result = result - (this.getMaximumDisplayedRows() * this.getCurrentPage());
                
                if ( result > this.getMaximumDisplayedRows() )
                {   result = -1; }
            }
        }
        
        return result;
    }
    
//    @Override
//    public void editingStopped(ChangeEvent e)
//    {
//        // Take in the new value
//        TableCellEditor editor = getCellEditor();
//        if (editor != null)
//	{
//            Object value = editor.getCellEditorValue();
//            setValueAt(value, this.convertRowIndexToWrapped(editingRow), editingColumn);
//            removeEditor();
//        }
//    }
    
    /** return a String representing a TableModelEvent
     *	@param e a TableModelEvent
     *	@return a String
     */
    private String tableModelEventToString(TableModelEvent e)
    {
	String result = null;
	
	if ( e != null )
	{
	    String buffer = "{type=";

	    if ( e.getType() == TableModelEvent.INSERT )
	    {
		buffer += "insert";
	    }
	    else if ( e.getType() == TableModelEvent.DELETE )
	    {
		buffer += "delete";
	    }
	    else if ( e.getType() == TableModelEvent.UPDATE )
	    {
		buffer += "update";
	    }

	    buffer += " columns="  + e.getColumn();

	    buffer += " rows[" + e.getFirstRow() + ", " + e.getLastRow() + "]";
	    
	    buffer += " with source : " + e.getSource() + "}";
	    
	    result = buffer.toString();
	}
	
	return result;
    }

    /**
     * This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     */
    @Override
    public void tableChanged(TableModelEvent e)
    {   
	/** keep sélection */
	int[] selectedRows = null;
	
	/**
	 *  TODO : see why row selection is broken
	 *
	 *  if the event is an update event,
	 *  then this could be because, we have selected some rows and via bean editor, a property
	 *  of the selected items has changed --> this cause the selection to change
	 *  to avoid this, I keep in memory the selected rows and reapply row selection
	 */
	if ( e.getType() == TableModelEvent.UPDATE )
	{
	    selectedRows = this.getSelectedRows();
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("receiving a TableModelEvent of kind " + this.tableModelEventToString(e));
	}
        
        boolean         callSuper = true;
        TableModelEvent newEvt    = e;
        
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("pageable functionnalities activated ? " + this.arePageableFunctionnalitiesActivated());
	}
        if ( this.arePageableFunctionnalitiesActivated() )
        {
            /** create a new TableModelEvent if this event concerns an element currently displayed */
            if ( e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.DELETE )
            {   
                /** if it concern a row that is greater than the last row displayed --> do nothing
                 *  if it is a row currently displayed, then create a new TableModelEvent with the converted row index
                 *  else, we must change the entire content of the table
                 */
                int firstRow = this.convertRowIndexToWrapped(0);
                int lastRow  = this.convertRowIndexToWrapped(this.getRowCount() - 1);
		
		/** translated positions */
		int translatedFirstRow = Math.max(0, this.convertRowIndexToWrapping(e.getFirstRow()));
		int translatedLastRow  = Math.min(this.getRowCount() - 1, this.convertRowIndexToWrapping(e.getLastRow()));
		
		int currentPage = this.getCurrentPage();
		
		/** only update page count */
		this.updatePagesCount();
		
		/* if we have to change page, just indicate to the table to repaint its data */
		if ( currentPage >= this.getPageCount() )
		{
		    this.setCurrentPage( this.getPageCount() - 1 );
		}
		else // the page does not change, so indicate to the table the index where insertion or deletion occur
		{
		    if ( e.getFirstRow() <= lastRow )
		    {
			/* changes occur on previous pages or on the current page at least */
			int eventRowLength = e.getLastRow() - e.getFirstRow() + 1;
			
			newEvt = new TableModelEvent(this.getModel(), translatedFirstRow,
						     Math.min(eventRowLength + translatedFirstRow, this.getRowCount() - 1), e.getColumn(), e.getType());
			
//			if ( e.getLastRow() >= lastRow )
//			{
//			    newEvt = new TableModelEvent(this.getModel(), translatedFirstRow, translatedLastRow, e.getColumn(), e.getType());
//			}
//			else if ( e.getFirstRow() >= firstRow && e.getLastRow() <= lastRow )
//			{
//			    newEvt = new TableModelEvent(this.getModel(), translatedFirstRow, translatedLastRow, e.getColumn(), e.getType());
//			}
//			else if ( e.getFirstRow() < firstRow && e.getLastRow() >= firstRow )
//			{
//			    newEvt = new TableModelEvent(this.getModel(), translatedFirstRow, translatedLastRow, e.getColumn(), e.getType());
//			}
//			else if ( e.getLastRow() < firstRow )
//			{
//			    newEvt = new TableModelEvent(this.getModel(), 0, e.getLastRow() - e.getFirstRow(), e.getColumn(), e.getType());
//			}
		    }
		}
		
            }
            else if ( e.getType() == TableModelEvent.UPDATE )
            {   
		if ( e.getFirstRow() == TableModelEvent.HEADER_ROW )
		{
		    newEvt = e;
		}
		else
		{
		    /** only convert index if the change occurs in the displayed rows */
		    int firstRow = this.convertRowIndexToWrapped(0);
		    int lastRow  = this.convertRowIndexToWrapped(this.getRowCount() - 1);

		    if ( (e.getFirstRow() <= firstRow && e.getLastRow() >= firstRow) ||
			 (e.getFirstRow() >= firstRow && e.getLastRow() <= lastRow)  ||
			 (e.getFirstRow() <= lastRow  && e.getLastRow() >= lastRow) )
		    {
			
			newEvt = new TableModelEvent(this.getModel(),
						     Math.max(0, this.convertRowIndexToWrapping(firstRow)),
						     Math.min(this.getRowCount() - 1, this.convertRowIndexToWrapping(lastRow)),
						     e.getColumn(), e.getType());
		    }
		}
            }
	    
//	    System.out.println("\tcreating TableModelEvent " + this.tableModelEventToString(newEvt));
        }
        
        if ( newEvt != null ) 
        {   
	    super.tableChanged(newEvt);
	
	    if ( selectedRows != null )
	    {
		/** restore sélection */
		Arrays.sort(selectedRows);
		this.getSelectionModel().clearSelection();

		// TODO : find a better solution to avoid losing row selection
		/* hack to avoid clearing the selection of the table :-( */
		for(int i = 0; i < selectedRows.length; i++)
		{
		    int index = selectedRows[i];

		    if ( index >= 0 && index < this.getRowCount() )
		    {
			this.getSelectionModel().addSelectionInterval(index, index);
		    }
		}
	    }
	}
    }
    
}
