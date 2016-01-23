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
package org.siberia.editor.event;

import java.util.EventListener;
import org.siberia.editor.Editor;
import org.siberia.editor.exception.ClosingRefusedException;

/**
 *
 * Interface for object that have to be warned when the editor request to be closed
 *
 * @author alexis
 */
public interface EditorClosureListener extends EventListener
{
    /** method called when the editor request to be closed.
     *  this request was accepted
     *  @param editor the editor that requested to be closed
     */
    public void editorRequestedToClose(Editor editor);
    
    /** method called when the editor request to be closed
     *  @param editor the editor that requested to be closed
     *  
     *  @exception ClosingRefusedException if the listener refused the editor to be closed
     */
    public void editorRequestingToClose(Editor editor) throws ClosingRefusedException;
    
    /** method called when the editor is really closed
     *  @param editor the editor that requested to be closed
     *  
     */
    public void editorClosed(Editor editor);
    
}
