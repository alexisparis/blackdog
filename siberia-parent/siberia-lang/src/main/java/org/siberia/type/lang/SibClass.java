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
import java.lang.reflect.Modifier;
import java.util.List;
import org.siberia.type.AbstractSibType;

/**
 *
 * Class representing a class in Sib structure
 *
 * @author alexis
 */
public class SibClass extends AbstractSibType implements LangageElement
{
    // PENDING(api): revoir commentaire ( ajouter des exemples dans javadoc
    private SibPackage           packageOwner    = null; 
    
    private List<SibMethod>      methodList      = null;
    
    private List<SibConstructor> constructorList = null;
    
    private List<SibField>       fieldList       = null;
    
    private Class                  javaClass     = null;
    
    /** Creates a new instance of SibPackage */
    public SibClass()
    {   super(); }

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {   return null; }

    public Class getJavaClass()
    {   return this.javaClass; }

    public void setJavaClass(Class javaClass)
    {   this.javaClass = javaClass;
        
        if ( javaClass == null )
        {
            try
            {   this.setName(""); }
            catch(PropertyVetoException e)
            {   e.printStackTrace(); }
        }
        else
        {   int lastPointPosition = javaClass.getName().lastIndexOf(".");
            if ( lastPointPosition == -1 )
            {   try
                {   this.setName(javaClass.getName()); }
                catch(PropertyVetoException e)
                {   e.printStackTrace(); }
            }
            else
            {   try
                {   this.setName(javaClass.getName().substring(lastPointPosition + 1)); }
                catch(PropertyVetoException e)
                {   e.printStackTrace(); }
            }
        }
    }
    
    public void setValue(Object value)
    {   if ( value instanceof Class )
            this.setJavaClass((Class)value);
    }

    public SibPackage getPackage()
    {   return packageOwner; }

    public void setPackage(SibPackage packageOwner)
    {   this.packageOwner = packageOwner; }
    
    public Object getValue()
    {   return this.getJavaClass(); }
    
    /** return true if the method is public
     *  @return true if the method is public
     */
    public boolean isPublic()
    {   if ( this.getJavaClass() == null ) return false;
        
        return this.getJavaClass().getModifiers() == Modifier.PUBLIC;
    }
    
    /** return true if the method is public static
     *  @return true if the method is public static
     */
    public boolean isStatic()
    {   if ( this.getJavaClass() == null ) return false;
        
        return this.getJavaClass().getModifiers() == Modifier.STATIC;
    }
    
    /** return the package of the element
     *  @return a Strign representing the package
     */
    public String getOwnedClass()
    {   if ( this.getJavaClass() != null )
            return this.getJavaClass().getName();
        return null;
    }
    
    public String getCompletionObject()
    {   return this.getName();
//        if ( this.getJavaClass() != null )
//            return this.getJavaClass().getName();
//        return null;
    }
    
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   
        int index = this.getJavaClass().getName().lastIndexOf(".");
        return this.getName() + " (" +
               this.getJavaClass().getName().substring(0, index) + ")";
    }
    
    public static void main(String[] args)
    {   Class pac = java.lang.String.class;
        
        System.out.println("class name : " + pac.getName());
    }
    
}
