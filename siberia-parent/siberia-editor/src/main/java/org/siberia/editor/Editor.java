/* 
 * Siberia editor : siberia plugin defining editor framework
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
/*
 * Editor.java
 *
 * Created on 23 mars 2006, 22:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.siberia.editor;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import org.siberia.editor.event.EditorClosureListener;
import org.siberia.editor.event.EditorFocusListener;
import org.siberia.editor.exception.ClosingRefusedException;
import org.siberia.type.SibType;

/**
 *
 * @author alexis
 */
public interface Editor
{
    /** property names */
    
    /** name of the property instance */
    public static final String PROPERTY_INSTANCE                       = "instance";
    
    /** name of the property icon */
    public static final String PROPERTY_ICON                           = "icon";
    
    /** name of the property icon */
    public static final String PROPERTY_TITLE                          = "title";
    
    /** name of the property icon */
    public static final String PROPERTY_MODIFIED                       = "modified";
    
    /** name of the property closable */
    public static final String PROPERTY_CLOSABLE                       = "closable";
    
    /** name of the property maximize */
    public static final String PROPERTY_MAXIMIZE                       = "maximizable";
    
    /** name of the property minimize */
    public static final String PROPERTY_MINIMIZE                       = "minimizable";
    
    /** name of the property 'focus on editor when launched' */
    public static final String PROPERTY_FOCUS_ON_EDITOR_WHEN_LAUNCHED  = "gainFocusWhenLaunched";
    
    /** name of the property 'focus on editor when reedition'
     *
     *	re-edition comes when an object is already edited by a given editor
     *	and an action provoke the item to be re-edited, in some case, the editor could want to grab focus 
     *	sometimes not
     */
    public static final String PROPERTY_FOCUS_ON_EDITOR_WHEN_REEDITION = "gainFocusWhenReedition";
    
    /** return the instance edited
     *  @return an instance of SibType
     */
    public SibType getInstance();
    
    /** set the SibType instance associated with the editor
     *  @param instance instance of SibType
     **/
    public void setInstance(SibType instance);
    
    /** update the title
     *	this method has a parameter which represents the default title to apply but
     *	all editor are free to refuse this value if it consider that already has a valid title
     */
    public void updateTitle(String defaultTitle);
    
    /** initialize the title of the editor
     *  @param title the title to apply to the editor
     */
    public void setTitle(String title);
    
    /** return the title of the editor
     *  @return the title to apply to the editor
     */
    public String getTitle();
    
    /** indicate that the editor feels modified
     *  @param modified true to indicate that the editor feels modified
     */
    public void setModified(boolean modified);
    
    /** return true if the editor is modified
     *  @return true if the editor is modified
     */
    public boolean isModified();
    
    /** return the component that render the editor
     *  @return a Component
     */
    public Component getComponent();
    
    /** set the icon related to this editor
     *  @param icon an Icon
     */
    public void setIcon(Icon icon);
    
    /** erturn the icon related to this editor
     *  @return an Icon or null if editor is not associated with an icon
     */
    public Icon getIcon();
    
    /** indicate if the editor is closable
     *  @return true if the editor is closable
     */
    public boolean isClosable();
    
    /** indicate if the editor is closable
     *  @param closable true if the editor is closable
     */
    public void setClosable(boolean closable);
    
    /** indicate if the editor is maximizable
     *  @return true if the editor is maximizable
     */
    public boolean isMaximizable();
    
    /** indicate if the editor is maximizable
     *  @param maximizable true if the editor is maximizable
     */
    public void setMaximizable(boolean maximizable);
    
    /** indicate if the editor is minimizable
     *  @return true if the editor is minimizable
     */
    public boolean isMinimizable();
    
    /** indicate if the editor is minimizable
     *  @param minimizable true if the editor is minimizable
     */
    public void setMinimizable(boolean minimizable);
    
    /** indicate if the editor should gain focus when launched
     *	@param gainFocus true to force the editor to gain focus when launched
     */
    public void setGainFocusWhenLaunched(boolean gainFocus);
    
    /** indicate if the editor should gain focus when launched
     *	@return true to force the editor to gain focus when launched
     */
    public boolean getGainFocusWhenLaunched();
    
    /** indicate if the editor should gain focus when reedition
     *	@param gainFocus true to force the editor to gain focus when reedition
     */
    public void setGainFocusWhenReEdited(boolean gainFocus);
    
    /** indicate if the editor should gain focus when reedition
     *	@return true to force the editor to gain focus when reedition
     */
    public boolean getGainFocusWhenReEdited();
    
