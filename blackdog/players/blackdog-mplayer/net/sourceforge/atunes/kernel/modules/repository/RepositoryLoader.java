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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.kernel.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.Genre;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

/**
 * Class for loading audiofiles into repository
 * 
 */
public class RepositoryLoader extends Thread {

	private static Logger logger = new Logger();

	// Some attributes to speed up populate info process
	private static final String UNKNOWN_ARTIST = LanguageTool.getString("UNKNOWN_ARTIST");
	private static final String UNKNOWN_ALBUM = LanguageTool.getString("UNKNOWN_ALBUM");
	private static final String UNKNOWN_GENRE = LanguageTool.getString("UNKNOWN_GENRE");
	private LoaderListener listener;

	private List<File> folders;

	private boolean refresh;

	private boolean interrupt;
	/**
	 * Used for refreshing
	 */
	private Repository oldRepository;
	private Repository repository;

	private int totalFilesToLoad;
	private int filesLoaded;
	private long startReadTime;
	private String fastRepositoryPath;
	private int fastFirstChar;

	public RepositoryLoader(List<File> folders, boolean refresh) {
		this.refresh = refresh;
		this.folders = folders;
		repository = new Repository(folders);
		setPriority(Thread.MAX_PRIORITY);
	}

	/**
	 * Add files of a folder to repository
	 * 
	 * @param rep
	 *            Repository to which files should be added
	 * @param files
	 *            Files to add
	 * @param folder
	 *            Folder in which the files are located
	 */
	public static void addToRepository(Repository rep, List<File> files, File folder) {
		String repositoryPath = getRepositoryFolderContaining(rep, folder).getAbsolutePath().replace('\\', '/');
		if (repositoryPath.endsWith("/"))
			repositoryPath = repositoryPath.substring(0, repositoryPath.length() - 2);
		int firstChar = repositoryPath.length() + 1;

		File[] list = folder.listFiles();
		List<File> pictures = new ArrayList<File>();
		if (list != null) {
			for (File element : list) {
				if (element.getName().toUpperCase().endsWith("JPG")) {
					pictures.add(element);
				}
			}
		}

		Map<String, AudioFile> repositoryFiles = rep.getFiles();
		for (File f : files) {
			AudioFile audioFile = null;
			audioFile = new AudioFile(f.getAbsolutePath());
			audioFile.setExternalPictures(pictures);
			repositoryFiles.put(audioFile.getUrl(), audioFile);
			populateInfo(rep, audioFile);

			String pathToFile = audioFile.getUrl().replace('\\', '/');
			int lastChar = pathToFile.lastIndexOf('/') + 1;
			String relativePath;
			if (firstChar < lastChar)
				relativePath = pathToFile.substring(firstChar, lastChar);
			else
				relativePath = ".";
			populateFolderTree(rep, getRepositoryFolderContaining(rep, folder), relativePath, audioFile);
			rep.setTotalSizeInBytes(rep.getTotalSizeInBytes() + audioFile.length());
			rep.addDurationInSeconds(audioFile.getDuration());
		}
	}

	private static int countFiles(File dir) {
		int files = 0;
		File[] list = dir.listFiles();
		if (list == null)
			return files;
		for (File element : list) {
			if (AudioFile.isValidAudioFile(element)) {
				files++;
			} else if (element.isDirectory())
				files = files + countFiles(element);
		}
		return files;
	}

	public static int countFilesInRepository(Repository rep) {
		int files = 0;
		for (File dir : rep.getFolders()) {
			files = files + countFiles(dir);
		}
		return files;
	}

