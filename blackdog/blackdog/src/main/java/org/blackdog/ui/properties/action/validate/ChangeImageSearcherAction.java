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
package org.blackdog.ui.properties.action.validate;

import org.blackdog.kernel.MusikKernelResources;
import org.siberia.properties.XmlProperty;
import org.siberia.properties.action.ValidateAction;

//import org.siberia.ui.UserInterface;

/**
 *
 * indicate to refresh image searcher used by Musik kernel resources
 *
 * @author alexis
 */
public class ChangeImageSearcherAction implements ValidateAction
{
    /** methods which defines the action to do when the changes on a property is confirmed
     *  @param property a ColdXmlProperty
     *  @param initialValue the initial value before modification
     *  @param actualValue the actual value
     */
    public void modificationConfirmedOn(XmlProperty property, Object initialValue, Object actualValue)
    {
	/* force kernel resources to reassign Image searcher */
	MusikKernelResources.getInstance().initializeCoverSearchImageSearcher();
    }
    
}
