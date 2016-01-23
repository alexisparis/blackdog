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

package net.sourceforge.atunes.kernel.handlers;

import java.util.Comparator;
import java.util.List;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntryRetriever;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class PodcastFeedHandler {

	private static PodcastFeedHandler instance = new PodcastFeedHandler();

	private Logger logger = new Logger();

	private List<PodcastFeed> podcastFeeds;

	private PodcastFeedHandler() {
	}

	public static PodcastFeedHandler getInstance() {
		return instance;
	}

	public void addPodcastFeed() {
		PodcastFeed podcastFeed = VisualHandler.getInstance().showAddPodcastFeedDialog();
		if (podcastFeed != null) {
			addPodcastFeed(podcastFeed);
			ControllerProxy.getInstance().getNavigationController().refreshPodcastFeedTreeContent();
			PodcastFeedEntryRetriever.getInstance().retrievePodcastFeedEntriesAsynchronously();
		}
	}

	private void addPodcastFeed(PodcastFeed podcastFeed) {
		logger.info(LogCategories.HANDLER, "Adding podcast feed");
		// Note: Do not use Collection.sort(...);
		boolean added = false;
		Comparator<PodcastFeed> comparator = PodcastFeed.getComparator();
		for (int i = 0; i < podcastFeeds.size(); i++) {
			if (comparator.compare(podcastFeed, podcastFeeds.get(i)) < 0) {
				podcastFeeds.add(i, podcastFeed);
				added = true;
				break;
			}
		}
		if (!added) {
			podcastFeeds.add(podcastFeed);
		}
	}

	public void finish() {
		if (PodcastFeedEntryRetriever.getInstance() != null) {
			PodcastFeedEntryRetriever.getInstance().interrupt();
		}
		ApplicationDataHandler.getInstance().persistPodcastFeedCache(podcastFeeds);
	}

	/**
	 * @return the podcast feeds
	 */
	public List<PodcastFeed> getPodcastFeeds() {
		return podcastFeeds;
	}

	public void readPodcastFeeds() {
		podcastFeeds = ApplicationDataHandler.getInstance().retrievePodcastFeedCache();
	}

	public void removePodcastFeed(PodcastFeed podcastFeed) {
		logger.info(LogCategories.HANDLER, "Removing podcast feed");
		podcastFeeds.remove(podcastFeed);
		ControllerProxy.getInstance().getNavigationController().refreshPodcastFeedTreeContent();
	}

}
