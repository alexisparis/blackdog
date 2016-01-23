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
package org.blackdog.type.base;


/**
 *
 * define a Rate
 *
 * @author alexis
 */
public class Rate
	implements Comparable<Rate>
{
    /** note as integer */
    private int integerRate = 0;
    
    /** Creates a new instance of Rate */
    public Rate(  )
    {
    }
    
    /** return an integer representing the rate
     *        @return an integer representing the rate
     */
    public int getRateValue(  )
    {
	return this.integerRate;
    }
    
    /** initialize the rate value
     *        @param value an integer greater or equals to zero and lower or equals to 10
     *
     *        @exception IllegalArgumentException if value is less than 0 or greater than 10
     */
    public void setRateValue( int value )
    {
	if ( ( value < 0 ) || ( value > 10 ) )
	{
	    throw new IllegalArgumentException( "the value must be in [0, 10] (" + value + " is invalid)" );
	}
	
	this.integerRate = value;
    }
    
    public boolean equals( Object obj )
    {
	boolean result = false;
	
	if ( obj instanceof Rate )
	{
	    result = this.getRateValue(  ) == ( (Rate) obj ).getRateValue(  );
	}
	
	return result;
    }
    
    public int compareTo( Rate o )
    {
	int result = 1;
	
	if ( o != null )
	{
	    result = this.getRateValue(  ) - o.getRateValue(  );
	}
	
	return result;
    }
    
    public String toString(  )
    {
	return Integer.toString( this.getRateValue(  ) );
    }
}
