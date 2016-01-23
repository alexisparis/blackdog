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

package net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model;

import java.io.Serializable;
import java.util.List;

import net.sourceforge.atunes.utils.StringUtils;

public class CDInfo implements Serializable {

	private static final long serialVersionUID = -979380358668685738L;

	private String id;
	private int tracks;
	private String duration;
	private List<String> durations;
	private String album;
	// Album artist
	private String artist;
	private List<String> titles;
	private List<String> artists;
	private List<String> composers;

	public String getAlbum() {
		return album;
	}

	public String getArtist() {
		return artist;
	}

	public List<String> getArtists() {
		return artists;
	}

	public List<String> getComposers() {
		return composers;
	}

	public String getDuration() {
		return duration;
	}

	public List<String> getDurations() {
		return durations;
	}

	public String getId() {
		return id;
	}

	public List<String> getTitles() {
		return titles;
	}

	public int getTracks() {
		return tracks;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setArtists(List<String> artists) {
		this.artists = artists;
	}

	public void setComposers(List<String> composers) {
		this.composers = composers;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setDurations(List<String> durations) {
		this.durations = durations;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public void setTracks(int tracks) {
		this.tracks = tracks;
	}

	@Override
	public String toString() {
		return StringUtils.getString("(id=", id, " tracks=", tracks, " duration=", duration, ")");
	}

}
