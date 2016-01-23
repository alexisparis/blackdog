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
package org.siberia.ui.swing.property.simple;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 *
 * Simple cell renderer
 *
 * @author alexis
 */
public class SimpleCellRenderer extends DefaultCellRenderer
{
    /**
     * Creates a new instance of SimpleCellRenderer
     */
    public SimpleCellRenderer()
    {	}
    
    /** method that allow to customize a JLabel according to the value
     *	@param label a JLabel
     *	@param value an Object
     */
    protected void customizeLabel(JLabel label, Object value)
    {	}
	
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
	Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	
	if ( c instanceof JLabel )
	{
	    this.customizeLabel( (JLabel)c, value );
	
	    if (isSelected)
	    {
		((JLabel)c).setBackground(table.getSelectionBackground());
	        ((JLabel)c).setForeground(table.getSelectionForeground());
	    }
	    else
	    {
		((JLabel)c).setBackground(table.getBackground());
		((JLabel)c).setForeground(table.getForeground());
	    }
	}
	
	return c;
    }
    
}
