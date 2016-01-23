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

// Layout of the general panel

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.LookAndFeelSelector;
import net.sourceforge.atunes.gui.images.themes.ThemePreviewLoader;
import net.sourceforge.atunes.gui.views.Renderers;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.utils.LanguageTool;

public class GeneralPanel extends PreferencesPanel {

	private static final long serialVersionUID = -9216216930198145476L;
	private JCheckBox showTitle;
	private JComboBox windowType;
	private JComboBox language;
	private JCheckBox showIconTray;
	private JCheckBox showTrayPlayer;
	private JCheckBox useDefaultFont;
	JComboBox theme;
	JLabel themePreview;

	public GeneralPanel() {
		// Titles are in bold. Label order has been changed, so number don't match any more
		super();
		showTitle = new JCheckBox(LanguageTool.getString("SHOW_TITLE"));
		JLabel label = new JLabel(LanguageTool.getString("WINDOW_TYPE"));
		label.setFont(Fonts.GENERAL_FONT_BOLD);
		windowType = new JComboBox(new String[] { LanguageTool.getString("STANDARD_WINDOW"), LanguageTool.getString("MULTIPLE_WINDOW") });
		JLabel label2 = new JLabel(LanguageTool.getString("CHANGE_WINDOW_TYPE_ON_NEXT_START"));
		JLabel label3 = new JLabel(LanguageTool.getString("LANGUAGE"));
		label3.setFont(Fonts.GENERAL_FONT_BOLD);

		List<Locale> langs = LanguageTool.getLanguages();
		Locale[] array = langs.toArray(new Locale[langs.size()]);
		final Locale currentLocale = Kernel.getInstance().state.getLocale().getLocale();
		Arrays.sort(array, new Comparator<Locale>() {
			@Override
			public int compare(Locale l1, Locale l2) {
				return java.text.Collator.getInstance().compare(l1.getDisplayName(currentLocale), l2.getDisplayName(currentLocale));
			}
		});
		language = new JComboBox(array);
		language.setRenderer(Renderers.PREFERENCES_DIALOG_FLAG_RENDERER);

		JLabel label4 = new JLabel(LanguageTool.getString("CHANGE_LANGUAGE_ON_NEXT_START"));
		showIconTray = new JCheckBox(LanguageTool.getString("SHOW_TRAY_ICON"));
		showTrayPlayer = new JCheckBox(LanguageTool.getString("SHOW_TRAY_PLAYER"));
		JLabel label5 = new JLabel(LanguageTool.getString("THEME"));
		label5.setFont(Fonts.GENERAL_FONT_BOLD);
		useDefaultFont = new JCheckBox(LanguageTool.getString("USE_DEFAULT_FONT"));
		theme = new JComboBox(LookAndFeelSelector.getListOfThemes());
		theme.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String t = (String) theme.getSelectedItem();
				themePreview.setIcon(ThemePreviewLoader.getImage(t));
			}
		});
		themePreview = new JLabel(LanguageTool.getString("PREVIEW"));
		themePreview.setVerticalTextPosition(SwingConstants.TOP);
		themePreview.setHorizontalTextPosition(SwingConstants.CENTER);
		JLabel label6 = new JLabel(LanguageTool.getString("CHANGE_THEME_ON_NEXT_START"));

		GridBagConstraints c = new GridBagConstraints();
		// First display language
		c.gridy = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(5, 0, 0, 0);
		add(label3, c);
		c.gridx = 1;
		c.insets = new Insets(15, 0, 0, 0);
		add(language, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.insets = new Insets(5, 0, 0, 0);
		add(label4, c);
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(25, 0, 5, 0);
		add(label, c);
		c.gridwidth = 1;
		c.gridx = 1;
		c.insets = new Insets(30, 0, 0, 0);
		add(windowType, c);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.insets = new Insets(5, 0, 0, 0);
		add(label2, c);
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 1;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 0, 0);
		add(useDefaultFont, c);
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 1;
		c.insets = new Insets(5, 0, 0, 0);
		add(showTitle, c);
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 0, 0);
		add(showTrayPlayer, c);
		c.gridx = 1;
		c.gridy = 5;
		c.insets = new Insets(5, 0, 0, 0);
		add(showIconTray, c);
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 2;
		c.insets = new Insets(20, 0, 0, 0);
		add(label5, c);
		c.gridx = 0;
		c.gridy = 7;
		c.weighty = 1;
		c.insets = new Insets(5, 0, 0, 0);
		add(themePreview, c);
		c.gridx = 1;
		c.weighty = 1;
		c.insets = new Insets(40, 0, 0, 0);
		add(theme, c);
		c.gridy = 8;
		c.weighty = 0;
		c.gridx = 0;
		add(label6, c);
	}

	@Override
	public Map<String, Object> getResult() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(LanguageTool.getString("SHOW_TITLE"), showTitle.isSelected());
		result.put(LanguageTool.getString("WINDOW_TYPE"), windowType.getSelectedItem());
		result.put(LanguageTool.getString("LANGUAGE"), language.getSelectedItem());
		result.put(LanguageTool.getString("SHOW_TRAY_ICON"), showIconTray.isSelected());
		result.put(LanguageTool.getString("SHOW_TRAY_PLAYER"), showTrayPlayer.isSelected());
		result.put(LanguageTool.getString("THEME"), theme.getSelectedItem());
		result.put(LanguageTool.getString("USE_DEFAULT_FONT"), useDefaultFont.isSelected());
		return result;
	}

	public void setLanguage(Locale language) {
		this.language.setSelectedItem(language);
	}

	public void setShowIconTray(boolean show) {
		this.showIconTray.setSelected(show);
	}

	public void setShowTitle(boolean show) {
		showTitle.setSelected(show);
	}

	public void setShowTrayPlayer(boolean show) {
		this.showTrayPlayer.setSelected(show);
	}

	public void setTheme(String t) {
		theme.setSelectedItem(t);
		themePreview.setIcon(ThemePreviewLoader.getImage(t));
	}

	public void setUseDefaultFont(boolean use) {
		useDefaultFont.setSelected(use);
	}

	public void setWindowType(String type) {
		windowType.setSelectedItem(type);
	}
}
