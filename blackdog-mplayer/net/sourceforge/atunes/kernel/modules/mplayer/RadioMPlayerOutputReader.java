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

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.handlers.AudioScrobblerServiceHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListCommonOps;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.utils.log4j.LogCategories;

class RadioMPlayerOutputReader extends MPlayerOutputReader {

	private Radio radio;

	RadioMPlayerOutputReader(MPlayerHandler handler, Process process, Radio radio) {
		super(handler, process);
		this.radio = radio;
	}

	@Override
	protected void init() {
		super.init();

		handler.setCurrentDuration(radio.getDuration() * 1000);
		handler.setDuration(radio.getDuration() * 1000);
	}

	@Override
	protected void read(String line) {
		super.read(line);

		// When starting playback, update status bar
		if (line.startsWith("Starting playback")) {
			VisualHandler.getInstance().updateStatusBar(radio);
			if (!started) {
				handler.notifyRadioOrPodcastFeedEntryStarted();
				started = true;
			}
		}

		// Read bitrate and frequency of radios
		if (line.startsWith("AUDIO:")) {
			final String[] s = line.split(" ");
			if (s.length >= 2) {
				try {
					radio.setFrequency(Integer.parseInt(s[1]));
				} catch (NumberFormatException e) {
					logger.info(LogCategories.PLAYER, "Could not read radio frequency");
				}
			}
			if (s.length >= 7) {
				try {
					radio.setBitrate((long) Double.parseDouble(s[6]));
				} catch (NumberFormatException e) {
					logger.info(LogCategories.PLAYER, "Could not read radio bitrate");
				}
			}
			PlayListCommonOps.refreshPlayList();
		}

		// Read song info from radio stream
		if (Kernel.getInstance().state.isReadInfoFromRadioStream() && line.startsWith("ICY Info:")) {
			try {
				int i = line.indexOf("StreamTitle=");
				int j = line.indexOf(";", i);
				String info = line.substring(i + 13, j - 1);
				int k = info.indexOf("-");
				radio.setArtist(info.substring(0, k).trim());
				radio.setTitle(info.substring(k + 1, info.length()).trim());
				radio.setSongInfoAvailable(true);
				PlayListCommonOps.refreshPlayList();
			} catch (IndexOutOfBoundsException e) {
				logger.info(LogCategories.PLAYER, "Could not read song info from radio");
			}
			if (Kernel.getInstance().state.isUseAudioScrobbler() && radio.equals(PlayListCommonOps.getCurrentAudioObject())) {
				AudioScrobblerServiceHandler.getInstance().retrieveInfo(radio);
			}
		}

		// End (Quit)
		if (line.matches(".*\\x2e\\x2e\\x2e.*\\(.*\\).*")) {
			radio.deleteSongInfo();
			PlayListCommonOps.refreshPlayList();
		}
	}

}
