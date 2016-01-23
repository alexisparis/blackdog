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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.utils.LanguageTool;

// General characteristics of the preference dialog

public class EditPreferencesDialog extends CustomModalDialog {

	private static final long serialVersionUID = -4759149194433605946L;

	private JButton ok;
	private JButton cancel;

	private JPanel options;
	private JList list;

	private PreferencesPanel[] panels;

	public EditPreferencesDialog(JFrame owner) {
		super(owner, 900, 590, true);
		setLocationRelativeTo(null);
		setResizable(false);
		setTitle(LanguageTool.getString("PREFERENCES"));
		add(getContent());
		GuiUtils.applyComponentOrientation(this);
		enableCloseActionWithEscapeKey();
	}

	public static void main(String[] args) {
		new EditPreferencesDialog(null).setVisible(true);
	}

	public AudioScrobblerPanel getAudioScrobblerPanel() {
		return (AudioScrobblerPanel) panels[5];
	}

	public JButton getCancel() {
		return cancel;
	}

	private JPanel getContent() {
		JPanel container = new JPanel(new GridBagLayout());
		container.setOpaque(false);
		list = new JList();
		JScrollPane scrollPane = new JScrollPane(list);
		//scrollPane.setPreferredSize(new Dimension(170,100));
		options = new JPanel();
		ok = new CustomButton(null, LanguageTool.getString("OK"));
		cancel = new CustomButton(null, LanguageTool.getString("CANCEL"));
		JPanel auxPanel = new JPanel();
		auxPanel.add(ok);
		auxPanel.add(cancel);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx = 0.9;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 0, 5);
		container.add(scrollPane, c);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.6;
		c.insets = new Insets(10, 5, 0, 10);
		container.add(options, c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(10, 0, 10, 10);
		container.add(auxPanel, c);

		return container;
	}

	public JList getList() {
		return list;
	}

	public JButton getOk() {
		return ok;
	}

	public void setListModel(DefaultListModel listModel) {
		list.setModel(listModel);
	}

	public void setPanels(PreferencesPanel[] panels) {
		this.panels = panels;
		options.setLayout(new CardLayout());
		for (int i = 0; i < panels.length; i++)
			options.add(Integer.toString(i), panels[i]);
		GuiUtils.applyComponentOrientation(this);
	}

	public void showPanel(int index) {
		((CardLayout) options.getLayout()).show(options, Integer.toString(index));
	}

}
