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

import org.siberia.type.CompletionObjectProducer;
import org.siberia.type.HtmlPrintable;
import org.siberia.type.Namable;

/**
 *
 * Interface for all elements that are related to java langage structure
 *
 * @author alexis
 */
public interface LangageElement extends CompletionObjectProducer, HtmlPrintable, Namable
{
    /** return the package of the element
     *  @return a Strign representing the package
     */
    public String getOwnedClass();
    
    /** return true if the method is public
     *  @return true if the method is public
     */
    public boolean isPublic();
    
    /** return true if the method is public
     *  @return true if the method is public
     */
    public boolean isStatic();
    
    
}
