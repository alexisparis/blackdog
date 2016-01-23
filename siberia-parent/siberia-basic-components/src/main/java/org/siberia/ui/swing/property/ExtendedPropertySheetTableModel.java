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
package org.siberia.ui.swing.property;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyVetoException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.siberia.type.SibType;
import org.siberia.type.Namable;
import org.siberia.ui.swing.dialog.ExtendedSwingWorker;

/**
 *
 * PropertySheetTableModel that can be linked to a Bean class instance
 *
 * @author alexis
 */
public class ExtendedPropertySheetTableModel extends PropertySheetTableModel implements PropertyChangeListener
{   
    /** logger */
    private Logger                           logger               = Logger.getLogger(ExtendedPropertySheetTableModel.class);
    
    /** Object actually managed by the model */
    private SibType[]                        objects              = null;
    
    /** List of PropertyVetoListener listener */
    private Collection<PropertyVetoListener> listeners            = null;
    
    /** indicate the name of the property concerned by a current call to setValueAt
     *	null to indicate that it is not the case
     */
    private String                           inSetValueAtProperty = null;
    
    /** Creates a new instance of ExtendedPropertySheetTableModel */
    public ExtendedPropertySheetTableModel()
    {   super(); }

    /** return the objects managed by the model
     *  @return an array SibType
     */
    public SibType[] getObjects()
    {   return this.objects; }

    /** initialize the object managed by the model
     *  @param object an SibType
     */
    public void setObjects(SibType... objects)
    {   
	/** remove PropertyChangeListener from old object */
	if ( this.getObjects() != null )
	{
	    for(int i = 0; i < this.getObjects().length; i++)
	    {
		SibType current = this.getObjects()[i];
		
		if ( current != null )
		{
		    current.removePropertyChangeListener(this);
		}
	    }
	}

	this.objects = objects;

	/** add PropertyChangeListener to new object */
	if ( this.getObjects() != null )
	{
	    for(int i = 0; i < this.getObjects().length; i++)
	    {
		SibType current = this.getObjects()[i];
		
		if ( current != null )
		{
		    current.addPropertyChangeListener(this);
		}
	    }
	}
    }
    
    /**
     * Initializes the PropertySheet from the given object. If any, it cancels
     * pending edit before proceeding with properties.
     *
     * @param data
     */
    public void readFromObjects(SibType[] data)
    {
	this.setObjects(data);
	
	Property[] properties = this.getProperties();
	for (int i = 0, c = properties.length; i < c; i++)
	{
	    this.updatePropertyValue(properties[i], data);
	}
    }

