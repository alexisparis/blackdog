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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.gui.views.Renderers;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.DragSourceTable;
import net.sourceforge.atunes.gui.views.controls.DragSourceTree;
import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;

import org.jvnet.substance.SubstanceDefaultTableCellRenderer;
import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * @author fleax
 * 
 */
public class NavigationPanel extends JPanel {

	private static final long serialVersionUID = -2900418193013495812L;

	private JTree navigationTree;
	private JScrollPane navigationTreeScrollPane;
	private DragSourceTable navigationTable;
	private JPanel navigationTableButtonPanel;
	private DefaultTreeModel navigationTreeModel;
	private CustomButton navigationTableInfoButton;
	private CustomButton navigationTableAddButton;
	private JRadioButtonMenuItem sortByTrack;
	private JRadioButtonMenuItem sortByFile;
	private JRadioButtonMenuItem sortByTitle;
	private JPanel navigationTableContainer;

	private JTree fileNavigationTree;
	private DefaultTreeModel fileNavigationTreeModel;

	private JTree favoritesTree;
	private DefaultTreeModel favoritesTreeModel;
	private JTabbedPane tabbedPane;

	private JTree deviceTree;
	private DefaultTreeModel deviceTreeModel;

	private JTree radioTree;
	private DefaultTreeModel radioTreeModel;

	private JTree podcastFeedTree;
	private DefaultTreeModel podcastFeedTreeModel;

	private JSplitPane splitPane;

	private PopUpButton prefsButton;
	private PopUpButton navigationTableSortButton;
	private JRadioButtonMenuItem showArtist;
	private JRadioButtonMenuItem showAlbum;
	private JRadioButtonMenuItem showGenre;

	private CustomButton addToPlayList;

	private JMenuItem expandTree;
	private JMenuItem collapseTree;

	private JLabel filterLabel;
	private JTextField filterTextField;
	private CustomButton clearFilterButton;

	private JPopupMenu favoriteMenu;
	private JMenuItem addToPlaylistMenuItem;
	private JMenuItem setAsPlaylistMenuItem;
	private JMenuItem playNowMenuItem;
	private JMenuItem removeFromFavoritesMenuItem;

	private JPopupMenu nonFavoriteMenu;
	private JMenuItem nonFavoriteAddToPlaylistMenuItem;
	private JMenuItem nonFavoriteSetAsPlaylistMenuItem;
	private JMenuItem nonFavoritePlayNowMenuItem;
	private JMenuItem nonFavoriteMarkPodcastEntryAsListened;
	private JMenuItem nonFavoriteSetAsFavoriteSongMenuItem;
	private JMenuItem nonFavoriteSetAsFavoriteAlbumMenuItem;
	private JMenuItem nonFavoriteSetAsFavoriteArtistMenuItem;
	private JMenuItem nonFavoriteEditTagMenuItem;
	private JMenuItem nonFavoriteClearTagMenuItem;
	private JMenuItem nonFavoriteExtractPictureMenuItem;
	private JMenuItem nonFavoriteEditTitlesMenuItem;
	private JMenuItem nonFavoriteSearch;
	private JMenuItem nonFavoriteSearchAt;
	private JMenuItem nonFavoriteRemovePhysicallyMenuItem;
	private JMenuItem nonFavoriteCopyToDeviceMenuItem;

	private JPopupMenu deviceMenu;
	private JMenuItem deviceCopyToRepositoryMenuItem;

	private JPopupMenu radioMenu;
	private JMenuItem radioAddRadioMenuItem;
	private JMenuItem radioRenameRadioMenuItem;
	private JMenuItem radioRemoveRadioMenuItem;

	private JPopupMenu podcastFeedMenu;
	private JMenuItem podcastFeedAddPodcastFeedMenuItem;
	private JMenuItem podcastFeedRenamePodcastFeedMenuItem;
	private JMenuItem podcastFeedRemovePodcastFeedMenuItem;
	private JMenuItem podcastFeedMarkAsListenedMenuItem;

	public NavigationPanel() {
		super(new BorderLayout(), true);
		addContent();
	}

