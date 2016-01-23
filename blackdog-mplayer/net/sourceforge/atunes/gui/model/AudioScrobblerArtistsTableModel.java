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

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerArtist;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class AudioScrobblerArtistsTableModel implements TableModel {

	private List<AudioScrobblerArtist> artists;
	private List<TableModelListener> listeners;

	public AudioScrobblerArtistsTableModel() {
		this.artists = new ArrayList<AudioScrobblerArtist>();
		listeners = new ArrayList<TableModelListener>();
	}

	public void addArtist(AudioScrobblerArtist artist) {
		artists.add(artist);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public AudioScrobblerArtist getArtist(int index) {
		return artists.get(index);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return AudioScrobblerArtist.class;
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0)
			return LanguageTool.getString("SIMILAR_ARTISTS");
		return "";
	}

	@Override
	public int getRowCount() {
		return artists.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0)
			return artists.get(rowIndex);
		return StringUtils.getString(artists.get(rowIndex).getMatch(), " %");
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// Nothing to do
	}
}
