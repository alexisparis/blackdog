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

/**
 *
 *
 *
 * @author alexis
 */
public class SibInstantiation extends SibField
{
    // PENDING(api): revoir commentaire ( ajouter des exemples dans javadoc
    /** Class of the instantiation */
    private Class classInstantiation = null;
    
    /** name of the instantiation */
    private String nameInstantiation = null;
    
    /** Creates a new instance of SibInstantiation */
    public SibInstantiation(String nameInstantiation, Class classInstantiation)
    {   super();
        
        this.nameInstantiation  = nameInstantiation;
        this.classInstantiation = classInstantiation;
        
        try
        {   this.setName(nameInstantiation); }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
    }
    
    /** return the type for the field
     *  @return a class 
     */
    public Class getType()
    {   return this.classInstantiation; }
    
    public String getFieldName()
    {   return this.nameInstantiation; }
    
    public String toString()
    {   return (this.getType() == null ? "" : this.classInstantiation) + " " + this.nameInstantiation;
    }
    
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   return SibField.TAG_BEG_TYPE + this.classInstantiation + SibField.TAG_END_TYPE + " " + 
               SibField.TAG_BEG_NAME + this.nameInstantiation + SibField.TAG_END_NAME;
    }
    
    public String getCompletionObject()
    {   return this.nameInstantiation; }
    
    /** return the package of the element
     *  @return a Strign representing the package
     */
    public String getOwnedClass()
    {   return this.classInstantiation.getName(); }
    
    /** return true if the method is public
     *  @return true if the method is public
     */
    public boolean isPublic()
    {   return true; }
    
    /** return true if the method is public
     *  @return true if the method is public
     */
    public boolean isStatic()
    {   return false; }
    
}
