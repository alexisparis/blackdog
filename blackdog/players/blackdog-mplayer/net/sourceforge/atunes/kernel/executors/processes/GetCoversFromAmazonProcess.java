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

package net.sourceforge.atunes.kernel.executors.processes;

import java.awt.Image;
import java.io.IOException;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.dialogs.IndeterminateProgressDialog;
import net.sourceforge.atunes.kernel.controllers.coverNavigator.CoverNavigatorController;
import net.sourceforge.atunes.kernel.modules.amazon.AmazonAlbum;
import net.sourceforge.atunes.kernel.modules.amazon.AmazonService;
import net.sourceforge.atunes.kernel.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.kernel.utils.PictureExporter;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;

public class GetCoversFromAmazonProcess implements Runnable {

	private Artist artist;
	private CoverNavigatorController coverNavigatorController;
	private IndeterminateProgressDialog progressDialog;

	public GetCoversFromAmazonProcess(Artist artist, IndeterminateProgressDialog progressDialog, CoverNavigatorController coverNavigatorController) {
		this.artist = artist;
		this.coverNavigatorController = coverNavigatorController;
		this.progressDialog = progressDialog;
	}

	@Override
	public void run() {
		for (Album album : artist.getAlbums().values()) {
			if (!album.hasCoverDownloaded()) {
				AmazonAlbum amazonAlbum = AmazonService.getAlbum(artist.getName(), album.getName());
				if (amazonAlbum != null) {
					Image image = AmazonService.getAmazonImage(amazonAlbum.getImageURL());
					try {
						PictureExporter.savePicture(image, AudioFilePictureUtils.getFileNameForCover(album.getAudioFiles().get(0)));
					} catch (IOException e1) {
						// Don't save image
					}
				}
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Fire selection event to refresh covers
				coverNavigatorController.updateCovers();
			}
		});
		progressDialog.setVisible(false);
	}

}
