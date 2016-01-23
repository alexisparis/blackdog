/* 
 * Siberia resource editor : siberia plugin defining resource editor
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
package org.siberia.ui.editor.impl;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import org.siberia.kernel.Kernel;
import org.siberia.kernel.editor.NoLauncherFoundException;
import org.siberia.kernel.resource.ColdResource;
import org.siberia.kernel.resource.ResourceVisualizationMode;
import org.siberia.type.SibType;
import org.siberia.editor.annotation.Editor;
import org.siberia.ui.swing.tree.GenericTree;
import org.siberia.ui.swing.tree.model.GenericTreeModel;
import org.siberia.ui.swing.tree.model.MutableTreeModel;
import org.siberia.ui.swing.tree.model.VisualizationTreeModel;
import org.siberia.kernel.resource.event.ResourceListener;
import org.siberia.editor.launch.DefaultEditorLaunchContext;

/**
 *
 * Resource editor based on a GenericTree
 *
 * @author alexis
 */
@Editor(relatedClasses={org.siberia.kernel.resource.DefaultColdResource.class},
                  description="Editor for instances of ColdResources",
                  name="Resource editor",
                  launchedInstancesMaximum=-1)
public class DefaultResourceEditor extends AbstractResourceEditor implements MouseListener, TreeSelectionListener
{
    /** generic tree that display the resource */
    private GenericTree      tree             = null;
    
    /** scroll pane */
    private JScrollPane      scroll           = null;
    
    /** Creates a new instance of ResourceEditor */
    public DefaultResourceEditor()
    {   }
    
    /** return the items that are actually selected
     *	@return an Array of SibType
     */
    protected SibType[] getSelectedItems()
    {
	return this.tree.getSelectedElements();
    }

    @Override
    public void setInstance(SibType instance)
    {   
	super.setInstance(instance);
	
	if ( tree != null )
	{
	    TreeModel model = tree.getModel();
	    
	    if ( model instanceof GenericTreeModel )
	    {
		if ( this.getInstance() == null )
		{
		    ((GenericTreeModel)model).setRoot(null);
		}
		else
		{
		    ((GenericTreeModel)model).setRoot(this.getInstance().getItem());
		}
	    }
	}
    }
    
    /** called when the mode of visualization of the resources changed
     *	@param mode then ew mode of visualization
     */
    protected void visualizationModeChanged(ResourceVisualizationMode mode)
    {
	if ( tree != null )
	{
	    tree.setExpansionMode(convertVisualizationMode(mode));
	}
    }
    
    /** return the GenericTree used by this editor
     *	@return a GenericTree
     */
    protected GenericTree getTree()
    {
	return this.tree;
    }
    
    /** return the JScrollPane used by this editor
     *	@return a JScrollPane
     */
    protected JScrollPane getScrollPane()
    {
	return this.scroll;
    }
    
    /** method which initialize the graphical components of the editor */
    protected void initializeGraphicalComponents()
    {
	ColdResource resource = this.getInstance();
	
	if ( this.tree == null )
	{
	    this.tree = new GenericTree();
	}

	/* mouse listener for edit support */
	this.tree.addMouseListener(this);

	this.tree.getSelectionModel().addTreeSelectionListener(this);

	TreeModel model = null;
	if ( resource.getItem() != null )
	{   
	    SibType root = resource.getItem();
	    
	    if ( root.isReadOnly() )
		model = new VisualizationTreeModel(root);
	    else
		model = new MutableTreeModel(root);
	}

	if ( model != null )
	{   this.tree.setModel(model); }

	this.scroll = new JScrollPane(this.tree);
    }
    
    /** return the component that display the resources
     *	@return a Component
     */
    protected Component getResourcesComponent()
    {   
	if ( this.scroll == null )
	{
	    this.initializeGraphicalComponents();
	}
        return this.scroll;
    }
    
    /* #########################################################################
     * ################## TreeSelectionListener implementation #################
     * ######################################################################### */
    
    /** 
      * Called whenever the value of the selection changes.
      * @param e the event that characterizes the change.
      */
    public void valueChanged(TreeSelectionEvent e)
    {   this.updateKRSelectedItems(); }
    
    /* #########################################################################
     * ###################### MouseListener implementation #####################
     * ######################################################################### */

    public void mouseReleased(MouseEvent e)
    {   }

    public void mousePressed(MouseEvent e)
    {   }

    public void mouseExited(MouseEvent e)
    {   }

    public void mouseEntered(MouseEvent e)
    {   }

    public void mouseClicked(MouseEvent e)
    {   
        boolean edit = false;
        if ( e.getClickCount() >= 2 )
        {   if ( SwingUtilities.isLeftMouseButton(e) )
            {   edit = true; }
        }
        else if ( e.getClickCount() >= 1 )
        {   if ( SwingUtilities.isMiddleMouseButton(e) )
            {   edit = true; }
        }
                
        if ( e.getSource() == this.tree && edit )
        {   SibType type = this.tree.getElementAt(e.getX(), e.getY());

            if ( type != null && this.tree.getModel().isLeaf(type) )
            {   
                try
                {   Kernel.getInstance().getResources().edit(new DefaultEditorLaunchContext(type)); }
                catch (NoLauncherFoundException ex)
                {   ex.printStackTrace(); }
            }
        }
    }
    
}
