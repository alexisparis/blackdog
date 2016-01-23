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

package net.sourceforge.atunes.kernel.modules.mplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.utils.ClosingUtils;
import net.sourceforge.atunes.model.AudioObject;

class MPlayerErrorReader extends Thread {

	private MPlayerHandler handler;
	private BufferedReader in;

	private AudioObject audioObject;

	MPlayerErrorReader(MPlayerHandler handler, Process process, AudioObject audioObject) {
		this.handler = handler;
		in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		this.audioObject = audioObject;
	}

	@Override
	public void run() {
		String line = null;
		try {
			while ((line = in.readLine()) != null) {
				if (audioObject instanceof Radio || audioObject instanceof PodcastFeedEntry) {
					// When starting playback, update status bar
					if (line.startsWith("File not found")) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								handler.next(true);
							}
						});
					}
				}
			}
		} catch (final IOException e) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					handler.notifyPlayerError(e);
				}
			});
		} finally {
			ClosingUtils.close(in);
		}
	}

}
