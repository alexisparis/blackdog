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

package net.sourceforge.atunes.kernel.controllers.editPreferencesDialog;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.DefaultListModel;

import net.sourceforge.atunes.gui.views.dialogs.editPreferences.AudioScrobblerPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.DevicePanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.EditPreferencesDialog;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.GeneralPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.InternetPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.LastFmPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.NavigatorPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.OSDPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PlayerPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PodcastFeedPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PreferencesPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.RadioPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.RepositoryPanel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.model.DialogController;
import net.sourceforge.atunes.kernel.handlers.AudioScrobblerServiceHandler;
import net.sourceforge.atunes.kernel.handlers.HotkeyHandler;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.handlers.SystemTrayHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.device.DeviceConnectionMonitor;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntryRetriever;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.kernel.utils.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.log4j.LogCategories;

public class EditPreferencesDialogController extends DialogController<EditPreferencesDialog> {

	private static final String GENERAL = LanguageTool.getString("GENERAL");
	private static final String REPOSITORY = LanguageTool.getString("REPOSITORY");
	private static final String PLAYER = LanguageTool.getString("PLAYER");
	private static final String NAVIGATOR = LanguageTool.getString("NAVIGATOR");
	private static final String OSD = LanguageTool.getString("OSD");
	private static final String AUDIO_SCROBBLER = LanguageTool.getString("AUDIO_SCROBBLER");
	private static final String INTERNET = LanguageTool.getString("INTERNET");
	private static final String LASTFM = "Last.fm";
	private static final String DEVICE = LanguageTool.getString("DEVICE");
	private static final String RADIO = LanguageTool.getString("RADIO");
	private static final String PODCAST_FEED = LanguageTool.getString("PODCAST_FEEDS");

	private String[] treeNodes = new String[] { GENERAL, REPOSITORY, PLAYER, NAVIGATOR, OSD, AUDIO_SCROBBLER, INTERNET, LASTFM, DEVICE, RADIO, PODCAST_FEED };

	private InternetPanel internetPanel;

	private ApplicationState state = Kernel.getInstance().state;

	private PreferencesPanel[] panels = new PreferencesPanel[] { getGeneralPanel(), getRepositoryPanel(), getPlayerPanel(), getNavigatorPanel(), getOSDPanel(),
			getAudioScrobblerPanel(), getInternetPanel(), getLastFmPanel(), getDevicePanel(), getRadioPanel(), getPodcastFeedPanel() };

	public EditPreferencesDialogController() {
		super(VisualHandler.getInstance().getEditPreferencesDialog());
		// addBindings();
	}

	@Override
	protected void addBindings() {
		EditPreferencesDialogListener listener = new EditPreferencesDialogListener(dialogControlled, this);
		dialogControlled.getList().addListSelectionListener(listener);
		dialogControlled.getCancel().addActionListener(listener);
		dialogControlled.getOk().addActionListener(listener);
		dialogControlled.getAudioScrobblerPanel().getClearCache().addActionListener(listener);
	}

	@Override
	protected void addStateBindings() {
		// Nothing to do
	}

	private void buildList() {
		DefaultListModel listModel = new DefaultListModel();

		for (String s : treeNodes)
			listModel.addElement(s);

		dialogControlled.setListModel(listModel);
		dialogControlled.getList().setSelectedIndex(0);
	}

	private PreferencesPanel getAudioScrobblerPanel() {
		AudioScrobblerPanel panel = new AudioScrobblerPanel();
		panel.setActivateAudioScrobbler(state.isUseAudioScrobbler());
		panel.setSavePictures(state.isSavePictureFromAudioScrobbler());
		return panel;
	}

	private DevicePanel getDevicePanel() {
		DevicePanel panel = new DevicePanel();
		panel.setDefaultDeviceLocation(state.getDefaultDeviceLocation());
		return panel;
	}

	private PreferencesPanel getGeneralPanel() {
		GeneralPanel panel = new GeneralPanel();
		panel.setShowTitle(state.isShowTitle());
		panel.setWindowType(state.isMultipleWindow() ? LanguageTool.getString("MULTIPLE_WINDOW") : LanguageTool.getString("STANDARD_WINDOW"));
		panel.setLanguage(LanguageTool.getLanguageSelected());
		panel.setShowIconTray(state.isShowSystemTray());
		panel.setShowTrayPlayer(state.isShowTrayPlayer());
		panel.setTheme(state.getTheme());
		panel.setUseDefaultFont(state.isUseDefaultFont());
		return panel;
	}

