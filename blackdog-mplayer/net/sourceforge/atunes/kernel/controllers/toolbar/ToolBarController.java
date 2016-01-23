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

import net.sourceforge.atunes.gui.ToolBar;
import net.sourceforge.atunes.utils.log4j.LogCategories;

public class ToolBarController extends net.sourceforge.atunes.kernel.controllers.model.ToolBarController<ToolBar> {

	public ToolBarController(ToolBar toolBar) {
		super(toolBar);
		addBindings();
	}

	@Override
	protected void addBindings() {
		ToolBarListener l = new ToolBarListener(toolBarControlled);
		toolBarControlled.getSelectRepository().addActionListener(l);
		toolBarControlled.getRefreshRepository().addActionListener(l);
		toolBarControlled.getPreferences().addActionListener(l);
		toolBarControlled.getShowNavigator().addActionListener(l);
		toolBarControlled.getShowFileProperties().addActionListener(l);
		toolBarControlled.getShowAudioScrobbler().addActionListener(l);
		toolBarControlled.getStats().addActionListener(l);
		toolBarControlled.getRipCD().addActionListener(l);
	}

	@Override
	protected void addStateBindings() {
		// Nothing to do
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}

	public void setShowAudioScrobblerPanel(boolean show) {
		logger.debug(LogCategories.CONTROLLER, new String[] { Boolean.toString(show) });

		toolBarControlled.getShowAudioScrobbler().setSelected(show);
	}

	public void setShowNavigatorPanel(boolean show) {
		logger.debug(LogCategories.CONTROLLER, new String[] { Boolean.toString(show) });

		toolBarControlled.getShowNavigator().setSelected(show);
	}

	public void setShowSongProperties(boolean show) {
		logger.debug(LogCategories.CONTROLLER, new String[] { Boolean.toString(show) });

		toolBarControlled.getShowFileProperties().setSelected(show);
	}

}
