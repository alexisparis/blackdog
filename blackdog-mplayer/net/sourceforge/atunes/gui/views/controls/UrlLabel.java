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

import java.awt.Color;

import javax.swing.UIManager;

import org.jdesktop.swingx.JXHyperlink;

public class UrlLabel extends JXHyperlink {

	private static final long serialVersionUID = -8368596300673361747L;

	public UrlLabel() {
		super();
	}

	public UrlLabel(String text, final String url) {
		super(new Link(url));
		setTextAndColor(text);
	}

	public void setText(String text, final String url) {
		setAction(new Link(url));
		setTextAndColor(text);
	}

	private void setTextAndColor(String text) {
		setText(text);
		setForeground((Color) UIManager.get("Label.foreground"));
	}
}
