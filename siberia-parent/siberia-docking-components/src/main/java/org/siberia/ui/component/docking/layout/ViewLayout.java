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
package org.siberia.ui.component.docking.layout;

import net.infonode.docking.RootWindow;
import net.infonode.docking.View;

/**
 *
 * Interface that defines an element able to lay a view out into a RootDockingPanel
 *
 * @author alexis
 */
public interface ViewLayout
{
    /** return a placement for a given Object
     *  @param object an Object related to a view
     *  @return a ViewPlacement
     */
    public ViewConstraints getPosition(Object object);
    
    /** set the default placement for a given Object
     *  @param object an Object related to a view
     *  @param placement a ViewPlacement
     */
    public void setPosition(Object object, ViewConstraints placement);
    
    /** lay a view out into a RootWindow
     *  @param panel a RootWindow
     *  @param view the view
     *  @param object an Object related to the view
     */
    public void doLayout(RootWindow panel, View view, Object object);
    
    /** lay a view out into a RootWindow
     *  @param panel a RootWindow
     *  @param view the view
     *  @param object an Object related to the view
     *  @param placement a ViewPlacement
     */
    public void doLayout(RootWindow panel, View view, Object object, ViewConstraints placement);
    
}
