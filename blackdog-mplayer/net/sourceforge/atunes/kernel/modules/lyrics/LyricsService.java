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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.PropertyResourceBundle;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class LyricsService {

	/**
	 * Logger
	 */
	private static Logger logger = new Logger();

	/**
	 * Contains a list of LyricsEngine to get lyrics
	 */
	private static List<LyricsEngine> lyricsEngines = loadEngines();

	/**
	 * Public method to retrieve lyrics for a song
	 * 
	 * @param artist
	 * @param song
	 * @return
	 */
	public static String getLyrics(String artist, String song) {
		logger.debug(LogCategories.SERVICE, artist, song);

		// Try to get from cache
		String lyric = LyricsCache.retrieveLyric(artist, song);
		if (lyric == null) {
			// If any engine is loaded
			if (lyricsEngines != null) {
				// Ask for lyrics until a lyric is found in some engine
				int i = 0;
				while (i < lyricsEngines.size() && lyric == null) {
					lyric = lyricsEngines.get(i++).getLyricsFor(artist, song);
				}
			}
			LyricsCache.storeLyric(artist, song, lyric);
		}
		// Return lyric
		return lyric;
	}

	/**
	 * Loads lyrics engines from configuration file
	 */
	private static List<LyricsEngine> loadEngines() {
		try {
			List<LyricsEngine> lyricsEngines1 = new ArrayList<LyricsEngine>();

			// Load lyrics engine configuration
			PropertyResourceBundle enginesBundle = new PropertyResourceBundle(new FileInputStream(Constants.LYRICS_ENGINES_FILE));
			Enumeration<String> keys = enginesBundle.getKeys();

			List<String> keysList = new ArrayList<String>();
			while (keys.hasMoreElements())
				keysList.add(keys.nextElement());

			Collections.sort(keysList);

			// Load engines
			for (String key : keysList) {
				try {
					Class<?> engineClass = Class.forName(enginesBundle.getString(key));
					if (engineClass.getSuperclass().equals(LyricsEngine.class)) {
						lyricsEngines1.add((LyricsEngine) engineClass.newInstance());
					} else {
						logger.error(LogCategories.SERVICE, StringUtils.getString("Class ", enginesBundle.getString(key), " is not a LyricsEngine implementation"));
					}
				} catch (ClassNotFoundException e) {
					logger.error(LogCategories.SERVICE, StringUtils.getString("Class ", enginesBundle.getString(key), " could not be found"));
				} catch (IllegalAccessException e1) {
					logger.error(LogCategories.SERVICE, e1);
				}
			}
			return lyricsEngines1;
		} catch (Exception e) {
			logger.error(LogCategories.SERVICE, StringUtils.getString("Could not load lyrics engines ", e));
			return null;
		}
	}

}