	public static void fillStats(Repository repository, AudioFile song) {
		String songPath = song.getUrl();
		if (repository.getFiles().containsKey(songPath)) {
			repository.getStats().setTotalPlays(repository.getStats().getTotalPlays() + 1);

			SongStats stats = repository.getStats().getSongsStats().get(songPath);
			if (stats != null) {
				stats.setLastPlayed(new Date());
				stats.increaseTimesPlayed();
			} else {
				stats = new SongStats();
				repository.getStats().getSongsStats().put(songPath, stats);
				repository.getStats().setDifferentSongsPlayed(repository.getStats().getDifferentSongsPlayed() + 1);
			}
			repository.getStats().getSongsRanking().addItem(song);

			String artist = song.getArtist();

			Artist a = repository.getStructure().getTreeStructure().get(artist);

			// Unknown artist -> don't fill artist stats
			if (a == null)
				return;

			repository.getStats().getArtistsRanking().addItem(a);

			String album = song.getAlbum();

			Album alb = a.getAlbum(album);

			// Unknown album -> don't fill album stats
			if (alb == null)
				return;

			repository.getStats().getAlbumsRanking().addItem(alb);
		}
	}

	private static File getRepositoryFolderContaining(Repository rep, File folder) {
		String path = folder.getAbsolutePath();
		for (File f : rep.getFolders())
			if (path.startsWith(f.getAbsolutePath()))
				return f;
		return null;
	}

	public static List<AudioFile> getSongsForDir(File dir) {
		List<AudioFile> result = new ArrayList<AudioFile>();

		File[] list = dir.listFiles();
		List<File> pictures = new ArrayList<File>();
		List<File> files = new ArrayList<File>();
		List<File> dirs = new ArrayList<File>();
		if (list != null) {
			//First find pictures, audio and files
			for (File element : list) {
				if (AudioFile.isValidAudioFile(element)) {
					files.add(element);
				} else if (element.isDirectory()) {
					dirs.add(element);
				} else if (element.getName().toUpperCase().endsWith("JPG")) {
					pictures.add(element);
				}
			}

			for (int i = 0; i < files.size(); i++) {
				AudioFile mp3 = null;
				mp3 = new AudioFile(files.get(i).getAbsolutePath());
				mp3.setExternalPictures(pictures);
				result.add(mp3);
			}

			for (int i = 0; i < dirs.size(); i++) {
				getSongsForDir(dirs.get(i));
			}
		}
		return result;
	}

	private static void populateFolderTree(Repository repository, File relativeTo, String relativePath, AudioFile file) {
		Folder relativeFolder = null;
		if (repository.getStructure().getFolderStructure().containsKey(relativeTo.getAbsolutePath()))
			relativeFolder = repository.getStructure().getFolderStructure().get(relativeTo.getAbsolutePath());
		else {
			relativeFolder = new Folder(relativeTo.getAbsolutePath());
			repository.getStructure().getFolderStructure().put(relativeFolder.getName(), relativeFolder);
		}

		StringTokenizer st = new StringTokenizer(relativePath, "/");
		Folder parentFolder = relativeFolder;
		while (st.hasMoreTokens()) {
			String folderName = st.nextToken();
			Folder f;
			if (parentFolder != null) {
				if (parentFolder.containsFolder(folderName))
					f = parentFolder.getFolder(folderName);
				else {
					f = new Folder(folderName);
					parentFolder.addFolder(f);
				}
			} else {
				if (repository.getStructure().getFolderStructure().containsKey(folderName))
					f = repository.getStructure().getFolderStructure().get(folderName);
				else {
					f = new Folder(folderName);
					repository.getStructure().getFolderStructure().put(f.getName(), f);
				}
			}
			parentFolder = f;
		}
		if (parentFolder == null)
			parentFolder = new Folder(".");
		parentFolder.addFile(file);

	}

