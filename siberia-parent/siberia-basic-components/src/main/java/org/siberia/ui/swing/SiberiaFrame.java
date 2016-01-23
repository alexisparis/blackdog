/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing;

import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;
import org.apache.log4j.Logger;

/**
 *
 * Frame that allow to change simply its icon
 *
 * @author alexis
 */
public class SiberiaFrame extends JFrame
{
    /** logger */
    private Logger logger = Logger.getLogger(SiberiaFrame.class);
    
    /** Creates a new instance of ColdFrame */
    public SiberiaFrame()
    {   super();
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    /** set the icon of the frame
     *  @param icon a String representing a png image resource
     */
    public void setIcon(String icon)
    {       
        /* change the main icon of the frame */
        try
        {   Image i = ResourceLoader.getInstance().getImageNamed(icon);
            this.setIconImage(i);
        }
        catch(ResourceException e)
        {   logger.error("Unable to find icon main-icon.png for main frame"); }
	
	SwingUtilities.updateComponentTreeUI(null);
    }
}
