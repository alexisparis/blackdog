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

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.siberia.properties.XmlProperty;
import org.siberia.properties.util.PropertyValueValidator;
import org.siberia.ui.swing.SibTextField;
import org.siberia.ui.swing.TextFieldVerifier;
import org.siberia.xml.schema.properties.NatureType;

/**
 *
 * generic property renderer
 *
 * @author alexis
 */
public class TextFieldPropertyEditor extends AbstractPropertyEditor
{   
    /** textfield */
    protected SibTextField   textField   = null;
    
    /** default cell editor */
    private DefaultCellEditor tableEditor = null;
    
    /** column count */
    protected int             columnCount = 20;
    
    /** Creates a new instance of TextFieldPropertyRenderer */
    public TextFieldPropertyEditor()
    {   super(); }
    
    /** indicate the number of column occupied by the textField
     *  @param colCount the number  of column occupied by the textField
     */
    public void setTextFieldColumn(int colCount)
    {   this.columnCount = colCount;
        if ( this.textField != null )
            this.textField.setColumns(colCount);
    }
    
    /** return the component
     *  @return a component
     */
    public JComponent getComponent()
    {   if ( this.textField == null )
        {   this.textField = new SibTextField(this.columnCount);
            
            this.textField.setColdVerifier(new TextFieldVerifier()
            {
                public boolean applyValue(Component input, Object value)
                {   
                    if ( input instanceof JTextComponent && value instanceof String )
                    {   try
                        {   getProperty().setValue( (String)value );
                        }
                        catch(Exception e)
                        {   e.printStackTrace(); }
                        
                        return true;
                    }
                    return false;
                }
                
                public boolean verify(JComponent input)
                {   if ( input instanceof JTextComponent )
                    {   boolean valid = PropertyValueValidator.isValid(((JTextComponent)input).getText(), getProperty());
                        return valid;
                    }
                    return true;
                }
            });
            
            if ( this.getProperty() != null )
            {   if ( this.getProperty().getCurrentValue() != null )
                    this.textField.setText(this.getProperty().getCurrentValue());
            }
        }
        return this.textField;
    }
    
    /** initialize the associated property
     *  @param property the associated property
     */
    public void setProperty(XmlProperty property)
    {   super.setProperty(property);
        
        int alignment = JTextField.RIGHT;
        
        if ( property != null )
        {
            NatureType type = NatureType.fromValue(property.getNature());
            
            if ( type == NatureType.STRING || type == NatureType.BOOLEAN )
            {   alignment = JTextField.LEFT; }
        }
        
        ((JTextField)this.getComponent()).setHorizontalAlignment(alignment);
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
    {   if ( value instanceof String )
            ((JTextField)this.getComponent()).setText( (String)value );
    }
    
    /** method to overwrite when the event does not concern the label
     *  @param evt a PropertyChangeEvent
     */
    public void propertyChangeImpl(PropertyChangeEvent evt)
    {   if ( evt.getSource() == this.getProperty() )
        {
            if ( evt.getPropertyName().equals(XmlProperty.PROP_VALUE) )
            {   textField.setText(evt.getNewValue().toString()); }
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
