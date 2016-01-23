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

import java.util.List;

import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationControllerViews;
import net.sourceforge.atunes.kernel.handlers.DeviceHandler;
import net.sourceforge.atunes.model.AudioObject;

public class NavigationTabbedPaneChangeListener implements ChangeListener {

	private NavigationController controller;
	private NavigationPanel panel;

	public NavigationTabbedPaneChangeListener(NavigationController controller, NavigationPanel panel) {
		this.controller = controller;
		this.panel = panel;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		int view = panel.getTabbedPane().getSelectedIndex();
		controller.setNavigationView(view);
		JTree tree;
		if (view == NavigationControllerViews.TAG_VIEW)
			tree = panel.getNavigationTree();
		else if (view == NavigationControllerViews.FILE_VIEW)
			tree = panel.getFileNavigationTree();
		else if (view == NavigationControllerViews.FAVORITE_VIEW)
			tree = panel.getFavoritesTree();
		else if (view == NavigationControllerViews.DEVICE_VIEW)
			tree = panel.getDeviceTree();
		else if (view == NavigationControllerViews.PODCAST_FEED_VIEW)
			tree = panel.getPodcastFeedTree();
		else
			tree = panel.getRadioTree();

		boolean showButtonsCondition = view == NavigationControllerViews.TAG_VIEW || DeviceHandler.getInstance().isDeviceConnected()
				&& view == NavigationControllerViews.DEVICE_VIEW && controller.isSortDeviceByTag();

		panel.getShowAlbum().setEnabled(showButtonsCondition);
		panel.getShowArtist().setEnabled(showButtonsCondition);
		panel.getShowGenre().setEnabled(showButtonsCondition);
		panel.getFilterTextField().setEnabled(view != NavigationControllerViews.FAVORITE_VIEW);
		panel.getFilterLabel().setEnabled(view != NavigationControllerViews.FAVORITE_VIEW);
		panel.getClearFilterButton().setEnabled(view != NavigationControllerViews.FAVORITE_VIEW);
		if (tree.getSelectionPath() != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) (tree.getSelectionPath().getLastPathComponent());
			List<AudioObject> songs;
			if (view == NavigationControllerViews.FAVORITE_VIEW)
				songs = controller.getSongsForFavoriteTreeNode(node);
			else if (view == NavigationControllerViews.DEVICE_VIEW)
				songs = controller.getSongsForDeviceTreeNode(node);
			else if (view == NavigationControllerViews.PODCAST_FEED_VIEW)
				songs = controller.getSongsForPodcastFeedTreeNode(node);
			else if (view != NavigationControllerViews.RADIO_VIEW)
				songs = controller.getSongsForTreeNode(node);
			else
				songs = controller.getSongsForRadioTreeNode(node);
			((NavigationTableModel) panel.getNavigationTable().getModel()).setSongs(songs);
			panel.getNavigationTableAddButton().setEnabled(false);
			panel.getNavigationTableInfoButton().setEnabled(false);
		}
		panel.getPrefsButton().setEnabled(view != NavigationControllerViews.RADIO_VIEW && view != NavigationControllerViews.PODCAST_FEED_VIEW);
		panel.getNavigationTableSortButton().setEnabled(view != NavigationControllerViews.RADIO_VIEW && view != NavigationControllerViews.PODCAST_FEED_VIEW);
	}
}
