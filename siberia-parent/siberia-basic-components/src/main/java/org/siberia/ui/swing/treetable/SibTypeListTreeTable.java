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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.siberia.type.SibType;
import org.siberia.ui.swing.error.ErrorOriginator;
import org.siberia.ui.swing.table.SibTypeListTable;
import org.siberia.ui.swing.tree.GenericTree;
import org.siberia.ui.swing.treetable.adapter.AdapterFactory;
import org.siberia.ui.swing.treetable.adapter.DefaultAdapterFactory;
import org.siberia.ui.swing.treetable.introspection.IntrospectionSibTypeListTreeTableModel;

/**
 *
 * TreeTable based on a SibTypeListTable
 *
 * @author alexis
 */
public class SibTypeListTreeTable extends SibTypeListTable implements TreeExpansionListener
{   
    protected TreeTableCellRenderer  tree              = null;
    
    protected TreeTableCellEditor    treeEditor        = null;
    
    /** propertyChangeListener that allow to listen to factory change on the TreeTableModel */
    protected PropertyChangeListener adapterListener   = null;
    
    /** line style listener */
    protected PropertyChangeListener lineStyleListener = null;
    
    private   boolean                consumedOnPress   = false;
    
    /** index of the column in which the tree is displayed */
    private   int                    treeColumn        = 0;
        
    /** Creates a new instance of SibTypeListTreeTable */
    public SibTypeListTreeTable()
    {
	super();
	
        super.setSortable(false);
	
//	this.setIntercellSpacing(new Dimension(0, 0));
	
	this.adapterListener = new PropertyChangeListener()
	{
	    public void propertyChange(PropertyChangeEvent evt)
	    {
		if ( evt.getSource() == getTreeTableModel() && TreeTableModel.PROPERTY_ADAPTER_FACTORY.equals(evt.getPropertyName()) )
		{
		    initTableModel();
		}
	    }
	};
    }
    
    /** return the index of the column in which the tree is displayed
     *	@return an integer
     */
    public int getTreeColumnIndex()
    {	return this.treeColumn; }
    
    /** initialize the index of the column in which the tree is displayed
     *	@param column an integer
     */
    public void setTreeColumnIndex(int column)
    {	
	if ( column != this.getTreeColumnIndex() )
	{
	    this.treeColumn = column;
	    
	    this.repaint();
	}
    }
    
    /**
      * Called whenever an item in the tree has been expanded.
      */
    public void treeExpanded(TreeExpansionEvent event)
    {
	if ( event.getSource() == this.getTreeRenderer() )
	{
	    this.tableChanged(new TableModelEvent(this.getModel(), 0, this.getRowCount()));
	    
	    this.updatePagesCountAndPageSelection();
	}
    }

    /**
      * Called whenever an item in the tree has been collapsed.
      */
    public void treeCollapsed(TreeExpansionEvent event)
    {
	if ( event.getSource() == this.getTreeRenderer() )
	{
	    this.tableChanged(new TableModelEvent(this.getModel(), 0, this.getRowCount()));
	    
	    this.updatePagesCountAndPageSelection();
	}
    }

    @Override
    public boolean isCellEditableImpl(int rowIndex, int columnIndex)
    {
	if ( rowIndex == 0 && (this.getCurrentPage() == 0 || ! this.arePageableFunctionnalitiesActivated()) )
	{
	    return false;
	}
	else
	{
	    return super.isCellEditableImpl(rowIndex, columnIndex);
	}
    }

    @Override
    public void setValueAtImpl(Object aValue, int rowIndex, int columnIndex)
    {
	TreePath path = this.getTreeRenderer().getPathForRow( this.convertRowIndexToWrapped(rowIndex) );
	
	if ( columnIndex == this.getTreeColumnIndex() )
	{   
	    this.getTreeRenderer().getModel().valueForPathChanged(path, aValue);
	    this.repaint();
	}
	else
	{
	    this.getTreeTableModel().setValueAt(aValue, path.getLastPathComponent(), columnIndex);
	}
    }

//    /** this kind of table is not sortable */
//    @Override
//    public void setSortable(boolean sortable)
//    {	/* do nothing */ }
//
//    /** this kind of table does not supports filters */
//    @Override
//    public void setFilters(FilterPipeline pipeline)
//    {	/* do nothing */ }
    
