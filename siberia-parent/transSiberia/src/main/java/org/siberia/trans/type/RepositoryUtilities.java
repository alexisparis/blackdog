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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.log4j.Logger;
import org.siberia.trans.exception.InvalidBuildDependencyException;
import org.siberia.trans.exception.InvalidRepositoryException;
import org.siberia.trans.exception.PluginBuildVersionNotFoundException;
import org.siberia.trans.exception.RepositoryCreationException;
import org.siberia.trans.exception.ResourceNotFoundException;
import org.siberia.trans.type.plugin.Plugin;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.type.plugin.PluginDependency;
import org.siberia.trans.type.plugin.Version;
import org.siberia.trans.type.plugin.VersionChoice;
import org.siberia.trans.type.plugin.VersionConstraint;
import org.siberia.trans.type.repository.*;
import org.siberia.trans.xml.JAXBLoader;
import org.siberia.trans.xml.JAXBUtilities;
import org.siberia.utilities.cache.GenericCache2;
import org.siberia.utilities.security.check.CheckSum;
import org.siberia.utilities.task.TaskStatus;
import org.siberia.xml.schema.pluginarch.CheckType;
import org.siberia.xml.schema.pluginarch.License;
import org.siberia.xml.schema.pluginarch.LicenseLink;
import org.siberia.xml.schema.pluginarch.ModuleDeclaration;
import org.siberia.xml.schema.pluginarch.ModuleDependency;
import org.siberia.xml.schema.pluginarch.PhantomRepositories;
import org.siberia.xml.schema.pluginarch.Repository;
import org.siberia.xml.schema.pluginarch.RepositoryDecl;
import org.siberia.xml.schema.pluginarch.Module;
import org.siberia.xml.schema.pluginarch.ModuleBuild;

/**
 *
 * Declare some useful static methods that deal with SiberiaRepository
 *
 * @author alexis
 */
public class RepositoryUtilities
{
    /** logger */
    private static Logger logger = Logger.getLogger(RepositoryUtilities.class);
    
    /** name of the file that represent the declaration of the content of a repository */
    private static final String REPOSITORY_CONTENT_DECLARATION_FILENAME = "declaration.xml";
    
    /* #########################################################################
     * ########## attributes that are used to obtain the Repository ############
     * #################### declaration of a repository ########################
    /* ######################################################################### */
    
    /** map that link a SiberiaRepository to a filename that represents the copy of 
     *  repository's declaration in local
     *
     *  this map can contains a pair with value equals to null : that means that the repository does not provide a valid declaration
     *      ( it avoid to download the same invalid declaration severall times )
     */
    private static Map<SiberiaRepository, String> repositoryDeclarations = new HashMap<SiberiaRepository, String>();
    
    /** set of suspicious repository that seems to provide invalid declaration */
    private static Set<SiberiaRepository>         suspectRepositories    = new HashSet<SiberiaRepository>();
    
    /** map linking repository with their Repository declaration
     *  all repository appearing in this list have a valid content declaration file that has been downloaded in local
     *
     *  So, if the reference returned as a Repository returns null, then the local file have to be read again
     */
    private static Map<SiberiaRepository, SoftReference<Repository>> repositoryDeclMap = new HashMap<SiberiaRepository, SoftReference<Repository>>();
    
    /* #########################################################################
     * ######### attributes to get licenses and phantoms repositories ##########
     * ########################### information #################################
    /* ######################################################################### */
    
    /** cache for licenses */
    private static LicenseCache licenseCache = new LicenseCache();
    
    /** cache for phantom repositories */
    private static PhantomRepositoriesCache phantomRepositoriesCache = new PhantomRepositoriesCache();
    
    /** Creates a new instance of RepositoryUtilities */
    private RepositoryUtilities()
    {   }
    
    /** return the CheckSum to use for the given CheckType
     *	@param type a CheckType
     *	@return a CheckSum
     */
    public static CheckSum getCheckSumForType(CheckType type)
    {
	CheckSum check = null;
	
	if ( type != null )
	{
	    check = CheckSum.getCheckSumForAbbreviation(type.value());
	}
	
	if ( check == null )
	{
	    check = CheckSum.NONE;
	}
	
	return check;
    }
    
