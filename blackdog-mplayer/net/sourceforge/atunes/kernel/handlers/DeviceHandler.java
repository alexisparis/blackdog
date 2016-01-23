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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.PlayListTableModel;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.dialogs.CopyProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.FileSelectionDialog;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationControllerViews;
import net.sourceforge.atunes.kernel.executors.BackgroundExecutor;
import net.sourceforge.atunes.kernel.executors.processes.ExportFilesProcess;
import net.sourceforge.atunes.kernel.modules.device.DeviceConnectionListener;
import net.sourceforge.atunes.kernel.modules.device.DeviceConnectionMonitor;
import net.sourceforge.atunes.kernel.modules.device.DeviceCopyFinishListener;
import net.sourceforge.atunes.kernel.modules.device.DeviceDisconnectionListener;
import net.sourceforge.atunes.kernel.modules.device.DeviceDisconnectionMonitor;
import net.sourceforge.atunes.kernel.modules.repository.LoaderListener;
import net.sourceforge.atunes.kernel.modules.repository.Repository;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryLoader;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class DeviceHandler implements LoaderListener, DeviceCopyFinishListener, DeviceConnectionListener, DeviceDisconnectionListener {

	private static DeviceHandler instance = new DeviceHandler();

	private Logger logger = new Logger();

	private Repository deviceRepository;
	private Repository tempDeviceRepository;

	private RepositoryLoader currentLoader;

	private File devicePath;

	private DeviceHandler() {
	}

	public static DeviceHandler getInstance() {
		return instance;
	}

	/**
	 * Connect device
	 * 
	 */
	public void connectDevice() {
		FileSelectionDialog dialog = VisualHandler.getInstance().getFileSelectionDialog(true);
		dialog.setTitle(LanguageTool.getString("SELECT_DEVICE"));
		dialog.startDialog();
		if (!dialog.isCanceled()) {
			File dir = dialog.getSelectedDir();

			VisualHandler.getInstance().showProgressBar(true);
			this.retrieveDevice(dir);
		}
	}

	/**
	 * Copy files to mp3 device
	 * 
	 * @param files
	 *            Files to be written to a mp3 device
	 */
	public void copyFilesToDevice(List<AudioFile> files) {
		// Get size of files
		long size = 0;
		for (AudioFile file : files) {
			size = size + file.length();
		}
		// Get free space in device
		long deviceFreeSpace = deviceRepository.getFolders().get(0).getFreeSpace();

		if (size > deviceFreeSpace) {
			VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("NOT_ENOUGH_SPACE_ON_DEVICE"));
			return;
		}

		CopyProgressDialog exportProgressDialog = VisualHandler.getInstance().getExportProgressDialog();
		exportProgressDialog.getTotalFilesLabel().setText(StringUtils.getString(files.size()));
		exportProgressDialog.getProgressBar().setMaximum(files.size());
		exportProgressDialog.setVisible(true);
		final ExportFilesProcess process = BackgroundExecutor.exportFiles(files, ExportFilesProcess.FULL_STRUCTURE, null, deviceRepository.getFolders().get(0).getAbsolutePath(),
				true);
		process.addFinishListener(this);

		exportProgressDialog.getCancelButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				process.notifyCancel();
			}
		});
	}

	/**
	 * Called when a device monitor detects a device connected
	 */
	@Override
	public void deviceConnected(String location) {
		if (VisualHandler.getInstance().showConfirmationDialog(LanguageTool.getString("DEVICE_CONNECT_CONFIRMATION"), LanguageTool.getString("DEVICE_DETECTED")) == JOptionPane.OK_OPTION) {
			VisualHandler.getInstance().showProgressBar(true);
			this.retrieveDevice(new File(location));
		}
	}

	/**
	 * Called when a device monitor detects a device disconnection
	 */
	@Override
	public void deviceDisconnected(String location) {
		VisualHandler.getInstance().showMessage(LanguageTool.getString("DEVICE_DISCONNECTION_DETECTED"));
		disconnectDevice();

		// Start device connection monitor
		DeviceConnectionMonitor.startMonitor();
	}

	/**
	 * Disconnect device
	 * 
	 */
	public void disconnectDevice() {
		VisualHandler.getInstance().showIconOnStatusBar(null, null);

		List<Integer> songsToRemove = new ArrayList<Integer>();
		for (int i = 0; i < PlayerHandler.getInstance().getCurrentPlayList().size(); i++) {
			if (PlayerHandler.getInstance().getCurrentPlayList().get(i) instanceof AudioFile) {
				AudioFile song = (AudioFile) PlayerHandler.getInstance().getCurrentPlayList().get(i);
				if (song.getPath().startsWith(this.getDeviceRepository().getFolders().get(0).getPath()))
					songsToRemove.add(i);
			}
		}
		int[] indexes = new int[songsToRemove.size()];
		for (int i = 0; i < songsToRemove.size(); i++)
			indexes[i] = songsToRemove.get(i);

		if (indexes.length > 0) {
			PlayListTable table = VisualHandler.getInstance().getPlayListTable();
			((PlayListTableModel) table.getModel()).removeSongs(indexes);
			VisualHandler.getInstance().getPlayListPanel().getPlayListTable().getSelectionModel().setSelectionInterval(-1, -1);
			PlayListHandler.getInstance().removeSongs(indexes);
		}

		deviceRepository = null;
		notifyFinishRefresh(null);
		logger.info(LogCategories.REPOSITORY, "Device disconnected");
		ControllerProxy.getInstance().getMenuController().setDeviceConnected(false);
	}

	public Map<String, Artist> getDeviceArtistAndAlbumStructure() {
		if (deviceRepository != null)
			return deviceRepository.getStructure().getTreeStructure();
		return new HashMap<String, Artist>();
	}

	/**
	 * Returns a string with total and free space for a dir
	 * 
	 * @param dir
	 * @return
	 */
	private String getDeviceData() {
		if (deviceRepository != null) {
			int songs = deviceRepository.countFiles();
			File dir = deviceRepository.getFolders().get(0);
			return StringUtils.getString(Integer.toString(songs), " ", LanguageTool.getString("SONGS"), "  (", LanguageTool.getString("FREE_SPACE"), ": ", StringUtils
					.fromByteToMegaOrGiga(dir.getFreeSpace()), ")");
		}
		return null;
	}

	public Map<String, Folder> getDeviceFolderStructure() {
		if (deviceRepository != null)
			return deviceRepository.getStructure().getFolderStructure();
		return new HashMap<String, Folder>();
	}

	public Repository getDeviceRepository() {
		return deviceRepository;
	}

	public List<AudioFile> getDeviceSongs() {
		if (deviceRepository != null)
			return deviceRepository.getFilesList();
		return new ArrayList<AudioFile>();
	}

	public boolean isDeviceConnected() {
		return deviceRepository != null;
	}

	/**
	 * Checks if given file is in the device path
	 * 
	 * @param path
	 *            Absolute path of the file
	 * @return true if file is in device, false otherwise
	 */
	public boolean isDevicePath(String path) {
		if (path.contains(devicePath.toString()))
			return true;
		return false;
	}

	/**
	 * Called when finish copy to device
	 */
	@Override
	public void notifyCopyFinish() {
		// Force device refresh
		refreshDevice();
	}

	@Override
	public void notifyCurrentPath(String path) {
	}

	private void notifyDeviceReload(RepositoryLoader loader) {
		if (ControllerProxy.getInstance().getNavigationController() != null) {
			VisualHandler.getInstance().showProgressBar(false);
			ControllerProxy.getInstance().getNavigationController().notifyDeviceReload();
			ControllerProxy.getInstance().getMenuController().setDeviceConnected(loader != null);

			// Update status bar info
			if (loader != null)
				VisualHandler.getInstance().showIconOnStatusBar(ImageLoader.DEVICE, getDeviceData());
			else
				VisualHandler.getInstance().showIconOnStatusBar(null, null);

			if (loader != null)
				// Switch to device view in navigator
				ControllerProxy.getInstance().getNavigationController().setNavigationView(NavigationControllerViews.DEVICE_VIEW);
		}
	}

	@Override
	public void notifyFileLoaded() {
	}

	@Override
	public void notifyFilesInRepository(int files) {
	}

	@Override
	public void notifyFinishRead(RepositoryLoader loader) {
		deviceRepository = loader.getRepository();
		logger.info(LogCategories.REPOSITORY, "Device read");
		notifyDeviceReload(loader);
		// Start device disconnection monitor
		DeviceDisconnectionMonitor.startMonitor();
		DeviceDisconnectionMonitor.addListener(this);
	}

	@Override
	public void notifyFinishRefresh(RepositoryLoader loader) {
		if (loader != null)
			tempDeviceRepository = loader.getRepository();
		deviceRepository = tempDeviceRepository;
		tempDeviceRepository = null;
		notifyDeviceReload(loader);
	}

	@Override
	public void notifyRemainingTime(long time) {
	}

	public void refreshDevice() {
		VisualHandler.getInstance().showProgressBar(true);
		logger.info(LogCategories.REPOSITORY, "Refreshing device");
		currentLoader = new RepositoryLoader(deviceRepository.getFolders(), true);
		currentLoader.addRepositoryLoaderListener(this);
		currentLoader.setOldRepository(deviceRepository);
		currentLoader.start();
	}

	public void retrieveDevice(File path) {
		logger.info(LogCategories.REPOSITORY, StringUtils.getString("Reading device mounted on ", path));
		List<File> folders = new ArrayList<File>();
		folders.add(path);
		currentLoader = new RepositoryLoader(folders, false);
		currentLoader.addRepositoryLoaderListener(this);
		setDevicePath(path);
		currentLoader.start();
	}

	/**
	 * Set the device path
	 * 
	 * @param path
	 *            Device path (absolute)
	 */
	private void setDevicePath(File path) {
		devicePath = path;
	}

}