    @Override
    public String getToolTipText(MouseEvent event)
    {
	int yDec = this.arePageableFunctionnalitiesActivated() ?
			    (this.getCurrentPage() * this.getMaximumDisplayedRows() * this.getRowHeight()) :
			    0;
	
	MouseEvent convertedEvent = new MouseEvent((Component)event.getSource(),
						   event.getID(),
						   event.getWhen(),
						   event.getModifiers(),
						   event.getX(),
						   event.getY() - yDec,
						   event.getXOnScreen(),
						   event.getYOnScreen() - yDec,
						   event.getClickCount(),
						   event.isPopupTrigger(),
						   event.getButton());
	
        int column = columnAtPoint(convertedEvent.getPoint());
        if ( column == this.getTreeColumnIndex() )
	{
	    // ne fonctionne pas --> return -1
//            int row = rowAtPoint(convertedEvent.getPoint());
            int row = rowAtPoint(event.getPoint()) + 
				 (this.getCurrentPage() * this.getMaximumDisplayedRows());
	    
	    
            return this.tree.getToolTipText(convertedEvent, row, column);
        }
        return super.getToolTipText(convertedEvent);
    }

    /**
     * Overridden to message super and forward the method to the tree.
     * Since the tree is not actually in the component hieachy it will
     * never receive this unless we forward it in this manner.
     */
    @Override
    public void updateUI()
    {
        super.updateUI();
        if ( this.getTreeRenderer() != null )
	{
            this.getTreeRenderer().updateUI();
	}
    }
    
    /** method that allow to initialize my TableModel */
    private void initTableModel()
    {
	// Install a tableModel representing the visible rows in the tree.
	TreeTableModel model = this.getTreeTableModel();
	AdapterFactory factory = model.getAdapterFactory();
	if ( factory == null )
	{
	    factory = new DefaultAdapterFactory();
	}
	
	if ( model != null )
	{
	    model.removePropertyChangeListener(TreeTableModel.PROPERTY_ADAPTER_FACTORY, this.adapterListener);
	}
	
	TableModel tableModel = factory.createModelAdapter(model, this.tree);
	
	super.setModel(tableModel);
    }
    
    @Override
    public void setSelectionMode(int mode)
    {
        if (this.getTreeRenderer() != null)
	{
            switch (mode)
	    {
                case ListSelectionModel.SINGLE_INTERVAL_SELECTION:
		{
                    this.getTreeRenderer().getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
                    break;
                }
                case ListSelectionModel.MULTIPLE_INTERVAL_SELECTION:
		{
                    this.getTreeRenderer().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
                    break;
                }
                default:
		{
                    this.getTreeRenderer().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                    break;
                }
            }
        }
        super.setSelectionMode(mode);
    }
    
