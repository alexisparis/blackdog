/*
 * blackdog player : define the interface Player supported by blackdog
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog;

/**
 *
 * Static declarations about the plugin siberia-types
 *
 * @author alexis
 */
public class BlackdogPlayerPlugin
{
    /** id of the plugin */
    public static final String PLUGIN_ID             = "blackdog-player";
    
    /** name of the extension point 'player' */
    public static final String PLAYER_EXTENSION_NAME = "Player";
    
    /** parameter name of the extension point player */
    public static final String EXTENSION_POINT_PLAYER_NAME     = "name";
    
    /** parameter class of the extension point player */
    public static final String EXTENSION_POINT_PLAYER_CLASS    = "class";
    
    /** parameter priority of the extension point player */
    public static final String EXTENSION_POINT_PLAYER_PRIORITY = "priority";
    
    /** Creates a new instance of BlackdogPlayerPlugin */
    protected BlackdogPlayerPlugin()
    {   }
    
}
