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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

/**
 * Handler for global hotkeys and Intellitype keys
 */
public class HotkeyHandler implements HotkeyListener, IntellitypeListener {

	private static HotkeyHandler instance = new HotkeyHandler();

	private static Logger logger = new Logger();

	private static final int RIGHT_ARROW = 39;
	private static final int LEFT_ARROW = 37;
	private static final int UP_ARROW = 38;
	private static final int DOWN_ARROW = 40;

	private static final int CTRL_ALT_RIGHT = 1;
	private static final int CTRL_ALT_LEFT = 2;
	private static final int CTRL_ALT_UP = 3;
	private static final int CTRL_ALT_DOWN = 4;
	private static final int CTRL_ALT_P = 5;
	private static final int CTRL_ALT_S = 6;
	private static final int CTRL_ALT_W = 7;
	private static final int CTRL_ALT_M = 8;
	private static final int CTRL_ALT_I = 9;

	private static boolean supported;
	static {
		try {
			supported = JIntellitype.isJIntellitypeSupported();
		} catch (Throwable e) {
			supported = false;
			logger.debug(LogCategories.HANDLER, "JIntellitype is not supported");
		}
	}

	private boolean enabled;

	private Map<Integer, String[]> hotkeyDescription = new HashMap<Integer, String[]>();

	private HotkeyHandler() {
	}

	/**
	 * Checks if hotkeys and Intellitype are supported
	 * 
	 * @return if hotkeys and Intellitype are supported
	 */
	public static boolean areHotkeysAndIntellitypeSupported() {
		return supported;
	}

	public static HotkeyHandler getInstance() {
		return instance;
	}

	/**
	 * Enables or disables hotkeys
	 * 
	 * @param enable
	 *            if hotkeys should be enabled
	 */
	public void enableHotkeys(boolean enable) {
		if (!areHotkeysAndIntellitypeSupported()) {
			return;
		}
		if (enable && !enabled) {
			// Next
			registerHotkey(CTRL_ALT_RIGHT, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, RIGHT_ARROW, LanguageTool.getString("NEXT"));
			// Previous
			registerHotkey(CTRL_ALT_LEFT, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, LEFT_ARROW, LanguageTool.getString("PREVIOUS"));
			//Volume up
			registerHotkey(CTRL_ALT_UP, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, UP_ARROW, LanguageTool.getString("VOLUME_UP"));
			// Volume down
			registerHotkey(CTRL_ALT_DOWN, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, DOWN_ARROW, LanguageTool.getString("VOLUME_DOWN"));
			// Play
			registerHotkey(CTRL_ALT_P, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'P', LanguageTool.getString("PAUSE"));
			// Stop
			registerHotkey(CTRL_ALT_S, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'S', LanguageTool.getString("STOP"));
			// Toggle window visibility
			registerHotkey(CTRL_ALT_W, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'W', LanguageTool.getString("TOGGLE_WINDOW_VISIBILITY"));
			// Mute
			registerHotkey(CTRL_ALT_M, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'M', LanguageTool.getString("MUTE"));
			// Show Osd info
			registerHotkey(CTRL_ALT_I, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'I', LanguageTool.getString("SHOW_OSD"));

			JIntellitype.getInstance().addHotKeyListener(this);
			JIntellitype.getInstance().addIntellitypeListener(this);
			enabled = true;
		}
		if (!enable && enabled) {
			unregisterHotkey(CTRL_ALT_RIGHT);
			unregisterHotkey(CTRL_ALT_LEFT);
			unregisterHotkey(CTRL_ALT_UP);
			unregisterHotkey(CTRL_ALT_DOWN);
			unregisterHotkey(CTRL_ALT_P);
			unregisterHotkey(CTRL_ALT_S);
			unregisterHotkey(CTRL_ALT_W);
			unregisterHotkey(CTRL_ALT_M);
			unregisterHotkey(CTRL_ALT_I);
			JIntellitype.getInstance().removeHotKeyListener(this);
			JIntellitype.getInstance().removeIntellitypeListener(this);
			enabled = false;
		}
	}