    /** create a SiberiaRepositoryLoaction according to a SiberiaRepository and initialize its name
     *	@param repository a SiberiaRepository
     *	@return a SiberiaRepositoryLocation
     */
    public static SiberiaRepositoryLocation createLocation(SiberiaRepository repository)
    {
	return createLocation(repository, true);
    }
    
    /** create a SiberiaRepositoryLoaction according to a SiberiaRepository
     *	@param repository a SiberiaRepository
     *	@param setName true to initialize the name of the repository location
     *	@return a SiberiaRepositoryLocation
     */
    public static SiberiaRepositoryLocation createLocation(SiberiaRepository repository, boolean setName)
    {
	SiberiaRepositoryLocation location = null;
	
	if ( repository != null )
	{
	    location = new SiberiaRepositoryLocation();
			
	    try
	    {
		location.setValue(repository.getURL());
		if ( setName )
		{
		    location.setName(repository.getName());
		}
	    }
	    catch (PropertyVetoException ex)
	    {	ex.printStackTrace(); }
	}
	
	return location;
    }
    
    /** create a RepositoryPluginContainer according to a SiberiaRepository and initialize it
     *	@param repository a SiberiaRepository
     *	@return a RepositoryPluginContainer
     */
    public static RepositoryPluginContainer createPluginContainer(SiberiaRepository repository)
    {
	RepositoryPluginContainer container = null;
	
	if ( repository != null )
	{
	    container = new RepositoryPluginContainer();
	    container.setRepository(repository);
	}
	
	return container;
    }
    
    /** return a map that linked the name of a license to the license terms for a given repository
     *  @param repository a SiberiaRepository
     *  @return a map or null if no license declared
     */
    public static Map<String, String> getLicensesFor(SiberiaRepository repository)
    {   return licenseCache.get(repository); }
    
    /** return a set of phantom repositories associated with the given repository
     *  @param repository a SiberiaRepository
     *  @return a set or null if no phantom repository declared
     */
    public static Set<SiberiaRepository> getPhantomRepositoriesFor(SiberiaRepository repository)
    {   return phantomRepositoriesCache.get(repository); }
    
    /** create a Plugin according to a ModuleDeclaration and a repository
     *	@param declaration a ModuleDeclaration
     *	@param repository a SiberiaRepository
     *	@return a Plugin or null if creation, failed
     */
    private static Plugin createPlugin(ModuleDeclaration declaration, SiberiaRepository repository)
    {
	Plugin plugin = null;
                
	if ( declaration != null )
	{   
	    plugin = JAXBUtilities.createPlugin(declaration, repository);

	    if ( plugin != null )
	    {   
		plugin.setRepository(repository);

		VersionChoice choice = new VersionChoice();

		/** feed VersionChoice of plugin */
		Module modul = null;
		try
		{	modul = repository.getModuleDeclaration(plugin, null); }
		catch (Exception ex)
		{	
		    logger.warn("unable to load plugin declaration for plugin '" + plugin.getName() + "' " +
				"from repository : '" + 
				(plugin.getRepository() == null ? null : plugin.getRepository().getURL()), ex);
		}

		if ( modul != null )
		{
		    List<ModuleBuild> builds = modul.getBuild();
		    if ( builds != null )
		    {
			for(int i = 0; i < builds.size(); i++)
			{
			    ModuleBuild currentBuild = builds.get(i);

			    if ( currentBuild != null && currentBuild.isActive() )
			    {
				Version version = Version.parse(currentBuild.getVersion());
				if ( version != null )
				{
				    choice.addAvailableVersion(version);

				    /** add specific version information */
				    plugin.setCheckSumForVersion(version, getCheckSumForType(currentBuild.getCheck()));
				    
				    XMLGregorianCalendar cal = currentBuild.getReleaseDate();
				    plugin.setReleaseDateForVersion(version, (cal == null ? null : cal.toGregorianCalendar()));
				    
				    plugin.setLicenseVersion(version, currentBuild.getLicense());
				    
				    plugin.setRebootableVersion(version, currentBuild.isRebootNeeded());
				}
			    }
			}

			choice.setSelectedVersion(choice.getMostRecentVersion());
		    }
		}

		try
		{
		    plugin.setVersionChoice(choice);
		}
		catch (PropertyVetoException ex)
		{	ex.printStackTrace(); }
	    }
	}
	
	return plugin;
    }
    
