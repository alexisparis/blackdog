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
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import org.apache.log4j.Logger;

/**
 *
 * provides methods to manage DockingWindows
 *
 * @author alexis
 */
public class DockingWindowUtilities
{   
    /** logger */
    private static Logger logger = Logger.getLogger(DockingWindowUtilities.class);
    
    /** insert a SplitPane between the parent window
     *  @param parentWindow a DockingWindow
     *  @param horizontal true if the inserted SplitPane is to be horizontal
     *  @param currentChildWindow the current Window contained by parentWindow
     *  @param isFirstInFutureSplitPane indicates if currentChildWindow has to be the first window in the inserted SplitWindow
     *  @return the inserted SplitWindow
     */
    public static SplitWindow insertSplitWindow(DockingWindow parentWindow, boolean horizontal,
                                       final DockingWindow currentChildWindow, final boolean isFirstInFutureSplitPane)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling insertSplitWindow(parent=" + parentWindow + ", horizontal?" + horizontal + ", child=" + 
				    currentChildWindow + ", firstInSplit?" + isFirstInFutureSplitPane + ")");
	}
        final SplitWindow subSplit = new SplitWindow(horizontal);
        
        boolean isRightComponent = false;
        if ( parentWindow instanceof SplitWindow )
        {   isRightComponent = ((SplitWindow)parentWindow).getRightWindow() == currentChildWindow; }
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("current child is the right child in the parent ? " + isRightComponent);
	}
        
        /* 
         * WARNING :
         * add additive DockingTabbedPanes to avoid NullPointer when 
         * running "optimizeAfter(null, new Runnable()" on SplitWindow.setWindows(...)
         * the true window elements will be set after indicating the
         * new elements to set as windows in parentSplit
         */
        subSplit.setWindows( new TabWindow(), new TabWindow() );

        if ( parentWindow instanceof RootWindow )
        {   //((RootWindow)parentWindow).getRootWindowProperties().setRecursiveTabsEnabled(true);
            ((RootWindow)parentWindow).setWindow(subSplit);
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("parent is a RootWindow --> setting its window with the new SplitWindow created");
	    }
        }
        else if ( parentWindow instanceof SplitWindow )
        {   SplitWindow parentSplit = (SplitWindow)parentWindow;

            if ( isRightComponent )
            {   
		parentSplit.setWindows( parentSplit.getLeftWindow(), subSplit );
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("parent is a SplitWindow --> setting windows (" + parentSplit.getLeftWindow() + ", the new SplitWindow)");
		}
	    }
            else
            {   
		parentSplit.setWindows( subSplit, parentSplit.getRightWindow() );
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("parent is a SplitWindow --> setting windows (the new SplitWindow, " + parentSplit.getLeftWindow() + ")");
		}
	    }
	    
        }
        else
	{
            throw new RuntimeException("unable to consider " +
                                (parentWindow == null ? "null" : parentWindow.getClass()));
	}
        
        try
        {
            subSplit.setWindows( isFirstInFutureSplitPane ? currentChildWindow : new TabWindow(),
                                 isFirstInFutureSplitPane ? new TabWindow() : currentChildWindow );
        }
        catch(IndexOutOfBoundsException e)
        {   System.err.println("error occurred in insertSplitWindow"); }
        
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("insertSplitWindow returns " + subSplit);
	}
	
        return subSplit;
    }
    
    /** insert a TabWindow as parent of the given view ( respect the current window hierarchy by
     *      adding the new TabWindow as child of the parent of the view before insertion )
     *  @param view a View
     *  @return the inserted TabWindow
     */
    public static TabWindow insertTabWindow(View view)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling insertTabWindow(" + view + ")");
	}
        TabWindow tabbed = null;
        
        if ( view != null )
        {   tabbed = new TabWindow();
            
            DockingWindow parent = view.getWindowParent();
            
            boolean firstWindow = true;
            if ( parent instanceof SplitWindow )
            {   firstWindow = ((SplitWindow)parent).getLeftWindow() == view; }
            
            if ( parent != null )
            {   if ( parent instanceof RootWindow )
                {   
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("parent of view is a RootWindow");
		    }
		    
		    tabbed.addTab(view);
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("view added in the new tab");
		    }
		    
                    ((RootWindow)parent).setWindow(tabbed);
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("root window set to tabbed docking component");
		    }
                }
                else if ( parent instanceof SplitWindow )
                {   
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("parent of view is a SplitWindow");
		    }
		    tabbed.addTab(view);
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("view added in the new tab");
		    }
                    SplitWindow split = (SplitWindow)parent;
                    split.setWindows(firstWindow ? tabbed : split.getRightWindow(),
                                     firstWindow ? split.getLeftWindow() : tabbed);
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("tabbed set as " + (firstWindow ? "left" : "right") + " components of SplitPane");
		    }
                }
                else if ( parent instanceof TabWindow )
                {   
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("parent of view is a TabWindow");
		    }
		    ((TabWindow)parent).addTab(tabbed);
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("new TabWindow added as tab of the old view parent");
		    }
                    tabbed.addTab(view);
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("view added in the new tab");
		    }
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("insertTabWindow returns " + tabbed);
	}
        return tabbed;
    }
    
    /** remove the empty container
     *  @param container a DockingWindow
     */
    public static void removeEmptyContainers(DockingWindow container)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("removing empty docking containers");
	    logger.error("TO DO");
	}
	
//	/** affichage de l'arbre */
//	System.out.println("container : " + container.getClass());
//	
//	if ( container instanceof EditorRootDockingPanel )
//	{
//	    System.out.println("window tree : ");
//	    ((EditorRootDockingPanel)container).printWindowTree();
//	}
//        
//        if ( container != null )
//        {   
//	    System.out.println("window count : " + container.getChildWindowCount());
//	    for(int i = 0; i < container.getChildWindowCount(); i++)
//            {   
//                DockingWindow current = container.getChildWindow(i);
//		
//		System.out.println("\tcurrent : " + current);
//                
//		if ( current instanceof SplitWindow )
//		{
//		    DockingWindow rightWindow = ((SplitWindow)current).getRightWindow();
//		    DockingWindow leftWindow = ((SplitWindow)current).getRightWindow();
//		    
//		    removeEmptyContainers(rightWindow);
//		    removeEmptyContainers(leftWindow);
//		    
//		    
//		}
//		else if ( current instanceof TabWindow )
//		{
//		    System.out.println("tab count : " + ((TabWindow)current).getChildWindowCount());
//		    
//		    removeEmptyContainers(current);
//		    
//		    if ( current.getChildWindowCount() == 0 )
//		    {
//			if ( current.getWindowParent() != null )
//			{
//			    current.close();
//			}
//		    }
//		}
//            }
//        }
    }
    
}
