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

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.handlers.AudioScrobblerServiceHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

public class LyricsRunnable implements Runnable {

	private AudioObject audioObject;
	private long id;

	public LyricsRunnable(AudioObject audioObject, long id) {
		this.audioObject = audioObject;
		this.id = id;
	}

	@Override
	public void run() {
		String lyrics = null;
		if (!audioObject.getTitle().trim().equals("") && !audioObject.getArtist().equals(LanguageTool.getString("UNKNOWN_ARTIST")) || audioObject.getLyrics() == null)
			lyrics = LyricsService.getLyrics(audioObject.getArtist(), audioObject.getTitle());
		if (audioObject.getLyrics() != null)
			lyrics = audioObject.getLyrics();
		if (lyrics != null)
			lyrics = lyrics.replaceAll("'", "\'");
		final String lyricsHelp = lyrics;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				AudioScrobblerServiceHandler.getInstance().notifyLyricsRetrieved(audioObject, lyricsHelp, id);
			}
		});
	}

}
