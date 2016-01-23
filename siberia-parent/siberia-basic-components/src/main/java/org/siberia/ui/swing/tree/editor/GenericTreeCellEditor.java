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
package org.siberia.ui.swing.tree.editor;

import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultTreeCellEditor;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.JTree;
import java.awt.Component;
import java.awt.Font;
import java.util.EventObject;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.tree.TreePath;
import org.siberia.ResourceLoader;
import org.siberia.TypeInformationProvider;
import org.siberia.exception.ResourceException;
import org.siberia.type.SibType;
import org.siberia.ui.swing.TextFieldVerifier;
import org.siberia.ui.swing.tree.GenericTree;
import org.siberia.ui.IconCache;
import org.siberia.ui.swing.tree.model.SibTypeLink;

/**
 *
 * generic class to edit item on a GenericTree
 *
 * @author alexis
 */
public class GenericTreeCellEditor extends DefaultTreeCellEditor
{
    
    /** Creates a new instance of ProjectTreeCellRenderer
     *  @param tree a GenericTree
     *  @param renderer a DefaultTreeCellRenderer
     */
    public GenericTreeCellEditor(GenericTree tree, DefaultTreeCellRenderer renderer)
    {   super(tree, renderer); }
    
    public Component getTreeCellEditorComponent(JTree tree, Object value,
						boolean isSelected,
						boolean expanded,
						boolean leaf, int row)
    {   Component c = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
        	
        if ( this.editingComponent != null )
        {   if ( this.editingComponent instanceof JTextField )
            {   JTextField field = (JTextField)this.editingComponent;
                
		SibType type = null;
                if ( value instanceof SibType )
                {   
		    type = (SibType)value;
		}
		else if ( value instanceof SibTypeLink )
		{
		    type = ((SibTypeLink)value).getLinkedItem();
		}
		
		if ( type == null )
		{
		    field.setText("");
		}
		else
		{
		    field.setText(type.getName());
		}
            }
        }
        
        return c;
    }
    
    public void updateUI()
    {
	if ( this.editingComponent != null )
	{
	    SwingUtilities.updateComponentTreeUI(this.editingComponent);
	}
	if ( this.editingContainer != null )
	{
	    SwingUtilities.updateComponentTreeUI(this.editingContainer);
	}
	
	if ( this.realEditor != null )
	{
	    Component c = this.realEditor.getTreeCellEditorComponent(this.tree, "", false, true, false, 0);
	    
	    if ( c != null )
	    {
		SwingUtilities.updateComponentTreeUI(c);
	    }
	}
    }

    @Override
    protected void determineOffset(JTree tree, Object value,
				   boolean isSelected, boolean expanded,
				   boolean leaf, int row)
    {
        editingIcon = IconCache.getInstance().get(value);
        
        offset = renderer.getIconTextGap() + (editingIcon == null ? 0 : editingIcon.getIconWidth());
    }

    /**
     * If the <code>realEditor</code> returns true to this
     * message, <code>prepareForEditing</code>
     * is messaged and true is returned.
     */
    public boolean isCellEditable(EventObject event)
    {   if ( ! super.isCellEditable(event) )
            return false;
        
        boolean editable = true;
        if ( this.tree != null )
        {   /* get the selection in the tree and look if it has to be editable */
            if ( this.tree.getSelectionCount() != 1 )
                editable = false;
            else
            {   TreePath path = this.tree.getSelectionPath();
                Object last = path.getLastPathComponent();
		
		SibType type = null;
		
                if ( last instanceof SibType )
		{
		    type = (SibType)last;
		}
		else if ( last instanceof SibTypeLink )
		{
		    type = ((SibTypeLink)last).getLinkedItem();
		}
		
		if ( type == null )
		{
		    editable = false;
		}
		else
		{
                    editable = type.nameCouldChange() && ! type.isReadOnly();
		}
            }
        }
        
        return editable;
    }

    /**
     * Starts the editing timer.
     */
    protected void startEditingTimer() {
	if(timer == null) {
	    timer = new Timer(1000, this);
	    timer.setRepeats(false);
	}
	timer.start();
    }
}
