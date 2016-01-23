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

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeModel;
import org.siberia.type.SibType;

/**
 *
 * Interface defining a tree model in the siberia structure
 *
 * @author alexis
 */
public interface ConfigurableTreeModel extends TreeModel
{
    /** indicates if the tree model allows tree modifications
     *  @return true if the tree model allows tree modifications
     */
    public boolean allowModifications();
    
    /** indicates if the root element of the tree model is a virtual element
     *  @return true if the root element of the tree model is a virtual element
     */
    public boolean isRootVirtual();
    
    /** set the root entity for this model
     *  @param type a SibType
     */
    public void setRoot(SibType type);

    /** manage change events
     *  @param event a TreeModelEvent
     */
    public void fireTreeNodesChanged (TreeModelEvent event);

    /** manage insertion events
     *  @param event a TreeModelEvent
     */
    public void fireTreeNodesInserted (TreeModelEvent event);
  
    /** manage removing events
     *  @param event a TreeModelEvent
     */
    public void fireTreeNodesRemoved (TreeModelEvent event);

    /** manage structure modifications
     *  @param event a TreeModelEvent
     */
    public void fireTreeStructureChanged (TreeModelEvent event);
    
}
