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

package net.sourceforge.atunes.gui.views.dialogs.properties;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

class RadioPropertiesDialog extends PropertiesDialog {

	private static final long serialVersionUID = -73744354419152730L;

	private JLabel pictureLabel;
	private JLabel titleLabel;
	private JLabel urlLabel;
	private JLabel bitrateLabel;
	private JLabel frequencyLabel;

	private Radio radio;

	RadioPropertiesDialog(Radio radio) {
		super(getTitleText(radio));
		this.radio = radio;
		addContent();

		setContent();

		GuiUtils.applyComponentOrientation(this);
	}

	/**
	 * Gives a title for dialog
	 * 
	 * @param radio
	 * @return title for dialog
	 */
	private static String getTitleText(Radio radio) {
		return StringUtils.getString(LanguageTool.getString("INFO_OF_RADIO"), " ", radio.getName());
	}

	private void addContent() {
		JPanel panel = new JPanel(new GridBagLayout());

		pictureLabel = new JLabel();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.insets = new Insets(40, 10, 5, 10);
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.VERTICAL;
		panel.add(pictureLabel, c);

		titleLabel = new JLabel();
		titleLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(titleLabel, c);

		urlLabel = new JLabel();
		urlLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(5, 10, 5, 10);
		panel.add(urlLabel, c);

		bitrateLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 2;
		panel.add(bitrateLabel, c);

		frequencyLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 3;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTH;
		panel.add(frequencyLabel, c);

		add(panel);
	}

	private void fillPicture() {
		ImageIcon picture = ImageLoader.RADIO;
		pictureLabel.setPreferredSize(new Dimension(picture.getIconWidth(), picture.getIconHeight()));
		pictureLabel.setIcon(picture);
		pictureLabel.setVisible(true);
	}

	private void setContent() {
		fillPicture();
		titleLabel.setText(getHtmlFormatted(LanguageTool.getString("NAME"), radio.getName()));
		urlLabel.setText(getHtmlFormatted(LanguageTool.getString("URL"), radio.getUrl()));
		bitrateLabel
				.setText(getHtmlFormatted(LanguageTool.getString("BITRATE"), radio.getBitrate() > 0 ? StringUtils.getString(String.valueOf(radio.getBitrate()), " kbps") : "-"));
		frequencyLabel.setText(getHtmlFormatted(LanguageTool.getString("FREQUENCY"), radio.getFrequency() > 0 ? StringUtils.getString(String.valueOf(radio.getFrequency()), " Hz")
				: "-"));
	}
}
