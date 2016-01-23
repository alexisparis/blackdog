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
import org.siberia.trans.exception.InvalidBuildDependencyException;
import org.siberia.trans.exception.InvalidPluginDeclaration;
import org.siberia.trans.exception.ResourceNotFoundException;
import org.siberia.trans.exception.UnsatisfiedDependencyException;
import org.siberia.trans.type.plugin.Plugin;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.type.plugin.Version;
import org.siberia.trans.type.repository.DefaultSiberiaRepository;
import org.siberia.trans.type.repository.SiberiaRepository;
import org.siberia.type.SibList;
import org.siberia.utilities.math.Matrix;
import org.siberia.utilities.math.graph.GraphUtilities;
//import org.siberia.xml.schema.pluginarch.Application;

/**
 *
 * @author alexis
 */
public class PluginGraphTest extends TestCase
{   
    /* do test */
    private static final boolean DO_TEST   = false;
    
    /** do test 4 */
    private static final boolean DO_TEST_4 = true && DO_TEST;
    
    /** do test 6 */
    private static final boolean DO_TEST_6 = true && DO_TEST;
    
    /** build a 0.0.1 */
    private PluginBuild a001 = null;
    /** build a 0.0.2 */
    private PluginBuild a002 = null;
    /** build a 0.0.3 */
    private PluginBuild a003 = null;
    /** build a 0.0.4 */
    private PluginBuild a004 = null;
    /** build a 0.0.5 */
    private PluginBuild a005 = null;
    /** build a 0.0.6 */
    private PluginBuild a006 = null;
    
    /** build b 0.0.1 */
    private PluginBuild b001 = null;
    /** build b 0.0.2 */
    private PluginBuild b002 = null;
    
    /** build c 0.0.1 */
    private PluginBuild c001 = null;
    /** build c 0.0.2 */
    private PluginBuild c002 = null;
    
    /** build d 0.0.1 */
    private PluginBuild d001 = null;
    /** build d 0.0.2 */
    private PluginBuild d002 = null;
    
    /** build e 0.0.1 */
    private PluginBuild e001 = null;
    
    public PluginGraphTest(String testName)
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
        TestSuite suite = new TestSuite(PluginGraphTest.class);
        
