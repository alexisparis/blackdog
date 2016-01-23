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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController;
import net.sourceforge.atunes.kernel.handlers.DeviceHandler;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.Genre;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.LanguageTool;

public class NavigationTreeMouseListener extends MouseAdapter {

	private NavigationController controller;
	private NavigationPanel panel;

	public NavigationTreeMouseListener(NavigationController controller, NavigationPanel panel) {
		this.controller = controller;
		this.panel = panel;
	}

	private boolean isNewRowSelection(JTree tree, MouseEvent e) {
		int[] rowsSelected = tree.getSelectionRows();
		if (rowsSelected == null) {
			return false;
		}
		int selected = tree.getRowForLocation(e.getX(), e.getY());
		boolean found = false;
		int i = 0;
		while (!found && i < rowsSelected.length) {
			if (rowsSelected[i] == selected)
				found = true;
			i++;
		}
		return !found;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == panel.getNavigationTree()) {
			if (e.getButton() == MouseEvent.BUTTON3) {

				//	BUG 1626896
				int row = panel.getNavigationTree().getRowForLocation(e.getX(), e.getY());
				if (isNewRowSelection(panel.getNavigationTree(), e) && row != -1) {
					panel.getNavigationTree().setSelectionRow(row);
				}
				//	BUG 1626896

				boolean rootSelected = panel.getNavigationTree().isRowSelected(0);

				controller.getState().setPopupmenuCaller(panel.getNavigationTree());
				panel.getNonFavoriteSetAsFavoriteSongMenuItem().setEnabled(false);
				panel.getNonFavoriteSetAsFavoriteAlbumMenuItem().setEnabled(!rootSelected);
				panel.getNonFavoriteSetAsFavoriteArtistMenuItem().setEnabled(!rootSelected);
				panel.getNonFavoriteMarkPodcastEntryAsListened().setEnabled(false);
				panel.getNonFavoritePlayNowMenuItem().setEnabled(false);
				panel.getNonFavoriteRemovePhysicallyMenuItem().setEnabled(!rootSelected);
				panel.getNonFavoriteCopyToDeviceMenuItem().setEnabled(!rootSelected && DeviceHandler.getInstance().isDeviceConnected());
				panel.getNonFavoriteEditTagMenuItem().setEnabled(!rootSelected && panel.getNavigationTree().getSelectionCount() == 1);
				panel.getNonFavoriteClearTagMenuItem().setEnabled(!rootSelected && panel.getNavigationTree().getSelectionCount() == 1);
				panel.getNonFavoriteExtractPictureMenuItem().setEnabled(false);
				TreePath path = panel.getNavigationTree().getSelectionPath();
				if (path != null) {
					Object obj = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					if (obj instanceof Genre) {
						panel.getNonFavoriteSetAsFavoriteAlbumMenuItem().setEnabled(false);
						panel.getNonFavoriteSetAsFavoriteArtistMenuItem().setEnabled(false);
						panel.getNonFavoriteEditTagMenuItem().setEnabled(false);
						panel.getNonFavoriteClearTagMenuItem().setEnabled(false);
					}
					panel.getNonFavoriteEditTitlesMenuItem().setEnabled(obj instanceof Album);
					panel.getNonFavoriteSearch().setEnabled(obj instanceof Artist && !(((Artist) obj).getName().equals(LanguageTool.getString("UNKNOWN_ARTIST"))));
					panel.getNonFavoriteSearchAt().setEnabled(obj instanceof Artist && !(((Artist) obj).getName().equals(LanguageTool.getString("UNKNOWN_ARTIST"))));
				} else {
					panel.getNonFavoriteEditTitlesMenuItem().setEnabled(false);
					panel.getNonFavoriteSearch().setEnabled(false);
					panel.getNonFavoriteSearchAt().setEnabled(false);
				}
				panel.getNonFavoriteMenu().show(controller.getState().getPopupmenuCaller(), e.getX(), e.getY());
			} else {
				int selRow = panel.getNavigationTree().getRowForLocation(e.getX(), e.getY());
				TreePath selPath = panel.getNavigationTree().getPathForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 2) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
						List<AudioObject> songs = controller.getSongsForTreeNode(node);
						PlayListHandler.getInstance().addToPlayList(songs);
					}
				}
			}

		} else if (e.getSource() == panel.getFileNavigationTree()) {
			if (e.getButton() == MouseEvent.BUTTON3) {

				//	BUG 1626896
				int row = panel.getFileNavigationTree().getRowForLocation(e.getX(), e.getY());
				if (isNewRowSelection(panel.getFileNavigationTree(), e) && row != -1) {
					panel.getFileNavigationTree().setSelectionRow(row);
				}
				//	BUG 1626896

				boolean rootSelected = panel.getFileNavigationTree().isRowSelected(0);

				controller.getState().setPopupmenuCaller(panel.getFileNavigationTree());
				panel.getNonFavoriteSetAsFavoriteSongMenuItem().setEnabled(false);
				panel.getNonFavoriteSetAsFavoriteAlbumMenuItem().setEnabled(!rootSelected);
				panel.getNonFavoriteSetAsFavoriteArtistMenuItem().setEnabled(!rootSelected);
				panel.getNonFavoriteMarkPodcastEntryAsListened().setEnabled(false);
				panel.getNonFavoritePlayNowMenuItem().setEnabled(false);
				panel.getNonFavoriteEditTagMenuItem().setEnabled(!rootSelected && panel.getFileNavigationTree().getSelectionCount() == 1);
				panel.getNonFavoriteEditTitlesMenuItem().setEnabled(false);
				panel.getNonFavoriteSearch().setEnabled(false);
				panel.getNonFavoriteSearchAt().setEnabled(false);
				panel.getNonFavoriteClearTagMenuItem().setEnabled(!rootSelected && panel.getFileNavigationTree().getSelectionCount() == 1);
				panel.getNonFavoriteExtractPictureMenuItem().setEnabled(false);
				panel.getNonFavoriteMenu().show(controller.getState().getPopupmenuCaller(), e.getX(), e.getY());

				TreePath path = panel.getFileNavigationTree().getSelectionPath();
				if (path != null) {
					Object obj = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					panel.getNonFavoriteRemovePhysicallyMenuItem().setEnabled(obj instanceof Folder && ((Folder) obj).getParentFolder() != null);
					panel.getNonFavoriteCopyToDeviceMenuItem().setEnabled(
							obj instanceof Folder && ((Folder) obj).getParentFolder() != null && DeviceHandler.getInstance().isDeviceConnected());
				}

			} else {
				int selRow = panel.getFileNavigationTree().getRowForLocation(e.getX(), e.getY());
				TreePath selPath = panel.getFileNavigationTree().getPathForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 2) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
						List<AudioObject> songs = controller.getSongsForTreeNode(node);
						PlayListHandler.getInstance().addToPlayList(songs);
					}
				}
			}

		} else if (e.getSource() == panel.getFavoritesTree()) {
			if (e.getButton() == MouseEvent.BUTTON3) {

				//	BUG 1626896
				int row = panel.getFavoritesTree().getRowForLocation(e.getX(), e.getY());
				if (isNewRowSelection(panel.getFavoritesTree(), e) && row != -1) {
					panel.getFavoritesTree().setSelectionRow(row);
				}
				//	BUG 1626896

				TreePath path = panel.getFavoritesTree().getSelectionPath();
				if (path != null) {
					Object obj = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					if (obj instanceof Album) {
						Object[] objs = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObjectPath();
						boolean foundArtist = false;
						for (Object element : objs) {
							if (element instanceof Artist) {
								foundArtist = true;
								break;
							}
						}
						panel.getRemoveFromFavoritesMenuItem().setEnabled(!foundArtist);
					} else
						panel.getRemoveFromFavoritesMenuItem().setEnabled(obj instanceof TreeObject);
					controller.getState().setPopupmenuCaller(panel.getFavoritesTree());
					panel.getFavoriteMenu().show(panel.getFavoritesTree(), e.getX(), e.getY());
				}
				panel.getPlayNowMenuItem().setEnabled(false);
			} else {
				int selRow = panel.getFavoritesTree().getRowForLocation(e.getX(), e.getY());
				TreePath selPath = panel.getFavoritesTree().getPathForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 2) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
						List<AudioObject> songs = controller.getSongsForFavoriteTreeNode(node);
						PlayListHandler.getInstance().addToPlayList(songs);
					}
				}
			}
		} else if (e.getSource() == panel.getDeviceTree()) {
			if (e.getButton() == MouseEvent.BUTTON3) {

				//	BUG 1626896
				int row = panel.getDeviceTree().getRowForLocation(e.getX(), e.getY());
				if (isNewRowSelection(panel.getDeviceTree(), e) && row != -1) {
					panel.getDeviceTree().setSelectionRow(row);
				}
				//	BUG 1626896

				panel.getNonFavoriteMarkPodcastEntryAsListened().setEnabled(false);
				panel.getNonFavoritePlayNowMenuItem().setEnabled(false);

				if (DeviceHandler.getInstance().isDeviceConnected()) {
					controller.getState().setPopupmenuCaller(panel.getDeviceTree());
					panel.getDeviceMenu().show(controller.getState().getPopupmenuCaller(), e.getX(), e.getY());
				}
			} else {
				int selRow = panel.getDeviceTree().getRowForLocation(e.getX(), e.getY());
				TreePath selPath = panel.getDeviceTree().getPathForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 2) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
						List<AudioObject> songs = controller.getSongsForDeviceTreeNode(node);
						PlayListHandler.getInstance().addToPlayList(songs);
					}
				}
			}
		} else if (e.getSource() == panel.getRadioTree()) {
			if (e.getButton() == MouseEvent.BUTTON3) {

				//	BUG 1626896
				int row = panel.getRadioTree().getRowForLocation(e.getX(), e.getY());
				if (isNewRowSelection(panel.getRadioTree(), e) && row != -1) {
					panel.getRadioTree().setSelectionRow(row);
				}
				//	BUG 1626896

				panel.getNonFavoritePlayNowMenuItem().setEnabled(false);

				controller.getState().setPopupmenuCaller(panel.getRadioTree());
				int selRow = panel.getRadioTree().getRowForLocation(e.getX(), e.getY());
				panel.getRadioRemoveRadioMenuItem().setEnabled(selRow > 0);
				panel.getRadioRenameRadioMenuItem().setEnabled(selRow > 0);
				panel.getRadioMenu().show(controller.getState().getPopupmenuCaller(), e.getX(), e.getY());
			} else {
				int selRow = panel.getRadioTree().getRowForLocation(e.getX(), e.getY());
				TreePath selPath = panel.getRadioTree().getPathForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 2) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
						List<AudioObject> songs = controller.getSongsForRadioTreeNode(node);
						PlayListHandler.getInstance().addToPlayList(songs);
					}
				}
			}
		} else if (e.getSource() == panel.getPodcastFeedTree()) {
			if (e.getButton() == MouseEvent.BUTTON3) {

				//	BUG 1626896
				int row = panel.getPodcastFeedTree().getRowForLocation(e.getX(), e.getY());
				if (isNewRowSelection(panel.getPodcastFeedTree(), e) && row != -1) {
					panel.getPodcastFeedTree().setSelectionRow(row);
				}
				//	BUG 1626896

				panel.getNonFavoritePlayNowMenuItem().setEnabled(false);

				controller.getState().setPopupmenuCaller(panel.getPodcastFeedTree());
				int selRow = panel.getPodcastFeedTree().getRowForLocation(e.getX(), e.getY());
				panel.getPodcastFeedRemovePodcastFeedMenuItem().setEnabled(selRow > 0);
				panel.getPodcastFeedRenamePodcastFeedMenuItem().setEnabled(selRow > 0);
				panel.getPodcastFeedMarkAsListenedMenuItem().setEnabled(selRow > 0);
				panel.getPodcastFeedMenu().show(controller.getState().getPopupmenuCaller(), e.getX(), e.getY());
			} else {
				int selRow = panel.getPodcastFeedTree().getRowForLocation(e.getX(), e.getY());
				TreePath selPath = panel.getPodcastFeedTree().getPathForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 2) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
						List<AudioObject> songs = controller.getSongsForPodcastFeedTreeNode(node);
						PlayListHandler.getInstance().addToPlayList(songs);
					}
				}
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if (!Kernel.getInstance().state.isShowAlbumTooltip())
			return;

		controller.setLastAlbumToolTipContent(null);
		controller.getAlbumToolTip().setVisible(false);
		controller.getAlbumToolTip().timer.stop();
	}

}
