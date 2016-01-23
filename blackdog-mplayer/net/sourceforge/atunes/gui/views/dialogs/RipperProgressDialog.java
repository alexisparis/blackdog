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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class RipperProgressDialog extends CustomFrame {

	private static final long serialVersionUID = -3891515847607545757L;

	private JLabel cover;
	private JProgressBar totalProgressBar;
	private JLabel totalProgressValueLabel;
	private JProgressBar decodeProgressBar;
	private JLabel decodeProgressValueLabel;
	private JProgressBar encodeProgressBar;
	private JLabel encodeProgressValueLabel;
	private JButton cancelButton;

	public RipperProgressDialog() {
		super(LanguageTool.getString("RIPPING_CD"));
		setSize(420, 180);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setContent();
		GuiUtils.applyComponentOrientation(this);
	}

	public static void main(String[] args) {
		new RipperProgressDialog().setVisible(true);
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	public void setArtistAndAlbum(String artist, String album) {
		setTitle(StringUtils.getString(LanguageTool.getString("RIPPING_CD"), " ", artist, " - ", album));
	}

	private void setContent() {
		JPanel panel = new JPanel(new GridBagLayout());

		cover = new JLabel(ImageLoader.CD_AUDIO);

		JLabel totalProgressLabel = new JLabel(LanguageTool.getString("TOTAL_PROGRESS"));
		totalProgressBar = new JProgressBar();
		totalProgressBar.setPreferredSize(new Dimension(10, 12));
		totalProgressValueLabel = new JLabel();
		JLabel decodeProgressLabel = new JLabel(LanguageTool.getString("DECODING"));
		decodeProgressBar = new JProgressBar();
		decodeProgressBar.setPreferredSize(new Dimension(10, 12));
		decodeProgressValueLabel = new JLabel();
		JLabel encodeProgressLabel = new JLabel(LanguageTool.getString("ENCODING"));
		encodeProgressBar = new JProgressBar();
		encodeProgressBar.setPreferredSize(new Dimension(10, 12));
		encodeProgressLabel.setBorder(BorderFactory.createEmptyBorder());
		encodeProgressValueLabel = new JLabel();
		cancelButton = new CustomButton(null, LanguageTool.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 6;
		c.insets = new Insets(10, 20, 0, 20);
		panel.add(cover, c);

		c.gridx = 1;
		c.weightx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(20, 0, 0, 20);
		panel.add(totalProgressLabel, c);
		c.gridx = 2;
		c.weightx = 0;
		c.anchor = GridBagConstraints.EAST;
		panel.add(totalProgressValueLabel, c);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 20);
		panel.add(totalProgressBar, c);

		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(3, 0, 0, 20);
		panel.add(decodeProgressLabel, c);
		c.gridx = 2;
		c.weightx = 0;
		c.anchor = GridBagConstraints.EAST;
		panel.add(decodeProgressValueLabel, c);
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 20);
		panel.add(decodeProgressBar, c);

		c.gridy = 4;
		c.gridwidth = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(3, 0, 0, 20);
		panel.add(encodeProgressLabel, c);
		c.gridx = 2;
		c.weightx = 0;
		c.anchor = GridBagConstraints.EAST;
		panel.add(encodeProgressValueLabel, c);
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 20);
		panel.add(encodeProgressBar, c);

		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(10, 0, 5, 0);
		panel.add(cancelButton, c);

		add(panel);
	}

	public void setCover(Image img) {
		cover.setIcon(ImageUtils.resize(new ImageIcon(img), 90, 90));
	}

	public void setDecodeProgressBarLimits(int min, int max) {
		setLimits(decodeProgressBar, min, max);
	}

	public void setDecodeProgressValue(int value) {
		decodeProgressBar.setValue(value);
	}

	public void setDecodeProgressValue(String value) {
		decodeProgressValueLabel.setText(value);
	}

	public void setEncodeProgressBarLimits(int min, int max) {
		setLimits(encodeProgressBar, min, max);
	}

	public void setEncodeProgressValue(int value) {
		encodeProgressBar.setValue(value);
	}

	public void setEncodeProgressValue(String value) {
		encodeProgressValueLabel.setText(value);
	}

	private void setLimits(JProgressBar progressBar, int min, int max) {
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
	}

	public void setTotalProgressBarLimits(int min, int max) {
		setLimits(totalProgressBar, min, max);
	}

	public void setTotalProgressValue(int value) {
		totalProgressBar.setValue(value);
	}

	public void setTotalProgressValue(String value) {
		totalProgressValueLabel.setText(value);
	}

}
