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
import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;

import net.sourceforge.atunes.gui.views.dialogs.CopyProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.ExportOptionsDialog;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.controllers.model.DialogController;
import net.sourceforge.atunes.kernel.executors.BackgroundExecutor;
import net.sourceforge.atunes.kernel.executors.processes.ExportFilesProcess;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;

public class ExportOptionsDialogController extends DialogController<ExportOptionsDialog> {

	private int exportType;
	private int exportStructure;
	private String filePattern;
	private String path;

	private boolean canceled;

	private ExportOptionsDialog dialogControlled;

	public ExportOptionsDialogController(ExportOptionsDialog dialogControlled) {
		super(dialogControlled);
		this.dialogControlled = dialogControlled;
		addBindings();
		addStateBindings();
	}

	@Override
	protected void addBindings() {
		ExportOptionsDialogListener listener = new ExportOptionsDialogListener(this, dialogControlled);
		dialogControlled.getExportButton().addActionListener(listener);
		dialogControlled.getCancelButton().addActionListener(listener);
		dialogControlled.getFlatStructureRadioButton().addActionListener(listener);
		dialogControlled.getArtistStructureRadioButton().addActionListener(listener);
		dialogControlled.getFullStructureRadioButton().addActionListener(listener);
		dialogControlled.getNoChangeFileNamesRadioButton().addActionListener(listener);
		dialogControlled.getAllTypeRadioButton().addActionListener(listener);
		dialogControlled.getSelectionTypeRadioButton().addActionListener(listener);
		dialogControlled.getFavoritesTypeRadioButton().addActionListener(listener);
		dialogControlled.addWindowListener(listener);
	}

	@Override
	protected void addStateBindings() {
		exportType = ExportFilesProcess.ALL_EXPORT;
		dialogControlled.getAllTypeRadioButton().setSelected(true);

		exportStructure = ExportFilesProcess.FULL_STRUCTURE;
		dialogControlled.getFullStructureRadioButton().setSelected(true);

		filePattern = null;
		dialogControlled.getNoChangeFileNamesRadioButton().setSelected(true);
	}

	public void beginExportProcess() {
		logger.debug(LogCategories.CONTROLLER);

		dialogControlled.setVisible(true);
		if (!canceled) {
			boolean isValidPath = verifyPath();
			boolean isValidFilePattern = verifyFilePattern();
			if (isValidPath && isValidFilePattern) {
				boolean pathExists = new File(path).exists();
				boolean userWantsToCreate = false;
				if (!pathExists) {
					if (VisualHandler.getInstance().showConfirmationDialog(LanguageTool.getString("DIR_NO_EXISTS"), LanguageTool.getString("INFO")) == JOptionPane.OK_OPTION) {
						pathExists = new File(path).mkdir();
						userWantsToCreate = true;
					}
				}
				if (pathExists) {
					List<AudioFile> songs;
					if (exportType == ExportFilesProcess.ALL_EXPORT)
						songs = RepositoryHandler.getInstance().getSongs();
					else if (exportType == ExportFilesProcess.SELECTION_EXPORT)
						songs = AudioFile.getAudioFiles(ControllerProxy.getInstance().getNavigationController().getSongsForNavigationTree());
					else
						songs = FavoritesHandler.getInstance().getFavoriteSongs();
					int filesToExport = songs.size();

					CopyProgressDialog exportProgressDialog = VisualHandler.getInstance().getExportProgressDialog();
					exportProgressDialog.getTotalFilesLabel().setText(StringUtils.getString(filesToExport));
					exportProgressDialog.getProgressBar().setMaximum(filesToExport);
					exportProgressDialog.setVisible(true);

					final ExportFilesProcess process = BackgroundExecutor.exportFiles(songs, exportStructure, filePattern, path, false);

					exportProgressDialog.getCancelButton().addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							process.notifyCancel();
						}
					});
				} else if (userWantsToCreate) {
					VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("COULD_NOT_CREATE_DIR"));
				}
			} else {
				if (!isValidPath) {
					VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("INCORRECT_EXPORT_PATH"));
				}
				if (!isValidFilePattern) {
					VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("INCORRECT_FILE_PATTERN"));
				}
			}
		}
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public void setExportStructure(int exportStructure) {
		this.exportStructure = exportStructure;
	}

	public void setExportType(int exportType) {
		this.exportType = exportType;
	}

	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}

	public void setPath(String path) {
		this.path = path;
	}

	private boolean verifyFilePattern() {
		return filePattern == null || !filePattern.equals("");
	}

	private boolean verifyPath() {
		return path != null && !path.equals("");
	}
}
