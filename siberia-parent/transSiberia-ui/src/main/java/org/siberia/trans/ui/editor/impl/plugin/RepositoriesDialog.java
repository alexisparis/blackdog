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
import java.awt.Window;
import java.beans.PropertyVetoException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeModel;
import org.apache.log4j.Logger;
import org.siberia.trans.TransSiberia;
import org.siberia.trans.exception.ConfigurationException;
import org.siberia.trans.exception.InvalidRepositoryException;
import org.siberia.trans.exception.RepositoryCreationException;
import org.siberia.trans.type.RepositoryUtilities;
import org.siberia.trans.type.repository.SiberiaRepositoryLocation;
import org.siberia.type.SibList;
import org.siberia.type.SibType;
import org.siberia.type.event.ContentChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeListener;
import org.siberia.ui.swing.dialog.ValidationDialog;
import org.siberia.ui.swing.tree.model.ConfigurableTreeModel;
import org.siberia.ui.swing.tree.model.MutableTreeModel;
import org.siberia.ui.swing.treetable.SibTypeListTreeTablePanel;
import org.siberia.ui.swing.treetable.introspection.IntrospectionSibTypeListTreeTableModel;
import org.siberia.ui.swing.treetable.introspection.PageablePathConverter;
import org.siberia.type.message.SibMessage;
import org.siberia.type.message.SibWaitingMessage;
import org.siberia.ui.swing.table.model.PropertyDeclaration;

/**
 *
 * Dialog that allow to manage Siberia repositories
 *
 * @author alexis
 */
public class RepositoriesDialog extends ValidationDialog implements HierarchicalPropertyChangeListener
{   
    /** logger */
    private Logger                    logger             = Logger.getLogger(RepositoriesDialog.class);
    
    /** url properties panel */
    private SibTypeListTreeTablePanel treeTablePanel     = null;
    
    /** item displayed in the table during repositories information collection */
    private SibMessage                waitingMessage     = null;
    
    /** Executor that do the repositories information collection */
    private ExecutorService           executorService    = null;
    
    /** current future */
    private Future                    repositoriesFuture = null;
    
    /** TransSiberia */
    private TransSiberia              transSiberia       = null;
    
    /** Creates a new instance of RepositoriesDialog
     *	@param window a Window
     *	@param transSiberia the TransSiberia to use
     */
    public RepositoriesDialog(Window window, TransSiberia transSiberia)
    {
	super(window, null);
	
	this.transSiberia = transSiberia;
	
	this.setModal(false);
	
	ResourceBundle rb = ResourceBundle.getBundle(RepositoriesDialog.class.getName());
	this.setTitle(rb.getString("title"));
	
	this.treeTablePanel = new SibTypeListTreeTablePanel();
	this.treeTablePanel.getTable(). setMaximumDisplayedRows(-1);
	this.treeTablePanel.setPreferredSize(new java.awt.Dimension(500, 300));
	
        CellConstraints cc = new CellConstraints();
        this.getContentPanel().add(this.treeTablePanel, cc.xy(1, 1));
    }

    @Override
    public void display()
    { 
	this.createGui();

	super.display();

	this.loadRepositories();
    }
    
    private void createGui()
    {
	ResourceBundle rb = ResourceBundle.getBundle(RepositoriesDialog.class.getName());
	
	IntrospectionSibTypeListTreeTableModel treeTableModel = new IntrospectionSibTypeListTreeTableModel();
	
	MutableTreeModel treeModel = new MutableTreeModel();
	
	this.waitingMessage = new SibWaitingMessage();
	
	try
	{   
	    this.waitingMessage.setName(rb.getString("waitingLabel"));
	    this.waitingMessage.setReadOnly(true);
	}
	catch (PropertyVetoException ex)
	{
	    ex.printStackTrace();
	}
	
	treeModel.setRoot(this.waitingMessage);
	treeTableModel.setInnerConfigurableTreeModel(treeModel);
	
	treeTableModel.getInnerTableModel().setSpecificAllowedClass(org.siberia.trans.type.repository.SiberiaRepositoryLocation.class);
	
	treeTableModel.getInnerTableModel().addPropertyDeclarations(
		new PropertyDeclaration(SiberiaRepositoryLocation.PROPERTY_LOCATION, true, false, 200));
	
	this.treeTablePanel.getTable().setTreeTableModel(treeTableModel);
//	this.treeTablePanel.getTable().setPreferredScrollableViewPortSize(new java.awt.Dimension(500, 300));
	
	treeTableModel.setPathConverter(new PageablePathConverter(this.treeTablePanel.getTable().getTreeRenderer()));
    }
    
