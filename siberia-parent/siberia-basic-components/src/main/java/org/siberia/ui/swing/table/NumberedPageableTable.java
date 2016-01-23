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

import java.awt.FontMetrics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.table.TableColumnExt;

/**
 *
 * Extension to PageableTable that allow to show a column displaying the row index
 *
 * @author alexis
 */
public class NumberedPageableTable extends PageableTable
{
    /** property name 'displayRowNumber' */
    public static final String                 PROPERTY_DISPLAY_ROW_NUMBER = "displayRowNumber";
    
    /** static map linking integer and the RowNumber to use */
    private static Map<Number, RowNumber>      rowNumbers                  = new WeakHashMap<Number, RowNumber>(1000);
    
    /** logger */
    private Logger                             logger                      = Logger.getLogger(NumberedPageableTable.class);
    
    /** display row number ? */
    private boolean                            displayRowNumber            = false;
    
    /** title of the row index column */
    private String                             rowNumberColumnTitle        = null;
    
    /** index of the row number column */
    private int                                rowNumberColumn             = 0;
    
    /** Creates a new instance of NumberedPageableTable */
    public NumberedPageableTable()
    {	super();
	
	// TODO : add highlighters
//	this.setHighlighters(new org.jdesktop.swingx.decorator.ShadingColorHighlighter(null));
    }
    
    /** convert the given index<br>
     *	the index represents the index of a preferred column in the model<br>
     *	the returns index consider immuable columns like row number that is always the first column if displayed
     *	@param index 
     *	@return an integer
     */
    @Override
    public int convertViewToView(int index)
    {
	if ( this.isRowNumberDisplayed() )
	{
	    return super.convertViewToView(index + 1);
	}
	else
	{
	    return super.convertViewToView(index);
	}
    }

    @Override
    public void setColumnModel(TableColumnModel columnModel)
    {
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("entering setColumnModel(TableColumnModel)");
	    logger.debug("calling setColumnModel(TableColumnModel) with " + columnModel);
	}
	
