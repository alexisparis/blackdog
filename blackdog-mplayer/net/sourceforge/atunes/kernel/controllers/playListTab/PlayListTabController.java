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

package net.sourceforge.atunes.kernel.controllers.playListTab;

import java.awt.Dimension;

import javax.swing.JPanel;

import net.sourceforge.atunes.gui.views.panels.ButtonTabComponent;
import net.sourceforge.atunes.gui.views.panels.PlayListTabPanel;
import net.sourceforge.atunes.kernel.controllers.model.PanelController;

public class PlayListTabController extends PanelController<PlayListTabPanel> {

	public PlayListTabController(PlayListTabPanel panel) {
		super(panel);
		addBindings();
		addStateBindings();
	}

	@Override
	protected void addBindings() {
		PlayListTabListener listener = new PlayListTabListener(panelControlled);

		panelControlled.getNewPlayListButton().addActionListener(listener);
		panelControlled.getPlayListTabbedPane().addChangeListener(listener);
		panelControlled.getPlayListTabbedPane().addMouseListener(listener);
		panelControlled.getRenameMenuItem().addActionListener(listener);
	}

	@Override
	protected void addStateBindings() {
		// Nothing to do
	}

	public void deletePlayList(int index) {
		panelControlled.getPlayListTabbedPane().removeTabAt(index);
	}

	public void forceSwitchTo(int index) {
		panelControlled.getPlayListTabbedPane().setSelectedIndex(index);
	}

	public void newPlayList(String name) {
		JPanel emptyPanel = new JPanel();
		emptyPanel.setPreferredSize(new Dimension(0, 0));
		emptyPanel.setSize(0, 0);
		panelControlled.getPlayListTabbedPane().addTab(name, emptyPanel);
		panelControlled.getPlayListTabbedPane().setTabComponentAt(panelControlled.getPlayListTabbedPane().indexOfComponent(emptyPanel),
				new ButtonTabComponent(name, panelControlled.getPlayListTabbedPane()));
		// Force size of tabbed pane to avoid increasing height
		panelControlled.getPlayListTabbedPane().setPreferredSize(new Dimension(0, PlayListTabPanel.TAB_HEIGHT));
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}

	public void renamePlayList(int index, String newName) {
		panelControlled.getPlayListTabbedPane().setTitleAt(index, newName);
		((ButtonTabComponent) panelControlled.getPlayListTabbedPane().getTabComponentAt(index)).getLabel().setText(newName);
	}
}
