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

import java.util.Iterator;
import org.siberia.editor.Editor;
import org.siberia.type.SibType;
import org.siberia.editor.agreement.SupportAgreement;
import org.siberia.editor.launch.EditorLaunchContext;

/**
 *
 * Interface defining all methods that must implements graphical object
 * that are able to manager editor
 *
 * @author alexis
 */
public interface EditorSupport
{
    /** return the priority of the support<br>
     *	    the registered support with the highest priority will be asked first to support an editor<br>
     *	@return the priority of the support
     */
    public int getPriority();
    
    /** return an object telling if this support will accept the given editor
     *  @param editor the editor to support
     *  @param launchContext a EditorLaunchContext
     */
    public SupportAgreement agreeToSupport(Editor editor, EditorLaunchContext launchContext);
    
    /** register a new Editor
     *  @param editor an Editor to register
     *  @param launchContext a EditorLaunchContext
     */
    public void register(Editor editor, EditorLaunchContext launchContext);
    
    /** unregister a new Editor
     *  @param editor an Editor to unregister
     */
    public void unregister(Editor editor);
    
    /** unregister a SibType
     *  @param editor a SibType to unregister
     */
    public void unregister(SibType type);
    
    /** indicates if the given editor is being registered
     *  @param editor an Editor
     *  @return true if it is registered
     */
    public boolean isRegistering(Editor editor);
    
    /** indicates if the given type is being registered
     *  @param editor a SibType
     *  @return true if it is registered
     */
    public boolean isRegistering(SibType type);
    
    /** show a registered editor
     *  @param editor an Editor that is already registered
     */
    public void show(Editor editor);
    
    /** show a registered editor linked with the given type
     *  @param type a SibType
     */
    public void show(SibType type);
    
    /** return true if the given editor is currently being shown
     *  @param editor an Editor that is already registered
     *  @return true if the support is currently showing the given editor
     */
    public boolean isShowing(Editor editor);
    
    /** return true if the support is currently showing an editor related to this type
     *  @param type a SibType
     *  @return true if the support is currently showing an editor related to the given SibType
     */
    public boolean isShowing(SibType type);
    
    /** return a registered editor linked with this type
     *  @param type a SibType
     *  @return a registered editor or null if no found
     */
    public Editor getEditor(SibType type);
    
    /** return an Iterator over all Editor being registered by the support
     *  @return an Iterator over Editor
     */
    public Iterator<Editor> editors();
    
}
