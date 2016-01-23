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

import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.Genre;

public class RepositoryStructure implements Serializable {

	private static final long serialVersionUID = -2230698137764691254L;

	private Map<String, Artist> treeStructure;
	private Map<String, Folder> folderStructure;
	private Map<String, Genre> genreStructure;

	protected RepositoryStructure() {
		treeStructure = new HashMap<String, Artist>();
		folderStructure = new HashMap<String, Folder>();
		genreStructure = new HashMap<String, Genre>();
	}

	public Map<String, Folder> getFolderStructure() {
		return folderStructure;
	}

	public Map<String, Genre> getGenreStructure() {
		return genreStructure;
	}

	public Map<String, Artist> getTreeStructure() {
		return treeStructure;
	}
}
