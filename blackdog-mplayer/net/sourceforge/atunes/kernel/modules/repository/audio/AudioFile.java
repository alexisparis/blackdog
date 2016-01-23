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

package net.sourceforge.atunes.kernel.modules.repository.audio;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.tags.reader.TagDetector;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.DefaultTag;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.jaudiotagger.audio.AudioFileIO;

/**
 * AudioFile class initializes audio files so tags and audio information can be
 * retrieved by tagging library. Provides information about bitrate, duration
 * and frequency of the audio file. Provides tag informations.
 * 
 * @author fleax
 * 
 */
public class AudioFile extends File implements AudioObject, Serializable {

	private static transient Logger logger = new Logger();

	private static final long serialVersionUID = 7907490762633634241L;

	public static final String MP3_FORMAT = "mp3";
	public static final String OGG_FORMAT = "ogg";
	public static final String MP4_FORMAT_1 = "m4a";
	public static final String MP4_FORMAT_2 = "mp4";
	public static final String MP4_FORMAT_3 = "aac";
	public static final String WAV_FORMAT = "wav";
	public static final String WMA_FORMAT = "wma";
	public static final String FLAC_FORMAT = "flac";
	public static final String APE_FORMAT = "ape";
	public static final String MPC_FORMAT = "mpc";
	//public static final String AU_FORMAT = "au";
	public static final String REALAUDIO_FORMAT = "ra";
	public static final String REALAUDIO_FORMAT_2 = "rm";
	public static final String MPplus_FORMAT = "mp+";
	public static final String MAC_FORMAT = "mac";
	//public static final String MP2_FORMAT = "mp2";

	private Tag tag;
	private List<File> externalPictures;
	private long duration;
	private long bitrate;
	private int frequency;
	private long readTime;
	private int stars = 0;

	public AudioFile(String fileName) {
		super(fileName);
		introspectTags();
		readAudioProperties(this);
		readTime = System.currentTimeMillis();
	}

	public static List<AudioFile> getAudioFiles(List<AudioObject> audioObjects) {
		List<AudioFile> result = new ArrayList<AudioFile>();
		for (AudioObject audioObject : audioObjects) {
			if (audioObject instanceof AudioFile) {
				result.add((AudioFile) audioObject);
			}
		}
		return result;
	}

	public static Tag getNewTag(AudioFile file, Map<String, ?> properties) {
		return new DefaultTag().getTagFromProperties(properties);
	}

	public static boolean isApeFile(File file) {
		return file.getAbsolutePath().toLowerCase().endsWith(APE_FORMAT) || file.getAbsolutePath().toLowerCase().endsWith(MAC_FORMAT);
	}

	public static boolean isFlacFile(File file) {
		return file.getAbsolutePath().toLowerCase().endsWith(FLAC_FORMAT);
	}

	public static boolean isMp3File(File file) {
		return file.getAbsolutePath().toLowerCase().endsWith(MP3_FORMAT);
	}

	public static boolean isMp4File(File file) {
		return file.getAbsolutePath().toLowerCase().endsWith(MP4_FORMAT_1) || file.getAbsolutePath().toLowerCase().endsWith(MP4_FORMAT_2)
				|| file.getAbsolutePath().toLowerCase().endsWith(MP4_FORMAT_3);
	}

	public static boolean isMPCFile(File file) {
		return file.getAbsolutePath().toLowerCase().endsWith(MPC_FORMAT) || file.getAbsolutePath().toLowerCase().endsWith(MPplus_FORMAT);
	}

	public static boolean isOggFile(File file) {
		return file.getAbsolutePath().toLowerCase().endsWith(OGG_FORMAT);
	}

	public static boolean isRealAudioFile(File file) {
		return file.getAbsolutePath().toLowerCase().endsWith(REALAUDIO_FORMAT) || file.getAbsolutePath().toLowerCase().endsWith(REALAUDIO_FORMAT_2);
	}

	public static boolean isValidAudioFile(File file) {
		return !file.isDirectory()
				&& (isMp3File(file) || isOggFile(file) || isMp4File(file) || isWavFile(file) || isWmaFile(file) || isFlacFile(file) || isRealAudioFile(file) || isApeFile(file) || isMPCFile(file));
	}

	public static boolean isValidAudioFile(String file) {
		File f = new File(file);
		return f.exists() && isValidAudioFile(f);
	}

	public static boolean isWavFile(File file) {
		return file.getAbsolutePath().toLowerCase().endsWith(WAV_FORMAT);
	}

	public static boolean isWmaFile(File file) {
		return file.getAbsolutePath().toLowerCase().endsWith(WMA_FORMAT);
	}

	private static void readAudioProperties(AudioFile file) {
		try {
			org.jaudiotagger.audio.AudioFile f = AudioFileIO.read(file);
			file.duration = f.getAudioHeader().getTrackLength();
			file.bitrate = f.getAudioHeader().getBitRateAsNumber();
			file.frequency = f.getAudioHeader().getSampleRateAsNumber();
		} catch (Exception e) {
			logger.error(LogCategories.FILE_READ, e.getMessage());
		}
	}

