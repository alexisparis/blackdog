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

package net.sourceforge.atunes.kernel.controllers.fileProperties;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.panels.FilePropertiesPanel;
import net.sourceforge.atunes.kernel.controllers.model.PanelController;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;

/**
 * @author fleax
 * 
 */
public class FilePropertiesController extends PanelController<FilePropertiesPanel> {

	AudioFile currentFile;

	public FilePropertiesController(FilePropertiesPanel panelControlled) {
		super(panelControlled);
		addBindings();
		addStateBindings();
	}

	@Override
	protected void addBindings() {
		panelControlled.getPictureLabel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				VisualHandler.getInstance().showImageDialog(currentFile);
			}
		});
	}

	@Override
	protected void addStateBindings() {
		// Nothing to do
	}

	public void clear() {
		currentFile = null;
		panelControlled.getPictureLabel().setIcon(null);
		panelControlled.getPictureLabel().setVisible(false);
		panelControlled.getSongPanel().setVisible(false);
	}

	void fillFileProperties() {
		if (currentFile != null) {
			panelControlled.getBitrateLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("BITRATE"), ":</b>    ", currentFile.getBitrate(), " Kbps"));
			panelControlled.getFrequencyLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("FREQUENCY"), ":</b>    ", currentFile.getFrequency(), " Hz"));
		} else {
			panelControlled.getBitrateLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("BITRATE"), ":</b>    "));
			panelControlled.getFrequencyLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("FREQUENCY"), ":</b>    "));
		}

	}

	void fillPicture() {
		new SwingWorker<ImageIcon, Void>() {
			@Override
			protected ImageIcon doInBackground() throws Exception {
				return AudioFilePictureUtils.getImageForAudioFile(currentFile, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT);
			}

			@Override
			protected void done() {
				try {
					if (get() != null) {
						panelControlled.getPictureLabel().setPreferredSize(new Dimension(get().getIconWidth(), get().getIconHeight()));
						panelControlled.getPictureLabel().setIcon(get());
						panelControlled.getPictureLabel().setVisible(true);
					} else {
						panelControlled.getPictureLabel().setIcon(null);
						panelControlled.getPictureLabel().setVisible(false);
					}
				} catch (InterruptedException e) {
					logger.internalError(e);
				} catch (ExecutionException e) {
					logger.internalError(e);
				}
			}
		}.execute();
	}

	void fillSongProperties() {
		if (currentFile != null && currentFile.getTag() != null) {
			long size = currentFile.length();
			// \u202D is orientation override to LTR. 
			panelControlled.getFileNameLabel().setText(
					StringUtils.getString("<html><b>", LanguageTool.getString("FILE"), ":</b>    ", "\u202D", currentFile.getUrl(), " (", StringUtils.fromByteToMegaOrGiga(size),
							") \u202C </html>"));

			panelControlled.getSongLabel().setText(
					StringUtils.getString("<html><b>", LanguageTool.getString("SONG"), ":</b>    ", currentFile.getTitleOrFileName(), " - ", currentFile.getArtist(), " - ",
							currentFile.getAlbum(), "\u202D (", TimeUtils.seconds2String(currentFile.getDuration()), ") \u202C </html>"));

			if (currentFile.getTag().getTrackNumber() > 0) {
				panelControlled.getTrackLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("TRACK"), ":</b>    ", currentFile.getTag().getTrackNumber()));
			} else
				panelControlled.getTrackLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("TRACK"), ":"));

			if (currentFile.getTag().getYear() >= 0)
				panelControlled.getYearLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("YEAR"), ":</b>    ", currentFile.getTag().getYear()));
			else
				panelControlled.getYearLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("YEAR"), ":"));

			panelControlled.getGenreLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("GENRE"), ":</b>    ", currentFile.getTag().getGenre()));

			// Favorite icons
			refreshFavoriteIcons();
		} else {
			panelControlled.getFileNameLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("FILE"), ":</b>    "));
			panelControlled.getSongLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("SONG"), ":</b>    "));
			panelControlled.getTrackLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("TRACK"), ":</b>    "));
			panelControlled.getYearLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("YEAR"), ":</b>    "));
			panelControlled.getGenreLabel().setText(StringUtils.getString("<html><b>", LanguageTool.getString("GENRE"), ":</b>    "));
		}
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}

	public void onlyShowPropertiesPanel() {
		currentFile = null;
		// Song properties
		fillSongProperties();
		// File Properties
		fillFileProperties();
		// Picture
		fillPicture();
		panelControlled.getSongPanel().setVisible(true);
	}

	public void refreshFavoriteIcons() {
		if (currentFile == null)
			return;
		boolean favorite = FavoritesHandler.getInstance().getFavoriteSongsInfo().containsValue(currentFile)
				|| FavoritesHandler.getInstance().getFavoriteArtistsInfo().containsKey(currentFile.getArtist())
				|| FavoritesHandler.getInstance().getFavoriteAlbumsInfo().containsKey(currentFile.getAlbum());

		panelControlled.getSongLabel().setIcon(favorite ? ImageLoader.FAVORITE : null);
	}

	public void refreshPicture() {
		fillPicture();
	}

	//TODO
	public void updateValues(AudioObject file) {
		logger.debug(LogCategories.CONTROLLER, new String[] { file.getUrl() });
		if (!(file instanceof AudioFile)) {
			clear();
			return;
		}
		currentFile = (AudioFile) file;
		// Song properties
		fillSongProperties();
		// File Properties
		fillFileProperties();
		// Picture
		fillPicture();
		panelControlled.getSongPanel().setVisible(true);
	}
}
