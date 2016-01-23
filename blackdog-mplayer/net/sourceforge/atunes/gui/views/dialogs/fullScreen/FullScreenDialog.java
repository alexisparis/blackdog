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

package net.sourceforge.atunes.gui.views.dialogs.fullScreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.Cover3D;
import net.sourceforge.atunes.gui.views.controls.CustomDialog;
import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.handlers.AudioScrobblerServiceHandler;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;

public class FullScreenDialog extends CustomDialog {

	private static final long serialVersionUID = 3422799994808333945L;

	private Cover3D cover;

	private JLabel textLabel;

	private JSlider progressBar;

	private JLabel time;
	private JLabel remainingTime;

	private PopUpButton options;
	private JMenuItem exitFullScreen;
	private JMenuItem selectBackground;
	private JMenuItem removeBackground;

	private JLabel previousButton;
	private JLabel playButton;
	private JLabel stopButton;
	private JLabel nextButton;

	private boolean playing;

	private AudioObject currentPlayed;

	private ImageIcon background;

	private KeyAdapter keyAdapter = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_F11)
				setVisible(false);
		}
	};

	public FullScreenDialog() {
		super();
		setFullScreen();
		setUndecorated(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setContent();
		addKeyListener(keyAdapter);
		File backgroundFile = null;
		if (Kernel.getInstance().state.getFullScreenBackground() != null) {
			backgroundFile = new File(Kernel.getInstance().state.getFullScreenBackground());
			if (!backgroundFile.exists())
				backgroundFile = null;
		}
		if (backgroundFile == null)
			background = null;
		else
			setBackground(backgroundFile);
		GuiUtils.applyComponentOrientation(this);
	}

	public static void main(String[] args) {
		new FullScreenDialog().setVisible(true);
	}

	private int getInsetForProgressBar() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return (int) (screenSize.width * 0.25);
	}

	/**
	 * @return the remainingTime
	 */
	public JLabel getRemainingTime() {
		return remainingTime;
	}

	/**
	 * @return the time
	 */
	public JLabel getTime() {
		return time;
	}

	public boolean isPlaying() {
		return playing;
	}

	private void paint(AudioObject song, boolean updateCover) {
		if (song == null) {
			return;
		}
		// If it's a radio, hide progress bar
		boolean isRadio = (song instanceof Radio);
		boolean isPodcastEntry = (song instanceof PodcastFeedEntry);
		progressBar.setVisible(!isRadio && !isPodcastEntry);
		time.setVisible(!isRadio && !isPodcastEntry);
		remainingTime.setVisible(!isRadio && !isPodcastEntry);

		if (!isRadio && !isPodcastEntry) {
			textLabel.setText(StringUtils.getString(song.getArtist(), " - ", song.getTitleOrFileName(), " (", TimeUtils.seconds2String(song.getDuration()), ")"));

			if (updateCover) {
				// When changing song, set no image, until audioscrobbler updates it
				cover.setImage(ImageLoader.NO_COVER);

				// Fetch cover
				AudioScrobblerServiceHandler.getInstance().getAlbumCover((AudioFile) song);
			}
		} else {
			if (isRadio) {
				textLabel.setText(((Radio) song).getName());
				cover.setImage(ImageLoader.RADIO_BIG);
			} else if (isPodcastEntry) {
				textLabel.setText(((PodcastFeedEntry) song).getName());
				cover.setImage(ImageLoader.RSS_BIG);
			}
		}
	}

	public void setAudioObject(AudioObject song) {
		if (song == null) {
			textLabel.setText("");
			return;
		}

		if (song == currentPlayed)
			return;

		boolean updateCover = currentPlayed == null || !song.getAlbum().equals(currentPlayed.getAlbum());

		currentPlayed = song;

		if (!isVisible())
			return;

		paint(song, updateCover);
	}

	void setBackground(File file) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		try {
			background = new ImageIcon(ImageUtils.scaleImage(ImageIO.read(file), screenSize.width, screenSize.height));
			Kernel.getInstance().state.setFullScreenBackground(file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setContent() {
		JPanel panel = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 109708757849271173L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (background == null) {
					Graphics2D g2d = (Graphics2D) g;
					g2d.setPaint(new GradientPaint(0, this.getHeight() / 2f, Color.black, 0, this.getHeight(), Color.LIGHT_GRAY));
					g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
				} else {
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					g.drawImage(ImageUtils.resize(background, screenSize.width, screenSize.height).getImage(), 0, 0, this);
				}
			}
		};
		panel.setBackground(Color.black);
		add(panel);

		options = new PopUpButton(LanguageTool.getString("OPTIONS"), PopUpButton.TOP_RIGHT);
		options.addKeyListener(keyAdapter);
		selectBackground = new JMenuItem(LanguageTool.getString("SELECT_BACKGROUND"));

		selectBackground.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						if (pathname.isDirectory())
							return true;
						String fileName = pathname.getName().toUpperCase();
						return fileName.endsWith("JPG") || fileName.endsWith("JPEG") || fileName.endsWith("PNG");
					}

					@Override
					public String getDescription() {
						return LanguageTool.getString("IMAGES");
					}
				});
				if (fileChooser.showOpenDialog(FullScreenDialog.this) == JFileChooser.APPROVE_OPTION) {
					File selectedBackground = fileChooser.getSelectedFile();
					setBackground(selectedBackground);
					FullScreenDialog.this.invalidate();
					FullScreenDialog.this.repaint();
				}
			}
		});

		removeBackground = new JMenuItem(LanguageTool.getString("REMOVE_BACKGROUND"));
		removeBackground.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				background = null;
				Kernel.getInstance().state.setFullScreenBackground(null);
				FullScreenDialog.this.invalidate();
				FullScreenDialog.this.repaint();
			}
		});
		exitFullScreen = new JMenuItem(LanguageTool.getString("CLOSE"));
		exitFullScreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		options.add(selectBackground);
		options.add(removeBackground);
		options.add(exitFullScreen);

		previousButton = new JLabel(ImageLoader.PREVIOUS);
		previousButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PlayerHandler.getInstance().previous();
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
				PlayerHandler.getInstance().play(true);
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
				PlayerHandler.getInstance().stop();
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
				PlayerHandler.getInstance().next(false);
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

		cover = new Cover3D();

		textLabel = new JLabel();
		textLabel.setFont(Fonts.AUDIO_SCROBBLER_BIG_FONT);

		time = new JLabel("0:00");
		time.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));

		remainingTime = new JLabel("0:00");
		remainingTime.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));

		progressBar = new JSlider();
		progressBar.setOpaque(false);
		progressBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int widthClicked = e.getPoint().x;
				int widthOfProgressBar = progressBar.getSize().width;
				double perCent = (double) widthClicked / (double) widthOfProgressBar;
				PlayerHandler.getInstance().seek(perCent);
			}
		});
		progressBar.addKeyListener(keyAdapter);
		progressBar.setToolTipText(LanguageTool.getString("CLICK_TO_SEEK"));
		progressBar.setMinimum(0);
		progressBar.setValue(0);

		JPanel controlsPanel = new JPanel(new GridBagLayout());
		controlsPanel.setOpaque(false);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		controlsPanel.add(textLabel, c);
		int inset = getInsetForProgressBar();

		c.gridy = 1;
		c.insets = new Insets(50, inset, 0, 10);
		c.gridwidth = 1;
		controlsPanel.add(time, c);
		c.gridx = 1;
		c.insets = new Insets(50, 0, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		controlsPanel.add(progressBar, c);
		c.gridx = 2;
		c.insets = new Insets(50, 10, 0, inset);
		c.weightx = 0;
		controlsPanel.add(remainingTime, c);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setOpaque(false);
		buttonsPanel.add(previousButton);
		buttonsPanel.add(playButton);
		buttonsPanel.add(stopButton);
		buttonsPanel.add(nextButton);

		c.gridx = 0;
		c.gridwidth = 3;
		c.gridy = 2;
		c.insets = new Insets(20, 0, 5, 0);
		controlsPanel.add(buttonsPanel, c);
		c.gridy = 3;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5, 0, 20, 0);
		controlsPanel.add(options, c);

		panel.add(cover, BorderLayout.CENTER);

		panel.add(controlsPanel, BorderLayout.SOUTH);

	}

	private void setFullScreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize);
	}

	public void setMaxDuration(long maxDuration) {
		progressBar.setMaximum((int) maxDuration);
	}

	public void setPicture(AudioFile file, Image picture) {
		// If is not current file, discard
		if (file != currentPlayed)
			return;

		if (picture == null) {
			ImageIcon[] externalPictures = AudioFilePictureUtils.getPicturesForFile(file, -1, -1);
			if (externalPictures.length > 0)
				cover.setImage(ImageUtils.scaleImageBicubic(externalPictures[0].getImage(), Constants.FULL_SCREEN_COVER, Constants.FULL_SCREEN_COVER));
			else
				cover.setImage(ImageLoader.NO_COVER);
		} else {
			ImageIcon imageScaled = ImageUtils.scaleImageBicubic(picture, Constants.FULL_SCREEN_COVER, Constants.FULL_SCREEN_COVER);
			cover.setImage(imageScaled);
		}
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
		if (playing)
			playButton.setIcon(ImageLoader.PAUSE);
		else
			playButton.setIcon(ImageLoader.PLAY);
	}

	public void setTime(long time, long totalTime) {
		long remainingTime1 = totalTime - time;
		if (time == 0)
			this.remainingTime.setText(TimeUtils.milliseconds2String(time));
		else
			this.remainingTime.setText(remainingTime1 > 0 ? StringUtils.getString("- ", TimeUtils.milliseconds2String(remainingTime1)) : "-");

		this.time.setText(TimeUtils.milliseconds2String(time));
		progressBar.setValue((int) time);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			paint(currentPlayed, true);
	}

}
