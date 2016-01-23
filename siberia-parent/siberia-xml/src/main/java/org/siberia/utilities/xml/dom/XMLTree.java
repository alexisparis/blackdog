/* 
 * Siberia xml : siberia plugin to provide utilities for xml format
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
package org.siberia.utilities.xml.dom;

//Java
import java.io.*;
import java.util.*;
import org.jdom.Namespace;


/**
 * Define an entity that is able to deal with xml structure
 * 
 * @author  Alexis PARIS
 */
public interface XMLTree
{   
    /** add a new node to the current node
     *  @param node the node to add to the current node
     *  @return the global node or null if failed
     */
    public Object addTreeNode(Object node);
    
    /** create a noeud root node
     *  @param name the name of the root node
     *  @return false if creation failed
     */
    public boolean createTreeNode(String name);
    
    /** create a noeud root node
     *  @param root a node
     *  @return false if creation failed
     */
    public boolean createTreeNode(Object root);

    /** return the root node of the tree
     *  @return the root node
     */
    public Object getTreeNode();
    
    /** load the node declared in the given file
     *  @param file a File
     *  @return false if loading failed
     */
    public boolean loadTreeNode(File file);
    
    /** delete the current node and set its parent as current node
     *  @return false if failed
     */
    public boolean removeTreeNode();
    
    /** save the tree structure in a file
     *  @param filename the path to a File
     *  @return the created File
     */
    public File saveTreeNode(String filename);

    /** save the tree structure in a file
     *  @param filename the path to a File
     *  @param encoding (examples : ISO-8859-1, UTF-8)
     *  @return the created File
     */
    public File saveTreeNode(String filename, String encoding);
    
    /** create a deep copy of the current node
     *  @return a deep copy of the current node
     */
    public Object cloneCurrentNode();
    
    /** return the current node
     *  @return the current node
     */
    public Object getCurrentNode();
    
    /** return an XPath String representation of the current node
     *  @return an XPath String representation of the current node
     */
    public String getPosition();
    
    /** return an XPath String representation of the given node
     *  @param node a node
     *  @return an XPath String representation of the given node
     */
    public String getPosition(Object node);
        
    /** return an Iterator over all node direct child of the current node
     *  @return an Iterator over all node direct child of the current node
     */
    public Iterator getContentIterator();

    /** return an Iterator over all node direct child of the current node
     *  @return an Iterator over all node direct child of the current node
     */
    public Iterator getChildNodeIterator();
    
    /** set the current node
     *  @param node a node
     */
    public void setCurrentNode(Object node);
    
    /** set the current node by giving a String representation fo an XPath
     *  @param path a String representation of an XPath
     */
    public void setPosition(String path);
    
    /** the parent of the current node become the current node
     *  @return false if failed
     */
    public boolean toParent();
    
    /** add an annotation to the current node
     *  @param annotation an annotation
     *  @return false if failed
     */
    public boolean addChildComment(String annotation);
    
    /** add a new node to the current node
     *  @param name the name of the node to add
     *  @return false if failed
     */
    public boolean addChildNode(String name);
    
    /** add a new node to the current node
     *  @param name the name of the node to add
     *  @param value the value of the node
     *  @return false if failed
     */
    public boolean addChildNode(String name, String value);
    
    /** add a new node to the current node
     *  @param name the name of the node to add
     *  @param value the value of the node
     *  @param annotation an annotation to append to the new inserted node
     *  @return false if failed
     */
    public boolean addChildNode(String name, String value, String annotation);

    /** add a new node to the current node
     *  @param name the name of the node to add
     *  @param value the value of the node
     *  @param attrName the name of an attribute to add to the new node
     *  @param attrValue the value associated with attribute attrName
     *  @return false if failed
     */
    public boolean addChildNode(String name, String value, String attrName, String attrValue);

    /** create a new node and set it as the current node
     *  @param name the name of the node
     *  @return false if failed 
     */
    public boolean createChildNode(String name);

    /** create a new node and set it as the current node
     *  @param name the name of the node
     *  @param annotation an annotation to be associated with the node newly inserted
     *  @return false if failed 
     */
    public boolean createChildNode(String name, String annotation);
    
