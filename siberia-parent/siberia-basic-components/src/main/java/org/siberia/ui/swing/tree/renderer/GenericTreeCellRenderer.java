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
package org.siberia.ui.swing.tree.renderer;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Hashtable;
import javax.swing.SwingUtilities;
import org.siberia.exception.ResourceException;
import org.siberia.type.SibType;
import org.siberia.ResourceLoader;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.JTree;
import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import org.siberia.TypeInformationProvider;
import org.siberia.ui.IconCache;
import org.siberia.ui.swing.tree.model.SibTypeLink;

/**
 *
 * generic class to render item on a GenericTree
 *
 * @author alexis
 */
public class GenericTreeCellRenderer extends DefaultTreeCellRenderer
{
    
    /** Creates a new instance of ProjectTreeCellRenderer */
    public GenericTreeCellRenderer()
    {   super(); }
    
    /** return the component to render in a tree **/
    public Component getTreeCellRendererComponent(JTree tree,
                                              Object  value,
                                              boolean sel,
                                              boolean expanded,
                                              boolean leaf,
                                              int     row,
                                              boolean hasFocus)
    {   /* use DefaultTreeCellRenderer behaviour */
        Component compo = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
//        if ( compo instanceof JComponent )
//        {   
//	    if ( selected )
//            {   ((JComponent)compo).setOpaque(false); }
//            else
//            {   ((JComponent)compo).setOpaque(true); }
//        }
	
	SibType type = null;
        
        /* set the name that will be displayed */
        if ( value instanceof SibType )
        {   
	    type = (SibType)value;
	}
	else if ( value instanceof SibTypeLink )
	{
	    type = ((SibTypeLink)value).getLinkedItem();
	}
        
        /* get the icon corresponding to the object type we have to render */
        Icon img = IconCache.getInstance().get(type);
        
        /* change icon */
        this.setIcon(img);
	
	if ( type == null )
	{
	    this.setText("");
	}
        else if ( value instanceof DefaultMutableTreeNode )
        {   /*System.out.println("user object : " + ((DefaultMutableTreeNode)value).getUserObject().getClass());*/ }
	else
	{
	    this.setText( type.getName());
            
            /* set tooltip */
            this.setToolTipText( (String)type.getClass().getName() );
        }
        
        return compo;
    }
}
