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

import java.util.List;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.model.AudioObject;

public class PlayListCommonOps {

	public static void addToPlayListAndPlay(List<AudioObject> audioObjects) {
		int playListCurrentSize = getPlayList().size();
		PlayListHandler.getInstance().addToPlayList(audioObjects);
		PlayerHandler.getInstance().setPlayListPositionToPlay(playListCurrentSize);
		PlayerHandler.getInstance().play(false);
	}

	public static AudioObject getCurrentAudioObject() {
		return PlayerHandler.getInstance().getCurrentPlayList().getCurrentAudioObject();
	}

	public static PlayList getPlayList() {
		return PlayerHandler.getInstance().getCurrentPlayList();
	}

	public static List<AudioObject> getSelectedSongs() {
		return ControllerProxy.getInstance().getPlayListController().getSelectedAudioObjects();
	}

	public static void refreshPlayList() {
		VisualHandler.getInstance().getPlayListTableModel().refresh();
	}

	public static void setPlayList(PlayList playList) {
		PlayerHandler.getInstance().setCurrentPlayList(playList);
		ControllerProxy.getInstance().getPlayListController().notifyAudioObjectsAddedToController(playList, playList.getNextAudioObject());
		VisualHandler.getInstance().showPlayListInformation(playList);
		PlayListHandler.getInstance().selectedAudioObjectChanged(playList.getCurrentAudioObject());
	}
}
