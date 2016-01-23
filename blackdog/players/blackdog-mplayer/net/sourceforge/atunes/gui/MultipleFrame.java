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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.sourceforge.atunes.gui.dockableView.CloseListener;
import net.sourceforge.atunes.gui.dockableView.DockableDialog;
import net.sourceforge.atunes.gui.dockableView.DockableFrame;
import net.sourceforge.atunes.gui.dockableView.DockableFrameController;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.menu.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.AudioScrobblerPanel;
import net.sourceforge.atunes.gui.views.panels.FilePropertiesPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.utils.LanguageTool;

public class MultipleFrame implements Frame {

	private static final Dimension frameDimension = new Dimension(500, 400);

	private static final Dimension navigatorDimension = new Dimension(300, 689);
	private static final Dimension filePropertiesDimension = new Dimension(500, 130);
	private static final Dimension audioScrobblerDimension = new Dimension(300, 689);

	private DockableFrameController dockableController;

	private DockableFrame frame;
	private DockableDialog navigatorDialog;
	private DockableDialog filePropertiesDialog;
	private DockableDialog audioScrobblerDialog;

	private ApplicationMenuBar menuBar; // NOT USED
	private ToolBar toolBar; // NOT USED
	private NavigationPanel navigationPanel;
	private PlayListPanel playListPanel;
	private PlayerControlsPanel playerControlsPanel;
	private FilePropertiesPanel filePropertiesPanel;
	private AudioScrobblerPanel audioScrobblerPanel;

	public MultipleFrame() {
		dockableController = new DockableFrameController(new CloseListener() {
			@Override
			public void close() {
				VisualHandler.getInstance().finish();
			}

			@Override
			public void minimize() {
				// Nothing to do
			}
		});
	}

	private void addContentToFileProperties() {
		filePropertiesPanel = new FilePropertiesPanel();
		filePropertiesDialog.addContent(filePropertiesPanel);
		GuiUtils.applyComponentOrientation(filePropertiesPanel);
	}

	private void addContentToFrame() {
		frame.setIcon(ImageLoader.APP_ICON.getImage());
		playListPanel = new PlayListPanel();
		playerControlsPanel = new PlayerControlsPanel();

		JPanel auxPanel = new JPanel(new BorderLayout());
		auxPanel.add(playListPanel, BorderLayout.CENTER);
		auxPanel.add(playerControlsPanel, BorderLayout.SOUTH);

		GuiUtils.applyComponentOrientation(auxPanel);

		frame.addContent(auxPanel);
		JPopupMenu menu = menuBar.getMenuAsPopupMenu();
		GuiUtils.applyComponentOrientation(menu);
		frame.setMenu(menu);
	}

	private void addContentToNavigator() {
		navigationPanel = new NavigationPanel();
		navigatorDialog.addContent(navigationPanel);
		GuiUtils.applyComponentOrientation(navigationPanel);
	}

	private void addContentToOpenStrands() {
		audioScrobblerPanel = new AudioScrobblerPanel();
		audioScrobblerDialog.addContent(audioScrobblerPanel);
		GuiUtils.applyComponentOrientation(audioScrobblerPanel);
	}

	@Override
	public void create() {
		// Created but not used
		menuBar = new ApplicationMenuBar();
		toolBar = new ToolBar();

		frame = dockableController.getNewFrame("", frameDimension.width, frameDimension.height, null, DockableFrameController.NONE, frameDimension);

		Point p = null;
		if (Kernel.getInstance().state.getMultipleViewXPosition() > 0 && Kernel.getInstance().state.getMultipleViewYPosition() > 0)
			p = new Point(Kernel.getInstance().state.getMultipleViewXPosition(), Kernel.getInstance().state.getMultipleViewYPosition());
		if (p == null) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation((screenSize.width - frameDimension.width) / 2, 100);
		} else {
			frame.setLocation(p.x, p.y);
		}

		Dimension d = null;
		if (Kernel.getInstance().state.getMultipleViewWidth() != 0 && Kernel.getInstance().state.getMultipleViewHeight() != 0)
			d = new Dimension(Kernel.getInstance().state.getMultipleViewWidth(), Kernel.getInstance().state.getMultipleViewHeight());
		if (d != null) {
			frame.setSize(d);
		}

		addContentToFrame();

		JDialog.setDefaultLookAndFeelDecorated(false);
		navigatorDialog = dockableController.getNewDialog(frame, LanguageTool.getString("NAVIGATOR"), navigatorDimension.width, navigatorDimension.height, frame,
				DockableFrameController.WEST, navigatorDimension);
		addContentToNavigator();

