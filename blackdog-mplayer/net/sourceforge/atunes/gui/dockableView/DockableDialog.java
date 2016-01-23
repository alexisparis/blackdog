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

package net.sourceforge.atunes.gui.dockableView;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * This class represents a dockable JDialog
 * 
 * @author fleax
 */
public class DockableDialog extends DockableWindow {

	/**
	 * Constructor
	 * 
	 * @param parent
	 * @param width
	 * @param heigth
	 * @param minSize
	 * @param listener
	 */
	public DockableDialog(DockableFrame parent, int width, int heigth, Dimension minSize, DockFramePositionListener listener) {
		super(listener, minSize);
		this.listener = listener;
		frame = new JDialog((JFrame) parent.frame);
		((JDialog) frame).setUndecorated(true);
		frame.setSize(width, heigth);
		frame.add(container);
		frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		addResizableBorders();
		setHidden(false);
		frame.setFocusable(false);
	}

	/**
	 * Set Default Close Operation
	 */
	@Override
	public void setDefaultCloseOperation(int op) {
		((JDialog) frame).setDefaultCloseOperation(op);
	}

	/**
	 * Sets image
	 */
	@Override
	public void setIcon(Image i) {
		// Dockable dialogs have no image
	}

	/**
	 * Set Location Relative
	 */
	@Override
	public void setLocationRelativeTo(Component c) {
		((JDialog) frame).setLocationRelativeTo(c);
	}

	/**
	 * Sets title
	 */
	@Override
	public void setTitle(String s) {
		title.setText(s);
		((JDialog) frame).setTitle(s);
	}

}
