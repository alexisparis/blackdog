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

package net.sourceforge.atunes.kernel.controllers.editPreferencesDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.views.dialogs.editPreferences.EditPreferencesDialog;
import net.sourceforge.atunes.kernel.handlers.AudioScrobblerServiceHandler;

public class EditPreferencesDialogListener implements ListSelectionListener, ActionListener {

	private EditPreferencesDialog editPreferencesDialog;
	private EditPreferencesDialogController editPreferencesDialogController;

	public EditPreferencesDialogListener(EditPreferencesDialog editPreferencesDialog, EditPreferencesDialogController editPreferencesDialogController) {
		this.editPreferencesDialog = editPreferencesDialog;
		this.editPreferencesDialogController = editPreferencesDialogController;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == editPreferencesDialog.getOk()) {
			if (editPreferencesDialogController.noErrors()) {
				editPreferencesDialogController.processPreferences();
				editPreferencesDialog.setVisible(false);
				editPreferencesDialogController.updateApplication();
			}
		} else if (e.getSource() == editPreferencesDialog.getCancel()) {
			editPreferencesDialog.setVisible(false);
		} else if (e.getSource() == editPreferencesDialog.getAudioScrobblerPanel().getClearCache()) {
			AudioScrobblerServiceHandler.getInstance().clearCaches();
		}

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == editPreferencesDialog.getList()) {
			editPreferencesDialog.showPanel(editPreferencesDialog.getList().getSelectedIndex());
		}

	}

}
