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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.utils.LanguageTool;

public class EditTagDialog extends CustomModalDialog {

	private static final long serialVersionUID = 3395292301087643037L;

	private JLabel titleLabel;
	private JTextField titleTextField;
	private JLabel albumLabel;
	private JComboBox albumTextField;
	private JLabel artistLabel;
	private JComboBox artistTextField;
	private JLabel yearLabel;
	private JTextField yearTextField;
	private JLabel genreLabel;
	private JComboBox genreComboBox;
	private JLabel commentLabel;
	private JTextArea commentTextArea;
	private JLabel trackNumberLabel;
	private JTextField trackNumberTextField;
	private JLabel lyricsLabel;
	private JTextArea lyricsTextArea;
	private JLabel composerLabel;
	private JTextField composerTextField;
	private JLabel albumArtistLabel;
	private JTextField albumArtistTextField;

	private JButton okButton;
	private JButton cancelButton;

	public EditTagDialog(JFrame owner) {
		super(owner, 480, 560, true);
		setTitle(LanguageTool.getString("EDIT_TAG"));
		setResizable(false);
		add(getContent());
		setLocationRelativeTo(null);
		GuiUtils.applyComponentOrientation(this);
		enableCloseActionWithEscapeKey();
	}

	public static void main(String[] args) {
		new EditTagDialog(null).setVisible(true);
	}

	public JTextField getAlbumArtistTextField() {
		return albumArtistTextField;
	}

	public JLabel getAlbumLabel() {
		return albumLabel;
	}

	public JComboBox getAlbumTextField() {
		return albumTextField;
	}

	public JLabel getArtistLabel() {
		return artistLabel;
	}

	public JComboBox getArtistTextField() {
		return artistTextField;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	public JLabel getCommentLabel() {
		return commentLabel;
	}

	public JTextArea getCommentTextArea() {
		return commentTextArea;
	}

	public JLabel getComposerLabel() {
		return composerLabel;
	}

	public JTextField getComposerTextField() {
		return composerTextField;
	}

	private JPanel getContent() {
		JPanel panel = new JPanel(new GridBagLayout());
		titleLabel = new JLabel(LanguageTool.getString("TITLE"));
		titleTextField = new JTextField();
		albumLabel = new JLabel(LanguageTool.getString("ALBUM"));
		albumTextField = new JComboBox();
		albumTextField.setEditable(true);
		artistLabel = new JLabel(LanguageTool.getString("ARTIST"));
		artistTextField = new JComboBox();
		artistTextField.setEditable(true);
		yearLabel = new JLabel(LanguageTool.getString("YEAR"));
		yearTextField = new JTextField();
		genreLabel = new JLabel(LanguageTool.getString("GENRE"));
		genreComboBox = new JComboBox();
		genreComboBox.setEditable(true);
		commentLabel = new JLabel(LanguageTool.getString("COMMENT"));
		commentTextArea = new JTextArea();
		commentTextArea.setBorder(BorderFactory.createEmptyBorder());
		JScrollPane scrollPane = new JScrollPane(commentTextArea);
		//scrollPane.setBorder(BorderFactory.createEmptyBorder());
		lyricsLabel = new JLabel(LanguageTool.getString("LYRICS"));
		lyricsTextArea = new JTextArea();
		lyricsTextArea.setBorder(BorderFactory.createEmptyBorder());
		JScrollPane scrollPane2 = new JScrollPane(lyricsTextArea);
		//scrollPane2.setBorder(BorderFactory.createEmptyBorder());
		trackNumberLabel = new JLabel(LanguageTool.getString("TRACK"));
		trackNumberTextField = new JTextField();
		composerLabel = new JLabel(LanguageTool.getString("COMPOSER"));
		composerTextField = new JTextField();
		albumArtistLabel = new JLabel(LanguageTool.getString("ALBUM_ARTIST"));
		albumArtistTextField = new JTextField();

		okButton = new CustomButton(null, LanguageTool.getString("OK"));
		cancelButton = new CustomButton(null, LanguageTool.getString("CANCEL"));

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 2, 10);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		panel.add(titleLabel, c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		panel.add(titleTextField, c);

		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.insets = new Insets(2, 10, 2, 10);
		panel.add(albumArtistLabel, c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		panel.add(albumArtistTextField, c);

		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.insets = new Insets(2, 10, 2, 10);
		panel.add(artistLabel, c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		panel.add(artistTextField, c);

		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.gridheight = 1;
		panel.add(albumLabel, c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		panel.add(albumTextField, c);

		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		panel.add(yearLabel, c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		panel.add(yearTextField, c);

		c.gridx = 0;
		c.gridy = 5;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		panel.add(trackNumberLabel, c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		panel.add(trackNumberTextField, c);

		c.gridx = 0;
		c.gridy = 6;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		panel.add(genreLabel, c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		panel.add(genreComboBox, c);

		c.gridx = 0;
		c.gridy = 7;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.insets = new Insets(2, 10, 2, 10);
		panel.add(composerLabel, c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		panel.add(composerTextField, c);

		c.gridx = 0;
		c.gridy = 8;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		panel.add(commentLabel, c);

		c.gridx = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 2;
		panel.add(scrollPane, c);

		c.gridx = 0;
		c.gridy = 9;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		panel.add(lyricsLabel, c);

		c.gridx = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		panel.add(scrollPane2, c);

		JPanel auxPanel = new JPanel();
		auxPanel.setOpaque(false);
		auxPanel.add(okButton);
		auxPanel.add(cancelButton);

		c.gridx = 0;
		c.gridy = 10;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 0;
		panel.add(auxPanel, c);

		return panel;
	}

	public JComboBox getGenreComboBox() {
		return genreComboBox;
	}

	public JLabel getGenreLabel() {
		return genreLabel;
	}

	public JTextArea getLyricsTextArea() {
		return lyricsTextArea;
	}

	public JButton getOkButton() {
		return okButton;
	}

	public JLabel getTitleLabel() {
		return titleLabel;
	}

	public JTextField getTitleTextField() {
		return titleTextField;
	}

	public JLabel getTrackNumberLabel() {
		return trackNumberLabel;
	}

	public JTextField getTrackNumberTextField() {
		return trackNumberTextField;
	}

	public JLabel getYearLabel() {
		return yearLabel;
	}

	public JTextField getYearTextField() {
		return yearTextField;
	}

}
