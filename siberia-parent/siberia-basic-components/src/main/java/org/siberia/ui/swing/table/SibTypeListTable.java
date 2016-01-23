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
package org.siberia.ui.swing.table;

import com.l2fprod.common.propertysheet.CellEditorAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.WeakHashMap;
import javax.swing.DefaultCellEditor;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.apache.log4j.Logger;
import org.siberia.ui.swing.property.ExtendedPropertyEditorFactory;
import org.siberia.ui.swing.property.ExtendedPropertyRendererFactory;
import org.siberia.ui.swing.table.model.IntrospectionSibTypeListTableModel;
import org.siberia.ui.swing.table.model.ObjectsTableModel;
import org.siberia.ui.swing.table.model.PropertyBasedTableModel;
import org.siberia.ui.swing.table.model.PropertyBasedTableModelListener;
import org.siberia.ui.swing.table.model.PropertyDeclaration;
import org.siberia.ui.swing.table.model.SibTypeListTableModel;
import org.siberia.ui.swing.table.model.SiberiaTableModel;
import org.siberia.ui.bar.PluginBarFactory;
import org.siberia.ui.swing.transfer.SibTypeTransferHandler;

/**
 *
 * Table that is able to display a List of ColdType
 *
 * @author alexis
 */
public class SibTypeListTable extends NumberedPageableTable implements PropertyBasedTableModelListener
{
    /** property use empty renderer */
    public static final String PROPERTY_USE_EMPTY_RENDERER = "use-empty-renderer";
    
    /** logger */
    private Logger                                logger               = Logger.getLogger(SibTypeListTable.class);
    
    private ExtendedPropertyEditorFactory         editorFactory        = null;
    private ExtendedPropertyRendererFactory       rendererFactory      = null;
    
    /** hashmap for editors */
    private WeakHashMap<Class, TableCellEditor>   editorsMap           = new WeakHashMap<Class, TableCellEditor>();
    
    /** hashmap for renderers */
    private WeakHashMap<Class, TableCellRenderer> renderersMap         = new WeakHashMap<Class, TableCellRenderer>();
    
    /** empty renderer */
    private EmptyRenderer                         emptyRenderer        = null;

    /** true to use the empty renderer for a cell that has no data */
    private boolean                               useEmptyRenderer     = true;
    
    /** true to indicate to propose context menu provided by the plauginBarFactory */
    private boolean                               automaticContextMenu = true;
    
