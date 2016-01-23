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

package net.sourceforge.atunes.kernel.modules.audioscrobbler;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.utils.PictureExporter;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.kernel.utils.XMLUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.apache.commons.io.FileUtils;

public class AudioScrobblerCache {

	/**
	 * Logger
	 */
	private static Logger logger = new Logger();

	/**
	 * Album Cover Cache dir
	 */
	private static File albumCoverCacheDir = new File(StringUtils
			.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.fileSeparator, Constants.CACHE_DIR, SystemProperties.fileSeparator,
					Constants.AUDIOSCROBBLER_CACHE_DIR, SystemProperties.fileSeparator, Constants.AUDIOSCROBBLER_ALBUM_COVER_CACHE_DIR));

	/**
	 * Album Cover Cache dir
	 */
	private static File albumInfoCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.fileSeparator, Constants.CACHE_DIR,
			SystemProperties.fileSeparator, Constants.AUDIOSCROBBLER_CACHE_DIR, SystemProperties.fileSeparator, Constants.AUDIOSCROBBLER_ALBUM_INFO_CACHE_DIR));

	/**
	 * Artist thumbs cache dir
	 */
	private static File artistThumbCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.fileSeparator,
			Constants.CACHE_DIR, SystemProperties.fileSeparator, Constants.AUDIOSCROBBLER_CACHE_DIR, SystemProperties.fileSeparator,
			Constants.AUDIOSCROBBLER_ARTIST_THUMB_CACHE_DIR));

	/**
	 * Artist image cache dir
	 */
	private static File artistImageCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.fileSeparator,
			Constants.CACHE_DIR, SystemProperties.fileSeparator, Constants.AUDIOSCROBBLER_CACHE_DIR, SystemProperties.fileSeparator,
			Constants.AUDIOSCROBBLER_ARTIST_IMAGE_CACHE_DIR));

	/**
	 * Artist image cache dir
	 */
	private static File artistSimilarCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.fileSeparator,
			Constants.CACHE_DIR, SystemProperties.fileSeparator, Constants.AUDIOSCROBBLER_CACHE_DIR, SystemProperties.fileSeparator,
			Constants.AUDIOSCROBBLER_ARTIST_SIMILAR_CACHE_DIR));

	/**
	 * Artist info cache dir
	 */
	private static File artistInfoCacheDir = new File(StringUtils
			.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.fileSeparator, Constants.CACHE_DIR, SystemProperties.fileSeparator,
					Constants.AUDIOSCROBBLER_CACHE_DIR, SystemProperties.fileSeparator, Constants.AUDIOSCROBBLER_ARTIST_INFO_CACHE_DIR));

	/**
	 * Artist info cache dir
	 */
	private static File artistWikiCacheDir = new File(StringUtils
			.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.fileSeparator, Constants.CACHE_DIR, SystemProperties.fileSeparator,
					Constants.AUDIOSCROBBLER_CACHE_DIR, SystemProperties.fileSeparator, Constants.AUDIOSCROBBLER_ARTIST_WIKI_CACHE_DIR));

	/**
	 * Clears the cache
	 * 
	 * @return If an IOException occured during clearing
	 */
	public static synchronized boolean clearCache() {
		boolean exception = false;
		try {
			FileUtils.cleanDirectory(albumCoverCacheDir);
		} catch (IOException e) {
			logger.info(LogCategories.FILE_DELETE, "Could not delete all files from album cover cache");
			exception = true;
		}
		try {
			FileUtils.cleanDirectory(albumInfoCacheDir);
		} catch (IOException e) {
			logger.info(LogCategories.FILE_DELETE, "Could not delete all files from album info cache");
			exception = true;
		}
		try {
			FileUtils.cleanDirectory(artistImageCacheDir);
		} catch (IOException e) {
			logger.info(LogCategories.FILE_DELETE, "Could not delete all files from artist image cache");
			exception = true;
		}
		try {
			FileUtils.cleanDirectory(artistInfoCacheDir);
		} catch (IOException e) {
			logger.info(LogCategories.FILE_DELETE, "Could not delete all files from artist info cache");
			exception = true;
		}
		try {
			FileUtils.cleanDirectory(artistSimilarCacheDir);
		} catch (IOException e) {
			logger.info(LogCategories.FILE_DELETE, "Could not delete all files from similar artist cache");
			exception = true;
		}
		try {
			FileUtils.cleanDirectory(artistThumbCacheDir);
		} catch (IOException e) {
			logger.info(LogCategories.FILE_DELETE, "Could not delete all files from artist thumbs cache");
			exception = true;
		}
		try {
			FileUtils.cleanDirectory(artistWikiCacheDir);
		} catch (IOException e) {
			logger.info(LogCategories.FILE_DELETE, "Could not delete all files from artist wiki cache");
			exception = true;
		}
		return exception;
	}

	/**
	 * Private getter for albumCoverCacheDir. If dir does not exist, it's
	 * created
	 * 
	 * @return
	 * @throws IOException
	 */
	private static synchronized File getAlbumCoverCacheDir() throws IOException {
		if (!albumCoverCacheDir.exists())
			FileUtils.forceMkdir(albumCoverCacheDir);
		return albumCoverCacheDir;
	}

	/**
	 * Private getter for albumInfoCacheDir. If dir does not exist, it's created
	 * 
	 * @return
	 * @throws IOException
	 */
	private static synchronized File getAlbumInfoCacheDir() throws IOException {
		if (!albumInfoCacheDir.exists())
			FileUtils.forceMkdir(albumInfoCacheDir);
		return albumInfoCacheDir;
	}

	/**
	 * Private getter for artistImageCacheDir. If dir does not exist, it's
	 * created
	 * 
	 * @return
	 * @throws IOException
	 */
	private static synchronized File getArtistImageCacheDir() throws IOException {
		if (!artistImageCacheDir.exists())
			FileUtils.forceMkdir(artistImageCacheDir);
		return artistImageCacheDir;
	}

	/**
	 * Private getter for artistInfoCacheDir. If dir does not exist, it's
	 * created
	 * 
	 * @return
	 * @throws IOException
	 */
	private static synchronized File getArtistInfoCacheDir() throws IOException {
		if (!artistInfoCacheDir.exists())
			FileUtils.forceMkdir(artistInfoCacheDir);
		return artistInfoCacheDir;
	}

	/**
	 * Private getter for artistSimilarCacheDir. If dir does not exist, it's
	 * created
	 * 
	 * @return
	 * @throws IOException
	 */
	private static synchronized File getArtistSimilarCacheDir() throws IOException {
		if (!artistSimilarCacheDir.exists())
			FileUtils.forceMkdir(artistSimilarCacheDir);
		return artistSimilarCacheDir;
	}

	/**
	 * Private getter for artistThumbCacheDir. If dir does not exist, it's
	 * created
	 * 
	 * @return
	 * @throws IOException
	 */
	private static synchronized File getArtistThumbsCacheDir() throws IOException {
		if (!artistThumbCacheDir.exists())
			FileUtils.forceMkdir(artistThumbCacheDir);
		return artistThumbCacheDir;
	}

	/**
	 * Private getter for artistWikiCacheDir. If dir does not exist, it's
	 * created
	 * 
	 * @return
	 * @throws IOException
	 */
	private static synchronized File getArtistWikiCacheDir() throws IOException {
		if (!artistWikiCacheDir.exists())
			FileUtils.forceMkdir(artistWikiCacheDir);
		return artistWikiCacheDir;
	}

	/**
	 * Album Cover Filename
	 * 
	 * @param album
	 * @return
	 */
	private static String getFileNameForAlbumCover(AudioScrobblerAlbum album) {
		return StringUtils.getString(album.getBigCoverURL().hashCode(), ".", PictureExporter.FILES_EXTENSION);
	}

	/**
	 * Absolute Path to Album Cover Filename
	 * 
	 * @param album
	 * @return
	 * @throws IOException
	 */
	private static String getFileNameForAlbumCoverAtCache(AudioScrobblerAlbum album) throws IOException {
		File albumCoverCacheDir = getAlbumCoverCacheDir();

		if (albumCoverCacheDir == null) {
			return null;
		}

		return StringUtils.getString(albumCoverCacheDir.getAbsolutePath(), SystemProperties.fileSeparator, getFileNameForAlbumCover(album));
	}

	/**
	 * Album Cover Filename
	 * 
	 * @param album
	 * @return
	 */
	private static String getFileNameForAlbumInfo(String artist, String album) {
		return StringUtils.getString(artist.hashCode(), album.hashCode(), ".xml");
	}

	/**
	 * Absolute Path to Album Info Filename
	 * 
	 * @param album
	 * @return
	 * @throws IOException
	 */
	private static String getFileNameForAlbumInfoAtCache(String artist, String album) throws IOException {
		File albumInfoCacheDir = getAlbumInfoCacheDir();

		if (albumInfoCacheDir == null) {
			return null;
		}

		return StringUtils.getString(albumInfoCacheDir.getAbsolutePath(), SystemProperties.fileSeparator, getFileNameForAlbumInfo(artist, album));
	}

	/**
	 * Artist Image Filename
	 * 
	 * @param artist
	 * @return
	 */
	private static String getFileNameForArtistImage(AudioScrobblerSimilarArtists artist) {
		return StringUtils.getString(artist.getArtistName().hashCode(), ".", PictureExporter.FILES_EXTENSION);
	}

	/**
	 * Absolute Path to Artist Image Filename
	 * 
	 * @param artist
	 * @return
	 * @throws IOException
	 */
	private static String getFileNameForArtistImageAtCache(AudioScrobblerSimilarArtists artist) throws IOException {
		File artistImageCacheDir = getArtistImageCacheDir();

		if (artistImageCacheDir == null) {
			return null;
		}

		return StringUtils.getString(artistImageCacheDir.getAbsolutePath(), SystemProperties.fileSeparator, getFileNameForArtistImage(artist));
	}

	/**
	 * Artist Info Filename
	 * 
	 * @param artist
	 * @return
	 */
	private static String getFileNameForArtistInfo(String artist) {
		return StringUtils.getString(artist.hashCode(), ".xml");
	}

	/**
	 * Absolute Path to Artist info Filename
	 * 
	 * @param artist
	 * @return
	 * @throws IOException
	 */
	private static String getFileNameForArtistInfoAtCache(String artist) throws IOException {
		File artistInfoCacheDir = getArtistInfoCacheDir();

		if (artistInfoCacheDir == null) {
			return null;
		}

		return StringUtils.getString(artistInfoCacheDir.getAbsolutePath(), SystemProperties.fileSeparator, getFileNameForArtistInfo(artist));
	}

	/**
	 * Artist Similar Filename
	 * 
	 * @param artist
	 * @return
	 */
	private static String getFileNameForArtistSimilar(String artist) {
		return StringUtils.getString(artist.hashCode(), ".xml");
	}

	/**
	 * Absolute Path to Artist similar Filename
	 * 
	 * @param artist
	 * @return
	 * @throws IOException
	 */
	private static String getFileNameForArtistSimilarAtCache(String artist) throws IOException {
		File artistSimilarCacheDir = getArtistSimilarCacheDir();

		if (artistSimilarCacheDir == null) {
			return null;
		}

		return StringUtils.getString(artistSimilarCacheDir.getAbsolutePath(), SystemProperties.fileSeparator, getFileNameForArtistSimilar(artist));
	}

	/**
	 * Artist Thumb Filename
	 * 
	 * @param artist
	 * @return
	 */
	private static String getFileNameForArtistThumb(AudioScrobblerArtist artist) {
		return StringUtils.getString(artist.getName().hashCode(), ".", PictureExporter.FILES_EXTENSION);
	}

	/**
	 * Absolute Path to Artist Thumb Filename
	 * 
	 * @param artist
	 * @return
	 * @throws IOException
	 */
	private static String getFileNameForArtistThumbAtCache(AudioScrobblerArtist artist) throws IOException {
		File artistThumbCacheDir = getArtistThumbsCacheDir();

		if (artistThumbCacheDir == null) {
			return null;
		}

		return StringUtils.getString(artistThumbCacheDir.getAbsolutePath(), SystemProperties.fileSeparator, getFileNameForArtistThumb(artist));
	}

	/**
	 * Artist Info Filename
	 * 
	 * @param artist
	 * @return
	 */
	private static String getFileNameForArtistWiki(String artist) {
		return StringUtils.getString(artist.hashCode(), ".xml");
	}

	/**
	 * Absolute Path to Artist similar Filename
	 * 
	 * @param artist
	 * @return
	 * @throws IOException
	 */
	private static String getFileNameForArtistWikiAtCache(String artist) throws IOException {
		File artistWikiCacheDir = getArtistWikiCacheDir();

		if (artistWikiCacheDir == null) {
			return null;
		}

		return StringUtils.getString(artistWikiCacheDir.getAbsolutePath(), SystemProperties.fileSeparator, getFileNameForArtistWiki(artist));
	}

	/**
	 * Retrieves an Album Cover from cache
	 * 
	 * @param album
	 * @return
	 */
	public static synchronized Image retrieveAlbumCover(AudioScrobblerAlbum album) {
		try {
			String path = getFileNameForAlbumCoverAtCache(album);
			if (path != null && new File(path).exists())
				return new ImageIcon(path).getImage();
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
		return null;
	}

	/**
	 * Retrieves an Album Cover from cache
	 * 
	 * @param album
	 * @return
	 */
	public static synchronized AudioScrobblerAlbum retrieveAlbumInfo(String artist, String album) {
		try {
			String path = getFileNameForAlbumInfoAtCache(artist, album);
			if (path != null && new File(path).exists())
				return (AudioScrobblerAlbum) XMLUtils.readBeanFromFile(path);
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
		return null;
	}

	/**
	 * Retrieves an Artist Image from cache
	 * 
	 * @param artist
	 * @return
	 */
	public static synchronized Image retrieveArtistImage(AudioScrobblerSimilarArtists artist) {
		try {
			String path = getFileNameForArtistImageAtCache(artist);
			if (path != null && new File(path).exists())
				return new ImageIcon(path).getImage();
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
		return null;
	}

	/**
	 * Retrieves an Artist info from cache
	 * 
	 * @param artist
	 * @return
	 */
	public static synchronized AudioScrobblerAlbumList retrieveArtistInfo(String artist) {
		try {
			String path = getFileNameForArtistInfoAtCache(artist);
			if (path != null && new File(path).exists())
				return (AudioScrobblerAlbumList) XMLUtils.readBeanFromFile(path);
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
		return null;
	}

	/**
	 * Retrieves an Artist similar from cache
	 * 
	 * @param artist
	 * @return
	 */
	public static synchronized AudioScrobblerSimilarArtists retrieveArtistSimilar(String artist) {
		try {
			String path = getFileNameForArtistSimilarAtCache(artist);
			if (path != null && new File(path).exists())
				return (AudioScrobblerSimilarArtists) XMLUtils.readBeanFromFile(path);
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
		return null;
	}

	/**
	 * Retrieves an Artist Thumb from cache
	 * 
	 * @param artist
	 * @return
	 */
	public static synchronized Image retrieveArtistThumbImage(AudioScrobblerArtist artist) {
		try {
			String path = getFileNameForArtistThumbAtCache(artist);
			if (path != null && new File(path).exists())
				return new ImageIcon(path).getImage();
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
		return null;
	}

	/**
	 * Retrieves an Artist wiki from cache
	 * 
	 * @param artist
	 * @return
	 */
	public static synchronized String retrieveArtistWiki(String artist) {
		try {
			String path = getFileNameForArtistWikiAtCache(artist);
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
	public static synchronized void storeAlbumCover(AudioScrobblerAlbum album, Image cover) {
		if (cover == null || album == null)
			return;

		try {
			String fileAbsPath = getFileNameForAlbumCoverAtCache(album);
			if (fileAbsPath != null) {
				PictureExporter.savePicture(cover, fileAbsPath);
				logger.debug(LogCategories.CACHE, StringUtils.getString("Stored album Cover for album ", album.getTitle()));
			}
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
	}

	/**
	 * Stores an Album Cover at cache
	 * 
	 * @param album
	 * @param cover
	 */
	public static synchronized void storeAlbumInfo(String artist, String album, AudioScrobblerAlbum albumObject) {
		if (artist == null || album == null || albumObject == null)
			return;

		try {
			String fileAbsPath = getFileNameForAlbumInfoAtCache(artist, album);
			if (fileAbsPath != null) {
				XMLUtils.writeBeanToFile(albumObject, fileAbsPath);
				logger.debug(LogCategories.CACHE, StringUtils.getString("Stored album info for album ", artist, " ", album));
			}
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
	}

	/**
	 * Store an Artist Image at cache
	 * 
	 * @param artist
	 * @param image
	 */
	public static synchronized void storeArtistImage(AudioScrobblerSimilarArtists artist, Image image) {
		if (image == null || artist == null)
			return;

		try {
			String fileAbsPath = getFileNameForArtistImageAtCache(artist);
			if (fileAbsPath != null) {
				PictureExporter.savePicture(image, fileAbsPath);
				logger.debug(LogCategories.CACHE, StringUtils.getString("Stored artist image for ", artist.getArtistName()));
			}
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
	}

	/**
	 * Store an Artist info at cache
	 * 
	 * @param artist
	 * @param image
	 */
	public static synchronized void storeArtistInfo(String artist, AudioScrobblerAlbumList list) {
		if (artist == null || list == null)
			return;

		try {
			String fileAbsPath = getFileNameForArtistInfoAtCache(artist);
			if (fileAbsPath != null) {
				XMLUtils.writeBeanToFile(list, fileAbsPath);
				logger.debug(LogCategories.CACHE, StringUtils.getString("Stored artist info for ", artist));
			}
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
	}

	/**
	 * Store an Artist similar at cache
	 * 
	 * @param artist
	 * @param image
	 */
	public static synchronized void storeArtistSimilar(String artist, AudioScrobblerSimilarArtists similar) {
		if (artist == null || similar == null)
			return;

		try {
			String fileAbsPath = getFileNameForArtistSimilarAtCache(artist);
			if (fileAbsPath != null) {
				XMLUtils.writeBeanToFile(similar, fileAbsPath);
				logger.debug(LogCategories.CACHE, StringUtils.getString("Stored artist similar for ", artist));
			}
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
	}

	/**
	 * Stores an Artist Thumb at cache
	 * 
	 * @param artist
	 * @param image
	 */
	public static synchronized void storeArtistThumbImage(AudioScrobblerArtist artist, Image image) {
		if (image == null || artist == null)
			return;

		try {
			String fileAbsPath = getFileNameForArtistThumbAtCache(artist);
			if (fileAbsPath != null) {
				PictureExporter.savePicture(image, fileAbsPath);
				logger.debug(LogCategories.CACHE, StringUtils.getString("Stored artist thumb for ", artist.getName()));
			}
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
	}

	/**
	 * Store an Artist wiki at cache
	 * 
	 * @param artist
	 * @param image
	 */
	public static synchronized void storeArtistWiki(String artist, String wikiText) {
		if (artist == null || wikiText == null)
			return;

		try {
			String fileAbsPath = getFileNameForArtistWikiAtCache(artist);
			if (fileAbsPath != null) {
				XMLUtils.writeBeanToFile(wikiText, fileAbsPath);
				logger.debug(LogCategories.CACHE, StringUtils.getString("Stored artist wiki for ", artist));
			}
		} catch (IOException e) {
			logger.error(LogCategories.CACHE, e);
		}
	}
}
