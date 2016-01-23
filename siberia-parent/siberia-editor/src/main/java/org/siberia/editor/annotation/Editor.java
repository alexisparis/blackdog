/* 
 * Siberia editor : siberia plugin defining editor framework
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
package org.siberia.editor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotation that allow to attach an editor with the kind of ColdType it is related
 *
 * @author alexis
 */


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE}) 

public @interface Editor
{
    /** return a description of the editor
     *  @return a description of the editor
     */
    public String description();
    
    /** return a name for the editor
     *  @return a name for the editor
     */
    public String name();
    
    /** return the classes instance of which can be edited with this editor
     *  @return an array of Class
     */
    public Class[] relatedClasses();
    
    /** return that maximum number of programs that can be launched at the same time
     *  @return an integer representing the maximum number of programs that can be launched at the same time
     *      return a negative integer if illimited
     */
    public int launchedInstancesMaximum() default -1;
    
}