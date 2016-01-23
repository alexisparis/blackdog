/* 
 * TransSiberia : siberia plugin allowing to update siberia pplications and download new plugins
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
package org.siberia.trans.exception;

import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.type.repository.SiberiaRepository;

/**
 *
 * Exception thrown when trying to access to a plugin build declaration
 * on a given repository but the repository does not declare such version
 * for the plugin.
 *
 * @author alexis
 */
public class PluginBuildVersionNotFoundException extends ResourceNotFoundException
{
    
    /**
     * Creates a new instance of PluginBuildVersionNotFoundException
     * 
     * @param pluginBuild the plugin build which version was not found
     * @param repository the repository used to get the build declaration
     */
    public PluginBuildVersionNotFoundException(PluginBuild build, SiberiaRepository repository)
    {
	super("could not found version '" + (build == null ? null : build.getVersion()) + 
	      "' for plugin '" + (build == null ? null : build.getPluginId()) + 
	      "' in repository '" + repository);
    }
    
}
