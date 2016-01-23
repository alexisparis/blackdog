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

import java.io.Serializable;

/**
 *
 * Represents the duration of an audio source
 *
 * @author alexis
 */
public class AudioDuration
	implements Comparable,
	Serializable
{
    /** duration in milli-seconds */
    private long inMilliSeconds = -1;
    
    /** string value of the duration */
    private transient String stringValue = null;
    
    /**
     * Creates a new instance of AudioDuration
     */
    public AudioDuration(  )
    {
	this( -1 );
    }
    
    /**
     * Creates a new instance of AudioDuration
     *
     * @param nbMilliSeconds the number of milli-seconds
     */
    public AudioDuration( long nbMilliSeconds )
    {
	this.setInMilliSeconds( nbMilliSeconds );
    }
    
    /** return the number of milli-seconds that represents this duration
     *        @return a long or -1 if duration is undefined
     */
    public long getTimeInMilli(  )
    {
	return this.getInMilliSeconds(  );
    }
    
    /** return a String representing the duration in the format HHH:mm:ss
     *        @return a string
     */
    public String getStringRepresentation(  )
    {
	return this.stringValue;
    }
    
    @Override
    public String toString(  )
    {
	return this.getStringRepresentation(  );
    }
    
    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *                 is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     */
    public int compareTo( Object o )
    {
	int result = 1;
	
	if ( o instanceof AudioDuration )
	{
	    result = (int) ( this.getTimeInMilli(  ) - ( (AudioDuration) o ).getTimeInMilli(  ) );
	}
	
	return result;
    }
    
    private long getInMilliSeconds(  )
    {
	return inMilliSeconds;
    }
    
    private void setInMilliSeconds( long inMilliSeconds )
    {
	this.inMilliSeconds = inMilliSeconds;
	
	if ( this.getInMilliSeconds(  ) < 0 )
	{
	    this.stringValue = "";
	}
	else
	{
	    /*** 4 for optimization --> generally it will be 4:32 */
	    StringBuffer buffer = new StringBuffer( 4 );
	    
	    long milli = this.getInMilliSeconds(  ) / 1000;
	    
	    long rest = -1;
	    long seconds = milli % 60;
	    rest = milli / 60;
	    
	    long minutes = rest % 60;
	    rest = rest / 60; // hours
	    
//	    System.out.println("h=" + rest + " m=" + minutes + " s=" + seconds);
	    if ( rest > 0 )
	    {
		buffer.append( rest + ":" );
	    }
	    
	    if ( true )
	    {
		if ( buffer.length(  ) > 0 )
		{
		    if ( minutes < 10 )
		    {
			buffer.append( "0" );
		    }
		}
		
		buffer.append( minutes );
	    }
	    
	    if ( ( buffer.length(  ) > 0 ) || ( seconds > 0 ) )
	    {
		if ( buffer.length(  ) > 0 )
		{
		    buffer.append( ":" );
		}
		
		if ( seconds < 10 )
		{
		    buffer.append( "0" );
		}
		
		buffer.append( seconds );
	    }
	    
	    this.stringValue = buffer.toString(  );
	}
    }
}
