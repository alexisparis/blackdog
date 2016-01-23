/* 
 * Siberia components : siberia plugin for graphical components
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
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

package org.siberia.ui.bar.customizer;

import org.siberia.ui.bar.PluginBarCustomizer;
import org.siberia.xml.schema.bar.TypeMenu;
import org.siberia.bar.customizer.PopupMenuCustomizer;
import org.siberia.ui.bar.PluginI18NResolver;

/**
 *
 * @author alexis
 */
public class PluginPopupMenuCustomizer extends PluginBarCustomizer<TypeMenu>
				       implements PopupMenuCustomizer
{
    /**
     * Creates a new instance of DefaultPopupMenuCustomizer
     */
    public PluginPopupMenuCustomizer()
    {
	this.setI18NResolver(new PluginI18NResolver());
    }
}
