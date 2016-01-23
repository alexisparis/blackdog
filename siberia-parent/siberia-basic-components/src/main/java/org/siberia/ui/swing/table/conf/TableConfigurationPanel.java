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
package org.siberia.ui.swing.table.conf;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.siberia.ui.swing.table.*;
import org.siberia.ui.swing.table.model.PropertyBasedTableModel;
import org.siberia.ui.swing.table.model.PropertyDeclaration;

/**
 *
 * @author  alexis
 */
public class TableConfigurationPanel extends javax.swing.JPanel implements ItemListener,
									   ChangeListener,
									   PropertyChangeListener
{
    /** property modified */
    public static final String PROPERTY_MODIFIED = "modified";
    
    /** TablePanel */
    private TablePanel                          tablePanel         = null;
    
    /** modified */
    private boolean                             modified           = false;
    
    /** label mouse Listener */
    private MouseListener                       labelMouseListener = null;
    
    /** Creates new form TableConfigurationPanel
     *	@param tablePanel a TablePanel
     */
    public TableConfigurationPanel(TablePanel tablePanel)
    {
	initComponents();
	
	this.columnsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	
	this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
	
	this.tablePanel = tablePanel;
	
	/* internationalization */
	ResourceBundle rb = ResourceBundle.getBundle(TableConfigurationPanel.class.getName());
	
	this.labelDspRowNumber.setText(rb.getString("dspRowNumberLabel"));
	this.labelPageSize.setText(rb.getString("activatePaginationLabel"));
	this.labelHorizontalScroll.setText(rb.getString("horizontalScrollLabel"));
	this.labelHorizontalLines.setText(rb.getString("horizontalLinesLabel"));
	this.labelVerticalLines.setText(rb.getString("verticalLinesLabel"));
	
	this.columnsScrollpane.setBorder(BorderFactory.createTitledBorder(rb.getString("columnsBorderTitle")));
	
	/** configure components */
	this.spinnerPageSize.setModel(new SpinnerNumberModel(50, 1, Integer.MAX_VALUE, 1));
	
	/** complete columnsPanel with the columns available */
	// TODO
	
	/** add listeners */
	this.checkDspRowNumber.addItemListener(this);
	this.checkPageSize.addItemListener(this);
	this.checkHorizontalScroll.addItemListener(this);
	this.checkHorizontalLines.addItemListener(this);
	this.checkVerticalLines.addItemListener(this);
	
	this.labelMouseListener = new MouseAdapter()
	{
	    public void mousePressed(MouseEvent e)
	    {
		if ( e.getSource() == labelDspRowNumber )
		{
		    checkDspRowNumber.setSelected(! checkDspRowNumber.isSelected());
		}
		else if ( e.getSource() == labelHorizontalScroll )
		{
		    checkHorizontalScroll.setSelected(! checkHorizontalScroll.isSelected());
		}
		else if ( e.getSource() == labelPageSize )
		{
		    checkPageSize.setSelected(! checkPageSize.isSelected());
		}
	    }
	};
	
	this.labelDspRowNumber.addMouseListener(this.labelMouseListener);
	this.labelHorizontalScroll.addMouseListener(this.labelMouseListener);
	this.labelPageSize.addMouseListener(this.labelMouseListener);
	
	this.spinnerPageSize.addChangeListener(this);
	
	this.checkDspRowNumber.setVisible( tablePanel.getTable() instanceof NumberedPageableTable );
	
	ColumnsTableModel tableModel = new ColumnsTableModel(rb.getString("propertyNameColumnName"),
							     rb.getString("visibilityColumnName"),
							     rb.getString("sizeColumnName"));
	tableModel.addPropertyChangeListener(ColumnsTableModel.PROPERTY_MODIFIED, this);
	
	this.columnsTable.setModel(tableModel);
	
	TableColumn firstColumn = this.columnsTable.getColumnModel().getColumn(1);
	firstColumn.setPreferredWidth(30);
	firstColumn.setMinWidth(30);
	firstColumn.setWidth(30);
    }
    
    /** return true if the popup menu feel modified
     *	@return true if the popup menu feel modified
     */
    public boolean isModified()
    {
	return this.modified;
    }
    
    /** reset panel modification state
     *	@param modified true if the component has to feel modified
     */
    public void setModified(boolean modified)
    {
	if ( this.isModified() != modified )
	{
	    this.modified = modified;
	    
	    this.firePropertyChange(PROPERTY_MODIFIED, ! this.modified, this.modified);
	}
	
	if ( ! modified )
	{
	    TableModel model = this.columnsTable.getModel();
	    if ( model instanceof ColumnsTableModel )
	    {
		((ColumnsTableModel)model).setModified(modified);
	    }
	}
    }
    
    /** force the panel to refresh the state of its components to reflect the state of the related TablePanel */
    public void update()
    {
	boolean pageable = true;
	if ( this.tablePanel.getTable().arePageableFunctionnalitiesActivated() )
	{
	    this.spinnerPageSize.setValue(this.tablePanel.getTable().getMaximumDisplayedRows());
	    pageable = true;
	}
	else
	{
	    pageable = false;
	}
	
	this.checkPageSize.setSelected(pageable);
	/* security */
	this.spinnerPageSize.setEnabled(pageable);
	
	if ( this.tablePanel.getTable() instanceof NumberedPageableTable )
	{
	    this.labelDspRowNumber.setVisible(true);
	    this.checkDspRowNumber.setVisible(true);
	    
	    boolean rowNumberDisplayed = ((NumberedPageableTable)this.tablePanel.getTable()).isRowNumberDisplayed();
	    this.checkDspRowNumber.setSelected(rowNumberDisplayed);
	}
	else
	{
	    this.labelDspRowNumber.setVisible(false);
	    this.checkDspRowNumber.setVisible(false);
	}
	
	this.checkHorizontalScroll.setSelected(this.tablePanel.getTable().isHorizontalScrollEnabled());
	
	
	this.checkHorizontalLines.setSelected(this.tablePanel.getTable().getShowHorizontalLines());
	this.checkVerticalLines.setSelected(this.tablePanel.getTable().getShowVerticalLines());
	
	PropertyBasedTableModel propertyBasedModel = null;
	boolean columnsManagment = false;
	if ( this.tablePanel != null )
	{
	    if ( this.tablePanel.getTable() != null )
	    {
		if ( this.tablePanel.getTable().getModel() instanceof PropertyBasedTableModel )
		{
		    propertyBasedModel = (PropertyBasedTableModel)this.tablePanel.getTable().getModel();
		}
	    }
	}
	this.columnsScrollpane.setVisible( propertyBasedModel != null );
	
	((ColumnsTableModel)this.columnsTable.getModel()).setPropertyBasedModel(propertyBasedModel);
	
	Dimension dim = new Dimension(this.columnsTable.getPreferredScrollableViewportSize());
	
	/** modify the height */
	int rowVisible = (Math.min(10, this.columnsTable.getRowCount()));
	dim.height = this.columnsTable.getRowHeight() * rowVisible;
	this.columnsTable.setPreferredScrollableViewportSize(dim);
	this.columnsTable.revalidate();
	
//	this.revalidate();
    }
    
    /** update the TableConfiguration
     *	before saving it
     */
    public void prepareConfigurationSave()
    {
	
    }
    
    /** method called to free panel
     *	it will not be used anymore
     */
    public void release()
    {
	this.tablePanel = null;
	
	this.checkDspRowNumber.removeItemListener(this);
	this.checkPageSize.removeItemListener(this);
	this.checkHorizontalScroll.removeItemListener(this);
	this.checkHorizontalLines.removeItemListener(this);
	this.checkVerticalLines.removeItemListener(this);
	
	this.labelDspRowNumber.removeMouseListener(this.labelMouseListener);
	this.labelHorizontalScroll.removeMouseListener(this.labelMouseListener);
	this.labelPageSize.removeMouseListener(this.labelMouseListener);
	this.labelMouseListener = null;
	
	this.spinnerPageSize.removeChangeListener(this);
	
	((ColumnsTableModel)this.columnsTable.getModel()).removePropertyChangeListener(ColumnsTableModel.PROPERTY_MODIFIED, this);
	
	((ColumnsTableModel)this.columnsTable.getModel()).setPropertyBasedModel(null);
	((ColumnsTableModel)this.columnsTable.getModel()).setModified(false);
    }
    
    /* #########################################################################
     * ####################### ItemListener implementation #####################
     * ######################################################################### */
    
    public void itemStateChanged(ItemEvent e)
    {
	if ( e.getSource() == this.checkDspRowNumber && this.tablePanel.getTable() instanceof NumberedPageableTable )
	{
	    ((NumberedPageableTable)this.tablePanel.getTable()).setDisplayRowNumber(this.checkDspRowNumber.isSelected());
	    
	    if ( ! this.isModified() )
	    {
		this.setModified(true);
	    }
	}
	else if( e.getSource() == this.checkPageSize )
	{
	    if ( this.checkPageSize.isSelected() )
	    {
		/* enabled spinner and update table panel state */
		this.spinnerPageSize.setEnabled(true);
		
		this.tablePanel.setMaximumDisplayedRows((Integer)this.spinnerPageSize.getValue());
		
		if ( ! this.isModified() )
		{
		    this.setModified(true);
		}
	    }
	    else
	    {
		/* enabled spinner and update table panel state */
		this.spinnerPageSize.setEnabled(false);
		
		this.tablePanel.setMaximumDisplayedRows(-1);
		
		if ( ! this.isModified() )
		{
		    this.setModified(true);
		}
	    }
	}
	else if( e.getSource() == this.checkHorizontalScroll )
	{
	    this.tablePanel.getTable().setHorizontalScrollEnabled(this.checkHorizontalScroll.isSelected());
	    
	    if ( ! this.isModified() )
	    {
		this.setModified(true);
	    }
	}
	else if ( e.getSource() == this.checkHorizontalLines )
	{
	    this.tablePanel.getTable().setShowHorizontalLines(this.checkHorizontalLines.isSelected());
	    
	    if ( ! this.isModified() )
	    {
		this.setModified(true);
	    }
	}
	else if ( e.getSource() == this.checkVerticalLines )
	{
	    this.tablePanel.getTable().setShowVerticalLines(this.checkVerticalLines.isSelected());
	    
	    if ( ! this.isModified() )
	    {
		this.setModified(true);
	    }
	}
    }
    
    /* #########################################################################
     * ###################### ChangeListener implementation ####################
     * ######################################################################### */
    
    public void stateChanged(ChangeEvent e)
    {
	if ( e.getSource() == this.spinnerPageSize && this.checkPageSize.isSelected() )
	{
	    Object value = this.spinnerPageSize.getValue();
	    if ( value instanceof Integer )
	    {
		if ( ((Integer)value) > 0 && ((Integer)value) <= Integer.MAX_VALUE )
		{
		    this.tablePanel.setMaximumDisplayedRows((Integer)value);

		    if ( ! this.isModified() )
		    {
			this.setModified(true);
		    }
		}
	    }
	}
    }
    
    /* #########################################################################
     * ###################### ChangeListener implementation ####################
     * ######################################################################### */
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */

    public void propertyChange(PropertyChangeEvent evt)
    {
	if ( evt.getSource() == this.columnsTable.getModel() && evt.getPropertyName().equals(ColumnsTableModel.PROPERTY_MODIFIED) )
	{
	    if ( evt.getNewValue() instanceof Boolean && ((Boolean)evt.getNewValue()).booleanValue() )
	    {
		if ( ! this.isModified() )
		{
		    this.setModified(true);
		}
	    }
	}
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        columnsScrollpane = new javax.swing.JScrollPane();
        columnsTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        labelVerticalLines = new javax.swing.JLabel();
        labelHorizontalLines = new javax.swing.JLabel();
        labelHorizontalScroll = new javax.swing.JLabel();
        labelPageSize = new javax.swing.JLabel();
        labelDspRowNumber = new javax.swing.JLabel();
        checkPageSize = new javax.swing.JCheckBox();
        spinnerPageSize = new javax.swing.JSpinner();
        checkDspRowNumber = new javax.swing.JCheckBox();
        checkHorizontalLines = new javax.swing.JCheckBox();
        checkHorizontalScroll = new javax.swing.JCheckBox();
        checkVerticalLines = new javax.swing.JCheckBox();

        columnsScrollpane.setBorder(javax.swing.BorderFactory.createTitledBorder("columns"));
        columnsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        columnsScrollpane.setViewportView(columnsTable);

        labelVerticalLines.setText("vertical lines");

        labelHorizontalLines.setText("horizontal lines");

        labelHorizontalScroll.setText("horizontal scroll");

        labelPageSize.setText("activate pagination");

        labelDspRowNumber.setText("display row number");

        checkPageSize.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkPageSize.setMargin(new java.awt.Insets(0, 0, 0, 0));

        checkDspRowNumber.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkDspRowNumber.setMargin(new java.awt.Insets(0, 0, 0, 0));

        checkHorizontalLines.setText("jCheckBox1");
        checkHorizontalLines.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkHorizontalLines.setMargin(new java.awt.Insets(0, 0, 0, 0));

        checkHorizontalScroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkHorizontalScroll.setMargin(new java.awt.Insets(0, 0, 0, 0));

        checkVerticalLines.setText("jCheckBox1");
        checkVerticalLines.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkVerticalLines.setMargin(new java.awt.Insets(0, 0, 0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(labelVerticalLines, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelHorizontalLines, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelHorizontalScroll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelPageSize, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelDspRowNumber, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(checkPageSize)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerPageSize, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(checkDspRowNumber)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(checkHorizontalLines, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                        .addComponent(checkHorizontalScroll, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(checkVerticalLines, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spinnerPageSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelPageSize)
                    .addComponent(checkPageSize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelDspRowNumber)
                    .addComponent(checkDspRowNumber))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelHorizontalScroll)
                    .addComponent(checkHorizontalScroll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelHorizontalLines)
                    .addComponent(checkHorizontalLines))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelVerticalLines)
                    .addComponent(checkVerticalLines)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(columnsScrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(columnsScrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkDspRowNumber;
    private javax.swing.JCheckBox checkHorizontalLines;
    private javax.swing.JCheckBox checkHorizontalScroll;
    private javax.swing.JCheckBox checkPageSize;
    private javax.swing.JCheckBox checkVerticalLines;
    private javax.swing.JScrollPane columnsScrollpane;
    private javax.swing.JTable columnsTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelDspRowNumber;
    private javax.swing.JLabel labelHorizontalLines;
    private javax.swing.JLabel labelHorizontalScroll;
    private javax.swing.JLabel labelPageSize;
    private javax.swing.JLabel labelVerticalLines;
    private javax.swing.JSpinner spinnerPageSize;
    // End of variables declaration//GEN-END:variables
    
}
