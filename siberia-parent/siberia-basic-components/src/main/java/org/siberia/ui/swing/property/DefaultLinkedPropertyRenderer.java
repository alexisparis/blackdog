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
package org.siberia.ui.swing.property;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * default renderer for item that only needs to display a link button and their toString() representation
 *
 * @author alexis
 */
public class DefaultLinkedPropertyRenderer extends LinkedPropertyRenderer<JTextField>
{
    /** textfield */
    private JTextField field = null;
    
    /**
     * return a Component that will be insert at first index in the container
     * 
     * @param table
     * @param value
     * @param selected
     * @param hasFocus
     * @param row
     * @param column
     * @return a Component
     */
    protected JTextField getInnerComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
	if ( field == null )
	{
	    field = new JTextField();
	    field.setBorder(BorderFactory.createEmptyBorder());
	}
	
	if ( table != null )
	{
	    field.setEnabled(table.isCellEditable(row, column));
	}
	
	field.setText( value == null ? "" : value.toString() );
	
	if ( table != null )
	{
	    if (isSelected)
	    {
		field.setBackground(table.getSelectionBackground());
		field.setForeground(table.getSelectionForeground());
	    }
	    else
	    {
		field.setBackground(table.getBackground());
		field.setForeground(table.getForeground());
	    }
	}
	
	return field;
    }
    
    /** return the textfield
     *	@return a JTextField
     */
    public JTextField getTextField()
    {	return this.field; }
    
    
}
