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
package org.siberia.ui.swing.tree;

/**
 *
 * mode of path expansion
 *
 * @author alexis
 */
public enum PathExpansionMode
{
    NONE(false, false, false),
    NODE_INSERTION(true, false, false),
    NODE_CHANGE(false, true, false),
    MODEL_CHANGE(false, false, true),
    NODE_INSERTION_AND_NODE_CHANGE(true, true, false),
    MODEL_CHANGE_AND_NODE_CHANGE(false, true, true),
    MODEL_CHANGE_AND_NODE_INSERTION(true, false, true),
    MODEL_CHANGE_AND_NODE_CHANGE_AND_NODE_INSERTION(true, true, true);

    /** true if an insertion should provoke path expansion */
    boolean insertNode  = false;

    /** true if a node change should provoke path expansion */
    boolean nodeChange  = false;

    /** true if a change of model should provoke all paths expansion */
    boolean modelChange = false;

    private PathExpansionMode(boolean insertNode, boolean nodeChange, boolean modelChange)
    {
	this.insertNode = insertNode;
	this.nodeChange = nodeChange;
	this.modelChange = modelChange;
    }

    /** true if an insertion should provoke path expansion
     *  @return a boolean
     */
    public boolean insertNode()
    {
	return insertNode;
    }

    /** true if a node change should provoke path expansion
     *  @return a boolean
     */
    public boolean nodeChange()
    {
	return nodeChange;
    }

    /** true if a change of model should provoke all paths expansion
     *  @return a boolean
     */
    public boolean modelChange()
    {
	return modelChange;
    }
}