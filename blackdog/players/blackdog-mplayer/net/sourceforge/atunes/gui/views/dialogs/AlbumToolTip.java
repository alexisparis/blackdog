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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.utils.StringUtils;

public class AlbumToolTip extends CustomModalDialog {

	private static final long serialVersionUID = -5041702404982493070L;

	private JLabel picture;
	private JLabel album;
	private JLabel artist;
	private JLabel songs;

	public Timer timer = new Timer(0, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			setVisible(true);
		}
	});

	public AlbumToolTip() {
		super(null, Constants.TOOLTIP_IMAGE_WIDTH + 200, Constants.TOOLTIP_IMAGE_HEIGHT + 10, false);
		setFocusableWindowState(false);
		JPanel container = new JPanel(new GridBagLayout());
		picture = new JLabel();
		album = new JLabel();
		album.setFont(Fonts.GENERAL_FONT_BOLD);
		artist = new JLabel();
		songs = new JLabel();

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 3;
		c.insets = new Insets(0, 5, 0, 0);
		container.add(picture, c);
		c.gridx = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0.3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(15, 10, 0, 10);
		container.add(album, c);
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 10, 0, 10);
		container.add(artist, c);
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(0, 10, 15, 10);
		container.add(songs, c);
		setContent(container);
		GuiUtils.applyComponentOrientation(this);
	}

	public void setAlbum(String album) {
		this.album.setText(StringUtils.getString(album));
	}

	public void setArtist(String artist) {
		this.artist.setText(artist);
	}

	public void setPicture(ImageIcon picture) {
		this.picture.setIcon(picture);
	}

	public void setSongs(String songs) {
		this.songs.setText(songs);
	}

}
