/*
 * blackdog : audio player / manager
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.ui.list;

import java.awt.Component;
import java.awt.Font;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.blackdog.ui.FilterListModel;

/**
 *
 * @author alexis
 */
public class FilterList extends JList
{
    /** Creates a new instance of FilterList
     *	@param model a FilterListModel
     */
    public FilterList(FilterListModel model)
    {	
	super(model);
	
	this.setCellRenderer(new DefaultListCellRenderer()
	{
	    Map<Font, Font> fonts = new HashMap<Font, Font>(2);
	    
	    @Override
	    public Component getListCellRendererComponent(
		JList list,
		Object value,
		int index,
		boolean isSelected,
		boolean cellHasFocus)
	    {
		Component c = super.getListCellRendererComponent(
						    list,
						    value,
						    index,
						    isSelected,
						    cellHasFocus);
		
		if ( index == 0 || (index == 1 && getModel().getConsiderUnknown()) )
		{
		    if ( ! c.getFont().isBold() )
		    {
			Font newFont = fonts.get(c.getFont());
			if ( newFont == null )
			{
			    newFont = c.getFont().deriveFont(Font.BOLD);
			    this.fonts.put(c.getFont(), newFont);
			}
			
			c.setFont(newFont);
		    }
		}
		else
		{
		    if ( c.getFont().isBold() )
		    {
			Font newFont = fonts.get(c.getFont());
			if ( newFont == null )
			{
			    newFont = c.getFont().deriveFont(Font.PLAIN);
			    this.fonts.put(c.getFont(), newFont);
			}
			
			c.setFont(newFont);
		    }
		}
		
		return c;
	    }
	});
    }
    
    @Override
    public FilterListModel getModel()
    {
	return (FilterListModel)super.getModel();
    }
    
    /** return true if all item is selected
     *	@return true if the item 'all' is selected
     */
    public boolean isAllSelected()
    {
	return this.getSelectionModel().isSelectedIndex(0);
    }
    
    /** return true if only all items is selected
     *	@return a boolean
     */
    public boolean isOnlyAllSelected()
    {
	boolean result = false;
	
	if ( this.isAllSelected() )
	{
	    int[] selection = this.getSelectedIndices();
	    
	    if ( selection != null && selection.length == 1 )
	    {
		result = true;
	    }
	}
	
	return result;
    }
    
    /** select all items only */
    public void selectAllItems()
    {
	this.setSelectedIndex(0);
    }
    
    /** keep selection as possible
     *	@param selection a Collection of selectedItem to try to set as selection if there exists
     */
    public void setSelectedItems(Collection<String> selection)
    {
	this.setSelectedItems(selection, false);
    }
    
    /** keep selection as possible
     *	@param selection a Collection of selectedItem to try to set as selection if there exists
     *	@param selectUnknown true to force unknown selection
     */
    public void setSelectedItems(Collection<String> selection, boolean selectUnknown)
    {
	this.getSelectionModel().clearSelection();
	
	boolean applyDefaultSelection = false;
	
	if ( (selection != null && selection.size() > 0) || selectUnknown )
	{
	    if ( selection != null )
	    {
		Iterator<String> it = selection.iterator();

		while(it.hasNext())
		{
		    String current = it.next();

		    int position = this.getModel().indexOf(current);
		    
//		    if ( position == 1 )
//		    {
//			System.out.println("current is : " + current);
//		    }

		    if ( position >= 0 && position < this.getModel().getSize() )
		    {
			this.getSelectionModel().addSelectionInterval(position, position);
		    }
		}
	    }
	    if ( selectUnknown )
	    {
		if ( this.getModel().getConsiderUnknown() )
		{
		    this.getSelectionModel().addSelectionInterval(1, 1);
		}
	    }
	    
	    /** if no selection, then select all items */
	    if ( this.getSelectionModel().isSelectionEmpty() )
	    {
		applyDefaultSelection = true;
	    }
	}
	else
	{
	    applyDefaultSelection = true;
	}
	
	if ( applyDefaultSelection )
	{
	    this.getSelectionModel().setSelectionInterval(0, 0);
	}
    }
    
    /** return true if unknown item is selected
     *	@return a boolean
     */
    public boolean isUnknownSelected()
    {
	boolean result = false;
	
	if ( this.getModel().getConsiderUnknown() && this.getSelectionModel().isSelectedIndex(1) )
	{
	    result = true;
	}
	
	return result;
    }
    
    /** return a new Set containing all selected element except all items and unknown
     *	@return a Set
     */
    public Set<String> getSpecificSelectedItems()
    {
	Set<String> set = null;
	
	Object[] values = this.getSelectedValues();
	
	if ( values != null && values.length > 0 )
	{
	    for(int i = 0; i < values.length; i++)
	    {
		if ( set == null )
		{
		    set = new HashSet<String>(values.length);
		}
		
		Object current = values[i];
		
		if ( current instanceof String )
		{   set.add( (String)current ); }
	    }
	}
	
	if ( set == null )
	{
	    set = Collections.emptySet();
	}
	else
	{
	    set.remove(this.getModel().getLabelAllItems());
	    set.remove(this.getModel().getLabelUnknownItems());
	}
	
	return set;
    }
    
}
