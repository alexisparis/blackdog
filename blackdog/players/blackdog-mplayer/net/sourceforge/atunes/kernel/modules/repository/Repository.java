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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;

/**
 * @author fleax
 * 
 */
public class Repository implements Serializable {

	private static final long serialVersionUID = -8278937514875788175L;

	private List<File> folders;
	private Map<String, AudioFile> files;
	private long totalSizeInBytes;
	private long totalDurationInSeconds;

	private RepositoryStructure structure;
	private RepositoryStats stats;

	public Repository(List<File> folders) {
		this.folders = folders;
		files = new HashMap<String, AudioFile>();
		structure = new RepositoryStructure();
		stats = new RepositoryStats();
	}

	public void addDurationInSeconds(long seconds) {
		totalDurationInSeconds += seconds;
	}

	public int countFiles() {
		return files.size();
	}

	public AudioFile getFile(String fileName) {
		return files.get(fileName);
	}

	public Map<String, AudioFile> getFiles() {
		return files;
	}

	public List<AudioFile> getFilesList() {
		return new ArrayList<AudioFile>(files.values());
	}

	public List<File> getFolders() {
		return folders;
	}

	public RepositoryStats getStats() {
		return stats;
	}

	public RepositoryStructure getStructure() {
		return structure;
	}

	public long getTotalDurationInSeconds() {
		return totalDurationInSeconds;
	}

	public long getTotalSizeInBytes() {
		return totalSizeInBytes;
	}

	public void removeDurationInSeconds(long seconds) {
		totalDurationInSeconds -= seconds;
	}

	public void setTotalSizeInBytes(long totalSizeInBytes) {
		this.totalSizeInBytes = totalSizeInBytes;
	}
}
