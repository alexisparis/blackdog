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

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.utils.LanguageTool;

public class AudioScrobblerSimilarArtistsRunnable implements Runnable {

	private AudioScrobblerListener listener;
	private AudioScrobblerService service;
	private String artist;

	private volatile boolean interrupted;

	private long id;

	public AudioScrobblerSimilarArtistsRunnable(AudioScrobblerListener listener, AudioScrobblerService service, String artist, long id) {
		this.listener = listener;
		this.service = service;
		this.artist = artist;
		this.id = id;
	}

	protected void interrupt() {
		interrupted = true;
	}

	@Override
	public void run() {
		if (!interrupted && artist != null && !artist.equals(LanguageTool.getString("UNKNOWN_ARTIST"))) {
			AudioScrobblerSimilarArtists artists = service.getSimilarArtists(artist);

			if (!interrupted && artists != null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						listener.notifyStartRetrievingArtistImages(id);
					}
				});
				final Image artistImage = service.getImage(artists);
				if (!interrupted && artistImage != null) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							listener.notifyArtistImage(artistImage, id);
						}
					});
				}

				for (int i = 0; i < artists.getArtists().size(); i++) {
					final Image img;
					final AudioScrobblerArtist a = artists.getArtists().get(i);
					if (!interrupted) {
						img = service.getImage(a);
					} else {
						img = null;
					}

					if (!interrupted) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								listener.notifyFinishGetSimilarArtist(a, img, id);
							}
						});
					}
				}
			}
		}
	}

}
