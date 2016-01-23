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

import java.awt.Rectangle;
import javax.swing.SwingUtilities;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import org.apache.log4j.Logger;
import org.siberia.ui.component.docking.DockingTabbedPane;
import org.siberia.ui.component.docking.DockingWindowUtilities;

/**
 *
 * It defines a ViewLayout which allows views to be placed by giving cardinal points
 *
 * @author alexis
 */
public class BorderViewLayout extends AbstractViewLayout
{
    /** logger */
    private transient Logger logger = Logger.getLogger(BorderViewLayout.class);
    
    /** Creates a new instance of BorderViewLayout */
    public BorderViewLayout()
    {   super(); }
    
    /** indicates if the layout accept those kind of ViewPlacement
     *  @param placement a ViewPlacement
     *  @return true if the layout support this kind of ViewPlacement
     */
    public boolean accept(ViewConstraints placement)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling accept(" + placement + ")");
	}
	boolean accept = true;
        if ( placement == null )
            accept = false;
        else
        {   if ( ! (placement instanceof BorderViewConstraints) )
                accept = false;
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of accept(" + placement + ") returns " + accept);
	}
        return accept;
    }
    
    /** return a placement for a given Object
     *  @param object an Object related to a view
     *  @return a ViewPlacement
     */
    public ViewConstraints getPosition(Object object)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getPosition(" + object + ")");
	}
	ViewConstraints placement = null;
        if ( this.defaultPlacements != null )
        {   
            if ( object instanceof Class )
            {   Class current = (Class)object;
                
                while(current != Object.class)
                {   
                    /* test the class directly */
                    placement = this.defaultPlacements.get(object);
                    if ( placement != null )
                        break;
                    
                    /* test interfaces */
                    for(int i = 0; i < current.getInterfaces().length; i++)
                    {   Class c = current.getInterfaces()[i];
                        
                        placement = this.defaultPlacements.get(c);
                        if ( placement != null )
                            break;
                    }
                    
                    if ( placement != null )
                        break;
                    
                    current= current.getSuperclass();
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of getPosition(" + object + ") returns " + placement);
	}
        
        return placement;
    }
    
    /** create a default ViewPlacement
     *  @return a ViewPlacement
     */
    public ViewConstraints createDefaultPlacement()
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling createDefaultPlacement()");
	}
	
	ViewConstraints cc = BorderViewConstraints.DEFAULT_PLACEMENT;
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of createDefaultPlacement()");
	}
	
	return cc;
    }
    
    /** lay a view out into a RootWindow
     *  @param panel a RootWindow
     *  @param view the view
     *  @param object an Object related to the view
     *  @param placement a ViewPlacement
     */
    public void doLayoutImpl(RootWindow panel, View view, Object object, ViewConstraints placement)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling doLayoutImpl(rootwindow=" + panel + ", view=" + view + ", object=" + object + ", placement=" + placement + ")");
	}
        
