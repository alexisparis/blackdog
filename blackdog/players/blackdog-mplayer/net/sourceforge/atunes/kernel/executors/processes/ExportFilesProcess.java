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

package net.sourceforge.atunes.kernel.executors.processes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.dialogs.CopyProgressDialog;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.device.DeviceCopyFinishListener;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.FileNameUtils;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.apache.commons.io.FileUtils;

/**
 * 
 * Exports (song) files to a partition/device and checks if filename is valid
 * 
 */
public class ExportFilesProcess implements Runnable {

	public static final int ALL_EXPORT = 1;

	public static final int SELECTION_EXPORT = 2;
	public static final int FAVORITES_EXPORT = 3;
	public static final int FLAT_STRUCTURE = 1;

	public static final int ARTIST_STRUCTURE = 2;
	public static final int FULL_STRUCTURE = 3;

	private Logger logger = new Logger();

	private List<AudioFile> songs;
	private int structure;
	private String filePattern;
	private String path;

	private boolean interrupt;
	private boolean isMp3Device;

	private List<DeviceCopyFinishListener> listeners = new ArrayList<DeviceCopyFinishListener>();

	/**
	 * Exports songs from the repository to an external path, call via
	 * BackgroundExecutor.java . Sets the values
	 * 
	 * @param songs
	 *            List with songs to export
	 * @param structure
	 *            Folder structure, either 1, 2 or 3
	 * @param filePattern
	 *            Filename pattern
	 * @param path
	 *            Path to where the files should be exported
	 * @param isMp3Device
	 *            Set true if writting to a device/partition with Windows OS
	 *            filename limitations
	 */
	public ExportFilesProcess(List<AudioFile> songs, int structure, String filePattern, String path, boolean isMp3Device) {
		this.songs = songs;
		this.structure = structure;
		this.filePattern = filePattern;
		this.path = path;
		this.isMp3Device = isMp3Device;
	}

	public void addFinishListener(DeviceCopyFinishListener listener) {
		listeners.add(listener);
	}

	/**
	 * Prepares the directory structure in which the song will be written
	 * 
	 * @param song
	 *            Song to be written
	 * @param destination
	 *            Destination path
	 * @param struct
	 *            structure Folder structure, either 1, 2 or 3
	 * @return Returns the directory structure with full path where the file
	 *         will be written
	 */
	private File getDirectory(AudioFile song, File destination, int struct) {
		if (struct == FLAT_STRUCTURE)
			return new File(FileNameUtils.getValidFolderName(destination.toString().replace("\\", "\\\\").replace("$", "\\$"), isMp3Device));
		else if (struct == ARTIST_STRUCTURE)
			return new File(StringUtils.getString(destination.getAbsolutePath(), SystemProperties.fileSeparator, FileNameUtils.getValidFolderName(song.getArtist().replace("\\",
					"\\\\").replace("$", "\\$"), isMp3Device)));
		else
			// FULL_STRUCTURE
			return new File(StringUtils.getString(destination.getAbsolutePath(), SystemProperties.fileSeparator, FileNameUtils.getValidFolderName(song.getArtist().replace("\\",
					"\\\\").replace("$", "\\$")
					+ SystemProperties.fileSeparator + song.getAlbum().replace("\\", "\\\\").replace("$", "\\$"), isMp3Device)));
	}

	/**
	 * Prepares the filename in order to write it
	 * 
	 * @param pattern
	 *            Filename pattern
	 * @param song
	 *            Song file to be written
	 * @return Returns a (hopefully) valid filename
	 */
	private String getNewName(String pattern, AudioFile song) {
		String result = pattern.replaceAll("%T", song.getTitleOrFileName());
		result = result.replaceAll("%A", song.getArtist());
		result = result.replaceAll("%L", song.getAlbum());
		result = result + song.getName().substring(song.getName().lastIndexOf('.'));
		// We need to place \\ before escape sequences otherwise the ripper hangs. We can not do this later.
		result = result.replace("\\", "\\\\").replace("$", "\\$");
		result = FileNameUtils.getValidFileName(result);
		return result;
	}

	public void notifyCancel() {
		interrupt = true;
	}

	@Override
	public void run() {
		logger.debug(LogCategories.PROCESS);
		File destination = new File(path);
		int filesExported = 0;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				VisualHandler.getInstance().getExportProgressDialog().getProgressLabel().setText(Integer.toString(0));
			}
		});
		boolean errors = false;
		logger.info(LogCategories.PROCESS, StringUtils.getString("Exporting songs to ", destination));
		try {
			for (Iterator<AudioFile> it = songs.iterator(); it.hasNext() && !interrupt;) {
				AudioFile song = it.next();
				File destDir = getDirectory(song, destination, structure);
				String newName;

				if (filePattern != null) {
					newName = getNewName(filePattern, song);
				}

				else {
					// Files written to mp3 devices do not have any filepattern passed, so only check if
					// if filename is valid
					newName = FileNameUtils.getValidFileName(song.getName().replace("\\", "\\\\").replace("$", "\\$"), isMp3Device);
				}

				File destFile = new File(StringUtils.getString(destDir.getAbsolutePath(), SystemProperties.fileSeparator, newName));
				// Now that we (supposedly) have a valid filename write file
				FileUtils.copyFile(song, destFile);
				final int filesExportedHelp = filesExported++;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						VisualHandler.getInstance().getExportProgressDialog().getProgressLabel().setText(Integer.toString(filesExportedHelp));
						VisualHandler.getInstance().getExportProgressDialog().getProgressBar().setValue(filesExportedHelp);
					}
				});
			}
		} catch (IOException e) {
			errors = true;
			logger.error(LogCategories.PROCESS, e.getMessage());
		}
		logger.info(LogCategories.PROCESS, "Exporting process done");
		final CopyProgressDialog dialog = VisualHandler.getInstance().getExportProgressDialog();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dialog.setVisible(false);
			}
		});
		if (errors) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("ERRORS_IN_EXPORT_PROCESS"));
				}
			});
		}

		for (final DeviceCopyFinishListener l : listeners) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					l.notifyCopyFinish();
				}
			});
		}
	}
}
