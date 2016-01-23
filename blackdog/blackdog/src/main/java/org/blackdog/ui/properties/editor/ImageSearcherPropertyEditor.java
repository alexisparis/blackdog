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
 * Editor for property image searcher
 *
 * @author alexis
 */
public class ImageSearcherPropertyEditor extends org.siberia.ui.properties.editor.ComboBoxPropertyEditor
{
    /** combo model */
    private ComboBoxModel model = null;
    
    /**
     * Creates a new instance of ImageSearcherPropertyEditor
     */
    public ImageSearcherPropertyEditor()
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
            {   this.model = new ImageSearcherComboBoxModel();
                
                if ( this.getProperty() != null )
                {   this.model.setSelectedItem(this.getProperty().getCurrentValue()); }
                
                ((JComboBox)component).setModel(this.model);
            }
        }
        
        return component;
    }
    
    /** combobox model for image searcher */
    private class ImageSearcherComboBoxModel extends DefaultComboBoxModel
    {
        public ImageSearcherComboBoxModel()
        {   
	    super();
	    
	    Map<String, String> map = MusikKernelResources.getInstance().getImageSearchersDeclared();
	    
	    if ( map != null )
	    {
		List<String> searchers = new ArrayList<String>(map.keySet());
		
		Collections.sort(searchers);
		
		/** add each items */
		ListIterator it = searchers.listIterator();
		
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
