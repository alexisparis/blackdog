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
import java.util.ResourceBundle;
import org.siberia.type.SibType;

/**
 *
 * TableModel that offers severall functionnalities :<br>
 *  <ul>
 *      <li>could manager a column that display the number of the row</li>
 *      <li>manage general editability of the model</li>
 *      <li>manage PropertyChangeListeners</li>
 *      <li>manage ErrorHandler</li>
 *  </ul>
 *
 * @author alexis
 */
@Deprecated
public abstract class NumberedTableModel<T extends SibType> extends ErrorOriginatorTableModel
{   
    /** property name 'displayRowNumber' */
    public static final String PROPERTY_DISPLAY_ROW_NUMBER = "displayRowNumber";
    
    /** indicate if the model is editable */
    private boolean                            editable             = true;
    
    /** display row number ? */
    private boolean                            displayRowNumber     = false;
    
    /** title of the row index column */
    private String                             rowNumberColumnTitle = null;
    
    /** Creates a new instance of NumberedTableModel */
    public NumberedTableModel()
    {   super(); }
    
    /** set if the model must consider a first column with row number
     *  @param considerRowNumber
     */
    public void setDisplayRowNumber(boolean considerRowNumber)
    {   if ( this.isRowNumberDisplayed() != considerRowNumber )
        {   
            this.displayRowNumber = considerRowNumber;
            
            this.fireTableStructureChanged();
            
//            this.support.firePropertyChange(PROPERTY_DISPLAY_ROW_NUMBER, considerRowNumber, this.isRowNumberDisplayed());
        }
    }
    
    /** return true if the model must consider a first column with row number
     *  @return a boolean
     */
    public boolean isRowNumberDisplayed()
    {   return this.displayRowNumber; }
    
    /* #########################################################################
     * ########################## TableModel methods ###########################
     * ######################################################################### */
    
    /** indicates if a cell is editable
     *  @param rowIndex
     *  @param columnIndex
     *  @return true if the cell at the given position is editable
     */
    @Override
    public final boolean isCellEditable(int rowIndex, int columnIndex)
    {   boolean result = true;
        
        if ( ! this.isEditable() )
        {   result = false; }
        else
        {
            if ( this.isRowNumberDisplayed() )
            {
                if ( columnIndex == 0 )
                {   result = false; }
                else
                {   result = this.isCellEditableImpl(rowIndex, columnIndex - 1); }
            }
            else
            {   result = this.isCellEditableImpl(rowIndex, columnIndex); }
        }
        
        return result;
    }
    
    /** inner declaration of isCellEditable that allow not to care about row number column
     *  @param rowIndex
     *  @param columnIndex
     *  @return true if the cell at the given position is editable
     */
    protected abstract boolean isCellEditableImpl(int rowIndex, int columnIndex);

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     * 
     * @param rowIndex	the row whose value is to be queried
     * @param columnIndex 	the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public final Object getValueAt(int rowIndex, int columnIndex)
    {   Object result = null;
        
        if ( this.isRowNumberDisplayed() )
        {   if ( columnIndex == 0 )
            {   result = rowIndex + 1; }
            else
            {   result = this.getValueAtImpl(rowIndex, columnIndex - 1); }
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
    protected abstract Object getValueAtImpl(int rowIndex, int columnIndex);

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     * 
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public final int getColumnCount()
    {   return this.getColumnCountImpl() + (this.isRowNumberDisplayed() ? 1 : 0); }
    
    
    /** inner declaration of getColumnCount that allow not to care about row number column
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     * 
     * @return the number of columns in the model
     * @see #getRowCount
     */
    protected abstract int getColumnCountImpl();

    /**
     *  This empty implementation is provided so users don't have to implement
     *  this method if their data model is not editable.
     * 
     * @param aValue   value to assign to cell
     * @param rowIndex   row of cell
     * @param columnIndex  column of cell
     */
    public final void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        if ( this.isRowNumberDisplayed() )
        {   if ( columnIndex > 0 )
            {
                this.setValueAtImpl(aValue, rowIndex, columnIndex - 1);
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
    protected abstract void setValueAtImpl(Object aValue, int rowIndex, int columnIndex);

    /**
     *  Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     * 
     * @param columnIndex  the column being queried
     * @return the Object.class
     */
    public final Class<?> getColumnClass(int columnIndex)
    {
        Class retValue;
        
        if ( this.isRowNumberDisplayed() )
        {   if ( columnIndex == 0 )
            {   retValue = Integer.class; }
            else
            {   retValue = this.getColumnClassImpl(columnIndex - 1); }
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
    protected abstract Class<?> getColumnClassImpl(int columnIndex);

    /**
     *  Returns a default name for the column using spreadsheet conventions:
     *  A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     *  returns an empty string.
     * 
     * @param column  the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public final String getColumnName(int column)
    {
        String retValue;
        
        if ( this.isRowNumberDisplayed() )
        {
            if ( column == 0 )
            {
                if ( this.rowNumberColumnTitle == null )
                {   ResourceBundle bd = ResourceBundle.getBundle(NumberedTableModel.class.getName());

                    this.rowNumberColumnTitle = bd.getString("rowNumberColumnTitle");

                    if ( this.rowNumberColumnTitle == null )
                    {   this.rowNumberColumnTitle = "n°"; }
                }

                retValue = this.rowNumberColumnTitle;
            }
            else
            {
                retValue = this.getColumnNameImpl(column - 1);
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
    protected abstract String getColumnNameImpl(int column);
}
