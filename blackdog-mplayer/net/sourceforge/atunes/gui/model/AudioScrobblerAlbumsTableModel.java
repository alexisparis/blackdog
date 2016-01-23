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

import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerAlbum;
import net.sourceforge.atunes.utils.LanguageTool;

public class AudioScrobblerAlbumsTableModel implements TableModel {

	private List<AudioScrobblerAlbum> albums;
	private List<TableModelListener> listeners;

	public AudioScrobblerAlbumsTableModel() {
		this.albums = new ArrayList<AudioScrobblerAlbum>();
		listeners = new ArrayList<TableModelListener>();
	}

	public void addAlbum(AudioScrobblerAlbum album) {
		albums.add(album);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public AudioScrobblerAlbum getAlbum(int index) {
		return albums.get(index);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return AudioScrobblerAlbum.class;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return LanguageTool.getString("ALBUMS");
	}

	@Override
	public int getRowCount() {
		return albums.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return albums.get(rowIndex);
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
