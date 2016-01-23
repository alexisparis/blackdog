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
import java.lang.reflect.Method;
import org.siberia.type.AbstractSibType;

/**
 *
 * Class representing a method in Sib kernel
 *
 * @author alexis
 */
public class SibMethod extends AbstractSibType implements LangageElement
{
    // PENDING(api): revoir commentaire ( ajouter des exemples dans javadoc
    /** constant for Html generation */
    private static final String TAG_BEG_RETURN    = "<font color=\"red\">";
    private static final String TAG_END_RETURN    = "</font>";
    private static final String TAG_BEG_NAME      = "<b><code><font size=\"3\">";
    private static final String TAG_END_NAME      = "</font></code></b>";
    private static final String TAG_BEG_PARAMETER = "<font color=\"green\">";
    private static final String TAG_END_PARAMETER = "</font>";
    private static final String TAG_BEG_EXCEPTION = "<font color=\"blue\">";
    private static final String TAG_END_EXCEPTION = "</font>";
    
    private Method javaMethod = null;
    
    /** Creates a new instance of SibMethod */
    public SibMethod()
    {   super(); }

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {   return null; }

    public Method getJavaMethod()
    {   return javaMethod; }

    public void setJavaMethod(Method javaMethod) throws PropertyVetoException
    {   this.javaMethod = javaMethod;
        
        if ( this.javaMethod != null )
        {   try
            {   this.setName(this.javaMethod.getName()); }
            catch(PropertyVetoException e)
            {   e.printStackTrace(); }
        }
    }
    
    public void setValue(Object value) throws PropertyVetoException
    {   if ( value instanceof Method )
            this.setJavaMethod((Method)value);
    }
    
    public Object getValue()
    {   return this.getJavaMethod(); }
    
    /** get the type returned by the method
     *  @return a class
     */
    public Class getReturnType()
    {   if ( this.getJavaMethod() != null )
        {   return this.getJavaMethod().getReturnType(); }
        
        return null;
    }
    
    /** return a list of exception that can be throwed by the method
     *  @return a list containing class extending Exception.class
     */
    public Class[] getExceptionTypes()
    {   if ( this.getJavaMethod() != null )
        {   return this.getJavaMethod().getExceptionTypes(); }
        
        return null;
    }
    
    public int getModifiers()
    {   if ( this.getJavaMethod() != null )
            return this.getJavaMethod().getModifiers();
        return -1;
    }
    
    /** return true if the method is public
     *  @return true if the method is public
     */
    public boolean isPublic()
    {   return this.getModifiers() == 1;  }
    
    /** return true if the method is public static
     *  @return true if the method is public static
     */
    public boolean isStatic()
    {   return this.getModifiers() == 9;  }
    
    public Class[] getParameterTypes()
    {   if ( this.getJavaMethod() != null )
            return this.getJavaMethod().getParameterTypes();
        
        return null;
    }
    
    public String getMethodName()
    {   if ( this.getJavaMethod() != null )
            return this.getJavaMethod().getName();
        
        return null;
    }
    
    public String toString()
    {   StringBuffer parametersString = new StringBuffer();
        
        Class[] array = this.getParameterTypes();
        
        for(int i = 0; i < array.length; i++)
        {   parametersString.append( array[i].getSimpleName() );
            
            if ( i != array.length - 1 )
                parametersString.append(", ");
        }
        
        StringBuffer exceptionsString = new StringBuffer();
        
        array = this.getExceptionTypes();
        
        for(int i = 0; i < array.length; i++)
        {   exceptionsString.append( array[i].getSimpleName() );
            
            if ( i != array.length - 1 )
                exceptionsString.append(", ");
        }
        
        return this.getReturnType().getSimpleName() + " " + 
               this.getMethodName() + "(" + parametersString.toString() + ")" + 
               (exceptionsString.toString().trim().length() == 0 ?
                   "" : " throws " + exceptionsString.toString());
    }
    
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   StringBuffer parametersString = new StringBuffer();
        
        Class[] array = this.getParameterTypes();
        
        for(int i = 0; i < array.length; i++)
        {   parametersString.append(SibMethod.TAG_BEG_PARAMETER);
            parametersString.append( array[i].getSimpleName() );
            parametersString.append(SibMethod.TAG_END_PARAMETER);
            
            if ( i != array.length - 1 )
                parametersString.append(", ");
        }
        
        StringBuffer exceptionsString = new StringBuffer();
        
        array = this.getExceptionTypes();
        
        for(int i = 0; i < array.length; i++)
        {   exceptionsString.append(SibMethod.TAG_BEG_EXCEPTION);
            exceptionsString.append( array[i].getSimpleName() );
            exceptionsString.append(SibMethod.TAG_END_EXCEPTION);
            
            if ( i != array.length - 1 )
                exceptionsString.append(", ");
        }
        
        return SibMethod.TAG_BEG_RETURN + this.getReturnType().getSimpleName() + SibMethod.TAG_END_RETURN + " " + 
               SibMethod.TAG_BEG_NAME + this.getMethodName() + SibMethod.TAG_END_NAME + "(" + parametersString.toString() + ")" + 
               (exceptionsString.toString().trim().length() == 0 ?
                   "" : " throws " + exceptionsString.toString());
    }
    
    public String getCompletionObject()
    {   return this.getMethodName() + "(" + (this.getJavaMethod().getParameterTypes().length == 0 ? ")":""); }
    
    /** return the package of the element
     *  @return a Strign representing the package
     */
    public String getOwnedClass()
    {   if ( this.getJavaMethod() != null )
            return this.getJavaMethod().getDeclaringClass().getName();
        return null;
    }
    
}
