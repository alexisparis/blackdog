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

import net.sourceforge.atunes.kernel.handlers.AudioScrobblerServiceHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;

public class AudioScrobblerAlbumCoverRunnable implements Runnable {

	private AudioScrobblerServiceHandler handler;
	private AudioScrobblerService service;
	private AudioFile file;

	private Image image;

	public AudioScrobblerAlbumCoverRunnable(AudioScrobblerServiceHandler handler, AudioScrobblerService service, AudioFile file) {
		this.handler = handler;
		this.service = service;
		this.file = file;
	}

	@Override
	public void run() {
		AudioScrobblerAlbum album = null;
		album = service.getAlbum(file.getArtist(), file.getAlbum());
		if (album != null) {
			image = service.getImage(album);
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				handler.notifyAlbumCoverRetrieved(file, image);
			}
		});
	}
}