	private void addContent() {
		JPanel treePanel = new JPanel(new BorderLayout());
		navigationTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode(LanguageTool.getString("REPOSITORY")));
		navigationTree = new DragSourceTree(navigationTreeModel);
		navigationTree.setToggleClickCount(0);
		navigationTree.setCellRenderer(Renderers.NAVIGATION_TREE_RENDERER);
		ToolTipManager.sharedInstance().registerComponent(navigationTree);
		navigationTreeScrollPane = new JScrollPane(navigationTree);

		fileNavigationTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode(LanguageTool.getString("FOLDERS")));
		fileNavigationTree = new DragSourceTree(fileNavigationTreeModel);
		fileNavigationTree.setToggleClickCount(0);
		fileNavigationTree.setCellRenderer(Renderers.FILE_TREE_RENDERER);
		JScrollPane scrollPane3 = new JScrollPane(fileNavigationTree);

		favoritesTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode(LanguageTool.getString("FAVORITES")));
		favoritesTree = new DragSourceTree(favoritesTreeModel);
		favoritesTree.setToggleClickCount(0);
		favoritesTree.setCellRenderer(Renderers.FAVORITE_TREE_RENDERER);
		JScrollPane scrollPane4 = new JScrollPane(favoritesTree);

		deviceTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode(LanguageTool.getString("DEVICE")));
		deviceTree = new DragSourceTree(deviceTreeModel);
		deviceTree.setToggleClickCount(0);
		deviceTree.setCellRenderer(Renderers.DEVICE_BY_TAG_TREE_RENDERER);
		JScrollPane scrollPane5 = new JScrollPane(deviceTree);

		radioTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode(LanguageTool.getString("RADIO")));
		radioTree = new DragSourceTree(radioTreeModel);
		radioTree.setToggleClickCount(0);
		radioTree.setCellRenderer(Renderers.RADIO_TREE_RENDERER);
		JScrollPane scrollPane6 = new JScrollPane(radioTree);

		podcastFeedTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode(LanguageTool.getString("PODCAST_FEEDS")));
		podcastFeedTree = new DragSourceTree(podcastFeedTreeModel);
		podcastFeedTree.setToggleClickCount(0);
		podcastFeedTree.setCellRenderer(Renderers.PODCAST_FEED_TREE_RENDERER);
		podcastFeedTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane scrollPane7 = new JScrollPane(podcastFeedTree);

		favoriteMenu = new JPopupMenu();
		addToPlaylistMenuItem = new JMenuItem(LanguageTool.getString("ADD_TO_PLAYLIST"));
		setAsPlaylistMenuItem = new JMenuItem(LanguageTool.getString("SET_AS_PLAYLIST"));
		playNowMenuItem = new JMenuItem(LanguageTool.getString("PLAY_NOW"));
		removeFromFavoritesMenuItem = new JMenuItem(LanguageTool.getString("REMOVE_FROM_FAVORITES"));
		favoriteMenu.add(addToPlaylistMenuItem);
		favoriteMenu.add(setAsPlaylistMenuItem);
		favoriteMenu.add(playNowMenuItem);
		favoriteMenu.add(removeFromFavoritesMenuItem);

		nonFavoriteMenu = new JPopupMenu();
		nonFavoriteAddToPlaylistMenuItem = new JMenuItem(LanguageTool.getString("ADD_TO_PLAYLIST"));
		nonFavoriteSetAsPlaylistMenuItem = new JMenuItem(LanguageTool.getString("SET_AS_PLAYLIST"));
		nonFavoritePlayNowMenuItem = new JMenuItem(LanguageTool.getString("PLAY_NOW"));
		nonFavoriteMarkPodcastEntryAsListened = new JMenuItem(LanguageTool.getString("MARK_PODCAST_ENTRY_AS_LISTENED"));
		nonFavoriteEditTagMenuItem = new JMenuItem(LanguageTool.getString("EDIT_TAG"));
		nonFavoriteEditTitlesMenuItem = new JMenuItem(LanguageTool.getString("EDIT_TITLES"));
		nonFavoriteClearTagMenuItem = new JMenuItem(LanguageTool.getString("CLEAR_TAG"));
		nonFavoriteExtractPictureMenuItem = new JMenuItem(LanguageTool.getString("EXTRACT_PICTURE"));
		nonFavoriteSetAsFavoriteSongMenuItem = new JMenuItem(LanguageTool.getString("SET_FAVORITE_SONG"));
		nonFavoriteSetAsFavoriteAlbumMenuItem = new JMenuItem(LanguageTool.getString("SET_FAVORITE_ALBUM"));
		nonFavoriteSetAsFavoriteArtistMenuItem = new JMenuItem(LanguageTool.getString("SET_FAVORITE_ARTIST"));
		nonFavoriteSearch = new JMenuItem(LanguageTool.getString("SEARCH_ARTIST"));
		nonFavoriteSearchAt = new JMenuItem(StringUtils.getString(LanguageTool.getString("SEARCH_ARTIST_AT"), "..."));
		nonFavoriteRemovePhysicallyMenuItem = new JMenuItem(LanguageTool.getString("REMOVE_FROM_DISK"));
		nonFavoriteCopyToDeviceMenuItem = new JMenuItem(LanguageTool.getString("COPY_TO_DEVICE"));
		nonFavoriteMenu.add(nonFavoriteAddToPlaylistMenuItem);
		nonFavoriteMenu.add(nonFavoriteSetAsPlaylistMenuItem);
		nonFavoriteMenu.add(nonFavoritePlayNowMenuItem);
		nonFavoriteMenu.add(new JSeparator());
		nonFavoriteMenu.add(nonFavoriteMarkPodcastEntryAsListened);
		nonFavoriteMenu.add(new JSeparator());
		nonFavoriteMenu.add(nonFavoriteEditTagMenuItem);
		nonFavoriteMenu.add(nonFavoriteEditTitlesMenuItem);
		nonFavoriteMenu.add(nonFavoriteClearTagMenuItem);
		nonFavoriteMenu.add(nonFavoriteExtractPictureMenuItem);
		nonFavoriteMenu.add(new JSeparator());
		nonFavoriteMenu.add(nonFavoriteRemovePhysicallyMenuItem);
		nonFavoriteMenu.add(new JSeparator());
		nonFavoriteMenu.add(nonFavoriteCopyToDeviceMenuItem);
		nonFavoriteMenu.add(new JSeparator());
		nonFavoriteMenu.add(nonFavoriteSetAsFavoriteSongMenuItem);
		nonFavoriteMenu.add(nonFavoriteSetAsFavoriteAlbumMenuItem);
		nonFavoriteMenu.add(nonFavoriteSetAsFavoriteArtistMenuItem);
		nonFavoriteMenu.add(new JSeparator());
		nonFavoriteMenu.add(nonFavoriteSearch);
		nonFavoriteMenu.add(nonFavoriteSearchAt);

		deviceMenu = new JPopupMenu();
		deviceCopyToRepositoryMenuItem = new JMenuItem(LanguageTool.getString("COPY_TO_REPOSITORY"));
		deviceMenu.add(deviceCopyToRepositoryMenuItem);

		radioMenu = new JPopupMenu();
		radioAddRadioMenuItem = new JMenuItem(LanguageTool.getString("ADD_RADIO"));
		radioRenameRadioMenuItem = new JMenuItem(LanguageTool.getString("RENAME_RADIO"));
		radioRemoveRadioMenuItem = new JMenuItem(LanguageTool.getString("REMOVE_RADIO"));
		radioMenu.add(radioAddRadioMenuItem);
		radioMenu.add(radioRenameRadioMenuItem);
		radioMenu.add(radioRemoveRadioMenuItem);

		podcastFeedMenu = new JPopupMenu();
		podcastFeedAddPodcastFeedMenuItem = new JMenuItem(LanguageTool.getString("ADD_PODCAST_FEED"));
		podcastFeedRenamePodcastFeedMenuItem = new JMenuItem(LanguageTool.getString("RENAME_PODCAST_FEED"));
		podcastFeedRemovePodcastFeedMenuItem = new JMenuItem(LanguageTool.getString("REMOVE_PODCAST_FEED"));
		podcastFeedMarkAsListenedMenuItem = new JMenuItem(LanguageTool.getString("MARK_PODCAST_AS_LISTENED"));
		podcastFeedMenu.add(podcastFeedAddPodcastFeedMenuItem);
		podcastFeedMenu.add(podcastFeedRenamePodcastFeedMenuItem);
		podcastFeedMenu.add(podcastFeedMarkAsListenedMenuItem);
		podcastFeedMenu.add(podcastFeedRemovePodcastFeedMenuItem);

		tabbedPane = new JTabbedPane(SwingConstants.LEFT);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder());
		tabbedPane.addTab(LanguageTool.getString("TAGS"), ImageLoader.INFO, navigationTreeScrollPane, LanguageTool.getString("TAGS_TAB_TOOLTIP"));
		tabbedPane.addTab(LanguageTool.getString("FOLDERS"), ImageLoader.FOLDER, scrollPane3, LanguageTool.getString("FOLDER_TAB_TOOLTIP"));
		tabbedPane.addTab(LanguageTool.getString("FAVORITES"), ImageLoader.FAVORITE, scrollPane4, LanguageTool.getString("FAVORITES_TAB_TOOLTIP"));
		tabbedPane.addTab(LanguageTool.getString("DEVICE"), ImageLoader.DEVICE, scrollPane5, LanguageTool.getString("DEVICE_VIEW"));
		tabbedPane.addTab(LanguageTool.getString("RADIO"), ImageLoader.RADIO_LITTLE, scrollPane6, LanguageTool.getString("RADIO_VIEW"));
		tabbedPane.addTab(LanguageTool.getString("PODCAST_FEEDS"), ImageLoader.RSS_LITTLE, scrollPane7, LanguageTool.getString("PODCAST_FEED_VIEW"));

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		addToPlayList = new CustomButton(ImageLoader.ADD, null);
		addToPlayList.setPreferredSize(new Dimension(20, 20));
		addToPlayList.setToolTipText(LanguageTool.getString("ADD_TO_PLAYLIST_TOOLTIP"));

		prefsButton = new PopUpButton(LanguageTool.getString("OPTIONS"), PopUpButton.TOP_RIGHT);
		showArtist = new JRadioButtonMenuItem(LanguageTool.getString("SHOW_ARTISTS"), ImageLoader.ARTIST);
		showAlbum = new JRadioButtonMenuItem(LanguageTool.getString("SHOW_ALBUMS"), ImageLoader.ALBUM);
		showGenre = new JRadioButtonMenuItem(LanguageTool.getString("SHOW_GENRE"), ImageLoader.GENRE);
		prefsButton.add(showArtist);
		prefsButton.add(showAlbum);
		prefsButton.add(showGenre);
		showArtist.setOpaque(true);
		showAlbum.setOpaque(true);
		showGenre.setOpaque(true);
		prefsButton.add(new JSeparator());
		expandTree = new JMenuItem(LanguageTool.getString("EXPAND"));
		prefsButton.add(expandTree);
		expandTree.setOpaque(true);
		collapseTree = new JMenuItem(LanguageTool.getString("COLLAPSE"));
		prefsButton.add(collapseTree);
		collapseTree.setOpaque(true);

		// Filter controls
		filterLabel = new JLabel(LanguageTool.getString("FILTER"));
		filterTextField = new JTextField();
		filterTextField.setToolTipText(LanguageTool.getString("FILTER_TEXTFIELD_TOOLTIP"));
		clearFilterButton = new CustomButton(ImageLoader.UNDO, null);
		clearFilterButton.setPreferredSize(new Dimension(20, 20));
		clearFilterButton.setToolTipText(LanguageTool.getString("CLEAR_FILTER_BUTTON_TOOLTIP"));

		ButtonGroup group2 = new ButtonGroup();
		group2.add(showArtist);
		group2.add(showAlbum);
		group2.add(showGenre);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(1, 1, 1, 5);
		buttonPanel.add(prefsButton, c);

		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(2, 0, 0, 2);
		buttonPanel.add(filterLabel, c);

		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(1, 1, 1, 2);
		buttonPanel.add(filterTextField, c);

		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(1, 1, 1, 3);
		buttonPanel.add(clearFilterButton, c);

		c.gridx = 4;
		c.gridy = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(1, 1, 1, 0);
		buttonPanel.add(addToPlayList, c);

		treePanel.add(tabbedPane, BorderLayout.CENTER);
		treePanel.add(buttonPanel, BorderLayout.SOUTH);

		navigationTable = new DragSourceTable();
		navigationTable.getTableHeader().setResizingAllowed(false);
		navigationTable.getTableHeader().setReorderingAllowed(false);
		navigationTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		navigationTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

		navigationTable.setColumnModel(new DefaultTableColumnModel() {
			private static final long serialVersionUID = 276248774998741620L;

			@Override
			public void addColumn(TableColumn aColumn) {
				super.addColumn(aColumn);
				if (aColumn.getModelIndex() == 0)
					aColumn.setMaxWidth(18);
				else if (aColumn.getModelIndex() == 2)
					aColumn.setMaxWidth(50);
			}
		});

		navigationTable.setDefaultRenderer(Integer.class, new SubstanceDefaultTableCellRenderer() {
			private static final long serialVersionUID = 1337377851290885658L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
				ImageIcon icon = ImageLoader.EMPTY;
				Integer val = (Integer) value;
				if (val == NavigationTableModel.FAVORITE)
					icon = ImageLoader.FAVORITE;
				else if (val == NavigationTableModel.NOT_LISTENED_ENTRY)
					icon = ImageLoader.NEW_PODCAST_ENTRY;
				((JLabel) comp).setIcon(icon);
				return comp;

			}
		});

		navigationTable.setDefaultRenderer(String.class, new SubstanceDefaultTableCellRenderer() {
			private static final long serialVersionUID = 8693307342964711167L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				GuiUtils.applyComponentOrientation(((JLabel) comp));
				return comp;
			}
		});

		navigationTable.setDefaultRenderer(Long.class, new SubstanceDefaultTableCellRenderer() {
			private static final long serialVersionUID = 7614440163302045553L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component comp;
				if (((Long) value) > 0) {
					comp = super.getTableCellRendererComponent(table, TimeUtils.seconds2String((Long) value), isSelected, hasFocus, row, column);
					((JLabel) comp).setHorizontalAlignment(SwingConstants.RIGHT);
				} else {
					comp = super.getTableCellRendererComponent(table, "-", isSelected, hasFocus, row, column);
					((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
				}
				return comp;
			}
		});

		JScrollPane scrollPane2 = new JScrollPane(navigationTable);
		//scrollPane2.setBorder(BorderFactory.createEmptyBorder());

		navigationTableContainer = new JPanel(new BorderLayout());
		//navigationTableContainer.setMinimumSize(d);
		navigationTableButtonPanel = new JPanel(new GridBagLayout());

		navigationTableInfoButton = new CustomButton(ImageLoader.INFO, null);
		navigationTableInfoButton.setPreferredSize(new Dimension(20, 20));
		navigationTableInfoButton.setToolTipText(LanguageTool.getString("INFO_BUTTON_TOOLTIP"));
		navigationTableAddButton = new CustomButton(ImageLoader.ADD, null);
		navigationTableAddButton.setPreferredSize(new Dimension(20, 20));
		navigationTableAddButton.setToolTipText(LanguageTool.getString("ADD_TO_PLAYLIST_TOOLTIP"));

		navigationTableSortButton = new PopUpButton(LanguageTool.getString("OPTIONS"), PopUpButton.TOP_RIGHT);
		sortByTrack = new JRadioButtonMenuItem(LanguageTool.getString("SORT_BY_TRACK_NUMBER"));
		sortByTitle = new JRadioButtonMenuItem(LanguageTool.getString("SORT_BY_TITLE"));
		sortByFile = new JRadioButtonMenuItem(LanguageTool.getString("SORT_BY_FILE_NAME"));
		navigationTableSortButton.add(sortByTrack);
		navigationTableSortButton.add(sortByTitle);
		navigationTableSortButton.add(sortByFile);
		sortByTrack.setOpaque(true);
		sortByTitle.setOpaque(true);
		sortByFile.setOpaque(true);
		ButtonGroup group = new ButtonGroup();
		group.add(sortByTrack);
		group.add(sortByTitle);
		group.add(sortByFile);

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(1, 1, 1, 0);
		navigationTableButtonPanel.add(navigationTableSortButton, c);

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.EAST;
		navigationTableButtonPanel.add(navigationTableInfoButton, c);
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		navigationTableButtonPanel.add(navigationTableAddButton, c);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		navigationTableContainer.add(scrollPane2, BorderLayout.CENTER);
		navigationTableContainer.add(navigationTableButtonPanel, BorderLayout.SOUTH);
		splitPane.add(treePanel);
		splitPane.add(navigationTableContainer);

		// Split pane divider should use background color (can maybe removed with future Substance versions)
		splitPane.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
		treePanel.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.FALSE);
		navigationTableContainer.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.FALSE);

		splitPane.setResizeWeight(0.8);

		add(splitPane);

		GuiUtils.applyComponentOrientation(favoriteMenu, nonFavoriteMenu, deviceMenu, radioMenu, podcastFeedMenu, navigationTableSortButton);
	}

	public CustomButton getAddToPlayList() {
		return addToPlayList;
	}

	public JMenuItem getAddToPlaylistMenuItem() {
		return addToPlaylistMenuItem;
	}

	public CustomButton getClearFilterButton() {
		return clearFilterButton;
	}

	public JMenuItem getCollapseTree() {
		return collapseTree;
	}

	public JMenuItem getDeviceCopyToRepositoryMenuItem() {
		return deviceCopyToRepositoryMenuItem;
	}

	public JPopupMenu getDeviceMenu() {
		return deviceMenu;
	}

	public JTree getDeviceTree() {
		return deviceTree;
	}

	public DefaultTreeModel getDeviceTreeModel() {
		return deviceTreeModel;
	}

	public JMenuItem getExpandTree() {
		return expandTree;
	}

	public JPopupMenu getFavoriteMenu() {
		return favoriteMenu;
	}

	public JTree getFavoritesTree() {
		return favoritesTree;
	}

	public DefaultTreeModel getFavoritesTreeModel() {
		return favoritesTreeModel;
	}

	public JTree getFileNavigationTree() {
		return fileNavigationTree;
	}

	public DefaultTreeModel getFileNavigationTreeModel() {
		return fileNavigationTreeModel;
	}

	public JLabel getFilterLabel() {
		return filterLabel;
	}

	public JTextField getFilterTextField() {
		return filterTextField;
	}

	public JTable getNavigationTable() {
		return navigationTable;
	}

	public CustomButton getNavigationTableAddButton() {
		return navigationTableAddButton;
	}

	public JPanel getNavigationTableContainer() {
		return navigationTableContainer;
	}

	public CustomButton getNavigationTableInfoButton() {
		return navigationTableInfoButton;
	}

	/**
	 * @return the navigationTableSortButton
	 */
	public PopUpButton getNavigationTableSortButton() {
		return navigationTableSortButton;
	}

	public JTree getNavigationTree() {
		return navigationTree;
	}

	public DefaultTreeModel getNavigationTreeModel() {
		return navigationTreeModel;
	}

	public JScrollPane getNavigationTreeScrollPane() {
		return navigationTreeScrollPane;
	}

	public JMenuItem getNonFavoriteAddToPlaylistMenuItem() {
		return nonFavoriteAddToPlaylistMenuItem;
	}

	public JMenuItem getNonFavoriteClearTagMenuItem() {
		return nonFavoriteClearTagMenuItem;
	}

	/**
	 * @return the nonFavoriteCopyToDeviceMenuItem
	 */
	public JMenuItem getNonFavoriteCopyToDeviceMenuItem() {
		return nonFavoriteCopyToDeviceMenuItem;
	}

	public JMenuItem getNonFavoriteEditTagMenuItem() {
		return nonFavoriteEditTagMenuItem;
	}

	public JMenuItem getNonFavoriteEditTitlesMenuItem() {
		return nonFavoriteEditTitlesMenuItem;
	}

	public JMenuItem getNonFavoriteExtractPictureMenuItem() {
		return nonFavoriteExtractPictureMenuItem;
	}

	public JMenuItem getNonFavoriteMarkPodcastEntryAsListened() {
		return nonFavoriteMarkPodcastEntryAsListened;
	}

	public JPopupMenu getNonFavoriteMenu() {
		return nonFavoriteMenu;
	}

	/**
	 * @return the nonFavoritePlayNowMenuItem
	 */
	public JMenuItem getNonFavoritePlayNowMenuItem() {
		return nonFavoritePlayNowMenuItem;
	}

	/**
	 * @return the nonFavoriteRemovePhysicallyMenuItem
	 */
	public JMenuItem getNonFavoriteRemovePhysicallyMenuItem() {
		return nonFavoriteRemovePhysicallyMenuItem;
	}

	public JMenuItem getNonFavoriteSearch() {
		return nonFavoriteSearch;
	}

	public JMenuItem getNonFavoriteSearchAt() {
		return nonFavoriteSearchAt;
	}

	public JMenuItem getNonFavoriteSetAsFavoriteAlbumMenuItem() {
		return nonFavoriteSetAsFavoriteAlbumMenuItem;
	}

	public JMenuItem getNonFavoriteSetAsFavoriteArtistMenuItem() {
		return nonFavoriteSetAsFavoriteArtistMenuItem;
	}

	public JMenuItem getNonFavoriteSetAsFavoriteSongMenuItem() {
		return nonFavoriteSetAsFavoriteSongMenuItem;
	}

	public JMenuItem getNonFavoriteSetAsPlaylistMenuItem() {
		return nonFavoriteSetAsPlaylistMenuItem;
	}

	/**
	 * @return the playNowMenuItem
	 */
	public JMenuItem getPlayNowMenuItem() {
		return playNowMenuItem;
	}

	/**
	 * @return the podcastFeedAddPodcastFeed
	 */
	public JMenuItem getPodcastFeedAddPodcastFeedMenuItem() {
		return podcastFeedAddPodcastFeedMenuItem;
	}

	/**
	 * @return the podcastFeedMarkAsListenedMenuItem
	 */
	public JMenuItem getPodcastFeedMarkAsListenedMenuItem() {
		return podcastFeedMarkAsListenedMenuItem;
	}

	/**
	 * @return the podcastFeedMenu
	 */
	public JPopupMenu getPodcastFeedMenu() {
		return podcastFeedMenu;
	}

	/**
	 * @return the podcastFeedRemovePodcastFeedMenuItem
	 */
	public JMenuItem getPodcastFeedRemovePodcastFeedMenuItem() {
		return podcastFeedRemovePodcastFeedMenuItem;
	}

	public JMenuItem getPodcastFeedRenamePodcastFeedMenuItem() {
		return podcastFeedRenamePodcastFeedMenuItem;
	}

	/**
	 * @return the podcastFeedTree
	 */
	public JTree getPodcastFeedTree() {
		return podcastFeedTree;
	}

	/**
	 * @return the podcastFeedTreeModel
	 */
	public DefaultTreeModel getPodcastFeedTreeModel() {
		return podcastFeedTreeModel;
	}

	public PopUpButton getPrefsButton() {
		return prefsButton;
	}

	/**
	 * @return the radioAddRadio
	 */
	public JMenuItem getRadioAddRadioMenuItem() {
		return radioAddRadioMenuItem;
	}

	/**
	 * @return the radioMenu
	 */
	public JPopupMenu getRadioMenu() {
		return radioMenu;
	}

	/**
	 * @return the radioRemoveRadioMenuItem
	 */
	public JMenuItem getRadioRemoveRadioMenuItem() {
		return radioRemoveRadioMenuItem;
	}

	public JMenuItem getRadioRenameRadioMenuItem() {
		return radioRenameRadioMenuItem;
	}

	/**
	 * @return the radioTree
	 */
	public JTree getRadioTree() {
		return radioTree;
	}

	/**
	 * @return the radioTreeModel
	 */
	public DefaultTreeModel getRadioTreeModel() {
		return radioTreeModel;
	}

	public JMenuItem getRemoveFromFavoritesMenuItem() {
		return removeFromFavoritesMenuItem;
	}

	public JMenuItem getSetAsPlaylistMenuItem() {
		return setAsPlaylistMenuItem;
	}

	public JRadioButtonMenuItem getShowAlbum() {
		return showAlbum;
	}

	public JRadioButtonMenuItem getShowArtist() {
		return showArtist;
	}

	public JRadioButtonMenuItem getShowGenre() {
		return showGenre;
	}

	public JRadioButtonMenuItem getSortByFile() {
		return sortByFile;
	}

	public JRadioButtonMenuItem getSortByTitle() {
		return sortByTitle;
	}

	public JRadioButtonMenuItem getSortByTrack() {
		return sortByTrack;
	}

	public JSplitPane getSplitPane() {
		return splitPane;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

}
