/* 
 * TransSiberia-ui : siberia plugin frontend for TransSiberia
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
package org.siberia.trans.ui.property;

import java.awt.Component;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.siberia.trans.type.plugin.VersionChoice;
import org.siberia.ui.swing.combo.EnumListCellRenderer;
import org.siberia.ui.swing.combo.model.EnumComboBoxModel;

/**
 *
 * Renderer for item that are part of an Enumeration
 *
 * @author alexis
 */
public class VersionChoicePropertyRenderer implements TableCellRenderer
{
    /** component */
    private JComboBox combo    = null;
    
    /**
     * Creates a new instance of VersionChoicePropertyRenderer
     */
    public VersionChoicePropertyRenderer()
    { 
	this.combo = new JComboBox();
	this.combo.setRenderer(new VersionChoiceListCellRenderer());
        this.combo.setModel(new VersionChoiceComboBoxModel());
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, 
                                                   int row, int column)
    {   
	
	ComboBoxModel model = this.combo.getModel();
	if ( model instanceof VersionChoiceComboBoxModel )
	{
	    /* verify that the model installed is correct according to the value */
	    if ( value instanceof VersionChoice )
	    {   
		((VersionChoiceComboBoxModel)model).setVersionChoice( (VersionChoice)value );
	    }
	    else
	    {
		((VersionChoiceComboBoxModel)model).setVersionChoice( null );
	    }
	}
        
        return this.combo;
    }
    
}
