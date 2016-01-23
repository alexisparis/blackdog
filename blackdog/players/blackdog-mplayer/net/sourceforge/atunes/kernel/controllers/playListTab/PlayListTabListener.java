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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.atunes.gui.views.panels.PlayListTabPanel;
import net.sourceforge.atunes.kernel.handlers.MultiplePlaylistHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.utils.LanguageTool;

public class PlayListTabListener extends MouseAdapter implements ActionListener, ChangeListener {

	private PlayListTabPanel panel;

	public PlayListTabListener(PlayListTabPanel panel) {
		this.panel = panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == panel.getNewPlayListButton()) {
			MultiplePlaylistHandler.getInstance().newPlayList();
		} else if (e.getSource() == panel.getRenameMenuItem()) {
			int selectedPlaylist = panel.getPlayListTabbedPane().getSelectedIndex();
			String currentName = panel.getPlayListTabbedPane().getTitleAt(selectedPlaylist);
			String newName = VisualHandler.getInstance().showInputDialog(LanguageTool.getString("RENAME_PLAYLIST"), currentName);
			if (newName != null) {
				MultiplePlaylistHandler.getInstance().renamePlayList(selectedPlaylist, newName);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		if (e.getButton() == MouseEvent.BUTTON3) {
			panel.getPopupMenu().show(panel, e.getX(), e.getY());
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		int selectedPlaylist = panel.getPlayListTabbedPane().getSelectedIndex();
		MultiplePlaylistHandler.getInstance().switchToPlaylist(selectedPlaylist);
	}

}
