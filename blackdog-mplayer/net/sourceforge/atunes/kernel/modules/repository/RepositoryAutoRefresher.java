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

package net.sourceforge.atunes.kernel.modules.repository;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class RepositoryAutoRefresher extends Thread {

	private Logger logger = new Logger();

	private RepositoryHandler handler;

	public RepositoryAutoRefresher(RepositoryHandler repositoryHandler) {
		super();
		this.handler = repositoryHandler;
		setPriority(Thread.MIN_PRIORITY);
		if (Kernel.getInstance().state.getAutoRepositoryRefreshTime() != 0)
			start();
	}

	@Override
	public void run() {
		super.run();
		try {
			while (true) {
				if (!handler.repositoryIsNull()) {
					logger.info(LogCategories.PROCESS, StringUtils.getString("Checking for repository changes... (", new SimpleDateFormat("HH:mm:ss").format(new Date()), ')'));
					int filesLoaded = handler.getSongs().size();
					int newFilesCount = RepositoryLoader.countFilesInRepository(handler.getRepository());
					if (filesLoaded != newFilesCount)
						RepositoryHandler.getInstance().refreshRepository();
				}
				// If it has been disabled exit
				if (Kernel.getInstance().state.getAutoRepositoryRefreshTime() == 0)
					break;
				Thread.sleep(Kernel.getInstance().state.getAutoRepositoryRefreshTime() * 60000);
			}
		} catch (Exception e) {
			return;
		}
	}
}
