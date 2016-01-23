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
package org.siberia.editor.support;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.siberia.editor.Editor;
import org.siberia.type.SibType;
import org.siberia.editor.agreement.IndifferentSupportAgreement;
import org.siberia.editor.agreement.LikelySupportAgreement;
import org.siberia.editor.agreement.NegativeSupportAgreement;
import org.siberia.editor.agreement.SupportAgreement;
import org.siberia.editor.launch.EditorLaunchContext;

/**
 *
 * support for EditorSupport
 *
 * @author alexis
 */
public class EditorSupportEntity implements EditorSupport
{
    /** logger */
    private Logger                          logger               = Logger.getLogger(EditorSupportEntity.class);
    
    /** associated support */
    private EditorSupport		    support              = null;
    
    /** registered editor */
    private List<Editor>                    editors              = null;
    
    /** editor currently shown */
    private Editor                          shownEditor          = null;
    
    /** object to allow synchronization */
    private Object                          semaphore            = new Object();
    
    /** array of likely supported Editors */
    private Class[]                         likelySupported      = null;
    
    /** array of indifferent Editors */
    private Class[]                         indifferentSupported = null;
    
    /** array of non wanted Editors */
    private Class[]                         unwantedSupported    = null;
    
    /** array of forbidden Editors */
//    private Class[]          forbiddenSupported   = null;
    
    /** default support agreement */
    private SupportAgreement                defaultAgreement     = null;
    
    /** priority */
    private int                             priority             = 50;
    
    /** create a new EditorSupportEntity
     *  return an IndifferentSuppotrAgreement if no defautl agreement is provided
     *  @param support the EditorSupport associated with this entity
     *  @param likely an Array of class extending Editor representing Editor that are likely supported
     *  @param indifferent an Array of class extending Editor representing Editor that indifferently supported
     *  @param unwanted an Array of class extending Editor representing Editor that could not be supported if possible
     *  @param forbidden an Array of class extending Editor representing Editor that are forbidden
     */
    public EditorSupportEntity(EditorSupport support, Class[] likely, Class[] indifferent, Class[] unwanted)//, Class[] forbidden)
    {   
        this.support = support;
        
        this.likelySupported      = likely;
        this.indifferentSupported = indifferent;
        this.unwantedSupported    = unwanted;
//        this.forbiddenSupported   = forbidden;
    }
    
    /** return the priority of the support<br>
     *	    the registered support with the highest priority will be asked first to support an editor<br>
     *	@return the priority of the support
     */
    public int getPriority()
    {
	return this.priority;
    }
    
    /** initialize the priority of the support<br>
     *	    the registered support with the highest priority will be asked first to support an editor<br>
     *	@param priority the priority of the support
     */
    public void setPriority(int priority)
    {
	this.priority = priority;
    }
    
    /** initialize the default agreement to provide if no agreement found
     *  @param agreement an SupportAgreement
     */
    public void setDefaultAgreement(SupportAgreement agreement)
    {   this.defaultAgreement = agreement;
        if ( this.defaultAgreement != null )
        {   this.defaultAgreement.setSupport(this.support); }
    }
    
    /** return an object telling if this support will accept the given editor
     *  @param editor the editor to support
     *  @param launchContext a EditorLaunchContext
     */
    public SupportAgreement agreeToSupport(Editor editor, EditorLaunchContext launchContext)
    {   
        SupportAgreement agreement = null;
        
        if ( editor != null )
        {   
//            if ( forbiddenSupported != null )
//            {   for(int i = 0; i < forbiddenSupported.length; i++)
//                {   Class current = forbiddenSupported[i];
//                    if ( editor.getClass().isAssignableFrom(current) )
//                    {   agreement = new ForbiddenSupportAgreement(this.support, editor);
//                        break;
//                    }
//                }
//            }
            if ( unwantedSupported != null && agreement == null )
            {   for(int i = 0; i < unwantedSupported.length; i++)
                {   Class current = unwantedSupported[i];
                    if ( editor.getClass().isAssignableFrom(current) )
                    {   agreement = new NegativeSupportAgreement(this.support, editor);
                        break;
                    }
                }
            }
            if ( indifferentSupported != null && agreement == null )
            {   for(int i = 0; i < indifferentSupported.length; i++)
                {   Class current = indifferentSupported[i];
                    if ( editor.getClass().isAssignableFrom(current) )
                    {   agreement = new IndifferentSupportAgreement(this.support, editor);
                        break;
                    }
                }
            }
            if ( likelySupported != null && agreement == null )
            {   for(int i = 0; i < likelySupported.length; i++)
                {   Class current = likelySupported[i];
                    if ( editor.getClass().isAssignableFrom(current) )
                    {   agreement = new LikelySupportAgreement(this.support, editor);
                        break;
                    }
                }
            }
        }
        
        if ( agreement == null )
        {   if ( this.defaultAgreement != null )
                agreement = this.defaultAgreement; 
            else
                agreement = new IndifferentSupportAgreement(null, null);
        }
        
        if ( agreement.getEditor() != editor )
            agreement.setEditor(editor);
        if ( agreement.getSupport() != this.support )
            agreement.setSupport(this.support);
        
        return agreement;
    }
    
