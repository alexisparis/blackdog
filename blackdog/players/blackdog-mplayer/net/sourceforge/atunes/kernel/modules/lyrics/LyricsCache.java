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

package net.sourceforge.atunes.kernel.modules.lyrics;

import java.io.File;
import java.io.IOException;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.kernel.utils.XMLUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.apache.commons.io.FileUtils;

public class LyricsCache {

	/**
	 * Logger
	 */
	private static Logger logger = new Logger();

	/**
	 * Album Cover Cache dir
	 */
	private static File lyricsCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.fileSeparator, Constants.CACHE_DIR,
			SystemProperties.fileSeparator, Constants.LYRICS_CACHE_DIR));

	/**
	 * Clears the cache
	 * 
	 * @return If an IOException occured during clearing
	 */
	public static synchronized boolean clearCache() {
		try {
			FileUtils.cleanDirectory(lyricsCacheDir);
		} catch (IOException e) {
			logger.info(LogCategories.FILE_DELETE, "Could not delete all files from lyricsr cache");
			return true;
		}
		return false;
	}

	/**
	 * Lyrics Filename
	 * 
	 * @param album
	 * @return
	 */
	private static String getFileNameForLyric(String artist, String title) {
		return StringUtils.getString(artist.hashCode(), title.hashCode(), ".xml");
	}

	/**
	 * Absolute Path to Lyric Filename
	 * 
	 * @param album
	 * @return
	 * @throws IOException
	 */
	private static String getFileNameForLyricAtCache(String artist, String title) throws IOException {
		File lyricCacheDir = getLyricsCacheDir();

		if (lyricCacheDir == null) {
			return null;
		}

		return StringUtils.getString(lyricCacheDir.getAbsolutePath(), SystemProperties.fileSeparator, getFileNameForLyric(artist, title));
	}

	/**
	 * Private getter for lyricsCacheDir. If dir does not exist, it's created
	 * 
	 * @return
	 * @throws IOException
	 */
	private synchronized static File getLyricsCacheDir() throws IOException {
		if (!lyricsCacheDir.exists())
			FileUtils.forceMkdir(lyricsCacheDir);
		return lyricsCacheDir;
	}

	/**
	 * Retrieves an Artist wiki from cache
	 * 
	 * @param artist
	 * @return
	 */
	public static synchronized String retrieveLyric(String artist, String title) {
		try {
			String path = getFileNameForLyricAtCache(artist, title);
			if (path != null && new File(path).exists())
				return (String) XMLUtils.readBeanFromFile(path);
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
		return null;
	}

	/**
	 * Stores an Album Cover at cache
	 * 
	 * @param album
	 * @param cover
	 */
	public static synchronized void storeLyric(String artist, String title, String lyric) {
		if (artist == null || title == null || lyric == null)
			return;

		try {
			String fileAbsPath = getFileNameForLyricAtCache(artist, title);
			if (fileAbsPath != null) {
				XMLUtils.writeBeanToFile(lyric, fileAbsPath);
				logger.debug(LogCategories.CACHE, StringUtils.getString("Stored lyric for ", title));
			}
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
	}
}
