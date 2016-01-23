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

package net.sourceforge.atunes.kernel.controllers.navigation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.controllers.navigation.NavigationControllerState.ViewMode;
import net.sourceforge.atunes.kernel.handlers.DeviceHandler;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.handlers.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.handlers.RadioHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.LanguageTool;

public class NodeToSongsTranslator {

	private NavigationControllerState state;

	protected NodeToSongsTranslator(NavigationControllerState state) {
		this.state = state;
	}

	protected List<AudioObject> getSongsForDeviceTreeNode(DefaultMutableTreeNode node) {
		List<AudioObject> songs = new ArrayList<AudioObject>();
		if (node.isRoot()) {
			if (state.getCurrentFilter() == null)
				songs.addAll(DeviceHandler.getInstance().getDeviceSongs());
			else {
				songs = new ArrayList<AudioObject>();
				for (int i = 0; i < node.getChildCount(); i++) {
					TreeObject obj = (TreeObject) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
					songs.addAll(obj.getAudioObjects());
				}
			}
		} else {
			TreeObject obj = (TreeObject) node.getUserObject();
			songs = obj.getAudioObjects();
		}
		return RepositoryHandler.getInstance().sort(songs, state.getSortType());
	}

	protected List<AudioObject> getSongsForFavoriteTreeNode(DefaultMutableTreeNode node) {
		List<AudioObject> songs = null;

		if (node.isRoot()) {
			songs = new ArrayList<AudioObject>();
			songs.addAll(RepositoryHandler.getInstance().getSongsForArtists(FavoritesHandler.getInstance().getFavoriteArtistsInfo()));
			songs.addAll(RepositoryHandler.getInstance().getSongsForAlbums(FavoritesHandler.getInstance().getFavoriteAlbumsInfo()));
			songs.addAll(FavoritesHandler.getInstance().getFavoriteSongsInfo().values());
		} else {
			if (node.getUserObject() instanceof TreeObject) {
				songs = ((TreeObject) node.getUserObject()).getAudioObjects();
			} else {
				songs = new ArrayList<AudioObject>();
				if (node.getUserObject().toString().equals(LanguageTool.getString("ARTISTS"))) {
					songs.addAll(RepositoryHandler.getInstance().getSongsForArtists(FavoritesHandler.getInstance().getFavoriteArtistsInfo()));
				} else if (node.getUserObject().toString().equals(LanguageTool.getString("ALBUMS"))) {
					songs.addAll(RepositoryHandler.getInstance().getSongsForAlbums(FavoritesHandler.getInstance().getFavoriteAlbumsInfo()));
				} else {
					songs.addAll(new ArrayList<AudioFile>(FavoritesHandler.getInstance().getFavoriteSongsInfo().values()));
				}
			}
		}

		return RepositoryHandler.getInstance().sort(songs, state.getSortType());
	}

	protected List<AudioObject> getSongsForNavigationTree(TreePath[] paths) {
		if (paths != null) {
			List<AudioObject> songs = new ArrayList<AudioObject>();
			for (TreePath element : paths) {
				Object obj = ((DefaultMutableTreeNode) element.getLastPathComponent()).getUserObject();
				if (obj instanceof TreeObject)
					songs.addAll(((TreeObject) obj).getAudioObjects());
				else { // if (((DefaultMutableTreeNode)paths[i].getLastPathComponent()).isRoot()) {
					songs.addAll(RepositoryHandler.getInstance().getSongs());
				}
			}
			return songs;
		}
		return new ArrayList<AudioObject>();
	}

	protected List<AudioObject> getSongsForPodcastFeedTreeNode(DefaultMutableTreeNode node) {
		List<AudioObject> songs = new ArrayList<AudioObject>();
		if (node.isRoot()) {
			if (state.getCurrentFilter() == null) {
				List<PodcastFeed> podcastFeeds = PodcastFeedHandler.getInstance().getPodcastFeeds();
				for (PodcastFeed pf : podcastFeeds)
					songs.addAll(pf.getAudioObjects());
			} else {
				for (int i = 0; i < node.getChildCount(); i++) {
					PodcastFeed obj = (PodcastFeed) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
					songs.addAll(obj.getAudioObjects());
				}
			}
		} else {
			PodcastFeed obj = (PodcastFeed) node.getUserObject();
			songs = obj.getAudioObjects();
		}
		return songs;
	}

	protected List<AudioObject> getSongsForRadioTreeNode(DefaultMutableTreeNode node) {
		List<AudioObject> songs = new ArrayList<AudioObject>();
		if (node.isRoot()) {
			if (state.getCurrentFilter() == null) {
				List<Radio> radios = RadioHandler.getInstance().getRadios();
				for (Radio r : radios)
					songs.add(r);
			} else {
				for (int i = 0; i < node.getChildCount(); i++) {
					Radio obj = (Radio) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
					songs.add(obj);
				}
			}
		} else {
			Radio obj = (Radio) node.getUserObject();
			songs.add(obj);
		}
		return songs;
	}

	protected List<AudioObject> getSongsForTreeNode(DefaultMutableTreeNode node) {
		List<AudioObject> songs = new ArrayList<AudioObject>();
		if (node.isRoot()) {
			if (state.getCurrentFilter() == null)
				songs.addAll(RepositoryHandler.getInstance().getSongs());
			else {
				songs = new ArrayList<AudioObject>();
				for (int i = 0; i < node.getChildCount(); i++) {
					TreeObject obj = (TreeObject) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
					songs.addAll(obj.getAudioObjects());
				}
			}
		} else {
			// If album view is selected, we must work with artist nodes, not album node because
			// several artist can have an album with the same name but only songs of one artist 
			// would be shown.
			if (state.getViewMode() == ViewMode.ALBUM) {
				songs = new ArrayList<AudioObject>();
				// Here we have child nodes so it is not the last child (subAlbumNode) that is selected
				if (node.getChildCount() != 0)
					for (int i = 0; i < node.getChildCount(); i++) {
						TreeObject obj = (TreeObject) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
						if (node.getChildAt(i).getChildCount() == 0)
							songs.addAll(obj.getAudioObjects());
						else {
							// We are in album node and must first open one more node
							for (int j = 0; j < node.getChildAt(i).getChildCount(); j++) {
								TreeObject obj2 = (TreeObject) ((DefaultMutableTreeNode) node.getChildAt(i).getChildAt(j)).getUserObject();
								if (node.getChildAt(i).getChildAt(j).getChildCount() == 0)
									songs.addAll(obj2.getAudioObjects());
							}
						}
					}
				// Artist is selected
				else {
					TreeObject obj = (TreeObject) node.getUserObject();
					songs = obj.getAudioObjects();
				}
			} else {
				TreeObject obj = (TreeObject) node.getUserObject();
				songs = obj.getAudioObjects();
			}
		}
		List<AudioObject> result = RepositoryHandler.getInstance().sort(songs, state.getSortType());
		return result;
	}
}
