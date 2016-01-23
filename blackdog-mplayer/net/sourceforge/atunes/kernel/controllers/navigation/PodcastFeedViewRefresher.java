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

package net.sourceforge.atunes.kernel.controllers.navigation;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.utils.LanguageTool;

public class PodcastFeedViewRefresher {

	private static void addPodcastFeedNodes(List<PodcastFeed> podcastFeeds, DefaultMutableTreeNode root, String currentFilter) {
		if (podcastFeeds == null)
			return;

		for (PodcastFeed pf : podcastFeeds) {
			if (currentFilter == null || pf.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(pf);
				root.add(child);
			}
		}
	}

	public static void refresh(List<PodcastFeed> podcastFeeds, DefaultTreeModel treeModel, String currentFilter) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
		root.setUserObject(LanguageTool.getString("PODCAST_FEEDS"));
		root.removeAllChildren();
		addPodcastFeedNodes(podcastFeeds, root, currentFilter);
		treeModel.reload();
	}
}
