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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.dialogs.CopyProgressDialog;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.apache.commons.io.FileUtils;

public class ImportProcess extends Thread {

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

	private boolean interrupt;

	public ImportProcess(List<AudioFile> songs, int structure, String filePattern) {
		this.songs = songs;
		this.structure = structure;
		this.filePattern = filePattern;
	}

	private File getDirectory(AudioFile song, File destination, int struct) {
		if (struct == FLAT_STRUCTURE)
			return destination;
		else if (struct == ARTIST_STRUCTURE)
			return new File(StringUtils.getString(destination.getAbsolutePath(), SystemProperties.fileSeparator, song.getArtist()));
		else
			// FULL_STRUCTURE
			return new File(StringUtils
					.getString(destination.getAbsolutePath(), SystemProperties.fileSeparator, song.getArtist() + SystemProperties.fileSeparator, song.getAlbum()));
	}

	private String getNewName(String pattern, AudioFile song) {
		String result = pattern.replaceAll("%T", song.getTitleOrFileName());
		result = result.replaceAll("%A", song.getArtist());
		result = result.replaceAll("%L", song.getAlbum());
		result = StringUtils.getString(result, song.getName().substring(song.getName().lastIndexOf('.')));
		return result;
	}

	public void notifyCancel() {
		interrupt = true;
	}

	@Override
	public void run() {
		super.run();
		File destination = new File(RepositoryHandler.getInstance().getRepositoryPath());
		int filesImported = 0;

		final int filesImportedHelp = filesImported;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				VisualHandler.getInstance().getCopyProgressDialog().getProgressLabel().setText(Integer.toString(filesImportedHelp));
			}
		});
		boolean errors = false;
		logger.info(LogCategories.PROCESS, "Copying songs from device to repository");
		try {
			for (Iterator<AudioFile> it = songs.iterator(); it.hasNext() && !interrupt;) {
				AudioFile song = it.next();
				File destDir = getDirectory(song, destination, structure);
				FileUtils.copyFileToDirectory(song, destDir);
				if (filePattern != null) {
					File destFile = new File(StringUtils.getString(destDir.getAbsolutePath(), SystemProperties.fileSeparator, song.getName()));
					String newName = getNewName(filePattern, song);
					destFile.renameTo(new File(StringUtils.getString(destDir.getAbsolutePath(), SystemProperties.fileSeparator, newName)));
				}
				final int filesImportedHelp2 = filesImported++;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						VisualHandler.getInstance().getCopyProgressDialog().getProgressLabel().setText(Integer.toString(filesImportedHelp2));
						VisualHandler.getInstance().getCopyProgressDialog().getProgressBar().setValue(filesImportedHelp2);
					}
				});
			}
		} catch (IOException e) {
			errors = true;
			logger.error(LogCategories.PROCESS, e.getMessage());
		}
		logger.info(LogCategories.PROCESS, "Copying process done");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				CopyProgressDialog dialog = VisualHandler.getInstance().getCopyProgressDialog();
				dialog.setVisible(false);
			}
		});
		if (errors) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("ERRORS_IN_COPYING_PROCESS"));
				}
			});
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Force a refresh of repository to add new songs
				RepositoryHandler.getInstance().refreshRepository();
			}
		});
	}
}
