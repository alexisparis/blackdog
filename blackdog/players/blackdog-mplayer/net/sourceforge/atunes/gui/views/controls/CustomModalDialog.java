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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.GuiUtils;

public abstract class CustomModalDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel container;

	public CustomModalDialog(JFrame owner, int width, int height, boolean modal) {
		super(owner);
		setSize(width, height);
		setUndecorated(true);
		setModalityType(modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		container = new JPanel(new BorderLayout());

		container.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
		add(container);
	}

	public void enableCloseActionWithEscapeKey() {
		GuiUtils.addCloseActionWithEscapeKey(this);
	}

	public void enableDisposeActionWithEscapeKey() {
		GuiUtils.addDisposeActionWithEscapeKey(this);
	}

	public void setContent(JPanel content) {
		content.setOpaque(false);
		container.add(content, BorderLayout.CENTER);
	}

}
