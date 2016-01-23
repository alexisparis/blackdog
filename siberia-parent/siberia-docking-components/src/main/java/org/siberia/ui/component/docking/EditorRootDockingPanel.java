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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.SwingUtilities;
import net.infonode.docking.View;
import org.apache.log4j.Logger;
import org.siberia.editor.event.EditorClosureListener;
import org.siberia.editor.exception.ClosingRefusedException;
import org.siberia.type.SibType;
import org.siberia.ui.component.docking.view.AccessibleView;
import org.siberia.ui.component.docking.view.EditorView;
import org.siberia.editor.Editor;
import org.siberia.editor.ResourceEditor;
import org.siberia.editor.agreement.IndifferentSupportAgreement;
import org.siberia.editor.agreement.SupportAgreement;
import org.siberia.editor.support.EditorSupport;
import org.siberia.editor.support.EditorSupportEntity;
import org.siberia.editor.launch.EditorLaunchContext;

/**
 *
 * Root docking panel that inherits from RootDockingPanel and that manages<br>
 * Editors.<br>
 *
 * this class implements EditorSupport
 *
 * @author alexis
 */
public class EditorRootDockingPanel extends RootDockingPanel
                                    implements EditorSupport, 
					       EditorClosureListener
{   
    /** logger */
    private Logger              logger  = Logger.getLogger(EditorRootDockingPanel.class);
    
    /** support for EditorSupport */
    private EditorSupportEntity support = new EditorSupportEntity((EditorSupport)this, null ,null, null);
    
    /** map linking Editor and view */
    private Map<Editor, View>   editors = new WeakHashMap<Editor, View>();
    
    /** tell if the panel supports always accessible views */
    private boolean             supportAlwaysAccessibleViews = false;
    
    /** Creates a new instance of EditorRootDockingPanel */
    public EditorRootDockingPanel()
    {   super(); }
    
    /** tells if the panel supports always accessible views
     *  @return true if the panel supports always accessible views
     */
    public boolean supportAlwaysAccessibleViews()
    {   return this.supportAlwaysAccessibleViews; }
    
    /** initialize if the panel supports always accessible views
     *  @param support true if the panel supports always accessible views
     */
    public void supportAlwaysAccessibleViews(boolean support)
    {   this.supportAlwaysAccessibleViews = support; }
    
    /** return an Object related to the given View<br>
     *  To overwritte for specific needs
     *  @param view a View
     *  @return an Object
     */
    public Object getRelatedInstanceTo(View view)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getRelatedInstanceTo(" + view + ")");
	}
	Object o = null;
        if ( this.editors != null )
        {   synchronized(this.editors)
            {
                Iterator<Editor> it = this.editors.keySet().iterator();
                while(it.hasNext())
                {   Editor current = it.next();

                    View v = this.editors.get(current);

                    if ( v == view )
                    {   o = current;
                        break;
                    }
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getRelatedInstanceTo(" + view + ") returns " + o);
	}
        return o;
    }

    public void show(SibType type)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling show(" + type + ")");
	}
	this.support.show(type);
        
        /* find the editor linked with the given type */
        if ( type != null )
        {   if ( this.editors != null )
            {   synchronized(this.editors)
                {   Iterator<Editor> it = this.editors.keySet().iterator();
		    
		    boolean shown = false;
                    while(it.hasNext())
                    {   Editor current = it.next();

                        if ( current.getInstance() == type )
                        {   this.show(current);
			    shown = true;
			    break;
			}
                    }
		    
		    if ( ! shown )
		    {
			logger.warn("item " + type + " could not be shown (not found in the {editor, view} map)");
		    }
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of show(" + type + ")");
	}
    }

    public boolean isShowing(SibType type)
    {   return this.support.isShowing(type); }

    public boolean isRegistering(SibType type)
    {   return this.support.isRegistering(type); }
    
    public Editor getEditor(SibType type)
    {   return this.support.getEditor(type); }

    public boolean isShowing(Editor editor)
    {   return this.support.isShowing(editor); }

    public boolean isRegistering(Editor editor)
    {   return this.support.isRegistering(editor); }

    public SupportAgreement agreeToSupport(Editor editor, EditorLaunchContext launchContext)
    {   return new IndifferentSupportAgreement(this, editor); }

    public synchronized Iterator<Editor> editors()
    {   Iterator<Editor> editors = null;
        if ( this.editors != null )
            editors = this.editors.keySet().iterator();
        if ( editors == null )
            editors = Collections.EMPTY_LIST.iterator();
        return editors;
    }
    
    /** return the priority of the support<br>
     *	    the registered support with the highest priority will be asked first to support an editor<br>
     *	@return the priority of the support
     */
    public int getPriority()
    {
	return 50;
    }

    public void show(Editor editor)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling show(" + editor + ")");
	}
	this.support.show(editor);
        
        if ( editor != null )
        {   /* find the view */
            if ( this.editors != null )
            {   synchronized(this.editors)
                {   View v = this.editors.get(editor);
                    if ( v != null )
                    {   this.showView(v); }
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of show(" + editor + ")");
	}
    }

    public void unregister(SibType type)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling unregister(" + type + ")");
	    logger.debug("asking editor support entity to unregister editor for type " + type);
	}
	this.support.unregister(type);
        
        /* find the editor linked with the given type */
        if ( type != null )
        {   if ( this.editors != null )
            {   synchronized(this.editors)
                {   Iterator<Editor> it = this.editors.keySet().iterator();
                    while(it.hasNext())
                    {   Editor current = it.next();

                        if ( current.getInstance() == type )
                        {   View view = this.editors.get(current);

                            if ( ! (view instanceof AccessibleView) )
                            {   /* remove the view */
                                this.removeView(view);

                                this.editors.remove(current);
				
				current.removeEditorClosureListener(this);
                            }
                        }
                    }
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of unregister(" + type + ")");
	}
    }

    public void unregister(Editor editor)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling unregister(" + editor + ")");
	}
	if ( editor != null )
            this.unregister(editor.getInstance());
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling unregister(" + editor + ")");
	}
    }
    
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    
    /** remove a view from the docking panel<br>
     *  If the view is accessible and is set to always accessible, then, the view could be show with <br>
     *  the popup menu of the root window.
     *  @param view the view to remove
     */
    public void removeView(View view)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling removeView(" + view + ")");
	    logger.debug("view is a " + (view == null ? null : view.getClass()));
	}
	
	super.removeView(view);
        
        if ( view != null )
        {   if ( view instanceof EditorView )
            {   if ( ! ((AccessibleView)view).isAlwaysAccessible() )
                {   
		    Editor viewEditor = ((EditorView)view).getEditor();
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("editor of view is " + viewEditor);
		    }
		    this.support.unregister( viewEditor );
                    if ( this.editors != null )
                    {   synchronized(this.editors)
                        {   
			    this.editors.remove(viewEditor);
			    
			    viewEditor.removeEditorClosureListener(this);
			}
                    }
                }
            }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("editor is not instance of EditorView --> could not be removed");
		}
	    }
        }
	
	if ( view instanceof EditorView )
	{
	    ((EditorView)view).release();
	}
	view.close();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of removeView(" + view + ")");
	}
    }

    public void register(final Editor editor, EditorLaunchContext launchContext)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling register(" + editor + ", " + launchContext + ")");
	}
	if ( editor != null )
        {   if ( ! this.isRegistering(editor.getInstance()) )
            {   this.support.register(editor, launchContext);
		
//		editor.addEditorClosureListener(this);
		
                boolean alwaysAccessible = false;
                
                if ( this.supportAlwaysAccessibleViews() )
                {   if ( editor instanceof ResourceEditor )
		    {
                        alwaysAccessible = true;
		    }
                }
		
		final boolean finalAlwaysAccessible = alwaysAccessible;

		Runnable runnable = new Runnable()
		{
		    public void run()
		    {
			View v = new EditorView(editor, finalAlwaysAccessible);

			if ( editors == null )
			    editors = new HashMap<Editor, View>();
			// in edt, no need
			synchronized(editors)
			{   editors.put(editor, v); }

			if ( logger.isDebugEnabled() )
			{
			    logger.debug("trying to add view " + v + " with gainFocusAtLaunch=" + editor.getGainFocusWhenLaunched());
			}
			addView(v, editor.getGainFocusWhenLaunched());
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
            else if ( editor.getGainFocusWhenReEdited() )
            {   /* force to show the view if editor support it */
                if ( this.editors != null )
                {   synchronized(this.editors)
                    {   Iterator<Editor> it = this.editors.keySet().iterator();
			
			boolean shown = false;
			
                        while(it.hasNext())
                        {   Editor current = it.next();

                            if ( current != null )
                            {   if ( current.getInstance() == editor.getInstance() )
                                {   
				    final Editor choosenEditor = current;
				    
				    Runnable runnable = new Runnable()
				    {
					public void run()
					{
					    show(choosenEditor);
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
				    
				    shown = true;
                                    break;
                                }
                            }
                        }
			if ( ! shown )
			{
			    logger.warn("edior " + editor + " could not be shown (not found in the {editor, view} map)");
			}
                    }
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of register(" + editor + ", " + launchContext + ")");
	}
    }
    
    /* #########################################################################
     * ################ EditorClosureListener implementation ###################
     * ######################################################################### */
    
    /** method called when the editor request to be closed.
     *  this request was accepted
     *  @param editor the editor that requested to be closed
     */
    public void editorRequestedToClose(Editor editor)
    {	}
    
    /** method called when the editor request to be closed
     *  @param editor the editor that requested to be closed
     *  
     *  @exception ClosingRefusedException if the listener refused the editor to be closed
     */
    public void editorRequestingToClose(Editor editor) throws ClosingRefusedException
    {	}
    
    /** method called when the editor is really closed
     *  @param editor the editor that requested to be closed
     *  
     */
    public void editorClosed(Editor editor)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("editor " + editor + " was closed --> unregister it");
	}
	this.unregister(editor);
    }
    
}
