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
package org.siberia.trans.ui.editor.impl;

import java.awt.Component;
import java.util.Collections;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import org.siberia.editor.AbstractEditor;
import org.siberia.editor.annotation.Editor;
import org.siberia.env.PluginResources;
import org.siberia.env.PluginResourcesEvent;
import org.siberia.env.PluginResourcesListener;
import org.siberia.trans.TransSiberia;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.type.SibList;
import org.siberia.type.SibType;
import org.siberia.ui.swing.table.SibTypeListTable;
import org.siberia.ui.swing.table.model.IntrospectionSibTypeListTableModel;
import org.siberia.trans.type.TransSiberianContext;
import org.siberia.ui.swing.table.model.PropertyDeclaration;

/**
 *
 * Update manager editor
 *
 * @author alexis
 */
@Editor(relatedClasses={TransSiberianContext.class},
                  description="Plugins manager",
                  name="Plugins Manager",
                  launchedInstancesMaximum=-1)
public class PluginManagerEditor extends AbstractEditor implements PluginResourcesListener
{
    /** PluginManagerPanel */
    private PluginManagerPanel panel        = null;
    
    /** create a new PluginManagerEditor */
    public PluginManagerEditor()
    {
	super();
	
	PluginResources.getInstance().addPluginResourcesListener(this);
    }
    
    /* #########################################################################
     * ################# PluginResourcesListener implementation ################
     * ######################################################################### */
    
    /** indicate to a listener that a modification has been made in the plugin context
     *	@param evt a PluginResourcesEvent describing the modification
     */
    public void pluginContextChanged(PluginResourcesEvent evt)
    {
	// TODO
    }

    @Override
    public void dispose()
    {
	super.dispose();
	
	PluginResources.getInstance().removePluginResourcesListener(this);
    }
    
    /** set the SibType instance associated with the editor
     *  @param instance instance of SibType
     **/
    @Override
    public void setInstance(SibType instance)
    {   
        if ( instance != null && ! (instance instanceof TransSiberianContext ) )
            throw new IllegalArgumentException("UpdateManager editor only support instance of " + TransSiberianContext.class);
        
        super.setInstance(instance);
	
	TransSiberianContext context = this.getTransSiberianContext();

	IntrospectionSibTypeListTableModel model = (IntrospectionSibTypeListTableModel)this.getComponent().getTablePanel().getTable().getModel();	
	model.setEditable(false);
	model.setList(context);
	
	this.getComponent().getTablePanel().getTable().setAutomaticContextMenuEnabled(false);
	
	this.getComponent().setTransSiberian( (context == null ? null : context.getTransSiberia()) );
	    
	this.panel.setPluginCount( (context == null ? 0 : context.size()) );
    }
    
    /** return the object TransSiberia context that will be used to get information about modules
     *  @return a TransSiberianContext
     */
    private TransSiberianContext getTransSiberianContext()
    {   
	return (TransSiberianContext)this.getInstance();
    }
    
    /** return the component that render the editor
     *  @return a PluginManagerPanel
     */
    public PluginManagerPanel getComponent()
    {   if ( this.panel == null )
        {   this.panel = new PluginManagerPanel();
	    
	    final IntrospectionSibTypeListTableModel<PluginBuild> model = new IntrospectionSibTypeListTableModel<PluginBuild>();
            
	    model.addPropertyDeclarations(
		new PropertyDeclaration(SibType.PROPERTY_NAME, true, false, 280),
		new PropertyDeclaration(PluginBuild.PROPERTY_LICENSE_NAME, true, true, 80),
		new PropertyDeclaration(PluginBuild.PROPERTY_VERSION, true, true, 80),
//		new PropertyDeclaration(PluginBuild.PROPERTY_RELEASE_DATE, true, true, 60), // no renderer for calendar yet
		new PropertyDeclaration(PluginBuild.PROPERTY_CHECKTYPE, false, true, 100)
		    );
            
            this.panel.getTablePanel().setModel(model);
            
            this.panel.getTablePanel();
            this.panel.getTablePanel().setMaximumDisplayedRows(-1);
            
            this.panel.getTablePanel().getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener()
            {
                public void valueChanged(ListSelectionEvent e)
                {
                    if ( ! e.getValueIsAdjusting() )
                    {   
                        if ( e.getSource() == panel.getTablePanel().getTable().getSelectionModel() )
                        {   
                            String description = null;
                            
                            ListSelectionModel selectionModel = panel.getTablePanel().getTable().getSelectionModel();
                            if ( selectionModel.getMaxSelectionIndex() == selectionModel.getMinSelectionIndex() )
                            {   
                                JTable table = panel.getTablePanel().getTable();
                                
                                if ( table instanceof SibTypeListTable )
                                {
                                    Object o = ((SibTypeListTable)table).getItem(selectionModel.getMaxSelectionIndex());
                                    
                                    if ( o instanceof PluginBuild )
                                    {
                                        description = ((PluginBuild)o).getShortDescription();
                                    }
                                }
                            }
                            
                            panel.getExplanationTextPane().setText(description);
                        }
                    }
                }
            });
        }
        
        return this.panel;
    }
}
