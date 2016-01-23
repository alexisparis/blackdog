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

package net.sourceforge.atunes.kernel.controllers.playListControls;

import net.sourceforge.atunes.gui.views.panels.PlayListControlsPanel;
import net.sourceforge.atunes.kernel.controllers.model.PanelController;

public class PlayListControlsController extends PanelController<PlayListControlsPanel> {

	public PlayListControlsController(PlayListControlsPanel panel) {
		super(panel);
		addBindings();
		addStateBindings();
	}

	@Override
	protected void addBindings() {
		PlayListControlsListener listener = new PlayListControlsListener(panelControlled);

		panelControlled.getArrangeColumns().addActionListener(listener);
		panelControlled.getSearchFilter().addActionListener(listener);
		panelControlled.getSavePlaylistButton().addActionListener(listener);
		panelControlled.getLoadPlaylistButton().addActionListener(listener);
		panelControlled.getTopButton().addActionListener(listener);
		panelControlled.getUpButton().addActionListener(listener);
		panelControlled.getDeleteButton().addActionListener(listener);
		panelControlled.getDownButton().addActionListener(listener);
		panelControlled.getBottomButton().addActionListener(listener);
		panelControlled.getInfoButton().addActionListener(listener);
		panelControlled.getClearButton().addActionListener(listener);
		panelControlled.getFavoriteSong().addActionListener(listener);
		panelControlled.getFavoriteAlbum().addActionListener(listener);
		panelControlled.getFavoriteArtist().addActionListener(listener);
		panelControlled.getArtistButton().addActionListener(listener);
		panelControlled.getAlbumButton().addActionListener(listener);
		panelControlled.getScrollPlaylistToCurrentSongButton().addActionListener(listener);
	}

	@Override
	protected void addStateBindings() {
		disablePlayListControls(true, false);
	}

	public void disablePlayListControls(boolean disable, boolean radioOrPodcastFeedEntrySelected) {
		panelControlled.getSearchFilter().setEnabled(true);
		panelControlled.getInfoButton().setEnabled(!disable);
		panelControlled.getDeleteButton().setEnabled(!disable);
		panelControlled.getClearButton().setEnabled(true);
		panelControlled.getTopButton().setEnabled(!disable);
		panelControlled.getUpButton().setEnabled(!disable);
		panelControlled.getDownButton().setEnabled(!disable);
		panelControlled.getBottomButton().setEnabled(!disable);
		panelControlled.getFavoritePopup().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		panelControlled.getArtistButton().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
		panelControlled.getAlbumButton().setEnabled(!disable && !radioOrPodcastFeedEntrySelected);
	}

	public void enableSaveButton(boolean enable) {
		panelControlled.getSavePlaylistButton().setEnabled(enable);

	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}
}
