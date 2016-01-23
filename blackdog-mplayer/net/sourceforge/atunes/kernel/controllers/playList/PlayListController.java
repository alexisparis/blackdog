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

package net.sourceforge.atunes.kernel.controllers.playList;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.model.PlayListTableModel;
import net.sourceforge.atunes.gui.views.controls.playList.Column;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListColumnClickedListener;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListColumnSelector;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListColumns;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.model.PanelController;
import net.sourceforge.atunes.kernel.executors.BackgroundExecutor;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.TagEditionOperations;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.log4j.LogCategories;

public class PlayListController extends PanelController<PlayListPanel> implements PlayListColumnClickedListener {

	private boolean allowScrolling = true;
	private Rectangle visibleRect;

	public PlayListController(PlayListPanel panel) {
		super(panel);
		addBindings();
		addStateBindings();
	}

	@Override
	protected void addBindings() {
		final PlayListTable table = panelControlled.getPlayListTable();
		table.addPlayListColumnClickedListener(this);

		// Keys
		table.getDeleteItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		table.getPlayItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		table.getInfoItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		table.getSaveItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		table.getLoadItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		table.getClearItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		table.getTopItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		table.getDownItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, ActionEvent.CTRL_MASK));
		table.getUpItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, ActionEvent.CTRL_MASK));
		table.getBottomItem().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
		table.getFavoriteSong().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		table.getFavoriteAlbum().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		table.getFavoriteArtist().setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		// End Keys

		PlayListListener listener = new PlayListListener(table, this);

		table.getArrangeColumns().addActionListener(listener);
		table.getPlayItem().addActionListener(listener);
		table.getEditTagItem().addActionListener(listener);
		table.getAutoSetLyricsItem().addActionListener(listener);
		table.getAutoSetTrackItem().addActionListener(listener);
		table.getAutoSetGenreItem().addActionListener(listener);
		table.getAutoSetTitleItem().addActionListener(listener);
		table.getSaveItem().addActionListener(listener);
		table.getLoadItem().addActionListener(listener);
		table.getFilterItem().addActionListener(listener);
		table.getTopItem().addActionListener(listener);
		table.getUpItem().addActionListener(listener);
		table.getDownItem().addActionListener(listener);
		table.getBottomItem().addActionListener(listener);
		table.getDeleteItem().addActionListener(listener);
		table.getInfoItem().addActionListener(listener);
		table.getClearItem().addActionListener(listener);
		table.addMouseListener(listener);

		table.getSelectionModel().addListSelectionListener(listener);
		table.getFavoriteSong().addActionListener(listener);
		table.getFavoriteAlbum().addActionListener(listener);
		table.getFavoriteArtist().addActionListener(listener);
		table.getArtistItem().addActionListener(listener);
		table.getAlbumItem().addActionListener(listener);

		table.getAdd10RandomSongs().addActionListener(listener);
		table.getAdd50RandomSongs().addActionListener(listener);
		table.getAdd100RandomSongs().addActionListener(listener);
		table.getAdd10SongsMostPlayed().addActionListener(listener);
		table.getAdd50SongsMostPlayed().addActionListener(listener);
		table.getAdd100SongsMostPlayed().addActionListener(listener);
		table.getAdd1AlbumsMostPlayed().addActionListener(listener);
		table.getAdd5AlbumsMostPlayed().addActionListener(listener);
		table.getAdd10AlbumsMostPlayed().addActionListener(listener);
		table.getAdd1ArtistsMostPlayed().addActionListener(listener);
		table.getAdd5ArtistsMostPlayed().addActionListener(listener);
		table.getAdd10ArtistsMostPlayed().addActionListener(listener);
	}

	@Override
	protected void addStateBindings() {
		disablePlayListItems(true, false);
	}

	public void arrangeColumns() {
		// Show column selector
		PlayListColumnSelector selector = VisualHandler.getInstance().getPlayListColumnSelector();
		selector.setColumns(PlayListColumns.getColumnsForSelection());
		selector.setVisible(true);

		// Apply changes
		((PlayListTableModel) panelControlled.getPlayListTable().getModel()).arrangeColumns(true);

	}

	/**
	 * Notifies when user clicks on a column header
	 */
	public void columnClicked(Column columnClicked) {
		logger.debug(LogCategories.CONTROLLER, new String[] { columnClicked.getHeaderText() });

		// Sort play list
		PlayListHandler.getInstance().sortPlayList(columnClicked.getComparator());
	}

	public void deleteSelection() {
		logger.debug(LogCategories.CONTROLLER);

		PlayListTable table = VisualHandler.getInstance().getPlayListTable();
		int[] rows = table.getSelectedRows();
		if (rows.length > 0) {
			((PlayListTableModel) table.getModel()).removeSongs(rows);
			panelControlled.getPlayListTable().getSelectionModel().setSelectionInterval(-1, -1);
			PlayListHandler.getInstance().removeSongs(rows);
		}
	}

	public void disablePlayListItems(boolean disable, boolean radioOrPodcastFeedEntrySelected) {
		PlayListTable table = panelControlled.getPlayListTable();
		table.getPlayItem().setEnabled(!disable);
		table.getInfoItem().setEnabled(!disable);
		table.getDeleteItem().setEnabled(!disable);
		table.getClearItem().setEnabled(true);
		table.getTopItem().setEnabled(!disable);
		table.getUpItem().setEnabled(!disable);
		table.getDownItem().setEnabled(!disable);
		table.getBottomItem().setEnabled(!disable);
		table.getFavoriteSong().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		table.getFavoriteAlbum().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		table.getFavoriteArtist().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		table.getArtistItem().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		table.getAlbumItem().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		table.getEditTagItem().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		table.getAutoSetGenreItem().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		table.getAutoSetTitleItem().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		table.getAutoSetTrackItem().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		table.getAutoSetLyricsItem().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
	}

	public JScrollPane getMainPlayListScrollPane() {
		return panelControlled.getPlayListTableScroll();
	}

	public PlayListTable getMainPlayListTable() {
		return panelControlled.getPlayListTable();
	}

	public List<AudioObject> getSelectedAudioObjects() {
		return getSelectedAudioObjects(VisualHandler.getInstance().getPlayListTable());
	}

	private List<AudioObject> getSelectedAudioObjects(JTable table) {
		int[] selectedRows = table.getSelectedRows();
		List<AudioObject> songs = new ArrayList<AudioObject>();
		for (int element : selectedRows) {
			AudioObject file = ((PlayListTableModel) table.getModel()).getFileAt(element);
			songs.add(file);
		}
		return songs;
	}

	public void moveDown() {
		logger.debug(LogCategories.CONTROLLER);

		PlayListTable table = VisualHandler.getInstance().getPlayListTable();
		int[] rows = table.getSelectedRows();
		if (rows.length > 0 && rows[rows.length - 1] < table.getRowCount() - 1) {
			((PlayListTableModel) table.getModel()).moveDown(rows);
			PlayListHandler.getInstance().moveDown(rows);
			panelControlled.getPlayListTable().getSelectionModel().setSelectionInterval(rows[0] + 1, rows[rows.length - 1] + 1);
		}
	}

	public void moveToBottom() {
		logger.debug(LogCategories.CONTROLLER);

		PlayListTable table = VisualHandler.getInstance().getPlayListTable();
		int[] rows = table.getSelectedRows();
		if (rows.length > 0 && rows[rows.length - 1] < table.getRowCount() - 1) {
			((PlayListTableModel) table.getModel()).moveToBottom(rows);
			PlayListHandler.getInstance().moveToBottom(rows);
			panelControlled.getPlayListTable().getSelectionModel().setSelectionInterval(panelControlled.getPlayListTable().getRowCount() - rows.length,
					panelControlled.getPlayListTable().getRowCount() - 1);
		}
	}

	public void moveToTop() {
		logger.debug(LogCategories.CONTROLLER);

		PlayListTable table = VisualHandler.getInstance().getPlayListTable();
		int[] rows = table.getSelectedRows();
		if (rows.length > 0 && rows[0] > 0) {
			((PlayListTableModel) table.getModel()).moveToTop(rows);
			PlayListHandler.getInstance().moveToTop(rows);
			panelControlled.getPlayListTable().getSelectionModel().setSelectionInterval(0, rows.length - 1);
		}
	}

	public void moveUp() {
		logger.debug(LogCategories.CONTROLLER);

		PlayListTable table = VisualHandler.getInstance().getPlayListTable();
		int[] rows = table.getSelectedRows();
		if (rows.length > 0 && rows[0] > 0) {
			((PlayListTableModel) table.getModel()).moveUp(rows);
			PlayListHandler.getInstance().moveUp(rows);
			panelControlled.getPlayListTable().getSelectionModel().setSelectionInterval(rows[0] - 1, rows[rows.length - 1] - 1);
		}
	}

	public void notifyAudioObjectsAddedToController(List<AudioObject> audioObjects, int selected) {
		int selectedAudioObject = selected;

		if (selectedAudioObject == -1)
			selectedAudioObject = panelControlled.getPlayListTable().getPlayingSong();

		for (int i = 0; i < audioObjects.size(); i++)
			((PlayListTableModel) panelControlled.getPlayListTable().getModel()).addSong(audioObjects.get(i));

		setSelectedSongOnTable(selectedAudioObject);

		ControllerProxy.getInstance().getPlayListControlsController().enableSaveButton(true);
		ControllerProxy.getInstance().getMenuController().enableSavePlaylist(true);
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}

	public void playSelectedAudioObject() {
		int audioObject = panelControlled.getPlayListTable().getSelectedRow();
		PlayerHandler.getInstance().setPlayListPositionToPlay(audioObject);
		PlayerHandler.getInstance().play(false);
	}

	/**
	 * Scrolls to songs currently playing
	 * 
	 */
	public void scrollPlayList() {
		scrollPlayList(panelControlled.getPlayListTable().getPlayingSong());
	}

	/**
	 * Scrolls the playlist to the playing song.
	 * 
	 * @param audioObject
	 *            Audio object which should have focus
	 */
	private synchronized void scrollPlayList(int audioObject) {
		if (!allowScrolling)
			return;

		logger.debug(LogCategories.CONTROLLER, "Scrolling PlayList");

		// Get visible rectangle
		visibleRect = (Rectangle) panelControlled.getPlayListTable().getVisibleRect().clone();

		// Get cell height
		int heightOfRow = panelControlled.getPlayListTable().getCellRect(audioObject, 0, true).height;

		// Do calculation
		visibleRect.y = audioObject * heightOfRow - visibleRect.height / 2;

		// Correct negative value of y
		visibleRect.y = visibleRect.y >= 0 ? visibleRect.y : 0;

		// Apply scroll
		// We add a delay in order to reduce freezes
		Timer t = new Timer(1500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panelControlled.getPlayListTable().scrollRectToVisible(visibleRect);
			}
		});
		t.setRepeats(false);
		t.start();
	}

	public void setAlbumAsPlaylist() {
		logger.debug(LogCategories.CONTROLLER);

		String artist = PlayerHandler.getInstance().getCurrentPlayList().getCurrentAudioObject().getArtist();
		String album = PlayerHandler.getInstance().getCurrentPlayList().getCurrentAudioObject().getAlbum();
		Map<String, Artist> structure = RepositoryHandler.getInstance().getArtistAndAlbumStructure();
		if (structure.containsKey(artist)) {
			Artist a = structure.get(artist);
			Album al = a.getAlbum(album);
			if (al != null) {
				PlayListHandler.getInstance().clearList();

				// Sort songs
				List<AudioObject> songs = RepositoryHandler.getInstance().sort(al.getAudioObjects());

				PlayListHandler.getInstance().addToPlayList(songs);
			}
		}
	}

	public void setArtistAsPlaylist() {
		logger.debug(LogCategories.CONTROLLER);

		String artist = PlayerHandler.getInstance().getCurrentPlayList().getCurrentAudioObject().getArtist();
		Map<String, Artist> structure = RepositoryHandler.getInstance().getArtistAndAlbumStructure();
		if (structure.containsKey(artist)) {
			Artist a = structure.get(artist);
			PlayListHandler.getInstance().clearList();

			// Sort songs
			List<AudioObject> songs = RepositoryHandler.getInstance().sort(a.getAudioObjects());

			PlayListHandler.getInstance().addToPlayList(songs);
		}
	}

	public void setGenre() {
		PlayListTable table = VisualHandler.getInstance().getPlayListTable();
		int[] rows = table.getSelectedRows();
		List<AudioFile> files = new ArrayList<AudioFile>();
		for (int element : rows) {
			AudioObject ao = ((PlayListTableModel) table.getModel()).getFileAt(element);
			if (ao instanceof AudioFile) {
				files.add((AudioFile) ao);
			}
		}
		TagEditionOperations.editGenre(files);
	}

	public void setLyrics() {
		PlayListTable table = VisualHandler.getInstance().getPlayListTable();
		int[] rows = table.getSelectedRows();
		List<AudioFile> files = new ArrayList<AudioFile>();
		for (int element : rows) {
			AudioObject ao = ((PlayListTableModel) table.getModel()).getFileAt(element);
			if (ao instanceof AudioFile) {
				files.add((AudioFile) ao);
			}
		}
		TagEditionOperations.addLyrics(files);
	}

	public void setSelectedSong(int song) {
		setSelectedSongOnTable(song);
	}

	private void setSelectedSongOnTable(int song) {
		panelControlled.getPlayListTable().setPlayingSong(song);

		// Scroll automatically play list to song
		scrollPlayList(song);
	}

	/**
	 * Gets selected songs on play list and sets title based on artist, album
	 * and track
	 * 
	 */
	public void setTitle() {
		PlayListTable table = VisualHandler.getInstance().getPlayListTable();
		int[] rows = table.getSelectedRows();
		List<AudioFile> files = new ArrayList<AudioFile>();
		for (int element : rows) {
			AudioObject ao = ((PlayListTableModel) table.getModel()).getFileAt(element);
			if (ao instanceof AudioFile) {
				files.add((AudioFile) ao);
			}
		}
		BackgroundExecutor.changeTitles(files);
	}

	public void setTrackNumber() {
		PlayListTable table = VisualHandler.getInstance().getPlayListTable();
		int[] rows = table.getSelectedRows();
		List<AudioFile> files = new ArrayList<AudioFile>();
		for (int element : rows) {
			AudioObject ao = ((PlayListTableModel) table.getModel()).getFileAt(element);
			if (ao instanceof AudioFile) {
				files.add((AudioFile) ao);
			}
		}
		TagEditionOperations.editTrackNumber(files);
	}

	public void showPlaylistControls(boolean show) {
		logger.debug(LogCategories.CONTROLLER);

		Kernel.getInstance().state.setShowPlaylistControls(show);
		// getMainPlayListTable().getShowControls().setSelected(show);
		ControllerProxy.getInstance().getMenuController().setShowPlaylistControls(show);
		panelControlled.getPlayListControls().setVisible(show);
	}

	public void updatePositionInTable(int pos) {
		((PlayListTableModel) panelControlled.getPlayListTable().getModel()).refresh(pos);
	}
}
