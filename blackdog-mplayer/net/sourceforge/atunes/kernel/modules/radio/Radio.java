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

package net.sourceforge.atunes.kernel.modules.radio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;

public class Radio implements AudioObject, Serializable, TreeObject {

	private static final long serialVersionUID = 3295941106814718559L;

	private static final String[] PLAYLISTS = { "m3u", "pls", "asx", "wax", "b4s", "kpl", "wvx", "ram", "smil" };

	private static Comparator<Radio> comparator = new Comparator<Radio>() {
		@Override
		public int compare(Radio o1, Radio o2) {
			return o1.name.compareToIgnoreCase(o2.name);
		}
	};

	private String name;
	private String url;

	// Song infos from radio stream
	private transient String artist;
	private transient String title;
	private transient boolean songInfoAvailable;

	private long bitrate;
	private int frequency;

	public Radio(String name, String url) {
		this.name = name;
		this.url = url;
	}

	/**
	 * @return the comparator
	 */
	public static Comparator<Radio> getComparator() {
		return comparator;
	}

	public static List<Radio> getRadios(List<AudioObject> audioObjects) {
		List<Radio> result = new ArrayList<Radio>();
		for (AudioObject audioObject : audioObjects) {
			if (audioObject instanceof Radio) {
				result.add((Radio) audioObject);
			}
		}
		return result;
	}

	public void deleteSongInfo() {
		artist = null;
		title = null;
		songInfoAvailable = false;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Radio)) {
			return false;
		} else {
			return (name + url).equals(((Radio) o).getName() + ((Radio) o).getUrl());
		}

	}

	@Override
	public String getAlbum() {
		return "";
	}

	@Override
	public String getAlbumArtist() {
		return "";
	}

	@Override
	public String getArtist() {
		return artist == null ? "" : artist;
	}

	@Override
	public List<AudioObject> getAudioObjects() {
		List<AudioObject> songs = new ArrayList<AudioObject>();
		songs.add(this);
		return songs;
	}

	@Override
	public long getBitrate() {
		return bitrate;
	}

	@Override
	public String getComposer() {
		return "";
	}

	@Override
	public long getDuration() {
		return 0;
	}

	@Override
	public int getFrequency() {
		return frequency;
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

	public List<Radio> getRadios() {
		List<Radio> songs = new ArrayList<Radio>();
		songs.add(this);
		return songs;
	}

	@Override
	public int getStars() {
		return 0;
	}

	@Override
	public String getTitle() {
		return title == null ? getName() : title;
	}

	@Override
	public String getTitleOrFileName() {
		return getTitle();
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
		return "";
	}

	@Override
	public int hashCode() {
		return (name + url).hashCode();
	}

	public boolean hasPlaylistUrl() {
		for (String pl : PLAYLISTS) {
			if (url.trim().toLowerCase().endsWith(pl)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSongInfoAvailable() {
		return songInfoAvailable;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setBitrate(long bitrate) {
		this.bitrate = bitrate;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSongInfoAvailable(boolean songInfoAvailable) {
		this.songInfoAvailable = songInfoAvailable;
	}

	@Override
	public void setStars(int stars) {
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return name;
	}

}
