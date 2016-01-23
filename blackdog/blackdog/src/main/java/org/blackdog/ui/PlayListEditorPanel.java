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
import org.blackdog.type.SongItem;
import org.blackdog.ui.filter.PlayListEditorFilter;
import org.blackdog.ui.filter.PlayListEditorFilter2;
import org.blackdog.ui.list.AlbumList;
import org.blackdog.ui.list.ArtistList;
import org.siberia.ui.swing.transfer.SibTypeTransferHandler;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PipelineEvent;
import org.jdesktop.swingx.decorator.PipelineListener;
import org.siberia.base.LangUtilities;
import org.siberia.type.SibType;
import org.siberia.ui.swing.table.SibListTablePanel;
import org.siberia.ui.swing.table.SibTypeListTable;
import org.siberia.ui.swing.table.TablePanel;
import org.siberia.ui.swing.table.model.SibTypeListTableModel;
import org.siberia.base.collection.SortedList;

/**
 *
 * @author  alexis
 */
public class PlayListEditorPanel extends javax.swing.JPanel
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
    
    /** table model listener */
    private        TableModelListener    modelListener            = this.createModelListener();
    
    /** artist selection set */
    private        Set<String>           artistSelection          = new HashSet<String>();
    
    /** album selection set */
    private        Set<String>           albumSelection           = new HashSet<String>();
    
    /** filter */
    private        PlayListEditorFilter2 playlistFilter2          = null;
    
    /* artist list */
    private        ArtistList            artistList               = null;
    
    /* album list */
    private        AlbumList             albumList                = null;
    
    /** indicate if setValueIsAdjusting has been called manually */
                   boolean               valueAdjustingManually   = false;
		   
    /** inhibit filter update if > 0 */
    private        int                   inhibitFilterUpdate      = 0;
    
    /** action listener on the remvoe filter button */
    private        ActionListener        removeFilterAction       = null;
    
    /** Creates new form PluginManagerPanel */
    public PlayListEditorPanel() {
	
        initComponents();
        
        ResourceBundle rb = ResourceBundle.getBundle(PlayListEditorPanel.class.getName());
	
	Dimension dim = new Dimension(this.removeFilterButton.getPreferredSize());
	dim.height = this.searchTextField.getPreferredSize().height;
	this.removeFilterButton.setSize(dim);
	this.removeFilterButton.setPreferredSize(dim);
	this.removeFilterButton.setToolTipText(rb.getString("removeFilter.tooltip"));
	
	this.removeFilterAction = new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
		removeFiltersAndSort();
	    }
	};
	
	this.removeFilterButton.addActionListener(this.removeFilterAction);
	
	this.artistList = new ArtistList(new FilterListModel(rb.getString("allArtistLabel.label"), rb.getString("unknownArtistLabel.label")));
	this.albumList  = new AlbumList(new FilterListModel(rb.getString("allAlbumLabel.label"), rb.getString("unknownAlbumLabel.label")));
	
	this.artistList.setVisibleRowCount(10);
	this.albumList.setVisibleRowCount(10);
	
	this.filterSplitpane.setLeftComponent(new JScrollPane(this.getArtistList()));
	this.filterSplitpane.setRightComponent(new JScrollPane(this.getAlbumList()));
	
	/* automatically select all items */
	this.getArtistList().selectAllItems();
	this.getAlbumList().selectAllItems();
        
        panel = new SibListTablePanel();
	
	panel.getTable().setDisplayRowNumber(true);
	panel.getTable().setMaximumDisplayedRows(-1);
	
	panel.setConfigurationRelativeFilePath("playlistEditor.properties");
        
        panel.getTable().addPropertyChangeListener("model", new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                /** remove the model listener on the old model and
                 *  add it to the new model
                 */
                Object o = evt.getOldValue();
                if ( o instanceof TableModel )
                {
                    ((TableModel)o).removeTableModelListener(modelListener);
                }
                
                o = evt.getNewValue();
                if ( o instanceof TableModel )
                {
                    ((TableModel)o).addTableModelListener(modelListener);
                }
                
                updateFilter(searchTextField.getText());
            }
        });
	panel.getTable().getModel().addTableModelListener(modelListener);
	
	ListSelectionListener selectionListener = new ListSelectionListener()
	{
	    public void valueChanged(ListSelectionEvent e)
	    {
		if ( ! e.getValueIsAdjusting() )
		{
		    if ( ! valueAdjustingManually )
		    {
			updateFilter(searchTextField.getText());
		    }
		}
	    }
	};
	
	this.getArtistList().getSelectionModel().addListSelectionListener(selectionListener);
	this.getAlbumList().getSelectionModel().addListSelectionListener(selectionListener);
        
        panel.getNavigationPanel().setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        
