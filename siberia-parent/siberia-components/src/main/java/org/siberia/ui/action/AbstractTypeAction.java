/* 
 * Siberia components : siberia plugin for graphical components
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
package org.siberia.ui.action;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.siberia.type.SibType;

/**
 *
 * Default implementation of a TypeAction
 *
 * the reference to the type is a hard reference
 *
 * @author alexis
 */
public abstract class AbstractTypeAction<E extends SibType> extends GenericAction
                                                            implements TypeAction<E>
{
    /** weak reference to the type */
    private List<WeakReference<E>> types = null;
    
    /** Creates a new instance of DefaultTypeAction */
    public AbstractTypeAction()
    {   }
    
    /** set the type related to this action
     *  @param types an array SibType
     */
    public void setTypes(E... types)
    {   
	if ( types == null || types.length == 0 )
	{
	    this.types = null;
	}
	else
	{
	    this.types = new ArrayList<WeakReference<E>>(types.length);
	    for(int i = 0; i < types.length; i++)
	    {
		this.types.add(new WeakReference<E>(types[i]));
	    }
	}
    }

    /** return the Objects related to this action
     *  @return the Objects related to this action
     */
    public List<E> getTypes()
    {   
	List<E> copy = null;
	
	/** build a new list with reference */
	if ( this.types != null && this.types.size() > 0 )
	{
	    copy = new ArrayList<E>(this.types.size());
	    
	    for(int i = 0; i < this.types.size(); i++)
	    {
		WeakReference<E> currentRef = this.types.get(i);
		
		if ( currentRef != null )
		{
		    E currentPtr = currentRef.get();
		    
		    if ( currentPtr != null )
		    {
			copy.add(currentPtr);
		    }
		}
	    }
	}
	return copy;
    }
    
}