    /** return the value of an attribute of the current node
     *  @param attrName the name of the attribute
     *  @return the value of the attribute or null if not find
     */
    public String getAttributeValue(String attrName);
    
    /** return the value of an attribute of the given node
     *  @param attrName the name of the attribute
     *  @param node the node where to search for the attribute
     *  @return the value of the attribute or null if not find
     */
    public String getAttributeValue(String attrName, Object node);

    /** return the first child node of the current node which name is 'name'
     *  @param name the name of a node
     *  @return the first child node of the current node or null if not found
     */
    public Object getFirstChild(String name);

    /** return the first child node of the given node which name is 'name'
     *  @param name the name of a node
     *  @param node the node to consider
     *  @return the first child node of the current node or null if not found
     */
    public Object getFirstChild(String name, Object node);
    
    /** return a List containing the first level child of the current node
     *  @return a List containing the first level child of the current node
     */
    public java.util.List getChildren();
    
    /** return a List containing the first level child of the given node
     *  @param node the node to consider
     *  @return a List containing the first level child of the given node
     */
    public java.util.List getChildren(Object node);
    
    /** return a List containing the first level child of the current node which name is 'name'
     *  @param name a String representing a node name
     *  @param node the node to consider
     *  @return a List containing the first level child of the current node
     */
    public java.util.List getChildren(String name);
    
    /** return a List containing the first level child of the given node which name is 'name'
     *  @param name a String representing a node name
     *  @param node the node to consider
     *  @return a List containing the first level child of the given node
     */
    public java.util.List getChildren(String name, Object node);

    /** return the name of the current node
     *  @return the name of the current node or null if no current node
     */
    public String getNodeName();

    /** return the name of the given node
     *  @param node the node to consider
     *  @return the name of the given node or null if node is null
     */
    public String getNodeName(Object node);
    
    /** return the value of the current node
     *  @return the value of the current node or null if the current node is null
     */
    public String getNodeValue();

    /** return the value of the given node
     *  @param node the node to consider
     *  @return the value of the given node or null if node is null
     */
    public String getNodeValue(Object node);
    
    /** return the root node
     *  @return the root node
     */
    public Object getRootNode();

    /** tell if the given node has children
     *  @param node a node
     *  @return true if node contains children nodes
     */
    public boolean hasChildNode(Object node);
    
    /** tells if the given element is considered as a node
     *  @param element
     *  @return true if element is a node
     */
    public boolean isNode(Object element);
    
    /** Tell if the given object is considered as a annotation
     *  @param element
     *  @return true if element is an annotation
     */
    public boolean isComment(Object element);
    
    /** delete all attributes of the current node
     *  @return false if failed
     */
    public boolean removeAllAttribute();
    
    /** delete the attribute named 'attrName' in the current node
     *  @param attrName the name of the attribute to remove
     *  @return false if failed
     */
    public boolean removeAttribute(String attrName);
    
    /** Delete all the content of the current node (annotations too!)
     *  @return false if failed
     */
    public boolean removeAllContent();
    
    /** Delete a child from the current node
     *  @param index the position of the node to delete in the list of nodes of current node
     *  @return false if failed
     */
    public boolean removeChildNode(int index);
    
    /** Delete a child from the current node by giving its name
     *  @param childName the name of the child node to remove from the current node
     *  @return false if failed
     */
    public boolean removeFirstChildNode(String childName);
    
    /** Delete all children from the current node by giving its name
     *  @param childName the name of nodes to remove from the current node
     *  @return false if failed
     */
    public boolean removeAllChildNode(String childName);

    /** Declare an attribute on the current node (or overwritte value if it exists)
     *  @param attrName the name of the attribute
     *  @param attrValue the value to apply to the attribute
     *  @return false if failed
     */
    public boolean setAttribute(String attrName, String attrValue);

    /** Declare an attribute on the current node (or overwritte value if it exists)
     *  @param attrName the name of the attribute
     *  @param attrValue the value to apply to the attribute
     *  @param namespace a Namespace
     *  @return false if failed
     */        
    public boolean setAttribute(String attrName, String attrValue, Namespace namespace);
    
    /** set the value of the current node
     *  @param value the value to apply to the current node
     *  @return false if failed
     */
    public boolean setNodeValue(String value);
    
}