	public void addExternalPicture(File picture) {
		if (externalPictures != null && !externalPictures.contains(picture))
			externalPictures.add(0, picture);
	}

	private void deleteTags() {
		tag = null;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof AudioFile)) {
			return false;
		} else {
			return ((AudioFile) o).getUrl().equals(getUrl());
		}
	}

	@Override
	@Deprecated
	public String getAbsolutePath() {
		return super.getAbsolutePath();
	}

	@Override
	public String getAlbum() {
		String album;
		if (tag != null) {
			if (tag.getAlbum() == null || tag.getAlbum().equals(""))
				album = LanguageTool.getString("UNKNOWN_ALBUM");
			else
				album = tag.getAlbum();
		} else
			album = LanguageTool.getString("UNKNOWN_ALBUM");
		return album;
	}

	@Override
	public String getAlbumArtist() {
		String albumArtist;
		if (tag != null) {
			if (tag.getAlbumArtist() == null || tag.getAlbumArtist().isEmpty())
				albumArtist = "";
			else
				albumArtist = tag.getAlbumArtist();
		} else
			albumArtist = "";
		return albumArtist;
	}

	@Override
	public String getArtist() {
		String artist;
		if (tag != null) {
			if (tag.getArtist() == null || tag.getArtist().equals(""))
				artist = LanguageTool.getString("UNKNOWN_ARTIST");
			else
				artist = tag.getArtist();
		} else
			artist = LanguageTool.getString("UNKNOWN_ARTIST");
		return artist;
	}

	@Override
	public long getBitrate() {
		return bitrate;
	}

	@Override
	public String getComposer() {
		String composer;
		if (tag != null) {
			if (tag.getComposer() == null || tag.getComposer().isEmpty())
				composer = "";
			else
				composer = tag.getComposer();
		} else
			composer = "";
		return composer;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	public List<File> getExternalPictures() {
		return externalPictures;
	}

	public int getExternalPicturesCount() {
		return externalPictures != null ? externalPictures.size() : 0;
	}

	@Override
	public int getFrequency() {
		return frequency;
	}

	@Override
	public String getGenre() {
		if (tag != null && tag.getGenre() != null) {
			return tag.getGenre();
		}
		return LanguageTool.getString("UNKNOWN_GENRE");
	}

	public String getLyrics() {
		String lyrics;
		if (tag != null) {
			if (tag.getLyrics() == null || tag.getLyrics().equals(""))
				lyrics = null;
			else
				lyrics = tag.getLyrics();
		} else
			lyrics = null;
		return lyrics;
	}

	public String getNameWithoutExtension() {
		if (getName().indexOf('.') != -1)
			return getName().substring(0, getName().lastIndexOf('.'));
		return getName();
	}

	/**
	 * @return the stars
	 */
	@Override
	public int getStars() {
		return stars;
	}

	public Tag getTag() {
		return tag;
	}

	@Override
	public String getTitle() {
		String title;
		if (tag != null) {
			if (tag.getTitle() == null || tag.getTitle().equals(""))
				title = "";
			else
				title = tag.getTitle();
		} else
			title = "";
		return title;
	}

	@Override
	public String getTitleOrFileName() {
		String title;
		if (tag != null) {
			if (tag.getTitle() == null || tag.getTitle().equals(""))
				title = getNameWithoutExtension();
			else
				title = tag.getTitle();
		} else
			title = getNameWithoutExtension();
		return title;
	}

	@Override
	public Integer getTrackNumber() {
		if (tag != null) {
			if (tag instanceof DefaultTag)
				return ((DefaultTag) tag).getTrackNumber();
			else
				return 0;
		}
		return 0;
	}

	@Override
	public String getUrl() {
		return super.getAbsolutePath();
	}

	@Override
	public String getYear() {
		if (tag != null && tag.getYear() > 0)
			return Integer.toString(tag.getYear());
		return "";
	}

	@Override
	public int hashCode() {
		return getUrl().hashCode();
	}

	// TODO Reimplement reading and writing of Picture to tag. JAudiotagger should support this.
	public final boolean hasInternalPicture() {
		return false;
	}

	private void introspectTags() {
		TagDetector.getTags(this);
	}

	public boolean isUpToDate() {
		return readTime > lastModified();
	}

	public void refreshTag() {
		deleteTags();
		introspectTags();
		readTime = System.currentTimeMillis();
	}

	public void setExternalPictures(List<File> externalPictures) {
		this.externalPictures = externalPictures;
	}

	/**
	 * @param stars
	 *            the stars to set
	 */
	@Override
	public void setStars(int stars) {
		this.stars = stars;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	/**
	 * Sets write permissions if is not writable
	 */
	public void setWritable() {
		// Set write permission on file
		if (!canWrite())
			setWritable(true);
		// Set write permission on parent
		if (!getParentFile().canWrite())
			getParentFile().setWritable(true);
	}

	@Override
	public String toString() {
		return getName();
	}
}
