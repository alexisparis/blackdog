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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationControllerViews;
import net.sourceforge.atunes.model.AudioObject;

public class NavigationTreeSelectionListener implements javax.swing.event.TreeSelectionListener {

	private NavigationController controller;
	private NavigationPanel panel;

	public NavigationTreeSelectionListener(NavigationController controller, NavigationPanel panel) {
		this.controller = controller;
		this.panel = panel;

	}

	private void treeSelection(JTree tree) {
		List<AudioObject> songs = new ArrayList<AudioObject>();
		TreePath[] paths = tree.getSelectionPaths();

		// Avoid events when changes on a tree different than the one which is visible
		// TODO: Adding a new tab requires to add an if. Try to generalize

		if (tree == panel.getNavigationTree() && controller.getState().getNavigationView() != NavigationControllerViews.TAG_VIEW)
			return;

		if (tree == panel.getFavoritesTree() && controller.getState().getNavigationView() != NavigationControllerViews.FAVORITE_VIEW)
			return;

		if (tree == panel.getPodcastFeedTree() && controller.getState().getNavigationView() != NavigationControllerViews.PODCAST_FEED_VIEW)
			return;

		if (tree == panel.getRadioTree() && controller.getState().getNavigationView() != NavigationControllerViews.RADIO_VIEW)
			return;

		if (tree == panel.getFileNavigationTree() && controller.getState().getNavigationView() != NavigationControllerViews.FILE_VIEW)
			return;

		if (tree == panel.getDeviceTree() && controller.getState().getNavigationView() != NavigationControllerViews.DEVICE_VIEW)
			return;

		if (paths != null) {
			for (TreePath element : paths) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) (element.getLastPathComponent());
				if (controller.getState().getNavigationView() == NavigationControllerViews.FAVORITE_VIEW)
					songs.addAll(controller.getSongsForFavoriteTreeNode(node));
				else if (controller.getState().getNavigationView() == NavigationControllerViews.DEVICE_VIEW)
					songs.addAll(controller.getSongsForDeviceTreeNode(node));
				else if (controller.getState().getNavigationView() == NavigationControllerViews.PODCAST_FEED_VIEW)
					songs.addAll(controller.getSongsForPodcastFeedTreeNode(node));
				else if (controller.getState().getNavigationView() != NavigationControllerViews.RADIO_VIEW)
					songs.addAll(controller.getSongsForTreeNode(node));
				else
					songs.addAll(controller.getSongsForRadioTreeNode(node));
			}
			((NavigationTableModel) panel.getNavigationTable().getModel()).setSongs(songs);

			panel.getNavigationTableAddButton().setEnabled(false);
			panel.getNavigationTableInfoButton().setEnabled(false);
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		treeSelection((JTree) e.getSource());
	}
}
