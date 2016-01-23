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

package net.sourceforge.atunes.kernel.modules.repository.tags.writer;

import java.util.List;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.handlers.ApplicationDataHandler;
import net.sourceforge.atunes.kernel.handlers.MultiplePlaylistHandler;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

/**
 * Class for writting tags to an audio file. We only use JAudiotagger for it. In
 * general, for writting a complete tag, call setInfo.
 * 
 * @author sylvain
 * 
 */
public class TagModifier {

	private static Logger logger = new Logger();

	public static void deleteTags(AudioFile file) {

		file.setWritable();
		try {
			org.jaudiotagger.audio.AudioFileIO.delete(org.jaudiotagger.audio.AudioFileIO.read(file));
		} catch (Exception e) {
			logger.error(LogCategories.FILE_WRITE, e);
		}

	}

	public static void refreshAfterTagModify(final List<AudioFile> audioFilesEditing) {
		boolean playListContainsRefreshedFile = false;
		for (int i = 0; i < audioFilesEditing.size(); i++) {
			RepositoryHandler.getInstance().refreshFile(audioFilesEditing.get(i));

			if (PlayerHandler.getInstance().getCurrentPlayList().contains(audioFilesEditing.get(i))) {
				playListContainsRefreshedFile = true;
				for (int j = 0; j < PlayerHandler.getInstance().getCurrentPlayList().size(); j++) {
					if (PlayerHandler.getInstance().getCurrentPlayList().get(j).equals(audioFilesEditing.get(i)))
						ControllerProxy.getInstance().getPlayListController().updatePositionInTable(j);
				}
			}

			if (PlayerHandler.getInstance().getCurrentPlayList().getCurrentAudioObject() != null
					&& PlayerHandler.getInstance().getCurrentPlayList().getCurrentAudioObject().equals(audioFilesEditing.get(i))) {
				PlayListHandler.getInstance().selectedAudioObjectChanged(audioFilesEditing.get(i));

				if (PlayerHandler.getInstance().isPlaying())
					VisualHandler.getInstance().updateTitleBar(audioFilesEditing.get(i));
			}
		}
		ControllerProxy.getInstance().getNavigationController().notifyReload();

		// Check if modified songs are present on other playlists
		if (!playListContainsRefreshedFile)
			playListContainsRefreshedFile = MultiplePlaylistHandler.getInstance().checkIfPlayListsContainsAudioObjects(audioFilesEditing);

		if (playListContainsRefreshedFile)
			ApplicationDataHandler.getInstance().persistPlayList();
	}

