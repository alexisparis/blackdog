/* 
 * Siberia types : siberia plugin defining structures managed by siberia platform
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
package org.siberia.type.event;

import org.siberia.type.SibType;

/**
 *  describe an event that tell that the collection has been cleared
 *  This class extends ContentChangeEvent
 *
 * @author alexis
 */
public class ContentClearedEvent extends ContentChangeEvent
{   
    /** create a new instance of ContentChangeEvent that tell that the collection has been cleared
     *  @param source the source of the event
     *  @param propertyName the name of the property
     *	@param the content if the collection that was cleared
     */
    public ContentClearedEvent(Object source, String propertyName, SibType... content)
    {   
	super(source, propertyName, ContentClearedEvent.REMOVE, null, content);
	
	this.hasBeenCleared = true;
    }
}
