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

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.sourceforge.atunes.utils.LanguageTool;

public class DevicePanel extends PreferencesPanel {

	private static final long serialVersionUID = 3331810461314007217L;

	private JTextField locationTextField;

	public DevicePanel() {
		super();
		JLabel label = new JLabel(LanguageTool.getString("DEVICE_DEFAULT_LOCATION"));
		locationTextField = new JTextField();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		add(label, c);
		c.gridx = 1;
		add(locationTextField, c);
	}

	@Override
	public Map<String, Object> getResult() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(LanguageTool.getString("DEVICE_DEFAULT_LOCATION"), locationTextField.getText());
		return result;
	}

	public void setDefaultDeviceLocation(String location) {
		locationTextField.setText(location);
	}

}
