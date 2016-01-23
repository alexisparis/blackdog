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
package org.siberia.ui.swing.table;

import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;

/**
 *
 * Empty renderer
 *
 * @author alexis
 */
public class EmptyRenderer extends DefaultTableRenderer
{   
    /** text to use for the empty renderer */
    private String  text    = null;
    
    /** set rather to use a text for the renderer to indicate clearly that it contains no data */
    private boolean useText = true;
    
    /** Creates a new instance of EmptyRenderer */
    public EmptyRenderer()
    {	}
    
    /** return true if we have to use a text for the renderer to indicate clearly that it contains no data
     *	@return a boolean
     */
    public boolean isToUseText()
    { 
	return this.useText;
    }
    
    /** true if we have to use a text for the renderer to indicate clearly that it contains no data
     *	@param useText a boolean
     */
    public void setUseText(boolean useText)
    { 
	this.useText = useText;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
	Component retValue = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	
	if ( retValue instanceof JLabel )
	{
	    ((JLabel)retValue).setOpaque(true);
		
	    if ( this.isToUseText() && this.text == null )
	    {
		ResourceBundle rb = ResourceBundle.getBundle(EmptyRenderer.class.getName());
		
		this.text = rb.getString("text");
		
		if ( this.text == null )
		{
		    this.text = "";
		}
	    }
	    
	    ((JLabel)retValue).setText( this.isToUseText() ? this.text : "" );
	}
	
	return retValue;
    }
    
}