    /** initialize the model to use for the component
     *	@param model a TreeTableModel
     */
    public void setTreeTableModel(TreeTableModel model)
    {
	TreeTableModel oldModel = this.getTreeTableModel();
	
	if ( oldModel != null )
	{
	    oldModel.removePropertyChangeListener(TreeTableModel.PROPERTY_ADAPTER_FACTORY, this.adapterListener);
	    
	    if ( oldModel instanceof ErrorOriginator )
	    {
		((ErrorOriginator)oldModel).removeErrorHandler(this);
	    }
	}
	
	if ( model instanceof ErrorOriginator )
	{
	    ((ErrorOriginator)model).addErrorHandler(this);
	}
	
	// Create the tree. It will be used as a renderer and editor.
	
	if ( this.tree != null )
	{
	    this.tree.removeTreeExpansionListener(this);
	}
	
        ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper();
	
	this.tree = new TreeTableCellRenderer();
	this.tree.setPopupInvoker(this);
	this.tree.setModel(model);
        this.tree.setSelectionModel(selectionWrapper);
	
        this.setSelectionModel(selectionWrapper.getListSelectionModel());
	
	/* force to update the selection mode of the tree */
	this.setSelectionMode(this.getSelectionMode());
	
	this.tree.addTreeExpansionListener(this);
	
        // propagate the lineStyle property to the renderer
	if ( this.lineStyleListener == null )
	{
	    this.lineStyleListener = new PropertyChangeListener()
	    {   
		public void propertyChange(PropertyChangeEvent evt)
		{
		    getTreeRenderer().putClientProperty(evt.getPropertyName(), evt.getNewValue());
		}

	    };
	    this.addPropertyChangeListener("JTree.lineStyle", this.lineStyleListener);
	}
	
	this.treeEditor = new TreeTableCellEditor(this.tree);
	
	this.initTableModel();
	
	// Make the tree and table row heights the same. 
	this.tree.setRowHeight(getRowHeight());

	// Install the tree editor renderer and editor. 
	this.setDefaultRenderer(JTree.class, this.tree);
    }
    
    /** get the model to use for the component
     *	@return a TreeTableModel
     */
    public TreeTableModel getTreeTableModel()
    {
	TreeTableModel model = null;
	if ( this.getTreeRenderer() != null )
	{
	    model = (TreeTableModel)this.getTreeRenderer().getModel();
	}
	return model;
    }

    /* Workaround for BasicTableUI anomaly. Make sure the UI never tries to 
     * paint the editor. The UI currently uses different techniques to 
     * paint the renderers and editors and overriding setBounds() below 
     * is not the right thing to do for an editor. Returning -1 for the 
     * editing row in this case, ensures the editor is never painted. 
     */
    public int getEditingRow() {
        return (getColumnClass(editingColumn) == JTree.class) ? -1 : editingRow;  
    }
    
    /** retourne l'arbre responsable du rendu
     *  @return un GenericTree
     */
    public GenericTree getTreeRenderer()
    {   return this.tree; }
    
    /** this method is override to perform a fast research for the editor of the tree */
    @Override
    public TableCellEditor getCellEditor(int row, int column)
    {   
        TableCellEditor result = null;
	
	Class c = this.getColumnClass(column);
	
	if ( JTree.class.isAssignableFrom(c) )
	{
	    result = this.treeEditor;
	}
	else
	{
	    result = super.getCellEditor(row, column);
	}
        
        return result;
    }
    
    /** this method is override to perform a fast research for the renderer of the tree */
    @Override
    public TableCellRenderer getCellRenderer(int row, int column)
    {
        TableCellRenderer result = null;
	
	Class c = this.getColumnClass(column);
	
	if ( JTree.class.isAssignableFrom(c) )
	{
	    result = this.tree;
	}
	else
	{
	    result = super.getCellRenderer(row, column);
	}
        
        return result;
    }

    /**
     * Overridden to enable hit handle detection a mouseEvent which triggered
     * a expand/collapse. 
     */
    @Override
    protected void processMouseEvent(MouseEvent e)
    {
	int column = this.columnAtPoint(e.getPoint());
	if ( column == this.getTreeColumnIndex() )
	{
	    MouseEvent converted = e;
	    
	    if ( this.arePageableFunctionnalitiesActivated() && this.getCurrentPage() > 0 )
	    {
		int dec = (this.getCurrentPage() * this.getMaximumDisplayedRows()) * this.getRowHeight();
		
		converted = new MouseEvent(this.getTreeRenderer(),
					   e.getID(),
					   e.getWhen(),
					   e.getModifiers(),
					   e.getX(),
					   e.getY() + dec,
					   e.getXOnScreen(),
					   e.getYOnScreen() + dec,
					   e.getClickCount(),
					   e.isPopupTrigger(),
					   e.getButton());
	    }
	    
	    this.getTreeRenderer().dispatchEvent(converted);
	}
	
	super.processMouseEvent(e);
    }
    
