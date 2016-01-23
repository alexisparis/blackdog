/* 
 * Siberia resources : siberia plugin to facilitate resource loading
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

/**
 *
 * Object that is able to parse a String representing a class on a siberia software : <br/>
 *  It can be represented by : <br/>
 *  <ul>
 *      <li>"siberia::org.siberia.ResourceLoader" which means the class <br/>
 *                      org.siberia.ResourceLoader that can be retrieved by the classloader of plugin siberia</li>
 *      <li>"org.siberia.ResourceLoader" which means the class <br/>
 *                      org.siberia.ResourceLoader that can be retrieved from a registered plugin<br/>
 *                      this methods is not efficient because, we must search on every plugin</li>
 *  </ul>
 *
 * @author alexis
 */
public class PluginClass
{
    /** id of the plugin containing the class */
    private String pluginId  = null;
    
    /* class name */
    private String className = null;
    
    /** Creates a new instance of Resource
     *  @param completePath a path containing the plugin id, the index of<br/>
     *      the rc directory to use and the path to the resource.<br/>
     *          example : "siberia::org.siberia.ResourceLoader" which means the class<br/>
     *                  that can be retrieved by classloader of plugin siberia.
     */
    public PluginClass(String completePath) throws IllegalArgumentException
    {   if ( completePath == null )
            throw new IllegalArgumentException("path must be specified");
        
        int endPlugin = completePath.indexOf("::");
        
        if ( endPlugin != -1 )
        {   String plugin = completePath.substring(0, endPlugin);
            
            this.className = completePath.substring(endPlugin + 2).trim();
            
            this.pluginId = completePath.substring(0, endPlugin);
        }
        else
        {   this.className = completePath; }
        
        if ( this.className.trim().length() == 0 )
            throw new IllegalArgumentException("class name is not specified");
    }
   
    /** return the id of the plugin containing the resource
     *  @return the id of the plugin containing the resource or null if none specified
     */
    public String getPlugin()
    {   return this.pluginId; }
    
    /** return the complete name of the class
     *  @return the complete name of the class
     */
    public String getClassName()
    {   return this.className; }
}
