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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import org.apache.log4j.Logger;
import org.siberia.ui.component.docking.DockingWindowUtilities;
import org.siberia.ui.component.docking.view.EditorView;
import org.siberia.utilities.util.ClassMap;

/**
 *
 * Entity that place view in this way : 
 *
 * - 
 *
 * @author alexis
 */
public class EditorViewLayout extends BorderViewLayout
{
    /* logger */
    private transient Logger logger = Logger.getLogger(EditorViewLayout.class);
    
    /** defines rule to place an editor
     *  the class is an Editor class
     */
    private ClassMap<EditorLayoutRule> rules = null;
    
    /** Creates a new instance of EditorViewLayout */
    public EditorViewLayout()
    {   super(); }
    
    /** add a rule
     *  @param c the Class related to Editor concerned by the rule
     *  @param rule an EditorLayoutRule
     */
    public void setRule(Class c, EditorLayoutRule rule)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling setRule(" + c + ", " + rule + ")");
	}
	if ( c != null && rule != null )
        {   if ( this.rules == null )
                this.rules = new ClassMap<EditorLayoutRule>();
            this.rules.put(c, rule);
        }
	else if ( c == null )
	{
	    logger.warn("could not add a rule for a null class");
	}
	else if ( rule == null )
	{
	    logger.warn("could not add a null rule for class " + c);
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of setRule(" + c + ", " + rule + ")");
	}
    }
    
    /** return a preferred constraints for a given object
     *  @param object an Object that is contained in defaultPlacements
     *  @return a ViewConstraints
     */
    protected ViewConstraints getDefaultConstraintsFor(Object object)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getDefaultConstraintsFor(" + object + ")");
	}
	/* this.defaultPlacements is containing class as key, use inheritance and interfaces set to find the
         * most preferred Constraints
         */
        ViewConstraints placement = null;
        
        Object o = ClassMap.get(this.defaultPlacements, object);
        if ( o instanceof ViewConstraints )
            placement = (ViewConstraints)o;
        
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getDefaultConstraintsFor(" + object + ") returns " + placement);
	}
        return placement;
    }
    
    /** remove a rule
     *  @param c the Class related to Editor
     */
    public void removeRule(Class c)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling removeRule(" + c + ")");
	}
	if ( this.rules != null )
            this.rules.remove(c);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of removeRule(" + c + ")");
	}
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
	    logger.debug("calling doLayoutImpl(rootwindow=" + panel + ", view=" + view +
			    ", object=" + object + ", placement=" + placement + ")");
	}
	/* traverse the window tree and search for a location that is
         * valid according to rules
         */
        if ( view instanceof EditorView && this.rules != null && panel != null )
        {   /* if we find a rule for the kind of editor, let's traverse the window tree to
             * search for a component which will accept the editor
             */
            EditorLayoutRule rule = null;
            
            if ( ((EditorView)view).getEditor() != null )
            {   
		if ( logger.isDebugEnabled() )
		{
		    Iterator<Map.Entry<Class, EditorLayoutRule>> entries = this.rules.entrySet().iterator();
		    
		    logger.debug("displaying the map rules content : ");
		    
		    while(entries.hasNext())
		    {
			Map.Entry<Class, EditorLayoutRule> currentEntry = entries.next();
			
			logger.debug("\t" + currentEntry.getKey() + " --> " + currentEntry.getValue());
		    }
		}
		
		rule = this.rules.search( ((EditorView)view).getEditor().getClass() );
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("recovering rule " + rule + " for editor " + ((EditorView)view).getEditor().getClass());
		}
	    }
            
            if ( rule != null )
            {   /* traverse the window tree and search for a component respecting the rule */
                if ( this.tryToAddView(view, panel, rule) )
		{
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("tryToAddView has succeeded");
		    }
		    return;
		}
		else
		{
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("tryToAddView failed --> continue");
		    }
		}
            }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("could not try to add view using a null rule --> continue");
		}
	    }
        }
	else
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("asking super class to do doLayoutImpl");
	    }
	}
        
        /* if the view has not been added, then use the layout border */
        super.doLayoutImpl(panel, view, object, placement);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of doLayoutImpl(rootwindow=" + panel + ", view=" + view +
			    ", object=" + object + ", placement=" + placement + ")");
	}
    }
    
    /** method that try to add a View on a given DockingWindow
     *  @param view a view
     *  @param window a DockingWindow
     *  @param rule EditorLayoutRule
     *  @return true if the view was added
     */
    private boolean tryToAddView(View view, DockingWindow window, EditorLayoutRule rule)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling tryToAddView(view=" + view + ", window=" + window + ", rule=" + rule + ")");
	}
	
	boolean succeed = false;
        if ( window != null && view != null && rule != null )
        {   
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("window child count is " + window.getChildWindowCount());
	    }
	    if ( window.getChildWindowCount() > 0 )
            {   
                for(int i = 0; i < window.getChildWindowCount(); i++)
                {   
		    DockingWindow child = window.getChildWindow(i);
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("considering child docking window '" + child + "'");
		    }

                    if ( child instanceof TabWindow )
                    {   /* create a List of View and test if the rule is valid */
                        TabWindow tabbed = (TabWindow)child;

			if ( logger.isDebugEnabled() )
			{
			    logger.debug("collecting the view of this TabWindow...");
			}
                        List<View> views = null;
                        for(int j = 0; j < tabbed.getChildWindowCount(); j++)
                        {   /* build the list of view */
                            DockingWindow tabPart = tabbed.getChildWindow(j);

                            if ( tabPart instanceof View )
                            {   if ( views == null )
                                    views = new ArrayList<View>();
                                views.add( (View)tabPart );
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("adding " + tabPart + " as views collection");
				}
                            }
                        }

			boolean ruleValidation = rule.isValid( views == null ? null : (View[])views.toArray(new View[]{}));
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("rule " + rule + " okay ? " + ruleValidation);
			}
                        if ( ruleValidation )
                        {   /* add the view to the tabbedPane and return */
                            tabbed.addTab(view);
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("adding view " + view + " to Tabwindow " + tabbed);
			    }
                            return true;
                        }
			

                        /* we have to check one DockingWindow inside the TabWindow that are not View */
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("trying to add view on all children of TabWindow that are not views");
			}
                        for(int j = 0; j < tabbed.getChildWindowCount(); j++)
                        {   /* build the list of view */
                            DockingWindow tabPart = tabbed.getChildWindow(j);

                            if ( ! (tabPart instanceof View) )
                            {   
				boolean validated = this.tryToAddView(view, tabbed.getChildWindow(j), rule);
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("rule " + rule + " okay ? " + validated);
				}
				if ( validated )
				{
                                    return true;
				}
                            }
                        }
                    }
                    else if ( child instanceof SplitWindow )
                    {   SplitWindow split = (SplitWindow)child;

    //                    if ( this.tryToAddView(view, split.getLeftWindow(), rule) )
    //                        return true;
    //                    
    //                    if ( this.tryToAddView(view, split.getRightWindow(), rule) )
    //                        return true;

                        if ( this.tryToAddView(view, split, rule) )
                            return true;
                    }
                    else if ( child instanceof View )
                    {   /* create a new TabWindow between the parent and the view and try to
                         * add our view in the new TabWindow
                         */
			boolean validated = rule.isValid( (View)child );
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("rule " + rule + " okay ? " + validated);
			}
                        if ( validated )
                        {   TabWindow tabbed = DockingWindowUtilities.insertTabWindow( (View)child );
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("inserting TabWindow between window and view " + child);
			    }
                            if ( tabbed != null )
                            {   
                                new Exception().printStackTrace();
                                tabbed.addTab(view);
                                return true;
                            }
                        }
                    }
                }
            }
            else
            {   if ( window instanceof TabWindow )
                {   ((TabWindow)window).addTab(view);
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("view " + view + " added to TabWindow " + window);
		    }
                    succeed = true;
                }
                else if ( window instanceof SplitWindow )
                {   
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("try to add view " + view + " on the left window of a SplitWindow");
		    }
		    if ( this.tryToAddView(view, ((SplitWindow)window).getLeftWindow(), rule) )
                        succeed = true;
                    if ( ! succeed )
                    {   
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("try to add view " + view + " on the right window of a SplitWindow");
			}
			if ( this.tryToAddView(view, ((SplitWindow)window).getRightWindow(), rule) )
                            succeed = true;
                    }
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of tryToAddView(view=" + view + ", window=" + window + ", rule=" + rule + ") returns " + succeed);
	}
        return succeed;
    }
}
