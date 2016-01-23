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
import javax.swing.table.AbstractTableModel;

/**
 *
 * Extension of the AbstractTableModel that provide a PropertyChangesupport
 *  and could manage the entire editability of the model via setEditable method<br>
 *  It is the first implementation of interface SiberiaTableModel
 *
 * @author alexis
 */
public abstract class AbstractSiberiaTableModel extends AbstractTableModel implements SiberiaTableModel
{   
    /** property model editable */
    public static final String PROPERTY_EDITABLE           = "editable";
    
    /** PropertyChangeSupport */
    private PropertyChangeSupport              support     = new PropertyChangeSupport(this);
    
    /** indicate if the model is editable */
    private boolean                            editable    = true;
    
    /**
	 * Creates a new instance of AbstractSiberiaTableModel
	 */
    public AbstractSiberiaTableModel()
    {	}

    /** return true if the model is editable
     *  @return a boolean
     */
    public boolean isEditable()
    {   return editable; }

    public void setEditable(boolean editable)
    {
        if ( editable != this.isEditable() )
        {   
            this.editable = editable;
            
            this.firePropertyChangeEvent(PROPERTY_EDITABLE, ! editable, editable);
        }
    }
    
    /** indicates if a cell is editable
     *  @param rowIndex
     *  @param columnIndex
     *  @return true if the cell at the given position is editable
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {   boolean result = true;
        
        if ( ! this.isEditable() )
        {   result = false; }
        
        return result;
    }
    
    /** return the kind of item for the given row and column
     *
     *	this is an extension to TableModel to provide severall classes per column<br>
     *	this helps some table to provide appropriate renderers and editors in the same column.<br>
     *
     *	@param row the row index
     *	@param column the column index
     *	@return a Class
     */
    public Class getColumnClass(int row, int column)
    {
	/** default, use the getColumnClass of the TableModel */
	return this.getColumnClass(column);
    }
    
    /* #########################################################################
     * #################### PropertyChangeListener methods #####################
     * ######################################################################### */
    
    /** add a new PropertyChangeListener
     *  @param l a listener
     */
    public void addPropertyChangeListener(PropertyChangeListener l)
    {   this.support.addPropertyChangeListener(l); }
    
    /** add a new PropertyChangeListener
     *  @param propertyName the name of a property
     *  @param l a listener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener l)
    {   this.support.addPropertyChangeListener(propertyName, l); }
    
    /** remove a PropertyChangeListener
     *  @param l a listener
     */
    public void removePropertyChangeListener(PropertyChangeListener l)
    {   this.support.removePropertyChangeListener(l); }
    
    /** remove a PropertyChangeListener
     *  @param propertyName the name of a property
     *  @param l a listener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener l)
    {   this.support.removePropertyChangeListener(propertyName, l); }
    
    /** fire a PropertyChangeEvent
     *  @param propertyName the name of the property
     *  @param oldValue the old value
     *  @param newValue the new value
     */
    protected void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue)
    {   this.support.firePropertyChange(propertyName, oldValue, newValue); }
    
}