    /** Creates a new instance of SibTable */
    public SibTypeListTable()
    {   
	this.setTransferHandler(new SibTypeTransferHandler());
	this.setDragEnabled(true);
	
	this.editorFactory = new ExtendedPropertyEditorFactory();
        this.rendererFactory = new ExtendedPropertyRendererFactory();
        
        this.addMouseListener(new MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                if ( isAutomaticContextMenuEnabled() && (SwingUtilities.isRightMouseButton(e) || e.isPopupTrigger()) )
                {
                    SibTypeListTableModel listModel = null;
                    TableModel            model     = getModel();

                    if ( model instanceof SibTypeListTableModel )
                    {   listModel = (SibTypeListTableModel)model; }

                    if ( listModel != null )
                    {   int[] selection = getSelectedRows();
                        
                        int rowUnderMouse = rowAtPoint(e.getPoint());
                        
                        boolean rowUnderMouseAlreadySelected = false;
                        
                        if ( selection != null && rowUnderMouse >= 0 )
                        {   for(int i = 0; i < selection.length; i++)
                            {   if ( rowUnderMouse == selection[i] )
                                {   rowUnderMouseAlreadySelected = true;
                                    break;
                                }
                            }
                        }
                        
                        if ( ! rowUnderMouseAlreadySelected && rowUnderMouse >= 0 )
                        {   getSelectionModel().setSelectionInterval(rowUnderMouse, rowUnderMouse);
                            selection = getSelectedRows();
                        }
                        
                        Object[] selectedItems = getSelectedObjects();
                        
                        JPopupMenu menu = PluginBarFactory.getInstance().getContextMenuForItem(selectedItems);
                        if ( menu != null )
                        {   menu.show(SibTypeListTable.this, e.getX(), e.getY()); }
                    }
                }
            }
        });
    }
    
    /** return true if context menus are provided by the plauginBarFactory
     *	@return a boolean
     */
    public boolean isAutomaticContextMenuEnabled()
    {
	return automaticContextMenu;
    }

    /** indicate if context menus are provided by the plauginBarFactory
     *	@param automaticContextMenu a boolean
     */
    public void setAutomaticContextMenuEnabled(boolean automaticContextMenu)
    {
	if ( automaticContextMenu != this.isAutomaticContextMenuEnabled() )
	{
	    this.automaticContextMenu = automaticContextMenu;
	    
	    this.firePropertyChange("automatic-context-popup", ! automaticContextMenu, automaticContextMenu);
	}
    }

    @Override
    public void setModel(TableModel newModel)
    {
	TableModel oldModel = this.getModel();
	
	if ( oldModel instanceof PropertyBasedTableModel )
	{
	    ((PropertyBasedTableModel)oldModel).removePropertyBasedTableModelListener(this);
	}
	
	super.setModel(newModel);
	
	if ( newModel instanceof PropertyBasedTableModel )
	{
	    ((PropertyBasedTableModel)newModel).addPropertyBasedTableModelListener(this);
	}
    }
    
    /** return an array of Object currently selected
     *  @return an array containing the objects located at selected rows
     */
    public Object[] getSelectedObjects()
    {   Object[] selectedItems = null;
        
        int[] selection = getSelectedRows();
                        
        if ( selection != null )
        {
            ObjectsTableModel     listModel = null;
            TableModel            model     = getModel();

            if ( model instanceof ObjectsTableModel )
            {   listModel = (ObjectsTableModel)model; }
         
            if ( listModel != null )
            {
                selectedItems = new Object[selection.length];

                for(int i = 0; i < selectedItems.length; i++)
                {   
		    int selectionIndex = selection[i];
		    if ( selectionIndex >= 0 && selectionIndex < this.getRowCount() )
		    {
			int tableIndex = selection[i];
			int index = this.getModelIndexForTableRowAt(tableIndex);
			
			selectedItems[i] = listModel.getItem(index);
		    }
		    else
		    {
			selectedItems[i] = null;
		    }
                }
            }
        }
        
        
        return selectedItems;
    }
    
    /** return the index of the given item
     *  @param item an item that managed by the model
     *  @return the index of the item in the table or -1 if it does not appear
     */
    public int getIndexOfItem(Object item)
    {
	int row = -1;
	
	TableModel model = this.getModel();
	
	if ( model instanceof ObjectsTableModel )
	{
	    int rowModel = ((ObjectsTableModel)model).getIndexOfItem(item);
	    
	    if ( rowModel >= 0 && rowModel < model.getRowCount() )
	    {
		row = this.getTableIndexForRowModelAt(rowModel);
	    }
	}
	
	return row;
    }
    
    /** return the index of the given item
     *  @param item an item that managed by the model
     *  @return the index of the item in the table or -1 if it does not appear
     */
    public int getAbsoluteIndexOfItem(Object item)
    {
	int result = this.getIndexOfItem(item);

	/** take pagination into account */
	if ( this.arePageableFunctionnalitiesActivated() )
	{
	    result = result + (this.getMaximumDisplayedRows() * this.getCurrentPage());

	    if ( result < 0 )
	    {   result = -1; }
	}
	
	return result;
    }
    
    /** return the Object located at the given position
     *  @param tableIndex an integer
     *  @return an Object or null if not found
     */
    public Object getItem(int tableIndex)
    {
        Object result = null;
        
        TableModel model = this.getModel();
        
        if ( model instanceof ObjectsTableModel )
        {
	    if ( tableIndex >= 0 && tableIndex < model.getRowCount() )
	    {
		result = ((ObjectsTableModel)model).getItem(this.getModelIndexForTableRowAt(tableIndex));
	    }
        }
        
        return result;
    }
    
    /** return the Object located at the given position
     *  @param tableIndex an integer
     *  @return an Object or null if not found
     */
    public Object getItemAtAbsoluteIndex(int tableIndex)
    {
        Object result = null;
        
        TableModel model = this.getModel();
        
        if ( model instanceof ObjectsTableModel )
        {
	    if ( tableIndex >= 0 && tableIndex < model.getRowCount() )
	    {
		int index = tableIndex;

//		/** take pagination into account */
//		if ( this.arePageableFunctionnalitiesActivated() )
//		{
//		    index = index + (this.getMaximumDisplayedRows() * this.getCurrentPage());
//		}

		/** consider filter and sorter */
		index = this.convertRowIndexToModel(index);
		
		
		result = ((ObjectsTableModel)model).getItem(index);
	    }
        }
        
        return result;
    }
    
    @Override
    public TableCellEditor getCellEditor(int row, int column)
    {   
        TableCellEditor result = null;
        Class c = this.getColumnClass(column);
	
        result = this.editorsMap.get(c);
        
        if ( result == null )
        {   PropertyEditor editor = null;
            
            Class currentClass = c;
            while(currentClass != null && ! currentClass.equals(Object.class))
            {   editor = this.editorFactory.getEditor(currentClass);
                
                if ( editor != null )
                {   break; }
                else
                {   currentClass = currentClass.getSuperclass(); }
            }
        
            if (editor != null)
            {   result = new CellEditorAdapter(editor);
                this.editorsMap.put(c, result);
            }
        }
        
        if ( result == null )
        {   result = super.getCellEditor(row, column); }
        
        return result;
    }
    
    @Override
    public TableCellRenderer getCellRenderer(int row, int column)
    {   TableCellRenderer renderer = null;
        Class c = null;
	
	/* special case when SiberiaTableModel --> use extension */
	if ( this.getUseEmptyRenderer() && this.getModel() instanceof SiberiaTableModel )
	{
	    boolean containsData = ((SiberiaTableModel)this.getModel()).containsDataAt(
//		    this.convertRowIndexToWrapped(this.convertRowIndexToModel(row)),
		    this.convertRowIndexToModel(this.convertRowIndexToWrapped(row)),
		    this.convertColumnIndexToModel(column));
	    if ( containsData )
	    {
		c = this.getColumnClass(column);
	    }
	}
	else
	{   
	    c = super.getColumnClass(column);
	}
	
	if ( c == null )
	{
	    /* use empty renderer */
	    if ( this.emptyRenderer == null )
	    {	this.emptyRenderer = new EmptyRenderer(); }
	    
	    renderer = this.emptyRenderer;
	}
	else
	{
	    renderer = this.renderersMap.get(c);

	    if ( renderer == null )
	    {   
		Class currentClass = c;
		while(currentClass != null && ! currentClass.equals(Object.class))
		{   renderer = this.rendererFactory.getRenderer(currentClass);

		    if ( renderer != null )
		    {   break; }
		    else
		    {   currentClass = currentClass.getSuperclass(); }
		}

		this.renderersMap.put(c, renderer);
	    }
	}
        
        if ( renderer == null )
        {   renderer = super.getCellRenderer(row, column); }
        
        return renderer;
    }
    
    @Override
    public void updateUI()
    {
	super.updateUI();
	
	/** reinit */
	if ( this.editorFactory != null )
	{
	    this.editorFactory.registerDefaults();
	}
	
	/** reinit */
	if ( this.rendererFactory != null )
	{
	    this.rendererFactory.registerDefaults();
	}
	
	if ( this.renderersMap != null )
	{
	    this.renderersMap.clear();
	}
	if ( this.editorsMap != null )
	{
	    this.editorsMap.clear();
	}
	
	this.repaint();
    }

    /** return true to use the empty renderer for a cell that has no data
     *	@return a boolean
     */
    public boolean getUseEmptyRenderer()
    {
	return useEmptyRenderer;
    }

    /** indicate wheter to use the empty renderer for a cell that has no data
     *	@param useEmptyRenderer true to use the empty renderer for a cell that has no data
     */
    public void setUseEmptyRenderer(boolean useEmptyRenderer)
    {
	if ( useEmptyRenderer != this.getUseEmptyRenderer() )
	{
	    this.useEmptyRenderer = useEmptyRenderer;
	    
	    this.firePropertyChange(PROPERTY_USE_EMPTY_RENDERER, ! this.getUseEmptyRenderer(), this.getUseEmptyRenderer());
	}
    }
    
    /** customize a TableColumn
     *	@param column a TableColumn
     *	@param index of the table column
     */
    @Override
    protected void postPrepareTableColumn(TableColumn column, int index)
    {
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("entering prepareTableColumn(TableColumn, int)");
	    logger.debug("calling prepareTableColumn(TableColumn, int) with " + column + " and " + index);
	}
	
	super.postPrepareTableColumn(column, index);
	
