/*
 * Siberia docking window : define an editor support based on Infonode docking window framework
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
package org.siberia.ui.component.docking;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.TabWindow;

/**
 *
 * TabbedPane extension
 *
 * @author alexis
 */
public class DockingTabbedPane extends TabWindow
{
    /** indicates if the tabbedPane is permanent */
    private boolean permanent = false;
    
    /** Creates a new instance of DockingTabbedPane */
    public DockingTabbedPane()
    {   super();
        
//        this.addTab(new View("A", null, new JButton("A")));
    }

    /** return true if the tabbedPane is permanent
     *  @return a boolean
     */
    public boolean isPermanent()
    {   return permanent; }

    /** indicates if the tabbedPane is permanent
     *  @param permanent true if the tabbedPane is permanent
     */
    public void setPermanent(boolean permanent)
    {   this.permanent = permanent;
        
        if ( this.permanent )
        {   this.addTab(new InvisibleView()); }
    }
    
    /** add a tab at a given position and choose to select it
     *  @param window a DockingWindow
     */
    public void addTab(DockingWindow window)
    {   this.addTab(window, true); }
    
    /** add a tab at a given position and choose to select it
     *  @param window a DockingWindow
     *  @param selectIt indicate if the new tab containing the new window has to be selected
     */
    public void addTab(DockingWindow window, boolean selectIt)
    {   this.addTab(window, this.getTabbedPanel().getTabCount(), selectIt); }
    
    /** add a tab at a given position and choose to select it
     *  @param window a DockingWindow
     *  @param index 
     *  @param selectIt indicate if the new tab containing the new window has to be selected
     */
    public void addTab(DockingWindow window, int index, boolean selectIt)
    {   if ( selectIt )
        {   if ( index >= 0 )
            {   this.addTab(window, index); }
            else
            {   this.addTab(window); }
        }
        else
        {   this.addTabNoSelect(window, index); }        
    }
}