		filePropertiesDialog = dockableController.getNewDialog(frame, LanguageTool.getString("PROPERTIES"), filePropertiesDimension.width, filePropertiesDimension.height, frame,
				DockableFrameController.SOUTH, filePropertiesDimension);
		addContentToFileProperties();

		audioScrobblerDialog = dockableController.getNewDialog(frame, LanguageTool.getString("AUDIO_SCROBBLER"), audioScrobblerDimension.width, audioScrobblerDimension.height,
				frame, DockableFrameController.EAST, audioScrobblerDimension);
		addContentToOpenStrands();
		JDialog.setDefaultLookAndFeelDecorated(true);
	}

	@Override
	public ApplicationMenuBar getAppMenuBar() {
		return menuBar;
	}

	@Override
	public AudioScrobblerPanel getAudioScrobblerPanel() {
		return audioScrobblerPanel;
	}

	@Override
	public int getExtendedState() {
		return frame.getExtendedState();
	}

	@Override
	public JFrame getFrame() {
		return frame.getFrame();
	}

	@Override
	public Point getLocation() {
		return frame.getLocation();
	}

	@Override
	public NavigationPanel getNavigationPanel() {
		return navigationPanel;
	}

	@Override
	public PlayerControlsPanel getPlayerControls() {
		return playerControlsPanel;
	}

	@Override
	public PlayListPanel getPlayListPanel() {
		return playListPanel;
	}

	@Override
	public PlayListTable getPlayListTable() {
		return playListPanel.getPlayListTable();
	}

	@Override
	public FilePropertiesPanel getPropertiesPanel() {
		return filePropertiesPanel;
	}

	@Override
	public Dimension getSize() {
		return frame.getFrame().getSize();
	}

	@Override
	public ToolBar getToolBar() {
		return toolBar;
	}

	@Override
	public boolean isVisible() {
		return frame.isVisible();
	}

	@Override
	public void setCenterStatusBar(String text) {
		// Nothing to do
	}

	@Override
	public void setDefaultCloseOperation(int op) {
		frame.setDefaultCloseOperation(op);
		navigatorDialog.setDefaultCloseOperation(op);
		filePropertiesDialog.setDefaultCloseOperation(op);
		audioScrobblerDialog.setDefaultCloseOperation(op);
	}

	@Override
	public void setExtendedState(int state) {
		//frame.setExtendedState(state);
	}

	@Override
	public void setLeftStatusBarText(String text) {
		//	Nothing to do
	}

	@Override
	public void setLocation(Point location) {
		//frame.setLocation(location.x, location.y);
	}

	@Override
	public void setLocationRelativeTo(Component c) {
		frame.setLocationRelativeTo(c);
	}

	@Override
	public void setRightStatusBar(String text) {
		// Nothing to do
	}

	@Override
	public void setStatusBarImageLabelIcon(Icon icon, String text) {
		// Nothing to do
	}

	@Override
	public void setTitle(String title) {
		frame.setTitle(title);
	}

	@Override
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
		if (!visible || Kernel.getInstance().state.isShowNavigationPanel())
			navigatorDialog.setVisible(visible);
		if (!visible || Kernel.getInstance().state.isShowSongProperties())
			filePropertiesDialog.setVisible(visible);
		if (!visible || Kernel.getInstance().state.isUseAudioScrobbler())
			audioScrobblerDialog.setVisible(visible);
	}

	@Override
	public void showAudioScrobblerPanel(boolean show, boolean changeSize) {
		audioScrobblerDialog.setVisible(show);
	}

	@Override
	public void showNavigationPanel(boolean show, boolean changeSize) {
		navigatorDialog.setVisible(show);
	}

	@Override
	public void showNavigationTable(boolean show) {
		navigationPanel.getNavigationTableContainer().setVisible(show);
		if (show) {
			navigationPanel.getSplitPane().setDividerLocation(Kernel.getInstance().state.getLeftHorizontalSplitPaneDividerLocation());
		} else {
			// Save location
			Kernel.getInstance().state.setLeftHorizontalSplitPaneDividerLocation(navigationPanel.getSplitPane().getDividerLocation());
		}
	}

	@Override
	public void showProgressBar(boolean visible) {
		// Nothing to do
	}

	@Override
	public void showSongProperties(boolean show) {
		filePropertiesDialog.setVisible(show);
	}

	@Override
	public void showStatusBar(boolean show) {
		// Nothing to do
	}

	@Override
	public void showStatusBarImageLabel(boolean visible) {
		// Nothing to do
	}
}