    /** return a set of plugin available in a SiberiaRepository
     *  @param repository a SiberiaRepository
     *  @return a set of Plugin that can be downloaded from the given repository
     *
     *	@exception InvalidRepositoryException if the repository is invalid
     */
    public static Set<Plugin> getAvailablePluginsFrom(SiberiaRepository repository) throws InvalidRepositoryException
    {
        logger.info("trying to get available plugins for repository at : " + (repository == null ? null : repository.getURL()));
        Set<Plugin> set = null;
        
        Repository repDecl = getRepositoryDeclaration(repository, null);
	
        if ( repDecl != null )
        {   List<ModuleDeclaration> list = repDecl.getModules().getModuleDeclaration();
            
            logger.info(list.size() + " plugins declared by " + (repository == null ? null : repository.getURL()));
            Iterator<ModuleDeclaration> modules = list.iterator();
            
            while(modules.hasNext())
            {   ModuleDeclaration module = modules.next();
		
		Plugin plugin = createPlugin(module, repository);
		
		if ( plugin != null )
		{
		    if ( set == null )
			set = new HashSet<Plugin>(list.size());

		    set.add(plugin);
		}
            }
        }
        
        if ( set == null )
	{
            set = Collections.EMPTY_SET;
	}
        
        return set;
    }
    
    /** method that return the administrator mail adresse linked to the given repository
     *  @param repository a SiberiaRepository
     *  @return the administrator mail
     *
     *	@exception InvalidRepositoryException if the repository is invalid
     */
    public static String getAdministratorMail(SiberiaRepository repository) throws InvalidRepositoryException
    {
        String mail = null;
        
        Repository rep = getRepositoryDeclaration(repository, null);
        
        if ( rep != null )
        {
            mail = rep.getAdminmail();
        }
        
        return mail;
    }
    
    /** method that return the name linked to the given repository location
     *  @param location a SiberiaRepositoryLocation
     *  @return the name of the given repository
     *
     *	@exception InvalidRepositoryException if the repository is invalid
     *  @throw RepositoryCreationException when error occurred when creating the repository
     */
    public static String getName(SiberiaRepositoryLocation location) throws RepositoryCreationException
    {
	String name = null;
	
	SiberiaRepository repository = null;
	
	try
	{
	    repository = SiberiaRepositoryFactory.create(location.getURL());
	}
	catch (InvalidRepositoryException ex)
	{
	    ex.printStackTrace();
	}
	
	name = getName(repository);
	
	return name;
    }
    
    /** method that return the name linked to the given repository location
     *  @param location a SiberiaRepositoryLocation
     *  @return the name of the given repository
     *
     *	@exception InvalidRepositoryException if the repository is invalid
     *  @throw RepositoryCreationException when error occurred when creating the repository
     */
    public static String getNameWithException(SiberiaRepositoryLocation location) throws InvalidRepositoryException,
								 		         RepositoryCreationException
    {
	String name = null;
	
	SiberiaRepository repository = SiberiaRepositoryFactory.create(location.getURL());
	name = getNameWithException(repository);
	
	return name;
    }
    
    /** method that return the name linked to the given repository
     *  @param repository a SiberiaRepository
     *  @return the name of the given repository
     */
    public static String getName(SiberiaRepository repository)
    {
        String name = null;
        
        Repository rep = getRepositoryDeclaration(repository, null);
        
        if ( rep != null )
        {
            name = rep.getName();
        }
        
        return name;
    }
    
    /** method that return the name linked to the given repository
     *  @param repository a SiberiaRepository
     *  @return the name of the given repository
     *
     *	@exception InvalidRepositoryException if the repository is invalid
     */
    public static String getNameWithException(SiberiaRepository repository) throws InvalidRepositoryException
    {
        String name = null;
        
        Repository rep = getRepositoryDeclarationWithException(repository, null);
        
        if ( rep != null )
        {
            name = rep.getName();
        }
        
        return name;
    }
    
