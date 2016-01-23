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
import org.siberia.ui.swing.tree.model.MutableTreeModel;
import org.siberia.ui.swing.tree.model.VisualizationTreeModel;
import org.siberia.kernel.resource.event.ResourceListener;
import org.siberia.editor.launch.DefaultEditorLaunchContext;
import org.siberia.ui.swing.treetable.SibTypeListTreeTablePanel;
import org.siberia.ui.swing.treetable.TreeTableModel;
import org.siberia.ui.swing.treetable.introspection.IntrospectionSibTypeListTreeTableModel;
import org.siberia.ui.swing.treetable.introspection.PageablePathConverter;
import org.siberia.ui.swing.table.model.PropertyDeclaration;

/**
 *
 * Resource editor based on a SibTypeListTreeTablePanel
 *
 * @author alexis
 */
@Editor(relatedClasses={org.siberia.kernel.resource.DefaultColdResource.class},
                  description="Editor for instances of ColdResources",
                  name="Extended resource editor",
                  launchedInstancesMaximum=-1)
public class ExtendedResourceEditor extends AbstractResourceEditor implements MouseListener, TreeSelectionListener
{
    /** panel that display the resource */
    private SibTypeListTreeTablePanel panel = null;
    
    /** Creates a new instance of ExtendedResourceEditor */
    public ExtendedResourceEditor()
    {   }
    
    /** return the items that are actually selected
     *	@return an Array of SibType
     */
    protected SibType[] getSelectedItems()
    {
	SibType[] types = this.panel.getTable().getTreeRenderer().getSelectedElements();
//	if ( types == null )
//	{
//	    System.err.println("pas de sélection");
//	}
//	else
//	{
//	    System.err.println("affichage de la sélection : ");
//	    for(int i = 0; i < types.length; i++)
//	    {
//		System.err.println("\t" + types[i].getName());
//	    }
//	}
	
	return types;
    }
    
    /** called when the mode of visualization of the resources changed
     *	@param mode then ew mode of visualization
     */
    protected void visualizationModeChanged(ResourceVisualizationMode mode)
    {
	if ( panel != null )
	{
	    if ( panel.getTable() != null )
	    {
		if ( panel.getTable().getTreeRenderer() != null )
		{
		    panel.getTable().getTreeRenderer().setExpansionMode(convertVisualizationMode(mode));
		}
	    }
	}
    }

    @Override
    public void setInstance(SibType instance)
    {   
	super.setInstance(instance);
	
	if ( panel != null )
	{
	    TreeTableModel model = panel.getTable().getTreeTableModel();
	    
	    if ( model instanceof IntrospectionSibTypeListTreeTableModel )
	    {
		if ( this.getInstance() == null )
		{
		    ((IntrospectionSibTypeListTreeTableModel)model).setRoot(null);
		}
		else
		{
		    ((IntrospectionSibTypeListTreeTableModel)model).setRoot(this.getInstance().getItem());
		}
	    }
	}
    }
    
    /** return the component that display the resources
     *	@return a Component
     */
    protected Component getResourcesComponent()
    {   
        Component component = null;
	ColdResource resource = this.getInstance();
        if ( resource != null )
        {   if ( this.panel == null )
	    {
		this.panel = new SibTypeListTreeTablePanel();
//		this.panel.getTable().setMaximumDisplayedRows(-1);
		IntrospectionSibTypeListTreeTableModel treeTableModel = new IntrospectionSibTypeListTreeTableModel();

		MutableTreeModel treeModel = new MutableTreeModel();
		treeModel.setRoot(resource.getItem());
		treeTableModel.setInnerConfigurableTreeModel(treeModel);
	
		treeTableModel.getInnerTableModel().addPropertyDeclarations(
		    new PropertyDeclaration(SibType.PROPERTY_NAME, true, false, 250));
	
		this.panel.getTable().setTreeTableModel(treeTableModel);
	
		treeTableModel.setPathConverter(new PageablePathConverter(this.panel.getTable().getTreeRenderer()));
//
		this.panel.getTable().getTreeRenderer().getSelectionModel().addTreeSelectionListener(this);
	    }
	    
	    component = this.panel;
        }
        return component;
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
                
        if ( e.getSource() == this.panel.getTable().getTreeRenderer() && edit )
        {   SibType type = this.panel.getTable().getTreeRenderer().getElementAt(e.getX(), e.getY());

	    System.out.println("type name : " + type.getName());
            if ( type != null && this.panel.getTable().getTreeRenderer().getModel().isLeaf(type) )
            {   
                try
                {   Kernel.getInstance().getResources().edit(new DefaultEditorLaunchContext(type)); }
                catch (NoLauncherFoundException ex)
                {   ex.printStackTrace(); }
            }
        }
    }
    
}
