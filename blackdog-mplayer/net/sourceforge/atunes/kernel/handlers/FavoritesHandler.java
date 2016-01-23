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

package net.sourceforge.atunes.kernel.handlers;

import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.favorites.Favorites;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.TreeObject;

public class FavoritesHandler {

	private static FavoritesHandler instance = new FavoritesHandler();

	private Favorites favorites;

	public static FavoritesHandler getInstance() {
		return instance;
	}

	public void addFavoriteAlbums(List<AudioFile> songs) {
		Map<String, Artist> structure = RepositoryHandler.getInstance().getRepository().getStructure().getTreeStructure();
		Map<String, Album> favAlbums = favorites.getFavoriteAlbums();
		for (int i = 0; i < songs.size(); i++) {
			AudioFile f = songs.get(i);
			Artist artist = structure.get(f.getArtist());
			Album album = artist.getAlbum(f.getAlbum());
			favAlbums.put(album.getName(), album);
		}
		// Update playlist to add favorite icon
		VisualHandler.getInstance().getPlayListTableModel().refreshTable();

		// Update favorite tree
		ControllerProxy.getInstance().getNavigationController().refreshFavoriteTree();

		// Update file properties panel
		ControllerProxy.getInstance().getFilePropertiesController().refreshFavoriteIcons();
	}

	public void addFavoriteArtists(List<AudioFile> songs) {
		Map<String, Artist> structure = RepositoryHandler.getInstance().getRepository().getStructure().getTreeStructure();
		Map<String, Artist> favArtists = favorites.getFavoriteArtists();
		for (int i = 0; i < songs.size(); i++) {
			AudioFile f = songs.get(i);
			Artist artist = structure.get(f.getArtist());
			favArtists.put(artist.getName(), artist);
		}
		// Update playlist to add favorite icon
		VisualHandler.getInstance().getPlayListTableModel().refreshTable();

		// Update favorite tree
		ControllerProxy.getInstance().getNavigationController().refreshFavoriteTree();

		// Update file properties panel
		ControllerProxy.getInstance().getFilePropertiesController().refreshFavoriteIcons();
	}

	public void addFavoriteSongs(List<AudioFile> songs) {
		Map<String, AudioFile> favSongs = favorites.getFavoriteSongs();
		for (int i = 0; i < songs.size(); i++) {
			AudioFile f = songs.get(i);
			favSongs.put(f.getUrl(), f);
		}
		// Update playlist to add favorite icon
		VisualHandler.getInstance().getPlayListTableModel().refreshTable();

		// Update favorite tree
		ControllerProxy.getInstance().getNavigationController().refreshFavoriteTree();

		// Update file properties panel
		ControllerProxy.getInstance().getFilePropertiesController().refreshFavoriteIcons();
	}

	public void finish() {
		ApplicationDataHandler.getInstance().persistFavoritesCache(favorites);
	}

	public Map<String, Album> getFavoriteAlbumsInfo() {
		return favorites.getFavoriteAlbums();
	}

	public Map<String, Artist> getFavoriteArtistsInfo() {
		return favorites.getFavoriteArtists();
	}

	/**
	 * @return the favorites
	 */
	public Favorites getFavorites() {
		return favorites;
	}

	public List<AudioFile> getFavoriteSongs() {
		return favorites.getAllFavoriteSongs();
	}

	public Map<String, AudioFile> getFavoriteSongsInfo() {
		return favorites.getFavoriteSongs();
	}

	public void readFavorites() {
		favorites = ApplicationDataHandler.getInstance().retrieveFavoritesCache();
		if (favorites == null)
			favorites = new Favorites();
	}

	public void removeFromFavorites(TreeObject obj) {
		if (obj instanceof Artist)
			favorites.getFavoriteArtists().remove(obj.toString());
		else
			favorites.getFavoriteAlbums().remove(obj.toString());

		// Update playlist to remove favorite icon
		VisualHandler.getInstance().getPlayListTableModel().refreshTable();

		// Update favorites tree
		ControllerProxy.getInstance().getNavigationController().refreshFavoriteTree();

		// Update file properties panel
		ControllerProxy.getInstance().getFilePropertiesController().refreshFavoriteIcons();
	}

	public void removeSongFromFavorites(AudioFile file) {
		favorites.getFavoriteSongs().remove(file.getUrl());

		// Update playlist to remove favorite icon
		VisualHandler.getInstance().getPlayListTableModel().refreshTable();

		// Update favorites tree
		ControllerProxy.getInstance().getNavigationController().refreshFavoriteTree();

		// Update file properties panel
		ControllerProxy.getInstance().getFilePropertiesController().refreshFavoriteIcons();
	}

}
