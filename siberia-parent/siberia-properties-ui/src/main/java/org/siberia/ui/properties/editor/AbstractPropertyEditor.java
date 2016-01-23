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
import java.beans.PropertyChangeListener;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import org.siberia.properties.XmlProperty;
import org.siberia.properties.XmlPropertyContainer;
import org.siberia.exception.ResourceException;
import org.siberia.ResourceLoader;
import org.siberia.ui.properties.PropertyEditor;

/**
 *
 * abstract property renderer that manage label
 *
 * @author alexis
 */
public abstract class AbstractPropertyEditor implements PropertyEditor, PropertyChangeListener
{   
    /** an instance of XmlProperty */
    private XmlProperty   property = null;
    
    /** reference to the label related to this editor */
    private JLabel            label    = null;
    
    /** default cell editor */
    private DefaultCellEditor editor   = null;
    
    /** Creates a new instance of AbstractPropertyRenderer */
    public AbstractPropertyEditor()
    {   }
    
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
    public abstract void modifyComponent(JTable table, Object value, boolean isSelected, int row, int column);
    
    /** return an initialize cell editor
     *  @return a DefaultCellEditor
     */
    protected DefaultCellEditor getCellEditor()
    {   if ( this.editor == null )
        {   if ( this.getComponent() instanceof JTextField )
                this.editor = new DefaultCellEditor( (JTextField)this.getComponent() );
            else if ( this.getComponent() instanceof JCheckBox )
                this.editor = new DefaultCellEditor( (JCheckBox)this.getComponent() );
            else if ( this.getComponent() instanceof JComboBox )
                this.editor = new DefaultCellEditor( (JComboBox)this.getComponent() );
            else
                throw new IllegalArgumentException("getComponent has to return a JTextField, JComboBox or JCheckBox");
        }
        
        return this.editor;
    }
    
    /** initialize the associated property
     *  @param property the associated property
     */
    public void setProperty(XmlProperty property)
    {   
        if ( this.property != null )
            this.property.removePropertyChangeListener(this);
        
        this.property = property;
        
        /* update the state of graphical component according to property */
        this.updateAccordingToProperty();
            
        if ( this.property != null )
        {   this.property.addPropertyChangeListener(this); }
    }
    
    /** return the associated property
     *  @return the associated property
     */
    public XmlProperty getProperty()
    {   return this.property; }
    
    /** set the label related to the component
     *  @param label a JLabel
     */
    public void setLabel(JLabel label)
    {   this.label = label;
        
        this.updateAccordingToProperty();
    }
    
    /** return the label related to the component
     *  @return a JLabel
     */
    public JLabel getLabel()
    {   return this.label; }
    
    /** update caracteristics according to the current property */
    protected void updateAccordingToProperty()
    {
        if ( this.property != null )
        {   this.updateEditability(this.property.isEditable());
            this.updateVisibility(this.property.isVisible());
            this.updateIcon(this.property.getIcon());
            this.updateLabel(this.property.getLabel());
            this.updateDescription(this.property.getDescription());
        }
        else
        {   this.updateVisibility(false); }
    }
    
    /** update editability state according to parameter
     *  @param editable true if should be editable
     */
    protected void updateEditability(boolean editable)
    {   JComponent c = this.getComponent();
        if ( c != null )
        {   c.setEnabled(editable); }
        if ( this.getLabel() != null )
            this.getLabel().setEnabled(editable);
    }
    
    /** update visibility state according to parameter
     *  @param visible true if should be visible
     */
    protected void updateVisibility(boolean visible)
    {   JComponent c = this.getComponent();
        if ( c != null )
        {   c.setVisible(visible); }
        if ( this.getLabel() != null )
            this.getLabel().setVisible(visible);
    }
    
    /** update the description according to parameter
     *  @param tooltip the new tooltip to apply
     */
    protected void updateDescription(String tooltip)
    {   if ( this.getLabel() != null )
            this.getLabel().setToolTipText(tooltip);
    }
    
    /** update icon according to parameter
     *  @param icon the new icon to apply
     */
    protected void updateIcon(String icon)
    {   try
        {   if ( this.getLabel() != null )
            {   if ( icon != null )
                {   this.getLabel().setIcon(ResourceLoader.getInstance().getIconNamed(icon)); }
                else
                {   this.getLabel().setIcon(null); }
            }
        }
        catch(ResourceException e)
        {   e.printStackTrace(); }
    }
    
    /** update label according to parameter
     *  @param label the new label to apply
     */
    protected void updateLabel(String label)
    {   if ( this.getLabel() != null )
            this.getLabel().setText(label);
    }
    
    /** ########################################################################
     *  ############### PropertyChangeListener implementation ##################
     *  ######################################################################## */
    
    /** method to overwrite when the event does not concern the label
     *  @param evt a PropertyChangeEvent
     */
    public void propertyChangeImpl(PropertyChangeEvent evt)
    {   /* to overwrite */ }
    
