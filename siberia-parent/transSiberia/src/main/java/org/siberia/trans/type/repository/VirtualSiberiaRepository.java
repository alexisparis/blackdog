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
package org.siberia.trans.type.repository;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import org.siberia.trans.exception.InvalidPluginDeclaration;
import org.siberia.trans.exception.ResourceNotFoundException;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.type.plugin.PluginStructure;
import org.siberia.trans.type.plugin.VirtualPluginBuild;
import org.siberia.utilities.task.TaskStatus;
import org.siberia.xml.schema.pluginarch.Module;

/**
 *
 * @author alexis
 */
public class VirtualSiberiaRepository extends DefaultSiberiaRepository
{
    /** logger */
    private Logger logger = Logger.getLogger(VirtualSiberiaRepository.class);
    
    /** Creates a new instance of VirtualSiberiaRepository */
    public VirtualSiberiaRepository()
    {	
	try
	{
	    this.setName("virtual repository");
	}
	catch (PropertyVetoException ex)
	{
	    ex.printStackTrace();
	}
    }
    
    /** return the module declaration contains builds information
     *  @param plugin a PluginStructure
     *  @param status a TaskStatus
     *
     *  @return a Module
     */
    @Override
    public Module getModuleDeclaration(PluginStructure plugin, TaskStatus status) throws ResourceNotFoundException,
                                                                                         IOException,
											 JAXBException
    {
	/* no module specification for virtual repository */
	return null;
    }
    
    /** return the URL representing the given Build
     *	@param build a PluginBuild
     *	@return an URL of null if error
     */
    @Override
    protected URL getBuildURL(PluginBuild build) throws InvalidPluginDeclaration,
							MalformedURLException
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getBuildURL(" + build + ")");
	}
	
	URL url = null;
	
	/* special case for VirtualPluginBuild */
	if ( build instanceof VirtualPluginBuild )
	{
	    url = ((VirtualPluginBuild)build).getURL();
	}
	else
	{
	    url = super.getBuildURL(build);
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getBuildURL(" + build + ") returns " + url);
	}
	
	return url;
    }
}
