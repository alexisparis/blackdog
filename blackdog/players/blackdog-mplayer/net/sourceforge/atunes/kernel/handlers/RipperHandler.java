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

package net.sourceforge.atunes.kernel.handlers;

import java.awt.Cursor;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.views.dialogs.IndeterminateProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.RipCdDialog;
import net.sourceforge.atunes.gui.views.dialogs.RipperProgressDialog;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.amazon.AmazonAlbum;
import net.sourceforge.atunes.kernel.modules.amazon.AmazonDisc;
import net.sourceforge.atunes.kernel.modules.amazon.AmazonService;
import net.sourceforge.atunes.kernel.modules.cdripper.CdRipper;
import net.sourceforge.atunes.kernel.modules.cdripper.ProgressListener;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.Cdda2wav;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.NoCdListener;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.kernel.modules.cdripper.encoders.Encoder;
import net.sourceforge.atunes.kernel.modules.cdripper.encoders.FlacEncoder;
import net.sourceforge.atunes.kernel.modules.cdripper.encoders.LameEncoder;
import net.sourceforge.atunes.kernel.modules.cdripper.encoders.Mp4Encoder;
import net.sourceforge.atunes.kernel.modules.cdripper.encoders.OggEncoder;
import net.sourceforge.atunes.kernel.modules.cdripper.encoders.WavEncoder;
import net.sourceforge.atunes.kernel.utils.PictureExporter;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class RipperHandler {

	private static RipperHandler instance = new RipperHandler();

	private CdRipper ripper;
	private volatile boolean interrupted;
	private boolean folderCreated;
	private Logger logger = new Logger();

	private String albumCoverURL;
	private String quality;
	private String encoder;
	private String fileNamePattern;

	private RipperHandler() {
		// Nothing to do
	}

	public static RipperHandler getInstance() {
		return instance;
	}

	/**
	 * Add files to existing repository if destiny folder is in repository. This
	 * method is used after an import operation
	 */
	private void addFilesToRepositoryAndRefresh(List<File> files, File folder) {
		if (RepositoryHandler.getInstance().isRepository(folder)) {
			VisualHandler.getInstance().setCenterStatusBarText(StringUtils.getString(LanguageTool.getString("REFRESHING"), "..."));
			VisualHandler.getInstance().showProgressBar(true);
			RepositoryHandler.getInstance().addAndRefresh(files, folder);
		}
	}

	public void cancelProcess() {
		interrupted = true;
		ripper.stop();
		logger.info(LogCategories.RIPPER, "Process cancelled");
	}

	public void fillSongsFromAmazon(final String artist, final String album) {
		VisualHandler.getInstance().getRipCdDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		VisualHandler.getInstance().getRipCdDialog().getAmazonButton().setEnabled(false);
		new SwingWorker<AmazonAlbum, Void>() {
			@Override
			protected AmazonAlbum doInBackground() throws Exception {
				return AmazonService.getAlbum(artist, album);
			}

			@Override
			protected void done() {
				try {
					if (get() != null) {
						albumCoverURL = get().getImageURL();
						List<String> tracks = new ArrayList<String>();
						for (AmazonDisc disc : get().getDiscs()) {
							tracks.addAll(disc.getTracks());
						}
						VisualHandler.getInstance().getRipCdDialog().updateTrackNames(tracks);
					}
				} catch (InterruptedException e) {
					logger.internalError(e);
				} catch (ExecutionException e) {
					logger.error(LogCategories.RIPPER, e);
				} finally {
					VisualHandler.getInstance().getRipCdDialog().setCursor(Cursor.getDefaultCursor());
					VisualHandler.getInstance().getRipCdDialog().getAmazonButton().setEnabled(true);
				}
			}
		}.execute();
	}

	/**
	 * Gets encoder which was used for ripping CD's
	 * 
	 * @return Return the encoder used the previous time
	 */
	public String getEncoder() {
		if (encoder != null)
			return encoder;
		return "OGG";
	}

	public String getEncoderQuality() {
		return quality;
	}

	/**
	 * Returns the filename pattern which is used
	 * 
	 * @return The filename pattern
	 */
	public String getFileNamePattern() {
		return fileNamePattern;
	}

	/**
	 * Test for avaible encoders and returns an List of the found encoders. In
	 * order to keep the same order as previously, we begin with ogg then mp3
	 * and finally with flac.
	 */
	public List<String> getInstalledEncoders() {
		List<String> avaibleEncoders = new ArrayList<String>();
		if (OggEncoder.testTool() == true)
			avaibleEncoders.add("OGG");
		if (LameEncoder.testTool() == true)
			avaibleEncoders.add("MP3");
		if (Mp4Encoder.testTool() == true)
			avaibleEncoders.add("MP4");
		if (FlacEncoder.testTool() == true)
			avaibleEncoders.add("FLAC");
		avaibleEncoders.add("WAV");
		return avaibleEncoders;
	}

	/**
	 * Controlls the import process for ripping audio CD's
	 * 
	 * @param folder
	 *            The folder where the files should be saved
	 * @param artist
	 *            Artist name (whole CD)
	 * @param album
	 *            Album name
	 * @param year
	 *            Release year
	 * @param genre
	 *            Album genre
	 * @param tracks
	 *            List of the track numbers
	 * @param trckNames
	 *            List of the track names
	 * @param format
	 *            Format in which the files should converted
	 * @param quality1
	 *            Quality setting to be used
	 */
	private void importSongs(String folder, final String artist, final String album, final int year, final String genre, final List<Integer> tracks, final List<String> trckNames, final List<String> artistNames, final List<String> composerNames, final String format, final String quality1) {
		// Disable import cd option in menu
		ControllerProxy.getInstance().getMenuController().setRipCDEnabled(false);

		final File folderFile = new File(folder);
		if (!folderFile.exists()) {
			if (folderFile.mkdirs())
				folderCreated = true;
			else {
				logger.error(LogCategories.RIPPER, "Folder could not be created");
				return;
			}
		}

		// Prepares commands for the encoder
		Encoder encoder1;

		// Ogg encoder
		if (format.equals("OGG"))
			encoder1 = new OggEncoder();
		// lame (mp3) encoder
		else if (format.equals("MP3"))
			encoder1 = new LameEncoder();
		// flac encoder
		else if (format.equals("FLAC"))
			encoder1 = new FlacEncoder();
		else if (format.equals("MP4"))
			encoder1 = new Mp4Encoder();
		else
			encoder1 = new WavEncoder();
		encoder1.setQuality(quality1);
		ripper.setEncoder(encoder1);
		ripper.setArtist(artist);
		ripper.setAlbum(album);
		ripper.setYear(year);
		ripper.setGenre(genre);
		ripper.setFileNamePattern(fileNamePattern);

		final RipperProgressDialog dialog = VisualHandler.getInstance().getRipperProgressDialog();

		// Get image from amazon if necessary
		if (albumCoverURL != null) {
			Image cover = AmazonService.getAmazonImage(albumCoverURL);
			dialog.setCover(cover);
			savePicture(cover, folderFile, artist, album);
		}

		dialog.setArtistAndAlbum(artist, album);

		dialog.setTotalProgressBarLimits(0, tracks.size());
		dialog.setDecodeProgressBarLimits(0, 100);
		dialog.setEncodeProgressBarLimits(0, 100);

		final List<File> filesImported = new ArrayList<File>();

		ripper.setDecoderListener(new ProgressListener() {
			@Override
			public void notifyFileFinished(File f) {
				// Nothing to do
			}

			@Override
			public void notifyProgress(int percent) {
				dialog.setDecodeProgressValue(percent);
				dialog.setDecodeProgressValue(StringUtils.getString(percent, "%"));
			}
		});

		ripper.setEncoderListener(new ProgressListener() {
			@Override
			public void notifyFileFinished(File f) {
				// Nothing to do
			}

			@Override
			public void notifyProgress(int percent) {
				dialog.setEncodeProgressValue(percent);
				dialog.setEncodeProgressValue(StringUtils.getString(percent, "%"));
			}
		});

		ripper.setTotalProgressListener(new ProgressListener() {
			@Override
			public void notifyFileFinished(File f) {
				filesImported.add(f);
			}

			@Override
			public void notifyProgress(int value) {
				dialog.setTotalProgressValue(value);
				dialog.setTotalProgressValue(StringUtils.getString(value, " / ", tracks.size()));
				dialog.setDecodeProgressValue(0);
				dialog.setDecodeProgressValue(StringUtils.getString(0, "%"));
				dialog.setEncodeProgressValue(0);
				dialog.setEncodeProgressValue(StringUtils.getString(0, "%"));
			}
		});

		Thread t = new Thread() {
			@Override
			public void run() {
				ripper.ripTracks(tracks, trckNames, folderFile, artistNames, composerNames);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						notifyFinishImport(filesImported, folderFile);
						// Enable import cd option in menu
						ControllerProxy.getInstance().getMenuController().setRipCDEnabled(true);
					}
				});
			}
		};

		t.start();
		dialog.setVisible(true);
	}

	public void notifyFinishImport(final List<File> filesImported, final File folder) {
		VisualHandler.getInstance().getRipperProgressDialog().setVisible(false);
		if (interrupted) { // If process is interrupted delete all imported files
			Thread deleter = new Thread() {
				@Override
				public void run() {
					for (File f : filesImported)
						f.delete();

					// Wait two seconds to assure filesImported are deleted
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						logger.internalError(e);
					}
					if (folderCreated)
						folder.delete();
				}
			};
			deleter.start();
		} else
			addFilesToRepositoryAndRefresh(filesImported, folder);
	}

	private void savePicture(Image image, File path, String artist, String album) {
		String imageFileName = StringUtils.getString(path.getAbsolutePath(), SystemProperties.fileSeparator, artist, "_", album, "_Cover.png");
		try {
			PictureExporter.savePicture(image, imageFileName);
		} catch (IOException e) {
			logger.internalError(e);
		}
	}

	public void setEncoder(String encoder) {
		this.encoder = encoder;
	}

	public void setEncoderQuality(String quality) {
		this.quality = quality;
	}

	/**
	 * Sets the used filename pattern
	 * 
	 * @param fileNamePattern
	 *            The filename pattern used
	 */
	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

	public void startCdRipper() {
		interrupted = false;
		final RipCdDialog dialog = VisualHandler.getInstance().getRipCdDialog();
		final IndeterminateProgressDialog waitDialog = VisualHandler.getInstance().getIndeterminateProgressDialog();

		SwingWorker<CDInfo, Void> getCdInfoAndStartRipping = new SwingWorker<CDInfo, Void>() {
			@Override
			protected CDInfo doInBackground() throws Exception {
				if (!testTools())
					return null;
				ripper = new CdRipper();
				ripper.setNoCdListener(new NoCdListener() {
					@Override
					public void noCd() {
						logger.error(LogCategories.RIPPER, "No cd inserted");
						interrupted = true;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								waitDialog.setVisible(false);
								VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("NO_CD_INSERTED"));
							}
						});
					}
				});
				return ripper.getCDInfo();
			}

			@Override
			protected void done() {
				waitDialog.setVisible(false);
				CDInfo cdInfo;
				try {
					cdInfo = get();
					if (cdInfo != null) {
						ControllerProxy.getInstance().getRipCdDialogController().showCdInfo(cdInfo, RepositoryHandler.getInstance().getPathForNewSongsRipped());
						if (!ControllerProxy.getInstance().getRipCdDialogController().isCancelled()) {
							String artist = ControllerProxy.getInstance().getRipCdDialogController().getArtist();
							String album = ControllerProxy.getInstance().getRipCdDialogController().getAlbum();
							int year = ControllerProxy.getInstance().getRipCdDialogController().getYear();
							String genre = ControllerProxy.getInstance().getRipCdDialogController().getGenre();
							String folder = ControllerProxy.getInstance().getRipCdDialogController().getFolder();
							List<Integer> tracks = dialog.getTracksSelected();
							List<String> trckNames = dialog.getTrackNames();
							List<String> artistNames = dialog.getArtistNames();
							List<String> composerNames = dialog.getComposerNames();
							setEncoder(dialog.getFormat().getSelectedItem().toString());
							setEncoderQuality(dialog.getQuality());
							setFileNamePattern(dialog.getFileNamePattern());
							ControllerProxy.getInstance().getRipCdDialogController().setEncoderSettingChanged(false);
							importSongs(folder, artist, album, year, genre, tracks, trckNames, artistNames, composerNames, dialog.getFormat().getSelectedItem().toString(), dialog
									.getQuality());
						} else {
							setEncoder(dialog.getFormat().getSelectedItem().toString());
							setEncoderQuality(dialog.getQuality());
							setFileNamePattern(dialog.getFileNamePattern());
							ControllerProxy.getInstance().getRipCdDialogController().setEncoderSettingChanged(false);
						}
					}
				} catch (InterruptedException e) {
					VisualHandler.getInstance().getRipCdDialog().setVisible(false);
					logger.internalError(e);
				} catch (ExecutionException e) {
					VisualHandler.getInstance().getRipCdDialog().setVisible(false);
					logger.internalError(e);
				}
			}
		};
		getCdInfoAndStartRipping.execute();
		waitDialog.setVisible(true);
	}

	/**
	 * Test the presence of cdda2wav/icedax. Calls the test function from
	 * Cdda2wav.java
	 * 
	 * @return Returns true if cdda2wav/icedax is present, false otherwise
	 */
	private boolean testTools() {
		if (!Cdda2wav.testTool()) {
			logger.error(LogCategories.RIPPER, "Error testing \"cdda2wav\". Check program is installed");
			VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("CDDA2WAV_NOT_FOUND"));
			return false;
		}
		return true;
	}
}
