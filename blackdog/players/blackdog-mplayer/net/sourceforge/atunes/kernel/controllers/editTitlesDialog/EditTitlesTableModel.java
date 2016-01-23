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

package net.sourceforge.atunes.kernel.controllers.editTitlesDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.utils.LanguageTool;

public class EditTitlesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -4440078678648669115L;

	private List<AudioFile> files;
	private Map<AudioFile, String> newValues;

	private List<TableModelListener> listeners;

	public EditTitlesTableModel(List<AudioFile> files) {
		this.files = files;
		this.newValues = new HashMap<AudioFile, String>();
		this.listeners = new ArrayList<TableModelListener>();
	}

	public void addListener(TableModelListener listener) {
		listeners.add(listener);
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		if (column == 0)
			return LanguageTool.getString("FILE");
		return LanguageTool.getString("TITLE");
	}

	public Map<AudioFile, String> getNewValues() {
		return newValues;
	}

	@Override
	public int getRowCount() {
		return files.size();
	}

	@Override
	public String getValueAt(int rowIndex, int columnIndex) {
		AudioFile file = files.get(rowIndex);
		if (columnIndex == 0)
			return file.getName();
		if (newValues.containsKey(file))
			return newValues.get(file);
		return file.getTitle();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}

	public void setTitles(List<String> titles) {
		for (int i = 0; i < files.size(); i++) {
			newValues.put(files.get(i), titles.get(i));
		}

		TableModelEvent event;
		event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).tableChanged(event);
	}

	public void setValueAt(String aValue, int rowIndex, int columnIndex) {
		newValues.put(files.get(rowIndex), aValue);
		fireTableCellUpdated(rowIndex, columnIndex);
	}
}
