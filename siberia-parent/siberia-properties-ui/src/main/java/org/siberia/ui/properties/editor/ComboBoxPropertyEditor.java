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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import org.siberia.properties.XmlProperty;
import org.siberia.xml.schema.properties.ValueType;
import org.siberia.properties.exception.PropertiesException;

/**
 *
 * generic property renderer
 *
 * @author alexis
 */
public class ComboBoxPropertyEditor extends AbstractPropertyEditor
{   
    /** textfield */
    private JComboBox combo = null;
    
    /** Creates a new instance of ComboBoxPropertyRenderer */
    public ComboBoxPropertyEditor()
    {   super(); }
    
    /** return the component
     *  @return a component
     */
    public JComponent getComponent()
    {   if ( this.combo == null )
        {   this.combo = new JComboBox();
            if ( this.getProperty() != null )
            {   /* create a DefaultComboBoxModel with values if it exists */
                if ( this.getProperty().getInnerProperty().getValues() != null )
                {   List list = this.getProperty().getInnerProperty().getValues().getValue();
                    if ( list != null )
                    {   List modelElement = new ArrayList();
                        
                        Iterator it = list.iterator();
                        while(it.hasNext())
                        {   Object current = it.next();
                            if ( current instanceof ValueType )
                            {   modelElement.add( ((ValueType)current).getRepr() ); }
                        }
                        
                        
                        this.combo.setModel(new DefaultComboBoxModel(modelElement.toArray()));
                    }
                }
                
                this.combo.setSelectedItem(this.getProperty().getCurrentValue());
                
                /** add listener */
                this.combo.addItemListener(new ItemListener()
                {
                    public void itemStateChanged(ItemEvent e)
                    {   
                        try
                        {   getProperty().setValue( e.getItem().toString() ); }
                        catch(Exception ex)
                        {   ex.printStackTrace(); }
                    }

                });
                
            }
        }
        return this.combo;
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
    {   ((JComboBox)this.getComponent()).setSelectedItem(value); }
    
    /** method to overwrite when the event does not concern the label
     *  @param evt a PropertyChangeEvent
     */
    public void propertyChangeImpl(PropertyChangeEvent evt)
    {   if ( evt.getSource() == this.getProperty() )
        {   if ( evt.getPropertyName().equals(XmlProperty.PROP_VALUE) )
            {   if ( this.combo != null )
                    this.combo.setSelectedItem(evt.getNewValue());
            }
        }
    }
    
//    public static void main(String[] args)
//    {
//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        
//        JPanel panel = new JPanel();
//        panel.add(new TextFieldPropertyRenderer(new XmlProperty(null)).getExtendedComponents()[0]);
//        
//        panel.add(new JButton("a"));
//        panel.add(new JButton("b"));
//        
//        frame.getContentPane().add(panel);
//        
//        frame.pack();//setSize(200, 250);
//        frame.setVisible(true);
//        
//    }
}
