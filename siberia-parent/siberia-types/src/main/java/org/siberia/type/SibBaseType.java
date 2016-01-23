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
package org.siberia.type;

import java.beans.PropertyVetoException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 *
 * base class for all kind of SibType that deals with basic type like String, Number, Boolean, etc..
 *
 * @author alexis
 */
public abstract class SibBaseType extends AbstractSibType
{
    /** property names */
    public static final String PROPERTY_VALUE   = "value";
    
    /** return an Object representing the value of the Object
     *  @return an Object
     */
    protected abstract Object getObject();
    
    /** initialize the object of the type
     *  @param o an Object
     */
    protected abstract void setObject(Object o) throws PropertyVetoException;

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {   Object o = this.getObject();
        if ( o != null )
            return o.toString();
        return "";
    }
    
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   return super.getHtmlContent() + "  " + this.getObject(); }
    
    /** return a string representation of the SibBoolean
     *  @param maxLength the length of the string representation
     *  @return the string representation 
     */
    public String toString(int maxLength)
    {   String toReturn = this.getName() + " : " + this.getObject();
        if (maxLength != -1) return toReturn.substring(0, maxLength);
        return toReturn;
    }
    
    /** return the value of the type
     *	@return an Object
     */
    public abstract Object getValue();
    
    /** return true if the givn value is equals to current value
     *	@param value an Object
     *	@return true if the givn value is equals to current value
     */
    protected boolean equalsValue(Object value)
    {
	boolean result = false;
	
	if ( value == null )
	{
	    if ( this.getValue() == null )
	    {
		result = true;
	    }
	}
	else
	{
	    result = value.equals(this.getValue());
	}
	
	return result;
    }
    
    
    /** create another instance
     *  @return a clone
     */
    public Object clone()
    {   /** create a new instance by reflexion */
        SibBaseType c = null;
        try
        {   c = (SibBaseType)this.getClass().newInstance(); }
        catch(Exception e)
        {   return null; }
        
        try
        {   c.setName(this.getName()); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        if ( this.getObject() != null )
        {   try
            {   if ( this.getObject() instanceof Cloneable )
                {   Method m = this.getObject().getClass().getMethod("clone", new Class[]{});
                    c.setObject(m.invoke(this.getObject(), new Object[]{}));
                }
                else
                {   /* maybe, there is a constructor (String.class) :-s */
                    Constructor cons = this.getObject().getClass().getConstructor(String.class);
                    Object o = cons.newInstance(this.getObject().toString());
                    try
                    {   c.setObject( o ); }
                    catch (PropertyVetoException ex)
                    {   ex.printStackTrace(); }
                }
            }
            catch(Exception e)
            {   
                try
                {   c.setObject(this.getObject()); }
                catch (PropertyVetoException ex)
                {   ex.printStackTrace(); }
            }
        }
        else
        {   
            try
            {   c.setObject(null); }
            catch (PropertyVetoException ex)
            {   ex.printStackTrace(); }
        }
        
        return c;
    }
    
    public boolean equals(Object t)
    {   if ( ! super.equals(t) ) return false;
        if ( t != null )
        {   if ( t instanceof SibBaseType )
            {   Object value  = this.getObject();
                Object valueT = ((SibBaseType)t).getObject();
                
                if ( value == null )
                {   if ( valueT == null)
                        return true;
                    else
                        return false;
                }
                else
                {   if ( valueT == null)
                        return false;
                    else
                        return value.equals(valueT);
                }
            }
        }
        return false;
    }
}