	private static void populateGenreTree(Repository repository, AudioFile audioFile) {
		try {
			Tag tag = audioFile.getTag();
			String albumArtist = null;
			String artist = null;
			String album = null;
			String genre = null;
			if (tag != null) {
				albumArtist = tag.getAlbumArtist();
				artist = tag.getArtist();
				album = tag.getAlbum();
				genre = tag.getGenre();
			}
			
			/* 
			 * FIX: Bug 1894536. Some formats do not support album artist field and can cause problems.
			 * If album artist field does not contain expected data just set it to following to avoid issues.
			 */
			if (albumArtist == null || albumArtist.isEmpty() || albumArtist.length() == 0)
				albumArtist = "";
			if ((artist == null || artist.equals("")) && albumArtist.equals(""))
				artist = UNKNOWN_ARTIST;
			if (album == null || album.equals(""))
				album = UNKNOWN_ALBUM;
			if (genre == null || genre.equals("") || genre.isEmpty() || genre.length() == 0)
				genre = UNKNOWN_GENRE;

			if (repository.getStructure().getGenreStructure().containsKey(genre)) {
				Genre g = repository.getStructure().getGenreStructure().get(genre);
				Artist art = null;
				art = g.getArtist(albumArtist);
				if (art == null)
					art = g.getArtist(artist);

				// Artist not present in genre list, so lets add him
				if (art == null && !"".equals(albumArtist)) {
					art = new Artist(albumArtist);
					g.addArtist(art);
				} else if (art == null && artist != null && !artist.equals("")) {
					art = new Artist(artist);
					g.addArtist(art);
				}
				if (art != null) {
					Album alb = art.getAlbum(album);
					if (alb != null)
						alb.addSong(audioFile);
					else {
						alb = new Album(album);
						alb.addSong(audioFile);
						alb.setArtist(art);
						art.addAlbum(alb);
					}
				} else {
					art = new Artist(albumArtist);
					if (art.equals(""))
						art = new Artist(artist);
					Album alb = new Album(album);
					alb.addSong(audioFile);
					alb.setArtist(art);
					art.addAlbum(alb);
					g.addArtist(art);
				}
			} else {
				Genre g = new Genre(genre);
				Artist a = null;
				if (albumArtist.equals(""))
					a = new Artist(artist);
				else
					a = new Artist(albumArtist);
				Album alb = new Album(album);
				alb.addSong(audioFile);
				alb.setArtist(a);
				a.addAlbum(alb);
				g.addArtist(a);
				repository.getStructure().getGenreStructure().put(genre, g);
			}
		} catch (Exception e) {
			logger.error(LogCategories.FILE_READ, e.getMessage());
		}
	}

	/**
	 * Organizes the tag view.
	 * 
	 * @param repository
	 *            Repository to use
	 * @param audioFile
	 *            AudioFile to display
	 */
	private static void populateInfo(Repository repository, AudioFile audioFile) {
		try {
			Tag tag = audioFile.getTag();
			String artist = null;
			String album = null;
			if (tag != null) {
				// First see if album artist is present
				artist = tag.getAlbumArtist();
				if (artist == null || artist.equals(""))
					// If not, use artist field
					artist = tag.getArtist();
				album = tag.getAlbum();
			}
			if (artist == null || artist.equals(""))
				artist = UNKNOWN_ARTIST;
			if (album == null || album.equals(""))
				album = UNKNOWN_ALBUM;

			if (repository.getStructure().getTreeStructure().containsKey(artist)) {
				Artist a = repository.getStructure().getTreeStructure().get(artist);
				Album alb = a.getAlbum(album);
				if (alb != null) {
					alb.addSong(audioFile);
				} else {
					alb = new Album(album);
					alb.addSong(audioFile);
					alb.setArtist(a);
					a.addAlbum(alb);
				}
			} else {
				Artist a = new Artist(artist);
				Album alb = new Album(album);
				alb.addSong(audioFile);
				alb.setArtist(a);
				a.addAlbum(alb);
				repository.getStructure().getTreeStructure().put(artist, a);
			}
		} catch (Exception e) {
			logger.error(LogCategories.FILE_READ, e.getMessage());
		}
	}

