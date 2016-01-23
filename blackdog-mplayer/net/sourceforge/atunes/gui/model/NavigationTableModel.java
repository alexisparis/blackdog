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

package net.sourceforge.atunes.gui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationControllerViews;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * @author fleax
 * 
 */
public class NavigationTableModel implements TableModel {

	public static final int NO_PROPERTIES = 0;
	public static final int FAVORITE = 1;
	public static final int NOT_LISTENED_ENTRY = 2;

	private NavigationController controller;

	private List<AudioObject> songs;

	private List<TableModelListener> listeners;

	public NavigationTableModel(NavigationController controller) {
		this.controller = controller;
		songs = new ArrayList<AudioObject>();
		listeners = new ArrayList<TableModelListener>();
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return Integer.class;
		else if (columnIndex == 1)
			return String.class;
		else
			return Long.class;
	}

	@Override
	public int getColumnCount() {
		if (controller.getState().getNavigationView() != NavigationControllerViews.RADIO_VIEW)
			return 3;
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {
		int view = controller.getState().getNavigationView();
		if (columnIndex == 0)
			return "";
		else if (columnIndex == 2)
			return LanguageTool.getString("DURATION");
		else if (view == NavigationControllerViews.TAG_VIEW || view == NavigationControllerViews.FAVORITE_VIEW
				|| (view == NavigationControllerViews.DEVICE_VIEW && Kernel.getInstance().state.isSortDeviceByTag()))
			return LanguageTool.getString("TITLE");
		else if (view == NavigationControllerViews.PODCAST_FEED_VIEW)
			return LanguageTool.getString("PODCAST_ENTRIES");
		else if (view != NavigationControllerViews.RADIO_VIEW)
			return LanguageTool.getString("FILE");
		else
			return LanguageTool.getString("URL");
	}

	@Override
	public int getRowCount() {
		return songs.size();
	}

	public AudioObject getSongAt(int row) {
		return songs.get(row);
	}

	public List<AudioObject> getSongs() {
		return songs;
	}

	public List<AudioObject> getSongsAt(int[] rows) {
		List<AudioObject> result = new ArrayList<AudioObject>();
		for (int element : rows) {
			result.add(getSongAt(element));
		}
		return result;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		AudioObject ao = songs.get(rowIndex);

		if (ao instanceof Radio) {
			if (columnIndex == 0)
				return 0;
			else if (columnIndex == 1) {
				return ao.getUrl();
			} else
				return Long.valueOf(0);
		}

		if (columnIndex == 0) {
			if (ao instanceof PodcastFeedEntry)
				return ((PodcastFeedEntry) ao).isListened() ? NO_PROPERTIES : NOT_LISTENED_ENTRY;
			return Kernel.getInstance().state.isShowFavoritesInNavigator() && FavoritesHandler.getInstance().getFavoriteSongs().contains(ao) ? FAVORITE : NO_PROPERTIES;
		} else if (columnIndex == 1) {
			if (controller.getState().getNavigationView() == NavigationControllerViews.TAG_VIEW
					|| controller.getState().getNavigationView() == NavigationControllerViews.FAVORITE_VIEW || Kernel.getInstance().state.isSortDeviceByTag())
				return ao.getTitleOrFileName();
			return ao instanceof AudioFile ? ((AudioFile) ao).getName() : ao.getTitleOrFileName();
		} else
			return ao.getDuration();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void refresh() {
		TableModelEvent event = new TableModelEvent(this, 0, this.getRowCount() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).tableChanged(event);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	public void setSongs(List<AudioObject> songs) {
		this.songs = songs;
		TableModelEvent event = new TableModelEvent(this, -1, this.getRowCount() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).tableChanged(event);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// Nothing to do
	}

}
