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
 * Object that is able to parse a String representing a resource on a siberia software : <br/>
 *  It can be represented by : <br/>
 *  <ul>
 *      <li>"siberia;2::menu/main.xml" which means the xml file main.xml<br/>
 *                      located in the menu directory of the second resources directory declared<br/>
 *                      by plugin 'siberia'</li>
 *      <li>"siberia::menu/main.xml" which means the xml file main.xml<br/>
 *                      located in the menu directory of one of the resources directory declared<br/>
 *                      by plugin 'siberia'</li>
 *      <li>"menu/main.xml" which means an xml file main.xml<br/>
 *                      located in a menu directory of one resource directory of one plugin<br/>
 *                      which is not specified. this way is not efficient, because several<br/>
 *                      resources could be represented like this, and it must look on all plugin</li>
 *  </ul>
 *
 * @author alexis
 */
public class PluginResource
{
    /** id of the plugin containing the resource */
    private String pluginId         = null;
    
    /** index of the resource directory to use */
    private int    pluginRcDirIndex = 1;
    
    /* path to the resource */
    private String path             = null;
    
    /** Creates a new instance of Resource
     *  @param completePath a path containing the plugin id, the index of<br/>
     *      the rc directory to use and the path to the resource.<br/>
     *          example : "siberia;1::menu/main.xml" which means the xml file main.xml<br/>
     *                  located in the main directory of the first resources directory declared<br/>
     *                  by plugin 'siberia'.
     */
    public PluginResource(String completePath) throws IllegalArgumentException
    {   if ( completePath == null )
            throw new IllegalArgumentException("path must be specified");
        
        int endPlugin = completePath.indexOf("::");
        
        if ( endPlugin != -1 )
        {   String plugin = completePath.substring(0, endPlugin);
            
            this.path = completePath.substring(endPlugin + 2).trim();
            
            int sep = plugin.indexOf(';');
            if ( sep != -1 )
            {   this.pluginId = plugin.substring(0, sep).trim();
                
                String index = plugin.substring(sep + 1);
                
                try
                {   this.pluginRcDirIndex = Integer.parseInt(index); }
                catch(NumberFormatException e)
                {   this.pluginRcDirIndex = -1; }
            }
            else
            {   this.pluginId = plugin.trim();
                this.pluginRcDirIndex = -1;
            }
            
            if ( this.pluginId.trim().length() == 0 )
                throw new IllegalArgumentException("plugin id is not specified");
        }
        else
        {   this.pluginRcDirIndex = -1;
            int sep = completePath.indexOf(';');
            if ( sep != -1 )
                throw new IllegalArgumentException("invalid representation of a resource");
            this.path = completePath.trim();
        }
        
        if ( this.path.trim().length() == 0 )
            throw new IllegalArgumentException("path is not specified");
    }
   
    /** return the id of the plugin containing the resource
     *  @return the id of the plugin containing the resource or null if none specified
     */
    public String getPlugin()
    {   return this.pluginId; }
    
    /** return the index of the resource directory to use withing the plugin identified by id given by getPlugin()
     *  @return the index of the resource directory to use withing the plugin identified by id given by getPlugin()<br/>
     *      if getPlugin() return null, then the result of this method should not be taken into account
     */
    public int getRcDirIndex()
    {   return this.pluginRcDirIndex; }
    
    /** return the path of the resource in the resource directory specified if so
     *  @return the path of the resource in the resource directory specified if so
     */
    public String getPath()
    {   return this.path; }
}