	/**
	 * Refresh navigator views
	 * 
	 * @param repository
	 * @param file
	 */
	public static void refreshFile(Repository repository, AudioFile file) {
		try {
			Tag tag = file.getTag();
			String albumArtist = null;
			String artist = null;
			String album = null;
			String genre = null;
			if (tag != null) {
				albumArtist = tag.getAlbumArtist();
				artist = tag.getArtist();
				album = tag.getAlbum();
				genre = tag.getGenre();
			}
			if (artist == null || artist.equals(""))
				artist = UNKNOWN_ARTIST;
			if (album == null || album.equals(""))
				album = UNKNOWN_ALBUM;
			if (genre == null || genre.equals(""))
				genre = UNKNOWN_GENRE;

			// Remove from tree structure if necessary
			boolean albumArtistPresent = true;
			Artist a = repository.getStructure().getTreeStructure().get(albumArtist);
			if (a == null) {
				a = repository.getStructure().getTreeStructure().get(artist);
				albumArtistPresent = false;
			}
			if (a != null) {
				Album alb = a.getAlbum(album);
				if (alb != null) {
					if (alb.getAudioObjects().size() == 1)
						a.removeAlbum(alb);
					else
						alb.removeSong(file);

					if (a.getAudioObjects().size() <= 0)
						repository.getStructure().getTreeStructure().remove(a.getName());
				}
				// If album artist field is present, audiofile might still be present under artist name so check
				if (albumArtistPresent) {
					a = repository.getStructure().getTreeStructure().get(artist);
					alb = a.getAlbum(album);
					if (alb != null) {
						if (alb.getAudioObjects().size() == 1)
							a.removeAlbum(alb);
						else
							alb.removeSong(file);
						// Maybe needs to be set to 0 in case node gets deleted
						if (a.getAudioObjects().size() <= 1)
							repository.getStructure().getTreeStructure().remove(a.getName());
					}
				}
			}

			// Remove from genre structure if necessary
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

			// TODO: Why are we doing this??
			file.refreshTag();

			populateInfo(repository, file);
			populateGenreTree(repository, file);
		} catch (Exception e) {
			logger.error(LogCategories.FILE_READ, e.getMessage());
		}
	}

	public void addRepositoryLoaderListener(LoaderListener l) {
		this.listener = l;
	}

	private int countFilesInDir(File dir) {
		int files = 0;
		if (!interrupt) {
			File[] list = dir.listFiles();
			if (list == null)
				return files;
			for (File element : list) {
				if (AudioFile.isValidAudioFile(element)) {
					files++;
				} else if (element.isDirectory())
					files = files + countFilesInDir(element);
			}
		}
		return files;
	}

	private int countFilesInDir(List<File> folders1) {
		int files = 0;
		for (File f : folders1)
			files = files + countFilesInDir(f);
		return files;
	}

	public Repository getRepository() {
		return repository;
	}

	public void interruptLoad() {
		logger.info(LogCategories.REPOSITORY, "Load interrupted");
		interrupt = true;
	}