	/**
	 * Writes album to tag
	 * 
	 * @param file
	 *            File to which the tag should be written
	 * @param album
	 *            Album of file
	 */
	public static void setAlbum(AudioFile file, String album) {
		file.setWritable();
		try {
			org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file);
			org.jaudiotagger.tag.Tag newTag = audioFile.getTag();
			newTag.setAlbum(album);
			audioFile.commit();
		} catch (Exception e) {
			logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
		}
	}

	/**
	 * Writes genre to tag
	 * 
	 * @param file
	 *            File to which the tag should be written
	 * @param genre
	 *            Genre of file
	 */
	public static void setGenre(AudioFile file, String genre) {
		file.setWritable();
		try {
			org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file);
			org.jaudiotagger.tag.Tag newTag = audioFile.getTag();
			newTag.setGenre(genre);
			audioFile.commit();
		} catch (Exception e) {
			logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
		}
	}

	/**
	 * Writes tag to audiofile using JAudiotagger. For mp3 we write id3v1 and
	 * id3v2 tags to file.
	 * 
	 * @param file
	 *            File to which the tags should be written
	 * @param tag
	 *            Tag to be written
	 */
	public static void setInfo(AudioFile file, Tag tag) {
		if (!AudioFile.isMp3File(file)) {
			String title = tag.getTitle() != null ? tag.getTitle() : "";
			String album = tag.getAlbum() != null ? tag.getAlbum() : "";
			String artist = tag.getArtist() != null ? tag.getArtist() : "";
			String year = Integer.toString(tag.getYear());
			String comment = tag.getComment() != null ? tag.getComment() : "";
			String genreString = tag.getGenre();
			String lyrics = tag.getLyrics() != null ? tag.getLyrics() : "";
			String composer = tag.getComposer() != null ? tag.getComposer() : "";
			int track = 0;
			track = tag.getTrackNumber();
			String albumArtist = tag.getAlbumArtist() != null ? tag.getAlbumArtist() : "";

			try {
				org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file);
				org.jaudiotagger.tag.Tag newTag = audioFile.getTag();
				newTag.setAlbum(album);
				newTag.setArtist(artist);
				newTag.setComment(comment);
				newTag.setGenre(genreString);
				newTag.setTitle(title);
				newTag.setYear(year);
				newTag.setTrack(Integer.toString(track));
				newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.LYRICS, lyrics));
				newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.COMPOSER, composer));
				newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.ALBUM_ARTIST, albumArtist));
				audioFile.setTag(newTag);
				logger.debug(LogCategories.FILE_WRITE, "Tag to be written: " + newTag);
				audioFile.commit();
			} catch (Exception e) {
				logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
			}
		}
		if (AudioFile.isMp3File(file)) { // We treat mp3 separately so we can write both id3 v1 and v2
			String title = tag.getTitle() != null ? tag.getTitle() : "";
			String album = tag.getAlbum() != null ? tag.getAlbum() : "";
			String artist = tag.getArtist() != null ? tag.getArtist() : "";
			String year = Integer.toString(tag.getYear());
			String comment = tag.getComment() != null ? tag.getComment() : "";
			String genreString = tag.getGenre();
			String track = "0";
			String lyrics = tag.getLyrics() != null ? tag.getLyrics() : "";
			String composer = tag.getComposer() != null ? tag.getComposer() : "";
			track = Integer.toString(tag.getTrackNumber());
			String albumArtist = tag.getAlbumArtist() != null ? tag.getAlbumArtist() : "";

			// Write id3v1 tag. Note id3v1 specifications does not know more fields. If you want to use more, use id3v2.
			try {
				org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file);
				audioFile.setTag(new org.jaudiotagger.tag.id3.ID3v11Tag());
				org.jaudiotagger.tag.Tag newTag = audioFile.getTag();
				newTag.setAlbum(album);
				newTag.setArtist(artist);
				newTag.setComment(comment);
				newTag.setGenre(genreString);
				newTag.setTitle(title);
				newTag.setYear(year);
				newTag.setTrack(track);
				audioFile.setTag(newTag);

				logger.debug(LogCategories.FILE_WRITE, "Tag to be written: " + audioFile.getTag());
				audioFile.commit();
			} catch (Exception e) {
				logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
			}

			// Write id3v2 tag
			try {
				org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file);
				audioFile.setTag(new org.jaudiotagger.tag.id3.ID3v24Tag());
				org.jaudiotagger.tag.TagOptionSingleton.getInstance().setId3v24DefaultTextEncoding(org.jaudiotagger.tag.id3.valuepair.TextEncoding.UTF_16);
				org.jaudiotagger.tag.Tag newTag = audioFile.getTag();
				newTag.setAlbum(album);
				newTag.setArtist(artist);
				newTag.setComment(comment);
				newTag.setGenre(genreString);
				newTag.setTitle(title);
				newTag.setYear(year);
				newTag.setTrack(track);
				newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.LYRICS, lyrics));
				newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.COMPOSER, composer));
				newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.ALBUM_ARTIST, albumArtist));
				audioFile.setTag(newTag);

				logger.debug(LogCategories.FILE_WRITE, "Tag to be written: " + audioFile.getTag());
				audioFile.commit();
			} catch (Exception e) {
				logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
			}
		}
	}

	public static void setLyrics(AudioFile file, String lyrics) {
		file.setWritable();
		try {
			org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file);
			org.jaudiotagger.tag.Tag newTag = audioFile.getTag();
			newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.LYRICS, lyrics));
			audioFile.commit();
		} catch (Exception e) {
			logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
		}
	}

	/**
	 * Writes title name to tag
	 * 
	 * @param file
	 *            File to which the tag should be written
	 * @param newTitle
	 *            New title
	 */
	public static void setTitles(AudioFile file, String newTitle) {
		file.setWritable();
		try {
			org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file);
			org.jaudiotagger.tag.Tag newTag = audioFile.getTag();
			newTag.setTitle(newTitle);
			audioFile.commit();
		} catch (Exception e) {
			logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
		}
	}

	/**
	 * Sets track number on a file
	 * 
	 * @param file
	 *            File to which the tag should be written
	 * @param track
	 *            Track number
	 */
	public static void setTrackNumber(AudioFile file, Integer track) {
		file.setWritable();
		try {
			org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file);
			org.jaudiotagger.tag.Tag newTag = audioFile.getTag();
			newTag.setTrack(track.toString());
			audioFile.commit();
		} catch (Exception e) {
			logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
		}
	}

}
