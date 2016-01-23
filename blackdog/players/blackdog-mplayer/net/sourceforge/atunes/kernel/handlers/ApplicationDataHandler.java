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

package net.sourceforge.atunes.kernel.handlers;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.WindowConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.StandardFrame;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListColumns;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.favorites.Favorites;
import net.sourceforge.atunes.kernel.modules.playlist.ListOfPlayLists;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.Repository;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.utils.ClosingUtils;
import net.sourceforge.atunes.kernel.utils.CryptoUtils;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.kernel.utils.XMLUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

/**
 * This class is responsible of read, write and apply application state, and
 * caches
 */
public class ApplicationDataHandler {

	/**
	 * Logger
	 */
	private static Logger logger = new Logger();

	private static ApplicationDataHandler instance = new ApplicationDataHandler();

	private ApplicationDataHandler() {
	}

	public static ApplicationDataHandler getInstance() {
		return instance;
	}

	/**
	 * Apply state. Some properties (window maximized, position, etc) are
	 * already setted in gui creation
	 */
	public void applyState() {
		logger.debug(LogCategories.HANDLER);

		Kernel kernel = Kernel.getInstance();
		ApplicationState state = kernel.state;

		// System tray player
		if (state.isShowTrayPlayer()) {
			SystemTrayHandler.getInstance().initTrayPlayerIcons();
		}

		// System tray
		if (state.isShowSystemTray()) {
			SystemTrayHandler.getInstance().initTrayIcon();
		} else {
			VisualHandler.getInstance().setFrameDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}

		// Show playlist controls
		ControllerProxy.getInstance().getPlayListController().showPlaylistControls(state.isShowPlaylistControls());

		// Show OSD
		state.setShowOSD(state.isShowOSD());
		ControllerProxy.getInstance().getMenuController().setShowOSD(state.isShowOSD());
		SystemTrayHandler.getInstance().setShowOSD(state.isShowOSD());
		ControllerProxy.getInstance().getOSDDialogController().setTransparent(state.isTransparentOSD());

		// Shuffle and repeat
		ControllerProxy.getInstance().getPlayerControlsController().setShuffle(state.isShuffle());
		PlayerHandler.getInstance().setShuffle(state.isShuffle());
		SystemTrayHandler.getInstance().setShuffle(state.isShuffle());
		ControllerProxy.getInstance().getPlayerControlsController().setRepeat(state.isRepeat());
		PlayerHandler.getInstance().setRepeat(state.isRepeat());
		SystemTrayHandler.getInstance().setRepeat(state.isRepeat());

		// MPlayer options
		PlayerHandler.getInstance().setUseNormalisation(state.isUseNormalisation());
		PlayerHandler.getInstance().setKaraoke(state.isKaraoke());
		PlayerHandler.getInstance().setEqualizer(state.getEqualizerSettings());
		ControllerProxy.getInstance().getPlayerControlsController().setKaraoke(state.isKaraoke());

		// Hotkeys
		HotkeyHandler.getInstance().enableHotkeys(state.isEnableHotkeys());

		// Status Bar
		VisualHandler.getInstance().showStatusBar(state.isShowStatusBar());
		ControllerProxy.getInstance().getMenuController().setShowStatusBar(state.isShowStatusBar());

		// Song properties visible
		VisualHandler.getInstance().showSongProperties(state.isShowSongProperties(), false);

		// Show navigation panel
		VisualHandler.getInstance().showNavigationPanel(state.isShowNavigationPanel(), false);

		// Show AudioScrobbler
		VisualHandler.getInstance().showAudioScrobblerPanel(state.isUseAudioScrobbler(), false);

		// Selected AudioScrobbler tab
		VisualHandler.getInstance().getAudioScrobblerPanel().getTabbedPane().setSelectedIndex(kernel.state.getSelectedAudioScrobblerTab());

		// Show navigation table
		VisualHandler.getInstance().showNavigationTable(state.isShowNavigationTable());
		ControllerProxy.getInstance().getMenuController().setShowNavigationTable(state.isShowNavigationTable());

		// Navigation Panel View
		ControllerProxy.getInstance().getNavigationController().setNavigationView(state.getNavigationView());

		// Sort device by tag
		ControllerProxy.getInstance().getMenuController().setSortDeviceByTag(state.isSortDeviceByTag());

		// Set volume
		VisualHandler.getInstance().setVolume(state.getVolume());
		PlayerHandler.getInstance().setVolume(state.getVolume());

		// CD ripper settings
		RipperHandler.getInstance().setEncoder(state.getEncoder());
		RipperHandler.getInstance().setEncoderQuality(state.getEncoderQuality());
		RipperHandler.getInstance().setFileNamePattern(state.getFileNamePattern());

		if (!VisualHandler.getInstance().isMultipleWindow()) {
			// Split panes divider location
			if (state.getLeftVerticalSplitPaneDividerLocation() != 0)
				((StandardFrame) VisualHandler.getInstance().frame).setLeftVerticalSplitPaneDividerLocationAndSetWindowSize(state.getLeftVerticalSplitPaneDividerLocation());
			if (state.getRightVerticalSplitPaneDividerLocation() != 0)
				((StandardFrame) VisualHandler.getInstance().frame).setRightVerticalSplitPaneDividerLocationAndSetWindowSize(state.getRightVerticalSplitPaneDividerLocation());
		}
	}