        return suite;
    }
    
    /** create and configure a TransSiberia
     *	@return a TransSiberia
     */
    private TransSiberia createTransSiberia(URL urlRepository, String configFilename) throws Exception
    {
	TransSiberia trans = null;
	
	if ( urlRepository != null )
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

	    trans = new TransSiberia(tmpdir, configFile, false);
	    try
	    {
		trans.addRepository(repository);
	    }
	    catch(Exception e)
	    {   
		e.printStackTrace();
	    }
	}
	
	this.initializeBuildReferences(trans);
	
	return trans;
    }
    
    /** initialize the Build references */
    private void initializeBuildReferences(TransSiberia trans) throws Exception
    {
	if ( trans == null )
	{
	    this.a001 = null;
	    this.a002 = null;
	    this.b001 = null;
	    this.b002 = null;
	}
	else
	{
	    SibList list = trans.getAvailablePlugins();
	    for(int i = 0; i < list.size(); i++)
	    {
		Object item = list.get(i);

		try
		{
		    if ( item instanceof Plugin )
		    {
			if ( "a".equals( ((Plugin)item).getName() ) )
			{
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.1"));
			    this.a001 = ((Plugin)item).createBuild();
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.2"));
			    this.a002 = ((Plugin)item).createBuild();
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.3"));
			    this.a003 = ((Plugin)item).createBuild();
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.4"));
			    this.a004 = ((Plugin)item).createBuild();
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.5"));
			    this.a005 = ((Plugin)item).createBuild();
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.6"));
			    this.a006 = ((Plugin)item).createBuild();
			}
			else if ( "b".equals( ((Plugin)item).getName() ) )
			{
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.1"));
			    this.b001 = ((Plugin)item).createBuild();
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.2"));
			    this.b002 = ((Plugin)item).createBuild();
			}
			else if ( "c".equals( ((Plugin)item).getName() ) )
			{
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.1"));
			    this.c001 = ((Plugin)item).createBuild();
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.2"));
			    this.c002 = ((Plugin)item).createBuild();
			}
			else if ( "d".equals( ((Plugin)item).getName() ) )
			{
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.1"));
			    this.d001 = ((Plugin)item).createBuild();
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.2"));
			    this.d002 = ((Plugin)item).createBuild();
			}
			else if ( "e".equals( ((Plugin)item).getName() ) )
			{
			    ((Plugin)item).getVersionChoice().setSelectedVersion(Version.parse("0.0.1"));
			    this.e001 = ((Plugin)item).createBuild();
			}
		    }
		}
		catch(IllegalArgumentException e)
		{
//		    e.printStackTrace();
		}
	    }
	}
    }
    
    /* #########################################################################
     * #################### test for repository 4 & 5 ##########################
     * ######################################################################### */
    
    public void testInstallPluginsFromLocalRepository4() throws Exception
    {
	URL url = null;
	
	File f = new File("src" + File.separator +
		     "test" + File.separator +
		     "java" + File.separator +
		     "org" + File.separator +
		     "siberia" + File.separator +
		     "trans" + File.separator +
		     "repositories" + File.separator +
		     "repository4");
	try
	{
	    url = f.toURI().toURL();
	}
	catch (MalformedURLException ex)
	{
	    throw new AssertionError("could not get url from " + f);
	}
	
	this.testInstallPluginsFromRepository4(url);
    }
    
    public void testInstallPluginsFromHttpRepository4() throws Exception
    {
	URL url = new URL("http://alexis.paris.perso.cegetel.net/test_transsiberia/repository4");
	
	this.testInstallPluginsFromRepository4(url);
    }
    
    public void testInstallPluginsFromRepository4(URL url) throws Exception
    {
	if ( ! DO_TEST_4 )
	{
	    return;
	}
	
	TransSiberia trans = this.createTransSiberia(url, "ts-config-test-localrepository4.xml");
	PluginGraph graph = new PluginGraph(trans);
	Matrix m = null;
	
	graph.clear();
	graph.registerBuild(this.a001);
	assertEquals(1, graph.getRegisteredBuilds().size());
	m = graph.createAdjacenceMatrix();
	assertEquals(1, m.getRowDimension());
	assertEquals(1, m.getColumnDimension());
	assertEquals(0, m.get(0, 0));
	assertFalse(GraphUtilities.containsCycle(m));
	
	graph.clear();
	graph.registerBuild(this.a002);
	// a 0.0.2 needs only b 0.0.1 but b 0.0.2 exists and needs c 0.0.2
	assertEquals(3, graph.getRegisteredBuilds().size());
	m = graph.createAdjacenceMatrix();
	assertEquals(3, m.getRowDimension());
	assertEquals(3, m.getColumnDimension());
	assertFalse(GraphUtilities.containsCycle(m));
	
	graph.clear();
	graph.registerBuild(this.a003);
	assertEquals(3, graph.getRegisteredBuilds().size());
	m = graph.createAdjacenceMatrix();
	assertEquals(3, m.getRowDimension());
	assertEquals(3, m.getColumnDimension());
	assertFalse(GraphUtilities.containsCycle(m));
	
	graph.clear();
	graph.registerBuild(this.a004);
	assertEquals(3, graph.getRegisteredBuilds().size());
	m = graph.createAdjacenceMatrix();
	assertEquals(3, m.getRowDimension());
	assertEquals(3, m.getColumnDimension());
	assertFalse(GraphUtilities.containsCycle(m));
	
	graph.clear();
	graph.registerBuild(this.a005);
	graph.registerBuild(this.d001);
	// a 0.0.4 needs c 0.0.1 but c exists in version 0.0.2
	// so in this test case, c is not taken in version 0.0.1 and 0.0.2 but only in version 0.0.2
	assertEquals(4, graph.getRegisteredBuilds().size());
	m = graph.createAdjacenceMatrix();
	assertEquals(4, m.getRowDimension());
	assertEquals(4, m.getColumnDimension());
	assertFalse(GraphUtilities.containsCycle(m));
	
	graph.clear();
	graph.registerBuild(this.d002);
	assertEquals(2, graph.getRegisteredBuilds().size());
	m = graph.createAdjacenceMatrix();
	assertEquals(2, m.getRowDimension());
	assertEquals(2, m.getColumnDimension());
	assertFalse(GraphUtilities.containsCycle(m));
	
	graph.clear();
	graph.registerBuild(this.a006);
	assertEquals(3, graph.getRegisteredBuilds().size());
	m = graph.createAdjacenceMatrix();
	assertEquals(3, m.getRowDimension());
	assertEquals(3, m.getColumnDimension());
	assertFalse(GraphUtilities.containsCycle(m));
	
	graph.clear();
	graph.registerBuild(this.a001);
	graph.registerBuild(this.a002);
	graph.registerBuild(this.a003);
	graph.registerBuild(this.a004);
	graph.registerBuild(this.a005);
	graph.registerBuild(this.b001);
	graph.registerBuild(this.b002);
	graph.registerBuild(this.c001);
	graph.registerBuild(this.c002);
	graph.registerBuild(this.d001);
	graph.registerBuild(this.d002);
	// 11 + e 0.0.1 from repository5 needed by d 0.0.2
	assertEquals(12, graph.getRegisteredBuilds().size());
	m = graph.createAdjacenceMatrix();
	assertEquals(12, m.getRowDimension());
	assertEquals(12, m.getColumnDimension());
	assertFalse(GraphUtilities.containsCycle(m));
    }
    
    /* #########################################################################
     * #################### test for repository 6 & 7 ##########################
     * ######################################################################### */
    
    public void testInstallPluginsFromLocalRepository6() throws Exception
    {
	URL url = null;
	
	File f = new File("src" + File.separator +
		     "test" + File.separator +
		     "java" + File.separator +
		     "org" + File.separator +
		     "siberia" + File.separator +
		     "trans" + File.separator +
		     "repositories" + File.separator +
		     "repository6");
	
	try
	{
	    url = f.toURI().toURL();
	}
	catch (MalformedURLException ex)
	{
	    throw new AssertionError("could not get url from " + f);
	}
	
//	this.testInstallPluginsFromRepository6(url);
    }
    
    public void testInstallPluginsFromHttpRepository6() throws Exception
    {
	URL url = new URL("http://alexis.paris.perso.cegetel.net/test_transsiberia/repository6");
	
	this.testInstallPluginsFromRepository6(url);
    }
    
    public void testInstallPluginsFromRepository6(URL url) throws Exception
    {
	if ( ! DO_TEST_6 )
	{
	    return;
	}
	
	TransSiberia trans = this.createTransSiberia(url, "ts-config-test-localrepository6.xml");
	PluginGraph graph = new PluginGraph(trans);
	Matrix m = null;
	
	graph.clear();
	try
	{
	    graph.registerBuild(this.b001);
	    assertFalse(true);
	}
	catch(UnsatisfiedDependencyException e)
	{
	    assertTrue(true);
	}
	catch(Exception e)
	{
	    assertFalse(true);
	}
	
	graph.clear();
	try
	{
	    graph.registerBuild(this.a001);
	    assertFalse(true);
	}
	catch(UnsatisfiedDependencyException e)
	{
	    assertTrue(true);
	}
	catch(Exception e)
	{
	    assertFalse(true);
	}
	
	graph.clear();
	try
	{
	    graph.registerBuild(this.e001);
	    assertFalse(true);
	}
	catch(UnsatisfiedDependencyException e)
	{
	    assertTrue(true);
	}
	catch(Exception e)
	{
	    assertFalse(true);
	}
	
	graph.clear();
	try
	{
	    graph.registerBuild(this.a002);
	    assertFalse(true);
	}
	catch(InvalidBuildDependencyException e)
	{
	    assertTrue(true);
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    assertFalse(true);
	}
    }
    
}
