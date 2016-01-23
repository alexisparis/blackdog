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

import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class RadioHandler {

	private static RadioHandler instance = new RadioHandler();

	private Logger logger = new Logger();

	private List<Radio> radios;

	private RadioHandler() {
	}

	public static RadioHandler getInstance() {
		return instance;
	}

	public void addRadio() {
		Radio radio = VisualHandler.getInstance().showAddRadioDialog();
		if (radio != null) {
			addRadio(radio);
			ControllerProxy.getInstance().getNavigationController().refreshRadioTreeContent();
		}
	}

	private void addRadio(Radio radio) {
		logger.info(LogCategories.HANDLER, "Adding radio");
		radios.add(radio);
		Collections.sort(radios, Radio.getComparator());
	}

	public void finish() {
		ApplicationDataHandler.getInstance().persistRadioCache(radios);
	}

	/**
	 * @return the radios
	 */
	public List<Radio> getRadios() {
		return radios;
	}

	public void readRadios() {
		radios = ApplicationDataHandler.getInstance().retrieveRadioCache();
	}

	public void removeRadio(Radio radio) {
		logger.info(LogCategories.HANDLER, "Removing radio");
		radios.remove(radio);
		ControllerProxy.getInstance().getNavigationController().refreshRadioTreeContent();
	}

}
