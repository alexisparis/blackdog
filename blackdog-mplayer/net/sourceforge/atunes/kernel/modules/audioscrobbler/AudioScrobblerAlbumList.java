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

package net.sourceforge.atunes.kernel.modules.audioscrobbler;

import java.util.List;

public class AudioScrobblerAlbumList {

	private String artist;
	private List<AudioScrobblerAlbum> albums;

	/**
	 * @return the albums
	 */
	public List<AudioScrobblerAlbum> getAlbums() {
		return albums;
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @param albums
	 *            the albums to set
	 */
	public void setAlbums(List<AudioScrobblerAlbum> albums) {
		this.albums = albums;
	}

	/**
	 * @param artist
	 *            the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

}
