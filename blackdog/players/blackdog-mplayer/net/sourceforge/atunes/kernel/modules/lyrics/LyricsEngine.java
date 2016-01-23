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

package net.sourceforge.atunes.kernel.modules.lyrics;

import java.io.IOException;
import java.net.URLConnection;
import java.net.UnknownHostException;

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.utils.NetworkUtils;

public abstract class LyricsEngine {

	protected final static String encodeString(String str) {
		return NetworkUtils.encodeString(str);
	}

	protected final static URLConnection getConnection(String url) throws UnknownHostException, IOException {
		return NetworkUtils.getConnection(url, NetworkUtils.getProxy(Kernel.getInstance().state.getProxy()));
	}

	protected final static String readURL(URLConnection connection, String charset) throws IOException {
		return NetworkUtils.readURL(connection, charset);
	}

	public abstract String getLyricsFor(String artist, String title);
}
