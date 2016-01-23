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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class gets images associated to audio files Images can be internal (like
 * in ID3v2) or external (in the same folder than audio file)
 * 
 * @author fleax
 * 
 */
public class AudioFilePictureUtils {

	public static void exportPicture(AudioFile song) throws FileNotFoundException, IOException {
		JFileChooser fileChooser = new JFileChooser();
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toUpperCase().endsWith("PNG");
			}

			@Override
			public String getDescription() {
				return "PNG files";
			}
		};
		fileChooser.setFileFilter(filter);
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().toUpperCase().endsWith("PNG"))
				file = new File(StringUtils.getString(file.getAbsolutePath(), ".png"));
			if (!file.exists() || (file.exists() && JOptionPane.showConfirmDialog(null, LanguageTool.getString("OVERWRITE_FILE")) == JOptionPane.OK_OPTION)) {
				savePictureToFile(song, file);
			}
		}
	}

	private static ImageIcon getExternalPicture(AudioFile file, int width, int height) {
		return getExternalPicture(file, 0, width, height);
	}

	private static ImageIcon getExternalPicture(AudioFile file, int index, int width, int height) {
		if (file != null && file.getExternalPictures() != null && file.getExternalPictures().size() > index) {
			File firstPicture = file.getExternalPictures().get(index);
			ImageIcon image = new ImageIcon(firstPicture.getAbsolutePath());
			if (width == -1 || height == -1) {
				return image;
			}
			int maxSize = (image.getIconWidth() > image.getIconHeight()) ? image.getIconWidth() : image.getIconHeight();
			int newWidth = (int) ((float) image.getIconWidth() / (float) maxSize * width);
			int newHeight = (int) ((float) image.getIconHeight() / (float) maxSize * height);
			BufferedImage resizedImage = ImageUtils.scaleImage(image.getImage(), newWidth, newHeight);
			return resizedImage != null ? new ImageIcon(resizedImage) : null;
		}
		return null;
	}

	/**
	 * Returns a file name to save an external image associated to an audio file
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileNameForCover(AudioFile file) {
		if (file == null)
			return null;
		return StringUtils.getString(file.getParentFile().getAbsolutePath(), SystemProperties.fileSeparator, file.getArtist(), '_', file.getAlbum(), "_Cover.",
				PictureExporter.FILES_EXTENSION);
	}

	/**
	 * Returns an image associated to an audio file, with following order: - If
	 * a image saved by aTunes exists, then return it. - If not, find an
	 * internal image - If not, find an external image - If not, return null
	 * 
	 * @param file
	 *            Audio File
	 * @param width
	 *            Width in pixels or -1 to keep original width
	 * @param height
	 *            Height in pixels or -1 to keep original height
	 * @return
	 */
	public static ImageIcon getImageForAudioFile(AudioFile file, int width, int height) {
		ImageIcon result = null;

		String fileNameCover = getFileNameForCover(file);
		if (fileNameCover != null) {
			File coverFile = new File(fileNameCover);
			if (coverFile.exists()) {
				ImageIcon image = new ImageIcon(coverFile.getAbsolutePath());
				if (width == -1 || height == -1) {
					return image;
				}
				int maxSize = (image.getIconWidth() > image.getIconHeight()) ? image.getIconWidth() : image.getIconHeight();
				int newWidth = (int) ((float) image.getIconWidth() / (float) maxSize * width);
				int newHeight = (int) ((float) image.getIconHeight() / (float) maxSize * height);
				BufferedImage resizedImage = ImageUtils.scaleImage(image.getImage(), newWidth, newHeight);
				return resizedImage != null ? new ImageIcon(resizedImage) : null;
			}
		}

		result = getInsidePicture(file, width, height);
		if (result == null)
			result = getExternalPicture(file, width, height);

		return result;
	}

	/**
	 * Returns image stored into audio file, if exists
	 * 
	 * @param file
	 * @param width
	 *            Width in pixels or -1 to keep original width
	 * @param height
	 *            Height in pixels or -1 to keep original height
	 * @return
	 */
	private static ImageIcon getInsidePicture(AudioFile file, int width, int height) {
		FileInputStream stream = null;
		try {
			//			if (file.getTag() instanceof ID3v2Tag) {
			//				long pictureBegin = ((ID3v2Tag) file.getTag()).getPictureBegin();
			//				long pictureLength = ((ID3v2Tag) file.getTag()).getPictureLength();
			//				stream = new FileInputStream(file);
			//				stream.skip(pictureBegin + 1);
			//				byte[] image = new byte[(int) pictureLength];
			//				stream.read(image);
			//
			//				if (image.length > 3) {
			//					int pointer = 0;
			//					while (image[pointer] != 0)
			//						pointer++; // Skip mime type
			//					pointer++; // skip picture type
			//					while (image[pointer] != 0)
			//						pointer++; // Skip description
			//					pointer++;
			//					while (image[pointer] == 0)
			//						pointer++;
			//					byte[] picture = new byte[(int) pictureLength - pointer + 1];
			//					for (int i = 0; pointer + i < image.length; i++) {
			//						picture[i] = image[pointer + i];
			//					}
			//
			//					ImageIcon imageIcon = new ImageIcon(picture);
			//
			//					if (width == -1 || height == -1) {
			//						return imageIcon;
			//					}
			//					int maxSize = (imageIcon.getIconWidth() > imageIcon.getIconHeight()) ? imageIcon.getIconWidth() : imageIcon.getIconHeight();
			//					int newWidth = (int) ((float) imageIcon.getIconWidth() / (float) maxSize * width);
			//					int newHeight = (int) ((float) imageIcon.getIconHeight() / (float) maxSize * height);
			//					BufferedImage resizedImage = ImageUtils.scaleImage(imageIcon.getImage(), newWidth, newHeight);
			//					if (resizedImage != null)
			//						return new ImageIcon(resizedImage);
			//					return null;
			//				}
			//				return null;
			//			}
			return null;
		} catch (Exception e) {
			return null;
		} finally {
			ClosingUtils.close(stream);
		}
	}

	/**
	 * Returns all pictures associated to an audio file
	 * 
	 * @param file
	 * @param width
	 *            Width in pixels or -1 to keep original width
	 * @param height
	 *            Height in pixels or -1 to keep original height
	 * @return
	 */
	public static ImageIcon[] getPicturesForFile(AudioFile file, int width, int height) {
		int size = 0;
		ImageIcon image = getInsidePicture(file, width, height);
		if (image != null)
			size++;
		size = size + file.getExternalPicturesCount();

		ImageIcon[] result = new ImageIcon[size];
		int firstExternalIndex = 0;
		if (image != null) {
			result[0] = image;
			firstExternalIndex++;
		}
		for (int i = firstExternalIndex; i < size; i++)
			result[i] = getExternalPicture(file, i, width, height);

		return result;
	}

	//	private static void savePicture(Image img, AudioFile file) {
	//		if (img != null && Kernel.getInstance().state.isSavePictureFromAudioScrobbler()) { // save image in folder of file
	//			String imageFileName = getFileNameForCover(file);
	//
	//			File imageFile = new File(imageFileName);
	//			if (!imageFile.exists()) {
	//				// Save picture
	//				try {
	//					PictureExporter.savePicture(img, imageFileName);
	//					// Add picture to songs of album
	//					RepositoryHandler.getInstance().addExternalPictureForAlbum(file.getArtist(), file.getAlbum(), imageFile);
	//
	//					// Update file properties panel
	//					ControllerProxy.getInstance().getFilePropertiesController().refreshPicture();
	//				} catch (IOException e) {
	//					logger.internalError(e);
	//				}
	//			}
	//		}
	//
	//	}

	/**
	 * Returns true if file represents a valid picture (jpg or png)
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isValidPicture(File file) {
		return file.getName().toUpperCase().endsWith("JPG") || file.getName().toUpperCase().endsWith("JPEG") || file.getName().toUpperCase().endsWith("PNG");
	}

	/**
	 * Saves and internal image of an audio file to a file
	 * 
	 * @param song
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void savePictureToFile(AudioFile song, File file) throws FileNotFoundException, IOException {
		ImageIcon image = getInsidePicture(song, -1, -1);
		PictureExporter.savePicture(image.getImage(), file.getAbsolutePath());
	}
}
