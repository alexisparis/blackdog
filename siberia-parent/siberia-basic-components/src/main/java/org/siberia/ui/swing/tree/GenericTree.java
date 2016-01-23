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
package org.siberia.ui.swing.tree;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTree;
import org.siberia.type.SibType;
import org.siberia.type.SibCollection;
import org.siberia.GraphicalResources;
import org.siberia.ui.swing.error.ErrorEvent;
import org.siberia.ui.swing.error.ErrorHandler;
import org.siberia.ui.swing.error.ErrorOriginator;
import org.siberia.ui.swing.transfer.ArrowImage;
import org.siberia.ui.swing.transfer.SibTypeTransferHandler;
import org.siberia.ui.swing.transfer.SibTypeTransferable;
import org.siberia.ui.swing.transfer.ComponentTransfer;
import org.siberia.ui.swing.tree.model.ConfigurableTreeModel;
import org.siberia.ui.swing.tree.editor.GenericTreeCellEditor;
import org.siberia.ui.swing.tree.model.GenericTreeModel;
import org.siberia.ui.swing.tree.model.SibTypeLink;
import org.siberia.ui.swing.tree.renderer.GenericTreeCellRenderer;
import org.siberia.ui.bar.PluginBarFactory;



/**
 *
 * Default tree which is the main class for all tree component used in the Sib application
 * Thanks to Andrew J. Armstrong (http://www.javaworld.com/javaworld/javatips/jw-javatip114.html)
 *
 * @author alexis
 */
