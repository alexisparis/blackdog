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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sourceforge.atunes.gui.model.PlayListTableModel;
import net.sourceforge.atunes.gui.views.dialogs.MultiFolderSelectionDialog;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.repository.LoaderListener;
import net.sourceforge.atunes.kernel.modules.repository.Repository;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryAutoRefresher;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryLoader;
import net.sourceforge.atunes.kernel.modules.repository.SongStats;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.Genre;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.RankList;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class RepositoryHandler implements LoaderListener {

	public enum SortType {
		BY_TRACK, BY_TITLE, BY_FILE
	}

	private static RepositoryHandler instance = new RepositoryHandler();

	private Logger logger = new Logger();

	private Repository repository;
	private Repository tempRepository;

	private int filesLoaded;

	private RepositoryLoader currentLoader;

	private RepositoryAutoRefresher repositoryRefresher;
	private boolean refreshDevice = false;

	private RepositoryHandler() {
		this.repositoryRefresher = new RepositoryAutoRefresher(this);
	}

	public static RepositoryHandler getInstance() {
		return instance;
	}

	public void addAndRefresh(List<File> files, File folder) {
		RepositoryLoader.addToRepository(repository, files, folder);

		// Persist
		ApplicationDataHandler.getInstance().persistRepositoryCache(repository);

		VisualHandler.getInstance().showProgressBar(false);
		VisualHandler.getInstance().showRepositorySongNumber(getSongs().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
		if (ControllerProxy.getInstance().getNavigationController() != null)
			ControllerProxy.getInstance().getNavigationController().notifyReload();
		logger.info(LogCategories.REPOSITORY, "Repository refresh done");
	}

	public void addExternalPictureForAlbum(String artistName, String albumName, File picture) {
		if (repository != null) {
			Artist artist = repository.getStructure().getTreeStructure().get(artistName);
			if (artist == null)
				return;
			Album album = artist.getAlbum(albumName);
			if (album == null)
				return;
			List<AudioFile> songs = album.getAudioFiles();
			for (AudioFile f : songs) {
				f.addExternalPicture(picture);
			}
		}
	}

	/**
	 * Permanently deletes an audio file from the repository metainformation and
	 * then from the disk or device
	 * 
	 * @param file
	 *            File to be removed permanently
	 */
	private void deleteFile(AudioFile file) {
		String albumArtist = file.getAlbumArtist();
		String artist = file.getArtist();
		String album = file.getAlbum();
		String genre = file.getGenre();

		// Only do this if file is in repository
		if (getFolderForFile(file) != null) {
			// Remove from file structure
			Folder f = getFolderForFile(file);
			if (f != null) {
				f.removeFile(file);
				// If folder is empty, remove too
				if (f.isEmpty()) {
					f.getParentFolder().removeFolder(f);
				}
			}

			// Remove from tree structure
			boolean artistRemoved = false;
			boolean albumRemoved = false;
			Artist a = repository.getStructure().getTreeStructure().get(albumArtist);
			if (a == null || a.equals(""))
				a = repository.getStructure().getTreeStructure().get(artist);
			if (a != null) {
				Album alb = a.getAlbum(album);
				if (alb != null) {
					if (alb.getAudioObjects().size() == 1) {
						a.removeAlbum(alb);
						albumRemoved = true;
					} else
						alb.removeSong(file);

					if (a.getAudioObjects().size() <= 1) {
						repository.getStructure().getTreeStructure().remove(a.getName());
						artistRemoved = true;
					}
				}
			}

			// Remove from genre structure
			Genre g = repository.getStructure().getGenreStructure().get(genre);
			if (g != null) {
				Artist art = g.getArtist(artist);
				if (art != null) {
					Album alb = art.getAlbum(album);
					if (alb != null) {
						if (alb.getAudioObjects().size() == 1)
							art.removeAlbum(alb);
						else
							alb.removeSong(file);
					}

					if (art.getAudioObjects().size() <= 1)
						g.removeArtist(art);
				}

				if (g.getAudioObjects().size() <= 1)
					repository.getStructure().getGenreStructure().remove(genre);
			}

			// Remove from file list
			repository.getFiles().remove(file.getUrl());

			// Update repository size
			repository.setTotalSizeInBytes(repository.getTotalSizeInBytes() - file.length());

			// Update repository duration
			repository.removeDurationInSeconds(file.getDuration());

			// Remove from favorite songs
			FavoritesHandler.getInstance().getFavorites().getFavoriteSongs().remove(file.getUrl());

			if (artistRemoved)
				FavoritesHandler.getInstance().getFavorites().getFavoriteArtists().remove(file.getArtist());

			if (albumRemoved)
				FavoritesHandler.getInstance().getFavorites().getFavoriteAlbums().remove(file.getAlbum());

			// Now delete file
			file.delete();

		}
		// File is on a device
		else if (DeviceHandler.getInstance().isDevicePath(file.getUrl())) {
			// This is OK for generic device, but what about iPods?
			file.delete();
			logger.info(LogCategories.REPOSITORY, StringUtils.getString("Deleted file ", file, " from device"));
			// In order to avoid refresh after each song deleted, refresh only at the end of function 
			// removePhisically(List<AudioFile> filesToRemove)
			refreshDevice = true;
		}
	}

	public void finish() {
		repositoryRefresher.interrupt();
		ApplicationDataHandler.getInstance().persistRepositoryCache(repository);
	}

	public Map<String, Integer> getAlbumMostPlayed() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		if (repository != null && repository.getStats().getAlbumsRanking().size() > 0) {
			String firstAlbum = repository.getStats().getAlbumsRanking().getNFirstElements(1).get(0).toString();
			Integer count = repository.getStats().getAlbumsRanking().getNFirstElementCounts(1).get(0);
			result.put(firstAlbum, count);
		} else
			result.put(null, 0);
		return result;
	}

	public List<Album> getAlbums() {
		List<Album> result = new ArrayList<Album>();
		if (repository != null) {
			Collection<Artist> artists = repository.getStructure().getTreeStructure().values();
			for (Artist a : artists) {
				result.addAll(a.getAlbums().values());
			}
			Collections.sort(result);
		}
		return result;
	}

	public Integer getAlbumTimesPlayed(AudioFile song) {
		if (song != null) {
			if (repository != null) {
				Album a = repository.getStructure().getTreeStructure().get(song.getArtist()).getAlbum(song.getAlbum());
				if (repository.getStats().getAlbumsRanking().getCount(a) != null) {
					return repository.getStats().getAlbumsRanking().getCount(a);
				}
			}
			return 0;
		}
		return null;
	}

	public Map<String, Artist> getArtistAndAlbumStructure() {
		if (repository != null)
			return repository.getStructure().getTreeStructure();
		return new HashMap<String, Artist>();
	}

	public Map<String, Integer> getArtistMostPlayed() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		if (repository != null && repository.getStats().getArtistsRanking().size() > 0) {
			String firstArtist = repository.getStats().getArtistsRanking().getNFirstElements(1).get(0).toString();
			Integer count = repository.getStats().getArtistsRanking().getNFirstElementCounts(1).get(0);
			result.put(firstArtist, count);
		} else
			result.put(null, 0);
		return result;
	}

	public List<Artist> getArtists() {
		List<Artist> result = new ArrayList<Artist>();
		if (repository != null) {
			result.addAll(repository.getStructure().getTreeStructure().values());
			Collections.sort(result);
		}
		return result;
	}

	public Integer getArtistTimesPlayed(AudioFile song) {
		if (song != null) {
			if (repository != null) {
				Artist a = repository.getStructure().getTreeStructure().get(song.getArtist());
				if (repository.getStats().getArtistsRanking().getCount(a) != null) {
					return repository.getStats().getArtistsRanking().getCount(a);
				}
			}
			return 0;
		}
		return null;
	}

	public int getDifferentSongsPlayed() {
		if (repository != null) {
			return repository.getStats().getDifferentSongsPlayed();
		}
		return 0;
	}

	public AudioFile getFileIfLoaded(String fileName) {
		return repository == null ? null : repository.getFile(fileName);
	}

	/**
	 * Finds folder that contains file
	 * 
	 * @param file
	 *            Audio file for which the folder is wanted
	 * @return Either folder or null if file is not in repository
	 */
	private Folder getFolderForFile(AudioFile file) {
		// Get repository folder where file is
		File repositoryFolder = getRepositoryFolderContainingFile(file);
		// If the file is not in the repository, return null
		if (repositoryFolder == null)
			return null;

		// Get root folder
		Folder rootFolder = repository.getStructure().getFolderStructure().get(repositoryFolder.getAbsolutePath());

		// Now navigate through folder until find folder that contains file
		String path = file.getParentFile().getAbsolutePath();
		path = path.replace(repositoryFolder.getAbsolutePath(), "");

		Folder f = rootFolder;
		StringTokenizer st = new StringTokenizer(path, SystemProperties.fileSeparator);
		while (st.hasMoreTokens()) {
			String folderName = st.nextToken();
			f = f.getFolder(folderName);
		}
		return f;
	}

	public Map<String, Folder> getFolderStructure() {
		if (repository != null)
			return repository.getStructure().getFolderStructure();
		return new HashMap<String, Folder>();
	}

	public Map<String, Genre> getGenreStructure() {
		if (repository != null)
			return repository.getStructure().getGenreStructure();
		return new HashMap<String, Genre>();
	}

	public List<Album> getMostPlayedAlbums(int n) {
		if (repository != null) {
			List<Album> albums = repository.getStats().getAlbumsRanking().getNFirstElements(n);
			if (albums != null)
				return albums;
		}
		return null;
	}

	public List<Object[]> getMostPlayedAlbumsInRanking(int n) {
		if (repository != null) {
			List<Object[]> result = new ArrayList<Object[]>();
			List<Album> albums = repository.getStats().getAlbumsRanking().getNFirstElements(n);
			List<Integer> count = repository.getStats().getAlbumsRanking().getNFirstElementCounts(n);
			if (albums != null)
				for (int i = 0; i < albums.size(); i++) {
					Object[] obj = new Object[2];
					obj[0] = albums.get(i).toString();
					obj[1] = count.get(i);
					result.add(obj);
				}
			return result;
		}
		return null;
	}

	public List<Artist> getMostPlayedArtists(int n) {
		if (repository != null) {
			List<Artist> artists = repository.getStats().getArtistsRanking().getNFirstElements(n);
			if (artists != null)
				return artists;
		}
		return null;
	}

	public List<Object[]> getMostPlayedArtistsInRanking(int n) {
		if (repository != null) {
			List<Object[]> result = new ArrayList<Object[]>();
			List<Artist> artists = repository.getStats().getArtistsRanking().getNFirstElements(n);
			List<Integer> count = repository.getStats().getArtistsRanking().getNFirstElementCounts(n);
			if (artists != null)
				for (int i = 0; i < artists.size(); i++) {
					Object[] obj = new Object[2];
					obj[0] = artists.get(i).toString();
					obj[1] = count.get(i);
					result.add(obj);
				}
			return result;
		}
		return null;
	}

	public List<AudioFile> getMostPlayedSongs(int n) {
		if (repository != null) {
			List<AudioFile> songs = repository.getStats().getSongsRanking().getNFirstElements(n);
			if (songs != null)
				return songs;
		}
		return null;
	}

	public List<Object[]> getMostPlayedSongsInRanking(int n) {
		if (repository != null) {
			List<Object[]> result = new ArrayList<Object[]>();
			List<AudioFile> songs = repository.getStats().getSongsRanking().getNFirstElements(n);
			List<Integer> count = repository.getStats().getSongsRanking().getNFirstElementCounts(n);
			if (songs != null)
				for (int i = 0; i < songs.size(); i++) {
					Object[] obj = new Object[2];
					AudioFile song = songs.get(i);
					obj[0] = StringUtils.getString(song.getTitleOrFileName(), " (", song.getArtist(), ")");
					obj[1] = count.get(i);
					result.add(obj);
				}
			return result;
		}
		return null;
	}

	public String getPathForNewSongsRipped() {
		return StringUtils.getString(RepositoryHandler.getInstance().getRepositoryPath(), SystemProperties.fileSeparator, LanguageTool.getString("UNKNOWN_ALBUM"), " - ", DateUtils
				.toPathString(new Date()));
	}

	public Repository getRepository() {
		return repository;
	}

	/**
	 * Returns repository root folder that contains file
	 * 
	 * @param folder
	 * @return
	 */
	public File getRepositoryFolderContainingFile(AudioFile file) {
		if (repository == null)
			return null;
		for (File folder : repository.getFolders()) {
			if (file.getUrl().startsWith(folder.getAbsolutePath()))
				return folder;
		}
		return null;
	}

	public String getRepositoryPath() {
		return repository != null ? repository.getFolders().get(0).getAbsolutePath() : "";
	}

	public long getRepositoryTotalSize() {
		return repository != null ? repository.getTotalSizeInBytes() : 0;
	}

	public Map<AudioFile, Integer> getSongMostPlayed() {
		Map<AudioFile, Integer> result = new HashMap<AudioFile, Integer>();
		if (repository != null && repository.getStats().getSongsRanking().size() > 0) {
			AudioFile firstSong = repository.getStats().getSongsRanking().getNFirstElements(1).get(0);
			Integer count = repository.getStats().getSongsRanking().getNFirstElementCounts(1).get(0);
			result.put(firstSong, count);
		} else
			result.put(null, 0);
		return result;
	}

	public List<AudioFile> getSongs() {
		if (repository != null)
			return repository.getFilesList();
		return new ArrayList<AudioFile>();
	}

	public List<AudioFile> getSongsForAlbums(Map<String, Album> albums) {
		List<AudioFile> result = new ArrayList<AudioFile>();
		for (String string : albums.keySet()) {
			Album a = albums.get(string);
			result.addAll(a.getAudioFiles());
		}
		return result;
	}

	public List<AudioFile> getSongsForArtists(Map<String, Artist> artists) {
		List<AudioFile> result = new ArrayList<AudioFile>();
		for (String string : artists.keySet()) {
			Artist a = artists.get(string);
			result.addAll(a.getAudioFiles());
		}
		return result;
	}

	public String getSongsPlayed() {
		if (repository != null) {
			int totalPlays = repository.getStats().getDifferentSongsPlayed();
			int total = repository.countFiles();
			float perCent = (float) totalPlays / (float) total * 100;
			return StringUtils.getString(totalPlays, " / ", total, " (", StringUtils.toString(perCent, 2), "%)");
		}
		return "0 / 0 (0%)";
	}

	public SongStats getSongStatistics(AudioFile song) {
		if (repository != null) {
			return repository.getStats().getStatsForFile(song);
		}
		return null;
	}

	public int getTotalSongsPlayed() {
		return repository != null ? repository.getStats().getTotalPlays() : -1;
	}

	/**
	 * Returns true if folder is in repository
	 * 
	 * @param folder
	 * @return
	 */
	public boolean isRepository(File folder) {
		if (repository == null)
			return false;
		String path = folder.getAbsolutePath();
		for (File folders : repository.getFolders()) {
			if (path.startsWith(folders.getAbsolutePath()))
				return true;
		}
		return false;
	}

	public void notifyCancel() {
		currentLoader.interruptLoad();
		VisualHandler.getInstance().getProgressDialog().setVisible(false);
		VisualHandler.getInstance().getProgressDialog().deactivateGlassPane();
		VisualHandler.getInstance().getProgressDialog().setCancelButtonVisible(false);
		VisualHandler.getInstance().getProgressDialog().setCancelButtonEnabled(true);
	}

	@Override
	public void notifyCurrentPath(final String dir) {
		VisualHandler.getInstance().getProgressDialog().getFolderLabel().setText(dir);

	}

	@Override
	public void notifyFileLoaded() {
		this.filesLoaded++;
		final int aux = this.filesLoaded;
		VisualHandler.getInstance().getProgressDialog().getProgressLabel().setText(Integer.toString(aux));
		VisualHandler.getInstance().getProgressDialog().getProgressBar().setValue(aux);

	}

	@Override
	public void notifyFilesInRepository(int totalFiles) {
		logger.debug(LogCategories.REPOSITORY, new String[] { Integer.toString(totalFiles) });

		VisualHandler.getInstance().getProgressDialog().getProgressBar().setIndeterminate(false);
		VisualHandler.getInstance().getProgressDialog().getTotalFilesLabel().setText(StringUtils.getString(totalFiles));
		VisualHandler.getInstance().getProgressDialog().getProgressBar().setMaximum(totalFiles);
	}

	@Override
	public void notifyFinishRead(RepositoryLoader loader) {
		if (loader != null)
			tempRepository = loader.getRepository();
		VisualHandler.getInstance().getProgressDialog().setCancelButtonEnabled(false);
		VisualHandler.getInstance().getProgressDialog().getLabel().setText(LanguageTool.getString("STORING_REPOSITORY_INFORMATION"));
		VisualHandler.getInstance().getProgressDialog().getProgressLabel().setText("");
		VisualHandler.getInstance().getProgressDialog().getTotalFilesLabel().setText("");
		VisualHandler.getInstance().getProgressDialog().getFolderLabel().setText(" ");

		// Set new repository
		repository = tempRepository;

		VisualHandler.getInstance().getProgressDialog().setVisible(false);
		VisualHandler.getInstance().getProgressDialog().deactivateGlassPane();
		VisualHandler.getInstance().getProgressDialog().setCancelButtonVisible(false);
		VisualHandler.getInstance().getProgressDialog().setCancelButtonEnabled(true);

		notifyFinishRepositoryRead();
	}

	@Override
	public void notifyFinishRefresh(RepositoryLoader loader) {
		tempRepository = loader.getRepository();

		// Save stats
		transferStatsFrom(tempRepository, repository);

		// Set new repository
		repository = tempRepository;

		// Persist
		ApplicationDataHandler.getInstance().persistRepositoryCache(repository);

		VisualHandler.getInstance().showProgressBar(false);
		VisualHandler.getInstance().showRepositorySongNumber(getSongs().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
		if (ControllerProxy.getInstance().getNavigationController() != null)
			ControllerProxy.getInstance().getNavigationController().notifyReload();
		logger.info(LogCategories.REPOSITORY, "Repository refresh done");
	}

	private void notifyFinishRepositoryRead() {
		ControllerProxy.getInstance().getNavigationController().notifyReload();
		VisualHandler.getInstance().showRepositorySongNumber(getSongs().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
	}

	@Override
	public void notifyRemainingTime(final long millis) {
		VisualHandler.getInstance().getProgressDialog().getRemainingTimeLabel().setText(
				StringUtils.getString(LanguageTool.getString("REMAINING_TIME"), ":   ", TimeUtils.milliseconds2String(millis)));

	}

	public void readRepository() {
		// Try to read repository cache. If fails or not exists, should be selected again
		final Repository rep = ApplicationDataHandler.getInstance().retrieveRepositoryCache();
		if (rep != null) {
			if (!rep.getFolders().get(0).exists()) {
				VisualHandler.getInstance().hideSplashScreen();
				VisualHandler.getInstance().setFullFrameVisible(true);
				VisualHandler.getInstance().showMessage(StringUtils.getString(LanguageTool.getString("REPOSITORY_NOT_FOUND"), ": ", rep.getFolders().get(0)));
				if (!selectRepository(true)) {
					// select "old" repository if repository was not found and no new repository was selected
					repository = rep;
				}
			} else {
				tempRepository = rep;
				notifyFinishRead(null);
			}
		} else {
			VisualHandler.getInstance().hideSplashScreen();
			VisualHandler.getInstance().setFullFrameVisible(true);
			VisualHandler.getInstance().showRepositorySelectionInfoDialog();
			selectRepository();
		}
	}

	private void readRepository(List<File> folders) {
		VisualHandler.getInstance().getProgressDialog().setCancelButtonVisible(true);
		VisualHandler.getInstance().getProgressDialog().setCancelButtonEnabled(true);
		currentLoader = new RepositoryLoader(folders, false);
		currentLoader.addRepositoryLoaderListener(this);
		currentLoader.start();
	}

	private void refresh() {
		logger.info(LogCategories.REPOSITORY, "Refreshing repository");
		filesLoaded = 0;
		currentLoader = new RepositoryLoader(repository.getFolders(), true);
		currentLoader.addRepositoryLoaderListener(this);
		currentLoader.setOldRepository(repository);
		currentLoader.start();
	}

	public void refreshFile(AudioFile file) {
		RepositoryLoader.refreshFile(repository, file);
	}

	public void refreshRepository() {
		if (!repositoryIsNull()) {
			VisualHandler.getInstance().setCenterStatusBarText(StringUtils.getString(LanguageTool.getString("REFRESHING"), "..."));
			VisualHandler.getInstance().showProgressBar(true);
			refresh();
		}
	}

	/**
	 * Removes a list of files permanently from disk
	 * 
	 * @param filesToRemove
	 *            Files that should be removed
	 */
	public void removePhysically(List<AudioFile> filesToRemove) {
		if (filesToRemove == null || filesToRemove.isEmpty())
			return;

		boolean playListContainsRemovedFile = false;
		for (AudioFile fileToRemove : filesToRemove) {
			deleteFile(fileToRemove);

			if (PlayerHandler.getInstance().getCurrentPlayList().contains(fileToRemove)) {
				playListContainsRemovedFile = true;

				// Remove from play list
				while (PlayerHandler.getInstance().getCurrentPlayList().contains(fileToRemove)) {
					int[] songsToRemove = new int[] { PlayerHandler.getInstance().getCurrentPlayList().indexOf(fileToRemove) };
					((PlayListTableModel) VisualHandler.getInstance().getPlayListTable().getModel()).removeSongs(songsToRemove);
					PlayListHandler.getInstance().removeSongs(songsToRemove);
				}

			}
		}

		// Remove songs from other playlists
		playListContainsRemovedFile = MultiplePlaylistHandler.getInstance().removeSongsFromOtherPlayLists(filesToRemove);

		ControllerProxy.getInstance().getNavigationController().notifyReload();
		if (playListContainsRemovedFile)
			ApplicationDataHandler.getInstance().persistPlayList();
		// At least a song has been deleted on a device so refresh it and reset refreshDevice
		if (refreshDevice) {
			DeviceHandler.getInstance().refreshDevice();
			refreshDevice = false;
		}
		// Update status bar
		VisualHandler.getInstance().showRepositorySongNumber(getSongs().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
	}

	public boolean repositoryIsNull() {
		return repository == null;
	}

	public boolean retrieve(List<File> folders) {
		filesLoaded = 0;
		try {
			if (folders == null || folders.isEmpty()) {
				repository = null;
				return false;
			}
			readRepository(folders);
			return true;
		} catch (Exception e) {
			repository = null;
			logger.error(LogCategories.REPOSITORY, e);
			return false;
		}
	}

	public boolean selectRepository() {
		return selectRepository(false);
	}

	public boolean selectRepository(boolean repositoryNotFound) {
		MultiFolderSelectionDialog dialog = VisualHandler.getInstance().getMultiFolderSelectionDialog();
		dialog.setText(LanguageTool.getString("SELECT_REPOSITORY_FOLDERS"));
		dialog.startDialog((repository != null && !repositoryNotFound) ? repository.getFolders() : null);
		if (!dialog.isCancelled()) {
			List<File> folders = dialog.getFoldersSelected();
			if (!folders.isEmpty()) {
				VisualHandler.getInstance().getProgressDialog().setVisible(true);
				VisualHandler.getInstance().getProgressDialog().activateGlassPane();
				this.retrieve(folders);
				return true;
			}
		}
		return false;
	}

	public void setSongStatistics(AudioFile song) {
		if (repository != null) {
			RepositoryLoader.fillStats(repository, song);
		}
	}

	public List<AudioObject> sort(List<AudioObject> songs) {
		return sort(songs, ControllerProxy.getInstance().getNavigationController().getState().getSortType());
	}

	public List<AudioObject> sort(List<AudioObject> songs, SortType type) {
		AudioFile[] array = songs.toArray(new AudioFile[songs.size()]);

		if (type == SortType.BY_TRACK) {
			Arrays.sort(array, new Comparator<AudioObject>() {
				@Override
				public int compare(AudioObject a1, AudioObject a2) {
					int c1 = a1.getArtist().compareTo(a2.getArtist());
					if (c1 != 0)
						return c1;

					int c2 = a1.getAlbum().compareTo(a2.getAlbum());
					if (c2 != 0)
						return c2;

					return a1.getTrackNumber().compareTo(a2.getTrackNumber());
				}
			});
		} else if (type == SortType.BY_TITLE) {
			Arrays.sort(array, new Comparator<AudioObject>() {
				@Override
				public int compare(AudioObject a0, AudioObject a1) {
					return a0.getTitleOrFileName().compareTo(a1.getTitleOrFileName());
				}
			});
		} else { // Sort songs by file name
			Arrays.sort(array);
		}

		List<AudioObject> songsArray = new ArrayList<AudioObject>();
		for (AudioFile element : array)
			songsArray.add(element);

		return songsArray;
	}

	/**
	 * Gets stats from a repository
	 * 
	 * @param oldRepository
	 */
	public void transferStatsFrom(Repository newRepository, Repository oldRepository) {
		// Total plays
		newRepository.getStats().setTotalPlays(oldRepository.getStats().getTotalPlays());

		// DifferentSongsPlayed
		newRepository.getStats().setDifferentSongsPlayed(oldRepository.getStats().getDifferentSongsPlayed());

		// SongStats
		newRepository.getStats().setSongsStats(oldRepository.getStats().getSongsStats());

		// Songs Ranking
		newRepository.getStats().setSongsRanking(oldRepository.getStats().getSongsRanking());

		// Album Ranking
		RankList<Album> albumsRanking = oldRepository.getStats().getAlbumsRanking();
		List<Album> albums = albumsRanking.getOrder();
		for (int i = 0; i < albums.size(); i++) {
			Album oldAlbum = albums.get(i);
			Artist artist = newRepository.getStructure().getTreeStructure().get(oldAlbum.getArtist());
			if (artist != null) {
				Album newAlbum = artist.getAlbum(oldAlbum.getName());
				if (newAlbum != null) {
					albumsRanking.replaceItem(oldAlbum, newAlbum);
				}
			}
		}
		newRepository.getStats().setAlbumsRanking(albumsRanking);

		// Artist Ranking
		RankList<Artist> artistsRanking = oldRepository.getStats().getArtistsRanking();
		List<Artist> artists = artistsRanking.getOrder();
		for (int i = 0; i < artists.size(); i++) {
			Artist oldArtist = artists.get(i);
			Artist newArtist = newRepository.getStructure().getTreeStructure().get(oldArtist.getName());
			if (newArtist != null) {
				artistsRanking.replaceItem(oldArtist, newArtist);
			}
		}
		newRepository.getStats().setArtistsRanking(artistsRanking);
	}

}
