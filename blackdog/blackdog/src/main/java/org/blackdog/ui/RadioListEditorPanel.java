/*
 * blackdog : audio player / manager
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import org.apache.log4j.Logger;
import org.blackdog.BlackdogPlugin;
import org.blackdog.type.SongItem;
import org.blackdog.ui.filter.PlayListEditorFilter;
import org.blackdog.ui.filter.PlayListEditorFilter2;
import org.blackdog.ui.filter.RadioListEditorFilter;
import org.blackdog.ui.list.AlbumList;
import org.blackdog.ui.list.ArtistList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PipelineEvent;
import org.jdesktop.swingx.decorator.PipelineListener;
import org.siberia.ResourceLoader;
import org.siberia.base.LangUtilities;
import org.siberia.exception.ResourceException;
import org.siberia.type.SibList;
import org.siberia.type.SibType;
import org.siberia.ui.action.impl.AddingTypeAction;
import org.siberia.ui.swing.table.SibListTablePanel;
import org.siberia.ui.swing.table.SibTypeListTable;
import org.siberia.ui.swing.table.TablePanel;
import org.siberia.ui.swing.table.model.IntrospectionSibTypeListTableModel;
import org.siberia.ui.swing.table.model.SibTypeListTableModel;
import org.siberia.base.collection.SortedList;

/**
 *
 * @author  alexis
 */
public class RadioListEditorPanel extends javax.swing.JPanel
{   
    /** logger */
    private        Logger                logger                   = Logger.getLogger(PlayListEditorPanel.class.getName());
    
    private        SibListTablePanel     panel                    = null;
    
    /** executor for filter */
    private        ExecutorService       service                  = Executors.newSingleThreadExecutor();
    
    /**
     * current future managed by the executor service
     */
    private        Future                currentFilterFuture      = null;
    
    /** filter */
    private        RadioListEditorFilter radiolistFilter          = null;
		   
    /** inhibit filter update if > 0 */
    private        int                   inhibitFilterUpdate      = 0;
    