	/**
	 * Release JIntellitype ressources
	 */
	public void finish() {
		if (!areHotkeysAndIntellitypeSupported()) {
			return;
		}
		JIntellitype.getInstance().cleanUp();
	}

	/**
	 * Returns an array with hotkeys and their descrption
	 * 
	 * @return an array with hotkeys and their descrption
	 */
	public String[][] getHotkeyDescription() {
		String[][] d = new String[hotkeyDescription.size()][];
		int i = 0;
		for (Integer key : hotkeyDescription.keySet()) {
			d[i++] = hotkeyDescription.get(key);
		}
		return d;
	}

	@Override
	public void onHotKey(final int id) {
		logger.debug(LogCategories.HANDLER, "Hotkey " + id);
		switch (id) {
		case CTRL_ALT_RIGHT: {
			PlayerHandler.getInstance().next(false);
			break;
		}
		case CTRL_ALT_LEFT: {
			PlayerHandler.getInstance().previous();
			break;
		}
		case CTRL_ALT_UP: {
			PlayerHandler.getInstance().volumeUp();
			break;
		}
		case CTRL_ALT_DOWN: {
			PlayerHandler.getInstance().volumeDown();
			break;
		}
		case CTRL_ALT_P: {
			PlayerHandler.getInstance().play(true);
			break;
		}
		case CTRL_ALT_S: {
			PlayerHandler.getInstance().stop();
			break;
		}
		case CTRL_ALT_W: {
			VisualHandler.getInstance().toggleWindowVisibility();
			break;
		}
		case CTRL_ALT_M: {
			ControllerProxy.getInstance().getPlayerControlsController().setMute(!PlayerHandler.getInstance().isMute());
			SystemTrayHandler.getInstance().setMute(!PlayerHandler.getInstance().isMute());
			PlayerHandler.getInstance().setMute(!PlayerHandler.getInstance().isMute());
			break;
		}
		case CTRL_ALT_I: {
			VisualHandler.getInstance().showOSDDialog();
			break;
		}
		}
	}

	@Override
	public void onIntellitype(final int command) {
		logger.debug(LogCategories.HANDLER, "Intellitype command " + command);
		switch (command) {
		case JIntellitypeConstants.APPCOMMAND_MEDIA_NEXTTRACK: {
			PlayerHandler.getInstance().next(false);
			break;
		}
		case JIntellitypeConstants.APPCOMMAND_MEDIA_PREVIOUSTRACK: {
			PlayerHandler.getInstance().previous();
			break;
		}
		case JIntellitypeConstants.APPCOMMAND_MEDIA_PLAY_PAUSE: {
			PlayerHandler.getInstance().play(true);
			break;
		}
		case JIntellitypeConstants.APPCOMMAND_MEDIA_STOP: {
			PlayerHandler.getInstance().stop();
			break;
		}
		case JIntellitypeConstants.APPCOMMAND_VOLUME_UP: {
			PlayerHandler.getInstance().volumeUp();
			break;
		}
		case JIntellitypeConstants.APPCOMMAND_VOLUME_DOWN: {
			PlayerHandler.getInstance().volumeDown();
			break;
		}
		case JIntellitypeConstants.APPCOMMAND_VOLUME_MUTE: {
			ControllerProxy.getInstance().getPlayerControlsController().setMute(!PlayerHandler.getInstance().isMute());
			SystemTrayHandler.getInstance().setMute(!PlayerHandler.getInstance().isMute());
			PlayerHandler.getInstance().setMute(!PlayerHandler.getInstance().isMute());
			break;
		}
		}
	}

	private void registerHotkey(int id, int mod, int key, String description) {
		JIntellitype.getInstance().registerSwingHotKey(id, mod, key);
		hotkeyDescription.put(id, new String[] { InputEvent.getModifiersExText(mod) + "+" + KeyEvent.getKeyText(key), description });
	}

	private void unregisterHotkey(int id) {
		JIntellitype.getInstance().unregisterHotKey(id);
		hotkeyDescription.remove(id);
	}
}
