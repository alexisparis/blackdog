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

import java.io.Serializable;
import java.util.Map;

/**
 * @author fleax
 * 
 */
public abstract class Tag implements Serializable {

	/**
	 * Description of the Field
	 */
	public static final String[] genres = { "Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other",
			"Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop",
			"Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk",
			"Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream",
			"Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave",
			"Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk",
			"Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock",
			"Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Brass",
			"Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock",
			"Drum Solo", "A Capela", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat",
			"Christian Gangsta Rap", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "JPop",
			"SynthPop" };
	private String title;
	private String artist;
	private String album;
	private int year;
	private String comment;
	private String genre;
	private String lyrics;
	private String composer;
	private String albumArtist;
	private int trackNumber;

	/**
	 * Tries to find the string provided in the table and returns the
	 * corresponding int code if successful. Returns -1 if the genres is not
	 * found in the table.
	 * 
	 * @param str
	 *            the genre to search for
	 * @return the integer code for the genre or -1 if the genre is not found
	 */
	public static int getGenre(String str) {
		int retval = -1;

		for (int i = 0; (i < genres.length); i++) {
			if (genres[i].equalsIgnoreCase(str)) {
				retval = i;
				break;
			}
		}

		return retval;
	}

	public static final String getGenreForCode(int code) {
		return code >= 0 ? genres[code] : "";
	}

	public String getAlbum() {
		return album;
	}

	public String getAlbumArtist() {
		return albumArtist;
	}

	public String getArtist() {
		return artist;
	}

	public String getComment() {
		return comment;
	}

	public String getComposer() {
		return composer;
	}

	public String getGenre() {
		return genre;
	}

	public String getLyrics() {
		return lyrics;
	}

	public abstract Tag getTagFromProperties(Map<String, ?> properties);

	public String getTitle() {
		return title;
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public int getYear() {
		return year;
	}

	public void setAlbum(String album) {
		this.album = album != null ? album.trim() : "";
	}

	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}

	public void setArtist(String artist) {
		this.artist = artist != null ? artist.trim() : "";
	}

	public void setComment(String comment) {
		this.comment = comment != null ? comment.trim() : "";
	}

	public void setComposer(String composer) {
		this.composer = composer;
	}

	public void setGenre(int genre) {
		this.genre = getGenreForCode(genre);
	}

	public void setGenre(String genre) {
		this.genre = genre != null ? genre.trim() : "";
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}

	public void setTitle(String title) {
		this.title = title != null ? title.trim() : "";
	}

	public void setTrackNumber(int tracknumber) {
		this.trackNumber = tracknumber;
	}

	public void setYear(int year) {
		this.year = year;
	}

}