//	this.setColumnSize(column, index, true, true);
	
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("exiting prepareTableColumn(TableColumn, int)");
	}
    }

    @Override
    protected void initializeColumnPreferredWidth(TableColumn tableColumn)
    {
	super.initializeColumnPreferredWidth(tableColumn);
	
	int index = -1;
	
	for(int i = 0; i < this.getColumnModel().getColumnCount() && index < 0; i++)
	{
	    TableColumn current = this.getColumnModel().getColumn(i);
	    if ( current == tableColumn )
	    {
		index = i;
	    }
	}
	
	if ( index > 0 )
	{
	    this.setColumnSize(tableColumn, index, false, true);
	}
    }
    
    /** set the size for all columns according to property declaration of the model
     *	@param column a TableColumn
     *	@param index the index of the column
     *	@param setSize true to set the size
     *	@param setPreferredSize true to set the preferred size
     */
    private void setColumnSize(TableColumn column, int index, boolean setSize, boolean setPreferredSize)
    {
	/** find the PropertyDeclaration related to this column */
	TableModel model = this.getModel();
	if ( model instanceof PropertyBasedTableModel )
	{
	    PropertyBasedTableModel propertyModel = (PropertyBasedTableModel)model;
	    
	    if ( index >= 0 && index < this.getColumnModel().getColumnCount() )
	    {
		int convertedIndex = this.convertColumnIndexToModel(index);
		if ( convertedIndex >= 0 && convertedIndex < propertyModel.getPropertyDeclarationCount() )
		{
		    PropertyDeclaration decl = propertyModel.getPropertyDeclaration( convertedIndex );
		    if ( decl == null )
		    {
			if ( logger != null && logger.isDebugEnabled() )
			{
			    logger.debug("get a null PropertyDeclaration for index " + convertedIndex);
			}
		    }
		    else
		    {
			int prefSize = decl.getPreferredSize();

			if ( prefSize >= 0 )
			{
			    if ( setPreferredSize )
			    {
				column.setPreferredWidth(prefSize);
			    }
			    if ( setSize )
			    {
				column.setWidth(prefSize);
			    }
			    
//			    column.addPropertyChangeListener(new PropertyChangeListener()
//			    {
//				public void propertyChange(PropertyChangeEvent evt)
//				{
//				    if ( "width".equals(evt.getPropertyName()) )
//				    {
//					new Exception( ((TableColumn)evt.getSource()).getIdentifier() + " :: modif de width de " + evt.getOldValue() + " a " + evt.getNewValue()).printStackTrace();
//				    }
//				}
//			    });
			    
			    if ( logger != null && logger.isDebugEnabled() )
			    {
				logger.debug("size " + prefSize + " applied for property " + decl.getPropertyName());
			    }
			}
			else
			{
			    if ( logger != null && logger.isDebugEnabled() )
			    {
				logger.debug("size " + prefSize + " is negative and will not be applied");
			    }
			}
		    }
		}
		else
		{
		    if ( logger != null && logger.isDebugEnabled() )
		    {
			logger.debug("converted model index " + convertedIndex + " out of bounds [0, " + propertyModel.getPropertyDeclarationCount() + "]");
		    }
		}
	    }
	    else
	    {
		if ( logger != null && logger.isDebugEnabled() )
		{
		    logger.debug("index " + index + " out of bounds [0, " + this.getColumnModel().getColumnCount() + "]");
		}
	    }
	}
	else
	{
	    if ( logger != null && logger.isDebugEnabled() )
	    {
		logger.debug("unable to prepare the column with a non PropertyBasedTableModel model --> " + model);
	    }
	}
    }
    
    /* #########################################################################
     * ############# PropertyBasedTableModelListener implementation ############
     * ######################################################################### */
    
    /** called when a modification of a PropertyDeclaration is detected
     *	on the given PropertyBasedTableModel
     *	@param model a PropertyBasedTableModel
     *	@param descriptor a PropertyDeclaration
     *	@param event the event describing the modification
     */
    public void declarationChanged(PropertyBasedTableModel model,
				   PropertyDeclaration descriptor,
				   PropertyChangeEvent event)
    {
	/** change the size of the related column if displayed */
	if ( event != null && descriptor != null && model == this.getModel() )
	{
	    if ( PropertyDeclaration.PROPERTY_PREFERRED_SIZE.equals(event.getPropertyName()) )
	    {
		/** find the table colonne related to this property and change its width */
		
		int index = model.getColumnIndexOfProperty(descriptor.getPropertyName());
		
		index = this.convertColumnIndexToView(index);
		
		TableColumn column = this.getColumnModel().getColumn(index);
		
		column.setWidth( ((Integer)event.getNewValue()).intValue() );
		column.setPreferredWidth( ((Integer)event.getNewValue()).intValue() );
	    }
	}
    }
    
}
