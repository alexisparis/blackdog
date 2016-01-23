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

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.handlers.AudioScrobblerServiceHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.utils.log4j.LogCategories;

class AudioFileMPlayerOutputReader extends MPlayerOutputReader {

	private AudioFile audioFile;

	AudioFileMPlayerOutputReader(MPlayerHandler handler, Process process, AudioFile audioFile) {
		super(handler, process);
		this.audioFile = audioFile;
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void read(String line) {
		super.read(line);

		// Read length
		if (line.matches(".*ANS_LENGTH.*")) {
			// Length still inaccurate with mp3 VBR files!
			length = (int) (Float.parseFloat(line.substring(line.indexOf("=") + 1)) * 1000.0);
			handler.setCurrentDuration(length);
			handler.setDuration(audioFile.getDuration() * 1000);
		}
		// Submit to Last.fm and update stats
		if (!submitted && (time > handler.getCurrentDuration() / 2 || time >= 240000)) {
			AudioScrobblerServiceHandler.getInstance().submitSong(audioFile, time / 1000);
			RepositoryHandler.getInstance().setSongStatistics(audioFile);
			if (VisualHandler.getInstance().getStatsDialog().isVisible())
				ControllerProxy.getInstance().getStatsDialogController().updateStats();
			submitted = true;
		}

		// MPlayer bug: Workaround (for audio files) for "mute bug" [1868482] 
		if (handler.isMute() && length > 0 && length - time < 1000) {
			logger.debug(LogCategories.PLAYER, "MPlayer 'mute bug' workaround applied");
			handler.next(true);
			interrupt();
		}
	}
}
