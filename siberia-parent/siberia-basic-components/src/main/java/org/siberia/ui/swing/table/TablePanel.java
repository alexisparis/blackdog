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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.SiberiaBasicComponentsPlugin;
import org.siberia.exception.ResourceException;
import org.siberia.ui.swing.TextFieldVerifier;
import org.siberia.ui.swing.table.conf.TableConfiguration;
import org.siberia.ui.swing.table.conf.TableConfigurationManager;
import org.siberia.ui.swing.table.conf.TableConfigurationPopupMenu;
import org.siberia.ui.swing.table.model.IntrospectionSibTypeListTableModel;
import org.siberia.ui.swing.table.model.PropertyBasedTableModel;
import org.siberia.ui.swing.table.model.PropertyDeclaration;

/**
 *
 * Panel that contains a Table and that allow pageable functionnalities
 *
 * @author  alexis
 */
public class TablePanel<T extends PageableTable> extends javax.swing.JPanel implements PropertyChangeListener,
										       ActionListener,
										       PopupMenuListener
{   
    /** property configuration relative file path */
    public static final String PROPERTY_CONF_RELATIVE_FILEPATH   = "configurationRelativeFilePath";
    
    /** logger */
    private static Logger               logger                   = Logger.getLogger(TablePanel.class);
    
    /** table */
    private T                           table                    = null;
    
    /** array of buttons */
    private JButton[]                   actionButtons            = null;
    
    /** relative file path representing the configuration file to use */
    private String                      confRelativeFilePath     = null;
    
    /** TablecolumnModelListener to manage preferred size */
    private TableColumnModelListener    columnModelListener      = null;
    
    /** TableConfigurationPopupMenu */
    private TableConfigurationPopupMenu confPopupMenu            = null;
    
    /** > 0 to indicate that a column event (moved, added, removed, etc..) event should not provoke
     *	the saving of the table configuration
     */
    private int                         inhibitConfigurationSave = 0;
    
    /** Creates new form TablePanel */
    public TablePanel() {
        initComponents();
        
        this.actionButtons = new JButton[8];
        
        this.actionButtons[0] = this.button1;
        this.actionButtons[1] = this.button2;
        this.actionButtons[2] = this.button3;
        this.actionButtons[3] = this.button4;
        this.actionButtons[4] = this.button5;
        this.actionButtons[5] = this.button6;
        this.actionButtons[6] = this.button7;
        this.actionButtons[7] = this.button8;
        
        this.setActionButtonsVisible(false);
        
        this.table = this.createTable();
	
	this.confPopupMenu = new TableConfigurationPopupMenu(this);
	this.table.getTableHeader().setComponentPopupMenu(this.confPopupMenu);
	
	this.confPopupMenu.addPopupMenuListener(this);
	
        ResourceBundle rb = ResourceBundle.getBundle(TablePanel.class.getName());
        
        this.table.addPropertyChangeListener("model", this);
        this.table.addPropertyChangeListener(NumberedPageableTable.PROPERTY_DISPLAY_ROW_NUMBER, this);
        this.scrollPane.setViewportView(this.table);
        
        this.nextPageButton.addActionListener(this);
        this.previousPageButton.addActionListener(this);
        
        /** if the header changes, we have to update the preferred columns size */
	this.columnModelListener = new TableColumnModelListener()
        {
            public void columnAdded(TableColumnModelEvent e)
            {   
		if ( confPopupMenu != null && ! confPopupMenu.isVisible() )
		{
		    if ( inhibitConfigurationSave == 0 )
		    {
			/** update configuration */
//			saveConfiguration();
		    }
		}
	    }
            
            public void columnMarginChanged(ChangeEvent e)
            {   }
            
            public void columnMoved(TableColumnModelEvent e)
            {   
		if ( confPopupMenu != null && ! confPopupMenu.isVisible() )
		{
		    if ( inhibitConfigurationSave == 0 )
		    {
			/** update configuration */
			saveConfiguration();
		    }
		}
	    }
            
            public void columnRemoved(TableColumnModelEvent e)
            {   
		if ( confPopupMenu != null && ! confPopupMenu.isVisible() )
		{
		    if ( inhibitConfigurationSave == 0 )
		    {
			/** update configuration */
//			saveConfiguration();
		    }
		}
	    }
            
            public void columnSelectionChanged(ListSelectionEvent e)
            {   }
        };
        this.table.getColumnModel().addColumnModelListener(this.columnModelListener);
	
        try
        {   this.previousPageButton.setIcon(ResourceLoader.getInstance().getIconNamed(SiberiaBasicComponentsPlugin.PLUGIN_ID + ";1::img/previous.png"));
            this.previousPageButton.setText(null);
        }
        catch (ResourceException ex)
        {   ex.printStackTrace(); }
        
        try
        {   this.nextPageButton.setIcon(ResourceLoader.getInstance().getIconNamed(SiberiaBasicComponentsPlugin.PLUGIN_ID + ";1::img/next.png"));
            this.nextPageButton.setText(null);
        }
        catch (ResourceException ex)
        {   ex.printStackTrace(); }
        
        this.previousPageButton.setToolTipText(rb.getString("previousButtonTooltip"));
        this.nextPageButton.setToolTipText(rb.getString("nextButtonTooltip"));
        
        this.getTable().addPropertyChangeListener(PageableTable.PROPERTY_CURRENT_PAGE_NUMBER, this);
        this.getTable().addPropertyChangeListener(PageableTable.PROPERTY_PAGE_COUNT, this);
        this.getTable().addPropertyChangeListener(PageableTable.PROPERTY_MAX_DISPLAYED_ROWS, this);
//        this.getTable().addPropertyChangeListener(PageableTable.PROPERTY_DISPLAY_ROW_NUMBER, this);
        
        this.pageTextField.setColdVerifier(new TextFieldVerifier()
        {
            int lastParsedValue = -1;
            
            public boolean applyValue(Component input, Object value)
            {   boolean applied = false;
                if ( this.lastParsedValue > 0 )
                {   
//		    System.out.println("current page is : " + (this.lastParsedValue - 1));
		    setCurrentPage(this.lastParsedValue - 1);
                    applied = true;
                }
                
                return applied;
            }
            
            public boolean verify(JComponent input)
            {   boolean accept = false;
                
                this.lastParsedValue = -1;
                
                String value = ((JTextComponent)input).getText();
                if ( value != null )
                {   try
                    {   int v = Integer.parseInt( value );
                        
                        if ( v > 0 && v <= getTable().getPageCount() )
                        {   accept = true;
                            
                            this.lastParsedValue = v;
                        }
                    }
                    catch(NumberFormatException e)
                    {   }
                }
		
//		System.out.println("verify returns " + accept);
                
                return accept;
            }
        });
	
	
        /** i18n */
        
    }
    
    /** refresh TablePanel states according to the given configuration
     *	@param configuration a TableConfiguration
     */
    public void loadConfiguration(TableConfiguration configuration)
    {
	if ( configuration != null )
	{
	    this.setMaximumDisplayedRows(configuration.getPageSize());
	    
	    if (this.getTable() != null )
	    {
		if ( this.getTable() instanceof NumberedPageableTable )
		{
		    ((NumberedPageableTable)this.getTable()).setDisplayRowNumber(configuration.isRowNumberDisplayed());
		}
		
		this.getTable().setHorizontalScrollEnabled(configuration.isHorizontalScrollEnabled());
		
		this.getTable().setShowHorizontalLines(configuration.areHorizontalLinesShown());
		this.getTable().setShowVerticalLines(configuration.areVerticalLinesShown());
		
		TableModel model = this.getTable().getModel();
		if ( model instanceof PropertyBasedTableModel )
		{
		    PropertyBasedTableModel propertyModel = (PropertyBasedTableModel)model;
		    
		    propertyModel.enableProperties(configuration.getDisplayedColumns());
		    
		    for(int i = 0; i < propertyModel.getPropertyDeclarationCount(); i++)
		    {
			PropertyDeclaration declaration = propertyModel.getPropertyDeclaration(i);
			
			if ( declaration != null )
			{
			    int prefSize = configuration.getColumnPreferredSizeForProperty(declaration.getPropertyName());
			    
			    if ( prefSize >= 0 )
			    {
				declaration.setPreferredSize(prefSize);
			    }
			}
		    }
		    
		    /** force the order of the columns as described by configuration.getDisplayedColumns() */
		    TableColumnModel columnModel = this.getTable().getColumnModel();
		    
		    /** avoid to save configuration if a column is moved */
		    this.inhibitConfigurationSave ++;
		    
		    if ( configuration.getDisplayedColumns() != null )
		    {
			for(int i = 0; i < configuration.getDisplayedColumns().length; i++)
			{
			    String currentProperty = configuration.getDisplayedColumns()[i];
			    
			    if ( currentProperty != null )
			    {
				int modelIndex = propertyModel.getColumnIndexOfProperty(currentProperty);
				
				if ( modelIndex >= 0 && modelIndex < columnModel.getColumnCount() )
				{
				    int convertedIndex = this.getTable().convertColumnIndexToView(modelIndex);
				    
				    if ( convertedIndex >= 0 && convertedIndex < this.getTable().getColumnCount() )
				    {
					columnModel.moveColumn(convertedIndex, this.getTable().convertViewToView(i));
				    }
				}
			    }
			}
		    }
			    
		    this.inhibitConfigurationSave --;
		}
	    }
	}
    }
    
    /** save the configuration of the current TablePanel */
    public void saveConfiguration()
    {
	try
	{
	    TableConfiguration configuration = this.createConfiguration();
	    
	    TableConfigurationManager.saveConfiguration(this.getConfigurationRelativeFilePath(), configuration, this);
	    
	    if ( this.getTable().getModel() instanceof PropertyBasedTableModel )
	    {
		PropertyBasedTableModel propertyModel = (PropertyBasedTableModel)this.getTable().getModel();
		
		/** avoid to save configuration if a column is moved */
		this.inhibitConfigurationSave ++;

		TableColumnModel columnModel = this.getTable().getColumnModel();

		if ( configuration.getDisplayedColumns() != null )
		{
		    for(int i = 0; i < configuration.getDisplayedColumns().length; i++)
		    {
			String currentProperty = configuration.getDisplayedColumns()[i];

			if ( currentProperty != null )
			{
			    int modelIndex = propertyModel.getColumnIndexOfProperty(currentProperty);

			    if ( modelIndex >= 0 && modelIndex < columnModel.getColumnCount() )
			    {
				int convertedIndex = this.getTable().convertColumnIndexToView(modelIndex);

				if ( convertedIndex >= 0 && convertedIndex < this.getTable().getColumnCount() )
				{
				    columnModel.moveColumn(convertedIndex, this.getTable().convertViewToView(i));
				}
			    }
			}
		    }
		}

		this.inhibitConfigurationSave --;
	    }
	}
	catch (IOException ex)
	{
	    logger.error("unable to save TablePanel configuration linked to path '" + this.getConfigurationRelativeFilePath() + "'", ex);
	}
    }
    
    /** return the relative file path representing the configuration file to use
     *	@return a String
     */
    public String getConfigurationRelativeFilePath()
    {
	return this.confRelativeFilePath;
    }
    
    /** return the relative file path representing the configuration file to use<br>
     *	you can for example simply put the name of the file which will contains the configuration : "configuration.properties"<br>
     *	@return a String
     */
    public void setConfigurationRelativeFilePath(String confRelativeFilePath)
    {
	boolean equals = false;
	
	if ( this.getConfigurationRelativeFilePath() == null )
	{
	    if ( confRelativeFilePath == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    equals = this.getConfigurationRelativeFilePath().equals(confRelativeFilePath);
	}
	
	if ( ! equals )
	{
	    String oldValue = this.getConfigurationRelativeFilePath();
	    
	    if ( oldValue != null )
	    {
		/** unregister from manager */
		TableConfigurationManager.unregister(oldValue, this);
	    }
	    
	    this.confRelativeFilePath = confRelativeFilePath;
	    
	    this.firePropertyChange(PROPERTY_CONF_RELATIVE_FILEPATH, oldValue, this.getConfigurationRelativeFilePath());
	    
	    /** unregister from manager */
	    if ( this.getConfigurationRelativeFilePath() != null )
	    {
		TableConfigurationManager.register(this.getConfigurationRelativeFilePath(), this);
		
		try
		{
		    TableConfiguration config = TableConfigurationManager.loadConfiguration(this.getConfigurationRelativeFilePath());
		    this.loadConfiguration(config);
		}
		catch (IOException ex)
		{
		    logger.error("unable to load configuration from '" + this.getConfigurationRelativeFilePath() + "'", ex);
		}
	    }
	}
    }
    
    /** indicate if the buttons have to be visible
     *  @param visible true to set actions buttons as visible
     */
    public void setActionButtonsVisible(boolean visible)
    {   this.buttonsPanel.setVisible(visible); }
    
    /** return the action button related to given index
     *  @param index the index of the action button (from 1 to ?)
     *  @return a JButton or null if not found
     */
    public JButton getActionButton(int index)
    {   JButton button = null;
        
        int newIndex = index - 1;
        
        if ( this.actionButtons != null && newIndex >= 0 && newIndex < this.actionButtons.length )
        {   button = this.actionButtons[newIndex]; }
        
        return button;
    }
    
    /** methods that make first 'count' buttons visible
     *  @param count the number of button that we want to see visible
     */
    public void ensureActionButtonsVisible(int count)
    {   
        if ( this.actionButtons != null )
        {
            this.setActionButtonsVisible(true);
        
            int max = Math.min(count, this.actionButtons.length);
            
            for(int i = 0; i < this.actionButtons.length; i++)
            {   JButton button = this.actionButtons[i];
                
                if ( button != null )
                {
                    if ( i < max )
                    {   button.setVisible(true); }
                    else
                    {   button.setVisible(false); }
                }
            }
        }
    }
    
    /** method that create the table used in this component
     *  @return a PageableTable
     */
    protected T createTable()
    {   
        return (T)new PageableTable();
    }
    
    /** return the table contained by this panel
     *  @return a PageableTable
     */
    public T getTable()
    {   return this.table; }
    
    /** initialize the model to use
     *  @param model a TableModel or a PageableTableModel to be able to control pageable functionnalities directly by the model
     */
    public void setModel(TableModel model)
    {   this.getTable().setModel(model); }
    
    /** set the maximum row count displayed
     *  @param maxDisplayedRows the maximum row count that the model can display at a time
     *      
     *  @exception IllegalArgumentException if maxDisplayedRows is &lt;= 1 and != -1
     */
    public void setMaximumDisplayedRows(int maxDisplayedRows)
    {   this.getTable().setMaximumDisplayedRows(maxDisplayedRows); }
    
    /** return the maximum row count displayed
     *  @return the maximum row count that the model can display at a time
     */
    public int getMaximumDisplayedRows()
    {   return this.getTable().getMaximumDisplayedRows(); }
    
    /** set the current page number
     *  @param pageNumber the number of the page to display<br>
     *      if pageNumber is greater than the page count, then we will try to see the last page.<br>
     *      if pageNumber is less than 0, then we will try to display the content of the first page
     */
    public void setCurrentPage(int pageNumber)
    {   this.getTable().setCurrentPage(pageNumber); }
    
    /** return the current page number
     *  @return an integer (from 0 to ?)
     */
    public int getCurrentPage()
    {   return this.getTable().getCurrentPage(); }
    
    /** return the panel that contains the graphical item that enable to control pagination
     *  such as the page number text field and the navigation buttons
     *  @return a JPanel
     */
    public JPanel getNavigationPanel()
    {
        return this.pageablePanel;
    }
    
    /** release the TablePanel */
    public void release()
    {
	this.setConfigurationRelativeFilePath(null);
	
	if ( this.getTable() != null )
	{
	    this.getTable().removePropertyChangeListener("model", this);
	    this.getTable().removePropertyChangeListener(NumberedPageableTable.PROPERTY_DISPLAY_ROW_NUMBER, this);
	    
	    this.getTable().removePropertyChangeListener(PageableTable.PROPERTY_CURRENT_PAGE_NUMBER, this);
	    this.getTable().removePropertyChangeListener(PageableTable.PROPERTY_PAGE_COUNT, this);
	    this.getTable().removePropertyChangeListener(PageableTable.PROPERTY_MAX_DISPLAYED_ROWS, this);
    //        this.getTable().removePropertyChangeListener(PageableTable.PROPERTY_DISPLAY_ROW_NUMBER, this);
	    
	    TableColumnModel columnModel = this.getTable().getColumnModel();
	    if ( columnModel != null )
	    {
		columnModel.removeColumnModelListener(this.columnModelListener);
		this.columnModelListener = null;
	    }
	    
	    if ( this.getTable().getTableHeader() != null )
	    {
		this.getTable().getTableHeader().setComponentPopupMenu(null);
	    }
	    
	    if ( this.confPopupMenu != null )
	    {
		this.confPopupMenu.removePopupMenuListener(this);
		this.confPopupMenu.release();
		this.confPopupMenu = null;
	    }
	    
	    this.table = null;
	}
	
	if ( this.previousPageButton != null )
	{
	    this.previousPageButton.removeActionListener(this);
	    this.previousPageButton = null;
	}
	if ( this.nextPageButton != null )
	{
	    this.nextPageButton.removeActionListener(this);
	    this.nextPageButton = null;
	}
	
	if ( this.pageTextField != null )
	{
	    this.pageTextField.setColdVerifier(null);
	    this.pageTextField = null;
	}
    }
    
    /** create a TableConfiguration that describe the state of the TablePanel
     *	@return a Tableconfiguration
     */
    public TableConfiguration createConfiguration()
    {
	/* create a TableConfiguration and ask to save it */
	boolean rowDisplayed     = true;
	boolean horScrollEnabled = false;
	boolean horGrid          = false;
	boolean verGrid          = false;

	if ( this.getTable() != null )
	{
	    if ( this.getTable() instanceof NumberedPageableTable )
	    {
		rowDisplayed = ((NumberedPageableTable)this.getTable()).isRowNumberDisplayed();
	    }
	    
	    horGrid = this.getTable().getShowHorizontalLines();
	    verGrid = this.getTable().getShowVerticalLines();
	    
	    horScrollEnabled = this.getTable().isHorizontalScrollEnabled();
	}

	TableConfiguration conf = new TableConfiguration(null, horScrollEnabled, rowDisplayed, this.getMaximumDisplayedRows(), horGrid, verGrid);
	
	if ( this.getTable() != null )
	{
	    TableColumnModel columnModel = this.getTable().getColumnModel();
	    
	    for(int i = 0; i < columnModel.getColumnCount(); i++)
	    {
		TableColumn column = columnModel.getColumn(i);
		System.out.println("\ti=" + i + " --> pref width=" + column.getPreferredWidth() + " id=" + column.getIdentifier() + 
			    " header value=" + column.getHeaderValue());
	    }
	}
	
	
//	conf.setColumnPreferredSizeForProperty()
//	
//			    int prefSize = configuration.getColumnPreferredSizeForProperty(declaration.getPropertyName());
	
	return conf;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        pageablePanel = new javax.swing.JPanel();
        previousPageButton = new javax.swing.JButton();
        pageLabel = new javax.swing.JLabel();
        pageTextField = new org.siberia.ui.swing.SibTextField();
        pageCountLabel = new javax.swing.JLabel();
        nextPageButton = new javax.swing.JButton();
        buttonsPanel = new javax.swing.JPanel();
        button1 = new javax.swing.JButton();
        button2 = new javax.swing.JButton();
        button3 = new javax.swing.JButton();
        button4 = new javax.swing.JButton();
        button5 = new javax.swing.JButton();
        button6 = new javax.swing.JButton();
        button7 = new javax.swing.JButton();
        button8 = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();

        pageablePanel.setAlignmentX(0.0F);
        previousPageButton.setText("<<");
        previousPageButton.setMaximumSize(new java.awt.Dimension(20, 18));
        previousPageButton.setMinimumSize(new java.awt.Dimension(20, 18));
        previousPageButton.setPreferredSize(new java.awt.Dimension(20, 18));

        pageLabel.setText("page");

        pageTextField.setText("sibTextField1");

        pageCountLabel.setText("/ 10");

        nextPageButton.setText(">>");
        nextPageButton.setMaximumSize(new java.awt.Dimension(20, 18));
        nextPageButton.setMinimumSize(new java.awt.Dimension(20, 18));
        nextPageButton.setPreferredSize(new java.awt.Dimension(20, 18));

        org.jdesktop.layout.GroupLayout pageablePanelLayout = new org.jdesktop.layout.GroupLayout(pageablePanel);
        pageablePanel.setLayout(pageablePanelLayout);
        pageablePanelLayout.setHorizontalGroup(
            pageablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pageablePanelLayout.createSequentialGroup()
                .add(previousPageButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pageTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pageCountLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(nextPageButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pageablePanelLayout.setVerticalGroup(
            pageablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pageablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(previousPageButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(pageLabel)
                .add(pageTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(pageCountLabel)
                .add(nextPageButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        button1.setMaximumSize(new java.awt.Dimension(18, 18));
        button1.setMinimumSize(new java.awt.Dimension(18, 18));
        button1.setPreferredSize(new java.awt.Dimension(18, 18));

        button2.setMaximumSize(new java.awt.Dimension(18, 18));
        button2.setMinimumSize(new java.awt.Dimension(18, 18));
        button2.setPreferredSize(new java.awt.Dimension(18, 18));

        button3.setMaximumSize(new java.awt.Dimension(18, 18));
        button3.setMinimumSize(new java.awt.Dimension(18, 18));
        button3.setPreferredSize(new java.awt.Dimension(18, 18));

        button4.setMaximumSize(new java.awt.Dimension(18, 18));
        button4.setMinimumSize(new java.awt.Dimension(18, 18));
        button4.setPreferredSize(new java.awt.Dimension(18, 18));

        button5.setMaximumSize(new java.awt.Dimension(18, 18));
        button5.setMinimumSize(new java.awt.Dimension(18, 18));
        button5.setPreferredSize(new java.awt.Dimension(18, 18));

        button6.setMaximumSize(new java.awt.Dimension(18, 18));
        button6.setMinimumSize(new java.awt.Dimension(18, 18));
        button6.setPreferredSize(new java.awt.Dimension(18, 18));

        button7.setMaximumSize(new java.awt.Dimension(18, 18));
        button7.setMinimumSize(new java.awt.Dimension(18, 18));
        button7.setPreferredSize(new java.awt.Dimension(18, 18));

        button8.setMaximumSize(new java.awt.Dimension(18, 18));
        button8.setMinimumSize(new java.awt.Dimension(18, 18));
        button8.setPreferredSize(new java.awt.Dimension(18, 18));

        org.jdesktop.layout.GroupLayout buttonsPanelLayout = new org.jdesktop.layout.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(button1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(button2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(button3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(button4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(button5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(button6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(button7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(button8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanelLayout.createSequentialGroup()
                .add(button1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(button2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(button3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(button4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(button5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(button6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(button7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(button8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(pageablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pageablePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .add(buttonsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton button1;
    protected javax.swing.JButton button2;
    protected javax.swing.JButton button3;
    protected javax.swing.JButton button4;
    protected javax.swing.JButton button5;
    protected javax.swing.JButton button6;
    protected javax.swing.JButton button7;
    protected javax.swing.JButton button8;
    protected javax.swing.JPanel buttonsPanel;
    protected javax.swing.JButton nextPageButton;
    protected javax.swing.JLabel pageCountLabel;
    protected javax.swing.JLabel pageLabel;
    protected org.siberia.ui.swing.SibTextField pageTextField;
    protected javax.swing.JPanel pageablePanel;
    protected javax.swing.JButton previousPageButton;
    protected javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
    
    /* #########################################################################
     * ############### PropertyChangeListener implementation ###################
     * ######################################################################### */
    
    public void propertyChange(PropertyChangeEvent evt)
    {
        if ( evt.getSource() == this.getTable() )
        {   
            if ( evt.getPropertyName().equals(PageableTable.PROPERTY_CURRENT_PAGE_NUMBER) ||
                 evt.getPropertyName().equals(PageableTable.PROPERTY_PAGE_COUNT) )
            {   
                int currentPage = -1;
                int pageCount   = -1;
                
                if ( evt.getPropertyName().equals(PageableTable.PROPERTY_CURRENT_PAGE_NUMBER) )
                {   currentPage = (Integer)evt.getNewValue();
                    this.pageTextField.setText("" + (currentPage + 1));
                }
                else
                {   currentPage = this.getTable().getCurrentPage(); }
                
                if ( evt.getPropertyName().equals(PageableTable.PROPERTY_PAGE_COUNT) )
                {   pageCount = (Integer)evt.getNewValue();
                    
                    this.pageCountLabel.setText("/ " + pageCount);
                }
                else
                {   pageCount = this.getTable().getPageCount(); }
                
                this.previousPageButton.setEnabled(currentPage != 0);
                this.nextPageButton.setEnabled(currentPage < pageCount - 1);
            }
            else if ( evt.getPropertyName().equals(PageableTable.PROPERTY_MAX_DISPLAYED_ROWS) )
            {   if ( evt.getNewValue() instanceof Number )
                {   this.pageablePanel.setVisible( ((Number)evt.getNewValue()).intValue() > 0 ); }
            }
	    else if ( evt.getPropertyName().equals("model") )
	    {
		/** load configuration and update */
		String confRelPath = this.getConfigurationRelativeFilePath();
		if ( confRelPath != null )
		{
		    TableConfiguration conf = null;
		    try
		    {
			conf = TableConfigurationManager.loadConfiguration(confRelPath);
		    }
		    catch(IOException e)
		    {
			logger.error("unable to load configuration for TablePanel linked to '" + confRelPath + "'", e);
		    }
		    
		    this.loadConfiguration(conf);
		}
	    }
        }
    }
    
    /* #########################################################################
     * ################### ActionListener implementation #######################
     * ######################################################################### */
    
    public void actionPerformed(ActionEvent evt)
    {   if ( evt.getSource() == this.nextPageButton )
        {   if ( this.getCurrentPage() < this.getTable().getPageCount() )
            {   this.setCurrentPage(this.getCurrentPage() + 1); }
        }
        else if ( evt.getSource() == this.previousPageButton )
        {   if ( this.getCurrentPage() > 0 )
            {   this.setCurrentPage(this.getCurrentPage() - 1); }
        }
    }
    
    /* #########################################################################
     * ################## PopupMenuListener implementation #####################
     * ######################################################################### */
    
    /**
     *  This method is called before the popup menu becomes visible 
     */
    public void popupMenuWillBecomeVisible(PopupMenuEvent e)
    {	}

    /**
     * This method is called before the popup menu becomes invisible
     * Note that a JPopupMenu can become invisible any time 
     */
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering popupMenuWillBecomeInvisible(PopupMenuEvent)");
	}
	if ( e.getSource() == this.confPopupMenu )
	{
	    /** if the popup menu was modified, then save configuration */
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("configuration feels modified ? " + this.confPopupMenu.isModified());
	    }
	    if ( this.getConfigurationRelativeFilePath() != null && this.confPopupMenu.isModified() )
	    {	this.saveConfiguration();
		
		this.confPopupMenu.setModified(false);
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting popupMenuWillBecomeInvisible(PopupMenuEvent)");
	}
	
    }

    /**
     * This method is called when the popup menu is canceled
     */
    public void popupMenuCanceled(PopupMenuEvent e)
    {	}
}
