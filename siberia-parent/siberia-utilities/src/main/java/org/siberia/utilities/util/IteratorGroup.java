/* 
 * Siberia utilities : siberia plugin providing severall utilities classes
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
package org.siberia.utilities.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * Iterator over an array of Iterators or Iterables
 *
 * @author alexis
 */
public class IteratorGroup<T> implements Iterator<T>
{
    /** array of sub Iterator */
    private Iterator<T>[]  iterators       = null;
    
    /** index of the current iterator */
    private int            index           = 0;
    
    /** current iterator */
    private Iterator<T>    currentIterator = null;
    
    /** Creates a new instance of IterableIterator
     *  @param iterators an array of Iterator
     */
    public IteratorGroup(Iterator<T>... iterators)
    {   this.iterators = iterators; }
    
    /** Creates a new instance of IterableIterator
     *  @param iterables an array of Iterable
     */
    public IteratorGroup(Iterable<T>... iterables)
    {   this.iterators = (iterables == null ? null : (Iterator<T>[])new Iterator[iterables.length]);
        
        if ( iterables != null )
        {   for(int i = 0; i < iterables.length; i++)
            {   Iterable<T> current = iterables[i];
                Iterator<T> iterator = null;
                if ( current != null )
                    iterator = current.iterator();
                this.iterators[i] = iterator;
            }
        }
    }
    
    /** return the current iterator
     *  @return an Iterator or null if the iterator has finished
     */
    private Iterator<T> getCurrentIterator()
    {   if ( this.iterators == null )
        {   this.currentIterator = null; }
        else
        {   if ( currentIterator == null || ! currentIterator.hasNext() )
            {   if ( index < this.iterators.length )
                {   boolean gotOne = false;
                    for(int i = index; i < this.iterators.length; i++)
                    {   /* find the next valid iterator */
                        currentIterator = this.iterators[i];
                        
                        if ( currentIterator != null && currentIterator.hasNext() )
                        {   index = i;
                            gotOne = true;
                            break;
                        }
                    }
                    
                    /* if we find no iterator, then reinitialize current to null */
                    if ( ! gotOne )
                        this.currentIterator = null;
                }
            }
        }
        return this.currentIterator;
    }

    /**
     * Returns the next element in the iteration.  Calling this method
     * repeatedly until the {@link #hasNext()} method returns false will
     * return each element in the underlying collection exactly once.
     * 
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public T next()
    {   Iterator<T> it = this.getCurrentIterator();
        
        if ( it == null )
            throw new NoSuchElementException();
        else
            return it.next();
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     * 
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext()
    {   Iterator<T> it = this.getCurrentIterator();
        
        if ( it == null )
            return false;
        else
            return it.hasNext();
    }

    /**
     * 
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     * 
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     * 		  operation is not supported by this Iterator.
     * @exception IllegalStateException if the <tt>next</tt> method has not
     * 		  yet been called, or the <tt>remove</tt> method has already
     * 		  been called after the last call to the <tt>next</tt>
     * 		  method.
     */
    public void remove()
    {   /* removed the last item returned by the next(), so do not
         * change the current iterator
         */
        if ( this.currentIterator != null )
            this.currentIterator.remove();
    }
}
