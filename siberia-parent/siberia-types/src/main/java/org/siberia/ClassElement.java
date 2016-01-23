/* 
 * Siberia types : siberia plugin defining structures managed by siberia platform
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
package org.siberia;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.siberia.type.SibType;
import org.siberia.utilities.xml.dom.XMLTree;

/**
 *
 * description of a class and its sub classes
 *
 * @author alexis
 */
public class ClassElement
{
    /** static map that indicate the id of the plugin owner for a type class */
    public static Map<String, String> typePluginId = null;
    
    /** set of sub-ClassElements */
    private Set<ClassElement>    subClasses  = null;

    /** associated class */
    private Class                c           = null;
    
    /** id of the plugin that declared the class */
    private String               pluginId    = null;
    
    /** map of additive information */
    private Map<String, Object>  information = null;
    
    /** return the plugin id related to the given class
     *  @param c a Class
     *  @return a String
     */
    private static String getPluginId(Class c)
    {   String pluginId = null;
        
        if ( c != null )
        {   if ( typePluginId != null )
            {   pluginId = typePluginId.get(c.getName()); }
            
            if ( pluginId == null )
            {   pluginId = ResourceLoader.getInstance().getPluginIdWhichDeclare(c); }
        }
        
        return pluginId;
    }

    /** create a new ClassElement
     *  @param c a class
     *  @param pluginId the id of the plugin that declare the class
     */
    public ClassElement(Class c, String pluginId)
    {   if ( c == null )
            throw new IllegalArgumentException("class could not be null");
        if ( pluginId == null )
            throw new IllegalArgumentException("pluginId could not be null");
        
        this.c        = c;
        this.pluginId = pluginId;
    }

    /** save the class element
     *  @param manager instance of XMLTree
     */
    public void marshall(XMLTree manager)
    {   if ( manager != null )
        {   manager.createChildNode(TypeInformationProvider.NODE_CLASS);
            manager.setAttribute(TypeInformationProvider.ATTR_NAME, this.getRelatedClass().getName());
            manager.setAttribute(TypeInformationProvider.ATTR_PLUGIN_ID, this.pluginId);
            manager.setAttribute(TypeInformationProvider.ATTR_ABSTRACT,
                                Modifier.isAbstract(this.getRelatedClass().getModifiers()) ? "true" : "false");

            if ( this.subClasses != null )
            {   Iterator<ClassElement> it = this.subClasses.iterator();
                while(it.hasNext())
                    it.next().marshall(manager);
            }

            manager.toParent();
        }
    }

    /** return the class related to this item
     *  @return the class related to this item
     */
    public Class getRelatedClass()
    {   return this.c; }
    
    /** return the id of the plugin that declares the class
     *  @return a String
     */
    public String getPluginId()
    {   return this.pluginId; }

    /** return the subClasses
     *  @return a set of sub classes
     */
    public Set<ClassElement> getSubClasses()
    {   return this.subClasses; }

