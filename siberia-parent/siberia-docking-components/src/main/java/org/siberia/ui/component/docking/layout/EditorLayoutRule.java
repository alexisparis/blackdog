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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.infonode.docking.View;
import org.apache.log4j.Logger;
import org.siberia.ui.component.docking.view.EditorView;

/**
 *
 * define a rule for an editor in an EditorViewLayout
 *
 * @author alexis
 */
public class EditorLayoutRule
{
    /** logger */
    private transient Logger     logger        = Logger.getLogger(EditorLayoutRule.class);
    
    /** set containing friend 'editor' classes */
    private	      Set<Class> friendClasses = null;

    /** set containing enemy 'editor' classes */
    private	      Set<Class> enemyClasses  = null;

    /** tells if the rule is strict
     *  if the rule is not strict, then if no element are used from the two set to determine
     *  if it is valid, the isValid(..) will return true, otherwise, it will return false
     */
    private boolean    strict        = false;

    /** create a new non strict EditorLayoutRule */
    public EditorLayoutRule()
    {   this(false); }

    /** create a new EditorLayoutRule
     *  @param strict true if the rule is strict :<br>
     *      if the rule is not strict, then if no element are used from the two set to determine
     *      if it is valid, the isValid(..) will return true, otherwise, it will return false
     */
    public EditorLayoutRule(boolean strict)
    {   this.strict = strict; }

    /** add a new friend Class
     *  @param c a Class
     */
    public void addFriendClass(Class c)
    {   if ( c != null )
	{   if ( this.friendClasses == null )
		this.friendClasses = new HashSet<Class>();
	    this.friendClasses.add(c);
	}   
    }

    /** add a new enemy Class
     *  @param c a Class
     */
    public void addEnemyClass(Class c)
    {   if ( c != null )
	{   if ( this.enemyClasses == null )
		this.enemyClasses = new HashSet<Class>();
	    this.enemyClasses.add(c);
	}   
    }

    public String toString()
    {
	StringBuffer buffer = new StringBuffer();
	
	buffer.append(this.getClass().getName() + " strict ? " + this.strict);
	
	if ( this.friendClasses != null )
	{
	    buffer.append(" friendly classes={");
	    Iterator<Class> classes = this.friendClasses.iterator();
	    while(classes.hasNext())
	    {
		Class current = classes.next();
		
		buffer.append(current);
		
		if ( classes.hasNext() )
		{
		    buffer.append(", ");
		}
	    }
	    buffer.append("}");
	}
	if ( this.enemyClasses != null )
	{
	    buffer.append(" enemy classes={");
	    Iterator<Class> classes = this.enemyClasses.iterator();
	    while(classes.hasNext())
	    {
		Class current = classes.next();
		
		buffer.append(current);
		
		if ( classes.hasNext() )
		{
		    buffer.append(", ");
		}
	    }
	    buffer.append("}");
	}
	
	return buffer.toString();
    }

    /** return true if the rule is valid for a given DockingWindow
     *  @param views an array of Views
     *  @return true if the rule is valid for a given DockingWindow
     */
    public boolean isValid(View... views)
    {   
	if ( logger.isDebugEnabled() )
	{
	    StringBuffer buffer = new StringBuffer();
	    
	    if ( views != null )
	    {
		for(int i = 0; i < views.length; i++)
		{
		    View currentView = views[i];
		    
		    buffer.append( (currentView == null ? null : currentView.getTitle()) );
		    
		    if ( i < views.length - 1 )
		    {
			buffer.append(", ");
		    }
		}
	    }
	    
	    if ( buffer.length() > 0 )
	    {
		buffer.insert(0, "[");
		buffer.append("]");
	    }
	    
	    logger.debug("calling is valid on " + this.toString() + " with views " + buffer.toString());
	}
	
	boolean convey = false;

	boolean enemyFound  = false;
	boolean friendFound = false;

	if ( views != null )
	{   for(int i = 0; i < views.length; i++)
	    {   View v = views[i];
		if ( v instanceof EditorView )
		{   EditorView edView = (EditorView)v;
		    if ( edView.getEditor() != null )
		    {   Class editorClass = edView.getEditor().getClass();
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("considering editor : " + edView.getEditor().getClass().getName() + " with title " + edView.getTitle());
			}
			
			if ( this.friendClasses != null )
			{   Iterator<Class> friendIterator = this.friendClasses.iterator();

			    while(friendIterator.hasNext())
			    {   Class c = friendIterator.next();

				if ( c.isAssignableFrom(editorClass) )
				{   
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("found a friend which is of kind " + c);
				    }
				    friendFound = true;
				    break;
				}
			    }
			}
			
			if ( ! friendFound )
			{
			    if ( this.enemyClasses != null )
			    {   Iterator<Class> enemyIterator = this.enemyClasses.iterator();

				while(enemyIterator.hasNext())
				{   Class c = enemyIterator.next();

				    if ( c.isAssignableFrom(editorClass) )
				    {   
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("found an enemy which is of kind " + c);
					}
					enemyFound = true;
					break;
				    }
				}
			    }
			}

			if ( logger.isDebugEnabled() )
			{
			    logger.debug("friend found ? " + friendFound);
			}
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("enemy found ? " + enemyFound);
			}
			if ( friendFound || enemyFound )
			    break;
		    }
		}
	    }

	    if ( enemyFound )
	    {	convey = false; }
	    else
	    {   convey = (! this.strict) || friendFound; }
	}
	else
	{   convey = ! this.strict; }
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug(this.toString() + " returns validity : " + convey);
	}

	return convey;
    }
    
}
