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

/**
 * This class defines accepted arguments by application
 */
public class ApplicationArguments {

	/**
	 * Debug constant This argument makes a big log file
	 */
	public static final String DEBUG = "debug";

	/**
	 * Ignore look and feel constant This argument makes application use OS
	 * default Look And Feel
	 */
	public static final String IGNORE_LOOK_AND_FEEL = "ignore-theme";

	/**
	 * Disable multiple instances control
	 */
	public static final String ALLOW_MULTIPLE_INSTANCE = "multiple-instance";

}
