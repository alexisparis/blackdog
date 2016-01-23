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

package net.sourceforge.atunes.gui.views.controls.playList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.PlayListTableModel;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class PlayListTable extends JTable {

	public enum PlayState {
		STOPPED, PLAYING, PAUSED
	}

	private static final long serialVersionUID = 9209069236823917569L;

	int playingSong;
	PlayState playState = PlayState.STOPPED;

	private JPopupMenu menu;

	JPopupMenu rightMenu;

	private JMenuItem playItem;

	private JMenu tagMenu;
	private JMenuItem editTagItem;
	private JMenuItem autoSetLyricsItem;
	private JMenuItem autoSetTrackNumberItem;
	private JMenuItem autoSetGenreItem;
	private JMenuItem autoSetTitleItem;

	private JMenuItem saveItem;
	private JMenuItem loadItem;

	private JMenuItem filterItem;

	private JMenuItem topItem;
	private JMenuItem upItem;
	private JMenuItem deleteItem;
	private JMenuItem downItem;
	private JMenuItem bottomItem;
	private JMenuItem infoItem;
	private JMenuItem clearItem;

	private JMenuItem favoriteSong;
	private JMenuItem favoriteArtist;
	private JMenuItem favoriteAlbum;

	private JMenuItem artistItem;
	private JMenuItem albumItem;

	// private JMenuItem showControls;

	private JMenuItem arrangeColumns;

	private JMenuItem smartPlayList;
	private JMenuItem add10RandomSongs;
	private JMenuItem add50RandomSongs;
	private JMenuItem add100RandomSongs;
	private JMenuItem add10SongsMostPlayed;
	private JMenuItem add50SongsMostPlayed;
	private JMenuItem add100SongsMostPlayed;
	private JMenuItem add1ArtistsMostPlayed;
	private JMenuItem add5ArtistsMostPlayed;
	private JMenuItem add10ArtistsMostPlayed;
	private JMenuItem add1AlbumsMostPlayed;
	private JMenuItem add5AlbumsMostPlayed;
	private JMenuItem add10AlbumsMostPlayed;

	List<PlayListColumnClickedListener> listeners = new ArrayList<PlayListColumnClickedListener>();

	public PlayListTable() {
		super();
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setShowGrid(false);

		// Add mouse click listener on table header
		getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Use left button to sort
				if (e.getButton() == MouseEvent.BUTTON1) {
					PlayListColumnModel colModel = (PlayListColumnModel) getColumnModel();
					int column = colModel.getColumnIndexAtPosition(e.getX());
					if (column >= 0) {
						Column columnClicked = ((PlayListTableModel) getModel()).getColumn(column);
						for (int i = 0; i < listeners.size(); i++) {
							listeners.get(i).columnClicked(columnClicked);
						}
					}
				}
				// Use right button to arrange columns
				else if (e.getButton() == MouseEvent.BUTTON3) {
					rightMenu.show(PlayListTable.this, e.getX(), e.getY());
				}
			}
		});

		// Set table model
		setModel(new PlayListTableModel(this));

		// Set column model
		setColumnModel(new PlayListColumnModel(this));

		// Disable autoresize, as we will control it
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// Set renderers
		PlayListRenderers.addRenderers(this);

		rightMenu = new JPopupMenu();
		arrangeColumns = setMenuItem(new JMenuItem(LanguageTool.getString("ARRANGE_COLUMNS")));
		rightMenu.add(arrangeColumns);

		menu = new JPopupMenu();

		playItem = setMenuItem(new JMenuItem(LanguageTool.getString("PLAY"), ImageLoader.PLAY_MENU));

		tagMenu = setMenu(new JMenu(LanguageTool.getString("TAGS")));
		editTagItem = setMenuItem(new JMenuItem(LanguageTool.getString("EDIT_TAG")));
		autoSetTrackNumberItem = setMenuItem(new JMenuItem(LanguageTool.getString("AUTO_SET_TRACK_NUMBER")));
		autoSetLyricsItem = setMenuItem(new JMenuItem(LanguageTool.getString("AUTO_SET_LYRICS")));
		autoSetGenreItem = setMenuItem(new JMenuItem(LanguageTool.getString("AUTO_SET_GENRE")));
		autoSetTitleItem = setMenuItem(new JMenuItem(LanguageTool.getString("AUTO_SET_TITLE")));
		tagMenu.add(editTagItem);
		tagMenu.add(autoSetLyricsItem);
		tagMenu.add(autoSetTrackNumberItem);
		tagMenu.add(autoSetTitleItem);
		tagMenu.add(autoSetTrackNumberItem);
		tagMenu.add(autoSetGenreItem);

		saveItem = setMenuItem(new JMenuItem(StringUtils.getString(LanguageTool.getString("SAVE"), "..."), ImageLoader.SAVE));
		loadItem = setMenuItem(new JMenuItem(StringUtils.getString(LanguageTool.getString("LOAD"), "..."), ImageLoader.FOLDER));
		filterItem = setMenuItem(new JMenuItem(LanguageTool.getString("FILTER"), ImageLoader.SEARCH));
		topItem = setMenuItem(new JMenuItem(LanguageTool.getString("MOVE_TO_TOP"), ImageLoader.GO_TOP));
		upItem = setMenuItem(new JMenuItem(LanguageTool.getString("MOVE_UP"), ImageLoader.GO_UP));
		deleteItem = setMenuItem(new JMenuItem(LanguageTool.getString("REMOVE"), ImageLoader.REMOVE));
		downItem = setMenuItem(new JMenuItem(LanguageTool.getString("MOVE_DOWN"), ImageLoader.GO_DOWN));
		bottomItem = setMenuItem(new JMenuItem(LanguageTool.getString("MOVE_TO_BOTTOM"), ImageLoader.GO_BOTTOM));
		infoItem = setMenuItem(new JMenuItem(LanguageTool.getString("INFO"), ImageLoader.INFO));
		clearItem = setMenuItem(new JMenuItem(LanguageTool.getString("CLEAR"), ImageLoader.CLEAR));
		favoriteSong = setMenuItem(new JMenuItem(LanguageTool.getString("SET_FAVORITE_SONG"), ImageLoader.FAVORITE));
		favoriteAlbum = setMenuItem(new JMenuItem(LanguageTool.getString("SET_FAVORITE_ALBUM"), ImageLoader.FAVORITE));
		favoriteArtist = setMenuItem(new JMenuItem(LanguageTool.getString("SET_FAVORITE_ARTIST"), ImageLoader.FAVORITE));
		artistItem = setMenuItem(new JMenuItem(LanguageTool.getString("SET_ARTIST_AS_PLAYLIST"), ImageLoader.ARTIST));
		albumItem = setMenuItem(new JMenuItem(LanguageTool.getString("SET_ALBUM_AS_PLAYLIST"), ImageLoader.ALBUM));
		// showControls = setMenuItem(new JCheckBoxMenuItem(LanguageTool.getString("SHOW_PLAYLIST_CONTROLS")));

		smartPlayList = new JMenu(LanguageTool.getString("SMART_PLAYLIST"));
		add10RandomSongs = new JMenuItem(LanguageTool.getString("ADD_10_RANDOM_SONGS"));
		add50RandomSongs = new JMenuItem(LanguageTool.getString("ADD_50_RANDOM_SONGS"));
		add100RandomSongs = new JMenuItem(LanguageTool.getString("ADD_100_RANDOM_SONGS"));
		smartPlayList.add(add10RandomSongs);
		smartPlayList.add(add50RandomSongs);
		smartPlayList.add(add100RandomSongs);
		smartPlayList.add(new JSeparator());
		add10SongsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_10_SONGS_MOST_PLAYED"));
		add50SongsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_50_SONGS_MOST_PLAYED"));
		add100SongsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_100_SONGS_MOST_PLAYED"));
		smartPlayList.add(add10SongsMostPlayed);
		smartPlayList.add(add50SongsMostPlayed);
		smartPlayList.add(add100SongsMostPlayed);
		smartPlayList.add(new JSeparator());
		add1AlbumsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_ALBUM_MOST_PLAYED"));
		add5AlbumsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_5_ALBUMS_MOST_PLAYED"));
		add10AlbumsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_10_ALBUMS_MOST_PLAYED"));
		smartPlayList.add(add1AlbumsMostPlayed);
		smartPlayList.add(add5AlbumsMostPlayed);
		smartPlayList.add(add10AlbumsMostPlayed);
		smartPlayList.add(new JSeparator());
		add1ArtistsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_ARTIST_MOST_PLAYED"));
		add5ArtistsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_5_ARTISTS_MOST_PLAYED"));
		add10ArtistsMostPlayed = new JMenuItem(LanguageTool.getString("ADD_10_ARTISTS_MOST_PLAYED"));
		smartPlayList.add(add1ArtistsMostPlayed);
		smartPlayList.add(add5ArtistsMostPlayed);
		smartPlayList.add(add10ArtistsMostPlayed);

		menu.add(playItem);
		menu.add(infoItem);
		menu.add(new JSeparator());
		menu.add(tagMenu);
		menu.add(new JSeparator());
		menu.add(saveItem);
		menu.add(loadItem);
		menu.add(new JSeparator());
		menu.add(filterItem);
		menu.add(new JSeparator());
		menu.add(deleteItem);
		menu.add(clearItem);
		menu.add(new JSeparator());
		menu.add(topItem);
		menu.add(upItem);
		menu.add(downItem);
		menu.add(bottomItem);
		menu.add(new JSeparator());
		menu.add(smartPlayList);
		menu.add(new JSeparator());
		menu.add(favoriteSong);
		menu.add(favoriteAlbum);
		menu.add(favoriteArtist);
		menu.add(new JSeparator());
		menu.add(artistItem);
		menu.add(albumItem);

		GuiUtils.applyComponentOrientation(menu);
	}

	public void addPlayListColumnClickedListener(PlayListColumnClickedListener l) {
		listeners.add(l);
	}

	/**
	 * @return the add100RandomSongs
	 */
	public JMenuItem getAdd100RandomSongs() {
		return add100RandomSongs;
	}

	/**
	 * @return the add100SongsMostPlayed
	 */
	public JMenuItem getAdd100SongsMostPlayed() {
		return add100SongsMostPlayed;
	}

	/**
	 * @return the add10AlbumsMostPlayed
	 */
	public JMenuItem getAdd10AlbumsMostPlayed() {
		return add10AlbumsMostPlayed;
	}

	/**
	 * @return the add10ArtistsMostPlayed
	 */
	public JMenuItem getAdd10ArtistsMostPlayed() {
		return add10ArtistsMostPlayed;
	}

	/**
	 * @return the add10RandomSongs
	 */
	public JMenuItem getAdd10RandomSongs() {
		return add10RandomSongs;
	}

	/**
	 * @return the add10SongsMostPlayed
	 */
	public JMenuItem getAdd10SongsMostPlayed() {
		return add10SongsMostPlayed;
	}

	/**
	 * @return the add1AlbumsMostPlayed
	 */
	public JMenuItem getAdd1AlbumsMostPlayed() {
		return add1AlbumsMostPlayed;
	}

	/**
	 * @return the add1ArtistsMostPlayed
	 */
	public JMenuItem getAdd1ArtistsMostPlayed() {
		return add1ArtistsMostPlayed;
	}

	/**
	 * @return the add50RandomSongs
	 */
	public JMenuItem getAdd50RandomSongs() {
		return add50RandomSongs;
	}

	/**
	 * @return the add50SongsMostPlayed
	 */
	public JMenuItem getAdd50SongsMostPlayed() {
		return add50SongsMostPlayed;
	}

	/**
	 * @return the add5AlbumsMostPlayed
	 */
	public JMenuItem getAdd5AlbumsMostPlayed() {
		return add5AlbumsMostPlayed;
	}

	/**
	 * @return the add5ArtistsMostPlayed
	 */
	public JMenuItem getAdd5ArtistsMostPlayed() {
		return add5ArtistsMostPlayed;
	}

	public JMenuItem getAlbumItem() {
		return albumItem;
	}

	/**
	 * @return the arrangeColumns
	 */
	public JMenuItem getArrangeColumns() {
		return arrangeColumns;
	}

	public JMenuItem getArtistItem() {
		return artistItem;
	}

	public JMenuItem getAutoSetGenreItem() {
		return autoSetGenreItem;
	}

	public JMenuItem getAutoSetLyricsItem() {
		return autoSetLyricsItem;
	}

	public JMenuItem getAutoSetTitleItem() {
		return autoSetTitleItem;
	}

	public JMenuItem getAutoSetTrackItem() {
		return autoSetTrackNumberItem;
	}

	public JMenuItem getBottomItem() {
		return bottomItem;
	}

	public JMenuItem getClearItem() {
		return clearItem;
	}

	public JMenuItem getDeleteItem() {
		return deleteItem;
	}

	public JMenuItem getDownItem() {
		return downItem;
	}

	/*
	 * public JMenuItem getShowControls() { return showControls; }
	 */
	public JMenuItem getEditTagItem() {
		return editTagItem;
	}

	public JMenuItem getFavoriteAlbum() {
		return favoriteAlbum;
	}

	public JMenuItem getFavoriteArtist() {
		return favoriteArtist;
	}

	public JMenuItem getFavoriteSong() {
		return favoriteSong;
	}

	public JMenuItem getFilterItem() {
		return filterItem;
	}

	public JMenuItem getInfoItem() {
		return infoItem;
	}

	public JMenuItem getLoadItem() {
		return loadItem;
	}

	public JPopupMenu getMenu() {
		return menu;
	}

	public int getPlayingSong() {
		return playingSong;
	}

	public JMenuItem getPlayItem() {
		return playItem;
	}

	public PlayState getPlayState() {
		return playState;
	}

	public JMenuItem getSaveItem() {
		return saveItem;
	}

	public JMenuItem getTopItem() {
		return topItem;
	}

	public JMenuItem getUpItem() {
		return upItem;
	}

	private JMenu setMenu(JMenu item) {
		item.setFont(Fonts.PLAY_LIST_FONT);
		return item;
	}

	private JMenuItem setMenuItem(JMenuItem item) {
		item.setFont(Fonts.PLAY_LIST_FONT);
		return item;
	}

	public void setPlayingSong(int row) {
		playingSong = row;
		((PlayListTableModel) getModel()).refreshTable();
	}

	public void setPlayState(PlayState playState) {
		this.playState = playState;
		((PlayListTableModel) getModel()).refreshTable();
	}
}
