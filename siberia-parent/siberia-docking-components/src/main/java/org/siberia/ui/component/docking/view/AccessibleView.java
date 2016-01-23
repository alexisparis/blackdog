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
package org.siberia.ui.component.docking.view;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.Icon;
import net.infonode.docking.View;

/**
 *
 * Docking View that renders Swing Component<br>
 * This kind of view could be declared persistent.<br>
 * that means that even if the view is closed, it could <br>
 * be access thanks to the view menu of the root window<br>
 *
 * @author alexis
 */
public class AccessibleView extends View
{
    /** property name for DockingView accessibility */
    public static final String PROP_ACCESSIBILITY = "accessibility";
    
    /** accessible */
    private boolean               accessible     = false;
    
    /** Creates a new instance of ColdView
     *  @param name the name of the view
     *  @param icon an Icon
     *  @param Component a Component
     *  @param accessible true if the view always have to accessible<br>
     *          in the root window even if the view is closed
     */
    public AccessibleView(String name, Icon icon, Component component, boolean alwaysAccessible)
    {   super(name, icon, component);
        
        this.accessible = alwaysAccessible;
    }
    
    /** return true if the view can be accessed on the root window even if the view is closed
     *  @return true if the view can be accessed on the root window even if the view is closed
     */
    public boolean isAlwaysAccessible()
    {   return this.accessible; }
    
    /** tell if the view can be accessed on the root window even if the view is closed
     *  @param accessible true if the view can be accessed on the root window even if the view is closed
     */
    public void setAlwaysAccessible(boolean accessible)
    {   if ( this.accessible != accessible )
        {   
            this.accessible = accessible;
            
            this.firePropertyChange(PROP_ACCESSIBILITY, this.accessible, accessible);
        }
    }
}
