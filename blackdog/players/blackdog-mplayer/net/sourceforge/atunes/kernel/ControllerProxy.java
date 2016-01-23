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

package net.sourceforge.atunes.kernel;

import net.sourceforge.atunes.gui.ToolBar;
import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.gui.views.menu.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.FilePropertiesPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.gui.views.panels.PlayListFilterPanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayListTabPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.controllers.audioScrobbler.AudioScrobblerController;
import net.sourceforge.atunes.kernel.controllers.editPreferencesDialog.EditPreferencesDialogController;
import net.sourceforge.atunes.kernel.controllers.editTagDialog.EditTagDialogController;
import net.sourceforge.atunes.kernel.controllers.editTitlesDialog.EditTitlesDialogController;
import net.sourceforge.atunes.kernel.controllers.exportOptionsDialog.ExportOptionsDialogController;
import net.sourceforge.atunes.kernel.controllers.fileProperties.FilePropertiesController;
import net.sourceforge.atunes.kernel.controllers.menu.MenuController;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController;
import net.sourceforge.atunes.kernel.controllers.osd.OSDDialogController;
import net.sourceforge.atunes.kernel.controllers.playList.PlayListController;
import net.sourceforge.atunes.kernel.controllers.playListControls.PlayListControlsController;
import net.sourceforge.atunes.kernel.controllers.playListFilter.PlayListFilterController;
import net.sourceforge.atunes.kernel.controllers.playListTab.PlayListTabController;
import net.sourceforge.atunes.kernel.controllers.playerControls.PlayerControlsController;
import net.sourceforge.atunes.kernel.controllers.ripcd.RipCdDialogController;
import net.sourceforge.atunes.kernel.controllers.stats.StatsDialogController;
import net.sourceforge.atunes.kernel.controllers.toolbar.ToolBarController;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

/**
 * Static class to access controllers by calling
 * ControllerProxy.getInstance().get<Name of controller>()
 * 
 */
public class ControllerProxy {

	/**
	 * Logger
	 */
	private static Logger logger = new Logger();

	/**
	 * Singleton instance of controller
	 */
	private static ControllerProxy instance = new ControllerProxy();

	private MenuController fullViewMenuController;

	private NavigationController navigationController;
	private PlayListController playListController;
	private PlayListControlsController playListControlsController;
	private PlayListFilterController playListFilterController;
	private PlayListTabController playListTabController;
	private PlayerControlsController playerControlsController;
	private FilePropertiesController filePropertiesController;
	private EditTagDialogController editTagDialogController;
	private ExportOptionsDialogController exportOptionsController;
	private StatsDialogController statsDialogController;
	private EditTitlesDialogController editTitlesDialogController;
	private AudioScrobblerController audioScrobblerController;
	private EditPreferencesDialogController editPreferencesDialogController;
	private ToolBarController toolBarController;
	private OSDDialogController osdDialogController;
	private RipCdDialogController ripCdDialogController;

	private ControllerProxy() {
		logger.debug(LogCategories.CONTROLLER, "Creating ControllerProxy");
		// Force creation of non-autocreated controllers
		getPlayListFilterController();
		getPlayListControlsController();
		getPlayListTabController();
	}

	/**
	 * Getter for singleton instance
	 * 
	 * @return
	 */
	public static ControllerProxy getInstance() {
		return instance;
	}

	public AudioScrobblerController getAudioScrobblerController() {
		if (audioScrobblerController == null) {
			audioScrobblerController = new AudioScrobblerController(VisualHandler.getInstance().getAudioScrobblerPanel());
		}
		return audioScrobblerController;
	}

	public EditPreferencesDialogController getEditPreferencesDialogController() {
		if (editPreferencesDialogController == null)
			editPreferencesDialogController = new EditPreferencesDialogController();
		return editPreferencesDialogController;
	}

	public EditTagDialogController getEditTagDialogController() {
		if (editTagDialogController == null) {
			editTagDialogController = new EditTagDialogController(VisualHandler.getInstance().getEditTagDialog());
		}
		return editTagDialogController;
	}

	public EditTitlesDialogController getEditTitlesDialogController() {
		if (editTitlesDialogController == null) {
			EditTitlesDialog dialog = VisualHandler.getInstance().getEditTitlesDialog();
			editTitlesDialogController = new EditTitlesDialogController(dialog);
		}
		return editTitlesDialogController;
	}

	public ExportOptionsDialogController getExportOptionsController() {
		if (exportOptionsController == null) {
			exportOptionsController = new ExportOptionsDialogController(VisualHandler.getInstance().getExportDialog());
		}
		return exportOptionsController;
	}

	public FilePropertiesController getFilePropertiesController() {
		if (filePropertiesController == null) {
			FilePropertiesPanel panel = VisualHandler.getInstance().getPropertiesPanel();
			filePropertiesController = new FilePropertiesController(panel);
		}
		return filePropertiesController;
	}

	public MenuController getMenuController() {
		if (fullViewMenuController == null) {
			ApplicationMenuBar menuBar = VisualHandler.getInstance().getMenuBar();
			fullViewMenuController = new MenuController(menuBar);
		}
		return fullViewMenuController;
	}

	public NavigationController getNavigationController() {
		if (navigationController == null) {
			NavigationPanel panel = VisualHandler.getInstance().getNavigationPanel();
			navigationController = new NavigationController(panel);
		}
		return navigationController;
	}

	public OSDDialogController getOSDDialogController() {
		if (osdDialogController == null)
			osdDialogController = new OSDDialogController(VisualHandler.getInstance().getOSDDialog());
		return osdDialogController;
	}

	public PlayerControlsController getPlayerControlsController() {
		if (playerControlsController == null) {
			PlayerControlsPanel panel = null;
			panel = VisualHandler.getInstance().getPlayerControls();
			playerControlsController = new PlayerControlsController(panel);
		}
		return playerControlsController;
	}

	public PlayListController getPlayListController() {
		if (playListController == null) {
			PlayListPanel panel = null;
			panel = VisualHandler.getInstance().getPlayListPanel();
			playListController = new PlayListController(panel);
		}
		return playListController;
	}

	public PlayListControlsController getPlayListControlsController() {
		if (playListControlsController == null) {
			PlayListPanel panel = VisualHandler.getInstance().getPlayListPanel();
			playListControlsController = new PlayListControlsController(panel.getPlayListControls());
		}
		return playListControlsController;
	}

	public PlayListFilterController getPlayListFilterController() {
		if (playListFilterController == null) {
			PlayListFilterPanel panel = VisualHandler.getInstance().getPlayListPanel().getPlayListFilter();
			playListFilterController = new PlayListFilterController(panel);
		}
		return playListFilterController;
	}

	public PlayListTabController getPlayListTabController() {
		if (playListTabController == null) {
			PlayListTabPanel panel = VisualHandler.getInstance().getPlayListPanel().getPlayListTabPanel();
			playListTabController = new PlayListTabController(panel);
		}
		return playListTabController;
	}

	public RipCdDialogController getRipCdDialogController() {
		if (ripCdDialogController == null)
			ripCdDialogController = new RipCdDialogController(VisualHandler.getInstance().getRipCdDialog());
		return ripCdDialogController;
	}

	public StatsDialogController getStatsDialogController() {
		if (statsDialogController == null) {
			statsDialogController = new StatsDialogController(VisualHandler.getInstance().getStatsDialog());
		}
		return statsDialogController;
	}

	public ToolBarController getToolBarController() {
		if (toolBarController == null) {
			ToolBar t = VisualHandler.getInstance().getToolBar();
			toolBarController = new ToolBarController(t);
		}
		return toolBarController;
	}

}
