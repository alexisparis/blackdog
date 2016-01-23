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

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.sourceforge.atunes.utils.LanguageTool;

public class LastFmPanel extends PreferencesPanel {

	private static final long serialVersionUID = -9216216930198145476L;

	private JTextField lastFmUser;
	private JPasswordField lastFmPassword;

	public LastFmPanel() {
		super();
		JLabel lastFmLabel = new JLabel(LanguageTool.getString("LASTFM_PREFERENCES"));
		// The keyword/string LASTFM_PREFERENCES_CONTINUED is only used for overlenght strings for
		// languages like greek. Do not use this string for languages that don't use this extra line
		int moreSpace = 0;
		if (LanguageTool.getString("LASTFM_PREFERENCES_CONTINUED") != "LASTFM_PREFERENCES_CONTINUED")
			moreSpace = 1;
		JLabel lastFmLabelExtraLong = new JLabel(LanguageTool.getString("LASTFM_PREFERENCES_CONTINUED"));
		JLabel userLabel = new JLabel(LanguageTool.getString("LASTFM_USER"));
		lastFmUser = new JTextField();
		JLabel passwordLabel = new JLabel(LanguageTool.getString("LASTFM_PASSWORD"));
		lastFmPassword = new JPasswordField();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 2;
		add(lastFmLabel, c);
		// Needed to make place for one more line for extralong translations

		if (moreSpace == 1) {
			c.gridy = 1;
			add(lastFmLabelExtraLong, c);
		}
		c.gridx = 0;
		c.gridy = 1 + moreSpace;
		c.gridwidth = 1;
		c.insets = new Insets(5, 2, 5, 2);
		add(userLabel, c);
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		add(lastFmUser, c);
		c.gridx = 0;
		c.gridy = 2 + moreSpace;
		c.weightx = 0;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTH;
		add(passwordLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		add(lastFmPassword, c);
	}

	@Override
	public Map<String, Object> getResult() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(LanguageTool.getString("LASTFM_USER"), lastFmUser.getText());
		result.put(LanguageTool.getString("LASTFM_PASSWORD"), new String(lastFmPassword.getPassword()));
		return result;
	}

	public void setLasFmPassword(String password) {
		lastFmPassword.setText(password);
	}

	public void setLastFmUser(String user) {
		lastFmUser.setText(user);
	}
}
