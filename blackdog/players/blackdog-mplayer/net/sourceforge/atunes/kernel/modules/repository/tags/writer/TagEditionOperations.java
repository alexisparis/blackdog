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

package net.sourceforge.atunes.kernel.modules.repository.tags.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import net.sourceforge.atunes.kernel.executors.BackgroundExecutor;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.regexp.RegexpUtils;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.utils.LanguageTool;

public class TagEditionOperations {

	public static void addLyrics(List<AudioFile> files) {
		BackgroundExecutor.addLyrics(files);
	}

	public static void editAlbumName(List<AudioFile> files) {
		BackgroundExecutor.changeAlbumNames(files);
	}

	/**
	 * Sets genre based on Last.fm tags
	 */
	public static void editGenre(List<AudioFile> files) {
		BackgroundExecutor.changeGenres(files);
	}

	/**
	 * Sets track number based on file name to an array of files
	 */
	public static void editTrackNumber(List<AudioFile> files) {
		Map<AudioFile, Integer> filesAndTracks = RegexpUtils.getFilesAndTrackNumbers(files);
		if (!filesAndTracks.isEmpty())
			BackgroundExecutor.changeTrackNumbers(filesAndTracks);
	}

	public static void repairAlbumNames() {
		// Show confirmation dialog
		if (VisualHandler.getInstance().showConfirmationDialog(LanguageTool.getString("REPAIR_ALBUM_NAMES_MESSAGE"), LanguageTool.getString("REPAIR_ALBUM_NAMES")) != JOptionPane.OK_OPTION)
			return;

		// Get all repository songs
		List<AudioFile> repositorySongs = RepositoryHandler.getInstance().getSongs();

		// Call album name edit
		editAlbumName(repositorySongs);
	}

	/**
	 * Sets genres on files that don't have correct genre
	 */
	public static void repairGenres() {
		// Show confirmation dialog
		if (VisualHandler.getInstance().showConfirmationDialog(LanguageTool.getString("REPAIR_GENRES_MESSAGE"), LanguageTool.getString("REPAIR_GENRES")) != JOptionPane.OK_OPTION)
			return;

		// Get all repository songs
		List<AudioFile> repositorySongs = RepositoryHandler.getInstance().getSongs();

		// Call genre edit
		editGenre(repositorySongs);
	}

	/**
	 * Sets track number to files that don't have track number setted
	 * 
	 */
	public static void repairTrackNumbers() {
		// Show confirmation dialog
		if (VisualHandler.getInstance().showConfirmationDialog(LanguageTool.getString("REPAIR_TRACK_NUMBERS_MESSAGE"), LanguageTool.getString("REPAIR_TRACK_NUMBERS")) != JOptionPane.OK_OPTION)
			return;

		// Get all repository songs
		List<AudioFile> repositorySongs = RepositoryHandler.getInstance().getSongs();

		// Get songs with empty track number
		List<AudioFile> songsToBeRepaired = new ArrayList<AudioFile>();
		for (AudioFile song : repositorySongs)
			if (song.getTrackNumber() == 0) {
				songsToBeRepaired.add(song);
			}

		// Call track number edit
		editTrackNumber(songsToBeRepaired);
	}
}
