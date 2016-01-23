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

package net.sourceforge.atunes.kernel.controllers.navigation.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class NavigationTreeToolTipListener implements MouseMotionListener, MouseWheelListener {

	private NavigationController controller;
	private NavigationPanel panel;

	public NavigationTreeToolTipListener(NavigationController controller, NavigationPanel panel) {
		this.controller = controller;
		this.panel = panel;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (!Kernel.getInstance().state.isShowAlbumTooltip())
			return;

		controller.setLastAlbumToolTipContent(null);
		controller.getAlbumToolTip().setVisible(false);
	}

	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) {
		if (!Kernel.getInstance().state.isShowAlbumTooltip())
			return;

		TreePath selectedPath = panel.getNavigationTree().getPathForLocation(e.getX(), e.getY());
		if (selectedPath != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
			Object content = node.getUserObject();

			if (content instanceof Album) {
				if (!controller.getAlbumToolTip().isVisible() || controller.getLastAlbumToolTipContent() == null || controller.getLastAlbumToolTipContent() != content) {
					if (controller.getAlbumToolTip().isVisible())
						controller.getAlbumToolTip().setVisible(false);
					controller.getAlbumToolTip().setLocation((int) panel.getNavigationTree().getLocationOnScreen().getX() + e.getX(),
							(int) panel.getNavigationTree().getLocationOnScreen().getY() + e.getY() + 20);
					if (content instanceof Album) {
						controller.getAlbumToolTip().setPicture(((Album) content).getPicture(Constants.TOOLTIP_IMAGE_WIDTH, Constants.TOOLTIP_IMAGE_HEIGHT));
						controller.getAlbumToolTip().setAlbum(((Album) content).getName());
						controller.getAlbumToolTip().setArtist(((Album) content).getArtist().toString());
						int songs = ((Album) content).getAudioObjects().size();
						controller.getAlbumToolTip().setSongs(StringUtils.getString(songs, " ", (songs > 1 ? LanguageTool.getString("SONGS") : LanguageTool.getString("SONG"))));
					}
					controller.setLastAlbumToolTipContent(content);

					controller.getAlbumToolTip().timer.setInitialDelay(Kernel.getInstance().state.getAlbumTooltipDelay() * 1000);
					controller.getAlbumToolTip().timer.setRepeats(false);
					controller.getAlbumToolTip().timer.start();
				}
			} else {
				controller.setLastAlbumToolTipContent(null);
				controller.getAlbumToolTip().setVisible(false);
				controller.getAlbumToolTip().timer.stop();
			}
		} else {
			controller.getAlbumToolTip().setVisible(false);
			controller.getAlbumToolTip().timer.stop();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!Kernel.getInstance().state.isShowAlbumTooltip())
			return;

		controller.setLastAlbumToolTipContent(null);
		controller.getAlbumToolTip().setVisible(false);
	}

}
