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

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
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
public class RatePropertyEditor extends AbstractPropertyEditor
{
    private Object oldValue;
    
    /**
     * create a new RatePropertyEditor
     */
    public RatePropertyEditor()
    {
	this(null);
    }
    
    /**
     * create a new RatePropertyEditor
     * 
     * @param renderer the renderer to use
     */
    public RatePropertyEditor(ListCellRenderer renderer)
    {
	editor = new JRate();
	
	((JRate)editor).setStarWidth(17);
	((JRate)editor).setSelectedStarColor(new Color(246, 255, 10));
	((JRate)editor).setUnselectedStarColor(new Color(226, 226, 226));
    }
    
    public Object getValue()
    {
	Object result = ((JRate)editor).getRate();
	
	boolean equals = false;
	
	if ( result == null )
	{
	    if ( this.oldValue == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    if ( result instanceof Short )
	    {
		if ( this.oldValue instanceof Rate )
		{
		    equals = ((Rate)this.oldValue).getRateValue() == ((Short)result).intValue();
		}
	    }
	}
	
	if ( equals )
	{
	    result = this.oldValue;
	}
	else if ( result instanceof Short )
	{
	    Rate rate = new Rate();
	    rate.setRateValue( ((Short)result).intValue() );
	    result = rate;
	}
	
	return result;
    }
    
    public void setValue(Object value)
    {
        Object newValue = value;
	
	this.oldValue = value;
        
	if ( value instanceof Rate )
	{
	    ((JRate)editor).setRate( (short)((Rate)value).getRateValue() );
	}
	else 
	{
	    ((JRate)editor).setRate( null );
	}
    }
    
//    public void setAvailableValues(Object[] values)
//    {
//        ((JComboBox)editor).setModel(new DefaultComboBoxModel(values));
//    }
}