//        if ( panel instanceof RootDockingPanel )
//        {   ((RootDockingPanel)panel).printWindowTree(); }
        
        DockingWindow candidate       = null;
        Rectangle     candidateBounds = null;
        
        BorderViewConstraints place     = null;
        if ( placement instanceof BorderViewConstraints )
        {   place = (BorderViewConstraints)placement; }
        else
        {   place = BorderViewConstraints.DEFAULT_PLACEMENT; }
        
        place = (BorderViewConstraints)place.clone();
        
        if ( panel != null )
        {   
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("trying to add the view on a child of the root window");
	    }
	    
	    for(int i = 0; i < panel.getChildWindowCount(); i++)
            {   DockingWindow current = panel.getChildWindow(i);
                
                if ( current instanceof TabWindow || current instanceof SplitWindow )
                {   this.addToWindow(current, view, object, place);
                    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("view " + view + " added");
		    }
//                    if ( panel instanceof RootDockingPanel )
//                    {   ((RootDockingPanel)panel).printWindowTree(); }
                    return;
                }
            }
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("trying to add the view on a child of the root window --> failed");
	    }
            
            if ( panel instanceof RootWindow )
            {   panel.setWindow(new DockingTabbedPane());
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("setting widow of the root window with a TabWindow and try to add view");
		}
                this.doLayoutImpl(panel, view, object, placement);
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of doLayoutImpl(rootwindow=" + panel + ", view=" + view + ", object=" + object + ", placement=" + placement + ")");
	}
    }
    
    /** modify to constraints
     *  @param horizontal true if the current splitwindow is horizontal
     *  @param place a Border constraints
     */
    protected void reduceConstraints(boolean horizontal, BorderViewConstraints place)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling reduceConstraints(horizontal?" + horizontal + ", place=" + place + ")");
	}
	if ( horizontal )
        {   switch(place.getHorizontalAlignement())
            {
                case(BorderViewConstraints.NONE)   : break;
                case(BorderViewConstraints.CENTER) : place.setHorizontalAlignement(BorderViewConstraints.EAST);
                                                   break;
                case(BorderViewConstraints.EAST)   : place.inhibitHorizontalAlignement();
                                                   break;
                case(BorderViewConstraints.WEST)   : place.inhibitHorizontalAlignement();
                                                   break;
            }
        }
        else
        {   switch(place.getVerticalAlignement())
            {
                case(BorderViewConstraints.NONE)   : break;
                case(BorderViewConstraints.CENTER) : place.setVerticalAlignement(BorderViewConstraints.SOUTH);
                                                   break;
                case(BorderViewConstraints.NORTH)  : place.inhibitVerticalAlignement();
                                                   break;
                case(BorderViewConstraints.SOUTH)  : place.inhibitVerticalAlignement();
                                                   break;
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of reduceConstraints() modify place : " + place);
	}
    }
    
    /** return true if according to constraints the current view must be added in the first window of a tabWindow
     *  @param horizontal true if the current splitwindow is horizontal
     *  @param place a Border constraints
     *  @return true if according to constraints the current view must be added in the first window of a tabWindow
     */
    protected boolean shouldBeFirstWindow(boolean horizontal, BorderViewConstraints placement)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling shouldBeFirstWindow(horizontal?" + horizontal + ", place=" + placement + ")");
	}
	boolean considerFirst = true;
        if ( horizontal )
        {   switch(placement.getHorizontalAlignement())
            {
                case(BorderViewConstraints.NONE)   : break;
                case(BorderViewConstraints.CENTER) : break;
                case(BorderViewConstraints.EAST)   : considerFirst = false;
                                                     break;
                case(BorderViewConstraints.WEST)   : break;
            }
        }
        else
        {   switch(placement.getVerticalAlignement())
            {
                case(BorderViewConstraints.NONE)   : break;
                case(BorderViewConstraints.CENTER) : break;
                case(BorderViewConstraints.NORTH)  : break;
                case(BorderViewConstraints.SOUTH)  : considerFirst = false;
                                                     break;
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of shouldBeFirstWindow(horizontal?" + horizontal + ", place=" + placement + ") returns " + considerFirst);
	}
        return considerFirst;
    }
    
    protected void addToWindow(DockingWindow window, View view, Object object, ViewConstraints placement)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling addToWindow(window=" + window + "(" + window.getClass().getSimpleName() + ")"
			 + ", view=" + view + ", object=" + object + ", placement=" + placement + ")");
	}
        //System.err.println("addToWindow(" + window.getClass() + ", with constraints : " + placement + ")");
        
        if ( window != null && placement instanceof BorderViewConstraints )
        {   BorderViewConstraints place = (BorderViewConstraints)placement;
            
            if ( window instanceof SplitWindow )
            {   
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("trying to add view " + view + " on a SplitWindow with placement : " + place);
		}
		
		SplitWindow split = (SplitWindow)window;
                
                if ( place.getHorizontalAlignement() == BorderViewConstraints.NONE &&
                     place.getVerticalAlignement() == BorderViewConstraints.NONE )
                {   /* find the first DockingWindow */
                    this.addToWindow(split.getLeftWindow(), view, object, placement);
                }
                else
                {
                    boolean compatible = true;
                    if ( split.isHorizontal() && place.getHorizontalAlignement() == BorderViewConstraints.NONE )
                        compatible = false;
                    else if ( ! split.isHorizontal() && place.getVerticalAlignement() == BorderViewConstraints.NONE )
                        compatible = false;

                    if ( compatible )
                    {
                        boolean considerFirst = this.shouldBeFirstWindow(split.isHorizontal(), place);
                        boolean horizontal = split.isHorizontal();

                        this.reduceConstraints(horizontal, place);

                        this.addToWindow(considerFirst ? split.getLeftWindow() : split.getRightWindow(),
                                         view, object, place);
                    }
                    else
                    {   /* create a new SplitPane to respect constraints */
                        boolean horizontal = place.getVerticalAlignement() == BorderViewConstraints.NONE;
                        SplitWindow subSplit = null;//new SplitWindow(horizontal);

                        boolean considerFirst = this.shouldBeFirstWindow(horizontal, place);

                        DockingWindow parentWindow = window.getWindowParent();
                        
                        subSplit = DockingWindowUtilities.insertSplitWindow(parentWindow, horizontal,
                                                                            window, ! considerFirst);

                        this.addToWindow(subSplit, view, object, placement);
                    }
                }
            }
            else if ( window instanceof TabWindow )
            {   
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("trying to add view " + view + " on a TabWindow with placement : " + place);
		}
		
		if ( place.getHorizontalAlignement() == BorderViewConstraints.NONE &&
                     place.getVerticalAlignement() == BorderViewConstraints.NONE )
                {   
//                    if ( window instanceof DockingTabbedPane )
//                    {   DockingTabbedPane dtp = (DockingTabbedPane)window;
//                        
//                        dtp.addTab(view, false);
//                    }
//                    else
                    {   ((TabWindow)window).addTab(view); }
                    
                    return;
                }
                else
                {
		    if ( //(place.getHorizontalAlignement() == BorderViewConstraints.NONE ||
			 //place.getVerticalAlignement() == BorderViewConstraints.NONE) && 
			 ((TabWindow)window).getChildWindowCount() == 0 )
		    {
                        ((TabWindow)window).addTab(view);
		    }
		    else
		    {
			/* must create a new SplitWindow and add it to the current DockingWindow */
			boolean horizontal = place.getVerticalAlignement() == BorderViewConstraints.NONE;
			SplitWindow subSplit = null;//new SplitWindow(horizontal);
			boolean considerFirst = this.shouldBeFirstWindow(horizontal, place);

			DockingWindow parentWindow = window.getWindowParent();

			subSplit = DockingWindowUtilities.insertSplitWindow(parentWindow, horizontal,
									    window, ! considerFirst);

			this.addToWindow(subSplit, view, object, placement);
			
			this.recomputeDividerLocation(subSplit);
		    }
                }
            }
            // PENDING : a tester, si algo ok, merger la gestion du TabWindow avec celle de la View
            else if ( window instanceof View )
            {   
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("trying to add view " + view + " on a View");
		}
		if ( place.getHorizontalAlignement() == BorderViewConstraints.NONE &&
                     place.getVerticalAlignement() == BorderViewConstraints.NONE )
                {   /* insert a TabWindow */
                    TabWindow tabbed = DockingWindowUtilities.insertTabWindow( (View)window );
                    this.addToWindow(tabbed, view, object, placement);
                }
                else
                {   /* must create a new SplitWindow and add it to the current DockingWindow */
                    boolean horizontal = place.getVerticalAlignement() == BorderViewConstraints.NONE;
                    SplitWindow subSplit = null;//new SplitWindow(horizontal);
                    
                    boolean considerFirst = this.shouldBeFirstWindow(horizontal, place);
                    
                    DockingWindow parentWindow = window.getWindowParent();
                    
                    subSplit = DockingWindowUtilities.insertSplitWindow(parentWindow, horizontal,
                                                                        window, ! considerFirst);
                    
                    this.addToWindow(subSplit, view, object, placement);
		    
		    this.recomputeDividerLocation(subSplit);
                }
            }
            else
	    {
		logger.error("trying to add view " + view + " on a '" + window.getClass() + "'");
                throw new RuntimeException("unable to consider " + window.getClass());
	    }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of addToWindow(window=" + window + ", view=" + view + ", object=" + object + ", placement=" + placement + ")");
	}
    }
    
    /** resize divider location according to preferred size of its internal windows
     *	@param split a SplitWindow
     */
    protected void recomputeDividerLocation(final SplitWindow split)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling recomputeDividerLocation(" + split + ")");
	}
	
	if ( split != null )
	{
	    Float location = null;
		
	    if ( split.isHorizontal() )
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("split is horizontal");
		}
		DockingWindow rightWindow = split.getRightWindow();
		DockingWindow leftWindow  = split.getLeftWindow();
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("right window : " + rightWindow);
		    logger.debug("left window : " + leftWindow);
		}
		
		if ( rightWindow != null && leftWindow != null &&
		     rightWindow.getPreferredSize() != null && leftWindow.getPreferredSize() != null )
		{
		    
		    float ratio = ((float)leftWindow.getPreferredSize().width) /
				    (rightWindow.getPreferredSize().width + leftWindow.getPreferredSize().width);
		    
		    if( logger.isDebugEnabled() )
		    {
			logger.debug("right window preferred width : " + rightWindow.getPreferredSize().width);
			logger.debug("left window preferred width : " + leftWindow.getPreferredSize().width);
			logger.debug("right window width : " + rightWindow.getSize().width);
			logger.debug("left window width : " + leftWindow.getSize().width);
			logger.debug("ratio is : " + ratio);
			logger.debug("split size : " + split.getSize());
			logger.debug("split preferred size : " + split.getPreferredSize());
		    }
		    
		    if ( split.getSize().width > 0 )
		    {
			location = ratio;//split.getSize().width * ratio;
		    }
		    else
		    {
			location = ratio;//split.getPreferredSize().width * ratio;
		    }
		}
		else
		{
		    logger.info("could not compute divider location");
		}
	    }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("split is vertical");
		}
		DockingWindow rightWindow = split.getRightWindow();
		DockingWindow leftWindow  = split.getLeftWindow();
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("right window : " + rightWindow);
		    logger.debug("left window : " + leftWindow);
		}
		
		if ( rightWindow != null && leftWindow != null &&
		     rightWindow.getPreferredSize() != null && leftWindow.getPreferredSize() != null )
		{
		    float ratio = ((float)leftWindow.getPreferredSize().height) /
				    (rightWindow.getPreferredSize().height + leftWindow.getPreferredSize().height);
		    
		    if( logger.isDebugEnabled() )
		    {
			logger.debug("right window preferred height : " + rightWindow.getPreferredSize().height);
			logger.debug("left window preferred height : " + leftWindow.getPreferredSize().height);
			logger.debug("right window height : " + rightWindow.getSize().height);
			logger.debug("left window height : " + leftWindow.getSize().height);
			logger.debug("ratio is : " + ratio);
			logger.debug("split size : " + split.getSize());
			logger.debug("split preferred size : " + split.getPreferredSize());
		    }
		    
		    if ( split.getSize().height > 0 )
		    {
			location = ratio;//split.getSize().height * ratio;
		    }
		    else
		    {
			location = ratio;//split.getPreferredSize().height * ratio;
		    }
		}
		else
		{
		    logger.info("could not compute divider location");
		}
	    }
	    
	    if( logger.isDebugEnabled() )
	    {
		logger.debug("divider location : " + location);
	    }
	    
	    if ( location == null )
	    {
		logger.info("could not compute a best divider location for split " + split);
	    }
	    else
	    {
		final float locationValue = location.floatValue();
		
		Runnable runnable = new Runnable()
		{
		    public void run()
		    {
			split.setDividerLocation(locationValue);
		    }
		};
		
		if ( SwingUtilities.isEventDispatchThread() )
		{
		    runnable.run();
		}
		else
		{
		    SwingUtilities.invokeLater(runnable);
		}
		
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of recomputeDividerLocation(" + split + ")");
	}
    }
}
