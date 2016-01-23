/* 
 * Siberia gui : siberia plugin defining basics of graphical application 
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
package org.siberia.ui.action.impl;

import java.awt.event.ActionEvent;
import org.siberia.ui.UserInterface;
import org.siberia.ui.action.GenericAction;

/**
 *
 * try to close the current application
 *
 * @author alexis
 */
public class CloseApplicationAction extends GenericAction
{
    
    /** Creates a new instance of CloseApplicationAction */
    public CloseApplicationAction()
    {	}

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
	UserInterface ui = UserInterface.getInstance();
	
	if ( ui != null )
	{
	    ui.tryToClose();
	}
    }
    
}
