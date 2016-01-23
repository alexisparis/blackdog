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
package org.siberia.utilities.math.graph;

import java.util.HashSet;
import java.util.Set;
import org.siberia.utilities.math.Matrix;

/**
 *
 * provide static methods to evaluate and caracterize graph
 *
 * @author alexis
 */
public class GraphUtilities
{
    
    /** Creates a new instance of GraphUtilities */
    private GraphUtilities()
    {	}
    
    /**
     * return true if an oriented graph represented by <code>matrix</code> contains cycle
     * 
     * @param matrix the adjacence Matrix of the oriented graph
     * @return true if an oriented graph contains cycle
     */
    public synchronized static boolean containsCycle(Matrix matrix)
    {
	boolean result = false;
	
	if ( matrix != null )
	{
	    if ( matrix.getRowDimension() != matrix.getColumnDimension() )
	    {
		throw new IllegalArgumentException("containsCycle can only be used with matrix with same row and column dimension");
	    }
	    else
	    {
		/** set of ignored index */
		Set<Integer> ignoredIndexes = new HashSet<Integer>(matrix.getRowDimension());
		
		while( ! result && ignoredIndexes.size() < matrix.getColumnDimension() )
		{
		    /* search in the matrix for a column index that is not in ignoredIndexes
		     *	and that all values contains by the column (except those which row index is in ignoredIndexes)
		     *	are 0.
		     *	if found, add it to ignoredIndexes, else,
		     *	the matrix contains cycle, so mark result as true and the loop will end
		     */
		    
		    int columnIndexWith0 = -1;
			
		    for(int i = 0; i < matrix.getColumnDimension(); i++)
		    {
			if ( ! ignoredIndexes.contains(i) )
			{
			    boolean containsElseThan0 = false;
			    
			    for(int j = 0; j < matrix.getRowDimension(); j++)
			    {
				if ( ! ignoredIndexes.contains(j) )
				{
				    Number value = matrix.get(j, i);
				    
				    /** consider null as 0 ! */
				    if ( value != null )
				    {
					if ( value.doubleValue() != 0 )
					{
					    containsElseThan0 = true;
					    break;
					}
				    }
				}
			    }
			    
			    if ( ! containsElseThan0 )
			    {
				columnIndexWith0 = i;
				break;
			    }
			}
		    }
		    
		    if ( columnIndexWith0 < 0 )
		    {
			/** no more columns found ... */
			result = true;
		    }
		    else
		    {
			ignoredIndexes.add(columnIndexWith0);
		    }
		}
	    }
	}
	
	return result;
    }
    
}
