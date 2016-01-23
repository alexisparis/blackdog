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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.Renderers;
import net.sourceforge.atunes.gui.views.controls.SubstanceAudioScrobblerImageJTable;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerAlbum;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerArtist;
import net.sourceforge.atunes.utils.LanguageTool;

public class AudioScrobblerPanel extends JPanel {

	private static final long serialVersionUID = 707242790413122482L;

	public static final Dimension PREF_SIZE = new Dimension(230, 0);
	public static final Dimension MINIMUM_SIZE = new Dimension(170, 0);

	private JTabbedPane tabbedPane;

	private JPanel albumTabPanel;
	private JPanel artistTabPanel;
	private JPanel similarTabPanel;
	private JPanel lyricsPanel;

	private JPanel albumPanel;
	private UrlLabel artistLabel;
	private UrlLabel albumLabel;
	private UrlLabel yearLabel;
	private JLabel albumCoverLabel;
	private JTable tracksTable;
	private JLabel lyricsCover;
	private JLabel lyricsLabel;
	private JLabel lyricsArtistLabel;
	private JLabel lyricsLastPlayDateLabel;
	private JTextArea lyricsContainer;
	private JScrollPane lyricScrollPane;
	private JButton copyLyricToClipboard;
	private JButton searchForVideo;

	private JPanel albumsPanel;
	private JScrollPane albumsScrollPane;
	private JLabel artistImageLabel;
	private UrlLabel artistAlbumsLabel;
	private JTable albumsTable;
	private JTextArea artistWikiAbstract;
	private UrlLabel artistWikiReadMore;

	private JPanel similarArtistsPanel;
	private JTable similarArtistsTable;
	private JScrollPane similarArtistsScrollPane;

	public AudioScrobblerPanel() {
		super(new GridBagLayout());
		setPreferredSize(PREF_SIZE);
		setMinimumSize(MINIMUM_SIZE);
		setContent();
	}

	private void addAlbumPanel() {
		albumPanel = new JPanel(new GridBagLayout());

		artistLabel = new UrlLabel();
		artistLabel.setHorizontalAlignment(SwingConstants.CENTER);
		albumLabel = new UrlLabel();
		albumLabel.setHorizontalAlignment(SwingConstants.CENTER);
		albumLabel.setFont(Fonts.AUDIO_SCROBBLER_BIG_FONT);
		albumCoverLabel = new JLabel();
		albumCoverLabel.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
		albumCoverLabel.setVisible(false);
		yearLabel = new UrlLabel();
		yearLabel.setHorizontalAlignment(SwingConstants.CENTER);

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(15, 0, 0, 0);
		albumPanel.add(albumCoverLabel, c);

		c.gridy = 1;
		c.insets = new Insets(5, 5, 0, 5);
		albumPanel.add(albumLabel, c);

		c.gridy = 2;
		albumPanel.add(artistLabel, c);

		c.gridy = 3;
		albumPanel.add(yearLabel, c);

		tracksTable = new JTable();
		tracksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tracksTable.setShowGrid(false);
		tracksTable.setDefaultRenderer(String.class, Renderers.AUDIO_SCROBBLER_TRACK_RENDERER);
		tracksTable.setDefaultRenderer(Integer.class, Renderers.AUDIO_SCROBBLER_TRACK_NUMBER_RENDERER);
		tracksTable.getTableHeader().setReorderingAllowed(true);
		tracksTable.getTableHeader().setResizingAllowed(false);
		tracksTable.setColumnModel(new DefaultTableColumnModel() {
			private static final long serialVersionUID = 1338172152164826400L;

			@Override
			public void addColumn(TableColumn column) {
				super.addColumn(column);
				if (column.getModelIndex() == 0)
					column.setMaxWidth(25);
			}
		});
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(20, 5, 5, 5);
		JScrollPane scroll = new JScrollPane(tracksTable);
		//scroll.setBorder(BorderFactory.createEmptyBorder());
		albumPanel.add(scroll, c);

		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		albumTabPanel.add(albumPanel, c);

		albumPanel.setVisible(false);
	}

	private void addAlbumsPanel() {
		albumsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		artistImageLabel = new JLabel();
		artistImageLabel.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
		artistImageLabel.setVisible(false);
		artistAlbumsLabel = new UrlLabel();
		artistAlbumsLabel.setFont(Fonts.AUDIO_SCROBBLER_BIG_FONT);
		artistAlbumsLabel.setVisible(false);
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(15, 5, 0, 5);
		albumsPanel.add(artistImageLabel, c);
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5, 5, 5, 5);
		albumsPanel.add(artistAlbumsLabel, c);