	/**
	 * Gets file where state is stored
	 * 
	 * @param useWorkDir
	 *            if the working diretory should be used for storing the state
	 * @return The file where state is stored
	 */
	private String getPropertiesFile(boolean useWorkDir) {
		return StringUtils.getString(SystemProperties.getUserConfigFolder(useWorkDir), "/", Constants.PROPERTIES_FILE);
	}

	/**
	 * Stores favorites cache
	 * 
	 * @param favorites
	 *            Favorites that should be persisted
	 */
	public void persistFavoritesCache(Favorites favorites) {
		logger.debug(LogCategories.HANDLER);

		ObjectOutputStream stream = null;
		try {
			stream = new ObjectOutputStream(new FileOutputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.CACHE_FAVORITES_NAME)));
			logger.info(LogCategories.HANDLER, "Storing favorites information...");
			stream.writeObject(favorites);
		} catch (Exception e) {
			logger.error(LogCategories.HANDLER, "Could not write favorites");
			logger.debug(LogCategories.HANDLER, e);
		} finally {
			ClosingUtils.close(stream);
		}

		if (Kernel.getInstance().state.isSaveRepositoryAsXml()) {
			try {
				XMLUtils.writeObjectToFile(favorites, StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.XML_CACHE_FAVORITES_NAME));
				logger.info(LogCategories.HANDLER, "Storing favorites information...");
			} catch (Exception e) {
				logger.error(LogCategories.HANDLER, "Could not write favorites");
				logger.debug(LogCategories.HANDLER, e);
			}
		}
	}

	/**
	 * Stores play lists
	 * 
	 */
	public void persistPlayList() {
		logger.debug(LogCategories.HANDLER);

		//		try {
		//			XMLUtils.writeObjectToFile(p, StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.LAST_PLAYLIST_FILE));
		//			logger.info(LogCategories.HANDLER, "Play list saved");
		//		} catch (Exception e) {
		//			logger.error(LogCategories.HANDLER, "Could not persist playlist");
		//			logger.debug(LogCategories.HANDLER, e);
		//		}

		ListOfPlayLists listOfPlayLists = MultiplePlaylistHandler.getInstance().getListOfPlayLists();

		ObjectOutputStream stream = null;
		try {
			stream = new ObjectOutputStream(new FileOutputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.LAST_PLAYLIST_FILE)));
			stream.writeObject(listOfPlayLists);
			logger.info(LogCategories.HANDLER, "Play lists saved");
		} catch (Exception e) {
			logger.error(LogCategories.HANDLER, "Could not persist playlists");
			logger.debug(LogCategories.HANDLER, e);
		} finally {
			ClosingUtils.close(stream);
		}
	}

	/**
	 * Stores podcast feeds
	 * 
	 * @param podcastFeeds
	 *            Podcast feeds that should be persist
	 */
	public void persistPodcastFeedCache(List<PodcastFeed> podcastFeeds) {
		logger.debug(LogCategories.HANDLER);

		try {
			XMLUtils.writeObjectToFile(podcastFeeds, StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.PODCAST_FEED_CACHE));
		} catch (Exception e) {
			logger.error(LogCategories.HANDLER, "Could not persist podcast feeds");
			logger.debug(LogCategories.HANDLER, e);
		}

		//		ObjectOutputStream stream = null;
		//		try {
		//			stream = new ObjectOutputStream(new FileOutputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.PODCAST_FEED_CACHE)));
		//			stream.writeObject(podcastFeeds);
		//		} catch (Exception e) {
		//			logger.error(LogCategories.HANDLER, "Could not persist podcast feeds");
		//			logger.debug(LogCategories.HANDLER, e);
		//		} finally {
		//			ClosingUtils.close(stream);
		//		}
	}

	/**
	 * Stores radios
	 * 
	 * @param radios
	 *            Radios that should be persisted
	 */
	public void persistRadioCache(List<Radio> radios) {
		logger.debug(LogCategories.HANDLER);

		try {
			XMLUtils.writeObjectToFile(radios, StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.RADIO_CACHE));
		} catch (Exception e) {
			logger.error(LogCategories.HANDLER, "Could not persist radios");
			logger.debug(LogCategories.HANDLER, e);
		}

		//		ObjectOutputStream stream = null;
		//		try {
		//			stream = new ObjectOutputStream(new FileOutputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.RADIO_CACHE)));
		//			stream.writeObject(radios);
		//		} catch (Exception e) {
		//			logger.error(LogCategories.HANDLER, "Could not persist radios");
		//			logger.debug(LogCategories.HANDLER, e);
		//		} finally {
		//			ClosingUtils.close(stream);
		//		}
	}

	/**
	 * Stores repository cache
	 * 
	 * @param repository
	 *            The retrieved repository
	 */
	public void persistRepositoryCache(Repository repository) {
		logger.debug(LogCategories.HANDLER);

		ObjectOutputStream oos = null;
		try {
			FileOutputStream fout = new FileOutputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.CACHE_REPOSITORY_NAME));
			oos = new ObjectOutputStream(fout);
			logger.info(LogCategories.HANDLER, "Serialize repository information...");
			long t0 = System.currentTimeMillis();
			oos.writeObject(repository);
			long t1 = System.currentTimeMillis();
			logger.info(LogCategories.HANDLER, StringUtils.getString("DONE (", (t1 - t0) / 1000.0, " seconds)"));
		} catch (Exception e) {
			logger.error(LogCategories.HANDLER, "Could not write serialized repository");
			logger.debug(LogCategories.HANDLER, e);
		} finally {
			ClosingUtils.close(oos);
		}

		if (Kernel.getInstance().state.isSaveRepositoryAsXml()) {
			try {
				logger.info(LogCategories.HANDLER, "Storing repository information as xml...");
				long t0 = System.currentTimeMillis();
				XMLUtils.writeObjectToFile(repository, StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.XML_CACHE_REPOSITORY_NAME));
				long t1 = System.currentTimeMillis();
				logger.info(LogCategories.HANDLER, StringUtils.getString("DONE (", (t1 - t0) / 1000.0, " seconds)"));
			} catch (Exception e) {
				logger.error(LogCategories.HANDLER, "Could not write repository as xml");
				logger.debug(LogCategories.HANDLER, e);
			}
		}
	}

	/**
	 * Read state stored.
	 * 
	 * @return if the state could be read
	 */
	public boolean readState() {
		logger.debug(LogCategories.HANDLER);

		try {
			Kernel.getInstance().state = (ApplicationState) XMLUtils.readBeanFromFile(getPropertiesFile(Kernel.DEBUG));
			ApplicationState state = Kernel.getInstance().state;

			// Decrypt passwords
			if (state.getProxy() != null && state.getProxy().getEncryptedPassword() != null && state.getProxy().getEncryptedPassword().length > 0) {
				state.getProxy().setPassword(new String(CryptoUtils.decrypt(state.getProxy().getEncryptedPassword())));
			}
			if (state.getEncryptedLastFmPassword() != null && state.getEncryptedLastFmPassword().length > 0) {
				state.setLastFmPassword(new String(CryptoUtils.decrypt(state.getEncryptedLastFmPassword())));
			}
			return true;
		} catch (IOException e) {
			logger.info(LogCategories.HANDLER, "Could not read application state");
			return false;
		} catch (GeneralSecurityException e) {
			logger.info(LogCategories.HANDLER, "Could not decrypt passord");
			return true;
		}
	}

	/**
	 * Reads repository cache
	 * 
	 * @return The retrieved favorites
	 */
	protected Favorites retrieveFavoritesCache() {
		logger.debug(LogCategories.HANDLER);

		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.CACHE_FAVORITES_NAME)));
			logger.info(LogCategories.HANDLER, "Reading serialized favorites cache");
			Favorites result = (Favorites) stream.readObject();
			return result;
		} catch (Exception e) {
			logger.info(LogCategories.HANDLER, "No serialized favorites info found");
			if (Kernel.getInstance().state.isSaveRepositoryAsXml()) {
				try {
					logger.info(LogCategories.HANDLER, "Reading xml favorites cache");
					Favorites result = (Favorites) XMLUtils.readObjectFromFile(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/",
							Constants.XML_CACHE_FAVORITES_NAME));
					return result;
				} catch (Exception e1) {
					logger.info(LogCategories.HANDLER, "No xml favorites info found");
					return null;
				}
			} else {
				return null;
			}
		} finally {
			ClosingUtils.close(stream);
		}
	}

	/**
	 * Reads play list cache
	 * 
	 * @return The retrieved play list
	 */
	protected ListOfPlayLists retrievePlayListCache() {
		logger.debug(LogCategories.HANDLER);

		//		try {
		//			PlayList obj = (PlayList) XMLUtils.readObjectFromFile(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.LAST_PLAYLIST_FILE));
		//			logger.info(LogCategories.HANDLER, StringUtils.getString("Play list loaded (", obj.size(), " songs)"));
		//			return obj;
		//		} catch (Exception e) {
		//			return new PlayList();
		//		}

		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.LAST_PLAYLIST_FILE)));
			ListOfPlayLists obj = (ListOfPlayLists) stream.readObject();
			logger.info(LogCategories.HANDLER, StringUtils.getString("Play lists loaded (", obj.getPlayLists().size(), " playlists)"));
			return obj;
		} catch (Exception e) {
			logger.error(LogCategories.HANDLER, e);
			return ListOfPlayLists.getEmptyPlayList();
		} finally {
			ClosingUtils.close(stream);
		}
	}

	/**
	 * Reads podcast feed cache
	 * 
	 * @return The retrieved podcast feeds
	 */
	@SuppressWarnings("unchecked")
	protected List<PodcastFeed> retrievePodcastFeedCache() {
		logger.debug(LogCategories.HANDLER);

		try {
			return (List<PodcastFeed>) XMLUtils.readObjectFromFile(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.PODCAST_FEED_CACHE));
		} catch (Exception e) {
			/*
			 * java.util.concurrent.CopyOnWriteArrayList instead of e.g.
			 * java.util.ArrayList to avoid ConcurrentModificationException
			 */
			return Collections.synchronizedList(new CopyOnWriteArrayList<PodcastFeed>());
		}

		//		ObjectInputStream stream = null;
		//		try {
		//			stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.PODCAST_FEED_CACHE)));
		//			List<PodcastFeed> podcastFeeds = (List<PodcastFeed>) stream.readObject();
		//			return podcastFeeds;
		//		} catch (Exception e) {
		//			/*
		//			 * java.util.concurrent.CopyOnWriteArrayList instead of e.g.
		//			 * java.util.ArrayList to avoid ConcurrentModificationException
		//			 */
		//			return Collections.synchronizedList(new CopyOnWriteArrayList<PodcastFeed>());
		//		} finally {
		//			ClosingUtils.close(stream);
		//		}
	}

	/**
	 * Reads radio cache
	 * 
	 * @return The retrieved radios
	 */
	@SuppressWarnings("unchecked")
	protected List<Radio> retrieveRadioCache() {
		logger.debug(LogCategories.HANDLER);

		try {
			return (List<Radio>) XMLUtils.readObjectFromFile(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.RADIO_CACHE));
		} catch (Exception e) {
			return new ArrayList<Radio>();
		}

		//		ObjectInputStream stream = null;
		//		try {
		//			stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.RADIO_CACHE)));
		//			List<Radio> radios = (List<Radio>) stream.readObject();
		//			return radios;
		//		} catch (Exception e) {
		//			return new ArrayList<Radio>();
		//		} finally {
		//			ClosingUtils.close(stream);
		//		}
	}

	/**
	 * Reads repository cache
	 * 
	 * @return The retrieved repository
	 */
	protected Repository retrieveRepositoryCache() {
		logger.debug(LogCategories.HANDLER);

		ObjectInputStream ois = null;
		try {
			FileInputStream fis = new FileInputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.CACHE_REPOSITORY_NAME));
			ois = new ObjectInputStream(fis);
			logger.info(LogCategories.HANDLER, "Reading serialized repository cache");
			long t0 = System.currentTimeMillis();
			Repository result = (Repository) ois.readObject();
			long t1 = System.currentTimeMillis();
			logger.info(LogCategories.HANDLER, StringUtils.getString("Reading repository cache done (", (t1 - t0) / 1000.0, " seconds)"));
			return result;
		} catch (Exception e) {
			logger.info(LogCategories.HANDLER, "No serialized repository info found");
			if (Kernel.getInstance().state.isSaveRepositoryAsXml()) {
				try {
					logger.info(LogCategories.HANDLER, "Reading xml repository cache");
					long t0 = System.currentTimeMillis();
					Repository repository = (Repository) XMLUtils.readObjectFromFile(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/",
							Constants.XML_CACHE_REPOSITORY_NAME));
					long t1 = System.currentTimeMillis();
					logger.info(LogCategories.HANDLER, StringUtils.getString("Reading repository cache done (", (t1 - t0) / 1000.0, " seconds)"));
					return repository;
				} catch (Exception e1) {
					logger.info(LogCategories.HANDLER, "No xml repository info found");
					return null;
				}
			} else {
				return null;
			}
		} finally {
			ClosingUtils.close(ois);
		}
	}

	/**
	 * Stores state
	 */
	public void storeState() {
		logger.debug(LogCategories.HANDLER);

		ApplicationState state = Kernel.getInstance().state;

		if (!VisualHandler.getInstance().isMultipleWindow()) {
			// Set window location on state
			state.setWindowXPosition(VisualHandler.getInstance().getWindowLocation().x);
			state.setWindowYPosition(VisualHandler.getInstance().getWindowLocation().y);
			// Window full maximized
			state.setMaximized(VisualHandler.getInstance().isMaximized());
			// Set window size
			state.setWindowWidth(VisualHandler.getInstance().getWindowSize().width);
			state.setWindowHeight(VisualHandler.getInstance().getWindowSize().height);
			// Set split panes divider location
			state.setLeftVerticalSplitPaneDividerLocation(((StandardFrame) VisualHandler.getInstance().frame).getLeftVerticalSplitPane().getDividerLocation());
			state.setRightVerticalSplitPaneDividerLocation(((StandardFrame) VisualHandler.getInstance().frame).getRightVerticalSplitPane().getDividerLocation());
		} else {
			// Set window location on state
			state.setMultipleViewXPosition(VisualHandler.getInstance().getWindowLocation().x);
			state.setMultipleViewYPosition(VisualHandler.getInstance().getWindowLocation().y);
			// Set window size
			state.setMultipleViewWidth(VisualHandler.getInstance().getWindowSize().width);
			state.setMultipleViewHeight(VisualHandler.getInstance().getWindowSize().height);
		}
		// Set horizontal split pane divider location
		state.setLeftHorizontalSplitPaneDividerLocation(VisualHandler.getInstance().frame.getNavigationPanel().getSplitPane().getDividerLocation());

		// Selected AudioScrobbler tab
		state.setSelectedAudioScrobblerTab(VisualHandler.getInstance().getAudioScrobblerPanel().getTabbedPane().getSelectedIndex());

		// Volume level
		state.setVolume(PlayerHandler.getInstance().getVolume());

		// CD ripper settings
		state.setEncoder(RipperHandler.getInstance().getEncoder());
		state.setEncoderQuality(RipperHandler.getInstance().getEncoderQuality());
		state.setEqualizerSettings(PlayerHandler.getInstance().getEqualizer());
		state.setFileNamePattern(RipperHandler.getInstance().getFileNamePattern());

		// Save column settings
		PlayListColumns.storeCurrentColumnSettings();

		// Encrypt passwords
		try {
			state.setEncryptedLastFmPassword(null);
			if (state.getLastFmPassword() != null && !state.getLastFmPassword().isEmpty()) {
				state.setEncryptedLastFmPassword(CryptoUtils.encrypt(state.getLastFmPassword().getBytes()));
			}
			if (state.getProxy() != null && state.getProxy().getPassword() != null && !state.getProxy().getPassword().isEmpty()) {
				state.getProxy().setEncryptedPassword(CryptoUtils.encrypt(state.getProxy().getPassword().getBytes()));
			}
		} catch (GeneralSecurityException e) {
			logger.error(LogCategories.HANDLER, e);
		} catch (IOException e) {
			logger.error(LogCategories.HANDLER, e);
		}

		try {
			// Make not encrypted password transient
			BeanInfo stateInfo = Introspector.getBeanInfo(ApplicationState.class);
			for (PropertyDescriptor pd : stateInfo.getPropertyDescriptors()) {
				if (pd.getName().equals("lastFmPassword")) {
					pd.setValue("transient", Boolean.TRUE);
				}
			}

			// Make not encrypted password transient
			BeanInfo proxyInfo = Introspector.getBeanInfo(ProxyBean.class);
			for (PropertyDescriptor pd : proxyInfo.getPropertyDescriptors()) {
				if (pd.getName().equals("password")) {
					pd.setValue("transient", Boolean.TRUE);
				}
			}

			XMLUtils.writeBeanToFile(state, getPropertiesFile(Kernel.DEBUG));
		} catch (IOException e) {
			logger.error(LogCategories.HANDLER, "Error storing application state");
			logger.error(LogCategories.HANDLER, e);
		} catch (IntrospectionException e) {
			logger.error(LogCategories.HANDLER, "Error storing application state");
			logger.error(LogCategories.HANDLER, e);
		}
	}
}
