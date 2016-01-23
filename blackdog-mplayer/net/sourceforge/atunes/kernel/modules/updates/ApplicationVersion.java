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

package net.sourceforge.atunes.kernel.modules.updates;

import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a version, i.e. "1.7.4" where 1 is major number, 7 is
 * minor number, and 4 is revision number.
 * 
 * Also contains a download URL for the version and a release date
 * 
 */
public class ApplicationVersion {

	/**
	 * Release date of version
	 */
	private String date;

	/**
	 * Major number
	 */
	private int majorNumber;

	/**
	 * Minor number
	 */
	private int minorNumber;

	/**
	 * Revision number
	 */
	private int revisionNumber;

	/**
	 * Url where downdload this version
	 */
	private String downloadURL;

	/**
	 * Gets release date
	 * 
	 * @return
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Gets download url
	 * 
	 * @return
	 */
	public String getDownloadURL() {
		return downloadURL;
	}

	/**
	 * Gets major number
	 * 
	 * @return
	 */
	public int getMajorNumber() {
		return majorNumber;
	}

	/**
	 * Gets minor number
	 * 
	 * @return
	 */
	public int getMinorNumber() {
		return minorNumber;
	}

	/**
	 * Gets revision number
	 * 
	 * @return
	 */
	public int getRevisionNumber() {
		return revisionNumber;
	}

	/**
	 * Returns full version in string format
	 * 
	 * @return
	 */
	public String getVersionNumber() {
		return StringUtils.getString(majorNumber, ".", minorNumber, ".", revisionNumber);
	}

	/**
	 * Sets release date
	 * 
	 * @param date
	 */
	protected void setDate(String date) {
		this.date = date;
	}

	/**
	 * Sets download url
	 * 
	 * @param downloadURL
	 */
	protected void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}

	/**
	 * Sets major number
	 * 
	 * @param majorNumber
	 */
	protected void setMajorNumber(int majorNumber) {
		this.majorNumber = majorNumber;
	}

	/**
	 * Sets minor number
	 * 
	 * @param minorNumber
	 */
	protected void setMinorNumber(int minorNumber) {
		this.minorNumber = minorNumber;
	}

	/**
	 * Sets revision number
	 * 
	 * @param revisionNumber
	 */
	protected void setRevisionNumber(int revisionNumber) {
		this.revisionNumber = revisionNumber;
	}

}