		artistWikiAbstract = new JTextArea();
		artistWikiAbstract.setLineWrap(true);
		artistWikiAbstract.setWrapStyleWord(true);
		artistWikiAbstract.setEditable(false);
		artistWikiAbstract.setBorder(BorderFactory.createEmptyBorder());
		JScrollPane artistWikiScrollPane = new JScrollPane(artistWikiAbstract);
		//artistWikiScrollPane.setBorder(BorderFactory.createEmptyBorder());
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 0.3;
		c.fill = GridBagConstraints.BOTH;
		albumsPanel.add(artistWikiScrollPane, c);

		artistWikiReadMore = new UrlLabel();
		c.gridy = 3;
		c.weighty = 0;
		albumsPanel.add(artistWikiReadMore, c);

		albumsTable = new SubstanceAudioScrobblerImageJTable();
		albumsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		albumsTable.setShowGrid(false);
		albumsTable.getTableHeader().setReorderingAllowed(false);
		albumsTable.getTableHeader().setResizingAllowed(false);
		albumsTable.setDefaultRenderer(AudioScrobblerAlbum.class, Renderers.AUDIO_SCROBBLER_ALBUMS_RENDERER);
		albumsTable.setRowHeight(Constants.AUDIO_SCROBBLER_IMAGE_HEIGHT);
		albumsScrollPane = new JScrollPane(albumsTable);
		albumsScrollPane.setAutoscrolls(false);
		albumsScrollPane.setOpaque(false);
		//albumsScrollPane.setBorder(BorderFactory.createEmptyBorder());

