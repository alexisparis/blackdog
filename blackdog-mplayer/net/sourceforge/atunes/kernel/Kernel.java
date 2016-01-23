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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.handlers.ApplicationDataHandler;
import net.sourceforge.atunes.kernel.handlers.DeviceHandler;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.handlers.HotkeyHandler;
import net.sourceforge.atunes.kernel.handlers.MultipleInstancesHandler;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.handlers.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.handlers.RadioHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.SystemTrayHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.device.DeviceConnectionMonitor;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListCommonOps;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntryRetriever;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.updates.ApplicationUpdates;
import net.sourceforge.atunes.kernel.utils.LanguageSelector;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

/**
 * The Kernel is the class responsible of create and interconnect all modules of
 * aTunes.
 */
public class Kernel {

	/** Logger */
	private static Logger logger = new Logger();

	/**
	 * Unique instance of Kernel. To access Kernel, Kernel.getInstance() must be
	 * called
	 */
	private static Kernel instance;

	/** Defines if aTunes is running in debug mode */
	public static boolean DEBUG;

	/** Defines if aTunes will ignore look and feel */
	public static boolean IGNORE_LOOK_AND_FEEL;

	/** Application State of aTunes */
	public ApplicationState state;

	/** Constructor of Kernel */
	private Kernel() {
		// Nothing to do
	}

	/**
	 * Getter of the Kernel instance
	 * 
	 * @return Kernel
	 */
	public static Kernel getInstance() {
		return instance;
	}

	/**
	 * Static method to create the Kernel instance. This method starts the
	 * application, so should be called from the main method of the application.
	 */
	public static void startKernel(final List<String> args) {
		logger.debug(LogCategories.START, "Starting Kernel");

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					instance = new Kernel();
					if (!ApplicationDataHandler.getInstance().readState())
						instance.state = new ApplicationState();
					// Set look and feel
					LookAndFeelSelector.setLookAndFeel(instance.state.getTheme());
					ColorDefinitions.initColors();
					// Set language
					LanguageSelector.setLanguage();
					// Init fonts
					Fonts.initialize();
				}
			});
		} catch (Exception e) {
			logger.internalError(e);
		}

		// Show title dialog
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					VisualHandler.getInstance().showSplashScreen();
				}
			});
		} catch (Exception e) {
			logger.internalError(e);
		}

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					// Find for audio files on arguments
					final List<String> songs = new ArrayList<String>();
					for (String arg : args) {
						if (AudioFile.isValidAudioFile(arg))
							songs.add(arg);
						else if (PlayListIO.isValidPlayList(arg)) {
							songs.addAll(PlayListIO.read(new File(arg)));
						}
					}
					// Start component creation
					instance.startCreation();
					// Start bussiness
					instance.start(PlayListIO.getAudioObjectsFromFileNamesList(songs));
				}
			});
		} catch (Exception e) {
			logger.internalError(e);
		}

		// Check for updates
		ApplicationUpdates.checkUpdates(instance.state.getProxy(), false);

	}

	/**
	 * Called when closing application, finished all necessary modules and
	 * writes configuration
	 */
	public void finish() {
		logger.info(LogCategories.END, StringUtils.getString("Closing ", Constants.APP_NAME, " ", Constants.APP_VERSION_NUMBER));

		try {
			VisualHandler.getInstance().hideWindows();
			SystemTrayHandler.getInstance().finish();
			PlayerHandler.getInstance().finish();
			PlayListHandler.getInstance().finish();
			RepositoryHandler.getInstance().finish();
			FavoritesHandler.getInstance().finish();
			RadioHandler.getInstance().finish();
			PodcastFeedHandler.getInstance().finish();
			ApplicationDataHandler.getInstance().storeState();
			HotkeyHandler.getInstance().finish();
			MultipleInstancesHandler.getInstance().finish();
		} catch (Exception exception) {
			logger.error(LogCategories.UNEXPEXTED_ERROR, exception);
		}

		logger.info(LogCategories.END, "Goodbye!!");
		System.exit(0);
	}

	/**
	 * Once all application is loaded, it's time to load data (repository,
	 * playlist, ...)
	 */
	private void start(List<AudioObject> playList) {
		ApplicationDataHandler.getInstance().applyState();
		try {
			RadioHandler.getInstance().readRadios();
			PodcastFeedHandler.getInstance().readPodcastFeeds();
			FavoritesHandler.getInstance().readFavorites();
			RepositoryHandler.getInstance().readRepository();
			if (playList.isEmpty())
				PlayListHandler.getInstance().getLastPlayList();
			else {
				PlayListCommonOps.addToPlayListAndPlay(playList);
				PlayListCommonOps.refreshPlayList();
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		VisualHandler.getInstance().setFullFrameVisible(true);
		//Hide title dialog
		VisualHandler.getInstance().hideSplashScreen();
		// Start device monitor
		DeviceConnectionMonitor.startMonitor();
		DeviceConnectionMonitor.addListener(DeviceHandler.getInstance());

		// Start podcast retriever
		PodcastFeedEntryRetriever.getInstance().start();

		logger.info(LogCategories.START, StringUtils.getString("Application started (", StringUtils.toString(Timer.stop(), 3), " seconds)"));
	}

	/** Starts controllers associated to visual classes */
	private void startControllers() {
		ControllerProxy.getInstance();
	}

	/** Creates all objects of aTunes: visual objects, controllers, and handlers */
	private void startCreation() {
		logger.debug(LogCategories.START, "Starting components");

		startVisualization();
		startControllers();
		VisualHandler.getInstance().setTitleBar("");
	}

	/** Starts visual objects */
	private void startVisualization() {
		VisualHandler.getInstance().startVisualization();
	}
}
