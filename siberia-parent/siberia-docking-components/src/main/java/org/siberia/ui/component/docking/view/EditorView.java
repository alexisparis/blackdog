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
package org.siberia.ui.component.docking.view;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.FocusManager;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.DockingWindowListener;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.View;
import org.siberia.type.SibType;
import org.siberia.editor.Editor;
import org.siberia.editor.exception.ClosingRefusedException;
import org.siberia.editor.event.EditorClosureListener;
import org.siberia.editor.event.EditorClosureAdapter;

/**
 *
 * Default View for editor
 *
 * @author alexis
 */
public class EditorView extends AccessibleView implements PropertyChangeListener
{
    /** editor reference */
    private Editor editor                        = null;
    
    /** dockingWindowListener to interact with editor habilities */
    private DockingWindowListener listener       = null;
    
    /** EditorClosureListener */
    private EditorClosureListener  editorListener = null;
    
    /** property change listener on the editor */
    private PropertyChangeListener propEdListener = null;
    
    /** Creates a new instance of non always accessible EditorView
     *  @param editor an Editor
     */
    public EditorView(Editor editor)
    {   this(editor, false); }
    
    /** Creates a new instance of EditorView
     *  @param editor an Editor
     *  @param alwaysAccessible true if the view always have to accessible<br>
     *          in the root window even if the view is closed
     */
    public EditorView(final Editor editor, boolean alwaysAccessible)
    {   super( "title",
               null,
               editor.getComponent(),
               alwaysAccessible);
        
        this.editor = editor;
        
        if ( this.getEditor() != null )
        {   if ( this.getEditor().getInstance() != null )
            {   this.getEditor().getInstance().addPropertyChangeListener(this); }
            
	    this.propEdListener = new PropertyChangeListener()
            {
                public void propertyChange(final PropertyChangeEvent evt)
                {   
                    Runnable run = null;
                    
                    if ( evt.getPropertyName().equals(Editor.PROPERTY_ICON) )
                    {   if ( evt.getNewValue() instanceof Icon )
                        {   run = new Runnable()
                            {   public void run()
                                {   getViewProperties().setIcon( (Icon)evt.getNewValue() ); }
                            };
                        }
                    }
                    else if ( evt.getPropertyName().equals(Editor.PROPERTY_TITLE) )
                    {   if ( evt.getNewValue() instanceof String )
                        {   run = new Runnable()
                            {   public void run()
                                {   updateTitle((String)evt.getNewValue(), getEditor().isModified() ); }
                            };
                        }
                    }
                    else if ( evt.getPropertyName().equals(Editor.PROPERTY_MODIFIED) )
                    {   if ( evt.getNewValue() instanceof Boolean )
                        {   run = new Runnable()
                            {   public void run()
                                {   updateTitle(getEditor().getTitle(), (Boolean)evt.getNewValue() ); }
                            };
                        }
                    }
                    else if ( evt.getPropertyName().equals(Editor.PROPERTY_CLOSABLE) )
                    {   if ( evt.getNewValue() instanceof Boolean )
                        {   run = new Runnable()
                            {   public void run()
                                {   getWindowProperties().setCloseEnabled( (Boolean)evt.getNewValue() ); }
                            };
                        }
                    }
                    else if ( evt.getPropertyName().equals(Editor.PROPERTY_MAXIMIZE) )
                    {   if ( evt.getNewValue() instanceof Boolean )
                        {   run = new Runnable()
                            {   public void run()
                                {   getWindowProperties().setMaximizeEnabled( (Boolean)evt.getNewValue() ); }
                            };
                        }
                    }
                    else if ( evt.getPropertyName().equals(Editor.PROPERTY_MINIMIZE) )
                    {   if ( evt.getNewValue() instanceof Boolean )
                        {   run = new Runnable()
                            {   public void run()
                                {   getWindowProperties().setMinimizeEnabled( (Boolean)evt.getNewValue() ); }
                            };
                        }
                    }
                    
                    if ( run != null )
                    {   if ( SwingUtilities.isEventDispatchThread() )
                        {   run.run(); }
                        else
                        {   SwingUtilities.invokeLater(run); }
                    }
                }
            };
            this.getEditor().addPropertyChangeListener(this.propEdListener);
        }
        
        this.getViewProperties().setIcon( this.getEditor().getIcon() );
        this.updateTitle(this.getEditor().getTitle(), this.getEditor().isModified());
        this.getWindowProperties().setCloseEnabled(this.getEditor().isClosable());
        this.getWindowProperties().setMaximizeEnabled(this.getEditor().isMaximizable());
        this.getWindowProperties().setMinimizeEnabled(this.getEditor().isMinimizable());
        
        this.listener = new DockingWindowAdapter()
        {   
            public void windowClosing(DockingWindow window) throws OperationAbortedException
            {   if ( getEditor() != null )
                {   try
                    {   getEditor().checkCloseAllowed();
                        
                        boolean shouldClose = false;

                        /** if it is modified, then ask user waht he wants to do */
                        if ( getEditor().isModified() )
                        {   int result = getEditor().askUserForSaving(EditorView.this);

                            if ( result == JOptionPane.YES_OPTION )
                            {   shouldClose = true;
                                getEditor().save();
                                getEditor().setModified(false);
                            }
                            else
                            {   if ( result == JOptionPane.NO_OPTION )
                                {   shouldClose = true;
                                    getEditor().cancel();
                                    getEditor().setModified(false);
                                }
                                else if ( result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION )
                                {   shouldClose = false; }
                            }
                        }
                        else
                        {   shouldClose = true; }
                        
                        if ( shouldClose )
                        {   getEditor().aboutToBeClosed(); }
                        else
                        {   throw new OperationAbortedException(); }
                    }
                    catch(ClosingRefusedException e)
                    {   /** indicate to the editor that it could not be closed */
                        getEditor().closingRefused();
                        
                        /** provide messages to the interface */
                        JOptionPane.showMessageDialog(SwingUtilities.getRoot(EditorView.this), e.getMessage(),
                                                      "Error", JOptionPane.ERROR_MESSAGE);

                        throw new OperationAbortedException();
                    }
                }
            }

            public void windowClosed(DockingWindow window)
            {   editor.close(); }
            
            public void windowShown(DockingWindow window)
            {   if ( getEditor() != null )
                {   getEditor().load();
                    getEditor().aboutToBeDisplayed();
                }
            }
            
        };
        this.addListener(this.listener);
        
        this.editorListener = new EditorClosureAdapter()
        {   
            /** method called when the editor is really closed
             *  @param editor the editor that requested to be closed
             *  
             */
            public void editorClosed(Editor editor)
            {   
		/** do close in EDT */
		Runnable runnable = new Runnable()
		{
		    public void run()
		    {
			close();
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
        };
        this.editor.addEditorClosureListener(this.editorListener);
    }
    
    public void release()
    {
	if ( this.editor != null )
	{
	    if ( this.editorListener != null )
	    {
		this.editor.removeEditorClosureListener(this.editorListener);
	    }
	    if ( this.propEdListener != null )
	    {
		this.editor.removePropertyChangeListener(this.propEdListener);
	    }
	    if ( this.getEditor().getInstance() != null )
            {   this.getEditor().getInstance().removePropertyChangeListener(this); }
	    
	    this.editor = null;
	}
	
	this.editorListener = null;
	this.propEdListener = null;
	
	if ( this.listener != null )
	{
	    this.removeListener(this.listener);
	    this.listener = null;
	}
    }
    
    /** method called to change the title of the view
     *  @param title the title to apply
     *  @param modified true if the editor is modified
     */
    protected void updateTitle(String title, boolean modified)
    {   
	this.getViewProperties().setTitle( (title == null ? "unknown" : title) + (modified ? " *" : "") );
    }
    
    /** method called to change the title of the view
     *  @param title the title to apply
     */
    protected void updateTitle(String title)
    {   
	this.updateTitle(title, (this.getEditor() == null ? false : this.getEditor().isModified()));
    }
    
    /** dispose the view */
    public void dispose()
    {   if ( this.getEditor() != null )
        {   if ( this.getEditor().getInstance() != null )
            {   this.getEditor().getInstance().removePropertyChangeListener(this); }
        }
    }
    
    /** return the editor contained by this view
     *  @return an Editor
     */
    public Editor getEditor()
    {   return this.editor; }

    /**
     * This method gets called when a bound property is changed.
     * 
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {   if ( this.getEditor() != null )
        {   if ( evt.getSource() == this.getEditor().getInstance() )
            {   if ( evt.getPropertyName().equals(SibType.PROPERTY_NAME) )
                {   if ( evt.getNewValue() instanceof String )
                    {   
			this.updateTitle( (String)evt.getNewValue() );
		    }
                }
            }
        }
    }
    
}
