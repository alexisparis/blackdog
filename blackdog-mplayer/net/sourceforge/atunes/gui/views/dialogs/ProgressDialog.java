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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
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
import net.sourceforge.atunes.utils.StringUtils;

/**
 * @author fleax
 * 
 */
public class ProgressDialog extends CustomModalDialog {

	private static final long serialVersionUID = -3071934230042256578L;

	private JLabel pictureLabel;
	private JLabel label;
	private JLabel progressLabel;
	private JLabel separatorLabel;
	private JLabel totalFilesLabel;
	private JProgressBar progressBar;
	private JLabel folderLabel;
	private JLabel remainingTimeLabel;
	private JButton cancelButton;

	private MouseListener listener = new MouseListener() {
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	};

	public ProgressDialog(JFrame parent) {
		super(parent, 400, 150, false);
		setContent(getContent());
		GuiUtils.applyComponentOrientation(this);
		cancelButton.setVisible(false);
	}

	public static void main(String[] args) {
		new ProgressDialog(null).setVisible(true);
	}

	public void activateGlassPane() {
		((JFrame) getParent()).getGlassPane().setVisible(true);
		((JFrame) getParent()).getGlassPane().addMouseListener(listener);
	}

	private void clear() {
		getLabel().setText(StringUtils.getString(LanguageTool.getString("LOADING"), "..."));
		getFolderLabel().setText(" ");
		getProgressBar().setValue(0);
		getProgressLabel().setText("");
		getTotalFilesLabel().setText("");
		getProgressBar().setIndeterminate(true);
		getRemainingTimeLabel().setText(" ");
	}

	public void deactivateGlassPane() {
		((JFrame) getParent()).getGlassPane().removeMouseListener(listener);
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	private JPanel getContent() {
		JPanel panel = new JPanel(new GridBagLayout());
		pictureLabel = new JLabel(ImageLoader.APP_ICON_TITLE);
		label = new JLabel(StringUtils.getString(LanguageTool.getString("LOADING"), "..."));
		Font f = label.getFont().deriveFont(Font.PLAIN);
		label.setFont(f);
		progressLabel = new JLabel();
		progressLabel.setFont(f);
		separatorLabel = new JLabel(" / ");
		separatorLabel.setFont(f);
		totalFilesLabel = new JLabel();
		totalFilesLabel.setFont(f);
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBorder(BorderFactory.createEmptyBorder());
		folderLabel = new JLabel(" ");
		folderLabel.setFont(f);
		remainingTimeLabel = new JLabel(" ");
		remainingTimeLabel.setFont(f);
		cancelButton = new CustomButton(null, LanguageTool.getString("CANCEL"));
		cancelButton.setFont(f);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 5;
		c.insets = new Insets(0, 20, 0, 0);
		panel.add(pictureLabel, c);
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 20, 0, 20);
		c.anchor = GridBagConstraints.WEST;
		panel.add(label, c);
		c.gridx = 2;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5, 0, 0, 3);
		c.anchor = GridBagConstraints.EAST;
		panel.add(progressLabel, c);
		c.gridx = 3;
		c.insets = new Insets(5, 0, 0, 0);
		panel.add(separatorLabel, c);
		c.gridx = 4;
		c.insets = new Insets(5, 0, 0, 20);
		panel.add(totalFilesLabel, c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 20, 5, 20);
		c.anchor = GridBagConstraints.CENTER;
		panel.add(progressBar, c);
		c.gridy = 2;
		c.insets = new Insets(0, 20, 0, 20);
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.WEST;
		panel.add(folderLabel, c);
		c.gridy = 3;
		c.insets = new Insets(0, 20, 10, 20);
		panel.add(remainingTimeLabel, c);
		c.gridy = 4;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(cancelButton, c);
		return panel;
	}

	public JLabel getFolderLabel() {
		return folderLabel;
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

	public JLabel getRemainingTimeLabel() {
		return remainingTimeLabel;
	}

	public JLabel getTotalFilesLabel() {
		return totalFilesLabel;
	}

	public void setCancelButtonEnabled(boolean enabled) {
		cancelButton.setEnabled(enabled);
	}

	public void setCancelButtonVisible(boolean visible) {
		cancelButton.setVisible(visible);
	}

	@Override
	public void setVisible(final boolean b) {
		super.setVisible(b);
		clear();
	}

}
