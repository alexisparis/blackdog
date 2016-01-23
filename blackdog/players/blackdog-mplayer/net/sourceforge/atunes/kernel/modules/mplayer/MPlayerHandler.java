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

package net.sourceforge.atunes.kernel.modules.mplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable.PlayState;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.handlers.PlayListHandler;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.ClosingUtils;
import net.sourceforge.atunes.kernel.utils.NativeFunctionsUtils;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.kernel.utils.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;

public class MPlayerHandler extends PlayerHandler {

	/**
	 * Command to be executed on Linux systems to launch mplayer. Mplayer should
	 * be in $PATH
	 */
	private static final String LINUX_COMMAND = "mplayer";

	/**
	 * Command to be executed on Windows systems to launch mplayer. Mplayer is
	 * in "win_tools" dir, inside aTunes package
	 */
	private static final String WIN_COMMAND = "win_tools/mplayer.exe";

	/**
	 * Command to be executed on Mac systems to launch mplayer.
	 */
	private static final String MACOS_COMMAND = "mplayer -ao macosx";

	/**
	 * Command to be executed on Solaris systems to launch mplayer. Note the
	 * workaround with the options - Java6 on Solaris Express appears to require
	 * these options added separately.
	 */
	private static final String SOLARIS_COMMAND = "mplayer";
	private static final String SOLARISOPTAO = "-ao";
	private static final String SOLARISOPTTYPE = "sun";

	// Arguments for mplayer

	/**
	 * Argument to not display more information than needed.
	 */
	private static final String QUIET = "-quiet";

	/**
	 * Argument to control mplayer through commands
	 */
	private static final String SLAVE = "-slave";

	/**
	 * Argument to pass mplayer a play list
	 */
	private static final String PLAYLIST = "-playlist";

	/**
	 * Arguments to filter audio output
	 */
	private static final String AUDIO_FILTER = "-af";
	private static final String VOLUME_NORM = "volnorm";
	private static final String KARAOKE = "karaoke";
	private static final String EQUALIZER = "equalizer=";

	private Process process;

	public MPlayerHandler() {
		super();
		if (!testMPlayerAvailability()) {
			notifyPlayerError(new Exception(LanguageTool.getString("MPLAYER_NOT_FOUND")));
		} else {
			MPlayerPositionThread positionThread = new MPlayerPositionThread(this);
			positionThread.start();
		}
	}

	/**
	 * Tests if mplayer is present
	 * 
	 * @return true mplayer is present, false mplayer is missing
	 */
	private static boolean testMPlayerAvailability() {
		if (SystemProperties.SYSTEM != OperatingSystem.WINDOWS) {
			BufferedReader stdInput = null;
			try {
				Process p = new ProcessBuilder(LINUX_COMMAND).start();
				stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while (stdInput.readLine() != null) {
					// Nothing to do
				}

				int code = p.waitFor();
				if (code != 0) {
					return false;
				}
			} catch (Exception e) {
				return false;
			} finally {
				ClosingUtils.close(stdInput);
			}
		}
		return true;
	}

	/**
	 * Stops the player
	 */
	@Override
	public void finish() {
		stop();
		logger.info(LogCategories.PLAYER, "Stopping player");
	}

	// _____________________________________________________________________
	// #####################################################################
	
