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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import net.sourceforge.atunes.gui.AutoCompleteFeature;
import net.sourceforge.atunes.gui.views.dialogs.RipCdDialog;
import net.sourceforge.atunes.kernel.controllers.model.DialogController;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.RipperHandler;
import net.sourceforge.atunes.kernel.modules.cdripper.CdRipper;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.kernel.utils.FileNameUtils;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

public class RipCdDialogController extends DialogController<RipCdDialog> {

	// Encoder options and file name patterns. Add here for more options
	static final String[] MP3_QUALITY = { "insane", "extreme", "medium", "standard", "128", "160", "192", "224", "256", "320" };
	static final String[] OGG_QUALITY = { "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
	static final String[] FLAC_QUALITY = { "-0", "-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8" };
	static final String[] MP4_QUALITY = { "50", "100", "150", "200", "250", "300", "350", "400", "450", "500" };
	public static final String[] FILENAMEPATTERN = { StringUtils.getString(CdRipper.TRACK_NUMBER, " - ", CdRipper.TITLE_PATTERN),
			StringUtils.getString(CdRipper.ARTIST_PATTERN, " - ", CdRipper.ALBUM_PATTERN, " - ", CdRipper.TRACK_NUMBER, " - ", CdRipper.TITLE_PATTERN),
			StringUtils.getString(CdRipper.ARTIST_PATTERN, " - ", CdRipper.TITLE_PATTERN) };

	// Default encoder "quality" settings.
	static final String DEFAULT_OGG_QUALITY = "5";
	static final String DEFAULT_MP3_QUALITY = "medium";
	static final String DEFAULT_FLAC_QUALITY = "-5";
	static final String DEFAULT_MP4_QUALITY = "200";

	private boolean folderNameEdited;
	private boolean cancelled;
	private boolean encoderSettingChanged;

	private String artist;
	private String album;
	private int year;
	private String genre;
	private String folder;

	public RipCdDialogController(RipCdDialog dialogControlled) {
		super(dialogControlled);
		addBindings();
	}

	@Override
	protected void addBindings() {

		// Add genres combo box items
		List<String> genresSorted = Arrays.asList(Tag.genres);
		Collections.sort(genresSorted);
		dialogControlled.getGenreComboBox().setModel(new ListComboBoxModel(genresSorted));

		// Get the encoder list
		List<String> encoderList = RipperHandler.getInstance().getInstalledEncoders();
		// We must process "List encoderList" to display it correctly in the dropdown menu. Encoders don't work otherwise.
		String[] avaibleEncoders = encoderList.toString().replace("]", "").replace(" ", "").replace("[", "").split(",");
		dialogControlled.getFormat().setModel(new DefaultComboBoxModel(avaibleEncoders));

		// Add autocompletion
		new AutoCompleteFeature(dialogControlled.getGenreComboBox());

		RipCdDialogListener listener = new RipCdDialogListener(dialogControlled, this);
		dialogControlled.getOk().addActionListener(listener);
		dialogControlled.getCancel().addActionListener(listener);
		dialogControlled.getFolderSelectionButton().addActionListener(listener);
		dialogControlled.getFormat().addActionListener(listener);
		dialogControlled.getFilePattern().addActionListener(listener);
		dialogControlled.getFolderName().addKeyListener(listener);
		dialogControlled.getAmazonButton().addActionListener(listener);
		dialogControlled.getArtistTextField().addKeyListener(listener);
		dialogControlled.getAlbumTextField().addKeyListener(listener);
	}

	@Override
	protected void addStateBindings() {
		// Nothing to to
	}

	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @return the folder
	 */
	public String getFolder() {
		return FileNameUtils.getValidFolderName(folder);
	}

	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @return the cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * @return the encoderSettingChanged
	 */
	public boolean isEncoderSettingChanged() {
		return encoderSettingChanged;
	}

	/**
	 * @return the folderNameEdited
	 */
	public boolean isFolderNameEdited() {
		return folderNameEdited;
	}

	@Override
	protected void notifyReload() {
		// Nothing to to
	}

	/**
	 * @param album
	 *            the album to set
	 */
	public void setAlbum(String album) {
		this.album = album;
	}

	/**
	 * @param artist
	 *            the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @param cancelled
	 *            the cancelled to set
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	/**
	 * @param encoderSettingChanged
	 *            the encoderSettingChanged to set
	 */
	public void setEncoderSettingChanged(boolean encoderSettingChanged) {
		this.encoderSettingChanged = encoderSettingChanged;
	}

	/**
	 * @param folder
	 *            the folder to set
	 */
	public void setFolder(String folder) {
		this.folder = folder;
	}

	/**
	 * @param folderNameEdited
	 *            the folderNameEdited to set
	 */
	public void setFolderNameEdited(boolean folderNameEdited) {
		this.folderNameEdited = folderNameEdited;
	}

	/**
	 * @param genre
	 *            the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	public void showCdInfo(CDInfo cdInfo, String path) {
		setArtist(cdInfo.getArtist());
		dialogControlled.getArtistTextField().setText(cdInfo.getArtist());
		setAlbum(cdInfo.getAlbum());
		dialogControlled.getAlbumTextField().setText(cdInfo.getAlbum());
		setYear(DateUtils.getCurrentYear());
		dialogControlled.getYearTextField().setText(Integer.toString(DateUtils.getCurrentYear()));
		setGenre(dialogControlled.getGenreComboBox().getSelectedItem().toString());
		// Creates folders when information is coming from cdda2wav
		if (cdInfo.getArtist() != null && cdInfo.getAlbum() != null) {
			dialogControlled.getFolderName().setText(
					StringUtils.getString(RepositoryHandler.getInstance().getRepositoryPath(), SystemProperties.fileSeparator, cdInfo.getArtist(), SystemProperties.fileSeparator,
							cdInfo.getAlbum()));
		} else
			dialogControlled.getFolderName().setText(path);
		dialogControlled.getAmazonButton().setEnabled(false);
		dialogControlled.getFormat().setSelectedItem(RipperHandler.getInstance().getEncoder());
		dialogControlled.getQualityComboBox().setSelectedItem(RipperHandler.getInstance().getEncoderQuality());
		setFolder(null);
		dialogControlled.setTableData(cdInfo);
		dialogControlled.updateTrackNames(cdInfo.getTitles());
		dialogControlled.updateArtistNames(cdInfo.getArtists());
		dialogControlled.updateComposerNames(cdInfo.getComposers());
		dialogControlled.setVisible(true);
	}

}
