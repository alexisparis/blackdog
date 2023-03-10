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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.utils.LanguageTool;

public class CopyProgressDialog extends CustomModalDialog {

	private static final long serialVersionUID = 2695343279468313536L;

	private JLabel pictureLabel;
	private JLabel label;
	private JLabel progressLabel;
	private JLabel separatorLabel;
	private JLabel totalFilesLabel;
	private JProgressBar progressBar;
	private JButton cancelButton;

	private String text;

	public CopyProgressDialog(JFrame parent, String text) {
		super(parent, 400, 100, false);
		this.text = text;
		setContent(getContent());
		GuiUtils.applyComponentOrientation(this);
	}

	public static void main(String[] args) {
		new CopyProgressDialog(null, "").setVisible(true);
	}

	private void clear() {
		getProgressBar().setValue(0);
		getProgressLabel().setText("");
		getTotalFilesLabel().setText("");
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	private JPanel getContent() {
		JPanel panel = new JPanel(new GridBagLayout());
		pictureLabel = new JLabel(ImageLoader.APP_ICON_BIG);
		label = new JLabel(text);
		progressLabel = new JLabel();
		separatorLabel = new JLabel(" / ");
		totalFilesLabel = new JLabel();
		progressBar = new JProgressBar();
		cancelButton = new CustomButton(null, LanguageTool.getString("CANCEL"));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 3;
		c.insets = new Insets(0, 20, 0, 0);
		panel.add(pictureLabel, c);
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 20, 0, 20);
		c.anchor = GridBagConstraints.WEST;
		panel.add(label, c);
		c.gridx = 2;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 0, 0, 3);
		c.anchor = GridBagConstraints.EAST;
		panel.add(progressLabel, c);
		c.gridx = 3;
		c.insets = new Insets(0, 0, 0, 3);
		panel.add(separatorLabel, c);
		c.gridx = 4;
		c.insets = new Insets(0, 0, 0, 20);
		panel.add(totalFilesLabel, c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 20, 5, 20);
		c.anchor = GridBagConstraints.CENTER;
		panel.add(progressBar, c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.VERTICAL;
		panel.add(cancelButton, c);
		return panel;
	}

	public JLabel getLabel() {
		return label;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JLabel getProgressLabel() {
		return progressLabel;
	}

	public JLabel getTotalFilesLabel() {
		return totalFilesLabel;
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (!b)
			clear();
	}
}
