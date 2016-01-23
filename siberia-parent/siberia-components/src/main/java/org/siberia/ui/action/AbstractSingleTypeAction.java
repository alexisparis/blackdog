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

import java.util.List;
import org.apache.log4j.Logger;
import org.siberia.type.SibType;

/**
 *
 * Default implementation of a TypeAction for a single type
 *
 * the reference to the type is a hard reference
 *
 * @author alexis
 */
public abstract class AbstractSingleTypeAction<E extends SibType> extends AbstractTypeAction<E>
{
    /** logger */
    private Logger logger = Logger.getLogger(AbstractSingleTypeAction.class);
    
    /** hard reference to the type */
    private E[]    types  = null;
    
    /** Creates a new instance of DefaultTypeAction */
    public AbstractSingleTypeAction()
    {   }
    
    /** return the type related to his action
     *  @return a E
     */
    public E getType()
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering getType()");
	}
	
	E item = null;
        List<E> items = this.getTypes();
        if ( items != null )
        {   if ( items.size() > 0 )
            {   item = items.get(0); }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getType() returns " + item);
	    logger.debug("exiting getType()");
	}
        
        return item;
    }
    
}
