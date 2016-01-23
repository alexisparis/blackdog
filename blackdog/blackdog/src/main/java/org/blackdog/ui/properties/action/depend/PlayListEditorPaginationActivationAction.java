/*
 * blackdog : audio player / manager
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.ui.properties.action.depend;

import java.util.Set;
import org.siberia.properties.action.DependAction;
import org.siberia.properties.XmlProperty;

/**
 *
 * Allow to modify editability of pagination size property when 
 *  the value of pagination changed
 *
 * @author alexis
 */
public class PlayListEditorPaginationActivationAction implements DependAction
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
            if ( changedProperty.getRepr().equals("playlist.configuration.pagination.activate") )
            {   boolean activateDependencies = ((Boolean)newValue).booleanValue();
                if ( propName.equals(XmlProperty.PROP_VALUE) &&
                     property.getRepr().equals("playlist.configuration.pagination.size") )
                {   property.setEditable(activateDependencies); }
            }
        }
    }
    
}
