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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.utils.ImageUtils;

public class OSDDialog extends CustomModalDialog {

	private static final long serialVersionUID = 8991547440913162267L;

	private static final int IMAGE_SIZE = 70;

	private int width = this.getToolkit().getScreenSize().width / 3;
	private int height = 84;

	private JLabel line1;
	private JLabel line2;
	private JLabel line3;
	private JLabel image;

	public OSDDialog() {
		super(null, 0, 0, false);
		setSize(width, height);
		setFocusableWindowState(false);
		setAlwaysOnTop(true);
		setContent(getContent());
		GuiUtils.applyComponentOrientation(this);
	}

	private JPanel getContent() {
		JPanel panel = new JPanel(null);
		panel.setSize(width, height);
		image = new JLabel();
		image.setOpaque(true);
		line1 = new JLabel();
		line2 = new JLabel();
		line3 = new JLabel();

		line1.setFont(Fonts.GENERAL_FONT_BOLD);

		line1.setHorizontalAlignment(SwingConstants.CENTER);
		line2.setHorizontalAlignment(SwingConstants.CENTER);
		line3.setHorizontalAlignment(SwingConstants.CENTER);

		panel.add(image);
		panel.add(line1);
		panel.add(line2);
		panel.add(line3);
		return panel;
	}

	public void setImage(ImageIcon img) {
		ImageIcon imgResized;
		if (img != null && (imgResized = ImageUtils.resize(img, IMAGE_SIZE, IMAGE_SIZE)) != null) {
			image.setIcon(imgResized);
			image.setSize(imgResized.getIconWidth(), imgResized.getIconHeight());
			image.setLocation(10, (height - IMAGE_SIZE) / 2);
			line1.setSize(width - 100, 20);
			line1.setLocation(90, 10);
			line2.setSize(width - 100, 20);
			line2.setLocation(90, 32);
			line3.setSize(width - 100, 20);
			line3.setLocation(90, 55);
		} else {
			image.setSize(0, 0);
			line1.setSize(width - 20, 20);
			line1.setLocation(10, 10);
			line2.setSize(width - 20, 20);
			line2.setLocation(10, 32);
			line3.setSize(width - 20, 20);
			line3.setLocation(10, 55);
		}
	}

	public void setLine1(String text) {
		line1.setText(text);
	}

	public void setLine2(String text) {
		line2.setText(text);
	}

	public void setLine3(String text) {
		line3.setText(text);
	}

	/**
	 * Sets rounded borders
	 * 
	 * @param set
	 */
	public void setRoundedBorders(boolean set) {
		Shape mask = null;
		if (set)
			mask = new Area(new RoundRectangle2D.Float(2, 2, width - 3, height - 3, 20, 25));
		GuiUtils.setWindowMask(this, mask);
	}

	/**
	 * Sets the window transparency
	 * 
	 * @param transparent
	 */
	public void setTransparent(boolean transparent) {
		GuiUtils.setWindowTransparentIfSupported(this, transparent);
	}

}
