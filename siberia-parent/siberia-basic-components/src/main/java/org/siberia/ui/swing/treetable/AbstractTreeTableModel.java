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
package org.siberia.ui.swing.treetable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import org.siberia.type.SibType;
import org.siberia.ui.swing.error.ErrorEvent;
import org.siberia.ui.swing.error.ErrorHandler;
import org.siberia.ui.swing.error.ErrorOriginator;
import org.siberia.ui.swing.tree.model.ConfigurableTreeModel;
import org.siberia.ui.swing.treetable.adapter.AdapterFactory;
import org.siberia.ui.swing.treetable.adapter.DefaultAdapterFactory;

/**
 *
 * Abstract implementation of a TreeTableModel
 *
 * @author alexis
 */
public abstract class AbstractTreeTableModel<M extends ConfigurableTreeModel> implements TreeTableModel,
											 TreeModelListener,
											 ErrorOriginator
{   
    /** property inner tree model */
    public static final String PROPERTY_INNER_TREE_MODEL = "innerTreeModel";
    
    /* liste de TreeModelListener */
    private List<TreeModelListener> listeners      = null;
    
    /** inner ConfigurableTreeModel */
    private M                       innerTreeModel = null;
    
    /** adapter factory */
    private AdapterFactory          factory        = new DefaultAdapterFactory();
    
    /** property change support */
    private PropertyChangeSupport   support        = new PropertyChangeSupport(this);
    
    /** list of ErrorHandler */
    private List<ErrorHandler>      errorHandlers  = null;
    
    /** Creates a new instance of AbstractTreeTableModel */
    public AbstractTreeTableModel()
    {   }
    
    /** create a TreePath according to the given node
     *	@param node a node
     *	@return a TreePath
     */
    protected TreePath createPathForNode(Object node)
    {
	TreePath path = null;
	
	if ( node instanceof SibType )
	{
	    SibType sibNode = (SibType)node;
	    
	    List<Object> items = new ArrayList<Object>(10);

	    Object root = this.getInnerConfigurableTreeModel().getRoot();
	    
	    SibType current = sibNode;
	    
	    while(current != null && current != root)
	    {
		items.add(0, current);
		current = current.getParent();
	    }
	    
	    if ( current != null )
	    {
		items.add(0, current);
	    }

	    path = new TreePath(items.toArray(new Object[items.size()]));
	}
	
	return path;
    }
    
    /** return the AdapterFactory to use to create the TableModel related to this tree table model
     *	@return an AdapterFactory
     */
    public AdapterFactory getAdapterFactory()
    {
	return this.factory;
    }
    
    /** initialize the AdapterFactory to use to create the TableModel related to this tree table model
     *	@param adapter an AdapterFactory
     */
    public void setAdapterFactory(AdapterFactory adapter)
    {
	if ( adapter == null )
	{
	    throw new IllegalArgumentException("a TreeTableModel needs an AdapterFactory");
	}
	
	AdapterFactory oldFactory = this.getAdapterFactory();
	
	if ( oldFactory != adapter )
	{
	    this.factory = adapter;
	    
	    this.firePropertyChange(PROPERTY_ADAPTER_FACTORY, oldFactory, this.factory);
	}
    }
    
    /** initialize the inner ConfigurableTreeModel
     *	@param model a ConfigurableTreeModel
     */
    public void setInnerConfigurableTreeModel(M model)
    {
	M old = this.getInnerConfigurableTreeModel();
	
	if ( old != model )
	{
	    if ( old != null )
	    {
		old.removeTreeModelListener(this);
	    }
	
	    this.innerTreeModel = model;
	    
	    if ( model != null )
	    {
		model.addTreeModelListener(this);
	    }
	    
	    this.firePropertyChange(PROPERTY_INNER_TREE_MODEL, old, model);
	    
	}
    }
    
    /** return the inner ConfigurableTreeModel
     *	@return a ConfigurableTreeModel
     */
    public M getInnerConfigurableTreeModel()
    {
	return this.innerTreeModel;
    }
    
    /** indicates if the tree model allows tree modifications
     *  @return true if the tree model allows tree modifications
     */
    public boolean allowModifications()
    {
	boolean result = false;
	
	ConfigurableTreeModel model = this.getInnerConfigurableTreeModel();
	
	if ( model != null )
	{
	    result = model.allowModifications();
	}
	
	return result;
    }
    
    /** indicates if the root element of the tree model is a virtual element
     *  @return true if the root element of the tree model is a virtual element
     */
    public boolean isRootVirtual()
    {
	boolean result = false;
	
	ConfigurableTreeModel model = this.getInnerConfigurableTreeModel();
	
	if ( model != null )
	{
	    result = model.isRootVirtual();
	}
	
	return result;
    }
    
    /** set the root entity for this model
     *  @param type a SibType
     */
    public void setRoot(SibType type)
    {
	this.getInnerConfigurableTreeModel().setRoot(type);
    }
    
    /* fire an event to TreeModelListener
     *  @param event a TreeModelEvent
     */
    public void fireTreeNodesChanged(TreeModelEvent event)
    {   if ( event != null )
        {   if ( this.listeners != null )
            {   Iterator<TreeModelListener> it = this.listeners.iterator();
                
                while(it.hasNext())
                {   it.next().treeNodesChanged(event); }
            }
        }
    }
    
    /* fire an event to TreeModelListener
     *  @param event a TreeModelEvent
     */
    public void fireTreeNodesInserted(TreeModelEvent event)
    {   if ( event != null )
        {   if ( this.listeners != null )
            {   Iterator<TreeModelListener> it = this.listeners.iterator();
                
                while(it.hasNext())
                {   it.next().treeNodesInserted(event); }
            }
        }
    }
    
    /* fire an event to TreeModelListener
     *  @param event a TreeModelEvent
     */
    public void fireTreeNodesRemoved(TreeModelEvent event)
    {   if ( event != null )
        {   if ( this.listeners != null )
            {   Iterator<TreeModelListener> it = this.listeners.iterator();
                
                while(it.hasNext())
                {   it.next().treeNodesRemoved(event); }
            }
        }
    }
    
    /* fire an event to TreeModelListener
     *  @param event a TreeModelEvent
     */
    public void fireTreeStructureChanged(TreeModelEvent event)
    {   if ( event != null )
        {   if ( this.listeners != null )
            {   Iterator<TreeModelListener> it = this.listeners.iterator();
                
                while(it.hasNext())
                {   it.next().treeStructureChanged(event); }
            }
        }
    }

    /**
     * Adds a listener for the <code>TreeModelEvent</code>
     * posted after the tree changes.
     * 
     * 
     * @param l       the listener to add
     * @see #removeTreeModelListener
     */
    public void addTreeModelListener(TreeModelListener l)
    {   if ( l != null )
        {   if ( this.listeners == null )
                this.listeners = new ArrayList<TreeModelListener>();
            this.listeners.add(l);
        }
    }

    /**
     * Removes a listener previously added with
     * <code>addTreeModelListener</code>.
     * 
     * 
     * @param l       the listener to remove
     * @see #addTreeModelListener
     */
    public void removeTreeModelListener(TreeModelListener l)
    {   if ( this.listeners != null )
        {   this.listeners.remove(l); }
    }
    
    /** ########################################################################
     *  ################### Property change Listener methods ###################
     *  ######################################################################## */
    
    /** add a new PropertyChangeListener
     *	@param l a PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener l)
    {
	this.support.addPropertyChangeListener(l);
    }
    
    /** add a new PropertyChangeListener
     *	@param propertyName the name of a property
     *	@param l a PropertyChangeListener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener l)
    {
	this.support.addPropertyChangeListener(propertyName, l);
    }
    
    /** remove a PropertyChangeListener
     *	@param l a PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener l)
    {
	this.support.removePropertyChangeListener(l);
    }
    
    /** remove a PropertyChangeListener
     *	@param propertyName the name of a property
     *	@param l a PropertyChangeListener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener l)
    {
	this.support.removePropertyChangeListener(propertyName, l);
    }
    
    /** fire a PropertyChangeEvent
     *	@param propertyName the name of a property
     *	@param oldValue the old value of the property
     *	@param newValue the new value of the property
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newvalue)
    {
	this.support.firePropertyChange(propertyName, oldValue, newvalue);
    }
    
    /** ########################################################################
     *  ################### TreeModelListener implementation ###################
     *  ######################################################################## */
    
    /** method that allow to convert a TreeModelEvent which source is the inner tree model
     *	the returned event is a copy of the given event but the source is override to return this
     *	@param event a TreeModelEvent
     *	@return a new TreeModelEvent
     */
    private TreeModelEvent createSelfEvent(final TreeModelEvent e)
    {
	return new TreeModelEvent(this, e.getTreePath(), e.getChildIndices(), e.getChildren());
    }
    
    /**
     * <p>Invoked after a node (or a set of siblings) has changed in some
     * way. The node(s) have not changed locations in the tree or
     * altered their children arrays, but other attributes have
     * changed and may affect presentation. Example: the name of a
     * file has changed, but it is in the same location in the file
     * system.</p>
     * <p>To indicate the root has changed, childIndices and children
     * will be null. </p>
     * 
     * <p>Use <code>e.getPath()</code> 
     * to get the parent of the changed node(s).
     * <code>e.getChildIndices()</code>
     * returns the index(es) of the changed node(s).</p>
     */
    public void treeNodesChanged(TreeModelEvent e)
    {
	if ( e.getSource() == this.getInnerConfigurableTreeModel() )
	{
	    this.fireTreeNodesChanged(this.createSelfEvent(e));
	}
    }

    /**
     * <p>Invoked after nodes have been inserted into the tree.</p>
     * 
     * <p>Use <code>e.getPath()</code> 
     * to get the parent of the new node(s).
     * <code>e.getChildIndices()</code>
     * returns the index(es) of the new node(s)
     * in ascending order.</p>
     */
    public void treeNodesInserted(TreeModelEvent e)
    {
	if ( e.getSource() == this.getInnerConfigurableTreeModel() )
	{
	    this.fireTreeNodesInserted(this.createSelfEvent(e));
	}
    }

    /**
     * <p>Invoked after nodes have been removed from the tree.  Note that
     * if a subtree is removed from the tree, this method may only be
     * invoked once for the root of the removed subtree, not once for
     * each individual set of siblings removed.</p>
     *
     * <p>Use <code>e.getPath()</code> 
     * to get the former parent of the deleted node(s).
     * <code>e.getChildIndices()</code>
     * returns, in ascending order, the index(es) 
     * the node(s) had before being deleted.</p>
     */
    public void treeNodesRemoved(TreeModelEvent e)
    {
	if ( e.getSource() == this.getInnerConfigurableTreeModel() )
	{
	    this.fireTreeNodesRemoved(this.createSelfEvent(e));
	}
    }

    /**
     * <p>Invoked after the tree has drastically changed structure from a
     * given node down.  If the path returned by e.getPath() is of length
     * one and the first element does not identify the current root node
     * the first element should become the new root of the tree.<p>
     * 
     * <p>Use <code>e.getPath()</code> 
     * to get the path to the node.
     * <code>e.getChildIndices()</code>
     * returns null.</p>
     */
    public void treeStructureChanged(TreeModelEvent e)
    {
	if ( e.getSource() == this.getInnerConfigurableTreeModel() )
	{
	    this.fireTreeStructureChanged(this.createSelfEvent(e));
	}
    }
    
    /* #########################################################################
     * #################### ErrorOriginator implementation #####################
     * ######################################################################### */
    
    /** warn ErrorHandler */
    protected void fireErrorHandlers(ErrorEvent evt)
    {   if ( this.errorHandlers != null )
        {   for(int i = 0; i < this.errorHandlers.size(); i++)
            {   ErrorHandler handler = this.errorHandlers.get(i);
                
                if ( handler != null )
                {   handler.handleError(evt); }
            }
        }
    }

    /**
     * remove a new ErrorHandler
     * 
     * @param handler an ErrorHandler
     */
    public void removeErrorHandler(ErrorHandler handler)
    {   if ( handler != null && this.errorHandlers != null )
        {   this.errorHandlers.remove(handler); }
    }

    /**
     * add a new ErrorHandler
     * 
     * @param handler an ErrorHandler
     */
    public void addErrorHandler(ErrorHandler handler)
    {   if ( handler != null )
        {   if ( this.errorHandlers == null )
                this.errorHandlers = new ArrayList<ErrorHandler>();
            this.errorHandlers.add(handler);
        }
    }
    
}
