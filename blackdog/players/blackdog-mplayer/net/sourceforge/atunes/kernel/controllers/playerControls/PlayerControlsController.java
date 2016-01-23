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

package net.sourceforge.atunes.kernel.controllers.playerControls;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.model.PanelController;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;

public class PlayerControlsController extends PanelController<PlayerControlsPanel> {

	public PlayerControlsController(PlayerControlsPanel panel) {
		super(panel);
		addBindings();
		addStateBindings();
	}

	@Override
	protected void addBindings() {
		PlayerControlsListener listener = new PlayerControlsListener(panelControlled, this);

		panelControlled.getProgressBar().addMouseListener(listener);
		panelControlled.getShuffleButton().addActionListener(listener);
		panelControlled.getRepeatButton().addActionListener(listener);
		panelControlled.getPlayButton().addMouseListener(listener);
		panelControlled.getStopButton().addMouseListener(listener);
		panelControlled.getNextButton().addMouseListener(listener);
		panelControlled.getPreviousButton().addMouseListener(listener);
		panelControlled.getVolumeButton().addActionListener(listener);
		panelControlled.getVolumeSlider().addChangeListener(listener);
		panelControlled.getBalanceSlider().addChangeListener(listener);
		panelControlled.getKaraokeButton().addActionListener(listener);
	}

	@Override
	protected void addStateBindings() {
		panelControlled.getShuffleButton().setSelected(Kernel.getInstance().state.isShuffle());
		panelControlled.getRepeatButton().setSelected(Kernel.getInstance().state.isRepeat());
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}

	public void setKaraoke(boolean karaoke) {
		panelControlled.getKaraokeButton().setSelected(karaoke);
		panelControlled.getKaraokeEnabledLabel().setVisible(karaoke);
	}

	public void setMaxDuration(long maxDuration) {
		logger.debug(LogCategories.CONTROLLER, new String[] { Long.toString(maxDuration) });

		panelControlled.getProgressBar().setMaximum((int) maxDuration);
	}

	public void setMute(boolean mute) {
		panelControlled.getVolumeButton().setSelected(mute);
		if (mute) {
			panelControlled.getVolumeButton().setIcon(ImageLoader.VOLUME_MUTE);
		} else {
			int value = panelControlled.getVolumeSlider().getValue();
			if (value > 80)
				panelControlled.getVolumeButton().setIcon(ImageLoader.VOlUME_MAX);
			else if (value > 40)
				panelControlled.getVolumeButton().setIcon(ImageLoader.VOLUME_MED);
			else if (value > 5)
				panelControlled.getVolumeButton().setIcon(ImageLoader.VOLUME_MIN);
			else
				panelControlled.getVolumeButton().setIcon(ImageLoader.VOLUME_ZERO);
		}
	}

	public void setPlaying(boolean playing) {
		panelControlled.setPlaying(playing);
	}

	public void setRepeat(boolean repeat) {
		panelControlled.getRepeatButton().setSelected(repeat);
	}

	public void setShuffle(boolean shuffle) {
		panelControlled.getShuffleButton().setSelected(shuffle);
	}

	public void setSlidable(boolean slidable) {
		panelControlled.getProgressBar().setEnabled(slidable);
	}

	public void setTime(long time, long totalTime) {
		long remainingTime = totalTime - time;
		if (time == 0)
			panelControlled.getRemainingTime().setText(TimeUtils.milliseconds2String(time));
		else
			panelControlled.getRemainingTime().setText(remainingTime > 0 ? StringUtils.getString("- ", TimeUtils.milliseconds2String(remainingTime)) : "-");

		panelControlled.getTime().setText(TimeUtils.milliseconds2String(time));
		panelControlled.getProgressBar().setValue((int) time);
	}

	protected void setVolume(int value) {
		panelControlled.getVolumeSlider().setValue(value);
	}

}
