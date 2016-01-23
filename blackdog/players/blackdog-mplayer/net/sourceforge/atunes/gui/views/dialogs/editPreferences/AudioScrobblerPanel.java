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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;

public class AudioScrobblerPanel extends PreferencesPanel {

	private static final long serialVersionUID = -9216216930198145476L;

	private JCheckBox activateAudioScrobbler;
	private JCheckBox savePictures;
	private JButton clearCache;

	public AudioScrobblerPanel() {
		super();
		activateAudioScrobbler = new JCheckBox(LanguageTool.getString("ACTIVATE_AUDIO_SCROBBLER"));
		savePictures = new JCheckBox(LanguageTool.getString("SAVE_PICTURES_TO_AUDIO_FOLDERS"));
		activateAudioScrobbler.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				savePictures.setEnabled(activateAudioScrobbler.isSelected());
			}
		});
		clearCache = new JButton(LanguageTool.getString("CLEAR_CACHE"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		add(activateAudioScrobbler, c);
		c.gridy = 1;
		//c.weighty = 1;
		add(savePictures, c);
		c.gridy = 2;
		c.weightx = 0;
		c.weighty = 1;
		c.insets = new Insets(10, 0, 10, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GuiUtils.getComponentOrientation().isLeftToRight() ? GridBagConstraints.NORTHWEST : GridBagConstraints.NORTHEAST;
		add(clearCache, c);
	}

	public JButton getClearCache() {
		return clearCache;
	}

	@Override
	public Map<String, Object> getResult() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(LanguageTool.getString("ACTIVATE_AUDIO_SCROBBLER"), activateAudioScrobbler.isSelected());
		result.put(LanguageTool.getString("SAVE_PICTURES_TO_AUDIO_FOLDERS"), savePictures.isSelected());
		return result;
	}

	public void setActivateAudioScrobbler(boolean activate) {
		activateAudioScrobbler.setSelected(activate);
		savePictures.setEnabled(activate);
	}

	public void setSavePictures(boolean save) {
		savePictures.setSelected(save);
	}
}
