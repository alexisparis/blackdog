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
package org.siberia.ui.swing.table.controller;

import javax.swing.JTable;

/**
 *
 * external controller for table
 *
 * @author alexis
 */
public interface TableController
{
    /** return the number of mouse click that have to be done to start edition on the given row and column
     *	@param table a JTable
     *	@param row the index of the row
     *	@param column the index of the column
     *	@return the number of click that have to be done to start edition on the given row and column
     */
    public int getClickCountToStartEditingAt(JTable table, int row, int column);
}
