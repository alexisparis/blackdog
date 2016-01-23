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

package net.sourceforge.atunes.kernel.controllers.editTitlesDialog;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.kernel.controllers.model.DialogController;
import net.sourceforge.atunes.kernel.executors.BackgroundExecutor;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.utils.log4j.LogCategories;

public class EditTitlesDialogController extends DialogController<EditTitlesDialog> {

	private List<AudioFile> filesToEdit;
	private Album album;
	private EditTitlesTableModel model;

	public EditTitlesDialogController(EditTitlesDialog dialog) {
		super(dialog);
		addBindings();
	}

	@Override
	protected void addBindings() {
		EditTitlesDialogActionListener actionListener = new EditTitlesDialogActionListener(dialogControlled, this);
		dialogControlled.getRetrieveFromAmazon().addActionListener(actionListener);
		dialogControlled.getOkButton().addActionListener(actionListener);
		dialogControlled.getCancelButton().addActionListener(actionListener);
	}

	@Override
	protected void addStateBindings() {
		// Nothing to do
	}

	protected void editFiles() {
		Map<AudioFile, String> filesAndTitles = ((EditTitlesTableModel) dialogControlled.getTable().getModel()).getNewValues();
		BackgroundExecutor.changeTitles(filesAndTitles);
	}

	public void editFiles(Album alb) {
		logger.debug(LogCategories.CONTROLLER, new String[] { alb.getName() });

		this.album = alb;
		filesToEdit = alb.getAudioFiles();
		Collections.sort(filesToEdit);
		model = new EditTitlesTableModel(filesToEdit);
		dialogControlled.getTable().setModel(model);
		dialogControlled.setVisible(true);
	}

	protected Album getAlbum() {
		return album;
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}

	protected void setTitles(List<String> tracks) {
		model.setTitles(tracks);
		dialogControlled.getTable().repaint();
	}
}
