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
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;

public interface AudioScrobblerListener {

	public List<AudioScrobblerAlbum> getAlbums();

	public void notifyAlbumRetrieved(AudioObject file, long id);

	public void notifyArtistImage(Image img, long id);

	public void notifyCoverRetrieved(AudioScrobblerAlbum album, Image cover, long id);

	public void notifyFinishGetSimilarArtist(AudioScrobblerArtist a, Image img, long id);

	public void notifyStartRetrievingArtistImages(long id);

	public void notifyStartRetrievingCovers(long id);

	public void notifyWikiInfoRetrieved(String wikiText, String wikiURL, long id);

	public void savePicture(Image img, AudioFile file, long id);

	public void setAlbum(AudioScrobblerAlbum album, long id);

	public void setAlbums(List<AudioScrobblerAlbum> album, long id);

	public void setImage(Image img, long id);

	public void setLastAlbumRetrieved(String album, long id);

	public void setLastArtistRetrieved(String artist, long id);
}
