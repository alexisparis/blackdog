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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.Cdda2wav;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.NoCdListener;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.kernel.modules.cdripper.encoders.Encoder;
import net.sourceforge.atunes.kernel.utils.FileNameUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class CdRipper {

	public static final String ARTIST_PATTERN = "%A";

	public static final String ALBUM_PATTERN = "%L";
	public static final String TITLE_PATTERN = "%T";
	public static final String TRACK_NUMBER = "%N";

	private Logger logger = new Logger();

	private Cdda2wav cddawav;
	private Encoder encoder;
	private ProgressListener listener;
	private boolean interrupted;

	private String artist;

	private String album;
	private int year;
	private String genre;
	private String fileNamePattern;

	public CdRipper() {
		cddawav = new Cdda2wav();
	}

	private boolean checkFolder(File folder) {
		boolean result = folder.exists() && folder.isDirectory();
		return result;
	}

	public CDInfo getCDInfo() {
		return cddawav.getCDInfo();
	}

	/**
	 * This prepares the filename for the encoder
	 */
	private String getFileName(List<String> titles, int trackNumber, String extension) {
		DecimalFormat df = new DecimalFormat("00");
		if (fileNamePattern == null)
			return StringUtils.getString("track", trackNumber, '.', extension);
		String result = StringUtils.getString(fileNamePattern, '.', extension);
		result = result.replaceAll(ARTIST_PATTERN, artist);
		result = result.replaceAll(ALBUM_PATTERN, album);
		result = result.replaceAll(TRACK_NUMBER, df.format(trackNumber));
		if (titles.size() > trackNumber - 1)
			// We need to place \\ before escape sequences otherwise the ripper hangs. We can not do this later.
			result = result.replaceAll(TITLE_PATTERN, titles.get(trackNumber - 1).replace("\\", "\\\\").replace("$", "\\$"));
		else
			result = result.replaceAll(TITLE_PATTERN, StringUtils.getString("track", trackNumber));
		// Replace known illegal characters. 
		result = FileNameUtils.getValidFileName(result);
		return result;
	}

	public boolean ripTracks(List<Integer> tracks, List<String> titles, File folder, List<String> artistNames, List<String> composerNames) {
		String extension = encoder != null ? encoder.getExtensionOfEncodedFiles() : "wav";
		logger.info(LogCategories.RIPPER, StringUtils.getString("Running cd ripping of ", tracks.size(), " to ", extension, "..."));
		long t0 = System.currentTimeMillis();
		if (!checkFolder(folder)) {
			logger.error(LogCategories.RIPPER, "Folder " + folder + " not found or not a directory");
			return false;
		}

		if (listener != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					listener.notifyProgress(0);
				}
			});
		}

		File wavFile = null;
		File resultFile = null;
		for (int i = 0; i < tracks.size(); i++) {
			if (!interrupted) {
				int trackNumber = tracks.get(i);
				wavFile = new File(StringUtils.getString(folder.getAbsolutePath(), "/track", trackNumber, ".wav"));
				final File resultFileTemp;
				if (encoder != null)
					resultFile = new File(StringUtils.getString(folder.getAbsolutePath(), '/', getFileName(titles, trackNumber, extension)));
				resultFileTemp = resultFile;

				boolean ripResult = false;
				if (!interrupted)
					ripResult = cddawav.cdda2wav(trackNumber, wavFile);

				if (!interrupted && ripResult && encoder != null) {
					boolean encodeResult = encoder.encode(wavFile, resultFile, (titles != null && titles.size() >= trackNumber ? titles.get(trackNumber - 1) : null), trackNumber,
							artistNames.get(trackNumber - 1), composerNames.get(trackNumber - 1));
					if (encodeResult && listener != null && !interrupted) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								listener.notifyFileFinished(resultFileTemp);
							}
						});
					}

					logger.info(LogCategories.RIPPER, "Deleting wav file...");
					wavFile.delete();

					if (interrupted && resultFile != null)
						resultFile.delete();
				} else if (interrupted)
					wavFile.delete();
				else if (!ripResult)
					logger.error(LogCategories.RIPPER, StringUtils.getString("Rip failed. Skipping track ", trackNumber, "..."));

				if (listener != null) {
					final int iHelp = i;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							listener.notifyProgress(iHelp + 1);
						}
					});
				}
			}
		}
		long t1 = System.currentTimeMillis();
		logger.info(LogCategories.RIPPER, StringUtils.getString("Process finished in ", (t1 - t0) / 1000.0, " seconds"));
		return true;
	}

	public void setAlbum(String album) {
		if (album == null || album.equals(""))
			this.album = LanguageTool.getString("UNKNOWN_ALBUM");
		else
			this.album = album;
		if (encoder != null)
			encoder.setAlbum(this.album);
	}

	public void setArtist(String artist) {
		if (artist == null || artist.equals(""))
			this.artist = LanguageTool.getString("UNKNOWN_ARTIST");
		else
			this.artist = artist;
		if (encoder != null)
			encoder.setArtist(this.artist);
	}

	public void setDecoderListener(ProgressListener listener) {
		cddawav.setListener(listener);
	}

	public void setEncoder(Encoder encoder) {
		this.encoder = encoder;
	}

	public void setEncoderListener(ProgressListener listener) {
		if (encoder != null)
			encoder.setListener(listener);
	}

	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

	public void setGenre(String genre) {
		this.genre = genre;
		if (encoder != null) {
			encoder.setGenre(this.genre);
		}
	}

	public void setNoCdListener(NoCdListener listener) {
		cddawav.setNoCdListener(listener);
	}

	public void setTotalProgressListener(ProgressListener listener) {
		this.listener = listener;
	}

	public void setYear(int year) {
		this.year = year;
		if (encoder != null)
			encoder.setYear(this.year);
	}

	public void stop() {
		interrupted = true;
		cddawav.stop();
		if (encoder != null)
			encoder.stop();
	}
}
