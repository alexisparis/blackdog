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
package org.siberia.ui.swing.combo;

import java.awt.Component;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import org.apache.log4j.Logger;

/**
 *
 * ListCellRenderer for JComboBox that deal with enumeration entries
 *
 *  this ListCellRenderer can use two methods : 
 *  - default : search for a method called label() that returns a internationalized String.
 *  - or use the given ResourceBundle when it get the label to use according to the name of the value enumeration
 *
 * @author alexis
 */
public class EnumListCellRenderer extends DefaultListCellRenderer
{
    /** logger */
    private Logger            logger  = Logger.getLogger(EnumListCellRenderer.class.getName());
    
    /** cache linking enumeration part and text to render */
    private Map<Enum, String> textMap = null;
    
    /** ResourceBundle */
    private ResourceBundle    i18nrb  = null;
    
    /** create a new EnumListCellRenderer */
    public EnumListCellRenderer()
    {	this(null); }
    
    /** create a new EnumListCellRenderer
     *	@param rb the resource bundle used to get the label to be used for values
     */
    public EnumListCellRenderer(ResourceBundle rb)
    {
	this.setI18nRb(rb);
    }
    
    /** return the resource bundle
     *	@return a ResourceBundle
     */
    public ResourceBundle getI18nRb()
    {
	return this.i18nrb;
    }
    
    /** set the resource bundle to be used to get the label according to a value
     *	set the resource bundle to null to use default behaviour
     *	@param rb a ResourceBundle
     */
    public void setI18nRb(ResourceBundle rb)
    {
	if ( this.i18nrb != rb )
	{
	    this.i18nrb = rb;
	    
	    if ( rb == null )
	    {
		/* clear map if necessary */
		if ( this.textMap != null )
		{
		    this.textMap.clear();
		    this.textMap = null;
		}
	    }
	}
    }
    
    public Component getListCellRendererComponent(
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
    {   Component compo = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if ( compo instanceof JLabel && value != null )
        {   
            try
            {   
		String s = null;
		
		if ( value instanceof Enum )
		{
		    
		    if ( this.i18nrb != null )
		    {
			s = this.i18nrb.getString( ((Enum)value).name() );
		    }
		    else
		    {
			boolean contains = false;
				
			if ( this.textMap != null )
			{
			    contains = this.textMap.containsKey(value);
			}
			else
			{
			    this.textMap = new WeakHashMap<Enum, String>(20);
			}

			if ( contains )
			{
			    s = this.textMap.get(value);
			}
			else
			{
			    try
			    {
				/** perhaps is there a method label that return a String */
				Method m = value.getClass().getMethod("label", new Class[]{});

				if ( m.getReturnType().equals(String.class) )
				{   s = (String)m.invoke(value, new Object[]{});

				    this.textMap.put((Enum)value, s);
				}
			    }
			    catch (Exception ex)
			    {
				logger.warn("unable to retrieve label from enumeration '" + value + "'", ex);

				/* force not to search anymore for this value */
				this.textMap.put((Enum)value, null);
			    }
			}
		    }
		    
		    /** default behaviour --> use value */
		    if ( s == null )
		    {
			s = ((Enum)value).name();
		    }
		}
		
		((JLabel)compo).setText( s );
            }
            catch (Exception ex)
            {   ex.printStackTrace(); }
        }
        
        return compo;
    }
}
