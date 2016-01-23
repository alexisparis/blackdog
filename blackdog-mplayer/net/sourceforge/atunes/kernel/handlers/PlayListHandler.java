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

package net.sourceforge.atunes.kernel.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.executors.BackgroundExecutor;
import net.sourceforge.atunes.kernel.modules.playlist.ListOfPlayLists;
import net.sourceforge.atunes.kernel.modules.playlist.PlayList;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListCommonOps;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListEventListener;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListFilterOps;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListRowOps;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class PlayListHandler implements PlayListEventListener {

	/**
	 * Logger
	 */
	static Logger logger = new Logger();

	/**
	 * Singleton instance
	 */
	private static PlayListHandler instance = new PlayListHandler();

	/**
	 * Private constructor
	 */
	private PlayListHandler() {
		// Nothing to do
	}

	/**
	 * Method to access singleton instance
	 * 
	 * @return PlayListHandler
	 */
	public static PlayListHandler getInstance() {
		return instance;
	}

	/**
	 * Gets n albums most played and adds to play list
	 * 
	 * @param n
	 */
	public void addAlbumsMostPlayed(int n) {
		logger.debug(LogCategories.HANDLER, new String[] { Integer.toString(n) });

		// Get n most played albums
		List<Album> albums = RepositoryHandler.getInstance().getMostPlayedAlbums(n);

		// Songs selected
		List<AudioObject> songsSelected = new ArrayList<AudioObject>();

		// Add album songs
		for (Album a : albums) {
			songsSelected.addAll(a.getAudioObjects());
		}

		// Sort
		songsSelected = RepositoryHandler.getInstance().sort(songsSelected);

		// Add to playlist
		addToPlayList(songsSelected);
	}

	/**
	 * Gets n artists most played and adds to play list
	 * 
	 * @param n
	 */
	public void addArtistsMostPlayed(int n) {
		logger.debug(LogCategories.HANDLER, new String[] { Integer.toString(n) });

		// Get n most played albums
		List<Artist> artists = RepositoryHandler.getInstance().getMostPlayedArtists(n);

		// Songs selected
		List<AudioFile> songsSelected = new ArrayList<AudioFile>();

		// Add album songs
		for (Artist a : artists) {
			songsSelected.addAll(a.getAudioFiles());
		}

		// Sort
		List<AudioObject> songsSorted = RepositoryHandler.getInstance().sort(new ArrayList<AudioObject>(songsSelected));

		// Add to playlist
		addToPlayList(songsSorted);
	}

	/**
	 * Gets a number of random songs and adds to play list
	 * 
	 * @param n
	 */
	public void addRandomSongs(int n) {
		logger.debug(LogCategories.HANDLER, new String[] { Integer.toString(n) });

		// Get reference to Repository songs
		List<AudioFile> songs = RepositoryHandler.getInstance().getSongs();

		// Songs selected
		List<AudioObject> songsSelected = new ArrayList<AudioObject>();

		// Initialize random generator
		Random r = new Random(new Date().getTime());

		// Get n songs
		for (int i = 0; i < n; i++) {
			// Get song number
			int number = r.nextInt(songs.size());

			// Add selectedSong
			songsSelected.add(songs.get(number));
		}

		// Sort
		songsSelected = RepositoryHandler.getInstance().sort(songsSelected);

		// Add to playlist
		addToPlayList(songsSelected);
	}

	/**
	 * Gets n songs most played and adds to play list
	 * 
	 * @param n
	 */
	public void addSongsMostPlayed(int n) {
		logger.debug(LogCategories.HANDLER, new String[] { Integer.toString(n) });

		// Get songs
		List<AudioFile> songsSelected = RepositoryHandler.getInstance().getMostPlayedSongs(n);

		// Sort
		List<AudioObject> songsSorted = RepositoryHandler.getInstance().sort(new ArrayList<AudioObject>(songsSelected));

		// Add to playlist
		addToPlayList(songsSorted);
	}

	/**
	 * Adds audio objects to play list
	 * 
	 * @param audioObjects
	 */
	public void addToPlayList(List<AudioObject> audioObjects) {
		// If null or empty, nothing to do
		if (audioObjects == null || audioObjects.isEmpty())
			return;

		// Get play list
		PlayList playList = PlayListCommonOps.getPlayList();

		// If play list is filtered remove filter
		if (PlayListFilterOps.isFiltered())
			setFilter(null);

		// Add songs to play list
		playList.addAll(audioObjects);

		// If play list was empty, then set song as selected for play
		if (playList.size() == audioObjects.size()) {
			int selectedSong = 0;

			// If shuffle enabled, select a random audio object
			if (PlayerHandler.getInstance().isShuffle()) {
				selectedSong = playList.getRandomPosition();
			}

			ControllerProxy.getInstance().getPlayListController().setSelectedSong(selectedSong);
			selectedAudioObjectChanged(audioObjects.get(selectedSong));
			playList.setNextAudioObject(selectedSong);
		}

		// Notify controller
		ControllerProxy.getInstance().getPlayListController().notifyAudioObjectsAddedToController(audioObjects, -1);

		// Update play list number in status bar
		VisualHandler.getInstance().showPlayListInformation(playList);

		logger.info(LogCategories.HANDLER, StringUtils.getString(audioObjects.size(), " songs added to play list"));
	}

	/**
	 * Method of PlayListEventListener. Called when play list is cleared
	 */
	@Override
	public void clear() {
		// Clear file properties
		if (Kernel.getInstance().state.isShowSongProperties())
			ControllerProxy.getInstance().getFilePropertiesController().clear();

		// Clear audioscrobbler information
		if (Kernel.getInstance().state.isUseAudioScrobbler()) {
			AudioScrobblerServiceHandler.getInstance().clear();
		}

		VisualHandler.getInstance().getFullScreenFrame().setAudioObject(null);
	}

	/**
	 * Removes all songs from play list
	 */
	public void clearList() {
		logger.debug(LogCategories.HANDLER);

		// Remove filter
		setFilter(null);

		// Remove songs from model
		VisualHandler.getInstance().getPlayListTableModel().removeSongs();

		// Set selection interval to none
		VisualHandler.getInstance().getPlayListTable().getSelectionModel().setSelectionInterval(-1, -1);

		PlayList playList = PlayListCommonOps.getPlayList();
		if (!playList.isEmpty()) {
			// Clear play list
			playList.clear();

			// If app is playing STOP
			PlayerHandler.getInstance().stop();

			// Set first song as next file
			playList.setNextAudioObject(0);

			// Disable buttons
			ControllerProxy.getInstance().getPlayListControlsController().enableSaveButton(false);
			ControllerProxy.getInstance().getMenuController().enableSavePlaylist(false);

			// Update song number
			VisualHandler.getInstance().showPlayListInformation(playList);

			logger.info(LogCategories.HANDLER, "Play list clear");
		}

		// Fire clear event
		clear();
	}

	/**
	 * Edit tags of selected songs on play list
	 */
	public void editTags() {
		logger.debug(LogCategories.HANDLER);
		// Get selected songs
		List<AudioObject> audioObjects = PlayListCommonOps.getSelectedSongs();
		// Start edit by opening edit dialog
		ControllerProxy.getInstance().getEditTagDialogController().editFiles(AudioFile.getAudioFiles(audioObjects));
	}

	/**
	 * Called by kernel when application is finishing
	 */
	public void finish() {
		logger.debug(LogCategories.HANDLER);
		// Store play list
		ApplicationDataHandler.getInstance().persistPlayList();
	}

	/**
	 * Retrieves stored play list and loads it. This method is used when opening
	 * application, to load play list of previous execution
	 */
	public void getLastPlayList() {
		logger.debug(LogCategories.HANDLER);

		// Get playlists from application cache
		final ListOfPlayLists listOfPlayLists = ApplicationDataHandler.getInstance().retrievePlayListCache();

		// Set selected play list as default 
		final PlayList lastPlayList = listOfPlayLists.getSelectedPlayListObject();

		// Add playlists
		MultiplePlaylistHandler.getInstance().addListOfPlayLists(listOfPlayLists);

		// If play list is not empty
		if (lastPlayList.size() > 0) {
			// Check that at least first entry exists. This is to avoid loading a play list that contains songs deleted or moved
			if (lastPlayList.get(0) instanceof Radio || lastPlayList.get(0) instanceof PodcastFeedEntry || lastPlayList.get(0) instanceof AudioFile) {

				// When possible, take songs from Repository instead of from PlayList stored.
				// This way we prevent to have duplicated objects in PlayList for same song, one of PlayList and one of Repository
				@SuppressWarnings("unchecked")
				List<AudioObject> audioObjects = (List<AudioObject>) lastPlayList.clone();
				lastPlayList.clear();
				//TODO also for radios and podcast feed entries
				for (AudioObject ao : audioObjects) {
					AudioFile repositoryFile = RepositoryHandler.getInstance().getFileIfLoaded(ao.getUrl());
					if (repositoryFile == null)
						lastPlayList.add(ao);
					else
						lastPlayList.add(repositoryFile);
				}

				// Set play list
				PlayListCommonOps.setPlayList(lastPlayList);

				// Start to play if configured
				startToPlay(Kernel.getInstance().state.isPlayAtStartup());
			}
		}

		// Refresh play list
		// For some strange reason, this is needed even if play list is empty
		PlayListCommonOps.refreshPlayList();
	}

	/**
	 * Loads play list from a file
	 */
	public void loadPlaylist() {
		logger.debug(LogCategories.HANDLER);
		JFileChooser fileChooser = new JFileChooser();
		FileFilter filter = PlayListIO.getPlaylistFileFilter();
		// Open file chooser
		if (VisualHandler.getInstance().showOpenDialog(fileChooser, filter) == JFileChooser.APPROVE_OPTION) {
			// Get selected file
			File file = fileChooser.getSelectedFile();

			// If exists...
			if (file.exists()) {
				// Read file names
				List<String> filesToLoad = PlayListIO.read(file);
				// Background loading - but only when returned array is not null (Progress dialog hangs otherwise)
				if (filesToLoad != null)
					BackgroundExecutor.loadPlayList(filesToLoad);
			} else
				VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("FILE_NOT_FOUND"));
		}
	}

	/**
	 * Move rows of play list down
	 * 
	 * @param rows
	 */
	public void moveDown(int[] rows) {
		logger.debug(LogCategories.HANDLER);
		PlayListRowOps.moveDown(rows);
	}

	/**
	 * Move rows to bottom of play list
	 * 
	 * @param rows
	 */
	public void moveToBottom(int[] rows) {
		logger.debug(LogCategories.HANDLER);
		PlayListRowOps.moveToBottom(rows);
	}

	/**
	 * Move rows to top of play list
	 * 
	 * @param rows
	 */
	public void moveToTop(int[] rows) {
		logger.debug(LogCategories.HANDLER);
		PlayListRowOps.moveToTop(rows);
	}

	/**
	 * Move rows of play list up
	 * 
	 * @param rows
	 */
	public void moveUp(int[] rows) {
		logger.debug(LogCategories.HANDLER);
		PlayListRowOps.moveUp(rows);
	}

	/**
	 * Plays song passed to argument. If if not added to play list, it adds
	 */
	public void playNow(AudioObject song) {
		if (!PlayListCommonOps.getPlayList().contains(song)) {
			ArrayList<AudioObject> list = new ArrayList<AudioObject>();
			list.add(song);
			PlayListCommonOps.addToPlayListAndPlay(list);
		} else {
			PlayerHandler.getInstance().setPlayListPositionToPlay(PlayListCommonOps.getPlayList().indexOf(song));
			PlayerHandler.getInstance().play(false);
		}
	}

	/**
	 * Removes songs from play list
	 * 
	 * @param rows
	 */
	public void removeSongs(int[] rows) {
		PlayListRowOps.removeSongs(rows);
		logger.info(LogCategories.HANDLER, StringUtils.getString(rows.length, " songs removed from play list"));
	}

	/**
	 * Saves current play list to a file
	 */
	public void savePlaylist() {
		logger.debug(LogCategories.HANDLER);
		JFileChooser fileChooser = new JFileChooser();
		FileFilter filter = PlayListIO.getPlaylistFileFilter();
		// Open file chooser
		if (VisualHandler.getInstance().showSaveDialog(fileChooser, filter) == JFileChooser.APPROVE_OPTION) {
			// Get selected file
			File file = fileChooser.getSelectedFile();

			// If filename have incorrect extension, add it
			if (!file.getName().toUpperCase().endsWith(PlayListIO.M3U_FILE_EXTENSION.toUpperCase()))
				file = new File(StringUtils.getString(file.getAbsolutePath(), ".", PlayListIO.M3U_FILE_EXTENSION));

			// If file does not exist, or exist and overwrite is confirmed, then write file
			if (!file.exists()
					|| (file.exists() && VisualHandler.getInstance().showConfirmationDialog(LanguageTool.getString("OVERWRITE_FILE"), LanguageTool.getString("INFO")) == JOptionPane.OK_OPTION)) {
				PlayListIO.write(PlayListCommonOps.getPlayList(), file);
			}
		}
	}

	/**
	 * Method of PlayListEventListener. Called when current song changes
	 */
	@Override
	public void selectedAudioObjectChanged(AudioObject audioObject) {
		// Update file properties
		if (Kernel.getInstance().state.isShowSongProperties())
			ControllerProxy.getInstance().getFilePropertiesController().updateValues(audioObject);

		// Update full screen information
		VisualHandler.getInstance().getFullScreenFrame().setAudioObject(audioObject);

		// Update audioscrobbler information
		if (Kernel.getInstance().state.isUseAudioScrobbler())
			ControllerProxy.getInstance().getAudioScrobblerController().updatePanel(audioObject);

		// Disable slider if audio object is a radio or podcast feed entry
		ControllerProxy.getInstance().getPlayerControlsController().setSlidable(audioObject instanceof AudioFile);

	}

	/**
	 * Applies filter to play list.
	 * 
	 * @param filter
	 */
	public void setFilter(String filter) {
		PlayListFilterOps.setFilter(filter);
	}

	/**
	 * Sorts play list with a given comparator
	 * 
	 * @param comp
	 */
	public void sortPlayList(Comparator<AudioObject> comp) {
		// If comparator is null, do nothing
		if (comp == null)
			return;

		logger.debug(LogCategories.HANDLER);

		AudioObject currentAudioObject = PlayListCommonOps.getCurrentAudioObject();
		PlayList currentPlaylist = PlayListCommonOps.getPlayList();

		// If current play list is empty, don't sort
		if (currentPlaylist.isEmpty())
			return;

		// Sort play list with comparator 
		Collections.sort(currentPlaylist, comp);

		// Get position of current file in sorted play list
		int pos = currentPlaylist.indexOf(currentAudioObject);

		// Remove songs from play list model
		VisualHandler.getInstance().getPlayListTableModel().removeSongs();

		// Set sorted play list
		ControllerProxy.getInstance().getPlayListController().notifyAudioObjectsAddedToController(currentPlaylist, pos);

		// Set position of current file in sorted play list
		currentPlaylist.setNextAudioObject(pos);
	}

	/**
	 * Starts playing current song
	 * 
	 * @param start
	 */
	public void startToPlay(boolean start) {
		if (start)
			PlayerHandler.getInstance().play(true);
	}
}
