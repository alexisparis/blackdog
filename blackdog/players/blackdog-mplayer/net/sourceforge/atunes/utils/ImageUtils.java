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

package net.sourceforge.atunes.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class ImageUtils {

	private static final Logger logger = new Logger();

	/**
	 * Resizes an ImageIcon object. If any size is -1 returns image with no
	 * modification
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static ImageIcon resize(ImageIcon image, int width, int height) {
		if (width == -1 || height == -1) {
			return image;
		}
		if (width == image.getIconWidth() && height == image.getIconHeight())
			return image;

		int maxSize = (image.getIconWidth() > image.getIconHeight()) ? image.getIconWidth() : image.getIconHeight();
		int newWidth = (int) ((float) image.getIconWidth() / (float) maxSize * width);
		int newHeight = (int) ((float) image.getIconHeight() / (float) maxSize * height);
		BufferedImage resizedImage = ImageUtils.scaleImage(image.getImage(), newWidth, newHeight);
		return resizedImage != null ? new ImageIcon(resizedImage) : null;
	}

	/**
	 * Scales an image
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage scaleImage(Image image, int width, int height) {
		if (image == null)
			return null;
		image = new ImageIcon(image).getImage();
		ImageFilter filter = new ReplicateScaleFilter(width, height);
		ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
		Image resizedImage = Toolkit.getDefaultToolkit().createImage(producer);

		return toBufferedImage(resizedImage);
	}

	/**
	 * Scales an image with Bicubic algorithm
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static ImageIcon scaleImageBicubic(Image image, int width, int height) {
		if (image == null)
			return null;

		double thumbRatio = (double) width / (double) height;
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		double imageRatio = (double) imageWidth / (double) imageHeight;
		if (thumbRatio < imageRatio) {
			height = (int) (width / imageRatio);
		} else {
			width = (int) (height * imageRatio);
		}
		BufferedImage thumbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics2D.drawImage(image, 0, 0, width, height, null);
		return new ImageIcon(thumbImage);
	}

	/**
	 * Gets a BufferedImage from an Image object
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage toBufferedImage(Image image) {
		BufferedImage bufferedImage;
		try {
			image = new ImageIcon(image).getImage();
			bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bufferedImage.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
		} catch (IllegalArgumentException e) {
			logger.info(LogCategories.IMAGE, "Maybe picture file with wrong ending?");
			logger.error(LogCategories.IMAGE, e);
			return null;
		}

		return bufferedImage;
	}

}
