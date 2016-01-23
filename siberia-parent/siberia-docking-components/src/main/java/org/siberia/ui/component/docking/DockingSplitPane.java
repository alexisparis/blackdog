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

import java.awt.Component;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.SplitWindow;

/**
 *
 * Split pane with docking habilities
 *
 * @author alexis
 */
public class DockingSplitPane extends SplitWindow
{
    /** instance to use as the first component */
    private DockingWindow component1 = null;
    
    /** instance to use as the second component */
    private DockingWindow component2 = null;
    
    /** Creates a new instance of DockingSplitPane */
    public DockingSplitPane()
    {   this(true); }
    
    /** Creates a new instance of DockingSplitPane
     *  @param horizontal true if the split i horizontal
     */
    public DockingSplitPane(boolean horizontal)
    {   super(horizontal);
        
        this.setDividerLocation(0.5f);
    }

    /**
     * Creates a split window with with the given child windows.
     *
     * @param horizontal  true if the split is horizontal
     * @param leftWindow  the left/upper window
     * @param rightWindow the right/lower window
     */
    public DockingSplitPane(boolean horizontal, DockingWindow leftWindow, DockingWindow rightWindow)
    {   super(horizontal, leftWindow, rightWindow); }
    
    protected void doRemoveWindow(DockingWindow window)
    {   super.doRemoveWindow(window);
//        if (window == getLeftWindow()) {
//          leftWindow = null;
//          splitPane.setLeftComponent(null);
//        }
//        else {
//          rightWindow = null;
//          splitPane.setRightComponent(null);
//        }
    }
    
    public Component add(Component component)
    {   
        if ( component instanceof DockingWindow )
        {   boolean first = true;
            if ( component1 != null )
                first = false;
            
            this.initializeComponent( (DockingWindow)component, first );
        }
        else
        {   super.add(component); }
        return component;
    }
    
    public void setOrientation(String hor)
    {   if ( hor != null )
        {   if ( hor.equalsIgnoreCase("VERTICAL") )
                this.setHorizontal(false);
            if ( hor.equalsIgnoreCase("HORIZONTAL") )
                this.setHorizontal(true);
        }
    }
    
    public void setDividerLocation(String location)
    {   try
        {   float f = Float.parseFloat(location);
            this.setDividerLocation(f);
        }
        catch(NumberFormatException e)
        {   }
    }
    
    /** set the first component
     *  @param class a Class extending DockingWindow
     */
    public void setFirstComponent(Class c)
    {   this.initializeComponent(c, true); }
    
    /** set the second component
     *  @param class a Class extending DockingWindow
     */
    public void setSecondComponent(Class c)
    {   this.initializeComponent(c, false); }
    
    /** set the first component
     *  @param class a Class extending DockingWindow
     *  @param first true if it is the first component
     */
    protected void initializeComponent(Class c, boolean first)
    {   if ( c != null )
        {   try
            {   Object o = c.newInstance();
                
                this.initializeComponent( (DockingWindow)o, first );
            }
            catch(Exception e)
            {   throw new RuntimeException("Error when building the first DockingWindow component with " + c); }
        }
    }
    
    /** set the first component
     *  @param class a Class extending DockingWindow
     *  @param first true if it is the first component
     */
    protected void initializeComponent(DockingWindow c, boolean first)
    {   if ( c != null )
        {   if ( first )
                this.component1 = c;
            else
                this.component2 = c;
            
            if ( this.component1 != null && this.component2 != null )
                this.setComponent(this.component1, this.component2);
        }
    }
    
    /** initialize the component that are displayed in the split component
     *  @param component1 the first component
     *  @param component2 the first component
     */
    public void setComponent(DockingWindow component1, DockingWindow component2)
    {   this.setWindows(component1, component2); }
    
}
