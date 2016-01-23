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

package net.sourceforge.atunes.utils;

/**
 * @author fleax
 * 
 */
public class TimeUtils {

	/**
	 * Return a string representation of a given amount of microseconds
	 * 
	 * @param micros
	 * @return
	 */
	public static String microseconds2String(long micros) {
		long aux = micros / 1000000;
		int minutes = (int) aux / 60;
		aux = aux % 60;
		return StringUtils.getString(minutes, ":", (aux < 10 ? "0" : ""), aux);
	}

	/**
	 * Return a string representation of a given amount of milliseconds
	 * 
	 * @param millis
	 * @return
	 */
	public static String milliseconds2String(long millis) {
		long aux = millis / 1000;
		int minutes = (int) aux / 60;
		aux = aux % 60;
		return StringUtils.getString(minutes, ":", (aux < 10 ? "0" : ""), aux);
	}

	/**
	 * Return a string representation of a given amount of seconds
	 * 
	 * @param s
	 * @return
	 */
	public static String seconds2String(long s) {
		long seconds = s;

		int minutes = (int) seconds / 60;
		seconds = seconds % 60;
		return StringUtils.getString(minutes, ":", (seconds < 10 ? "0" : ""), seconds);
	}
}
