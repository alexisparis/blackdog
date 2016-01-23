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
import java.util.Map;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.gui.views.controls.playList.Column;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListColumns;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListColumns.PlayListColumn;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * @author fleax
 * 
 */
public class PlayListTableModel implements TableModel {

	/*
	 * Available columns
	 */
	private static Map<PlayListColumn, Column> columns = PlayListColumns.getColumns();

	private JTable table;
	private List<AudioObject> songs;

	private List<TableModelListener> listeners;

	private PlayListColumn[] currentHeaders;

	/**
	 * Constructor
	 * 
	 * @param table
	 */
	public PlayListTableModel(JTable table) {
		this.table = table;
		songs = new ArrayList<AudioObject>();
		listeners = new ArrayList<TableModelListener>();
		setCurrentHeaders();
	}

	/**
	 * Sets width for a column
	 * 
	 * @param c
	 * @param width
	 */
	private static void setWidthForColumn(PlayListColumn c, int width) {
		columns.get(c).setWidth(width);
	}

	/**
	 * Adds a song to Play list
	 * 
	 * @param song
	 */
	public void addSong(AudioObject song) {
		songs.add(song);

		TableModelEvent event;
		event = new TableModelEvent(this, this.getRowCount() - 1, this.getRowCount() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);

		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).tableChanged(event);
	}

	/**
	 * Adds a listener
	 */
	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public void arrangeColumns(boolean reapplyFilter) {
		setCurrentHeaders();
		refresh();
		if (reapplyFilter)
			ControllerProxy.getInstance().getPlayListFilterController().reapplyFilter();
	}

	/**
	 * Return Column object for a given column number
	 * 
	 * @param column
	 * @return
	 */
	public Column getColumn(int column) {
		return columns.get(getColumnId(column));
	}

	/**
	 * Returns column data class
	 */
	@Override
	public Class<?> getColumnClass(int colIndex) {
		return columns.get(getColumnId(colIndex)).getColumnClass();
	}

	/**
	 * Return column count
	 */
	@Override
	public int getColumnCount() {
		int visibleColumns = 0;

		for (Column c : columns.values())
			if (c.isVisible())
				visibleColumns++;

		return visibleColumns;
	}

	/**
	 * Returns Column ID given a column number
	 * 
	 * @param colIndex
	 * @return
	 */
	public PlayListColumn getColumnId(int colIndex) {
		return currentHeaders[colIndex];
	}

	/**
	 * Return column name
	 */
	@Override
	public String getColumnName(int colIndex) {
		return LanguageTool.getString(columns.get(getColumnId(colIndex)).getHeaderText());
	}

	/**
	 * Return a file of a row
	 * 
	 * @param pos
	 * @return
	 */
	public AudioObject getFileAt(int pos) {
		return songs.get(pos);
	}

	/**
	 * Return row count
	 */
	@Override
	public int getRowCount() {
		return songs.size();
	}

	/**
	 * Returns value of a row and column
	 */
	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		// AudioFile from we must take a value
		AudioObject file = songs.get(rowIndex);

		// Column ID
		PlayListColumn c = getColumnId(colIndex);

		// Call Column method to get value from AudioFile
		return columns.get(c).getValueFor(file);
	}

	/**
	 * Returns width for a given column number
	 * 
	 * @param column
	 * @return
	 */
	public int getWidthForColumn(int column) {
		PlayListColumn c = getColumnId(column);
		Integer width = columns.get(c).getWidth();
		return width;
	}

	/**
	 * Returns width for a given column
	 * 
	 * @param c
	 * @return
	 */
	public int getWidthForColumn(PlayListColumn c) {
		return columns.get(c).getWidth();
	}

	public boolean isAlbumVisible() {
		return columns.get(PlayListColumn.ALBUM_ID).isVisible();
	}

	public boolean isArtistVisible() {
		return columns.get(PlayListColumn.ARTIST_ID).isVisible();
	}

	/**
	 * Returns if a cell is editable.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// Column ID
		PlayListColumn c = getColumnId(columnIndex);

		// Call Column method to see if is editable
		return columns.get(c).isEditable();
	}

	public boolean isEmpty() {
		return songs.isEmpty();
	}

	/**
	 * Move down
	 * 
	 * @param rows
	 */
	public void moveDown(int[] rows) {
		for (int i = rows.length - 1; i >= 0; i--) {
			AudioObject aux = songs.get(rows[i]);
			songs.remove(rows[i]);
			songs.add(rows[i] + 1, aux);
		}

		TableModelEvent event;
		event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).tableChanged(event);
	}

	/**
	 * Move bottom
	 * 
	 * @param rows
	 */
	public void moveToBottom(int[] rows) {
		int j = 0;
		for (int i = rows.length - 1; i >= 0; i--) {
			AudioObject aux = songs.get(rows[i]);
			songs.remove(rows[i]);
			songs.add(songs.size() - j++, aux);
		}

		TableModelEvent event;
		event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).tableChanged(event);
	}

	/**
	 * Move to top
	 * 
	 * @param rows
	 */
	public void moveToTop(int[] rows) {
		for (int i = 0; i < rows.length; i++) {
			AudioObject aux = songs.get(rows[i]);
			songs.remove(rows[i]);
			songs.add(i, aux);
		}

		TableModelEvent event;
		event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).tableChanged(event);
	}

	/**
	 * Move up
	 * 
	 * @param rows
	 */
	public void moveUp(int[] rows) {
		for (int element : rows) {
			AudioObject aux = songs.get(element);
			songs.remove(element);
			songs.add(element - 1, aux);
		}

		TableModelEvent event;
		event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).tableChanged(event);
	}

	/**
	 * Refresh table
	 * 
	 */
	public void refresh() {
		TableModelEvent event;
		event = new TableModelEvent(this, -1, -1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).tableChanged(event);
	}

	/**
	 * Refresh a row
	 * 
	 * @param pos
	 */
	public void refresh(int pos) {
		TableModelEvent event;
		event = new TableModelEvent(this, pos, pos, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).tableChanged(event);
	}

	/**
	 * Refresh visually table
	 * 
	 */
	public void refreshTable() {
		table.revalidate();
		table.repaint();
	}

	/**
	 * Removes all songs
	 * 
	 */
	public void removeSongs() {
		songs.clear();

		TableModelEvent event;
		event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);

		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).tableChanged(event);
	}

	/**
	 * Removes songs from Play List
	 * 
	 * @param rows
	 */
	public void removeSongs(int[] rows) {
		for (int i = rows.length - 1; i >= 0; i--) {
			songs.remove(rows[i]);
		}

		TableModelEvent event;
		event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);

		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).tableChanged(event);
	}

	/**
	 * Removes a listener
	 */
	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	/**
	 * Sets column order
	 * 
	 */
	private void setCurrentHeaders() {
		int columnNumber = getColumnCount();
		if (columnNumber == 0)
			return;

		currentHeaders = new PlayListColumn[columnNumber];

		int i = 0;
		for (Column c : PlayListColumns.getColumnsOrdered()) {
			if (c.isVisible())
				currentHeaders[i++] = c.getColumnId();
		}

	}

	/**
	 * Sets value for a cell. Does nothing as cells are not editable
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// AudioFile
		AudioObject file = songs.get(rowIndex);

		// Column ID
		PlayListColumn c = getColumnId(columnIndex);

		// Call column set value
		columns.get(c).setValueFor(file, aValue);
	}

	/**
	 * Updates columns width
	 */
	public void updateColumnWidth() {
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			PlayListColumn col = getColumnId(i);
			int width = table.getColumnModel().getColumn(i).getPreferredWidth();
			setWidthForColumn(col, width);
		}
	}
}
