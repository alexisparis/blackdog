/* 
 * Siberia gui : siberia plugin defining basics of graphical application 
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
package org.siberia.ui.properties.action.depend;

import java.util.Set;
import org.siberia.properties.action.DependAction;
import org.siberia.properties.XmlProperty;

/**
 *
 * @author alexis
 */
public class FramePositionsDependAction implements DependAction
{
    
    /** methods which defines the action to do when changes occurs on a depending property 
     *  @param propName property name defined in XmlProperty <ul><li>PROP_EDITABILITY</li>
     *                                                               <li>PROP_VISILITY</li>
     *                                                               <li>PROP_LABEL</li>
     *                                                               <li>PROP_ICON</li>
     *                                                               <li>PROP_DESCRIPTION</li>
     *                                                               <li>PROP_VALUE</li></ul>
     *  @param property a XmlProperty that depends on other properties
     *  @param changedProperty the property that changes
     *  @param set a set containing all properties 'property' depends on
     *  @param oldValue the old value
     *  @param newValue the new value
     */
    public void dependingPropertyChange(String propName, XmlProperty property,
                                        XmlProperty changedProperty,
                                        Set<XmlProperty> set,
                                        Object oldValue, Object newValue)
    {   
        if ( newValue instanceof Boolean )
        {   
            if ( changedProperty.getRepr().equals("frame.main.automaticpositioning") )
            {   boolean activateDependencies = ! ((Boolean)newValue).booleanValue();
                if ( propName.equals(XmlProperty.PROP_VALUE) && property.getRepr().equals("frame.main.maximized") )
                {   property.setEditable(activateDependencies); }
            }
            else if ( changedProperty.getRepr().equals("frame.main.maximized") )
            {   
                boolean activateDependencies = true;
                if ( propName.equals(XmlProperty.PROP_VALUE) )
                    activateDependencies = ! ((Boolean)newValue).booleanValue();
                else if ( propName.equals(XmlProperty.PROP_EDITABILITY) )
                {   activateDependencies = ((Boolean)newValue).booleanValue();
                    
                    if ( activateDependencies )
                    {
                        /* warning, if frame.main.maximized is set to true, then do not activate sub items */
                        Object o = changedProperty.getCurrentParsedValue();
                        if ( o instanceof Boolean )
                        {   activateDependencies = ! ((Boolean)o).booleanValue(); }
                        else
                            activateDependencies = false;
                    }
                }   
                
                if ( propName.equals(XmlProperty.PROP_VALUE) || propName.equals(XmlProperty.PROP_EDITABILITY) )
                {   if ( property.getRepr().equals("frame.main.x") ||
                         property.getRepr().equals("frame.main.y") ||
                         property.getRepr().equals("frame.main.width") ||
                         property.getRepr().equals("frame.main.height") )
                    {   property.setEditable(activateDependencies); }
                }
            }
        }
    }
    
}
