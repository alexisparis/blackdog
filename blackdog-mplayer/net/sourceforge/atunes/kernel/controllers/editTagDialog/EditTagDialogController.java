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

package net.sourceforge.atunes.kernel.controllers.editTagDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.atunes.gui.AutoCompleteFeature;
import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;
import net.sourceforge.atunes.kernel.controllers.model.DialogController;
import net.sourceforge.atunes.kernel.executors.BackgroundExecutor;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.DefaultTag;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.utils.log4j.LogCategories;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

public class EditTagDialogController extends DialogController<EditTagDialog> {

	private List<AudioFile> audioFilesEditing;

	public EditTagDialogController(EditTagDialog dialog) {
		super(dialog);
		addBindings();
		addStateBindings();
	}

	@Override
	protected void addBindings() {
		// Add genres combo box items
		List<String> genresSorted = Arrays.asList(Tag.genres);
		Collections.sort(genresSorted);
		dialogControlled.getGenreComboBox().setModel(new ListComboBoxModel(genresSorted));
		// Add autocompletion
		new AutoCompleteFeature(dialogControlled.getGenreComboBox());

		EditTagDialogActionListener actionListener = new EditTagDialogActionListener(this, dialogControlled);
		dialogControlled.getOkButton().addActionListener(actionListener);
		dialogControlled.getCancelButton().addActionListener(actionListener);
	}

	@Override
	protected void addStateBindings() {
		// Nothing to do
	}

	public void editFiles(List<AudioFile> files) {
		audioFilesEditing = files;

		// Load artists into combo box
		List<Artist> artistList = RepositoryHandler.getInstance().getArtists();
		List<String> artistNames = new ArrayList<String>();
		for (Artist a : artistList)
			artistNames.add(a.getName());
		dialogControlled.getArtistTextField().setModel(new ListComboBoxModel(artistNames));
		// Activate autocompletion of artists
		new AutoCompleteFeature(dialogControlled.getArtistTextField());

		// Load albums into combo box
		List<Album> albumList = RepositoryHandler.getInstance().getAlbums();
		List<String> albumNames = new ArrayList<String>();
		for (Album alb : albumList)
			albumNames.add(alb.getName());
		dialogControlled.getAlbumTextField().setModel(new ListComboBoxModel(albumNames));
		// Active autocompletion of albums
		new AutoCompleteFeature(dialogControlled.getAlbumTextField());

		if (files.size() == 1) {
			dialogControlled.getTrackNumberTextField().setEnabled(true);
			Tag tag = files.get(0).getTag();
			if (tag != null) {
				dialogControlled.getTitleTextField().setText(tag.getTitle());
				dialogControlled.getTitleTextField().setEnabled(true);
				dialogControlled.getTrackNumberTextField().setText(Integer.toString(((DefaultTag) tag).getTrackNumber()));
			} else {
				dialogControlled.getTitleTextField().setText("");
				dialogControlled.getTitleTextField().setEnabled(true);
				dialogControlled.getTrackNumberTextField().setText("");
			}
		} else {
			dialogControlled.getTitleTextField().setText("");
			dialogControlled.getTitleTextField().setEnabled(false);
			dialogControlled.getTrackNumberTextField().setText("");
			dialogControlled.getTrackNumberTextField().setEnabled(false);
		}

		Set<String> artists = new HashSet<String>();
		Set<String> albums = new HashSet<String>();
		Set<Integer> years = new HashSet<Integer>();
		Set<String> comments = new HashSet<String>();
		Set<String> genres = new HashSet<String>();
		Set<String> composers = new HashSet<String>();
		Set<String> lyrics2 = new HashSet<String>();
		Set<String> albumArtists = new HashSet<String>();
		for (int i = 0; i < files.size(); i++) {
			String artist = "";
			String album = "";
			int year = -1;
			String comment = "";
			String genre = "";
			String composer = "";
			String lyrics = "";
			String albumArtist = "";
			if (files.get(i).getTag() != null) {
				Tag tag = files.get(i).getTag();
				artist = tag.getArtist();
				album = tag.getAlbum();
				year = tag.getYear();
				comment = tag.getComment();
				genre = tag.getGenre();
				composer = tag.getComposer();
				lyrics = tag.getLyrics();
				albumArtist = tag.getAlbumArtist();
			}
			artists.add(artist);
			albums.add(album);
			years.add(year);
			comments.add(comment);
			genres.add(genre);
			composers.add(composer);
			lyrics2.add(lyrics);
			albumArtists.add(albumArtist);
		}

		if (artists.size() == 1 && files.get(0).getTag() != null)
			dialogControlled.getArtistTextField().getEditor().setItem(files.get(0).getTag().getArtist());
		else
			dialogControlled.getArtistTextField().getEditor().setItem("");

		if (albums.size() == 1 && files.get(0).getTag() != null)
			dialogControlled.getAlbumTextField().getEditor().setItem(files.get(0).getTag().getAlbum());
		else
			dialogControlled.getAlbumTextField().getEditor().setItem("");

		if (years.size() == 1 && files.get(0).getTag() != null && files.get(0).getTag().getYear() > 0)
			dialogControlled.getYearTextField().setText(Integer.toString(files.get(0).getTag().getYear()));
		else
			dialogControlled.getYearTextField().setText("");

		if (comments.size() == 1 && files.get(0).getTag() != null)
			dialogControlled.getCommentTextArea().setText(files.get(0).getTag().getComment());
		else
			dialogControlled.getCommentTextArea().setText("");

		if (genres.size() == 1 && files.get(0).getTag() != null) {
			dialogControlled.getGenreComboBox().getEditor().setItem(files.get(0).getTag().getGenre());
		} else
			dialogControlled.getGenreComboBox().getEditor().setItem("");

		if (lyrics2.size() == 1 && files.get(0).getTag() != null)
			dialogControlled.getLyricsTextArea().setText(files.get(0).getTag().getLyrics());
		else
			dialogControlled.getLyricsTextArea().setText("");

		if (composers.size() == 1 && files.get(0).getTag() != null)
			dialogControlled.getComposerTextField().setText(files.get(0).getTag().getComposer());
		else
			dialogControlled.getComposerTextField().setText("");

		if (albumArtists.size() == 1 && files.get(0).getTag() != null)
			dialogControlled.getAlbumArtistTextField().setText(files.get(0).getTag().getAlbumArtist());
		else
			dialogControlled.getAlbumArtistTextField().setText("");

		dialogControlled.setVisible(true);
	}

