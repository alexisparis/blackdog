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

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.siberia.ui.swing.table.PageableTable;

/**
 *
 * PathConverter that can take paginability into account to determine row index and path
 *
 *  the method getRowForPath does not need to be overriden since the tree knows every thing about the tree structure
 *  and therefore, pageability abilities does not complicate this.
 *
 * @author alexis
 */
public class PageablePathConverter extends DefaultPathConverter
{
    /** link to a PageableTable */
    private PageableTable pageableTable = null;
    
    /** Creates a new instance of PageablePathConverter
     *	@param tree a JTree
     */
    public PageablePathConverter(JTree tree)
    {
	super(tree);
    }
    
    /** set the pageable table
     *	@param table a PageableTable
     */
    public void setPageableTable(PageableTable table)
    {
	this.pageableTable = table;
    }
    
    /** return the pageable table
     *	@return a PageableTable
     */
    public PageableTable getPageableTable()
    {
	return this.pageableTable;
    }

    /**
     * return the index for the given row
     * 
     * 
     * @param row an integer representing the row position
     * @return path a TreePath
     */
    @Override
    public TreePath getPathForRow(int row)
    {
	/** convert index */
	int newRow = row;
	
	PageableTable table = this.getPageableTable();
	if ( table != null && table.arePageableFunctionnalitiesActivated() )
	{
	    newRow += table.getCurrentPage() * table.getMaximumDisplayedRows();
	}
	
	return super.getPathForRow(newRow);
    }
    
}
