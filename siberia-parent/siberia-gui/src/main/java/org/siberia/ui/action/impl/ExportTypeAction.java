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
import org.siberia.kernel.Kernel;
import org.siberia.type.SibType;
import org.siberia.ui.action.AbstractSingleTypeAction;

/**
 *
 * action called to import a new item on a siberia collection
 *
 * @author alexis
 */
public class ExportTypeAction extends AbstractSingleTypeAction
{
    
    /** Creates a new instance of ImportTypeAction */
    public ExportTypeAction()
    {   }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        SibType type = this.getType();
        
        if ( type != null )
        {   try
            {   Kernel.getInstance().getDefaultBindingManager().store(type); }
            catch(Exception ex)
            {   ex.printStackTrace(); }
        }
        else
        {   throw new RuntimeException("type is null"); }
    }
    
}
