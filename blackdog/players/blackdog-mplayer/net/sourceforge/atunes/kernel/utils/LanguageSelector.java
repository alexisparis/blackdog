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

package net.sourceforge.atunes.kernel.utils;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.handlers.DesktopHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

/**
 * This class is responsible of setting the language
 */
public class LanguageSelector {

	/**
	 * Logger
	 */
	private static Logger logger = new Logger();

	/**
	 * Sets application language. If a language is defined in the state, it's
	 * used. If not, a dialog is shown to let the user choose. The language
	 * selected is used and stored in state
	 */
	public static void setLanguage() {
		LocaleBean locale = Kernel.getInstance().state.getLocale();
		if (locale != null) {
			//TODO: Update languages that are outdated. This triggers a nag screen, so only use if no translation update was avaible for a long time for the language you add to the list.
			//The message will appear three times. It begins after seven starts of the application. Don't forget to remove the language in case of success of this measure.
			if (locale.getLocale().getISO3Language().equals("zho") || locale.getLocale().getISO3Language().equals("jpn") && Kernel.getInstance().state.getNagDialogCounter() < 10) {
				logger.info(LogCategories.START, StringUtils.getString("Outdated locale: ", locale.getLocale()));
				if (Kernel.getInstance().state.getNagDialogCounter() > 6) {
					VisualHandler.getInstance().hideSplashScreen();
					VisualHandler
							.getInstance()
							.showMessage(
									StringUtils
											.getString(LanguageTool
													.getString("The translation file you are using has not been updated for a long time. \nPlease contact us in order to refresh your favourite translation.")));
					DesktopHandler.getInstance().openURL(Constants.CONTRIBUTORS_WANTED);
				}
				Kernel.getInstance().state.setNagDialogCounter(Kernel.getInstance().state.getNagDialogCounter() + 1);
			}
			LanguageTool.setLanguage(locale.getLocale());
			logger.info(LogCategories.START, StringUtils.getString("Setting language: ", locale.getLocale()));
		} else {
			logger.info(LogCategories.START, "Language not configured; using default language");
			LanguageTool.setLanguage(null);
			Kernel.getInstance().state.setLocale(new LocaleBean(LanguageTool.getLanguageSelected()));
		}
		// Set Locale for DateUtils
		DateUtils.setLocale(Kernel.getInstance().state.getLocale().getLocale());
	}
}
