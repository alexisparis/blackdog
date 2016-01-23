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

package net.sourceforge.atunes.kernel.modules.regexp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Util;

public class RegexpUtils {

	private static Logger logger = new Logger();

	private static Pattern numberSeparatorPattern;
	private static Perl5Matcher matcher = new Perl5Matcher();

	static {
		Perl5Compiler c = new Perl5Compiler();
		try {
			numberSeparatorPattern = c.compile("[^0-9]+");
		} catch (MalformedPatternException e) {
			logger.internalError(e);
		}
	}

	/**
	 * Given an array of files, returns a hash containing each file and its
	 * track number based on information found on file name
	 */
	public static Map<AudioFile, Integer> getFilesAndTrackNumbers(List<AudioFile> files) {
		Map<AudioFile, Integer> filesToSet = new HashMap<AudioFile, Integer>();

		for (int j = 0; j < files.size(); j++) {
			// Try to get a number from file name
			String fileName = files.get(j).getNameWithoutExtension();

			List<String> aux = new ArrayList<String>();
			Util.split(aux, matcher, numberSeparatorPattern, fileName);

			int trackNumber = 0;
			int i = 0;
			while (trackNumber == 0 && i < aux.size()) {
				String token = aux.get(i);
				try {
					trackNumber = Integer.parseInt(token);
				} catch (NumberFormatException e) {
					// Ok, it's not a valid number, skip it
				}
				i++;
			}

			if (trackNumber != 0)
				filesToSet.put(files.get(j), trackNumber);
		}

		return filesToSet;
	}
}
