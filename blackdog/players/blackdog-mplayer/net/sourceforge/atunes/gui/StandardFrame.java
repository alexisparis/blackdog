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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.menu.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.AudioScrobblerPanel;
import net.sourceforge.atunes.gui.views.panels.FilePropertiesPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;

import org.jdesktop.swingx.JXStatusBar;
import org.jvnet.substance.SubstanceLookAndFeel;

public class StandardFrame extends CustomFrame implements net.sourceforge.atunes.gui.Frame {

	private static final long serialVersionUID = 1L;

	public static final int NAVIGATION_PANEL_WIDTH = GuiUtils.getComponentSizeForResolution(1280, 280);
	public static final int NAVIGATION_PANEL_MINIMUM_WIDTH = NAVIGATION_PANEL_WIDTH - 80;
	public static final int NAVIGATION_PANEL_MAXIMUM_WIDTH = NAVIGATION_PANEL_WIDTH + 50;
	public static final int AUDIO_SCROBBLERPANEL_WIDTH = GuiUtils.getComponentSizeForResolution(1280, 280);
	public static final int AUDIO_SCROBBLER_MINIMUM_WIDTH = AUDIO_SCROBBLERPANEL_WIDTH - 50;
	public static final int FILE_PROPERTIES_PANEL_HEIGHT = 100;
	public static final int PLAY_LIST_PANEL_WIDTH = GuiUtils.getComponentSizeForResolution(1280, 490);

	public static final int NAVIGATOR_SPLIT_PANE_DIVIDER_LOCATION = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2;

	public static final int margin = 100;
	private JSplitPane leftVerticalSplitPane;
	private JSplitPane rightVerticalSplitPane;
	private JLabel leftStatusBar;
	private JLabel centerStatusBar;
	private JLabel rightStatusBar;
	private JLabel statusBarImageLabel;
	private JProgressBar progressBar;
	private ApplicationMenuBar appMenuBar;
	private NavigationPanel navigationPanel;
	private PlayListPanel playListPanel;
	private FilePropertiesPanel propertiesPanel;
	private AudioScrobblerPanel audioScrobblerPanel;
	private PlayerControlsPanel playerControls;
	private JXStatusBar statusBar;

	private ToolBar toolBar;

	public StandardFrame() {
		super();
	}

	@Override
	public void create() {
		Point windowLocation = null;
		if (Kernel.getInstance().state.getWindowXPosition() >= 0 && Kernel.getInstance().state.getWindowYPosition() >= 0)
			windowLocation = new Point(Kernel.getInstance().state.getWindowXPosition(), Kernel.getInstance().state.getWindowYPosition());
		if (windowLocation != null)
			setLocation(windowLocation);
		else
			setLocationRelativeTo(null);

		// Set window state listener
		addWindowStateListener(VisualHandler.getInstance().getWindowStateListener());
		addWindowFocusListener(VisualHandler.getInstance().getWindowStateListener());

		addComponentListener(VisualHandler.getInstance().getStandardWindowListener());

		// Create frame content
		setContentPane(getContentPanel());

		// Set frame basic attributes
		setWindowSize();

		GuiUtils.applyComponentOrientation(this);
	}

	@Override
	public void dispose() {
		VisualHandler.getInstance().finish();
		super.dispose();
	}

	@Override
	public ApplicationMenuBar getAppMenuBar() {
		return appMenuBar;
	}

	@Override
	public AudioScrobblerPanel getAudioScrobblerPanel() {
		if (audioScrobblerPanel == null) {
			audioScrobblerPanel = new AudioScrobblerPanel();
			audioScrobblerPanel.setPreferredSize(new Dimension(AUDIO_SCROBBLERPANEL_WIDTH, 1));
			audioScrobblerPanel.setMinimumSize(new Dimension(AUDIO_SCROBBLER_MINIMUM_WIDTH, 1));
			if (!Kernel.getInstance().state.isUseAudioScrobbler())
				audioScrobblerPanel.setVisible(false);
		}
		return audioScrobblerPanel;
	}

