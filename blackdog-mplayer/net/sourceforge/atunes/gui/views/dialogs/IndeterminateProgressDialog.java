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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * @author fleax
 * 
 */
public class IndeterminateProgressDialog extends CustomModalDialog {

	private static final long serialVersionUID = -3071934230042256578L;

	private JLabel pictureLabel;
	private JLabel label;
	private JProgressBar progressBar;

	public IndeterminateProgressDialog(JFrame parent) {
		super(parent, 400, 130, true);
		setContent(getContent());
		GuiUtils.applyComponentOrientation(this);
		setAlwaysOnTop(true);
	}

	public static void main(String[] args) {
		new IndeterminateProgressDialog(null).setVisible(true);
	}

	private JPanel getContent() {
		JPanel panel = new JPanel(new GridBagLayout());
		pictureLabel = new JLabel(ImageLoader.APP_ICON_TITLE);
		label = new JLabel(StringUtils.getString(LanguageTool.getString("PLEASE_WAIT"), "..."));
		Font f = label.getFont().deriveFont(Font.PLAIN);
		label.setFont(f);
		progressBar = new JProgressBar();
		progressBar.setBorder(BorderFactory.createEmptyBorder());
		progressBar.setIndeterminate(true);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.insets = new Insets(0, 20, 0, 0);
		panel.add(pictureLabel, c);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 20, 0, 20);
		c.anchor = GridBagConstraints.SOUTH;
		panel.add(label, c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 20, 5, 20);
		c.anchor = GridBagConstraints.NORTH;
		panel.add(progressBar, c);
		return panel;
	}
}
