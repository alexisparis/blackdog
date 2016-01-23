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

import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableModel;
import org.siberia.type.SibType;
import org.siberia.ui.swing.property.event.PropertySheetPanelEvent;
import org.siberia.ui.swing.property.event.PropertySheetPanelListener;

/**
 *
 * @author alexis
 */
public class ExtendedPropertySheetPanel extends PropertySheetPanel
{
    /** event listener list */
    private EventListenerList listeners = new EventListenerList();
    
    public ExtendedPropertySheetPanel(PropertySheetTable table)
    {
	super(table);
	
	/* I do not want to see the action panel */
	for(int i = 0; i < this.getComponentCount(); i++)
	{
	    Component current = this.getComponent(i);
	    
	    if ( current instanceof JPanel )
	    {
		if ( ((JPanel)current).getComponentCount() == 3 )
		{
		    /* if all sub components are JToggleButton, then make this panel invisible */
		    boolean containsOnlyJToggleButton = true;
		    for(int j = 0; j < ((JPanel)current).getComponentCount(); j++)
		    {
			if ( ! (((JPanel)current).getComponent(j) instanceof JToggleButton) )
			{
			    containsOnlyJToggleButton = false;
			    break;
			}
		    }
		    
		    if ( containsOnlyJToggleButton )
		    {
			current.setVisible(false);
			break;
		    }
		}
	    }
	}
	
	this.setDescriptionVisible(false);
    }
    
    /**
     * Initializes the PropertySheet from the given object. If any, it cancels
     * pending edit before proceeding with properties.
     *
     * @param data
     */
    public void readFromObjects(SibType[] data)
    {
	// cancel pending edits
	getTable().cancelEditing();
	
	if ( this.getTable() != null )
	{
	    TableModel model = this.getTable().getModel();
	    
	    if ( model instanceof ExtendedPropertySheetTableModel )
	    {
		((ExtendedPropertySheetTableModel)model).readFromObjects(data);
	    }
	}
	this.repaint();
	
	this.fireTablecontentUpdated();
    }
    
    /* ######################################################################
     * ################## PropertySheetPanelListener impl ###################
     * ###################################################################### */
    
    /** add a PropertySheetPanelListener
     *	@param listener a PropertySheetPanelListener
     */
    public void addPropertySheetPanelListener(PropertySheetPanelListener listener)
    {
	if ( listener != null )
	{
	    this.listeners.add(PropertySheetPanelListener.class, listener);
	}
    }
    
    /** remove a PropertySheetPanelListener
     *	@param listener a PropertySheetPanelListener
     */
    public void removePropertySheetPanelListener(PropertySheetPanelListener listener)
    {
	if ( this.listeners != null )
	{
	    this.listeners.remove(PropertySheetPanelListener.class, listener);
	}
    }
    
    /** fire table content updated */
    private void fireTablecontentUpdated()
    {
	PropertySheetPanelEvent evt = null;
	
	if ( this.listeners != null )
	{
	    PropertySheetPanelListener[] beanListeners = (PropertySheetPanelListener[])this.listeners.getListeners(PropertySheetPanelListener.class);
	    
	    if ( beanListeners != null )
	    {
		for(int i = 0; i < beanListeners.length; i++)
		{
		    PropertySheetPanelListener currentListener = beanListeners[i];
		    
		    if ( currentListener != null )
		    {
			if ( evt == null )
			{
			    evt = new PropertySheetPanelEvent(this);
			}
			
			currentListener.tableContentUpdated(evt);
		    }
		}
	    }
	}
    }
    
}
