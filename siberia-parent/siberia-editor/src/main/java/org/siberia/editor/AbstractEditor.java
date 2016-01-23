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
package org.siberia.editor;


import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.event.EventListenerList;
import org.apache.log4j.Logger;
import org.siberia.editor.event.EditorClosureListener;
import org.siberia.editor.event.EditorFocusListener;
import org.siberia.editor.exception.ClosingRefusedException;
import org.siberia.type.SibType;
import org.siberia.ui.IconCache;


/**
 * 
 * abstract class to build a generic editor
 *
 * @author alexis
 */
public abstract class AbstractEditor implements org.siberia.editor.Editor
{   
    /** logger */
    protected Logger                logger            = Logger.getLogger(AbstractEditor.class);
    
    /** PropertyChangeSupport */
    protected PropertyChangeSupport support           = new PropertyChangeSupport(this);
    
    /** the abstract associated instance **/
    protected SibType               instance          = null;
    
    /** icon � associer � l'�diteur */
    private Icon                    icon              = null;
    
    /* title of the editor */
    private String                  title             = null;
    
    /** is the editor modified ? */
    private boolean                 modified          = false;
    
    /** panel that render the editor */
    private JPanel                  panel             = null;
    
    /** list of listeners */
    private EventListenerList       listeners         = null;
    
    /** closable */
    private boolean                 closable          = true;
    
    /** maximizable */
    private boolean                 maximizable       = true;
    
    /** minimizable */
    private boolean                 minimizable       = true;
    
    /** get the focus when launched */
    private boolean                 gainFocusAtLaunch = true;
    
    /** get the focus when reedition occurs */
    private boolean                 gainFocusAtReedition = true;
    
    /** default constructor */
    public AbstractEditor()
    {   this(null); }
    
    /** default constructor 
     *  @param instance SibType instance associated with the editor
     **/
    public AbstractEditor(SibType instance)
    {   this.setInstance(instance); }
    
    /** teturn the panel that render the editor
     *  @return a JPanel
     */
    protected JPanel getPanel()
    {   if ( this.panel == null )
            this.panel = new JPanel();
        return this.panel;
    }
    
    /** set the icon related to this editor
     *  @param icon an Icon
     */
    public void setIcon(Icon icon)
    {   Icon old = this.icon;
        this.icon = icon;
        
        this.support.firePropertyChange(Editor.PROPERTY_ICON, old, icon);
    }
    
    /** erturn the icon related to this editor
     *  @return an Icon or null if editor is not associated with an icon
     */
    public Icon getIcon()
    {   return this.icon; }
    
    /** update the title
     *	this method has a parameter which represents the default title to apply but
     *	all editor are free to refuse this value if it consider that already has a valid title
     */
    public void updateTitle(String defaultTitle)
    {
	this.setTitle(defaultTitle);
    }
    
    /** initialize the title of the editor
     *  @param title the title to apply to the editor
     */
    public void setTitle(String title)
    {   String old = this.getTitle();
        this.title = title;
        this.support.firePropertyChange(PROPERTY_TITLE, old, this.getTitle());
    }
    
    /** return the title of the editor
     *  @return the title to apply to the editor
     */
    public String getTitle()
    {   return this.title; }
    
    /** indicate that the editor feels modified
     *  @param modified true to indicate that the editor feels modified
     */
    public void setModified(boolean modified)
    {   boolean old = this.isModified();
        this.modified = modified;
        this.support.firePropertyChange(PROPERTY_MODIFIED, old, this.isModified());
    }
    
    /** return true if the editor is modified
     *  @return true if the editor is modified
     */
    public boolean isModified()
    {   return this.modified; }
    
    /** return the SibType instance associated with the editor
     *  @return instance of SibType
     **/
    public SibType getInstance()
    {   return this.instance; }
    
    /** set the SibType instance associated with the editor
     *  @param instance instance of SibType
     **/
    public void setInstance(SibType instance)
    {   
	SibType old = this.instance;
	
        this.instance = instance;
	
        this.support.firePropertyChange(Editor.PROPERTY_INSTANCE, old, instance);
	
	/* initialize title */
	this.updateTitle( instance == null ? null : instance.getName());

	/* initialize icon */
	Icon img = null;
	
	if ( instance != null )
	{
	    img = IconCache.getInstance().get(instance);
	}

	this.setIcon(img);
    }
    
