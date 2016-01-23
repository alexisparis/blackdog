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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.blackdog.type.AudioItem;
import org.blackdog.type.Playable;
import org.blackdog.type.RadioList;
import org.blackdog.type.RatedItem;
import org.blackdog.type.SongItem;
import org.blackdog.type.RadioItem;
import org.blackdog.type.PlayList;
import org.blackdog.type.CategorizedItem;
import org.blackdog.type.session.PlayableSession;
import org.siberia.editor.AbstractEditor;
import org.siberia.editor.annotation.Editor;
import org.siberia.editor.launch.EditorLaunchContext;
import org.siberia.kernel.Kernel;
import org.siberia.kernel.editor.NoLauncherFoundException;
import org.siberia.properties.PropertiesManager;
import org.siberia.type.SibCollection;
import org.siberia.type.SibType;
import org.siberia.ui.action.impl.DefaultTypeEditingAction;
import org.siberia.ui.swing.table.SibListTablePanel;
import org.siberia.ui.swing.table.model.IntrospectionSibTypeListTableModel;
import org.siberia.editor.launch.DefaultEditorLaunchContext;
import org.siberia.ui.swing.table.model.SibTypeListTableModel;
import org.siberia.ui.swing.table.model.PropertyDeclaration;
import org.siberia.ui.action.impl.AddingTypeAction;
import org.blackdog.type.TimeBasedItem;

/**
 *
 * Playlist editor
 *
 * @author alexis
 */
@Editor(relatedClasses={org.blackdog.type.RadioList.class},
                  description="Radiolist editor",
                  name="Radiolist editor",
                  launchedInstancesMaximum=-1)
public class RadioListEditor extends AbstractEditor
{
    /** logger */
    private Logger              logger    = Logger.getLogger(RadioListEditor.class);
    
    /** PluginManagerPanel */
    private RadioListEditorPanel panel     = null;
    
    /** create a new PlayListEditor */
    public RadioListEditor()
    {
	super();
    }
    
    /** set the SibType instance associated with the editor
     *  @param instance instance of SibType
     **/
    @Override
    public void setInstance(SibType instance)
    {   
        if ( instance != null && ! (instance instanceof RadioList ) )
            throw new IllegalArgumentException("UpdateManager editor only support instance of " + RadioList.class);
        
        super.setInstance(instance);
	
	if ( this.panel != null )
	{
	    SibListTablePanel pan = this.panel.getTablePanel();
	    
	    if ( pan != null )
	    {
		JTable table = pan.getTable();
		
		if ( table != null && table.getModel() instanceof IntrospectionSibTypeListTableModel )
		{
		    ((IntrospectionSibTypeListTableModel)table.getModel()).setList( (RadioList)this.getInstance() );
		}
	    }
	}
        
        this.refreshProperties();
    }
    
    /** upadte the editor according to properties */
    public void refreshProperties()
    {   
	boolean searchEnabled = false;
	
        Object o = PropertiesManager.getGeneralProperty("radiolist.configuration.filter.searchEnabled");
        if ( o instanceof Boolean )
        {
            searchEnabled = ((Boolean)o).booleanValue();
        }
        
        if ( this.panel != null )
        {   
            this.panel.setSearchEnabled(searchEnabled);
        }
    }
    
    /** launch edition of the selected items in the table */
    private void launchEditionOfItem(SibType item)
    {
	if ( item instanceof RadioItem )
	{   
	    EditorLaunchContext context = new SongEditorLaunchContext(item, null);

	    if ( context != null )
	    {
		try
		{   Kernel.getInstance().getResources().edit(context); }
		catch (NoLauncherFoundException ex)
		{   ex.printStackTrace(); }
	    }
	}
    }
    