	/**
	 * Returns a mplayer process to play an audiofile
	 * 
	 * @param audioObject
	 *            audio object which should be played
	 * @return mplayer process
	 * @throws IOException
	 */
	private Process getProcess(AudioObject audioObject) throws IOException {

		ProcessBuilder pb = new ProcessBuilder();

		List<String> command = new ArrayList<String>();
		command.add(getProcessNameForOS());
		if (SystemProperties.SYSTEM == OperatingSystem.SOLARIS) {
			command.add(SOLARISOPTAO);
			command.add(SOLARISOPTTYPE);
		}
		command.add(QUIET);
		command.add(SLAVE);

		// If a radio has a playlist url add playlist command
		if (audioObject instanceof Radio && ((Radio) audioObject).hasPlaylistUrl()) {
			command.add(PLAYLIST);
		}

		if (Kernel.getInstance().state.isUseShortPathNames() && SystemProperties.SYSTEM == OperatingSystem.WINDOWS && audioObject instanceof AudioFile)
			command.add(NativeFunctionsUtils.getShortPathNameW(audioObject.getUrl()));
		else
			command.add(audioObject.getUrl());

		if ((audioObject instanceof AudioFile && getEqualizer() != null) || isUseNormalisation() || isKaraoke())
			command.add(AUDIO_FILTER);

		if (isUseNormalisation()) {
			command.add(VOLUME_NORM);
		}

		// Build equalizer command. Mplayer uses 10 bands
		if (audioObject instanceof AudioFile && getEqualizer() != null)
			command.add(EQUALIZER + getEqualizer()[0] + ":" + getEqualizer()[1] + ":" + getEqualizer()[2] + ":" + getEqualizer()[3] + ":" + getEqualizer()[4] + ":"
					+ getEqualizer()[5] + ":" + getEqualizer()[6] + ":" + getEqualizer()[7] + ":" + getEqualizer()[8] + ":" + getEqualizer()[9]);

		if (isKaraoke()) {
			command.add(KARAOKE);
		}

		logger.debug(LogCategories.PLAYER, command.toArray(new String[command.size()]));

		return pb.command(command).start();
	}

	/**
	 * Returns string command to call mplayer
	 * 
	 * @return
	 */
	private String getProcessNameForOS() {
		if (SystemProperties.SYSTEM == OperatingSystem.WINDOWS)
			return WIN_COMMAND;
		else if (SystemProperties.SYSTEM == OperatingSystem.LINUX)
			return LINUX_COMMAND;
		else if (SystemProperties.SYSTEM == OperatingSystem.SOLARIS)
			return SOLARIS_COMMAND;
		else
			return MACOS_COMMAND;
	}

	@Override
	public boolean isPlaying() {
		return process != null && !paused;
	}

	/**
	 * Goes to next audio file in playlist.
	 */
	@Override
	public void next(boolean autoNext) {
		boolean wasStopped = false;
		AudioObject nextAudioObject = currentPlayList.getNextAudioObjectToPlay();
		if (nextAudioObject != null) {
			try {
				PlayListHandler.getInstance().selectedAudioObjectChanged(nextAudioObject);
				if (process != null || autoNext) {
					stop();
					if (!paused) {
						play(nextAudioObject);
						VisualHandler.getInstance().setPlaying(true);
						if (muted)
							setMute(true);
						else
							setVolume(volume);
						setBalance(balance);
						logger.info(LogCategories.PLAYER, StringUtils.getString("Started play of file ", nextAudioObject));
					} else {
						setTime(0);
						wasStopped = true;
					}
				} else {
					wasStopped = true;
					setTime(0);
				}
				if (wasStopped)
					playListController.setSelectedSong(currentPlayList.getNextAudioObject());
				else {
					updateStatusBar(nextAudioObject, false);
					VisualHandler.getInstance().updateTitleBar(nextAudioObject);
					VisualHandler.getInstance().updatePlayState(PlayState.PLAYING);
					logger.debug(LogCategories.PLAYER, Integer.toString(currentPlayList.getNextAudioObject()));
					playListController.setSelectedSong(currentPlayList.getNextAudioObject());
				}
			} catch (Exception e) {
				notifyPlayerError(e);
				stop();
			}
		} else { // End of play list. Go to first song and stop
			stop();
			currentPlayList.setNextAudioObject(0);
			playListController.setSelectedSong(currentPlayList.getNextAudioObject());
			PlayListHandler.getInstance().selectedAudioObjectChanged(currentPlayList.getCurrentAudioObject());
		}

	}

	/**
	 * Notifys the handler that the radio or podcast feed entry has started
	 * playing (MPlayer bug workaround)
	 */
	void notifyRadioOrPodcastFeedEntryStarted() {
		logger.debug(LogCategories.PLAYER, "radio or podcast feed entry has started playing");
		// send volume command
		setVolume(volume);
		// if muted set mute again
		if (muted) {
			setMute(muted);
		}
		logger.debug(LogCategories.PLAYER, "MPlayer bug (ignoring muting and volume settings after streamed file starts playing) workaround applied");
	}

