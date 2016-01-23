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
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sourceforge.atunes.utils.LanguageTool;

public class OSDPanel extends PreferencesPanel {

	private static final long serialVersionUID = 4489293347321979288L;

	private JCheckBox animateOSD;
	private JComboBox osdDuration;
	private JCheckBox transparentOSD;

	public OSDPanel() {
		super();
		animateOSD = new JCheckBox(LanguageTool.getString("ANIMATE_OSD"));
		JLabel label = new JLabel(LanguageTool.getString("OSD_DURATION"));
		osdDuration = new JComboBox(new Integer[] { 2, 4, 6 });
		transparentOSD = new JCheckBox(LanguageTool.getString("TRANSPARENT_OSD"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		add(animateOSD, c);
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		add(transparentOSD, c);
		c.gridy = 2;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);
		add(label, c);
		c.gridx = 1;
		c.insets = new Insets(0, 0, 0, 0);
		add(osdDuration, c);
	}

	@Override
	public Map<String, Object> getResult() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(LanguageTool.getString("ANIMATE_OSD"), animateOSD.isSelected());
		result.put(LanguageTool.getString("OSD_DURATION"), osdDuration.getSelectedItem());
		result.put(LanguageTool.getString("TRANSPARENT_OSD"), transparentOSD.isSelected());
		return result;
	}

	public void setAnimateOSD(boolean animate) {
		animateOSD.setSelected(animate);
	}

	public void setOSDDuration(int time) {
		osdDuration.setSelectedItem(time);
	}

	public void setTransparentOSD(boolean transparent) {
		transparentOSD.setSelected(transparent);
	}

}
