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
package org.siberia.trans;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.bind.JAXBException;
import org.siberia.ResourceLoader;
import org.siberia.env.PluginResources;
import org.siberia.trans.download.DownloadTransaction;
import org.siberia.trans.exception.ConfigurationException;
import org.siberia.trans.exception.FileCheckException;
import org.siberia.trans.exception.InvalidBuildDependencyException;
import org.siberia.trans.exception.InvalidPluginDeclaration;
import org.siberia.trans.exception.InvalidRepositoryException;
import org.siberia.trans.exception.ResourceNotFoundException;
import org.siberia.trans.handler.DefaultDownloadHandler;
import org.siberia.trans.type.RepositoryUtilities;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.type.plugin.PluginDependency;
import org.siberia.trans.type.plugin.VirtualPluginBuild;
import org.siberia.trans.type.repository.SiberiaRepository;
import org.siberia.trans.type.SiberiaRepositoryFactory;
import org.siberia.trans.type.plugin.Plugin;
import org.siberia.trans.type.repository.SiberiaRepositoryLocation;
import org.siberia.trans.type.repository.VirtualSiberiaRepository;
import org.siberia.trans.xml.JAXBLoader;
import org.siberia.type.AbstractSibType;
import org.siberia.type.SibList;
import org.siberia.utilities.io.IOUtilities;
import org.siberia.utilities.math.Matrix;
import org.siberia.utilities.math.graph.GraphUtilities;
import org.siberia.utilities.task.SimpleTaskStatus;
import org.siberia.utilities.task.TaskStatus;
import org.siberia.utilities.task.TaskStatusContainer;
import org.siberia.xml.schema.pluginarch.ObjectFactory;
import org.siberia.xml.schema.pluginarch.Repositories;
import org.siberia.xml.schema.pluginarch.UrlRepository;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.siberia.trans.exception.DownloadAbortedException;
import org.siberia.trans.handler.DownloadHandler;
import org.siberia.trans.type.plugin.Version;
import org.siberia.trans.type.plugin.VersionChoice;

/**
 *
 * Entity that copy all the needed siberia plugin in a given local directory<br/>
 * And which can install a plugin desired by the user
 *
 * @author alexis
 */
public class TransSiberia extends AbstractSibType
{
    /* prefixe of the name of the backup dir */
    private static final String BACKUP_DIR_NAME_PREFIX = "backup-";
    
    /** name of the file that declare the configuration of TransSiberia */
    private static final String TRANSSIBERIA_CONFIG_FILENAME = "ts-config.properties";
    
    /** logger */
    private static Logger logger = Logger.getLogger(TransSiberia.class);
    
    /** path to the local plugin directory wheer downloaded plugin will be placed */
    private String                  pluginLocalDirectory = null;
    
    /** ref to the properties file */
    private File                    serverConfigurationFile = null;
    
    /** set containing already declared siberia plugin repositories */
    private SibList                 remoteRepositories      = null;
    
    /** backup is activated ?? */
    private boolean                 backupActivated         = true;
    
    /** path of the directory where backup was placed */
    private String                  backupDirPath           = null;
    
    /** filter for the local repository which only list File different that last back up directory */
    private FileFilter              localRepFileFilter      = null;
    
    /** indicate if the first try to parse configuration file was successful */
    private boolean                 configurationOk         = true;
    
    /** installed builds */
    private List<PluginBuild>       installedBuild          = null;
    
    /** update all url */
    private URL                     updateAllUrl            = null;
    
    /** general Properties of TransSiberia */
    private Properties              generalProperties       = null;
    
    /**
     * Creates a new instance of TransSiberia
     * 
     * @param pluginLocalDir the local directory containing plugins
     * @param serverConfigurationFile the file declaring TransSiberia configuration
     */
    public TransSiberia(String pluginLocalDir, File serverConfigurationFile)
    {
	this(pluginLocalDir, serverConfigurationFile, null);
    }
    
    /**
     * Creates a new instance of TransSiberia
     * 
     * @param pluginLocalDir the local directory containing plugins
     * @param serverConfigurationFile the file declaring TransSiberia configuration
     */
    public TransSiberia(String pluginLocalDir, File serverConfigurationFile, URL updateAllUrl)
    {
	this(pluginLocalDir, serverConfigurationFile, true);
	
	this.updateAllUrl = updateAllUrl;
    }
    
    /**
     * Creates a new instance of TransSiberia
     * 
     * @param pluginLocalDir the local directory containing plugins
     * @param serverConfigurationFile the file declaring TransSiberia configuration
     * @param useDefaultRepositories true to indicate that if the configuration file is missing, then 
     * 		the configuration file have to be generated with the default repositories
     */
    TransSiberia(String pluginLocalDir, File serverConfigurationFile, boolean useDefaultRepositories)
    {   if ( pluginLocalDir == null )
            throw new IllegalArgumentException("local plugins repository not set");
        if ( serverConfigurationFile == null )
            throw new IllegalArgumentException("TransSiberia properties file does not exists");
        
        if ( ! serverConfigurationFile.exists() )
        {   try
            {   new org.siberia.trans.xml.JAXBLoader().createDefaultRepositories(serverConfigurationFile, useDefaultRepositories); }
            catch(Exception e)
            {   e.printStackTrace(); }
        }
        
        this.pluginLocalDirectory    = pluginLocalDir;
        this.serverConfigurationFile = serverConfigurationFile;
    }
    
