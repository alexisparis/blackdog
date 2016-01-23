/*
 * aTunes 1.8.2
 * Copyright (C) 2006-2008 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/?page_id=7 for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
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
 */

package net.sourceforge.atunes.kernel.controllers.navigation;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.gui.views.Renderers;
import net.sourceforge.atunes.gui.views.dialogs.AlbumToolTip;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.model.PanelController;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationControllerState.ViewMode;
import net.sourceforge.atunes.kernel.controllers.navigation.listeners.NavigationMenusActionListener;
import net.sourceforge.atunes.kernel.controllers.navigation.listeners.NavigationTabbedPaneChangeListener;
import net.sourceforge.atunes.kernel.controllers.navigation.listeners.NavigationTableControlsActionListener;
import net.sourceforge.atunes.kernel.controllers.navigation.listeners.NavigationTableMouseListener;
import net.sourceforge.atunes.kernel.controllers.navigation.listeners.NavigationTreeControlsActionListener;
import net.sourceforge.atunes.kernel.controllers.navigation.listeners.NavigationTreeMouseListener;
import net.sourceforge.atunes.kernel.controllers.navigation.listeners.NavigationTreeSelectionListener;
import net.sourceforge.atunes.kernel.controllers.navigation.listeners.NavigationTreeToolTipListener;
import net.sourceforge.atunes.kernel.handlers.DeviceHandler;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.handlers.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.handlers.RadioHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler.SortType;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.Genre;
import net.sourceforge.atunes.utils.log4j.LogCategories;

/**
 * @author fleax
 * 
 */
public class NavigationController extends PanelController<NavigationPanel> {

	NavigationControllerState state;
	private NodeToSongsTranslator nodeTranslator;

	private Object lastAlbumToolTipContent;
	private AlbumToolTip albumToolTip;

	public NavigationController(NavigationPanel panel) {
		super(panel);
		state = new NavigationControllerState();
		nodeTranslator = new NodeToSongsTranslator(state);
		addBindings();
		addStateBindings();
	}