    /** add a new element class
     *  @param _c a new class
     *  @param pluginId the id of the plugin that declared type class _c
     *  @return the resulting ClassElement
     */
    public ClassElement addClass(Class _c, String pluginId)
    {   if ( _c != null && ! _c.isInterface() )
        {   if ( _c == this.getRelatedClass() )
                return this;
            else
            {   Class superClass = _c.getSuperclass();
                if ( superClass == this.getRelatedClass() )
                {   if ( this.subClasses == null )
                        this.subClasses =  new HashSet<ClassElement>();
                    ClassElement i = new ClassElement(_c, pluginId);
                    if ( ! this.subClasses.contains(i) )
                    {   this.getSubClasses().add(i);
                        return i;
                    }
                    else
                    {   /** loop until found */
                        Iterator<ClassElement> it = this.subClasses.iterator();
                        while(it.hasNext())
                        {   ClassElement current = it.next();
                            if ( current.getRelatedClass() == _c )
                                return current;
                        }
                    }
                }
                else
                {   
                    if ( this.getRelatedClass().isAssignableFrom(superClass) )
                    {   
                        ClassElement t = null;
                        t = this.addClass(superClass, getPluginId(superClass));
                        if ( t == null )
                        {   if ( this.getSubClasses() != null )
                            {   Iterator<ClassElement> it = this.getSubClasses().iterator();
                                
                                while(it.hasNext())
                                {   ClassElement current = it.next();
                                    
                                    if ( current != null && current.getRelatedClass().isAssignableFrom(superClass) )
                                    {   t = current.addClass(superClass, getPluginId(superClass));
                                        
                                        if ( t != null )
                                            break;
                                    }
                                }
                                
                            }
                        }
                        
                        if ( t != null )
                            return t.addClass(_c, getPluginId(_c));
                    }
                    /** particular case --> relatedClass is interface SibType and the type we try to add
                     *  directly inherits from Object and implements SibType
                     */
                    else if ( SibType.class.isAssignableFrom(_c) &&
                              ! SibType.class.isAssignableFrom(superClass) )
                    {   ClassElement superImplementation = new ClassElement(_c, getPluginId(_c));
                        if ( this.subClasses == null )
                            this.subClasses =  new HashSet<ClassElement>();
                        if ( ! this.subClasses.contains(superImplementation) )
                        {   this.getSubClasses().add(superImplementation);
                            return superImplementation;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /** return the ClassElement associated with the given class
     *  @param c a Class
     *  @return a ClassElement which related class is the given class or null if not found
     */
    private ClassElement getSubClassElementFor(Class c)
    {   ClassElement elt = null;
        
        if ( c != null )
        {   if ( c.equals(this.getRelatedClass()) )
            {   elt = this; }
            else if ( this.getRelatedClass().isAssignableFrom(c) )
            {   /* search on sub ClassElement */
                if ( this.subClasses != null )
                {   Iterator<ClassElement> elts = this.subClasses.iterator();
                    
                    while(elts.hasNext())
                    {   ClassElement currentElt = elts.next();
                        
                        if ( currentElt != null )
                        {   elt = currentElt.getSubClassElementFor(c); }
                        
                        if ( elt != null )
                            break;
                    }
                }
            }
        }
        
        return elt;
    }
    
    /* #########################################################################
     * ##################### information management ############################
     * ######################################################################### */
    
    /** add a new information
     *  @param name the name of the information
     *  @param obj an Object
     */
    private void addInformation(String name, Object obj)
    {   if ( this.information == null )
            this.information = new HashMap<String, Object>();
        
        this.information.put(name, obj);
    }
    
    /** remove a new information
     *  @param name the name of the information
     *  @param obj an Object
     */
    private void removeInformation(String name)
    {   if ( this.information != null )
            this.information.remove(name);
    }
    
    /** return the object linked with the given information name
     *  @param name the name of the information
     *  @return an Object
     */
    public Object getInformation(String name)
    {   Object o = null;
        if ( this.information != null )
            o = this.information.get(name);
        return o;
    }
    
    /** add a new information
     *  @param c the class where to add this information
     *  @param name the name of the information
     *  @param obj an Object
     */
    public void addInformation(Class c, String name, Object obj)
    {   ClassElement elt = this.getSubClassElementFor(c);
        if ( elt != null )
            elt.addInformation(name, obj);
    }
    
    /** remove a new information
     *  @param c the class where to remove this information
     *  @param name the name of the information
     *  @param obj an Object
     */
    public void removeInformation(Class c, String name)
    {   ClassElement elt = this.getSubClassElementFor(c);
        if ( elt != null )
            elt.removeInformation(name);
    }
    
    /** return the object linked with the given information name on the given class
     *  @param c the class where to get this information
     *  @param name the name of the information
     *  @return an Object
     */
    public Object getInformation(Class c, String name)
    {   Object o = null;
        ClassElement elt = this.getSubClassElementFor(c);
        if ( elt != null )
            o = elt.getInformation(name);
        return o;
    }
    
    /** ########################################################################
     *  ####################### Object overriden methods #######################
     *  ######################################################################## */

    public boolean equals(Object t)
    {   if ( t != null )
        {   if ( t instanceof ClassElement )
            {   return this.getRelatedClass() == ((ClassElement)t).getRelatedClass(); }
        }
        return false;
    }

    public int hashCode()
    {   return this.getRelatedClass().hashCode(); }
}