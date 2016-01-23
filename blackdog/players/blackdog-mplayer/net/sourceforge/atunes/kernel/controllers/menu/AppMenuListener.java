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

package net.sourceforge.atunes.kernel.controllers.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.menu.ApplicationMenuBar;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationControllerViews;
import net.sourceforge.atunes.kernel.handlers.DesktopHandler;
import net.sourceforge.atunes.kernel.handlers.DeviceHandler;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.handlers.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.handlers.RadioHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.RipperHandler;
import net.sourceforge.atunes.kernel.handlers.SystemTrayHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.TagEditionOperations;
import net.sourceforge.atunes.kernel.modules.updates.ApplicationUpdates;

public class AppMenuListener implements ActionListener {

	private ApplicationMenuBar menu;

	protected AppMenuListener(ApplicationMenuBar menu) {
		this.menu = menu;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(menu.getSelectRepository()))
			RepositoryHandler.getInstance().selectRepository();
		else if (e.getSource().equals(menu.getRefreshRepository()))
			RepositoryHandler.getInstance().refreshRepository();
		else if (e.getSource().equals(menu.getExit())) {
			VisualHandler.getInstance().setFullFrameVisible(false);
			Kernel.getInstance().finish();
		} else if (e.getSource().equals(menu.getEditPreferences()))
			ControllerProxy.getInstance().getEditPreferencesDialogController().start();
		else if (e.getSource().equals(menu.getTagView()))
			ControllerProxy.getInstance().getNavigationController().setNavigationView(NavigationControllerViews.TAG_VIEW);
		else if (e.getSource().equals(menu.getFolderView()))
			ControllerProxy.getInstance().getNavigationController().setNavigationView(NavigationControllerViews.FILE_VIEW);
		else if (e.getSource().equals(menu.getFavoriteView()))
			ControllerProxy.getInstance().getNavigationController().setNavigationView(NavigationControllerViews.FAVORITE_VIEW);
		else if (e.getSource().equals(menu.getDeviceView()))
			ControllerProxy.getInstance().getNavigationController().setNavigationView(NavigationControllerViews.DEVICE_VIEW);
		else if (e.getSource().equals(menu.getRadioView()))
			ControllerProxy.getInstance().getNavigationController().setNavigationView(NavigationControllerViews.RADIO_VIEW);
		else if (e.getSource().equals(menu.getPodcastFeedView()))
			ControllerProxy.getInstance().getNavigationController().setNavigationView(NavigationControllerViews.PODCAST_FEED_VIEW);
		else if (e.getSource().equals(menu.getShowStatusBar()))
			VisualHandler.getInstance().showStatusBar(menu.getShowStatusBar().isSelected());
		else if (e.getSource().equals(menu.getShowNavigationPanel()))
			VisualHandler.getInstance().showNavigationPanel(menu.getShowNavigationPanel().isSelected(), true);
		else if (e.getSource().equals(menu.getShowNavigationTable()))
			VisualHandler.getInstance().showNavigationTable(menu.getShowNavigationTable().isSelected());
		else if (e.getSource().equals(menu.getShowProperties()))
			VisualHandler.getInstance().showSongProperties(menu.getShowProperties().isSelected(), true);
		else if (e.getSource().equals(menu.getShowAudioScrobblerPanel()))
			VisualHandler.getInstance().showAudioScrobblerPanel(menu.getShowAudioScrobblerPanel().isSelected(), true);
		else if (e.getSource().equals(menu.getShowOSD())) {
			Kernel.getInstance().state.setShowOSD(menu.getShowOSD().isSelected());
			SystemTrayHandler.getInstance().setShowOSD(menu.getShowOSD().isSelected());
		} else if (e.getSource().equals(menu.getVolumeDown()))
			PlayerHandler.getInstance().volumeDown();
		else if (e.getSource().equals(menu.getVolumeUp()))
			PlayerHandler.getInstance().volumeUp();
		else if (e.getSource().equals(menu.getMute())) {
			ControllerProxy.getInstance().getPlayerControlsController().setMute(!PlayerHandler.getInstance().isMute());
			SystemTrayHandler.getInstance().setMute(!PlayerHandler.getInstance().isMute());
			PlayerHandler.getInstance().setMute(!PlayerHandler.getInstance().isMute());
		} else if (e.getSource().equals(menu.getEqualizer()))
			VisualHandler.getInstance().getEqualizerDialog().setVisible(true);
		else if (e.getSource().equals(menu.getPlayListPlay()))
			ControllerProxy.getInstance().getPlayListController().playSelectedAudioObject();
		else if (e.getSource().equals(menu.getPlayListEditTag()))
			PlayListHandler.getInstance().editTags();
		else if (e.getSource().equals(menu.getPlayListAutoSetTrack()))
			ControllerProxy.getInstance().getPlayListController().setTrackNumber();
		else if (e.getSource().equals(menu.getPlayListAutoSetLyrics()))
			ControllerProxy.getInstance().getPlayListController().setLyrics();
		else if (e.getSource().equals(menu.getPlayListAutoSetTitle()))
			ControllerProxy.getInstance().getPlayListController().setTitle();
		else if (e.getSource().equals(menu.getPlayListAutoSetGenre()))
			ControllerProxy.getInstance().getPlayListController().setGenre();
		else if (e.getSource().equals(menu.getPlayListSave()))
			PlayListHandler.getInstance().savePlaylist();
		else if (e.getSource().equals(menu.getPlayListLoad()))
			PlayListHandler.getInstance().loadPlaylist();
		else if (e.getSource().equals(menu.getPlayListFilter()))
			VisualHandler.getInstance().showFilter(true);
		else if (e.getSource().equals(menu.getPlayListInfo()))
			VisualHandler.getInstance().showInfo();
		else if (e.getSource().equals(menu.getPlayListDelete()))
			ControllerProxy.getInstance().getPlayListController().deleteSelection();
		else if (e.getSource().equals(menu.getPlayListClear()))
			PlayListHandler.getInstance().clearList();
		else if (e.getSource().equals(menu.getPlayListTop()))
			ControllerProxy.getInstance().getPlayListController().moveToTop();
		else if (e.getSource().equals(menu.getPlayListUp()))
			ControllerProxy.getInstance().getPlayListController().moveUp();
		else if (e.getSource().equals(menu.getPlayListDown()))
			ControllerProxy.getInstance().getPlayListController().moveDown();
		else if (e.getSource().equals(menu.getPlayListBottom()))
			ControllerProxy.getInstance().getPlayListController().moveToBottom();
		else if (e.getSource().equals(menu.getPlayListFavoriteSong()))
			FavoritesHandler.getInstance().addFavoriteSongs(AudioFile.getAudioFiles(ControllerProxy.getInstance().getPlayListController().getSelectedAudioObjects()));
		else if (e.getSource().equals(menu.getPlayListFavoriteAlbum()))
			FavoritesHandler.getInstance().addFavoriteAlbums(AudioFile.getAudioFiles(ControllerProxy.getInstance().getPlayListController().getSelectedAudioObjects()));
		else if (e.getSource().equals(menu.getPlayListFavoriteArtist()))
			FavoritesHandler.getInstance().addFavoriteArtists(AudioFile.getAudioFiles(ControllerProxy.getInstance().getPlayListController().getSelectedAudioObjects()));
		else if (e.getSource().equals(menu.getPlayListArtist()))
			ControllerProxy.getInstance().getPlayListController().setArtistAsPlaylist();
		else if (e.getSource().equals(menu.getPlayListAlbum()))
			ControllerProxy.getInstance().getPlayListController().setAlbumAsPlaylist();
		else if (e.getSource().equals(menu.getPlayListShowButtons()))
			ControllerProxy.getInstance().getPlayListController().showPlaylistControls(menu.getPlayListShowButtons().isSelected());
		else if (e.getSource().equals(menu.getDeviceConnect()))
			DeviceHandler.getInstance().connectDevice();
		else if (e.getSource().equals(menu.getDeviceRefresh()))
			DeviceHandler.getInstance().refreshDevice();
		else if (e.getSource().equals(menu.getDeviceDisconnect()))
			DeviceHandler.getInstance().disconnectDevice();
		else if (e.getSource().equals(menu.getDeviceViewByTag())) {
			Kernel.getInstance().state.setSortDeviceByTag(true);
			ControllerProxy.getInstance().getNavigationController().refreshDeviceTreeContent();
		} else if (e.getSource().equals(menu.getDeviceViewByFolder())) {
			Kernel.getInstance().state.setSortDeviceByTag(false);
			ControllerProxy.getInstance().getNavigationController().refreshDeviceTreeContent();
		} else if (e.getSource().equals(menu.getToolsExport()))
			ControllerProxy.getInstance().getExportOptionsController().beginExportProcess();
		else if (e.getSource().equals(menu.getRipCd()))
			RipperHandler.getInstance().startCdRipper();
		else if (e.getSource().equals(menu.getStats()))
			ControllerProxy.getInstance().getStatsDialogController().showStats();
		else if (e.getSource().equals(menu.getCheckUpdates()))
			ApplicationUpdates.checkUpdates(Kernel.getInstance().state.getProxy(), true);
		else if (e.getSource().equals(menu.getReportBugOrRequestFeature()))
			DesktopHandler.getInstance().openURL(Constants.REPORT_BUG_OR_REQUEST_FEATURE_URL);
		else if (e.getSource().equals(menu.getAboutItem()))
			VisualHandler.getInstance().showAboutDialog();
		else if (e.getSource().equals(menu.getAdd10RandomSongs()))
			PlayListHandler.getInstance().addRandomSongs(10);
		else if (e.getSource().equals(menu.getAdd50RandomSongs()))
			PlayListHandler.getInstance().addRandomSongs(50);
		else if (e.getSource().equals(menu.getAdd100RandomSongs()))
			PlayListHandler.getInstance().addRandomSongs(100);
		else if (e.getSource().equals(menu.getAdd10SongsMostPlayed()))
			PlayListHandler.getInstance().addSongsMostPlayed(10);
		else if (e.getSource().equals(menu.getAdd50SongsMostPlayed()))
			PlayListHandler.getInstance().addSongsMostPlayed(50);
		else if (e.getSource().equals(menu.getAdd100SongsMostPlayed()))
			PlayListHandler.getInstance().addSongsMostPlayed(100);
		else if (e.getSource().equals(menu.getAdd1AlbumsMostPlayed()))
			PlayListHandler.getInstance().addAlbumsMostPlayed(1);
		else if (e.getSource().equals(menu.getAdd5AlbumsMostPlayed()))
			PlayListHandler.getInstance().addAlbumsMostPlayed(5);
		else if (e.getSource().equals(menu.getAdd10AlbumsMostPlayed()))
			PlayListHandler.getInstance().addAlbumsMostPlayed(10);
		else if (e.getSource().equals(menu.getAdd1ArtistsMostPlayed()))
			PlayListHandler.getInstance().addArtistsMostPlayed(1);
		else if (e.getSource().equals(menu.getAdd5ArtistsMostPlayed()))
			PlayListHandler.getInstance().addArtistsMostPlayed(5);
		else if (e.getSource().equals(menu.getAdd10ArtistsMostPlayed()))
			PlayListHandler.getInstance().addArtistsMostPlayed(10);
		else if (e.getSource().equals(menu.getCoverNavigator()))
			VisualHandler.getInstance().showCoverNavigator();
		else if (e.getSource().equals(menu.getRepairTrackNumbers()))
			TagEditionOperations.repairTrackNumbers();
		else if (e.getSource().equals(menu.getRepairGenres()))
			TagEditionOperations.repairGenres();
		else if (e.getSource().equals(menu.getRepairAlbumNames()))
			TagEditionOperations.repairAlbumNames();
		else if (e.getSource().equals(menu.getAddRadio()))
			RadioHandler.getInstance().addRadio();
		else if (e.getSource().equals(menu.getAddPodcastFeed()))
			PodcastFeedHandler.getInstance().addPodcastFeed();
		else if (e.getSource().equals(menu.getFullScreen()))
			VisualHandler.getInstance().getFullScreenFrame().setVisible(true);
	}
}
