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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.utils.RankList;

public class RepositoryStats implements Serializable {

	private static final long serialVersionUID = -3603928907730394504L;

	private int totalPlays;
	private int differentSongsPlayed;

	private RankList<AudioFile> songsRanking;
	private RankList<Album> albumsRanking;
	private RankList<Artist> artistsRanking;

	private Map<String, SongStats> songsStats;

	protected RepositoryStats() {
		songsRanking = new RankList<AudioFile>();
		albumsRanking = new RankList<Album>();
		artistsRanking = new RankList<Artist>();
		songsStats = new HashMap<String, SongStats>();
	}

	public RankList<Album> getAlbumsRanking() {
		return albumsRanking;
	}

	public RankList<Artist> getArtistsRanking() {
		return artistsRanking;
	}

	public int getDifferentSongsPlayed() {
		return differentSongsPlayed;
	}

	public RankList<AudioFile> getSongsRanking() {
		return songsRanking;
	}

	public Map<String, SongStats> getSongsStats() {
		return songsStats;
	}

	public SongStats getStatsForFile(AudioFile song) {
		if (song != null)
			return songsStats.get(song.getUrl());
		return null;
	}

	public int getTotalPlays() {
		return totalPlays;
	}

	public void setAlbumsRanking(RankList<Album> albumsRanking) {
		this.albumsRanking = albumsRanking;
	}

	public void setArtistsRanking(RankList<Artist> artistsRanking) {
		this.artistsRanking = artistsRanking;
	}

	public void setDifferentSongsPlayed(int differentSongsPlayed) {
		this.differentSongsPlayed = differentSongsPlayed;
	}

	public void setSongsRanking(RankList<AudioFile> songsRanking) {
		this.songsRanking = songsRanking;
	}

	public void setSongsStats(Map<String, SongStats> songsStats) {
		this.songsStats = songsStats;
	}

	public void setTotalPlays(int totalPlays) {
		this.totalPlays = totalPlays;
	}

}