	protected void editTag() {
		logger.debug(LogCategories.CONTROLLER);

		dialogControlled.setVisible(false);

		// Build editor props
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("TITLE", dialogControlled.getTitleTextField().getText());
		if (dialogControlled.getArtistTextField().getSelectedItem().toString().isEmpty())
			properties.put("ARTIST", null);
		else
			properties.put("ARTIST", dialogControlled.getArtistTextField().getSelectedItem());
		if (dialogControlled.getAlbumTextField().getSelectedItem().toString().isEmpty())
			properties.put("ALBUM", null);
		else
			properties.put("ALBUM", dialogControlled.getAlbumTextField().getSelectedItem());
		if (dialogControlled.getYearTextField().getText().isEmpty())
			properties.put("YEAR", null);
		else
			properties.put("YEAR", dialogControlled.getYearTextField().getText());
		if (dialogControlled.getCommentTextArea().getText().isEmpty())
			properties.put("COMMENT", null);
		else
			properties.put("COMMENT", dialogControlled.getCommentTextArea().getText());
		if (dialogControlled.getGenreComboBox().getSelectedIndex() == 0)
			properties.put("GENRE", null);
		else
			properties.put("GENRE", dialogControlled.getGenreComboBox().getSelectedItem());
		if (dialogControlled.getLyricsTextArea().getText().isEmpty())
			properties.put("LYRICS", null);
		else
			properties.put("LYRICS", dialogControlled.getLyricsTextArea().getText());
		if (dialogControlled.getComposerTextField().getText().isEmpty())
			properties.put("COMPOSER", null);
		else
			properties.put("COMPOSER", dialogControlled.getComposerTextField().getText());
		if (dialogControlled.getAlbumArtistTextField().getText().isEmpty())
			properties.put("ALBUM_ARTIST", null);
		else
			properties.put("ALBUM_ARTIST", dialogControlled.getAlbumArtistTextField().getText());

		properties.put("TRACK", dialogControlled.getTrackNumberTextField().getText());

		BackgroundExecutor.changeTags(audioFilesEditing, properties);
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}
}