	private JLabel getCenterStatusBar() {
		if (centerStatusBar == null) {
			centerStatusBar = new JLabel(" ");
		}
		return centerStatusBar;
	}

	private Container getContentPanel() {
		// Main Container
		JPanel panel = new JPanel(new GridBagLayout());

		// Main Split Pane			
		leftVerticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		// Create menu bar
		appMenuBar = new ApplicationMenuBar();
		setJMenuBar(appMenuBar);

		GridBagConstraints c = new GridBagConstraints();

		// Play List, File Properties, AudioScrobbler panel
		JPanel nonNavigatorPanel = new JPanel(new BorderLayout());
		rightVerticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		rightVerticalSplitPane.setBorder(BorderFactory.createEmptyBorder());
		rightVerticalSplitPane.setResizeWeight(1);

		JPanel centerPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		centerPanel.add(getPlayListPanel(), c);
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		centerPanel.add(getPropertiesPanel(), c);
		c.gridy = 2;
		centerPanel.add(getPlayerControls(), c);

		// JSplitPane does not support component orientation, so we must do this manually
		// -> http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4265389
		if (GuiUtils.getComponentOrientation().isLeftToRight()) {
			rightVerticalSplitPane.add(centerPanel);
			rightVerticalSplitPane.add(getAudioScrobblerPanel());
		} else {
			rightVerticalSplitPane.add(getAudioScrobblerPanel());
			rightVerticalSplitPane.add(centerPanel);
		}

		nonNavigatorPanel.add(rightVerticalSplitPane, BorderLayout.CENTER);

		// Navigation Panel
		// JSplitPane does not support component orientation, so we must do this manually
		// -> http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4265389
		if (GuiUtils.getComponentOrientation().isLeftToRight()) {
			leftVerticalSplitPane.add(getNavigationPanel());
			leftVerticalSplitPane.add(nonNavigatorPanel);
			leftVerticalSplitPane.setResizeWeight(0.2);
		} else {
			leftVerticalSplitPane.add(nonNavigatorPanel);
			leftVerticalSplitPane.add(getNavigationPanel());
			rightVerticalSplitPane.setResizeWeight(0.2);
		}

		// Split pane divider should use background color (can maybe removed with future Substance versions)
		leftVerticalSplitPane.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
		rightVerticalSplitPane.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
		getNavigationPanel().putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.FALSE);
		getAudioScrobblerPanel().putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.FALSE);
		nonNavigatorPanel.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.FALSE);
		centerPanel.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.FALSE);

		toolBar = new ToolBar();

		c.gridx = 0;
		c.gridy = 0;
		panel.add(toolBar, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(leftVerticalSplitPane, c);

		c.gridy = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(getStatusBar(), c);

		return panel;
	}

	@Override
	public JFrame getFrame() {
		return this;
	}

	private JLabel getLeftStatusBar() {
		if (leftStatusBar == null) {
			leftStatusBar = new JLabel(" ");
		}
		return leftStatusBar;
	}

	public JSplitPane getLeftVerticalSplitPane() {
		return leftVerticalSplitPane;
	}

	@Override
	public NavigationPanel getNavigationPanel() {
		if (navigationPanel == null) {
			navigationPanel = new NavigationPanel();
			navigationPanel.setPreferredSize(new Dimension(NAVIGATION_PANEL_WIDTH, 1));
			navigationPanel.setMinimumSize(new Dimension(NAVIGATION_PANEL_MINIMUM_WIDTH, 1));
			navigationPanel.setMaximumSize(new Dimension(NAVIGATION_PANEL_MAXIMUM_WIDTH, 1));

			// If must be hidden, hide directly
			if (!Kernel.getInstance().state.isShowNavigationPanel())
				navigationPanel.setVisible(false);
		}
		return navigationPanel;
	}

	@Override
	public PlayerControlsPanel getPlayerControls() {
		if (playerControls == null)
			playerControls = new PlayerControlsPanel();
		return playerControls;
	}

	@Override
	public PlayListPanel getPlayListPanel() {
		if (playListPanel == null) {
			playListPanel = new PlayListPanel();
			playListPanel.setMinimumSize(new Dimension(PLAY_LIST_PANEL_WIDTH, 1));
			playListPanel.setPreferredSize(new Dimension(PLAY_LIST_PANEL_WIDTH, 1));
		}
		return playListPanel;
	}

	@Override
	public PlayListTable getPlayListTable() {
		return getPlayListPanel().getPlayListTable();
	}

	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);
			progressBar.setBorder(BorderFactory.createEmptyBorder());
		}
		return progressBar;
	}

	@Override
	public FilePropertiesPanel getPropertiesPanel() {
		if (propertiesPanel == null) {
			propertiesPanel = new FilePropertiesPanel();
			propertiesPanel.setPreferredSize(new Dimension(1, FILE_PROPERTIES_PANEL_HEIGHT));
			if (!Kernel.getInstance().state.isShowSongProperties())
				propertiesPanel.setVisible(false);
		}
		return propertiesPanel;
	}

	private JLabel getRightStatusBar() {
		if (rightStatusBar == null) {
			rightStatusBar = new JLabel(" ");
		}
		return rightStatusBar;
	}

	public JSplitPane getRightVerticalSplitPane() {
		return rightVerticalSplitPane;
	}

	public JXStatusBar getStatusBar() {
		if (statusBar == null) {
			statusBar = new JXStatusBar();
			JXStatusBar.Constraint c = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL);
			statusBar.add(getLeftStatusBar(), c);
			c = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL);
			statusBar.add(getCenterStatusBar(), c);
			c = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL);
			statusBar.add(getRightStatusBar(), c);
		}
		return statusBar;
	}

	private JLabel getStatusBarImageLabel() {
		if (statusBarImageLabel == null) {
			statusBarImageLabel = new JLabel();
		}
		return statusBarImageLabel;
	}

	@Override
	public ToolBar getToolBar() {
		return toolBar;
	}

	@Override
	public void setCenterStatusBar(String text) {
		getCenterStatusBar().setText(text);
	}

	@Override
	public void setLeftStatusBarText(String text) {
		getLeftStatusBar().setText(text);
	}

	public void setLeftVerticalSplitPaneDividerLocationAndSetWindowSize(int location) {
		leftVerticalSplitPane.setDividerLocation(location);
		setWindowSize();
	}

	@Override
	public void setRightStatusBar(String text) {
		getRightStatusBar().setText(text);
	}

	public void setRightVerticalSplitPaneDividerLocationAndSetWindowSize(int location) {
		rightVerticalSplitPane.setDividerLocation(location);
		setWindowSize();
	}

	@Override
	public void setStatusBarImageLabelIcon(Icon icon, String text) {
		getStatusBarImageLabel().setIcon(icon);
		getStatusBarImageLabel().setText(text);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		//TODO
		/*
		 * We need this because when switching from LTR to RTL component
		 * orientation (or vice versa) the navigation panel width is too big
		 * because of an unknown reason
		 */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (Toolkit.getDefaultToolkit().getScreenSize().width + 15 < getSize().width) {
					setWindowSize();
				}
			}
		});
	}

	public void setWindowSize() {
		setMinimumSize(new Dimension(655, 410));
		if (Kernel.getInstance().state.isMaximized()) {
			Dimension screen = getToolkit().getScreenSize();
			setSize(screen.width - margin, screen.height - margin);
			setExtendedState(Frame.MAXIMIZED_BOTH);
		} else {
			Dimension d = null;
			if (Kernel.getInstance().state.getWindowWidth() != 0 && Kernel.getInstance().state.getWindowHeight() != 0)
				d = new Dimension(Kernel.getInstance().state.getWindowWidth(), Kernel.getInstance().state.getWindowHeight());
			if (d != null) {
				setSize(d);
			} else {
				Dimension screen = getToolkit().getScreenSize();
				setSize(screen.width - margin, screen.height - margin);
			}
		}
	}

	@Override
	public void showAudioScrobblerPanel(boolean show, boolean changeSize) {
		boolean wasVisible = getAudioScrobblerPanel().isVisible();
		getAudioScrobblerPanel().setVisible(show);
		if (!wasVisible && show) {
			int playListWidth = playListPanel.getWidth();
			if (Kernel.getInstance().state.getRightVerticalSplitPaneDividerLocation() != 0)
				rightVerticalSplitPane.setDividerLocation(Kernel.getInstance().state.getRightVerticalSplitPaneDividerLocation());
			else
				rightVerticalSplitPane.setDividerLocation(rightVerticalSplitPane.getSize().width - StandardFrame.AUDIO_SCROBBLERPANEL_WIDTH);
			playListWidth = playListWidth - StandardFrame.AUDIO_SCROBBLERPANEL_WIDTH;
			if (playListWidth < PLAY_LIST_PANEL_WIDTH && changeSize) {
				int diff = PLAY_LIST_PANEL_WIDTH - playListWidth;
				setSize(getSize().width + diff, getSize().height);
			}
		} else if (!show) {
			// Save panel width
			Kernel.getInstance().state.setRightVerticalSplitPaneDividerLocation(getRightVerticalSplitPane().getDividerLocation());
		}
	}

	@Override
	public void showNavigationPanel(boolean show, boolean changeSize) {
		getNavigationPanel().setVisible(show);
		if (show) {
			int playListWidth = playListPanel.getWidth();

			getLeftVerticalSplitPane().setDividerLocation(Kernel.getInstance().state.getLeftVerticalSplitPaneDividerLocation());
			playListWidth = playListWidth - StandardFrame.NAVIGATION_PANEL_WIDTH;
			if (playListWidth < PLAY_LIST_PANEL_WIDTH && changeSize) {
				int diff = PLAY_LIST_PANEL_WIDTH - playListWidth;
				setSize(getSize().width + diff, getSize().height);
			}
		} else {
			// Save panel width
			Kernel.getInstance().state.setLeftVerticalSplitPaneDividerLocation(getLeftVerticalSplitPane().getDividerLocation());
		}
	}

	@Override
	public void showNavigationTable(boolean show) {
		getNavigationPanel().getNavigationTableContainer().setVisible(show);
		if (show) {
			super.setVisible(show);
			getNavigationPanel().getSplitPane().setDividerLocation(Kernel.getInstance().state.getLeftHorizontalSplitPaneDividerLocation());

		} else {
			// Save location
			Kernel.getInstance().state.setLeftHorizontalSplitPaneDividerLocation(getNavigationPanel().getSplitPane().getDividerLocation());
		}
	}

	@Override
	public void showProgressBar(boolean visible) {
		if (visible) {
			JXStatusBar.Constraint c = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FIXED);
			statusBar.add(getProgressBar(), c);
		} else {
			statusBar.remove(getProgressBar());
		}
		getProgressBar().setVisible(visible);
	}

	@Override
	public void showSongProperties(boolean show) {
		getPropertiesPanel().setVisible(show);
	}

	@Override
	public void showStatusBar(boolean show) {
		getStatusBar().setVisible(show);
	}

	@Override
	public void showStatusBarImageLabel(boolean visible) {
		if (visible) {
			JXStatusBar.Constraint c = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FIXED);
			statusBar.add(getStatusBarImageLabel(), c);
		} else {
			statusBar.remove(getStatusBarImageLabel());
		}
		getStatusBarImageLabel().setVisible(visible);
	}

}
