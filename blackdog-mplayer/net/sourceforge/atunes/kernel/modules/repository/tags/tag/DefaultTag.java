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

package net.sourceforge.atunes.kernel.modules.repository.tags.tag;

import java.util.Map;

public class DefaultTag extends Tag {

	private static final long serialVersionUID = 6200185803652819029L;

	public DefaultTag() {
		// Nothing to do
	}

	/**
	 * Stores tag so application can read them. Regular method. Uses
	 * JAudiotagger.
	 * 
	 * @param tag
	 *            JAudiotagger type tag must be passed
	 */
	public DefaultTag(org.jaudiotagger.tag.Tag tag) {
		setAlbum(tag.getFirstAlbum());
		setArtist(tag.getFirstArtist());
		setComment(tag.getFirstComment());
		setGenre(tag.getFirstGenre());
		setTitle(tag.getFirstTitle());
		try {
			//  Certain tags are in the form of track number/total number of tracks so check for this:
			if (tag.getFirstTrack().contains("/")) {
				int separatorPosition;
				separatorPosition = tag.getFirstTrack().indexOf("/");
				setTrackNumber(Integer.parseInt(tag.getFirstTrack().substring(0, separatorPosition)));
			} else
				setTrackNumber(Integer.parseInt(tag.getFirstTrack()));
		} catch (NumberFormatException e) {
			setTrackNumber(-1);
		}
		try {
			setYear(Integer.parseInt(tag.getFirstYear()));
		} catch (NumberFormatException e) {
			setYear(-1);
		}
		setLyrics(tag.getFirst(org.jaudiotagger.tag.TagFieldKey.LYRICS));
		setComposer(tag.getFirst(org.jaudiotagger.tag.TagFieldKey.COMPOSER));
		setAlbumArtist(tag.getFirst(org.jaudiotagger.tag.TagFieldKey.ALBUM_ARTIST));
	}

	@Override
	public Tag getTagFromProperties(Map<String, ?> properties) {
		DefaultTag defaultTag = new DefaultTag();
		defaultTag.setTitle((String) properties.get("TITLE"));
		defaultTag.setArtist((String) properties.get("ARTIST"));
		defaultTag.setAlbum((String) properties.get("ALBUM"));
		try {
			defaultTag.setYear(Integer.parseInt((String) properties.get("YEAR")));
		} catch (NumberFormatException ex) {
			defaultTag.setYear(-1);
		}
		defaultTag.setComment((String) properties.get("COMMENT"));
		try {
			defaultTag.setTrackNumber(Integer.parseInt((String) properties.get("TRACK")));
		} catch (NumberFormatException ex) {
			defaultTag.setTrackNumber(-1);
		}
		String genreString = (String) properties.get("GENRE");
		if (genreString == null)
			defaultTag.setGenre("");
		else
			defaultTag.setGenre(genreString);
		defaultTag.setLyrics((String) properties.get("LYRICS"));
		defaultTag.setComposer((String) properties.get("COMPOSER"));
		defaultTag.setAlbumArtist((String) properties.get("ALBUM_ARTIST"));
		return defaultTag;
	}
}
