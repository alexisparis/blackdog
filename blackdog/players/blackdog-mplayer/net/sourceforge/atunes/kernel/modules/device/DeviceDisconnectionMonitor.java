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

package net.sourceforge.atunes.kernel.modules.device;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.handlers.DeviceHandler;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class DeviceDisconnectionMonitor extends Thread {

	private static DeviceDisconnectionMonitor instance;

	private static List<DeviceDisconnectionListener> listeners = new ArrayList<DeviceDisconnectionListener>();

	private static int TIME_TO_WAIT = 5000;

	private Logger logger = new Logger();

	private DeviceDisconnectionMonitor() {
		super();
	}

	public static void addListener(DeviceDisconnectionListener listener) {
		listeners.add(listener);
	}

	public static void startMonitor() {
		if (instance == null) {
			instance = new DeviceDisconnectionMonitor();
			instance.start();
		}
	}

	public static void stopMonitor() {
		if (instance != null) {
			instance.interrupt();
			instance = null;
		}
	}

	@Override
	public void run() {
		super.run();

		while (!isInterrupted()) {
			if (!DeviceHandler.getInstance().isDeviceConnected())
				return;

			final File deviceLocationFile = DeviceHandler.getInstance().getDeviceRepository().getFolders().get(0);
			if (!deviceLocationFile.exists()) {
				logger.info(LogCategories.PROCESS, "Device disconnected");
				for (final DeviceDisconnectionListener l : listeners) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							l.deviceDisconnected(deviceLocationFile.getAbsolutePath());
						}
					});
				}
				return;
			}
			try {
				Thread.sleep(TIME_TO_WAIT);
			} catch (InterruptedException e) {
				logger.error(LogCategories.PROCESS, e);
			}
		}
	}

}
