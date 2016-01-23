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
package org.siberia.ui.swing.tree.model;

import org.siberia.type.SibType;

/**
 *
 * Default implementation of a GenericTreeModel that does not allow
 * The structure to be modified directly by the user
 *
 * @author alexis
 */
public class VisualizationTreeModel extends GenericTreeModel
{
    
    /** Creates a new instance of VisualizationTreeModel */
    public VisualizationTreeModel()
    {   this(null); }
    
    /** create a new VisualizationTreeModel
     *  @param root an instance of SibType
     */
    public VisualizationTreeModel(SibType root)
    {   super(root); }
    
    /** ########################################################################
     *  ################# ConfigurableTreeModel implementation #################
     *  ######################################################################## */
    
    /** indicates if the tree model allows tree modifications
     *  @return true if the tree model allows tree modifications
     */
    public final boolean allowModifications()
    {   return false; }
    
}
