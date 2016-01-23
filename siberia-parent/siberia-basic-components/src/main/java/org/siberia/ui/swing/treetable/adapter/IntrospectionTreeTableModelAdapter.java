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
package org.siberia.ui.swing.treetable.adapter;

import javax.swing.JTree;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import org.apache.log4j.Logger;
import org.siberia.ui.swing.table.model.ObjectsTableModel;
import org.siberia.ui.swing.treetable.TreeTableModel;
import org.siberia.ui.swing.treetable.introspection.IntrospectionSibTypeListTreeTableModel;


/**
 *
 * Adapter that has to be used with IntrospectionSibTypeListTreeTableModel
 *  to be able to propagate TableModelEvent
 *
 * @author Alexis
 */
public class IntrospectionTreeTableModelAdapter extends
	    TreeTableModelAdapter<IntrospectionSibTypeListTreeTableModel>
						implements TableModelListener,
							   ObjectsTableModel
{
    /** logger */
    private Logger logger = Logger.getLogger(IntrospectionTreeTableModelAdapter.class);
    
    public IntrospectionTreeTableModelAdapter(IntrospectionSibTypeListTreeTableModel treeTableModel,
				              JTree tree)
    {
	super(treeTableModel, tree);
	
	if ( treeTableModel == null )
	{
	    throw new IllegalArgumentException("the tree table model cannot be null");
	}
	
	this.getAdaptedModel().getInnerTableModel().addTableModelListener(this);
    }
    
    /** return the item at the given index in the list
     *  @param index an integer
     *  @return an Object
     */
    public synchronized Object getItem(int index)
    {
	return this.getAdaptedModel().getInnerTableModel().getItem(index);
    }
    
    /** return the index of the given item
     *  @param item an item that managed by the model
     *  @return the index of the item in the table or -1 if it does not appear
     */
    public int getIndexOfItem(Object item)
    {
	return this.getAdaptedModel().getInnerTableModel().getIndexOfItem(item);
    }
    
    /* #########################################################################
     * #################### TableModelListener implementation ##################
     * ######################################################################### */
    
    /**
     * This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e)
    {
	if ( e.getSource() == this.getAdaptedModel() )
	{
	    int newColumn = -1;
	    
	    if ( e.getColumn() == TableModelEvent.ALL_COLUMNS )
	    {
		newColumn = TableModelEvent.ALL_COLUMNS;
	    }
	    else
	    {
		newColumn = e.getColumn() + 1;
	    }
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("received an event from " + e.getSource().getClass() + " : " + e);
	    }
	    
	    TableModelEvent newEvent = new TableModelEvent(this, e.getFirstRow(), e.getLastRow(), newColumn, e.getType());
	    
	    this.fireTableChanged(newEvent);
	}
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e)
    {   
	this.getAdaptedModel().getInnerTableModel().clearCache();
	
	super.treeStructureChanged(e);
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e)
    {   
	// TODO
//	this.getAdaptedModel().clearCacheAt(e.getTreePath());
	this.getAdaptedModel().getInnerTableModel().clearCache();
	
	super.treeNodesRemoved(e);
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e)
    {   
	// TODO
//	this.getAdaptedModel().ensureCacheExistenceAt(e.getTreePath());
	this.getAdaptedModel().getInnerTableModel().clearCache();
	
	super.treeNodesInserted(e);
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e)
    {   
	super.treeNodesChanged(e);
    }
}