//        panel.ensureActionButtonsVisible(1);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = gbc.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0f;
        gbc.weighty = 1.0f;
        
        this.mainPanel.add(panel, gbc);
        
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

                updateFilter(input, getSelectedArtists(), getSelectedAlbums());
            }
        });
        
        this.getAlbumList().getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.getArtistList().getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	
	this.copyToLabel.setText( rb.getString("copyToLabel.label") );
	
	this.copyToLabel.setToolTipText( rb.getString("copyToLabel.tooltip") );
	this.copyToCombo.setToolTipText( rb.getString("copyToCombo.tooltip") );
	this.copyToButton.setToolTipText( rb.getString("copyToButton.tooltip") );
	
	this.copyToCombo.setRenderer(new DefaultListCellRenderer()
	{
	    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	    {
		Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if ( c instanceof JLabel )
		{
		    if ( value instanceof SibType )
		    {
			((JLabel)c).setText( ((SibType)value).getName() );
		    }
		    else if ( value instanceof String )
		    {
			((JLabel)c).setText( (String)value );
		    }
		    else if ( value != null )
		    {
			((JLabel)c).setText( value.toString() );
		    }
		}
		
		return c;
	    }
	});
	
        this.filterSplitpane.setDividerLocation(0.5d);
    }
    
    /** clear all filter items */
    void removeFiltersAndSort()
    {
	Runnable runnable = new Runnable()
	{
	    public void run()
	    {
		inhibitFilterUpdate ++;

		boolean launchFilterUpdate = false;

		try
		{
		    if ( searchTextField.getText().trim().length() > 0 ||
			 ! artistList.isOnlyAllSelected() ||
			 ! albumList.isOnlyAllSelected() )
		    {
			searchTextField.setText("");

			artistList.selectAllItems();
			albumList.selectAllItems();

			launchFilterUpdate = true;
		    }
		    
		    panel.getTable().resetSortOrder();

		    Component c = null;

		    /** force scrolls to go on the upper left corner */
		    c = filterSplitpane.getLeftComponent();
		    if ( c instanceof JScrollPane )
		    {
			((JScrollPane)c).getVerticalScrollBar().setValue(0);
			((JScrollPane)c).getHorizontalScrollBar().setValue(0);
		    }
		    c = filterSplitpane.getRightComponent();
		    if ( c instanceof JScrollPane )
		    {
			((JScrollPane)c).getVerticalScrollBar().setValue(0);
			((JScrollPane)c).getHorizontalScrollBar().setValue(0);
		    }
		    c = mainSplitPane.getBottomComponent();
		    if ( c instanceof JScrollPane )
		    {
			((JScrollPane)c).getVerticalScrollBar().setValue(0);
			((JScrollPane)c).getHorizontalScrollBar().setValue(0);
		    }
		    c = mainSplitPane.getTopComponent();
		    if ( c instanceof JScrollPane )
		    {
			((JScrollPane)c).getVerticalScrollBar().setValue(0);
			((JScrollPane)c).getHorizontalScrollBar().setValue(0);
		    }

		}
		finally
		{
		    inhibitFilterUpdate --;
		}

		if ( launchFilterUpdate )
		{
		    updateFilter();
		}
	    }
	};
	
	if ( SwingUtilities.isEventDispatchThread() )
	{
	    runnable.run();
	}
	else
	{
	    SwingUtilities.invokeLater(runnable);
	}
    }
    
    /** return the filter currently applied
     *	@return a PlayListEditorFilter2
     */
     PlayListEditorFilter2 getPlayListFilter2()
     {
	 return this.playlistFilter2;
     }
    
    /** return the combo that allow to copy selected items to another playlist
     *	@return a JComboBox
     */
    public JComboBox getCopyToCombo()
    {
	return this.copyToCombo;
    }
    
    /** return the button that allow to copy selected items to another playlist
     *	@return a JButton
     */
    public JButton getCopyToButton()
    {
	return this.copyToButton;
    }
    
    /** return the selected artists
     *  @return a Set of String representing the selected artists to consider in the filter
     */
    protected Set<String> getSelectedArtists()
    {
        this.artistSelection.clear();
        
        ListSelectionModel selectionModel = this.artistList.getSelectionModel();
        ListModel          model          = this.artistList.getModel();

        if ( model instanceof FilterListModel )
        {
            this.updateSelectedItems((FilterListModel)model, selectionModel, this.artistSelection);
        }
        
        return this.artistSelection;
    }
    
    /** return the selected albums
     *  @return a Set of String representing the selected albums to consider in the filter
     */
    protected Set<String> getSelectedAlbums()
    {
        this.albumSelection.clear();
        
        ListSelectionModel selectionModel = this.albumList.getSelectionModel();
        ListModel          model          = this.albumList.getModel();

        if ( model instanceof FilterListModel )
        {
            this.updateSelectedItems((FilterListModel)model, selectionModel, this.albumSelection);
        }
        
        return this.albumSelection;
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
    
    /** create the model listener
     *  @return a model listener
     */
    private TableModelListener createModelListener()
    {
        return new TableModelListener()
        {
            public void tableChanged(TableModelEvent e)
            {
		/** if the event represents item deletion, then force the filter to be launched
		 *  to avoid showing albums or authors that are no longer reasons to be shown
		 */
		if ( e.getType() == e.DELETE )
		{
		    updateFilter();
		}
            }
        };
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
    
    /** indicate if the album/artist lists have to be visible
     *  @param visible a boolean
     */
    public void setArtistAlbumFilterVisible(boolean visible)
    {
        this.mainSplitPane.setDividerLocation( (visible ? 100 : 0) );
    }
    
    /** return the ArtistList that represents the artists
     *  @return a ArtistList
     */
    ArtistList getArtistList()
    {
        return this.artistList;
    }
    
    /** return the AlbumList that represents the albums
     *  @return a AlbumList
     */
    AlbumList getAlbumList()
    {
        return this.albumList;
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
        this.updateFilter(input, this.getSelectedArtists(), this.getSelectedAlbums());
    }
    
    /** method that try to submit a new runnable to apply the filter in its current state
     *  @param input the input entered by user
     *  @param artists the set of artists to filter
     *  @param albums the set of albums to filter
     */
    private void updateFilter(String input, Set<String> artists, Set<String> albums)
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
	    
	    this.playlistFilter2 = new PlayListEditorFilter2(this.playlistFilter2,
							   this.panel.getTable().getModel(), 
							   this.getArtistList(),
							   this.getAlbumList());

	    this.playlistFilter2.setInput(input);

	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("after building new filter");
		logger.debug("before building new filter runnable");
	    }
	    Runnable filterRunnable = new PlayListFilterRunnable(this, this.playlistFilter2);
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
        mainSplitPane = new javax.swing.JSplitPane();
        mainPanel = new javax.swing.JPanel();
        filterSplitpane = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        searchLabel = new javax.swing.JLabel();
        searchTextField = new javax.swing.JTextField();
        copyToCombo = new javax.swing.JComboBox();
        copyToLabel = new javax.swing.JLabel();
        copyToButton = new javax.swing.JButton();
        removeFilterButton = new javax.swing.JButton();

        mainSplitPane.setDividerLocation(100);
        mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setContinuousLayout(true);
        mainSplitPane.setOneTouchExpandable(true);
        mainPanel.setLayout(new java.awt.GridBagLayout());

        mainPanel.setAlignmentX(0.0F);
        mainSplitPane.setBottomComponent(mainPanel);

        filterSplitpane.setDividerLocation(400);
        filterSplitpane.setResizeWeight(0.5);
        filterSplitpane.setContinuousLayout(true);
        filterSplitpane.setOneTouchExpandable(true);
        filterSplitpane.setLeftComponent(jScrollPane1);

        filterSplitpane.setRightComponent(jScrollPane2);

        mainSplitPane.setLeftComponent(filterSplitpane);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 0, 0));
        searchLabel.setText("search");

        copyToCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        copyToLabel.setText("copy to");

        copyToButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/blackdog/rc/img/addToPlaylist.png")));

        removeFilterButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/blackdog/rc/img/removeFilter.png")));
        removeFilterButton.setAlignmentY(0.0F);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(searchLabel)
                .add(4, 4, 4)
                .add(searchTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeFilterButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 90, Short.MAX_VALUE)
                .add(copyToLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(copyToCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(copyToButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(copyToButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(searchLabel)
                .add(searchTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(copyToCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(copyToLabel)
                .add(removeFilterButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(mainSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton copyToButton;
    protected javax.swing.JComboBox copyToCombo;
    protected javax.swing.JLabel copyToLabel;
    protected javax.swing.JSplitPane filterSplitpane;
    protected javax.swing.JPanel jPanel1;
    protected javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JScrollPane jScrollPane2;
    protected javax.swing.JPanel mainPanel;
    protected javax.swing.JSplitPane mainSplitPane;
    protected javax.swing.JButton removeFilterButton;
    protected javax.swing.JLabel searchLabel;
    protected javax.swing.JTextField searchTextField;
    // End of variables declaration//GEN-END:variables
    
    
}