    /** return the Repository declaration of the given SiberiaRepository
     *  @param repository a SiberiaRepository
     *  @param status a TaskStatus
     *  @return an xml Repository or null if the repository does not provide a valid declaration
     *
     *	@exception InvalidRepositoryException if the repository is invalid
     */
    private static Repository getRepositoryDeclarationWithException(SiberiaRepository repository, TaskStatus status) throws InvalidRepositoryException
    {
	Repository rep = getRepositoryDeclaration(repository, status);
	
	if ( rep == null )
	{
	    throw new InvalidRepositoryException( (repository == null ? null : repository.getURL()) );
	}
	
	return rep;
    }
    
    /** return the Repository declaration of the given SiberiaRepository
     *  @param repository a SiberiaRepository
     *  @param status a TaskStatus
     *  @return an xml Repository or null if the repository does not provide a valid declaration
     */
    private static Repository getRepositoryDeclaration(SiberiaRepository repository, TaskStatus status)
    {   Repository repDecl = null;
        
        logger.debug("calling getRepositoryDeclaration with repository " + (repository == null ? null : repository.getURL()));
        
        if ( repository != null )
        {   
            /** perhaps it is in memory ... */
            SoftReference<Repository> ref = repositoryDeclMap.get(repository);
            if ( ref != null )
            {   repDecl = ref.get(); }
            
            if ( repDecl == null )
            {
                String localRepDeclFilename = null;

                boolean downloadedFile = repositoryDeclarations.containsKey(repository);
                
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("declaration file for repository " + repository.getURL() + " already downloaded ? " + downloadedFile);
		}

                if ( downloadedFile )
                {   /** search in the map */
                    localRepDeclFilename = repositoryDeclarations.get(repository);
                }
                else
                {
                    try
                    {
                        File f = repository.copyRepositoryDeclarationToLocalTemp(CheckSum.NONE, status);
                        localRepDeclFilename = f.getAbsolutePath();
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("declaration file to consider for repository " + 
				    (repository == null ? null : repository.getURL()) + " : " + localRepDeclFilename);
			    logger.debug("putting filepath " + localRepDeclFilename + " in cache for repository " + repository.getURL());
			}

                        /** update the map */
			repositoryDeclarations.put(repository, localRepDeclFilename);
                    }
                    catch(Exception e)
                    {   e.printStackTrace(); }
                }
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("local declaration filename : " + localRepDeclFilename);
		}

