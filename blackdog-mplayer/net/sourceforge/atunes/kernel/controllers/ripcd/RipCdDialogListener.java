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

package net.sourceforge.atunes.kernel.controllers.ripcd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;

import net.sourceforge.atunes.gui.views.dialogs.RipCdDialog;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.RipperHandler;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class RipCdDialogListener extends KeyAdapter implements ActionListener {

	private RipCdDialog ripCdDialog;
	private RipCdDialogController ripCdDialogController;

	public RipCdDialogListener(RipCdDialog ripCdDialog, RipCdDialogController ripCdDialogController) {
		this.ripCdDialog = ripCdDialog;
		this.ripCdDialogController = ripCdDialogController;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ripCdDialog.getOk()) {
			ripCdDialogController.setCancelled(false);
			ripCdDialogController.setArtist(ripCdDialog.getArtistTextField().getText());
			ripCdDialogController.setAlbum(ripCdDialog.getAlbumTextField().getText());
			try {
				ripCdDialogController.setYear(Integer.parseInt(ripCdDialog.getYearTextField().getText()));
			} catch (NumberFormatException ex) {
				ripCdDialogController.setYear(DateUtils.getCurrentYear());
			}
			ripCdDialogController.setGenre(ripCdDialog.getGenreComboBox().getSelectedItem().toString());
			ripCdDialogController.setFolder(ripCdDialog.getFolderName().getText());
			ripCdDialog.setVisible(false);
		} else if (e.getSource() == ripCdDialog.getCancel()) {
			ripCdDialogController.setCancelled(true);
			ripCdDialog.setVisible(false);
		} else if (e.getSource() == ripCdDialog.getFolderSelectionButton()) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = chooser.showDialog(ripCdDialog, LanguageTool.getString("SELECT_FOLDER"));
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File selectedPath = chooser.getSelectedFile();
				ripCdDialog.getFolderName().setText(selectedPath.getAbsolutePath());
				ripCdDialogController.setFolderNameEdited(true);
			}
		} else if (e.getSource() == ripCdDialog.getFormat()) {
			if (ripCdDialog.getFormat().getSelectedItem().equals("MP3")) {
				ripCdDialog.getQualityComboBox().setEnabled(true);
				ripCdDialog.getQualityComboBox().setModel(new DefaultComboBoxModel(RipCdDialogController.MP3_QUALITY));
				ripCdDialog.getQualityComboBox().setSelectedItem(RipCdDialogController.DEFAULT_MP3_QUALITY);
			} else if (ripCdDialog.getFormat().getSelectedItem().equals("MP4")) {
				ripCdDialog.getQualityComboBox().setEnabled(true);
				ripCdDialog.getQualityComboBox().setModel(new DefaultComboBoxModel(RipCdDialogController.MP4_QUALITY));
				ripCdDialog.getQualityComboBox().setSelectedItem(RipCdDialogController.DEFAULT_MP4_QUALITY);
			} else if (ripCdDialog.getFormat().getSelectedItem().equals("FLAC")) {
				ripCdDialog.getQualityComboBox().setEnabled(true);
				ripCdDialog.getQualityComboBox().setModel(new DefaultComboBoxModel(RipCdDialogController.FLAC_QUALITY));
				ripCdDialog.getQualityComboBox().setSelectedItem(RipCdDialogController.DEFAULT_FLAC_QUALITY);
			} else if (ripCdDialog.getFormat().getSelectedItem().equals("OGG")) {
				ripCdDialog.getQualityComboBox().setEnabled(true);
				ripCdDialog.getQualityComboBox().setModel(new DefaultComboBoxModel(RipCdDialogController.OGG_QUALITY));
				ripCdDialog.getQualityComboBox().setSelectedItem(RipCdDialogController.DEFAULT_OGG_QUALITY);
			} else if (ripCdDialog.getFormat().getSelectedItem().equals("WAV")) {
				ripCdDialog.getQualityComboBox().setEnabled(false);
			}
			// First load the previously used settings.
			if (!ripCdDialogController.isEncoderSettingChanged()) {
				ripCdDialog.getFormat().setSelectedItem(RipperHandler.getInstance().getEncoder());
				// Display quality settings matching the encoder
				if (RipperHandler.getInstance().getEncoder().equals("OGG"))
					ripCdDialog.getQualityComboBox().setModel(new DefaultComboBoxModel(RipCdDialogController.OGG_QUALITY));
				else if (RipperHandler.getInstance().getEncoder().equals("MP3"))
					ripCdDialog.getQualityComboBox().setModel(new DefaultComboBoxModel(RipCdDialogController.MP3_QUALITY));
				else if (RipperHandler.getInstance().getEncoder().equals("FLAC"))
					ripCdDialog.getQualityComboBox().setModel(new DefaultComboBoxModel(RipCdDialogController.FLAC_QUALITY));
				else if (RipperHandler.getInstance().getEncoder().equals("MP4"))
					ripCdDialog.getQualityComboBox().setModel(new DefaultComboBoxModel(RipCdDialogController.MP4_QUALITY));
				else
					ripCdDialog.getQualityComboBox().setSelectedItem(RipperHandler.getInstance().getEncoderQuality());
				// Set true so user can change encoder format.
				ripCdDialogController.setEncoderSettingChanged(true);
			}
		} else if (e.getSource() == ripCdDialog.getFilePattern()) {
			// The list contains already what to do so just assign the value
			RipperHandler.getInstance().setFileNamePattern((String) ripCdDialog.getFilePattern().getSelectedItem());
			if (!ripCdDialogController.isEncoderSettingChanged()) {
				ripCdDialog.getFilePattern().setSelectedItem(RipperHandler.getInstance().getFileNamePattern());
			}
		} else if (e.getSource() == ripCdDialog.getAmazonButton()) {
			RipperHandler.getInstance().fillSongsFromAmazon(ripCdDialog.getArtistTextField().getText(), ripCdDialog.getAlbumTextField().getText());
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getSource() == ripCdDialog.getFolderName() || e.getSource() == ripCdDialog.getAlbumTextField()) {
			ripCdDialogController.setFolderNameEdited(true);
		} else if (e.getSource() == ripCdDialog.getArtistTextField()) {
			String artist = ripCdDialog.getArtistTextField().getText();
			String album = ripCdDialog.getAlbumTextField().getText();
			String repositoryPath = RepositoryHandler.getInstance().getRepositoryPath();
			boolean enabled = !artist.equals("") && !album.equals("");
			ripCdDialog.getAmazonButton().setEnabled(enabled);
			if (!ripCdDialogController.isFolderNameEdited()) {
				if (enabled)
					ripCdDialog.getFolderName().setText(StringUtils.getString(repositoryPath, SystemProperties.fileSeparator, artist, SystemProperties.fileSeparator, album));
				else if (artist.equals(""))
					ripCdDialog.getFolderName().setText(StringUtils.getString(repositoryPath, SystemProperties.fileSeparator, album));
				else
					ripCdDialog.getFolderName().setText(StringUtils.getString(repositoryPath, SystemProperties.fileSeparator, artist));
			}
		}
	}

}
