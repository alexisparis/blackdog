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

package net.sourceforge.atunes.kernel.utils;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.sourceforge.atunes.utils.StringUtils;

/**
 * Utility methods for picture exporting
 */
public class PictureExporter {

	public static final String FILES_EXTENSION = "png";

	/**
	 * Writes an image into a file in png format
	 * 
	 * @param image
	 *            The image that should be written to a file
	 * @param fileName
	 *            The name of the file
	 * @throws IOException
	 *             If an IO exception occurs
	 */
	public static void savePicture(Image image, String fileName) throws IOException {
		if (image == null)
			return;

		ImageIcon img = new ImageIcon(image);

		BufferedImage buf = new BufferedImage(img.getIconWidth(), img.getIconHeight(), BufferedImage.TYPE_INT_BGR);
		Graphics g = buf.createGraphics();
		g.drawImage(image, 0, 0, null);
		String fileNameWithExtension = fileName;
		if (!fileName.toUpperCase().endsWith(StringUtils.getString(".", FILES_EXTENSION).toUpperCase()))
			fileNameWithExtension = StringUtils.getString(fileName, ".", FILES_EXTENSION);

		ImageIO.write(buf, FILES_EXTENSION, new FileOutputStream(fileNameWithExtension));
	}

}
