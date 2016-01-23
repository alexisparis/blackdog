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

package net.sourceforge.atunes;

import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

/**
 * Catches exceptions not catched on application
 */
public class UncaughtExceptionHandler {

	/**
	 * logger
	 */
	static Logger logger = new Logger();

	/**
	 * Redirects uncaught exceptions to logger
	 * 
	 */
	static void uncaughtExceptions() {
		try {
			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					if (e.getMessage().equals("net.sourceforge.atunes.gui.views.controls.JTrayIcon cannot be cast to java.awt.Component")) {
						logger.debug(LogCategories.TRAY, "CCE from JTrayIcon ignored");
						return;
					}
					logger.error(LogCategories.UNEXPEXTED_ERROR, StringUtils.getString("Thread: ", t.getName()));
					logger.error(LogCategories.UNEXPEXTED_ERROR, e);
				}
			});
		} catch (Throwable t) {
			logger.error(LogCategories.UNEXPEXTED_ERROR, t);
		}
	}

}