	private PreferencesPanel getInternetPanel() {
		InternetPanel panel = new InternetPanel();
		internetPanel = panel;
		panel.setConfiguration(state.getProxy());
		return panel;
	}

	private PreferencesPanel getLastFmPanel() {
		LastFmPanel panel = new LastFmPanel();
		panel.setLastFmUser(state.getLastFmUser());
		panel.setLasFmPassword(state.getLastFmPassword());
		return panel;
	}

	private PreferencesPanel getNavigatorPanel() {
		NavigatorPanel panel = new NavigatorPanel();
		panel.setShowFavorites(state.isShowFavoritesInNavigator());
		panel.setShowAlbumToolTip(state.isShowAlbumTooltip());
		panel.setAlbumToolTipDelay(state.getAlbumTooltipDelay());
		panel.setUseSmartTagViewSorting(state.isUseSmartTagViewSorting());
		return panel;
	}

	private PreferencesPanel getOSDPanel() {
		OSDPanel panel = new OSDPanel();
		panel.setAnimateOSD(state.isAnimateOSD());
		panel.setOSDDuration(state.getOsdDuration());
		panel.setTransparentOSD(state.isTransparentOSD());
		return panel;
	}

	private PreferencesPanel getPlayerPanel() {
		PlayerPanel panel = new PlayerPanel();
		panel.setUseNormalisation(state.isUseNormalisation());
		panel.setPlayAtStartup(state.isPlayAtStartup());
		panel.setEnableHotkeys(state.isEnableHotkeys());
		panel.setUseShortPathNames(state.isUseShortPathNames());
		panel.getUseShortPathNames().setEnabled(SystemProperties.SYSTEM == OperatingSystem.WINDOWS);
		panel.getEnableGlobalHotkeys().setEnabled(HotkeyHandler.areHotkeysAndIntellitypeSupported());
		return panel;
	}

	private PodcastFeedPanel getPodcastFeedPanel() {
		PodcastFeedPanel panel = new PodcastFeedPanel();
		panel.setRetrievalInterval(state.getPodcastFeedEntriesRetrievalInterval());
		return panel;
	}

	private RadioPanel getRadioPanel() {
		RadioPanel panel = new RadioPanel();
		panel.setReadInfoFromRadioStream(state.isReadInfoFromRadioStream());
		return panel;
	}

	private PreferencesPanel getRepositoryPanel() {
		RepositoryPanel panel = new RepositoryPanel();
		panel.setRefreshTime(state.getAutoRepositoryRefreshTime());
		panel.setSaveRepositoryAsXml(state.isSaveRepositoryAsXml());
		return panel;
	}

	boolean noErrors() {
		return internetPanel.getResult() != null;
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}

