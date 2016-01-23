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

import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;

class PodcastFeedEntryMPlayerOutputReader extends MPlayerOutputReader {

	private PodcastFeedEntry podcastFeedEntry;

	PodcastFeedEntryMPlayerOutputReader(MPlayerHandler handler, Process process, PodcastFeedEntry podcastFeedEntry) {
		super(handler, process);
		this.podcastFeedEntry = podcastFeedEntry;
	}

	@Override
	protected void init() {
		super.init();

		handler.setCurrentDuration(podcastFeedEntry.getDuration() * 1000);
		handler.setDuration(podcastFeedEntry.getDuration() * 1000);
	}

	@Override
	protected void read(String line) {
		super.read(line);

		// When starting playback, update status bar
		if (line.startsWith("Starting playback")) {
			VisualHandler.getInstance().updateStatusBar(podcastFeedEntry);
			if (!started) {
				handler.notifyRadioOrPodcastFeedEntryStarted();
				started = true;
			}
		}
	}

}
