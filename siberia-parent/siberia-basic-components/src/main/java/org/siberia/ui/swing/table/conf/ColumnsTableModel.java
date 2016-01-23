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
package org.siberia.ui.swing.table.conf;

import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.siberia.ui.swing.table.model.AbstractSiberiaTableModel;
import org.siberia.ui.swing.table.model.PropertyBasedTableModel;
import org.siberia.ui.swing.table.model.PropertyDeclaration;

/**
 *
 * model that allow to change the caracteristics of the column of a table
 *
 * @author alexis
 */
public class ColumnsTableModel extends AbstractSiberiaTableModel
{
    /** property modified */
    public static final String PROPERTY_MODIFIED = "modified";
    
    /** logger */
    private Logger                  logger      = Logger.getLogger(ColumnsTableModel.class);
    
    /** the property based table model */
    private PropertyBasedTableModel tableModel  = null;
    
    /** indicate if the model is modified */
    private boolean                 modified    = false;
    
    /** column names */
    private String[]                columnNames = null;
    
    /** Creates a new instance of ColumnsTableModel
     *	@param columnNames then ame of the column
     */
    public ColumnsTableModel(String... columnNames)
    {	
	if ( logger.isDebugEnabled() )
	{
	    StringBuffer buffer = new StringBuffer();
	    
	    if ( columnNames != null )
	    {
		for(int i = 0; i < columnNames.length; i++)
		{
		    buffer.append(columnNames[i]);
		    
		    if ( i < columnNames.length - 1 )
		    {
			buffer.append(", ");
		    }
		}
	    }
	    logger.debug("creating a ColumnsTableModel with column names = {" + buffer + "}");
	}
	this.columnNames = columnNames;
    }
    
