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

package net.sourceforge.atunes.kernel.controllers.playListControls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.gui.views.panels.PlayListControlsPanel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;

public class PlayListControlsListener implements ActionListener {

	private PlayListControlsPanel panel;

	public PlayListControlsListener(PlayListControlsPanel panel) {
		this.panel = panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(panel.getArrangeColumns()))
			ControllerProxy.getInstance().getPlayListController().arrangeColumns();
		else if (e.getSource().equals(panel.getSearchFilter()))
			VisualHandler.getInstance().showFilter(true);
		else if (e.getSource().equals(panel.getSavePlaylistButton()))
			PlayListHandler.getInstance().savePlaylist();
		else if (e.getSource().equals(panel.getLoadPlaylistButton()))
			PlayListHandler.getInstance().loadPlaylist();
		else if (e.getSource().equals(panel.getTopButton()))
			ControllerProxy.getInstance().getPlayListController().moveToTop();
		else if (e.getSource().equals(panel.getUpButton()))
			ControllerProxy.getInstance().getPlayListController().moveUp();
		else if (e.getSource().equals(panel.getDeleteButton()))
			ControllerProxy.getInstance().getPlayListController().deleteSelection();
		else if (e.getSource().equals(panel.getDownButton()))
			ControllerProxy.getInstance().getPlayListController().moveDown();
		else if (e.getSource().equals(panel.getBottomButton()))
			ControllerProxy.getInstance().getPlayListController().moveToBottom();
		else if (e.getSource().equals(panel.getInfoButton()))
			VisualHandler.getInstance().showInfo();
		else if (e.getSource().equals(panel.getClearButton()))
			PlayListHandler.getInstance().clearList();
		else if (e.getSource().equals(panel.getFavoriteSong()))
			FavoritesHandler.getInstance().addFavoriteSongs(AudioFile.getAudioFiles(ControllerProxy.getInstance().getPlayListController().getSelectedAudioObjects()));
		else if (e.getSource().equals(panel.getFavoriteAlbum()))
			FavoritesHandler.getInstance().addFavoriteAlbums(AudioFile.getAudioFiles(ControllerProxy.getInstance().getPlayListController().getSelectedAudioObjects()));
		else if (e.getSource().equals(panel.getFavoriteArtist()))
			FavoritesHandler.getInstance().addFavoriteArtists(AudioFile.getAudioFiles(ControllerProxy.getInstance().getPlayListController().getSelectedAudioObjects()));
		else if (e.getSource().equals(panel.getArtistButton()))
			ControllerProxy.getInstance().getPlayListController().setArtistAsPlaylist();
		else if (e.getSource().equals(panel.getAlbumButton()))
			ControllerProxy.getInstance().getPlayListController().setAlbumAsPlaylist();
		else if (e.getSource().equals(panel.getScrollPlaylistToCurrentSongButton())) {
			ControllerProxy.getInstance().getPlayListController().scrollPlayList();
		}
	}
}
