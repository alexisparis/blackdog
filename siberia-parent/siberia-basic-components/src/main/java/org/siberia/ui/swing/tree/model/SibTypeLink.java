/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.tree.model;

import java.lang.ref.WeakReference;
import org.siberia.type.SibType;

/**
 *
 * link to a SibType object
 *
 * @author alexis
 */
public class SibTypeLink
{
    /** weak reference to a SibType */
    private WeakReference<SibType> item = null;
    
    /** Creates a new instance of SibTypeLink
     *	@param item the item it is linked to
     */
    public SibTypeLink(SibType item)
    {
	this.item = new WeakReference<SibType>(item);
    }
    
    /** return the item it is linked to
     *	@return a SibType
     */
    public SibType getLinkedItem()
    {
	SibType type = (this.item == null ? null : this.item.get());
	
	return type;
    }
    
    /** reinitialize the link
     *	@param object the new linkied item
     */
    void updateLinkedItem(SibType object)
    {
	this.item = new WeakReference<SibType>(object);
    }

    @Override
    public String toString()
    {
	return "link to " + this.getLinkedItem();
    }
    
}
