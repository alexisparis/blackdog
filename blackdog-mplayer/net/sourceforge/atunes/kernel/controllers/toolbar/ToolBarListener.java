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

package net.sourceforge.atunes.kernel.controllers.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.gui.ToolBar;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.RipperHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;

public class ToolBarListener implements ActionListener {

	private ToolBar toolBar;

	public ToolBarListener(ToolBar t) {
		this.toolBar = t;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(toolBar.getSelectRepository())) {
			RepositoryHandler.getInstance().selectRepository();
		} else if (e.getSource().equals(toolBar.getRefreshRepository())) {
			RepositoryHandler.getInstance().refreshRepository();
		} else if (e.getSource().equals(toolBar.getPreferences())) {
			ControllerProxy.getInstance().getEditPreferencesDialogController().start();
		} else if (e.getSource().equals(toolBar.getShowNavigator())) {
			VisualHandler.getInstance().showNavigationPanel(toolBar.getShowNavigator().isSelected(), true);
		} else if (e.getSource().equals(toolBar.getShowFileProperties())) {
			VisualHandler.getInstance().showSongProperties(toolBar.getShowFileProperties().isSelected(), true);
		} else if (e.getSource().equals(toolBar.getShowAudioScrobbler())) {
			VisualHandler.getInstance().showAudioScrobblerPanel(toolBar.getShowAudioScrobbler().isSelected(), true);
		} else if (e.getSource().equals(toolBar.getStats())) {
			ControllerProxy.getInstance().getStatsDialogController().showStats();
		} else if (e.getSource().equals(toolBar.getRipCD())) {
			RipperHandler.getInstance().startCdRipper();
		}
	}

}
