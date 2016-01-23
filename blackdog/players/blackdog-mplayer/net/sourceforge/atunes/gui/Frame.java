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

package net.sourceforge.atunes.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JFrame;

import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.menu.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.AudioScrobblerPanel;
import net.sourceforge.atunes.gui.views.panels.FilePropertiesPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;

public interface Frame {

	public void create();

	public ApplicationMenuBar getAppMenuBar();

	public AudioScrobblerPanel getAudioScrobblerPanel();

	public int getExtendedState();

	public JFrame getFrame();

	public Point getLocation();

	public NavigationPanel getNavigationPanel();

	public PlayerControlsPanel getPlayerControls();

	public PlayListPanel getPlayListPanel();

	public PlayListTable getPlayListTable();

	public FilePropertiesPanel getPropertiesPanel();

	public Dimension getSize();

	public ToolBar getToolBar();

	public boolean isVisible();

	public void setCenterStatusBar(String text);

	public void setDefaultCloseOperation(int op);

	public void setExtendedState(int state);

	public void setLeftStatusBarText(String text);

	public void setLocation(Point location);

	public void setLocationRelativeTo(Component c);

	public void setRightStatusBar(String text);

	public void setStatusBarImageLabelIcon(Icon icon, String text);

	public void setTitle(String title);

	public void setVisible(boolean visible);

	public void showAudioScrobblerPanel(boolean show, boolean changeSize);

	public void showNavigationPanel(boolean show, boolean changeSize);

	public void showNavigationTable(boolean show);

	public void showProgressBar(boolean visible);

	public void showSongProperties(boolean show);

	public void showStatusBar(boolean show);

	public void showStatusBarImageLabel(boolean visible);

}