	@Override
	protected void addBindings() {
		// Keys
		panelControlled.getNonFavoriteAddToPlaylistMenuItem().setAccelerator(KeyStroke.getKeyStroke('+'));
		panelControlled.getNonFavoriteSetAsPlaylistMenuItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		// End keys

		panelControlled.getNavigationTable().setModel(new NavigationTableModel(this));
		//adjustColumnsWidth();

		NavigationTreeSelectionListener treeSelectionListener = new NavigationTreeSelectionListener(this, panelControlled);
		panelControlled.getNavigationTree().addTreeSelectionListener(treeSelectionListener);
		panelControlled.getFileNavigationTree().addTreeSelectionListener(treeSelectionListener);
		panelControlled.getFavoritesTree().addTreeSelectionListener(treeSelectionListener);
		panelControlled.getDeviceTree().addTreeSelectionListener(treeSelectionListener);
		panelControlled.getRadioTree().addTreeSelectionListener(treeSelectionListener);
		panelControlled.getPodcastFeedTree().addTreeSelectionListener(treeSelectionListener);

		NavigationTreeMouseListener treeMouseListener = new NavigationTreeMouseListener(this, panelControlled);
		panelControlled.getNavigationTree().addMouseListener(treeMouseListener);
		panelControlled.getFileNavigationTree().addMouseListener(treeMouseListener);
		panelControlled.getFavoritesTree().addMouseListener(treeMouseListener);
		panelControlled.getDeviceTree().addMouseListener(treeMouseListener);
		panelControlled.getRadioTree().addMouseListener(treeMouseListener);
		panelControlled.getPodcastFeedTree().addMouseListener(treeMouseListener);

		NavigationTreeToolTipListener tooltipListener = new NavigationTreeToolTipListener(this, panelControlled);
		panelControlled.getNavigationTree().addMouseMotionListener(tooltipListener);
		panelControlled.getNavigationTreeScrollPane().addMouseWheelListener(tooltipListener);

		panelControlled.getNavigationTable().addMouseListener(new NavigationTableMouseListener(this, panelControlled));
		panelControlled.getTabbedPane().addChangeListener(new NavigationTabbedPaneChangeListener(this, panelControlled));

		NavigationTreeControlsActionListener treeControlsActionListener = new NavigationTreeControlsActionListener(this, panelControlled);
		panelControlled.getCollapseTree().addActionListener(treeControlsActionListener);
		panelControlled.getExpandTree().addActionListener(treeControlsActionListener);
		panelControlled.getFilterTextField().addActionListener(treeControlsActionListener);
		panelControlled.getFilterTextField().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					panelControlled.getFilterTextField().setText("");
					state.setCurrentFilter(null);
					refreshTreeContent();
				}
			}
		});
		panelControlled.getClearFilterButton().addActionListener(treeControlsActionListener);
		panelControlled.getAddToPlayList().addActionListener(treeControlsActionListener);
		panelControlled.getShowArtist().addActionListener(treeControlsActionListener);
		panelControlled.getShowAlbum().addActionListener(treeControlsActionListener);
		panelControlled.getShowGenre().addActionListener(treeControlsActionListener);

		NavigationTableControlsActionListener tableControlsActionListener = new NavigationTableControlsActionListener(this, panelControlled);
		panelControlled.getNavigationTableInfoButton().addActionListener(tableControlsActionListener);
		panelControlled.getNavigationTableAddButton().addActionListener(tableControlsActionListener);
		panelControlled.getSortByTrack().addActionListener(tableControlsActionListener);
		panelControlled.getSortByFile().addActionListener(tableControlsActionListener);
		panelControlled.getSortByTitle().addActionListener(tableControlsActionListener);

		NavigationMenusActionListener menusActionListener = new NavigationMenusActionListener(this, panelControlled);
		panelControlled.getAddToPlaylistMenuItem().addActionListener(menusActionListener);
		panelControlled.getSetAsPlaylistMenuItem().addActionListener(menusActionListener);
		panelControlled.getPlayNowMenuItem().addActionListener(menusActionListener);
		panelControlled.getRemoveFromFavoritesMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteAddToPlaylistMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteSetAsPlaylistMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoritePlayNowMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteSetAsFavoriteSongMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteSetAsFavoriteAlbumMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteSetAsFavoriteArtistMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteEditTagMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteMarkPodcastEntryAsListened().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteEditTitlesMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteClearTagMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteExtractPictureMenuItem().addActionListener(menusActionListener);
		panelControlled.getDeviceCopyToRepositoryMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteSearch().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteSearchAt().addActionListener(menusActionListener);
		panelControlled.getRadioAddRadioMenuItem().addActionListener(menusActionListener);
		panelControlled.getRadioRenameRadioMenuItem().addActionListener(menusActionListener);
		panelControlled.getRadioRemoveRadioMenuItem().addActionListener(menusActionListener);
		panelControlled.getPodcastFeedAddPodcastFeedMenuItem().addActionListener(menusActionListener);
		panelControlled.getPodcastFeedRenamePodcastFeedMenuItem().addActionListener(menusActionListener);
		panelControlled.getPodcastFeedRemovePodcastFeedMenuItem().addActionListener(menusActionListener);
		panelControlled.getPodcastFeedMarkAsListenedMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteRemovePhysicallyMenuItem().addActionListener(menusActionListener);
		panelControlled.getNonFavoriteCopyToDeviceMenuItem().addActionListener(menusActionListener);
	}

	@Override
	protected void addStateBindings() {
		state.setViewMode(ViewMode.ARTIST);
		panelControlled.getShowArtist().setSelected(true);
		state.setSortType(SortType.BY_TRACK);
		panelControlled.getSortByTrack().setSelected(true);

		panelControlled.getNavigationTableAddButton().setEnabled(false);
		panelControlled.getNavigationTableInfoButton().setEnabled(false);
	}

	public AlbumToolTip getAlbumToolTip() {
		if (albumToolTip == null) {
			JDialog.setDefaultLookAndFeelDecorated(false);
			albumToolTip = new AlbumToolTip();
			JDialog.setDefaultLookAndFeelDecorated(true);
		}
		return albumToolTip;
	}

	public DefaultTreeModel getFavoritesTreeModel() {
		return panelControlled.getFavoritesTreeModel();
	}

	public DefaultTreeModel getFolderTreeModel() {
		return panelControlled.getFileNavigationTreeModel();
	}

	public Object getLastAlbumToolTipContent() {
		return lastAlbumToolTipContent;
	}

	public DefaultTreeModel getNavigationTreeModel() {
		return panelControlled.getNavigationTreeModel();
	}

	public AudioObject getSongInNavigationTable(int row) {
		return ((NavigationTableModel) panelControlled.getNavigationTable().getModel()).getSongAt(row);
	}

	public List<AudioObject> getSongsForDeviceTreeNode(DefaultMutableTreeNode node) {
		return nodeTranslator.getSongsForDeviceTreeNode(node);
	}

	public List<AudioObject> getSongsForFavoriteTreeNode(DefaultMutableTreeNode node) {
		return nodeTranslator.getSongsForFavoriteTreeNode(node);
	}

	public List<AudioObject> getSongsForNavigationTree() {
		TreePath[] paths = panelControlled.getNavigationTree().getSelectionPaths();
		return nodeTranslator.getSongsForNavigationTree(paths);
	}

	public List<AudioObject> getSongsForPodcastFeedTreeNode(DefaultMutableTreeNode node) {
		return nodeTranslator.getSongsForPodcastFeedTreeNode(node);
	}

	public List<AudioObject> getSongsForRadioTreeNode(DefaultMutableTreeNode node) {
		return nodeTranslator.getSongsForRadioTreeNode(node);
	}

	public List<AudioObject> getSongsForTreeNode(DefaultMutableTreeNode node) {
		return nodeTranslator.getSongsForTreeNode(node);
	}

	public NavigationControllerState getState() {
		return state;
	}

	public boolean isSortDeviceByTag() {
		return Kernel.getInstance().state.isSortDeviceByTag();
	}

	public void notifyDeviceReload() {
		refreshDeviceTreeContent();
	}

	@Override
	public void notifyReload() {
		refreshTreeContent();
		refreshFavoriteTree();
		//adjustColumnsWidth();
	}

	public void refreshDeviceTreeContent() {
		logger.debug(LogCategories.CONTROLLER);

		Map<String, ?> data;
		if (Kernel.getInstance().state.isSortDeviceByTag()) {
			panelControlled.getDeviceTree().setCellRenderer(Renderers.DEVICE_BY_TAG_TREE_RENDERER);
			data = DeviceHandler.getInstance().getDeviceArtistAndAlbumStructure();
		} else {
			panelControlled.getDeviceTree().setCellRenderer(Renderers.DEVICE_BY_FOLDER_TREE_RENDERER);
			data = DeviceHandler.getInstance().getDeviceFolderStructure();
		}

		if (state.getNavigationView() == NavigationControllerViews.DEVICE_VIEW) {
			panelControlled.getShowAlbum().setEnabled(DeviceHandler.getInstance().getDeviceRepository() != null && Kernel.getInstance().state.isSortDeviceByTag());
			panelControlled.getShowArtist().setEnabled(DeviceHandler.getInstance().getDeviceRepository() != null && Kernel.getInstance().state.isSortDeviceByTag());
		}

		DeviceViewRefresher.refresh(data, panelControlled.getDeviceTreeModel(), state.getViewMode(), state.getCurrentFilter(), Kernel.getInstance().state.isSortDeviceByTag());
		panelControlled.getDeviceTree().expandRow(0);
		panelControlled.getDeviceTree().setSelectionRow(0);
	}

	public void refreshFavoriteTree() {
		logger.debug(LogCategories.CONTROLLER);

		int[] rowSelected = panelControlled.getFavoritesTree().getSelectionRows();

		Map<String, Artist> artists = FavoritesHandler.getInstance().getFavoriteArtistsInfo();
		Map<String, Album> albums = FavoritesHandler.getInstance().getFavoriteAlbumsInfo();
		FavoriteViewRefresher.refresh(panelControlled.getFavoritesTree(), artists, albums, panelControlled.getFavoritesTreeModel());
		panelControlled.getFavoritesTree().expandRow(0);
		panelControlled.getFavoritesTree().setSelectionRow(rowSelected != null ? rowSelected[0] : 0);
	}

	private void refreshFileViewTreeContent() {
		logger.debug(LogCategories.CONTROLLER);

		Map<String, Folder> data = RepositoryHandler.getInstance().getFolderStructure();
		FileViewRefresher.refresh(data, panelControlled.getFileNavigationTreeModel(), state.getCurrentFilter());
		panelControlled.getFileNavigationTree().expandRow(0);

		// Expand root childs
		TreePath rootPath = panelControlled.getFileNavigationTree().getPathForRow(0);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootPath.getLastPathComponent();
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode childNode = node.getChildAt(i);
			TreePath childPath = rootPath.pathByAddingChild(childNode);
			panelControlled.getFileNavigationTree().expandPath(childPath);
		}

		panelControlled.getFileNavigationTree().setSelectionRow(0);
	}

	public void refreshPodcastFeedTreeContent() {
		logger.debug(LogCategories.CONTROLLER);
		List<PodcastFeed> podcastFeeds = PodcastFeedHandler.getInstance().getPodcastFeeds();

		// remember selected rows (if no podcast feed was added or removed)
		int[] selectedRows = panelControlled.getPodcastFeedTree().getSelectionRows();
		int podcastCount = podcastFeeds.size();
		int podcastInTreeModelCount = panelControlled.getPodcastFeedTreeModel().getChildCount(panelControlled.getPodcastFeedTreeModel().getRoot());
		if (podcastCount != podcastInTreeModelCount) {
			selectedRows = null;
		}
		PodcastFeedViewRefresher.refresh(podcastFeeds, panelControlled.getPodcastFeedTreeModel(), state.getCurrentFilter());
		if (selectedRows != null) {
			panelControlled.getPodcastFeedTree().setSelectionRows(selectedRows);
		} else {
			panelControlled.getPodcastFeedTree().setSelectionRow(0);
		}

		panelControlled.getPodcastFeedTree().expandRow(0);
	}

	public void refreshRadioTreeContent() {
		logger.debug(LogCategories.CONTROLLER);

		List<Radio> radios = RadioHandler.getInstance().getRadios();
		RadioViewRefresher.refresh(radios, panelControlled.getRadioTreeModel(), state.getCurrentFilter());
		panelControlled.getRadioTree().expandRow(0);
		panelControlled.getRadioTree().setSelectionRow(0);
	}

	public void refreshTable() {
		((NavigationTableModel) panelControlled.getNavigationTable().getModel()).refresh();
	}

	public void refreshTagViewTreeContent() {
		logger.debug(LogCategories.CONTROLLER);

		if (state.getViewMode() != ViewMode.GENRE) {
			Map<String, Artist> data = RepositoryHandler.getInstance().getArtistAndAlbumStructure();
			TagViewRefresher.refresh(data, panelControlled.getNavigationTree(), panelControlled.getNavigationTreeModel(), state.getViewMode(), state.getCurrentFilter());
		} else {
			Map<String, Genre> data = RepositoryHandler.getInstance().getGenreStructure();
			TagViewRefresher.refreshGenreTree(data, panelControlled.getNavigationTree(), panelControlled.getNavigationTreeModel(), state.getCurrentFilter());
		}
		panelControlled.getNavigationTree().expandRow(0);

	}

	public void refreshTreeContent() {
		refreshTagViewTreeContent();
		refreshFileViewTreeContent();
		refreshDeviceTreeContent();
		refreshRadioTreeContent();
		refreshPodcastFeedTreeContent();
	}

	public void setLastAlbumToolTipContent(Object lastAlbumToolTipContent) {
		this.lastAlbumToolTipContent = lastAlbumToolTipContent;
	}

	public void setNavigationView(int view) {
		logger.debug(LogCategories.CONTROLLER, new String[] { Integer.toString(view) });
		state.setNavigationView(view);
		Kernel.getInstance().state.setNavigationView(view);
		panelControlled.getTabbedPane().setSelectedIndex(view);

		panelControlled.getShowAlbum().setEnabled(view == NavigationControllerViews.TAG_VIEW);
		panelControlled.getShowArtist().setEnabled(view == NavigationControllerViews.TAG_VIEW);

		if (view == NavigationControllerViews.FAVORITE_VIEW) {
			panelControlled.getFilterLabel().setEnabled(false);
			panelControlled.getFilterTextField().setEnabled(false);
			panelControlled.getClearFilterButton().setEnabled(false);
		} else {
			panelControlled.getFilterLabel().setEnabled(true);
			panelControlled.getFilterTextField().setEnabled(true);
			panelControlled.getClearFilterButton().setEnabled(true);
		}
	}

	public List<AudioObject> sort(List<AudioObject> songs, SortType type) {
		return RepositoryHandler.getInstance().sort(songs, type);
	}

}
