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

import javax.swing.ImageIcon;

import net.sourceforge.atunes.kernel.utils.XMLUtils;

import org.w3c.dom.Element;

public class AudioScrobblerArtist {

	private String name;
	private String match;
	private String url;
	private String imageUrl;

	// Used by renderers
	private ImageIcon image;

	protected static AudioScrobblerArtist getArtist(Element el) {
		AudioScrobblerArtist artist = new AudioScrobblerArtist();
		artist.name = XMLUtils.getChildElementContent(el, "name");
		artist.match = XMLUtils.getChildElementContent(el, "match");
		artist.url = XMLUtils.getChildElementContent(el, "url");
		artist.imageUrl = XMLUtils.getChildElementContent(el, "image_small");
		return artist;
	}

	public ImageIcon getImage() {
		return image;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getMatch() {
		return match;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

	/**
	 * @param imageUrl
	 *            the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * @param match
	 *            the match to set
	 */
	public void setMatch(String match) {
		this.match = match;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