    /** indicate if the editor is closable
     *  @return true if the editor is closable
     */
    public boolean isClosable()
    {   return this.closable; }
    
    /** indicate if the editor is closable
     *  @param closable true if the editor is closable
     */
    public void setClosable(boolean closable)
    {   if ( closable != this.isClosable() )
        {
            this.closable = closable;
            
            this.support.firePropertyChange(PROPERTY_CLOSABLE, ! this.isClosable(), this.isClosable());
        }
    }
    
    /** indicate if the editor is maximizable
     *  @return true if the editor is maximizable
     */
    public boolean isMaximizable()
    {   return this.maximizable; }
    
    /** indicate if the editor is maximizable
     *  @param maximizable true if the editor is maximizable
     */
    public void setMaximizable(boolean maximizable)
    {   if ( maximizable != this.isMaximizable() )
        {
            this.maximizable = maximizable;
            
            this.support.firePropertyChange(PROPERTY_MAXIMIZE, ! this.isMaximizable(), this.isMaximizable());
        }
    }
    
    /** indicate if the editor is minimizable
     *  @return true if the editor is minimizable
     */
    public boolean isMinimizable()
    {   return this.minimizable; }
    
    /** indicate if the editor is minimizable
     *  @param minimizable true if the editor is minimizable
     */
    public void setMinimizable(boolean minimizable)
    {   if ( minimizable != this.isMinimizable() )
        {
            this.minimizable = minimizable;
            
            this.support.firePropertyChange(PROPERTY_MINIMIZE, ! this.isMinimizable(), this.isMinimizable());
        }
    }
    
    /** indicate if the editor should gain focus when launched
     *	@param gainFocus true to force the editor to gain focus when launched
     */
    public void setGainFocusWhenLaunched(boolean gainFocus)
    {
	if ( gainFocus != this.getGainFocusWhenLaunched() )
	{
	    this.gainFocusAtLaunch = gainFocus;
	    
	    this.support.firePropertyChange(Editor.PROPERTY_FOCUS_ON_EDITOR_WHEN_LAUNCHED, ! gainFocus, gainFocus);
	}
    }
    
    /** indicate if the editor should gain focus when launched
     *	@return true to force the editor to gain focus when launched
     */
    public boolean getGainFocusWhenLaunched()
    {
	return this.gainFocusAtLaunch;
    }
    
    /** indicate if the editor should gain focus when reedition
     *	@param gainFocus true to force the editor to gain focus when reedition
     */
    public void setGainFocusWhenReEdited(boolean gainFocus)
    {
	if ( gainFocus != this.getGainFocusWhenReEdited() )
	{
	    this.gainFocusAtReedition = gainFocus;
	    
	    this.support.firePropertyChange(Editor.PROPERTY_FOCUS_ON_EDITOR_WHEN_REEDITION, ! gainFocus, gainFocus);
	}
    }
    
    /** indicate if the editor should gain focus when reedition
     *	@return true to force the editor to gain focus when reedition
     */
    public boolean getGainFocusWhenReEdited()
    {
	return this.gainFocusAtReedition;
    }
    
    /** ########################################################################
     *  ################### validation, cancel and load ########################
     *  ######################################################################## */

    /**
     * method that indicate that the modifications that occurs in this editor
     *  should no longer exist.
     */
    public void cancel()
    {   
	this.setModified(false);
    }

    /**
     * method to update the caracteristics of the associated entity according
     *  to the value of the graphical components in the editor
     */
    public void save()
    {   
	this.setModified(false);
    }

    /**
     * method to update the graphical components in the editor
     *  according to the state of the relative associated entity
     */
    public void load()
    {   /* do nothing */ }
    
    /** ########################################################################
     *  ################# display and hide related methods #####################
     *  ######################################################################## */
    
    /** method called when the editor is about to be displayed */
    public void aboutToBeDisplayed()
    {   }
    
    /** method called to check if listeners accept the editor to be closed
     *  @exception ClosingRefusedException if a listener refused the editor to be closed
     */
    public void checkCloseAllowed() throws ClosingRefusedException
    {   
	this.fireClosingRequest();
	 
	if ( this.isModified() )
	{
	    throw new ClosingRefusedException("editor " + this + " is modified", null);
	}
    }
    
    /** method called when the editor is about to be closed */
    public void aboutToBeClosed()
    {   
        this.setModified(false);
    }
    
