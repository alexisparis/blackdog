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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


/**
 * Implementation of XMLTree interface
 *
 * @author  Alexis PARIS
 */
public class XMLTreeImpl implements XMLTree
{
    /** current element */
    protected Element currentElt = null;
    
    /** root element */
    protected Element rootElt    = null;
    
    /* Constructeur de la classe XMLTreeImpl */
    public XMLTreeImpl()
    {   }
    
    /** add a new node to the current node
     *  @param node the node to add to the current node
     *  @return the global node or null if failed
     */
    public Object addTreeNode(Object node)
    {   if( this.currentElt == null)
            return null;
        
        Element elt = (Element)((Content)node).clone();
        return (Object)this.currentElt.addContent(elt);
    }
    
    /** create a noeud root node
     *  @param name the name of the root node
     *  @return false if creation failed
     */
    public boolean createTreeNode(String name)
    {   this.rootElt    = new Element(name);
        this.currentElt = this.rootElt;
        return true;
    }
    
    /** create a noeud root node
     *  @param root a node
     *  @return false if creation failed
     */
    public boolean createTreeNode(Object root)
    {
        Element elt = (Element)root;
        Element elt2; 
        
        if(elt.getParent() == null)
        {
            this.rootElt = elt;
            this.currentElt = this.rootElt;
        }
        else
        {
            elt2 = (Element)elt.clone();
            this.rootElt = elt2;
            this.currentElt = this.rootElt;
        }
        
        return true;
    }
    
    /** return the root node of the tree
     *  @return the root node
     */
    public Object getTreeNode()
    { return this.rootElt; }
    
    /** load the node declared in the given file
     *  @param file a File
     *  @return false if loading failed
     */
    public boolean loadTreeNode(File file)
    {
        if(file == null) return false;
        
        try
        {
            Document doc    = new SAXBuilder().build(file);
            this.rootElt    = (Element)doc.getRootElement().clone();
            this.currentElt = this.rootElt;
        }
        catch(Exception e)
        {   return false;  }
        
        return true;
    }
    
    /** delete the current node and set its parent as current node
     *  @return false if failed
     */
    public boolean removeTreeNode()
    {
        if( this.currentElt == null )
            return false;
        
        Parent pere = this.currentElt.getParent();
        if( pere == null || pere.getClass().getName().equals("org.jdom.Element") == false )
        {   return false; }
        else
        {   Element elt = (Element)pere;
            elt.removeContent(this.currentElt);
            this.currentElt = elt;
            return true;
        }
    }
    
    /** save the tree structure in a file
     *  @param filename the path to a File
     *  @return the created File
     */
    public File saveTreeNode(String filename)
    {   return saveTreeNode(filename, ""); }
    
    /** save the tree structure in a file
     *  @param filename the path to a File
     *  @param encoding (examples : ISO-8859-1, UTF-8)
     *  @return the created File
     */
    public File saveTreeNode(String filename, String encoding)
    {   File file = null;
        
        try
        {   file = new File(filename);
            
            Format format = Format.getPrettyFormat();
            if( ! encoding.equals("") )
                format.setEncoding(encoding);
            
            XMLOutputter outputter = new XMLOutputter(format);
            FileWriter writer = new FileWriter(filename);
            Document doc = new Document(this.rootElt);
            outputter.output(doc, writer);
            writer.close();
        }
        catch(IOException e)
        {   return file; }
        
        return file;
    }
    
    /** create a deep copy of the current node
     *  @return a deep copy of the current node
     */
    public Object cloneCurrentNode()
    {   if(this.currentElt == null)
            return null;
        
        return this.currentElt.clone();
    }
    
    /** return the current node
     *  @return the current node
     */
    public Object getCurrentNode()
    {   return (Object) this.currentElt; }
    
    /** return an XPath String representation of the current node
     *  @return an XPath String representation of the current node
     */
    public String getPosition()
    {   if(this.currentElt == null)
            return null;
        
        Element e = currentElt;
        return getPosition(e);
    }
        
