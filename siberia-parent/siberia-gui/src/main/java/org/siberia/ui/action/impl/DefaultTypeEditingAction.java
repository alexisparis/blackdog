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

import java.util.List;
import java.util.Set;
import org.siberia.type.SibType;
import org.siberia.kernel.Kernel;
import org.siberia.env.PluginContext;

/**
 *
 * Default action to edit a type.
 *
 * It manages its editability according to the kind of editors installed
 *
 * @author alexis
 */
public class DefaultTypeEditingAction<E extends SibType> extends TypeEditingAction<E>
{
    
    /** Creates a new instance of DefaultTypeEditingAction */
    public DefaultTypeEditingAction()
    {   }
    
    /** set the type related to this action
     *  @return a SibType
     */
    @Override
    public void setTypes(E[] types)
    {   super.setTypes(types);
    
        boolean enabled = false;
        
        List<E> items = this.getTypes();
        if ( items != null )
        {   if ( items.size() > 0 )
            {   SibType type = items.get(0);
                
                if ( type != null )
                {   Set<PluginContext.EditorDescriptor> descs = Kernel.getInstance().getPluginContext().getEditorsFor(type.getClass());

                    if ( descs.size() > 0 )
                    {   enabled = true; }
                }
            }
        }
        
        this.setEnabled(enabled);
    }
    
}
