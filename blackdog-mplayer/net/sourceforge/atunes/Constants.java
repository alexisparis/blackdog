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

package net.sourceforge.atunes;

import net.sourceforge.atunes.utils.StringUtils;

/**
 * Constants used by application
 * 
 * @author fleax
 * 
 */
public class Constants {

	/**
	 * MAJOR NUMBER
	 */
	public static final int APP_MAJOR_NUMBER = 1;

	/**
	 * MINOR NUMBER
	 */
	public static final int APP_MINOR_NUMBER = 8;

	/**
	 * REVISION NUMBER
	 */
	public static final int APP_REVISION_NUMBER = 2;

	/**
	 * Full version number: concatenation of major, minor and revision numbers
	 */
	public static final String APP_VERSION_NUMBER = StringUtils.getString(Integer.toString(APP_MAJOR_NUMBER), ".", Integer.toString(APP_MINOR_NUMBER), ".", Integer
			.toString(APP_REVISION_NUMBER));

	/**
	 * File containing log4j properties
	 */
	public static final String LOG4J_FILE = "log4j.properties";

	/**
	 * File containing extended log properties
	 */
	public static final String EXTENDED_LOG_FILE = "extendedLog.properties";

	/**
	 * Application Home Page
	 */
	public static final String APP_WEB = "http://www.atunes.org";

	/**
	 * Application Home Page at Sourceforge
	 */
	public static final String APP_SOURCEFORGE_WEB = "http://sourceforge.net/projects/atunes";

	public static final String CONTRIBUTORS_WEB = "http://www.atunes.org/?page_id=7";

	public static final String CONTRIBUTORS_WANTED = "http://www.atunes.org/?page_id=30";

	/**
	 * Web site for reporting bug and requesting features
	 */
	public static final String REPORT_BUG_OR_REQUEST_FEATURE_URL = "http://sourceforge.net/tracker/?group_id=161929";

	/**
	 * Application name
	 */
	public static final String APP_NAME = "aTunes";

	/**
	 * Application description
	 */
	public static final String APP_DESCRIPTION = "GPL Audio Player";

	/**
	 * Version string
	 */
	public static final String APP_VERSION = StringUtils.getString("Version ", APP_VERSION_NUMBER);

	/**
	 * Author and year
	 */
	public static final String APP_AUTHOR = "2006-2008 The aTunes Team";

	/**
	 * File where repository information is stored
	 */
	public static final String CACHE_REPOSITORY_NAME = "repository.dat";

	public static final String XML_CACHE_REPOSITORY_NAME = "repository.xml";

	public static final String CACHE_FAVORITES_NAME = "favorites.dat";

	public static final String XML_CACHE_FAVORITES_NAME = "favorites.xml";

	/**
	 * Image width at FilePropertiesPanel
	 */
	public static final int IMAGE_WIDTH = 90;

	/**
	 * Image height at FilePropertiesPanel
	 */
	public static final int IMAGE_HEIGHT = 90;

	/**
	 * Image width at FilePropertiesDialog
	 */
	public static final int FILE_PROPERTIES_DIALOG_IMAGE_WIDTH = 120;

	/**
	 * Image height at FilePropertiesDialog
	 */
	public static final int FILE_PROPERTIES_DIALOG_IMAGE_HEIGHT = 120;

	/**
	 * Image width at tooltips
	 */
	public static final int TOOLTIP_IMAGE_WIDTH = 60;

	/**
	 * Image height at tooltips
	 */
	public static final int TOOLTIP_IMAGE_HEIGHT = 60;

	/**
	 * Thumbs window width
	 */
	public static final int THUMBS_WINDOW_WIDTH = 900;

	/**
	 * Thumbs window height
	 */
	public static final int THUMBS_WINDOW_HEIGHT = 700;

	/**
	 * Max number of repositories saved
	 */
	public static final int MAX_RECENT_REPOSITORIES = 5;

	/**
	 * File where application configuracion is stored
	 */
	public static final String PROPERTIES_FILE = "aTunesConfig.xml";

	/**
	 * File where last playlist is stored
	 */
	public static final String LAST_PLAYLIST_FILE = "playList.dat";

	/**
	 * Directory where are Windows binaries (i.e. mplayer, lame, etc)
	 */
	public static final String WINDOWS_TOOLS_DIR = "win_tools";

	/**
	 * Image Width at AudioScrobblerPanel
	 */
	public static final int AUDIO_SCROBBLER_IMAGE_WIDTH = 56;

	/**
	 * Image Height at AudioScrobblerPanel
	 */
	public static final int AUDIO_SCROBBLER_IMAGE_HEIGHT = 56;

	/**
	 * File where radios are stored
	 */
	public static final String RADIO_CACHE = "radios.xml";

	/**
	 * File where podcast feeds are stored
	 */
	public static final String PODCAST_FEED_CACHE = "podcastFeeds.xml";

	/**
	 * Size in pixels of Full Screen Covers
	 * 
	 */
	public static final int FULL_SCREEN_COVER = 350;

	/**
	 * Port number for multiple instances socket communication
	 */
	public static final int MULTIPLE_INSTANCES_SOCKET = 7777;

	/**
	 * Directory where are translations
	 */
	public static final String TRANSLATIONS_DIR = "translations";

	/**
	 * Lyrics engine configuration file
	 */
	public static final String LYRICS_ENGINES_FILE = "lyricsEngines.properties";

	/**
	 * Cache dir
	 */
	public static final String CACHE_DIR = "cache";

	/**
	 * Audioscrobbler cache dir
	 */
	public static final String AUDIOSCROBBLER_CACHE_DIR = "audioscrobbler";

	/**
	 * Audioscrobbler album cover cache dir
	 */
	public static final String AUDIOSCROBBLER_ALBUM_COVER_CACHE_DIR = "album_covers";

	/**
	 * Audioscrobbler album info cache dir
	 */
	public static final String AUDIOSCROBBLER_ALBUM_INFO_CACHE_DIR = "album_info";

	/**
	 * Audioscrobbler artist image cache dir
	 */
	public static final String AUDIOSCROBBLER_ARTIST_IMAGE_CACHE_DIR = "artist_images";

	/**
	 * Audioscrobbler artist info cache dir
	 */
	public static final String AUDIOSCROBBLER_ARTIST_SIMILAR_CACHE_DIR = "artist_similar";

	/**
	 * Audioscrobbler artist thumb cache dir
	 */
	public static final String AUDIOSCROBBLER_ARTIST_THUMB_CACHE_DIR = "artist_thumbs";

	/**
	 * Audioscrobbler artist info cache dir
	 */
	public static final String AUDIOSCROBBLER_ARTIST_INFO_CACHE_DIR = "artist_info";

	/**
	 * Audioscrobbler artist wiki cache dir
	 */
	public static final String AUDIOSCROBBLER_ARTIST_WIKI_CACHE_DIR = "artist_wiki";

	/**
	 * Lyrics cache dir
	 */
	public static final String LYRICS_CACHE_DIR = "lyrics";
	
	/**
	 * Equalizer presets file
	 */
	public static final String EQUALIZER_PRESETS_FILE = "presets.properties";
}