    /** return a Properties object that contains general properties of TransSiberia
     *	@return a Properties
     */
    private synchronized Properties getGeneralProperties()
    {
	if ( this.generalProperties == null )
	{
	    this.generalProperties = new Properties();
	    
	    InputStream stream = null;
	    
	    try
	    {
		File f = new File(ResourceLoader.getInstance().getApplicationHomeDirectory() + System.getProperty("file.separator") +
				  TRANSSIBERIA_CONFIG_FILENAME);

		if ( ! f.exists() )
		{
		    File parent = f.getParentFile();

		    if ( ! parent.exists() )
		    {
			parent.mkdirs();
		    }

		    f.createNewFile();
		}
		
		stream = new FileInputStream(f);
		this.generalProperties.load(stream);
	    }
	    catch(IOException e)
	    {
		logger.error("could not load general transsiberia config file", e);
	    }
	    finally
	    {
		if ( stream != null )
		{
		    try
		    {
			stream.close();
		    }
		    catch(Exception e)
		    {
			logger.error("", e);
		    }
		}
	    }
	}
	
	return this.generalProperties;
    }
    
    /** save general TransSiberia properties */
    private synchronized void saveGeneralProperties()
    {
	Properties props = this.getGeneralProperties();
	
	if ( props != null )
	{
	    OutputStream stream = null;
	    
	    try
	    {
		stream = new FileOutputStream(ResourceLoader.getInstance().getApplicationHomeDirectory() + System.getProperty("file.separator") +
					      TRANSSIBERIA_CONFIG_FILENAME);
		
		props.store(stream, "");
	    }
	    catch(IOException e)
	    {
		logger.error("unable to store general properties", e);
	    }
	    finally
	    {
		if ( stream != null )
		{
		    try
		    {
			stream.close();
		    }
		    catch(Exception e)
		    {
			logger.error("", e);
		    }
		}
	    }
	}
    }
    
    /** return the Date when the last update-all occurs
     *	@return a Date
     */
    public Date getLastUpdateAllDate()
    {
	Date date = null;
	
	try
	{
	    Properties props = this.getGeneralProperties();

	    String value = props.getProperty("last-update-all");

	    if ( value != null )
	    {
		date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US).parse(value);
	    }
	}
	catch(ParseException e)
	{
	    logger.error("unable to get last update all date", e);
	}
	
