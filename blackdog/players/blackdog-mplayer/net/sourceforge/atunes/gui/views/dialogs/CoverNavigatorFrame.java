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

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.utils.LanguageTool;

public class CoverNavigatorFrame extends CustomFrame {

	private static final long serialVersionUID = -1744765531225480303L;

	private JList list;
	private JPanel coversPanel;

	private JButton coversButton;

	private int width = GuiUtils.getComponentSizeForResolution(1280, 1150);
	private int height = GuiUtils.getComponentSizeForResolution(1280, 650);

	private int coversScrollPaneWidth = (int) (width * 0.7) - 50;
	private int coversSize = 150;

	public CoverNavigatorFrame(List<Artist> artists) {
		super(LanguageTool.getString("COVER_NAVIGATOR"));
		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setContent(artists);
		setResizable(false);
		GuiUtils.applyComponentOrientation(this);
		enableCloseActionWithEscapeKey();
	}

	/**
	 * @return the coversButton
	 */
	public JButton getCoversButton() {
		return coversButton;
	}

	/**
	 * @return the coversPanel
	 */
	public JPanel getCoversPanel() {
		return coversPanel;
	}

	/**
	 * @return the coversScrollPaneWidth
	 */
	public int getCoversScrollPaneWidth() {
		return coversScrollPaneWidth;
	}

	/**
	 * @return the coversSize
	 */
	public int getCoversSize() {
		return coversSize;
	}

	/**
	 * @return the list
	 */
	public JList getList() {
		return list;
	}

	private void setContent(List<Artist> artists) {
		JPanel panel = new JPanel(null);

		coversPanel = new JPanel(new GridBagLayout());

		list = new JList(artists.toArray());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane listScrollPane = new JScrollPane(list);
		listScrollPane.setLocation(10, 10);
		listScrollPane.setSize((int) (width * 0.3), height - 50);

		JScrollPane coversScrollPane = new JScrollPane(coversPanel);
		coversScrollPane.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
		coversScrollPane.setLocation(10 + (int) (width * 0.3) + 20, 10);
		coversScrollPane.setSize(coversScrollPaneWidth, height - 90);
		coversScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		coversButton = new JButton(LanguageTool.getString("GET_COVERS"));
		coversButton.setSize(new Dimension(150, 25));
		coversButton.setLocation(10 + (int) (width * 0.3) + 20, height - 70);

		panel.add(listScrollPane);
		panel.add(coversScrollPane);
		panel.add(coversButton);

		add(panel);
	}

}
