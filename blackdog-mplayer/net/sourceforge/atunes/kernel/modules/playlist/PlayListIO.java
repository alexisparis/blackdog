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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.ClosingUtils;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.kernel.utils.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class PlayListIO {

	// The different Strings used
	private static final String M3U_HEADER = "#EXTM3U";
	private static final String M3U_START_COMMENT = "#";
	private static final String M3U_UNIX_ABSOLUTE_PATH = "/";
	private static final String M3U_WINDOWS_ABSOLUTE_PATH = ":\\";
	public static final String M3U_FILE_EXTENSION = "m3u";

	/**
	 * Returns a list of files contained in a list of file names
	 */
	public static List<AudioObject> getAudioObjectsFromFileNamesList(List<String> fileNames) {
		List<AudioObject> result = new ArrayList<AudioObject>();
		for (String fileName : fileNames) {
			AudioFile f = RepositoryHandler.getInstance().getFileIfLoaded(fileName);
			if (f == null)
				f = new AudioFile(fileName);
			result.add(f);
		}
		return result;
	}

	/**
	 * Returns a list of files contained in a play list file
	 * 
	 * @param file
	 * @return
	 */
	public static List<AudioObject> getFilesFromList(File file) {
		List<String> list = read(file);
		return getAudioObjectsFromFileNamesList(list);
	}

	/**
	 * Runnable object used to load play list from a list of file names
	 * 
	 * @param files
	 * @return
	 */
	public static Runnable getLoadPlayListProcess(final List<String> files) {
		return new Runnable() {
			@Override
			public void run() {
				final List<AudioObject> songsLoaded = PlayListIO.getAudioObjectsFromFileNamesList(files);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (songsLoaded.size() >= 1) {
							if (PlayerHandler.getInstance().getCurrentPlayList().isEmpty())
								PlayListHandler.getInstance().selectedAudioObjectChanged(songsLoaded.get(0));
							PlayListHandler.getInstance().addToPlayList(songsLoaded);
						}
						VisualHandler.getInstance().getProgressDialog().setVisible(false);
					}
				});
			}
		};
	}

	/**
	 * FileFilter to be used when loading and saving a play list file
	 * 
	 * @return
	 */
	public static final FileFilter getPlaylistFileFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().toLowerCase().endsWith(M3U_FILE_EXTENSION);
			}

			@Override
			public String getDescription() {
				return LanguageTool.getString("PLAYLIST");
			}
		};
	}

	public static boolean isValidPlayList(String playListFile) {
		File f = new File(playListFile);
		return playListFile.endsWith(M3U_FILE_EXTENSION) && f.exists();
	}

	/**
	 * This function reads the filenames from the playlist file (m3u). It will
	 * return all filenames with absolute path. For this playlists with relative
	 * pathname must be detected and the path must be added. Current problem of
	 * this implementation is clearly the charset used. Java reads/writes in the
	 * charset used by the OS! But for many *nixes this is UTF8, while Windows
	 * will use CP1252 or similar. So, as long as we have the same charset
	 * encoding or do not use any special character playlists will work
	 * (absolute filenames with a pathname incompatible with the current OS are
	 * not allowed), but as soon as we have say french accents in the filename a
	 * playlist created under an application using CP1252 will not import
	 * correctly on a UTF8 system (better: the files with accents in their
	 * filename will not).
	 * 
	 * Only playlist with local files have been tested! Returns a list of file
	 * names contained in a play list file
	 * 
	 * @param file
	 *            The playlist file
	 * @return Returns an List of files of the playlist as String.
	 */
	public static List<String> read(File file) {

		BufferedReader br = null;
		try {
			List<String> result = new ArrayList<String>();
			br = new BufferedReader(new FileReader(file));
			String line;
			line = br.readLine();
			// Do look for the first uncommented line
			while (line.startsWith(M3U_START_COMMENT))
				line = br.readLine();
			// First absolute path. Windows path detection is very rudimentary, but should work
			if (line.startsWith(M3U_WINDOWS_ABSOLUTE_PATH, 1) || line.startsWith(M3U_UNIX_ABSOLUTE_PATH)) {
				// Let's check if we are at least using the right OS. Maybe a message should be returned, but for now it doesn't
				if (((SystemProperties.SYSTEM == OperatingSystem.WINDOWS) && line.startsWith(M3U_UNIX_ABSOLUTE_PATH))
						|| (!(SystemProperties.SYSTEM == OperatingSystem.WINDOWS) && line.startsWith(M3U_WINDOWS_ABSOLUTE_PATH, 1))) {
					return null;
				}
				result.add(line);
				while ((line = br.readLine()) != null) {
					if (!line.startsWith(M3U_START_COMMENT))
						result.add(line);
				}
			}
			// The path is relative! We must add it to the filename
			else {
				String path = file.getParent() + SystemProperties.fileSeparator;
				result.add(StringUtils.getString(path, line));
				while ((line = br.readLine()) != null) {
					if (!line.startsWith(M3U_START_COMMENT))
						result.add(StringUtils.getString(path, line));
				}
			}
			// Return the filenames with their absolute path
			return result;
		} catch (IOException e) {
			return null;
		} finally {
			ClosingUtils.close(br);
		}
	}

	/**
	 * Writes a play list to a file
	 * 
	 * @param playlist
	 * @param file
	 * @return
	 */
	public static boolean write(PlayList playlist, File file) {
		FileWriter writer = null;
		try {
			if (file.exists())
				file.delete();
			writer = new FileWriter(file);
			writer.append(StringUtils.getString(M3U_HEADER, SystemProperties.lineTerminator));
			for (AudioObject f : playlist) {
				writer.append(StringUtils.getString(f.getUrl(), SystemProperties.lineTerminator));
			}
			writer.flush();
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			ClosingUtils.close(writer);
		}
	}
}
