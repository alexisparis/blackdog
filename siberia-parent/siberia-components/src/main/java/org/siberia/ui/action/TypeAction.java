/* 
 * Siberia components : siberia plugin for graphical components
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
package org.siberia.ui.action;

import java.util.List;
import javax.swing.Action;
import org.siberia.type.SibType;

/**
 *
 * Define an action that is related to an array of type
 *
 * @author alexis
 */
public interface TypeAction<E> extends Action
{
    /** set the type to link with this action
     *  @param types an array of Object
     */
    public void setTypes(E... types);

    /** return the Objects related to this action
     *  @return the Objects related to this action
     */
    public List<E> getTypes();
}
