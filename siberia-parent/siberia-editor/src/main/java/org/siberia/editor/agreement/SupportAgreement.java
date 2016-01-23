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
package org.siberia.editor.agreement;

import org.siberia.editor.Editor;
import org.siberia.editor.support.EditorSupport;

/**
 *
 * Interface defining an object that tells if a support will accept a kind od editor
 *
 * @author alexis
 */
public interface SupportAgreement extends Comparable
{
    /** return true if the agreement indicates that this kind of editor is not supported by the support
     *  @return true if the agreement indicates that this kind of editor is not supported by the support
     */
//    public boolean isRejecting();

    /** return true if the agreement indicates that this kind of editor is likely supported by the support
     *  @return true if the agreement indicates that this kind of editor is likely supported by the support
     */
    public boolean isLikelySupported();
    
    /** return true if indicator agree is true
     *  @return true if indicator agree is true
     */
    public boolean agree();
    
    /** return the force of the agreement
     *  @return an integer representing the force of the agreement
     */
    public int getForce();

    /** return the editor concerned by the agreement
     *  @return an Editor
     */
    public Editor getEditor();

    /** initialize the editor concerned by this agreement
     *  @param editor an Editor
     */
    public void setEditor(Editor editor);

    /** return the support concerned by this agreement
     *  @return an EditorSupport
     */
    public EditorSupport getSupport();

    /** initialize the support concerned by this agreement
     *  @param support an EditorSupport
     */
    public void setSupport(EditorSupport support);
}
