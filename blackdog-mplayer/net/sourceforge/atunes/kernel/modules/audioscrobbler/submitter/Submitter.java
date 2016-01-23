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

package net.sourceforge.atunes.kernel.modules.audioscrobbler.submitter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.NetworkUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class Submitter {

	/**
	 * Max num of retries
	 */
	private static final int MAX_RETRIES = 3;

	static Logger logger = new Logger();

	private static String protocolVersion = "1.1";
	private static String clientId = "atu";
	private static String clientVer = "0.1";

	private static String user;
	private static String password;
	private static Proxy proxy;

	private static String md5Challenge;
	private static String submissionURL;

	//private static String interval;

	private static String getMd5Response() {
		return md5DigestPassword(StringUtils.getString(md5DigestPassword(password), md5Challenge));
	}

	private static String getQueryString(AudioFile file, long startedToPlay) {
		StringBuilder builder = new StringBuilder();
		builder.append("u=").append(NetworkUtils.encodeString(user));
		builder.append("&s=").append(NetworkUtils.encodeString(getMd5Response()));
		builder.append("&a[0]=").append(NetworkUtils.encodeString(file.getArtist()));
		builder.append("&t[0]=").append(NetworkUtils.encodeString(file.getTitle()));
		builder.append("&b[0]=").append((!file.getAlbum().equals(LanguageTool.getString("UNKNOWN_ALBUM")) ? NetworkUtils.encodeString(file.getAlbum()) : ""));
		builder.append("&m[0]=");
		builder.append("&l[0]=").append(NetworkUtils.encodeString(Long.toString(file.getDuration())));

		Date date = new Date(startedToPlay * 1000);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
		builder.append("&i[0]=").append(NetworkUtils.encodeString(formatter.format(date)));
		return builder.toString();
	}

	private static void handshake() throws SubmitterException {
		String url = StringUtils.getString("http://post.audioscrobbler.com/?hs=true&p=", protocolVersion, "&c=", clientId, "&v=", clientVer, "&u=", user);

		URLConnection connection;
		try {
			connection = NetworkUtils.getConnection(url, proxy);
			String result = NetworkUtils.readURL(connection);
			// Parse result
			String[] lines = result.split("\n");
			if (lines[0].equals("UPTODATE")) {
				md5Challenge = lines[1];
				submissionURL = lines[2];
				//interval = lines[3];
			} else
				throw new SubmitterException(lines[0]);
		} catch (IOException e) {
			throw new SubmitterException(e.getMessage());
		}
	}

	/**
	 * Encodes a byte array into a hexidecimal String.
	 * 
	 * @param array
	 *            The byte array to encode.
	 * @return A heidecimal String representing the byte array.
	 */
	private static String hexEncode(byte[] array) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
		}

		return sb.toString();
	}

	/**
	 * Creates a MD5 digest String from a given password.
	 * 
	 * @param password1
	 *            The password to digest.
	 * @return The MD5 digested password.
	 */
	private static String md5DigestPassword(String password1) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			return hexEncode(md.digest(password1.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No MD5 algorithm present on the system");
		}
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public static void setPassword(String password) {
		Submitter.password = password;
	}

	/**
	 * @param proxy
	 *            the proxy to set
	 */
	public static void setProxy(Proxy proxy) {
		Submitter.proxy = proxy;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public static void setUser(String user) {
		Submitter.user = user;
	}

	public static void submitTrack(AudioFile file, long secondsPlayed) throws SubmitterException {
		if (!Kernel.getInstance().state.isLastFmEnabled()) {
			logger.debug(LogCategories.SERVICE, "Don't submit to Last.fm. Last.fm disabled");
			return;
		}

		if (user == null || user.equals("")) {
			logger.debug(LogCategories.SERVICE, "Don't submit to Last.fm. Empty user");
			return;
		}

		if (password == null || password.equals("")) {
			logger.debug(LogCategories.SERVICE, "Don't submit to Last.fm. Empty password");
			return;
		}

		// Get started to play
		long startedToPlay = System.currentTimeMillis() / 1000 - secondsPlayed;

		// If artist unknown don't submit
		if (file.getArtist().equals(LanguageTool.getString("UNKNOWN_ARTIST"))) {
			logger.debug(LogCategories.SERVICE, "Don't submit to Last.fm. Unknown Artist");
			return;
		}

		// If title unknown don't submit
		if (file.getTitle().equals("")) {
			logger.debug(LogCategories.SERVICE, "Don't submit to Last.fm. Unknown Title");
			return;
		}

		// Do not submit tracks under 30 seconds
		if (file.getDuration() < 30) {
			logger.debug(LogCategories.SERVICE, "Don't submit to Last.fm. Lenght < 30");
			return;
		}

		logger.info(LogCategories.SERVICE, "Submitting song to Last.fm");
		submitTrackToLastFm(file, startedToPlay, 1);
	}

	private static void submitTrackToLastFm(AudioFile file, long startedToPlay, int retries) throws SubmitterException {
		if (submissionURL == null)
			handshake();

		HttpURLConnection connection;
		String queryString = getQueryString(file, startedToPlay);
		try {
			connection = NetworkUtils.getConnection(submissionURL, proxy);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", new Integer(queryString.length()).toString());
			connection.setRequestProperty("Connection", "close");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			String result = NetworkUtils.readPostURL(connection, queryString);

			String[] lines = result.split("\n");
			if (!lines[0].equals("OK")) {
				if (lines[0].equals("BADAUTH")) { // Retry up to MAX_RETRIES
					if (retries == MAX_RETRIES)
						throw new SubmitterException(lines[0]);
					submissionURL = null;
					// Wait one second before retry
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					submitTrackToLastFm(file, startedToPlay, retries + 1);
				}
			}
		} catch (IOException e) {
			throw new SubmitterException(e.getMessage());
		}
	}

}
