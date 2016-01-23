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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListCommonOps;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.ClosingUtils;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;

public class MultipleInstancesHandler {

	/**
	 * This class is responsible of listening to server socket and accept
	 * connections from "slave" aTunes instances.
	 */
	static class SocketListener extends Thread {

		private ServerSocket socket;
		private SongsQueue queue;

		SocketListener(ServerSocket serverSocket, SongsQueue queue) {
			super();
			socket = serverSocket;
			this.queue = queue;
		}

		@Override
		public void run() {
			Socket s = null;
			BufferedReader br = null;
			try {
				while (true) {
					s = socket.accept();
					// Once a connection arrives, read args
					br = new BufferedReader(new InputStreamReader(s.getInputStream()));
					String str;
					while ((str = br.readLine()) != null) {
						System.out.println(StringUtils.getString("INFO: Receiving arg from other instance: \"", str, "\""));
						if (PlayListIO.isValidPlayList(str)) {
							List<String> songs = PlayListIO.read(new File(str));
							List<AudioObject> files = PlayListIO.getAudioObjectsFromFileNamesList(songs);
							for (AudioObject file : files)
								queue.addSong(file);
						} else
							queue.addSong(RepositoryHandler.getInstance().getFileIfLoaded(str));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				ClosingUtils.close(br);
				ClosingUtils.close(s);
			}
		}
	}

	/**
	 * This class is responsible of create a queue of songs to be added. When
	 * opening multiple files, OS launch a "slave" aTunes for every file, so
	 * this queue adds songs in the order connections are made, and when no more
	 * connections are received, then add to playlist
	 */
	static class SongsQueue extends Thread {

		private List<AudioObject> songsQueue;

		private long lastSongAdded = 0;

		SongsQueue() {
			songsQueue = new ArrayList<AudioObject>();
		}

		public void addSong(AudioObject song) {
			songsQueue.add(song);
			lastSongAdded = System.currentTimeMillis();
		}

		@Override
		public void run() {
			try {
				while (true) {
					if (!songsQueue.isEmpty() && lastSongAdded < System.currentTimeMillis() - 1000) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								PlayListCommonOps.addToPlayListAndPlay(songsQueue);
							}
						});
						songsQueue = new ArrayList<AudioObject>();
					} else {
						Thread.sleep(1000);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private static MultipleInstancesHandler instance = new MultipleInstancesHandler();

	private ServerSocket serverSocket;

	private MultipleInstancesHandler() {
	}

	public static MultipleInstancesHandler getInstance() {
		return instance;
	}

	/**
	 * Called when aTunes finishes
	 */
	public void finish() {
		if (serverSocket != null)
			ClosingUtils.close(serverSocket);
	}

	/**
	 * Tries to open a server socket to listen to other aTunes instances
	 * 
	 * @return true if server socket could be opened
	 */
	public boolean isFirstInstance() {
		try {
			// Open server socket
			serverSocket = new ServerSocket(Constants.MULTIPLE_INSTANCES_SOCKET);
			System.out.println(StringUtils.getString("INFO: aTunes is listening for other instances on port ", Constants.MULTIPLE_INSTANCES_SOCKET));

			// Initialize songs queue
			SongsQueue songsQueue = new SongsQueue();

			// Initialize socket listener
			SocketListener listener = new SocketListener(serverSocket, songsQueue);

			// Start threads
			songsQueue.start();
			listener.start();

			// Server socket could be opened, so this instance is a "master"
			return true;
		} catch (Exception e) {
			// Server socket could not be opened, so this instance is a "slave"
			System.out.println("INFO: Another aTunes instance is running");
			return false;
		}
	}

	/**
	 * Opens a client socket and sends arguments to "master"
	 * 
	 * @param args
	 */
	public void sendArgumentsToFirstInstance(List<String> args) {
		Socket clientSocket = null;
		PrintWriter output = null;
		try {
			// Open client socket to communicate with "master"
			clientSocket = new Socket("localhost", Constants.MULTIPLE_INSTANCES_SOCKET);
			output = new PrintWriter(clientSocket.getOutputStream(), true);
			for (String arg : args) {
				if (AudioFile.isValidAudioFile(arg) || PlayListIO.isValidPlayList(arg)) {
					// Send args: audio files or play lists
					System.out.println(StringUtils.getString("INFO: Sending arg \"", arg, "\""));
					output.write(arg);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ClosingUtils.close(output);
			ClosingUtils.close(clientSocket);
		}
	}
}