	/**
	 * Starts play process
	 * 
	 * @param audioObject
	 *            audio object which should be played
	 */
	private void play(AudioObject audioObject) {
		try {
			// Send stop command in order to try to avoid two mplayer
			// instaces are running at the same time
			sendStopCommand();
			// Start the play process
			process = getProcess(audioObject);
			MPlayerOutputReader outputReader = MPlayerOutputReader.newInstance(this, process, audioObject);
			outputReader.start();
			MPlayerErrorReader errorReader = new MPlayerErrorReader(this, process, audioObject);
			errorReader.start();
			sendGetDurationCommand();

			setVolume(volume);
			setBalance(balance);

			// Show OSD
			if (Kernel.getInstance().state.isShowOSD())
				VisualHandler.getInstance().showOSDDialog();

			// If we are playing a podcast, mark entry as listened
			if (audioObject instanceof PodcastFeedEntry) {
				((PodcastFeedEntry) audioObject).setListened(true);
				// Update pod cast navigator
				ControllerProxy.getInstance().getNavigationController().refreshPodcastFeedTreeContent();
			}

		} catch (Exception e) {
			stop();
			notifyPlayerError(e);
		}
	}

	/**
	 * Play music. Pass true if user pressed a button.
	 */
	@Override
	public void play(boolean buttonPressed) {
		if (isPlaying() && buttonPressed) { // Pause
			paused = true;
			VisualHandler.getInstance().setPlaying(false);
			try {
				sendPauseCommand();
				VisualHandler.getInstance().updateStatusBar(LanguageTool.getString("PAUSED"));
				VisualHandler.getInstance().updateTitleBar("");
				VisualHandler.getInstance().updatePlayState(PlayState.PAUSED);
				logger.info(LogCategories.PLAYER, "Pause");
			} catch (Exception e) {
				notifyPlayerError(e);
				stop();
			}
		} else {
			// If list is empty, is not playing
			VisualHandler.getInstance().setPlaying(currentPlayList.size() > 0);

			AudioObject nextAudioObject = null;
			try {
				if (paused && buttonPressed) {
					paused = false;
					sendResumeCommand();
					if (!currentPlayList.isEmpty()) {
						updateStatusBar(currentPlayList.get(currentPlayList.getNextAudioObject()), true);
						VisualHandler.getInstance().updateTitleBar(currentPlayList.get(currentPlayList.getNextAudioObject()));
						VisualHandler.getInstance().updatePlayState(PlayState.PAUSED);
						logger.info(LogCategories.PLAYER, "Resumed paused song");
					}
				} else {
					nextAudioObject = currentPlayList.getCurrentAudioObject();
					if (nextAudioObject != null) {
						if (isPlaying() || paused)
							stop();

						PlayListHandler.getInstance().selectedAudioObjectChanged(nextAudioObject);
						play(nextAudioObject);
						// Setting volume and balance
						if (muted)
							setMute(true);
						else
							setVolume(volume);
						setBalance(balance);

						updateStatusBar(nextAudioObject, false);
						VisualHandler.getInstance().updateTitleBar(nextAudioObject);
						playListController.setSelectedSong(currentPlayList.getNextAudioObject());
						VisualHandler.getInstance().setPlaying(true);
						logger.info(LogCategories.PLAYER, StringUtils.getString("Started play of file ", nextAudioObject));
					}
				}
				VisualHandler.getInstance().updatePlayState(PlayState.PLAYING);
			} catch (Exception e) {
				notifyPlayerError(e);
				stop();
			}
		}
	}

	@Override
	/**
	 * Go to previous audio file in playlist.
	 */
	public void previous() {
		boolean wasStopped = false;
		AudioObject previousAudioObject = currentPlayList.getPreviousAudioObjectToPlay();
		if (previousAudioObject != null) {
			try {
				PlayListHandler.getInstance().selectedAudioObjectChanged(previousAudioObject);
				if (process != null) {
					stop();
					// Not paused and counter has correct value
					if (!paused) {
						play(previousAudioObject);
						VisualHandler.getInstance().setPlaying(true);
						if (muted)
							setMute(true);
						else
							setVolume(volume);
						setBalance(balance);
						logger.info(LogCategories.PLAYER, StringUtils.getString("Started play of file ", previousAudioObject));
					} else {
						setTime(0);
						wasStopped = true;
					}
				} else {
					wasStopped = true;
					setTime(0);
				}
				playListController.setSelectedSong(currentPlayList.getNextAudioObject());
				if (wasStopped)
					playListController.setSelectedSong(currentPlayList.getNextAudioObject());
				else {
					updateStatusBar(previousAudioObject, false);
					VisualHandler.getInstance().updateTitleBar(previousAudioObject);
					VisualHandler.getInstance().updatePlayState(PlayState.PLAYING);
					playListController.setSelectedSong(currentPlayList.getNextAudioObject());
				}
			} catch (Exception e) {
				notifyPlayerError(e);
				stop();
			}
		}
	}

