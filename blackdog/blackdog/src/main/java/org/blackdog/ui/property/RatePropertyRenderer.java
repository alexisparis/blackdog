/*
 * blackdog : audio player / manager
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.ui.property;

import java.awt.Color;
import java.awt.Component;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.blackdog.type.base.Rate;
import org.siberia.ui.swing.combo.EnumListCellRenderer;
import org.siberia.ui.swing.combo.model.EnumComboBoxModel;
import org.siberia.ui.swing.rate.JRate;

/**
 *
 * Renderer for item that are part of an Enumeration
 *
 * @author alexis
 */
public class RatePropertyRenderer implements TableCellRenderer
{
    /** component */
    private JRate rateComponent = null;
    
    /**
     * Creates a new instance of RatePropertyRenderer
     */
    public RatePropertyRenderer()
    {
	super();
	
	this.rateComponent = new JRate();
	this.rateComponent.setOpaque(true);
	
	this.rateComponent.setStarWidth(17);
	
	this.rateComponent.setSelectedStarColor(new Color(246, 255, 10));
	this.rateComponent.setUnselectedStarColor(new Color(226, 226, 226));
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, 
                                                   int row, int column)
    {   
        /* verify that the model installed is correct according to the value */
        if ( value instanceof Rate )
        {   
	    int rate = ((Rate)value).getRateValue();
	    
	    if ( rate < 0 )
	    {
		rate = 0;
	    }
	    else if ( rate > 10 )
	    {
		rate = 10;
	    }
	    
	    this.rateComponent.setRate( (short)rate );
        }
        else
        {   
            this.rateComponent.setRate(null);
        }
	
	if (isSelected)
	{
	    this.rateComponent.setBackground(table.getSelectionBackground());
	    this.rateComponent.setForeground(table.getSelectionForeground());
	}
	else
	{
	    this.rateComponent.setBackground(table.getBackground());
	    this.rateComponent.setForeground(table.getForeground());
	}
        
        return this.rateComponent;
    }
    
}
