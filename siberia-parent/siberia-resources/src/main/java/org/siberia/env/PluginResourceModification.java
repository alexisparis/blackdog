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
package org.siberia.env;

/**
 *
 * Define a modification in the plugins
 *
 * @author alexis
 */
public class PluginResourceModification
{
    /** kind of modification */
    public static enum Kind
    {
	ADD,
	REMOVE,
	UPDATE;
    }
    
    /** kind of modification */
    private Kind   kind     = Kind.ADD;
    
    /** the id of the plugin */
    private String pluginId = null;
    
    /** Creates a new instance of PluginResourceModification
     *	@param kind the kind of modification
     *	@param pluginId the id of the plugin affected by the modification
     */
    public PluginResourceModification(Kind kind, String pluginId)
    {
	this.kind = kind;
	this.pluginId = pluginId;
    }
    
    /** return the kind of modification
     *	@return a Kind
     */
    public Kind getModificationKind()
    {
	return this.kind;
    }
    
    /** return the id of the plugin which was affected by this modification
     *	@return a String
     */
    public String getPluginId()
    {
	return this.pluginId;
    }
    
}
