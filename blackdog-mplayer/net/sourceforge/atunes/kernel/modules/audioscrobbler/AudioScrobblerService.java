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

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.utils.NetworkUtils;
import net.sourceforge.atunes.kernel.utils.XMLUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AudioScrobblerService {

	private static Logger logger = new Logger();

	private static final String ARTIST_WILDCARD = "(%ARTIST%)";
	private static final String ALBUM_WILDCARD = "(%ALBUM%)";
	private static final String LANGUAGE_PARAM = "?setlang=";
	private static final String LANGUAGE_WILDCARD = "(%LANGUAGE%)";

	private static final String albumInfoURL = StringUtils.getString("http://ws.audioscrobbler.com/1.0/album/", ARTIST_WILDCARD, "/", ALBUM_WILDCARD, "/info.xml");
	private static final String albumListURL = StringUtils.getString("http://ws.audioscrobbler.com/1.0/artist/", ARTIST_WILDCARD, "/topalbums.xml");
	private static final String similarArtistsURL = StringUtils.getString("http://ws.audioscrobbler.com/1.0/artist/", ARTIST_WILDCARD, "/similar.xml");
	private static final String artistTagURL = StringUtils.getString("http://ws.audioscrobbler.com/1.0/artist/", ARTIST_WILDCARD, "/toptags.xml");

	private static final String noCoverURL = "/depth/catalogue/noimage/cover_large.gif";

	private static final String artistWikiAbstractURL = StringUtils.getString("http://www.lastfm.com/music/", ARTIST_WILDCARD, LANGUAGE_PARAM, LANGUAGE_WILDCARD);
	private static final String artistWikiURL = StringUtils.getString("http://www.lastfm.com/music/", ARTIST_WILDCARD, "/+wiki", LANGUAGE_PARAM, LANGUAGE_WILDCARD);

	private static final boolean showAlbumsWithoutCover = false;

	private Proxy proxy;

	public AudioScrobblerService(Proxy proxy) {
		this.proxy = proxy;
	}

	public AudioScrobblerAlbum getAlbum(String artist, String album) {
		try {
			// Try to get from cache
			AudioScrobblerAlbum albumObject = AudioScrobblerCache.retrieveAlbumInfo(artist, album);
			if (albumObject == null) {
				// build url
				String urlString = albumInfoURL.replace(ARTIST_WILDCARD, NetworkUtils.encodeString(artist)).replace(ALBUM_WILDCARD, NetworkUtils.encodeString(album));
				// read xml
				Document xml = XMLUtils.getXMLDocument(NetworkUtils.readURL(NetworkUtils.getConnection(urlString, proxy)));
				albumObject = AudioScrobblerAlbum.getAlbum(xml);
				AudioScrobblerCache.storeAlbumInfo(artist, album, albumObject);
			}
			return albumObject;
		} catch (Exception e) {
			logger.debug(LogCategories.SERVICE, StringUtils.getString("No info found for artist ", artist, " album ", album));
		}
		return null;
	}

	public AudioScrobblerAlbumList getAlbumList(String artist) {
		try {
			// Try to get from cache
			AudioScrobblerAlbumList albumList = AudioScrobblerCache.retrieveArtistInfo(artist);
			if (albumList == null) {
				// build url
				String urlString = albumListURL.replace(ARTIST_WILDCARD, NetworkUtils.encodeString(artist));
				// read xml
				Document xml = XMLUtils.getXMLDocument(NetworkUtils.readURL(NetworkUtils.getConnection(urlString, proxy)));
				AudioScrobblerAlbumList albums = AudioScrobblerAlbum.getAlbumList(xml);
				if (showAlbumsWithoutCover)
					return albums;
				List<AudioScrobblerAlbum> result = new ArrayList<AudioScrobblerAlbum>();
				for (AudioScrobblerAlbum a : albums.getAlbums()) {
					if (!a.getBigCoverURL().endsWith(noCoverURL))
						result.add(a);
				}

				albumList = new AudioScrobblerAlbumList();
				albumList.setArtist(artist);
				albumList.setAlbums(result);
				AudioScrobblerCache.storeArtistInfo(artist, albumList);
			}
			return albumList;
		} catch (Exception e) {
			logger.debug(LogCategories.SERVICE, StringUtils.getString("No info found for artist ", artist));
		}
		return null;
	}

	public String getArtistTopTag(String artist) {
		try {
			// build url
			String urlString = artistTagURL.replace(ARTIST_WILDCARD, NetworkUtils.encodeString(artist));
			// read xml
			Document xml = XMLUtils.getXMLDocument(NetworkUtils.readURL(NetworkUtils.getConnection(urlString, proxy)));
			return getTopTag(xml);
		} catch (Exception e) {
			logger.debug(LogCategories.SERVICE, StringUtils.getString("No tag found for artist ", artist));
		}
		return null;
	}

	public Image getImage(AudioScrobblerAlbum album) {
		try {
			// Try to retrieve from cache
			Image img = AudioScrobblerCache.retrieveAlbumCover(album);
			if (img == null) {
				img = NetworkUtils.getImage(NetworkUtils.getConnection(album.getBigCoverURL(), proxy));
				AudioScrobblerCache.storeAlbumCover(album, img);
			}
			return img;
		} catch (Exception e) {
			logger.debug(LogCategories.SERVICE, StringUtils.getString("No image found for album ", album));
		}
		return null;
	}

	public Image getImage(AudioScrobblerArtist artist) {
		try {
			// Try to retrieve from cache
			Image img = AudioScrobblerCache.retrieveArtistThumbImage(artist);
			if (img == null) {
				img = NetworkUtils.getImage(NetworkUtils.getConnection(artist.getImageUrl(), proxy));
				AudioScrobblerCache.storeArtistThumbImage(artist, img);
			}
			return img;
		} catch (Exception e) {
			logger.debug(LogCategories.SERVICE, StringUtils.getString("No image found for artist ", artist));
		}
		return null;
	}

	public Image getImage(AudioScrobblerSimilarArtists similar) {
		try {
			// Try to retrieve from cache
			Image img = AudioScrobblerCache.retrieveArtistImage(similar);
			if (img == null) {
				img = NetworkUtils.getImage(NetworkUtils.getConnection(similar.getPicture(), proxy));
				AudioScrobblerCache.storeArtistImage(similar, img);
			}
			return img;
		} catch (Exception e) {
			logger.debug(LogCategories.SERVICE, StringUtils.getString("No image found for similar artist ", similar));
		}
		return null;
	}

	public AudioScrobblerSimilarArtists getSimilarArtists(String artist) {
		try {
			// Try to get from cache
			AudioScrobblerSimilarArtists similar = AudioScrobblerCache.retrieveArtistSimilar(artist);
			if (similar == null) {
				// build url
				String urlString = similarArtistsURL.replace(ARTIST_WILDCARD, NetworkUtils.encodeString(artist));
				// read xml
				Document xml = XMLUtils.getXMLDocument(NetworkUtils.readURL(NetworkUtils.getConnection(urlString, proxy)));
				similar = AudioScrobblerSimilarArtists.getSimilarArtists(xml);
				AudioScrobblerCache.storeArtistSimilar(artist, similar);
			}
			return similar;
		} catch (Exception e) {
			logger.debug(LogCategories.SERVICE, StringUtils.getString("No info found for similar artists to artist ", artist));
		}
		return null;
	}

	private String getTopTag(Document xml) {
		Element el = (Element) xml.getElementsByTagName("toptags").item(0);

		NodeList tags = el.getElementsByTagName("tag");
		if (tags.getLength() > 0) {
			Element e = (Element) tags.item(0);
			return XMLUtils.getChildElementContent(e, "name");
		}
		return null;
	}

	public String getWikiText(String artist) {
		try {
			// Try to get from cache
			String wikiText = AudioScrobblerCache.retrieveArtistWiki(artist);
			if (wikiText == null) {
				String urlString = artistWikiAbstractURL.replace(ARTIST_WILDCARD, NetworkUtils.encodeString(artist));
				urlString = urlString.replace(LANGUAGE_WILDCARD, Kernel.getInstance().state.getLocale().getLanguage());
				String htmlString = NetworkUtils.readURL(NetworkUtils.getConnection(urlString, proxy));
				wikiText = AudioScrobblerWikiParser.getWikiText(htmlString);
				AudioScrobblerCache.storeArtistWiki(artist, wikiText);
			}
			return wikiText;
		} catch (Exception e) {
			logger.debug(LogCategories.SERVICE, "No ");
		}
		return null;
	}

	public String getWikiURL(String artist) {
		return artistWikiURL.replace(ARTIST_WILDCARD, NetworkUtils.encodeString(artist)).replace(LANGUAGE_WILDCARD, Kernel.getInstance().state.getLocale().getLanguage());
	}
}
