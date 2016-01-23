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

import java.util.Comparator;

/**
 *
 * Abstract comparator to use when developpers want to avoid 
 * testing null entities
 *
 * @author alexis
 */
public abstract class AbstractComparator implements Comparator
{
    /** class associated with the comparator */
    private Class c = null;
    
    /** Creates a new instance of AbstractComparator
     *  @param class a Class
     */
    public AbstractComparator(Class c)
    {   this.c = c; }
    
    /** comparator implementation for AbstractComparator */
    public final int compare(Object o1, Object o2)
    {   if ( o1 == null )
        {   if ( o2 == null )
                return 0;
            else
                return -1;
        }
        else if ( o2 == null )
            return 1;
        
        if ( c != null )
        {
            if ( c.isAssignableFrom(o1.getClass()) )
            {   if ( c.isAssignableFrom(o2.getClass()) )
                {   return this.compareObjects(o1, o2); }
                else
                    return 1;
            }
            else if ( c.isAssignableFrom(o2.getClass()) )
                return -1;
            
        }
        return 0;
    }
    
    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     *
     * The implementor must ensure that <tt>sgn(compare(x, y)) ==
     * -sgn(compare(y, x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>compare(x, y)</tt> must throw an exception if and only
     * if <tt>compare(y, x)</tt> throws an exception.)<p>
     *
     * The implementor must also ensure that the relation is transitive:
     * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt> implies
     * <tt>compare(x, z)&gt;0</tt>.<p>
     *
     * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt>
     * implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for all
     * <tt>z</tt>.<p>
     *
     * It is generally the case, but <i>not</i> strictly required that 
     * <tt>(compare(x, y)==0) == (x.equals(y))</tt>.  Generally speaking,
     * any comparator that violates this condition should clearly indicate
     * this fact.  The recommended language is "Note: this comparator
     * imposes orderings that are inconsistent with equals."
     * 
     * @param o1 the first object to be compared ( != null and instanceof of the class of this AbstractComparator ).
     * @param o2 the second object to be compared ( != null and instanceof of the class of this AbstractComparator ).
     * @return a negative integer, zero, or a positive integer as the
     * 	       first argument is less than, equal to, or greater than the
     *	       second. 
     * @throws ClassCastException if the arguments' types prevent them from
     * 	       being compared by this Comparator.
     */
    protected abstract int compareObjects(Object o1, Object o2);
    
}
