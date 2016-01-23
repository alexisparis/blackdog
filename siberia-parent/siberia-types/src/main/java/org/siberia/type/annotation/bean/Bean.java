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
 * Annotation that allow to attach differents information related to bean on a SibType.
 *
 * WARNING : 
 *<b>When a class used a Bean annotation, the class has to be declared in plugin declaration.
 *  In fact, the type declaration in plugin declaration help to find the internationalization related to the class</b>
 *
 * @author alexis
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE}) 

public @interface Bean
{
    /** return the name of the type
     *  @return the name of the type
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
    
    /** return the Class that is the limit for getting properties
     *  @return a Class
     */
    public Class propertiesClassLimit();
    
    /** return the Class that is the limit for getting methods
     *  @return a Class
     */
    public Class methodsClassLimit();
    
}