/* 
 * Siberia properties ui : siberia plugin defining components to edit properties
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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import org.siberia.properties.XmlProperty;
import org.siberia.properties.XmlPropertyContainer;
import org.siberia.properties.exception.PropertiesException;

/**
 *
 * generic property renderer
 *
 * @author alexis
 */
public class CheckBoxPropertyEditor extends AbstractPropertyEditor
{   
    /** textfield */
    private JCheckBox check = null;
    
    /** Creates a new instance of CheckBoxPropertyRenderer */
    public CheckBoxPropertyEditor()
    {   super(); }
    
    /** return the component
     *  @return a component
     */
    public JComponent getComponent()
    {   if ( this.check == null )
        {   this.check = new JCheckBox();
            if ( this.getProperty() != null )
            {   this.check.setSelected(Boolean.parseBoolean(this.getProperty().getCurrentValue()));

                /* add state change listener */
                this.check.addItemListener(new ItemListener()
                {
                    public void itemStateChanged(ItemEvent e)
                    {   try
                        {   getProperty().setValue( e.getStateChange() == ItemEvent.SELECTED ? "true" : "false"); }
                        catch(Exception ec)
                        {   ec.printStackTrace(); }
                    }
                });
            }
        }
        return this.check;
    }
    
    /**
     * change the appearance of the component
     * @param table		the <code>JTable</code> that is asking the
     * 				editor to edit; can be <code>null</code>
     * @param value		the value of the cell to be edited; it is
     * 				up to the specific editor to interpret
     * 				and draw the value.  For example, if value is
     * 				the string "true", it could be rendered as a
     * 				string or it could be rendered as a check
     * 				box that is checked.  <code>null</code>
     * 				is a valid value
     * @param isSelected	true if the cell is to be rendered with
     * 				highlighting
     * @param row     	the row of the cell being edited
     * @param column  	the column of the cell being edited
     */
    public void modifyComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {   if ( value instanceof Boolean )
            ((JCheckBox)this.getComponent()).setSelected( (Boolean)value );
    }
    
    /** method to overwrite when the event does not concern the label
     *  @param evt a PropertyChangeEvent
     */
    public void propertyChangeImpl(PropertyChangeEvent evt)
    {   if ( evt.getSource() == this.getProperty() )
        {   if ( evt.getPropertyName().equals(XmlProperty.PROP_VALUE) )
            {   if ( this.check != null )
                    this.check.setSelected(Boolean.parseBoolean(evt.getNewValue().toString()));
            }
        }
    }
}