	return date;
    }
    
    /** set the Date when the last update-all occurs
     *	@param date a date
     */
    public void setLastUpdateAllDate(Date date)
    {
	try
	{
	    Properties props = this.getGeneralProperties();

	    props.setProperty("last-update-all", date == null ? null : 
								DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US).format(date));
	    this.saveGeneralProperties();
	}
	catch(Exception e)
	{
	    logger.error("unable to get last update all date", e);
	}
    }
    
    /** set the list of installed plugin builds
     *	@param list a List of PluginBuild
     */
    public void setInstalledBuilds(List<PluginBuild> list)
    {
	this.installedBuild = list;
    }
    
    /** get the list of installed plugin builds
     *	@return a List of PluginBuild
     */
    public List<PluginBuild> getInstalledBuilds()
    {
	return this.installedBuild;
    }
    
    /** return the version of the installed plugin identified by id
     *	@param id the name of the plugin
     *	@return a Version or null if the plugin is not installed
     */
    public Version getInstalledVersionOfPlugin(String id)
    {
	Version v = null;
	
	if ( id != null && this.getInstalledBuilds() != null )
	{
	    for(int i = 0; i < this.getInstalledBuilds().size(); i++)
	    {
		PluginBuild build = this.getInstalledBuilds().get(i);
		
		if ( build != null && id.equals(build.getPluginId()) )
		{
		    v = build.getVersion();
		    break;
		}
	    }
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getInstalledVersionOfPlugin(" + id + ") returns " + v);
	}
	
	return v;
    }
    
    /** return the path to the current backup directory
     *  @return the path to the current backup directory
     */
    public String getCurrentBackupDirectoryName()
    {   String name = null;
        if ( this.backupActivated )
            name = this.backupDirPath;
        return name;
    }
    
    /** configure TransSiberia */
    private void configure() throws ConfigurationException
    {   
        Repositories repositories = null;
        try
        {   repositories = new JAXBLoader().loadRepositories(new FileInputStream(this.serverConfigurationFile)); }
        catch(Exception e)
        {   e.printStackTrace();
            
            if ( this.configurationOk )
            {   this.configurationOk = false;
                try
                {   new org.siberia.trans.xml.JAXBLoader().createDefaultRepositories(this.serverConfigurationFile); }
                catch(Exception ex)
                {   ex.printStackTrace(); }
            }
            else
                throw new ConfigurationException();
        }

        if ( repositories == null )
	{
            throw new ConfigurationException();
	}
	
	this.backupActivated = repositories.isAutomaticLocalBackUp();
	
        
        Iterator<UrlRepository> it = repositories.getUrlRepository().iterator();
        int repoCount = 0;
        SiberiaRepositoryFactory factory = null;
	
	if ( this.remoteRepositories == null )
	{   this.remoteRepositories = new SibList();
	    try
	    {   this.remoteRepositories.setName("repositories"); }
	    catch(PropertyVetoException e)
	    {   e.printStackTrace(); }

	    this.remoteRepositories.setAllowedClass(SiberiaRepository.class);
	}
	
        
        while(it.hasNext())
        {   UrlRepository current = it.next();
            
            if ( current != null )
            {   
                if ( factory == null )
                {
                    factory = new SiberiaRepositoryFactory();
                }

                SiberiaRepository newRepository = null;
                try
                {   newRepository = factory.create(current.getValue());
                    
                    this.remoteRepositories.add(newRepository);
                    repoCount ++;
                }
                catch (Exception ex)
                {   ex.printStackTrace(); }
            }
        }
        
        factory = null;
    }
    
    /* #########################################################################
     * ####################### Repository management ###########################
     * ######################################################################### */
    
    /** return a SibList of SiberiaRepositoryLocation
     *	the list returned is build according to the declared repositories
     *	it is used to visualize the declared repositories and modify them
     *	@param setName true to initialize the name of the repository location
     *  @return a SibList containing SiberiaRepositoryLocation
     *
     *	@exception ConfigurationException if the repositories could not be initialized
     */
    public SibList getRepositoryLocations(boolean setName) throws ConfigurationException
    {   
	SibList locations = new SibList();
	locations.setAllowedClass(SiberiaRepositoryLocation.class);
	
	SibList repositories = this.getRepositories();
	
	if ( repositories != null )
	{
	    synchronized(repositories)
	    {
		locations.setPreferredCapacity(repositories.size());
		
		for(int i = 0; i < repositories.size(); i++)
		{
		    Object o = repositories.get(i);
		    
		    if ( o instanceof SiberiaRepository )
		    {
			locations.add( RepositoryUtilities.createLocation( (SiberiaRepository)o, setName ) );
		    }
		}
	    }
	}

	/** post prepare the list */	
	try
	{
	    locations.setRemovable(false);
	    locations.setRemoveAuthorization(true);
	    locations.setCreateAuthorization(true);
	    locations.setEditAuthorization(true);
	    locations.setMoveable(false);
	    locations.setAcceptSubClassesItem(false);
	    locations.setConfigurable(false);
	}
	catch (PropertyVetoException ex)
	{   ex.printStackTrace(); }
	
	return locations;
    }
    
    /** set the location of the repositories
     *	@param locations a SibList containing SiberiaRepositoryLocation
     *
     *	@exception JAXBException, IOException
     */
    public synchronized void setRepositoryLocations(SibList locations) throws JAXBException, IOException
    {
	
	/** reinitialize the list of repositories if created to force reconfiguration next time */
	this.remoteRepositories = null;
	
	/** overwrite the ts configuration with the given locations */
	ObjectFactory factory = new ObjectFactory();
	
	Repositories repositories = factory.createRepositories();
	
	if ( locations != null && locations.size() > 0 )
	{
	    for(int i = 0; i < locations.size(); i++)
	    {
		Object current = locations.get(i);
		
		if ( current instanceof SiberiaRepositoryLocation )
		{
		    URL url = ((SiberiaRepositoryLocation)current).getURL();
		    
		    String urlToString = (url == null ? "" : url.toString());
		    
		    if ( urlToString.trim().length() > 0 )
		    {
			UrlRepository repoDecl = factory.createUrlRepository();
			repoDecl.setValue(urlToString);
			
			repositories.getUrlRepository().add(repoDecl);
		    }
		}
	    }
	}
		
	new JAXBLoader().saveRepositories(repositories, this.serverConfigurationFile);
    }
    
    /** return a SibList of SiberiaRepository
     *  @return a SibList containing SiberiaRepository
     *
     *	@exception ConfigurationException if the repositories could not be initialized
     */
    public synchronized SibList getRepositories() throws ConfigurationException
    {   
	if ( this.remoteRepositories == null )
	{
	    this.configure();
	}
	
	return this.remoteRepositories;
    }
    
    /** add a new SiberiaRepository
     *  @param repository a SiberiaRepository
     *	@return true if the repository was successfully added
     *
     *	@exception JAXBException 
     *	@exception IOException
     *	@exception ConfigurationException if the repositories could not be initialized
     */
    public boolean addRepository(SiberiaRepository repository) throws JAXBException, IOException, ConfigurationException
    {   
	boolean result = false;
	
	if ( this.remoteRepositories == null )
	{
	    this.configure();
	}
	
	if ( repository != null)
        {   
	    if ( this.remoteRepositories.add(repository) )
	    {
		JAXBLoader jaxbloader = new JAXBLoader();
		
		URL url = repository.getURL();
		
		Exception e = null;
				
		try
		{
		    Repositories reps = jaxbloader.loadRepositories(new FileInputStream(this.serverConfigurationFile));
		    
		    /** search if the repository already exists */
		    String urlToString = (url == null ? null : url.toString());
		    boolean found = false;
                    for(int i = 0; i < reps.getUrlRepository().size(); i++)
                    {   
                        UrlRepository decl = reps.getUrlRepository().get(i);

                        if ( decl.getValue().equals(urlToString) )
                        {   
			    found = true;
                            break;
                        }
                    }

		    if ( ! found )
		    {
			UrlRepository decl = new ObjectFactory().createUrlRepository();

			decl.setValue(url.toString());

			reps.getUrlRepository().add(decl);

			jaxbloader.saveRepositories(reps, this.serverConfigurationFile);
		    }

		    result = true;
		}
		catch (Exception ex)
		{
		    e = ex;
		    
		    logger.error("unable to add repository '" + url, ex);
		    
		    /* if failed, remove the repository previously added */
		    this.remoteRepositories.remove(repository);
		}
		
		if ( e != null )
		{
		    if ( e instanceof JAXBException )
		    {
			throw (JAXBException)e;
		    }
		    else if ( e instanceof IOException )
		    {
			throw (IOException)e;
		    }
		}
	    }
        }
	
	return result;
    }
    
    /** remove a new SiberiaRepository
     *  @param url an url
     *	@return true if the repository was successfully added
     *
     *	@exception JAXBException 
     *	@exception IOException
     *	@exception ConfigurationException if the repositories could not be initialized
     */
    public boolean removeRepository(URL url) throws JAXBException, IOException, ConfigurationException
    {   
	boolean result = false;
	
	if ( this.remoteRepositories == null )
	{
	    this.configure();
	}
	
	if ( url != null )
        {   
	    SiberiaRepository repositoryToRemove = null;
	    
            if ( this.remoteRepositories != null )
            {   Iterator<SiberiaRepository> it = (Iterator<SiberiaRepository>)this.remoteRepositories.iterator();

                while(it.hasNext())
                {   SiberiaRepository repository = it.next();

                    if ( repository != null )
                    {   if ( url.equals(repository.getURL()) )
                        {   repositoryToRemove = repository;
                            break;
                        }
                    }
                }
		
		if ( repositoryToRemove != null )
		{
		    /** if deletion failed, do not update xml declaration */
		    if ( ! this.remoteRepositories.remove(repositoryToRemove) )
		    {
			repositoryToRemove = null;
		    }
		}
            }
            
            if ( repositoryToRemove != null )
            {   
                String urlToString = url.toString();
		
		Exception e = null;
                
                try
                {
                    JAXBLoader jaxbloader = new JAXBLoader();
                    Repositories reps = jaxbloader.loadRepositories(new FileInputStream(this.serverConfigurationFile));

		    boolean found = false;
                    for(int i = 0; i < reps.getUrlRepository().size(); i++)
                    {   
                        UrlRepository decl = reps.getUrlRepository().get(i);

                        if ( decl.getValue().equals(urlToString) )
                        {   reps.getUrlRepository().remove(i);
			    found = true;
                            break;
                        }
                    }

		    /** if the repository was not found, then no need to update configuration file */
		    if ( found )
		    {
			jaxbloader.saveRepositories(reps, this.serverConfigurationFile);
		    }
		    
		    result = true;
                }
                catch(Exception ex)
                {   
		    e = ex;
		    
		    logger.error("unable to remove repository '" + repositoryToRemove.getURL() + "'", ex);
		    
		    this.remoteRepositories.add(repositoryToRemove);
		}
		
		if ( e != null )
		{
		    if ( e instanceof JAXBException )
		    {
			throw (JAXBException)e;
		    }
		    else if ( e instanceof IOException )
		    {
			throw (IOException)e;
		    }
		}
            }
        }
	
	return result;
    }
    
    /* #########################################################################
     * ######################## Plugins installation ###########################
     * ######################################################################### */
    
    /** return a list of Plugin available for declared repositories
     *  @return a SibList containing Plugin
     *
     *	@exception InvalidRepositoryException
     */
    public SibList getAvailablePlugins() throws InvalidRepositoryException
    {   return this.getAvailablePlugins((Comparator)null); }
    
    /** return a list of Plugin available for declared repositories
     *  @param comparator a Comparator over Plugin
     *  @return a SibList containing Plugin
     *
     *	@exception InvalidRepositoryException
     */
    public SibList getAvailablePlugins(Comparator<Plugin> comparator) throws InvalidRepositoryException
    {
	return this.getAvailablePlugins( this.remoteRepositories == null ? Collections.EMPTY_LIST.iterator() : this.remoteRepositories.iterator(),
			                 comparator);
    }
    
    /** return a list of Plugin available for the given repository
     *	@param repository a SiberiaRepository
     *  @param comparator a Comparator over Plugin
     *  @return a SibList containing Plugin
     *
     *	@exception InvalidRepositoryException
     */
    public SibList getAvailablePlugins(final SiberiaRepository repository) throws InvalidRepositoryException
    {
	return this.getAvailablePlugins(repository, null);
    }
    
    /** return a list of Plugin available for the given repository
     *	@param repository a SiberiaRepository
     *  @param comparator a Comparator over Plugin
     *  @return a SibList containing Plugin
     *
     *	@exception InvalidRepositoryException
     */
    public SibList getAvailablePlugins(final SiberiaRepository repository, Comparator<Plugin> comparator) throws InvalidRepositoryException
    {
	return this.getAvailablePlugins(new Iterator<SiberiaRepository>()
	{
	    boolean consumed = false;
	    
	    public boolean hasNext()
	    {
		return ! this.consumed;
	    }
	    public SiberiaRepository next()
	    {
		if ( this.consumed )
		{
		    throw new NoSuchElementException();
		}
		else
		{
		    this.consumed = true;
		    return repository;
		}
	    }
	    
	    public void remove()
	    {
		throw new UnsupportedOperationException("remove cannot be applied to this iterator");
	    }
	}, comparator);
    }
    
    /** return a list of Plugin available
     *	@param repositories an Iterator over SiberiaRepository
     *  @param comparator a Comparator over Plugin
     *  @return a SibList containing Plugin
     *
     *	@exception InvalidRepositoryException
     */
    public SibList getAvailablePlugins(Iterator<SiberiaRepository> repositories, Comparator<Plugin> comparator) throws InvalidRepositoryException
    {   
        SibList list = new SibList();
        list.setAllowedClass(Plugin.class);
        
        if ( repositories != null )
        {
            while(repositories.hasNext())
            {   SiberiaRepository current = repositories.next();

                if ( current != null )
                {   
		    list.addAll(RepositoryUtilities.getAvailablePluginsFrom(current));
		}
            }

            /** sort the list */
            Comparator<Plugin> comp = comparator;

            if ( comp == null )
            {
                comp = new Comparator<Plugin>()
                {   
                    public int compare(Plugin o1, Plugin o2)
                    {   int result = 0;
                        if ( o1 == null )
                        {   if ( o2 != null )
                            {   result = -1; }
                        }
                        else
                        {   if ( o2 != null )
                            {   result = o1.getName().compareTo(o2.getName()); }
                        }
                        return result;
                    }
                };
            }
            Collections.sort(list, comp);
        }
        
        return list;
    }
    
    /** download given builds with the default DownloadHandler
     *	@param transaction a DownloadTransaction to commit at the end of all download process
     *	@param builds an array of PluginBuild to download
     *
     *	@exception DownloadAbortedException if the download has been aborted
     */
    public void downloadBuilds(DownloadTransaction transaction, PluginBuild... builds) throws DownloadAbortedException,
							     ResourceNotFoundException,
							     FileCheckException,
							     InvalidPluginDeclaration,
							     InvalidBuildDependencyException,
							     InvalidRepositoryException,
							     JAXBException,
							     IOException
    {
	this.downloadBuilds(transaction, new DefaultDownloadHandler(), builds);
    }
    
    /** download given builds
     *	@param transaction a DownloadTransaction to commit at the end of all download process
     *	@param handler a DownloadHandler that is able to modify TransSiberia behaviour
     *	@param builds an array of PluginBuild to download
     *
     *	@return an ExecutorService that is able to launch the download of all builds
     *
     *	@exception IllegalArgumentException if the handler provided is null
     *	@exception DownloadAbortedException if the download has been aborted
     */
    public void downloadBuilds(final DownloadTransaction transaction, final DownloadHandler handler, final PluginBuild... builds) throws DownloadAbortedException,
										      ResourceNotFoundException,
							     			      FileCheckException,
							     			      InvalidPluginDeclaration,
							     			      InvalidBuildDependencyException,
							     			      InvalidRepositoryException,
							     			      JAXBException,
							     			      IOException
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling downloadBuilds(" + transaction + ", " + handler + ", ..) for " + (builds == null ? 0 : builds.length) + builds);
	}
	
	if ( handler == null )
	{
	    throw new IllegalArgumentException("provide an handler");
	}
	
	final ResourceBundle rb = ResourceBundle.getBundle(TransSiberia.class.getName());
	
	if ( builds != null )
	{   
	    try
	    {
		handler.pluginRegistrationBeginned();
		
		for(int i = 0; i < builds.length; i++)
		{
		    PluginBuild currentBuild = builds[i];
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("current build to download is " + currentBuild);
		    }

		    boolean tryAgain = false;

		    if ( currentBuild != null )
		    {
			SiberiaRepository repository = currentBuild.getRepository();

			if ( logger.isDebugEnabled() )
			{
			    logger.debug("repository of " + currentBuild + " is " + repository);
			}
			try
			{
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("before asking repository to regsiter build " + currentBuild);
			    }
			    
			    transaction.getPluginGraph().registerBuild(currentBuild);
			    
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("after asking repository to regsiter build " + currentBuild);
			    }
			}
			catch (ResourceNotFoundException ex)
			{
			    tryAgain = handleErrorOnRegistration(handler, transaction.getPluginGraph(), currentBuild, rb, "regResourceNotFoundMsg", ex);
			}
			catch (InvalidBuildDependencyException ex)
			{
			    tryAgain = handleErrorOnRegistration(handler, transaction.getPluginGraph(), currentBuild, rb, "regInvalidBuildDependencyMsg", ex);
			}
			catch (IOException ex)
			{
			    tryAgain = handleErrorOnRegistration(handler, transaction.getPluginGraph(), currentBuild, rb, "regIOMsg", ex);
			}
			catch (JAXBException ex)
			{
			    tryAgain = handleErrorOnRegistration(handler, transaction.getPluginGraph(), currentBuild, rb, "regJAXBMsg", ex);
			}
			catch (InvalidRepositoryException ex)
			{
			    tryAgain = handleErrorOnRegistration(handler, transaction.getPluginGraph(), currentBuild, rb, "regInvalidRepositoryMsg", ex);
			}
			catch (Exception ex)
			{
			    logger.error("error while registering build " + currentBuild, ex);
			}
		    }
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("should try again ? " + tryAgain);
		    }

		    if ( tryAgain )
		    {
			i--;
		    }
		}
	    }
	    finally
	    {
		handler.pluginRegistrationEnded();
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("plugin registration ended");
		}
	    }
	    
	    handler.setNumberOfBuildsToDownload(transaction.getPluginGraph().getBuildCount());
	    
	    Matrix matrix = transaction.getPluginGraph().createAdjacenceMatrix();
	    
	    boolean containsCycle = GraphUtilities.containsCycle(matrix);
	    
	    logger.info("contains cycle ? " + containsCycle);
	    
	    if ( containsCycle )
	    {
		
	    }
	    else
	    {
		List<TaskStatus>    allStatus    = new ArrayList<TaskStatus>(transaction.getPluginGraph().getBuildCount());
		
		TaskStatusContainer globalStatus = new TaskStatusContainer();
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("plugin graph build count : " + transaction.getPluginGraph().getBuildCount());
		}
		
		for(int i = 0; i < transaction.getPluginGraph().getBuildCount(); i++)
		{
		    PluginBuild build = transaction.getPluginGraph().getBuildAt(i);
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("registration of download process for " + build);
		    }
		    
		    TaskStatus status = new SimpleTaskStatus();
		    
		    int buildSize = build.getRepository().getBuildSize(build);
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug(build.getPluginId() + " " + build.getVersion() + " build size : " + buildSize);
		    }
		    
		    globalStatus.append(status, Math.min(buildSize, 1000));
		    
		    boolean licenseAccepted = handler.confirmLicense(this, build);
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("license accepted for " + build.getPluginId() + " " + build.getVersion() + " ? " + licenseAccepted);
		    }
		    
		    build.setLicenseAccepted(licenseAccepted);
		    
		    allStatus.add(status);
		}
		
		handler.setGlobalTaskStatus(globalStatus);
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("plugin graph build count : " + transaction.getPluginGraph().getBuildCount());
		}
		
		for(int i = 0; i < transaction.getPluginGraph().getBuildCount(); i++)
		{
		    final PluginBuild build = transaction.getPluginGraph().getBuildAt(i);
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("current build : " + build);
		    }
		    
		    final boolean hasNext = i < transaction.getPluginGraph().getBuildCount() - 1;
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("has next ? " + hasNext);
		    }
		    
		    boolean tryAgain = false;
		    
		    if ( build != null )
		    {
			File f = null;
			
			final TaskStatus status = allStatus.get(i);
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("related task status : " + status);
			}
			
			Callable<File> callable = new Callable<File>()
			{
			    public File call() throws Exception
			    {
				File f = null;
				try
				{
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("before beginning download of " + build);
				    }

				    handler.buildDownloadBegan(build, status);

				    f = build.getRepository().copyPluginToLocalTemp(build, status);

				    handler.buildDownloadFinished(build, status, hasNext);

				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("build " + build + " copied in " + f);
				    }
				}
				catch (ResourceNotFoundException ex)
				{
				    boolean tryAgain = handleErrorOnDownload(handler, transaction.getPluginGraph(), build, rb, "doResourceNotFoundMsg", ex);
				}
				catch (FileCheckException ex)
				{
				    boolean tryAgain = handleErrorOnDownload(handler, transaction.getPluginGraph(), build, rb, "doFileCheckMsg", ex);
				}
				catch (InvalidPluginDeclaration ex)
				{
				    boolean tryAgain = handleErrorOnDownload(handler, transaction.getPluginGraph(), build, rb, "doIOMsg", ex);
				}
				catch (IOException ex)
				{
				    boolean tryAgain = handleErrorOnDownload(handler, transaction.getPluginGraph(), build, rb, "doInvalidPluginDeclarationMsg", ex);
				}
				
				return f;
			    }
			};
			
			transaction.appendTask(callable);
		    }
		    
