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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.utils.StringUtils;

public class SplashScreenDialog extends CustomModalDialog {

	private static final long serialVersionUID = -7279259267018738903L;

	public SplashScreenDialog() {
		super(null, 475, 200, false);
		setAlwaysOnTop(true);
		setContent(getContent());
		GuiUtils.applyComponentOrientation(this);
	}

	public static void main(String[] args) {
		new SplashScreenDialog().setVisible(true);
	}

	private JPanel getContent() {
		JPanel panel = new JPanel(null);
		panel.setOpaque(false);

		JLabel image = new JLabel(ImageLoader.APP_TITLE);
		image.setSize(new Dimension(475, 200));
		image.setLocation(0, 0);

		JLabel label = new JLabel(StringUtils.getString(Constants.APP_VERSION, "  ", new Character((char) 169), " ", Constants.APP_AUTHOR));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(Fonts.APP_VERSION_TITLE_FONT);
		label.setForeground(ColorDefinitions.TITLE_DIALOG_FONT_COLOR);
		label.setSize(475, 20);
		label.setLocation(0, 170);

		panel.add(label);
		panel.add(image);

		return panel;
	}
}
