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

package net.sourceforge.atunes.model;

/**
 * Interface for all audio objects (e.g. AudioFile, Radio, PodcastFeedEntry)
 */
public interface AudioObject {

	public String getAlbum();

	public String getAlbumArtist();

	public String getArtist();

	public long getBitrate();

	public String getComposer();

	public long getDuration();

	public int getFrequency();

	public String getGenre();

	public String getLyrics();

	public int getStars();

	public String getTitle();

	public String getTitleOrFileName();

	public Integer getTrackNumber();

	public String getUrl();

	public String getYear();

	public void setStars(int stars);
}
