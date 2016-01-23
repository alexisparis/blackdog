/* 
 * TransSiberia-ui : siberia plugin frontend for TransSiberia
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
package org.siberia.trans.ui.property;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.siberia.ResourceLoader;
import org.siberia.TransSiberiaPlugin;
import org.siberia.ui.swing.combo.EnumListCellRenderer;
import org.siberia.xml.schema.pluginarch.ModuleCategory;

/**
 *
 * specific renderer that is related to xml jaxb ModuleCategory generated in plugin transSiberia
 *
 * @author alexis
 */
public class ModuleCategoryListCellRenderer extends EnumListCellRenderer
{
    
    /** Creates a new instance of ModuleCategoryListCellRenderer */
    public ModuleCategoryListCellRenderer()
    {
	super();
	
	ResourceBundle rb = null;
		
	try
	{
	    rb = ResourceLoader.getInstance().getResourceBundle(
		    TransSiberiaPlugin.PLUGIN_ID, ModuleCategory.class.getName());
	}
	catch (MissingResourceException ex)
	{
	    ex.printStackTrace();
	}
	catch (Exception ex)
	{
	    ex.printStackTrace();
	}
	
	this.setI18nRb(rb);
    }
    
}