	if ( columnModel != this.getColumnModel() )
	{
	    /** inhibit to move the first column */
	    super.setColumnModel(new TableColumnModelWrapper(columnModel)
	    {
		@Override
		public void moveColumn(int columnIndex, int newIndex)
		{
		    if ( ! isRowNumberDisplayed() || (columnIndex != rowNumberColumn && newIndex != 0) )
		    {
			super.moveColumn(columnIndex, newIndex);
		    }
		}
	    });
	}
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("exiting setColumnModel(TableColumnModel)");
	}
    }

    /**
     * Decides if the column at columnIndex can be interactively sorted. 
     * <p>
     * Here: true if both this table and the column sortable property is
     * enabled, false otherwise.
     * 
     * @param columnIndex column in view coordinates
     * @return boolean indicating whether or not the column is sortable
     *            in this table.
     */
    @Override
    protected boolean isSortable(int columnIndex)
    {
	boolean result = true;
	
	if ( columnIndex == this.rowNumberColumn && this.isRowNumberDisplayed() )
	{   
	    result = false;
	}
	else
	{
	    result = super.isSortable(columnIndex);
	}
	
	return result;
    }
    
    /** set if the model must consider a first column with row number
     *  @param considerRowNumber
     */
    public void setDisplayRowNumber(boolean considerRowNumber)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setDisplayRowNumber(boolean)");
	    logger.debug("calling setDisplayRowNumber(boolean) with " + considerRowNumber);
	}
	if ( this.isRowNumberDisplayed() != considerRowNumber )
        {   
            this.displayRowNumber = considerRowNumber;
            
	    /* made as if the event was fired by the table model */
	    this.tableChanged(new TableModelEvent(this.getModel(), TableModelEvent.HEADER_ROW));
	    
            this.firePropertyChange(PROPERTY_DISPLAY_ROW_NUMBER, considerRowNumber, this.isRowNumberDisplayed());
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setDisplayRowNumber(boolean)");
	}
    }
    
    /** return true if the model must consider a first column with row number
     *  @return a boolean
     */
    public boolean isRowNumberDisplayed()
    {   return this.displayRowNumber; }
    
    /** indicates if a cell is editable
     *  @param rowIndex
     *  @param columnIndex
     *  @return true if the cell at the given position is editable
     */
    @Override
    public final boolean isCellEditable(int rowIndex, int columnIndex)
    {   boolean result = true;
        
	if ( this.isRowNumberDisplayed() )
	{
	    if ( columnIndex == this.rowNumberColumn )
	    {   result = false; }
	    else
	    {   result = this.isCellEditableImpl(rowIndex, columnIndex); }
	}
	else
	{   result = this.isCellEditableImpl(rowIndex, columnIndex); }
        
        return result;
    }
    
    /** inner declaration of isCellEditable that allow not to care about row number column
     *  @param rowIndex
     *  @param columnIndex
     *  @return true if the cell at the given position is editable
     */
    protected boolean isCellEditableImpl(int rowIndex, int columnIndex)
    {
	return super.isCellEditable(rowIndex, columnIndex);
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
    public final Object getValueAt(int rowIndex, int columnIndex)
    {   Object result = null;
        
        if ( this.isRowNumberDisplayed() )
        {   if ( columnIndex == this.rowNumberColumn )
            {   
		int convertedIndex = this.convertRowIndexToWrapped(rowIndex + 1);
		result = rowNumbers.get(convertedIndex);
		
		if ( result == null )
		{
		    result = new RowNumber(convertedIndex);
		    
		    rowNumbers.put(convertedIndex, (RowNumber)result);
		}
	    }
            else
            {   result = this.getValueAtImpl(rowIndex, columnIndex); }
        }
        else
        {   result = this.getValueAtImpl(rowIndex, columnIndex); }
        
        return result;
    }
    
    /** inner declaration of getValueAt that allow not to care about row number column
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     * 
     * @param rowIndex	the row whose value is to be queried
     * @param columnIndex 	the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    protected Object getValueAtImpl(int rowIndex, int columnIndex)
    {
	return super.getValueAt(rowIndex, columnIndex);
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
    public final void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        if ( this.isRowNumberDisplayed() )
        {   if ( columnIndex > this.rowNumberColumn )
            {
                this.setValueAtImpl(aValue, rowIndex, columnIndex);
            }
        }
        else
        {   this.setValueAtImpl(aValue, rowIndex, columnIndex); }
    }
    
    /** inner declaration of setValueAt that allow not to care about row number column
     *  This empty implementation is provided so users don't have to implement
     *  this method if their data model is not editable.
     * 
     * @param aValue   value to assign to cell
     * @param rowIndex   row of cell
     * @param columnIndex  column of cell
     */
    protected void setValueAtImpl(Object aValue, int rowIndex, int columnIndex)
    {
	super.setValueAt(aValue, rowIndex, columnIndex);
    }

    /**
     *  Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     * 
     * @param columnIndex  the column being queried
     * @return the Object.class
     */
    @Override
    public final Class<?> getColumnClass(int columnIndex)
    {
        Class retValue;
        
        if ( this.isRowNumberDisplayed() )
        {   if ( columnIndex == this.rowNumberColumn )
            {   retValue = RowNumber.class; }
            else
            {   retValue = this.getColumnClassImpl(columnIndex); }
        }
        else
        {   retValue = this.getColumnClassImpl(columnIndex); }
        
        return retValue;
    }
    
    /** inner declaration of getColumnClass that allow not to care about row number column
     *  Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     * 
     * @param columnIndex  the column being queried
     * @return the Object.class
     */
    protected Class<?> getColumnClassImpl(int columnIndex)
    {
	return super.getColumnClass(columnIndex);
    }

    /**
     *  Returns a default name for the column using spreadsheet conventions:
     *  A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     *  returns an empty string.
     * 
     * @param column  the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    @Override
    public final String getColumnName(int column)
    {
        String retValue;
        
        if ( this.isRowNumberDisplayed() )
        {
            if ( column == this.rowNumberColumn )
            {
                if ( this.rowNumberColumnTitle == null )
                {   ResourceBundle bd = ResourceBundle.getBundle(NumberedPageableTable.class.getName());

                    this.rowNumberColumnTitle = bd.getString("rowNumberColumnTitle");

                    if ( this.rowNumberColumnTitle == null )
                    {   this.rowNumberColumnTitle = "n°"; }
                }

                retValue = this.rowNumberColumnTitle;
            }
            else
            {
                retValue = this.getColumnNameImpl(column);
            }
        }
        else
        {   retValue = this.getColumnNameImpl(column); }
        
        return retValue;
    }
    
    /** inner declaration of getColumnName that allow not to care about row number column
     *  Returns a default name for the column using spreadsheet conventions:
     *  A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     *  returns an empty string.
     * 
     * @param column  the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    protected String getColumnNameImpl(int column)
    {
	return super.getColumnName(column);
    }
    
    @Override
    public void createDefaultColumnsFromModel()
    {
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("entering createDefaultColumnsFromModel()");
	}
	
	// JW: when could this happen?
	if (getModel() == null)
	    return;
	// Remove any current columns
	List<TableColumn> columns = this.getColumns(true);
	for (Iterator<TableColumn> iter = columns.iterator(); iter.hasNext();) {
	    getColumnModel().removeColumn(iter.next());

	}

	int columnCount = (this.isRowNumberDisplayed() ? 1 : 0) + this.getModel().getColumnCount();
	for (int i = 0; i < columnCount; i++)
	{
	    int columnIndex = i;

	    if ( this.isRowNumberDisplayed() )
	    {
		if ( columnIndex == this.rowNumberColumn )
		{
		    columnIndex = -1;
		}
		else
		{
		    columnIndex -= 1;
		}
	    }

	    TableColumnExt column = new TableColumnExt(columnIndex);
	    
//	    if ( columnIndex == -1 )
//	    {
//		column.addPropertyChangeListener(new PropertyChangeListener()
//		{
//		    public void propertyChange(PropertyChangeEvent evt)
//		    {
//			System.out.println("property : " + evt.getPropertyName());
//			if ( "width".equals(evt.getPropertyName()) )
//			{
//			    new Exception("modif de " + evt.getPropertyName() + " de " + ((TableColumn)evt.getSource()).getHeaderValue() + " de " + 
//				    evt.getOldValue() + " a "  + evt.getNewValue()).printStackTrace();
//			}
//		    }
//		});
//	    }

	    String name = null;
	    if ( this.isRowNumberDisplayed() )
	    {
		if ( i == this.rowNumberColumn )
		{	name = this.getColumnName(this.rowNumberColumn); }
		else
		{	name = this.getModel().getColumnName(i - 1); }
	    }
	    else
	    {   name = this.getModel().getColumnName(i); }

	    column.setHeaderValue(name);

	    if ( i == this.rowNumberColumn && this.isRowNumberDisplayed() )
	    {
		this.getTableHeader().setReorderingAllowed(true);
	    }

	    this.prePrepareTableColumn(column, i);

	    this.getColumnModel().addColumn(column);

	    this.postPrepareTableColumn(column, i);
	}
	
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("exiting createDefaultColumnsFromModel()");
	}
    }
    
    /** customize a TableColumn
     *	@param column a TableColumn
     *	@param index of the table column
     */
    protected void prePrepareTableColumn(TableColumn column, int index)
    {
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("entering prepareTableColumn(Tablecolumn, int)");
	    logger.debug("calling prepareTableColumn(Tablecolumn, int) with " + column + " and " + index);
	}
	if ( column != null )
	{
	    if ( index == 0 && this.isRowNumberDisplayed() )
	    {
		/** determine the preferred size according to the number of row */
		int globalRowCount = this.getAllRowsCount();
		
		FontMetrics metrics = this.getFontMetrics(this.getFont());
		int prefSize = metrics.stringWidth(Integer.toString(globalRowCount)) + 10;
//		int prefSize = metrics.charWidth('9') * ( ((int)Math.log10(globalRowCount)) + 2 );
		
		column.setMaxWidth(prefSize + 100);
		column.setWidth(prefSize);
		column.setPreferredWidth(prefSize);
	    }
	}
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("exiting prepareTableColumn(Tablecolumn, int)");
	}
    }
    
    /** customize a TableColumn
     *	@param column a TableColumn
     *	@param index of the table column
     */
    protected void postPrepareTableColumn(TableColumn column, int index)
    {
	
    }

    /**
     * This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     */
    @Override
    public void tableChanged(TableModelEvent e)
    {   
	/* event comes from model */
	TableModelEvent newEvt = e;
	if ( this.isRowNumberDisplayed() )
	{
	    TableColumn column = null;

	    if ( this.getColumnModel().getColumnCount() > 0 )
	    {
		column = this.getColumnModel().getColumn(0);
	    }

	    if ( column != null )
	    {
		this.prePrepareTableColumn(column, 0);
	    }
	}
	
	super.tableChanged(newEvt);
    }
    
}
