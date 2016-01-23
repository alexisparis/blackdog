/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.property.enumeration;

import java.awt.Component;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.siberia.ui.swing.combo.EnumListCellRenderer;
import org.siberia.ui.swing.combo.model.EnumComboBoxModel;

/**
 *
 * Renderer for item that are part of an Enumeration
 *
 * @author alexis
 */
public class EnumPropertyRenderer implements TableCellRenderer
{
    /** component */
    private JComboBox                                    combo    = null;
    
    /** map linking class and related ComboBoxModel */
    private Map<Class, SoftReference<EnumComboBoxModel>> modelMap = new HashMap<Class, SoftReference<EnumComboBoxModel>>();
    
    /** Creates a new instance of EnumPropertyRenderer */
    public EnumPropertyRenderer()
    {
	this(null);
    }
    
    /** Creates a new instance of EnumPropertyRenderer
     *	@param renderer a ListCellRenderer
     */
    public EnumPropertyRenderer(ListCellRenderer renderer)
    {   
	if ( this.combo == null )
        {   this.combo = new JComboBox();
            this.combo.setRenderer(renderer == null ? new EnumListCellRenderer() : renderer);
        }
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, 
                                                   int row, int column)
    {   
        /* verify that the model installed is correct according to the value */
        if ( value instanceof Enum )
        {   boolean changeModel = true;
            if ( this.combo.getModel() instanceof EnumComboBoxModel )
            {   if ( value.getClass().equals( ((EnumComboBoxModel)this.combo.getModel()).getEnumClass()) )
                {   changeModel = false; }
            }
            
            if ( changeModel )
            {   SoftReference<EnumComboBoxModel> ref = this.modelMap.get(value.getClass());
                EnumComboBoxModel model = (ref == null ? null : ref.get());
                
                if ( model == null )
                {   model = this.createModel( (Enum)value );
                    
                    /** add in the map */
                    this.modelMap.put(value.getClass(), new SoftReference<EnumComboBoxModel>(model));
                }
                
                this.combo.setModel(model);
            }
            
            /** change selection */
            this.combo.getModel().setSelectedItem(value);
        }
        else
        {   
//            this.combo.setModel(null);
        }
        
        return this.combo;
    }
    
    /** method that create a new ComboBoxModel according to value
     *  @param value the value to render
     *  @return a EnumComboBoxModel or null if value is null
     */
    private EnumComboBoxModel createModel(Enum value)
    {   return new EnumComboBoxModel(value.getClass()); }
    
    
}
