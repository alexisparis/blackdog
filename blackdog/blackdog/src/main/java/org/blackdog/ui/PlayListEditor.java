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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.blackdog.kernel.MusikKernelResources;
import org.blackdog.type.Playable;
import org.blackdog.type.RatedItem;
import org.blackdog.type.SongItem;
import org.blackdog.type.PlayList;
import org.blackdog.type.CategorizedItem;
import org.blackdog.type.session.PlayListEditorPlayableSession;
import org.blackdog.type.session.PlayableSession;
import org.siberia.ui.swing.dialog.ExtendedSwingWorker;
import org.siberia.ui.swing.dialog.SwingWorkerDialog;
import org.siberia.ui.swing.table.SibTypeListTable;
import org.siberia.ui.swing.transfer.SibTypeTransferHandler;
import org.siberia.ResourceLoader;
import org.siberia.base.LangUtilities;
import org.siberia.editor.AbstractEditor;
import org.siberia.editor.annotation.Editor;
import org.siberia.editor.launch.EditorLaunchContext;
import org.siberia.kernel.Kernel;
import org.siberia.kernel.editor.NoLauncherFoundException;
import org.siberia.properties.PropertiesManager;
import org.siberia.type.Namable;
import org.siberia.type.SibCollection;
import org.siberia.type.SibType;
import org.siberia.type.event.HierarchicalPropertyChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeListener;
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
@Editor(relatedClasses={org.blackdog.type.PlayList.class},
                  description="Playlist editor",
                  name="Playlist editor",
                  launchedInstancesMaximum=-1)
public class PlayListEditor extends AbstractEditor implements ActionListener
{
    /** logger */
    private Logger			       logger               = Logger.getLogger(PlayListEditor.class);
    
    /** PluginManagerPanel */
    private PlayListEditorPanel		       panel		    = null;
    
    /** listener on playlist list */
    private HierarchicalPropertyChangeListener playlistlistListener = null;
    
