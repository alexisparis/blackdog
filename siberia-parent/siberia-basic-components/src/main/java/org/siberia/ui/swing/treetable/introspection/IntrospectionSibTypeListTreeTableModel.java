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
package org.siberia.ui.swing.treetable.introspection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;
import org.siberia.type.SibCollection;
import org.siberia.type.SibType;
import org.siberia.type.event.HierarchicalPropertyChangeEvent;
import org.siberia.ui.swing.error.ErrorEvent;
import org.siberia.ui.swing.error.ErrorHandler;
import org.siberia.ui.swing.table.model.IntrospectionSibTypeListTableModel;
import org.siberia.ui.swing.tree.model.ConfigurableTreeModel;
import org.siberia.ui.swing.tree.model.GenericTreeModel;
import org.siberia.ui.swing.tree.model.SibTypeLink;
import org.siberia.ui.swing.treetable.*;
import org.siberia.ui.swing.treetable.adapter.IntrospectionAdapterFactory;

/**
 *
 * Implémentation of the TreeTableModel based on a IntrospectionSibTypeListTableModel
 *
 * @author alexis
 */
public class IntrospectionSibTypeListTreeTableModel extends AbstractTreeTableModel<GenericTreeModel>
						    implements ErrorHandler
						    
{
    /** logger */
    private Logger                             logger          = Logger.getLogger(IntrospectionSibTypeListTreeTableModel.class);
    
    /** inner IntrospectionSibTypeListTableModel */
    private ExtendedIntroSibTypeListTableModel innerTableModel = null;
    
    /* path converter */
    private PathConverter                      pathConverter   = null;
    
    /** Creates a new instance of IntrospectionSibTypeListTreeTableModel */
    public IntrospectionSibTypeListTreeTableModel()
    {	super();
	
	this.setAdapterFactory(new IntrospectionAdapterFactory());
	
	this.innerTableModel = new ExtendedIntroSibTypeListTableModel();
	
	this.innerTableModel.addErrorHandler(this);
	
	this.addPropertyChangeListener(PROPERTY_INNER_TREE_MODEL, new PropertyChangeListener()
	{
	    public void propertyChange(PropertyChangeEvent evt)
	    {
		if ( evt.getSource() == IntrospectionSibTypeListTreeTableModel.this &&
		     PROPERTY_INNER_TREE_MODEL.equals(evt.getPropertyName())  )
		{
		    SibType rootItem    = null;
		    SibType oldRootItem = null;
		    
		    if ( evt.getNewValue() instanceof ConfigurableTreeModel )
		    {
			Object item = ((ConfigurableTreeModel)evt.getNewValue()).getRoot();
			
			if ( item instanceof SibType )
			{
			    rootItem = (SibType)item;
			}
		    }
		    
		    if ( evt.getOldValue() instanceof ConfigurableTreeModel )
		    {
			Object item = ((ConfigurableTreeModel)evt.getOldValue()).getRoot();
			
			if ( item instanceof SibType )
			{
			    oldRootItem = (SibType)item;
			}
		    }
		    
		    /** indicate to inner table model to listen to modifications of the root item
		     *	of the configurable tree model
		     */
		    innerTableModel.managedItemChangedExt(oldRootItem, rootItem);
		}
	    }
	});
    }
    
    /** return true if the model that is contains data at the given location
     *	@param row the row index
     *	@param column the column index
     *	@return true if the model that is contains data at the given location
     */
    public boolean containsDataAt(int row, int column)
    {
	return this.getInnerTableModel().containsDataAt(row, column);
    }
    
    /** return the inner IntrospectionSibTypeListTableModel
     *	@return a IntrospectionSibTypeListTableModel that is the base model for this tree table model
     */
    public IntrospectionSibTypeListTableModel getInnerTableModel()
    {
	return this.innerTableModel;
    }
    
    /** initialize the path converter to use
     *	@param converter a PathConverter
     */
    public void setPathConverter(PathConverter converter)
    {
	this.pathConverter = converter;
    }
    
    /** return the path converter to use
     *	@return a PathConverter
     */
    public PathConverter getPathConverter()
    {
	return this.pathConverter;
    }
    
    /** method that returns the index where the given node is represented
     *	@param node an Object in the tree
     *	@return the row index or -1 if the row index could not be discovered
     */
    protected int getRowIndexForNode(Object node)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getRowIndexForNode(" + node + ")");
	}
	/** build a new TreePath according to node */
	TreePath path = this.getInnerConfigurableTreeModel().getTreePathForNode(node);
	
	int index = this.getPathConverter().getRowForPath(path);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getRowIndexForNode(" + node + ") returns " + index);
	}
	
	return index;
    }

    /**
     * Returns the root of the tree.  Returns <code>null</code>
     * only if the tree has no nodes.
     * 
     * @return the root of the tree
     */
    public SibType getRoot()
    {
	SibType type = null;
	
	SibTypeLink link = this.getInnerConfigurableTreeModel().getRoot();
	if ( link != null )
	{
	    type = link.getLinkedItem();
	}
	
	return type;
    }

    /**
     * Returns the index of child in parent.  If either <code>parent</code>
     * or <code>child</code> is <code>null</code>, returns -1.
     * If either <code>parent</code> or <code>child</code> don't
     * belong to this tree model, returns -1.
     * 
     * @param parent a node in the tree, obtained from this data source
     * @param child the node we are interested in
     * @return the index of the child in the parent, or -1 if either
     *    <code>child</code> or <code>parent</code> are <code>null</code>
     *    or don't belong to this tree model
     */
    public int getIndexOfChild(Object parent, Object child)
    {
	return this.getInnerConfigurableTreeModel().getIndexOfChild(parent, child);
    }

    /**
     * Returns the number ofs availible column.
     */
    public int getColumnCount()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getColumnCount()");
	}
	int count = this.innerTableModel.getColumnCount() + 1;
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getColumnCount() returns " + count);
	}
	
	return count;
    }

    /**
     * Sets the value for node <code>node</code>, 
     * at column number <code>column</code>.
     */
    public void setValueAt(Object aValue, Object node, int column)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling setValueAt(" + aValue + ", " + node + ", " + column + ")");
	}
	/** found the rowIndex according to the given node */
	int rowIndex = this.getRowIndexForNode(node);
	
	if ( rowIndex >= 0 )
	{
	    if ( column > 0 )
	    {
		this.innerTableModel.setValueAt(aValue, rowIndex, column - 1);
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of setValueAt(" + aValue + ", " + node + ", " + column + ")");
	}
    }

    /**
     * Indicates whether the the value for node <code>node</code>, 
     * at column number <code>column</code> is editable.
     */
    public boolean isCellEditable(Object node, int column)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling isCellEditable(" + node + ", " + column + ")");
	}
	boolean editable = false;
	
	if ( column == 0 )
	{
	    editable = true;
	}
	else
	{
	    /** found the rowIndex according to the given node */
	    int rowIndex = this.getRowIndexForNode(node);

	    if ( rowIndex >= 0 )
	    {
		editable = this.innerTableModel.isCellEditable(rowIndex, column - 1);
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("inner table model set editable to " + editable);
		}
	    }
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("isCellEditable(" + node + ", " + column + ") returns " + editable);
	}
	
	return editable;
    }

    /**
     * Returns the value to be displayed for node <code>node</code>, 
     * at column number <code>column</code>.
     */
    public Object getValueAt(Object node, int column)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getValueAt(" + node + ", " + column + ")");
	}
	Object value = null;
	
	if ( column > 0 )
	{
	    /** found the rowIndex according to the given node */
	    int rowIndex = this.getRowIndexForNode(node);

	    if ( rowIndex >= 0 )
	    {
		value = this.innerTableModel.getValueAt(rowIndex, column - 1);
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getValueAt(" + node + ", " + column + ") returns " + value);
	}
	
	return value;
    }

    /**
     * Returns the child of <code>parent</code> at index <code>index</code>
     * in the parent's
     * child array.  <code>parent</code> must be a node previously obtained
     * from this data source. This should not return <code>null</code>
     * if <code>index</code>
     * is a valid index for <code>parent</code> (that is <code>index >= 0 &&
     * index < getChildCount(parent</code>)).
     * 
     * @param parent  a node in the tree, obtained from this data source
     * @return the child of <code>parent</code> at index <code>index</code>
     */
    public SibType getChild(Object parent, int index)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getChildAt(" + parent + ", " + index + ")");
	}
	SibType type = null;
	
	SibTypeLink link = this.getInnerConfigurableTreeModel().getChild(parent, index);
	if ( link != null )
	{
	    type = link.getLinkedItem();
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getChildAt(" + parent + ", " + index + ") return " + type);
	}
	
	return type;
    }

    /**
     * Returns <code>true</code> if <code>node</code> is a leaf.
     * It is possible for this method to return <code>false</code>
     * even if <code>node</code> has no children.
     * A directory in a filesystem, for example,
     * may contain no files; the node representing
     * the directory is not a leaf, but it also has no children.
     * 
     * @param node  a node in the tree, obtained from this data source
     * @return true if <code>node</code> is a leaf
     */
    public boolean isLeaf(Object node)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling isLeaf(" + node + ")");
	}
	boolean leaf = this.getInnerConfigurableTreeModel().isLeaf(node);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("isLeaf(" + node + ") returns " + leaf);
	}
	
	return leaf;
    }

    /**
     * Returns the number of children of <code>parent</code>.
     * Returns 0 if the node
     * is a leaf or if it has no children.  <code>parent</code> must be a node
     * previously obtained from this data source.
     * 
     * @param parent  a node in the tree, obtained from this data source
     * @return the number of children of the node <code>parent</code>
     */
    public int getChildCount(Object parent)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getChildCount(" + parent + ")");
	}
	int childCount = this.getInnerConfigurableTreeModel().getChildCount(parent);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getChildCount(" + parent + ") returns " + childCount);
	}
	
	return childCount;
    }

    /**
     * Returns the name for column number <code>column</code>.
     */
    public String getColumnName(int column)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getColumnName(" + column + ")");
	}
	String name = null;
	
	if ( column == 0 )
	{
	    name = "Structure";
	}
	else
	{
	    name = this.getInnerTableModel().getColumnName(column - 1);
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getColumnName(" + column + ") returns " + name);
	}
	
	return name;
    }

    /**
     * Returns the type for column number <code>column</code>.
     */
    public Class getColumnClass(int column)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getColumnClass(" + column + ")");
	}
	Class cls = null;
	
	if ( column == 0 )
	{
	    cls = javax.swing.JTree.class;
	}
	else
	{
	    cls = this.getInnerTableModel().getColumnClass(column - 1);
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getColumnClass(" + column + ") returns " + cls);
	}
	
	return cls;
    }

    /**
     * Messaged when the user has altered the value for the item identified
     * by <code>path</code> to <code>newValue</code>. 
     * If <code>newValue</code> signifies a truly new value
     * the model should post a <code>treeNodesChanged</code> event.
     * 
     * @param path path to the node that the user has altered
     * @param newValue the new value from the TreeCellEditor
     */
    public void valueForPathChanged(TreePath path, Object newValue)
    {
	this.getInnerConfigurableTreeModel().valueForPathChanged(path, newValue);
    }
    
    /** ########################################################################
     *  ###################### ErrorHandler implementation #####################
     *  ######################################################################## */
    
    /** indicate that an error has occurred
     *  @param evt an ErrorEvent
     */
    public void handleError(ErrorEvent evt)
    {
	this.fireErrorHandlers(evt);
    }
    
    /** extension to  */
    private class ExtendedIntroSibTypeListTableModel extends IntrospectionSibTypeListTableModel<SibType>
    {
	/** return the item at the given index in the list
	 *  @param index an integer
	 *  @return a SibType
	 */
	@Override
	public SibType getItem(int index)
	{
	    TreePath path = getPathConverter().getPathForRow(index);

	    SibType type = null;

	    if (  path != null )
	    {
		Object lastItem = path.getLastPathComponent();
		if ( lastItem instanceof SibType )
		{
		    type = (SibType)lastItem;
		}
	    }

	    return type;
	}

	/** return the index of the given item
	 *  @param item an item that managed by the model
	 *  @return the index of the item in the table or -1 if it does not appear
	 */
	@Override
	public int getIndexOfItem(Object item)
	{
	    return getRowIndexForNode(item);
	}

	/**
	 * Returns the number of rows in the model. A
	 * <code>JTable</code> uses this method to determine how many rows it
	 * should display.  This method should be quick, as it
	 * is called frequently during rendering.
	 * 
	 * @return the number of rows in the model
	 * @see #getColumnCount
	 */
	public int getRowCount()
	{
	    return getPathConverter().getRowCount();
	}

	/** indicate to model that the object it manage has changed
	 *	@param oldObject the old object managed by the model
	 *	@param newObject the new object managed by the model
	 */
	public void managedItemChangedExt(SibType oldObject, SibType newObject)
	{
	    this.managedItemChanged(oldObject, newObject);
	}
    
	/** check if the given HierarchicalPropertyChangeEvent has to be taken into account
	 *	@param event a HierarchicalPropertyChangeEvent
	 *	@return true if the given HierarchicalPropertyChangeEvent has to be taken into account
	 */
	@Override
	protected boolean shouldConsiderEvent(HierarchicalPropertyChangeEvent e)
	{
	    boolean result = super.shouldConsiderEvent(e);
	    
	    if ( result )
	    {
		/** if the modification is a structure modification
		 *  item removed, item added, then do nothing --> the tree model will detect it
		 *  and warn treetable for modifications
		 *  only consider event if it is a property value modification event
		 */
		
		if ( SibCollection.PROPERTY_CONTENT.equals(e.getPropertyChangeEvent().getPropertyName()) )
		{
		    result = false;
		}
	    }
	    
	    return result;
	}
    }
}
