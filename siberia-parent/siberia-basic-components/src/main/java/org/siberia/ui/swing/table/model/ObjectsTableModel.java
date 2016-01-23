/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.table.model;

import javax.swing.table.TableModel;

/**
 *
 * Define a TableMode that is able to given the objects for a row index
 *
 * @author alexis
 */
public interface ObjectsTableModel<U> extends TableModel
{
    
    /** return the item at the given index in the list
     *  @param index an integer
     *  @return U
     */
    public U getItem(int index);
    
    /** return the index of the given item
     *  @param item an item that managed by the model
     *  @return the index of the item in the table or -1 if it does not appear
     */
    public int getIndexOfItem(Object item);
}