    /** Creates new form RadioListEditorPanel */
    public RadioListEditorPanel()
    {	
        initComponents();
        
        ResourceBundle rb = ResourceBundle.getBundle(RadioListEditorPanel.class.getName());
        
        panel = new SibListTablePanel();
	
	panel.getTable().setDisplayRowNumber(true);
	panel.getTable().setMaximumDisplayedRows(-1);
	
	panel.setConfigurationRelativeFilePath("radiolistEditor.properties");
        
        panel.getTable().addPropertyChangeListener("model", new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateFilter(searchTextField.getText());
            }
        });
	
	ListSelectionListener selectionListener = new ListSelectionListener()
	{
	    public void valueChanged(ListSelectionEvent e)
	    {
		if ( ! e.getValueIsAdjusting() )
		{
		    updateFilter(searchTextField.getText());
		}
	    }
	};
	
	this.panelTable.add(panel);
        
        panel.getNavigationPanel().setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        
        panel.ensureActionButtonsVisible(1);
	JButton button = panel.getActionButton(1);
	
	button.addActionListener(new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
		if ( panel != null && panel.getTable() != null && panel.getTable().getModel() instanceof IntrospectionSibTypeListTableModel )
		{
		    SibList list = ((IntrospectionSibTypeListTableModel)panel.getTable().getModel()).getList();
		    
		    if ( list != null )
		    {
			/** create a new create action and execute it */
			AddingTypeAction action = new AddingTypeAction();
			action.setTypes( list );
			
			action.actionPerformed(e);
		    }
		}
	    }
	});
	
	try
	{
	    Icon icon = ResourceLoader.getInstance().getIconNamed(BlackdogPlugin.PLUGIN_ID + ";1::img/new_radio_item.png");
	    if ( icon != null )
	    {
		button.setIcon(icon);
		
		Dimension newDim = new Dimension(icon.getIconWidth() + 4, icon.getIconHeight() + 4);
		Dimension dim = button.getPreferredSize();
		
		/** compute the best dimension for the creation button */
		if ( dim != null )
		{
		    if ( newDim.width < dim.width )
		    {
			newDim.width = dim.width;
		    }
		    if ( newDim.height < dim.height )
		    {
			newDim.height = dim.height;
		    }
		}
		
		button.setPreferredSize(newDim);
		button.setMaximumSize(newDim);
	    }
	}
	catch (ResourceException ex)
	{
	    ex.printStackTrace();
	}
        
        this.searchLabel.setText(rb.getString("searchLabel.label"));
        
        final JXTable table = panel.getTable();
        
        this.searchTextField.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent e)
            {   /* do nothing */ }
            
            public void insertUpdate(DocumentEvent e)
            {   this.updateFilterCriterion(e); }
            
            public void removeUpdate(DocumentEvent e)
            {   this.updateFilterCriterion(e); }
            
            /** update table filter
             *  this methods could be called only in EDT since it is called on insertUpdate and on
             *  removeUpdate which are called on Event Dispatch Thread
             *  @param e a DocumentEvent
             */
            private void updateFilterCriterion(DocumentEvent e)
            {
                /** update filter properties */
                String input = null;
                try
                {   input = e.getDocument().getText(e.getDocument().getStartPosition().getOffset(), e.getDocument().getLength()); }
                catch(BadLocationException ex)
                {   ex.printStackTrace(); }

                updateFilter(input);
            }
        });
    }
    
    /** return a set of Strings representing the selected item on the given list selection model and model
     *  @param listModel a FilterListModel
     *  @param selectionModel a ListSelectionModel
     *  @param selection a set of strings to update
     */
    private void updateSelectedItems(FilterListModel listModel, ListSelectionModel selectionModel, Set<String> selection)
    {
        selection.clear();
        
        if ( listModel != null && selectionModel != null )
        {
            if ( ! selectionModel.isSelectionEmpty() )
            {
                int minIndex = selectionModel.getMinSelectionIndex();
                int maxIndex = selectionModel.getMaxSelectionIndex();
                
                for(int i = minIndex; i <= maxIndex; i++)
                {
                    if ( selectionModel.isSelectedIndex(i) )
                    {
                        selection.add( listModel.getElementAt(i) );
                    }
                }
            }
        }
    }
    
    /** return the SibListTablePanel
     *  @return a SibListTablePanel
     */
    public SibListTablePanel getTablePanel()
    {   return this.panel; }
    
    /** indicate if the search capabilities have to be enabled
     *  @param enabled true to set search enabled
     */
    public void setSearchEnabled(boolean enabled)
    {
        this.searchLabel.setVisible(enabled);
        this.searchTextField.setVisible(enabled);
    }
    
    /** return true if the search capabilities are enabled
     *  @return true if search is enabled
     */
    public boolean isSearchEnabled()
    {
        return this.searchTextField.isVisible();
    }
    
    /** method that try to submit a new runnable to apply the filter in its current state */
    void updateFilter()
    {
	this.updateFilter(this.searchTextField.getText());
    }
    
    /** method that try to submit a new runnable to apply the filter in its current state
     *  @param input the input entered by user
     */
    private void updateFilter(String input)
    {
	if ( this.inhibitFilterUpdate == 0 )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("updating filter");
		logger.debug("total thread count : " + Thread.activeCount());
	    }

	    /** if there is a future that is running, then stop it */
	    if ( currentFilterFuture != null && ! currentFilterFuture.isDone() )
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("trying to stop current filter runnable");
		}
		boolean result = currentFilterFuture.cancel(true);
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("canceling result of current filter runnable ? " + result);
		}
	    }

	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("before building new filter");
	    }
	    
	    this.radiolistFilter = new RadioListEditorFilter(this.panel.getTable().getModel());

	    this.radiolistFilter.setInput(input);

	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("after building new filter");
		logger.debug("before building new filter runnable");
	    }
	    Runnable filterRunnable = new RadioListFilterRunnable(this, this.radiolistFilter);
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("after building new filter runnable");
	    }

	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("before submitting filter runnable");
	    }
	    currentFilterFuture = service.submit(filterRunnable);
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("after submitting filter runnable");
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
        jPanel1 = new javax.swing.JPanel();
        searchLabel = new javax.swing.JLabel();
        searchTextField = new javax.swing.JTextField();
        panelTable = new javax.swing.JPanel();

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 0));
        searchLabel.setText("search");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(searchLabel)
                .add(4, 4, 4)
                .add(searchTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(370, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(searchLabel)
                .add(searchTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        panelTable.setLayout(new javax.swing.BoxLayout(panelTable, javax.swing.BoxLayout.LINE_AXIS));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(panelTable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelTable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JPanel jPanel1;
    protected javax.swing.JPanel panelTable;
    protected javax.swing.JLabel searchLabel;
    protected javax.swing.JTextField searchTextField;
    // End of variables declaration//GEN-END:variables
    
    
}
