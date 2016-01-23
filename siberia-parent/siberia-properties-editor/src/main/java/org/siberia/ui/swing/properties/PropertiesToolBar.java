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
package org.siberia.ui.swing.properties;
import java.awt.Dimension;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.siberia.exception.ResourceException;
import org.siberia.ResourceLoader;

/**
 *
 * @author alexis
 */
public class PropertiesToolBar extends JToolBar
{
    
    /** Creates a new instance of PropertiesToolBar */
    public PropertiesToolBar()
    {   super();
        
        this.setFloatable(false);
    }
    
    /** add a toggle button
     *  @param category the category of the button
     *  @param imageName the name of the image
     *  @param extension the extension of the file icon
     *  @return an instance of JToggleButton
     */
    public JToggleButton create(String category, String imageName, String extension)
    {   JToggleButton button = null;
        
        try
        {   button = new JToggleButton(category, ResourceLoader.getInstance().getIconNamed(imageName)); 
            button.setRequestFocusEnabled(false);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFocusable(false);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setToolTipText("View " + category + " properties");
        }
        catch(ResourceException e)
        {   e.printStackTrace(); }
        
        button.setName(category);
        
        Dimension dim = new Dimension(140, 80);
        button.setMinimumSize(dim);
        button.setMaximumSize(dim);
        
        return button;
    }
}