    /** return an XPath String representation of the given node
     *  @param node a node
     *  @return an XPath String representation of the given node
     */
    public String getPosition(Object node)
    {   StringBuffer szPathBuffer = new StringBuffer();
        Element e;
        
        if( node instanceof Element )
            e = (Element)node;
        else
            return null;

        do
        {   if( e != null )
            {   StringBuffer szElementBuffer = new StringBuffer();
                szElementBuffer.append("/" + e.getName());
                
                Element eParent = e.getParentElement();
                if( eParent != null )
                {   List listElement = eParent.getChildren(e.getName());
                    for(int i=0 ; i< listElement.size() ; i++)
                    {   if(e.equals(listElement.get(i)))
                        {   szElementBuffer.append("[" + (i + 1) + "]");
                            break;
                        }
                    }
                }
                
                szPathBuffer.insert(0, szElementBuffer);
                e = eParent;
            }
        }
        while( e != null );

        return szPathBuffer.toString();
    }
    
    /** return an Iterator over all node direct child of the current node
     *  @return an Iterator over all node direct child of the current node
     */
    public Iterator getContentIterator()
    {   if(this.currentElt == null)
            return null;
        
        return this.currentElt.getContent().iterator();
    }
    
    /** return an Iterator over all node direct child of the current node
     *  @return an Iterator over all node direct child of the current node
     */
    public Iterator getChildNodeIterator()
    {   if(this.currentElt == null)
            return null;
        
        return this.currentElt.getChildren().iterator();
    }
    
    /** set the current node
     *  @param node a node
     */
    public void setCurrentNode(Object node)
    {   this.currentElt = (Element)node; }
    
    /** set the current node by giving a String representation fo an XPath
     *  @param path a String representation of an XPath
     */
    public void setPosition(String path)
    {   /* not yet implemented */ }
    
    /** the parent of the current node become the current node
     *  @return false if failed
     */
    public boolean toParent()
    {   if( this.currentElt == null )
            return false;
        
        Parent pere = this.currentElt.getParent();
        if( pere == null || pere.getClass().getName().equals("org.jdom.Element") == false )
        {   return false; }
        else
        {   this.currentElt = (Element)pere;
            return true;
        }
    }
    
    /** add an annotation to the current node
     *  @param annotation an annotation
     *  @return false if failed
     */
    public boolean addChildComment(String annotation)
    {   if ( annotation == null )
            return false;
        
        if(this.currentElt == null)
            return false;
        
        Comment comment = new Comment(annotation);
        this.currentElt.addContent(comment);
        
        return true;
    }
    
    /** add a new node to the current node
     *  @param name the name of the node to add
     *  @return false if failed
     */
    public boolean addChildNode(String name)
    {   return this.addChildNode(name, ""); }
    
    /** add a new node to the current node
     *  @param name the name of the node to add
     *  @param value the value of the node
     *  @return false if failed
     */
    public boolean addChildNode(String name, String value)
    {   if( this.currentElt == null )
            return false;
        
        Element elt = new Element(name);
        elt.setText(value);
        this.currentElt.addContent(elt);
        
        return true;
    }
    
    /** add a new node to the current node
     *  @param name the name of the node to add
     *  @param value the value of the node
     *  @param annotation an annotation to append to the new inserted node
     *  @return false if failed
     */
    public boolean addChildNode(String name, String value, String annotation)
    {   if( this.addChildComment(annotation) )
        {   return this.addChildNode(name, value); }
        else
        {   return false; }
    }
    /** add a new node to the current node
     *  @param name the name of the node to add
     *  @param value the value of the node
     *  @param attrName the name of an attribute to add to the new node
     *  @param attrValue the value associated with attribute attrName
     *  @return false if failed
     */
    public boolean addChildNode(String name, String value, String attrName, String attrValue)
    {   if(this.currentElt == null)
            return false;
        
        Element elt = new Element(name);
        elt.setText(value);
        
        try
        {   elt.setAttribute(attrName, attrValue); }
        catch(Exception e)
        {   return false; }
        
        this.currentElt.addContent(elt);
        
        return true;
    }
    
    /** create a new node and set it as the current node
     *  @param name the name of the node
     *  @return false if failed 
     */
    public boolean createChildNode(String name)
    {   if(this.currentElt == null)
            return false;
        
        Element elt = new Element(name);
        this.currentElt.addContent(elt);
        this.currentElt = elt;
        
        return true;
    }
    
