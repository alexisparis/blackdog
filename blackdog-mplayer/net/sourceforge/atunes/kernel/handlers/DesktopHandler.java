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

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.sourceforge.atunes.kernel.modules.search.Search;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

/**
 * Handler for Desktop interaction
 */
public class DesktopHandler {

	private static Logger logger = new Logger();

	private static DesktopHandler instance = new DesktopHandler();

	private boolean isDesktopSupported;
	private Desktop desktop;

	private DesktopHandler() {
		isDesktopSupported = Desktop.isDesktopSupported();
		if (isDesktopSupported) {
			desktop = Desktop.getDesktop();
		}
	}

	public static DesktopHandler getInstance() {
		return instance;
	}

	/**
	 * Starts web browser
	 * 
	 * @param search
	 *            Search object
	 * @param query
	 *            query
	 */
	public void openSearch(Search search, String query) {
		if (search != null && isDesktopSupported)
			try {
				desktop.browse(search.getURL(query).toURI());
			} catch (MalformedURLException e) {
				logger.error(LogCategories.DESKTOP, e);
			} catch (IOException e) {
				logger.error(LogCategories.DESKTOP, e);
			} catch (URISyntaxException e) {
				logger.error(LogCategories.DESKTOP, e);
			}
	}

	/**
	 * Starts web browser with specified URI
	 * 
	 * @param uri
	 *            URI
	 */
	public void openURI(URI uri) {
		if (isDesktopSupported) {
			try {
				desktop.browse(uri);
			} catch (IOException e) {
				logger.error(LogCategories.DESKTOP, e);
			}
		}
	}

	/**
	 * Starts web browser with specified URL
	 * 
	 * @param url
	 *            URL
	 */
	public void openURL(String url) {
		if (isDesktopSupported) {
			try {
				openURI(new URL(url).toURI());
			} catch (MalformedURLException e) {
				logger.error(LogCategories.DESKTOP, e);
			} catch (URISyntaxException e) {
				logger.error(LogCategories.DESKTOP, e);
			}
		}
	}

	/**
	 * Starts web browser with specified URL
	 * 
	 * @param url
	 *            URL
	 */
	public void openURL(URL url) {
		if (isDesktopSupported) {
			try {
				openURI(url.toURI());
			} catch (URISyntaxException e) {
				logger.error(LogCategories.DESKTOP, e);
			}
		}

	}

}
