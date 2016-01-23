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
package org.siberia.ui.swing.property.enumeration;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.siberia.ui.swing.combo.EnumListCellRenderer;
import org.siberia.ui.swing.combo.model.EnumComboBoxModel;

/**
 *
 * Renderer for item that are part of an Enumeration
 *
 * @author alexis
 */
public class EnumPropertyEditor extends AbstractPropertyEditor
{
    private Object oldValue;
    
    /** create a new EnumPropertyEditor */
    public EnumPropertyEditor()
    {
	this(null);
    }
    
    /** create a new EnumPropertyEditor
     *	@param renderer the renderer to use
     */
    public EnumPropertyEditor(ListCellRenderer renderer)
    {
        editor = new JComboBox()
        {
            public void setSelectedItem(Object anObject)
            {
//                oldValue = getSelectedItem();
                super.setSelectedItem(anObject);
            }
        };
        
	
        final JComboBox combo = (JComboBox)editor;
        combo.setMaximumRowCount(20);
        combo.setRenderer(renderer == null ? new EnumListCellRenderer() : renderer);
        
        combo.addPopupMenuListener(new PopupMenuListener()
        {
            public void popupMenuCanceled(PopupMenuEvent e)
            {}
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {
                firePropertyChange(oldValue,
                                    combo.getSelectedItem());
            }
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {}
        });
        combo.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    firePropertyChange(oldValue,
                                        combo.getSelectedItem());
                }
            }
        });
        combo.setSelectedIndex(-1);
    }
    
    public Object getValue()
    {
	Object result = ((JComboBox)editor).getSelectedItem();
	
	boolean equals = true;
	
	if ( result == null )
	{
	    if ( this.oldValue != null )
	    {
		equals = false;
	    }
	}
	else
	{
	    equals = result.equals(this.oldValue);
	}
	
	if ( equals )
	{
	    result = this.oldValue;
	}
	
	return result;
    }
    
    public void setValue(Object value)
    {
        Object newValue = value;
	
	this.oldValue = value;
        
        JComboBox combo = (JComboBox)editor;

        if ( newValue != null )
        {
            boolean changeModel = true;
            if ( combo.getModel() instanceof EnumComboBoxModel &&
                 ((EnumComboBoxModel)combo.getModel()).getEnumClass().equals(newValue.getClass()) )
                changeModel = false;

            if ( changeModel )
                combo.setModel(new EnumComboBoxModel(newValue.getClass()));

            Object current = null;
            int index = -1;
            for (int i = 0, c = combo.getModel().getSize(); i < c; i++)
            {
                current = combo.getModel().getElementAt(i);
                if (newValue == current || (current != null && current.equals(newValue)))
                {
                    index = i;
                    break;
                }
            }
            ((JComboBox)editor).setSelectedIndex(index);
        }
    }
    
    public void setAvailableValues(Object[] values)
    {
        ((JComboBox)editor).setModel(new DefaultComboBoxModel(values));
    }
}
