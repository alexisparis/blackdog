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

package net.sourceforge.atunes.kernel.controllers.audioScrobbler;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.AudioScrobblerAlbumsTableModel;
import net.sourceforge.atunes.gui.model.AudioScrobblerArtistsTableModel;
import net.sourceforge.atunes.gui.model.AudioScrobblerTracksTableModel;
import net.sourceforge.atunes.gui.views.panels.AudioScrobblerPanel;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.model.PanelController;
import net.sourceforge.atunes.kernel.handlers.AudioScrobblerServiceHandler;
import net.sourceforge.atunes.kernel.handlers.DesktopHandler;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerAlbum;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerArtist;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerTrack;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.SongStats;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.search.SearchFactory;
import net.sourceforge.atunes.kernel.utils.ClipboardFacade;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;

public class AudioScrobblerController extends PanelController<AudioScrobblerPanel> {

	/**
	 * Query to use when searching for a video
	 */
	private String searchVideoQuery = null;

	public AudioScrobblerController(AudioScrobblerPanel panel) {
		super(panel);
		addBindings();
	}

	@Override
	protected void addBindings() {
		AudioScrobblerListener objListener = new AudioScrobblerListener(this);

		panelControlled.getTracksTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int selectedTrack = panelControlled.getTracksTable().getSelectedRow();
					AudioScrobblerTrack track = ((AudioScrobblerTracksTableModel) panelControlled.getTracksTable().getModel()).getTrack(selectedTrack);
					DesktopHandler.getInstance().openURL(track.getUrl());
				}
			}
		});

		panelControlled.getAlbumsTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int selectedAlbum = panelControlled.getAlbumsTable().getSelectedRow();
					AudioScrobblerAlbum album = ((AudioScrobblerAlbumsTableModel) panelControlled.getAlbumsTable().getModel()).getAlbum(selectedAlbum);
					DesktopHandler.getInstance().openURL(album.getUrl());
				}
			}
		});

		panelControlled.getSimilarArtistsTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int selectedArtist = panelControlled.getSimilarArtistsTable().getSelectedRow();
					AudioScrobblerArtist artist = ((AudioScrobblerArtistsTableModel) panelControlled.getSimilarArtistsTable().getModel()).getArtist(selectedArtist);
					DesktopHandler.getInstance().openURL(artist.getUrl());
				}
			}
		});

		panelControlled.getCopyLyricToClipboard().addActionListener(objListener);
		panelControlled.getSearchForVideo().addActionListener(objListener);
	}

	@Override
	protected void addStateBindings() {
		// Nothing to do
	}

	public void clear(boolean clearArtistAndSimilar) {
		logger.debug(LogCategories.CONTROLLER, new String[] { Boolean.toString(clearArtistAndSimilar) });

		panelControlled.getTracksTable().removeAll();
		panelControlled.getAlbumPanel().setVisible(false);
		panelControlled.getLyricsCover().setVisible(false);
		panelControlled.getLyricsContainer().setText("");
		panelControlled.getLyricsLabel().setText("");
		panelControlled.getLyricsLabel().setToolTipText(null);
		panelControlled.getLyricsArtistLabel().setText("");
		panelControlled.getLyricsLastPlayDateLabel().setText("");
		panelControlled.getCopyLyricToClipboard().setVisible(false);
		panelControlled.getSearchForVideo().setVisible(false);
		panelControlled.getLyricScrollPane().setVisible(false);

		if (clearArtistAndSimilar) {
			panelControlled.getArtistImageLabel().setVisible(false);
			panelControlled.getAlbumsPanel().setVisible(false);
			panelControlled.getSimilarArtistsPanel().setVisible(false);
		}
	}

	public void clearAlbumsContainer() {
		panelControlled.getAlbumsTable().setModel(new AudioScrobblerAlbumsTableModel());
	}

	public void clearSimilarArtistsContainer() {
		panelControlled.getSimilarArtistsTable().setModel(new AudioScrobblerArtistsTableModel());
	}

	/**
	 * Copies current lyric to clipboard
	 */
	protected void copyToClipboard() {
		String sLyric = panelControlled.getLyricsContainer().getText();
		if (sLyric == null)
			sLyric = "";
		ClipboardFacade.copyToClipboard(sLyric);
	}

	void fillAlbumsList(AudioScrobblerAlbum album, Image cover) {
		BufferedImage resizedImage = ImageUtils.scaleImage(cover, Constants.AUDIO_SCROBBLER_IMAGE_WIDTH, Constants.AUDIO_SCROBBLER_IMAGE_HEIGHT);
		ImageIcon image = resizedImage != null ? new ImageIcon(resizedImage) : null;

		album.setCover(image);
		((AudioScrobblerAlbumsTableModel) panelControlled.getAlbumsTable().getModel()).addAlbum(album);

		panelControlled.getAlbumsTable().revalidate();
		panelControlled.getAlbumsTable().repaint();
	}

	void fillSimilarArtistsList(AudioScrobblerArtist artist, Image img) {
		BufferedImage resizedImage = ImageUtils.scaleImage(img, Constants.AUDIO_SCROBBLER_IMAGE_WIDTH, Constants.AUDIO_SCROBBLER_IMAGE_HEIGHT);
		ImageIcon image = resizedImage != null ? new ImageIcon(resizedImage) : null;

		artist.setImage(image);
		((AudioScrobblerArtistsTableModel) panelControlled.getSimilarArtistsTable().getModel()).addArtist(artist);

		panelControlled.getSimilarArtistsTable().revalidate();
		panelControlled.getSimilarArtistsTable().repaint();
	}

	public AudioScrobblerPanel getPanelControlled() {
		return panelControlled;
	}

	public void notifyArtistImage(final Image img) {
		logger.debug(LogCategories.CONTROLLER);

		panelControlled.getArtistImageLabel().setIcon(new ImageIcon(img));
		panelControlled.getArtistImageLabel().setVisible(true);
	}

	public void notifyFinishGetAlbumInfo(final String artist, final AudioScrobblerAlbum album, final Image img) {
		panelControlled.getArtistLabel().setText(album != null ? album.getArtist() : artist, album != null ? album.getArtistUrl() : null);
		panelControlled.getAlbumLabel().setText(album != null ? album.getTitle() : LanguageTool.getString("UNKNOWN_ALBUM"), album != null ? album.getUrl() : null);
		panelControlled.getYearLabel().setText(album != null ? album.getYear() : "",
				album != null && album.getYear() != null ? StringUtils.getString("http://en.wikipedia.org/wiki/", album.getYear()) : null);
		if (album != null) {
			logger.debug(LogCategories.CONTROLLER, new String[] { album.getTitle() });
		}
		if (img != null) {
			ImageIcon image = ImageUtils.resize(new ImageIcon(img), 170, 170);
			panelControlled.getAlbumCoverLabel().setIcon(image);
			panelControlled.getLyricsCover().setIcon(image);
			panelControlled.getLyricsCover().setVisible(true);
		}
		panelControlled.getAlbumCoverLabel().setVisible(img != null);

		if (album != null) {
			panelControlled.getTracksTable().setModel(new AudioScrobblerTracksTableModel(album));
		}
		panelControlled.getAlbumPanel().setVisible(true);
	}

	public void notifyFinishGetAlbumsInfo(final AudioScrobblerAlbum album, final Image cover) {
		logger.debug(LogCategories.CONTROLLER, new String[] { album.getTitle() });

		panelControlled.getArtistAlbumsLabel().setText(album.getArtist(), album.getArtistUrl());
		panelControlled.getArtistAlbumsLabel().setVisible(true);
		panelControlled.getAlbumsPanel().setVisible(true);
		fillAlbumsList(album, cover);
	}

	public void notifyFinishGetSimilarArtist(final AudioScrobblerArtist artist, final Image img) {
		logger.debug(LogCategories.CONTROLLER);

		panelControlled.getSimilarArtistsPanel().setVisible(true);
		fillSimilarArtistsList(artist, img);
	}

	public void notifyPodcast(PodcastFeedEntry entry) {
		clear(true);
		panelControlled.getLyricsCover().setIcon(ImageLoader.RSS);
		panelControlled.getLyricsCover().setVisible(true);
		panelControlled.getLyricsLabel().setText(entry.getName());
		panelControlled.getLyricsLabel().setToolTipText(entry.getName());
		panelControlled.getLyricScrollPane().setVisible(true);
		panelControlled.getLyricsContainer().setText(entry.getDescription());
		panelControlled.getLyricsContainer().setCaretPosition(0);
		panelControlled.getLyricsContainer().setLineWrap(true);
	}

	public void notifyRadio(Radio radio) {
		clear(true);
		panelControlled.getLyricsCover().setIcon(ImageLoader.RADIO);
		panelControlled.getLyricsCover().setVisible(true);
		panelControlled.getLyricsLabel().setText(radio.getName());
		panelControlled.getLyricsArtistLabel().setText(radio.getUrl());
		panelControlled.getLyricScrollPane().setVisible(true);
		panelControlled.getLyricsContainer().setLineWrap(true);
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}

	/**
	 * Opens a navigator to search for video
	 */
	public void searchForVideo() {
		if (searchVideoQuery != null)
			DesktopHandler.getInstance().openSearch(SearchFactory.getSearchForName("YouTube"), searchVideoQuery);
	}

	public void setLyrics(AudioObject audioObject, String lyrics) {
		logger.debug(LogCategories.CONTROLLER);

		// Get last date played
		SongStats stats = null;
		if (audioObject instanceof AudioFile) {
			stats = RepositoryHandler.getInstance().getSongStatistics((AudioFile) audioObject);
		}
		if (stats == null)
			panelControlled.getLyricsLastPlayDateLabel().setText(LanguageTool.getString("SONG_NEVER_PLAYED"));
		else {
			// If song is playing, take previous play, if not, take last
			Date date;
			if (PlayerHandler.getInstance().isPlaying())
				date = stats.getPreviousPlayed();
			else
				date = stats.getLastPlayed();

			// If date is null -> never player
			if (date == null)
				panelControlled.getLyricsLastPlayDateLabel().setText(LanguageTool.getString("SONG_NEVER_PLAYED"));
			else
				panelControlled.getLyricsLastPlayDateLabel().setText(StringUtils.getString(LanguageTool.getString("LAST_DATE_PLAYED"), ": ", DateUtils.toString(date)));
		}

		panelControlled.getLyricsLabel().setText(audioObject.getTitleOrFileName());
		panelControlled.getLyricsArtistLabel().setText(audioObject.getArtist());
		panelControlled.getLyricsContainer().setLineWrap(false);
		panelControlled.getLyricsContainer().setText(lyrics);
		panelControlled.getLyricsContainer().setCaretPosition(0);
		panelControlled.getLyricScrollPane().setVisible(true);
		panelControlled.getSearchForVideo().setVisible(true);

		panelControlled.getCopyLyricToClipboard().setVisible(true);

		// Set query string
		searchVideoQuery = StringUtils.getString(audioObject.getArtist(), " ", audioObject.getTitleOrFileName());
	}

	public void setWikiInformation(String wikiText, String wikiURL) {
		panelControlled.getArtistWikiAbstract().setText(wikiText);
		panelControlled.getArtistWikiAbstract().setCaretPosition(0);
		panelControlled.getArtistWikiReadMore().setText(LanguageTool.getString("READ_MORE"), wikiURL);
	}

	public void updatePanel(AudioObject file) {
		logger.debug(LogCategories.CONTROLLER);

		if (Kernel.getInstance().state.isUseAudioScrobbler()) {
			if (file == null)
				AudioScrobblerServiceHandler.getInstance().clear();
			else
				AudioScrobblerServiceHandler.getInstance().retrieveInfo(file);
		}
	}
}
