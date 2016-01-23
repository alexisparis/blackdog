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

package net.sourceforge.atunes.kernel.executors.processes;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.lyrics.LyricsService;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.TagModifier;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class AddLyricsToTagProcess implements Runnable {

	private Logger logger = new Logger();

	private List<AudioFile> files;

	/**
	 * Writes lyrics to files if any is found. AudioScrobbler is used for
	 * finding lyrics
	 * 
	 * @param files
	 *            to which the lyrics should be written
	 */
	public AddLyricsToTagProcess(List<AudioFile> files) {
		super();
		this.files = files;
	}

	@Override
	public void run() {
		logger.debug(LogCategories.PROCESS);
		VisualHandler.getInstance().showIndeterminateProgressDialog(StringUtils.getString(LanguageTool.getString("PERFORMING_CHANGES"), "..."));
		if (!files.isEmpty()) {
			final List<AudioFile> filesToEdit = new ArrayList<AudioFile>();
			logger.info(LogCategories.PROCESS, StringUtils.getString("Adding lyrics to ", files.size(), " Songs"));
			for (AudioFile file : files) {
				// Check if no lyrics is present and we have enough info for a querry
				if (file.getLyrics() == null && file.getArtist() != null && file.getTitle() != null) {
					logger.debug(LogCategories.PROCESS, StringUtils.getString("Looking for lyrics"));
					String lyrics = LyricsService.getLyrics(file.getArtist(), file.getTitle());
					TagModifier.setLyrics(file, lyrics);
					filesToEdit.add(file);
					logger.debug(LogCategories.PROCESS, StringUtils.getString("Tag should have been written"));
				}
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					TagModifier.refreshAfterTagModify(filesToEdit);
				}
			});
		}
		VisualHandler.getInstance().hideIndeterminateProgressDialog();
	}

}