    /** overwrite setValueAt to write the value to the current Object
     *
     */
    public void setValueAt(Object value, final int rowIndex, int columnIndex)
    {
	Item item = this.getPropertySheetElement(rowIndex);
	if ( item != null )
	{
	    Property property = item.getProperty();
	    
	    if ( property != null )
	    {
		this.inSetValueAtProperty = property.getName();
	    }
	}
	
        /** keep the value before modification to restore it if VetoExceptions are throwed */
        Object oldValue = this.getValueAt(rowIndex, columnIndex);
        
        super.setValueAt(value, rowIndex, columnIndex);
        
        Item i = this.getPropertySheetElement(rowIndex);
	
	/** create a copy of the array of objects actually managed by the model to prevent from selection change from outside */
	SibType[] copy = null;
	
	if ( this.getObjects() != null )
	{
	    copy = new SibType[this.getObjects().length];
	    
	    System.arraycopy(this.getObjects(), 0, copy, 0, this.getObjects().length);
	}
	
	if ( copy != null )
	{   
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("calling setValueAt(" + value + ", " + rowIndex + ", " + columnIndex + ") on items : ");
		
		if ( this.getObjects() != null )
		{
		    for(int j = 0; j < copy.length; j++)
		    {
			logger.debug("\t" + copy[j]);
		    }
		}
	    }
	    
	    for(int j = 0; j < copy.length; j++)
	    {
		SibType current = copy[j];
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("updating property on object : " + current);
		}
		
		if ( current != null )
		{
		    try
		    {   
			i.getProperty().writeToObject(current);
		    }
		    catch(RuntimeException e)
		    {   
			e.printStackTrace();
			if ( e.getCause() instanceof PropertyVetoException )
			{   
			    super.setValueAt(oldValue, rowIndex, columnIndex);
			    this.fireVetoException(i, (PropertyVetoException)e.getCause());
			}
			else
			{   throw e; }
		    }
		}   
	    }
	}
	
	this.inSetValueAtProperty = null;
	
	this.updatePropertyValue(i.getProperty(), this.getObjects());
	
	Runnable runnable = new Runnable()
	{
	    public void run()
	    {
		fireTableCellUpdated(rowIndex, PropertySheetTableModel.VALUE_COLUMN);
	    }
	};
	
	if ( SwingUtilities.isEventDispatchThread() )
	{
	    runnable.run();
	}
	else
	{
	    SwingUtilities.invokeLater(runnable);
	}
	
    }
    
    /** return the property related to the property named propertyName
     *  @param propertyName a String representing the name of a property
     *  @return a Property or null if not found
     */
    public Property getPropertyFor(String propertyName)
    {   Property result = null;
        
        if ( propertyName != null )
        {   Property[] properties = this.getProperties();
            if ( properties != null )
            {   for(int i = 0; i < properties.length; i++)
                {   Property current = properties[i];
                    
                    if ( current != null )
                    {   if ( propertyName.equals(current.getName()) )
                        {   result = current;
                            break;
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    /** return the index of the row related to the property named propertyName
     *  @param propertyName a String representing the name of a property
     *  @return an integer or -1 if not found
     */
    public int getRowForProperty(String propertyName)
    {   int rowIndex = -1;
        
        if ( propertyName != null )
        {   
	    for(int i = 0; i < this.getRowCount(); i++)
	    {
		Item item = getPropertySheetElement(i);
		
		if ( item != null )
		{
		    Property currentProperty = item.getProperty();
		    
		    if ( currentProperty != null && currentProperty.getName() != null && propertyName.equals(currentProperty.getName()) )
		    {
			rowIndex = i;
			break;
		    }
		}
	    }
	}
        
        return rowIndex;
    }
    
    /** add a new PropertyVetoListener
     *  @param listener an instance of PropertyVetoListener
     */
    public void addPropertyVetoListener(PropertyVetoListener listener)
    {   if ( listener != null )
        {   if ( this.listeners == null )
                this.listeners = new ArrayList<PropertyVetoListener>();
            this.listeners.add(listener);
        }
    }
    
    /** remove a new PropertyVetoListener
     *  @param listener an instance of PropertyVetoListener
     */
    public void removePropertyVetoListener(PropertyVetoListener listener)
    {   if ( listener != null && this.listeners != null )
        {   this.listeners.remove(listener); }
    }
    
    /** method that is called to fire event to PropertyVetoListener
     *  @param item the Item that is responsible
     *  @param exception a PropertyVetoException
     */
    public void fireVetoException(Item item, PropertyVetoException exception)
    {   this.fireVetoException(this.getObjects(), item, exception); }
    
    /** method that is called to fire event to PropertyVetoListener
     *  @param the current object managed by the model
     *  @param item the Item that is responsible
     *  @param exception a PropertyVetoException
     */
    public void fireVetoException(Object[] objects, Item item, PropertyVetoException exception)
    {   if ( this.listeners != null )
        {   Iterator<PropertyVetoListener> it = this.listeners.iterator();
            while(it.hasNext())
            {   PropertyVetoListener current = it.next();
                
                if ( current != null )
                {   current.vetoException(this, objects, item, exception); }
            }
        }
    }
    
    /** apply a value to a Property according to objects
     *	@param property a Property
     *	@param objects an array of objects
     */
    private void updatePropertyValue(Property property, SibType[] objects)
    {
	if ( property != null && objects != null )
	{
	    for(int i = 0; i < objects.length; i++)
	    {
		SibType current = objects[i];
		
		Object oldValue = property.getValue();
		
		property.readFromObject(current);
		
		Object newValue = property.getValue();
		
		boolean equalsValue = false;
		
		if ( i == 0 )
		{
		    equalsValue = true;
		}
		else
		{
		    if ( oldValue == null )
		    {
			if ( newValue == null )
			{
			    equalsValue = true;
			}
		    }
		    else
		    {
			if ( newValue != null )
			{
			    equalsValue = oldValue.equals(newValue);
			}
		    }
		}
		
		if ( ! equalsValue )
		{
		    property.setValue(DifferentValues.INSTANCE);
		    break;
		}
	    }
	}
    }
    
    /* #########################################################################
     * ################ PropertyChangeListener implementation ##################
     * ######################################################################### */
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */
     @Override
    public void propertyChange(PropertyChangeEvent evt)
    {   
	 super.propertyChange(evt);
	 
	 boolean considerChangeEvent = false;
	 
	 if ( this.inSetValueAtProperty == null )
	 {
	     considerChangeEvent = true;
	 }
	 else
	 {
	     /* if the change does not concern the current property in SetValueAt call, then, we have to indicate the update */
	     if ( ! this.inSetValueAtProperty.equals(evt.getPropertyName()) )
	     {
		 considerChangeEvent = true;
	     }
	 }
	 
	 if ( considerChangeEvent )
	 {
	     boolean concernOwnItems = false;
	     
	     if ( this.getObjects() != null )
	     {
		 for(int i = 0; i < this.getObjects().length; i++)
		 {
		     SibType current = this.getObjects()[i];
		     
		     if ( evt.getSource() == current )
		     {
			 concernOwnItems = true;
			 break;
		     }
		 }
	     }
	     
	     if ( concernOwnItems )
	     {   /** according to property name, we are able to determine the row that display the
	      *  information according to the property change
	      */
		 String propName = evt.getPropertyName();
		 if ( propName != null )
		 {   int index = this.getRowForProperty(propName);
		     
		     if ( index > -1 && index < this.getRowCount() )
		     {
			 Property property = this.getPropertyFor(evt.getPropertyName());
			 
			 if ( property != null )
			 {
			     this.updatePropertyValue(property, this.getObjects());
			     this.fireTableCellUpdated(index, PropertySheetTableModel.VALUE_COLUMN);
			 }
		     }
		 }
	     }
	 }
    }
    
    /** represents a value for a Property that indicate that objects have differents values */
    static class DifferentValues extends Object
    {
	public static final DifferentValues INSTANCE = new DifferentValues();
	
	private DifferentValues()
	{   }
    }
    
}
