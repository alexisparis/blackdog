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
package org.siberia.bar.factory;

import java.util.List;
import org.siberia.bar.customizer.DefaultMenuBarCustomizer;
import org.siberia.bar.customizer.MenuBarCustomizer;
import org.siberia.xml.schema.bar.Menubar;

/**
 *
 * @author alexis
 */
class MenubarMerger extends AbstractMenubarMerger<Menubar, MenuBarCustomizer>
{
    /**
     * create a new MenubarMerger
     * 
     * @param list a list of menubar to merge. the order of the list is important because, the first menubar is<br>
     *  the menubar with the weakest priority.
     */
    public MenubarMerger(List<Menubar> list)
    {
	this(list, new DefaultMenuBarCustomizer());
    }

    /**
     * create a new MenubarMerger
     * 
     * @param list a list of menubar to merge. the order of the list is important because, the first menubar is<br>
     *  the menubar with the weakest priority.
     * @param customizer a MenuBarCustomizer
     */
    public MenubarMerger(List<Menubar> list, MenuBarCustomizer customizer)
    {
	super(list, customizer);
    }
    
}
