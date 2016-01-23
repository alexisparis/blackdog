package org.siberia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.registry.DefaultPluginRegistryBuilder;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.interactivity.InputHandler;
import org.java.plugin.registry.ManifestInfo;
import org.java.plugin.registry.ManifestProcessingException;
import org.java.plugin.registry.PluginRegistry;
import org.siberia.env.PluginContext;
import org.siberia.trans.type.RepositoryUtilities;
import org.siberia.trans.type.plugin.PluginConstants;
import org.siberia.trans.xml.JAXBLoader;
import org.siberia.utilities.io.IOUtilities;
import org.siberia.utilities.security.check.CheckMethod;
import org.siberia.utilities.security.check.CheckSum;
import org.siberia.xml.schema.pluginarch.CheckType;
import org.siberia.xml.schema.pluginarch.Module;
import org.siberia.xml.schema.pluginarch.ModuleBuild;
import org.siberia.xml.schema.pluginarch.ModuleCategory;
import org.siberia.xml.schema.pluginarch.ModuleDeclaration;
import org.siberia.xml.schema.pluginarch.ModuleDependency;
import org.siberia.xml.schema.pluginarch.ObjectFactory;
import org.siberia.xml.schema.pluginarch.Repository;

/**
 *
 * install siberia plugin
 *
 * @author alexis
 *
 * @goal install-siberia-plugin
 */
public class InstallSiberiaPluginMojo extends AbstractMojo
{
    private static final String LOCAL_REPOSITORY_NAME = "My local repository";
    private static final String ADMIN_MAIL            = "alexis.paris@wanadoo.fr";
    
    /** configuration for this plugin */
    private static final String CONFIGURATION_FILE    = ".siberia_mvn_conf";
    
    private static final String DEFAULT_LOCAL_REPOSITORY_PATH = System.getProperty ("user.home") + File.separator + "my-local-siberia-repository";
    
    /** name of the property local repository */
    private static final String PROPERTY_LOCAL_REPOSITORY = "localRepository";
    
    /**
     * inputHandler
     * @component
     */
    private InputHandler inputHandler;
    
    /**
     * Projet en cours de deploiement.
     * @parameter expression="${project}"
     */ 
    private MavenProject project;
    
    /** Creates a new instance of InstallSiberiaPluginMojo */
    public InstallSiberiaPluginMojo ()
    {	}
    
