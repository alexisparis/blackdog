/* 
 * TransSiberia-ui : siberia plugin frontend for TransSiberia
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
package org.siberia.trans.ui.editor.impl.plugin;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyVetoException;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreePath;
import org.siberia.SiberiaIntrospector;
import org.siberia.trans.TransSiberia;
import org.siberia.trans.exception.ConfigurationException;
import org.siberia.trans.exception.InvalidRepositoryException;
import org.siberia.trans.type.RepositoryUtilities;
import org.siberia.trans.type.plugin.Plugin;
import org.siberia.trans.type.RepositoryPluginContainer;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.type.plugin.Version;
import org.siberia.trans.type.repository.SiberiaRepository;
import org.siberia.type.SibList;
import org.siberia.type.SibType;
import org.siberia.type.message.SibMessage;
import org.siberia.type.message.SibWaitingMessage;
import org.siberia.type.message.SibErrorMessage;
import org.siberia.type.message.SibInformationMessage;
import org.siberia.type.event.ContentChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeListener;
import org.siberia.ui.swing.tree.model.GenericTreeModel;
import org.siberia.ui.swing.tree.model.VisualizationTreeModel;
import org.siberia.ui.swing.treetable.SibTypeListTreeTable;
import org.siberia.ui.swing.treetable.SibTypeListTreeTablePanel;
import org.siberia.ui.swing.treetable.TreeTableModel;
import org.siberia.ui.swing.treetable.introspection.IntrospectionSibTypeListTreeTableModel;
import org.siberia.ui.swing.treetable.introspection.PageablePathConverter;
import org.siberia.ui.swing.table.model.PropertyDeclaration;

/**
 *
 * @author  alexis
 */
