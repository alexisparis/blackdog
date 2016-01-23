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

import java.awt.Container;
import javax.swing.AbstractButton;
import javax.swing.JToolBar;
import org.apache.log4j.Logger;
import org.siberia.ui.bar.PluginBarCustomizer;
import org.siberia.xml.schema.bar.Menubar;
import org.siberia.xml.schema.bar.MenuType;

import org.siberia.bar.customizer.MenuBarCustomizer;
import org.siberia.ui.bar.PluginI18NResolver;
/**
 *
 * @author alexis
 */
public class PluginMenuBarCustomizer extends PluginBarCustomizer<Menubar>
				     implements MenuBarCustomizer
{
    /** logger */
    private Logger logger = Logger.getLogger(PluginMenuBarCustomizer.class);
    
    /** Creates a new instance of DefaultMenuBarCustomizer */
    public PluginMenuBarCustomizer()
    {
	this.setI18NResolver(new PluginI18NResolver());
    }

    /** add a new graphical item to the bar
     *	@param parentComponent a Container
     *	@param item the xml item which allow to generate an AbstractButton
     *	@return an AbstractButton or null
     *
     *	do not add the component to the parent container
     */
    public AbstractButton createItem(Container parentComponent, Object current)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createItem(Container, Object)");
	}
	
	AbstractButton button = null;

	if (current instanceof MenuType)
	{
	    button = this.createMenu((MenuType) current);
	    if (parentComponent instanceof JToolBar)
	    {
		logger.warn("unable to proceed '" + current.getClass() + "' in a " + parentComponent.getClass());
	    }
	//this.feedComponent((JMenu)button, ((Menu)current).getMenuOrButtonOrCheck());
	}
	else
	{
	    button = super.createItem(parentComponent, current);
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createItem(Container, Object)");
	}

	return button;
    }
}
