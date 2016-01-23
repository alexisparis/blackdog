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
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationControllerState.ViewMode;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.Genre;
import net.sourceforge.atunes.model.TreeObject;

public class TagViewRefresher {

	// Ignores "The "
	private static Comparator<String> smartComparator = new Comparator<String>() {
		@Override
		public int compare(String s1, String s2) {
			if (s1.toLowerCase().startsWith("the ") && s1.length() > 4) {
				s1 = s1.substring(4);
			}
			if (s2.toLowerCase().startsWith("the ") && s2.length() > 4) {
				s2 = s2.substring(4);
			}
			return s1.compareTo(s2);
		}
	};

	public static void refresh(Map<String, Artist> structure, JTree tree, DefaultTreeModel treeModel, ViewMode viewMode, String currentFilter) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

		// Get objects selected before refreshing tree
		HashMap<TreeObject, TreeObject> objectsSelected = new HashMap<TreeObject, TreeObject>();
		ArrayList<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
		TreePath[] pathsSelected = tree.getSelectionPaths();

		// If any node was selected
		if (pathsSelected != null) {
			for (TreePath pathSelected : pathsSelected) {
				Object obj = ((DefaultMutableTreeNode) pathSelected.getLastPathComponent()).getUserObject();
				if (obj instanceof TreeObject) {
					objectsSelected.put((TreeObject) obj, (TreeObject) obj);
				}
			}
		}

		// Get objects expanded before refreshing tree
		HashMap<TreeObject, TreeObject> objectsExpanded = new HashMap<TreeObject, TreeObject>();
		ArrayList<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();
		Enumeration<TreePath> enume = tree.getExpandedDescendants(new TreePath(root.getPath()));

		// If any node was expanded
		if (enume != null) {
			while (enume.hasMoreElements()) {
				TreePath p = enume.nextElement();
				Object obj = ((DefaultMutableTreeNode) p.getLastPathComponent()).getUserObject();
				if (obj instanceof TreeObject) {
					objectsExpanded.put((TreeObject) obj, (TreeObject) obj);
				}
			}
		}

