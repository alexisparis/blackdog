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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.kernel.controllers.navigation.NavigationControllerState.ViewMode;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class DeviceViewRefresher {

	public static void refresh(Map<String, ?> data, DefaultTreeModel treeModel, ViewMode viewMode, String currentFilter, boolean isSortDeviceByTag) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
		root.removeAllChildren();
		if (isSortDeviceByTag) {
			root.setUserObject(LanguageTool.getString("DEVICE"));
			List<String> artistNamesList = new ArrayList<String>(data.keySet());
			Collections.sort(artistNamesList);
			Map<String, Album> albums = new HashMap<String, Album>();
			List<String> albumsList = new ArrayList<String>();
			if (viewMode == ViewMode.ARTIST) {
				for (int i = 0; i < artistNamesList.size(); i++) {
					Artist artist = (Artist) data.get(artistNamesList.get(i));
					DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
					List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
					if (currentFilter == null || artist.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
						Collections.sort(albumNamesList);
						for (int j = 0; j < albumNamesList.size(); j++) {
							Album album = artist.getAlbums().get(albumNamesList.get(j));
							DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
							artistNode.add(albumNode);
							root.add(artistNode);
						}
					}
				}
			} else {
				for (int i = 0; i < artistNamesList.size(); i++) {
					Artist artist = (Artist) data.get(artistNamesList.get(i));
					List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
					for (int j = 0; j < albumNamesList.size(); j++) {
						String a = albumNamesList.get(j);
						Album album = artist.getAlbums().get(a);
						if (currentFilter == null || album.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
							albums.put(album.getName(), album);
							albumsList.add(album.getName());
						}
					}
				}
				Collections.sort(albumsList);
				for (int i = 0; i < albumsList.size(); i++) {
					Album a = albums.get(albumsList.get(i));
					DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(a);
					root.add(albumNode);
				}
			}
		} else {
			List<String> rootFolderKeys = new ArrayList<String>(data.keySet());
			if (rootFolderKeys.isEmpty())
				root.setUserObject(LanguageTool.getString("DEVICE"));
			else {
				root.setUserObject(StringUtils.getString(LanguageTool.getString("DEVICE"), " (", rootFolderKeys.get(0), ")"));
				Folder rootFolder = (Folder) data.get(rootFolderKeys.get(0));
				RefreshUtils.addFolderNodes(rootFolder.getFolders(), root, null);
			}
		}
		treeModel.reload();
	}
}