    /** return the component that render the editor
     *  @return a Component
     */
    public Component getComponent()
    {   if ( this.panel == null )
        {   
	    this.panel = new RadioListEditorPanel();
	    
	    /* initialize model */
            final IntrospectionSibTypeListTableModel model = new IntrospectionSibTypeListTableModel();
            model.setEditable(false);
            
            model.setList( (RadioList)this.getInstance() );
	
	    model.addPropertyDeclarations(
		new PropertyDeclaration(AudioItem.PROPERTY_NAME, true, false, 180),
		new PropertyDeclaration(AudioItem.PROPERTY_CATEGORY, true, true, 80),
		new PropertyDeclaration(AudioItem.PROPERTY_RATE, true, true, 80),
		new PropertyDeclaration(AudioItem.PROPERTY_CREATION_DATE, false, true, 80),
		new PropertyDeclaration(AudioItem.PROPERTY_DATE_LAST_PLAYED, true, true, 80),
		new PropertyDeclaration(AudioItem.PROPERTY_PLAYED_COUNT, false, true, 80),
		new PropertyDeclaration(AudioItem.PROPERTY_VALUE, true, true, 180));
            
            this.panel.getTablePanel().setModel(model);
	    
	    this.panel.getTablePanel().getTable().setExternalController(
		new org.siberia.ui.swing.table.controller.TableController()
		{
		    /** return the number of mouse click that have to be done to start edition on the given row and column
		     *	@param table a JTable
		     *	@param row the index of the row
		     *	@param column the index of the column
		     *	@return the number of click that have to be done to start edition on the given row and column
		     */
		    public int getClickCountToStartEditingAt(JTable table, int row, int column)
		    {
			/** search for the property displayed at the given column */
			return 2;
		    }
		});
            
            this.refreshProperties();
            
//            this.panel.getTablePanel().getTable().setHorizontalScrollEnabled(true);
	    
            this.panel.getTablePanel().getTable().addKeyListener(new KeyAdapter()
	    {
		@Override
		public void keyPressed(KeyEvent e)
		{
		    if ( e.getKeyCode() == KeyEvent.VK_ENTER )
		    {
			SibType item = null;

			int rowIndex = panel.getTablePanel().getTable().getSelectedRow();

			if ( rowIndex >= 0 )
			{   /* get the item at the given row */
			    Object o = panel.getTablePanel().getTable().getItem(rowIndex);
			    
			    if ( o instanceof SibType )
			    {
				item = (SibType)o;
			    }
			    
			    e.consume();
			}

			if ( item != null )
			{
			    launchEditionOfItem(item);
			}
		    }
		}
	    });
            
            this.panel.getTablePanel().getTable().addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    if ( e.getSource() == panel.getTablePanel().getTable() )
                    {   
			boolean editItem = false;
                        if ( SwingUtilities.isLeftMouseButton(e) )
                        {   if ( e.getClickCount() >= 2 )
                            {   editItem = true; }
                        }
                        else if ( SwingUtilities.isMiddleMouseButton(e) )
                        {   if ( e.getClickCount() >= 1 )
                            {   editItem = true; }
                        }
                        if ( editItem )
                        {   
                            SibType item = null;
                            
                            int rowIndex = panel.getTablePanel().getTable().rowAtPoint(e.getPoint());
			    
                            if ( rowIndex >= 0 && rowIndex < panel.getTablePanel().getTable().getRowCount() )
                            {   
				/* get the item at the given row */
				Object o = panel.getTablePanel().getTable().getItem(rowIndex);
				
				if ( o instanceof SibType )
				{
				    item = (SibType)o;
				}
                            }
			    
			    if ( item != null )
			    {
				launchEditionOfItem(item);
			    }
                        }
                    }
                }
            });
            
            this.panel.getTablePanel().getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener()
            {
                public void valueChanged(ListSelectionEvent e)
                {   
		    if ( ! e.getValueIsAdjusting() )
                    {   updateKRSelectedItems(); }
                }
            });
        }
        
        return this.panel;
    }
    
    /** warn KernelResources that selection changed */
    private void updateKRSelectedItems()
    {   
	org.siberia.ui.swing.table.SibTypeListTable table = this.panel.getTablePanel().getTable();
	
	Object[] objects = table.getSelectedObjects();
	
	SibType[] types = null;
	
	if ( objects != null && objects.length > 0 )
	{
	    List<SibType> sibTypes = new ArrayList<SibType>(objects.length);
	    
	    for(int i = 0; i < objects.length; i++)
	    {
		Object current = objects[i];
		
		if ( current instanceof SibType )
		{
		    sibTypes.add( (SibType)current );
		}
	    }
	    
	    types = (SibType[])sibTypes.toArray(new SibType[sibTypes.size()]);
	}
	
	Kernel.getInstance().getResources().setSelectedElements(types);
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
}
