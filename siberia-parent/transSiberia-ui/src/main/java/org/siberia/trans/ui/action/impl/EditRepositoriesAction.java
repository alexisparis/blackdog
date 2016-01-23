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
package org.siberia.trans.ui.action.impl;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.siberia.ResourceLoader;
import org.siberia.trans.TransSiberia;
import org.siberia.trans.ui.editor.impl.plugin.RepositoriesDialog;

/**
 *
 * Action that open a dialog that allow to edit registered Siberia repositories
 *
 * @author alexis
 */
public class EditRepositoriesAction extends TransSiberianAction
{   
    /* RepositoriesDialog */
    private RepositoriesDialog dialog        = null;
    
    /** Creates a new instance of EditRepositoriesAction */
    public EditRepositoriesAction()
    {	
	this.setEnabled( ResourceLoader.getInstance().isDebugEnabled() );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
	if ( this.dialog == null )
	{
	    Window window = null;
	    
	    if ( e.getSource() instanceof Component )
	    {
		window = SwingUtilities.getWindowAncestor((Component)e.getSource());
	    }
	    
	    this.dialog = new RepositoriesDialog(window, this.getTransSiberia());
	}
	
	this.dialog.display();
	
	/** force to build a new dialog each time for now */
	this.dialog = null;
    }
    
}
