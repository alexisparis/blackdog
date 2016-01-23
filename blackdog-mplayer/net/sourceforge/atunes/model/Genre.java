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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;

/**
 * This class represents a genre, with a name, and a set of artist of this genre
 * 
 */
public class Genre implements Serializable, TreeObject {

	private static final long serialVersionUID = -6552057266561177152L;

	/**
	 * Name of the genre
	 */
	private String name;

	/**
	 * Artists of this genre, indexed by artist name
	 */
	private Map<String, Artist> artists;

	/**
	 * Constructor
	 * 
	 * @param name
	 */
	public Genre(String name) {
		this.name = name;
		artists = new HashMap<String, Artist>();
	}

	/**
	 * Adds an artist to this genre
	 * 
	 * @param a
	 */
	public void addArtist(Artist a) {
		artists.put(a.getName(), a);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Genre)) {
			return false;
		} else {
			return ((Genre) o).name.equals(name);
		}
	}

	/**
	 * Returns an Artist for a given artist name
	 * 
	 * @param a
	 * @return
	 */
	public Artist getArtist(String a) {
		return artists.get(a);
	}

	/**
	 * Returns artists of this genre
	 * 
	 * @return
	 */
	public Map<String, Artist> getArtists() {
		return artists;
	}

	public List<AudioFile> getAudioFiles() {
		List<AudioFile> songs = new ArrayList<AudioFile>();
		for (Artist art : artists.values()) {
			songs.addAll(art.getAudioFiles());
		}
		return songs;
	}

	/**
	 * Returns all songs of this genre (all songs of all artists)
	 */
	@Override
	public List<AudioObject> getAudioObjects() {
		List<AudioObject> songs = new ArrayList<AudioObject>();
		for (Artist art : artists.values()) {
			songs.addAll(art.getAudioObjects());
		}
		return songs;
	}

	/**
	 * Returns the name of this genre
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * Removes an artist from this genre
	 * 
	 * @param a
	 */
	public void removeArtist(Artist a) {
		artists.remove(a.getName());
	}

	/**
	 * String representation
	 */
	@Override
	public String toString() {
		return getName();
	}

}
