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
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.ClosingUtils;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.log4j.Logger;

abstract class MPlayerOutputReader extends Thread {

	protected static final Logger logger = new Logger();

	protected MPlayerHandler handler;
	protected BufferedReader in;

	protected boolean submitted;
	protected boolean started;
	protected int length;
	protected int time;

	protected MPlayerOutputReader(MPlayerHandler handler, Process process) {
		this.handler = handler;
		this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
	}

	static MPlayerOutputReader newInstance(MPlayerHandler handler, Process process, AudioObject ao) {
		if (ao instanceof AudioFile) {
			return new AudioFileMPlayerOutputReader(handler, process, (AudioFile) ao);
		} else if (ao instanceof Radio) {
			return new RadioMPlayerOutputReader(handler, process, (Radio) ao);
		} else if (ao instanceof PodcastFeedEntry) {
			return new PodcastFeedEntryMPlayerOutputReader(handler, process, (PodcastFeedEntry) ao);
		} else
			throw new IllegalArgumentException("audio object is not from type AudioFile, Radio or PodcastFeedEntry");
	}

	protected void init() {
	}

	protected void read(String line) {
		// Read progress			
		// MPlayer bug: Duration still inaccurate with mp3 VBR files! Flac duration bug
		if (line.matches(".*ANS_TIME_POSITION.*")) {
			time = (int) (Float.parseFloat(line.substring(line.indexOf("=") + 1)) * 1000.0);
			handler.setTime(time);
		}

		// End
		if (line.matches(".*\\x2e\\x2e\\x2e.*\\(.*\\x20.*\\).*")) {
			handler.next(true);
		}
	}

	@Override
	public final void run() {
		String line = null;
		try {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					init();
				}
			});
			while ((line = in.readLine()) != null && !isInterrupted()) {
				final String lineHelp = line;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						read(lineHelp);
					}
				});
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
