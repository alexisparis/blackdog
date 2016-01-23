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

package net.sourceforge.atunes.kernel.modules.cdripper.encoders;

import java.io.File;

import net.sourceforge.atunes.kernel.modules.cdripper.ProgressListener;

public interface Encoder {

	public boolean encode(File originalFile, File encodedFile, String title, int trackNumber, String artist, String composer);

	public String getExtensionOfEncodedFiles();

	public void setAlbum(String album);

	public void setArtist(String artist);

	public void setGenre(String genre);

	public void setListener(ProgressListener listener);

	public void setQuality(String quality);

	public void setYear(int year);

	public void stop();

}
