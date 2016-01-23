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
 * Agreement which disagree with a force equal to 10
 *
 * @author alexis
 */
public class NegativeSupportAgreement extends DefaultSupportAgreement
{
    
    /** Creates a new instance of NegativeSupportAgreement
     *  @param support an EditorSupport
     *  @param editor an Editor
     */
    public NegativeSupportAgreement(EditorSupport support, Editor editor)
    {   super(support, editor, false, 10); }
    
}
