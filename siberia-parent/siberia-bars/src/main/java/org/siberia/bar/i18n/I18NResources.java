/* =============================================================================
 * Siberia bars
 * =============================================================================
 *
 * Project Lead:  Alexis Paris
 *
 * (C) Copyright 2007, by Alexis Paris.
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
package org.siberia.bar.i18n;

/**
 * 
 * represent an internationalization resources
 *
 * @author alexis
 */
public class I18NResources implements Comparable<I18NResources>
{
    /** code */
    private String code     = null;
    
    /** priority of the resources */
    private int    priority = 0;
    
    /** Creates a new instance of I18NResources
     *	@param code the code of the resources
     */
    public I18NResources(String code)
    {
	this(code, 0);
    }
    
    /** Creates a new instance of I18NResources
     *	@param code the code of the resources
     *	@param priority the priority of the resources<br>
     *	    greater priority means that the resources will be asked first to resolve a i18n reference
     */
    public I18NResources(String code, int priority)
    {
	this.code = code;
	this.priority = priority;
    }
    
    /** return the code of the resources
     *	@return a String
     */
    public String getCode()
    {
	return this.code;
    }
    
    /** return the priority of the resources<br>
     *	greater priority means that the resources will be asked first to resolve a i18n reference
     *	@return an integer representing the priority of the resources
     */
    public int getPriority()
    {
	return this.priority;
    }
    
    public int compareTo(I18NResources o)
    {
	int result = 0;
	
	if ( o == null )
	{
	    result = -1;
	}
	else
	{
	    result = o.getPriority() - this.getPriority();
	}
	
	return result;
    }
    
}
