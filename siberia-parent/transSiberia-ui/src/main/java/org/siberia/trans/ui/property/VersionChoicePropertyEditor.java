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

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.siberia.trans.type.plugin.Version;
import org.siberia.trans.type.plugin.VersionChoice;
import org.siberia.ui.swing.combo.EnumListCellRenderer;
import org.siberia.ui.swing.combo.model.EnumComboBoxModel;

/**
 *
 * Renderer for item that are part of an Enumeration
 *
 * @author alexis
 */
public class VersionChoicePropertyEditor extends AbstractPropertyEditor
{
    private VersionChoice oldValue;
    
    /**
     * create a new VersionChoicePropertyEditor
     */
    public VersionChoicePropertyEditor()
    {
	final JComboBox combo = new JComboBox();
	combo.setRenderer(new VersionChoiceListCellRenderer());
        combo.setMaximumRowCount(20);
	combo.setModel(new VersionChoiceComboBoxModel());
        this.editor = combo;
        
        combo.addPopupMenuListener(new PopupMenuListener()
        {
            public void popupMenuCanceled(PopupMenuEvent e)
            {	}
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {
                firePropertyChange(oldValue,
                                   combo.getSelectedItem());
            }
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {	}
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
    
    public VersionChoice getValue()
    {
	VersionChoice result = this.oldValue;
	
	/** get the selected item and if it does not changed,
	 *  then return oldValue
	 *  else, create a new VersionChoice according to oldValue and set the selected version
	 */
	boolean changed = false;
	Version selectedVersion = null;
	ComboBoxModel model = ((JComboBox)this.editor).getModel();
	if ( model instanceof VersionChoiceComboBoxModel )
	{
	    Object selection = ((VersionChoiceComboBoxModel)model).getSelectedItem();
	    if ( selection instanceof Version )
	    {
		if ( this.oldValue != null )
		{   
		    changed = ! ((Version)selection).equals( this.oldValue.getSelectedVersion() );
		}
		else
		{
		    changed = true;
		}
		    
		if ( changed )
		{
		    selectedVersion = (Version)selection;
		}
	    }
	}
	
	if ( changed )
	{
	    VersionChoice other = ((VersionChoice)this.oldValue).copy();
	    other.setSelectedVersion(selectedVersion);
	    result = other;
	}
	
	return result;
    }
    
    public void setValue(Object value)
    {
	VersionChoice choice = null;
	if ( value instanceof VersionChoice )
	{   choice = (VersionChoice)value; }
	
	this.oldValue = choice;
	
	ComboBoxModel model = ((JComboBox)this.editor).getModel();
	if ( model instanceof VersionChoiceComboBoxModel )
	{
	    ((VersionChoiceComboBoxModel)model).setVersionChoice(choice);
	}
    }
}
