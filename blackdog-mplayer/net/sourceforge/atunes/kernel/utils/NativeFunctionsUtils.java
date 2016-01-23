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

// Following file is also in common jukebox project
import net.sourceforge.atunes.kernel.modules.jna.Kernel32; // Note for developers: aTunes uses a custom logger making use of log4j 
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

// JNA library. Provided in common jukebox
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.WString;

/*
 * Thanks to Paul Loy from the JNA mailing list ->
 * https://jna.dev.java.net/servlets/ReadMsg?list=users&msgNo=928
 * 
 * Requires: JNA https://jna.dev.java.net/#getting_started
 */
public class NativeFunctionsUtils {

	private static final Logger logger = new Logger();

	private static Kernel32 NATIVE;
	private static final int CHAR_BYTE_WIDTH = 2;

	static {
		try {
			Native.setProtected(true);
			NATIVE = (Kernel32) Native.loadLibrary("Kernel32", Kernel32.class);
		} catch (UnsatisfiedLinkError e) {
			logger.debug(LogCategories.NATIVE, "kernel32 not found");
		}
	}

	/**
	 * Returns the 8.3 (DOS) file-/pathname for a given file. Only avaible for
	 * Windows, so check if this operating system is used before calling. The
	 * filename must include the path as whole and be passed as String.
	 * 
	 * @param longPathName
	 * @return File/Path in 8.3 format
	 */
	public static String getShortPathNameW(String longPathName) {
		WString pathname = new WString(longPathName);
		int bufferSize = (pathname.length() * CHAR_BYTE_WIDTH) + CHAR_BYTE_WIDTH;
		Memory buffer = new Memory(bufferSize);

		if (NATIVE.GetShortPathNameW(pathname, buffer, bufferSize) == 0) {
			return "";
		}
		return buffer.getString(0, true);
	}

}
