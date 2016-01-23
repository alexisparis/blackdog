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
@Target({ElementType.METHOD}) 

public @interface BeanMethod
{
    /** return the name of the type
     *  @return the name of the type
     */
    public String name();
    
    /** return the display name of the type
     *  @return the display name of the type
     */
    public String displayName();
    
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
    
    /** return a description of the type
     *  @return a description of the type
     */
    public String description();
    
    /** return a short description of the type
     *  @return a short description of the type
     */
    public String shortDescription();
    
}