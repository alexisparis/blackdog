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
import org.siberia.trans.type.plugin.PluginDependency;

/**
 *
 * Thrown when a dependency could not be found
 *
 * @author alexis
 */
public class UnsatisfiedDependencyException extends ResourceNotFoundException
{
    
    /** Creates a new instance of UnsatisfiedDependencyException
     *	@param build the build which dependency is not satisfied
     *	@param dependency the dependency that could not be found
     */
    public UnsatisfiedDependencyException(PluginBuild build, PluginDependency dependency)
    {
	super("could not find dependency : " +
	      (dependency == null ? null : dependency.getName() + " with version constraint : " + dependency.getVersionConstraint()) +
	      " for build : " + (build == null ? null : build.getPluginId() + " with version " + build.getVersion() +
	      " on " + (build.getRepository() == null ? null : build.getRepository().getURL())));
    }
    
}