    /** method called when closing this editor is refused
     *  override this method to perform actions on editor but not to send explanation messages
     */
    public void closingRefused()
    {   /* do nothing */ }
    
    /** method called to close the editor */
    public void close()
    {   
	if ( this.isModified() )
	{
	    this.cancel();
	    this.setModified(false);
	}
	
	this.fireClosedRequest();
        
        /* we warn all listener that they had to close the editor...
         * it must here be closed */
        this.fireClosed();
	
	this.setInstance(null);
	
	final Component c = this.getComponent();
	if ( c != null )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    Container container = c.getParent();
		    if ( container != null )
		    {
			container.remove(c);
		    }
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
    
    /** called when the system determine that the editor should ask user if he wants to save current modifications
     *  @param component the invoker
     *  @return an integer representing the choice of the user : 
     *      <ul>
     *          <li>JOptionPane.YES_OPTION</li>
     *          <li>JOptionPane.NO_OPTION</li>
     *          <li>JOptionPane.CLOSED_OPTION</li>
     *          <li>JOptionPane.CANCEL_OPTION</li>
     *      </ul>
     */
    public int askUserForSaving(Component component)
    {   
        ResourceBundle rb = ResourceBundle.getBundle(AbstractEditor.class.getName());
        
        String message = rb.getString("askToSaveQuestion");
        String title   = rb.getString("askToSaveTitle");
        
        int result = JOptionPane.showConfirmDialog(component, message, title,
                                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        return result;
    }
    
    /** ########################################################################
     *  ################### EditorFocusListener methods ########################
     *  ######################################################################## */
    
    /** return the EventListenerList maintained by this editor
     *	@param createItIfnull true to force the creation fo the listener list if null
     *	@return an EventListenerList
     */
    protected synchronized EventListenerList getEventListenerList(boolean createItIfnull)
    {
	if ( createItIfnull && this.listeners == null )
	{
	    this.listeners = new EventListenerList();
	}
	
	return this.listeners;
    }
    
    /** add a new EditorFocusListener
     *  @param listener an instance of EditorClosureListener
     */
    public void addEditorFocusListener(EditorFocusListener listener)
    {   
        if ( listener != null )
        {   
            this.getEventListenerList(true).add(EditorFocusListener.class, listener);
        }
    }
    
    /** remove a EditorFocusListener
     *  @param listener an instance of EditorFocusListener
     */
    public void removeEditorFocusListener(EditorFocusListener listener)
    {   
	if ( this.getEventListenerList(false) != null )
	{
            this.getEventListenerList(true).remove(EditorFocusListener.class, listener);
	}
    }
    
    /** methods called to warn EditorFocusListener that the current editor gained focus
     *  @param oppositeEditor the editor that lost focus
     */
    public void fireEditorFocusGained(Editor oppositeEditor)
    {   EditorFocusListener[] listeners = (EditorFocusListener[])this.listeners.getListeners(EditorFocusListener.class);
        
        this.editorGainedFocus(oppositeEditor);
        
        if ( listeners != null )
        {   for(int i = 0; i < listeners.length; i++)
            {   EditorFocusListener current = listeners[i];
                
                if ( current != null )
                {   current.focusGained(oppositeEditor, this); }
            }
        }
    }
    
    /** methods called to warn EditorFocusListener that the current editor lost focus
     *  @param oppositeEditor the editor that gains focus
     */
    public void fireEditorFocusLost(Editor oppositeEditor)
    {   EditorFocusListener[] listeners = (EditorFocusListener[])this.listeners.getListeners(EditorFocusListener.class);
        
        this.editorLostFocus(oppositeEditor);
        
        if ( listeners != null )
        {   for(int i = 0; i < listeners.length; i++)
            {   EditorFocusListener current = listeners[i];
                
                if ( current != null )
                {   current.focusLost(this, oppositeEditor); }
            }
        }
    }
    
    /** method called on the editor when it gained focus
     *  @param oppositeEditor the editor that lost focus
     *
     *  to overwrite to do additional actions when the editor gained focus
     *
     *  should not be called directly
     */
    public void editorGainedFocus(org.siberia.editor.Editor oppositeEditor)
    {   logger.debug("editor " + this + " gained focus from " + oppositeEditor); }
    
    /** method called on the editor when it loose focus
     *  @param oppositeEditor the editor that gained focus
     *
     *  to overwrite to do additional actions when the editor lost focus
     *
     *  should not be called directly
     */
    public void editorLostFocus(org.siberia.editor.Editor oppositeEditor)
    {   logger.debug("editor " + this + " lost focus to " + oppositeEditor); }
    
    /** ########################################################################
     *  ############### ClosingEditorRequestListener methods ###################
     *  ######################################################################## */
    
    /** add a new EditorClosureListener
     *  @param listener an instance of EditorClosureListener
     */
    public void addEditorClosureListener(EditorClosureListener listener)
    {
        if ( listener != null )
        {   
            this.getEventListenerList(true).add(EditorClosureListener.class, listener);
        }
    }
    
    /** remove a new EditorClosureListener
     *  @param listener an instance of EditorClosureListener
     */
    public void removeEditorClosureListener(EditorClosureListener listener)
    {   
	if ( this.getEventListenerList(false) != null )
	{
            this.getEventListenerList(true).remove(EditorClosureListener.class, listener);
	}
    }
    
    /** indicate to ClosingEditorRequestListener that the editor claims to close
     *  @exception ClosingRefusedException if a listener refused to close the editor
     */
    protected void fireClosingRequest() throws ClosingRefusedException
    {   
	if ( this.getEventListenerList(false) != null )
        {   
	    EditorClosureListener[] l = this.getEventListenerList(true).getListeners(EditorClosureListener.class);
            if ( l != null )
            {   
		for(int i = 0; i < l.length; i++)
                {   
		    EditorClosureListener current = l[i];
                    if ( current != null )
                    {   current.editorRequestingToClose(this); }
                }
            }
        }
    }
    
    /** indicate to ClosingEditorRequestListener that the editor have to be closed */
    protected void fireClosedRequest()
    {   
	if ( this.getEventListenerList(false) != null )
        {   
	    EditorClosureListener[] l = this.getEventListenerList(true).getListeners(EditorClosureListener.class);
            if ( l != null )
            {   
		for(int i = 0; i < l.length; i++)
                {   
		    EditorClosureListener current = l[i];
                    if ( current != null )
                    {   current.editorRequestedToClose(this); }
                }
            }
        }
    }
    
    /** indicate to ClosingEditorRequestListener that the editor is closed */
    protected void fireClosed()
    {   
	if ( this.getEventListenerList(false) != null )
        {   
	    EditorClosureListener[] l = this.getEventListenerList(true).getListeners(EditorClosureListener.class);
            if ( l != null )
            {   
		for(int i = 0; i < l.length; i++)
                {   
		    EditorClosureListener current = l[i];
                    if ( current != null )
                    {   current.editorClosed(this); }
                }
            }
        }
    }
    
    /** ########################################################################
     *  ##################### life management methods ##########################
     *  ######################################################################## */
    
    /** method that is called when an editor is not used anymore */
    public void dispose()
    {
        this.setInstance(null);
    }
    
    /** method that is called when an editor is put in registry
     *  waiting to be used later if possible
     */
    public void putInCache()
    {
        this.setInstance(null);
    }
    
    /** method that is called when an editor is being removed from registry to be used again */
    public void removedFromCache()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("editor " + this + " is removed from cache to be used again");
	}
        /* to overwrite */
    }
    
    @Override
    protected void finalize() throws Throwable
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("Editor of type " + this.getClass() + " is going to be garbage collected");
	}
        
        super.finalize();
        
        this.dispose();
    }
    
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
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {   this.support.addPropertyChangeListener(listener); }

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
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {   this.support.removePropertyChangeListener(listener); }

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

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {   this.support.addPropertyChangeListener(propertyName, listener); }

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

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {   this.support.removePropertyChangeListener(propertyName, listener); }
    
    /** specify the statusbar to be added to the editor
     *  @param statusbar the statusbar to be added in the editor area
     **/
//    public void setStatusbar(AbstractStatusBar statusbar)
//    {   this.statusbar = statusbar;
//        /* if this.toolbar is not null, so the column has already been added */
//        if (this.hasStatusbar) return;
//        
//        /* else we have to create a new RowSpec */
//        RowSpec statusSpec = new RowSpec(AbstractEditor.statusbarConstraint);
//        int position = 2;
//        if ( this.hasToolbar) position = 3;
//        this.layout.insertRow(position, statusSpec);
//        this.hasStatusbar = true;
//    }
}
