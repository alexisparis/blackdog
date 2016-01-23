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
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.gui.views.menu.ApplicationMenuBar;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.model.Controller;

/**
 * @author fleax
 * 
 */
public class MenuController extends Controller {

	ApplicationMenuBar menu;

	public MenuController(ApplicationMenuBar menu) {
		super();
		this.menu = menu;
		addBindings();
		addStateBindings();
	}

	@Override
	protected void addBindings() {
		// Keys
		menu.getRefreshRepository().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

		menu.getTagView().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menu.getFolderView().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
		menu.getFavoriteView().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));
		menu.getDeviceView().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK));
		menu.getRadioView().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.ALT_MASK));
		menu.getPodcastFeedView().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, ActionEvent.ALT_MASK));

		menu.getFullScreen().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));

		menu.getVolumeDown().setAccelerator(KeyStroke.getKeyStroke('-'));
		menu.getVolumeUp().setAccelerator(KeyStroke.getKeyStroke('+'));
		menu.getMute().setAccelerator(KeyStroke.getKeyStroke('m'));

		menu.getPlayListDelete().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		menu.getPlayListPlay().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		menu.getPlayListEditTag().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
		menu.getPlayListInfo().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		menu.getPlayListSave().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menu.getPlayListLoad().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		menu.getPlayListClear().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		menu.getPlayListTop().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		menu.getPlayListDown().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, ActionEvent.CTRL_MASK));
		menu.getPlayListUp().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, ActionEvent.CTRL_MASK));
		menu.getPlayListBottom().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
		menu.getPlayListFavoriteSong().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		menu.getPlayListFavoriteAlbum().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		menu.getPlayListFavoriteArtist().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		menu.getPlayListFilter().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		// End Keys

		// Listener
		AppMenuListener listener = new AppMenuListener(menu);
		menu.getSelectRepository().addActionListener(listener);
		menu.getRefreshRepository().addActionListener(listener);
		menu.getExit().addActionListener(listener);
		menu.getEditPreferences().addActionListener(listener);
		menu.getTagView().addActionListener(listener);
		menu.getFolderView().addActionListener(listener);
		menu.getFavoriteView().addActionListener(listener);
		menu.getDeviceView().addActionListener(listener);
		menu.getRadioView().addActionListener(listener);
		menu.getPodcastFeedView().addActionListener(listener);
		menu.getShowStatusBar().addActionListener(listener);
		menu.getShowNavigationPanel().addActionListener(listener);
		menu.getShowNavigationTable().addActionListener(listener);
		menu.getShowProperties().addActionListener(listener);
		menu.getShowAudioScrobblerPanel().addActionListener(listener);
		menu.getShowOSD().addActionListener(listener);
		menu.getFullScreen().addActionListener(listener);
		menu.getVolumeDown().addActionListener(listener);
		menu.getVolumeUp().addActionListener(listener);
		menu.getMute().addActionListener(listener);
		menu.getEqualizer().addActionListener(listener);
		menu.getPlayListPlay().addActionListener(listener);
		menu.getPlayListEditTag().addActionListener(listener);
		menu.getPlayListAutoSetTrack().addActionListener(listener);
		menu.getPlayListAutoSetTitle().addActionListener(listener);
		menu.getPlayListAutoSetLyrics().addActionListener(listener);
		menu.getPlayListAutoSetGenre().addActionListener(listener);
		menu.getPlayListSave().addActionListener(listener);
		menu.getPlayListLoad().addActionListener(listener);
		menu.getPlayListFilter().addActionListener(listener);
		menu.getPlayListInfo().addActionListener(listener);
		menu.getPlayListDelete().addActionListener(listener);
		menu.getPlayListClear().addActionListener(listener);
		menu.getPlayListTop().addActionListener(listener);
		menu.getPlayListUp().addActionListener(listener);
		menu.getPlayListDown().addActionListener(listener);
		menu.getPlayListBottom().addActionListener(listener);
		menu.getPlayListFavoriteSong().addActionListener(listener);
		menu.getPlayListFavoriteAlbum().addActionListener(listener);
		menu.getPlayListFavoriteArtist().addActionListener(listener);
		menu.getPlayListArtist().addActionListener(listener);
		menu.getPlayListAlbum().addActionListener(listener);
		menu.getPlayListShowButtons().addActionListener(listener);
		menu.getDeviceConnect().addActionListener(listener);
		menu.getDeviceRefresh().addActionListener(listener);
		menu.getDeviceDisconnect().addActionListener(listener);
		menu.getDeviceViewByTag().addActionListener(listener);
		menu.getDeviceViewByFolder().addActionListener(listener);
		menu.getRepairTrackNumbers().addActionListener(listener);
		menu.getRepairGenres().addActionListener(listener);
		menu.getRepairAlbumNames().addActionListener(listener);
		menu.getToolsExport().addActionListener(listener);
		menu.getRipCd().addActionListener(listener);
		menu.getStats().addActionListener(listener);
		menu.getCoverNavigator().addActionListener(listener);
		menu.getCheckUpdates().addActionListener(listener);
		menu.getReportBugOrRequestFeature().addActionListener(listener);
		menu.getAboutItem().addActionListener(listener);

		menu.getAdd10RandomSongs().addActionListener(listener);
		menu.getAdd50RandomSongs().addActionListener(listener);
		menu.getAdd100RandomSongs().addActionListener(listener);
		menu.getAdd10SongsMostPlayed().addActionListener(listener);
		menu.getAdd50SongsMostPlayed().addActionListener(listener);
		menu.getAdd100SongsMostPlayed().addActionListener(listener);
		menu.getAdd1AlbumsMostPlayed().addActionListener(listener);
		menu.getAdd5AlbumsMostPlayed().addActionListener(listener);
		menu.getAdd10AlbumsMostPlayed().addActionListener(listener);
		menu.getAdd1ArtistsMostPlayed().addActionListener(listener);
		menu.getAdd5ArtistsMostPlayed().addActionListener(listener);
		menu.getAdd10ArtistsMostPlayed().addActionListener(listener);

		menu.getAddRadio().addActionListener(listener);
		menu.getAddPodcastFeed().addActionListener(listener);
	}

	@Override
	protected void addStateBindings() {
		disablePlayListItems(true, false);
		menu.getShowOSD().setSelected(Kernel.getInstance().state.isShowOSD());

		setDeviceConnected(false);
	}

	public void disablePlayListItems(boolean disable, boolean radioOrPodcastFeedEntrySelected) {
		menu.getPlayListPlay().setEnabled(!disable);
		menu.getPlayListInfo().setEnabled(!disable);
		menu.getPlayListDelete().setEnabled(!disable);
		menu.getPlayListClear().setEnabled(true);
		menu.getPlayListTop().setEnabled(!disable);
		menu.getPlayListUp().setEnabled(!disable);
		menu.getPlayListDown().setEnabled(!disable);
		menu.getPlayListBottom().setEnabled(!disable);
		menu.getPlayListFavoriteSong().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		menu.getPlayListFavoriteAlbum().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		menu.getPlayListFavoriteArtist().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		menu.getPlayListArtist().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		menu.getPlayListAlbum().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		menu.getPlayListEditTag().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		menu.getPlayListAutoSetLyrics().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		menu.getPlayListAutoSetGenre().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		menu.getPlayListAutoSetTitle().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		menu.getPlayListAutoSetTrack().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
	}

	public void enableSavePlaylist(boolean enable) {
		menu.getPlayListSave().setEnabled(enable);
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}

	public void setDeviceConnected(boolean enable) {
		menu.getDeviceConnect().setEnabled(!enable);
		menu.getDeviceRefresh().setEnabled(enable);
		menu.getDeviceDisconnect().setEnabled(enable);
	}

	public void setRipCDEnabled(boolean enable) {
		menu.getRipCd().setEnabled(enable);
	}

	public void setShowAudioScrobblerPanel(boolean show) {
		menu.getShowAudioScrobblerPanel().setSelected(show);
	}

	public void setShowNavigationPanel(boolean show) {
		menu.getShowNavigationPanel().setSelected(show);
	}

	public void setShowNavigationTable(boolean show) {
		menu.getShowNavigationTable().setSelected(show);
	}

	public void setShowOSD(boolean enable) {
		menu.getShowOSD().setSelected(enable);
	}

	public void setShowPlaylistControls(boolean enable) {
		menu.getPlayListShowButtons().setSelected(enable);
	}

	public void setShowSongProperties(boolean show) {
		menu.getShowProperties().setSelected(show);
	}

	public void setShowStatusBar(boolean show) {
		menu.getShowStatusBar().setSelected(show);
	}

	public void setSortDeviceByTag(boolean enable) {
		menu.getDeviceViewByTag().setSelected(enable);
		menu.getDeviceViewByFolder().setSelected(!enable);
	}
}
