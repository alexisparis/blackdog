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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.Renderers;
import net.sourceforge.atunes.kernel.handlers.HotkeyHandler;
import net.sourceforge.atunes.utils.LanguageTool;

public class PlayerPanel extends PreferencesPanel {

	private static class HotkeyTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -5726677745418003289L;

		private String[][] data;

		public HotkeyTableModel() {
			if (HotkeyHandler.areHotkeysAndIntellitypeSupported()) {
				data = HotkeyHandler.getInstance().getHotkeyDescription();
			}
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int column) {
			return column == 0 ? LanguageTool.getString("HOTKEY") : LanguageTool.getString("ACTION");
		}

		@Override
		public int getRowCount() {
			return data != null ? data.length : 0;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return data != null ? data[rowIndex][columnIndex] : "";
		}

	}

	private static final long serialVersionUID = 4489293347321979288L;
	private JCheckBox useNormalisation;
	private JCheckBox playAtStartup;
	private JCheckBox useShortPathNames;
	private JCheckBox enableGlobalHotkeys;

	private JTable hotkeyTable;

	public PlayerPanel() {
		super();
		useNormalisation = new JCheckBox(LanguageTool.getString("USE_NORMALISATION"));
		playAtStartup = new JCheckBox(LanguageTool.getString("PLAY_AT_STARTUP"));
		useShortPathNames = new JCheckBox(LanguageTool.getString("USE_SHORT_PATH_NAMES_FOR_MPLAYER"));
		enableGlobalHotkeys = new JCheckBox(LanguageTool.getString("ENABLE_GLOBAL_HOTKEYS"));
		hotkeyTable = new JTable();
		hotkeyTable.setModel(new HotkeyTableModel());
		hotkeyTable.getTableHeader().setReorderingAllowed(false);
		hotkeyTable.getTableHeader().setResizingAllowed(false);
		hotkeyTable.setEnabled(HotkeyHandler.areHotkeysAndIntellitypeSupported());
		hotkeyTable.setDefaultRenderer(Object.class, Renderers.HOTKEY_RENDERER);
		JScrollPane scrollPane = new JScrollPane(hotkeyTable);
		scrollPane.setMinimumSize(new Dimension(400, 200));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		add(useNormalisation, c);
		c.gridy = 1;
		add(playAtStartup, c);
		c.gridy = 2;
		add(useShortPathNames, c);
		c.gridy = 3;
		add(enableGlobalHotkeys, c);
		c.gridy = 4;
		c.weighty = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GuiUtils.getComponentOrientation().isLeftToRight() ? GridBagConstraints.NORTHWEST : GridBagConstraints.NORTHEAST;
		c.insets = new Insets(10, 20, 10, 10);
		add(scrollPane, c);

	}

	public JCheckBox getEnableGlobalHotkeys() {
		return enableGlobalHotkeys;
	}

	@Override
	public Map<String, Object> getResult() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(LanguageTool.getString("USE_NORMALISATION"), useNormalisation.isSelected());
		result.put(LanguageTool.getString("PLAY_AT_STARTUP"), playAtStartup.isSelected());
		result.put(LanguageTool.getString("USE_SHORT_PATH_NAMES_FOR_MPLAYER"), useShortPathNames.isSelected());
		result.put(LanguageTool.getString("ENABLE_GOLBAL_HOTKEYS"), enableGlobalHotkeys.isSelected());
		return result;
	}

	public JCheckBox getUseShortPathNames() {
		return useShortPathNames;
	}

	public void setEnableHotkeys(boolean enable) {
		enableGlobalHotkeys.setSelected(enable);
	}

	public void setPlayAtStartup(boolean play) {
		playAtStartup.setSelected(play);
	}

	public void setUseNormalisation(boolean use) {
		useNormalisation.setSelected(use);
	}

	public void setUseShortPathNames(boolean use) {
		useShortPathNames.setSelected(use);
	}

}