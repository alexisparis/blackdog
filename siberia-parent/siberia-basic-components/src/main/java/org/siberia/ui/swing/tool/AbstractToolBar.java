/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.tool;

import java.awt.Component;
import java.awt.Insets;
import java.util.*;
import javax.swing.JToggleButton;

import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;

import javax.swing.AbstractButton;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.Action;
import java.awt.event.*;


/**
 * base class when creating a toolbar (abstract)
 * Action are not used for the moment
 *
 * @author alexis
 */
public abstract class AbstractToolBar extends JToolBar
{
    /** list of buttons contained by the toolbar **/
    protected List buttons;
    
    /** Creates a new instance of AbstractToolBar */
    public AbstractToolBar(boolean canFloat)
    {   super();
        this.buttons = new ArrayList();
        this.setFloatable(canFloat);
        this.setBorderPainted(false);
        this.setMargin(new Insets(1, 1, 1, 1));
    }
    
    /** create a button with an icon corresponding with the name
     *  @param name the name of the file icon that the button will render
     *  @param tooltip tooltip to be associated with the button
     **/
    protected JButton createButton(String name, String tooltip)
    {   JButton button = new JButton();
        button.setRequestFocusEnabled(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setToolTipText(tooltip);
        if ( name != null )
        {   try
            {   button.setIcon(ResourceLoader.getInstance().getIconNamed(name)); }
            catch (ResourceException e)
            {   e.printStackTrace(); }
        }
        return button;
    }
    
    /** create a button with an icon corresponding with the name
     *  @param name the name of the file icon that the button will render
     *  @param tooltip tooltip to be associated with the button
     *  @param action an action to be associated with the button
     **/
    protected JButton createButton(String name, String tooltip, Action action)
    {   JButton button = new JButton(action);
        button.setText("");
        button.setRequestFocusEnabled(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setToolTipText(tooltip);
        if ( name != null )
        {   try
            {   button.setIcon(ResourceLoader.getInstance().getIconNamed(name)); }
            catch (ResourceException e)
            {   e.printStackTrace(); }
        }
        return button;
    }
    
    /** create a button with an icon corresponding with the name
     *  @param name the name of the file icon that the button will render
     *  @param text a text to display
     *  @param tooltip tooltip to be associated with the button
     *  @param action an action to be associated with the button
     **/
    protected JButton createButton(String name, String text, String tooltip, Action action)
    {   JButton button = new JButton(action);
        button.setText(text);
        button.setRequestFocusEnabled(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setToolTipText(tooltip);
        if ( name != null )
        {   try
            {   button.setIcon(ResourceLoader.getInstance().getIconNamed(name)); }
            catch (ResourceException e)
            {   e.printStackTrace(); }
        }
        return button;
    }
    
    /** create a toggleButton with an icon corresponding with the name
     *  @param name the name of the file icon that the button will render
     *  @param tooltip tooltip to be associated with the button
     *  @param action an action to be associated with the button
     **/
    protected JToggleButton createToggleButton(String name, String tooltip, Action action)
    {   JToggleButton button = new JToggleButton(action);
        button.setText("");
        button.setRequestFocusEnabled(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setToolTipText(tooltip);
        if ( name != null )
        {   try
            {   button.setIcon(ResourceLoader.getInstance().getIconNamed(name)); }
            catch (ResourceException e)
            {   e.printStackTrace(); }
        }
        return button;
    }
    
    /** add a button to the toolbar. it is initialized with the given paramters
     *  @param action Action to set to the button !!! NOT USED YET !!!
     *  @param icon file name for the icon
     *  @return the new button
     **/
    public JButton add(Action action, String icon)
    {   return this.add(action, icon, null); }
    
    /** add a button to the toolbar. it is initialized with the given paramters
     *  @param action Action to set to the button !!! NOT USED YET !!!
     *  @param icon file name for the icon
     *  @param tooltip tooltip to set to the text
     *  @return the new button
     **/
    public JButton add(Action action, String icon, String tooltip)
    {   JButton button = new JButton();
        
        if ( icon != null)
        {   try
            {   button.setIcon(ResourceLoader.getInstance().getIconNamed(icon)); }
            catch (ResourceException e) { e.printStackTrace(); }
        }
        
        button.setAction(action);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setText("");
        this.add(button);
        this.buttons.add(button);
        return button;
    }
    
    /** add a button wit ha given Action
     *  @param action action asociated with a new button
     *  @deprecated
     **/
    public JButton add(Action action)
    {   JButton button;
        button = new JButton(action);
        button = super.add(action);
        button.setFocusPainted(false);
        this.buttons.add(button);

        return button;
    }
    
    /** add a listener for all buttons in the toolbar
     *  @param l actionListener that will listener actions on toolbar' buttons
     **/
    public void addButtonListener(ActionListener l)
    {   for (int i = 0; i < this.buttons.size(); i++)
            ((AbstractButton)this.buttons.get(i)).addActionListener(l);
    }
}
