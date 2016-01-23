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

import java.util.Arrays;
import java.util.Comparator;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 *
 * Editor for property look and feel
 *
 * @author alexis
 */
public class LafPropertyEditor extends org.siberia.ui.properties.editor.ComboBoxPropertyEditor
{
    /** combo model */
    private ComboBoxModel model = null;
    
    /** Creates a new instance of LafPropertyEditor */
    public LafPropertyEditor()
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
            {   this.model = new LookAndFeelComboBoxModel();
                
                if ( this.getProperty() != null )
                {   this.model.setSelectedItem(this.getProperty().getCurrentValue()); }
                
                ((JComboBox)component).setModel(this.model);
            }
        }
        
        return component;
    }
    
    /** combobox model for look and feel */
    private class LookAndFeelComboBoxModel extends DefaultComboBoxModel
    {
        public LookAndFeelComboBoxModel()
        {   super();
            
            
            LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
            
            /* sort the array */
            Arrays.sort(lafs, new Comparator<LookAndFeelInfo>()
            {
                public int compare(LookAndFeelInfo o1, LookAndFeelInfo o2)
                {   int result = 0;
                    
                    if ( o1 == null )
                    {   if ( o2 != null )
                            result = -1;
                    }
                    else
                    {   if ( o2 == null )
                        {   result = 1; }
                        else
                        {   result = o1.getName().compareTo(o2.getName()); }
                    }
                    
                    return result;
                }
                
            });
            
            if ( lafs != null )
            {   for(int i = 0; i < lafs.length; i++)
                {   this.addElement(lafs[i].getName()); }
            }
        }
    }
    
}
