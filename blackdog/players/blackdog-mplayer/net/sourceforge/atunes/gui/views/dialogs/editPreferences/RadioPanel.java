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

import net.sourceforge.atunes.utils.LanguageTool;

public class RadioPanel extends PreferencesPanel {

	private static final long serialVersionUID = 4489293347321979288L;

	private JCheckBox readInfoFromRadioStream;

	public RadioPanel() {
		super();
		readInfoFromRadioStream = new JCheckBox(LanguageTool.getString("READ_INFO_FROM_RADIO_STREAM"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		add(readInfoFromRadioStream, c);
	}

	@Override
	public Map<String, Object> getResult() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(LanguageTool.getString("READ_INFO_FROM_RADIO_STREAM"), readInfoFromRadioStream.isSelected());
		return result;
	}

	public void setReadInfoFromRadioStream(boolean animate) {
		readInfoFromRadioStream.setSelected(animate);
	}

}