	void processPreferences() {
		// Fetch result from panels
		Map<String, Object> result = new HashMap<String, Object>();
		for (PreferencesPanel p : panels)
			result.putAll(p.getResult());

		// Set properties
		state.setShowTitle((Boolean) result.get(LanguageTool.getString("SHOW_TITLE")));
		state.setMultipleWindow(result.get(LanguageTool.getString("WINDOW_TYPE")).equals(LanguageTool.getString("MULTIPLE_WINDOW")));
		state.setLocale(new LocaleBean((Locale) result.get(LanguageTool.getString("LANGUAGE"))));
		state.setUseDefaultFont((Boolean) result.get(LanguageTool.getString("USE_DEFAULT_FONT")));
		state.setShowSystemTray((Boolean) result.get(LanguageTool.getString("SHOW_TRAY_ICON")));
		state.setShowTrayPlayer((Boolean) result.get(LanguageTool.getString("SHOW_TRAY_PLAYER")));
		state.setTheme((String) result.get(LanguageTool.getString("THEME")));

		state.setUseNormalisation((Boolean) result.get(LanguageTool.getString("USE_NORMALISATION")));
		state.setPlayAtStartup((Boolean) result.get(LanguageTool.getString("PLAY_AT_STARTUP")));
		state.setUseShortPathNames((Boolean) result.get(LanguageTool.getString("USE_SHORT_PATH_NAMES_FOR_MPLAYER")));
		state.setEnableHotkeys((Boolean) result.get(LanguageTool.getString("ENABLE_GOLBAL_HOTKEYS")));

		state.setAutoRepositoryRefreshTime((Integer) result.get(LanguageTool.getString("REPOSITORY_REFRESH_TIME")));
		state.setSaveRepositoryAsXml((Boolean) result.get(LanguageTool.getString("SAVE_REPOSITORY_AS_XML")));

		state.setShowFavoritesInNavigator((Boolean) result.get(LanguageTool.getString("SHOW_FAVORITES")));
		state.setShowAlbumTooltip((Boolean) result.get(LanguageTool.getString("SHOW_ALBUM_TOOLTIP")));
		state.setAlbumTooltipDelay((Integer) result.get(LanguageTool.getString("ALBUM_TOOLTIP_DELAY")));
		state.setUseSmartTagViewSorting((Boolean) result.get(LanguageTool.getString("USE_SMART_TAG_VIEW_SORTING")));

		state.setAnimateOSD((Boolean) result.get(LanguageTool.getString("ANIMATE_OSD")));
		state.setOsdDuration((Integer) result.get(LanguageTool.getString("OSD_DURATION")));
		state.setTransparentOSD((Boolean) result.get(LanguageTool.getString("TRANSPARENT_OSD")));

		state.setUseAudioScrobbler((Boolean) result.get(LanguageTool.getString("ACTIVATE_AUDIO_SCROBBLER")));
		state.setSavePictureFromAudioScrobbler((Boolean) result.get(LanguageTool.getString("SAVE_PICTURES_TO_AUDIO_FOLDERS")));
		state.setProxy((ProxyBean) result.get("PROXY"));

		state.setLastFmUser((String) result.get(LanguageTool.getString("LASTFM_USER")));
		state.setLastFmPassword((String) result.get(LanguageTool.getString("LASTFM_PASSWORD")));
		state.setLastFmEnabled(true);

		state.setDefaultDeviceLocation((String) result.get(LanguageTool.getString("DEVICE_DEFAULT_LOCATION")));

		state.setReadInfoFromRadioStream((Boolean) result.get(LanguageTool.getString("READ_INFO_FROM_RADIO_STREAM")));

		state.setPodcastFeedEntriesRetrievalInterval((Long) result.get(LanguageTool.getString("PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL")));
	}

	public void start() {
		logger.debug(LogCategories.CONTROLLER);

		dialogControlled.setPanels(panels);
		buildList();
		updateProperties();

		addBindings();

		dialogControlled.setVisible(true);

	}

	void updateApplication() {
		VisualHandler.getInstance().showAudioScrobblerPanel(state.isUseAudioScrobbler(), true);
		VisualHandler.getInstance().repaint();

		ControllerProxy.getInstance().getOSDDialogController().setTransparent(state.isTransparentOSD());
		ControllerProxy.getInstance().getNavigationController().refreshTagViewTreeContent();
		PlayerHandler.getInstance().setUseNormalisation(state.isUseNormalisation());
		AudioScrobblerServiceHandler.getInstance().updateService(state.getProxy(), Kernel.getInstance().state.getLastFmUser(), state.getLastFmPassword());
		SystemTrayHandler.getInstance().setTrayIconVisible(state.isShowSystemTray());
		SystemTrayHandler.getInstance().setTrayPlayerVisible(state.isShowTrayPlayer());
		DeviceConnectionMonitor.startMonitor();
		if (PodcastFeedEntryRetriever.getInstance() != null) {
			PodcastFeedEntryRetriever.getInstance().setRetrievalInterval(state.getPodcastFeedEntriesRetrievalInterval());
		}
		HotkeyHandler.getInstance().enableHotkeys(state.isEnableHotkeys());
	}

	// Updates properties that may be changed
	private void updateProperties() {
		((AudioScrobblerPanel) panels[5]).setActivateAudioScrobbler(state.isUseAudioScrobbler());
	}

}
