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

package net.sourceforge.atunes.kernel.modules.state;

import java.util.Map;

import net.sourceforge.atunes.gui.LookAndFeelSelector;
import net.sourceforge.atunes.gui.StandardFrame;
import net.sourceforge.atunes.gui.views.controls.playList.ColumnBean;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntryRetriever;
import net.sourceforge.atunes.kernel.modules.search.Search;
import net.sourceforge.atunes.kernel.modules.search.SearchFactory;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;

/**
 * <p>
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * </p>
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationState {

	private boolean showNavigationPanel = true;
	private boolean showNavigationTable = true;
	private boolean showSongProperties;
	private boolean showStatusBar = true;
	private boolean showOSD;
	private boolean shuffle;
	private boolean repeat;
	private boolean sortDeviceByTag;
	private boolean showSystemTray;
	private boolean showTrayPlayer;
	private int navigationView;
	private LocaleBean locale;
	private String defaultSearch;
	private boolean useAudioScrobbler = true;
	private int selectedAudioScrobblerTab;
	private boolean multipleWindow;
	private boolean showPlaylistControls = true;
	private ProxyBean proxy;
	private String theme = LookAndFeelSelector.DEFAULT_THEME;
	private boolean useDefaultFont;
	private boolean playAtStartup;

	// MPlayer options
	private boolean useNormalisation;
	private boolean karaoke;
	private boolean useShortPathNames = true;
	private float[] equalizerSettings;

	private boolean readInfoFromRadioStream = true;

	private boolean enableHotkeys;
	private boolean showTitle = true;
	private int osdDuration = 2; // In seconds
	private int autoRepositoryRefreshTime = 60; // In minutes
	private boolean saveRepositoryAsXml = true;
	private boolean animateOSD = true;
	private boolean transparentOSD;
	private boolean showFavoritesInNavigator = true;
	private boolean useSmartTagViewSorting;
	private boolean savePictureFromAudioScrobbler = true;
	private boolean showAlbumTooltip = true;
	private int albumTooltipDelay = 1; // In seconds

	private boolean lastFmEnabled = true;
	private String lastFmUser;
	private String lastFmPassword;
	private byte[] encryptedLastFmPassword;

	private String fullScreenBackground;

	private int windowXPosition;
	private int windowYPosition;
	private boolean maximized;
	private int windowWidth;
	private int windowHeight;
	private int multipleViewXPosition;
	private int multipleViewYPosition;
	private int multipleViewWidth;
	private int multipleViewHeight;
	private int nagDialogCounter;
	private int volume = 50;
	private String encoder = "OGG";
	private String encoderQuality = "5";
	private String mp3EncoderQuality = "medium";
	private String flacEncoderQuality = "-5";
	private String fileNamePattern;

	// Columns config
	private Map<String, ColumnBean> columns;

	// Split panes divider location
	private int leftVerticalSplitPaneDividerLocation = StandardFrame.NAVIGATION_PANEL_WIDTH;
	private int rightVerticalSplitPaneDividerLocation;
	private int leftHorizontalSplitPaneDividerLocation = StandardFrame.NAVIGATOR_SPLIT_PANE_DIVIDER_LOCATION;

	/**
	 * Default location where device is plugged. Used to connect device
	 * automatically
	 */
	private String defaultDeviceLocation;

	private long podcastFeedEntriesRetrievalInterval = PodcastFeedEntryRetriever.DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL;

	public ApplicationState() {
	}

	public int getAlbumTooltipDelay() {
		return albumTooltipDelay;
	}

	public int getAutoRepositoryRefreshTime() {
		return autoRepositoryRefreshTime;
	}

	/**
	 * @return the columns
	 */
	public Map<String, ColumnBean> getColumns() {
		return columns;
	}

	/**
	 * @return the defaultDeviceLocation
	 */
	public String getDefaultDeviceLocation() {
		return defaultDeviceLocation;
	}

	public String getDefaultSearch() {
		return defaultSearch;
	}

	public Search getDefaultSearchObject() {
		return SearchFactory.getSearchForName(defaultSearch);
	}

	public String getEncoder() {
		return encoder;
	}

	/**
	 * Last used setting of the ogg encoder
	 * 
	 * @return Returns the last used encoder quality setting for the ogg encoder
	 */
	public String getEncoderQuality() {
		return encoderQuality;
	}

	/**
	 * @return the encryptedLastFmPassword
	 */
	public byte[] getEncryptedLastFmPassword() {
		return encryptedLastFmPassword;
	}

	/**
	 * 
	 * @return Equalizer settings for mplayer
	 */
	public float[] getEqualizerSettings() {
		return equalizerSettings;
	}

	/**
	 * Last used filename pattern.
	 * 
	 * @return Returns the last used filename pattern setting.
	 */
	public String getFileNamePattern() {
		return fileNamePattern;
	}

	/**
	 * Last used setting of the flac encoder.
	 * 
	 * @return Returns the last used encoder quality setting for the flac
	 *         encoder.
	 */
	public String getFlacEncoderQuality() {
		return flacEncoderQuality;
	}

	/**
	 * @return the fullScreenBackground
	 */
	public String getFullScreenBackground() {
		return fullScreenBackground;
	}

	/**
	 * @return the lastFmPassword
	 */
	public String getLastFmPassword() {
		return lastFmPassword;
	}

	/**
	 * @return the lastFmUser
	 */
	public String getLastFmUser() {
		return lastFmUser;
	}

	/**
	 * @return the leftHorizontalSplitPaneDividerLocation
	 */
	public int getLeftHorizontalSplitPaneDividerLocation() {
		return leftHorizontalSplitPaneDividerLocation;
	}

	/**
	 * @return the leftVerticalSplitPaneDividerLocation
	 */
	public int getLeftVerticalSplitPaneDividerLocation() {
		return leftVerticalSplitPaneDividerLocation;
	}

	public LocaleBean getLocale() {
		return locale;
	}

	/**
	 * Last used setting of the mp3 encoder
	 * 
	 * @return Returns the last used encoder quality setting for the mp3 encoder
	 */
	public String getMp3EncoderQuality() {
		return mp3EncoderQuality;
	}

	/**
	 * @return the multipleViewHeight
	 */
	public int getMultipleViewHeight() {
		return multipleViewHeight;
	}

	/**
	 * @return the multipleViewWidth
	 */
	public int getMultipleViewWidth() {
		return multipleViewWidth;
	}

	/**
	 * @return the multipleViewXPosition
	 */
	public int getMultipleViewXPosition() {
		return multipleViewXPosition;
	}

	/**
	 * @return the multipleViewYPosition
	 */
	public int getMultipleViewYPosition() {
		return multipleViewYPosition;
	}

	public int getNagDialogCounter() {
		return nagDialogCounter;
	}

	public int getNavigationView() {
		return navigationView;
	}

	public int getOsdDuration() {
		return osdDuration;
	}

	/**
	 * @return the podcastFeedEntriesRetrievalInterval
	 */
	public long getPodcastFeedEntriesRetrievalInterval() {
		return podcastFeedEntriesRetrievalInterval;
	}

	public ProxyBean getProxy() {
		return proxy;
	}

	/**
	 * @return the rightVerticalSplitPaneDividerLocation
	 */
	public int getRightVerticalSplitPaneDividerLocation() {
		return rightVerticalSplitPaneDividerLocation;
	}

	public int getSelectedAudioScrobblerTab() {
		return selectedAudioScrobblerTab;
	}

	public String getTheme() {
		return theme;
	}

	public int getVolume() {
		return volume;
	}

	/**
	 * @return the windowHeight
	 */
	public int getWindowHeight() {
		return windowHeight;
	}

	/**
	 * @return the windowWidth
	 */
	public int getWindowWidth() {
		return windowWidth;
	}

	/**
	 * @return the windowXPosition
	 */
	public int getWindowXPosition() {
		return windowXPosition;
	}

	/**
	 * @return the windowYPosition
	 */
	public int getWindowYPosition() {
		return windowYPosition;
	}

	public boolean isAnimateOSD() {
		return animateOSD;
	}

	public boolean isEnableHotkeys() {
		return enableHotkeys;
	}

	/**
	 * @return the karaoke
	 */
	public boolean isKaraoke() {
		return karaoke;
	}

	/**
	 * @return the lastFmEnabled
	 */
	public boolean isLastFmEnabled() {
		return lastFmEnabled;
	}

	public boolean isMaximized() {
		return maximized;
	}

	public boolean isMultipleWindow() {
		return multipleWindow;
	}

	public int isNavigationView() {
		return navigationView;
	}

	/**
	 * @return the playAtStartup
	 */
	public boolean isPlayAtStartup() {
		return playAtStartup;
	}

	public boolean isReadInfoFromRadioStream() {
		return readInfoFromRadioStream;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public boolean isSavePictureFromAudioScrobbler() {
		return savePictureFromAudioScrobbler;
	}

	public boolean isSaveRepositoryAsXml() {
		return saveRepositoryAsXml;
	}

	public boolean isShowAlbumTooltip() {
		return showAlbumTooltip;
	}

	public boolean isShowFavoritesInNavigator() {
		return showFavoritesInNavigator;
	}

	public boolean isShowNavigationPanel() {
		return showNavigationPanel;
	}

	public boolean isShowNavigationTable() {
		return showNavigationTable;
	}

	public boolean isShowOSD() {
		return showOSD;
	}

	public boolean isShowPlaylistControls() {
		return showPlaylistControls;
	}

	public boolean isShowSongProperties() {
		return showSongProperties;
	}

	public boolean isShowStatusBar() {
		return showStatusBar;
	}

	public boolean isShowSystemTray() {
		return showSystemTray;
	}

	public boolean isShowTitle() {
		return showTitle;
	}

	public boolean isShowTrayPlayer() {
		return showTrayPlayer;
	}

	public boolean isShuffle() {
		return shuffle;
	}

	public boolean isSortDeviceByTag() {
		return sortDeviceByTag;
	}

	public boolean isTransparentOSD() {
		return transparentOSD;
	}

	public boolean isUseAudioScrobbler() {
		return useAudioScrobbler;
	}

	/**
	 * @return the useDefaultFont
	 */
	public boolean isUseDefaultFont() {
		return useDefaultFont;
	}

	/**
	 * @return the useNormalisation
	 */
	public boolean isUseNormalisation() {
		return useNormalisation;
	}

	public boolean isUseShortPathNames() {
		return useShortPathNames;
	}

	/**
	 * @return the useSmartTagViewSorting
	 */
	public boolean isUseSmartTagViewSorting() {
		return useSmartTagViewSorting;
	}

	public void setAlbumTooltipDelay(int albumTooltipDelay) {
		this.albumTooltipDelay = albumTooltipDelay;
	}

	public void setAnimateOSD(boolean animateOSD) {
		this.animateOSD = animateOSD;
	}

	public void setAutoRepositoryRefreshTime(int autoRepositoryRefreshTime) {
		this.autoRepositoryRefreshTime = autoRepositoryRefreshTime;
	}

	/**
	 * @param columns
	 *            the columns to set
	 */
	public void setColumns(Map<String, ColumnBean> columns) {
		this.columns = columns;
	}

	/**
	 * @param defaultDeviceLocation
	 *            the defaultDeviceLocation to set
	 */
	public void setDefaultDeviceLocation(String defaultDeviceLocation) {
		this.defaultDeviceLocation = defaultDeviceLocation;
	}

	public void setDefaultSearch(String defaultSearch) {
		this.defaultSearch = defaultSearch;
	}

	public void setDefaultSearchObject(Search defaultSearch) {
		this.defaultSearch = defaultSearch.toString();
	}

	public void setEnableHotkeys(boolean enableHotkeys) {
		this.enableHotkeys = enableHotkeys;
	}

	/**
	 * Sets the encoder to use for CD ripping
	 * 
	 * @param encoder
	 *            As of aTunes 1.7.3 one of the following is permissible: "OGG",
	 *            "FLAC" or "MP3".
	 */
	public void setEncoder(String encoder) {
		this.encoder = encoder;
	}

	/**
	 * Sets the ogg encoder quality to use for CD ripping
	 * 
	 * @param encoderQuality
	 *            One of the following integer (no decimals) are permissible: -1
	 *            to 10
	 */
	public void setEncoderQuality(String encoderQuality) {
		this.encoderQuality = encoderQuality;
	}

	/**
	 * @param encryptedLastFmPassword
	 *            the encryptedLastFmPassword to set
	 */
	public void setEncryptedLastFmPassword(byte[] encryptedLastFmPassword) {
		this.encryptedLastFmPassword = encryptedLastFmPassword;
	}

	/**
	 * Sets the equalizer settings
	 * 
	 * @param equalizer
	 *            equalizer settings, ready to be passed to mplayer
	 */
	public void setEqualizerSettings(float[] equalizerSettings) {
		this.equalizerSettings = equalizerSettings;
	}

	/**
	 * Sets the filename pattern.
	 * 
	 * @param fileNamePattern
	 *            For valid filename patterns please see RipCdDialog.
	 */
	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

	/**
	 * Sets the flac encoder quality to use for CD ripping.
	 * 
	 * @param flacEncoderQuality
	 *            One of the following negative integer (no decimals) are
	 *            permissible: -8 to -0
	 */
	public void setFlacEncoderQuality(String flacEncoderQuality) {
		this.flacEncoderQuality = flacEncoderQuality;
	}

	/**
	 * @param fullScreenBackground
	 *            the fullScreenBackground to set
	 */
	public void setFullScreenBackground(String fullScreenBackground) {
		this.fullScreenBackground = fullScreenBackground;
	}

	/**
	 * @param karaoke
	 *            the karaoke to set
	 */
	public void setKaraoke(boolean karaoke) {
		this.karaoke = karaoke;
	}

	/**
	 * @param lastFmEnabled
	 *            the lastFmEnabled to set
	 */
	public void setLastFmEnabled(boolean lastFmEnabled) {
		this.lastFmEnabled = lastFmEnabled;
	}

	/**
	 * @param lastFmPassword
	 *            the lastFmPassword to set
	 */
	public void setLastFmPassword(String lastFmPassword) {
		this.lastFmPassword = lastFmPassword;
	}

	/**
	 * @param lastFmUser
	 *            the lastFmUser to set
	 */
	public void setLastFmUser(String lastFmUser) {
		this.lastFmUser = lastFmUser;
	}

	/**
	 * @param leftHorizontalSplitPaneDividerLocation
	 *            the leftHorizontalSplitPaneDividerLocation to set
	 */
	public void setLeftHorizontalSplitPaneDividerLocation(int leftHorizontalSplitPaneDividerLocation) {
		this.leftHorizontalSplitPaneDividerLocation = leftHorizontalSplitPaneDividerLocation;
	}

	/**
	 * @param leftVerticalSplitPaneDividerLocation
	 *            the leftVerticalSplitPaneDividerLocation to set
	 */
	public void setLeftVerticalSplitPaneDividerLocation(int leftVerticalSplitPaneDividerLocation) {
		this.leftVerticalSplitPaneDividerLocation = leftVerticalSplitPaneDividerLocation;
	}

	public void setLocale(LocaleBean locale) {
		this.locale = locale;
	}

	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}

	/**
	 * Sets the mp3 encoder quality to use for CD ripping
	 * 
	 * @param mp3EncoderQuality
	 *            One of the following are permissible: insane, extreme, medium,
	 *            standard, 128, 160, 192, 224, 256, 320
	 */
	public void setMp3EncoderQuality(String mp3EncoderQuality) {
		this.mp3EncoderQuality = mp3EncoderQuality;
	}

	/**
	 * @param multipleViewHeight
	 *            the multipleViewHeight to set
	 */
	public void setMultipleViewHeight(int multipleViewHeight) {
		this.multipleViewHeight = multipleViewHeight;
	}

	/**
	 * @param multipleViewWidth
	 *            the multipleViewWidth to set
	 */
	public void setMultipleViewWidth(int multipleViewWidth) {
		this.multipleViewWidth = multipleViewWidth;
	}

	/**
	 * @param multipleViewXPosition
	 *            the multipleViewXPosition to set
	 */
	public void setMultipleViewXPosition(int multipleViewXPosition) {
		this.multipleViewXPosition = multipleViewXPosition;
	}

	/**
	 * @param multipleViewYPosition
	 *            the multipleViewYPosition to set
	 */
	public void setMultipleViewYPosition(int multipleViewYPosition) {
		this.multipleViewYPosition = multipleViewYPosition;
	}

	public void setMultipleWindow(boolean multipleWindow) {
		this.multipleWindow = multipleWindow;
	}

	public void setNagDialogCounter(int nagDialogCounter) {
		this.nagDialogCounter = nagDialogCounter;
	}

	public void setNavigationView(int navigationView) {
		this.navigationView = navigationView;
	}

	public void setOsdDuration(int osdDuration) {
		this.osdDuration = osdDuration;
	}

	/**
	 * @param playAtStartup
	 *            the playAtStartup to set
	 */
	public void setPlayAtStartup(boolean playAtStartup) {
		this.playAtStartup = playAtStartup;
	}

	/**
	 * @param podcastFeedEntriesRetrievalInterval
	 *            the podcastFeedEntriesRetrievalInterval to set
	 */
	public void setPodcastFeedEntriesRetrievalInterval(long podcastFeedEntriesRetrievalInterval) {
		this.podcastFeedEntriesRetrievalInterval = podcastFeedEntriesRetrievalInterval;
	}

	public void setProxy(ProxyBean proxy) {
		this.proxy = proxy;
	}

	public void setReadInfoFromRadioStream(boolean readInfoFromRadioStream) {
		this.readInfoFromRadioStream = readInfoFromRadioStream;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	/**
	 * @param rightVerticalSplitPaneDividerLocation
	 *            the rightVerticalSplitPaneDividerLocation to set
	 */
	public void setRightVerticalSplitPaneDividerLocation(int rightVerticalSplitPaneDividerLocation) {
		this.rightVerticalSplitPaneDividerLocation = rightVerticalSplitPaneDividerLocation;
	}

	public void setSavePictureFromAudioScrobbler(boolean savePictureFromAudioScrobbler) {
		this.savePictureFromAudioScrobbler = savePictureFromAudioScrobbler;
	}

	public void setSaveRepositoryAsXml(boolean saveRepositoryAsXml) {
		this.saveRepositoryAsXml = saveRepositoryAsXml;
	}

	public void setSelectedAudioScrobblerTab(int selectedAudioScrobblerTab) {
		this.selectedAudioScrobblerTab = selectedAudioScrobblerTab;
	}

	public void setShowAlbumTooltip(boolean showAlbumTooltip) {
		this.showAlbumTooltip = showAlbumTooltip;
	}

	public void setShowFavoritesInNavigator(boolean showFavoritesInNavigator) {
		this.showFavoritesInNavigator = showFavoritesInNavigator;
	}

	public void setShowNavigationPanel(boolean showNavigationPanel) {
		this.showNavigationPanel = showNavigationPanel;
	}

	public void setShowNavigationTable(boolean showNavigationTable) {
		this.showNavigationTable = showNavigationTable;
	}

	public void setShowOSD(boolean showOSD) {
		this.showOSD = showOSD;
	}

	public void setShowPlaylistControls(boolean showPlaylistControls) {
		this.showPlaylistControls = showPlaylistControls;
	}

	public void setShowSongProperties(boolean showSongProperties) {
		this.showSongProperties = showSongProperties;
	}

	public void setShowStatusBar(boolean showStatusBar) {
		this.showStatusBar = showStatusBar;
	}

	public void setShowSystemTray(boolean showSystemTray) {
		this.showSystemTray = showSystemTray;
	}

	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

	public void setShowTrayPlayer(boolean showTrayPlayer) {
		this.showTrayPlayer = showTrayPlayer;
	}

	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	public void setSortDeviceByTag(boolean sortDeviceByTag) {
		this.sortDeviceByTag = sortDeviceByTag;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public void setTransparentOSD(boolean transparentOSD) {
		this.transparentOSD = transparentOSD;
	}

	public void setUseAudioScrobbler(boolean useAudioScrobbler) {
		this.useAudioScrobbler = useAudioScrobbler;
	}

	/**
	 * @param useDefaultFont
	 *            the useDefaultFont to set
	 */
	public void setUseDefaultFont(boolean useDefaultFont) {
		this.useDefaultFont = useDefaultFont;
	}

	/**
	 * @param useNormalisation
	 *            the useNormalisation to set
	 */
	public void setUseNormalisation(boolean useNormalisation) {
		this.useNormalisation = useNormalisation;
	}

	public void setUseShortPathNames(boolean useShortPathNames) {
		this.useShortPathNames = useShortPathNames;
	}

	/**
	 * @param useSmartTagViewSorting
	 *            the useSmartTagViewSorting to set
	 */
	public void setUseSmartTagViewSorting(boolean useSmartTagViewSorting) {
		this.useSmartTagViewSorting = useSmartTagViewSorting;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	/**
	 * @param windowHeight
	 *            the windowHeight to set
	 */
	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	/**
	 * @param windowWidth
	 *            the windowWidth to set
	 */
	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	/**
	 * @param windowXPosition
	 *            the windowXPosition to set
	 */
	public void setWindowXPosition(int windowXPosition) {
		this.windowXPosition = windowXPosition;
	}

	/**
	 * @param windowYPosition
	 *            the windowYPosition to set
	 */
	public void setWindowYPosition(int windowYPosition) {
		this.windowYPosition = windowYPosition;
	}

}