public class PluginChooserPanel extends javax.swing.JPanel implements HierarchicalPropertyChangeListener,
								      ListSelectionListener,
								      ActionListener
{
    /** selection panel */
    private PluginSelectionPanel      selectionPanel           = null;
    
    /** available plugins panel */
    private SibTypeListTreeTablePanel availablePluginPanel     = null;
    
    /** selected plugins panel */
    private SibTypeListTreeTablePanel selectedPluginPanel      = null;
    
    /** executors service */
    private ExecutorService           executor                 = null;
    
    /** inhibition count of ListSelectionModel */
    private int                       selectionInhibitionCount = 0;
    
    /** PropertyChangeListener that allow to listen to ListSelectionModel */
    private PropertyChangeListener    listSelectionManager     = null;
    
    /** transSiberia to use */
    private TransSiberia              trans                    = null;
    
    /** plugin that we are actually showing detail */
    private Plugin                    pluginOnDetail           = null;
    
    /** labels used in the textpane to display plugin information */
    private String                    labelLicense             = null;
    private String                    labelReleaseDate         = null;
    
    /** soft reference to a SiberiaIntrospector */
    private SoftReference<SiberiaIntrospector> introspectorRef       = new SoftReference<SiberiaIntrospector>(null);
    
    /** Creates new form PluginChooserPanel
     *	@param transSiberian an instance of TransSiberia
     */
    public PluginChooserPanel(final TransSiberia transSiberian)
    {
	if ( transSiberian == null )
	{
	    throw new IllegalArgumentException("transSiberian shall not be null");
	}
	
	this.trans = transSiberian;
	
	initComponents();
	
	this.pluginDescription.setEditorKit(new HTMLEditorKit());
	
	final ResourceBundle rb = ResourceBundle.getBundle(PluginChooserPanel.class.getName());
	
	this.availableVersionLabel.setText(rb.getString("availableVersionLabel"));
	this.installedVersionLabel.setText(rb.getString("installedVersionLabel"));
	this.mainLabel.setText(rb.getString("mainLabel"));
	
	/* initialize layout */
	FormLayout layout = new FormLayout("fill:pref:grow, 5px, pref, 5px, fill:pref:grow", "fill:pref:grow");
	layout.setColumnGroups(new int[][]{{1, 5}});
	this.mainPanel.setLayout(layout);
	CellConstraints cc = new CellConstraints();
	
	/** add selection buttons */
	this.selectionPanel = new PluginSelectionPanel();	
	this.mainPanel.add(this.selectionPanel, cc.xy(3, 1));
//	this.selectionPanel.getAddAllButton().setEnabled(true);
//	this.selectionPanel.getRemoveAllButton().setEnabled(true);
//	this.selectionPanel.getAddButton().setEnabled(false);
//	this.selectionPanel.getRemoveButton().setEnabled(false);
	
	this.selectionPanel.getAddButton().addActionListener(this);
	this.selectionPanel.getAddAllButton().addActionListener(this);
	this.selectionPanel.getRemoveButton().addActionListener(this);
	this.selectionPanel.getRemoveAllButton().addActionListener(this);
	
	this.installedVersionField.setText("");
	this.availableVersionField.setText("");
	
	/** install plugin panel */
	this.availablePluginPanel = new SibTypeListTreeTablePanel();
	this.selectedPluginPanel  = new SibTypeListTreeTablePanel();
	
	int visibleRowCount = 10;
	this.availablePluginPanel.getTable().setVisibleRowCount(visibleRowCount);
	this.selectedPluginPanel.getTable().setVisibleRowCount(visibleRowCount);
	int maxDspRows = -1; // unlimited
	this.availablePluginPanel.getTable().setMaximumDisplayedRows(maxDspRows);
	this.selectedPluginPanel.getTable().setMaximumDisplayedRows(maxDspRows);
	int selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
	this.availablePluginPanel.getTable().setSelectionMode(selectionMode);
	this.selectedPluginPanel.getTable().setSelectionMode(selectionMode);
	
	this.availablePluginPanel.getTable().getSelectionModel().addListSelectionListener(this);
	this.selectedPluginPanel.getTable().getSelectionModel().addListSelectionListener(this);
	
	this.listSelectionManager = new PropertyChangeListener()
	{
	    public void propertyChange(PropertyChangeEvent evt)
	    {
		if ( evt.getOldValue() instanceof ListSelectionModel )
		{
		    ((ListSelectionModel)evt.getOldValue()).removeListSelectionListener(PluginChooserPanel.this);
		}
		if ( evt.getNewValue() instanceof ListSelectionModel )
		{
		    ((ListSelectionModel)evt.getNewValue()).addListSelectionListener(PluginChooserPanel.this);
		}
	    }
	};
	
//	PropertyDeclaration decl_category = new PropertyDeclaration(PluginStructure.PROPERTY_CATEGORY, true, true, 40);
	PropertyDeclaration decl_choice   = new PropertyDeclaration(Plugin.PROPERTY_VERSION_CHOICE, true, true, 40);
	
	this.availablePluginPanel.getTable().addPropertyChangeListener("selectionModel", this.listSelectionManager);
	this.selectedPluginPanel.getTable().addPropertyChangeListener("selectionModel", this.listSelectionManager);
	
	/* configuration of availablePluginPanel */
	IntrospectionSibTypeListTreeTableModel availableTreeTableModel = new IntrospectionSibTypeListTreeTableModel();
	final GenericTreeModel availableTreeModel = new VisualizationTreeModel();
	
	/* list of RepositoryPluginContainer */
	SibList availableItems = new SibList(20);
	availableItems.addHierarchicalPropertyChangeListener(this);
	try
	{   availableItems.setName(rb.getString("availableRepositoriesLabel"));
	    availableItems.setAllowedClass(RepositoryPluginContainer.class);
	}
	catch (PropertyVetoException ex)
	{   ex.printStackTrace(); }
	    
	availableTreeModel.setRoot(availableItems);
	availableTreeTableModel.setInnerConfigurableTreeModel(availableTreeModel);
	
	availableTreeTableModel.getInnerTableModel().addPropertyDeclarations(decl_choice);
	this.availablePluginPanel.getTable().setTreeTableModel(availableTreeTableModel);
	availableTreeTableModel.setPathConverter(new PageablePathConverter(this.availablePluginPanel.getTable().getTreeRenderer()));
	
	
	/* configuration of selectedPluginPanel */
	IntrospectionSibTypeListTreeTableModel selectedTreeTableModel = new IntrospectionSibTypeListTreeTableModel();
	final GenericTreeModel selectedTreeModel = new VisualizationTreeModel();
	
	/* list of RepositoryPluginContainer */
	SibList selectedItems = new SibList(20);
	selectedItems.addHierarchicalPropertyChangeListener(this);
	try
	{   selectedItems.setName(rb.getString("selectedRepositoriesLabel"));
	    selectedItems.setAllowedClass(Plugin.class);
	}
	catch (PropertyVetoException ex)
	{   ex.printStackTrace(); }
	    
	selectedTreeModel.setRoot(selectedItems);
	selectedTreeTableModel.setInnerConfigurableTreeModel(selectedTreeModel);
	selectedTreeTableModel.getInnerTableModel().addPropertyDeclarations(decl_choice);
	this.selectedPluginPanel.getTable().setTreeTableModel(selectedTreeTableModel);
	selectedTreeTableModel.setPathConverter(new PageablePathConverter(this.selectedPluginPanel.getTable().getTreeRenderer()));
	
	SibList repositories = null;
	try
	{   
	    repositories = transSiberian.getRepositories();
	}
	catch (ConfigurationException ex)
	{
	    // TODO
	    ex.printStackTrace();
	}
	
	if ( repositories == null || repositories.size() == 0 )
	{
	    /* tell user that no repositories are declared */
	    // TODO : open the module that allow to declare siberia repositories
	    // TODO
	}
	
	/* set of runnable to launch */
	Set<Runnable> runnables = new HashSet<Runnable>(repositories.size());
	
	/* add a RepositoryPluginContainer for every declared repository */
	synchronized(repositories)
	{
	    final String waitingLabel   = rb.getString("repositoryWaitingLabel");
	    final String noUpdatesLabel = rb.getString("repositoryNoNewPluginLabel");
	    final String errorLabel     = rb.getString("repositoryErrorLabel");
	    
	    for(int i = 0; i < repositories.size(); i++)
	    {
		Object current = repositories.get(i);
		
		if ( current instanceof SiberiaRepository )
		{
		    final RepositoryPluginContainer container = RepositoryUtilities.createPluginContainer( (SiberiaRepository)current );
		    container.setAllowedClass(SibType.class);
		    
		    /* add one Message to indicate that plugin collect is runnig */
		    final SibWaitingMessage waitingMsg = new SibWaitingMessage();
		    
		    try
		    {	waitingMsg.setName(waitingLabel); }
		    catch(PropertyVetoException e)
		    {	e.printStackTrace(); }
		    
		    container.add(waitingMsg);
		    
		    /* add container to the list */
		    availableItems.add(container);
		    
		    /* create a runnable and add it to the list of runnable to launch after */
		    runnables.add(new Runnable()
		    {
			public void run()
			{
			    // TODO : to remove, just here to view the waiting message
			    try
			    {	Thread.sleep(1000); }
			    catch (InterruptedException ex)
			    {	ex.printStackTrace(); }
			    
			    /* launch the collect of plugin for the current container */
			    SibList plugins = null;
			    InvalidRepositoryException exception = null;
			    try
			    {
				plugins = transSiberian.getAvailablePlugins( container.getRepository() );
			    }
			    catch(InvalidRepositoryException e)
			    {
				exception = e;
			    }
			    
			    /* remove the waiting message */
			    container.remove(waitingMsg);
			    
			    if ( exception != null || plugins == null )
			    {
				/* add an error message to indicate to user that the plugin collect failed */
				SibMessage msg = new SibErrorMessage();
				try
				{   msg.setName(errorLabel); }
				catch(PropertyVetoException e)
				{   e.printStackTrace(); }
				    
				container.add(msg);
				
			    }
			    else if ( plugins.size() == 0 )
			    {
				/* add a message that tell that no plugin is available from this repository */
				SibMessage msg = new SibInformationMessage();
				try
				{   msg.setName(noUpdatesLabel); }
				catch(PropertyVetoException e)
				{   e.printStackTrace(); }
				    
				container.add(msg);
				
			    }
			    else
			    {
				/** add plugin */
				container.addAll(plugins);
			    }
			}
		    });
		};
	    }
	}
	
	// TODO
//	this.availablePluginPanel.getTable().setPreferredScrollableViewportSize(new java.awt.Dimension(150, 150));
//	this.availablePluginPanel.getTable().setPreferredScrollableViewportSize(new Dimension(150, 200));
//	this.selectedPluginPanel.getTable().setPreferredScrollableViewportSize(new Dimension(150, 200));
//	this.availablePluginPanel.getTable().setPreferredSize(new Dimension(150, 200));
//	this.selectedPluginPanel.getTable().setPreferredSize(new Dimension(150, 200));
	
	/** add SibTypeListTreeTablePanel to the main panel */
	this.mainPanel.add(this.availablePluginPanel, cc.xy(1, 1, cc.FILL, cc.FILL));
	this.mainPanel.add(this.selectedPluginPanel , cc.xy(5, 1, cc.FILL, cc.FILL));
	
	/* launch executor service */
	if ( this.executor == null )
	{
	    if ( runnables != null && runnables.size() > 0 )
	    {
		this.executor = Executors.newFixedThreadPool(runnables.size());
	    }
	    else
	    {
		this.executor = Executors.newSingleThreadExecutor();
	    }
	}
	
	if ( runnables != null )
	{
	    Iterator<Runnable> it = runnables.iterator();
	    while(it.hasNext())
	    {
		Runnable current = it.next();
		if ( current != null )
		{
		    this.executor.submit(current);
		}
	    }
	}
    }
    
    /** return a SiberiaIntrospector
     *  @return a SiberiaIntrospector
     */
    private SiberiaIntrospector getIntrospector()
    {   SiberiaIntrospector in = this.introspectorRef.get();
        if ( in == null )
        {   in = new SiberiaIntrospector();
            this.introspectorRef = new SoftReference<SiberiaIntrospector>(in);
        }
        
        return in;
    }
    
    /** return the List that contains the plugin to install
     *	@return a SibList
     */
    public SibList getSelectedPluginList()
    {
	return (SibList)this.selectedPluginPanel.getTable().getTreeTableModel().getRoot();
    }
    
    /** dispose the panel */
    public void dispose()
    {
	this.stopExecutorService();
	
	/* remove HierarchicalPropertyChangeListener */
	if ( this.availablePluginPanel != null )
	{
	    Object root = this.availablePluginPanel.getTable().getTreeTableModel().getRoot();
	    
	    if ( root instanceof SibType )
	    {
		((SibType)root).removeHierarchicalPropertyChangeListener(this);
	    }
	    
	    this.availablePluginPanel.getTable().getSelectionModel().removeListSelectionListener(this);
	    
	    this.availablePluginPanel.getTable().removePropertyChangeListener("selectionModel", this.listSelectionManager);
	}
	if ( this.selectedPluginPanel != null )
	{
	    Object root = this.selectedPluginPanel.getTable().getTreeTableModel().getRoot();
	    
	    if ( root instanceof SibType )
	    {
		((SibType)root).removeHierarchicalPropertyChangeListener(this);
	    }
	    
	    this.selectedPluginPanel.getTable().getSelectionModel().removeListSelectionListener(this);
	    
	    this.selectedPluginPanel.getTable().removePropertyChangeListener("selectionModel", this.listSelectionManager);
	}
    }
    
    /** stop executor service if necessary */
    public void stopExecutorService()
    {
	if ( this.executor != null && ! this.executor.isShutdown() )
	{
	    this.executor.shutdown();
	}
    }
    
    /** return the PluginSelectionPanel
     *	@return a PluginSelectionPanel
     */
    public PluginSelectionPanel getPluginSelectionPanel()
    {	return this.selectionPanel; }
    
    /** return the TablePanel that contains the available plugins
     *	@return a SibTypeListTreeTablePanel
     */
    public SibTypeListTreeTablePanel getAvailableTablePanel()
    {	return this.availablePluginPanel; }
    
    /** return the TablePanel that contains the selected plugins
     *	@return a SibTypeListTreeTablePanel
     */
    public SibTypeListTreeTablePanel getSelectedTablePanel()
    {	return this.selectedPluginPanel; }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        mainLabel = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pluginDescription = new javax.swing.JTextPane();
        availableVersionLabel = new javax.swing.JLabel();
        availableVersionField = new javax.swing.JTextField();
        installedVersionLabel = new javax.swing.JLabel();
        installedVersionField = new javax.swing.JTextField();

        setMinimumSize(new java.awt.Dimension(600, 500));
        mainLabel.setText("Select plugins to install");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 576, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("plugin"));
        pluginDescription.setBackground(new java.awt.Color(238, 238, 238));
        pluginDescription.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pluginDescription.setEditable(false);
        jScrollPane1.setViewportView(pluginDescription);

        availableVersionLabel.setText("available version");

        availableVersionField.setEditable(false);
        availableVersionField.setText("jTextField1");
        availableVersionField.setMinimumSize(new java.awt.Dimension(69, 19));

        installedVersionLabel.setText("installed version");

        installedVersionField.setEditable(false);
        installedVersionField.setText("jTextField2");
        installedVersionField.setMinimumSize(new java.awt.Dimension(69, 19));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(availableVersionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(availableVersionField, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(installedVersionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(installedVersionField, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(148, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(availableVersionLabel)
                    .addComponent(availableVersionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(installedVersionLabel)
                    .addComponent(installedVersionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mainLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField availableVersionField;
    private javax.swing.JLabel availableVersionLabel;
    private javax.swing.JTextField installedVersionField;
    private javax.swing.JLabel installedVersionLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel mainLabel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextPane pluginDescription;
    // End of variables declaration//GEN-END:variables
    
  
    /** ########################################################################
     *  #################### ActionListener implementation #####################
     *  ######################################################################## */
    
    public void actionPerformed(ActionEvent e)
    {
	SibTypeListTreeTable available = this.availablePluginPanel.getTable();
	SibTypeListTreeTable selected  = this.selectedPluginPanel.getTable();
	
	if ( e.getSource() == this.selectionPanel.getAddButton() )
	{
	    /** add selected item */
	    Object[] items = available.getSelectedObjects();
	    if ( items != null )
	    {
		for(int i = 0; i < items.length; i++)
		{
		    Object item = items[i];
		    
		    if ( item instanceof RepositoryPluginContainer )
		    {
			for(int j = ((RepositoryPluginContainer)item).size() - 1; j >= 0; j--)
			{
			    Object o = ((RepositoryPluginContainer)item).remove(j);
			    
			    /** add all plugin of the container */
			    ((SibList)selected.getTreeTableModel().getRoot()).add( o );
			}
		    }
		    else if ( item instanceof Plugin )
		    {
			SibType parent = ((Plugin)item).getParent();
			
			if ( parent instanceof RepositoryPluginContainer )
			{
			    ((RepositoryPluginContainer)parent).removePlugin( (Plugin)item );
			}
			((SibList)selected.getTreeTableModel().getRoot()).add( item );
		    }
		}
	    }
	}
	else if ( e.getSource() == this.selectionPanel.getAddAllButton() )
	{
	    /** add selected item */
	    Object root = available.getTreeTableModel().getRoot();
	    
	    if ( root instanceof SibList )
	    {
		for(int i = 0; i < ((SibList)root).size(); i++)
		{
		    Object item = ((SibList)root).get(i);
		    if ( item instanceof RepositoryPluginContainer )
		    {
			for(int j = ((RepositoryPluginContainer)item).size() - 1; j >= 0; j--)
			{
			    Object o = ((RepositoryPluginContainer)item).get(j);
			    
			    /** add all plugin of the container */
			    ((RepositoryPluginContainer)item).removePlugin( (Plugin)o );
			    ((SibList)selected.getTreeTableModel().getRoot()).add( o );
			}
		    }
		}
	    }
	}
	else if ( e.getSource() == this.selectionPanel.getRemoveButton() || e.getSource() == this.selectionPanel.getRemoveAllButton() )
	{
	    Object[] items = null;
	    
	    /** remove selected item */
	    if ( e.getSource() == this.selectionPanel.getRemoveButton() )
	    {
		items = selected.getSelectedObjects();
	    }
	    else
	    {
		items = new Object[ ((SibList)selected.getTreeTableModel().getRoot()).size() ];
		for(int i = 0; i < items.length; i++)
		{
		    items[i] = ((SibList)selected.getTreeTableModel().getRoot()).get(i);
		}
	    }
	    
	    if ( items != null )
	    {
		for(int i = 0; i < items.length; i++)
		{
		    Object item = items[i];
		    
		    if ( item instanceof Plugin )
		    {
			((SibList)selected.getTreeTableModel().getRoot()).remove( item );
			
			/** search for the repository container of the plugin */
			SiberiaRepository repository = ((Plugin)item).getRepository();
			
			Object availableRoot = available.getTreeTableModel().getRoot();
			
			if ( availableRoot instanceof SibList )
			{
			    for(int j = 0; j < ((SibList)availableRoot).size(); j++ )
			    {
				Object current = ((SibList)availableRoot).get(j);
				
				if ( current instanceof RepositoryPluginContainer )
				{
				    if ( ((RepositoryPluginContainer)current).getRepository().equals(repository) )
				    {
					/** search for the best location where to put the plugin */
					((RepositoryPluginContainer)current).addPlugin( (Plugin)item );
					break;
				    }
				}
			    }
			}
		    }
		}
	    }
	    
	}
    }
  
    /** ########################################################################
     *  ################# ListSelectionListener implementation #################
     *  ######################################################################## */
    
    /** 
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     */
    public void valueChanged(ListSelectionEvent e)
    {
	if ( ! e.getValueIsAdjusting() && this.selectionInhibitionCount == 0 )
	{
	    this.selectionInhibitionCount ++;
	    
	    boolean addActivated       = false;
	    boolean addAllActivated    = false;
	    boolean removeActivated    = false;
	    boolean removeAllActivated = false;
	    
	    ListSelectionModel availableSelectionModel = this.availablePluginPanel.getTable().getSelectionModel();
	    ListSelectionModel selectedSelectionModel  = this.selectedPluginPanel.getTable().getSelectionModel();

	    if ( e.getSource() == availableSelectionModel )
	    {
		/** if there are selection on the available panel, then ensure that there is no selection
		 *  in selected items
		 */
		if ( availableSelectionModel.getMaxSelectionIndex() != -1 )
		{
		    selectedSelectionModel.setSelectionInterval(-1, -1);
		}
	    }
	    else if ( e.getSource() == selectedSelectionModel )
	    {
		/** if there are selection on the available panel, then ensure that there is no selection
		 *  in selected items
		 */
		if ( selectedSelectionModel.getMaxSelectionIndex() != -1 )
		{
		    availableSelectionModel.setSelectionInterval(-1, -1);
		}
	    }
	    
	    /** reference to the selected plugin if there is only one plugin selected */
	    Plugin selectedPlugin = null;
	    if ( availableSelectionModel.getMaxSelectionIndex() == availableSelectionModel.getMinSelectionIndex() &&
		 availableSelectionModel.getMaxSelectionIndex() >= 0 )
	    {
		SibType item = this.availablePluginPanel.getTable().getTreeRenderer().getSelectedElement();
		if ( item instanceof Plugin )
		{
		    selectedPlugin = (Plugin)item;
		}
	    }
	    else if ( selectedSelectionModel.getMaxSelectionIndex() == selectedSelectionModel.getMinSelectionIndex() &&
		      selectedSelectionModel.getMaxSelectionIndex() >= 0 )
	    {
		SibType item = this.selectedPluginPanel.getTable().getTreeRenderer().getSelectedElement();
		if ( item instanceof Plugin )
		{
		    selectedPlugin = (Plugin)item;
		}
	    }
	    
	    this.updateDetails(selectedPlugin);
	    
//	    addAllActivated

//	    this.selectionPanel.getAddButton().setEnabled(addActivated);
//	    this.selectionPanel.getAddAllButton().setEnabled(addAllActivated);
//	    this.selectionPanel.getRemoveButton().setEnabled(removeActivated);
//	    this.selectionPanel.getRemoveAllButton().setEnabled(removeAllActivated);
	    
	    this.selectionInhibitionCount --;
	}
    }
    
    /** update plugin detail
     *	@param plugin the plugin to use to display details
     */
    private void updateDetails(Plugin plugin)
    {
	    
	if ( plugin != null )
	{
	    /** create a buffer with all information about the plugin */
	    StringBuffer buffer = new StringBuffer();

	    String fontSize = this.availableVersionLabel.getFont().getSize() + "pt";
	    fontSize = "-2";

	    buffer.append("<html><body><font size=\"" + fontSize + "\" face=\"" + this.availableVersionLabel.getFont().getFamily() + "\">");

	    if ( this.labelLicense == null || this.labelReleaseDate == null )
	    {
		/** get the BeanInfo for class PluginBuild and search in the PropertyDescriptor for 
		 *	the related display name
		 */
		BeanInfo info = getIntrospector().getBeanInfo(PluginBuild.class);

		if ( info != null )
		{
		    PropertyDescriptor[] descriptors = info.getPropertyDescriptors();

		    if ( descriptors != null )
		    {
			for(int i = 0; i < descriptors.length || (this.labelLicense != null && this.labelReleaseDate != null); i++ )
			{
			    PropertyDescriptor desc = descriptors[i];

			    if ( desc != null )
			    {
				if ( PluginBuild.PROPERTY_LICENSE_NAME.equals(desc.getName()) )
				{
				    this.labelLicense = desc.getDisplayName();
				}
				else if ( PluginBuild.PROPERTY_RELEASE_DATE.equals(desc.getName()) )
				{
				    this.labelReleaseDate = desc.getDisplayName();
				}
			    }
			}
		    }
		}

		if ( this.labelLicense == null )
		{
		    this.labelLicense = "License";
		}
		if ( this.labelReleaseDate == null )
		{
		    this.labelReleaseDate = "Release date";
		}

		this.labelLicense     += " : ";
		this.labelReleaseDate += " : ";
	    }

	    Calendar releaseDate = plugin.getReleaseDateForVersion(plugin.getVersionChoice().getSelectedVersion());
	    if ( releaseDate != null )
	    {
		DateFormat format = DateFormat.getDateInstance();
		buffer.append("<b>" + this.labelReleaseDate + "</b>" + (releaseDate == null ? "" : format.format(releaseDate.getTime())));
		buffer.append("<br>");
	    }

	    String license = plugin.getLicenseVersion(plugin.getVersionChoice().getSelectedVersion());
	    if ( license != null )
	    {
		buffer.append("<b>" + this.labelLicense + "</b>"  + (license == null ? "" : license));
		buffer.append("<br>");
		buffer.append("<br>");
	    }

	    buffer.append(plugin.getDescription());

	    buffer.append("</font></body></html>");

	    this.pluginDescription.setText(buffer.toString());

	    Version installedVersion = this.trans.getInstalledVersionOfPlugin(plugin.getName());
	    this.installedVersionField.setText( (installedVersion == null ? "" : installedVersion.toString()) );
	    this.availableVersionField.setText(plugin.getVersionChoice().getSelectedVersion().toString());
	}
	else
	{
	    this.pluginDescription.setText("");
	    this.installedVersionField.setText("");
	    this.availableVersionField.setText("");
	}
	
	this.pluginOnDetail = plugin;
    }
  
    /** ########################################################################
     *  ########### HierarchicalPropertyChangeListener implementation ##########
     *  ######################################################################## */
    
    /** called when a HierarchicalPropertyChangeEvent was throwed by
     *  an object listener by this one
     *  @param event a HierarchycalPropertyChangeEvent
     */
    public void propertyChange(HierarchicalPropertyChangeEvent event)
    {
	/** expand tree path of the item if event reflect an item added */
	if ( event.getPropertyChangeEvent() instanceof ContentChangeEvent )
	{
	    ContentChangeEvent evt = (ContentChangeEvent)event.getPropertyChangeEvent();
	    
	    if ( evt.getMode() == ContentChangeEvent.ADD )
	    {
		Object availableRoot = this.availablePluginPanel.getTable().getTreeTableModel().getRoot();
		SibTypeListTreeTablePanel panel = null;
		
		if ( event.getCurrentSource() == availableRoot )
		{
		    /* expand path in the available plugin panel */
		    panel = this.availablePluginPanel;
		}
		else
		{
		    /* expand path in the selected plugin panel */
		    panel = this.selectedPluginPanel;

		}
		
		if ( panel != null )
		{
		    this.expandPathRecursively(event.getArrayPath(), panel, panel.getTable().getTreeTableModel());
		}
	    }
	}
	else
	{
	    PropertyChangeEvent subEvent = event.getPropertyChangeEvent();
	    if ( subEvent != null )
	    {
		if ( Plugin.PROPERTY_VERSION_CHOICE.equals(subEvent.getPropertyName()) && subEvent.getSource() instanceof Plugin )
		{
		    /* update plugin details */
		    if ( subEvent.getSource() == this.pluginOnDetail )
		    {
			this.updateDetails(this.pluginOnDetail);
		    }
		}
	    }
	}
    }
    
    /** expand the path for an item in the given panel
     *	@param types an array of SibType defining the TreePath to expand
     *	@param panel a SibTypeListTreeTablePanel
     *	@param treeTableModel a TreeTableModel
     */
    private void expandPathRecursively(Object[] types, SibTypeListTreeTablePanel panel, TreeTableModel treeTableModel)
    {	
	TreePath path = new TreePath(types);

	panel.getTable().getTreeRenderer().expandPath(path);
	
	Object leaf = types[types.length - 1];
	int childCount = treeTableModel.getChildCount(leaf);
	
	if ( childCount > 0 )
	{
	    /* create a new array */
	    Object[] childPath = new Object[types.length + 1];
	    
	    System.arraycopy(types, 0, childPath, 0, types.length);
	    
	    /* for all child, expand too */
	    for(int i = 0; i < childCount; i++)
	    {
		Object child = treeTableModel.getChild(leaf, i);
		if ( child != null )
		{
		    childPath[childPath.length - 1] = child;
		    this.expandPathRecursively(childPath, panel, treeTableModel);
		}
	    }
	}
    }
}
