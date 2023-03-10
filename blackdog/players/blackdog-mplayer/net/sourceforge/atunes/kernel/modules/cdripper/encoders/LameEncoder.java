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

package net.sourceforge.atunes.kernel.modules.cdripper.encoders;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.cdripper.ProgressListener;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.TagModifier;
import net.sourceforge.atunes.kernel.utils.ClosingUtils;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.kernel.utils.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class LameEncoder implements Encoder {

	public static final String LAME = "lame";
	public static final String QUALITY = "-b";
	public static final String PRESET = "--preset";
	public static final String TITLE = "--tt";
	public static final String ARTIST = "--ta";
	public static final String ALBUM = "--tl";
	public static final String TRACK = "--tn";
	public static final String VERSION = "--version";

	private Process p;

	private Logger logger = new Logger();

	private ProgressListener listener;

	private String albumArtist;
	private String album;
	private int year;
	private String genre;

	private String quality;

	/**
	 * Test the presence of the mp3 encoder lame.
	 * 
	 * @return Returns true if lame was found, false otherwise.
	 */
	public static boolean testTool() {
		if (SystemProperties.SYSTEM == OperatingSystem.WINDOWS)
			return true;

		BufferedReader stdInput = null;
		try {
			Process p;
			if (SystemProperties.SYSTEM == OperatingSystem.WINDOWS)
				p = new ProcessBuilder(StringUtils.getString(Constants.WINDOWS_TOOLS_DIR, SystemProperties.fileSeparator, LAME), VERSION).start();
			else
				p = new ProcessBuilder(LAME, VERSION).start();
			stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			while (stdInput.readLine() != null) {
				// Nothing to do
			}

			int code = p.waitFor();
			if (code != 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			ClosingUtils.close(stdInput);
		}
	}

	/**
	 * Encode the wav file and tags it using entagged.
	 * 
	 * @param wavFile
	 *            The filename and path of the wav file that should be encoded
	 * @param mp3File
	 *            The name of the new file to be created
	 * @param title
	 *            The title of the song (only title, not artist and album)
	 * @param trackNumber
	 *            The track number of the song
	 * @return Returns true if encoding was successfull, false otherwise.
	 */
	@Override
	public boolean encode(File wavFile, File mp3File, String title, int trackNumber, String artist, String composer) {
		logger.info(LogCategories.LAME, StringUtils.getString("Mp3 encoding process started... ", wavFile.getName(), " -> ", mp3File.getName()));
		BufferedReader stdInput = null;
		try {
			// Prepare and execute the lame command
			List<String> command = new ArrayList<String>();
			if (SystemProperties.SYSTEM == OperatingSystem.WINDOWS)
				command.add(StringUtils.getString(Constants.WINDOWS_TOOLS_DIR, SystemProperties.fileSeparator, LAME));
			else
				command.add(LAME);
			// Presets don't need the -b option, but --preset, so check if preset is used
			if (quality.contains("insane") || quality.contains("extreme") || quality.contains("medium") || quality.contains("standard")) {
				command.add(PRESET);
			} else {
				command.add(QUALITY);
			}
			command.add(quality);

			command.add(wavFile.getAbsolutePath());
			command.add(mp3File.getAbsolutePath());
			p = new ProcessBuilder(command).start();
			stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String s = null;
			int percent = -1;
			while ((s = stdInput.readLine()) != null) {
				if (listener != null) {
					if (s.matches(".*\\(..%\\).*")) {
						int aux = Integer.parseInt((s.substring(s.indexOf('(') + 1, s.indexOf('%'))).trim());
						if (aux != percent) {
							percent = aux;
							final int percentHelp = percent;
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									listener.notifyProgress(percentHelp);
								}
							});
						}
					} else if (s.matches(".*\\(100%\\).*")) {
						if (percent != 100) {
							percent = 100;
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									listener.notifyProgress(100);
								}
							});
						}
					}
				}
			}

			int code = p.waitFor();
			if (code != 0) {
				logger.error(LogCategories.LAME, StringUtils.getString("Process returned code ", code));
				return false;
			}
			// Use entagged for writting tag
			try {
				AudioFile audiofile = new AudioFile(mp3File.getAbsolutePath());
				Map<String, String> Map = new HashMap<String, String>();
				Tag tag = AudioFile.getNewTag(audiofile, Map);

				tag.setAlbum(album);
				tag.setAlbumArtist(albumArtist);
				tag.setArtist(artist);
				tag.setYear(year);
				tag.setTitle(title);
				tag.setGenre(genre);
				tag.setComposer(composer);
				tag.setTrackNumber(trackNumber);

				TagModifier.setInfo(audiofile, tag);

			} catch (Exception e) {
				logger.error(LogCategories.OGGENC, StringUtils.getString("entagged: Process execution caused exception ", e));
				return false;
			}
			logger.info(LogCategories.LAME, "Encoded ok!!");
			return true;
		} catch (Exception e) {
			logger.error(LogCategories.LAME, StringUtils.getString("Process execution caused exception ", e));
			return false;
		} finally {
			ClosingUtils.close(stdInput);
		}
	}

	/**
	 * @return Returns the extension of the encoded file
	 */
	@Override
	public String getExtensionOfEncodedFiles() {
		return "mp3";
	}

	@Override
	public void setAlbum(String album) {
		this.album = album;
	}

	@Override
	public void setArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}

	@Override
	public void setGenre(String genre) {
		this.genre = genre;
	}

	@Override
	public void setListener(ProgressListener listener) {
		this.listener = listener;
	}

	@Override
	public void setQuality(String quality) {
		this.quality = quality;
	}

	@Override
	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public void stop() {
		if (p != null) {
			p.destroy();
		}
	}

}
