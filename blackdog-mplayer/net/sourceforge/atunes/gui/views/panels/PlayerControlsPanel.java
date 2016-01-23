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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.utils.LanguageTool;

public class PlayerControlsPanel extends JPanel {

	private static final long serialVersionUID = -8647737014195638177L;

	protected JLabel time;
	protected JLabel remainingTime;
	protected JSlider progressBar;

	protected JToggleButton shuffleButton;
	protected JToggleButton repeatButton;
	protected JToggleButton karaokeButton;

	protected JLabel previousButton;
	protected JLabel playButton;
	protected JLabel stopButton;
	protected JLabel nextButton;

	protected JToggleButton volumeButton;
	protected JSlider volumeSlider;
	protected JLabel balanceLabel;
	protected JSlider balanceSlider;

	protected JLabel karaokeEnabledLabel;

	boolean playing;

	public PlayerControlsPanel() {
		super(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder());
		addContent();
		GuiUtils.applyComponentOrientation(this);
	}

	protected void addContent() {
		time = new JLabel("0:00");
		time.setHorizontalAlignment(SwingConstants.CENTER);
		time.setPreferredSize(new Dimension(50, 10));

		progressBar = new JSlider();
		//progressBar.setPreferredSize(new Dimension(1,10));
		progressBar.setToolTipText(LanguageTool.getString("CLICK_TO_SEEK"));
		progressBar.setMinimum(0);
		progressBar.setValue(0);
		//progressBar.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		remainingTime = new JLabel("0:00");
		remainingTime.setHorizontalAlignment(SwingConstants.CENTER);
		remainingTime.setPreferredSize(new Dimension(50, 10));

		shuffleButton = new JToggleButton(ImageLoader.SHUFFLE, true);
		shuffleButton.setToolTipText(LanguageTool.getString("SHUFFLE"));

		repeatButton = new JToggleButton(ImageLoader.REPEAT, true);
		repeatButton.setToolTipText(LanguageTool.getString("REPEAT"));

		karaokeButton = new JToggleButton(ImageLoader.KARAOKE, false);
		karaokeButton.setToolTipText(LanguageTool.getString("KARAOKE"));

		previousButton = new JLabel(ImageLoader.PREVIOUS);
		previousButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Nothing to do
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				previousButton.setIcon(ImageLoader.PREVIOUS_OVER);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				previousButton.setIcon(ImageLoader.PREVIOUS);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				previousButton.setIcon(ImageLoader.PREVIOUS_PRESSED);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				previousButton.setIcon(ImageLoader.PREVIOUS_OVER);
			}
		});
		playButton = new JLabel(ImageLoader.PLAY);
		playButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Nothing to do
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (playing)
					playButton.setIcon(ImageLoader.PAUSE_OVER);
				else
					playButton.setIcon(ImageLoader.PLAY_OVER);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (playing)
					playButton.setIcon(ImageLoader.PAUSE);
				else
					playButton.setIcon(ImageLoader.PLAY);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (PlayerHandler.getInstance().getCurrentPlayList().size() == 0) {
					return;
				}
				if (playing)
					playButton.setIcon(ImageLoader.PAUSE_PRESSED);
				else
					playButton.setIcon(ImageLoader.PLAY_PRESSED);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (PlayerHandler.getInstance().getCurrentPlayList().size() == 0) {
					return;
				}
				if (playing)
					playButton.setIcon(ImageLoader.PAUSE_OVER);
				else
					playButton.setIcon(ImageLoader.PLAY_OVER);
			}
		});
		stopButton = new JLabel(ImageLoader.STOP);
		stopButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Nothing to do
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				stopButton.setIcon(ImageLoader.STOP_OVER);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				stopButton.setIcon(ImageLoader.STOP);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				stopButton.setIcon(ImageLoader.STOP_PRESSED);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				stopButton.setIcon(ImageLoader.STOP_OVER);
			}
		});
		nextButton = new JLabel(ImageLoader.NEXT);
		nextButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Nothing to do
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				nextButton.setIcon(ImageLoader.NEXT_OVER);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				nextButton.setIcon(ImageLoader.NEXT);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				nextButton.setIcon(ImageLoader.NEXT_PRESSED);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				nextButton.setIcon(ImageLoader.NEXT_OVER);
			}
		});
		volumeButton = new JToggleButton(ImageLoader.VOLUME_MED, false);
		volumeButton.setPreferredSize(new Dimension(24, 24));

		volumeSlider = new JSlider();
		volumeSlider.setOpaque(false);
		volumeSlider.setMinimum(0);
		volumeSlider.setMaximum(100);
		volumeSlider.setValue(50);
		volumeSlider.setPreferredSize(new Dimension(80, 20));

		balanceLabel = new JLabel(ImageLoader.BALANCE_MED);
		balanceLabel.setVisible(false);

		balanceSlider = new JSlider();
		balanceSlider.setMinimum(-5);
		balanceSlider.setMaximum(5);
		balanceSlider.setValue(0);
		balanceSlider.setPreferredSize(new Dimension(40, 20));
		balanceSlider.setVisible(false);

		karaokeEnabledLabel = new JLabel(LanguageTool.getString("KARAOKE"));
		karaokeEnabledLabel.setIcon(ImageLoader.KARAOKE);
		karaokeEnabledLabel.setForeground(ColorDefinitions.WARNING_COLOR);
		karaokeEnabledLabel.setVisible(false);

		JPanel topPanel = new JPanel(new GridBagLayout());
		topPanel.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		topPanel.add(time, c);
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 5, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		topPanel.add(progressBar, c);
		c.gridx = 2;
		c.weightx = 0;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.NONE;
		topPanel.add(remainingTime, c);

		JPanel bottomPanel = new JPanel(new GridBagLayout());
		bottomPanel.setOpaque(false);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 0, 0, 0);
		setButton(bottomPanel, previousButton, 30, c);
		c.gridx = 1;
		c.insets = new Insets(0, 2, 0, 0);
		setButton(bottomPanel, playButton, 30, c);
		c.gridx = 2;
		setButton(bottomPanel, stopButton, 30, c);
		c.gridx = 3;
		setButton(bottomPanel, nextButton, 30, c);
		c.gridx = 4;
		c.insets = new Insets(0, 10, 0, 0);
		bottomPanel.add(volumeButton, c);
		c.gridx = 5;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 3, 0, 0);
		bottomPanel.add(volumeSlider, c);
		c.gridx = 6;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 10, 0, 0);
		bottomPanel.add(balanceLabel, c);
		c.gridx = 7;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 3, 0, 0);
		bottomPanel.add(balanceSlider, c);
		c.gridx = 8;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 10, 0, 0);
		bottomPanel.add(karaokeEnabledLabel, c);
		c.gridx = 9;
		c.weightx = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GuiUtils.getComponentOrientation().isLeftToRight() ? GridBagConstraints.EAST : GridBagConstraints.WEST;
		setButton(bottomPanel, shuffleButton, 22, c);
		c.gridx = 10;
		c.weightx = 0;
		c.insets = new Insets(0, 5, 0, 0);
		setButton(bottomPanel, repeatButton, 22, c);
		c.gridx = 11;
		c.weightx = 0;
		c.insets = new Insets(0, 12, 0, 0);
		setButton(bottomPanel, karaokeButton, 22, c);

		c.gridx = 0;
		c.gridheight = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0.5;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 5, 5, 5);
		add(topPanel, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 0.5;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 10, 5, 5);
		add(bottomPanel, c);
	}

	public JLabel getBalanceLabel() {
		return balanceLabel;
	}

	public JSlider getBalanceSlider() {
		return balanceSlider;
	}

	public JToggleButton getKaraokeButton() {
		return karaokeButton;
	}

	public JLabel getKaraokeEnabledLabel() {
		return karaokeEnabledLabel;
	}

	public JLabel getNextButton() {
		return nextButton;
	}

	public JLabel getPlayButton() {
		return playButton;
	}

	public JLabel getPreviousButton() {
		return previousButton;
	}

	public JSlider getProgressBar() {
		return progressBar;
	}

	public JLabel getRemainingTime() {
		return remainingTime;
	}

	public JToggleButton getRepeatButton() {
		return repeatButton;
	}

	public JToggleButton getShuffleButton() {
		return shuffleButton;
	}

	public JLabel getStopButton() {
		return stopButton;
	}

	public JLabel getTime() {
		return time;
	}

	public JToggleButton getVolumeButton() {
		return volumeButton;
	}

	public JSlider getVolumeSlider() {
		return volumeSlider;
	}

	public boolean isPlaying() {
		return playing;
	}

	protected void setButton(JPanel panel, JComponent b, int size, GridBagConstraints c) {
		b.setPreferredSize(new Dimension(size, size));
		panel.add(b, c);
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
		if (playing)
			playButton.setIcon(ImageLoader.PAUSE);
		else
			playButton.setIcon(ImageLoader.PLAY);
	}
}
