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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

/**
 * Represents a rss or atom podcast feed
 */
public class PodcastFeed implements TreeObject, Serializable {

	private static final long serialVersionUID = 1416452911272034086L;

	private static Logger logger = new Logger();

	private static Comparator<PodcastFeed> comparator = new Comparator<PodcastFeed>() {
		@Override
		public int compare(PodcastFeed o1, PodcastFeed o2) {
			return o1.name.compareToIgnoreCase(o2.name);
		}
	};
	private String name;
	private String url;
	private FeedType feedType;
	private List<PodcastFeedEntry> podcastFeedEntries;

	private boolean hasNewEntries;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            the name of the podcast feed
	 * @param url
	 *            the url of the podcast feed
	 */
	public PodcastFeed(String name, String url) {
		this.name = name;
		this.url = url;
		podcastFeedEntries = new ArrayList<PodcastFeedEntry>();
	}

	/**
	 * @return the comparator
	 */
	public static Comparator<PodcastFeed> getComparator() {
		return comparator;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof PodcastFeed)) {
			return false;
		} else {
			return (name + url).equals(((PodcastFeed) o).getName() + ((PodcastFeed) o).getUrl());
		}

	}

	@Override
	public List<AudioObject> getAudioObjects() {
		return new ArrayList<AudioObject>(podcastFeedEntries);
	}

	/**
	 * @return the feedType of the podcast feed
	 */
	public FeedType getFeedType() {
		return feedType;
	}

	/**
	 * @return the name of the podcast feed
	 */
	public String getName() {
		return name;
	}

	public List<PodcastFeedEntry> getPodcastFeedEntries() {
		return new ArrayList<PodcastFeedEntry>(podcastFeedEntries);
	}

	/**
	 * @return the url of the podcast feed
	 */
	public String getUrl() {
		return url;
	}

	@Override
	public int hashCode() {
		return (name + url).hashCode();
	}

	/**
	 * @return if the podcast feed has new entries
	 */
	public boolean hasNewEntries() {
		return hasNewEntries;
	}

	/**
	 * Marks the entries of this podcast feed as listened
	 */
	public void markEntriesAsListened() {
		for (PodcastFeedEntry entry : podcastFeedEntries) {
			entry.setListened(true);
		}
	}

	/**
	 * Marks the entries of this podcastfeed as not new
	 */
	public void markEntriesAsNotNew() {
		hasNewEntries = false;
	}

	/**
	 * Sets the entries of this podcast feed and removes old entries
	 * 
	 * @param entries
	 *            The entries of this podcast feed
	 */
	public void setAsEntriesAndRemoveOldEntries(List<? extends PodcastFeedEntry> entries) {
		logger.debug(LogCategories.PODCAST);

		List<PodcastFeedEntry> oldEntries = new ArrayList<PodcastFeedEntry>(podcastFeedEntries);
		oldEntries.removeAll(entries);
		entries.removeAll(podcastFeedEntries);
		if (!entries.isEmpty()) {
			hasNewEntries = true;
		}
		podcastFeedEntries.addAll(0, entries);
		podcastFeedEntries.removeAll(oldEntries);
	}

	/**
	 * @param feedType
	 *            the feedType of the podcast feed to set
	 */
	public void setFeedType(FeedType feedType) {
		this.feedType = feedType;
	}

	/**
	 * @param name
	 *            the name of the podcast feed to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param url
	 *            the url of the podcast feed to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return name;
	}

}
