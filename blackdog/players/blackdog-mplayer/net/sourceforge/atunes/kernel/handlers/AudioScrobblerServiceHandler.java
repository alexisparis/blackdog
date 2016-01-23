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

package net.sourceforge.atunes.kernel.handlers;

import java.awt.Cursor;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerAlbum;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerAlbumCoverRunnable;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerArtist;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerCache;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerListener;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerRunnable;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerService;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerTrack;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.submitter.Submitter;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.submitter.SubmitterException;
import net.sourceforge.atunes.kernel.modules.lyrics.LyricsCache;
import net.sourceforge.atunes.kernel.modules.lyrics.LyricsRunnable;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.kernel.utils.NetworkUtils;
import net.sourceforge.atunes.kernel.utils.PictureExporter;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class AudioScrobblerServiceHandler implements AudioScrobblerListener {

	private static AudioScrobblerServiceHandler instance = new AudioScrobblerServiceHandler();

	private Logger logger = new Logger();

	private AudioScrobblerService service;

	private AudioScrobblerAlbum album;
	private Image image;
	private List<AudioScrobblerAlbum> albums;

	private AudioScrobblerRunnable currentWorker;
	private volatile long currentWorkerId;

	private volatile String lastAlbumRetrieved;
	private volatile String lastArtistRetrieved;

	private AudioScrobblerServiceHandler() {
		updateService(Kernel.getInstance().state.getProxy(), Kernel.getInstance().state.getLastFmUser(), Kernel.getInstance().state.getLastFmPassword());
		currentWorkerId = System.currentTimeMillis();
	}

	public static AudioScrobblerServiceHandler getInstance() {
		return instance;
	}

	private void cancelIfWorking() {
		if (currentWorker != null) {
			currentWorkerId = System.currentTimeMillis();
			currentWorker.interrupt();
			currentWorker = null;
			logger.debug(LogCategories.HANDLER, "Suspended Thread");
		}
	}

	public void clear() {
		cancelIfWorking();
		ControllerProxy.getInstance().getAudioScrobblerController().clear(true);
		clearLastRetrieved();
	}

	/**
	 * <p>
	 * Clears the caches (AudioScrobbler & lyrics cache)
	 * </p>
	 * <p>
	 * Deletes as many files in the cache directories as possible. Files that
	 * are currently used by aTunes won't be deleted.
	 * </p>
	 */
	public void clearCaches() {
		SwingWorker<Boolean, Void> clearCaches = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				boolean exception;
				exception = AudioScrobblerCache.clearCache();
				exception = LyricsCache.clearCache() || exception;
				logger.debug(LogCategories.HANDLER);
				logger.debug(LogCategories.HANDLER, exception);
				return exception;
			}

			@Override
			protected void done() {
				VisualHandler.getInstance().getEditPreferencesDialog().setCursor(Cursor.getDefaultCursor());
				VisualHandler.getInstance().getEditPreferencesDialog().getAudioScrobblerPanel().getClearCache().setEnabled(true);
			}
		};
		VisualHandler.getInstance().getEditPreferencesDialog().getAudioScrobblerPanel().getClearCache().setEnabled(false);
		VisualHandler.getInstance().getEditPreferencesDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		clearCaches.execute();
	}

	private void clearLastRetrieved() {
		lastAlbumRetrieved = null;
		lastArtistRetrieved = null;
	}

	public AudioScrobblerAlbum getAlbum() {
		return album;
	}

	public void getAlbumCover(AudioFile file) {
		AudioScrobblerAlbumCoverRunnable r = new AudioScrobblerAlbumCoverRunnable(this, service, file);
		Thread t = new Thread(r);
		t.start();
	}

	@Override
	public List<AudioScrobblerAlbum> getAlbums() {
		return albums;
	}

	public Map<AudioFile, String> getGenresForFiles(List<AudioFile> files) {
		Map<AudioFile, String> result = new HashMap<AudioFile, String>();

		Map<String, String> tagCache = new HashMap<String, String>();

		for (AudioFile f : files) {
			if (!f.getArtist().equals(LanguageTool.getString("UNKNOWN_ARTIST"))) {
				String tag = null;
				if (tagCache.containsKey(f.getArtist()))
					tag = tagCache.get(f.getArtist());
				else {
					tag = service.getArtistTopTag(f.getArtist());
					tagCache.put(f.getArtist(), tag);
					// Wait one second to avoid IP banning
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
				if (tag != null)
					result.put(f, tag);
			}
		}

		return result;
	}

	public String getLastAlbumRetrieved() {
		return lastAlbumRetrieved;
	}

	public String getLastArtistRetrieved() {
		return lastArtistRetrieved;
	}

	/**
	 * Returns a hash of files with its songs titles
	 * 
	 * @param files
	 * @return
	 */
	public Map<AudioFile, String> getTitlesForFiles(List<AudioFile> files) {
		Map<AudioFile, String> result = new HashMap<AudioFile, String>();

		AudioScrobblerAlbum lastAlbumRetrieved1 = null;

		// For each file
		for (AudioFile f : files) {
			// If has valid artist name, album name, and track number...
			if (!f.getArtist().equals(LanguageTool.getString("UNKNOWN_ARTIST")) && !f.getAlbum().equals(LanguageTool.getString("UNKNOWN_ALBUM")) && f.getTrackNumber() > 0) {

				// Find album
				AudioScrobblerAlbum albumRetrieved = null;
				if (lastAlbumRetrieved1 != null && lastAlbumRetrieved1.getArtist().equals(f.getArtist()) && lastAlbumRetrieved1.getTitle().equals(f.getAlbum())) {
					albumRetrieved = lastAlbumRetrieved1;
				}

				if (albumRetrieved == null)
					albumRetrieved = service.getAlbum(f.getArtist(), f.getAlbum());
				if (albumRetrieved != null) {
					lastAlbumRetrieved1 = albumRetrieved;
					if (albumRetrieved.getTracks().size() >= f.getTrackNumber()) {
						// Get track
						AudioScrobblerTrack track = albumRetrieved.getTracks().get(f.getTrackNumber() - 1);

						// Save track title with file
						result.put(f, track.getTitle());
					}
				}
			}
		}

		// Return files matched
		return result;
	}

	public List<String> getTrackNamesForAlbum(String artistName, String albumName) {
		AudioScrobblerAlbum alb = service.getAlbum(artistName, albumName);
		if (alb == null)
			return null;
		logger.info(LogCategories.HANDLER, StringUtils.getString("Received track names for artist ", artistName, " album ", albumName));
		List<String> tracks = new ArrayList<String>();
		for (int i = 0; i < alb.getTracks().size(); i++) {
			tracks.add(alb.getTracks().get(i).getTitle());
		}
		return tracks;
	}

	/**
	 * Used to update cover on Full Screen
	 * 
	 * @param file
	 * @param image1
	 */
	public void notifyAlbumCoverRetrieved(AudioFile file, Image image1) {
		VisualHandler.getInstance().getFullScreenFrame().setPicture(file, image1);
	}

	@Override
	public void notifyAlbumRetrieved(AudioObject file, long id) {
		if (currentWorkerId != id)
			return;

		logger.debug(LogCategories.HANDLER);

		ControllerProxy.getInstance().getAudioScrobblerController().notifyFinishGetAlbumInfo(file.getArtist(), album, image);

		lastAlbumRetrieved = file.getAlbum();
		lastArtistRetrieved = file.getArtist();
	}

	@Override
	public void notifyArtistImage(Image img, long id) {
		if (currentWorkerId != id)
			return;

		logger.debug(LogCategories.HANDLER);

		ControllerProxy.getInstance().getAudioScrobblerController().notifyArtistImage(img);
	}

	@Override
	public void notifyCoverRetrieved(AudioScrobblerAlbum alb, Image cover, long id) {
		if (currentWorkerId != id)
			return;

		logger.debug(LogCategories.HANDLER);

		ControllerProxy.getInstance().getAudioScrobblerController().notifyFinishGetAlbumsInfo(alb, cover);

		lastArtistRetrieved = alb.getArtist();
	}

	@Override
	public void notifyFinishGetSimilarArtist(AudioScrobblerArtist a, Image img, long id) {
		if (currentWorkerId != id)
			return;

		logger.debug(LogCategories.HANDLER);

		ControllerProxy.getInstance().getAudioScrobblerController().notifyFinishGetSimilarArtist(a, img);
	}

	public void notifyLyricsRetrieved(AudioObject audioObject, String lyrics, long id) {
		if (currentWorkerId != id)
			return;

		ControllerProxy.getInstance().getAudioScrobblerController().setLyrics(audioObject, lyrics);
	}

	public void notifyPodcast(PodcastFeedEntry podcast, long id) {
		if (currentWorkerId != id)
			return;

		clearLastRetrieved();
		ControllerProxy.getInstance().getAudioScrobblerController().notifyPodcast(podcast);
	}

	public void notifyRadio(Radio radio, long id) {
		if (currentWorkerId != id)
			return;

		clearLastRetrieved();
		ControllerProxy.getInstance().getAudioScrobblerController().notifyRadio(radio);
	}

	@Override
	public void notifyStartRetrievingArtistImages(long id) {
		if (currentWorkerId != id)
			return;

		ControllerProxy.getInstance().getAudioScrobblerController().clearSimilarArtistsContainer();
	}

	@Override
	public void notifyStartRetrievingCovers(long id) {
		if (currentWorkerId != id)
			return;

		ControllerProxy.getInstance().getAudioScrobblerController().clearAlbumsContainer();
	}

	@Override
	public void notifyWikiInfoRetrieved(String wikiText, String wikiURL, long id) {
		if (currentWorkerId != id)
			return;

		ControllerProxy.getInstance().getAudioScrobblerController().setWikiInformation(wikiText, wikiURL);
	}

	public void retrieveInfo(AudioObject audioObject) {
		logger.debug(LogCategories.HANDLER);

		if (audioObject == null)
			return;

		boolean getArtist = true;
		// If artist and album are equals than previous retrieved, there is nothing to do
		if (audioObject.getArtist().equals(lastArtistRetrieved) && audioObject.getAlbum().equals(lastAlbumRetrieved)) {
			ControllerProxy.getInstance().getAudioScrobblerController().notifyFinishGetAlbumInfo(audioObject.getArtist(), album, image);
			if (audioObject instanceof AudioFile) {
				// Find lyrics
				LyricsRunnable lyricsRunnable = new LyricsRunnable(audioObject, currentWorkerId);
				Thread lyricsThread = new Thread(lyricsRunnable);
				lyricsThread.start();
			}
			return;
		}
		// If artist is the same, but album not, don't get artist
		else if (audioObject.getArtist().equals(lastArtistRetrieved))
			getArtist = false;

		cancelIfWorking();
		ControllerProxy.getInstance().getAudioScrobblerController().clear(getArtist);

		currentWorkerId = System.currentTimeMillis();

		if (audioObject instanceof Radio && !((Radio) audioObject).isSongInfoAvailable()) {
			notifyRadio((Radio) audioObject, currentWorkerId);
			return;
		} else if (audioObject instanceof PodcastFeedEntry) {
			notifyPodcast((PodcastFeedEntry) audioObject, currentWorkerId);
			return;
		}

		// Find lyrics
		LyricsRunnable lyricsRunnable = new LyricsRunnable(audioObject, currentWorkerId);
		Thread lyricsThread = new Thread(lyricsRunnable);
		lyricsThread.start();

		currentWorker = new AudioScrobblerRunnable(this, service, audioObject, currentWorkerId);
		currentWorker.setRetrieveArtistInfo(getArtist);

		Thread t = new Thread(currentWorker);
		t.start();
	}

	@Override
	public void savePicture(Image img, AudioFile file, long id) {
		if (currentWorkerId != id)
			return;

		logger.debug(LogCategories.HANDLER);

		if (img != null && Kernel.getInstance().state.isSavePictureFromAudioScrobbler()) { // save image in folder of file
			String imageFileName = AudioFilePictureUtils.getFileNameForCover(file);

			File imageFile = new File(imageFileName);
			if (!imageFile.exists()) {
				// Save picture
				try {
					PictureExporter.savePicture(img, imageFileName);
					// Add picture to songs of album
					RepositoryHandler.getInstance().addExternalPictureForAlbum(file.getArtist(), file.getAlbum(), imageFile);

					// Update file properties panel
					ControllerProxy.getInstance().getFilePropertiesController().refreshPicture();
				} catch (IOException e) {
					logger.internalError(e);
				}
			}
		}

	}

	@Override
	public void setAlbum(AudioScrobblerAlbum album, long id) {
		if (currentWorkerId != id)
			return;

		this.album = album;
	}

	@Override
	public void setAlbums(List<AudioScrobblerAlbum> albums, long id) {
		if (currentWorkerId != id)
			return;

		this.albums = albums;
	}

	@Override
	public void setImage(Image image, long id) {
		if (currentWorkerId != id)
			return;

		this.image = image;
	}

	@Override
	public void setLastAlbumRetrieved(String lastAlbumRetrieved, long id) {
		if (currentWorkerId != id)
			return;

		this.lastAlbumRetrieved = lastAlbumRetrieved;
	}

	@Override
	public void setLastArtistRetrieved(String lastArtistRetrieved, long id) {
		if (currentWorkerId != id)
			return;

		this.lastArtistRetrieved = lastArtistRetrieved;
	}

	public void submitSong(final AudioFile file, final long secondsPlayed) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					Submitter.submitTrack(file, secondsPlayed);
				} catch (SubmitterException e) {
					if (e.getMessage().contains("BADAUTH")) {
						logger.error(LogCategories.SERVICE, "Authentication failure on Last.fm service");
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("LASTFM_USER_ERROR"));
								// Disable service by deleting password
								Kernel.getInstance().state.setLastFmEnabled(false);
							}
						});
					} else
						logger.error(LogCategories.SERVICE, e.getMessage());
				}
			}
		};
		new Thread(r).start();
	}

	public void updateService(ProxyBean proxy, String user, String password) {
		logger.debug(LogCategories.HANDLER);

		cancelIfWorking();
		Proxy p = null;
		try {
			if (proxy != null) {
				p = NetworkUtils.getProxy(proxy);
			}
			service = new AudioScrobblerService(p);
			Submitter.setProxy(p);
			Submitter.setUser(user);
			Submitter.setPassword(password);
		} catch (Exception e) {
			logger.error(LogCategories.HANDLER, e);
			service = null;
		}
		clearLastRetrieved();
	}
}
