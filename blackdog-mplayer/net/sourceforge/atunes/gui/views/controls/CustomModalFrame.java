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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JRootPane;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;

public abstract class CustomModalFrame extends JFrame {

	private static final long serialVersionUID = -2809415541883627950L;

	private static MouseListener mouseListener = new MouseAdapter() {
	};

	private JFrame owner;

	public CustomModalFrame(JFrame owner) {
		super();
		getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		setIconImage(ImageLoader.APP_ICON.getImage());
		this.owner = owner;
	}

	public void enableCloseActionWithEscapeKey() {
		GuiUtils.addCloseActionWithEscapeKey(this);
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (owner == null)
			return;

		if (b) {
			owner.getGlassPane().addMouseListener(mouseListener);
		} else {
			owner.getGlassPane().removeMouseListener(mouseListener);
		}

		owner.getGlassPane().setVisible(b);
	}

}
