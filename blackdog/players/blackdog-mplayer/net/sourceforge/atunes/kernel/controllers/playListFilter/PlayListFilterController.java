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

package net.sourceforge.atunes.kernel.controllers.playListFilter;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sourceforge.atunes.gui.views.panels.PlayListFilterPanel;
import net.sourceforge.atunes.kernel.controllers.model.PanelController;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListFilterOps;
import net.sourceforge.atunes.utils.log4j.LogCategories;

public class PlayListFilterController extends PanelController<PlayListFilterPanel> {

	public PlayListFilterController(PlayListFilterPanel panel) {
		super(panel);
		addBindings();
		addStateBindings();
	}

	@Override
	protected void addBindings() {
		panelControlled.getFilterTextField().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				String text = panelControlled.getFilterTextField().getText();
				if (text.equals(""))
					text = null;
				PlayListHandler.getInstance().setFilter(text);
			}
		});
		panelControlled.getClearFilterButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				panelControlled.getFilterTextField().setText("");
				PlayListHandler.getInstance().setFilter(null);
			}
		});
	}

	@Override
	protected void addStateBindings() {
		// Nothing to do
	}

	public void emptyFilter() {
		logger.debug(LogCategories.CONTROLLER);

		panelControlled.getFilterTextField().setText("");
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}

	public void reapplyFilter() {
		logger.debug(LogCategories.CONTROLLER);

		if (PlayListFilterOps.isFiltered())
			PlayListHandler.getInstance().setFilter(panelControlled.getFilterTextField().getText());
	}
}