    private void loadRepositories()
    {
	Runnable run = new Runnable()
	{
	    public void run()
	    {
		ResourceBundle rb = ResourceBundle.getBundle(RepositoriesDialog.class.getName());
		
		TransSiberia ts = transSiberia;

		SibList plugins = null;
		
		try
		{
		    plugins = ts.getRepositoryLocations(true);
		}
		catch (ConfigurationException ex)
		{
		    ex.printStackTrace();
		    
		    setVisible(false);
		    dispose();

		    JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(getOwner()),
						  rb.getString("configurationExceptionMessage"),
						  rb.getString("configurationExceptionTitle"),
						  JOptionPane.ERROR_MESSAGE);
		}
		
		final SibList plugins2 = plugins;

		try
		{   plugins2.setName(rb.getString("registeredRepositoriesLabel")); }
		catch (PropertyVetoException ex)
		{   ex.printStackTrace(); }

		plugins2.addHierarchicalPropertyChangeListener(RepositoriesDialog.this);

		final TreeModel model = treeTablePanel.getTable().getTreeRenderer().getModel();
		if ( model instanceof ConfigurableTreeModel )
		{
		    Runnable swingRun = new Runnable()
		    {
			public void run()
			{
			    ((ConfigurableTreeModel)model).setRoot(plugins2);
			}
		    };
		    
		    SwingUtilities.invokeLater(swingRun);
		}
	    }
	};
	
	if ( this.executorService == null )
	{
	    this.executorService = Executors.newSingleThreadExecutor();
	}
	
	this.repositoriesFuture = this.executorService.submit(run);
    }

    @Override
    public void valid()
    {
	super.valid();
	
	TransSiberia ts = this.transSiberia;
		
	try
	{
	    
	    ts.setRepositoryLocations( (SibList)( ((IntrospectionSibTypeListTreeTableModel)treeTablePanel.getTable().getTreeTableModel()).
		    getInnerConfigurableTreeModel().getSibTypeRoot() ) );
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void setVisible(boolean b)
    {
	super.setVisible(b);
	
	if ( ! b )
	{
	    Object root = ((IntrospectionSibTypeListTreeTableModel)this.treeTablePanel.getTable().getTreeTableModel()).getInnerConfigurableTreeModel().getSibTypeRoot();

	    if ( root instanceof SibType )
	    {
		((SibType)root).removeHierarchicalPropertyChangeListener(this);
	    }
	    
	    if ( this.repositoriesFuture != null )
	    {
		if ( ! this.repositoriesFuture.isDone() )
		{
		    this.repositoriesFuture.cancel(true);
		}
	    }
	    
	    this.executorService.shutdown();
	    this.executorService = null;
	    
	    this.repositoriesFuture = null;
	}
    }
    
    /** called to fullfill the name of a SiberiaRepositoryLocation
     *	this method could display a message dialog if the repository is wrong
     *	@param location a SiberiaRepositoryLocation
     */
    private void fullfillRepositoryName(SiberiaRepositoryLocation location)
    {
	String name = null;
	
	boolean validRepository = true;
	try
	{
	    try
	    {
		name = RepositoryUtilities.getNameWithException( location );
	    }
	    catch(InvalidRepositoryException e)
	    {
		logger.error("invalid repository exception", e);
		validRepository = false;
	    }
	    catch(RepositoryCreationException e)
	    {
		logger.error("repository creation exception", e);
		validRepository = false;
	    }
	    
	    location.setName(name);
	}
	catch(PropertyVetoException e)
	{
	    logger.error("", e);
	}
	
	if ( ! validRepository )
	{	    
	    JOptionPane.showMessageDialog(this.getOwner(), "'" + location.getURL() +
					  "' does not seem to be a valid siberia repository", "error", JOptionPane.ERROR_MESSAGE);
	}
    }
    
    /* #########################################################################
     * ########### HierarchicalPropertyChangeListener implementation ###########
     * ######################################################################### */

    /**
     * called when a HierarchicalPropertyChangeEvent was throwed by
     *  an object listener by this one
     * 
     * @param event a HierarchycalPropertyChangeEvent
     */
    public void propertyChange(HierarchicalPropertyChangeEvent event)
    {
	/** if the location of a repository changed, then
	 *  try to initialize the name and if InvalidRepositoryException is throwed
	 *  then provide a message that explain that the location of therepository seems to be wrong
	 */
	if ( event.getOriginSource() instanceof SiberiaRepositoryLocation )
	{
	    if ( SiberiaRepositoryLocation.PROPERTY_LOCATION.equals(event.getPropertyChangeEvent().getPropertyName()) )
	    {
		this.fullfillRepositoryName( (SiberiaRepositoryLocation)event.getOriginSource() );
	    }
	}
	else if ( event.getOriginSource() instanceof SibList )
	{
	    /** search for a new Location added to the list */
	    if ( SibList.PROPERTY_CONTENT.equals(event.getPropertyChangeEvent().getPropertyName()) )
	    {
		if ( event.getPropertyChangeEvent() instanceof ContentChangeEvent )
		{
		    ContentChangeEvent contentEvent = (ContentChangeEvent)event.getPropertyChangeEvent();
		    
		    if ( contentEvent.getMode() == ContentChangeEvent.ADD )
		    {
			SibType[] items = contentEvent.getObject();
			
			for(int i = 0; i < items.length; i++)
			{
			    SibType current = items[i];
			    
			    if ( current instanceof SiberiaRepositoryLocation )
			    {
				this.fullfillRepositoryName( (SiberiaRepositoryLocation)current );
			    }
			}
		    }
		}
	    }
	}
    }
    
}
