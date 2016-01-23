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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * Implementation of a sorted list based on an ArrayList
 *  It allow to add a new Item by dichotomie
 *
 * @author alexis
 */
public class SortedList<E extends Comparable> extends ArrayList<E>
{   
    /** Creates a new instance of SortedList */
    public SortedList()
    {	}

    public boolean add(E e)
    {
	boolean result = false;

	E processedItem = null;
	if ( e != null )
	{
	    processedItem = e;

//	    synchronized(this)
	    {
		if ( ! this.contains(processedItem) )
		{
		    /** search the index where to put the new item */
		    int indexTmp = -1;

		    if ( this.size() == 0 )
		    {
			indexTmp = 0;
		    }
		    else
		    {
			int size = this.size();

			/** in case addItem is called on a ordered list of item, then 
			 *  try to see if processedItem is not greater that the last item
			 */
			if ( processedItem.compareTo(this.get(size - 1)) > 0 )
			{
			    indexTmp = size;
			}
			else if ( processedItem.compareTo(this.get(0)) <= 0 )
			{
			    indexTmp = 0;
			}
			else
			{
			    /** included indexes of search */
			    int startIndex = 0;
			    int endIndex   = size - 1;
			    
//			    System.out.println("trying to add " + e);
//			    System.out.println("size : " + size);
//			    System.out.println("start index : " + startIndex);
//			    System.out.println("end index   : " + endIndex);
//			    
//			    for(int i = 0; i < this.size(); i++)
//			    {
//				System.out.println("\t" + this.get(i));
//			    }

			    /** the index will be between 0 and size - 1 */
			    while(indexTmp == -1)
			    {
				int dicIndex = (endIndex - startIndex) / 2 + startIndex;
				
//				System.out.println("dicIndex : " + dicIndex);

				E itemAtDicIndex = this.get(dicIndex);

				int compareResult = processedItem.compareTo(itemAtDicIndex);
				
				if ( compareResult > 0 )
				{
				    if ( dicIndex == startIndex )
				    {
					startIndex = dicIndex + 1;
				    }
				    else
				    {
					startIndex = dicIndex;
				    }
				}
				else if ( compareResult == 0 )
				{
				    indexTmp = dicIndex;
				}
				else
				{
				    if ( dicIndex == endIndex )
				    {
					endIndex = dicIndex - 1;
				    }
				    else
				    {
					endIndex = dicIndex;
				    }
				}

				if ( startIndex == endIndex )
				{
				    indexTmp = startIndex;
				}
			    }
			}
		    }


		    final int index = indexTmp;

		    result = true;

		    this.add(index, processedItem);
		}
	    }
	}
	
	return result;
    }
    
    /** method which determine if the given collection of item
     *	should be added one by one to benefit from dichotomy.
     *	if it returns false, then the element will be added and after the collection will be sorted
     *	@param c a Collection to add
     *	@return true to add element one by one by dichotomy
     */
    protected boolean useDichotomyToAdd(Collection<? extends E> c)
    {
	boolean result = true;
	
	if ( c != null )
	{
	    if ( this.size() <= 5 ) // particularly, if size is 0, it is better to add all and sort after
	    {
		result = false;
	    }
	    else
	    {
		if ( c.size() > this.size() )
		{
		    result = false;
		}
	    }
	}
	
	return result;
    }

    public boolean addAll(Collection<? extends E> c)
    {
	boolean result = true;
	
	if ( this.useDichotomyToAdd(c) )
	{
	    /** set of items allready added */
	    Collection<E> added = new ArrayList<E>(c.size());

	    Iterator<? extends E> it = c.iterator();
	    while(it.hasNext())
	    {
		E current = it.next();

		if ( this.add(current) )
		{
		    added.add(current);
		}
		else
		{
		    result = false;

		    /** remove the previously added items */
		    Iterator<? extends E> ite = added.iterator();
		    while(ite.hasNext())
		    {
			E toRemove = ite.next();

			this.remove(toRemove);
		    }

		    break;
		}
	    }
	}
	else
	{
	    this.addAll(c);
	    
	    Collections.sort(this);
	}
	
	return result;
    }
    

}
