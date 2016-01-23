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

package net.sourceforge.atunes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;

/**
 * This class represents a folder with a name, and a list of files and more
 * folders
 * 
 * @author fleax
 * 
 */
public class Folder implements Serializable, TreeObject {

	private static final long serialVersionUID = 2608221109707838025L;

	/**
	 * Name of the folder
	 */
	private String name;

	/**
	 * List of files in this folder
	 */
	private List<AudioFile> files;

	/**
	 * List of folders in this folder, indexed by name
	 */
	private Map<String, Folder> folders;

	/**
	 * Folder that contains this folder
	 */
	private Folder parentFolder;

	/**
	 * Constructor
	 * 
	 * @param name
	 */
	public Folder(String name) {
		this.name = name;
		this.files = new ArrayList<AudioFile>();
		folders = new HashMap<String, Folder>();
	}

	/**
	 * Adds a file to this folder
	 * 
	 * @param file
	 */
	public void addFile(AudioFile file) {
		files.add(file);
	}

	/**
	 * Adds a folder as child of this folder
	 * 
	 * @param f
	 */
	public void addFolder(Folder f) {
		if (folders.containsKey(f.getName())) {
			Folder folder = folders.get(f.getName());
			folder.addFoldersOf(f);
		} else {
			folders.put(f.getName(), f);
			f.setParentFolder(this);
		}
	}

	/**
	 * Adds all children folders of a given folder to this
	 * 
	 * @param f
	 */
	private void addFoldersOf(Folder f) {
		folders.putAll(f.getFolders());
	}

	/**
	 * Returns true if folder contains a folder with given name
	 * 
	 * @param folderName
	 * @return
	 */
	public boolean containsFolder(String folderName) {
		return folders.containsKey(folderName);
	}

	public List<AudioFile> getAudioFiles() {
		List<AudioFile> result = new ArrayList<AudioFile>();
		result.addAll(files);
		for (String string : folders.keySet()) {
			Folder f = folders.get(string);
			result.addAll(f.getAudioFiles());
		}
		return result;
	}

	/**
	 * Returns a list of songs in this folder and in children folders
	 */
	@Override
	public List<AudioObject> getAudioObjects() {
		List<AudioObject> result = new ArrayList<AudioObject>();
		result.addAll(files);
		for (String string : folders.keySet()) {
			Folder f = folders.get(string);
			result.addAll(f.getAudioObjects());
		}
		return result;
	}

	/**
	 * Returns files in this folder
	 * 
	 * @return
	 */
	public List<AudioFile> getFiles() {
		return files;
	}

	/**
	 * Returns a child folder given a folder name
	 * 
	 * @param folderName
	 * @return
	 */
	public Folder getFolder(String folderName) {
		return folders.get(folderName);
	}

	/**
	 * Returns all children folders
	 * 
	 * @return
	 */
	public Map<String, Folder> getFolders() {
		return folders;
	}

	/**
	 * Returns the name of this folder
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the parentFolder
	 */
	public Folder getParentFolder() {
		return parentFolder;
	}

	/**
	 * Returns true if folder is empty (contains neither files nor folders)
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return files.isEmpty() && folders.isEmpty();
	}

	/**
	 * Removes a file from this folder
	 * 
	 * @param file
	 */
	public void removeFile(AudioFile file) {
		files.remove(file);
	}

	/**
	 * Removes a folder from this folder
	 * 
	 * @param f
	 */
	public void removeFolder(Folder f) {
		folders.remove(f.getName());
	}

	/**
	 * Sets the name of this folder
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param parentFolder
	 *            the parentFolder to set
	 */
	private void setParentFolder(Folder parentFolder) {
		this.parentFolder = parentFolder;
	}

	/**
	 * String representation
	 */
	@Override
	public String toString() {
		return name;
	}
}
