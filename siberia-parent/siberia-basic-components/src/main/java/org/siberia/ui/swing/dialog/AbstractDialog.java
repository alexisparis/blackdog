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
package org.siberia.ui.swing.dialog;

import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * Abstract class for dialog window used in this application
 *
 * @author alexis
 */
public class AbstractDialog extends JDialog
{
    /* panel of the dialog */
    private JPanel panel;
    
    /* the parent frame */
    private Window frame;
    
    /** Creates a new instance of AbstractDialog */
    public AbstractDialog(Window frame, String title)
    {   super(frame, title, ModalityType.APPLICATION_MODAL);
        this.setResizable(false);
        
        this.frame = frame;
        this.panel = new JPanel();
        
        this.add(this.panel);
    }
    
    /** return the panel to add component inside
     *  @return a JPanel
     **/
    public JPanel getPanel()
    {   return this.panel; }
    
    /** show the dialog in the middle of the owner **/
    public void display()
    {   this.pack();
        this.setLocationRelativeTo(this.frame);
        
        Point point = new Point();
        if( this.frame == null )
        {   if ( this.getOwner() instanceof Window )
                this.frame = (Frame)this.getOwner();
        }
        
        if ( this.frame != null )
        {   point.setLocation( this.frame.getLocation().getX() + (int)(this.frame.getWidth() - this.getWidth()) / 2 ,
                               this.frame.getLocation().getY() + (int)(this.frame.getHeight() - this.getHeight()) / 2 );
        }
        
        this.setLocation(point);
        this.setVisible(true);
	
	if ( this.isModal() )
	{
	    this.dispose();
	}
    }
    
}
