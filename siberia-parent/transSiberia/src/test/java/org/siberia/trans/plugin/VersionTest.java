package org.siberia.trans.plugin;

import junit.framework.*;
import org.siberia.trans.type.plugin.Version;
import org.siberia.trans.type.plugin.Version.VersionFormatException;

/**
 *
 * @author alexis
 */
public class VersionTest extends TestCase
{   
    public VersionTest(String testName)
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
        TestSuite suite = new TestSuite(VersionTest.class);
        
        return suite;
    }

    /**
     * Test of location methods of PluginLink
     */
    public void testCreate() throws Exception
    {   Version version = new Version();
        
        assertEquals(0, version.getMajor());
        assertEquals(0, version.getMinor());
        assertEquals(0, version.getRevision());
        
        version = new Version();
        version.setMajor(2);
        
        assertEquals(2, version.getMajor());
        assertEquals(0, version.getMinor());
        assertEquals(0, version.getRevision());
        
        version = new Version();
        version.setMajor(2);
        version.setMinor(5);
        
        assertEquals(2, version.getMajor());
        assertEquals(5, version.getMinor());
        assertEquals(0, version.getRevision());
        
        version = new Version();
        version.setMajor(2);
        version.setMinor(5);
        version.setRevision(258);
        
        assertEquals(2, version.getMajor());
        assertEquals(5, version.getMinor());
        assertEquals(258, version.getRevision());
        
        try
        {   version.setMajor(-1);
            assertTrue(false); 
        }
        catch(IllegalArgumentException e)
        {   assertTrue(true); }
        
        try
        {   version.setMinor(-1);
            assertTrue(false); 
        }
        catch(IllegalArgumentException e)
        {   assertTrue(true); }
        
        try
        {   version.setRevision(-1);
            assertTrue(false); 
        }
        catch(IllegalArgumentException e)
        {   assertTrue(true); }
    }

    /**
     * Test of location methods of PluginLink
     */
    public void testEquals() throws Exception
    {
        Version version1 = new Version();
        version1.setMajor(1);
        version1.setMinor(2);
        version1.setRevision(3);
        
        Version version2 = null;
        assertFalse(version1.equals(version2));
        
        version2 = new Version();
        version2.setMajor(1);
        version2.setMinor(2);
        version2.setRevision(4);
        
        assertFalse(version1.equals(version2));
        
        version2.setRevision(3);
        assertTrue(version1.equals(version2));
        
        version2.setMinor(0);
        assertFalse(version1.equals(version2));
        
        version2.setMinor(2);
        version2.setMajor(4);
        assertFalse(version1.equals(version2));
    }

    /**
     * Test of location methods of PluginLink
     */
    public void testCompare() throws Exception
    {
        Version version1 = new Version();
        version1.setMajor(1);
        version1.setMinor(2);
        version1.setRevision(3);
        
        Version version2 = null;
        assertTrue(version1.compareTo(version2) > 0);
        
        version2 = new Version();
        version2.setMajor(1);
        version2.setMinor(2);
        version2.setRevision(4);
        assertTrue(version1.compareTo(version2) < 0);
        
        version2 = new Version();
        version2.setMajor(1);
        version2.setMinor(3);
        version2.setRevision(0);
        assertTrue(version1.compareTo(version2) < 0);
        
        version2 = new Version();
        version2.setMajor(2);
        version2.setMinor(0);
        version2.setRevision(0);
        assertTrue(version1.compareTo(version2) < 0);
    }

    /**
     * Test of location methods of PluginLink
     */
    public void testParse() throws Exception
    {   Version version = null;
        
        version = Version.parse("2.2.3");
        
        assertEquals(2, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(3, version.getRevision());
        
        version = Version.parse("2.2");
        assertEquals(2, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(0, version.getRevision());
        
        version = Version.parse("2");
        assertEquals(2, version.getMajor());
        assertEquals(0, version.getMinor());
        assertEquals(0, version.getRevision());
        
        version = Version.parse("");
        assertEquals(0, version.getMajor());
        assertEquals(0, version.getMinor());
        assertEquals(0, version.getRevision());
        
        version = Version.parse("2 2 235");
        assertEquals(2, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(235, version.getRevision());
        
        version = Version.parse("2-2 1254");
        assertEquals(2, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(1254, version.getRevision());
        
        version = Version.parse("2_2_1254");
        assertEquals(2, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(1254, version.getRevision());
        
        version = Version.parse("2_2_1254");
        assertEquals(2, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(1254, version.getRevision());
        
        version = Version.parse("2_2___1254");
        assertEquals(2, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(1254, version.getRevision());
        
        version = Version.parse("1 40 98");
        assertEquals(1, version.getMajor());
        assertEquals(40, version.getMinor());
        assertEquals(98, version.getRevision());
        
        version = Version.parse("1  40  98");
        assertEquals(1, version.getMajor());
        assertEquals(40, version.getMinor());
        assertEquals(98, version.getRevision());
        
        version = Version.parse("1      40                                 98");
        assertEquals(1, version.getMajor());
        assertEquals(40, version.getMinor());
        assertEquals(98, version.getRevision());
        
        try
        {   version = Version.parse(null);
            assertTrue(false);
        }
        catch(VersionFormatException e)
        {   assertTrue(true); }
        
        try
        {   version = Version.parse("a.236.6");
            assertTrue(false);
        }
        catch(VersionFormatException e)
        {   assertTrue(true); }
        
        try
        {   version = Version.parse("4_236.6 9");
            assertTrue(false);
        }
        catch(VersionFormatException e)
        {   assertTrue(true); }
    }
    
}
