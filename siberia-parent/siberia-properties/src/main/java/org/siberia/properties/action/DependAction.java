/* 
 * Siberia properties : siberia plugin defining system properties
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
package org.siberia.properties.action;

import java.util.Set;
import org.siberia.properties.XmlProperty;

/**
 *
 * action defining a method which will be called when changes occurs on depending properties
 *
 * @author alexis
 */
public interface DependAction
{
    /** methods which defines the action to do when changes occurs on a depending property 
     *  @param propName property name defined in ColdXmlProperty <ul><li>PROP_EDITABILITY</li>
     *                                                               <li>PROP_VISILITY</li>
     *                                                               <li>PROP_LABEL</li>
     *                                                               <li>PROP_ICON</li>
     *                                                               <li>PROP_DESCRIPTION</li>
     *                                                               <li>PROP_VALUE</li></ul>
     *  @param property a ColdXmlProperty that depends on other properties
     *  @param changedProperty the property that changes
     *  @param set a set containing all properties 'property' depends on
     *  @param oldValue the old value
     *  @param newValue the new value
     */
    public void dependingPropertyChange(String propName, XmlProperty property,
                                        XmlProperty changedProperty,
                                        Set<XmlProperty> set,
                                        Object oldValue, Object newValue);
    
}