    /** ########################################################################
     *  ########################### inner classes ##############################
     *  ######################################################################## */
    
    // 
    // The renderer used to display the tree nodes, a JTree.  
    //
    private class TreeTableCellRenderer extends GenericTree implements TableCellRenderer
    {

	protected int visibleRow;
   
	public TreeTableCellRenderer()
	{   super();
	    
	    
	}
    
	/** show a JPopupMenu for a given MouseEvent
	 *	@param popup a JPopupMenu
	 *	@param invoker the invoker to use
	 *	@param x the x position
	 *	@param y the y position
	 *	@param selection an array of SibType that are selected
	 */
	@Override
	protected void showPopup(JPopupMenu popup, Component invoker, int x, int y, SibType[] entities)
	{
	    /** consider pagination to modify the position where the popup will be displayed
	     *	that's because, when a MouseEvent occurred in the table and concerns the first column
	     *	then the event is relayed to the tree
	     */
	    int newY = y - (arePageableFunctionnalitiesActivated() ? getMaximumDisplayedRows() * getCurrentPage() * getRowHeight() : 0);
	    super.showPopup(popup, invoker, x, newY, entities);
	}

	public void setBounds(int x, int y, int w, int h)
	{   
	    super.setBounds(x, 0, w, SibTypeListTreeTable.this.getHeight());
	}

	public void paint(Graphics g)
	{   
	    
	    int dec = convertRowIndexToWrapped(visibleRow);
	    
	    dec *= getRowHeight();
	    
	    g.translate(0, - dec);
	    
	    super.paint(g);
	}

	/** copied from Swingx JXTreeTable */
        /**
         * Hack around #297-swingx: tooltips shown at wrong row.
         * 
         * The problem is that - due to much tricksery when rendering the tree -
         * the given coordinates are rather useless. As a consequence, super
         * maps to wrong coordinates. This takes over completely.
         * 
         * PENDING: bidi?
         * 
         * @param event the mouseEvent in treetable coordinates
         * @param row the view row index
         * @param column the view column index
         * @return the tooltip as appropriate for the given row
	 *
         */
	// TODO a revoir
        private String getToolTipText(MouseEvent event, int row, int column) {
            if (row < 0) return null;
            TreeCellRenderer renderer = getCellRenderer();
            TreePath     path = getPathForRow(row);
            Object       lastPath = path.getLastPathComponent();
            Component    rComponent = renderer.getTreeCellRendererComponent
                (this, lastPath, isRowSelected(row),
                 isExpanded(row), getModel().isLeaf(lastPath), row,
                 true);

            if(rComponent instanceof JComponent) {
                Rectangle       pathBounds = getPathBounds(path);
                Rectangle cellRect = getCellRect(row, column, false);
                // JW: what we are after
                // is the offset into the hierarchical column 
                // then intersect this with the pathbounds   
                Point mousePoint = event.getPoint();
                // translate to coordinates relative to cell
                mousePoint.translate(-cellRect.x, -cellRect.y);
                // translate horizontally to 
                mousePoint.translate(-pathBounds.x, 0);
                // show tooltip only if over renderer?
//                if (mousePoint.x < 0) return null;
//                p.translate(-pathBounds.x, -pathBounds.y);
                MouseEvent newEvent = new MouseEvent(rComponent, event.getID(),
                      event.getWhen(),
                      event.getModifiers(),
                      mousePoint.x, 
                      mousePoint.y,
//                    p.x, p.y, 
                      event.getClickCount(),
                      event.isPopupTrigger());
                
                return ((JComponent)rComponent).getToolTipText(newEvent);
            }

            return null;
        }