    /** register a new Editor<br>
     *  Current editor become the given editor if not null
     *  @param editor an Editor to register
     *  @param launchContext a EditorLaunchContext
     */
    public void register(Editor editor, EditorLaunchContext launchContext)
    {   if ( editor != null )
        {   synchronized(this.semaphore)
            {   if ( this.editors == null )
                    this.editors = new ArrayList<Editor>();
                this.editors.add(editor);

                /** the last editor added become the current edited */
                this.shownEditor = editor;
            }
        }
    }
    
    /** unregister a new Editor<br>
     *  Current editor could change
     *  @param editor an Editor to unregister
     */
    public void unregister(Editor editor)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling unregister(" + editor + ")");
	}
	if ( this.editors != null )
        {   synchronized(this.semaphore)
            {   
                int index = this.editors.indexOf(editor);
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("index of the editor : " + index);
		}
                
                if ( this.editors.remove(editor) )
                {   if ( index >= this.editors.size() )
                        index --;
                    
                    /* change the current editor */
                    if ( index < 0 )
                        this.shownEditor = null;
                    else
                        this.shownEditor = this.editors.get(index);
                }
		else
		{
		    logger.warn("could not remove editor " + editor);
		}
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling unregister(" + editor + ")");
	}
    }
    
    /** unregister a SibType<br>
     *  Current editor could change
     *  @param editor a SibType to unregister
     */
    public void unregister(SibType type)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling unregister(" + type + ")");
	}
	Editor editor = this.getEditor(type);
        if ( editor != null )
            this.unregister(editor);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of calling unregister(" + type + ")");
	}
    }
    
    /** indicates if the given editor is being registered
     *  @param editor an Editor
     *  @return true if it is registered
     */
    public boolean isRegistering(Editor editor)
    {   boolean registered = false;
        if ( this.editors != null )
	{
	    
            registered = this.editors.contains(editor);
	}
        return registered;
    }
    
    /** indicates if the given type is being registered
     *  @param editor a SibType
     *  @return true if it is registered
     */
    public boolean isRegistering(SibType type)
    {   return this.getEditor(type) != null; }
    
    /** show a registered editor
     *  @param editor an Editor that is already registered
     */
    public void show(Editor editor)
    {   synchronized(this.semaphore)
        {   this.shownEditor = editor; }
    }
    
    /** show a registered editor linked with the given type
     *  @param type a SibType
     */
    public void show(SibType type)
    {   synchronized(this.semaphore)
        {   this.shownEditor = this.getEditor(type); }
    }
    
    /** return true if the given editor is currently being shown
     *  @param editor an Editor that is already registered
     *  @return true if the support is currently showing the given editor
     */
    public boolean isShowing(Editor editor)
    {   
	Editor shown = this.shownEditor;
	return shown == editor;
    }
    
    /** return true if the support is currently showing an editor related to this type
     *  @param type a SibType
     *  @return true if the support is currently showing an editor related to the given SibType
     */
    public boolean isShowing(SibType type)
    {   boolean ok = false;
	
	synchronized(this.semaphore)
        {   if ( this.shownEditor != null )
	    {
                ok = this.shownEditor.getInstance() == type;
	    }
        }
	
        return ok;
    }
    
    /** return a registered editor linked with this type
     *  @param type a SibType
     *  @return a registered editor or null if no found
     */
    public Editor getEditor(SibType type)
    {   Editor edit = null;
        Iterator<Editor> it = this.editors();
        while(it.hasNext())
        {   Editor current = it.next();
            if ( current != null )
            {   if ( current.getInstance() == type )
                {   edit = current;
                    break;
                }
            }
        }
        return edit;
    }
    
    /** return an Iterator over all Editor being registered by the support
     *  @return an Iterator over Editor
     */
    public Iterator<Editor> editors()
    {   
	List<Editor> _editors = null;
        if ( this.editors != null )
	{
	    _editors = this.editors;
	}
	
	if ( _editors == null )
	{
	    _editors = Collections.EMPTY_LIST;
	}
	
        return _editors.iterator();
    }
    
}
