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

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.handlers.DeviceHandler;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class DeviceConnectionMonitor extends Thread {

	private static DeviceConnectionMonitor instance;

	private static List<DeviceConnectionListener> listeners = new ArrayList<DeviceConnectionListener>();

	private static int TIME_TO_WAIT = 5000;

	private Logger logger = new Logger();

	private DeviceConnectionMonitor() {
		super();
	}

	public static void addListener(DeviceConnectionListener listener) {
		listeners.add(listener);
	}

	public static synchronized void startMonitor() {
		if (instance == null) {
			instance = new DeviceConnectionMonitor();
			instance.start();
		}
	}

	public static synchronized void stopMonitor() {
		if (instance != null) {
			instance.interrupt();
			instance = null;
		}
	}

	@Override
	public void run() {
		super.run();

		while (!isInterrupted()) {
			final String deviceLocation = Kernel.getInstance().state.getDefaultDeviceLocation();
			if (deviceLocation != null && !deviceLocation.equals("")) {
				File deviceLocationFile = new File(deviceLocation);
				if (!DeviceHandler.getInstance().isDeviceConnected()) {
					if (deviceLocationFile.exists()) {
						logger.info(LogCategories.PROCESS, "Device connected");
						for (final DeviceConnectionListener l : listeners) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									l.deviceConnected(deviceLocation);
								}
							});
						}
						instance = null;
						return;
					}
				}
			}
			try {
				Thread.sleep(TIME_TO_WAIT);
			} catch (InterruptedException e) {
				logger.error(LogCategories.PROCESS, e);
			}
		}
	}

}