//		    if ( tryAgain )
//		    {
//			i--;
//		    }
		}
	    }
	}
    }
    
    /** handle an error during registration phase
     *	@param handler a DownloadHandler
     *	@param graph the PluginGraph
     *	@param build a PluginBuild
     *	@param rb the ResourceBundle
     *	@param messageCode the code message of the error (related to rb)
     *	@param throwable the throwable which gives the StackTrace
     *
     *	@return true to try again
     *	
     *	@exception DownloadAbortedException if the error provoke the download stop
     */
    private boolean handleErrorOnRegistration(DownloadHandler handler, PluginGraph graph, PluginBuild build, ResourceBundle rb,
					      String messageCode, Throwable throwable) throws DownloadAbortedException
    {
	boolean result = false;
	
	if ( handler != null )
	{
	    result = handler.handleErrorOnRegistration(graph, createMessage(rb, messageCode, build), throwable);
	}
	
	return result;
    }
    
    /** handle an error on download
     *	@param handler a DownloadHandler
     *	@param graph the PluginGraph
     *	@param build a PluginBuild
     *	@param rb the ResourceBundle
     *	@param messageCode the code message of the error (related to rb)
     *	@param throwable the throwable which gives the StackTrace
     *
     *	@return true to try again
     *	
     *	@exception DownloadAbortedException if the error provoke the download stop
     */
    public boolean handleErrorOnDownload(DownloadHandler handler, PluginGraph graph, PluginBuild build, ResourceBundle rb,
					 String messageCode, Throwable throwable) throws DownloadAbortedException
    {
	boolean result = false;
	
	if ( handler != null )
	{
	    result = handler.handleErrorOnDownload(graph, createMessage(rb, messageCode, build), throwable);
	}
	
	return result;
    }
    
    /** create a message according to the context
     *	@param rb a ResourceBundle
     *	@param rbKey the key of message in ResourceBundle
     *	@param build a PluginBuild
     *	@return a message
     */
    static String createMessage(ResourceBundle rb, String rbKey, PluginBuild build)
    {
	String result = null;
	
	if ( rb != null && rbKey != null )
	{
	    result = createMessage(rb.getString(rbKey), build);
	}
	
	return result;
    }
    
    /** create a message according to the context
     *	@param template a message template
     *	@param build a PluginBuild
     *	@return a message
     */
    static String createMessage(String template, PluginBuild build)
    {
	String message = null;
	
	if ( template != null )
	{
	    StringBuffer buffer = new StringBuffer();
	    buffer.append(template);
	    
	    if ( build != null )
	    {
		Map<String, String> substitution = new HashMap<String, String>(3);
		
		substitution.put("{pluginId}", build.getPluginId());
		substitution.put("{version}", (build.getVersion() == null ? "unknown" : build.getVersion().toString()));
		substitution.put("{repository}", (build.getRepository() == null ? "unknown" : build.getRepository().getURL().toString()));
		
		/** substitute information */
		Iterator<Map.Entry<String, String>> entries = substitution.entrySet().iterator();
		while(entries.hasNext())
		{
		    Map.Entry<String, String> entry = entries.next();
		    
		    if ( entry != null )
		    {
			int count = 0;
			int index = -1;
			
			while( (index = buffer.indexOf(entry.getKey())) > -1 && count < 10 )
			{
			    /** delete and insert */
			    buffer.delete(index, index + entry.getKey().length());
			    
			    buffer.insert(index, entry.getValue());
			    
			    count ++;
			}
		    }
		}
	    }
	    
	    message = buffer.toString();
	}
	
	return message;
    }
    
    /** search for a dependency in a given repository
     *	@param dependency a PluginDependency
     *	@param repository the repository where to search for the dependency
     *	@param searchInPhantomsRepositories true to also search the dependency in the declared phantom repositories of <code>repository</code>
     *	@return a Plugin
     */
    public PluginBuild resolveDependency(PluginDependency dependency, SiberiaRepository repository,
				   boolean searchInPhantomsRepositories)
    {
	return this.resolveDependency(dependency, repository, searchInPhantomsRepositories, new DefaultPluginBuildChooser());
    }
    
    /** search for a dependency in a given repository
     *	@param dependency a PluginDependency
     *	@param repository the repository where to search for the dependency
     *	@param searchInPhantomsRepositories true to also search the dependency in the declared phantom repositories of <code>repository</code>
     *	@param chooser a PluginBuildChooser which allow to refuse some build
     *	@return a PluginBuild
     */
    public PluginBuild resolveDependency(PluginDependency dependency, SiberiaRepository repository,
				   boolean searchInPhantomsRepositories, PluginBuildChooser chooser)
    {
	return this.resolveDependency(dependency, repository, searchInPhantomsRepositories, chooser, new HashSet<URL>(5));
    }
    
    /** search for a dependency in a given repository
     *	@param dependency a PluginDependency
     *	@param repository the repository where to search for the dependency
     *	@param searchInPhantomsRepositories true to also search the dependency in the declared phantom repositories of <code>repository</code>
     *	@param chooser a PluginBuildChooser which allow to refuse some build
     *	@param repositoriesUrl a set of URL representing the URL of SiberiaRepository that has already been asked
     *	@return a PluginBuild
     */
    private PluginBuild resolveDependency(PluginDependency dependency, SiberiaRepository repository,
				   boolean searchInPhantomsRepositories, PluginBuildChooser chooser, Set<URL> repositoriesUrl)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("resolving dependency " + (dependency == null ? null : dependency.getName() + " (" + dependency.getVersionConstraint() + ")") +
			 " in repository " + (repository == null ? null : repository.getURL()) + " (search in phantom repositories ? " + searchInPhantomsRepositories + ")");
	}
	
	PluginBuild plugin = null;
	
	if ( repository != null && dependency != null && dependency.getVersionConstraint() != null )
	{
	    /** search in the direct repository first */
	    
	    Plugin candidate = RepositoryUtilities.getPluginNamed(dependency.getName(), repository, searchInPhantomsRepositories);
	    
	    /** check all available version of the plugin with chooser */
	    if ( candidate != null )
	    {
		VersionChoice version = candidate.getVersionChoice();
		
		if ( version != null && version.getAvailableVersions().size() > 0 )
		{
		    List<Version> versions = version.getAvailableVersions();
		    
		    /** sort the list so that the first item will be the most recent version */
		    Collections.sort(versions, new Comparator<Version>()
		    {
			public int compare(Version o1, Version o2)
			{
			    if ( o2 == null )
			    {
				return -1;
			    }
			    else
			    {
				return o2.compareTo(o1);
			    }
			}
		    });
		    
		    for(int i = 0; i < versions.size(); i++)
		    {
			Version current = versions.get(i);
			
			boolean validated = dependency.getVersionConstraint().validate(current);
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("found version " + current + " for dependency " +
				         (dependency == null ? null : (dependency.getName() + " (" + dependency.getVersionConstraint() + ")")) +
					 " validated by constraint ? " + validated);
			}
			
			if ( validated )
			{
			    /* ask chooser */
			    if ( chooser.shouldAcceptBuild(candidate, current, dependency, repository) )
			    {
				candidate.getVersionChoice().setSelectedVersion(current);
				plugin = candidate.createBuild();
				
				if ( plugin != null )
				{
				    break;
				}
			    }
			}
		    }
		    
		}
	    }
	    
	    if ( plugin == null && searchInPhantomsRepositories )
	    {
		repositoriesUrl.add(repository.getURL());
		
		Set<SiberiaRepository> phantoms = RepositoryUtilities.getPhantomRepositoriesFor(repository);
		
		if ( phantoms != null )
		{
		    Iterator<SiberiaRepository> it = phantoms.iterator();
		    
		    while(it.hasNext())
		    {
			SiberiaRepository pRep = it.next();
			
			if ( pRep != null && ! repositoriesUrl.contains(pRep.getURL()) )
			{
			    plugin = this.resolveDependency(dependency, pRep, searchInPhantomsRepositories, chooser, repositoriesUrl);

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
    
    /** load the caracteristics of the update all link in a properties object
     *	@param url an URL
     *	@return a Properties
     */
    private Properties loadUpdateAllCaracteristics(URL url) throws Exception
    {
	Properties props = null;
	
	if ( url == null )
	{
	    logger.warn("no update all url --> no build to download");
	}
	else
	{
	    /** the url must contains a file plugins.properties containing the date of the update and the link 
	     *  on the plugins to download
	     */
	    String file = url.getFile();
	    
	    if ( ! file.endsWith("/") )
	    {
		file += "/";
	    }
	    file += "plugin.properties";
	    
	    InputStream stream = null;
	    
	    try
	    {
		URL u = new URL(url.getProtocol(), url.getHost(), url.getPort(), file);

		stream = u.openStream();

		props = new Properties();
		props.load(stream);
	    }
	    finally
	    {
		if ( stream != null )
		{
		    try
		    {
			stream.close();
		    }
		    catch(Exception e)
		    {
			logger.error("", e);
		    }
		}
	    }
	}
	
	return props;
    }
    
    /** ask if a new version of plugins environment is available
     *	@return true if an update is available from update all link
     */
    public boolean isUpdateAvailableFromUpdateAll()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling isUpdateAvailableFromUpdateAll");
	}
	
	boolean result = false;
	
	Date lastApplicationUpdate = this.getLastUpdateAllDate();
	
	Date releaseDate           = this.getReleaseDateForUpdateAll();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("last application update : " + (lastApplicationUpdate == null ? null : DateFormat.getDateTimeInstance().format(lastApplicationUpdate)));
	    logger.debug("release date : " + (releaseDate == null ? null : DateFormat.getDateTimeInstance().format(releaseDate)));
	}
	
	if ( lastApplicationUpdate == null )
	{
	    if ( this.updateAllUrl != null )
	    {
		result = true;
	    }
	}
	else
	{
	    if ( releaseDate != null )
	    {
		result = lastApplicationUpdate.compareTo(releaseDate) < 0;
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("isUpdateAvailableFromUpdateAll returns " + result);
	}
	
	return result;
    }
    
    /** return the date of release for the current upadet all url
     *	@return a Date
     */
    private Date getReleaseDateForUpdateAll()
    {
	Date date = null;
	
	if ( this.updateAllUrl != null )
	{
	    try
	    {
		Properties props = this.loadUpdateAllCaracteristics(this.updateAllUrl);

		String value = props.getProperty("release");

		if ( value != null )
		{
		    date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US).parse(value);
		}
	    }
	    catch(Exception e)
	    {
		logger.error("could not load date from update all url", e);
	    }
	}
	
	return date;
    }
    
    /** ask TransSiberia to build all PluginBuild declared from update all url declared in the application
     *	@return a List of PluginBuild
     */
    public List<PluginBuild> getAllBuildFromUpdateAll() throws Exception
    {
	return this.getAllBuildFromUpdateAll(this.updateAllUrl);
    }
    
    /** ask TransSiberia to build all PluginBuild declared from update all url
     *	@param url an URL
     *	@return a List of PluginBuild
     */
    public List<PluginBuild> getAllBuildFromUpdateAll(URL url) throws Exception
    {
	List<PluginBuild> builds = null;
	
	if ( url == null )
	{
	    logger.warn("no update all url --> no build to download");
	}
	else
	{
	    Properties properties = this.loadUpdateAllCaracteristics(url);
	    
	    if ( properties != null )
	    {
		/** use a virtual repository */
		VirtualSiberiaRepository repository = new VirtualSiberiaRepository();

		builds = new ArrayList<PluginBuild>();

		Enumeration e = properties.propertyNames();

		while(e.hasMoreElements())
		{
		    Object current = e.nextElement();

		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("update all properties file contains entry : " + current);
		    }

		    if ( current instanceof String )
		    {
			if ( ((String)current).startsWith("plugin.") )
			{
			    String value = properties.getProperty( (String)current );

			    try
			    {   
				/** create a repository according to his entry */
				URL currentUrl = new URL(value);

				PluginBuild newBuild = new VirtualPluginBuild(currentUrl);
				newBuild.setRepository(repository);

				if ( logger.isDebugEnabled() )
				{
				    logger.debug("adding build from " + currentUrl);
				}
				builds.add(newBuild);
			    }
			    catch (MalformedURLException ex)
			    {
				ex.printStackTrace();
			    }
			}
		    }
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("count of builds to download : " + builds.size());
		    }
		}
	    }
	}
	
	return builds;
    }
    
    /* #########################################################################
     * ########################## Backup management ############################
     * ######################################################################### */
    
    /** cancel current installation */
    public void cancel()
    {   if ( this.backupActivated )
	{
            this.restoreLastBackUp();
	}
    }
    
    /** copy the content of the local repository into a backup directory */
    private void createBackUp()
    {
        Calendar cal   = null;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy'-'HHmmss");
        while(true)
        {   cal = new GregorianCalendar();
            final File backupDir = new File(this.pluginLocalDirectory + File.separator +
                                 BACKUP_DIR_NAME_PREFIX + format.format(cal.getTime()));
            if ( ! backupDir.exists() )
            {   backupDir.mkdir();
                
                this.backupDirPath = backupDir.getAbsolutePath();
                
                /* copy all files */
                File localRepDir = new File(this.pluginLocalDirectory);
                
                if ( ! localRepDir.exists() )
                    localRepDir.mkdirs();
                
                try
                {   IOUtilities.copyDirectory(localRepDir, backupDir, this.getLocalRepFileFilter()); }
                catch (IOException ex)
                {   ex.printStackTrace(); }
                
                break;
            }
        }
    }
    
    /** restore the last back up */
    private void restoreLastBackUp()
    {   if ( this.backupDirPath != null )
        {   File backupDir = new File(this.backupDirPath);
            if ( backupDir.exists() )
            {   /* delete all plugins placed in local repository */
                File localRepDir = new File(this.pluginLocalDirectory);
                
                File[] plugins = localRepDir.listFiles(this.getLocalRepFileFilter());
                
                for(int i = 0; i < plugins.length; i++)
                {   File current = plugins[i];
                    current.delete();
                }
                
                /* copy files from the backup to the local repository */
                plugins = backupDir.listFiles(this.getLocalRepFileFilter());
                
                for(int i = 0; i < plugins.length; i++)
                {   File current = plugins[i];
                    
                    File destination = new File(this.pluginLocalDirectory + File.separator + current.getName());
                    current.renameTo(destination);
                }
                
                /* delete the backup dir */
                IOUtilities.delete(backupDir);
            }
        }
    }
    
    /* #########################################################################
     * ########################### Miscellaneous ###############################
     * ######################################################################### */
    
    /** return a FileFilter that only list the File on the local repository other than the last backup directory
     *  @return a FileFilter
     */
    private FileFilter getLocalRepFileFilter()
    {   if ( this.localRepFileFilter == null )
        {   this.localRepFileFilter = new FileFilter()
            {
                public boolean accept(File pathname)
                {   boolean accept = true;
                    if ( backupDirPath != null )
                    {   if ( pathname != null )
                        {   if ( pathname.isDirectory() )
                            {   if ( pathname.getAbsolutePath().startsWith(backupDirPath) )
                                    accept = false;
                            }
                        }
                    }
                    return accept;
                }
            };
        }
        
        return this.localRepFileFilter;
    }
    
    /* #########################################################################
     * ########################### BuildChooser ################################
     * ######################################################################### */
    
    /** define an Object that is able to refuse a build when searching for a dependency */
    public static interface PluginBuildChooser
    {
	/** ask chooser if we can accept this build
	 *  @param plugin the plugin
	 *  @param version the Version to confirm
	 *  @param dependency a PluginDependency
	 *  @param repository the repository where the build come from
	 *  @return true if the build can be accepted
	 */
	public boolean shouldAcceptBuild(Plugin plugin, Version version, PluginDependency dependency, SiberiaRepository repository);
    }
    
    /** define an Object that is able to refuse a build when searching for a dependency */
    public static class DefaultPluginBuildChooser implements PluginBuildChooser
    {
	/** ask chooser if we can accept this build
	 *  @param plugin the plugin
	 *  @param version the Version to confirm
	 *  @param dependency a PluginDependency
	 *  @param repository the repository where the build come from
	 *  @return true if the build can be accepted
	 */
	public boolean shouldAcceptBuild(Plugin plugin, Version version, PluginDependency dependency, SiberiaRepository repository)
	{
	    return true;
	}
    }
}
