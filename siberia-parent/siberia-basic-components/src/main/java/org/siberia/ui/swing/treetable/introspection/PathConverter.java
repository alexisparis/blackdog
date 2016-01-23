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
package org.siberia.ui.swing.treetable.introspection;

import javax.swing.tree.TreePath;

/**
 * 
 * Object that is able to determine the row for a TreePath
 *  and the TreePath for a given row
 *
 * @author alexis
 */
public interface PathConverter
{
    /** return the index for the given path
     *	@param path a TreePath
     *	@return an integer representing the row position
     */
    public int getRowForPath(TreePath path);
    
    /** return the index for the given row
     *	@param row an integer representing the row position
     *	@return path a TreePath
     */
    public TreePath getPathForRow(int row);
    
    /** return the total number of rom
     *	@return the row count
     */
    public int getRowCount();
}
