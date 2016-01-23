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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.Serializable;
import java.util.Date;

public class SongStats implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2392613471327847012L;
	private Date previousPlayed;
	private Date lastPlayed;
	private int timesPlayed;

	public SongStats() {
		previousPlayed = null;
		lastPlayed = new Date();
		timesPlayed = 1;
	}

	public Date getLastPlayed() {
		return lastPlayed;
	}

	public Date getPreviousPlayed() {
		return previousPlayed;
	}

	public int getTimesPlayed() {
		return timesPlayed;
	}

	public void increaseTimesPlayed() {
		this.timesPlayed++;
	}

	public void reset() {
		previousPlayed = null;
		lastPlayed = null;
		timesPlayed = 0;
	}

	public void setLastPlayed(Date lastPlayed) {
		this.previousPlayed = this.lastPlayed;
		this.lastPlayed = lastPlayed;
	}
}
