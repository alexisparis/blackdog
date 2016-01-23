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

package net.sourceforge.atunes.kernel.controllers.playList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.model.PlayListTableModel;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;

public class PlayListListener extends MouseAdapter implements ActionListener, ListSelectionListener {

	private PlayListTable table;
	private PlayListController controller;

	protected PlayListListener(PlayListTable table, PlayListController controller) {
		this.table = table;
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(table.getArrangeColumns()))
			controller.arrangeColumns();
		else if (e.getSource().equals(table.getPlayItem()))
			controller.playSelectedAudioObject();
		else if (e.getSource().equals(table.getEditTagItem()))
			PlayListHandler.getInstance().editTags();
		else if (e.getSource().equals(table.getAutoSetLyricsItem()))
			controller.setLyrics();
		else if (e.getSource().equals(table.getAutoSetTrackItem()))
			controller.setTrackNumber();
		else if (e.getSource().equals(table.getAutoSetGenreItem()))
			controller.setGenre();
		else if (e.getSource().equals(table.getAutoSetTitleItem()))
			controller.setTitle();
		else if (e.getSource().equals(table.getSaveItem()))
			PlayListHandler.getInstance().savePlaylist();
		else if (e.getSource().equals(table.getLoadItem()))
			PlayListHandler.getInstance().loadPlaylist();
		else if (e.getSource().equals(table.getFilterItem()))
			VisualHandler.getInstance().showFilter(true);
		else if (e.getSource().equals(table.getTopItem()))
			controller.moveToTop();
		else if (e.getSource().equals(table.getUpItem()))
			controller.moveUp();
		else if (e.getSource().equals(table.getDownItem()))
			controller.moveDown();
		else if (e.getSource().equals(table.getBottomItem()))
			controller.moveToBottom();
		else if (e.getSource().equals(table.getDeleteItem()))
			controller.deleteSelection();
		else if (e.getSource().equals(table.getInfoItem()))
			VisualHandler.getInstance().showInfo();
		else if (e.getSource().equals(table.getClearItem()))
			PlayListHandler.getInstance().clearList();
		else if (e.getSource().equals(table.getFavoriteSong()))
			FavoritesHandler.getInstance().addFavoriteSongs(AudioFile.getAudioFiles(controller.getSelectedAudioObjects()));
		else if (e.getSource().equals(table.getFavoriteAlbum()))
			FavoritesHandler.getInstance().addFavoriteAlbums(AudioFile.getAudioFiles(controller.getSelectedAudioObjects()));
		else if (e.getSource().equals(table.getFavoriteArtist()))
			FavoritesHandler.getInstance().addFavoriteArtists(AudioFile.getAudioFiles(controller.getSelectedAudioObjects()));
		else if (e.getSource().equals(table.getArtistItem()))
			controller.setArtistAsPlaylist();
		else if (e.getSource().equals(table.getAlbumItem()))
			controller.setAlbumAsPlaylist();
		else if (e.getSource().equals(table.getAdd10RandomSongs()))
			PlayListHandler.getInstance().addRandomSongs(10);
		else if (e.getSource().equals(table.getAdd50RandomSongs()))
			PlayListHandler.getInstance().addRandomSongs(50);
		else if (e.getSource().equals(table.getAdd100RandomSongs()))
			PlayListHandler.getInstance().addRandomSongs(100);
		else if (e.getSource().equals(table.getAdd10SongsMostPlayed()))
			PlayListHandler.getInstance().addSongsMostPlayed(10);
		else if (e.getSource().equals(table.getAdd50SongsMostPlayed()))
			PlayListHandler.getInstance().addSongsMostPlayed(50);
		else if (e.getSource().equals(table.getAdd100SongsMostPlayed()))
			PlayListHandler.getInstance().addSongsMostPlayed(100);
		else if (e.getSource().equals(table.getAdd1AlbumsMostPlayed()))
			PlayListHandler.getInstance().addAlbumsMostPlayed(1);
		else if (e.getSource().equals(table.getAdd5AlbumsMostPlayed()))
			PlayListHandler.getInstance().addAlbumsMostPlayed(5);
		else if (e.getSource().equals(table.getAdd10AlbumsMostPlayed()))
			PlayListHandler.getInstance().addAlbumsMostPlayed(10);
		else if (e.getSource().equals(table.getAdd1ArtistsMostPlayed()))
			PlayListHandler.getInstance().addArtistsMostPlayed(1);
		else if (e.getSource().equals(table.getAdd5ArtistsMostPlayed()))
			PlayListHandler.getInstance().addArtistsMostPlayed(5);
		else if (e.getSource().equals(table.getAdd10ArtistsMostPlayed()))
			PlayListHandler.getInstance().addArtistsMostPlayed(10);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource().equals(table)) {
			if (e.getClickCount() == 2) {
				controller.playSelectedAudioObject();
			} else if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
				int[] currentlySelected = table.getSelectedRows();
				int selected = table.rowAtPoint(e.getPoint());
				boolean found = false;
				int i = 0;
				while (!found && i < currentlySelected.length) {
					if (currentlySelected[i] == selected)
						found = true;
					i++;
				}
				if (!found)
					table.getSelectionModel().setSelectionInterval(selected, selected);

				table.getMenu().show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel) e.getSource();

		int[] selectedRows = table.getSelectedRows();
		boolean radioOrPodcastFeedEntrySelected = false;
		for (Integer row : selectedRows) {
			AudioObject audioObject = ((PlayListTableModel) table.getModel()).getFileAt(row);
			if (audioObject instanceof Radio || audioObject instanceof PodcastFeedEntry) {
				radioOrPodcastFeedEntrySelected = true;
				break;
			}
		}

		ControllerProxy.getInstance().getMenuController().disablePlayListItems(lsm.isSelectionEmpty(), radioOrPodcastFeedEntrySelected);
		controller.disablePlayListItems(lsm.isSelectionEmpty(), radioOrPodcastFeedEntrySelected);
		ControllerProxy.getInstance().getPlayListControlsController().disablePlayListControls(lsm.isSelectionEmpty(), radioOrPodcastFeedEntrySelected);
	}

}
