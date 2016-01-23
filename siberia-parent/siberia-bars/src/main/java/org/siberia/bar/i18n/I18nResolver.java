/* =============================================================================
 * Siberia bars
 * =============================================================================
 *
 * Project Lead:  Alexis Paris
 *
 * (C) Copyright 2007, by Alexis Paris.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.bar.i18n;

import java.util.ResourceBundle;

/**
 *
 * define an object that is able to create a ResourceBundle according to 
 * an internationalization resources
 *
 * @author alexis
 */
public interface I18nResolver
{
    /** get the ResourceBundle according to a String representing an internationalization resource
     *	@param i18nResource the reference to an internationalization resource
     *	@return a ResourceBundle
     */
    public ResourceBundle getResource(I18NResources i18nResources);
}
