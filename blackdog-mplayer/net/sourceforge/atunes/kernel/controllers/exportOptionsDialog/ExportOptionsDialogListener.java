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

package net.sourceforge.atunes.kernel.controllers.exportOptionsDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.sourceforge.atunes.gui.views.dialogs.ExportOptionsDialog;
import net.sourceforge.atunes.kernel.executors.processes.ExportFilesProcess;

public class ExportOptionsDialogListener extends WindowAdapter implements ActionListener {

	private ExportOptionsDialogController controller;
	private ExportOptionsDialog dialog;

	public ExportOptionsDialogListener(ExportOptionsDialogController controller, ExportOptionsDialog dialog) {
		super();
		this.controller = controller;
		this.dialog = dialog;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		dialog.setVisible(false);
		controller.setCanceled(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == dialog.getExportButton()) {
			dialog.setVisible(false);
			controller.setCanceled(false);
			controller.setPath(dialog.getExportLocation().getResult());
			if (!dialog.getNoChangeFileNamesRadioButton().isSelected()) {
				controller.setFilePattern(dialog.getCustomFileNameFormatTextField().getText());
			}
		} else if (e.getSource() == dialog.getCancelButton()) {
			dialog.setVisible(false);
			controller.setCanceled(true);
		} else if (e.getSource() == dialog.getFlatStructureRadioButton()) {
			controller.setExportStructure(ExportFilesProcess.FLAT_STRUCTURE);
		} else if (e.getSource() == dialog.getArtistStructureRadioButton()) {
			controller.setExportStructure(ExportFilesProcess.ARTIST_STRUCTURE);
		} else if (e.getSource() == dialog.getFullStructureRadioButton()) {
			controller.setExportStructure(ExportFilesProcess.FULL_STRUCTURE);
		} else if (e.getSource() == dialog.getNoChangeFileNamesRadioButton()) {
			controller.setFilePattern(null);
		} else if (e.getSource() == dialog.getAllTypeRadioButton()) {
			controller.setExportType(ExportFilesProcess.ALL_EXPORT);
		} else if (e.getSource() == dialog.getSelectionTypeRadioButton()) {
			controller.setExportType(ExportFilesProcess.SELECTION_EXPORT);
		} else if (e.getSource() == dialog.getFavoritesTypeRadioButton()) {
			controller.setExportType(ExportFilesProcess.FAVORITES_EXPORT);
		}
	}
}