	public Component getTableCellRendererComponent(JTable table,
						       Object value,
						       boolean isSelected,
						       boolean hasFocus,
						       int row, int column)
	{   if(isSelected)
	    {
		setBackground(table.getSelectionBackground());
	    }
	    else
	    {
		setBackground(table.getBackground());
	    }
       
	    visibleRow = row;
	    return this;
	}
    }
    
    /** copied from Swingx JXTreeTable */
    
    /**
     * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
     * to listen for changes in the ListSelectionModel it maintains. Once
     * a change in the ListSelectionModel happens, the paths are updated
     * in the DefaultTreeSelectionModel.
     */
    class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
    {
        /** Set to true when we are updating the ListSelectionModel. */
        protected boolean updatingListSelectionModel;

        public ListToTreeSelectionModelWrapper() {
            super();
            getListSelectionModel().addListSelectionListener
                (createListSelectionListener());
        }

        /**
         * Returns the list selection model. ListToTreeSelectionModelWrapper
         * listens for changes to this model and updates the selected paths
         * accordingly.
         */
        ListSelectionModel getListSelectionModel() {
            return listSelectionModel;
        }

        /**
         * This is overridden to set <code>updatingListSelectionModel</code>
         * and message super. This is the only place DefaultTreeSelectionModel
         * alters the ListSelectionModel.
         */
        @Override
        public void resetRowSelection() {
            if (!updatingListSelectionModel) {
                updatingListSelectionModel = true;
                try {
                    super.resetRowSelection();
                }
                finally {
                    updatingListSelectionModel = false;
                }
            }
            // Notice how we don't message super if
            // updatingListSelectionModel is true. If
            // updatingListSelectionModel is true, it implies the
            // ListSelectionModel has already been updated and the
            // paths are the only thing that needs to be updated.
        }

        /**
         * Creates and returns an instance of ListSelectionHandler.
         */
        protected ListSelectionListener createListSelectionListener() {
            return new ListSelectionHandler();
        }

        /**
         * If <code>updatingListSelectionModel</code> is false, this will
         * reset the selected paths from the selected rows in the list
         * selection model.
         */
        protected void updateSelectedPathsFromSelectedRows() {
            if (!updatingListSelectionModel) {
                updatingListSelectionModel = true;
                try {
                    if (listSelectionModel.isSelectionEmpty()) {
                        clearSelection();
                    } else {
                        // This is way expensive, ListSelectionModel needs an
                        // enumerator for iterating.
                        int min = listSelectionModel.getMinSelectionIndex();
                        int max = listSelectionModel.getMaxSelectionIndex();

                        List<TreePath> paths = new ArrayList<TreePath>();
                        for (int counter = min; counter <= max; counter++) {
                            if (listSelectionModel.isSelectedIndex(counter))
			    {
				TreePath selPath = null;
				
				if ( getTreeTableModel() instanceof IntrospectionSibTypeListTreeTableModel )
				{
				    selPath = ((IntrospectionSibTypeListTreeTableModel)getTreeTableModel()).getPathConverter().getPathForRow( convertRowIndexToWrapped(counter) );
				}
				else
				{
				    selPath = getTreeRenderer().getPathForRow( convertRowIndexToWrapped(counter) );
				}

                                if (selPath != null) {
                                    paths.add(selPath);
                                }
                            }
                        }
                        setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
                        // need to force here: usually the leadRow is adjusted 
                        // in resetRowSelection which is disabled during this method
                        leadRow = leadIndex;
                    }
		    
		    /* force repaint to avoid non selected path to be painted as selected */
		    repaint();
                }
                finally {
                    updatingListSelectionModel = false;
                }
            }
        }

        /**
         * Class responsible for calling updateSelectedPathsFromSelectedRows
         * when the selection of the list changse.
         */
        class ListSelectionHandler implements ListSelectionListener {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateSelectedPathsFromSelectedRows();
                }
            }
        }
    }
}
