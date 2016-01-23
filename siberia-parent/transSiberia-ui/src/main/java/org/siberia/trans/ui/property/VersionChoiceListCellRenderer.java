/* 
 * TransSiberia-ui : siberia plugin frontend for TransSiberia
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
package org.siberia.trans.ui.property;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import org.siberia.trans.type.plugin.Version;

/**
 *
 * specific renderer that is related to VersionChoice
 *
 * @author alexis
 */
public class VersionChoiceListCellRenderer extends DefaultListCellRenderer
{
    
    /** Creates a new instance of VersionChoiceListCellRenderer */
    public VersionChoiceListCellRenderer()
    {
	super();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
	Component retValue = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	
	if ( retValue instanceof JLabel )
	{
	    if( value instanceof Version )
	    {
		((JLabel)retValue).setText( ((Version)value).toString() );
	    }
	    else
	    {
		((JLabel)retValue).setText("");
	    }
	    
	    ((JLabel)retValue).setHorizontalAlignment(SwingConstants.RIGHT);
	}
	
	return retValue;
    }
    
}
