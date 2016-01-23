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
package org.siberia.ui.swing.treetable.adapter;

import javax.swing.JTree;
import javax.swing.table.TableModel;
import org.siberia.ui.swing.table.model.IntrospectionSibTypeListTableModel;
import org.siberia.ui.swing.treetable.introspection.IntrospectionSibTypeListTreeTableModel;


/**
 *
 * Implementation of AdapterFactory that fit IntrospectionSibTypeListTreeTableModel
 *
 * @author alexis
 */
public class IntrospectionAdapterFactory implements AdapterFactory<IntrospectionSibTypeListTreeTableModel>
{
    
    /** Creates a new instance of IntrospectionAdapterFactory */
    public IntrospectionAdapterFactory()
    {	}
    
    /** create a new TableModel according to the given parameters
     *	@param treeTableModel an instanceof M
     *	@param tree the tree component that represents the structure
     *	@return a TableModel
     */
    public TableModel createModelAdapter(IntrospectionSibTypeListTreeTableModel treeTableModel, JTree tree)
    {
	return new IntrospectionTreeTableModelAdapter(treeTableModel, tree);
    }
    
}
