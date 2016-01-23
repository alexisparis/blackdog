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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.playlist.ListOfPlayLists;
import net.sourceforge.atunes.kernel.modules.playlist.PlayList;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListCommonOps;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class MultiplePlaylistHandler {

	private static MultiplePlaylistHandler instance;

	private static int playListCounter = 1;

	private ArrayList<PlayList> playLists;

	private int currentPlayListIndex = 0;

	private MultiplePlaylistHandler() {
		playLists = new ArrayList<PlayList>();
	}

	public static MultiplePlaylistHandler getInstance() {
		if (instance == null)
			instance = new MultiplePlaylistHandler();
		return instance;
	}

	/**
	 * Adds a playlist
	 * 
	 * @param playlist
	 */
	public void addListOfPlayLists(ListOfPlayLists list) {
		for (PlayList playlist : list.getPlayLists()) {
			playLists.add(playlist);
			ControllerProxy.getInstance().getPlayListTabController().newPlayList(getNameForPlaylist(playlist));
		}
		currentPlayListIndex = list.getSelectedPlayList();
		ControllerProxy.getInstance().getPlayListTabController().forceSwitchTo(currentPlayListIndex);
	}

	/**
	 * Check if any of audio objects are in any of the playlists
	 * 
	 * @param audioObjects
	 * @return
	 */
	public boolean checkIfPlayListsContainsAudioObjects(List<AudioFile> audioObjects) {
		for (PlayList pl : playLists) {
			for (AudioFile af : audioObjects) {
				if (pl.contains(af))
					return true;
			}
		}
		return false;
	}

	/**
	 * Deletes play list referred by index
	 * 
	 * @param index
	 */
	public void deletePlayList(int index) {
		// if there is only one play list, don't delete
		if (playLists.size() <= 1)
			return;
		// delete tab and playlist
		// NOTE: deleting current tab play list calls automatically to switchToPlayList method
		if (index != currentPlayListIndex) {
			// Remove playlist from list
			playLists.remove(index);
			// If index < currentPlayListIndex, current play list has been moved to left, so
			// decrease in 1
			if (index < currentPlayListIndex)
				currentPlayListIndex--;
			// Delete tab
			ControllerProxy.getInstance().getPlayListTabController().deletePlayList(index);
		} else {
			// index == currentPlayList
			// switch play list and then delete
			if (index == 0)
				switchToPlaylist(1);
			else
				switchToPlaylist(currentPlayListIndex - 1);
			deletePlayList(index);
		}
	}

	public ListOfPlayLists getListOfPlayLists() {
		ListOfPlayLists l = new ListOfPlayLists();

		// Save current playlist
		playLists.remove(currentPlayListIndex);
		playLists.add(currentPlayListIndex, PlayerHandler.getInstance().getCurrentPlayList());

		l.setPlayLists(playLists);
		l.setSelectedPlayList(currentPlayListIndex);
		return l;
	}

	private String getNameForPlaylist(PlayList pl) {
		if (pl == null || pl.getName() == null)
			return StringUtils.getString(LanguageTool.getString("PLAYLIST"), " ", playListCounter++);

		return pl.getName();
	}

	/**
	 * Called to create a new play list
	 */
	public void newPlayList() {
		PlayList newPlayList = new PlayList();
		playLists.add(newPlayList);

		ControllerProxy.getInstance().getPlayListTabController().newPlayList(getNameForPlaylist(null));
	}

	/**
	 * Removes songs from other playlists than current one
	 * 
	 * @param audioObjects
	 * @return true if any song has been removed
	 */
	public boolean removeSongsFromOtherPlayLists(List<AudioFile> audioFiles) {
		boolean removed = false;
		for (PlayList pl : playLists) {
			if (playLists.indexOf(pl) != currentPlayListIndex) {
				pl.removeAll(audioFiles);
				removed = true;
			}
		}

		return removed;
	}

	/**
	 * Renames a play list
	 * 
	 * @param index
	 * @param newName
	 */
	public void renamePlayList(int index, String newName) {
		playLists.get(index).setName(newName);
		ControllerProxy.getInstance().getPlayListTabController().renamePlayList(index, newName);
	}

	/**
	 * Called when switching play list at tabbed pane
	 * 
	 * @param index
	 */
	public void switchToPlaylist(int index) {
		// If play list is the same, do nothing, except if this method is called when deleting a play list
		if (index == currentPlayListIndex)
			return;

		// Save current playlist
		playLists.remove(currentPlayListIndex);
		playLists.add(currentPlayListIndex, PlayerHandler.getInstance().getCurrentPlayList());

		currentPlayListIndex = index;

		PlayerHandler.getInstance().stop();

		PlayList newSelectedPlayList = playLists.get(index);

		// Remove filter
		PlayListHandler.getInstance().setFilter(null);

		// Remove songs from model
		VisualHandler.getInstance().getPlayListTableModel().removeSongs();

		// Set selection interval to none
		VisualHandler.getInstance().getPlayListTable().getSelectionModel().setSelectionInterval(-1, -1);

		PlayListCommonOps.setPlayList(newSelectedPlayList);
		PlayListCommonOps.refreshPlayList();
	}
}
