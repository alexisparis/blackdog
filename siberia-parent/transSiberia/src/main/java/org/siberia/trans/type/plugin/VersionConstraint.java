/* 
 * TransSiberia : siberia plugin allowing to update siberia pplications and download new plugins
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
package org.siberia.trans.type.plugin;

/**
 *
 * define a constraint of version
 *
 * @author alexis
 */
public class VersionConstraint
{   
    /** version */
    private Version version = null;
    
    /** Creates a new instance of VersionConstraint */
    public VersionConstraint()
    {
	this(null);
    }
    
    /** Creates a new instance of VersionConstraint
     *	@param version a Version
     */
    public VersionConstraint(Version version)
    {
	this.version = version;
    }
    
    /** return true if the given version convey to this constraint
     *	@param version a Version
     *	@return true if the given version convey to this constraint
     */
    public boolean validate(Version version)
    {
	boolean result = true;
	
	if ( this.version != null )
	{
	    if ( version == null )
	    {
		result = false;
	    }
	    else
	    {
		/** version should be greater or equals to this.version */
		result = version.compareTo(this.version) >= 0;
	    }
	}
	
	return result;
    }
    
    /** method that parse a String to provide a VersionConstraint
     *  @param s a String
     *  @return a VersionConstraint
     *
     *  @exception VersionConstraintFormatException
     */
    public static VersionConstraint parse(String s) throws VersionConstraintFormatException
    {   
	VersionConstraint constraint = null;
	
	try
	{   
	    Version v = Version.parse(s);
	    constraint = new VersionConstraint(v);
	}
	catch (Version.VersionFormatException ex)
	{
	    throw new VersionConstraintFormatException(s);
	}
        
        return constraint;
    }

    @Override
    public String toString()
    {
	return (this.version == null ? null : this.version.toString());
    }
    
    /** exception that is throwed when trying to parse an invalid string representation of a version */
    public static class VersionConstraintFormatException extends IllegalArgumentException
    {
        /** create a new VersionFormatException
         *  @param toParse the String that caused this exception to be created
         */
        public VersionConstraintFormatException(String toParse)
        {   super("unable to parse " + (toParse == null ? null : "'" + toParse + "'")); }
    }
}
