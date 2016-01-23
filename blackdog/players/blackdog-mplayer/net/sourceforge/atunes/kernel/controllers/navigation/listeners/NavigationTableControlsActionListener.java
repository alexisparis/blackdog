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

package net.sourceforge.atunes.kernel.controllers.navigation.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler.SortType;
import net.sourceforge.atunes.model.AudioObject;

public class NavigationTableControlsActionListener implements ActionListener {

	private NavigationController controller;
	private NavigationPanel panel;

	public NavigationTableControlsActionListener(NavigationController controller, NavigationPanel panel) {
		this.controller = controller;
		this.panel = panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == panel.getNavigationTableInfoButton()) {
			int[] selRow = panel.getNavigationTable().getSelectedRows();
			List<AudioObject> songs = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongsAt(selRow);
			VisualHandler.getInstance().showPropertiesDialog(songs.get(0));
		} else if (e.getSource() == panel.getNavigationTableAddButton()) {
			int[] selRow = panel.getNavigationTable().getSelectedRows();
			List<AudioObject> songs = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongsAt(selRow);
			PlayListHandler.getInstance().addToPlayList(songs);
		} else if (e.getSource() == panel.getSortByTrack()) {
			if (controller.getState().getSortType() != SortType.BY_TRACK) {
				controller.getState().setSortType(SortType.BY_TRACK);
				List<AudioObject> songs = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongs();
				((NavigationTableModel) panel.getNavigationTable().getModel()).setSongs(controller.sort(songs, SortType.BY_TRACK));
			}
		} else if (e.getSource() == panel.getSortByTitle()) {
			if (controller.getState().getSortType() != SortType.BY_TITLE) {
				controller.getState().setSortType(SortType.BY_TITLE);
				List<AudioObject> songs = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongs();
				((NavigationTableModel) panel.getNavigationTable().getModel()).setSongs(controller.sort(songs, SortType.BY_TITLE));
			}
		} else if (e.getSource() == panel.getSortByFile()) {
			if (controller.getState().getSortType() != SortType.BY_FILE) {
				controller.getState().setSortType(SortType.BY_FILE);
				List<AudioObject> songs = ((NavigationTableModel) panel.getNavigationTable().getModel()).getSongs();
				((NavigationTableModel) panel.getNavigationTable().getModel()).setSongs(controller.sort(songs, SortType.BY_FILE));
			}
		}
	}
}