	@Override
	public void seek(double position) {
		sendSeekCommand(position);
		if (paused) {
			paused = false;
			if (!currentPlayList.isEmpty()) {
				updateStatusBar(currentPlayList.get(currentPlayList.getNextAudioObject()), false);
				VisualHandler.getInstance().updateTitleBar(currentPlayList.get(currentPlayList.getNextAudioObject()));
				VisualHandler.getInstance().updatePlayState(PlayState.PLAYING);
				VisualHandler.getInstance().setPlaying(true);
				logger.info(LogCategories.PLAYER, "Resumed paused song");
			}
		}
	}

	private void sendCommand(String command) {
		if (process != null) {
			PrintStream out = new PrintStream(process.getOutputStream());
			StringBuilder sb = new StringBuilder();
			sb.append(command).append('\n');
			out.print(sb.toString());
			out.flush();
		}
	}

	private void sendGetDurationCommand() {
		sendCommand("get_time_length");
	}

	void sendGetPositionCommand() {
		if (!paused)
			sendCommand("get_time_pos");
	}

	private void sendMuteCommand() {
		sendCommand("mute");
	}

	private void sendPauseCommand() {
		sendCommand("pause");
	}

	private void sendResumeCommand() {
		sendCommand("pause");
	}

	private void sendSeekCommand(double perCent) {
		sendCommand(StringUtils.getString("seek ", perCent * 100, " 1"));
		if (paused)
			sendPauseCommand();
	}

	private void sendStopCommand() {
		sendCommand("quit");
	}

	private void sendVolumeCommand(int perCent) {
		sendCommand(StringUtils.getString("volume ", perCent, " 1"));
	}

	@Override
	public void setBalance(float value) {
		// Nothing to do
	}

	@Override
	public void setMute(boolean mute) {
		logger.debug(LogCategories.PLAYER, Boolean.toString(mute));

		sendMuteCommand();

		// MPlayer bug: paused, demute, muted -> starts playing
		if (paused && !mute && muted) {
			sendPauseCommand();
			logger.debug(LogCategories.PLAYER, "MPlayer bug (paused, demute, muted -> starts playing) workaround applied");
		}

		muted = mute;
	}

	@Override
	public void setVolume(int perCent) {
		if (perCent < 0)
			volume = 0;
		else if (perCent > 100)
			volume = 100;
		else
			volume = perCent;
		sendVolumeCommand(volume);

		// MPlayer bug: paused, volume change -> starts playing
		if (paused) {
			sendPauseCommand();
			logger.debug(LogCategories.PLAYER, "MPlayer bug (paused, volume change -> starts playing) workaround applied");
		}
		// MPlayer bug: muted, volume change -> demuted
		if (muted) {
			sendMuteCommand();
			logger.debug(LogCategories.PLAYER, "MPlayer bug (muted, volume change -> demuted) workaround applied");
		}
	}

	@Override
	/**
	 * Stops the player and resets values.
	 */
	public void stop() {
		VisualHandler.getInstance().setPlaying(false);
		if (paused)
			paused = false;
		try {
			sendStopCommand();

			process = null;

			setTime(0);
			VisualHandler.getInstance().updateStatusBar(LanguageTool.getString("STOPPED"));
			VisualHandler.getInstance().updateTitleBar("");
			VisualHandler.getInstance().updatePlayState(PlayState.STOPPED);
			logger.info(LogCategories.PLAYER, "Stop");
		} catch (Exception e) {
			notifyPlayerError(e);
		}
	}

	private void updateStatusBar(AudioObject file, boolean resumed) {
		if (resumed || !(file instanceof Radio || file instanceof PodcastFeedEntry))
			VisualHandler.getInstance().updateStatusBar(file);
		else
			VisualHandler.getInstance().updateStatusBar(StringUtils.getString("<html>", LanguageTool.getString("BUFFERING"), " <b>", file.getTitle(), "</b>...</html>"));

	}
	
	// #####################################################################
	// _____________________________________________________________________
}
