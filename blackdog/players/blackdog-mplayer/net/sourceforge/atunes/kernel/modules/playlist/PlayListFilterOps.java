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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.gui.model.PlayListTableModel;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.model.AudioObject;

public class PlayListFilterOps {

	/**
	 * Stores original play list without filter
	 */
	private static PlayList nonFilteredPlayList;

	public static boolean isFiltered() {
		return nonFilteredPlayList != null;
	}

	/**
	 * Applies filter to play list. If filter is null, previous existing filter
	 * is removed
	 * 
	 * @param filter
	 */
	public static void setFilter(String filter) {
		String filterText = filter;

		// If filter is null, remove previous filter
		if (filterText == null) {
			// If play list was filtered, back to non-filtered play list
			if (nonFilteredPlayList != null) {
				setPlayListAfterFiltering(nonFilteredPlayList);
				nonFilteredPlayList = null;
			}
			// Empty filter text
			ControllerProxy.getInstance().getPlayListFilterController().emptyFilter();
			/*
			 * In order to avoid that the filter changes its visibility status
			 * (shown/hidden) uncommented the following line. The search filter
			 * stays in the status it is, which seems reasonable. If its hidden
			 * no problem, if its visible it means the user is using it. // Hide
			 * filter text VisualHandler.getInstance().showFilter(false);
			 */
		} else {
			// Store original play list without filter
			if (nonFilteredPlayList == null)
				nonFilteredPlayList = (PlayList) PlayListCommonOps.getPlayList().clone();

			// Create a new play list by filtering elements
			// Filter is applied to title, artist and album, if viewable
			PlayList newPlayList = new PlayList();
			boolean filterArtist = VisualHandler.getInstance().getPlayListTableModel().isArtistVisible();
			boolean filterAlbum = VisualHandler.getInstance().getPlayListTableModel().isAlbumVisible();

			filterText = filterText.toLowerCase();

			for (AudioObject f : nonFilteredPlayList) {
				if (f.getTitleOrFileName().toLowerCase().contains(filterText) || // Match title
						(filterArtist && f.getArtist().toLowerCase().contains(filterText)) || // Match artist
						(filterAlbum && f.getAlbum().toLowerCase().contains(filterText))) // Match album
					// If macthes, add to filtered play list
					newPlayList.add(f);
			}

			setPlayListAfterFiltering(newPlayList);
		}
	}

	private static void setPlayListAfterFiltering(PlayList playList) {
		PlayList currentPlayList = PlayerHandler.getInstance().getCurrentPlayList();
		PlayListTable table = VisualHandler.getInstance().getPlayListTable();

		if (playList.size() > currentPlayList.size()) { // Removing filter
			AudioObject selectedSong = currentPlayList.getCurrentAudioObject();
			int index = playList.indexOf(selectedSong);
			((PlayListTableModel) table.getModel()).removeSongs();
			for (int i = 0; i < playList.size(); i++) {
				((PlayListTableModel) table.getModel()).addSong(playList.get(i));
				currentPlayList.add(playList.get(i));
			}
			PlayerHandler.getInstance().setCurrentPlayList(playList);
			PlayerHandler.getInstance().getCurrentPlayList().setNextAudioObject(index != -1 ? index : 0);
			ControllerProxy.getInstance().getPlayListController().setSelectedSong(index != -1 ? index : 0);
			if (index == -1)
				PlayListHandler.getInstance().selectedAudioObjectChanged(currentPlayList.get(0));
		} else {
			// Remove from table 
			List<Integer> rowsToRemove = new ArrayList<Integer>();
			for (int i = 0; i < currentPlayList.size(); i++) {
				AudioObject ao = currentPlayList.get(i);
				if (!playList.contains(ao)) {
					rowsToRemove.add(i);
				}
			}
			int[] rowsToRemoveArray = new int[rowsToRemove.size()];
			for (int i = 0; i < rowsToRemove.size(); i++)
				rowsToRemoveArray[i] = rowsToRemove.get(i);

			((PlayListTableModel) table.getModel()).removeSongs(rowsToRemoveArray);
			PlayListHandler.getInstance().removeSongs(rowsToRemoveArray);
		}
		VisualHandler.getInstance().showPlayListInformation(currentPlayList);
	}

}
