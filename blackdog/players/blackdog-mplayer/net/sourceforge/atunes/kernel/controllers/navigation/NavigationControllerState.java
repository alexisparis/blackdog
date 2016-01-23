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

package net.sourceforge.atunes.kernel.controllers.navigation;

import javax.swing.JComponent;

import net.sourceforge.atunes.kernel.handlers.RepositoryHandler.SortType;

public class NavigationControllerState {

	public enum ViewMode {
		ARTIST, ALBUM, GENRE
	}

	private ViewMode viewMode = ViewMode.ARTIST;
	private SortType sortType;
	private String currentFilter;
	private int navigationView;
	private JComponent popupmenuCaller;

	public String getCurrentFilter() {
		return currentFilter;
	}

	public int getNavigationView() {
		return navigationView;
	}

	public JComponent getPopupmenuCaller() {
		return popupmenuCaller;
	}

	public SortType getSortType() {
		return sortType;
	}

	public ViewMode getViewMode() {
		return viewMode;
	}

	public void setCurrentFilter(String currentFilter) {
		this.currentFilter = currentFilter;
	}

	public void setNavigationView(int view) {
		navigationView = view;
	}

	public void setPopupmenuCaller(JComponent popupmenuCaller) {
		this.popupmenuCaller = popupmenuCaller;
	}

	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}

	public void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
	}
}
