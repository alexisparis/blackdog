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
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.log4j.Logger;

public class AudioScrobblerAlbumsRunnable implements Runnable {

	private static Logger logger = new Logger();

	private AudioScrobblerListener listener;
	private AudioScrobblerService service;
	private AudioObject audioObject;

	private volatile boolean interrupted;

	private boolean retrieveArtistInfo = true;

	private long id;

	protected AudioScrobblerAlbumsRunnable(AudioScrobblerListener listener, AudioScrobblerService service, AudioObject audioObject, long id) {
		this.listener = listener;
		this.service = service;
		this.audioObject = audioObject;
		this.id = id;
	}

	private boolean forbiddenToken(String t) {
		return t.contains("/");
	}

	protected void interrupt() {
		interrupted = true;
	}

	@Override
	public void run() {
		if (!interrupted) {
			listener.setLastAlbumRetrieved(null, id);

			if (retrieveArtistInfo) {
				listener.setLastArtistRetrieved(null, id);
			}
		}

		// Get wiki start for artist
		final String wikiText = service.getWikiText(audioObject.getArtist());
		final String wikiURL = service.getWikiURL(audioObject.getArtist());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				listener.notifyWikiInfoRetrieved(wikiText, wikiURL, id);
			}
		});

		AudioScrobblerAlbum album = null;
		List<AudioScrobblerAlbum> albums = null;
		if (!interrupted) {
			album = service.getAlbum(audioObject.getArtist(), audioObject.getAlbum());
			final AudioScrobblerAlbum albumHelp = album;
			if (album != null) {
				listener.setAlbum(albumHelp, id);
				Image image = service.getImage(album);
				listener.setImage(image, id);
				if (audioObject instanceof AudioFile && image != null)
					listener.savePicture(image, (AudioFile) audioObject, id);
			}
		}
		if (album != null && !interrupted) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					listener.notifyAlbumRetrieved(audioObject, id);
				}
			});
		}

		try {
			Thread.sleep(1000); // Wait a second to prevent IP banning
		} catch (InterruptedException e) {
			logger.internalError(e);
		}

		// If we have to retrieve artist info do it. If not, get previous retrieved albums list
		if (retrieveArtistInfo) {
			if (!interrupted) {
				if (!audioObject.getArtist().equals(LanguageTool.getString("UNKNOWN_ARTIST"))) {
					AudioScrobblerAlbumList albumList = service.getAlbumList(audioObject.getArtist());
					if (albumList != null) {
						albums = albumList.getAlbums();
					}
				}
				if (albums == null)
					interrupted = true;
				listener.setAlbums(albums, id);
			}
		} else
			albums = listener.getAlbums();

		if (album == null && albums != null && !interrupted) {
			// Try to find an album which fits 
			AudioScrobblerAlbum auxAlbum = null;
			int i = 0;
			while (!interrupted && auxAlbum == null && i < albums.size()) {
				AudioScrobblerAlbum a = albums.get(i);
				StringTokenizer st = new StringTokenizer(a.getTitle(), " ");
				boolean matches = true;
				int tokensAnalyzed = 0;
				while (st.hasMoreTokens() && matches) {
					String t = st.nextToken();
					if (forbiddenToken(t)) { // Ignore album if contains forbidden chars
						matches = false;
						break;
					}
					if (!validToken(t)) { // Ignore tokens without alphanumerics
						if (tokensAnalyzed == 0 && !st.hasMoreTokens()) // Only this token
							matches = false;
						else
							continue;
					}
					if (!audioObject.getAlbum().toLowerCase().contains(t.toLowerCase()))
						matches = false;
					tokensAnalyzed++;
				}
				if (matches)
					auxAlbum = a;
				i++;
			}
			if (!interrupted && auxAlbum != null) {
				auxAlbum = service.getAlbum(auxAlbum.getArtist(), auxAlbum.getTitle());
				if (auxAlbum != null) {
					listener.setAlbum(auxAlbum, id);
					Image image = service.getImage(auxAlbum);
					listener.setImage(image, id);
					if (audioObject instanceof AudioFile && image != null)
						listener.savePicture(image, (AudioFile) audioObject, id);
				}
			}
			if (!interrupted && auxAlbum != null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						listener.notifyAlbumRetrieved(audioObject, id);
					}
				});
			}
		}

	}

	public void setRetrieveArtistInfo(boolean retrieveArtistInfo) {
		this.retrieveArtistInfo = retrieveArtistInfo;
	}

	private boolean validToken(String t) {
		return t.matches("[A-Za-z]+");
		//t.contains("(") || t.contains(")")
	}

}
