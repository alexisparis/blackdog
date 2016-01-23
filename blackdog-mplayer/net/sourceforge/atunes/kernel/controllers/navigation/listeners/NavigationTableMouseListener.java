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

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationControllerViews;
import net.sourceforge.atunes.kernel.handlers.DeviceHandler;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

//TODO: Refactor this class
public class NavigationTableMouseListener extends MouseAdapter {

	private NavigationController controller;
	private NavigationPanel panel;

	public NavigationTableMouseListener(NavigationController controller, NavigationPanel panel) {
		this.controller = controller;
		this.panel = panel;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON3) {
			int[] rowsSelected = panel.getNavigationTable().getSelectedRows();
			int selected = panel.getNavigationTable().rowAtPoint(event.getPoint());
			boolean found = false;
			int i = 0;
			while (!found && i < rowsSelected.length) {
				if (rowsSelected[i] == selected)
					found = true;
				i++;
			}
			if (!found)
				panel.getNavigationTable().getSelectionModel().setSelectionInterval(selected, selected);
			panel.getNonFavoriteEditTitlesMenuItem().setEnabled(false);
			panel.getNonFavoriteSearchAt().setEnabled(false);
			if (panel.getNavigationTable().getSelectedRowCount() == 1) {
				AudioObject file = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongAt(panel.getNavigationTable().getSelectedRow());
				panel.getNonFavoriteExtractPictureMenuItem().setEnabled(file instanceof AudioFile && ((AudioFile) file).hasInternalPicture());
				panel.getNonFavoritePlayNowMenuItem().setEnabled(true);
			} else {
				panel.getNonFavoriteExtractPictureMenuItem().setEnabled(false);
				panel.getNonFavoritePlayNowMenuItem().setEnabled(false);
			}
			if (controller.getState().getNavigationView() == NavigationControllerViews.FAVORITE_VIEW && panel.getNavigationTable().getSelectedRowCount() > 0) {
				controller.getState().setPopupmenuCaller(panel.getNavigationTable());
				if (panel.getFavoritesTree().getSelectionCount() == 1) {
					Object obj = ((DefaultMutableTreeNode) panel.getFavoritesTree().getSelectionPath().getLastPathComponent()).getUserObject();
					if (obj instanceof String && obj.equals(LanguageTool.getString("SONGS"))) {
						panel.getRemoveFromFavoritesMenuItem().setEnabled(true);
					} else
						panel.getRemoveFromFavoritesMenuItem().setEnabled(false);
				} else
					panel.getRemoveFromFavoritesMenuItem().setEnabled(false);
				panel.getFavoriteMenu().show(controller.getState().getPopupmenuCaller(), event.getX(), event.getY());
			} else if (panel.getNavigationTable().getSelectedRowCount() > 0) {
				controller.getState().setPopupmenuCaller(panel.getNavigationTable());
				panel.getNonFavoriteSetAsFavoriteSongMenuItem().setEnabled(true);
				panel.getNonFavoriteSetAsFavoriteAlbumMenuItem().setEnabled(false);
				panel.getNonFavoriteSetAsFavoriteArtistMenuItem().setEnabled(false);
				panel.getNonFavoriteMenu().show(controller.getState().getPopupmenuCaller(), event.getX(), event.getY());
			}

			if (controller.getState().getNavigationView() == NavigationControllerViews.RADIO_VIEW
					|| controller.getState().getNavigationView() == NavigationControllerViews.PODCAST_FEED_VIEW) {
				panel.getNonFavoriteEditTagMenuItem().setEnabled(false);
				panel.getNonFavoriteClearTagMenuItem().setEnabled(false);
				int[] selRow = panel.getNavigationTable().getSelectedRows();
				List<PodcastFeedEntry> podcastFeedEntries = PodcastFeedEntry.getPodcastFeedEntries(((NavigationTableModel) panel.getNavigationTable().getModel())
						.getSongsAt(selRow));
				boolean unlistendedPodcastFeedEntry = false;
				for (PodcastFeedEntry podcastFeedEntry : podcastFeedEntries) {
					if (!podcastFeedEntry.isListened()) {
						unlistendedPodcastFeedEntry = true;
						break;
					}
				}
				panel.getNonFavoriteMarkPodcastEntryAsListened().setEnabled(
						controller.getState().getNavigationView() == NavigationControllerViews.PODCAST_FEED_VIEW && unlistendedPodcastFeedEntry);
				panel.getNonFavoriteSetAsFavoriteSongMenuItem().setEnabled(false);
				panel.getNonFavoriteSearch().setEnabled(false);
				panel.getNonFavoriteRemovePhysicallyMenuItem().setEnabled(false);
				panel.getNonFavoriteCopyToDeviceMenuItem().setEnabled(false);
				panel.getNonFavoritePlayNowMenuItem().setEnabled(false);

			} else {
				panel.getNonFavoriteEditTagMenuItem().setEnabled(true);
				panel.getNonFavoriteClearTagMenuItem().setEnabled(true);
				panel.getNonFavoriteMarkPodcastEntryAsListened().setEnabled(false);
				panel.getNonFavoriteSetAsFavoriteSongMenuItem().setEnabled(true);
				panel.getNonFavoriteSearch().setEnabled(true);
				panel.getNonFavoriteRemovePhysicallyMenuItem().setEnabled(true);
				panel.getNonFavoriteCopyToDeviceMenuItem().setEnabled(DeviceHandler.getInstance().isDeviceConnected());
			}
		} else {
			if (event.getClickCount() == 2) {
				int[] selRow = panel.getNavigationTable().getSelectedRows();
				List<AudioObject> songs = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongsAt(selRow);
				if (songs != null && songs.size() >= 1) {
					PlayListHandler.getInstance().addToPlayList(songs);
					panel.getNavigationTableAddButton().setEnabled(true);
					panel.getNavigationTableInfoButton().setEnabled(true);
				}
			} else if (panel.getNavigationTable().getSelectedRowCount() > 0) {
				panel.getNavigationTableAddButton().setEnabled(true);
				panel.getNavigationTableInfoButton().setEnabled(true);
			}
		}
	}
}
