/*
 * PluginRcDeclarationTest.java
 * JUnit based test
 *
 * Created on 4 septembre 2006, 22:59
 */

package org.siberia;

import java.io.File;
import junit.framework.*;
import java.util.Set;

/**
 *
 * @author alexis
 */
public class PluginRcDeclarationTest extends TestCase
{
    
    public PluginRcDeclarationTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(PluginRcDeclarationTest.class);
        
        return suite;
    }

    public void testGetRcDirectoryPath()
    {
        PluginRcDeclaration instance = null;
        
        instance = new PluginRcDeclaration(this.getClass().getClassLoader(), "rcDir1", "rcDir2", "rcDir3");
        String expResult = "rcDir1/";
        String result = instance.getRcDirectoryPath(0);
        assertEquals(expResult, result);
        expResult = "rcDir2/";
        result = instance.getRcDirectoryPath(1);
        assertEquals(expResult, result);
        expResult = "rcDir3/";
        result = instance.getRcDirectoryPath(2);
        assertEquals(expResult, result);
    }

    public void testGetRcDirCount()
    {
        PluginRcDeclaration instance = null;
        
        instance = new PluginRcDeclaration(this.getClass().getClassLoader(), "rcDir1", "rcDir2", "rcDir3");
        int expResult = 3;
        int result = instance.getRcDirCount();
        assertEquals(expResult, result);
        
        try
        {
            instance = new PluginRcDeclaration(this.getClass().getClassLoader());
            expResult = 0;
            result = instance.getRcDirCount();
            assertFalse(false);
        }
        catch(IllegalArgumentException e)
        {   assertTrue(false); }
        
        instance = new PluginRcDeclaration(this.getClass().getClassLoader(), "rcDir");
        expResult = 1;
        result = instance.getRcDirCount();
        assertEquals(expResult, result);
    }

    public void testGetClassLoader()
    {
        PluginRcDeclaration instance = null;
        
        instance = new PluginRcDeclaration(this.getClass().getClassLoader(), "rcDir1", "rcDir1", "rcDir1");
        ClassLoader expResult = this.getClass().getClassLoader();
        ClassLoader result = instance.getClassLoader();
        assertEquals(expResult, result);
    }

    public void testGetPathCandidates()
    {   
        PluginRcDeclaration instance = null;
        
        instance = new PluginRcDeclaration(this.getClass().getClassLoader(), "rcDir1");
        PluginResource resource = new PluginResource("siberia::menu/main.xml");
        Set<String> candidates = instance.getPathCandidates(resource);
        
        assertEquals(1, candidates.size());
        
        assertTrue(candidates.contains("rcDir1/menu/main.xml"));
        
        instance = new PluginRcDeclaration(this.getClass().getClassLoader(), "rcDir1" + File.separator);
        resource = new PluginResource("siberia::menu/main.xml");
        candidates = instance.getPathCandidates(resource);
        
        assertEquals(1, candidates.size());
        
        assertTrue(candidates.contains("rcDir1/menu/main.xml"));
        
        instance = new PluginRcDeclaration(this.getClass().getClassLoader(), "rcDir1", "rcDir2", "rcDir3");
        resource = new PluginResource("menu/main.xml");
        candidates = instance.getPathCandidates(resource);
        
        assertEquals(3, candidates.size());
        
        assertTrue(candidates.contains("rcDir1/menu/main.xml"));
        assertTrue(candidates.contains("rcDir2/menu/main.xml"));
        assertTrue(candidates.contains("rcDir3/menu/main.xml"));
        
        instance = new PluginRcDeclaration(this.getClass().getClassLoader(), "rcDir1", "rcDir2", "rcDir3");
        resource = new PluginResource("siberia;::menu/main.xml");
        candidates = instance.getPathCandidates(resource);
        
        assertEquals(3, candidates.size());
        
        assertTrue(candidates.contains("rcDir1/menu/main.xml"));
        assertTrue(candidates.contains("rcDir2/menu/main.xml"));
        assertTrue(candidates.contains("rcDir3/menu/main.xml"));
        
        instance = new PluginRcDeclaration(this.getClass().getClassLoader(), "rcDir1", "rcDir2", "rcDir3");
        resource = new PluginResource("siberia;1::menu/main.xml");
        candidates = instance.getPathCandidates(resource);
        
        assertEquals(1, candidates.size());
        
        assertTrue(candidates.contains("rcDir1/menu/main.xml"));
    }
    
}
