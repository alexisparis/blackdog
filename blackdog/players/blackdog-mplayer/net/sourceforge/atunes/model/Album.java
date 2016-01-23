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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents an album, with it's name, artist, and songs
 * 
 * @author fleax
 * 
 */
public class Album implements Serializable, TreeObject, Comparable<Album> {

	private static final long serialVersionUID = -1481314950918557022L;

	/**
	 * Name of the album
	 */
	private String name;

	/**
	 * Name of the artist
	 */
	private Artist artist;

	/**
	 * List of songs of this album
	 */
	private List<AudioFile> songs;

	/**
	 * Constructor
	 * 
	 * @param name
	 */
	public Album(String name) {
		this.name = name;
		songs = new ArrayList<AudioFile>();
	}

	/**
	 * Adds a song to this album
	 * 
	 * @param file
	 */
	public void addSong(AudioFile file) {
		songs.add(file);
	}

	/**
	 * Comparator
	 */
	@Override
	public int compareTo(Album o) {
		return this.name.compareTo(o.name);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Album)) {
			return false;
		} else {
			return ((Album) o).name.equals(name) && ((Album) o).artist.equals(artist);
		}
	}

	/**
	 * Returns the name of the artist of this album
	 * 
	 * @return
	 */
	public Artist getArtist() {
		return artist;
	}

	public List<AudioFile> getAudioFiles() {
		return new ArrayList<AudioFile>(songs);
	}

	/**
	 * Returns a list of songs of this album
	 */
	@Override
	public List<AudioObject> getAudioObjects() {
		return new ArrayList<AudioObject>(songs);
	}

	/**
	 * Returns name of the album
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a picture of this album, with a given size
	 * 
	 * @param width
	 * @param heigth
	 * @return
	 */
	public ImageIcon getPicture(int width, int heigth) {
		return AudioFilePictureUtils.getImageForAudioFile(songs.get(0), width, heigth);
	}

	/**
	 * Returns true if aTunes has saved cover image
	 * 
	 * @return
	 */
	public boolean hasCoverDownloaded() {
		return new File(AudioFilePictureUtils.getFileNameForCover(songs.get(0))).exists();
	}

	@Override
	public int hashCode() {
		return StringUtils.getString(name, artist).hashCode();
	}

	/**
	 * Removes a song from this album
	 * 
	 * @param file
	 */
	public void removeSong(AudioFile file) {
		songs.remove(file);
	}

	/**
	 * Sets the name of the artist of this album
	 * 
	 * @param artist
	 */
	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	/**
	 * String representation of this object
	 */
	@Override
	public String toString() {
		return getName();
	}

}