    /** ########################################################################
     *  ################### validation, cancel and load ########################
     *  ######################################################################## */
    
    /** method to update the caracteristics of the associated entity according
     *  to the value of the graphical components in the editor
     *
     **/
    public void save();
    
    /** method that indicate that the modifications that occurs in this editor
     *  should no longer exist
     */
    public void cancel();
    
    /** method to update the graphical components in the editor
     *  according to the state of the relative associated entity
     */
    public void load();
    
    /** called when the system determine that the editor should ask user if he wants to save current modifications
     *  @param component the invoker
     *  @return an integer representing the choice of the user : 
     *      <ul>
     *          <li>JOptionPane.YES_OPTION</li>
     *          <li>JOptionPane.NO_OPTION</li>
     *          <li>JOptionPane.CLOSED_OPTION</li>
     *      </ul>
     */
    public int askUserForSaving(Component component);
    
    /** ########################################################################
     *  ################# display and hide related methods #####################
     *  ######################################################################## */
    
    /** method called when the editor is about to be displayed */
    public void aboutToBeDisplayed();
    
    /** method called to check if listeners accept the editor to be closed
     *  @exception ClosingRefusedException if a listener refused the editor to be closed
     */
    public void checkCloseAllowed() throws ClosingRefusedException;
    
    /** method called when the editor is about to be closed */
    public void aboutToBeClosed();
    
    /** method called when closing this editor is refused
     *  override this method to perform actions on editor but not to send explanation messages
     */
    public void closingRefused();
    
    /** method called to close the editor by itself */
    public void close();
    
    /** ########################################################################
     *  ############### ClosingEditorRequestListener methods ###################
     *  ######################################################################## */
    
    /** add a new EditorClosureListener
     *  @param listener an instance of EditorClosureListener
     */
    public void addEditorClosureListener(EditorClosureListener listener);
    
    /** remove a new EditorClosureListener
     *  @param listener an instance of EditorClosureListener
     */
    public void removeEditorClosureListener(EditorClosureListener listener);
    
    /** ########################################################################
     *  ################### EditorFocusListener methods ########################
     *  ######################################################################## */
    
    /** add a new EditorFocusListener
     *  @param listener an instance of EditorClosureListener
     */
    public void addEditorFocusListener(EditorFocusListener listener);
    
    /** remove a EditorFocusListener
     *  @param listener an instance of EditorFocusListener
     */
    public void removeEditorFocusListener(EditorFocusListener listener);
    
    /** methods called to warn EditorFocusListener that the current editor gained focus
     *  @param oppositeEditor the editor that lost focus
     */
    public void fireEditorFocusGained(Editor oppositeEditor);
    
    /** methods called to warn EditorFocusListener that the current editor lost focus
     *  @param oppositeEditor the editor that gains focus
     */
    public void fireEditorFocusLost(Editor oppositeEditor);
    
    /** ########################################################################
     *  ##################### life management methods ##########################
     *  ######################################################################## */
    
    /** method that is called when an editor is not used anymore */
    public void dispose();
    
    /** method that is called when an editor is put in registry
     *  waiting to be used later if possible
     */
    public void putInCache();
    
    /** method that is called when an editor is being removed from registry to be used again */
    public void removedFromCache();
    
    /** ########################################################################
     *  ################## PropertyChangeListener methods ######################
     *  ######################################################################## */

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * The same listener object may be added more than once, and will be called
     * as many times as it is added.
     * If <code>listener</code> is null, no exception is thrown and no action
     * is taken.
     *
     * @param listener  The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     * If <code>listener</code> was added more than once to the same event
     * source, it will be notified one less time after being removed.
     * If <code>listener</code> is null, or was never added, no exception is
     * thrown and no action is taken.
     *
     * @param listener  The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Add a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property.
     * The same listener object may be added more than once.  For each
     * property,  the listener will be invoked the number of times it was added
     * for that property.
     * If <code>propertyName</code> or <code>listener</code> is null, no
     * exception is thrown and no action is taken.
     *
     * @param propertyName  The name of the property to listen on.
     * @param listener  The PropertyChangeListener to be added
     */

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener for a specific property.
     * If <code>listener</code> was added more than once to the same event
     * source for the specified property, it will be notified one less time
     * after being removed.
     * If <code>propertyName</code> is null,  no exception is thrown and no
     * action is taken.
     * If <code>listener</code> is null, or was never added for the specified
     * property, no exception is thrown and no action is taken.
     *
     * @param propertyName  The name of the property that was listened on.
     * @param listener  The PropertyChangeListener to be removed
     */

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
    
}
