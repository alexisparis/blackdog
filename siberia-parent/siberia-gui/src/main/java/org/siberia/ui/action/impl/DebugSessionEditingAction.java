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

import org.siberia.debug.type.ColdDebugSession;
import java.util.List;
import org.siberia.ui.action.TypeReferenceAction.NoItemAnymoreException;
import org.siberia.utilities.util.Parameter;

/**
 *
 * Action that force the edition of a ColdURL
 *
 * @author alexis
 */
public class DebugSessionEditingAction extends TypeReferenceEditingAction<ColdDebugSession>
{
    
    /** Creates a new instance of WebBrowserEditingAction */
    public DebugSessionEditingAction()
    {   super(); }
    
    /** method which create the type if the reference has been broken
     *  this method is called by the getType when reference is broken
     *  @param list a list of Parameter
     *  @return an instanceof ColdDebugSession
     *
     *  @exception NoItemAnymoreException when no item will be created for greater or equal index
     */
    protected ColdDebugSession createType(List<Parameter> list, int index) throws NoItemAnymoreException
    {   
        if ( index > 0 ) 
        {   throw new NoItemAnymoreException(); }
         
        return new ColdDebugSession();
    }
}
