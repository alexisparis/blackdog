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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.utils.LanguageTool;

public class PlayListTabPanel extends JPanel {

	private static final long serialVersionUID = 7382098268271937439L;

	private static final int BUTTONS_SIZE = 20;

	public static final int TAB_HEIGHT = 24;

	/**
	 * Button to create a new play list
	 */
	private JButton newPlayListButton;

	/**
	 * TabbedPane of play lists
	 */
	private JTabbedPane playListTabbedPane;

	/**
	 * Popup menu
	 */
	private JPopupMenu popupMenu;

	/**
	 * Rename playlist
	 */
	private JMenuItem renameMenuItem;

	public PlayListTabPanel() {
		super(new BorderLayout());
		addContent();
		setPreferredSize(new Dimension(10, TAB_HEIGHT));
	}

	private void addContent() {
		newPlayListButton = new CustomButton(ImageLoader.NEW_PLAYLIST, null);
		newPlayListButton.setPreferredSize(new Dimension(BUTTONS_SIZE, BUTTONS_SIZE));
		playListTabbedPane = new JTabbedPane();
		playListTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		JPanel auxPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2, 0, 0, 0);
		auxPanel.add(newPlayListButton, c);

		add(auxPanel, GuiUtils.getComponentOrientation().isLeftToRight() ? BorderLayout.WEST : BorderLayout.EAST);
		add(playListTabbedPane, BorderLayout.CENTER);

		playListTabbedPane.setPreferredSize(new Dimension(0, TAB_HEIGHT));

		popupMenu = new JPopupMenu();
		renameMenuItem = new JMenuItem(LanguageTool.getString("RENAME_PLAYLIST"));
		popupMenu.add(renameMenuItem);
		GuiUtils.applyComponentOrientation(this, popupMenu);
	}

	/**
	 * @return the newPlayListButton
	 */
	public JButton getNewPlayListButton() {
		return newPlayListButton;
	}

	/**
	 * @return the playListTabbedPane
	 */
	public JTabbedPane getPlayListTabbedPane() {
		return playListTabbedPane;
	}

	/**
	 * @return the popupMenu
	 */
	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	/**
	 * @return the renameMenuItem
	 */
	public JMenuItem getRenameMenuItem() {
		return renameMenuItem;
	}

}
