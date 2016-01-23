/* =============================================================================
 * Siberia bars
 * =============================================================================
 *
 * Project Lead:  Alexis Paris
 *
 * (C) Copyright 2007, by Alexis Paris.
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
package org.siberia.bar.menu;

import java.awt.Component;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JMenu;

/**
 *
 * @author alexis
 */
public class ColdMenu extends JMenu
{
    /** Creates a new instance of ColdMenu */
    public ColdMenu()
    {   super(); }
    
    @Override
    public void setSelected(boolean b)
    {   if ( b )
        {   for(int i = 0; i < this.getItemCount(); i++)
            {   Component c = this.getItem(i);
                if ( c instanceof AbstractButton )
                {   Action a = ((AbstractButton)c).getAction();
                    if ( a != null )
                    {   boolean shouldBeEnabled = a.isEnabled();
                        if ( shouldBeEnabled != c.isEnabled() )
                            c.setEnabled(shouldBeEnabled);
                    }
                }
            }
        }
        super.setSelected(b);
    }
}
