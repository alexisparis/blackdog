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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.utils.ImageUtils;

import com.jhlabs.image.PerspectiveFilter;

public class Cover3D extends JPanel {

	private static final long serialVersionUID = -3836270786764203330L;

	private static final int angle = 30;
	private static final int gap = 10;

	private static final float opacity = 0.3f;
	private static final float fadeHeight = 0.6f;
	private BufferedImage image;
	private BufferedImage reflectedImage;

	public Cover3D() {
		super();
		setOpaque(false);
	}

	@Override
	public void paintComponent(Graphics g) {
		if (image == null) {
			super.paintComponent(g);
			return;
		}

		Graphics2D g2d = (Graphics2D) g;
		int width = getWidth();
		int height = getHeight();
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		g2d.translate((width - imageWidth) / 2, height / 2 - imageHeight / 2);
		g2d.drawRenderedImage(image, null);

		g2d.translate(0, 2 * imageHeight + gap);
		g2d.scale(1, -1);
		g2d.drawRenderedImage(reflectedImage, null);
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(ImageIcon image) {
		if (image != null) {
			// IMAGE
			this.image = ImageUtils.toBufferedImage(image.getImage());
			PerspectiveFilter filter1 = new PerspectiveFilter(0, angle, Constants.FULL_SCREEN_COVER - angle / 2, (int) (angle * (5.0 / 3.0)), Constants.FULL_SCREEN_COVER - angle
					/ 2, Constants.FULL_SCREEN_COVER, 0, Constants.FULL_SCREEN_COVER + angle);
			this.image = filter1.filter(this.image, null);

			// REFLECTED IMAGE
			int imageWidth = this.image.getWidth();
			int imageHeight = this.image.getHeight();
			BufferedImage reflection = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D rg = reflection.createGraphics();
			rg.drawRenderedImage(this.image, null);
			rg.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
			rg.setPaint(new GradientPaint(0, imageHeight * fadeHeight, new Color(0.0f, 0.0f, 0.0f, 0.0f), 0, imageHeight, new Color(0.0f, 0.0f, 0.0f, opacity)));
			rg.fillRect(0, 0, imageWidth, imageHeight);
			rg.dispose();

			PerspectiveFilter filter2 = new PerspectiveFilter(0, 0, Constants.FULL_SCREEN_COVER - angle / 2, angle * 2, Constants.FULL_SCREEN_COVER - angle / 2,
					Constants.FULL_SCREEN_COVER + angle * 2, 0, Constants.FULL_SCREEN_COVER);
			reflectedImage = filter2.filter(reflection, null);
		} else {
			this.image = null;
			this.reflectedImage = null;
		}
		this.invalidate();
		this.repaint();
	}
}
