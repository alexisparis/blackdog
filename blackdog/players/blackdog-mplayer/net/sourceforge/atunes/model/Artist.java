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
 * This class represents an artist, with a name, and a list of albums
 * 
 * @author fleax
 * 
 */
public class Artist implements Serializable, TreeObject, Comparable<Artist> {

	private static final long serialVersionUID = -7981636660798555640L;

	/**
	 * Name of the artist
	 */
	private String name;

	/**
	 * List of Album objects, indexed by name
	 */
	private Map<String, Album> albums;

	/**
	 * Constructor
	 * 
	 * @param name
	 */
	public Artist(String name) {
		this.name = name;
		albums = new HashMap<String, Album>();
	}

	/**
	 * Adds an album to this artist
	 * 
	 * @param album
	 */
	public void addAlbum(Album album) {
		albums.put(album.getName(), album);
	}

	/**
	 * Comparator
	 */
	@Override
	public int compareTo(Artist o) {
		return this.name.compareTo(o.name);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Artist)) {
			return false;
		} else {
			return ((Artist) o).name.equals(name);
		}
	}

	/**
	 * Return an Album for a given album name
	 * 
	 * @param albumName
	 * @return
	 */
	public Album getAlbum(String albumName) {
		return albums.get(albumName);
	}

	/**
	 * Return albums of this artist
	 * 
	 * @return
	 */
	public Map<String, Album> getAlbums() {
		return albums;
	}

	public List<AudioFile> getAudioFiles() {
		List<AudioFile> songs = new ArrayList<AudioFile>();
		for (Album album : albums.values()) {
			songs.addAll(album.getAudioFiles());
		}
		return songs;
	}

	/**
	 * Returns a list of songs of this artist (all songs of all albums)
	 */
	@Override
	public List<AudioObject> getAudioObjects() {
		List<AudioObject> songs = new ArrayList<AudioObject>();
		for (Album album : albums.values()) {
			songs.addAll(album.getAudioObjects());
		}
		return songs;
	}

	/**
	 * Returns the name of this artist
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
	 * Removes an album from this artist
	 * 
	 * @param alb
	 */
	public void removeAlbum(Album alb) {
		albums.remove(alb.getName());
	}

	/**
	 * String representation
	 */
	@Override
	public String toString() {
		return getName();
	}
}
