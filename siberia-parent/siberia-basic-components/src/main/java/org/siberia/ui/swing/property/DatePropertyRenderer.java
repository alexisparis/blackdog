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

import com.l2fprod.common.swing.renderer.DateRenderer;
import java.awt.Component;
import java.text.DateFormat;
import javax.swing.JComponent;
import javax.swing.JTable;

/**
 *
 * Override the DateRenderer
 *
 * @author alexis
 */
public class DatePropertyRenderer extends DateRenderer
{
    
    /** Creates a new instance of DatePropertyRenderer
     *	@param format a DateFormat
     */
    public DatePropertyRenderer(DateFormat format)
    {
	super(format);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
	Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	
	if (isSelected)
	{
	    c.setBackground(table.getSelectionBackground());
	    c.setForeground(table.getSelectionForeground());
	}
	else
	{
	    c.setBackground(table.getBackground());
	    c.setForeground(table.getForeground());
	}
	
	return c;
    }
}
