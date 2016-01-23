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
package org.siberia.type.annotation.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotation that allow to attach differents information related to bean on a SibType
 *
 * @author alexis
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD}) 

public @interface BeanProperty
{
    /** return the name of the property
     *  @return the name of the property
     */
    public String name();
    
    /** return the code of the properties internalization file related with the type class that declare this annotation
     *  @return a String
     */
    public String internationalizationRef();
    
    /** return true if the type is for expert
     *  @return true if the type is for expert
     */
    public boolean expert();
    
    /** return true if the type is hidden
     *  @return true if the type is hidden
     */
    public boolean hidden();
    
    /** return true if the type is important
     *  @return true if the type is important
     */
    public boolean preferred();
    
    /** return true if the property is bounded
     *  @return true if the rpoperty is bounded
     */
    public boolean bound();
    
    /** return true if the property is constrained
     *  @return true if the rpoperty is constrained
     */
    public boolean constrained();
    
    /** return the name of the write method
     *  @return the name of the write method
     */
    public String writeMethodName();
    
    /** return an array of class representing the parameter for the write method
     *  @return an array of class representing the parameter for the write method
     */
    public Class<?>[] writeMethodParametersClass();
    
    /** return the name of the read method
     *  @return the name of the read method
     */
    public String readMethodName();
    
    /** return an array of class representing the parameter for the read method
     *  @return an array of class representing the parameter for the read method
     */
    public Class<?>[] readMethodParametersClass();
    
}