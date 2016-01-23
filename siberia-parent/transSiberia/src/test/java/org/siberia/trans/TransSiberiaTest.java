package org.siberia.trans;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.*;
import org.siberia.trans.exception.FileCheckException;
import org.siberia.trans.exception.InvalidPluginDeclaration;
import org.siberia.trans.exception.ResourceNotFoundException;
import org.siberia.trans.type.plugin.Plugin;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.type.plugin.Version;
import org.siberia.trans.type.repository.DefaultSiberiaRepository;
import org.siberia.trans.type.repository.HttpSiberiaRepository;
import org.siberia.trans.type.repository.SiberiaRepository;
import org.siberia.type.SibList;
//import org.siberia.xml.schema.pluginarch.Application;

/**
 *
 * @author alexis
 */
public class TransSiberiaTest extends TestCase
{   
    /* do test */
    private static final boolean DO_TEST   = false;
    
    /** do test 1 */
    private static final boolean DO_TEST_1 = true && DO_TEST;
    
    /** do test 2 */
    private static final boolean DO_TEST_2 = true && DO_TEST;
    
    /** do test 3 */
    private static final boolean DO_TEST_3 = true && DO_TEST;
    
    public TransSiberiaTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(TransSiberiaTest.class);
        
        return suite;
    }
    
    public void testCreateMessage() throws Exception
    {
	PluginBuild build = new PluginBuild();
	build.setPluginId("siberia-types");
	build.setVersion(Version.parse("1.5.2"));
	HttpSiberiaRepository rep = new HttpSiberiaRepository();
	rep.setURL(new URL("http://www.siberia.org/siberia"));
	
	build.setRepository(rep);
	
	String message = TransSiberia.createMessage("Resource not found for {pluginId}{version} ({version}) on repository {repository} during registration", build);
	
	String result = "Resource not found for siberia-types1.5.2 (1.5.2) on repository http://www.siberia.org/siberia during registration";
	assertEquals(result, message);
    }
    
    public void testInstallRequiredPluginsInLocal() throws Exception
    {
//        new Exception("gjgghghjghjhjghjghjgjgjh").printStackTrace();
//        
//        TransSiberia transport = null;
//        
//        /** plugin names */
//        String[] plugins = new String[]{"bars-0.0.1.sbp"};
//        
//        String propertiesFilename = "src/test/java/transsiberia.properties";
//        String targetLocalRep     = "target/test-classes/org/siberia/localrepository";
//        String ApplPath           = "src/test/java/application";
//        
//        File f = new File(targetLocalRep);
//        if ( ! f.exists() )
//            f.mkdirs();
//        
//        transport = new TransSiberia(targetLocalRep, new File(propertiesFilename));
//        
//        /* load Application declaration */
//        Application application = null;
//        try
//        {   application = JAXBLoader.loadApplication(new File(ApplPath)); }
//        catch(JAXBException e)
//        {   e.printStackTrace(); }
//        
//        transport.installRequiredPluginsInLocal(application);
    }
    
    public void testBack() throws Exception
    {
//        TransSiberia transport = null;
//        
//        /** plugin names */
//        String[] plugins = new String[]{"bars-0.0.1.sbp"};
//        
//        String propertiesFilename = "src/test/java/transsiberia.properties";
//        String srcLocalRep        = "src/test/java/org/siberia/localrepository";
//        String targetLocalRep     = "target/test-classes/org/siberia/localbackuprepository";
//        
//        /** copy the local repository src to target */
//        File targetLocalDir   = new File(targetLocalRep);
//        
//        if ( targetLocalDir.exists() )
//            IOUtilities.delete(targetLocalDir);
//        
//        IOUtilities.copyDirectory(new File(srcLocalRep), targetLocalDir);
//        
//        transport = new TransSiberia(targetLocalRep, new File(propertiesFilename));
//        
//        /** verify that the backup dir contains the required plugin */
//        File current = null;
//        for(int i = 0; i < plugins.length; i++)
//        {   current = new File(transport.getCurrentBackupDirectoryName() + File.separator + plugins[i]);
//            
//            assertTrue("plugin " + plugins[i] + " does not exist in backup", current.exists());
//        }
//        
//        /** verify that local dir contains the required plugin */
//        current = null;
//        for(int i = 0; i < plugins.length; i++)
//        {   current = new File(targetLocalRep + File.separator + plugins[i]);
//            
//            assertTrue("plugin " + plugins[i] + " does not exist in backup", current.exists());
//        }
//        
//        /** cancel installation */
//        transport.cancel();
//        
//        /** verify that the backup dir was deleted */
//        current = new File(transport.getCurrentBackupDirectoryName());
//        assertFalse("cancel does not delete the backup dir", current.exists());
        
    }
    
    /** create and configure a TransSiberia
     *	@return a TransSiberia
     */
    private TransSiberia createTransSiberia(URL urlRepository, String configFilename) throws Exception
    {
	String tmpdir = System.getProperty("java.io.tmpdir");
	
	SiberiaRepository repository = null;
	
	repository = new DefaultSiberiaRepository();
	repository.setURL(urlRepository);
	
	File configFile = new File(tmpdir + File.separator + configFilename);
	
	if ( configFile.exists() )
	{
	    configFile.delete();
	}
	
	configFile.deleteOnExit();
	
	TransSiberia trans = new TransSiberia(tmpdir, configFile, false);
	try
	{
	    trans.addRepository(repository);
	}
	catch(Exception e)
	{   
	    e.printStackTrace();
	}
	
	return trans;
    }
    
    /* #########################################################################
     * ###################### test for repository 1 ############################
     * ######################################################################### */
    
    public void testInstallPluginsFromLocalRepository1() throws Exception
    {
	URL url = null;
	
	File f = new File("src" + File.separator +
		     "test" + File.separator +
		     "java" + File.separator +
		     "org" + File.separator +
		     "siberia" + File.separator +
		     "trans" + File.separator +
		     "repositories" + File.separator +
		     "repository1");
	try
	{
	    url = f.toURI().toURL();
	}
	catch (MalformedURLException ex)
	{
	    throw new AssertionError("could not get url from " + f);
	}
	
	this.testInstallPluginsFromRepository1(url);
    }
    
    public void testInstallPluginsFromHttpRepository1() throws Exception
    {
	URL url = new URL("http://alexis.paris.perso.cegetel.net/test_transsiberia/repository1");
	
	this.testInstallPluginsFromRepository1(url);
    }
    
    public void testInstallPluginsFromHttpRepository1_1() throws Exception
    {
	/** test for the download of some plugins on
	 *  http://alexis.paris.perso.cegetel.net/test_transsiberia/repository1//a/0.0.1/a-0.0.1.sbp
	 *
	 *  // must not provoke errors
	 */
	URL url = new URL("http://alexis.paris.perso.cegetel.net/test_transsiberia/repository1/");
	
	this.testInstallPluginsFromRepository1(url);
    }
    
    public void testInstallPluginsFromRepository1(URL url) throws Exception
    {
	if ( !DO_TEST_1 )
	{
	    return;
	}
	
	TransSiberia trans = this.createTransSiberia(url, "ts-config-test-localrepository1.xml");
	
	SibList list = trans.getAvailablePlugins();
	assertEquals(1, list.size());
	
	/* try to download all of the available plugins */
	Iterator it = list.iterator();
	
	while(it.hasNext())
	{
	    Object current = it.next();
	    
	    if ( current instanceof Plugin )
	    {
		Plugin plugin = (Plugin)current;
		
		if ( "a".equals(plugin.getName()) )
		{
		    /** we have 4 versions */
		    List<Version> v = new ArrayList<Version>(4);
		    v.add(Version.parse("0.0.1"));
		    v.add(Version.parse("0.0.2"));
		    v.add(Version.parse("0.0.3"));
		    v.add(Version.parse("0.0.4"));

		    List<Version> versions = plugin.getVersionChoice().getAvailableVersions();
		    for(int i = 0; i < versions.size(); i++)
		    {
			if ( v.contains(versions.get(i)) )
			{
			    v.remove(versions.get(i));
			}
			
			plugin.getVersionChoice().setSelectedVersion(versions.get(i));
			
			PluginBuild build = plugin.createBuild();
			
			if ( build.getVersion().equals(Version.parse("0.0.1")) )
			{
			    assertEquals(build.getCheckType(), org.siberia.utilities.security.check.CheckSum.NONE);
			}
			else if ( build.getVersion().equals(Version.parse("0.0.2")) )
			{
			    assertEquals(build.getCheckType(), org.siberia.utilities.security.check.CheckSum.MD5_SUM);
			}
			else if ( build.getVersion().equals(Version.parse("0.0.3")) )
			{
			    assertEquals(build.getCheckType(), org.siberia.utilities.security.check.CheckSum.SHA1_SUM);
			}
			else if ( build.getVersion().equals(Version.parse("0.0.4")) )
			{
			    assertEquals(build.getCheckType(), org.siberia.utilities.security.check.CheckSum.MD5_SUM);
			}
			
			/* try to download this build */
			build.getRepository().copyPluginToLocalTemp(build, null);
		    }
		    
		    assertEquals("the versions found for 'a' from repository1 are false", 0, v.size());
		}
	    }
	    else
	    {
		throw new AssertionError();
	    }
	}
	
	assertTrue(true);
    }
    
    /* #########################################################################
     * ###################### test for repository 2 ############################
     * ######################################################################### */
    
    public void testInstallPluginsFromLocalRepository2() throws Exception
    {
	URL url = null;
	
	File f = new File("src" + File.separator +
		     "test" + File.separator +
		     "java" + File.separator +
		     "org" + File.separator +
		     "siberia" + File.separator +
		     "trans" + File.separator +
		     "repositories" + File.separator +
		     "repository2");
	try
	{
	    url = f.toURI().toURL();
	}
	catch (MalformedURLException ex)
	{
	    throw new AssertionError("could not get url from " + f);
	}
	
	this.testInstallPluginsFromRepository2(url);
    }
    
    public void testInstallPluginsFromHttpRepository2() throws Exception
    {
	URL url = new URL("http://alexis.paris.perso.cegetel.net/test_transsiberia/repository2/");
	
	this.testInstallPluginsFromRepository2(url);
    }
    
    public void testInstallPluginsFromRepository2(URL url) throws Exception
    {
	if ( !DO_TEST_2 )
	{
	    return;
	}
	
	TransSiberia trans = this.createTransSiberia(url, "ts-config-test-localrepository2.xml");
	
	SibList list = trans.getAvailablePlugins();
	assertEquals(4, list.size());
	
	/* try to download all of the available plugins */
	Iterator it = list.iterator();
	
	while(it.hasNext())
	{
	    Object current = it.next();
	    
	    if ( current instanceof Plugin )
	    {
		Plugin plugin = (Plugin)current;
		
		if ( "a".equals(plugin.getName()) )
		{
		    List<Version> versions = plugin.getVersionChoice().getAvailableVersions();
		    for(int i = 0; i < versions.size(); i++)
		    {
			Version v = versions.get(i);
			
			plugin.getVersionChoice().setSelectedVersion(v);
			
			Exception e = null;
			
			PluginBuild build = plugin.createBuild();
			try
			{
			    /* try to download this build */
			    build.getRepository().copyPluginToLocalTemp(build, null);
			}
			catch (FileCheckException ex)
			{
			    e = ex;
			    if ( v.getRevision() == 3 )
			    {
				assertTrue(true);
			    }
			    else if ( v.getRevision() == 5 )
			    {
				assertTrue(true);
			    }
			    else
			    {
				assertFalse("for version " + v + " got a " + ex, true);
			    }
			}
			catch (ResourceNotFoundException ex)
			{
			    e = ex;
			    if ( v.getRevision() == 1 )
			    {
				assertTrue(true);
			    }
			    else if ( v.getRevision() == 2 )
			    {
				assertTrue(true);
			    }
			    else if ( v.getRevision() == 4 )
			    {
				assertTrue(true);
			    }
			    else if ( v.getRevision() == 6 )
			    {
				assertTrue(true);
			    }
			    else
			    {
				assertFalse("for version " + v + " got a " + ex, true);
			    }
			}
			catch (InvalidPluginDeclaration ex)
			{
			    assertTrue("this exception " + ex + " could not be thrown for plugin a", false);
			}
			catch (IOException ex)
			{
			    assertTrue("this exception " + ex + " could not be thrown for plugin a", false);
			}
			catch (Exception ex)
			{
			    ex.printStackTrace();
			}
			
			System.err.println("v'got a " + e + " for version " + v);
			
			if ( e == null )
			{
			    assertTrue("no exception thrown when downloading " + plugin.getName() + " with version " + v, false);
			}
		    }
		}
		else if ( "b".equals(plugin.getName()) )
		{
		    assertEquals(plugin.getVersionChoice().getAvailableVersions().size(), 0);
		}
		else if ( "c".equals(plugin.getName()) )
		{
		    // no special test to do but getAvailablePlugins do not have to fail
		}
		else if ( "d".equals(plugin.getName()) )
		{
		    // no special test to do but getAvailablePlugins do not have to fail
		}
	    }
	    else
	    {
		throw new AssertionError();
	    }
	}
	
	assertTrue(true);
    }
    
    /* #########################################################################
     * ###################### test for repository 3 ############################
     * ######################################################################### */
    
    public void testInstallPluginsFromLocalRepository3() throws Exception
    {
	URL url = null;
	
	File f = new File("src" + File.separator +
		     "test" + File.separator +
		     "java" + File.separator +
		     "org" + File.separator +
		     "siberia" + File.separator +
		     "trans" + File.separator +
		     "repositories" + File.separator +
		     "repository3");
	try
	{
	    url = f.toURI().toURL();
	}
	catch (MalformedURLException ex)
	{
	    throw new AssertionError("could not get url from " + f);
	}
	
	this.testInstallPluginsFromRepository3(url);
    }
    
    public void testInstallPluginsFromHttpRepository3() throws Exception
    {
	URL url = new URL("http://alexis.paris.perso.cegetel.net/test_transsiberia/repository3");
	
	if ( !DO_TEST_3 )
	{
	    return;
	}
	this.testInstallPluginsFromRepository3(url);
    }
    
    public void testInstallPluginsFromRepository3(URL url) throws Exception
    {
	if ( !DO_TEST_3 )
	{
	    return;
	}
	this.testInstallPluginsFromRepository1(url);
    }
    
}
