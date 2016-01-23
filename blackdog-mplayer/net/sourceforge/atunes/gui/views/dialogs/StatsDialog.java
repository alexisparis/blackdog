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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class StatsDialog extends CustomFrame {

	private static final long serialVersionUID = -7822497871738495670L;

	private JTabbedPane tabbedPane;

	private JTable generalTable;
	private JTable songsPlayedTable;
	private JTable artistsTable;
	private JTable albumsTable;
	private JTable songsTable;

	private JLabel generalChart;
	private JLabel songsPlayedChart;
	private JLabel artistsChart;
	private JLabel albumsChart;
	private JLabel songsChart;

	public StatsDialog() {
		super();
		setTitle(StringUtils.getString(LanguageTool.getString("STATS"), " - ", Constants.APP_NAME, " ", Constants.APP_VERSION_NUMBER));
		setSize(750, 750);
		setLocationRelativeTo(null);
		setResizable(false);
		add(getContent());
		GuiUtils.applyComponentOrientation(this);
		enableCloseActionWithEscapeKey();
	}

	public static void main(String[] args) {
		new StatsDialog().setVisible(true);
	}

	public JLabel getAlbumsChart() {
		return albumsChart;
	}

	public JTable getAlbumsTable() {
		return albumsTable;
	}

	public JLabel getArtistsChart() {
		return artistsChart;
	}

	public JTable getArtistsTable() {
		return artistsTable;
	}

	private JPanel getContent() {
		JPanel panel = new JPanel(new BorderLayout());

		// General stats
		JPanel generalPanel = new JPanel(new GridBagLayout());
		generalTable = new JTable();
		generalTable.setShowGrid(false);
		JScrollPane generalScrollPane = new JScrollPane(generalTable);
		generalChart = new JLabel();
		generalChart.setBorder(BorderFactory.createEtchedBorder());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 10, 10);
		generalPanel.add(generalScrollPane, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		generalPanel.add(generalChart, c);

		// Songs stats
		JPanel songPanel = new JPanel(new GridBagLayout());
		songsTable = new JTable();
		songsTable.setShowGrid(false);
		JScrollPane songsScrollPane = new JScrollPane(songsTable);
		songsChart = new JLabel();
		songsChart.setBorder(BorderFactory.createEtchedBorder());

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 10, 10);
		songPanel.add(songsScrollPane, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		songPanel.add(songsChart, c);

		// Albums stats
		JPanel albumPanel = new JPanel(new GridBagLayout());
		albumsTable = new JTable();
		albumsTable.setShowGrid(false);
		JScrollPane albumsScrollPane = new JScrollPane(albumsTable);
		albumsChart = new JLabel();
		albumsChart.setBorder(BorderFactory.createEtchedBorder());

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		albumPanel.add(albumsScrollPane, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		albumPanel.add(albumsChart, c);

		// Artists stats
		JPanel artistPanel = new JPanel(new GridBagLayout());
		artistsTable = new JTable();
		artistsTable.setShowGrid(false);
		JScrollPane artistsScrollPane = new JScrollPane(artistsTable);
		artistsChart = new JLabel();
		artistsChart.setBorder(BorderFactory.createEtchedBorder());

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		artistPanel.add(artistsScrollPane, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		artistPanel.add(artistsChart, c);

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(LanguageTool.getString("GENERAL"), ImageLoader.INFO, generalPanel);
		tabbedPane.addTab(LanguageTool.getString("SONG"), ImageLoader.FILE, songPanel);
		tabbedPane.addTab(LanguageTool.getString("ALBUM"), ImageLoader.ALBUM, albumPanel);
		tabbedPane.addTab(LanguageTool.getString("ARTIST"), ImageLoader.ARTIST, artistPanel);
		panel.add(tabbedPane, BorderLayout.CENTER);
		return panel;
	}

	public JLabel getGeneralChart() {
		return generalChart;
	}

	public JTable getGeneralTable() {
		return generalTable;
	}

	public JLabel getSongsChart() {
		return songsChart;
	}

	public JLabel getSongsPlayedChart() {
		return songsPlayedChart;
	}

	public JTable getSongsPlayedTable() {
		return songsPlayedTable;
	}

	public JTable getSongsTable() {
		return songsTable;
	}
}