    /** create a new PlayListEditor */
    public PlayListEditor()
    {
	super();
	
	this.playlistlistListener = new HierarchicalPropertyChangeListener()
	{
	    public void propertyChange(HierarchicalPropertyChangeEvent event)
	    {
		if ( (event.getOriginSource() == MusikKernelResources.getInstance().getAudioResources().getPlayListList() &&
		      SibCollection.PROPERTY_CONTENT.equals(event.getPropertyChangeEvent().getPropertyName())) ||
		     (event.getOriginSource() != null && 
		      event.getOriginSource().getParent() == MusikKernelResources.getInstance().getAudioResources().getPlayListList() &&
		      Namable.PROPERTY_NAME.equals(event.getPropertyChangeEvent().getPropertyName()))
		   )
		{
		    applyModelToCopytoCombo();
		}
	    }
	};
	
	/** add it as listener on playlist list to update the 'copy to' combo */
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("adding listener on playlist list");
	}
	MusikKernelResources.getInstance().getAudioResources().getPlayListList().addHierarchicalPropertyChangeListener(this.playlistlistListener);
    }
    
    /** return the SibTypeListTable used by this editor
     *	@return a SibTypeListTable
     */
    public SibTypeListTable getTable()
    {
	SibTypeListTable table = null;
	
	if ( this.panel != null )
	{
	    if ( this.panel.getTablePanel() != null )
	    {
		table = this.panel.getTablePanel().getTable();
	    }
	}
	
	return table;
    }
    
    /** method that is called when an editor is being removed from registry to be used again */
    @Override
    public void removedFromCache()
    {
	super.removedFromCache();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("adding listener on playlist list");
	}
	MusikKernelResources.getInstance().getAudioResources().getPlayListList().addHierarchicalPropertyChangeListener(this.playlistlistListener);
	
	/** if editor was in cache, it does not listen anymore on the modifications of the playlist list
	 *  so force the copy combo to update its model
	 */
	this.applyModelToCopytoCombo();
    }
    
    /** method called to close the editor */
    @Override
    public void close()
    {   
	super.close();
	
	/* clear filter */
	if ( this.panel != null )
	{
	    this.panel.removeFiltersAndSort();
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("removing listener on playlist list");
	}
	MusikKernelResources.getInstance().getAudioResources().getPlayListList().removeHierarchicalPropertyChangeListener(this.playlistlistListener);
    }
    
    /** set the SibType instance associated with the editor
     *  @param instance instance of SibType
     **/
    @Override
    public void setInstance(SibType instance)
    {   
        if ( instance != null && ! (instance instanceof PlayList ) )
            throw new IllegalArgumentException("UpdateManager editor only support instance of " + PlayList.class);
        
        super.setInstance(instance);
	
	if ( this.panel != null )
	{
	    SibListTablePanel pan = this.panel.getTablePanel();
	    
	    if ( pan != null )
	    {
		JTable table = pan.getTable();
		
		if ( table != null && table.getModel() instanceof IntrospectionSibTypeListTableModel )
		{
		    ((IntrospectionSibTypeListTableModel)table.getModel()).setList( (PlayList)this.getInstance() );
		}
	    }
	}
        
        this.refreshProperties();
        
        /** in case the editor was in cache */
        this.updateArtistAlbumFiltersVisibility();
    }
    
    /** upadte the editor according to properties */
    public void refreshProperties()
    {   
	boolean searchEnabled = false;
	
        Object o = PropertiesManager.getGeneralProperty("playlist.configuration.filter.searchEnabled");
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
	if ( item != null )
	{   
	    EditorLaunchContext context = null;

	    if ( getInstance() instanceof PlayList && item instanceof Playable )
	    {
		/** create a PlayableSession relative to the table */
		PlayableSession session = ((PlayList)getInstance()).getCurrentSession();
		
		if ( (session instanceof PlayListEditorPlayableSession) )
		{
		    /** attach current editor to the session	
		     *	when the previous editor (if so) was closed the session was detached from editor
		     */
		    ((PlayListEditorPlayableSession)session).setPlayListEditor(this);
		}
		else
		{
		    PlayList playlist = null;
		    
		    SibType type = this.getInstance();
		    if ( type instanceof PlayList )
		    {
			playlist = (PlayList)type;
		    }
		    
		    session = new PlayListEditorPlayableSession(playlist,this);
		    ((PlayList)getInstance()).setCurrentSession(session);
		}
		
		/* to force current playable change */
		session.setCurrentPlayable( null );
		session.setCurrentPlayable( (Playable)item );
		
		context = new SongEditorLaunchContext(session, (PlayList)getInstance());
	    }
	    else
	    {
		context = new DefaultEditorLaunchContext(item);
	    }

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
        {   this.panel = new PlayListEditorPanel();
	    
	    this.panel.getTablePanel().getTable().setTransferHandler(new SibTypeTransferHandler());
	    
	    boolean enabled = ((JTable)this.panel.getTablePanel().getTable()).getSelectedRowCount() > 0;
//	    this.panel.getCopyToCombo().setEnabled(enabled);
	    this.panel.getCopyToButton().setEnabled(enabled);
	    
	    ((JTable)this.panel.getTablePanel().getTable()).getSelectionModel().addListSelectionListener(new ListSelectionListener()
	    {
		public void valueChanged(ListSelectionEvent e)
		{
		    boolean enabled = ((JTable)panel.getTablePanel().getTable()).getSelectedRowCount() > 0;
//		    panel.getCopyToCombo().setEnabled(enabled);
		    panel.getCopyToButton().setEnabled(enabled);
		}
	    });
	    
	    // TODO : integrate highlighter
//	    HighlighterPipeline highlighters = new HighlighterPipeline();
//highlighters.addHighlighter(new AlternateRowHighlighter());
//table.setHighlighters(highlighters);
	    
	    this.applyModelToCopytoCombo();
	    
	    this.panel.getCopyToButton().addActionListener(this);
	    
	    /* initialize model */
            final IntrospectionSibTypeListTableModel model = new IntrospectionSibTypeListTableModel();
            model.setEditable(false);
            
            model.setList( (PlayList)this.getInstance() );
	
	    model.addPropertyDeclarations(
		new PropertyDeclaration(SongItem.PROPERTY_TITLE, true, false, 180),
		new PropertyDeclaration(CategorizedItem.PROPERTY_CATEGORY, true, true, 80),
		new PropertyDeclaration(SongItem.PROPERTY_ARTIST, true, true, 140),
		new PropertyDeclaration(SongItem.PROPERTY_ALBUM, true, true, 140),
		new PropertyDeclaration(SongItem.PROPERTY_TRACK_NUMBER, true, true, 50),
		new PropertyDeclaration(SongItem.PROPERTY_AUTHOR, true, true, 180),
		new PropertyDeclaration(TimeBasedItem.PROPERTY_DURATION, false, true, 50),
		new PropertyDeclaration(RatedItem.PROPERTY_RATE, true, true, 90),
		
		/* hidden */
		new PropertyDeclaration(TimeBasedItem.PROPERTY_CREATION_DATE, false, true, 90),
		new PropertyDeclaration(SongItem.PROPERTY_COMMENT, false, true, 180),
		new PropertyDeclaration(Playable.PROPERTY_DATE_LAST_PLAYED, false, true, 90),
		new PropertyDeclaration(SongItem.PROPERTY_LEAD_ARTIST, false, true, 140),
		new PropertyDeclaration(Playable.PROPERTY_PLAYED_COUNT, false, true, 90),
//		new PropertyDeclaration(SongItem.PROPERTY_BITRATE, false, true, 90),
		new PropertyDeclaration(SongItem.PROPERTY_YEAR_RELEASED, false, true, 90)
		);
	    
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
            
            this.updateArtistAlbumFiltersVisibility();
        }
        
        return this.panel;
    }
    
    /** method to update the visibility of the artist/album filter according to properties */
    private void updateArtistAlbumFiltersVisibility()
    {
        if ( this.panel != null )
        {
            boolean authorAlbumFilterVisible = false;

            Object o = PropertiesManager.getGeneralProperty("playlist.configuration.filter.artistAlbumFilterVisible");
            if ( o instanceof Boolean )
            {
                authorAlbumFilterVisible = ((Boolean)o).booleanValue();
            }
            this.panel.setArtistAlbumFilterVisible(authorAlbumFilterVisible);
        }
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
    
    /** create a ComboBoxModel for the 'copy to' combo
     *	@return a ComboBoxModel
     */
    private ComboBoxModel createCopyToComboModel()
    {
	DefaultComboBoxModel model = new DefaultComboBoxModel();
	
	List playlists = MusikKernelResources.getInstance().getAudioResources().getPlayListList();
	
	if ( playlists != null )
	{
	    List<PlayList> list = null;
	    
	    ListIterator it = playlists.listIterator();
	    
	    while(it.hasNext())
	    {
		Object current = it.next();
		
		if ( current instanceof PlayList )
		{
		    if ( current != this.getInstance() && current != MusikKernelResources.getInstance().getAudioResources().getPlayListLibrary() )
		    {
			if ( list == null )
			{
			    list = new ArrayList<PlayList>(playlists.size());
			}
			list.add((PlayList)current);
		    }
		}
	    }
	    
	    if ( list != null && list.size() > 0 )
	    {
		/* sort list according to name */
		Collections.sort(list, new Comparator<PlayList>()
		{
		    public int compare(PlayList o1, PlayList o2)
		    {
			int result = 0;
			
			if ( o1 == null )
			{
			    if ( o2 != null )
			    {
				result = -1;
			    }
			}
			else
			{
			    if ( o2 == null )
			    {
				result = 1;
			    }
			    else
			    {
				result = LangUtilities.compare(o1.getName(), o2.getName());
			    }
			}
			
			return result;
		    }
		});
		
		for(int i = 0; i < list.size(); i++)
		{
		    model.addElement(list.get(i));
		}
	    }
	    
	    ResourceBundle rb = ResourceBundle.getBundle(this.getClass().getName());
	    
	    model.addElement(rb.getString("copyToCombo.createPlayList.label"));
	}
	
	return model;
    }
    
    /** apply a new model to the 'copy to' combo */
    private void applyModelToCopytoCombo()
    {
	Runnable runnable = new Runnable()
	{
	    public void run()
	    {
		/** re-create the combo box model and assign it */
		if ( panel != null )
		{
		    JComboBox box = panel.getCopyToCombo();

		    if ( box != null )
		    {
			box.setModel(createCopyToComboModel());

			if ( box.getModel().getSize() > 0 )
			{
			    box.setSelectedIndex(0);
			}
		    }
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
    
    /** copy some items to a given playlists
     *	@param items an array of items to copy to the playlist
     *	@param playlist the playlist where to copy the previous items
     */
    public void copyToPlayList(Object[] items, PlayList playlist)
    {
	if ( playlist != null && items != null )
	{
	    for(int i = 0; i < items.length; i++)
	    {
		Object current = items[i];
		
		if ( ! playlist.contains(current) )
		{
		    playlist.add(current);
		}
	    }
	}
    }
    
    /* #########################################################################
     * #################### ActionListener implementation ######################
     * ######################################################################### */
    
    public void actionPerformed(ActionEvent evt)
    {
	if ( this.panel != null && evt.getSource() == this.panel.getCopyToButton() )
	{
	    PlayList choosenPlaylist = null;
	    
	    if ( this.panel.getCopyToCombo().getSelectedIndex() == this.panel.getCopyToCombo().getModel().getSize() - 1 )
	    {
		/* create a new PlayList */
		AddingTypeAction addingAction = new AddingTypeAction();
		addingAction.setTypes(new SibType[]{MusikKernelResources.getInstance().getAudioResources().getPlayListList()});
		
		addingAction.actionPerformed(evt);
		
		SibType newType = addingAction.getNewlyCreatedItem();
		
		if ( newType instanceof PlayList )
		{
		    choosenPlaylist = (PlayList)newType;
		}
	    }
	    else if ( this.panel.getCopyToCombo().getSelectedItem() instanceof PlayList )
	    {
		choosenPlaylist = (PlayList)this.panel.getCopyToCombo().getSelectedItem();
	    }
	    
	    if ( choosenPlaylist != null)
	    {
		final PlayList _choosenPlaylist = choosenPlaylist;
		
		final SwingWorkerDialog dialog = new SwingWorkerDialog(SwingUtilities.getWindowAncestor(this.panel.getCopyToButton()), true);

		dialog.setDifferWorkerExecutionEnabled(false);

		SwingWorker worker = new ExtendedSwingWorker()
		{
		    protected Object doInBackground() throws Exception
		    {
			try
			{
			    copyToPlayList(panel.getTablePanel().getTable().getSelectedObjects(), _choosenPlaylist);
			}
			catch(Exception e)
			{
			    logger.error("got error while removing items", e);
			}
			finally
			{
			    this.setProgress(100);
			}

			return null;
		    }
		};
		dialog.setWorker(worker);
		dialog.getProgressBar().setIndeterminate(true);
		ResourceBundle rb = ResourceBundle.getBundle(PlayListEditor.class.getName());
		dialog.getLabel().setText(rb.getString("dialog.copy.label"));
		dialog.setTitle(rb.getString("dialog.copy.title"));
		dialog.display();
	    }
	}
    }
}
