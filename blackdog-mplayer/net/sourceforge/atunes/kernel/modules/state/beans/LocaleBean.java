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

package net.sourceforge.atunes.kernel.modules.state.beans;

import java.beans.ConstructorProperties;
import java.util.Locale;

/**
 * Bean for java.util.Locale
 */
public class LocaleBean {

	private String language;
	private String country;

	/**
	 * Constructs LocaleBean from a given java.util.Locale
	 * 
	 * @param locale
	 *            locale
	 */
	public LocaleBean(Locale locale) {
		this.language = locale.getLanguage();
		this.country = locale.getCountry();
	}

	@ConstructorProperties( { "language", "country" })
	public LocaleBean(String language, String country) {
		this.language = language;
		this.country = country;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Get suitable java.util.Locale object
	 * 
	 * @return A suitable java.util.Locale object
	 */
	public Locale getLocale() {
		return new Locale(language, country);
	}

}