		root.removeAllChildren();
		List<String> artistNamesList = new ArrayList<String>(structure.keySet());
		if (Kernel.getInstance().state.isUseSmartTagViewSorting()) {
			Collections.sort(artistNamesList, smartComparator);
		} else {
			Collections.sort(artistNamesList);
		}
		if (viewMode == ViewMode.ARTIST) {
			for (int i = 0; i < artistNamesList.size(); i++) {
				Artist artist = structure.get(artistNamesList.get(i));
				DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
				List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
				if (Kernel.getInstance().state.isUseSmartTagViewSorting()) {
					Collections.sort(albumNamesList, smartComparator);
				} else {
					Collections.sort(albumNamesList);
				}
				if (currentFilter == null || artist.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
					for (int j = 0; j < albumNamesList.size(); j++) {
						Album album = artist.getAlbum(albumNamesList.get(j));
						DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
						artistNode.add(albumNode);
					}
					root.add(artistNode);

					// Reload the tree to refresh content
					treeModel.reload();
					// If node was selected before refreshing...
					if (objectsSelected.containsKey(artistNode.getUserObject()))
						nodesToSelect.add(artistNode);
					// If node was expanded before refreshing...
					if (objectsExpanded.containsKey(artistNode.getUserObject()))
						nodesToExpand.add(artistNode);
					// Once tree has been refreshed, select previously selected nodes
					if (nodesToSelect.isEmpty())
						tree.setSelectionRow(0);
				}
			}
			treeModel.reload();
		} else {
			// Album view

			// Gather albums
			Map<String, Album> albums = new HashMap<String, Album>();
			DefaultMutableTreeNode albumNode = null;
			for (int i = 0; i < artistNamesList.size(); i++) {
				Artist artist = structure.get(artistNamesList.get(i));
				List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
				Collections.sort(albumNamesList);
				for (int j = 0; j < albumNamesList.size(); j++) {
					String a = albumNamesList.get(j);
					Album album = artist.getAlbum(a);
					if (currentFilter == null || album.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
						albums.put(album.getName() + artist.toString(), album);
					}
				}
			}

			// Then add them in the tree with the artists
			List<String> albumsList = new ArrayList<String>(albums.keySet());
			Collections.sort(albumsList);
			for (int i = 0; i < albumsList.size(); i++) {
				Album a = albums.get(albumsList.get(i));
				DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(a.getArtist());
				if (i == 0 || !albums.get(albumsList.get(i)).toString().equals((albums.get(albumsList.get(i - 1))).toString())) {
					albumNode = new DefaultMutableTreeNode(a);
					root.add(albumNode);
					// If node was selected before refreshing...
					if (objectsSelected.containsKey(albumNode.getUserObject()))
						nodesToSelect.add(albumNode);
				}
				if (albumNode != null)
					albumNode.add(artistNode);
				// That one is a workaround because the Artist class returns all audiofiles of an artist and not only 
				// the songs from a specific album, so we add that album again. If you have a better idea you can improve
				DefaultMutableTreeNode subAlbumNode = new DefaultMutableTreeNode(a.getArtist().getAlbum(a.getName()));
				artistNode.add(subAlbumNode);
			}

			// Reload the tree to refresh content
			treeModel.reload();

			// Once tree has been refreshed, expand previously expanded nodes
			for (DefaultMutableTreeNode node : nodesToExpand)
				tree.expandPath(new TreePath(node.getPath()));

			// Once tree has been refreshed, select previously selected nodes
			if (nodesToSelect.isEmpty()) {
				tree.setSelectionRow(0);
			} else {
				TreePath[] pathsToSelect = new TreePath[nodesToSelect.size()];
				int i = 0;
				for (DefaultMutableTreeNode node : nodesToSelect)
					pathsToSelect[i++] = new TreePath(node.getPath());
				tree.setSelectionPaths(pathsToSelect);
			}
		}
	}

	public static void refreshGenreTree(Map<String, Genre> structure, JTree tree, DefaultTreeModel treeModel, String currentFilter) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

		// Get objects selected before refreshing tree
		HashMap<TreeObject, TreeObject> objectsSelected = new HashMap<TreeObject, TreeObject>();
		ArrayList<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
		TreePath[] pathsSelected = tree.getSelectionPaths();

		// If any node was selected
		if (pathsSelected != null) {
			for (TreePath pathSelected : pathsSelected) {
				Object obj = ((DefaultMutableTreeNode) pathSelected.getLastPathComponent()).getUserObject();
				if (obj instanceof TreeObject) {
					objectsSelected.put((TreeObject) obj, (TreeObject) obj);
				}
			}
		}

		// Get objects expanded before refreshing tree
		HashMap<TreeObject, TreeObject> objectsExpanded = new HashMap<TreeObject, TreeObject>();
		ArrayList<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();
		Enumeration<TreePath> enume = tree.getExpandedDescendants(new TreePath(root.getPath()));

		// If any node was expanded
		if (enume != null) {
			while (enume.hasMoreElements()) {
				TreePath p = enume.nextElement();
				Object obj = ((DefaultMutableTreeNode) p.getLastPathComponent()).getUserObject();
				if (obj instanceof TreeObject) {
					objectsExpanded.put((TreeObject) obj, (TreeObject) obj);
				}
			}
		}

		root.removeAllChildren();
		List<String> genreNamesList = new ArrayList<String>(structure.keySet());
		Collections.sort(genreNamesList);

		for (int i = 0; i < genreNamesList.size(); i++) {
			Genre genre = structure.get(genreNamesList.get(i));
			if (currentFilter == null || genre.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
				DefaultMutableTreeNode genreNode = new DefaultMutableTreeNode(genre);
				// If node was selected before refreshing...
				if (objectsSelected.containsKey(genreNode.getUserObject()))
					nodesToSelect.add(genreNode);
				// If node was expanded before refreshing...
				if (objectsExpanded.containsKey(genreNode.getUserObject()))
					nodesToExpand.add(genreNode);
				List<String> artistNamesList = new ArrayList<String>(genre.getArtists().keySet());
				Collections.sort(artistNamesList);
				for (int j = 0; j < artistNamesList.size(); j++) {
					Artist artist = genre.getArtist(artistNamesList.get(j));
					DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
					List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
					Collections.sort(albumNamesList);
					for (int k = 0; k < albumNamesList.size(); k++) {
						Album album = artist.getAlbum(albumNamesList.get(k));
						DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
						artistNode.add(albumNode);
						genreNode.add(artistNode);
						// If node was selected before refreshing...
						if (objectsSelected.containsKey(artistNode.getUserObject()))
							nodesToSelect.add(artistNode);
						// If node was expanded before refreshing...
						if (objectsExpanded.containsKey(artistNode.getUserObject())
								&& objectsExpanded.containsKey(((DefaultMutableTreeNode) artistNode.getParent()).getUserObject()))
							nodesToExpand.add(artistNode);
					}
				}
				root.add(genreNode);
				// Let's refresh. No idea why, if you don't do this, genre view does not work sometimes
				treeModel.reload();
			}
		}

		treeModel.reload();
		// Once tree has been refreshed, expand previously expanded nodes
		for (DefaultMutableTreeNode node : nodesToExpand)
			tree.expandPath(new TreePath(node.getPath()));

		// Once tree has been refreshed, select previously selected nodes
		if (nodesToSelect.isEmpty()) {
			tree.setSelectionRow(0);
		} else {
			TreePath[] pathsToSelect = new TreePath[nodesToSelect.size()];
			int i = 0;
			for (DefaultMutableTreeNode node : nodesToSelect)
				pathsToSelect[i++] = new TreePath(node.getPath());
			tree.setSelectionPaths(pathsToSelect);
		}

	}
}
