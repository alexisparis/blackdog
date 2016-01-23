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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.utils.LanguageTool;

public class PlayListFilterPanel extends JPanel {

	private static final long serialVersionUID = 795619869506188088L;
	private JLabel filterLabel;
	private JTextField filterTextField;
	private JLabel clearFilterButton;

	public PlayListFilterPanel() {
		super(new GridBagLayout());
		addContent();
	}

	private void addContent() {
		filterLabel = new JLabel(LanguageTool.getString("FILTER"));
		filterTextField = new JTextField();
		filterTextField.setToolTipText(LanguageTool.getString("FILTER_TEXTFIELD_TOOLTIP"));
		clearFilterButton = new JLabel(ImageLoader.UNDO);
		clearFilterButton.setToolTipText(LanguageTool.getString("CLEAR_FILTER_BUTTON_TOOLTIP"));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 5, 0, 5);
		add(filterLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 5, 1, 5);
		add(filterTextField, c);
		c.gridx = 2;
		c.weightx = 0;
		c.insets = new Insets(0, 5, 1, 5);
		add(clearFilterButton, c);
	}

	public JLabel getClearFilterButton() {
		return clearFilterButton;
	}

	public JTextField getFilterTextField() {
		return filterTextField;
	}

	/**
	 * Sets focus to filterTextField
	 */
	public void setFocusToTextField() {
		filterTextField.requestFocusInWindow();
	}
}
