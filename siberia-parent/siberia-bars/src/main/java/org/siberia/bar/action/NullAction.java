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
package org.siberia.bar.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * Action that does nothing !! 
 * action used when no action is assignated to a bar item
 *
 * @author alexis
 */
public class NullAction extends AbstractAction
{
    
    /** Creates a new instance of NullAction */
    public NullAction()
    {   /* do nothing */ }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {   /* do nothing */ }
    
    public void setA(String value)
    {   /* do nothing */ }
    
}
