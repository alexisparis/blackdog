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
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DateUtils;

/**
 * Represents a entry of a podcast feed
 */
public class PodcastFeedEntry implements AudioObject, Serializable {

	private static final long serialVersionUID = 4185336290582212484L;

	private static Comparator<PodcastFeedEntry> comparator = new Comparator<PodcastFeedEntry>() {
		@Override
		public int compare(PodcastFeedEntry o1, PodcastFeedEntry o2) {
			return o1.name.compareToIgnoreCase(o2.name);
		}
	};
	private String name;
	private String author;
	private String url;
	private String description;
	private long duration;
	private Date date;
	private PodcastFeed podcastFeed;

	private boolean listened;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            the name of the podcast feed entry
	 * @param author
	 *            the author of the podcast feed entry
	 * @param url
	 *            the url of the podcast feed entry
	 * @param description
	 *            the description of the podcast feed entry
	 * @param date
	 *            the date of the podcast feed entry
	 * @param duration
	 *            the duration of the podcast feed entry
	 * @param podcastFeed
	 *            the corresponding podcast feed of the podcast feed entry
	 */
	public PodcastFeedEntry(String name, String author, String url, String description, Date date, long duration, PodcastFeed podcastFeed) {
		this.name = name;
		this.author = author;
		this.url = url;
		this.description = description;
		this.date = date;
		this.podcastFeed = podcastFeed;
		this.duration = duration;
		listened = false;
	}

	/**
	 * @return the comparator of the podcast feed entries
	 */
	public static Comparator<PodcastFeedEntry> getComparator() {
		return comparator;
	}

	public static List<PodcastFeedEntry> getPodcastFeedEntries(List<AudioObject> audioObjects) {
		List<PodcastFeedEntry> result = new ArrayList<PodcastFeedEntry>();
		for (AudioObject audioObject : audioObjects) {
			if (audioObject instanceof PodcastFeedEntry) {
				result.add((PodcastFeedEntry) audioObject);
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof PodcastFeedEntry)) {
			return false;
		} else {
			return getUrl().equals(((PodcastFeedEntry) o).getUrl());
		}
	}

	@Override
	public String getAlbum() {
		return getPodcastFeed().getName();
	}

	@Override
	public String getAlbumArtist() {
		return "";
	}

	@Override
	public String getArtist() {
		return author;
	}

	public String getAuthor() {
		return author;
	}

	@Override
	public long getBitrate() {
		return 0;
	}

	@Override
	public String getComposer() {
		return "";
	}

	public Date getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	@Override
	public int getFrequency() {
		return 0;
	}

	@Override
	public String getGenre() {
		return "";
	}

	@Override
	public String getLyrics() {
		return "";
	}

	public String getName() {
		return name;
	}

	/**
	 * @return The corresponding podcast feed
	 */
	public PodcastFeed getPodcastFeed() {
		return podcastFeed;
	}

	@Override
	public int getStars() {
		return 0;
	}

	@Override
	public String getTitle() {
		return getName();
	}

	@Override
	public String getTitleOrFileName() {
		return getName();
	}

	@Override
	public Integer getTrackNumber() {
		return 0;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getYear() {
		if (date != null) {
			Calendar calendar = DateUtils.getCalendar();
			calendar.setTime(date);
			return String.valueOf(calendar.get(Calendar.YEAR));
		}
		return "";
	}

	@Override
	public int hashCode() {
		return getUrl().hashCode();
	}

	/**
	 * @return if the podcast feed entry was already listened
	 */
	public boolean isListened() {
		return listened;
	}

	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param listened
	 *            if the podcast feed entry was already listened
	 */
	public void setListened(boolean listened) {
		this.listened = listened;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param podcastFeed
	 *            the corresponding podcast feed to set
	 */
	public void setPodcastFeed(PodcastFeed podcastFeed) {
		this.podcastFeed = podcastFeed;
	}

	@Override
	public void setStars(int stars) {
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return name;
	}
}
