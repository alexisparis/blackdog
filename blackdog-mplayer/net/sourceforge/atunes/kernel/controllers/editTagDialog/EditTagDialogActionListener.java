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

package net.sourceforge.atunes.kernel.controllers.editTagDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;

public class EditTagDialogActionListener implements ActionListener {

	private EditTagDialogController controller;
	private EditTagDialog dialog;

	public EditTagDialogActionListener(EditTagDialogController controller, EditTagDialog dialog) {
		this.controller = controller;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == dialog.getOkButton()) {
			controller.editTag();
		} else if (e.getSource() == dialog.getCancelButton()) {
			dialog.setVisible(false);
		}
	}
}
