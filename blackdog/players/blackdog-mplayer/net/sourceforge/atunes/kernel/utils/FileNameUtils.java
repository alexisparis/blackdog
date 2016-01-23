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

import net.sourceforge.atunes.kernel.utils.SystemProperties.OperatingSystem;

/**
 * <p>
 * Utility methods for replacing illegal characters in filenames
 * </p>
 * 
 * <p>
 * The getValidFileName method is designed to handle filenames only, so do not
 * pass the whole path. For folders, use the getValidFolderName function, but do
 * not pass the filename with the folder name. Folder name can include full
 * path, except as said the filename. We are as permissive as possible, which
 * means this can cause interoperability problems (songs ripped on a Unix-like
 * system then played on a Windows OS for example). Further, we do not check
 * what file system is used, which can cause problems when writing to a
 * FAT/FAT32 partition for example. We do not yet check the filename length (255
 * characters max for all OS).
 * </p>
 * 
 * <p>
 * The characters in the list are probably incomplete and users might want other
 * substitutions so please change and complete accordingly.
 * </p>
 */
public class FileNameUtils {

	/**
	 * Checks for valid filenames. Only pass the filename without path! The
	 * filename is not checked for maximum filename of 255 characters (including
	 * path!). To check for valid folder names please use
	 * getValidFolderName(String folderName)
	 * 
	 * @param fileName
	 *            The filename to be checked. Please make sure to check for
	 *            escape sequences (\, $) and add a \ before them before calling
	 *            this method.
	 * @return Returns the filename with known illegal characters substituted.
	 */
	public static String getValidFileName(String fileName) {
		return getValidFileName(fileName, false);
	}

	/**
	 * Checks for valid filenames. Only pass the filename without path! The
	 * filename is not checked for maximum filename of 255 characters (including
	 * path!). To check for valid folder names please use
	 * getValidFolderName(String folderName)
	 * 
	 * @param fileName
	 *            The filename to be checked. Please make sure to check for
	 *            escape sequences (\, $) and add a \ before them before calling
	 *            this method.
	 * @param isMp3Device
	 *            if valid file name for Mp 3 device (->FAT/FAT32)
	 * @return Returns the filename with known illegal characters substituted.
	 */
	public static String getValidFileName(String fileName, boolean isMp3Device) {
		// First call generic function
		fileName = getValidName(fileName, isMp3Device);

		// Most OS do not like a slash, so replace it by default.
		fileName = fileName.replaceAll("/", "-");

		// This list is probably incomplete. Windows is quite picky.
		if (SystemProperties.SYSTEM == OperatingSystem.WINDOWS || isMp3Device) {
			fileName = fileName.replace("\\", "-");
		}
		return fileName;
	}

	/**
	 * Checks for valid folder names. Do pass the path WITHOUT the filename. The
	 * folder name is not checked for maximum filename of 255 characters
	 * (including filename!). To check for valid filenames please use
	 * getValidFileName(String fileName)
	 * 
	 * @param folderName
	 *            The folder name to be checked.
	 * @return Returns the path name with known illegal characters substituted.
	 */
	public static String getValidFolderName(String folderName) {
		return getValidFolderName(folderName, false);
	}

	/**
	 * Checks for valid folder names. Do pass the path WITHOUT the filename. The
	 * folder name is not checked for maximum filename of 255 characters
	 * (including filename!). To check for valid filenames please use
	 * getValidFileName(String fileName)
	 * 
	 * @param folderName
	 *            The folder name to be checked.
	 * @param isMp3Device
	 *            if valid folder name for Mp 3 device (->FAT/FAT32)
	 * @return Returns the path name with known illegal characters substituted.
	 */
	public static String getValidFolderName(String folderName, boolean isMp3Device) {
		// First call generic function
		folderName = getValidName(folderName, isMp3Device);

		// This list is probably incomplete. Windows is quite picky.
		if (SystemProperties.SYSTEM == OperatingSystem.WINDOWS || isMp3Device) {
			folderName = folderName.replace("\\.", "\\_");
			if (SystemProperties.SYSTEM == OperatingSystem.WINDOWS)
				folderName = folderName + "\\";
			folderName = folderName.replace(".\\", "_\\");
			if (SystemProperties.SYSTEM == OperatingSystem.WINDOWS) {
				folderName = folderName.replace("/", "-");
			}
		}

		return folderName;
	}

	/*
	 * Generic method that does the substitution for folder- and filenames. Do
	 * not call directly but call either getValidFileName or getValidFolderName
	 * to verify file/folder names
	 */
	private static String getValidName(String fileName, boolean isMp3Device) {
		/*
		 * This list is probably incomplete. Windows is quite picky. We do not
		 * check for (255?) character limit.
		 */
		if (SystemProperties.SYSTEM == OperatingSystem.WINDOWS || isMp3Device) {
			fileName = fileName.replace("\"", "'");
			fileName = fileName.replace("?", "_");
			// Replace all ":" except at the drive letter
			fileName = fileName.substring(0, 2) + fileName.substring(2).replace(":", "-");
			fileName = fileName.replace("<", "-");
			fileName = fileName.replace(">", "-");
			fileName = fileName.replace("|", "-");
			fileName = fileName.replace("*", "-");
		}

		// Unconfirmed, as no Mac avaible for testing.
		if (SystemProperties.SYSTEM == OperatingSystem.MACOSX)
			fileName = fileName.replace("|", "-");
		return fileName;
	}

}
