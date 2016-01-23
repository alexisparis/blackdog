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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.RepaintManager;

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.handlers.MultipleInstancesHandler;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;
import net.sourceforge.atunes.utils.log4j.Log4jPropertiesLoader;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

import com.fleax.ant.BuildNumberReader;

/**
 * Main class to launch aTunes
 */
public class Main {

	/**
	 * Logger
	 */
	static Logger logger = new Logger();

	/**
	 * Log some properties at start
	 * 
	 * @param arguments
	 */
	private static void logProgramProperties(List<String> arguments) {
		// First line: version number
		String firstLine = StringUtils.getString("Starting ", Constants.APP_NAME, " ", Constants.APP_VERSION_NUMBER, " (Build ", BuildNumberReader.getBuildNumber(), " [",
				new SimpleDateFormat("dd/MM/yyyy").format(BuildNumberReader.getBuildDate()), "])");
		logger.info(LogCategories.START, firstLine);

		// Second line: Java Virtual Machine Version
		logger.info(LogCategories.START, StringUtils.getString("Running in Java Virtual Machine ", System.getProperty("java.vm.version")));

		// Third line: Application Arguments
		logger.info(LogCategories.START, StringUtils.getString("Arguments = ", arguments));

		// Fourth line: DEBUG mode
		logger.info(LogCategories.START, StringUtils.getString("Debug mode = ", Kernel.DEBUG));

		// Fifth line: Execution path
		logger.info(LogCategories.START, StringUtils.getString("Execution path = ", new File("").getAbsolutePath()));
	}

	/**
	 * Main method for calling aTunes
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Fetch arguments into a list
		List<String> arguments = StringUtils.fromStringArrayToList(args);

		// First, look up for other instances running
		if (!arguments.contains(ApplicationArguments.ALLOW_MULTIPLE_INSTANCE) && !MultipleInstancesHandler.getInstance().isFirstInstance()) {
			// Is not first aTunes instance running, so send parameters and finalize
			MultipleInstancesHandler.getInstance().sendArgumentsToFirstInstance(arguments);
		} else {
			// NORMAL APPLICATION STARTUP

			// Start time measurement
			Timer.start();

			// WE ARE CLOSING ERROR STREAM!!!
			// THIS IS DONE TO AVOID ANNOYING MESSAGES FROM SOME LIBRARIES (I.E. ENTAGGED)
			System.err.close();

			// Set debug flag in kernel
			Kernel.DEBUG = arguments.contains(ApplicationArguments.DEBUG);

			// Set ignore look and feel flag in kernel
			Kernel.IGNORE_LOOK_AND_FEEL = arguments.contains(ApplicationArguments.IGNORE_LOOK_AND_FEEL);

			// Enable uncaught exception catching
			UncaughtExceptionHandler.uncaughtExceptions();

			// For detecting Swing threading violations
			if (Kernel.DEBUG) {
				RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
			}

			// Set log4j properties
			Log4jPropertiesLoader.loadProperties(Kernel.DEBUG);

			// Log program properties
			logProgramProperties(arguments);

			// Start the Kernel, which really starts application
			Kernel.startKernel(arguments);
		}
	}
}
