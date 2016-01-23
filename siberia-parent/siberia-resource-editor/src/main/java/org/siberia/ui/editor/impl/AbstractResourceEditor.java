/* 
 * Siberia resource editor : siberia plugin defining resource editor
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
package org.siberia.ui.editor.impl;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.siberia.kernel.Kernel;
import org.siberia.editor.AbstractEditor;
import org.siberia.kernel.resource.ColdResource;
import org.siberia.kernel.resource.ResourcePersistence;
import org.siberia.kernel.resource.ResourceVisualizationMode;
import org.siberia.kernel.resource.event.impl.ResourceLoadedEvent;
import org.siberia.type.SibType;
import org.siberia.editor.ResourceEditor;
import org.siberia.kernel.resource.event.ResourceEvent;
import org.siberia.kernel.resource.event.ResourceListener;
import org.siberia.kernel.resource.event.impl.ResourceModificationEvent;
import org.siberia.kernel.resource.event.impl.ResourceSavedEvent;
import org.siberia.ui.bar.PluginBarFactory;
import org.siberia.ui.swing.tree.PathExpansionMode;

/**
 *
 * Abstract resource editor
 *
 * @author alexis
 */
public abstract class AbstractResourceEditor extends AbstractEditor implements ResourceEditor,
									       PropertyChangeListener,
									       ResourceListener
{
    /** related resource */
    private ColdResource     resource         = null;
    
    /** ResourceListener */
    private ResourceListener resourceListener = null;
    
    /** toolbar allowing to manage resources */
    private JToolBar         toolbar          = null;
    
    /** main panel */
    private JPanel           panel            = null;
    
    /** Creates a new instance of ResourceEditor */
    public AbstractResourceEditor()
    {   }
    
    /** called when the mode of visualization of the resources changed
     *	@param mode then ew mode of visualization
     */
    protected abstract void visualizationModeChanged(ResourceVisualizationMode mode);

    @Override
    public void setInstance(SibType instance)
    {   super.setInstance(instance);
        if ( instance instanceof ColdResource )
        {   
            if ( this.resource != null )
            {   
		this.resource.removeResourceListener(this);
		
		this.resource.removePropertyChangeListener(ColdResource.PROPERTY_PERSISTENCE_TYPE, this);
		this.resource.removePropertyChangeListener(ColdResource.PROPERTY_LABEL, this);
		this.resource.removePropertyChangeListener(ColdResource.PROPERTY_VISUALIZATION_MODE, this);
	    }
            
            this.resource = (ColdResource)instance;
            
            if ( this.resource != null )
            {   
		this.resource.addResourceListener(this);
		
		this.resource.addPropertyChangeListener(ColdResource.PROPERTY_PERSISTENCE_TYPE, this);
		this.resource.addPropertyChangeListener(ColdResource.PROPERTY_LABEL, this);
		this.resource.addPropertyChangeListener(ColdResource.PROPERTY_VISUALIZATION_MODE, this);
	    }
	    
	    this.visualizationModeChanged( this.resource == null ? null : this.resource.getVisualizationMode() );
	    
	    this.updateEditorState( this.getInstance() == null ? null : this.getInstance().getPersistenceType(), false );
            
            this.setTitle(this.resource.getLabel());
        }
    }
    
    /** update the title
     *	this method has a parameter which represents the default title to apply but
     *	all editor are free to refuse this value if it consider that already has a valid title
     */
    @Override
    public void updateTitle(String defaultTitle)
    {
	String chosenTitle = defaultTitle;
	
	/** chose the label of the resource if not null */
	if ( this.getInstance() != null )
	{
	    String label = this.getInstance().getLabel();
	    
	    if ( label != null )
	    {
		label = label.trim();
		
		if ( label.length() > 0 )
		{
		    chosenTitle = label;
		}
	    }
	}
	
	this.setTitle(chosenTitle);
    }
    
    /** return the resources management toolbar
     *	@return a JToolbar
     */
    protected JToolBar getToolBar()
    {
	return this.toolbar;
    }
    
    /** return the component that display the resources
     *	@return a Component
     */
    protected abstract Component getResourcesComponent();

    public Component getComponent()
    {
	if ( this.panel == null )
	{
	    this.panel = new JPanel();
	    this.panel.setLayout(new GridBagLayout());
	    
	    GridBagConstraints gbc = new GridBagConstraints();
	    
	    PluginBarFactory barFactory = PluginBarFactory.getInstance();
	    this.toolbar = barFactory.createToolBar(new org.siberia.ui.bar.PluginToolBarProvider("resources.management.toolbar"));
	    this.toolbar.setFloatable(false);
	    
	    this.updateEditorState( this.getInstance() == null ? null : this.getInstance().getPersistenceType(), false );
	    
	    gbc.gridx   = 1;
	    gbc.gridy   = 1;
	    gbc.weightx = 1.0;
	    gbc.anchor  = GridBagConstraints.WEST;
	    gbc.fill    = GridBagConstraints.NONE;
	    
	    this.panel.add(this.toolbar, gbc);
	    
	    Component mainCmp = this.getResourcesComponent();
	    
	    this.visualizationModeChanged( this.getInstance() == null ? null : this.getInstance().getVisualizationMode() );
	    
	    if ( mainCmp != null )
	    {
		gbc.gridy   = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill    = GridBagConstraints.BOTH;
		
		this.panel.add(mainCmp, gbc);
	    }
	}
	
	return this.panel;
    }

    @Override
    public ColdResource getInstance()
    {   return this.resource; }

    @Override
    public void save()
    {   /* do nothing, let ResourceEvent do the job */ }
    
    /** method to update the graphical components in the editor
     *  according to the state of the relative associated entity
     */
    @Override
    public void load()
    {   /* do nothing, let ResourceEvent do the job */ }
    
    /** warn KernelResources that selection changed */
    protected void updateKRSelectedItems()
    {
	SibType[] selected = this.getSelectedItems();
	
	this.updateKRSelectedItems(selected);
    }
    
    /** return the items that are actually selected
     *	@return an Array of SibType
     */
    protected abstract SibType[] getSelectedItems();
    
    /** warn KernelResources that selection changed
     *	@param selected the object to consider as selected
     */
    protected void updateKRSelectedItems(SibType[] selected)
    {   
        if ( selected != null && selected.length > 0 )
        {   Kernel.getInstance().getResources().setSelectedElements(selected); }
	else
	{   Kernel.getInstance().getResources().setSelectedElements( (SibType[])null ); }
    }
    
    /** method called on the editor when it gained focus
     *  @param oppositeEditor the editor that lost focus
     *
     *  to overwrite to do additional actions when the editor gained focus
     *
     *  should not be called directly
     */
    @Override
    public void editorGainedFocus(org.siberia.editor.Editor oppositeEditor)
    {   
        super.editorGainedFocus(oppositeEditor);
        
        this.updateKRSelectedItems();
    }
    
    /** update state of the editor according to the persistence type given
     *	@param persistence a ResourcePersistence
     */
    protected void updateEditorState(ResourcePersistence persistence)
    {
	this.updateEditorState(persistence, true);
    }
    
    /** update state of the editor according to the persistence type given
     *	@param persistence a ResourcePersistence
     *	@param modifyModifiedState true to allow this method to modify the modified state of this editor
     */
    protected void updateEditorState(ResourcePersistence persistence, boolean modifyModifiedState)
    {
	boolean modified       = false;
	boolean toolbarVisible = true;
	if ( persistence == null )
	{
	    modified       = false;
	    toolbarVisible = false;
	}
	else
	{
	    if ( persistence.isDynamicallyPersistent() || persistence.equals(ResourcePersistence.NONE) )
	    {
		toolbarVisible = false;
		modified       = false;
	    }
	    else
	    {
		toolbarVisible = true;
		modified       = true;
	    }
	}
	
	if ( modifyModifiedState )
	{
	    this.setModified(modified);
	}
	if ( this.getToolBar() != null )
	{
	    this.getToolBar().setVisible(toolbarVisible);
	}
    }
    
    /** convert a ResourceVisualizationMode into a PathExpansionMode
     *	@param visuMode a ResourceVisualizationMode
     *	@return a PathExpansionMode
     */
    protected static PathExpansionMode convertVisualizationMode(ResourceVisualizationMode visuMode)
    {
	PathExpansionMode expansionMode = PathExpansionMode.NONE;
	
	if ( visuMode != null )
	{
	    if ( ResourceVisualizationMode.CLASSIC.equals(visuMode) )
	    {	expansionMode = PathExpansionMode.NONE; }
	    else if ( ResourceVisualizationMode.VISUALIZE_ADDED_ITEMS.equals(visuMode) )
	    {	expansionMode = PathExpansionMode.NODE_INSERTION; }
	    else if ( ResourceVisualizationMode.VISUALIZE_CHANGED_ITEMS.equals(visuMode) )
	    {	expansionMode = PathExpansionMode.NODE_CHANGE; }
	    else if ( ResourceVisualizationMode.VISUALIZE_ALL_ITEMS_AT_START.equals(visuMode) )
	    {	expansionMode = PathExpansionMode.MODEL_CHANGE; }
	    else if ( ResourceVisualizationMode.VISUALIZE_ADDED_AND_CHANGED_ITEMS.equals(visuMode) )
	    {	expansionMode = PathExpansionMode.NODE_INSERTION_AND_NODE_CHANGE; }
	    else if ( ResourceVisualizationMode.VISUALIZE_ALL_ITEMS_AT_START_AND_CHANGED_ITEMS.equals(visuMode) )
	    {	expansionMode = PathExpansionMode.MODEL_CHANGE_AND_NODE_CHANGE; }
	    else if ( ResourceVisualizationMode.VISUALIZE_ALL_ITEMS_AT_START_AND_ADDED_ITEMS.equals(visuMode) )
	    {	expansionMode = PathExpansionMode.MODEL_CHANGE_AND_NODE_INSERTION; }
	    else if ( ResourceVisualizationMode.VISUALIZE_ALL_ITEMS_AT_START_AND_CHANGE_ITEMS_AND_ADDED_ITEMS.equals(visuMode) )
	    {	expansionMode = PathExpansionMode.MODEL_CHANGE_AND_NODE_CHANGE_AND_NODE_INSERTION; }
	}
	
	return expansionMode;
    }
    
    /* #########################################################################
     * #################### ResourceListener implementation ####################
     * ######################################################################### */
    
    /** received a ResourceEvent from a listened ColdResource
     *  @param evt a ResourceEvent
     */
    public void resourceChanged(ResourceEvent e)
    {
	if ( this.getInstance() != null )
	{
	    if ( e instanceof ResourceModificationEvent )
	    {   
		if ( ! getInstance().getPersistenceType().isDynamicallyPersistent() )
		{
		    this.setModified(true);
		}
	    }
	    else if ( e instanceof ResourceSavedEvent )
	    {   this.setModified(false); }
	    else if ( e instanceof ResourceLoadedEvent ) // just loaded --> not modified !!
	    {   this.setModified(false); }
	}
    }
    
    /* #########################################################################
     * ################# PropertyChangeListener implementation #################
     * ######################################################################### */
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */

    public void propertyChange(PropertyChangeEvent evt)
    {
	if ( evt.getSource() == this.getInstance() )
	{
	    if ( ColdResource.PROPERTY_PERSISTENCE_TYPE.equals(evt.getPropertyName()) )
	    {
		if ( evt.getNewValue() == null )
		{
		    ResourcePersistence mode = null;
		    
		    if ( evt.getNewValue() instanceof ResourcePersistence )
		    {
			mode = (ResourcePersistence)evt.getNewValue();
		    }
		    this.updateEditorState( mode );
		}
	    }
	    else if ( ColdResource.PROPERTY_LABEL.equals(evt.getPropertyName()) )
	    {
		if ( evt.getNewValue() != null )
		{
		    this.setTitle((String)evt.getNewValue());
		}
	    }
	    else if ( ColdResource.PROPERTY_VISUALIZATION_MODE.equals(evt.getPropertyName()) &&
		      evt.getNewValue() instanceof ResourceVisualizationMode )
	    {
		this.visualizationModeChanged( (ResourceVisualizationMode)evt.getNewValue() );
	    }
	}
    }
    
}
