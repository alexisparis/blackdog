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

import java.awt.GridBagConstraints;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sourceforge.atunes.utils.LanguageTool;

public class RepositoryPanel extends PreferencesPanel {

	private static final long serialVersionUID = 3331810461314007217L;

	private JComboBox refreshTime;
	private JCheckBox saveRepositoryAsXml;

	public RepositoryPanel() {
		super();
		JLabel label = new JLabel(LanguageTool.getString("REPOSITORY_REFRESH_TIME"));
		refreshTime = new JComboBox(new Integer[] { 0, 5, 10, 15, 30, 60 });
		saveRepositoryAsXml = new JCheckBox(LanguageTool.getString("SAVE_REPOSITORY_AS_XML"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		add(label, c);
		c.gridx = 1;
		add(refreshTime, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1;
		add(saveRepositoryAsXml, c);
	}

	@Override
	public Map<String, Object> getResult() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(LanguageTool.getString("REPOSITORY_REFRESH_TIME"), refreshTime.getSelectedItem());
		result.put(LanguageTool.getString("SAVE_REPOSITORY_AS_XML"), saveRepositoryAsXml.isSelected());
		return result;
	}

	public void setRefreshTime(int time) {
		refreshTime.setSelectedItem(time);
	}

	public void setSaveRepositoryAsXml(boolean b) {
		saveRepositoryAsXml.setSelected(b);
	}

}