public class GenericTree extends JXTree implements MouseListener,
                                                   MouseMotionListener,
						   Autoscroll,
						   ErrorHandler,
						   ComponentTransfer
{   
    private static final int   AUTOSCROLL_MARGIN       = 12;
    
    /** logger */
    private transient Logger    logger                 = Logger.getLogger(GenericTree.class);
    
    // Where, in the drag image, the mouse was clicked
    private Point	        ptOffset               = new Point();
    
    // The 'drag image'
    private BufferedImage       ghostImage             = null;
    
    /** DropTarget if the tree allow drag'n drop */
    private DropTarget          dropTarget             = null;
    
    /** DragSource if the tree allow drag'n drop */
    private DragSource          dragSource             = null;
    
    /** DropTargetListener if the tree allow drag'n drop */
    private DropTargetListener  dropTListener          = null;
    
    /** DragGestureListener if the tree allow drag'n drop */
    private DragGestureListener dragGListener          = null;
    
    /** invoker for the popup menu */
    private Component           popupInvoker           = null;
    
    /** expansion mode */
    private PathExpansionMode   expandPathMode         = PathExpansionMode.NONE;
    
    /** transfer action */
    private int                 transferAction         = TransferHandler.MOVE;
    
    /** Creates a new instance of DefaultTree */
    public GenericTree()
    {   super();
        
        this.setScrollsOnExpand(true);
        
//        this.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        
        /* initialize the renderer to be used */
        DefaultTreeCellRenderer renderer = new GenericTreeCellRenderer();
        this.setCellRenderer(renderer);
        GenericTreeCellEditor editor     = new GenericTreeCellEditor(this, renderer);
        this.setCellEditor(editor);
        
        this.setShowsRootHandles(true);
	
	this.setTransferHandler(new SibTypeTransferHandler());
	this.setDragEnabled(true);
        
        this.addKeyListener(new KeyAdapter()
        {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e)
            {
                if ( e.getKeyCode() == KeyEvent.VK_DELETE )
                {   
                    /** consider selection */
                    SibType[] selectedItems = getSelectedElements();
                    
                    if ( selectedItems != null && selectedItems.length > 0 )
                    {   
//                        RemovingTypeAction action = new RemovingTypeAction();
//                        action.setType();
                    }
                }
            }
        });
	
	this.setPopupInvoker(this);
    }

    @Override
    public void updateUI()
    {
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("calling updateUI()");
	}
	super.updateUI();

	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("renderer is " + this.getCellRenderer());
	}
	
	TreeCellRenderer renderer = this.getCellRenderer();
	
	if ( renderer instanceof JXTree.DelegatingRenderer )
	{
	    renderer = ((JXTree.DelegatingRenderer)renderer).getDelegateRenderer();
	}

	if ( renderer instanceof GenericTreeCellRenderer )
	{
	    if ( logger != null && logger.isDebugEnabled() )
	    {
		logger.debug("updating cell renderer caracteristics");
	    }
	    ((GenericTreeCellRenderer)renderer).setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
	    ((GenericTreeCellRenderer)renderer).setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
	    ((GenericTreeCellRenderer)renderer).setOpenIcon(UIManager.getIcon("Tree.openIcon"));

	    ((GenericTreeCellRenderer)renderer).setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
	    ((GenericTreeCellRenderer)renderer).setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
	    ((GenericTreeCellRenderer)renderer).setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
	    ((GenericTreeCellRenderer)renderer).setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
	    ((GenericTreeCellRenderer)renderer).setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
//	    Object value = UIManager.get("Tree.drawsFocusBorderAroundIcon");
//	    drawsFocusBorderAroundIcon = (value != null && ((Boolean)value).
//					  booleanValue());
//	    value = UIManager.get("Tree.drawDashedFocusIndicator");
//	    drawDashedFocusIndicator = (value != null && ((Boolean)value).
//					booleanValue());
	    
	    SwingUtilities.updateComponentTreeUI(((GenericTreeCellRenderer)renderer));
	}
	
	TreeCellEditor editor = this.getCellEditor();
	
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("cell editor is " + editor);
	}
	
	if ( editor instanceof GenericTreeCellEditor )
	{
	    ((GenericTreeCellEditor)editor).updateUI();
	}
	
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("end of calling updateUI()");
	}
    }
    
    /* #########################################################################
     * ################## ComponentTransfer implementation #################
     * ######################################################################### */
    
    /** return the action that has to be done<br>
     *	TransferHandler.COPY
     *	TransferHandler.COPY_OR_MOVE
     *	TransferHandler.LINK
     *	TransferHandler.MOVE
     *	TransferHandler.NONE
     *	@return an integer representing a transfer action
     */
    public int getTransferAction()
    {
	return this.transferAction;
    }
    
    /** set the action that has to be done<br>
     *	TransferHandler.COPY
     *	TransferHandler.COPY_OR_MOVE
     *	TransferHandler.LINK
     *	TransferHandler.MOVE
     *	TransferHandler.NONE
     *	@param action an integer representing a transfer action
     */
    public void setTransferAction(int action)
    {
	this.transferAction = action;
    }

    /** return the mode of expansion
     *	@return a boolean
     */
    public PathExpansionMode getExpansionMode()
    {
	return expandPathMode;
    }

    /** set expansion mode
     *	@param mode a PathExpansionMode
     */
    public void setExpansionMode(PathExpansionMode mode)
    {
	PathExpansionMode _mode = mode;
	if ( _mode == null )
	{
	    _mode = PathExpansionMode.NONE;
	}
	
	if ( ! _mode.equals(this.getExpansionMode()) )
	{
	    this.expandPathMode = _mode;

	    if ( this.getExpansionMode().modelChange() )
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("asking to expand all path");
		}
		/** expand all paths */
		expandAll();
	    }
	}
    }

     /**
      * Creates and returns an instance of <code>TreeModelHandler</code>.
      * The returned
      * object is responsible for updating the expanded state when the
      * <code>TreeModel</code> changes.
      * <p>
      * For more information on what expanded state means, see the
      * <a href=#jtree_description>JTree description</a> above.
      */
    @Override
    protected TreeModelListener createTreeModelListener()
    {
	 return new JTree.TreeModelHandler()
	 {
	     @Override
	     public void treeNodesInserted(TreeModelEvent e)
	     {
		 super.treeNodesInserted(e);

		 if ( e != null && getExpansionMode().insertNode() )
		 {
		     TreePath path = e.getTreePath();

		     if ( path != null )
		     {
			 expandPath(path);
		     }
		 }
	     }

	     @Override
	     public void treeNodesChanged(TreeModelEvent e)
	     {
		 super.treeNodesChanged(e);

		 if ( e != null && getExpansionMode().nodeChange() )
		 {
		     TreePath path = e.getTreePath();

		     if ( path != null )
		     {
			 expandPath(path);
		     }
		 }
	     }
	 };
    }
    
    /** return the invoker for the popup menu
     *	@return a Component
     */
    public Component getPopupInvoker()
    {
	return this.popupInvoker;
    }
    
    /** set the invoker for the popup menu
     *	@param invoker a Component that will be considered as the invoker for the popup menu
     */
    public void setPopupInvoker(Component invoker)
    {
	this.popupInvoker = invoker;
    }
    
    /** initialize the model by creating an instance of the given class
     *  @param treeModelClass a class describing a tree model
     */
    public final void setModel(Class treeModelClass)
    {   if ( treeModelClass != null )
        {   try
            {   Object o = treeModelClass.newInstance();
                if ( o instanceof TreeModel )
                {   this.setModel((TreeModel)o); }
            }
            catch(Exception e)
            {   e.printStackTrace(); }
        }
    }
    
    /** set the model
     *  @param model a TreeModel implementation. The model could implement ConfigurableTreeModel<br> if not, the tree will
     *          come with no modifying functionnalities
     **/
    public final void setModel(TreeModel model)
    {   TreeModel oldModel = this.getModel();
	
	if ( oldModel instanceof ErrorOriginator )
	{
	    ((ErrorOriginator)oldModel).removeErrorHandler(this);
	}
        
        super.setModel(model);
	
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("expansion mode is " + this.getExpansionMode());
	}
	
	if ( this.getExpansionMode() != null && this.getExpansionMode().modelChange() )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("asking to expand all path");
	    }
	    this.expandAll();
	}
	
	if ( model instanceof ErrorOriginator )
	{
	    ((ErrorOriginator)model).addErrorHandler(this);
	}
        
        boolean dragndropAllowed = false;
        boolean popupMenuActive  = false;
        boolean rootVirtual      = false;
        
        if (model instanceof ConfigurableTreeModel)
        {   ConfigurableTreeModel _model = (ConfigurableTreeModel)model;
            
            dragndropAllowed = _model.allowModifications();
            popupMenuActive  = _model.allowModifications();
            
            rootVirtual      = _model.isRootVirtual();
        }
        
        this.setRootVisible(! rootVirtual);
        
        this.setEditable(dragndropAllowed);
	
	/** inhibé pour le moment */
	dragndropAllowed = false;
        
        /* manager drag'n drop functionnalities */
        if ( dragndropAllowed )
        {   this.addMouseMotionListener(this);
            
            if ( this.dragSource == null )
            {   this.dragSource = DragSource.getDefaultDragSource();
                this.dragGListener = new DefaultTreeDragGestureListener();
                this.dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this.dragGListener);
            }
            if ( this.dropTarget == null )
            {   this.dropTListener = new DefaultTreeDropTargetListener();
                this.dropTarget = new DropTarget(this, this.dropTListener);
            }
        }
        else
        {   this.removeMouseMotionListener(this);
            
            if ( this.dragSource != null )
            {   /** PENDING : to do remove drag functionnalities */
                //this.dragSource.removeDragSourceMotionListener(this.dragGListener);
            }
            if ( this.dropTarget != null && this.dropTListener != null )
            {   /** PENDING : verify remove drag functionnalities */
                this.dropTarget.removeDropTargetListener(this.dropTListener);
            }
        }
        
        /* manager popup menu functionnalities */
        if ( popupMenuActive )
            this.addMouseListener(this);
        else
            this.removeMouseListener(this);
    }

    /**
     * Selects the node identified by the specified path and initiates
     * editing.  The edit-attempt fails if the <code>CellEditor</code>
     * does not allow
     * editing for the specified item.
     * 
     * @param path  the <code>TreePath</code> identifying a node
     */
    @Override
    public void startEditingAtPath(TreePath path)
    {   /* if the entity pointed by the given path could not be renamed, show dialog */
        if ( path != null )
        {   
	    SibType type = this.getSibTypeLastPathComponent(path);
	    
            if ( type != null )
            {   
		if ( ! type.nameCouldChange() || type.isReadOnly() )
                {   
		    ResourceBundle rb = ResourceBundle.getBundle(GenericTree.class.getName());
		    
		    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                                                  rb.getString("cannotRenameObjectMessage"),
                                                  rb.getString("cannotRenameObjectTitle"),
						  JOptionPane.ERROR_MESSAGE);
                }
                else
                {   super.startEditingAtPath(path); }
            }
        }
    }
    
    /** return the SibType represented at the last component of the given path
     *	@param path a TreePath
     *	@return a SibType or null
     */
    private SibType getSibTypeLastPathComponent(TreePath path)
    {
	SibType type = null;
	
	if ( path != null )
	{
	    Object o = path.getLastPathComponent();
	    
	    if ( o instanceof SibType )
	    {
		type = (SibType)o;
	    }
	    else if ( o instanceof SibTypeLink )
	    {
		type = ((SibTypeLink)o).getLinkedItem();
	    }
	}
	
	return type;
    }
    
    /* return the selected item in the tree
     * @return the object selected in the tree
     **/
    public SibType getSelectedElement()
    {   
	return this.getSibTypeLastPathComponent(this.getSelectionModel().getSelectionPath());
    }
    
    /* return the selected items in the tree
     * @return an array of selected SibType
     **/
    public SibType[] getSelectedElements()
    {   
	if ( this.getSelectionModel().getSelectionPaths() == null ) return null;
        
        SibType[] selection = new SibType[this.getSelectionModel().getSelectionPaths().length];
        for(int i = 0; i < this.getSelectionModel().getSelectionPaths().length; i++)
	{
            selection[i] = this.getSibTypeLastPathComponent(this.getSelectionModel().getSelectionPaths()[i]);
	}
        return selection;
    }
    
    /* select an item in the tree
     * @param object an SibType instance to select in the tree
     **/
    public void setSelectedElement(SibType object)
    {   
	/** create a TreePath with all links linked to the branch of the given object */
	if ( object != null && this.getModel() instanceof GenericTreeModel )
	{
	    SibType[] branch = object.getPath();
	    
	    if ( branch != null )
	    {
		SibTypeLink[] links = new SibTypeLink[branch.length];
		
		for(int i = 0; i < links.length; i++)
		{
		    links[i] = ((GenericTreeModel)this.getModel()).getLink(branch[i]);
		}
		
		this.getSelectionModel().setSelectionPath(new TreePath(links));
	    }
	}
    }
    
    /** return the selected path
     *  @return the selected path
     */
    public TreePath getSelectedPath()
    {   return this.getSelectionModel().getSelectionPath(); }
    
    /** set the path to select
     *  @param path the path to select
     */
    public void setSelectedPath(TreePath path)
    {   this.getSelectionModel().setSelectionPath(path); }
    
    /** return the SibType for a given position
     *  @param x
     *  @param y
     *  @return a SibType or null
     */
    public SibType getElementAt(int x, int y)
    {   
	SibType type = null;
        TreePath path = this.getPathForLocation(x, y);
        if ( path != null )
        {   
	    type = this.getSibTypeLastPathComponent(path);
        }
        return type;
    }
    
    /** return the nearest SibType under mouse pointer
     *  @return the nearest SibType under mouse pointer
     */
    public SibType getNearestElementUnderMouse()
    {   
	PointerInfo pInfo = MouseInfo.getPointerInfo();
        return this.getSibTypeLastPathComponent(this.getClosestPathForLocation(pInfo.getLocation().x - this.getLocationOnScreen().x,
                            pInfo.getLocation().y - this.getLocationOnScreen().y));
    }
    
    /** return the SibType under mouse pointer
     *  @return the SibType under mouse pointer
     */
    public SibType getElementUnderMouse()
    {   
	PointerInfo pInfo = MouseInfo.getPointerInfo();
        TreePath path = this.getPathForLocation(pInfo.getLocation().x - this.getLocationOnScreen().x,
                            pInfo.getLocation().y - this.getLocationOnScreen().y);
        SibType type = this.getSibTypeLastPathComponent(path);
	
        return type;
    }
    
    /** show a JPopupMenu for a given MouseEvent
     *	@param popup a JPopupMenu
     *	@param invoker the invoker to use
     *	@param x the x position
     *	@param y the y position
     *	@param selection an array of SibType that are selected
     */
    protected void showPopup(JPopupMenu popup, Component invoker, int x, int y, SibType[] entities)
    {
	popup.show(invoker, x, y);
    }
    
    /** method to show the popup menu
     *  @param e a mouseEvent
     *  @param entities an array of SibType concerned by the popup
     */
    private void showPopup(MouseEvent e, SibType[] entities)
    {
        JPopupMenu menu = PluginBarFactory.getInstance().getContextMenuForItem(entities);
        if ( menu != null )
        {   
	    this.showPopup(menu, this.getPopupInvoker(), e.getX(), e.getY(), entities);
	}
        
        boolean ok = false;
        
//        if ( System.getProperty("os.name").equals("Linux") )
//            ok = true;
//        else
//        {   if (e.isPopupTrigger())
//                ok = true;
//        }
        
        if ( ok )
        {   /* find the current selected node */
//            JPopupMenu menu = ContextMenuFactory.getInstance().get(entity);
//            if ( menu != null )
//            {   menu.show(e.getComponent(), e.getX(), e.getY()); }
            
            /** PENDING : popup menu to do */
            //this.popup.buildMenuItems();
            //this.popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    /** return true if the given path represents root element
     *  @param path a TreePath
     *  @return true if the given path represents root element
     */
    private boolean isRootPath(TreePath path)
    {   return this.isRootVisible() && this.getRowForPath(path) == 0; }
    
    /** return true if the popup menu is activate
     *  @return true if the popup menu is activate
     */
    public boolean isPopupMenuActivated()
    {   boolean activate = false;
        if ( this.getModel() != null )
        {   if ( this.getModel() instanceof ConfigurableTreeModel )
	    {
                activate = ((ConfigurableTreeModel)this.getModel()).allowModifications();
	    }
        }
        return activate;
    }
    
    /* #########################################################################
     * ########################## MouseListener impl ###########################
     * ######################################################################### */
    
    public void mousePressed(MouseEvent e)
    {   if ( this.isPopupMenuActivated() )
        {   /* PENDING : manage correctly selection according to selection and popup menu activation */
            TreePath path = this.getPathForLocation(e.getX(), e.getY());
            if ( path != null )
            {
                
            }
        }
        /* get the nearest element and select it */
        TreePath path = this.getClosestPathForLocation(e.getX(), e.getY());
        if ( path instanceof SibType )
        {   SibType obj = this.getSibTypeLastPathComponent(path);
        
            /* select the element */
            this.setSelectedElement(obj);
        }
    }
    
    public void mouseReleased(MouseEvent e)
    {   
	if ( this.isPopupMenuActivated() && SwingUtilities.isRightMouseButton(e) )
        {
            /* v�rif si on tombe sur une feuille de l'arbre */
            int row = this.getRowForLocation(e.getX(), e.getY());
            if (row != -1)
            {   
                /** if the row is already selected, do not break selection */
                boolean alreadySelected = false;
                int [] selection = this.getSelectionRows();
                if ( selection != null )
                {   for(int i = 0; i < selection.length; i++)
                    {   if ( selection[i] == row )
                        {   alreadySelected = true;
                            break;
                        }
                    }
                }

                if ( ! alreadySelected )
                {
                    /* PENDING : allow popup on multiples elements */
                    SibType obj = this.getSibTypeLastPathComponent(this.getClosestPathForLocation(e.getX(), e.getY()));

                    /* select the element */
                    this.setSelectedElement(obj);
                }

                this.showPopup(e, this.getSelectedElements());
            }
        }
    }
    
    public void mouseClicked(MouseEvent e)
    {   /* do nothing */ }
    
    public void mouseEntered(MouseEvent e)
    {   /* do nothing */ }
    
    public void mouseExited(MouseEvent e)
    {   /* do nothing */ }
    
    /* #########################################################################
     * ####################### MouseMotionListener impl ########################
     * ######################################################################### */
    
    public void mouseDragged(MouseEvent e)
    {
        if ( e.getClickCount() == 1)
        {
            JComponent c = (JComponent)e.getSource();
            TransferHandler handler = c.getTransferHandler();
            handler.exportAsDrag(c, e, TransferHandler.MOVE);
        }
    }
    
    public void mouseMoved(MouseEvent e)
    {   /* do nothing */ }
    
    /* #########################################################################
     * ########################### ErrorHandler impl ###########################
     * ######################################################################### */
    
    /** indicate that an error has occurred
     *	@param event an ErrorEvent
     */
    public void handleError(ErrorEvent event)
    {
	if ( event != null )
	{
	    /** display a JXErrorDialog */
	    JXErrorPane.showDialog(this, event.createErrorInfo());
	}
    }
    
    /* #########################################################################
     * ########################### Autoscroll impl #############################
     * ######################################################################### */
    
    // Ok, we�ve been told to scroll because the mouse cursor is in our
    // scroll zone.
    public void autoscroll(Point pt)
    {
        int nRow = getClosestRowForLocation(pt.x, pt.y);
        
        // If we are not on a row then ignore this autoscroll request
        if (nRow < 0)
            return;
        
        Rectangle raOuter = getBounds();
        // Now decide if the row is at the top of the screen or at the
        // bottom. We do this to make the previous row (or the next
        // row) visible as appropriate. If we�re at the absolute top or
        // bottom, just return the first or last row respectively.
        
        nRow =	(pt.y + raOuter.y <= AUTOSCROLL_MARGIN)			// Is row at top of screen?
        ?
            (nRow <= 0 ? 0 : nRow - 1)	 					// Yes, scroll up one row
            :
                (nRow < getRowCount() - 1 ? nRow + 1 : nRow);	// No, scroll down one row
        
        nRow =	(pt.y + raOuter.y <= AUTOSCROLL_MARGIN)			// Is row at top of screen?
        ?
            (nRow <= 0 ? 0 : nRow - 1)						// Yes, scroll up one row
            :
                (nRow < getRowCount() - 1 ? nRow + 1 : nRow);	// No, scroll down one row
        
        scrollRowToVisible(nRow);
    }
    // Calculate the insets for the *JTREE*, not the viewport
    // the tree is in. This makes it a bit messy.
    public Insets getAutoscrollInsets()
    {
        Rectangle raOuter = getBounds();
        Rectangle raInner = getParent().getBounds();
        return new Insets(
                            raInner.y - raOuter.y + AUTOSCROLL_MARGIN, raInner.x - raOuter.x + AUTOSCROLL_MARGIN,
                            raOuter.height - raInner.height - raInner.y + raOuter.y + AUTOSCROLL_MARGIN,
                            raOuter.width - raInner.width - raInner.x + raOuter.x + AUTOSCROLL_MARGIN);
    }

    
    /* _________________________________________________________________________
     * _________________________________________________________________________
     * _________________________________________________________________________ */
    
    
    private class DefaultTreeDragGestureListener implements DragGestureListener
    {
        public void dragGestureRecognized(DragGestureEvent dragGestureEvent)
        {
            // Can only drag leafs
            GenericTree tree = (GenericTree) dragGestureEvent.getComponent();
            SibType var = tree.getSelectedElement();
            if (var == null)
            {   // Nothing selected, nothing to drag
                tree.getToolkit().beep();
            }
            else
            {
                Point ptDragOrigin = dragGestureEvent.getDragOrigin();
                TreePath path = GenericTree.this.getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);
                if (path == null)
                    return;
                if (GenericTree.this.isRootPath(path))
                    return;	// Ignore user trying to drag the root node
                
                
                
                // Work out the offset of the drag point from the TreePath bounding rectangle origin
                Rectangle raPath = GenericTree.this.getPathBounds(path);
                GenericTree.this.ptOffset.setLocation(ptDragOrigin.x-raPath.x, ptDragOrigin.y-raPath.y);
                
                // Get the cell renderer (which is a JLabel) for the path being dragged
                JLabel lbl = (JLabel) GenericTree.this.getCellRenderer().getTreeCellRendererComponent
                                    (   GenericTree.this, 											// tree
                                    path.getLastPathComponent(),					// value
                                    false,											// isSelected	(dont want a colored background)
                                    GenericTree.this.isExpanded(path), 								// isExpanded
                                    GenericTree.this.getModel().isLeaf(path.getLastPathComponent()), // isLeaf
                                    0, 												// row			(not important for rendering)
                                    false											// hasFocus		(dont want a focus rectangle)
                                    );
                lbl.setSize((int)raPath.getWidth(), (int)raPath.getHeight()); // <-- The layout manager would normally do this
                
                // Get a buffered image of the selection for dragging a ghost image
                GenericTree.this.ghostImage = new BufferedImage((int)raPath.getWidth(), (int)raPath.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
                Graphics2D g = GenericTree.this.ghostImage.createGraphics();
                
                // Ask the cell renderer to paint itself into the BufferedImage
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));		// Make the image ghostlike
                lbl.paint(g);
                
                // Now paint a gradient UNDER the ghosted JLabel text (but not under the icon if any)
                // Note: this will need tweaking if your icon is not positioned to the left of the text
                Icon icon = lbl.getIcon();
                int nStartOfText = (icon == null) ? 0 : icon.getIconWidth()+lbl.getIconTextGap();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.5f));	// Make the gradient ghostlike
                g.setPaint(new GradientPaint(nStartOfText,	0, SystemColor.controlShadow,
                                    getWidth(),	0, new Color(255,255,255,0)));
                g.fillRect(nStartOfText, 0, GenericTree.this.getWidth(), ghostImage.getHeight());
                
                g.dispose();
                
                GenericTree.this.setSelectionPath(path);	// Select this path in the tree
                
//                Transferable t = new TypeTransferable(var);
//                dragGestureEvent.startDrag(DragSource.DefaultMoveDrop, t, new DefaultDragSourceListener());
            }
        }
    }
    
    private class DefaultTreeDropTargetListener implements DropTargetListener
    {
        
        private TreePath      pathLast	     = null;
        private Rectangle2D   lineRectangle  = new Rectangle2D.Float();
        private Rectangle2D   ghostRectangle = new Rectangle2D.Float();
        private Color	      lineColor;
        private Point	      ptLast	     = new Point();
        private Timer	      timerHover;
        private int	      nLeftRight     = 0;	// Cumulative left/right mouse movement
        private BufferedImage imgRight	     = new ArrowImage(15,15,CArrowImage.ARROW_RIGHT);
        private BufferedImage imgLeft	     = new ArrowImage(15,15,CArrowImage.ARROW_LEFT );
        private int           nShift	     = 0;
        
        // Constructor...
        public DefaultTreeDropTargetListener()
        {
            lineColor = Color.BLACK;
            
            // Set up a hover timer, so that a node will be automatically expanded or collapsed
            // if the user lingers on it for more than a short time
            timerHover = new Timer(1500, new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    nLeftRight = 0;	// Reset left/right movement trend
                    if (GenericTree.this.isRootPath(pathLast))
                        return;	// Do nothing if we are hovering over the root node
                    if (GenericTree.this.isExpanded(pathLast))
                        GenericTree.this.collapsePath(pathLast);
                    else
                        GenericTree.this.expandPath(pathLast);
                }
            });
            timerHover.setRepeats(false);	// Set timer to one-shot mode
        }
        
        public void dragEnter(DropTargetDragEvent dropTargetDragEvent)
        {   SibType selected = GenericTree.this.getElementUnderMouse();
            
            if ( selected instanceof SibCollection ) dropTargetDragEvent.acceptDrag(dropTargetDragEvent.getDropAction());
            else                                      dropTargetDragEvent.rejectDrag();
        }
        
        public void dragExit(DropTargetEvent dropTargetEvent)
        {   }
        
        public void dragOver(DropTargetDragEvent dropTargetDragEvent)
        {   // Even if the mouse is not moving, this method is still invoked 10 times per second
            Point pt = dropTargetDragEvent.getLocation();
            if ( ! pt.equals(ptLast) )
            {
                // Try to determine whether the user is flicking the cursor right or left
                int nDeltaLeftRight = pt.x - ptLast.x;
                if ( (nLeftRight > 0 && nDeltaLeftRight < 0) || (nLeftRight < 0 && nDeltaLeftRight > 0) )
                    nLeftRight = 0;
                nLeftRight += nDeltaLeftRight;
                
                ptLast = pt;
            }
            
            Graphics2D g2 = (Graphics2D) getGraphics();
            
            // If a drag image is not supported by the platform, then draw my own drag image
            if (!DragSource.isDragImageSupported())
            {
                GenericTree.this.paintImmediately(ghostRectangle.getBounds());	// Rub out the last ghost image and cue line
                // And remember where we are about to draw the new ghost image
                ghostRectangle.setRect(pt.x - GenericTree.this.ptOffset.x, pt.y - GenericTree.this.ptOffset.y,
                                    ghostImage.getWidth(), ghostImage.getHeight());
                g2.drawImage(ghostImage, AffineTransform.getTranslateInstance(ghostRectangle.getX(),
                                    ghostRectangle.getY()), null);
            }
            else	// Just rub out the last cue line
                paintImmediately(lineRectangle.getBounds());
            
            
            
            TreePath path = getClosestPathForLocation(pt.x, pt.y);
            if (!(path == pathLast))
            {
                nLeftRight = 0; 	// We've moved up or down, so reset left/right movement trend
                pathLast = path;
                timerHover.restart();
            }
            
            // In any case draw (over the ghost image if necessary) a cue line indicating where a drop will occur
            Rectangle raPath = getPathBounds(path);
            lineRectangle.setRect(0,  raPath.y+(int)raPath.getHeight(), getWidth(), 2);
            
            g2.setColor(lineColor);
            g2.fill(lineRectangle);
            
            // Now superimpose the left/right movement indicator if necessary
            if ( false )
            {
                if (nLeftRight > 20)
                {
                    g2.drawImage(imgRight, AffineTransform.getTranslateInstance(pt.x - GenericTree.this.ptOffset.x,
                                        pt.y - GenericTree.this.ptOffset.y), null);
                    nShift = +1;
                }
                else if (nLeftRight < -20)
                {
                    g2.drawImage(imgLeft, AffineTransform.getTranslateInstance(pt.x - GenericTree.this.ptOffset.x,
                                        pt.y - GenericTree.this.ptOffset.y), null);
                    nShift = -1;
                }
                else
                    nShift = 0;
            }
            
            // And include the cue line in the area to be rubbed out next time
            ghostRectangle = ghostRectangle.createUnion(lineRectangle);
            
            SibType selected = GenericTree.this.getElementUnderMouse();
            
            if ( selected instanceof SibCollection )
            {   /* test the type */
                try
                {
                    Transferable transferable = dropTargetDragEvent.getTransferable();
                    DataFlavor[] flavours = dropTargetDragEvent.getCurrentDataFlavors();
                    for(int i = 0; i < flavours.length; i++)
                    {   System.out.println("\tflavour i=" + i + " --> " + flavours[i]);
                        System.out.println("\t\t" + transferable.getTransferData(flavours[i]));
                        System.out.println("\t\t\t" + transferable.getTransferData(flavours[i]).getClass());
                    }
                    
                    System.out.println("a faire !!");
                    if ( dropTargetDragEvent.getTransferable().getTransferData
                                        (dropTargetDragEvent.getCurrentDataFlavors()[0]).getClass().
                                        isAssignableFrom(SibType.class))//((SibList)selected).getItemType()) )
                        dropTargetDragEvent.acceptDrag(dropTargetDragEvent.getDropAction());
                    else
                        dropTargetDragEvent.rejectDrag();
                }
                catch(Exception ufe)
                {   dropTargetDragEvent.rejectDrag(); }
            }
            else
                dropTargetDragEvent.rejectDrag();
        }
        
        public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent)
        {   }
        
        public synchronized void drop(DropTargetDropEvent dropTargetDropEvent)
        {
            SibType current = null;
            
            try
            {   Object currentObj = dropTargetDropEvent.getTransferable().
                                    getTransferData(dropTargetDropEvent.getCurrentDataFlavors()[0]);
                
                if ( ! (currentObj instanceof SibType) )
                {   dropTargetDropEvent.getDropTargetContext().dropComplete(true);
                    return;
                }
                current = (SibType)currentObj;
            }
            catch(UnsupportedFlavorException fe)
            { fe.printStackTrace(); }
            catch(IOException io)
            { io.printStackTrace(); }
            // PENDING : to do
//            KernelResources.getInstance().remove(current, current.getParent());
            
            SibType   parent = GenericTree.this.getElementUnderMouse();
            // PENDING : to do
//            KernelResources.getInstance().add(current, parent);
            
            dropTargetDropEvent.getDropTargetContext().dropComplete(true);
            
            GenericTree.this.paintImmediately(ghostRectangle.getBounds());
        }
    }
    
    private static class DefaultDragSourceListener implements DragSourceListener
    {
        public void dragDropEnd(DragSourceDropEvent dragSourceDropEvent)
        {
            if (dragSourceDropEvent.getDropSuccess())
            {
                int dropAction = dragSourceDropEvent.getDropAction();
                if (dropAction == DnDConstants.ACTION_MOVE)
                {   System.out.println("MOVE: remove node"); }
            }
        }
        
        public void dragEnter(DragSourceDragEvent dragSourceDragEvent)
        {
            DragSourceContext context = dragSourceDragEvent.getDragSourceContext();
            int dropAction = dragSourceDragEvent.getDropAction();
            if ((dropAction & DnDConstants.ACTION_COPY) != 0)
            {
                context.setCursor(DragSource.DefaultCopyDrop);
            }
            else if ((dropAction & DnDConstants.ACTION_MOVE) != 0)
            {
                context.setCursor(DragSource.DefaultMoveDrop);
            }
            else
            {   context.setCursor(DragSource.DefaultCopyNoDrop); }
        }
        
        public void dragExit(DragSourceEvent dragSourceEvent)
        {   }
        
        public void dragOver(DragSourceDragEvent dragSourceDragEvent)
        {   }
        
        public void dropActionChanged(DragSourceDragEvent dragSourceDragEvent)
        {   }
    }
}
