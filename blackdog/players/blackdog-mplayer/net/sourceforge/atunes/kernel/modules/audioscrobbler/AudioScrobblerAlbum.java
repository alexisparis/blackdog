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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.kernel.utils.XMLUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AudioScrobblerAlbum {

	private static final SimpleDateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm", Locale.US);
	private String artist;
	private String title;
	private String url;
	private String releaseDateString;
	private String bigCoverURL;
	private String coverURL;
	private String smallCoverURL;

	private List<AudioScrobblerTrack> tracks;

	// Used by renderers
	private ImageIcon cover;

	protected static AudioScrobblerAlbum getAlbum(Document xml) {
		AudioScrobblerAlbum album = new AudioScrobblerAlbum();

		Element element = (Element) xml.getElementsByTagName("album").item(0);

		album.artist = element.getAttribute("artist");
		album.title = element.getAttribute("title");
		album.url = XMLUtils.getChildElementContent(element, "url");
		album.releaseDateString = XMLUtils.getChildElementContent(element, "releasedate").trim();
		album.bigCoverURL = XMLUtils.getChildElementContent(XMLUtils.getChildElement(element, "coverart"), "large");
		album.coverURL = XMLUtils.getChildElementContent(XMLUtils.getChildElement(element, "coverart"), "medium");
		album.smallCoverURL = XMLUtils.getChildElementContent(XMLUtils.getChildElement(element, "coverart"), "small");
		NodeList tracks = ((Element) element.getElementsByTagName("tracks").item(0)).getElementsByTagName("track");

		album.tracks = new ArrayList<AudioScrobblerTrack>();
		for (int i = 0; i < tracks.getLength(); i++)
			album.tracks.add(AudioScrobblerTrack.getTrack((Element) tracks.item(i)));

		return album;
	}

	protected static AudioScrobblerAlbumList getAlbumList(Document xml) {
		List<AudioScrobblerAlbum> albums = new ArrayList<AudioScrobblerAlbum>();

		Element element = (Element) xml.getElementsByTagName("topalbums").item(0);
		String artist = element.getAttribute("artist");
		NodeList list = element.getElementsByTagName("album");

		for (int i = 0; i < list.getLength(); i++) {
			Element alb = (Element) list.item(i);
			AudioScrobblerAlbum album = new AudioScrobblerAlbum();
			album.artist = artist;
			album.title = XMLUtils.getChildElementContent(alb, "name");
			album.url = XMLUtils.getChildElementContent(alb, "url");
			album.releaseDateString = XMLUtils.getChildElementContent(alb, "releasedate").trim();
			album.bigCoverURL = XMLUtils.getChildElementContent(XMLUtils.getChildElement(alb, "image"), "large");
			album.coverURL = XMLUtils.getChildElementContent(XMLUtils.getChildElement(alb, "image"), "medium");
			album.smallCoverURL = XMLUtils.getChildElementContent(XMLUtils.getChildElement(alb, "image"), "small");
			albums.add(album);
		}

		AudioScrobblerAlbumList albumList = new AudioScrobblerAlbumList();
		albumList.setArtist(artist);
		albumList.setAlbums(albums);
		return albumList;
	}

	public String getArtist() {
		return artist;
	}

	public String getArtistUrl() {
		return url.substring(0, url.lastIndexOf('/'));
	}

	/**
	 * @return the bigCoverURL
	 */
	public String getBigCoverURL() {
		return bigCoverURL;
	}

	/**
	 * @return the cover
	 */
	public ImageIcon getCover() {
		return cover;
	}

	public String getCoverURL() {
		return coverURL;
	}

	public Date getReleaseDate() {
		try {
			return df.parse(releaseDateString);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * @return the releaseDateString
	 */
	public String getReleaseDateString() {
		return releaseDateString;
	}

	public String getSmallCoverURL() {
		return smallCoverURL;
	}

	public String getTitle() {
		return title;
	}

	public List<AudioScrobblerTrack> getTracks() {
		return tracks;
	}

	public String getUrl() {
		return url;
	}

	public String getYear() {
		Date releaseDate = getReleaseDate();
		if (releaseDate == null)
			return "";
		Calendar c = Calendar.getInstance();
		c.setTime(releaseDate);
		return Integer.toString(c.get(Calendar.YEAR));
	}

	/**
	 * @param artist
	 *            the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @param bigCoverURL
	 *            the bigCoverURL to set
	 */
	public void setBigCoverURL(String bigCoverURL) {
		this.bigCoverURL = bigCoverURL;
	}

	/**
	 * @param cover
	 *            the cover to set
	 */
	public void setCover(ImageIcon cover) {
		this.cover = cover;
	}

	/**
	 * @param coverURL
	 *            the coverURL to set
	 */
	public void setCoverURL(String coverURL) {
		this.coverURL = coverURL;
	}

	/**
	 * @param releaseDateString
	 *            the releaseDateString to set
	 */
	public void setReleaseDateString(String releaseDateString) {
		this.releaseDateString = releaseDateString;
	}

	/**
	 * @param smallCoverURL
	 *            the smallCoverURL to set
	 */
	public void setSmallCoverURL(String smallCoverURL) {
		this.smallCoverURL = smallCoverURL;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param tracks
	 *            the tracks to set
	 */
	public void setTracks(List<AudioScrobblerTrack> tracks) {
		this.tracks = tracks;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return StringUtils.getString(artist, " - ", title);
	}
}
