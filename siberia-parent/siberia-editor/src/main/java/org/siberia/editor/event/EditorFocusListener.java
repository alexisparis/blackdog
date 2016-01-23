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

/**
 *
 * Interface for object that have to be warned when the editor loose or gain focus
 * 
 * @author alexis
 */
public interface EditorFocusListener extends EventListener
{
    /** editor gained focus
     *  @param oppositeEditor the editor that loose focus or null if we don't know
     *  @param editor the editor that gain focus
     */
    public void focusGained(Editor oppositeEditor, Editor editor);
    
    /** editor lost focus
     *  @param editor the editor that lost focus
     *  @param oppositeEditor the editor that gain focus or null if we don't know
     */
    public void focusLost(Editor editor, Editor oppositeEditor);
}
