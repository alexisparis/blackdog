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

import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.TagModifier;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class ChangeTagsProcess implements Runnable {

	private Logger logger = new Logger();

	private List<AudioFile> filesToEdit;
	private Map<String, ?> properties;

	/**
	 * Process for writting tag to files. Receives AudioFiles to be written and
	 * new properties (metainformation)
	 * 
	 * @param filesToEdit
	 *            Files that must be edited (tag)
	 * @param properties
	 *            Information to be written
	 */
	public ChangeTagsProcess(List<AudioFile> filesToEdit, Map<String, ?> properties) {
		super();
		this.filesToEdit = filesToEdit;
		this.properties = properties;
	}

	@Override
	public void run() {
		logger.debug(LogCategories.PROCESS);

		VisualHandler.getInstance().showIndeterminateProgressDialog(StringUtils.getString(LanguageTool.getString("PERFORMING_CHANGES"), "..."));

		for (int i = 0; i < filesToEdit.size(); i++) {
			Tag newTag = AudioFile.getNewTag(filesToEdit.get(i), properties);
			Tag oldTag = (filesToEdit.get(i)).getTag();
			if (filesToEdit.size() > 1) {
				if (oldTag != null) {
					/*
					 * This list in order to avoid deleting tag fields that were
					 * left blank in the edit dialog. When mass tagging, not all
					 * fields get passed so we must add them again to avoid
					 * losses.
					 * 
					 * Problem: When a field get emptied in the dialog, this
					 * part of the code will put it back. It is currently not
					 * possible to "delete" fields using mass tagging.
					 */
					newTag.setTitle(filesToEdit.get(i).getTag().getTitle());
					if (newTag.getAlbum().isEmpty())
						newTag.setAlbum(filesToEdit.get(i).getTag().getAlbum());
					if (newTag.getArtist().isEmpty())
						newTag.setArtist(filesToEdit.get(i).getTag().getArtist());
					if (newTag.getYear() == -1)
						newTag.setYear(filesToEdit.get(i).getTag().getYear());
					if (newTag.getComment().isEmpty())
						newTag.setComment(filesToEdit.get(i).getTag().getComment());
					if (newTag.getGenre().isEmpty())
						newTag.setGenre(filesToEdit.get(i).getTag().getGenre());
					newTag.setTrackNumber(oldTag.getTrackNumber());
					// Composer, album artist and Lyrics are null and not empty. Otherwise process freeze occur.
					if (newTag.getLyrics() == null)
						newTag.setLyrics(filesToEdit.get(i).getTag().getLyrics());
					if (newTag.getComposer() == null)
						newTag.setComposer(filesToEdit.get(i).getTag().getComposer());
					if (newTag.getAlbumArtist() == null)
						newTag.setAlbumArtist(filesToEdit.get(i).getTag().getAlbumArtist());
				}

			}
			TagModifier.setInfo(filesToEdit.get(i), newTag);
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TagModifier.refreshAfterTagModify(filesToEdit);
			}
		});
		VisualHandler.getInstance().hideIndeterminateProgressDialog();
	}
}
