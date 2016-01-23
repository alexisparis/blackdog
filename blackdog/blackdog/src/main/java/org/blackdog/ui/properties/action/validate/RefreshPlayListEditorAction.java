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

import java.util.Iterator;
import org.blackdog.ui.PlayListEditor;
import org.siberia.editor.Editor;
import org.siberia.kernel.Kernel;
import org.siberia.properties.XmlProperty;
import org.siberia.properties.action.ApplyAction;
import org.siberia.ui.UserInterface;
import org.siberia.properties.action.ValidateAction;


/**
 *
 * Refresh the PlayList editor pagination status
 *
 * @author alexis
 */
public class RefreshPlayListEditorAction implements ValidateAction
{
    /** methods which defines the action to do when the changes on a property is confirmed
     *  @param property a ColdXmlProperty
     *  @param initialValue the initial value before modification
     *  @param actualValue the actual value
     */
    public void modificationConfirmedOn(XmlProperty property, Object initialValue, Object actualValue)
    {   
        Iterator<Editor> editors = Kernel.getInstance().getEditorRegistry().getEditors(PlayListEditor.class).iterator();
        
        while(editors.hasNext())
        {
            Editor current = editors.next();
            
            if ( current instanceof PlayListEditor )
            {
                ((PlayListEditor)current).refreshProperties();
            }
        }
    }
    
}
