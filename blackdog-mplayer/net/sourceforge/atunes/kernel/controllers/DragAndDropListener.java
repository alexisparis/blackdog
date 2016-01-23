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

package net.sourceforge.atunes.kernel.controllers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.gui.model.TransferableList;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.handlers.DeviceHandler;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryLoader;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.log4j.Logger;

public class DragAndDropListener implements DropTargetListener {

	private static Logger logger = new Logger();

	DropTarget dropTarget;
	DropTarget dropTarget2;

	public DragAndDropListener() {
		dropTarget = new DropTarget(ControllerProxy.getInstance().getPlayListController().getMainPlayListScrollPane(), this);
		dropTarget2 = new DropTarget(ControllerProxy.getInstance().getPlayListController().getMainPlayListTable(), this);
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// Nothing to do
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// Nothing to do
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// Nothing to do
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		Transferable transferable = dtde.getTransferable();
		DataFlavor aTunesFlavorAccepted = new TransferableList<Object>(null).getTransferDataFlavors()[0];
		if (transferable.isDataFlavorSupported(aTunesFlavorAccepted)) {
			dtde.acceptDrop(DnDConstants.ACTION_COPY);
			try {
				List<AudioObject> songsToAdd = new ArrayList<AudioObject>();
				List<?> listOfObjectsDragged = (List<?>) transferable.getTransferData(aTunesFlavorAccepted);
				for (int i = 0; i < listOfObjectsDragged.size(); i++) {
					Object obj = listOfObjectsDragged.get(i);
					if (obj instanceof TreeObject) {
						TreeObject object = (TreeObject) obj;
						songsToAdd.addAll(object.getAudioObjects());
					} else if (obj instanceof Integer) {
						Integer row = (Integer) obj;
						songsToAdd.add(ControllerProxy.getInstance().getNavigationController().getSongInNavigationTable(row));
					} else if (obj instanceof String) { // The root of the tree
						String str = (String) obj;
						if (str.equals(LanguageTool.getString("REPOSITORY")) || str.startsWith(LanguageTool.getString("FOLDERS")))
							songsToAdd.addAll(RepositoryHandler.getInstance().getSongs());
						else if (str.equals(LanguageTool.getString("FAVORITES")))
							songsToAdd.addAll(FavoritesHandler.getInstance().getFavoriteSongs());
						else if (str.equals(LanguageTool.getString("ARTISTS"))) {
							songsToAdd.addAll(RepositoryHandler.getInstance().getSongsForArtists(FavoritesHandler.getInstance().getFavoriteArtistsInfo()));
						} else if (str.equals(LanguageTool.getString("ALBUMS"))) {
							songsToAdd.addAll(RepositoryHandler.getInstance().getSongsForAlbums(FavoritesHandler.getInstance().getFavoriteAlbumsInfo()));
						} else if (str.equals(LanguageTool.getString("SONGS"))) {
							songsToAdd.addAll(FavoritesHandler.getInstance().getFavoriteSongsInfo().values());
						} else if (str.equals(LanguageTool.getString("DEVICE"))) {
							songsToAdd.addAll(DeviceHandler.getInstance().getDeviceSongs());
						}
					}
				}
				List<AudioObject> songsSorted = RepositoryHandler.getInstance().sort(songsToAdd, ControllerProxy.getInstance().getNavigationController().getState().getSortType());
				PlayListHandler.getInstance().addToPlayList(songsSorted);
			} catch (Exception e) {
				logger.internalError(e);
			}

			dtde.getDropTargetContext().dropComplete(true);
		} else {
			try {
				DataFlavor fileListFlavorAccepted = new DataFlavor("application/x-java-file-list; class=java.util.List");
				if (transferable.isDataFlavorSupported(fileListFlavorAccepted)) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY);
					List<?> list = (List<?>) transferable.getTransferData(fileListFlavorAccepted);
					List<AudioObject> filesToAdd = new ArrayList<AudioObject>();
					for (int i = 0; i < list.size(); i++) {
						File f = (File) list.get(i);
						if (f.isDirectory()) {
							filesToAdd.addAll(RepositoryLoader.getSongsForDir(f));
						} else if (AudioFile.isValidAudioFile(f)) {
							AudioFile song = new AudioFile(f.getAbsolutePath());
							filesToAdd.add(song);
						} else if (f.getName().toLowerCase().endsWith("m3u")) {
							filesToAdd.addAll(PlayListIO.getFilesFromList(f));
						}
					}
					PlayListHandler.getInstance().addToPlayList(filesToAdd);
					dtde.getDropTargetContext().dropComplete(true);
				} else
					dtde.rejectDrop();
			} catch (Exception e) {
				logger.internalError(e);
			}
		}
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// Nothing to do
	}
}
