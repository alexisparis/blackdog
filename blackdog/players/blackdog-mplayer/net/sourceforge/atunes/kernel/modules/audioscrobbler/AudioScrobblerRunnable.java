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

import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class AudioScrobblerRunnable implements Runnable {

	private static Logger logger = new Logger();

	private volatile boolean interrupted;
	private AudioScrobblerAlbumsRunnable albumsRunnable;
	private AudioScrobblerCoversRunnable coversRunnable;
	private AudioScrobblerSimilarArtistsRunnable artistsRunnable;

	private AudioScrobblerListener listener;
	private AudioScrobblerService service;
	private AudioObject audioObject;

	private boolean retrieveArtistInfo = true;

	private long id;

	public AudioScrobblerRunnable(AudioScrobblerListener listener, AudioScrobblerService service, AudioObject audioObject, long id) {
		this.listener = listener;
		this.service = service;
		this.audioObject = audioObject;
		this.id = id;
	}

	public void interrupt() {
		interrupted = true;
		if (albumsRunnable != null)
			albumsRunnable.interrupt();
		if (coversRunnable != null)
			coversRunnable.interrupt();
		if (artistsRunnable != null)
			artistsRunnable.interrupt();
	}

	@Override
	public void run() {
		albumsRunnable = new AudioScrobblerAlbumsRunnable(listener, service, audioObject, id);
		albumsRunnable.setRetrieveArtistInfo(retrieveArtistInfo);
		Thread albumsInfoThread = new Thread(albumsRunnable);
		albumsInfoThread.start();
		logger.debug(LogCategories.SERVICE, "AudioScrobblerAlbumsRunnable started with id " + id + " for  " + audioObject.getArtist());
		try {
			albumsInfoThread.join();
		} catch (InterruptedException e) {
			logger.internalError(e);
		}

		if (retrieveArtistInfo) {
			coversRunnable = new AudioScrobblerCoversRunnable(listener, service, listener.getAlbums(), id);
			Thread coversThread = new Thread(coversRunnable);
			coversThread.start();
			logger.debug(LogCategories.SERVICE, "AudioScrobblerCoversRunnable started with id " + id);

			artistsRunnable = new AudioScrobblerSimilarArtistsRunnable(listener, service, audioObject.getArtist(), id);
			Thread artistsThread = new Thread(artistsRunnable);
			artistsThread.start();
			logger.debug(LogCategories.SERVICE, "AudioScrobblerSimilarArtistsRunnable started with id " + id + " for " + audioObject.getArtist());

			try {
				if (!interrupted)
					coversThread.join();
				if (!interrupted)
					artistsThread.join();
			} catch (InterruptedException e) {
				logger.internalError(e);
			}
		}
	}

	public void setRetrieveArtistInfo(boolean retrieveArtistInfo) {
		this.retrieveArtistInfo = retrieveArtistInfo;
	}
}
