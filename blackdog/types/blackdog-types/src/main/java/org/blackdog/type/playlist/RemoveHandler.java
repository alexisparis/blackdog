/*
 * blackdog types : define kind of items maanged by blackdog
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.type.playlist;

import java.util.Collection;
import org.blackdog.type.PlayList;

/**
 *
 * allow to interact on PlayList remove action
 *
 * @author alexis
 */
public interface RemoveHandler
{
    /** called when a remove has been ordered by index on the given playlist
     *	before calling super
     *	@param playlist a PlayList
     *	@param index
     *	@param toRemove the object that has to be removed
     */
    public void preRemoveByIndex(PlayList playList, int index, Object toRemove);
    
    /** called when a remove has been ordered by index on the given playlist
     *	after calling super
     *	@param playlist a PlayList
     *	@param index
     *	@param removed the object that has been removed
     */
    public void postRemoveByIndex(PlayList playList, int index, Object removed);
    
    /** called when a remove has been ordered by reference on the given playlist
     *	before calling super
     *	@param playlist a PlayList
     *	@param toRemove the object that has to be removed
     */
    public void preRemoveByReference(PlayList playList, Object toRemove);
    
    /** called when a remove has been ordered by index on the given playlist
     *	after calling super
     *	@param playlist a PlayList
     *	@param removed the object that has been removed
     */
    public void postRemoveByReference(PlayList playList, Object removed);
    
    /** called when a remove has been ordered by giving a collection on the given playlist
     *	before calling super
     *	@param playlist a PlayList
     *	@param collectionToRemove the collection to remove
     */
    public void preRemoveByCollectionReference(PlayList playList, Collection collectionToRemove);
    
    /** called when a remove has been ordered by index on the given playlist
     *	after calling super
     *	@param playlist a PlayList
     *	@param collectionRemoved the collecton that has been removed
     */
    public void postRemoveByCollectionReference(PlayList playList, Collection collectionRemoved);
    
    
}