                if ( localRepDeclFilename != null )
                {   
                    try
                    {   repDecl = new JAXBLoader().loadRepository(new FileInputStream(localRepDeclFilename));
        
                        logger.debug("parsing repository declaration from " + localRepDeclFilename);
                        
                        /** create the softReference and add it to the map */
                        repositoryDeclMap.put(repository, new SoftReference<Repository>(repDecl));

                        if ( suspectRepositories.contains(repository) )
			{
                            suspectRepositories.remove(repository);
			}
                    }
                    catch (Exception ex)
                    {   ex.printStackTrace();
			
			logger.debug("repository " + repository.getURL() + " seems suspucious !!");

                        /* the local file could not be parsed...
                         * if the repository appears suspicious, let's put null as value in the repository's declaration
                         * map. else, remove it from 'repositoryDeclarations', add it in the suspicious list and reload the declaration file from it
                         */
                        if ( suspectRepositories.contains(repository) )
                        {   
			    logger.info("putting filepath null in cache for repository " + repository.getURL());
			    
			    repositoryDeclarations.put(repository, null);

                            /** it is not suspicious, it is simply a repository to avoid */
                            suspectRepositories.remove(repository);
                        }
                        else
                        {   
			    logger.error("removing filepath in cache for repository " + repository.getURL());
			    
			    repositoryDeclarations.remove(repository);
                            suspectRepositories.add(repository);
                            repDecl = getRepositoryDeclaration(repository, null);
                        }
                    }
                }
            }
        }
        
        return repDecl;
    }
    
    /** return the plugin declared by the given repository which has the given name
     *	@param pluginName the name of the plugin
     *	@param repository a SiberiaRepository
     *	@param searchInPhantomsRepositories true to also search the dependency in the declared phantom repositories of <code>repository</code>
     *	@return a Plugin or null if the repository does not declared a plugin named <code>pluginName</code>
     */
    public static Plugin getPluginNamed(String pluginName, SiberiaRepository repository, boolean searchInPhantomsRepositories)
    {
	Plugin plugin = null;
	
	if ( repository != null && pluginName != null )
	{
	    Repository repo = getRepositoryDeclaration(repository, null);
	    
	    if ( repo != null )
	    {
		Iterator<ModuleDeclaration> decls = repo.getModules().getModuleDeclaration().iterator();
		while(decls.hasNext())
		{
		    ModuleDeclaration decl = decls.next();
		    
		    if ( decl != null )
		    {
			if ( pluginName.equals(decl.getName()) )
			{
			    plugin = createPlugin(decl, repository);
			    
			    if ( plugin != null )
			    {
				break;
			    }
			}
		    }
		}
	    }
	}
	
	return plugin;
    }
    
    /**
     * return a Set of direct dependencies for the given PluginBuild
     * 
     * @param build a PluginBuild
     * @return a non modifiable Set of PluginDependency
     * @exception IllegalArgumentException if the given build has no repository
     * @exception PluginBuildVersionNotFoundException when the repository does not declare such version of the repository
     * @exception InvalidBuildDependencyException if the dependency for this build is invalid
     * @exception ResourceNotFoundException if the declaration of the plugin was not found
     * @exception IOException if the declaration of the plugin could not be downloaded
     * @exception JAXBException if the declaration of the plugin could not be parsed
     */
    public static Set<PluginDependency> getBuildDependencies(PluginBuild build) throws PluginBuildVersionNotFoundException,
										       InvalidBuildDependencyException,
										       ResourceNotFoundException,
										       IOException,
										       JAXBException
    {
	Set<PluginDependency> set = null;
	
	if ( build != null )
	{
	    SiberiaRepository repository = build.getRepository();
	    
	    if ( repository == null )
	    {
		throw new IllegalArgumentException("trying to register a new PluginBuild '" + build.getName() + "' without repository");
	    }
	    else
	    {
		Module module = repository.getModuleDeclaration(build, null);
		if ( module != null )
		{
		    List<ModuleBuild> builds = module.getBuild();
		    
		    ModuleBuild myBuildDeclaration = null;
		    
		    if ( builds != null )
		    {
			for(int i = 0; i < builds.size(); i++)
			{
			    ModuleBuild current = builds.get(i);
			    
			    if ( current != null )
			    {
				try
				{   
				    Version v = Version.parse(current.getVersion());
				    
				    if ( v.equals(build.getVersion()) )
				    {
					myBuildDeclaration = current;
					break;
				    }
				    
				}
				catch (Version.VersionFormatException ex)
				{
				    continue;
				}
			    }
			}
		    }
		    
		    if ( myBuildDeclaration == null )
		    {
			throw new PluginBuildVersionNotFoundException(build, repository);
		    }
		    else
		    {
			// get dependencies...
			List<ModuleDependency> dependencies = myBuildDeclaration.getDependency();
			
			if ( dependencies != null )
			{
			    for(int i = 0; i < dependencies.size(); i++)
			    {
				ModuleDependency dependency = dependencies.get(i);
				
				if ( dependency != null )
				{
				    if ( set == null )
				    {
					set = new HashSet<PluginDependency>(dependencies.size());
				    }
				    
				    /** create the a PluginDependency according to dependency */
				    PluginDependency d = new  PluginDependency();
				    
				    try
				    {	
					d.setName(dependency.getName());
					
					d.setVersionConstraint(VersionConstraint.parse(dependency.getVersionConstraint()));
					
					set.add(d);
				    }
				    catch (PropertyVetoException ex)
				    {
					ex.printStackTrace();
				    }
				    catch (VersionConstraint.VersionConstraintFormatException ex)
				    {
					throw new InvalidBuildDependencyException(build, ex);
				    }
				}
			    }
			}
			
		    }
		}
	    }
	}
	
	if ( set == null )
	{
	    set = Collections.emptySet();
	}
	
	return set;
    }
    
    /** license cache */
    private static class LicenseCache extends GenericCache2<SiberiaRepository, Map<String, String>>
    {
        /**
         * method that create an elemet according to the key
         * 
         * @param key the corresponding key
         * @param parameters others parameters
         * @return the object that has been created
         */
        public Map<String, String> create(SiberiaRepository key, Object... parameters)
        {
            Map<String, String> licenseMap = null;
            
            Repository rep = null;
	    try
	    {	
		rep = RepositoryUtilities.getRepositoryDeclarationWithException(key, null);
	    }
	    catch (InvalidRepositoryException ex)
	    {
		ex.printStackTrace();
	    }
	    
//            if ( rep != null )
//            {   Iterator<License> licenses = rep.getLicenses().getLicense().iterator();
//                
//                if ( licenses.hasNext() )
//                {   licenseMap = new HashMap<String, String>();
//                
//                    License current = null;
//                    while(licenses.hasNext())
//                    {   current = licenses.next();
//
//                        if ( current != null )
//                        {   licenseMap.put(current.getName(), current.getValue()); }
//                    }
//                }
//            }
	    
            if ( rep != null && rep.getLicenses() != null )
            {   Iterator<LicenseLink> licenses = rep.getLicenses().getLicenseLink().iterator();
                
                if ( licenses.hasNext() )
                {   licenseMap = new HashMap<String, String>();
                
                    LicenseLink current = null;
                    while(licenses.hasNext())
                    {   current = licenses.next();

                        if ( current != null )
                        {   
			    InputStream stream = null;
			    try
			    {
				/** load the content */
				URL url = new URL(current.getValue());
				
				stream = url.openStream();
				
				int buffersize = 5000;
				try
				{   buffersize = stream.available(); }
				catch (IOException ex)
				{   ex.printStackTrace(); }
				
				StringBuffer buffer = new StringBuffer(buffersize);
				
				byte[] bytes = new byte[1024*1024];
				
				int bytesRead = -1;
				
				while( (bytesRead = stream.read(bytes)) != -1 )
				{
				    String s = new String(bytes, 0, bytesRead);
				    buffer.append(s);
				}

				licenseMap.put(current.getName(), buffer.toString());
			    }
			    catch(Exception e)
			    {
				logger.error("unable to read license on url " + current.getValue(), e);
			    }
			    finally
			    {
				if ( stream != null )
				{
				    try
				    {
					stream.close();
				    }
				    catch (IOException ex)
				    {	ex.printStackTrace(); }
				}
			    }
			}
                    }
                }
            }
            
            return licenseMap;
        }
    }
    
    /** license cache */
    private static class PhantomRepositoriesCache extends GenericCache2<SiberiaRepository, Set<SiberiaRepository>>
    {
        /**
         * method that create an elemet according to the key
         * 
         * @param key the corresponding key
         * @param parameters others parameters
         * @return the object that has benn created
         */
        public Set<SiberiaRepository> create(SiberiaRepository key, Object... parameters)
        {
            Set<SiberiaRepository> phantoms = null;
            
            Repository rep = null;
	    try
	    {	
		rep = RepositoryUtilities.getRepositoryDeclarationWithException(key, null);
	    }
	    catch (InvalidRepositoryException ex)
	    {
		ex.printStackTrace();
	    }
            if ( rep != null )
            {   
		PhantomRepositories pRepositories = rep.getPhantomRepositories();
		
		if ( pRepositories != null )
		{
		    List<RepositoryDecl> decls = pRepositories.getRepositoryDecl();
		    
		    if ( decls != null )
		    {
			Iterator<RepositoryDecl> it = decls.iterator();

			if ( it.hasNext() )
			{   phantoms = new HashSet<SiberiaRepository>();

			    RepositoryDecl current = null;
			    while(it.hasNext())
			    {   current = it.next();

				if ( current != null )
				{   
				    try
				    {   phantoms.add(SiberiaRepositoryFactory.create(current.getName(), current.getValue())); }
				    catch (RepositoryCreationException ex)
				    {   ex.printStackTrace(); }
				}
			    }
			}
		    }
		}
            }
            
            return phantoms;
            
        }
        
    }
    
}
