/* 
 * Siberia components : siberia plugin for graphical components
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
package org.siberia.ui.action;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.siberia.GraphicalResources;

/**
 *
 * @author alexis
 */
public abstract class GenericAction extends AbstractAction
{   
    /** logger */
    private Logger logger = Logger.getLogger(GenericAction.class);
    
    /** Creates a new instance of Action */
    public GenericAction()
    {   super(); }
    
    /** Creates a new instance of Action
     *  @param name the name of the action
     */
    public GenericAction(String name)
    {   super(name); }
    
    /** Creates a new instance of Action
     *  @param name the name of the action
     *  @param icon an icon
     */
    public GenericAction(String name, Icon icon)
    {   super(name, icon); }
    
    /** Creates a new instance of Action
     *  @param name the name of the action
     *  @param icon an icon
     *  @param desc a description
     */
    public GenericAction(String name, Icon icon, String desc)
    {   this(name, icon);
        this.putValue(Action.SHORT_DESCRIPTION, desc);
    }
    
    /** set the icon to use
     *  @param icon an icon
     */
    public void setIcon(Icon icon)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setIcon(Icon)");
	    logger.debug("calling setIcon(Icon) with " + icon);
	}
	this.putValue(Action.SMALL_ICON, icon);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setIcon(Icon)");
	}
    }
    
    /** set the label
     *  @param label a String
     */
    public void setLabel(String label)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setLabel(String)");
	    logger.debug("calling setLabel(String) with " + label);
	}
	this.putValue(Action.NAME, label);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setLabel(String)");
	}
    }
    
    /** static method that returns a component according to an ActionEvent
     *	@param e an ActionEvent
     *	@return a Component
     */
    protected static Component getComponent(ActionEvent e)
    {
	Component c = null;
	
	if ( e != null )
	{
	    Object source = e.getSource();
	    
	    if ( source instanceof Component )
	    {
		c = (Component)source;
	    }
	    
	    if ( c == null )
	    {
		c = GraphicalResources.getInstance().getMainFrame();
	    }
	}
	
	return c;
    }
    
    /** static method that returns the window ancestor according to an ActionEvent
     *	@param e an ActionEvent
     *	@return a Window
     */
    protected static Window getWindow(ActionEvent e)
    {
	Window window = null;
	
	Component c = getComponent(e);
	
	if ( c != null )
	{
	    window = SwingUtilities.getWindowAncestor(c);
	}
	
	if ( window == null )
	{
	    window = GraphicalResources.getInstance().getMainFrame();
	}
	
	return window;
    }
    
    /** static method that returns the frame ancestor according to an ActionEvent
     *	@param e an ActionEvent
     *	@return a Frame
     */
    protected static Frame getFrame(ActionEvent e)
    {
	Frame frame = null;
	
	Component c = getComponent(e);
	
	if ( c != null )
	{
	    Window window = SwingUtilities.getWindowAncestor(c);
	    
	    if ( window instanceof Frame )
	    {
		frame = (Frame)window;
	    }
	}
	
	if ( frame == null )
	{
	    frame = GraphicalResources.getInstance().getMainFrame();
	}
	
	return frame;
    }
    
    /** static method that returns the dialog ancestor according to an ActionEvent
     *	@param e an ActionEvent
     *	@return a Dialog
     */
    protected static Dialog getDialog(ActionEvent e)
    {
	Dialog dialog = null;
	
	Component c = getComponent(e);
	
	if ( c != null )
	{
	    Window window = SwingUtilities.getWindowAncestor(c);
	    
	    if ( window instanceof Dialog )
	    {
		dialog = (Dialog)window;
	    }
	}
	
	return dialog;
    }
    
        
}
