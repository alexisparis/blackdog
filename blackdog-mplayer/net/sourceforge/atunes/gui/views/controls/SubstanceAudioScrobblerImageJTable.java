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

package net.sourceforge.atunes.gui.views.controls;

import javax.swing.JTable;

import net.sourceforge.atunes.Constants;

/**
 * <p>
 * This class is needed for Substance look and feel 4.1+
 * </p>
 * <a href =
 * "https://substance.dev.java.net/servlets/ProjectForumMessageView?messageID=22522&forumID=1484">https://substance.dev.java.net/servlets/ProjectForumMessageView?messageID=22522&forumID=1484</a>
 */
public class SubstanceAudioScrobblerImageJTable extends JTable {

	private static final long serialVersionUID = 339974237840854168L;

	@Override
	public void setRowHeight(int heigth) {
		super.setRowHeight(Constants.AUDIO_SCROBBLER_IMAGE_HEIGHT + 5);
	}

}
