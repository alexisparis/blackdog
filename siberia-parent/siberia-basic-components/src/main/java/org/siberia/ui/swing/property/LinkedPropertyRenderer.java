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

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * Property renderer that contains a button
 *  to indicate that a dialog or something else exists to perform some changes on a property
 *
 *  T represents the kind of component that is returned by getInnerComponent
 *
 * @author alexis
 */
public abstract class LinkedPropertyRenderer<T extends Component> implements TableCellRenderer
{
    /** Link button */
    private JButton   button    = null;
    
    /** container return by the renderer */
    private Container container = null;
    
    /** Creates a new instance of LinkedPropertyRenderer */
    public LinkedPropertyRenderer()
    {	}
    
    /** return the button used by the renderer
     *	@return a JButton
     */
    public synchronized JButton getLinkButton()
    {
	if ( this.button == null )
	{
	    this.button = new LinkButton();
	    this.button.setVisible(false);
	}
	return this.button;
    }
    
    /** return a Component that will be insert at first index in the container
     *	@param table
     *	@param value
     *	@param selected
     *	@param hasFocus
     *	@param row
     *	@param column
     *	@return a Component
     */
    protected abstract T getInnerComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);
    
    /** insert the component returned by getInnerComponent to the given container
     *	@param container
     *	@param component
     *	@param linkButton true to indicate that component is the link button
     */
    protected void addComponentToContainer(Container container, Component component, boolean linkButton)
    {
	if ( container == null )
	{
	    throw new IllegalArgumentException("container cannot be null");
	}
	if ( component == null )
	{
	    throw new IllegalArgumentException("component cannot be null");
	}
	
	/** add it */
	GridBagConstraints gbc = new GridBagConstraints();
	
	if ( linkButton )
	{
	    gbc.gridx   = 2;
//	    gbc.fill    = gbc.VERTICAL;
	    gbc.weightx = 0;
	}
	else
	{
	    gbc.gridx   = 1;
	    gbc.fill    = gbc.BOTH;
	    gbc.weightx = 1;
	}
	
	container.add(component, gbc);
    }
    
    /** method that allow to create and customize the container
     *	especially, overwrite this method to use a specific layout manager for the container
     *	@return a container
     */
    protected Container createContainer()
    {
	JPanel cont = new JPanel();
	
	/** by default, use a */
	GridBagLayout layout = new GridBagLayout();
	cont.setLayout(layout);
	cont.setOpaque(true);
	cont.setBorder(null);
	
	/* container shall not get focus */
	cont.setFocusable(false);
	
	return cont;
    }
    

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
	Component newComponent = this.getInnerComponent(table, value, isSelected, hasFocus, row, column);
	
	if ( this.container == null )
	{
	    this.container = this.createContainer();
	    
	    /** add the button to the container */
	    this.addComponentToContainer(this.container, this.getLinkButton(), true);
	}
	
	Component toRemove = null;
	boolean add = false;
	if ( this.container.getComponentCount() < 2 )
	{
	    add = true;
	}
	else
	{
	    /** search the component to remove */
	    for(int i = 0; i < this.container.getComponentCount(); i++)
	    {
		Component current = this.container.getComponent(i);

		if ( current != this.getLinkButton() )
		{
		    if ( current != newComponent )
		    {
			toRemove = current;
			add = true;
		    }

		    break;
		}
	    }
	}
	
	/** remove old component and add the new one if different */
	if ( toRemove != null )
	{
	    this.container.remove(toRemove);
	}
	
	/** add the new component */
	if ( add )
	{
	    this.addComponentToContainer(this.container, newComponent, false);
	}
	
	if ( table != null )
	{
	    if (isSelected)
	    {
		this.container.setBackground(table.getSelectionBackground());
		this.container.setForeground(table.getSelectionForeground());
	    }
	    else
	    {
		this.container.setBackground(table.getBackground());
		this.container.setForeground(table.getForeground());
	    }
	}
	
	return this.container;
    }
    
}
