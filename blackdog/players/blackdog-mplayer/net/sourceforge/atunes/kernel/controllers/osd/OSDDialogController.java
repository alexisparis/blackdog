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

package net.sourceforge.atunes.kernel.controllers.osd;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.Timer;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.dialogs.OSDDialog;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.model.DialogController;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;

public class OSDDialogController extends DialogController<OSDDialog> {

	private enum Direction {
		UP, DOWN
	};

	private static final int STEP = 4;

	private Timer timer;

	private int width = Toolkit.getDefaultToolkit().getScreenSize().width / 3;
	private int height = 84;

	private Point locationToStay = new Point(width, Toolkit.getDefaultToolkit().getScreenSize().height - (int) (height * 1.7));
	private Point hiddenLocation = new Point(locationToStay.x, Toolkit.getDefaultToolkit().getScreenSize().height + 4);

	public OSDDialogController(OSDDialog dialogControlled) {
		super(dialogControlled);
		addBindings();
	}

	@Override
	protected void addBindings() {
		MouseListener listener = new OSDDialogMouseListener(dialogControlled, this);
		dialogControlled.addMouseListener(listener);
	}

	@Override
	protected void addStateBindings() {
		// Nothing to do
	}

	private Timer getTimer() {
		return new Timer(10, new ActionListener() {

			private boolean showAnimation = Kernel.getInstance().state.isAnimateOSD();
			private Direction direction = Direction.UP;
			private int show = Kernel.getInstance().state.getOsdDuration() * 100;
			private int wait;
			private int position;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (showAnimation && direction == Direction.UP && dialogControlled.getLocation().y > locationToStay.y) {
					// Move up
					dialogControlled.setLocation(locationToStay.x, dialogControlled.getLocation().y - STEP);
				} else {
					direction = Direction.DOWN;
					// Show
					if (++wait > show) {
						if (showAnimation && direction == Direction.DOWN && dialogControlled.getLocation().y < hiddenLocation.y) {
							// Workaround for Gnome	
							if (position == dialogControlled.getLocation().y) {
								showAnimation = false;
							}
							position = dialogControlled.getLocation().y;
							// Move down
							dialogControlled.setLocation(locationToStay.x, dialogControlled.getLocation().y + STEP);
						} else {
							// Finished
							direction = Direction.UP;
							wait = 0;
							dialogControlled.setVisible(false);
							timer.stop();
						}
					}
				}

			}
		});
	}

	public void hideDialogAndStopAnimation() {
		if (timer != null && timer.isRunning()) {
			dialogControlled.setVisible(false);
			timer.stop();
		}
	}

	@Override
	protected void notifyReload() {
		// Nothing to do
	}

	public void setTransparent(boolean transparent) {
		dialogControlled.setVisible(false);

		// Sets the window trannsparency
		dialogControlled.setTransparent(transparent);

		// Add rounded borders too if transparent
		dialogControlled.setRoundedBorders(transparent);
	}

	public void showOSD(AudioObject audioObject) {

		if (audioObject == null) {
			return;
		}

		// If the OSD is already visible stop animation
		hideDialogAndStopAnimation();

		if (audioObject instanceof PodcastFeedEntry) {
			dialogControlled.setImage(ImageLoader.RSS);
			dialogControlled.setLine1(audioObject.getTitle());
			dialogControlled.setLine2(audioObject.getUrl());
			dialogControlled.setLine3(audioObject.getArtist());
		} else if (audioObject instanceof Radio) {
			dialogControlled.setImage(ImageLoader.RADIO);
			dialogControlled.setLine1(audioObject.getTitle());
			dialogControlled.setLine2(audioObject.getUrl());
			dialogControlled.setLine3("");
		} else if (audioObject instanceof AudioFile) {
			dialogControlled.setImage(AudioFilePictureUtils.getImageForAudioFile((AudioFile) audioObject, -1, -1));
			dialogControlled.setLine1(StringUtils.getString(audioObject.getTitleOrFileName(), " (", TimeUtils.seconds2String(audioObject.getDuration()), ")"));
			dialogControlled.setLine2(audioObject.getAlbum());
			dialogControlled.setLine3(audioObject.getArtist());
		} else {
			throw new IllegalArgumentException(audioObject + " is not a audio file, radio or podcast feed entry");
		}
		dialogControlled.setLocation(Kernel.getInstance().state.isAnimateOSD() ? hiddenLocation : locationToStay);
		dialogControlled.setVisible(true);
		// see bug 1864517
		dialogControlled.repaint();
		timer = getTimer();
		timer.start();
	}
}
