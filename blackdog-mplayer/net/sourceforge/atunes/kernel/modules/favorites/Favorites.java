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

package net.sourceforge.atunes.kernel.modules.favorites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;

public class Favorites implements Serializable {

	private static final long serialVersionUID = 4783402394156393291L;

	private Map<String, AudioFile> favoriteSongs;
	private Map<String, Album> favoriteAlbums;
	private Map<String, Artist> favoriteArtists;

	public Favorites() {
		favoriteSongs = new HashMap<String, AudioFile>();
		favoriteAlbums = new HashMap<String, Album>();
		favoriteArtists = new HashMap<String, Artist>();
	}

	public List<AudioFile> getAllFavoriteSongs() {
		List<AudioFile> result = new ArrayList<AudioFile>();
		for (Artist artist : favoriteArtists.values()) {
			result.addAll(artist.getAudioFiles());
		}
		for (Album album : favoriteAlbums.values()) {
			result.addAll(album.getAudioFiles());
		}
		result.addAll(favoriteSongs.values());
		return result;
	}

	public Map<String, Album> getFavoriteAlbums() {
		return favoriteAlbums;
	}

	public Map<String, Artist> getFavoriteArtists() {
		return favoriteArtists;
	}

	public Map<String, AudioFile> getFavoriteSongs() {
		return favoriteSongs;
	}

	public void setFavoriteAlbums(Map<String, Album> favoriteAlbums) {
		this.favoriteAlbums = favoriteAlbums;
	}

	public void setFavoriteArtists(Map<String, Artist> favoriteArtists) {
		this.favoriteArtists = favoriteArtists;
	}

	public void setFavoriteSongs(Map<String, AudioFile> favoriteSongs) {
		this.favoriteSongs = favoriteSongs;
	}
}
