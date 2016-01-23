/* 
 * Siberia lang : java language utilities
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
package org.siberia.type.lang;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import org.siberia.type.AbstractSibType;

/**
 *
 * Class representing a package in Sib structure
 *
 * @author alexis
 */
public class SibPackage extends AbstractSibType implements LangageElement
{
    // PENDING(api): revoir commentaire ( ajouter des exemples dans javadoc
    private static List<Package> loadedPackages = null;
    
    private Package javaPackage = null;
    
    /** Creates a new instance of SibPackage */
    public SibPackage()
    {   super(); }

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {   return this.getName(); }

    public Package getJavaPackage()
    {   return javaPackage; }

    public void setJavaPackage(Package javaPackage)
    {   this.javaPackage = javaPackage;
        
        if ( this.javaPackage == null )
        {   try
            {   this.setName(""); }
            catch(PropertyVetoException e)
            {   e.printStackTrace(); }
        }
        else
        {   int lastPointPosition = this.javaPackage.getName().lastIndexOf(".");
            if ( lastPointPosition == -1 )
            {   try
                {   this.setName(this.javaPackage.getName()); }
                catch(PropertyVetoException e)
                {   e.printStackTrace(); }
            }
            else
            {   try
                {   this.setName(this.javaPackage.getName().substring(lastPointPosition + 1)); }
                catch(PropertyVetoException e)
                {   e.printStackTrace(); }
            }
        }
    }
    
    public void setValue(Object value)
    {   if ( value instanceof Package )
            this.setJavaPackage((Package)value);
    }
    
    public Object getValue()
    {   return this.getJavaPackage(); }
    
    /** return true if the method is public
     *  @return true if the method is public
     */
    public boolean isPublic()
    {   return true; }
    
    /** return true if the method is public static
     *  @return true if the method is public static
     */
    public boolean isStatic()
    {   return true; }
    
    /** return the package of the element
     *  @return a String representing the package
     */
    public String getOwnedClass()
    {   if ( this.getJavaPackage() != null )
            return this.getJavaPackage().getName();
        return null;
    }
    
    public String getCompletionObject()
    {   if ( this.getJavaPackage() != null )
            return this.getJavaPackage().getName();
        return null;
    }
    
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   return this.getName(); }
    
    public List<SibPackage> getDirectSubPackages()
    {   if ( loadedPackages == null )
            updatePackageList();
        
        List<SibPackage> subPackages = new ArrayList<SibPackage>();
        
        Package currentPackage = null;
        for(int i = 0; i < loadedPackages.size(); i++)
        {   currentPackage = loadedPackages.get(i);
            
            if ( currentPackage.getName().startsWith(this.getOwnedClass()) )
            {   if ( currentPackage.getName().substring(this.getOwnedClass().length() + 1).indexOf(".") == -1 )
                {   SibPackage pack = new SibPackage();
                    pack.setJavaPackage(currentPackage);
                    subPackages.add(pack);
                }
            }
        }
        return subPackages;
    }
    
    public static void updatePackageList()
    {   if ( loadedPackages == null )
            loadedPackages = new ArrayList<Package>(Package.getPackages().length);
        
        Package currentPackage = null;
        for(int i = 0; i < Package.getPackages().length; i++)
        {   currentPackage = Package.getPackages()[i];
            if ( ! loadedPackages.contains(currentPackage) )
                loadedPackages.add(currentPackage);
        }
        
    }
    
}
