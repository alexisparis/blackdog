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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AudioScrobblerSimilarArtists {

	public static final int MAX_SIMILAR_ARTISTS = 15;

	private String artistName;
	private String picture;
	private List<AudioScrobblerArtist> artists;

	protected static AudioScrobblerSimilarArtists getSimilarArtists(Document xml) {
		Element el = (Element) xml.getElementsByTagName("similarartists").item(0);
		AudioScrobblerSimilarArtists similar = new AudioScrobblerSimilarArtists();
		similar.artistName = el.getAttribute("artist");
		similar.picture = el.getAttribute("picture");

		similar.artists = new ArrayList<AudioScrobblerArtist>();
		NodeList artists = el.getElementsByTagName("artist");
		for (int i = 0; i < artists.getLength(); i++) {
			if (i == MAX_SIMILAR_ARTISTS)
				break;
			Element e = (Element) artists.item(i);
			similar.artists.add(AudioScrobblerArtist.getArtist(e));
		}

		return similar;
	}

	public String getArtistName() {
		return artistName;
	}

	public List<AudioScrobblerArtist> getArtists() {
		return artists;
	}

	public String getPicture() {
		return picture;
	}

	/**
	 * @param artistName
	 *            the artistName to set
	 */
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	/**
	 * @param artists
	 *            the artists to set
	 */
	public void setArtists(List<AudioScrobblerArtist> artists) {
		this.artists = artists;
	}

	/**
	 * @param picture
	 *            the picture to set
	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}

}
