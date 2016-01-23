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
package org.blackdog.ui.properties.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import org.blackdog.BlackdogTypesPlugin;
import org.blackdog.kernel.MusikKernelResources;
import org.siberia.env.PluginResources;
import org.siberia.env.SiberExtension;

/**
 *
 * Editor for property player
 *
 * @author alexis
 */
public class PlayerPropertyEditor extends org.siberia.ui.properties.editor.ComboBoxPropertyEditor
{
    /** combo model */
    private ComboBoxModel model = null;
    
    /**
     * Creates a new instance of PlayerPropertyEditor
     */
    public PlayerPropertyEditor()
    {   }
    
    /** return the component
     *  @return a component
     */
    public JComponent getComponent()
    {   JComponent component = super.getComponent();
        
        if ( component instanceof JComboBox )
        {   
            ((JComboBox)component).setMaximumRowCount(20);
            
            if ( this.model == null )
            {   this.model = new PlayerComboBoxModel();
                
                if ( this.getProperty() != null )
                {   this.model.setSelectedItem(this.getProperty().getCurrentValue()); }
                
                ((JComboBox)component).setModel(this.model);
            }
        }
        
        return component;
    }
    
    /** combobox model for player */
    private class PlayerComboBoxModel extends DefaultComboBoxModel
    {
        public PlayerComboBoxModel()
        {   
	    super();
	    
	    Map<MusikKernelResources.PlayerKey, String> map = MusikKernelResources.getInstance().getPlayersDeclared();
	    
	    if ( map != null )
	    {
		Set<MusikKernelResources.PlayerKey> keySet = map.keySet();
		
		List<String> players = new ArrayList<String>(keySet.size());
		
		Iterator<MusikKernelResources.PlayerKey> keys = keySet.iterator();
		
		while(keys.hasNext())
		{
		    MusikKernelResources.PlayerKey currentKey = keys.next();
		    
		    if ( currentKey != null && currentKey.getName() != null )
		    {
			players.add(currentKey.getName());
		    }
		}
		
		Collections.sort(players);
		
		/** add each items */
		ListIterator it = players.listIterator();
		
		while(it.hasNext())
		{
		    Object current = it.next();
		    
		    if ( current != null )
		    {
			this.addElement(current);
		    }
		}
	    }
        }
    }
    
}
