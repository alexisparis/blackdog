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

import java.io.FileNotFoundException;

import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.Translate;

public class LyricWikiEngine extends LyricsEngine {

	/**
	 * Logger
	 */
	private static Logger logger = new Logger();

	/**
	 * Charset to use
	 */
	private static final String CHARSET = "UTF-8";

	/**
	 * Artists and songs with spaces are replaced with an underscore "_"
	 * 
	 * @param str
	 * @return
	 */
	private static String getStringEncoded(String str) {
		return str.replaceAll(" ", "_");
	}

	@Override
	public String getLyricsFor(String artist, String title) {
		try {
			// Build url of type http://lyricwiki.org/Iron_Maiden:The_Number_Of_The_Beast
			StringBuilder builder = new StringBuilder();
			builder.append("http://lyricwiki.org/");
			builder.append(encodeString(getStringEncoded(artist)));
			builder.append(":");
			builder.append(encodeString(getStringEncoded(title)));
			String url = builder.toString();

			// Retrieve html
			String html = readURL(getConnection(url), CHARSET);

			Parser parser = Parser.createParser(html, CHARSET);

			// Get <div id="lyric"> tag
			NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter() {
				private static final long serialVersionUID = 0L;

				@Override
				public boolean accept(Node node) {
					return node.getText().contains("div id=\"lyric\"");
				}
			});

			// Remove <div id="lyric"> </div> and <br/>
			String lyric = nodeList.toHtml().replaceAll("<br/>", "\n").replaceFirst("<div id=\"lyric\">", "").replaceFirst("</div>", "");

			return Translate.decode(lyric);
		} catch (FileNotFoundException e) {
			logger.info(LogCategories.SERVICE, "No lyrics found at http://lyricwiki.org/ for " + artist + " - " + title);
			return null;
		} catch (Exception e) {
			logger.error(LogCategories.SERVICE, e);
			return null;
		}
	}
}
