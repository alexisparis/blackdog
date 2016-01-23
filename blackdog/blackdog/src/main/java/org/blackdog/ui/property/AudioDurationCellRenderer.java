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
package org.blackdog.ui.property;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.siberia.ui.swing.property.simple.SimpleCellRenderer;
import org.blackdog.type.base.AudioDuration;

/**
 *
 * @author alexis
 */
public class AudioDurationCellRenderer extends SimpleCellRenderer
{
    
    /** Creates a new instance of AudioDurationCellRenderer */
    public AudioDurationCellRenderer()
    {	}
    
    /** method that allow to customize a JLabel according to the value
     *	@param label a JLabel
     *	@param value an Object
     */
    @Override
    protected void customizeLabel(JLabel label, Object value)
    {
	if ( label != null )
	{
	    if ( value instanceof AudioDuration )
	    {
		label.setText( ((AudioDuration)value).getStringRepresentation() );		
	    }
	    else
	    {
		label.setText("");
	    }
	    
	    label.setHorizontalAlignment(SwingConstants.RIGHT);
	    label.setHorizontalTextPosition(SwingConstants.RIGHT);
	}
    }
    
}
