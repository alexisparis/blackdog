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

package net.sourceforge.atunes.kernel.controllers.navigation.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.gui.views.dialogs.CopyProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.SearchDialog;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController;
import net.sourceforge.atunes.kernel.executors.BackgroundExecutor;
import net.sourceforge.atunes.kernel.executors.ImportProcess;
import net.sourceforge.atunes.kernel.executors.processes.ExportFilesProcess;
import net.sourceforge.atunes.kernel.handlers.DesktopHandler;
import net.sourceforge.atunes.kernel.handlers.DeviceHandler;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.handlers.RadioHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.search.Search;
import net.sourceforge.atunes.kernel.modules.search.SearchFactory;
import net.sourceforge.atunes.kernel.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class NavigationMenusActionListener implements ActionListener {

	private Logger logger = new Logger();

	private NavigationController controller;
	private NavigationPanel panel;

	public NavigationMenusActionListener(NavigationController controller, NavigationPanel panel) {
		this.controller = controller;
		this.panel = panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == panel.getAddToPlaylistMenuItem()) {
			if (controller.getState().getPopupmenuCaller() == panel.getFavoritesTree()) {
				TreePath[] paths = panel.getFavoritesTree().getSelectionPaths();
				if (paths != null) {
					List<AudioObject> songs = new ArrayList<AudioObject>();
					songs.addAll(((NavigationTableModel) panel.getNavigationTable().getModel()).getSongs());
					PlayListHandler.getInstance().addToPlayList(songs);
				}
			} else {
				int[] rows = panel.getNavigationTable().getSelectedRows();
				if (rows.length > 0) {
					List<AudioObject> songs = new ArrayList<AudioObject>();
					for (int element : rows) {
						songs.add(((NavigationTableModel) panel.getNavigationTable().getModel()).getSongAt(element));
					}
					PlayListHandler.getInstance().addToPlayList(songs);
				}
			}
		} else if (e.getSource() == panel.getSetAsPlaylistMenuItem()) {
			PlayListHandler.getInstance().clearList();
			if (controller.getState().getPopupmenuCaller() == panel.getFavoritesTree()) {
				TreePath[] paths = panel.getFavoritesTree().getSelectionPaths();
				if (paths != null) {
					List<AudioObject> songs = new ArrayList<AudioObject>();
					songs.addAll(((NavigationTableModel) panel.getNavigationTable().getModel()).getSongs());
					PlayListHandler.getInstance().addToPlayList(songs);
				}
			} else {
				int[] rows = panel.getNavigationTable().getSelectedRows();
				if (rows.length > 0) {
					List<AudioObject> songs = new ArrayList<AudioObject>();
					for (int element : rows) {
						songs.add(((NavigationTableModel) panel.getNavigationTable().getModel()).getSongAt(element));
					}
					PlayListHandler.getInstance().addToPlayList(songs);
				}
			}
		} else if (e.getSource() == panel.getNonFavoritePlayNowMenuItem() || e.getSource() == panel.getPlayNowMenuItem()) {
			// Play now feature plays selected song inmediately. If song is added to play list, then is automatically selected.
			// If not, it's added to play list
			int selectedRow = panel.getNavigationTable().getSelectedRow();
			AudioObject song = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongAt(selectedRow);
			PlayListHandler.getInstance().playNow(song);
		} else if (e.getSource() == panel.getRemoveFromFavoritesMenuItem()) {
			if (controller.getState().getPopupmenuCaller() == panel.getFavoritesTree()) {
				TreePath[] paths = panel.getFavoritesTree().getSelectionPaths();
				if (paths != null) {
					for (TreePath element : paths) {
						TreeObject obj = (TreeObject) ((DefaultMutableTreeNode) element.getLastPathComponent()).getUserObject();
						FavoritesHandler.getInstance().removeFromFavorites(obj);
					}
				}
			} else {
				int[] rows = panel.getNavigationTable().getSelectedRows();
				if (rows.length > 0) {
					for (int element : rows) {
						AudioObject file = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongAt(element);
						if (file instanceof AudioFile) {
							FavoritesHandler.getInstance().removeSongFromFavorites((AudioFile) file);
						}
					}
				}
			}
			controller.refreshFavoriteTree();
		} else if (e.getSource() == panel.getNonFavoriteAddToPlaylistMenuItem()) {
			if (controller.getState().getPopupmenuCaller() == panel.getNavigationTable()) {
				int[] rows = panel.getNavigationTable().getSelectedRows();
				if (rows.length > 0) {
					List<AudioObject> songs = new ArrayList<AudioObject>();
					for (int element : rows) {
						songs.add(((NavigationTableModel) panel.getNavigationTable().getModel()).getSongAt(element));
					}
					PlayListHandler.getInstance().addToPlayList(songs);
				}
			} else {
				TreePath[] paths = ((JTree) controller.getState().getPopupmenuCaller()).getSelectionPaths();
				if (paths != null) {
					List<AudioObject> songs = new ArrayList<AudioObject>();
					for (TreePath element : paths) {
						TreeObject obj = (TreeObject) ((DefaultMutableTreeNode) element.getLastPathComponent()).getUserObject();
						songs.addAll(obj.getAudioObjects());
					}
					PlayListHandler.getInstance().addToPlayList(songs);
				}
			}
		} else if (e.getSource() == panel.getNonFavoriteSetAsPlaylistMenuItem()) {
			PlayListHandler.getInstance().clearList();
			if (controller.getState().getPopupmenuCaller() == panel.getNavigationTable()) {
				int[] rows = panel.getNavigationTable().getSelectedRows();
				if (rows.length > 0) {
					List<AudioObject> songs = new ArrayList<AudioObject>();
					for (int element : rows) {
						songs.add(((NavigationTableModel) panel.getNavigationTable().getModel()).getSongAt(element));
					}
					PlayListHandler.getInstance().addToPlayList(songs);
				}
			} else {
				TreePath[] paths = ((JTree) controller.getState().getPopupmenuCaller()).getSelectionPaths();
				if (paths != null) {
					PlayListHandler.getInstance().addToPlayList(((NavigationTableModel) panel.getNavigationTable().getModel()).getSongs());
				}
			}
		} else if (e.getSource() == panel.getNonFavoriteSetAsFavoriteSongMenuItem()) {
			int[] rows = panel.getNavigationTable().getSelectedRows();
			if (rows.length > 0) {
				List<AudioObject> songs = new ArrayList<AudioObject>();
				for (int element : rows) {
					songs.add(((NavigationTableModel) panel.getNavigationTable().getModel()).getSongAt(element));
				}
				FavoritesHandler.getInstance().addFavoriteSongs(AudioFile.getAudioFiles(songs));
				controller.refreshTable();
			}
		} else if (e.getSource() == panel.getNonFavoriteSetAsFavoriteAlbumMenuItem()) {
			TreePath[] paths = ((JTree) controller.getState().getPopupmenuCaller()).getSelectionPaths();
			if (paths != null) {
				List<AudioObject> songs = new ArrayList<AudioObject>();
				for (TreePath element : paths) {
					Object obj = ((DefaultMutableTreeNode) element.getLastPathComponent()).getUserObject();
					if (obj instanceof TreeObject)
						songs.addAll(((TreeObject) obj).getAudioObjects());
				}
				FavoritesHandler.getInstance().addFavoriteAlbums(AudioFile.getAudioFiles(songs));
				controller.refreshTable();
			}
		} else if (e.getSource() == panel.getNonFavoriteSetAsFavoriteArtistMenuItem()) {
			TreePath[] paths = ((JTree) controller.getState().getPopupmenuCaller()).getSelectionPaths();
			if (paths != null) {
				List<AudioObject> songs = new ArrayList<AudioObject>();
				for (TreePath element : paths) {
					Object obj = ((DefaultMutableTreeNode) element.getLastPathComponent()).getUserObject();
					if (obj instanceof TreeObject)
						songs.addAll(((TreeObject) obj).getAudioObjects());
				}
				FavoritesHandler.getInstance().addFavoriteArtists(AudioFile.getAudioFiles(songs));
				controller.refreshTable();
			}
		} else if (e.getSource() == panel.getNonFavoriteEditTagMenuItem()) {
			if (controller.getState().getPopupmenuCaller() == panel.getNavigationTable()) {
				int[] rows = panel.getNavigationTable().getSelectedRows();
				List<AudioObject> files = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongsAt(rows);
				ControllerProxy.getInstance().getEditTagDialogController().editFiles(AudioFile.getAudioFiles(files));
			} else if (controller.getState().getPopupmenuCaller() == panel.getNavigationTree()) {
				TreePath path = panel.getNavigationTree().getSelectionPath();
				if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof TreeObject) {
					List<AudioObject> files = ((TreeObject) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()).getAudioObjects();
					ControllerProxy.getInstance().getEditTagDialogController().editFiles(AudioFile.getAudioFiles(files));
				}
			} else if (controller.getState().getPopupmenuCaller() == panel.getFileNavigationTree()) {
				TreePath path = panel.getFileNavigationTree().getSelectionPath();
				if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof TreeObject) {
					List<AudioObject> files = ((TreeObject) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()).getAudioObjects();
					ControllerProxy.getInstance().getEditTagDialogController().editFiles(AudioFile.getAudioFiles(files));
				}
			}
		} else if (e.getSource() == panel.getNonFavoriteEditTitlesMenuItem()) {
			TreePath path = panel.getNavigationTree().getSelectionPath();
			Album a = (Album) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			ControllerProxy.getInstance().getEditTitlesDialogController().editFiles(a);
		} else if (e.getSource() == panel.getNonFavoriteClearTagMenuItem()) {
			if (controller.getState().getPopupmenuCaller() == panel.getNavigationTable()) {
				int[] rows = panel.getNavigationTable().getSelectedRows();
				List<AudioObject> files = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongsAt(rows);
				// Call process
				BackgroundExecutor.clearTags(AudioFile.getAudioFiles(files));
			} else if (controller.getState().getPopupmenuCaller() == panel.getNavigationTree()) {
				TreePath path = panel.getNavigationTree().getSelectionPath();
				if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof TreeObject) {
					List<AudioObject> files = ((TreeObject) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()).getAudioObjects();
					// Call process
					BackgroundExecutor.clearTags(AudioFile.getAudioFiles(files));
				}
			} else if (controller.getState().getPopupmenuCaller() == panel.getFileNavigationTree()) {
				TreePath path = panel.getFileNavigationTree().getSelectionPath();
				if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof TreeObject) {
					List<AudioObject> files = ((TreeObject) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()).getAudioObjects();
					// Call process
					BackgroundExecutor.clearTags(AudioFile.getAudioFiles(files));
				}
			}
		} else if (e.getSource() == panel.getNonFavoriteExtractPictureMenuItem()) {
			AudioObject file = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongAt(panel.getNavigationTable().getSelectedRow());
			try {
				AudioFilePictureUtils.exportPicture((AudioFile) file);
			} catch (Exception ex) {
				logger.error(LogCategories.CONTROLLER, ex.getMessage());
			}
		} else if (e.getSource() == panel.getDeviceCopyToRepositoryMenuItem()) {
			List<AudioObject> songs = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongs();
			int filesToExport = songs.size();
			CopyProgressDialog copyProgressDialog = VisualHandler.getInstance().getCopyProgressDialog();
			copyProgressDialog.getTotalFilesLabel().setText(StringUtils.getString(filesToExport));
			copyProgressDialog.getProgressBar().setMaximum(filesToExport);
			copyProgressDialog.setVisible(true);
			final ImportProcess importer = new ImportProcess(AudioFile.getAudioFiles(songs), ExportFilesProcess.FULL_STRUCTURE, null);
			copyProgressDialog.getCancelButton().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					importer.notifyCancel();
				}
			});
			importer.start();
		} else if (e.getSource() == panel.getNonFavoriteSearch()) {
			Search defaultSearch = SearchFactory.getSearchForName(Kernel.getInstance().state.getDefaultSearch());
			if (defaultSearch != null) {
				if (controller.getState().getPopupmenuCaller() == panel.getNavigationTree()) {
					TreePath path = panel.getNavigationTree().getSelectionPath();
					if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof Artist) {
						Artist a = (Artist) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
						DesktopHandler.getInstance().openSearch(defaultSearch, a.getName());
					}
				}
			} else {
				SearchDialog dialog = VisualHandler.getInstance().getSearchDialog();
				Search selectedSearch = openSearchDialog(dialog, false);
				if (selectedSearch != null)
					Kernel.getInstance().state.setDefaultSearch(selectedSearch.toString());
			}
		} else if (e.getSource() == panel.getNonFavoriteSearchAt()) {
			SearchDialog dialog = VisualHandler.getInstance().getSearchDialog();
			Search s = openSearchDialog(dialog, true);
			if (dialog.isSetAsDefault() && s != null)
				Kernel.getInstance().state.setDefaultSearch(s.toString());
		} else if (e.getSource() == panel.getRadioRemoveRadioMenuItem()) {
			TreePath path = panel.getRadioTree().getSelectionPath();
			Radio radioToRemove = (Radio) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			RadioHandler.getInstance().removeRadio(radioToRemove);
		} else if (e.getSource() == panel.getPodcastFeedRemovePodcastFeedMenuItem()) {
			TreePath path = panel.getPodcastFeedTree().getSelectionPath();
			PodcastFeed podcastFeedToRemove = (PodcastFeed) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			PodcastFeedHandler.getInstance().removePodcastFeed(podcastFeedToRemove);
		} else if (e.getSource() == panel.getPodcastFeedMarkAsListenedMenuItem()) {
			TreePath path = panel.getPodcastFeedTree().getSelectionPath();
			PodcastFeed podcastFeedToMarkAsListened = (PodcastFeed) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			podcastFeedToMarkAsListened.markEntriesAsListened();
			ControllerProxy.getInstance().getNavigationController().refreshPodcastFeedTreeContent();
		} else if (e.getSource() == panel.getNonFavoriteRemovePhysicallyMenuItem()) {
			List<AudioFile> files = getFilesSelectedInNavigator();
			// Show confirmation
			if (VisualHandler.getInstance().showConfirmationDialog(LanguageTool.getString("REMOVE_CONFIRMATION"), LanguageTool.getString("CONFIRMATION")) == JOptionPane.OK_OPTION)
				RepositoryHandler.getInstance().removePhysically(files);
		} else if (e.getSource() == panel.getNonFavoriteCopyToDeviceMenuItem()) {
			List<AudioFile> files = getFilesSelectedInNavigator();
			DeviceHandler.getInstance().copyFilesToDevice(files);
		} else if (e.getSource() == panel.getNonFavoriteMarkPodcastEntryAsListened()) {
			int[] rows = panel.getNavigationTable().getSelectedRows();
			if (rows.length > 0) {
				for (int element : rows) {
					((PodcastFeedEntry) ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongAt(element)).setListened(true);
				}
				controller.refreshTable();
			}
		} else if (e.getSource() == panel.getRadioAddRadioMenuItem()) {
			RadioHandler.getInstance().addRadio();
		} else if (e.getSource() == panel.getPodcastFeedAddPodcastFeedMenuItem()) {
			PodcastFeedHandler.getInstance().addPodcastFeed();
		} else if (e.getSource() == panel.getRadioRenameRadioMenuItem()) {
			TreePath path = panel.getRadioTree().getSelectionPath();
			Radio radio = (Radio) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			String result = VisualHandler.getInstance().showInputDialog(LanguageTool.getString("RENAME_RADIO"), radio.getName(), ImageLoader.RADIO_LITTLE.getImage());
			if (result != null) {
				radio.setName(result);
			}
		} else if (e.getSource() == panel.getPodcastFeedRenamePodcastFeedMenuItem()) {
			TreePath path = panel.getPodcastFeedTree().getSelectionPath();
			PodcastFeed podcastFeed = (PodcastFeed) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			String result = VisualHandler.getInstance().showInputDialog(LanguageTool.getString("RENAME_PODCAST_FEED"), podcastFeed.getName(), ImageLoader.RSS_LITTLE.getImage());
			if (result != null) {
				podcastFeed.setName(result);
			}
		}
	}

	/**
	 * Get files of all selected elements in navigator
	 * 
	 * @return
	 */
	private List<AudioFile> getFilesSelectedInNavigator() {
		List<AudioObject> files = null;
		if (controller.getState().getPopupmenuCaller() == panel.getNavigationTable()) {
			int[] rows = panel.getNavigationTable().getSelectedRows();
			files = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongsAt(rows);
		} else if (controller.getState().getPopupmenuCaller() == panel.getNavigationTree()) {
			TreePath[] paths = panel.getNavigationTree().getSelectionPaths();
			files = new ArrayList<AudioObject>();
			for (TreePath path : paths) {
				if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof TreeObject) {
					files.addAll(((TreeObject) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()).getAudioObjects());
				}
			}
		} else if (controller.getState().getPopupmenuCaller() == panel.getFileNavigationTree()) {
			TreePath[] paths = panel.getFileNavigationTree().getSelectionPaths();
			files = new ArrayList<AudioObject>();
			for (TreePath path : paths) {
				if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof TreeObject) {
					files.addAll(((TreeObject) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()).getAudioObjects());
				}
			}
		}
		return AudioFile.getAudioFiles(files);
	}

	private Search openSearchDialog(SearchDialog dialog, boolean setAsDefaultVisible) {
		dialog.setSetAsDefaultVisible(setAsDefaultVisible);
		dialog.setVisible(true);
		Search s = dialog.getResult();
		if (controller.getState().getPopupmenuCaller() == panel.getNavigationTree()) {
			TreePath path = panel.getNavigationTree().getSelectionPath();
			if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof Artist) {
				Artist a = (Artist) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
				DesktopHandler.getInstance().openSearch(s, a.getName());
			}
		}
		return s;
	}
}