    public void propertyChange(PropertyChangeEvent evt)
    {   if ( evt.getSource() == this.property )
        {   if ( evt.getPropertyName().equals(XmlPropertyContainer.PROP_DESCRIPTION) )
            {   this.updateDescription(evt.getNewValue().toString()); }
            else if ( evt.getPropertyName().equals(XmlPropertyContainer.PROP_ICON) )
            {   this.updateIcon(evt.getNewValue().toString()); }
            else if ( evt.getPropertyName().equals(XmlPropertyContainer.PROP_LABEL) )
            {   this.updateLabel(evt.getNewValue().toString()); }
            else if ( evt.getPropertyName().equals(XmlProperty.PROP_VISILITY) )
            {   boolean visible = Boolean.parseBoolean(evt.getNewValue().toString());
                this.updateVisibility(visible);
            }
            else if ( evt.getPropertyName().equals(XmlProperty.PROP_EDITABILITY) )
            {   boolean editable = Boolean.parseBoolean(evt.getNewValue().toString());
                this.updateEditability(editable);
            }
            else
            {   // concern the other component
                this.propertyChangeImpl(evt);
            }
        }
    }
    
    /* #########################################################################
     * #################### TableCellEditor implementation #####################
     * ######################################################################### */

    /**
     * Returns true if the editing cell should be selected, false otherwise.
     * Typically, the return value is true, because is most cases the editing
     * cell should be selected.  However, it is useful to return false to
     * keep the selection from changing for some types of edits.
     * eg. A table that contains a column of check boxes, the user might
     * want to be able to change those checkboxes without altering the
     * selection.  (See Netscape Communicator for just such an example) 
     * Of course, it is up to the client of the editor to use the return
     * value, but it doesn't need to if it doesn't want to.
     * 
     * @param anEvent		the event the editor should use to start
     * 				editing
     * @return true if the editor would like the editing cell to be selected;
     *    otherwise returns false
     * @see #isCellEditable
     */
    public boolean shouldSelectCell(EventObject anEvent)
    {   boolean result = true;
        if ( this.getCellEditor() != null )
            result = this.getCellEditor().shouldSelectCell(anEvent);
        return result;
    }

    /**
     * Asks the editor if it can start editing using <code>anEvent</code>.
     * <code>anEvent</code> is in the invoking component coordinate system.
     * The editor can not assume the Component returned by
     * <code>getCellEditorComponent</code> is installed.  This method
     * is intended for the use of client to avoid the cost of setting up
     * and installing the editor component if editing is not possible.
     * If editing can be started this method returns true.
     * 
     * 
     * @param anEvent		the event the editor should use to consider
     * 				whether to begin editing or not
     * @return true if editing can be started
     * @see #shouldSelectCell
     */
    public boolean isCellEditable(EventObject anEvent)
    {   boolean result = true;
        if ( this.getCellEditor() != null )
            result = this.getCellEditor().isCellEditable(anEvent);
        if ( this.getProperty() != null && result )
        {   result = this.getProperty().isEditable(); }
        
        return result;
    }

    /**
     * Removes a listener from the list that's notified
     * 
     * @param l		the CellEditorListener
     */
    public void removeCellEditorListener(CellEditorListener l)
    {   if ( this.getCellEditor() != null )
            this.getCellEditor().removeCellEditorListener(l);
    }

    /**
     * Adds a listener to the list that's notified when the editor 
     * stops, or cancels editing.
     * 
     * @param l		the CellEditorListener
     */
    public void addCellEditorListener(CellEditorListener l)
    {   if ( this.getCellEditor() != null )
            this.getCellEditor().addCellEditorListener(l);
    }

    /**
     * Tells the editor to stop editing and accept any partially edited
     * value as the value of the editor.  The editor returns false if
     * editing was not stopped; this is useful for editors that validate
     * and can not accept invalid entries.
     * 
     * @return true if editing was stopped; false otherwise
     */
    public boolean stopCellEditing()
    {   boolean result = true;
        if ( this.getCellEditor() != null )
            result = this.getCellEditor().stopCellEditing();
        return result;
    }

    /**
     *  Sets an initial <code>value</code> for the editor.  This will cause
     *  the editor to <code>stopEditing</code> and lose any partially
     *  edited value if the editor is editing when this method is called. <p>
     * 
     *  Returns the component that should be added to the client's
     *  <code>Component</code> hierarchy.  Once installed in the client's
     *  hierarchy this component will then be able to draw and receive
     *  user input.
     * 
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
     * @return the component for editing
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {   Component component = null;
        if ( this.getCellEditor() != null )
        {   this.modifyComponent(table, value, isSelected, row, column);
            component = this.getCellEditor().getTableCellEditorComponent(table, value, isSelected, row, column);
        }
        return component;
    }

    /**
     * Returns the value contained in the editor.
     * 
     * @return the value contained in the editor
     */
    public Object getCellEditorValue()
    {   Object result = null;
        if ( this.getCellEditor() != null )
            result = this.getCellEditor().getCellEditorValue();
        return result;
    }

    /**
     * Tells the editor to cancel editing and not accept any partially
     * edited value.
     */
    public void cancelCellEditing()
    {   if ( this.getCellEditor() != null )
            this.getCellEditor().cancelCellEditing();
    }
    
}
