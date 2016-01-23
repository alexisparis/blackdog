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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to contain all playlists when storing and reading from
 * disk
 * 
 * @author alex
 * 
 */
public class ListOfPlayLists implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9098493526495552598L;

	private List<PlayList> playLists;

	private int selectedPlayList;

	/**
	 * Returns a list of playlists with an empty playlist
	 * 
	 * @return
	 */
	public static ListOfPlayLists getEmptyPlayList() {
		ListOfPlayLists l = new ListOfPlayLists();
		List<PlayList> list = new ArrayList<PlayList>();
		list.add(new PlayList());
		l.setPlayLists(list);
		l.setSelectedPlayList(0);
		return l;
	}

	/**
	 * @return the playLists
	 */
	public List<PlayList> getPlayLists() {
		return playLists;
	}

	/**
	 * @return the selectedPlayList
	 */
	public int getSelectedPlayList() {
		return selectedPlayList;
	}

	/**
	 * Returns selected playlist object
	 * 
	 * @return
	 */
	public PlayList getSelectedPlayListObject() {
		if (playLists.size() > selectedPlayList)
			return playLists.get(selectedPlayList);
		return null;
	}

	/**
	 * @param playLists
	 *            the playLists to set
	 */
	public void setPlayLists(List<PlayList> playLists) {
		this.playLists = playLists;
	}

	/**
	 * @param selectedPlayList
	 *            the selectedPlayList to set
	 */
	public void setSelectedPlayList(int selectedPlayList) {
		this.selectedPlayList = selectedPlayList;
	}

}
