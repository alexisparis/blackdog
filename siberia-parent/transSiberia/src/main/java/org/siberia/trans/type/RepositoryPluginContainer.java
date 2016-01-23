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
package org.siberia.trans.type;

import java.beans.PropertyVetoException;
import org.siberia.trans.type.plugin.Plugin;
import org.siberia.trans.type.repository.SiberiaRepository;
import org.siberia.type.SibList;
import org.siberia.type.annotation.bean.Bean;

/**
 *
 * Object related to a SiberiaRepository that contains the plugin that the repository declares
 *
 * @author alexis
 */
@Bean(  name="pluginContainer",
        internationalizationRef="org.siberia.rc.i18n.type.RepositoryPluginContainer",
        expert=true,
        hidden=true,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class RepositoryPluginContainer extends SibList
{
    /** the repository linked to this container */
    private SiberiaRepository repository = null;
    
    /** Creates a new instance of RepositoryPluginContainer */
    public RepositoryPluginContainer()
    {	
	super(50);
	
	this.setAllowedClass(Plugin.class);
	this.setAcceptSubClassesItem(true);
	
	try
	{   
	    this.setRemoveAuthorization(false);
	    this.setCreateAuthorization(false);
	    this.setConfigurable(false);
	    this.setNameCouldChange(false);

	    this.setContentItemAsChild(true);
	    this.setRemovable(false);
	}
	catch (PropertyVetoException ex)
	{
	    ex.printStackTrace();
	}
    }
    
    /** return the repository linked to this container
     *	@return a SiberiaRepository
     */
    public SiberiaRepository getRepository()
    {	return this.repository; }
    
    /** initialize the repository linked to this container
     *	@param repository a SiberiaRepository
     */
    public void setRepository(SiberiaRepository repository)
    {	
	if ( repository != this.getRepository() )
	{
	    this.repository = repository;
	    
	    /* force the name to changed even if it cannot be modified externally */
	    try
	    {   
		this.setNameCouldChange(true);
		this.setName( this.getRepository() == null ? null : this.getRepository().getName() );
		this.setNameCouldChange(false);
	    }
	    catch (PropertyVetoException ex)
	    {
		ex.printStackTrace();
	    }
	}
    }
    
    /** add a new Plugin to the container
     *	@param plugin a Plugin
     */
    public void addPlugin(Plugin plugin)
    {
	this.add(plugin);
    }
    
    /** remove a Plugin in the container
     *	@param plugin a Plugin
     */
    public boolean removePlugin(Plugin plugin)
    {
	return this.remove(plugin);
    }
    
}