    /** create a new node and set it as the current node
     *  @param name the name of the node
     *  @param annotation an annotation to be associated with the node newly inserted
     *  @return false if failed 
     */
    public boolean createChildNode(String name, String annotation)
    {   if(this.addChildComment(annotation))
            return this.createChildNode(name);
        else
            return false;
    }
    
    /** return the value of an attribute of the current node
     *  @param attrName the name of the attribute
     *  @return the value of the attribute or null if not find
     */
    public String getAttributeValue(String attrName)
    {   if(this.currentElt == null)
            return null;
        
        return this.currentElt.getAttributeValue(attrName);
    }
    
    /** return the value of an attribute of the given node
     *  @param attrName the name of the attribute
     *  @param node the node where to search for the attribute
     *  @return the value of the attribute or null if not find
     */
    public String getAttributeValue(String attrName, Object node)
    {   if( node == null )
            return null;
        
        try
        {   return ((Element) node).getAttributeValue(attrName); }
        catch(Exception e)
        {   return null;}
    }

    /** return the first child node of the current node which name is 'name'
     *  @param name the name of a node
     *  @return the first child node of the current node or null if not found
     */
    public Object getFirstChild(String name)
    {   if(this.currentElt == null)
            return null;
        else
            return (Object)this.currentElt.getChild(name);
    }
    
    /** return the first child node of the given node which name is 'name'
     *  @param name the name of a node
     *  @param node the node to consider
     *  @return the first child node of the current node or null if not found
     */
    public Object getFirstChild(String name, Object node)
    {   if( node == null )
        {   return null; }
        else
        {   try
            {   return (Object) ((Element) node).getChild(name); }
            catch(Exception e)
            {   return null;}
        }
    }
    
    /** return a List containing the first level child of the current node
     *  @return a List containing the first level child of the current node
     */
    public java.util.List getChildren()
    {   if( this.currentElt == null )
            return null;
        else
            return this.currentElt.getChildren();
    }

    /** return a List containing the first level child of the given node
     *  @param node the node to consider
     *  @return a List containing the first level child of the given node
     */
    public java.util.List getChildren(Object node)
    {   if( node == null )
        {   return null; }
        else
        {   try
            {   return ((Element)node).getChildren(); }
            catch(Exception e)
            {   return null;}
        }
    }
    
    /** return a List containing the first level child of the current node which name is 'name'
     *  @param name a String representing a node name
     *  @param node the node to consider
     *  @return a List containing the first level child of the current node
     */
    public java.util.List getChildren(String name)
    {   if( this.currentElt == null )
        {   return null; }
        else
        {   return this.currentElt.getChildren(name); }
    }

    /** return a List containing the first level child of the given node which name is 'name'
     *  @param name a String representing a node name
     *  @param node the node to consider
     *  @return a List containing the first level child of the given node
     */
    public java.util.List getChildren(String name, Object node)
    {   if( node == null )
        {   return null; }
        else
        {   try
            {   return ((Element) node).getChildren(name); }
            catch(Exception e)
            {   return null; }
        }
    }
    
    /** return the name of the current node
     *  @return the name of the current node or null if no current node
     */
    public String getNodeName()
    {   if(this.currentElt == null)
            return null;
        else
            return this.currentElt.getName();
    }

    /** return the name of the given node
     *  @param node the node to consider
     *  @return the name of the given node or null if node is null
     */
    public String getNodeName(Object node)
    {   if( node == null )
            return null;
        
        try
        {   return ((Element)node).getName(); }
        catch(Exception e)
        {    return null; }
    }
    
    /** return the value of the current node
     *  @return the value of the current node or null if the current node is null
     */
    public String getNodeValue()
    {   if(this.currentElt == null)
            return null;
        else
            return this.currentElt.getText();
    }

    /** return the value of the given node
     *  @param node the node to consider
     *  @return the value of the given node or null if node is null
     */
    public String getNodeValue(Object node)
    {   if( node == null )
            return null;
        
        try
        {   return ((Element) node).getText(); }
        catch(Exception e)
        {   return null; }
    }
    
    /** return the root node
     *  @return the root node
     */
    public Object getRootNode()
    {   return this.rootElt; }
    
