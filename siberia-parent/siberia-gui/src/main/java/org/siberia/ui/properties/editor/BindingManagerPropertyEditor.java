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
package org.siberia.ui.properties.editor;

import java.util.Collections;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import org.siberia.binding.BindingManagerUtilities;
import org.siberia.kernel.resource.ResourcePersistence;

/**
 *
 * Editor for property look and feel
 *
 * @author alexis
 */
public class BindingManagerPropertyEditor extends org.siberia.ui.properties.editor.ComboBoxPropertyEditor
{
    /** combo model */
    private ComboBoxModel model = null;
    
    /** Creates a new instance of LafPropertyEditor */
    public BindingManagerPropertyEditor()
    {   }
    
    /** return the component
     *  @return a component
     */
    public JComponent getComponent()
    {   JComponent component = super.getComponent();
        
        if ( component instanceof JComboBox )
        {   if ( this.model == null )
            {   
                /** find the related ResourcePersistence */
                ResourcePersistence kind = ResourcePersistence.getPersistenceKindForProperty(this.getProperty().getRepr());
            
                if ( kind == null )
                {   throw new RuntimeException("unable to get the ResourcePersistence associated with property='" + this.getProperty().getRepr() + "'"); }
                
                this.model = new BindingManagerComboBoxModel(kind);
                
                if ( this.getProperty() != null )
                {   this.model.setSelectedItem(kind.getBindingManagerCode()); }
                
                ((JComboBox)component).setModel(this.model);
            }
        }
        
        return component;
    }
    
    /** combobox model for look and feel */
    private class BindingManagerComboBoxModel extends DefaultComboBoxModel
    {
        public BindingManagerComboBoxModel(ResourcePersistence kind)
        {   super();
            
            List<String> items = BindingManagerUtilities.getBindingManagersImplementing(kind.getExpectedBindingManagerKind());
            
            if ( items != null )
            {   
                Collections.sort(items);
                
                for(int i = 0; i < items.size(); i++)
                {   String current = items.get(i);
                    
                    if ( current != null )
                    {   this.addElement(current); }
                }
            }
        }
    }
}