    public void execute () throws MojoExecutionException
    {
	
	String            version	      = project.getVersion ();
	String            artifactId	      = project.getArtifactId ();
	String            location            = artifactId;
	String            declarationFileName = "module.xml";
	String            shortDesc           = "unknown";
	String            longDesc	      = "unknown";
	GregorianCalendar release             = new GregorianCalendar();
	String            license             = "GPL";
	ModuleCategory    category            = null;
	CheckType         check               = null;
	boolean           rebootNeeded        = true;
	
	boolean           moduleActif         = true;
	boolean           buildActif          = true;
	
	File projectFile = project.getFile ().getParentFile ();
	
	/** load configuration file */
	File configurationFile = new File(projectFile, CONFIGURATION_FILE);
	
	Properties confProperties = null;
	
	if ( configurationFile.exists () )
	{
	    confProperties = new Properties();
	    
	    try
	    {	confProperties.load (new FileInputStream(configurationFile)); }
	    catch (Exception ex)
	    {	}
	}
	
	if ( confProperties == null )
	{
	    confProperties = new Properties();
	}
	
	/* ask repository path if properties is null or empty */
	if ( confProperties.isEmpty () )
	{
	    getLog ().info ("install-siberia-plugin needs a configuration file named '" + CONFIGURATION_FILE + "' in the folder of the project indicating the local repository path");
	    getLog ().info ("the configuration file does not exists : it will be generated.");
	    getLog ().info ("");
	    getLog ().info ("enter the path to the local repository where you want to install this siberia plugin : ");
	    
	    try
	    {
		String line = inputHandler.readLine();
		if ( confProperties == null )
		{
		    confProperties = new Properties();
		}
		
		if ( line != null && line.trim ().length () > 0 )
		{
		    confProperties.setProperty (PROPERTY_LOCAL_REPOSITORY, line);
		}
	    }
	    catch(Exception e)
	    {	}
	}
	
	if ( confProperties.isEmpty () )
	{
	    confProperties.put (PROPERTY_LOCAL_REPOSITORY, DEFAULT_LOCAL_REPOSITORY_PATH);
	}
	
	if ( ! configurationFile.exists () ) 
	{
	    try
	    {	configurationFile.createNewFile (); }
	    catch (IOException ex)
	    {	throw new MojoExecutionException("error while creating configuration file " + configurationFile, ex); }
	}
	
	try
	{   /* save configuration file */
	    confProperties.save (new FileOutputStream(configurationFile), null);
	}
	catch (FileNotFoundException ex)
	{   throw new MojoExecutionException("error when saving plugin properties in " + configurationFile, ex); }
	
	File pluginManifest = new File(projectFile.getAbsolutePath () + File.separator + "src" +
									File.separator + "main" + 
									File.separator + "resources" + 
									File.separator + "plugin.xml");
	
	getLog ().info ("siberia plugin manifest exists ? " + pluginManifest.exists ());
	
	if ( ! pluginManifest.exists () )
	{
	    throw new MojoExecutionException("siberia plugin plugin manifest does not exists");
	}
	
	ManifestHandler handler = null;
	try
	{   handler = this.handleManifest(pluginManifest); }
	catch (Exception ex)
	{   throw new MojoExecutionException("error while parsing plugin manifest"); }
	
	if ( ! artifactId.equals (handler.getPluginId ()) )
	{   throw new MojoExecutionException("the plugin id is not equals to artifact id of this maven project"); }
	
	if ( ! version.equals (handler.getPluginVersion ()) )
	{   throw new MojoExecutionException("the plugin version is not equals to artifact version of this maven project"); }
	
	File i18nPlugindeclaration = new File(projectFile.getAbsolutePath () + File.separator + "src" +
									       File.separator + "main" +
									       File.separator + "resources" + 
									       File.separator + handler.getPlugini18nDeclaration () + 
									       ".properties");
	
	getLog ().info ("i18n : " + i18nPlugindeclaration);
	
	Properties props = new Properties();
	try
	{   props.load (new FileInputStream(i18nPlugindeclaration)); }
	catch (Exception ex)
	{
	    throw new MojoExecutionException("error while reading i18n plugin properties file", ex);
	}
	
	shortDesc = props.getProperty (PluginContext.I18NPROPERTY_SHORT_DESCRIPTION);
	longDesc  = props.getProperty (PluginContext.I18NPROPERTY_LONG_DESCRIPTION);
	String date = props.getProperty (PluginContext.I18NPROPERTY_GENERATION_DATE);
	DateFormat format = new SimpleDateFormat(PluginContext.GENERATION_DATE_FORMAT);
	Date generationDate = null;
	
	try
	{   generationDate = format.parse(date); }
	catch (ParseException ex)
	{   ex.printStackTrace(); }
	
	release = new GregorianCalendar();
	release.setTime(generationDate);
	license   = props.getProperty (PluginContext.I18NPROPERTY_LICENSE);
	
	if ( handler.getCategory () != null )
	{
	    try
	    {   category = ModuleCategory.valueOf (handler.getCategory ()); }
	    catch(IllegalArgumentException e)
	    {   }

	    if ( category == null )
	    {   try
		{   category = ModuleCategory.valueOf (handler.getCategory ().toUpperCase ()); }
		catch(IllegalArgumentException e)
		{   }
	    }
	}
	
	if ( category == null )
	{   category = ModuleCategory.UNKNOWN; }
	
	if ( handler.getCheckMode () != null )
	{
	    try
	    {   check = CheckType.valueOf (handler.getCheckMode ()); }
	    catch(IllegalArgumentException e)
	    {   }

	    if ( check == null )
	    {   try
		{   check = CheckType.valueOf (handler.getCheckMode ().toUpperCase ()); }
		catch(IllegalArgumentException e)
		{   }
	    }
	}
	
	if ( check == null )
	{
	    check = CheckType.SHA_1;
	}
	
	rebootNeeded = (handler.isRebootNeeded () == null ? true : handler.isRebootNeeded ().booleanValue ());
	
	
//generationDate=10-07-2006
//url=http://perso.wanadoo.fr/alexis.paris
//license=LGPL
//image=blackdog;1::img/siberia_sunset.jpg
//shortDescription=Siberia-jgoodies add the look'n feels created by JGoodies
//description=Siberia-jgoodies add the look'n feels created by JGoodies
	
	
	/** list of dependances */
	List           dependencies        = null;
	
	getLog ().info ("install siberia plugin : " + artifactId + " in version " + version);
	
	File repository = new File(confProperties.getProperty (PROPERTY_LOCAL_REPOSITORY));
	if ( ! repository.exists () )
	{   repository.mkdirs (); }
	
	JAXBLoader jaxbUtil = new JAXBLoader();
	ObjectFactory factory = new ObjectFactory();
	
	/* ########################################
	 * #### update repository declaration #####
	 * ######################################## */
	
	File repositoryDeclaration = new File(repository, "repository.xml");
	
	Repository repDecl = null;
	
	if ( repositoryDeclaration.exists () )
	{
	    try
	    {	repDecl = jaxbUtil.loadRepository (new FileInputStream(repositoryDeclaration)); }
	    catch (Exception ex)
	    {	throw new MojoExecutionException("erreur reading repository declaration", ex); }
	}
	
	boolean saveRepositoryDeclaration = false;
	
	if ( repDecl == null )
	{
	    repDecl = factory.createRepository();
	    repDecl.setName (LOCAL_REPOSITORY_NAME);
	    repDecl.setAdminmail (ADMIN_MAIL);
	    repDecl.setModules (factory.createModules ());
	    saveRepositoryDeclaration = true;
	}
	
	ModuleDeclaration decl = null;
	
	/** if the repository already contains a module named artifactId, then update module caracteristics */
	Iterator<ModuleDeclaration> moduleDecls = repDecl.getModules().getModuleDeclaration ().iterator ();
	while(moduleDecls.hasNext () && decl == null )
	{
	    ModuleDeclaration current = moduleDecls.next ();
	    
	    if ( current.getName ().equals (artifactId) )
	    {
		decl = current;
	    }
	}

	if ( decl != null )
	{
	    /* see if we have to update module declaration */
	    if ( ! decl.getLocation ().getValue ().equals (location) )
	    {
		decl.getLocation ().setValue (location);
		saveRepositoryDeclaration = true;
	    }
	    if ( ! decl.getDeclarationFileName ().getValue ().equals (declarationFileName) )
	    {
		decl.getDeclarationFileName ().setValue (declarationFileName);
		saveRepositoryDeclaration = true;
	    }
	    if ( decl.isActive () != moduleActif )
	    {
		decl.setActive (moduleActif);
		saveRepositoryDeclaration = true;
	    }
	    if ( ! decl.getCategory ().equals (category) )
	    {
		decl.setCategory (category);
		saveRepositoryDeclaration = true;
	    }
	    if ( ! decl.getShortDescription ().equals (shortDesc) )
	    {
		decl.setShortDescription (shortDesc);
		saveRepositoryDeclaration = true;
	    }
	    if ( ! decl.getLongDescription ().equals (longDesc) )
	    {
		decl.setLongDescription (longDesc);
		saveRepositoryDeclaration = true;
	    }
	}
	else
	{
	    decl = factory.createModuleDeclaration ();
	    decl.setActive (moduleActif);
	    decl.setName (artifactId);
	    
	    decl.setLocation (factory.createLocation ());
	    decl.getLocation ().setValue (location);
	    
	    decl.setShortDescription (shortDesc);
	    decl.setLongDescription (longDesc);
	    
	    decl.setDeclarationFileName (factory.createDeclarationFileName ());
	    decl.getDeclarationFileName ().setValue (declarationFileName);
	    
	    decl.setCategory (category);
	    
	    repDecl.getModules().getModuleDeclaration ().add (decl);
	    
	    saveRepositoryDeclaration = true;
	}
	
	if ( saveRepositoryDeclaration )
	{
	    try
	    {	jaxbUtil.saveRepository (repDecl, repositoryDeclaration); }
	    catch(Exception e)
	    {	throw new MojoExecutionException("error when saving repository declaration", e); }
	}
	
	/* ###########################################
	 * #### update module builds declaration #####
	 * ########################################### */
	
	File pluginDir = new File(repository, decl.getLocation ().getValue ());
	
	if ( ! pluginDir.exists () )
	{
	    pluginDir.mkdir ();
	}
	
	File buildDeclaration = new File(pluginDir, decl.getDeclarationFileName ().getValue ());
	
	Module module = null;
	
	if ( buildDeclaration.exists () )
	{
	    try
	    {	module = jaxbUtil.loadModule (new FileInputStream(buildDeclaration)); }
	    catch(Exception e)
	    {	throw new MojoExecutionException("erreur reading module declaration for " + decl.getName (), e); }
	}
	
	boolean saveModuleDeclaration = false;
	
	if ( module == null )
	{
	    module = factory.createModule ();
	    saveModuleDeclaration = true;
	}
	
	ModuleBuild build = null;
	
	/** if the module already declare the build then update build caracteristics */
	Iterator<ModuleBuild> moduleBuilds = module.getBuild ().iterator ();
	while(moduleBuilds.hasNext () && build == null )
	{
	    ModuleBuild current = moduleBuilds.next ();
	    
	    if ( current.getVersion ().equals (version) )
	    {
		build = current;
	    }
	}

	/** build build declaration from beginning */
	if ( build != null )
	{
	    module.getBuild ().remove (build);
	}
	
	build = factory.createModuleBuild ();

	build.setActive (Boolean.TRUE);
	build.setVersion (version);
	build.setLicense (license);
	build.setRebootNeeded (rebootNeeded);
	build.setCheck (check);
	try
	{
	    build.setReleaseDate (javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(release));
	} catch (DatatypeConfigurationException ex)
	{	ex.printStackTrace();
	    throw new MojoExecutionException("error while setting release date '" + DateFormat.getDateInstance ().format (release.getTime ()) + "'", ex);
	}
	
	/** create dependencies */
	Iterator<String> dependenciesIt = handler.getRequirements ().keySet ().iterator ();
	while(dependenciesIt.hasNext ())
	{
	    String currentDependency = dependenciesIt.next ();
	    
	    if ( currentDependency != null )
	    {
		String dependencyVersion = handler.getRequirements ().get(currentDependency);
		
		if ( dependencyVersion != null )
		{
		    ModuleDependency dep = factory.createModuleDependency ();
		    dep.setName (currentDependency);
		    dep.setVersionConstraint (dependencyVersion);
		    
		    build.getDependency ().add (dep);
		}
	    }
	}

	module.getBuild ().add (build);

	saveModuleDeclaration = true;
	
	if ( saveModuleDeclaration )
	{
	    try
	    {	jaxbUtil.saveModule (module, buildDeclaration); }
	    catch(Exception e)
	    {	throw new MojoExecutionException("error when saving module declaration '" + decl.getName () + "'", e); }
	}
	
	/* ########################################################################
	 * #### create version directory and add archive and check file to it #####
	 * ######################################################################## */
	
	File versionDir = new File(pluginDir, version);
	
	if ( versionDir.exists () )
	{
	    IOUtilities.delete (versionDir);
	}
	
	versionDir.mkdir ();
	
	/** add the archive */
	String archiveFile = projectFile.getAbsolutePath () + File.separator + "target" +
			     File.separator + artifactId + "-" + version + ".zip";
	File pluginFile = new File(versionDir, artifactId + "-" + version + PluginConstants.SIBERIA_PLUGIN_EXTENSION);
	
	if ( ! pluginFile.exists () )
	{
	    try
	    {
		pluginFile.createNewFile ();
	    }
	    catch (IOException ex)
	    {
		ex.printStackTrace();
		throw new MojoExecutionException("error while creating file '" + pluginFile + "'", ex);
	    }
	}
	
	getLog ().info ("copying " + archiveFile + " to " + pluginFile);
	try
	{   
	    IOUtilities.copy (new File(archiveFile), pluginFile);
	}
	catch (IOException ex)
	{
	    ex.printStackTrace ();
	    throw new MojoExecutionException("error when copying archive");
	}
	
	/** create check file */
	CheckSum sum = RepositoryUtilities.getCheckSumForType (check);
	CheckMethod method = sum.method ();
	if ( method != null )
	{
	    InputStream stream = null;
	    String hash = null;
	    try
	    {
		stream = new FileInputStream(pluginFile);
		hash = method.getHashSequence (stream);
	    }
	    catch(FileNotFoundException e)
	    {
		throw new MojoExecutionException("could not read " + pluginFile, e);
	    }
	    finally
	    {
		try
		{   stream.close (); }
		catch(IOException e)
		{   throw new MojoExecutionException("error while getting hash sequence of " + pluginFile, e); }
	    }
	    
	    if ( hash != null )
	    {
		File checkFile = new File(versionDir, artifactId + "-" + version + PluginConstants.SIBERIA_PLUGIN_EXTENSION + sum.extension ());

		if ( ! checkFile.exists () )
		{
		    try
		    {	checkFile.createNewFile (); }
		    catch (IOException ex)
		    {
			throw new MojoExecutionException("error while creating file " + checkFile, ex);
		    }
		}

		FileWriter writer = null;
		
		try
		{
		    writer = new FileWriter(checkFile);

		    writer.write (hash);
		}
		catch(IOException e)
		{
		    throw new MojoExecutionException("error while creating hash file for plugin", e);
		}
		finally
		{
		    if ( writer != null )
		    {
			try
			{
			    writer.close ();
			}
			catch (IOException ex)
			{
			    ex.printStackTrace();
			}
		    }
		}
	    }
	}
    }
    
    /** return a ManifestHandler that is able to parse the plugin manifest
     *	@param pluginManifest a File representign the plugin manifest
     *	@retrun a ManifestHandler
     */
    private ManifestHandler handleManifest(File pluginManifest) throws Exception
    {
	SAXParserFactory fabrique = SAXParserFactory.newInstance();
	SAXParser parseur = fabrique.newSAXParser();

	ManifestHandler handler = new ManifestHandler();
	parseur.parse(pluginManifest, handler);
	
	return handler;
    }
    
}
