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
package org.siberia.bar.customizer;

import java.awt.Container;
import javax.swing.AbstractButton;
import javax.swing.JToolBar;
import org.siberia.xml.schema.bar.TypeMenu;
import org.siberia.xml.schema.bar.MenuType;

/**
 *
 * Default implementation of a PopupBarCustomizer
 *
 * @author alexis
 */
public class DefaultPopupMenuCustomizer extends DefaultBarCustomizer<TypeMenu>
				        implements PopupMenuCustomizer
{
    
    /**
     * Creates a new instance of DefaultPopupMenuCustomizer
     */
    public DefaultPopupMenuCustomizer()
    {	}
    
}
