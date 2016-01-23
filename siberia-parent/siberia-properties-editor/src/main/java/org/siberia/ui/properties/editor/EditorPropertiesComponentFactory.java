/* 
 * Siberia properties editor : siberia plugin defining properties editor
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
package org.siberia.ui.properties.editor;


import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import org.siberia.ui.properties.PropertiesComponentFactory;
import org.siberia.properties.XmlProperty;
import org.siberia.properties.XmlCategory;
import org.siberia.exception.ResourceException;
import org.siberia.ResourceLoader;
import org.apache.log4j.Logger;

/**
 *
 * @author alexis
 */
public class EditorPropertiesComponentFactory extends PropertiesComponentFactory
{
    
    /** logger */
    private static Logger logger = Logger.getLogger(EditorPropertiesComponentFactory.class);
    
    /** Creates a new instance of EditorPropertiesComponentFactory */
    private EditorPropertiesComponentFactory()
    {   }
    
    /** return a swing component that represents a XmlCategory
     *  @rootCategory an instanceof XmlCategory
     *  @return a component that render this element
     */
    public static JToggleButton renderRootCategory(XmlCategory category)
    {   JToggleButton button = new JToggleButton(category.getLabel());
        
        try
        {   button.setIcon(ResourceLoader.getInstance().getIconNamed(category.getIcon())); }
        catch(ResourceException e)
        {   logger.debug("unable to load image " + category.getIcon() +
                                " for category with label '" + category.getLabel() + "'");
        }
        button.setRequestFocusEnabled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFocusable(false);
        button.setToolTipText(category.getDescription());
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
//        button.setVerticalTextPosition(SwingConstants.TOP);
        
//        Dimension dim = new Dimension(140, 80);
//        button.setMinimumSize(dim);
//        button.setMaximumSize(dim);
        return button;
    }
    
    /** return a swing component that represents a XmlCategory
     *  @param category an instance of XmlCategory
     *  @return a component that render this element
     */
    public static JPanel renderCategoryItems(XmlCategory category)
    {   JPanel panel = null;
        if ( category != null )
            panel = new CategoryPropertyRenderer(category, true);
        return panel;
    }
    
    /** return a swing component that represents a XmlCategory
     *  @param category an instance of XmlCategory
     *  @return a component that render this element
     */
    public static JPanel renderCategory(XmlCategory category)
    {   JPanel panel = null;
        if ( category != null )
            panel = new CategoryPropertyRenderer(category, false);
        return panel;
    }
    
}