    /** tell if the given node has children
     *  @param node a node
     *  @return true if node contains children nodes
     */
    public boolean hasChildNode(Object node)
    {   try
        {   if(((Element)node).getChildren().size() > 0)
            {   return true; }
            else
            {   return false; }
        }
        catch(Exception e)
        {   return false; }
    }
    
    /** tells if the given element is considered as a node
     *  @param element
     *  @return true if element is a node
     */
    public boolean isNode(Object element)
    {   try
        {   Element elt = (Element)element;
            return true;
        }
        catch(Exception e)
        {    return false; }
    }
    
    /** Tell if the given object is considered as a annotation
     *  @param element
     *  @return true if element is an annotation
     */
    public boolean isComment(Object element)
    {   try
        {   Comment comment = (Comment)element;
            return true;
        }
        catch(Exception e)
        {   return false; }
    }
    
    /** delete all attributes of the current node
     *  @return false if failed
     */
    public boolean removeAllAttribute()
    {   if( this.currentElt == null )
            return false;
        
        Iterator i = this.currentElt.getAttributes().iterator();
        boolean bSup = true;
        
        while(i.hasNext())
        {   Attribute att = (Attribute)i.next();
            if( this.currentElt.removeAttribute(att) == false )
            {   bSup = false; }
        }
        
        return bSup;
    }
    
    /** delete the attribute named 'attrName' in the current node
     *  @param attrName the name of the attribute to remove
     *  @return false if failed
     */
    public boolean removeAttribute(String attrName)
    {   if( this.currentElt == null )
            return false;
        
        return this.currentElt.removeAttribute(attrName);
    }
    
    /** Delete all the content of the current node (annotations too!)
     *  @return false if failed
     */
    public boolean removeAllContent()
    {   if( this.currentElt == null )
            return false;
        
        this.currentElt.removeContent();
        return true;
    }
    
    /** Delete a child from the current node
     *  @param index the position of the node to delete in the list of nodes of current node
     *  @return false if failed
     */
    public boolean removeChildNode(int index)
    {   if( this.currentElt == null )
            return false;
        
        Iterator i = this.currentElt.getChildren().iterator();
        int iCptLoop = 0;
        
        while(i.hasNext())
        {   Element elt = (Element)i.next();
            iCptLoop++;
            
            if(iCptLoop == index)
            {   return this.currentElt.removeContent(elt); }
        }
        
        return false;
    }
    
    /** Delete a child from the current node by giving its name
     *  @param childName the name of the child node to remove from the current node
     *  @return false if failed
     */
    public boolean removeFirstChildNode(String childName)
    {   if( this.currentElt == null )
            return false;
        
        return this.currentElt.removeChild(childName);
    }
    
    /** Delete all children from the current node by giving its name
     *  @param childName the name of nodes to remove from the current node
     *  @return false if failed
     */
    public boolean removeAllChildNode(String childName)
    {   if( this.currentElt == null )
            return false;
        
        return this.currentElt.removeChildren(childName);
    }
        
    /** Declare an attribute on the current node (or overwritte value if it exists)
     *  @param attrName the name of the attribute
     *  @param attrValue the value to apply to the attribute
     *  @return false if failed
     */
    public boolean setAttribute(String attrName, String attrValue)
    {   if( this.currentElt == null )
            return false;
        
        try
        {   this.currentElt.setAttribute(attrName, attrValue);
            return true;
        }
        catch(Exception e)
        {   return false; }
    }
        
    /** Declare an attribute on the current node (or overwritte value if it exists)
     *  @param attrName the name of the attribute
     *  @param attrValue the value to apply to the attribute
     *  @param namespace a Namespace
     *  @return false if failed
     */        
    public boolean setAttribute(String attrName, String attrValue, Namespace namespace)
    {   if( this.currentElt == null )
            return false;
        
        try
        {   this.currentElt.setAttribute(attrName, attrValue, namespace);
            return true;
        }
        catch(Exception e)
        {   return false; }
    }
    
    /** set the value of the current node
     *  @param value the value to apply to the current node
     *  @return false if failed
     */
    public boolean setNodeValue(String value)
    {   if( this.currentElt == null )
            return false;
        
        try
        {   this.currentElt.setText(value);
            return true;
        }
        catch(Exception e)
        {   return false;}
    }
}
