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
package org.siberia.trans.xml;

import java.beans.PropertyVetoException;
import org.siberia.trans.type.plugin.Plugin;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.type.plugin.Version;
import org.siberia.trans.type.plugin.Version.VersionFormatException;
import org.siberia.trans.type.repository.SiberiaRepository;
import org.siberia.xml.schema.pluginarch.ModuleBuild;
import org.siberia.xml.schema.pluginarch.ModuleDeclaration;

/**
 *
 * @author alexis
 */
public class JAXBUtilities
{
    
    /** Creates a new instance of TransUtilities */
    private JAXBUtilities()
    {   }
    
    /** create a Plugin according to a PluginType<br>
     *	WARNING : the Plugin created is not completely initialized<br>
     *		  prefer using static method createPlugin(...) of class RepositoryUtilities
     *  @param module a ModuleDeclaration
     *  @param repository a SiberiaRepository
     *  @return a Plugin or null if pluginType is null
     */
    public static Plugin createPlugin(ModuleDeclaration module, SiberiaRepository repository)
    {
        Plugin plugin = null;
        
        if ( module != null )
        {   plugin = new Plugin();
            
//            plugin.setCheckType()
            plugin.setDirectoryRelativePath(module.getLocation().getValue());
//            plugin.setLicenseCode()
            plugin.setPluginDeclarationFilename(module.getDeclarationFileName().getValue());
            
            if ( repository != null )
	    {	plugin.setRepository(repository); }
	    
            try
            {   plugin.setName(module.getName());
		plugin.setPluginId(module.getName());
	    }
            catch(PropertyVetoException e)
            {   e.printStackTrace(); }
            
            plugin.setShortDescription(module.getShortDescription());
            plugin.setDescription(module.getLongDescription());
            
            plugin.setCategory(module.getCategory());
        }
        
        return plugin;
    }
    
    /** create a given plugin build according to a ModuleBuild
     *  @param plugin a Plugin
     *  @param build a ModuleBuild
     *	@return a PluginBuild
     */
    public static PluginBuild createPluginBuild(Plugin plugin, ModuleBuild build)
    {
	PluginBuild pluginBuild = null;
	
	if ( plugin == null )
	{
	    pluginBuild = plugin.createBuild();
	    
	    if ( (pluginBuild != null) && (build != null) )
	    {
		pluginBuild.setCheckType(build.getCheck().value());
		pluginBuild.setReleaseDate(build.getReleaseDate().toGregorianCalendar());

		Version version = null;
		try
		{   version = Version.parse(build.getVersion()); }
		catch(VersionFormatException e)
		{   version = new Version();
		    version.setMajor(1);
		    version.setMinor(1);
		    version.setRevision(1);
		}

		pluginBuild.setVersion(version);
		pluginBuild.setLicenseName(build.getVersion());
	    }
	}
	
	return pluginBuild;
    }
    
}
