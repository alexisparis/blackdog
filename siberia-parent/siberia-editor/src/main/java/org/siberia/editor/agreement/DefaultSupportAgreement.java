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
 * Default implementation of SupportAgreement
 *
 * @author alexis
 */
public class DefaultSupportAgreement implements SupportAgreement
{
    /** the editor concerned by the agreement */
    private Editor        editor  = null;

    /** the support concerned by the agreement */
    private EditorSupport support = null;

    /* tell if it agrees */
    private boolean       agree   = true;

    /** represents the force of the agreement <br>
     *  if  agree is true and force is more than 20, it means that the support like to support<br>
     *  the given kind of editor. else if agree is false and force is less than 20, the support<br>
     *  prefer to avoid those kind of editor. if agree is false and force is more than 20, then this kind of editor <br>
     *  could not be given to the concerned support
     */
    private int           force   = 20;

    /** create a new Agreement
     *  @param support an EditorSupport
     *  @param editor an Editor
     *  @param agree true if the support accept this kind of editor
     *  @param force an integer <br>
     *      if  agree is true and force is more than 20, it means that the support like to support<br>
     *      the given kind of editor. else if agree is false and force is less than 20, the support<br>
     *      prefer to avoid those kind of editor. if agree is false and force is more than 20, then this kind of editor <br>
     *      could not be given to the concerned support
     */
    public DefaultSupportAgreement(EditorSupport support, Editor editor, boolean agree, int force)
    {   this.setSupport(support);
        this.setEditor(editor);
        this.agree = agree;
        this.force = force;
    }
    
    /** return true if indicator agree is true
     *  @return true if indicator agree is true
     */
    public boolean agree()
    {   return this.agree; }
    
    /** return the force of the agreement
     *  @return an integer representing the force of the agreement
     */
    public int getForce()
    {   return this.force; }

    /** return true if the agreement indicates that this kind of editor is not supported by the support
     *  @return true if the agreement indicates that this kind of editor is not supported by the support
     */
//    public boolean isRejecting()
//    {   return ( ! this.agree && this.force > 20 ); }

    /** return true if the agreement indicates that this kind of editor is likely supported by the support
     *  @return true if the agreement indicates that this kind of editor is likely supported by the support
     */
    public boolean isLikelySupported()
    {   return (this.agree && this.force > 20); }

    /** return the editor concerned by the agreement
     *  @return an Editor
     */
    public Editor getEditor()
    {   return editor; }

    /** initialize the editor concerned by this agreement
     *  @param editor an Editor
     */
    public void setEditor(Editor editor)
    {   this.editor = editor; }

    /** return the support concerned by this agreement
     *  @return an EditorSupport
     */
    public EditorSupport getSupport()
    {   return support; }

    /** initialize the support concerned by this agreement
     *  @param support an EditorSupport
     */
    public void setSupport(EditorSupport support)
    {   this.support = support; }

    /** return true if this is equals to object o
     *  @param o an Object
     *  @return true if this is equals to object o
     */
    public boolean equals(Object o)
    {   boolean result = false;
        if ( o != null )
        {   if ( o instanceof SupportAgreement )
            {   SupportAgreement other = (SupportAgreement)o;

                if ( this.agree == other.agree() )
                {   if ( this.force == other.getForce() )
                        result = true;
                }
            }
        }
        return result;
    }

    /** compare two agreement
     *  @param o another object
     */
    public int compareTo(Object o)
    {   int result = 0;
        if ( o == null )
            result = 1;
        else if ( o instanceof SupportAgreement )
        {   SupportAgreement other = (SupportAgreement)o;
            if ( this.agree != other.agree() )
            {   result = (this.agree ? 1 : -1); }
            else
            {   result = this.force - other.getForce(); }
        }

        return result;
    }

    @Override
    public String toString()
    {
	return this.getClass().getSimpleName() + " for editor " + editor + " agree ? " + this.agree() + " with force " + this.getForce();
    }
    
}
