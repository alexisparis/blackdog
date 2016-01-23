/* 
 * Siberia browser : siberia plugin defining a simple web browser
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

import java.beans.PropertyVetoException;
import org.siberia.type.SibType;
import org.siberia.type.SibHtmlURL;
import java.util.List;
import org.siberia.ui.action.TypeReferenceAction.NoItemAnymoreException;
import org.siberia.utilities.util.Parameter;
import org.siberia.ui.action.impl.TypeReferenceEditingAction;

/**
 *
 * Action that force the edition of a SibURL
 *
 * @author alexis
 */
public class WebBrowserEditingAction extends TypeReferenceEditingAction<SibHtmlURL>
{
    
    /** Creates a new instance of WebBrowserEditingAction */
    public WebBrowserEditingAction()
    {   super(); }
    
    /** method which create the type if the reference has been broken
     *  this method is called by the getType when reference is broken
     *  @param list a list of Parameter
     *  @return an instanceof SibHtmlURL
     *  @exception NoItemAnymoreException when no item will be created for greater or equal index
     */
    protected SibHtmlURL createType(List<Parameter> list, int index) throws NoItemAnymoreException
    {   
        if ( index > 0 )
        {   throw new NoItemAnymoreException(); }
        
        SibHtmlURL type = new SibHtmlURL();
        
        try
        {   type.setName("Web browser"); }
        catch(PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        return type;
    }
    
}
