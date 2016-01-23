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

package net.sourceforge.atunes.kernel.executors;

import java.awt.Dialog.ModalityType;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.gui.views.dialogs.CoverNavigatorFrame;
import net.sourceforge.atunes.gui.views.dialogs.IndeterminateProgressDialog;
import net.sourceforge.atunes.kernel.controllers.coverNavigator.CoverNavigatorController;
import net.sourceforge.atunes.kernel.executors.processes.AddLyricsToTagProcess;
import net.sourceforge.atunes.kernel.executors.processes.ChangeTagsProcess;
import net.sourceforge.atunes.kernel.executors.processes.ChangeTitlesProcess;
import net.sourceforge.atunes.kernel.executors.processes.ClearTagsProcess;
import net.sourceforge.atunes.kernel.executors.processes.ExportFilesProcess;
import net.sourceforge.atunes.kernel.executors.processes.GetCoversFromAmazonProcess;
import net.sourceforge.atunes.kernel.executors.processes.SetAlbumNamesProcess;
import net.sourceforge.atunes.kernel.executors.processes.SetGenresProcess;
import net.sourceforge.atunes.kernel.executors.processes.SetTrackNumberProcess;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.Artist;

/**
 * This class is responsible of execute operations in secondary threads
 */
public class BackgroundExecutor {

	private static final int NORMAL_PRIORITY = Thread.NORM_PRIORITY;

	//private static final int MAX_PRIORITY = Thread.MAX_PRIORITY;

	public static void addLyrics(List<AudioFile> files) {
		run(new AddLyricsToTagProcess(files), NORMAL_PRIORITY);
	}

	public static void changeAlbumNames(List<AudioFile> files) {
		run(new SetAlbumNamesProcess(files), NORMAL_PRIORITY);
	}

	public static void changeGenres(List<AudioFile> files) {
		run(new SetGenresProcess(files), NORMAL_PRIORITY);
	}

	public static void changeTags(List<AudioFile> files, Map<String, ?> properties) {
		run(new ChangeTagsProcess(files, properties), NORMAL_PRIORITY);
	}

	public static void changeTitles(List<AudioFile> files) {
		run(new ChangeTitlesProcess(files), NORMAL_PRIORITY);
	}

	public static void changeTitles(Map<AudioFile, String> filesAndTitles) {
		run(new ChangeTitlesProcess(filesAndTitles), NORMAL_PRIORITY);
	}

	public static void changeTrackNumbers(Map<AudioFile, Integer> filesAndTracks) {
		run(new SetTrackNumberProcess(filesAndTracks), NORMAL_PRIORITY);
	}

	public static void clearTags(List<AudioFile> files) {
		run(new ClearTagsProcess(files), NORMAL_PRIORITY);
	}

	/**
	 * Exports songs from the repository to an external path
	 * 
	 * @param songs
	 *            List of songs to be exported
	 * @param exportStructure
	 *            Folder structure, either 1, 2 or 3
	 * @param filePattern
	 *            Filename pattern
	 * @param path
	 *            Where to export
	 * @param isMp3Device
	 *            If on a non-Windows system it is needed to write on a
	 *            device/partition with Windows filesystem limitation set to
	 *            true.
	 * @return Returns a process
	 */
	public static ExportFilesProcess exportFiles(List<AudioFile> songs, int exportStructure, String filePattern, String path, boolean isMp3Device) {
		final ExportFilesProcess exporter = new ExportFilesProcess(songs, exportStructure, filePattern, path, isMp3Device);
		run(exporter, NORMAL_PRIORITY);
		return exporter;
	}

	public static void getCoversFromAmazon(Artist artist, CoverNavigatorFrame coverNavigatorFrame, CoverNavigatorController coverNavigatorController) {
		IndeterminateProgressDialog progressDialog = new IndeterminateProgressDialog(coverNavigatorFrame);
		progressDialog.setModalityType(ModalityType.MODELESS);
		progressDialog.setVisible(true);
		run(new GetCoversFromAmazonProcess(artist, progressDialog, coverNavigatorController), NORMAL_PRIORITY);
	}

	public static void loadPlayList(List<String> files) {
		// Show progress dialog
		VisualHandler.getInstance().getProgressDialog().setVisible(true);
		Runnable r = PlayListIO.getLoadPlayListProcess(files);
		run(r, NORMAL_PRIORITY);
	}

	private static void run(Runnable r, int priority) {
		Thread t = new Thread(r);
		t.setPriority(priority);
		t.start();
	}

}
