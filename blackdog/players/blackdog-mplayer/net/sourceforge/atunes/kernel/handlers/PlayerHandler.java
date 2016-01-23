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

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.controllers.playList.PlayListController;
import net.sourceforge.atunes.kernel.modules.mplayer.MPlayerHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayList;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public abstract class PlayerHandler {

	private static PlayerHandler instance = new MPlayerHandler();

	protected static final int PREVIOUS = 1;
	protected static final int PLAY = 2;
	protected static final int STOP = 3;
	protected static final int NEXT = 4;
	protected static final int SEEK = 5;

	protected int lastButtonPressed = -1;

	protected boolean muted;

	protected Logger logger = new Logger();

	protected PlayList currentPlayList;

	long currentDuration;

	protected int volume = 50; // Per cent
	protected float balance; // -1, +1

	protected boolean useNormalisation;
	protected boolean shuffle;
	protected boolean repeat;
	protected boolean karaoke;

	// Mplayer equalizer settings
	protected float[] equalizer;

	protected boolean paused;

	protected PlayListController playListController;

	protected PlayerHandler() {
		logger.debug(LogCategories.PLAYER);

		this.playListController = ControllerProxy.getInstance().getPlayListController();
		currentPlayList = new PlayList();
		VisualHandler.getInstance().updateStatusBar(LanguageTool.getString("STOPPED"));
	}

	public static PlayerHandler getInstance() {
		return instance;
	}

	public abstract void finish();

	public long getCurrentDuration() {
		return currentDuration;
	}

	public final PlayList getCurrentPlayList() {
		return currentPlayList;
	}

	public final float[] getEqualizer() {
		return equalizer;
	}

	public final int getVolume() {
		return volume;
	}

	public boolean isKaraoke() {
		return karaoke;
	}

	public final boolean isMute() {
		return muted;
	}

	public abstract boolean isPlaying();

	/**
	 * @return the repeat
	 */
	public boolean isRepeat() {
		return repeat;
	}

	/**
	 * @return the shuffle
	 */
	public boolean isShuffle() {
		return shuffle;
	}

	public boolean isUseNormalisation() {
		return useNormalisation;
	}

	public abstract void next(boolean autoNext);

	public final void notifyPlayerError(Exception e) {
		logger.error(LogCategories.PLAYER, StringUtils.getString("Player Error: ", e));
		e.printStackTrace(System.out);
		VisualHandler.getInstance().showErrorDialog(e.getMessage());
	}

	public abstract void play(boolean buttonPressed);

	public abstract void previous();

	public abstract void seek(double position);

	public abstract void setBalance(float value);

	public void setCurrentDuration(long currentDuration) {
		this.currentDuration = currentDuration;
	}

	public final void setCurrentPlayList(PlayList currentPlayList) {
		this.currentPlayList = currentPlayList;
	}

	public final void setDuration(long time) {
		logger.debug(LogCategories.PLAYER, new String[] { Long.toString(time) });

		ControllerProxy.getInstance().getPlayerControlsController().setMaxDuration(time);

		VisualHandler.getInstance().getFullScreenFrame().setMaxDuration(time);
	}

	public void setEqualizer(float[] equalizer) {
		this.equalizer = equalizer;
	}

	public void setKaraoke(boolean karaoke) {
		this.karaoke = karaoke;
	}

	public abstract void setMute(boolean mute);

	public final void setPlayListPositionToPlay(int pos) {
		currentPlayList.setNextAudioObject(pos);
	}

	public final void setRepeat(boolean enable) {
		repeat = enable;
		logger.info(LogCategories.PLAYER, StringUtils.getString("Repeat enabled: ", enable));
	}

	public final void setShuffle(boolean enable) {
		shuffle = enable;
		logger.info(LogCategories.PLAYER, StringUtils.getString("Shuffle enabled: ", enable));

	}

	public final void setTime(long time) {
		ControllerProxy.getInstance().getPlayerControlsController().setTime(time, currentDuration);
		VisualHandler.getInstance().getFullScreenFrame().setTime(time, currentDuration);
	}

	public void setUseNormalisation(boolean useNormalisation) {
		this.useNormalisation = useNormalisation;
	}

	public abstract void setVolume(int perCent);

	public abstract void stop();

	public final void volumeDown() {
		logger.debug(LogCategories.PLAYER);

		setVolume(getVolume() - 5);
		VisualHandler.getInstance().setVolume(getVolume());
	}

	public final void volumeUp() {
		logger.debug(LogCategories.PLAYER);

		setVolume(getVolume() + 5);
		VisualHandler.getInstance().setVolume(getVolume());
	}

}
