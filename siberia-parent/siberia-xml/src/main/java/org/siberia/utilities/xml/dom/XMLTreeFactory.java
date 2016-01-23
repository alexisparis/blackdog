/* 
 * Siberia xml : siberia plugin to provide utilities for xml format
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
package org.siberia.utilities.xml.dom;

//import org.atom3.xml.dom.XMISchemaManager;
//import org.atom3.xml.dom.XMISchemaManagerImpl;


/**
 * Factory for XMLTree
 * 
 * @author  Alexis PARIS
 */
public final class XMLTreeFactory
{
    /**
     * Constructeur prive de XMLTreeFactory 
     */
    private XMLTreeFactory()
    {   /* do nothing */ }
    
    /**
     *  create a new XMLTree
     *  @return an XMLTree
     */
    public static XMLTree createManager()
    {   return new XMLTreeImpl(); }
    
    /**
     * Cree une nouvelle instance d'un XMISchemaManager 
     */
//    public static XMISchemaManager createXMISchemaManager()
//    { return new XMISchemaManagerImpl(); }
}
