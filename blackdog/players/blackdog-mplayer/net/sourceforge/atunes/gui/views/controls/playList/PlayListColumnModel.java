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

package net.sourceforge.atunes.gui.views.controls.playList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.gui.model.PlayListTableModel;

public class PlayListColumnModel extends DefaultTableColumnModel {

	private static final long serialVersionUID = -2211160302611944001L;

	PlayListTable playList;
	PlayListTableModel model;

	int columnBeingMoved = -1;
	int columnMovedTo = -1;

	public PlayListColumnModel(PlayListTable playList) {
		super();
		this.playList = playList;
		this.model = (PlayListTableModel) this.playList.getModel();

		// Add listener for column size changes
		addColumnModelListener(new TableColumnModelListener() {
			public void columnAdded(TableColumnModelEvent e) {
			}

			public void columnMarginChanged(ChangeEvent e) {
				PlayListColumnModel.this.model.updateColumnWidth();
			}

			public void columnMoved(TableColumnModelEvent e) {
				if (columnBeingMoved == -1)
					columnBeingMoved = e.getFromIndex();
				columnMovedTo = e.getToIndex();
			}

			public void columnRemoved(TableColumnModelEvent e) {
			}

			public void columnSelectionChanged(ListSelectionEvent e) {
			}
		});

		this.playList.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (columnBeingMoved != -1) {
					// Test if first column has been moved, and undo movement
					if (columnBeingMoved == 0 || columnMovedTo == 0) {
						// Swap columns
						PlayListColumnModel.this.playList.moveColumn(columnMovedTo, columnBeingMoved);
					} else {
						// Swap order in model

						// Column moved to right
						if (columnBeingMoved < columnMovedTo) {
							int columnDestinyOrder = PlayListColumnModel.this.model.getColumn(columnMovedTo).getOrder();
							for (int i = columnBeingMoved + 1; i <= columnMovedTo; i++) {
								int order = PlayListColumnModel.this.model.getColumn(i).getOrder();
								PlayListColumnModel.this.model.getColumn(i).setOrder(order - 1);
							}
							PlayListColumnModel.this.model.getColumn(columnBeingMoved).setOrder(columnDestinyOrder);
						}
						// Column moved to left
						else if (columnBeingMoved > columnMovedTo) {
							int columnDestinyOrder = PlayListColumnModel.this.model.getColumn(columnMovedTo).getOrder();
							for (int i = columnBeingMoved - 1; i >= columnMovedTo; i--) {
								int order = PlayListColumnModel.this.model.getColumn(i).getOrder();
								PlayListColumnModel.this.model.getColumn(i).setOrder(order + 1);
							}
							PlayListColumnModel.this.model.getColumn(columnBeingMoved).setOrder(columnDestinyOrder);
						}
					}
					PlayListColumnModel.this.model.arrangeColumns(false);
				}
				columnBeingMoved = -1;
				columnMovedTo = -1;
			}
		});
	}

	// When a new column is added, set properties based on model
	@Override
	public void addColumn(TableColumn aColumn) {
		super.addColumn(aColumn);

		// Get column data
		Column column = model.getColumn(aColumn.getModelIndex());

		// Set preferred width
		aColumn.setPreferredWidth(column.getWidth());

		// Set resizable
		aColumn.setResizable(column.isResizable());

		// If has cell editor, set cell editor
		TableCellEditor cellEditor = column.getCellEditor();
		if (cellEditor != null) {
			aColumn.setCellEditor(cellEditor);
		}

		// If has renderer, set cell renderer
		TableCellRenderer cellRenderer = column.getCellRenderer();
		if (cellRenderer != null) {
			aColumn.setCellRenderer(cellRenderer);
		}
	}

	/**
	 * Return column for x position
	 * 
	 * @param x
	 * @return
	 */
	public int getColumnIndexAtPosition(int x) {
		if (x < 0)
			return -1;

		for (int column = 0; column < getColumnCount(); column++) {
			x = x - getColumn(column).getPreferredWidth();
			if (x < 0)
				return column;
		}

		return -1;
	}

}