		c.gridy = 4;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 5, 0, 5);
		albumsPanel.add(albumsScrollPane, c);
		c.gridy = 5;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);
		artistTabPanel.add(albumsPanel, c);

		albumsPanel.setVisible(false);
	}

	private void addLyricsPanel() {
		lyricsCover = new JLabel();
		lyricsCover.setVisible(false);
		lyricsCover.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
		lyricsLabel = new JLabel();
		lyricsLabel.setFont(Fonts.AUDIO_SCROBBLER_BIG_FONT);
		lyricsArtistLabel = new JLabel();
		lyricsLastPlayDateLabel = new JLabel();
		lyricsContainer = new JTextArea();
		//lyricsContainer.setBorder(BorderFactory.createEmptyBorder());
		lyricsContainer.setEditable(false);
		lyricsContainer.setWrapStyleWord(true);
		lyricScrollPane = new JScrollPane(lyricsContainer);
		lyricScrollPane.setAutoscrolls(false);
		lyricScrollPane.setOpaque(false);
		lyricScrollPane.setBorder(BorderFactory.createEmptyBorder());
		lyricScrollPane.setVisible(false);
		copyLyricToClipboard = new JButton(LanguageTool.getString("COPY_TO_CLIPBOARD"));
		copyLyricToClipboard.setToolTipText(LanguageTool.getString("COPY_TO_CLIPBOARD"));
		copyLyricToClipboard.setVisible(false);
		searchForVideo = new JButton(LanguageTool.getString("SEARCH_FOR_VIDEO"));
		searchForVideo.setToolTipText(LanguageTool.getString("SEARCH_FOR_VIDEO"));
		searchForVideo.setVisible(false);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(15, 0, 0, 0);
		lyricsPanel.add(lyricsCover, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 10, 0, 10);
		lyricsPanel.add(lyricsLabel, c);
		c.gridy = 2;
		c.insets = new Insets(5, 10, 10, 10);
		lyricsPanel.add(lyricsArtistLabel, c);
		c.gridy = 3;
		lyricsPanel.add(lyricsLastPlayDateLabel, c);
		c.gridy = 4;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 5, 5, 5);
		lyricsPanel.add(lyricScrollPane, c);
		c.gridy = 5;
		c.weighty = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		JPanel auxPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		auxPanel.add(copyLyricToClipboard);
		auxPanel.add(searchForVideo);
		lyricsPanel.add(auxPanel, c);
	}

	private void addSimilarArtistsPanel() {
		similarArtistsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		similarArtistsTable = new SubstanceAudioScrobblerImageJTable();
		similarArtistsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		similarArtistsTable.setShowGrid(false);
		similarArtistsTable.setDefaultRenderer(AudioScrobblerArtist.class, Renderers.AUDIO_SCROBLLER_ALBUMS_RENDERER);
		similarArtistsTable.setRowHeight(Constants.AUDIO_SCROBBLER_IMAGE_HEIGHT);
		similarArtistsTable.setColumnSelectionAllowed(false);
		similarArtistsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		similarArtistsTable.setColumnModel(new DefaultTableColumnModel() {
			private static final long serialVersionUID = 1338172152164826400L;

			@Override
			public void addColumn(TableColumn column) {
				super.addColumn(column);
				if (column.getModelIndex() == 0)
					column.setPreferredWidth(AudioScrobblerPanel.this.getWidth() - 50);
				else
					column.setResizable(false);
			}
		});
		similarArtistsScrollPane = new JScrollPane(similarArtistsTable);
		similarArtistsScrollPane.setAutoscrolls(false);
		similarArtistsScrollPane.setOpaque(false);
		//similarArtistsScrollPane.setBorder(BorderFactory.createEmptyBorder());
		similarArtistsPanel.add(similarArtistsScrollPane, c);

		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.3;
		similarTabPanel.add(similarArtistsPanel, c);

		similarArtistsPanel.setVisible(false);
	}

	public JLabel getAlbumCoverLabel() {
		return albumCoverLabel;
	}

	public UrlLabel getAlbumLabel() {
		return albumLabel;
	}

	public JPanel getAlbumPanel() {
		return albumPanel;
	}

	public JPanel getAlbumsPanel() {
		return albumsPanel;
	}

	public JScrollPane getAlbumsScrollPane() {
		return albumsScrollPane;
	}

	public JTable getAlbumsTable() {
		return albumsTable;
	}

	public UrlLabel getArtistAlbumsLabel() {
		return artistAlbumsLabel;
	}

	public JLabel getArtistImageLabel() {
		return artistImageLabel;
	}

	public UrlLabel getArtistLabel() {
		return artistLabel;
	}

	/**
	 * @return the artistWikiAbstract
	 */
	public JTextArea getArtistWikiAbstract() {
		return artistWikiAbstract;
	}

	/**
	 * @return the artistWikiReadMore
	 */
	public UrlLabel getArtistWikiReadMore() {
		return artistWikiReadMore;
	}

	public JButton getCopyLyricToClipboard() {
		return copyLyricToClipboard;
	}

	public JLabel getLyricsArtistLabel() {
		return lyricsArtistLabel;
	}

	public JTextArea getLyricsContainer() {
		return lyricsContainer;
	}

	public JLabel getLyricsCover() {
		return lyricsCover;
	}

	public JScrollPane getLyricScrollPane() {
		return lyricScrollPane;
	}

	public JLabel getLyricsLabel() {
		return lyricsLabel;
	}

	public JLabel getLyricsLastPlayDateLabel() {
		return lyricsLastPlayDateLabel;
	}

	/**
	 * @return the searchForVideo
	 */
	public JButton getSearchForVideo() {
		return searchForVideo;
	}

	public JPanel getSimilarArtistsPanel() {
		return similarArtistsPanel;
	}

	public JScrollPane getSimilarArtistsScrollPane() {
		return similarArtistsScrollPane;
	}

	public JTable getSimilarArtistsTable() {
		return similarArtistsTable;
	}

	public JPanel getSimilarTabPanel() {
		return similarTabPanel;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public JTable getTracksTable() {
		return tracksTable;
	}

	public UrlLabel getYearLabel() {
		return yearLabel;
	}

	private void setContent() {
		tabbedPane = new JTabbedPane();
		albumTabPanel = new JPanel(new GridBagLayout());
		artistTabPanel = new JPanel(new GridBagLayout());
		similarTabPanel = new JPanel(new GridBagLayout());
		lyricsPanel = new JPanel(new GridBagLayout());
		tabbedPane.addTab(LanguageTool.getString("SONG"), lyricsPanel);
		tabbedPane.addTab(LanguageTool.getString("ALBUM"), albumTabPanel);
		tabbedPane.addTab(LanguageTool.getString("ARTIST"), artistTabPanel);
		tabbedPane.addTab(LanguageTool.getString("SIMILAR"), similarTabPanel);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(3, 2, 0, 2);
		add(tabbedPane, c);

		addAlbumPanel();
		addAlbumsPanel();
		addSimilarArtistsPanel();
		addLyricsPanel();
	}

}