	private void loadRepository() {
		totalFilesToLoad = countFilesInDir(folders);
		if (listener != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					listener.notifyFilesInRepository(totalFilesToLoad);
				}
			});
		}
		startReadTime = System.currentTimeMillis();
		try {
			for (File folder : folders) {
				fastRepositoryPath = folder.getAbsolutePath().replace('\\', '/');
				if (fastRepositoryPath.endsWith("/"))
					fastRepositoryPath = fastRepositoryPath.substring(0, fastRepositoryPath.length() - 2);
				fastFirstChar = fastRepositoryPath.length() + 1;

				navigateDir(folder, folder);
			}
		} catch (FileNotFoundException e) {
			logger.error(LogCategories.REPOSITORY, e.getMessage());
		}
	}

	private void navigateDir(File relativeTo, File dir) throws FileNotFoundException {
		logger.debug(LogCategories.REPOSITORY, StringUtils.getString("Reading dir ", dir.getAbsolutePath()));

		if (!interrupt) {
			String pathToFile = dir.getAbsolutePath().replace('\\', '/');

			int lastChar = pathToFile.lastIndexOf('/') + 1;
			final String relativePath;
			if (fastFirstChar <= lastChar)
				relativePath = pathToFile.substring(fastFirstChar);
			else
				relativePath = ".";

			if (listener != null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						listener.notifyCurrentPath(relativePath);
					}
				});
			}

			File[] list = dir.listFiles();
			List<File> pictures = new ArrayList<File>();
			List<File> audioFiles = new ArrayList<File>();
			List<File> dirs = new ArrayList<File>();
			if (list != null) {
				//First find pictures, audio and files
				for (File element : list) {
					if (element.isDirectory()) {
						dirs.add(element);
					} else if (AudioFile.isValidAudioFile(element)) {
						audioFiles.add(element);
					} else if (AudioFilePictureUtils.isValidPicture(element)) {
						pictures.add(element);
					}
				}

				Map<String, AudioFile> repositoryFiles = repository.getFiles();
				for (int i = 0; i < audioFiles.size() && !interrupt; i++) {
					AudioFile audioFile = null;

					// If refreshing repository, check if file already was loaded.
					// If so, compare modification date. If modification date is equal to last repository load
					// don't read file again
					if (refresh) {
						AudioFile oldAudioFile = oldRepository.getFile(audioFiles.get(i).getAbsolutePath());
						if (oldAudioFile != null && oldAudioFile.isUpToDate())
							audioFile = oldAudioFile;
						else
							audioFile = new AudioFile(audioFiles.get(i).getAbsolutePath());
					} else {
						audioFile = new AudioFile(audioFiles.get(i).getAbsolutePath());
					}

					audioFile.setExternalPictures(pictures);
					if (listener != null) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								listener.notifyFileLoaded();
							}
						});
					}
					filesLoaded++;
					repositoryFiles.put(audioFile.getUrl(), audioFile);

					populateInfo(repository, audioFile);
					populateFolderTree(repository, relativeTo, relativePath, audioFile);
					populateGenreTree(repository, audioFile);

					repository.setTotalSizeInBytes(repository.getTotalSizeInBytes() + audioFile.length());
					repository.addDurationInSeconds(audioFile.getDuration());

					if (filesLoaded % 50 == 0) {
						long t1 = System.currentTimeMillis();
						final long remainingTime = filesLoaded != 0 ? (totalFilesToLoad - filesLoaded) * (t1 - startReadTime) / filesLoaded : 0;

						if (listener != null) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									listener.notifyRemainingTime(remainingTime);
								}
							});
						}
					}
				}

				for (int i = 0; i < dirs.size(); i++) {
					navigateDir(relativeTo, dirs.get(i));
				}
			}
		}
	}

	private void notifyFinish() {
		if (listener == null)
			return;
		if (refresh) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					listener.notifyFinishRefresh(RepositoryLoader.this);
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					listener.notifyFinishRead(RepositoryLoader.this);
				}
			});
		}
	}

	@Override
	public void run() {
		super.run();
		logger.info(LogCategories.REPOSITORY, "Starting repository read");
		Timer.start();
		if (!folders.isEmpty()) {
			loadRepository();
		} else {
			logger.error(LogCategories.REPOSITORY, "No folders selected for repository");
		}
		if (!interrupt) {
			double time = Timer.stop();
			long files = repository.countFiles();
			double averageFileTime = time / files;
			logger.info(LogCategories.REPOSITORY, StringUtils.getString("Read repository process DONE (", files, " files, ", time, " seconds, ", StringUtils.toString(
					averageFileTime, 4), " seconds / file)"));
			notifyFinish();
		}
	}

	/**
	 * @param oldRepository
	 *            the oldRepository to set
	 */
	public void setOldRepository(Repository oldRepository) {
		this.oldRepository = oldRepository;
	}
}
