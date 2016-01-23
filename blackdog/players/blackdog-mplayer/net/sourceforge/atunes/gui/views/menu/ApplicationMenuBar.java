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

package net.sourceforge.atunes.gui.views.menu;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class ApplicationMenuBar extends JMenuBar {

	private static final long serialVersionUID = 234977404080329591L;

	private JMenu file;
	private JMenu edit;
	private JMenu view;
	private JMenu playList;
	private JMenu tools;
	private JMenu device;
	private JMenu about;

	// File Menu
	private JMenuItem selectRepository;
	private JMenuItem refreshRepository;
	private JMenuItem exit;

	// Edit Menu
	private JMenuItem repair;
	private JMenuItem editPreferences;

	// View Menu
	private JMenuItem tagView;
	private JMenuItem folderView;
	private JMenuItem favoriteView;
	private JMenuItem deviceView;
	private JMenuItem radioView;
	private JMenuItem podcastFeedView;
	private JCheckBoxMenuItem showStatusBar;
	private JCheckBoxMenuItem showNavigationPanel;
	private JCheckBoxMenuItem showNavigationTable;
	private JCheckBoxMenuItem showProperties;
	private JCheckBoxMenuItem showAudioScrobblerPanel;
	private JCheckBoxMenuItem showOSD;
	private JMenuItem fullScreen;

	// Player Menu
	private JMenuItem player;
	private JMenuItem equalizer;
	private JMenuItem volumeDown;
	private JMenuItem volumeUp;
	private JMenuItem mute;

	// PlayList Menu
	private JMenuItem playListPlay;
	private JMenuItem playListTags;
	private JMenuItem playListEditTag;
	private JMenuItem playListAutoSetTitle;
	private JMenuItem playListAutoSetLyrics;
	private JMenuItem playListAutoSetTrack;
	private JMenuItem playListAutoSetGenre;
	private JMenuItem playListSave;
	private JMenuItem playListLoad;
	private JMenuItem playListFilter;
	private JMenuItem playListInfo;
	private JMenuItem playListDelete;
	private JMenuItem playListClear;
	private JMenuItem playListTop;
	private JMenuItem playListUp;
	private JMenuItem playListDown;
	private JMenuItem playListBottom;
	private JMenuItem playListFavoriteSong;
	private JMenuItem playListFavoriteAlbum;
	private JMenuItem playListFavoriteArtist;
	private JMenuItem playListArtist;
	private JMenuItem playListAlbum;
	private JCheckBoxMenuItem playListShowButtons;

	// Tools Menu
	private JMenuItem toolsExport;
	private JMenuItem ripCd;
	private JMenuItem stats;
	private JMenuItem coverNavigator;
	private JMenuItem addRadio;
	private JMenuItem addPodcastFeed;

	// Repair Menu
	private JMenuItem repairTrackNumbers;
	private JMenuItem repairGenres;
	private JMenuItem repairAlbumNames;

	// Device Menu
	private JMenuItem deviceConnect;
	private JMenuItem deviceRefresh;
	private JMenuItem deviceDisconnect;
	private JRadioButtonMenuItem deviceViewByTag;
	private JRadioButtonMenuItem deviceViewByFolder;

	// Smart playlist Sub-Menu - in Playlist menu
	private JMenuItem smartPlayList;
	private JMenuItem add10RandomSongs;
	private JMenuItem add50RandomSongs;
	private JMenuItem add100RandomSongs;
	private JMenuItem add10SongsMostPlayed;
	private JMenuItem add50SongsMostPlayed;
	private JMenuItem add100SongsMostPlayed;
	private JMenuItem add1ArtistsMostPlayed;
	private JMenuItem add5ArtistsMostPlayed;
	private JMenuItem add10ArtistsMostPlayed;
	private JMenuItem add1AlbumsMostPlayed;
	private JMenuItem add5AlbumsMostPlayed;
	private JMenuItem add10AlbumsMostPlayed;

	// About Menu
	private JMenuItem checkUpdates;
	private JMenuItem reportBugOrRequestFeature;
	private JMenuItem aboutItem;

	public ApplicationMenuBar() {
		super();
		addMenus();
	}

	// Menu structure
	private void addMenus() {
		file = new JMenu(LanguageTool.getString("FILE"));
		selectRepository = new JMenuItem(LanguageTool.getString("SELECT_REPOSITORY"), ImageLoader.FOLDER);
		file.add(selectRepository);
		refreshRepository = new JMenuItem(LanguageTool.getString("REFRESH_REPOSITORY"), ImageLoader.REFRESH);
		file.add(refreshRepository);
		file.add(new JSeparator());
		exit = new JMenuItem(LanguageTool.getString("EXIT"), ImageLoader.EXIT);
		file.add(exit);

		edit = new JMenu(LanguageTool.getString("EDIT"));
		player = new JMenu(LanguageTool.getString("PLAYER"));
		edit.add(player);
		volumeUp = new JMenuItem(LanguageTool.getString("VOLUME_UP"));
		volumeDown = new JMenuItem(LanguageTool.getString("VOLUME_DOWN"));
		mute = new JMenuItem(LanguageTool.getString("MUTE"));
		equalizer = new JMenuItem(LanguageTool.getString("EQUALIZER"));
		player.add(equalizer);
		player.add(volumeUp);
		player.add(volumeDown);
		player.add(mute);
		editPreferences = new JMenuItem(StringUtils.getString(LanguageTool.getString("PREFERENCES"), "..."), ImageLoader.PREFS);
		edit.add(editPreferences);
		edit.add(new JSeparator());
		repair = new JMenu(LanguageTool.getString("REPAIR"));
		repairTrackNumbers = new JMenuItem(LanguageTool.getString("REPAIR_TRACK_NUMBERS"));
		repair.add(repairTrackNumbers);
		repairGenres = new JMenuItem(LanguageTool.getString("REPAIR_GENRES"));
		repair.add(repairGenres);
		repairAlbumNames = new JMenuItem(LanguageTool.getString("REPAIR_ALBUM_NAMES"));
		repair.add(repairAlbumNames);
		edit.add(repair);

		view = new JMenu(LanguageTool.getString("VIEW"));
		tagView = new JMenuItem(LanguageTool.getString("TAGS"), ImageLoader.INFO);
		folderView = new JMenuItem(LanguageTool.getString("FOLDERS"), ImageLoader.FOLDER);
		favoriteView = new JMenuItem(LanguageTool.getString("FAVORITES"), ImageLoader.FAVORITE);
		deviceView = new JMenuItem(LanguageTool.getString("DEVICE"), ImageLoader.DEVICE);
		radioView = new JMenuItem(LanguageTool.getString("RADIO_VIEW"), ImageLoader.RADIO_LITTLE);
		podcastFeedView = new JMenuItem(LanguageTool.getString("PODCAST_FEED_VIEW"), ImageLoader.RSS_LITTLE);
		showStatusBar = new JCheckBoxMenuItem(LanguageTool.getString("SHOW_STATUS_BAR"), null);
		showNavigationPanel = new JCheckBoxMenuItem(LanguageTool.getString("SHOW_NAVIGATION_PANEL"), null);
		showNavigationTable = new JCheckBoxMenuItem(LanguageTool.getString("SHOW_NAVIGATION_TABLE"), null);
		showProperties = new JCheckBoxMenuItem(LanguageTool.getString("SHOW_SONG_PROPERTIES"), null);
		showAudioScrobblerPanel = new JCheckBoxMenuItem(LanguageTool.getString("SHOW_AUDIOSCROBBLER"), null);
		showOSD = new JCheckBoxMenuItem(LanguageTool.getString("SHOW_OSD"), null);
		fullScreen = new JMenuItem(LanguageTool.getString("FULL_SCREEN"), ImageLoader.FULLSCREEN);
		playListShowButtons = new JCheckBoxMenuItem(LanguageTool.getString("SHOW_PLAYLIST_CONTROLS"), null);

		view.add(tagView);
		view.add(folderView);
		view.add(favoriteView);
		view.add(deviceView);
		view.add(radioView);
		view.add(podcastFeedView);
		view.add(new JSeparator());
		view.add(showStatusBar);
		view.add(playListShowButtons);
		view.add(showNavigationPanel);
		view.add(showNavigationTable);
		view.add(showProperties);
		view.add(showAudioScrobblerPanel);
		view.add(showOSD);
		view.add(new JSeparator());
		view.add(fullScreen);

		playList = new JMenu(LanguageTool.getString("PLAYLIST"));
		playListPlay = new JMenuItem(LanguageTool.getString("PLAY"), ImageLoader.PLAY_MENU);
		playListTags = new JMenu(LanguageTool.getString("TAGS"));
		playListEditTag = new JMenuItem(LanguageTool.getString("EDIT_TAG"));
		playListAutoSetLyrics = new JMenuItem(LanguageTool.getString("AUTO_SET_LYRICS"));
		playListAutoSetTitle = new JMenuItem(LanguageTool.getString("AUTO_SET_TITLE"));
		playListAutoSetTrack = new JMenuItem(LanguageTool.getString("AUTO_SET_TRACK_NUMBER"));
		playListAutoSetGenre = new JMenuItem(LanguageTool.getString("AUTO_SET_GENRE"));
		playListTags.add(playListEditTag);
		playListTags.add(playListAutoSetLyrics);
		playListTags.add(playListAutoSetTitle);
		playListTags.add(playListAutoSetTrack);
		playListTags.add(playListAutoSetGenre);

		smartPlayList = new JMenu(LanguageTool.getString("SMART_PLAYLIST"));
		add10RandomSongs = new JMenuItem(LanguageTool.getString("ADD_10_RANDOM_SONGS"));
		add50RandomSongs = new JMenuItem(LanguageTool.getString("ADD_50_RANDOM_SONGS"));
		add100RandomSongs = new JMenuItem(LanguageTool.getString("ADD_100_RANDOM_SONGS"));
		smartPlayList.add(add10RandomSongs);
		smartPlayList.add(add50RandomSongs);
		smartPlayList.add(add100RandomSongs);
		smartPlayList.add(new JSeparator());
		add10SongsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_10_SONGS_MOST_PLAYED"));
		add50SongsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_50_SONGS_MOST_PLAYED"));
		add100SongsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_100_SONGS_MOST_PLAYED"));
		smartPlayList.add(add10SongsMostPlayed);
		smartPlayList.add(add50SongsMostPlayed);
		smartPlayList.add(add100SongsMostPlayed);
		smartPlayList.add(new JSeparator());
		add1AlbumsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_ALBUM_MOST_PLAYED"));
		add5AlbumsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_5_ALBUMS_MOST_PLAYED"));
		add10AlbumsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_10_ALBUMS_MOST_PLAYED"));
		smartPlayList.add(add1AlbumsMostPlayed);
		smartPlayList.add(add5AlbumsMostPlayed);
		smartPlayList.add(add10AlbumsMostPlayed);
		smartPlayList.add(new JSeparator());
		add1ArtistsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_ARTIST_MOST_PLAYED"));
		add5ArtistsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_5_ARTISTS_MOST_PLAYED"));
		add10ArtistsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_10_ARTISTS_MOST_PLAYED"));
		smartPlayList.add(add1ArtistsMostPlayed);
		smartPlayList.add(add5ArtistsMostPlayed);
		smartPlayList.add(add10ArtistsMostPlayed);

		playListSave = new JMenuItem(StringUtils.getString(LanguageTool.getString("SAVE"), "..."), ImageLoader.SAVE);
		playListLoad = new JMenuItem(StringUtils.getString(LanguageTool.getString("LOAD"), "..."), ImageLoader.FOLDER);
		playListFilter = new JMenuItem(LanguageTool.getString("FILTER"), ImageLoader.SEARCH);
		playListInfo = new JMenuItem(LanguageTool.getString("INFO"), ImageLoader.INFO);
		playListDelete = new JMenuItem(LanguageTool.getString("REMOVE"), ImageLoader.REMOVE);
		playListClear = new JMenuItem(LanguageTool.getString("CLEAR"), ImageLoader.CLEAR);
		playListTop = new JMenuItem(LanguageTool.getString("MOVE_TO_TOP"), ImageLoader.GO_TOP);
		playListUp = new JMenuItem(LanguageTool.getString("MOVE_UP"), ImageLoader.GO_UP);
		playListDown = new JMenuItem(LanguageTool.getString("MOVE_DOWN"), ImageLoader.GO_DOWN);
		playListBottom = new JMenuItem(LanguageTool.getString("MOVE_TO_BOTTOM"), ImageLoader.GO_BOTTOM);
		playListFavoriteSong = new JMenuItem(LanguageTool.getString("SET_FAVORITE_SONG"), ImageLoader.FAVORITE);
		playListFavoriteAlbum = new JMenuItem(LanguageTool.getString("SET_FAVORITE_ALBUM"), ImageLoader.FAVORITE);
		playListFavoriteArtist = new JMenuItem(LanguageTool.getString("SET_FAVORITE_ARTIST"), ImageLoader.FAVORITE);
		playListArtist = new JMenuItem(LanguageTool.getString("SET_ARTIST_AS_PLAYLIST"), ImageLoader.ARTIST);
		playListAlbum = new JMenuItem(LanguageTool.getString("SET_ALBUM_AS_PLAYLIST"), ImageLoader.ALBUM);

		playList.add(playListPlay);
		playList.add(playListInfo);
		playList.add(new JSeparator());
		playList.add(playListTags);
		playList.add(new JSeparator());
		playList.add(playListSave);
		playList.add(playListLoad);
		playList.add(new JSeparator());
		playList.add(playListFilter);
		playList.add(new JSeparator());
		playList.add(playListDelete);
		playList.add(playListClear);
		playList.add(new JSeparator());
		playList.add(playListTop);
		playList.add(playListUp);
		playList.add(playListDown);
		playList.add(playListBottom);
		playList.add(new JSeparator());
		playList.add(smartPlayList);
		playList.add(new JSeparator());
		playList.add(playListFavoriteSong);
		playList.add(playListFavoriteAlbum);
		playList.add(playListFavoriteArtist);
		playList.add(new JSeparator());
		playList.add(playListArtist);
		playList.add(playListAlbum);

		tools = new JMenu(LanguageTool.getString("TOOLS"));
		toolsExport = new JMenuItem(StringUtils.getString(LanguageTool.getString("EXPORT"), "..."), ImageLoader.EXPORT_FILE);
		ripCd = new JMenuItem(StringUtils.getString(LanguageTool.getString("RIP_CD"), "..."), ImageLoader.CD_AUDIO_TINY);
		stats = new JMenuItem(LanguageTool.getString("STATS"), ImageLoader.STATS);
		coverNavigator = new JMenuItem(LanguageTool.getString("COVER_NAVIGATOR"), ImageLoader.CD_COVER);
		addRadio = new JMenuItem(LanguageTool.getString("ADD_RADIO"), ImageLoader.RADIO_ADD);
		addPodcastFeed = new JMenuItem(LanguageTool.getString("ADD_PODCAST_FEED"), ImageLoader.RSS_LITTLE);
		tools.add(toolsExport);
		tools.add(ripCd);
		tools.add(new JSeparator());
		tools.add(stats);
		tools.add(coverNavigator);
		tools.add(new JSeparator());
		tools.add(addRadio);
		tools.add(addPodcastFeed);

		device = new JMenu(LanguageTool.getString("DEVICE"));
		deviceConnect = new JMenuItem(LanguageTool.getString("CONNECT"));
		device.add(deviceConnect);
		deviceRefresh = new JMenuItem(LanguageTool.getString("REFRESH"));
		device.add(deviceRefresh);
		deviceDisconnect = new JMenuItem(LanguageTool.getString("DISCONNECT"));
		device.add(deviceDisconnect);
		device.add(new JSeparator());
		deviceViewByTag = new JRadioButtonMenuItem(LanguageTool.getString("SORT_BY_TAG"));
		device.add(deviceViewByTag);
		deviceViewByFolder = new JRadioButtonMenuItem(LanguageTool.getString("SORT_BY_FOLDER"));
		device.add(deviceViewByFolder);
		ButtonGroup group2 = new ButtonGroup();

		group2.add(deviceViewByTag);
		group2.add(deviceViewByFolder);

		about = new JMenu(LanguageTool.getString("ABOUT"));
		checkUpdates = new JMenuItem(LanguageTool.getString("CHECK_FOR_UPDATES"), ImageLoader.CHECK_FOR_UPDATES);
		reportBugOrRequestFeature = new JMenuItem(LanguageTool.getString("REPORT_BUG_OR_REQUEST_FEATURE"), ImageLoader.REPORT_BUG_OR_REQUEST_FEATURE);
		aboutItem = new JMenuItem(LanguageTool.getString("ABOUT"));

		about.add(checkUpdates);
		about.add(reportBugOrRequestFeature);
		about.add(new JSeparator());
		about.add(aboutItem);

		add(file);
		add(edit);
		add(view);
		add(playList);
		add(device);
		add(tools);
		add(about);
	}

	public JMenuItem getAboutItem() {
		return aboutItem;
	}

	public JMenuItem getAdd100RandomSongs() {
		return add100RandomSongs;
	}

	public JMenuItem getAdd100SongsMostPlayed() {
		return add100SongsMostPlayed;
	}

	public JMenuItem getAdd10AlbumsMostPlayed() {
		return add10AlbumsMostPlayed;
	}

	public JMenuItem getAdd10ArtistsMostPlayed() {
		return add10ArtistsMostPlayed;
	}

	public JMenuItem getAdd10RandomSongs() {
		return add10RandomSongs;
	}

	public JMenuItem getAdd10SongsMostPlayed() {
		return add10SongsMostPlayed;
	}

	public JMenuItem getAdd1AlbumsMostPlayed() {
		return add1AlbumsMostPlayed;
	}

	public JMenuItem getAdd1ArtistsMostPlayed() {
		return add1ArtistsMostPlayed;
	}

	public JMenuItem getAdd50RandomSongs() {
		return add50RandomSongs;
	}

	public JMenuItem getAdd50SongsMostPlayed() {
		return add50SongsMostPlayed;
	}

	public JMenuItem getAdd5AlbumsMostPlayed() {
		return add5AlbumsMostPlayed;
	}

	public JMenuItem getAdd5ArtistsMostPlayed() {
		return add5ArtistsMostPlayed;
	}

	/**
	 * @return the addPodcastFeed
	 */
	public JMenuItem getAddPodcastFeed() {
		return addPodcastFeed;
	}

	/**
	 * @return the addRadio
	 */
	public JMenuItem getAddRadio() {
		return addRadio;
	}

	public JMenuItem getCheckUpdates() {
		return checkUpdates;
	}

	/**
	 * @return the coverNavigator
	 */
	public JMenuItem getCoverNavigator() {
		return coverNavigator;
	}

	public JMenuItem getDeviceConnect() {
		return deviceConnect;
	}

	public JMenuItem getDeviceDisconnect() {
		return deviceDisconnect;
	}

	public JMenuItem getDeviceRefresh() {
		return deviceRefresh;
	}

	public JMenuItem getDeviceView() {
		return deviceView;
	}

	public JRadioButtonMenuItem getDeviceViewByFolder() {
		return deviceViewByFolder;
	}

	public JRadioButtonMenuItem getDeviceViewByTag() {
		return deviceViewByTag;
	}

	public JMenuItem getEditPreferences() {
		return editPreferences;
	}

	public JMenuItem getEqualizer() {
		return equalizer;
	}

	public JMenuItem getExit() {
		return exit;
	}

	public JMenuItem getFavoriteView() {
		return favoriteView;
	}

	public JMenuItem getFolderView() {
		return folderView;
	}

	/**
	 * @return the fullScreen
	 */
	public JMenuItem getFullScreen() {
		return fullScreen;
	}

	public JPopupMenu getMenuAsPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		menu.add(file);
		menu.add(edit);
		menu.add(view);
		menu.add(playList);
		menu.add(tools);
		menu.add(about);
		return menu;
	}

	public JMenuItem getMute() {
		return mute;
	}

	public JMenuItem getPlayListAlbum() {
		return playListAlbum;
	}

	public JMenuItem getPlayListArtist() {
		return playListArtist;
	}

	public JMenuItem getPlayListAutoSetGenre() {
		return playListAutoSetGenre;
	}

	public JMenuItem getPlayListAutoSetLyrics() {
		return playListAutoSetLyrics;
	}

	public JMenuItem getPlayListAutoSetTitle() {
		return playListAutoSetTitle;
	}

	public JMenuItem getPlayListAutoSetTrack() {
		return playListAutoSetTrack;
	}

	public JMenuItem getPlayListBottom() {
		return playListBottom;
	}

	public JMenuItem getPlayListClear() {
		return playListClear;
	}

	public JMenuItem getPlayListDelete() {
		return playListDelete;
	}

	public JMenuItem getPlayListDown() {
		return playListDown;
	}

	public JMenuItem getPlayListEditTag() {
		return playListEditTag;
	}

	public JMenuItem getPlayListFavoriteAlbum() {
		return playListFavoriteAlbum;
	}

	public JMenuItem getPlayListFavoriteArtist() {
		return playListFavoriteArtist;
	}

	public JMenuItem getPlayListFavoriteSong() {
		return playListFavoriteSong;
	}

	public JMenuItem getPlayListFilter() {
		return playListFilter;
	}

	public JMenuItem getPlayListInfo() {
		return playListInfo;
	}

	public JMenuItem getPlayListLoad() {
		return playListLoad;
	}

	public JMenuItem getPlayListPlay() {
		return playListPlay;
	}

	public JMenuItem getPlayListSave() {
		return playListSave;
	}

	public JCheckBoxMenuItem getPlayListShowButtons() {
		return playListShowButtons;
	}

	public JMenuItem getPlayListTop() {
		return playListTop;
	}

	public JMenuItem getPlayListUp() {
		return playListUp;
	}

	public JMenuItem getPodcastFeedView() {
		return podcastFeedView;
	}

	public JMenuItem getRadioView() {
		return radioView;
	}

	public JMenuItem getRefreshRepository() {
		return refreshRepository;
	}

	/**
	 * @return the repairAlbumNames
	 */
	public JMenuItem getRepairAlbumNames() {
		return repairAlbumNames;
	}

	/**
	 * @return the repairGenres
	 */
	public JMenuItem getRepairGenres() {
		return repairGenres;
	}

	/**
	 * @return the repairTrackNumbers
	 */
	public JMenuItem getRepairTrackNumbers() {
		return repairTrackNumbers;
	}

	/**
	 * @return the reportBugOrRequestFeature
	 */
	public JMenuItem getReportBugOrRequestFeature() {
		return reportBugOrRequestFeature;
	}

	public JMenuItem getRipCd() {
		return ripCd;
	}

	public JMenuItem getSelectRepository() {
		return selectRepository;
	}

	public JCheckBoxMenuItem getShowAudioScrobblerPanel() {
		return showAudioScrobblerPanel;
	}

	public JCheckBoxMenuItem getShowNavigationPanel() {
		return showNavigationPanel;
	}

	public JCheckBoxMenuItem getShowNavigationTable() {
		return showNavigationTable;
	}

	public JCheckBoxMenuItem getShowOSD() {
		return showOSD;
	}

	public JCheckBoxMenuItem getShowProperties() {
		return showProperties;
	}

	public JCheckBoxMenuItem getShowStatusBar() {
		return showStatusBar;
	}

	public JMenuItem getStats() {
		return stats;
	}

	public JMenuItem getTagView() {
		return tagView;
	}

	public JMenuItem getToolsExport() {
		return toolsExport;
	}

	public JMenuItem getVolumeDown() {
		return volumeDown;
	}

	public JMenuItem getVolumeUp() {
		return volumeUp;
	}
}
