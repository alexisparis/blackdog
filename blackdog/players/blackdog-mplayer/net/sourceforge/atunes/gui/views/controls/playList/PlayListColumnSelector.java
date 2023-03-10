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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.Renderers;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.utils.LanguageTool;

public class PlayListColumnSelector extends CustomModalDialog {

	private class ColumnsTableModel implements TableModel {

		private static final long serialVersionUID = 5251001708812824836L;
		private List<Column> columns;

		private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

		private ColumnsTableModel() {
			// Nothing to do
		}

		@Override
		public void addTableModelListener(TableModelListener l) {
			listeners.add(l);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : String.class;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int column) {
			return "";
		}

		@Override
		public int getRowCount() {
			if (this.columns != null)
				return this.columns.size();
			return 0;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0)
				return columns.get(rowIndex).isVisible();
			return LanguageTool.getString(columns.get(rowIndex).getColumnName());
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0;
		}

		public void moveDown(int columnPos) {
			// Get this column and previous
			Column columnSelected = columns.get(columnPos);
			Column nextColumn = columns.get(columnPos + 1);

			// Swap order
			int aux = columnSelected.getOrder();
			columnSelected.setOrder(nextColumn.getOrder());
			nextColumn.setOrder(aux);

			// Swap position on columns array
			columns.remove(nextColumn);
			columns.add(columnPos, nextColumn);

			TableModelEvent event;
			event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

			for (int i = 0; i < listeners.size(); i++)
				listeners.get(i).tableChanged(event);

			columnsList.getColumnModel().getColumn(0).setMaxWidth(20);

			columnsList.getSelectionModel().setSelectionInterval(columnPos + 1, columnPos + 1);
		}

		public void moveUp(int columnPos) {
			// Get this column and previous
			Column columnSelected = columns.get(columnPos);
			Column previousColumn = columns.get(columnPos - 1);

			// Swap order
			int aux = columnSelected.getOrder();
			columnSelected.setOrder(previousColumn.getOrder());
			previousColumn.setOrder(aux);

			// Swap position on columns array
			columns.remove(previousColumn);
			columns.add(columnPos, previousColumn);

			TableModelEvent event;
			event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

			for (int i = 0; i < listeners.size(); i++)
				listeners.get(i).tableChanged(event);

			columnsList.getColumnModel().getColumn(0).setMaxWidth(20);

			columnsList.getSelectionModel().setSelectionInterval(columnPos - 1, columnPos - 1);
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			listeners.remove(l);
		}

		public void setColumns(List<Column> columns) {
			this.columns = columns;
			Collections.sort(this.columns);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (columnIndex == 0)
				columns.get(rowIndex).setVisible((Boolean) aValue);
		}
	}

	private static final long serialVersionUID = -7592059207162524630L;
	private JTable columnsList;

	private ColumnsTableModel model;

	public PlayListColumnSelector(JFrame owner) {
		super(owner, 250, 300, true);
		setContent(getContent());
		GuiUtils.applyComponentOrientation(this);
	}

	public static void main(String[] args) {
		new PlayListColumnSelector(null).setVisible(true);
	}

	private JPanel getContent() {
		JPanel panel = new JPanel(new GridBagLayout());

		model = new ColumnsTableModel();

		columnsList = new JTable(model);
		columnsList.setShowGrid(false);
		columnsList.getColumnModel().getColumn(0).setMaxWidth(20);
		columnsList.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		columnsList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		columnsList.setDefaultRenderer(String.class, Renderers.PLAYLIST_COLUMN_SELECTOR_RENDERER);

		JScrollPane scrollPane = new JScrollPane(columnsList);
		JLabel label = new JLabel(LanguageTool.getString("SELECT_COLUMNS"));
		JButton okButton = new JButton(LanguageTool.getString("OK"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayListColumnSelector.this.setVisible(false);
			}
		});

		JButton upButton = new JButton(ImageLoader.GO_UP);
		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedColumn = columnsList.getSelectedRow();
				// If some column has been selected, not the first one
				if (selectedColumn > 0)
					model.moveUp(selectedColumn);
			}
		});

		JButton downButton = new JButton(ImageLoader.GO_DOWN);
		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedColumn = columnsList.getSelectedRow();
				// If some column has been selected, not the last one
				if (selectedColumn < columnsList.getModel().getRowCount() - 1)
					model.moveDown(selectedColumn);
			}
		});

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 10, 10);
		panel.add(label, c);

		c.gridy = 1;
		c.weighty = 1;
		c.gridheight = 2;
		panel.add(scrollPane, c);

		c.gridx = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTH;
		c.insets = new Insets(10, 0, 10, 10);
		panel.add(upButton, c);

		c.gridy = 2;
		c.anchor = GridBagConstraints.NORTH;
		panel.add(downButton, c);

		c.gridx = 0;
		c.gridy = 3;
		c.weighty = 0;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		panel.add(okButton, c);

		return panel;
	}

	public void setColumns(List<Column> columns) {
		model.setColumns(columns);
	}

}
