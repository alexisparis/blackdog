/* 
 * Siberia lang : java language utilities
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
package org.siberia.type.lang;

import java.beans.PropertyVetoException;
import java.lang.reflect.Field;
import org.siberia.type.AbstractSibType;

/**
 *
 * Class representing a field in Sib kernel
 *
 * @author alexis
 */
public class SibField extends AbstractSibType implements LangageElement
{
    // PENDING(api): revoir commentaire ( ajouter des exemples dans javadoc
    /** constant for Html generation */
    protected static final String TAG_BEG_TYPE    = "<font color=\"blue\">";
    protected static final String TAG_END_TYPE    = "</font>";
    protected static final String TAG_BEG_NAME      = "<b>";
    protected static final String TAG_END_NAME      = "</b>";
    
    private Field javaField  = null;
    
    private Class classField = null;
    
    /** Creates a new instance of SibField */
    public SibField()
    {   super(); }

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {   return null; }

    public Field getJavaField()
    {   return javaField; }

    public void setJavaField(Field javaField)
    {   this.javaField = javaField;
        if ( javaField != null )
            this.classField = this.javaField.getType();
        
        if ( this.javaField != null )
        {   try
            {   this.setName(this.javaField.getName()); }
            catch(PropertyVetoException e)
            {   e.printStackTrace(); }
        }
    }
    
    /** return the type for the field
     *  @return a class 
     */
    public Class getType()
    {   return this.classField; }
    
    public String getFieldName()
    {   if ( this.getJavaField() != null )
            return this.getJavaField().getName();
        
        return "";
    }
    
    public String toString()
    {   return (this.getType() == null ? "" : this.getType().getSimpleName()) + " " + this.getFieldName();
    }
    
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   return SibField.TAG_BEG_TYPE + this.getType().getSimpleName() + SibField.TAG_END_TYPE + " " + 
               SibField.TAG_BEG_NAME + this.getFieldName() + SibField.TAG_END_NAME;
    }
    
    public String getCompletionObject()
    {   return this.getFieldName(); }
    
    /** return the package of the element
     *  @return a Strign representing the package
     */
    public String getOwnedClass()
    {   if ( this.getJavaField() != null )
            return this.getJavaField().getDeclaringClass().getName();
        return null;
    }
    
    public Object getValue()
    {   return this.getJavaField(); }
    
    public void setValue(Object value)
    {   if ( value instanceof Field )
            this.setJavaField((Field)value);
    }
    
    /** return true if the method is public
     *  @return true if the method is public
     */
    public boolean isPublic()
    {   return this.getJavaField().getModifiers() == 1;  }
    
    /** return true if the method is public
     *  @return true if the method is public
     */
    public boolean isStatic()
    {   return this.getJavaField().getModifiers() == 9;  }
    
}
