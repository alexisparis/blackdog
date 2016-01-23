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

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.model.AudioObject;

public class PlayListRowOps {

	public static void moveDown(int[] rows) {
		PlayList currentPlayList = PlayerHandler.getInstance().getCurrentPlayList();
		for (int i = rows.length - 1; i >= 0; i--) {
			AudioObject aux = currentPlayList.get(rows[i]);
			currentPlayList.remove(rows[i]);
			currentPlayList.add(rows[i] + 1, aux);
		}
		if (rows[rows.length - 1] + 1 == currentPlayList.getNextAudioObject()) {
			currentPlayList.setNextAudioObject(currentPlayList.getNextAudioObject() - rows.length);
			ControllerProxy.getInstance().getPlayListController().setSelectedSong(currentPlayList.getNextAudioObject());
		} else if (rows[0] <= currentPlayList.getNextAudioObject() && currentPlayList.getNextAudioObject() <= rows[rows.length - 1]) {
			currentPlayList.setNextAudioObject(currentPlayList.getNextAudioObject() + 1);
			ControllerProxy.getInstance().getPlayListController().setSelectedSong(currentPlayList.getNextAudioObject());
		}
	}

	public static void moveToBottom(int[] rows) {
		PlayList currentPlayList = PlayerHandler.getInstance().getCurrentPlayList();
		int j = 0;
		for (int i = rows.length - 1; i >= 0; i--) {
			AudioObject aux = currentPlayList.get(rows[i]);
			currentPlayList.remove(rows[i]);
			currentPlayList.add(currentPlayList.size() - j++, aux);
		}
		if (rows[rows.length - 1] < currentPlayList.getNextAudioObject()) {
			currentPlayList.setNextAudioObject(currentPlayList.getNextAudioObject() - rows.length);
			ControllerProxy.getInstance().getPlayListController().setSelectedSong(currentPlayList.getNextAudioObject());
		} else if (rows[0] <= currentPlayList.getNextAudioObject() && currentPlayList.getNextAudioObject() <= rows[rows.length - 1]) {
			currentPlayList.setNextAudioObject(currentPlayList.getNextAudioObject() + currentPlayList.size() - rows[rows.length - 1] - 1);
			ControllerProxy.getInstance().getPlayListController().setSelectedSong(currentPlayList.getNextAudioObject());
		}
	}

	public static void moveToTop(int[] rows) {
		PlayList currentPlayList = PlayerHandler.getInstance().getCurrentPlayList();
		for (int i = 0; i < rows.length; i++) {
			AudioObject aux = currentPlayList.get(rows[i]);
			currentPlayList.remove(rows[i]);
			currentPlayList.add(i, aux);
		}
		if (rows[0] > currentPlayList.getNextAudioObject()) {
			currentPlayList.setNextAudioObject(currentPlayList.getNextAudioObject() + rows.length);
			ControllerProxy.getInstance().getPlayListController().setSelectedSong(currentPlayList.getNextAudioObject());
		} else if (rows[0] <= currentPlayList.getNextAudioObject() && currentPlayList.getNextAudioObject() <= rows[rows.length - 1]) {
			currentPlayList.setNextAudioObject(currentPlayList.getNextAudioObject() - rows[0]);
			ControllerProxy.getInstance().getPlayListController().setSelectedSong(currentPlayList.getNextAudioObject());
		}
	}

	public static void moveUp(int[] rows) {
		PlayList currentPlayList = PlayerHandler.getInstance().getCurrentPlayList();
		for (int element : rows) {
			AudioObject aux = currentPlayList.get(element);
			currentPlayList.remove(element);
			currentPlayList.add(element - 1, aux);
		}
		if (rows[0] - 1 == currentPlayList.getNextAudioObject()) {
			currentPlayList.setNextAudioObject(currentPlayList.getNextAudioObject() + rows.length);
			ControllerProxy.getInstance().getPlayListController().setSelectedSong(currentPlayList.getNextAudioObject());
		} else if (rows[0] <= currentPlayList.getNextAudioObject() && currentPlayList.getNextAudioObject() <= rows[rows.length - 1]) {
			currentPlayList.setNextAudioObject(currentPlayList.getNextAudioObject() - 1);
			ControllerProxy.getInstance().getPlayListController().setSelectedSong(currentPlayList.getNextAudioObject());
		}
	}

	public static void removeSongs(int[] rows) {
		PlayList currentPlayList = PlayerHandler.getInstance().getCurrentPlayList();
		AudioObject playingSong = currentPlayList.getCurrentAudioObject();
		boolean hasToBeRemoved = false;
		for (int element : rows) {
			if (element == currentPlayList.getNextAudioObject())
				hasToBeRemoved = true;
		}
		for (int i = rows.length - 1; i >= 0; i--) {
			currentPlayList.remove(rows[i]);
		}

		if (hasToBeRemoved) {
			PlayerHandler.getInstance().stop();
			if (currentPlayList.isEmpty()) {
				PlayListHandler.getInstance().clear();
			} else {
				currentPlayList.setNextAudioObject(0);
				ControllerProxy.getInstance().getPlayListController().setSelectedSong(currentPlayList.getNextAudioObject());
				PlayListHandler.getInstance().selectedAudioObjectChanged(currentPlayList.getCurrentAudioObject());
			}
		} else {
			currentPlayList.setNextAudioObject(currentPlayList.indexOf(playingSong));
			ControllerProxy.getInstance().getPlayListController().setSelectedSong(currentPlayList.getNextAudioObject());
			PlayListHandler.getInstance().selectedAudioObjectChanged(currentPlayList.getCurrentAudioObject());
		}

		if (currentPlayList.isEmpty()) {
			ControllerProxy.getInstance().getPlayListControlsController().enableSaveButton(false);
			ControllerProxy.getInstance().getMenuController().enableSavePlaylist(false);
		}
		VisualHandler.getInstance().showPlayListInformation(currentPlayList);
	}
}
