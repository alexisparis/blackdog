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

import org.siberia.type.AbstractSibType;

/**
 *
 * Abstract information about an import
 *
 * @author alexis
 */
public class SibImport extends SibPackage
{
    
    // PENDING(api): revoir commentaire ( ajouter des exemples dans javadoc
    /** Creates a new instance of SibImport */
    public SibImport()
    {   super(); }
    
    public String toString()
    {   return "import " + super.toString(); }
    
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   return "import " + super.toHtml(); }
    
}
