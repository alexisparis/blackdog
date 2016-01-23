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

package net.sourceforge.atunes.kernel.utils;

import java.io.File;

import net.sourceforge.atunes.utils.StringUtils;

public class SystemProperties {

	/**
	 * Operating Systems Enum
	 */
	public enum OperatingSystem {

		WINDOWS, LINUX, MACOSX, SOLARIS;

		/**
		 * Returns <code>true</code> if Windows Vista is the current operating
		 * system
		 * 
		 * @return If Windows Vista is the current operating system
		 */
		public boolean isVista() {
			return (this.equals(OperatingSystem.WINDOWS) && System.getProperty("os.name").toLowerCase().contains("vista"));
		}

	}

	/**
	 * User home dir
	 */
	public static final String userHome = System.getProperty("user.home");

	/**
	 * OS dependent file separator
	 */
	public static final String fileSeparator = System.getProperty("file.separator");

	/**
	 * OS dependent line terminator
	 */
	public static final String lineTerminator = getSystemLineTerminator();

	/**
	 * Operating System
	 */
	public static final OperatingSystem SYSTEM = detectOperatingSystem();

	/**
	 * Detect OS
	 * 
	 * @return The detected OS
	 */
	private static OperatingSystem detectOperatingSystem() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("windows")) {
			return OperatingSystem.WINDOWS;
		} else if (osName.contains("mac os x")) {
			return OperatingSystem.MACOSX;
		} else if (osName.contains("sunos")) {
			return OperatingSystem.SOLARIS;
		}
		return OperatingSystem.LINUX;
	}

	private static String getSystemLineTerminator() {
		if (SYSTEM == OperatingSystem.WINDOWS)
			return "\r\n";
		return "\n";
	}

	/**
	 * Gets folder where state is stored. If not exists, it's created
	 * 
	 * @param useWorkDir
	 *            If the current working directory should be used
	 * @return The folder where the state is stored
	 */
	public static String getUserConfigFolder(boolean useWorkDir) {
		if (useWorkDir)
			return ".";
		String userHomePath = SystemProperties.userHome;
		if (userHomePath != null) {
			File userConfigFolder = new File(StringUtils.getString(userHomePath, "/.aTunes"));
			if (!userConfigFolder.exists()) {
				if (!userConfigFolder.mkdir())
					return ".";
			}
			return userConfigFolder.getAbsolutePath();
		}
		return ".";
	}

}
