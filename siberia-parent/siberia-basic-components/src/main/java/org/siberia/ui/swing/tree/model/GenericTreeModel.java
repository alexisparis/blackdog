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
package org.siberia.ui.swing.tree.model;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;
import org.siberia.type.SibType;
import org.siberia.type.Namable;
import org.siberia.type.SibCollection;
import org.siberia.type.event.ContentClearedEvent;
import org.siberia.type.event.HierarchicalPropertyChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeListener;
import org.siberia.type.event.ContentChangeEvent;
import org.siberia.ui.swing.error.ErrorEvent;
import org.siberia.ui.swing.error.ErrorHandler;
import org.siberia.ui.swing.error.ErrorOriginator;

/**
 *
 * Default model for tree component.<br>
 * This kind of model works on instances of SibType.
 * Implementation of ConfigurableTreeModel interface to<br>
 * allow viewing all part of a structure in a tree component.
 *
 * @author alexis
 */
public abstract class GenericTreeModel implements ConfigurableTreeModel,
                                                  HierarchicalPropertyChangeListener,
						  ErrorOriginator
{   
    /** logger */
    private Logger                    logger        = Logger.getLogger(GenericTreeModel.class);
    
    /** list of listeners **/
    private List<TreeModelListener>   listeners     = null;
    
    /** the root element of the model **/
    private SibType                   root          = null;
    
    /** indicates if */
    private boolean                   isRootVirtual = false;
    
    /** list of ErrorHandler */
    private List<ErrorHandler>        errorHandlers = null;
    
    /** weak hash map linking identity hashcode of items in the tree structure
     *  and their related SibTypeLink
     */
//    private Map<Integer, SibTypeLink> linkMaps      = new WeakHashMap<Integer, SibTypeLink>(50);
    private Map<Integer, SibTypeLink> linkMaps      = new HashMap<Integer, SibTypeLink>(50);
    
    /** list that is only used in the getTreePathForNode
     *	we used the same list to avoid the creation of a new list every call to getTreePathForNode
     */
    private List<SibTypeLink>         pathList      = new ArrayList<SibTypeLink>(20);
    
    /** create a new GenericTreeModel */
    public GenericTreeModel()
    {   this(null); }
    
    /** create a new GenericTreeModel
     *  @param root an instance of SibType
     */
    public GenericTreeModel(SibType root)
    {   this.setRoot(root); }
    
    /** return a SibType according to the given object
     *	if the given objet is a SibType, then returns it.
     *	ele if it is a link, then return the object linked by that link else returns null
     *	@param object an object
     *	@return a SibType
     */
    protected SibType getSibTypeFrom(Object object)
    {
	SibType type = null;
	
	if ( object instanceof SibType )
	{
	    type = (SibType)object;
	}
	else if ( object instanceof SibTypeLink )
	{
	    type = ((SibTypeLink)object).getLinkedItem();
	}
	
	return type;
    }
    
    /** return the link related to the given object
     *	@param object a SibType
     *	@return a sibTypeLink
     */
    public SibTypeLink getLink(SibType object)
    {
	SibTypeLink link = null;
	
	if ( object != null )
	{
	    link = this.linkMaps.get( System.identityHashCode(object) );
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("taille link maps : " + this.linkMaps.size());
	    }
	    
	    if ( link == null )
	    {
		link = new SibTypeLink(object);
		this.linkMaps.put(System.identityHashCode(object), link);
	    }
	    else
	    {
		link.updateLinkedItem(object);
	    }
	}
	
	return link;
    }
    
    /** return a TreePath for the given node
     *  @param node an Object
     *	@return a TreePath that indicate the path to the given object or null if the object was not found
     */
    public TreePath getTreePathForNode(Object node)
    {
	TreePath path = null;
	
	SibType type = this.getSibTypeFrom(node);
	
	if ( type != null )
	{
	    synchronized(pathList)
	    {
		SibType current = type;
		
		pathList.clear();
		
		boolean continueLoop = true;
		
		while(continueLoop)
		{
		    this.pathList.add(0, this.getLink(current));
		    
		    if ( current == this.root )
		    {
			continueLoop = false;
		    }
		    else
		    {
			current = current.getParent();
			
			if ( current == null )
			{
			    /** break with continueLoop true is abnormal */
			    break;
			}
		    }
		}
		
		if ( continueLoop )
		{
		    path = null;
		}
		else
		{
		    /** build treePath */
		    path = new TreePath( this.pathList.toArray(new SibTypeLink[this.pathList.size()]) );
		}
	    }
	}
	
	return path;
    }
    
    /** set the root element of the model
     *  @param root a SibType instance wich will be the root element of the model
     **/
    public void setRoot(SibType root)
    {   
        SibType oldRoot = this.root;
        
        if ( oldRoot != root )
        {   
            /** remove hierarchy listener */
            if ( oldRoot != null )
            {   oldRoot.removeHierarchicalPropertyChangeListener(this); }
        
            this.root = root;
	    
	    /** clear the link map */
	    this.linkMaps.clear();
            
            if ( this.root != null )
            {   this.root.addHierarchicalPropertyChangeListener(this); }
            
            /** the tree changed, fire some events ... */
            this.fireTreeStructureChanged(new TreeModelEvent(this, new TreePath(this.getLink(this.root))));
        }
    }
    
    /** return the root element of the model
     *  @return the root a SibType
     **/
    public SibType getSibTypeRoot()
    {
	return this.root;
    }
    
    /** return the root element of the model
     *  @return the root a SibType
     **/
    public SibTypeLink getRoot()
    {   
	SibTypeLink rootLink = null;
	
	if ( this.root != null )
	{
	    rootLink = this.getLink(this.root);
	}
	
	return rootLink;
    }
    
    /** tells if the root element of the tree model is a virtual element
     *  @param virtual true if the root element of the tree model is a virtual element
     */
    protected void setRootVirtual(boolean virtual)
    {   
	boolean old = this.isRootVirtual();
        
	this.isRootVirtual = virtual;
        
        if ( old != this.isRootVirtual() )
        {   
	    TreeModelEvent event = new TreeModelEvent(this, new TreePath(this.getRoot()));
            this.fireTreeStructureChanged(event);
        }
    }
    
    /** indicates if the root element of the tree model is a virtual element
     *  @return true if the root element of the tree model is a virtual element
     */
    public boolean isRootVirtual()
    {   return this.isRootVirtual; }
    
    /** return the child in position index in the children list of parent
     *  @param parent the parent
     *  @param index the index in the children list
     *  @return the object which is the index throws in the children list of parent
     **/
    public SibTypeLink getChild(Object parent, int index)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getChild(" + parent + ", " + index + ")");
	}
	SibTypeLink result = null;
	
	if ( parent != null )
        {   
	    SibType _parent = this.getSibTypeFrom(parent);
	    
	    if ( _parent != null )
            {   
		if ( index >= 0 && index < _parent.getChildrenCount() )
                {   
		    SibType child = _parent.getChildAt(index);
		    
		    result = this.getLink(child);
		}
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getChild(" + parent + ", " + index + ") returns " + result);
	}
	
        return result;
    }
    
    /** return the number of children that contains the given object
     *  @param parent the object we want to know how many children it contains
     *  @return the number of children
     **/
    public int getChildCount(Object parent)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getChildCount(" + parent + ")");
	}
	int result = 0;
        
	SibType _parent = this.getSibTypeFrom(parent);
	
	if ( _parent != null )
        {   
	    result = _parent.getChildrenCount();
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getChildCount(" + parent + ") returns " + result);
	}
	
        return result;
    }
    
    /** return the index of child in the children list of parent
     *  @param parent the parent
     *  @param child the child
     *  @return the index
     **/
    public int getIndexOfChild(Object parent, Object child)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getIndexOfChild(" + parent + ", " + child + ")");
	}
	int result = -1;
	
	SibType _parent = this.getSibTypeFrom(parent);	
	SibType _child  = this.getSibTypeFrom(child);
	
	if ( _parent != null && _child != null )
        {     
	    result = _parent.indexOfChildReference(_child);

	    if ( result < 0 )
	    {
		result = _parent.indexOfChild(_child);
	    }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getIndexOfChild(" + parent + ", " + child + ") returns " + result);
	}
        return result;
    }
    
    /** could the given object have children
     *  @param node the node we want to know if it could have children
     **/
    public boolean isLeaf(Object node)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling isLeaf(" + node + ")");
	}
	boolean leaf = true;
	
	SibType _node = this.getSibTypeFrom(node);
	
        if ( _node != null )
        {   
            leaf = _node.isLeaf();
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("isLeaf(" + node + ") returns " + leaf);
	}
        return leaf;
    }
    
    public void valueForPathChanged(TreePath path, Object newValue)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling valueForPathChanged(" + path + ", " + newValue + ")");
	}
	/* change the name of the instance */
        if ( path != null )
        {   
	    SibType type = this.getSibTypeFrom(path.getLastPathComponent());
	    
	    if ( type != null && newValue instanceof String )
            {   
		if ( type.nameCouldChange() )
                {   
		    try
                    {   
			String _newValue = (String)newValue;
			if ( _newValue.length() > 255 )
			{
			    _newValue = _newValue.substring(0, 255);
			}
			
			type.setName( _newValue );
		    }
                    catch(PropertyVetoException e)
                    {
			ResourceBundle rb = ResourceBundle.getBundle(GenericTreeModel.class.getName());
			
			ErrorEvent evt = new ErrorEvent(this, rb.getString("cannotChangeNameTitle"),
							      rb.getString("cannotChangeNameMessage"),
							      rb.getString("cannotChangeNameDetailedMessage"),
							      rb.getString("cannotChangeNameCategory"),
							      e,
							      Level.INFO);
			
			this.fireErrorHandlers(evt);
                    }
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of valueForPathChanged(" + path + ", " + newValue + ")");
	}
    }
    
    /** ########################################################################
     *  ################# TreeModelListeners management  #######################
     *  ######################################################################## */
    
    /** add a new tree model listener **/
    public void addTreeModelListener(TreeModelListener l)
    {   if ( l != null )
        {   if ( this.listeners == null )
                this.listeners = new ArrayList<TreeModelListener>();
            this.listeners.add(l);
        }
    }
    
    /** remove a tree model listener **/
    public void removeTreeModelListener(TreeModelListener l)
    {   if ( l != null && this.listeners != null )
            this.listeners.remove(l);
    }

    /** manage change events
     *  @param event a TreeModelEvent
     */
    public void fireTreeNodesChanged (TreeModelEvent event)
    {   if ( this.listeners != null && event != null )
        {   for (Iterator<TreeModelListener> i = listeners.iterator(); i.hasNext();)
            {   i.next().treeNodesChanged(event); }
        }
    }

    /** manage insertion events
     *  @param event a TreeModelEvent
     */
    public void fireTreeNodesInserted (TreeModelEvent event)
    {   if ( this.listeners != null && event != null )
        {   for (Iterator<TreeModelListener> i = listeners.iterator(); i.hasNext();)
            {   i.next().treeNodesInserted(event); }
        }
    }
  
    /** manage removing events
     *  @param event a TreeModelEvent
     */
    public void fireTreeNodesRemoved (TreeModelEvent event)
    {   if ( this.listeners != null && event != null )
        {   for (Iterator<TreeModelListener> i = listeners.iterator(); i.hasNext();)
            {   i.next().treeNodesRemoved(event); }
        }
    }

    /** manage structure modifications
     *  @param event a TreeModelEventget child count of
     */
    public void fireTreeStructureChanged (TreeModelEvent event)
    {   if ( this.listeners != null && event != null )
        {   for (Iterator<TreeModelListener> i = listeners.iterator(); i.hasNext();)
            {   i.next().treeStructureChanged(event); }
        }
    }
    
    /** ########################################################################
     *  ############ HierarchicalPropertyChangeListener implementation #########
     *  ######################################################################## */
    
    /** create an array of SibTypeLink according to an array of SibType
     *	@param types an array of SibType
     *	@return an array of sibTypeLink
     */
    private SibTypeLink[] convertTypeArray(SibType[] types)
    {
	SibTypeLink[] links = null;
	
	if ( types != null )
	{
	    links = new SibTypeLink[types.length];
	    
	    for(int i = 0; i < types.length; i++)
	    {
		links[i] = this.getLink(types[i]);
	    }
	}
	
	return links;
    }
    
    /** called when a HierarchicalPropertyChangeEvent was throwed by
     *  an object listener by this one
     *  @param eventa HierarchycalPropertyChangeEvent
     */
    public void propertyChange(HierarchicalPropertyChangeEvent event)
    {   
        /* only care about event that concern names and other that concern structure */
        if ( event.getPropertyChangeEvent().getPropertyName().equals(SibType.PROPERTY_NAME) )
        {
            /* create a TreeModelEvent that indicate that the name of a node has changed */
            SibType[] ar = event.getArrayPath();
	    
	    SibTypeLink[] linksPath = this.convertTypeArray(ar);
            
            final TreeModelEvent evt = new TreeModelEvent(this, ar);
            
            Runnable run = new Runnable()
            {
                public void run()
                {   fireTreeNodesChanged(evt); }
            };
            
            if ( SwingUtilities.isEventDispatchThread() )
            {   run.run(); }
            else
            {   SwingUtilities.invokeLater(run); }
        }
        else if ( event.getPropertyChangeEvent().getPropertyName().equals(SibCollection.PROPERTY_CONTENT_AS_CHILD) )
        {   
            /* create a TreeModelEvent that indicate to update the item */
            SibType[] ar = event.getArrayPath();
	    
	    SibTypeLink[] linksPath = this.convertTypeArray(ar);
            
            final TreeModelEvent evt = new TreeModelEvent(this, linksPath);
            
            Runnable run = new Runnable()
            {
                public void run()
                {   fireTreeStructureChanged(evt); }
            };
            
            if ( SwingUtilities.isEventDispatchThread() )
            {   run.run(); }
            else
            {   SwingUtilities.invokeLater(run); }
        }
        else if ( event.getPropertyChangeEvent().getPropertyName().equals(SibType.PROPERTY_CHILDREN) )
        {   
            if ( event.getPropertyChangeEvent() instanceof ContentChangeEvent )
            {   
		ContentChangeEvent ccEvent = (ContentChangeEvent)event.getPropertyChangeEvent();
                
                if ( ccEvent.getSource() instanceof SibCollection )
                {
                    if ( ((SibCollection)ccEvent.getSource()).isContentItemAsChild() )
                    {   
                        if ( ccEvent.getMode() == ContentChangeEvent.ADD )
                        {   /* create a TreeModelEvent that indicate that the name of a node has changed */
                            SibType[] ar = event.getArrayPath();
	    
			    SibTypeLink[] linksPath = this.convertTypeArray(ar);
			    
			    SibType[] objectsAdded = ccEvent.getObject();
			    
			    SibTypeLink[] linksAdded = this.convertTypeArray(objectsAdded);
			    
//			    System.out.println("affichage du path : ");
//			    for(int i = 0; i < linksPath.length; i++)
//			    {
//				System.out.println("\t" + linksPath[i]);
//			    }
//			    
//			    System.out.println("objects : ");
//			    for(int i = 0; i < linksAdded.length; i++)
//			    {
//				System.out.println("\t" + linksAdded[i] + " of kind : " + linksAdded[i].getLinkedItem().getClass());
//			    }
//			    System.out.println("positions : ");
//			    for(int i = 0; i < ccEvent.getPosition().length; i++)
//			    {
//				System.out.println("\t" + ccEvent.getPosition()[i]);
//			    }
			    
                            final TreeModelEvent evt = new TreeModelEvent(this, linksPath, ccEvent.getPosition(), linksAdded);
//                            final TreeModelEvent evt = new TreeModelEvent(this, ar, ccEvent.getPosition(), objectsAdded);

                            Runnable run = new Runnable()
                            {
                                public void run()
                                {   fireTreeNodesInserted(evt); }
                            };

                            if ( SwingUtilities.isEventDispatchThread() )
                            {   run.run(); }
                            else
                            {   SwingUtilities.invokeLater(run); }
                        }
                        else if ( ccEvent.getMode() == ContentChangeEvent.REMOVE )
                        {   
			    /* create a TreeModelEvent that indicate that the name of a node has changed */
                            SibType[] ar = event.getArrayPath();
			    
			    SibTypeLink[] _typesLink = null;
			    int[] _positions = null;
			    
			    if ( ccEvent.getPosition() == null )
			    {
				if ( ccEvent instanceof ContentClearedEvent )
				{
				    /** create positions according to the number of items */
				    _positions = new int[ccEvent.getObject().length];

				    for(int i = 0; i < _positions.length; i++)
				    {
					_positions[i] = i;
				    }
				}
			    }
			    else
			    {
				int length = ccEvent.getPosition().length;
				_positions = new int[length];
				for(int i = 0; i < length; i++)
				{
				    _positions[i] = ccEvent.getPosition()[length - i - 1];
				}
			    }
			    if ( ccEvent.getObject() != null )
			    {
				int length = ccEvent.getObject().length;
				_typesLink = new SibTypeLink[length];
				for(int i = 0; i < length; i++)
				{
				    _typesLink[i] = this.getLink(ccEvent.getObject()[length - i - 1]);
				}
			    }
	    
			    SibTypeLink[] linksPath = this.convertTypeArray(ar);

                            final TreeModelEvent evt = new TreeModelEvent(this, linksPath, _positions, _typesLink);

                            Runnable run = new Runnable()
                            {
                                public void run()
                                {   fireTreeNodesRemoved(evt); }
                            };

                            if ( SwingUtilities.isEventDispatchThread() )
                            {   run.run(); }
                            else
                            {   SwingUtilities.invokeLater(run); }
                        }
                    }
                }
            }
        }
    }
    
    /* #########################################################################
     * #################### ErrorOriginator implementation #####################
     * ######################################################################### */
    
    /** warn ErrorHandler */
    protected void fireErrorHandlers(ErrorEvent e)
    {   
	if ( this.errorHandlers != null )
        {   for(int i = 0; i < this.errorHandlers.size(); i++)
            {   ErrorHandler handler = this.errorHandlers.get(i);
                
                if ( handler != null )
                {   handler.handleError(e); }
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
