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
package org.siberia.base.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author alexis
 */
public class CollectionIntersector<T>
{
    /**
     * Creates a new instance of CollectionIntersector
     */
    public CollectionIntersector()
    {	}
    
    /** create a Set that represents the intersection of the given collection
     *	@param collections an array of collections
     *	@return a Set
     */
    public Set<T> intersection(Collection<T>... collections)
    {
	Set<T> result = null;
	
	if ( collections != null && collections.length > 0 )
	{
	    boolean containsNullOrEmptyCollection = false;
	    
	    for(int i = 0; i < collections.length && ! containsNullOrEmptyCollection; i++)
	    {
		Collection currentCollection = collections[i];
		
		if ( currentCollection == null || currentCollection.isEmpty() )
		{
		    containsNullOrEmptyCollection = true;
		}
	    }
	    
	    if ( ! containsNullOrEmptyCollection )
	    {
		result = new HashSet<T>(collections[0]);
		    
		/** collection fo items to remove from result */
		Set<T> toRemove = null;
		
		for(int i = 1; i < collections.length; i++)
		{
		    Collection<T> currentCollection = collections[i];

		    /* currentCollection is non null */
		    Iterator<T> it = result.iterator();
		    
		    if ( toRemove != null )
		    {
			toRemove.clear();
		    }
		    
		    while(it.hasNext())
		    {
			T currentItem = it.next();
			
			if ( ! currentCollection.contains(currentItem) )
			{
			    if ( toRemove == null )
			    {
				toRemove = new HashSet<T>(result.size());
			    }
			    
			    toRemove.add(currentItem);
			}
		    }
		    
		    if ( toRemove != null && toRemove.size() > 0 )
		    {
			Iterator<T> itemsToRemove = toRemove.iterator();
			
			while(itemsToRemove.hasNext())
			{
			    result.remove(itemsToRemove.next());
			}
		    }
		}
	    }
	}
	
	if ( result == null )
	{
	    result = Collections.emptySet();
	}
	
	return result;
    }
    
}
