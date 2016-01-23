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
import java.util.Map;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.TagModifier;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class SetTrackNumberProcess implements Runnable {

	private Logger logger = new Logger();

	private Map<AudioFile, Integer> filesAndTracks;

	public SetTrackNumberProcess(Map<AudioFile, Integer> filesAndTracks) {
		super();
		this.filesAndTracks = filesAndTracks;
	}

	@Override
	public void run() {
		logger.debug(LogCategories.PROCESS);

		final List<AudioFile> filesToEdit = new ArrayList<AudioFile>();
		VisualHandler.getInstance().showIndeterminateProgressDialog(StringUtils.getString(LanguageTool.getString("PERFORMING_CHANGES"), "..."));
		for (AudioFile file : filesAndTracks.keySet()) {
			Integer trackNumber = filesAndTracks.get(file);
			TagModifier.setTrackNumber(file, trackNumber);
			filesToEdit.add(file);
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