    /** return the PropertyBasedTableModel to use
     *	@return a PropertyBasedTableModel
     */
    public PropertyBasedTableModel getPropertyBasedModel()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering getPropertyBasedModel()");
	}
	PropertyBasedTableModel model = this.tableModel;
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getPropertyBasedModel() returns " + model);
	    logger.debug("exiting getPropertyBasedModel()");
	}
	return model;
    }
    
    /** set the PropertyBasedTableModel to use
     *	@param tableModel a PropertyBasedTableModel
     */
    public void setPropertyBasedModel(PropertyBasedTableModel tableModel)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setPropertyBasedModel(PropertyBasedTableModel)");
	    logger.debug("calling setPropertyBasedModel(PropertyBasedTableModel) with " + tableModel);
	}
	if ( tableModel == this.getPropertyBasedModel() )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("the PropertyBasedTableModel is the same as the current PropertyBasedTableModel --> do nothing");
	    }
	}
	else
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("changing PropertyBasedTableModel from " + this.getPropertyBasedModel() + " to " + tableModel);
	    }
	    this.tableModel = tableModel;
	    this.fireTableDataChanged();
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setPropertyBasedModel(PropertyBasedTableModel)");
	}
    }

    /** return true if the model is modified
     *	@return a boolean
     */
    public boolean isModified()
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering isModified()");
	}
	boolean modif = modified;
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("isModified() returns " + modif);
	    logger.debug("exiting isModified()");
	}
	return modif;
    }

    /** true if the model should considered itself as modified
     *	@param modified a boolean
     */
    public void setModified(boolean modified)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setModified(boolean)");
	    logger.debug("calling setModified(boolean) with " + modified);
	}
	if ( modified == this.isModified() )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("the model is already " + (modified ? "" : "not") + " modified");
	    }
	}
	else
	{
	    this.modified = modified;
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("changing modification status of model from " + (! modified) + " to " + modified);
	    }
	    
	    this.firePropertyChangeEvent(PROPERTY_MODIFIED, ! this.isModified(), this.isModified());
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setModified(boolean)");
	}
    }
    
    /** return the PropertyDeclaration for the given row
     *	@param row an integer
     *	@return a PropertyDeclaration or null
     */
    private PropertyDeclaration getPropertyDeclaration(int row)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering getPropertyDeclaration(int)");
	    logger.debug("calling getPropertyDeclaration(int) with " + row);
	}
	PropertyDeclaration declaration = null;
	
	if ( this.tableModel != null )
	{
	    if ( row >= 0 && row < this.tableModel.getPropertyDeclarationCount() )
	    {
		declaration = this.tableModel.getPropertyDeclaration(row);
	    }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("calling getPropertyDeclaration with index out of bounds [0, " + this.tableModel.getPropertyDeclarationCount() + "]");
		}
	    }
	}
	else
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("inner table model is null");
	    }
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getPropertyDeclaration(int) returns " + declaration);
	    logger.debug("exiting getPropertyDeclaration(int)");
	}
	
	return declaration;
    }

    public Class<?> getColumnClass(int columnIndex)
    {
	Class c = null;
	
	if ( columnIndex == 0 )
	{
	    c = String.class;
	}
	else if ( columnIndex == 1 )
	{
	    c = Boolean.class;
	}
	else if ( columnIndex == 2 )
	{
	    c = Integer.class;
	}
	
	return c;
    }

    public String getColumnName(int column)
    {
	String result = null;
	
	if ( this.columnNames != null && column >= 0 && column < this.columnNames.length )
	{
	    result = this.columnNames[column];
	}
	
	return result;
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
	boolean editable = false;
	if ( columnIndex == 1 )
	{
	    PropertyDeclaration declaration = this.getPropertyDeclaration(rowIndex);
	    
	    if ( declaration != null )
	    {
		editable = declaration.isEnabledModifiable();
	    }
	}
	else if ( columnIndex == 2 )
	{
	    PropertyDeclaration declaration = this.getPropertyDeclaration(rowIndex);
	    
	    if ( declaration != null )
	    {
		editable = declaration.isEnabled();
	    }
	}
	
	return editable;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
	if ( columnIndex == 1 )
	{
	    PropertyDeclaration declaration = this.getPropertyDeclaration(rowIndex);
	    
	    if ( declaration != null )
	    {
		if ( declaration.isEnabledModifiable() )
		{
		    if ( aValue instanceof Boolean )
		    {
			boolean b = ((Boolean)aValue).booleanValue();
			
			if ( b != declaration.isEnabled() )
			{
			    declaration.setEnabled( ((Boolean)aValue).booleanValue() );
			    
			    if ( ! this.isModified() )
			    {
				this.setModified(true);
			    }
			}
		    }
		    else
		    {
			logger.warn("trying to set '" + PropertyDeclaration.PROPERTY_ENABLED + "' for a PropertyDeclaration with the value " + aValue);
		    }
		}
		else
		{
		    logger.warn("trying to change '" + PropertyDeclaration.PROPERTY_ENABLED + "' for a PropertyDeclaration but cannot be modified");
		}
	    }
	}
	else if ( columnIndex == 2 )
	{
	    PropertyDeclaration declaration = this.getPropertyDeclaration(rowIndex);
	    
	    if ( declaration != null )
	    {
		if ( true )//declaration.isEnabledModifiable() )
		{
		    if ( aValue instanceof Integer )
		    {
			int value = declaration.getPreferredSize();
			if ( ((Integer)aValue) != value )
			{
			    if ( ((Integer)aValue).intValue() >= 0 )
			    {
				declaration.setPreferredSize( (Integer)aValue );

				if ( ! this.isModified() )
				{
				    this.setModified(true);
				}
			    }
			}
		    }
		    else
		    {
			logger.warn("trying to set '" + PropertyDeclaration.PROPERTY_PREFERRED_SIZE + "' for a PropertyDeclaration with the value " + aValue);
		    }
		}
		else
		{
		    logger.warn("trying to change '" + PropertyDeclaration.PROPERTY_PREFERRED_SIZE + "' for a PropertyDeclaration but cannot be modified");
		}
	    }
	}
	else
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.error("trying to set value " + aValue + " at row " + rowIndex + " and at column " + columnIndex);
	    }
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
    public Object getValueAt(int rowIndex, int columnIndex)
    {
	Object value = null;
	
	PropertyDeclaration declaration = this.getPropertyDeclaration(rowIndex);
	
	if ( declaration != null )
	{
	    if ( columnIndex == 0 )
	    {
		if ( this.tableModel != null )
		{
		    value = this.tableModel.getPropertyDisplayNameFor(declaration);
		}
	    }
	    else if ( columnIndex == 1 )
	    {
		value = declaration.isEnabled();
	    }
	    else if ( columnIndex == 2 )
	    {
		value = declaration.getPreferredSize();
	    }
	}
	
	return value;
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
    {
	int result = 0;
	
	if ( this.tableModel != null )
	{
	    result = this.tableModel.getPropertyDeclarationCount();
	}
	
	return result;
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     * 
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount()
    {
	return 3;
    }

    /**
     * return true if the model that is contains data at the given location
     * 
     * @param row the row index
     * @param column the column index
     * @return true if the model that is contains data at the given location
     */
    public boolean containsDataAt(int row, int column)
    {
	return true;
    }
    
}
