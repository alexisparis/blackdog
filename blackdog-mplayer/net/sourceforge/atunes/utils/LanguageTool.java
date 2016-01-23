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

package net.sourceforge.atunes.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

/**
 * @author fleax
 * 
 */
public class LanguageTool {

	private static final String BUNDLE_NAME = StringUtils.getString(Constants.TRANSLATIONS_DIR, ".MainBundle");

	/**
	 * Class loader for finding resources in working directory.
	 */
	private static final ClassLoader WD_CLASS_LOADER = new ClassLoader() {
		@Override
		public URL getResource(String name) {
			try {
				URL result = super.getResource(name);
				if (result != null) {
					return result;
				}
				return (new File(name)).toURI().toURL();
			} catch (MalformedURLException ex) {
				return null;
			}
		}
	};

	/**
	 * Language file resource bundle
	 */
	private static ResourceBundle languageBundle;

	/**
	 * Language selected in dialog
	 */
	private static Locale languageSelected;

	/**
	 * Return all available languages
	 * 
	 * @return
	 */
	public static List<Locale> getLanguages() {
		File transDir = new File(Constants.TRANSLATIONS_DIR);
		File[] files = transDir.listFiles();
		List<Locale> translations = new ArrayList<Locale>();
		for (File element : files) {
			String fileName = element.getName();
			if (element.isFile() && fileName.toLowerCase().endsWith(".properties") && fileName.contains("_")) {
				String[] name = fileName.substring(fileName.indexOf('_') + 1, fileName.lastIndexOf('.')).split("_", 3);
				(new Logger()).info(LogCategories.FILE_READ, Arrays.toString(name));
				Locale locale = Locale.getDefault();
				switch (name.length) {
				case 1:
					locale = new Locale(name[0]);
					break;
				case 2:
					locale = new Locale(name[0], name[1]);
					break;
				case 3:
					locale = new Locale(name[0], name[1], name[2]);
					break;
				}
				translations.add(locale);
			}
		}
		return translations;
	}

	/**
	 * Return the name of the selected language
	 * 
	 * @return
	 */
	public static Locale getLanguageSelected() {
		return languageSelected;
	}

	/**
	 * Get a string for a given key for current language
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		if (languageBundle != null) {
			String result;
			try {
				result = languageBundle.getString(key);
			} catch (MissingResourceException e) {
				return key;
			}
			return result;
		}
		return key;
	}

	/**
	 * Sets language to use in application
	 * 
	 * @param fileName
	 */
	public static void setLanguage(Locale locale) {
		if (locale == null) {
			languageBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault(), WD_CLASS_LOADER);
		} else {
			languageBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, WD_CLASS_LOADER);
		}
		languageSelected = languageBundle.getLocale();
	}
}
