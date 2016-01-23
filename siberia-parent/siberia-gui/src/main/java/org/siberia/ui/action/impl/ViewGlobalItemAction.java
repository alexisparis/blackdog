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

import org.siberia.kernel.Kernel;
import org.siberia.kernel.resource.ColdResource;
import org.siberia.type.SibType;

/**
 *
 * Action that allow to edit if it is not or just select the component that represents a resource in the Kernel
 *
 * @author alexis
 */
public abstract class ViewGlobalItemAction extends TypeEditingAction<SibType>
{
    
    /** Creates a new instance of ViewResourceAction
     *  @param globalItemCode the code associated with the global item
     */
    public ViewGlobalItemAction(String globalItemCode)
    {   super();
        
        this.setTypes(Kernel.getInstance().getResources().
		getGlobalItem(globalItemCode));
	
    }
    
}
