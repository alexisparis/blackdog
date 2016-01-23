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

package net.sourceforge.atunes.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

/**
 * Colors used on app
 * 
 */
public class ColorDefinitions {

	public static final Color TITLE_DIALOG_FONT_COLOR = Color.WHITE;

	public static final Color WARNING_COLOR = Color.RED;

	public static final Color GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR = Color.RED;

	public static void initColors() {
		UIManager.put("ToolTip.border", BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
		UIManager.put("ToolTip.background", new ColorUIResource(Color.WHITE));
		UIManager.put("ToolTip.foreground", new ColorUIResource(Color.BLACK));
	}

}
