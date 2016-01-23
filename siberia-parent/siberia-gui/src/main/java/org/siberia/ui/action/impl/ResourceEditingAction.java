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

import java.util.Iterator;
import java.util.List;
import org.siberia.kernel.Kernel;
import org.siberia.type.SibType;
import org.siberia.ui.action.TypeReferenceAction.NoItemAnymoreException;
import org.siberia.utilities.util.Parameter;

/**
 *
 * Edit a resource registered by the KernelResources
 *
 * @author alexis
 */
public class ResourceEditingAction extends TypeReferenceEditingAction<SibType>
{   
    /** method which create the type if the reference has been broken
     *  this method is called by the getType when reference is broken
     *  @param list a list of Parameter
     *  @return an instanceof SibType
     *
     *  @exception NoItemAnymoreException when no item will be created for greater or equal index
     */
    protected SibType createType(List<Parameter> list, int index) throws NoItemAnymoreException
    {   
        if ( index > 0 )
        {   throw new NoItemAnymoreException(); }
        
        SibType type = null;
        if ( list != null )
        {   Iterator<Parameter> it = list.iterator();
            while(it.hasNext())
            {   Parameter param = it.next();
                
                if ( param.getName().equals("id") )
                {   if ( param.getValue() instanceof String )
                    {   type = Kernel.getInstance().getResources().getResource((String)param.getValue()); }
//                        if ( type != null )
//                        {   this.setType(c);
//                            /* initialiaze the icon */
//                            try
//                            {   String resPath = TypeInformationProvider.getIconResource(c);
//                                if ( resPath != null )
//                                {   Icon i = ResourceLoader.getInstance().getIconNamed(resPath);
//                                    this.setIcon(i);
//                                }
//                            }
//                            catch(ResourceException e)
//                            {   }
//                        }
//                    }
                }
            }
        }
        return type;
    }
    
}
