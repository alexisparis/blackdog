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

import java.lang.reflect.Constructor;
import org.siberia.type.AbstractSibType;
import org.siberia.type.CompletionObjectProducer;


/**
 *
 * Class representing a constructor in Sib kernel
 *
 * @author alexis
 */
public class SibConstructor extends AbstractSibType implements LangageElement
{
    // PENDING(api): revoir commentaire ( ajouter des exemples dans javadoc
    /** constant for Html generation */
    private static final String TAG_BEG_NAME      = "<b>";
    private static final String TAG_END_NAME      = "</b>";
    private static final String TAG_BEG_PARAMETER = "<font color=\"red\">";
    private static final String TAG_END_PARAMETER = "</font>";
    private static final String TAG_BEG_EXCEPTION = "<font color=\"blue\">";
    private static final String TAG_END_EXCEPTION = "</font>";
    
    private Constructor javaConstructor = null;
    
    /** Creates a new instance of SibConstructor */
    public SibConstructor()
    {   super(); }

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {   return null; }

    public Constructor getJavaConstructor()
    {   return javaConstructor; }

    public void setJavaConstructor(Constructor javaConstructor)
    {   this.javaConstructor = javaConstructor; }
    
    /** return true if the method is public
     *  @return true if the method is public
     */
    public boolean isPublic()
    {   return this.getJavaConstructor().getModifiers() == 1;  }
    
    /** return true if the method is public
     *  @return true if the method is public
     */
    public boolean isStatic()
    {   return false; }
    
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
        
        return this.getConstructorName() + "(" + parametersString.toString() + ")" + 
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
        {   parametersString.append(SibConstructor.TAG_BEG_PARAMETER);
            parametersString.append( array[i].getSimpleName() );
            parametersString.append(SibConstructor.TAG_END_PARAMETER);
            
            if ( i != array.length - 1 )
                parametersString.append(", ");
        }
        
        StringBuffer exceptionsString = new StringBuffer();
        
        array = this.getExceptionTypes();
        
        for(int i = 0; i < array.length; i++)
        {   exceptionsString.append(SibConstructor.TAG_BEG_EXCEPTION);
            exceptionsString.append( array[i].getSimpleName() );
            exceptionsString.append(SibConstructor.TAG_END_EXCEPTION);
            
            if ( i != array.length - 1 )
                exceptionsString.append(", ");
        }
        
        return SibConstructor.TAG_BEG_NAME + this.getConstructorName() + SibConstructor.TAG_END_NAME + "(" + parametersString.toString() + ")" + 
               (exceptionsString.toString().trim().length() == 0 ?
                   "" : " throws " + exceptionsString.toString());
    }
    
    public String getConstructorName()
    {   if ( this.getJavaConstructor() != null )
        {   int lastIndex = this.getJavaConstructor().getName().lastIndexOf(".");
            return this.getJavaConstructor().getName().substring(lastIndex + 1);
        }
        
        return null;
    }
    
    public Class[] getParameterTypes()
    {   if ( this.getJavaConstructor() != null )
            return this.getJavaConstructor().getParameterTypes();
        
        return null;
    }
    
    /** return a list of exception that can be throwed by the method
     *  @return a list containing class extending Exception.class
     */
    public Class[] getExceptionTypes()
    {   if ( this.getJavaConstructor() != null )
        {   return this.getJavaConstructor().getExceptionTypes(); }
        
        return null;
    }
    
    public String getCompletionObject()
    {   return this.getConstructorName(); }
    
    /** return the package of the element
     *  @return a Strign representing the package
     */
    public String getOwnedClass()
    {   if ( this.getJavaConstructor() != null )
        {   int lastIndex = this.getJavaConstructor().getName().lastIndexOf(".");
            
            return this.getJavaConstructor().getName();//.substring(0, lastIndex);
        }
        
        return null;
    }
    
    public Object getValue()
    {   return this.getJavaConstructor(); }
    
    public void setValue(Object value)
    {   if ( value instanceof Constructor )
            this.setJavaConstructor((Constructor)value);
    }
    
}
