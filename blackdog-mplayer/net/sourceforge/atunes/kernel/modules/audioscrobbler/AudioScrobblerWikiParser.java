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

package net.sourceforge.atunes.kernel.modules.audioscrobbler;

import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.Translate;

public class AudioScrobblerWikiParser {

	private static Logger logger = new Logger();

	/**
	 * Extracts from an html page of Last.fm wiki information about artist
	 * 
	 * That's is a sample html code to parse
	 * 
	 * <div id="wikiAbstract"> The Rolling Stones es un conjunto de rock
	 * británico formado en 1962. Marcaron el renacimiento del rock and roll
	 * tras el período de Elvis Presley y ocuparon un lugar importante en el
	 * redescubrimiento del blues negro.<br />
	 * Sus integrantes más destacados son el guitarrista Keith Richards y Mick
	 * Jagger, cantante y líder del conjunto.<br />
	 * <br />
	 * Biografía<br />
	 * Jagger y Richards se conocieron un dia en el tren de Dartford formando
	 * una gran amistad (Richards se fijo en los discos de Little Richard,Chuck
	 * Berry y Carl Perkins <span class="wiki_continued" id="wikiSecondPart">que
	 * Jagger traia bajo el brazo), luego conocen el 7 de abril de 1962 a Brian
	 * Jones durante un concierto de Blues Incorporated, y juntos encontraron
	 * como punto en común su afición por el blues negro estadounidense y el
	 * Rock And Roll (el interpretado por músicos tales como Chuck Berry, Muddy
	 * Waters, B.B. King, John Lee Hooker y Bo Diddley) Deciden formar una banda
	 * de rhythm and blues. El resto de la banda en sus inicios lo formaron Dick
	 * Taylor (reemplazado luego de un corto plazo por Bill Wyman )(bajo),
	 * Charlie Watts (batería) y el pianista Ian Stewart.<br />
	 * La agrupación comenzó tocando blues y luego rock and roll y se dieron a
	 * conocer al popularizar el tema compuesto por The Beatles I wanna be your
	 * man. Como mánager tenían a Andrew Loog Oldham, que, a diferencia del
	 * manager de los Beatles, Brian Epstein, les alentó a que la imagen del
	 * grupo en escena sea el de «chicos malos», con una actitud más
	 * agresiva. La prensa musical pronto convirtió en rivales a The Beatles y
	 * The Rolling Stones, aunque la actitud de ambas bandas no era ésa, sino
	 * que más bien eran amigos y colaboradores.</span> <a id="wikiReadMore"
	 * href="/music/The+Rolling+Stones/+wiki">(leer más)</a> <script
	 * type="text/javascript"> Element.observe("wikiReadMore", "click", function
	 * (event) { if (Element.hasClassName("wikiSecondPart", "wiki_continued")) {
	 * Event.stop(event); Element.removeClassName("wikiSecondPart",
	 * "wiki_continued"); $("wikiReadMore").innerHTML = "(leer todo)"; } });
	 * </script> </div>
	 * 
	 * 
	 * 
	 * @param htmlCode
	 * @return
	 */
	public static String getWikiText(String htmlCode) {

		Parser parser = Parser.createParser(htmlCode, "ISO-8859-1");

		// Get <div id="wikiAbstract"> tag
		NodeList nodeList = null;
		try {
			nodeList = parser.extractAllNodesThatMatch(new NodeFilter() {
				private static final long serialVersionUID = -5578357763396554009L;

				@Override
				public boolean accept(Node node) {
					return node.getText().contains("wikiAbstract");
				}
			});
		} catch (ParserException e) {
			logger.error(LogCategories.INTERNAL_ERROR, e);
		}

		if (nodeList == null) {
			return "";
		}

		// Remove "<a id="wikiReadMore">" tag and javascript code
		nodeList.keepAllNodesThatMatch(new NodeFilter() {
			private static final long serialVersionUID = -2594307721622283632L;

			@Override
			public boolean accept(Node node) {
				return !(node instanceof ScriptTag) && !node.getText().contains("wikiReadMore");
			}
		}, true);

		// return string translated
		return Translate.decode(nodeList.elementAt(0).toPlainTextString().trim());
	}
}
