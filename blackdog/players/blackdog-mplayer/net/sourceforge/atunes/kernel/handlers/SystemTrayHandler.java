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

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.JTrayIcon;
import net.sourceforge.atunes.gui.views.controls.JTrayIcon.JTrayIconPopupMenu;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.kernel.utils.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class SystemTrayHandler {

	private static Logger logger = new Logger();

	private static SystemTrayHandler instance = new SystemTrayHandler();

	private boolean trayInitialized;

	private boolean trayIconVisible;
	private boolean trayPlayerVisible;

	private SystemTray tray;
	private JTrayIcon trayIcon;
	private TrayIcon previousIcon;
	private TrayIcon playIcon;
	private TrayIcon stopIcon;
	private TrayIcon nextIcon;

	private JMenuItem playMenu;

	private JCheckBoxMenuItem mute;
	private JCheckBoxMenuItem shuffle;
	private JCheckBoxMenuItem repeat;
	private JCheckBoxMenuItem showOSD;

	private SystemTrayHandler() {
	}

	public static SystemTrayHandler getInstance() {
		return instance;
	}

	private JTrayIconPopupMenu fillMenu(JTrayIconPopupMenu menu) {
		playMenu = new JMenuItem(LanguageTool.getString("PLAY"), ImageLoader.PLAY_TRAY_MENU);
		playMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerHandler.getInstance().play(true);
			}
		});
		menu.add(playMenu);

		JMenuItem stop = new JMenuItem(LanguageTool.getString("STOP"), ImageLoader.STOP_TRAY_MENU);
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerHandler.getInstance().stop();
			}
		});
		menu.add(stop);

		JMenuItem previous = new JMenuItem(LanguageTool.getString("PREVIOUS"), ImageLoader.PREVIOUS_TRAY_MENU);
		previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerHandler.getInstance().previous();
			}
		});
		menu.add(previous);

		JMenuItem next = new JMenuItem(LanguageTool.getString("NEXT"), ImageLoader.NEXT_TRAY_MENU);
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerHandler.getInstance().next(false);
			}
		});
		menu.add(next);

		menu.add(new JSeparator());

		mute = new JCheckBoxMenuItem(LanguageTool.getString("MUTE"));
		mute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ControllerProxy.getInstance().getPlayerControlsController().setMute(mute.isSelected());
				SystemTrayHandler.getInstance().setMute(mute.isSelected());
				PlayerHandler.getInstance().setMute(mute.isSelected());
			}
		});
		menu.add(mute);

		menu.add(new JSeparator());

		shuffle = new JCheckBoxMenuItem(LanguageTool.getString("SHUFFLE"));
		shuffle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Kernel.getInstance().state.setShuffle(shuffle.isSelected());
				ControllerProxy.getInstance().getPlayerControlsController().setShuffle(shuffle.isSelected());
				PlayerHandler.getInstance().setShuffle(shuffle.isSelected());
			}
		});
		menu.add(shuffle);

		repeat = new JCheckBoxMenuItem(LanguageTool.getString("REPEAT"));
		repeat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Kernel.getInstance().state.setRepeat(repeat.isSelected());
				ControllerProxy.getInstance().getPlayerControlsController().setRepeat(repeat.isSelected());
				PlayerHandler.getInstance().setRepeat(repeat.isSelected());
			}
		});
		menu.add(repeat);

		menu.add(new JSeparator());

		showOSD = new JCheckBoxMenuItem(LanguageTool.getString("SHOW_OSD"));
		showOSD.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Kernel.getInstance().state.setShowOSD(showOSD.isSelected());
				ControllerProxy.getInstance().getMenuController().setShowOSD(showOSD.isSelected());
			}
		});
		menu.add(showOSD);

		menu.add(new JSeparator());

		JMenuItem about = new JMenuItem(LanguageTool.getString("ABOUT"));
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VisualHandler.getInstance().showAboutDialog();
			}
		});
		menu.add(about);

		menu.add(new JSeparator());

		JMenuItem exit = new JMenuItem(LanguageTool.getString("EXIT"), ImageLoader.EXIT);
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Kernel.getInstance().finish();
			}
		});
		menu.add(exit);

		GuiUtils.applyComponentOrientation(menu);

		return menu;
	}

	public void finish() {
		setTrayIconVisible(false);
		setTrayPlayerVisible(false);
	}

	private void initSystemTray() {
		if (!trayInitialized && SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();
			trayInitialized = true;
		}
	}

	public void initTrayIcon() {
		initSystemTray();
		if (tray != null) {
			trayIconVisible = true;
			trayIcon = new JTrayIcon(ImageLoader.APP_ICON.getImage());
			trayIcon.setToolTip(StringUtils.getString(Constants.APP_NAME, " ", Constants.APP_VERSION_NUMBER));
			trayIcon.setJTrayIconJPopupMenu(fillMenu(trayIcon.new JTrayIconPopupMenu()));
			trayIcon.setImageAutoSize(true);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				logger.error(LogCategories.TRAY, e);
			}

			trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						VisualHandler.getInstance().toggleWindowVisibility();
					}
				}
			});
			VisualHandler.getInstance().setFrameDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		} else {
			logger.error(LogCategories.TRAY, "No system tray supported");
		}
	}

	public void initTrayPlayerIcons() {
		initSystemTray();
		if (tray != null) {
			trayPlayerVisible = true;
			nextIcon = new TrayIcon(ImageLoader.NEXT_TRAY.getImage());
			nextIcon.setImageAutoSize(true);
			nextIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					PlayerHandler.getInstance().next(false);
				}
			});
			try {
				tray.add(nextIcon);
			} catch (AWTException e) {
				logger.error(LogCategories.TRAY, e);
			}

			stopIcon = new TrayIcon(ImageLoader.STOP_TRAY.getImage());
			stopIcon.setImageAutoSize(true);
			stopIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					PlayerHandler.getInstance().stop();
				}
			});
			try {
				tray.add(stopIcon);
			} catch (AWTException e) {
				logger.error(LogCategories.TRAY, e);
			}

			playIcon = new TrayIcon(ImageLoader.PLAY_TRAY.getImage());
			playIcon.setImageAutoSize(true);
			playIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					PlayerHandler.getInstance().play(true);
				}
			});
			try {
				tray.add(playIcon);
			} catch (AWTException e) {
				logger.error(LogCategories.TRAY, e);
			}

			previousIcon = new TrayIcon(ImageLoader.PREVIOUS_TRAY.getImage());
			previousIcon.setImageAutoSize(true);
			previousIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					PlayerHandler.getInstance().previous();
				}
			});
			try {
				tray.add(previousIcon);
			} catch (AWTException e) {
				logger.error(LogCategories.TRAY, e);
			}
		} else {
			logger.error(LogCategories.TRAY, "No system tray supported");
		}
	}

	public void setMute(boolean value) {
		if (mute != null)
			mute.setSelected(value);
	}

	public void setPlaying(boolean playing) {
		if (playing) {
			if (trayIcon != null) {
				playMenu.setText(LanguageTool.getString("PAUSE"));
				playMenu.setIcon(ImageLoader.PAUSE_TRAY_MENU);
			}
			if (playIcon != null)
				playIcon.setImage(ImageLoader.PAUSE_TRAY.getImage());
		} else {
			if (trayIcon != null) {
				playMenu.setText(LanguageTool.getString("PLAY"));
				playMenu.setIcon(ImageLoader.PLAY_TRAY_MENU);
			}
			if (playIcon != null)
				playIcon.setImage(ImageLoader.PLAY_TRAY.getImage());
		}

	}

	public void setRepeat(boolean value) {
		if (repeat != null)
			repeat.setSelected(value);
	}

	public void setShowOSD(boolean value) {
		if (showOSD != null)
			showOSD.setSelected(value);
	}

	public void setShuffle(boolean value) {
		if (shuffle != null)
			shuffle.setSelected(value);
	}

	public void setTrayIconVisible(boolean visible) {
		if (visible && !trayIconVisible) {
			initTrayIcon();
			if (tray != null)
				trayIconAdvice();
		} else {
			if (!visible && trayIconVisible) {
				tray.remove(trayIcon);
				VisualHandler.getInstance().setFrameDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				trayIconVisible = false;
			}
		}
	}

	public void setTrayPlayerVisible(boolean visible) {
		if (visible && !trayPlayerVisible) {
			initTrayPlayerIcons();
		} else {
			if (!visible && trayPlayerVisible) {
				tray.remove(previousIcon);
				tray.remove(playIcon);
				tray.remove(stopIcon);
				tray.remove(nextIcon);
				trayPlayerVisible = false;
			}
		}
	}

	public void setTrayToolTip(String msg) {
		if (trayIcon != null)
			trayIcon.setToolTip(msg);
	}

	private void trayIconAdvice() {
		// For some reason, in Linux systems display message causes Swing freeze
		if (SystemProperties.SYSTEM != OperatingSystem.LINUX)
			trayIcon.displayMessage(Constants.APP_NAME, LanguageTool.getString("TRAY_ICON_MESSAGE"), TrayIcon.MessageType.INFO);
	}
}
