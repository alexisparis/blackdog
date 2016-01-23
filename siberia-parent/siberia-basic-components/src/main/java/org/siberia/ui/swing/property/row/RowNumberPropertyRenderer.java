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
package org.siberia.ui.swing.property.row;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.siberia.ui.swing.table.RowNumber;

/**
 *
 * Renderer for item of kind RowNumber
 *
 * @author alexis
 */
public class RowNumberPropertyRenderer extends DefaultTableRenderer
{
    /** map linking basic color and color to use */
    private Map<Color, Color> colors = null;//new WeakHashMap<Color, Color>(5);
    
    /**
     * Creates a new instance of RowNumberPropertyRenderer
     */
    public RowNumberPropertyRenderer()
    {	}
    
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, 
                                                   int row, int column)
    {   
	Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	
	if ( component instanceof JLabel )
	{
	    /* verify that the model installed is correct according to the value */
	    if ( value instanceof RowNumber )
	    {   
		((JLabel)component).setText( Long.toString(((RowNumber)value).getValue()) );
	    }
	    else
	    {   
		((JLabel)component).setText("");
	    }
	    
	    ((JLabel)component).setHorizontalAlignment(SwingConstants.RIGHT);
	}

	component.setBackground(UIManager.getColor("Label.background"));
	component.setForeground(UIManager.getColor("Label.foreground"));
        
        return component;
    }
    
}
