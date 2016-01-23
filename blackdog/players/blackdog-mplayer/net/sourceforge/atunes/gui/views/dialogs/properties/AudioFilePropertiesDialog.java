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
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

class AudioFilePropertiesDialog extends PropertiesDialog {

	private static final long serialVersionUID = 7504320983331038543L;

	private static final Logger logger = new Logger();

	private JLabel pictureLabel;
	private JLabel fileNameLabel;
	private JLabel pathLabel;
	private JLabel songLabel;
	private JLabel artistLabel;
	private JLabel albumArtistLabel;
	private JLabel composerLabel;
	private JLabel albumLabel;
	private JLabel durationLabel;
	private JLabel trackLabel;
	private JLabel yearLabel;
	private JLabel genreLabel;
	private JLabel bitrateLabel;
	private JLabel frequencyLabel;

	private AudioFile file;

	AudioFilePropertiesDialog(AudioFile file) {
		super(getTitleText(file));
		this.file = file;
		addContent();

		setContent();

		GuiUtils.applyComponentOrientation(this);
	}

	/**
	 * Gives a title for dialog
	 * 
	 * @param file
	 * @return title for dialog
	 */
	private static String getTitleText(AudioFile file) {
		return StringUtils.getString(LanguageTool.getString("INFO_OF_FILE"), " ", file.getName());
	}

	private void addContent() {
		JPanel panel = new JPanel(new GridBagLayout());

		pictureLabel = new JLabel();
		pictureLabel.setPreferredSize(new Dimension(Constants.FILE_PROPERTIES_DIALOG_IMAGE_WIDTH, Constants.FILE_PROPERTIES_DIALOG_IMAGE_HEIGHT));
		pictureLabel.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 4;
		c.insets = new Insets(5, 10, 5, 10);
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.VERTICAL;
		panel.add(pictureLabel, c);

		songLabel = new JLabel();
		songLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(songLabel, c);

		artistLabel = new JLabel();
		artistLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
		c.gridx = 1;
		c.gridy = 1;
		panel.add(artistLabel, c);

		albumArtistLabel = new JLabel();
		albumArtistLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
		c.gridx = 1;
		c.gridy = 2;
		panel.add(albumArtistLabel, c);

		albumLabel = new JLabel();
		albumLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
		c.gridx = 1;
		c.gridy = 3;
		panel.add(albumLabel, c);

		fileNameLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 4;
		panel.add(fileNameLabel, c);

		pathLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 5;
		panel.add(pathLabel, c);

		durationLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 6;
		panel.add(durationLabel, c);

		trackLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 7;
		panel.add(trackLabel, c);

		genreLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 8;
		panel.add(genreLabel, c);

		yearLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 9;
		panel.add(yearLabel, c);

		composerLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 10;
		panel.add(composerLabel, c);

		bitrateLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 11;
		panel.add(bitrateLabel, c);

		frequencyLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 12;
		panel.add(frequencyLabel, c);

		add(panel);
	}

	private void fillPicture() {
		new SwingWorker<ImageIcon, Void>() {
			@Override
			protected ImageIcon doInBackground() throws Exception {
				return AudioFilePictureUtils.getImageForAudioFile(file, Constants.FILE_PROPERTIES_DIALOG_IMAGE_WIDTH, Constants.FILE_PROPERTIES_DIALOG_IMAGE_HEIGHT);
			}

			@Override
			protected void done() {
				ImageIcon cover;
				try {
					cover = get();
					if (cover != null) {
						pictureLabel.setIcon(cover);
					} else {
						pictureLabel.setIcon(ImageLoader.NO_COVER_AUDIOFILE_PROPERTIES);
					}
					pictureLabel.setVisible(true);
				} catch (InterruptedException e) {
					logger.error(LogCategories.IMAGE, e);
				} catch (ExecutionException e) {
					logger.error(LogCategories.IMAGE, e);
				}
			}
		}.execute();

	}

	private void setContent() {
		fillPicture();
		songLabel.setText(getHtmlFormatted(LanguageTool.getString("SONG"), file.getTitle()));
		artistLabel.setText(getHtmlFormatted(LanguageTool.getString("ARTIST"), file.getArtist()));
		albumArtistLabel.setText(getHtmlFormatted(LanguageTool.getString("ALBUM_ARTIST"), file.getAlbumArtist()));
		albumLabel.setText(getHtmlFormatted(LanguageTool.getString("ALBUM"), file.getAlbum()));
		fileNameLabel.setText(getHtmlFormatted(LanguageTool.getString("FILE"), file.getName()));
		pathLabel.setText(getHtmlFormatted(LanguageTool.getString("LOCATION"), file.getParent()));
		durationLabel.setText(getHtmlFormatted(LanguageTool.getString("DURATION"), TimeUtils.seconds2String(file.getDuration())));
		trackLabel.setText(getHtmlFormatted(LanguageTool.getString("TRACK"), file.getTrackNumber() > 0 ? file.getTrackNumber().toString() : ""));
		genreLabel.setText(getHtmlFormatted(LanguageTool.getString("GENRE"), file.getGenre()));
		yearLabel.setText(getHtmlFormatted(LanguageTool.getString("YEAR"), file.getYear()));
		composerLabel.setText(getHtmlFormatted(LanguageTool.getString("COMPOSER"), file.getComposer()));
		bitrateLabel.setText(getHtmlFormatted(LanguageTool.getString("BITRATE"), StringUtils.getString(Long.toString(file.getBitrate()), " Kbps")));
		frequencyLabel.setText(getHtmlFormatted(LanguageTool.getString("FREQUENCY"), StringUtils.getString(Integer.toString(file.getFrequency()), " Hz")));
	}

}
