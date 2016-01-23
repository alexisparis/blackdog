/*
 * PluginResourceTest.java
 * JUnit based test
 *
 * Created on 4 septembre 2006, 21:01
 */

package org.siberia;

import junit.framework.*;

/**
 *
 * @author alexis
 */
public class PluginResourceTest extends TestCase
{
    
    public PluginResourceTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(PluginResourceTest.class);
        
        return suite;
    }

    public void testGetPlugin()
    {
        PluginResource instance = null;
        
        try
        {   instance = new PluginResource("siberia;2::");
            assertFalse(true);
        }
        catch(IllegalArgumentException e)
        {   assertTrue(true); }
        
        try
        {   instance = new PluginResource("siberia;2");
            assertFalse(true);
        }
        catch(IllegalArgumentException e)
        {   assertTrue(true); }
        
        try
        {   instance = new PluginResource("siberia;");
            assertFalse(true);
        }
        catch(IllegalArgumentException e)
        {   assertTrue(true); }
        
        try
        {   instance = new PluginResource("me;nu/main.xml");
            assertFalse(true);
        }
        catch(IllegalArgumentException e)
        {   assertTrue(true); }
        
        try
        {   instance = new PluginResource(";2::menu/main.xml");
            assertFalse(true);
        }
        catch(IllegalArgumentException e)
        {   assertTrue(true); }
        
        instance = new PluginResource("siberia;2::menu/main.xml");
        String expResult = "siberia";
        String result = instance.getPlugin();
        assertEquals(expResult, result);
        
        instance = new PluginResource(" siberia ;2:: menu/main.xml ");
        expResult = "siberia";
        result = instance.getPlugin();
        assertEquals(expResult, result);
        
        instance = new PluginResource(" siberia;2:: menu/main.xml ");
        expResult = "siberia";
        result = instance.getPlugin();
        assertEquals(expResult, result);
        
        instance = new PluginResource("siberia ;2:: menu/main.xml ");
        expResult = "siberia";
        result = instance.getPlugin();
        assertEquals(expResult, result);
        
        instance = new PluginResource("siberia;::menu/main.xml");
        expResult = "siberia";
        result = instance.getPlugin();
        assertEquals(expResult, result);
        
        instance = new PluginResource("siberia::menu/main.xml");
        expResult = "siberia";
        result = instance.getPlugin();
        assertEquals(expResult, result);
        
        instance = new PluginResource("menu/main.xml");
        expResult = null;
        result = instance.getPlugin();
        assertEquals(expResult, result);
    }

    public void testGetRcDirIndex()
    {
        PluginResource instance = null;
        
        instance = new PluginResource("siberia;2::menu/main.xml");
        int expResult = 2;
        int result = instance.getRcDirIndex();
        assertEquals(expResult, result);
        
        instance = new PluginResource("siberia;::menu/main.xml");
        expResult = -1;
        result = instance.getRcDirIndex();
        assertEquals(expResult, result);
        
        instance = new PluginResource("siberia::menu/main.xml");
        expResult = -1;
        result = instance.getRcDirIndex();
        assertEquals(expResult, result);
        
        instance = new PluginResource("menu/main.xml");
        expResult = -1;
        result = instance.getRcDirIndex();
        assertEquals(expResult, result);
    }

    public void testGetPath()
    {
        PluginResource instance = null;
        
        instance = new PluginResource("siberia;2::menu/main.xml");
        String expResult = "menu/main.xml";
        String result = instance.getPath();
        assertEquals(expResult, result);
        
        instance = new PluginResource("siberia;::menu/main.xml");
        expResult = "menu/main.xml";
        result = instance.getPath();
        assertEquals(expResult, result);
        
        instance = new PluginResource(" siberia ;2:: menu/main.xml ");
        expResult = "menu/main.xml";
        result = instance.getPath();
        assertEquals(expResult, result);
        
        instance = new PluginResource(" siberia;2::menu/main.xml ");
        expResult = "menu/main.xml";
        result = instance.getPath();
        assertEquals(expResult, result);
        
        instance = new PluginResource("siberia ;2:: menu/main.xml");
        expResult = "menu/main.xml";
        result = instance.getPath();
        assertEquals(expResult, result);
        
        instance = new PluginResource("siberia::menu/main.xml");
        expResult = "menu/main.xml";
        result = instance.getPath();
        assertEquals(expResult, result);
        
        instance = new PluginResource("menu/main.xml");
        expResult = "menu/main.xml";
        result = instance.getPath();
        assertEquals(expResult, result);
        
        instance = new PluginResource(" menu/main.xml ");
        expResult = "menu/main.xml";
        result = instance.getPath();
        assertEquals(expResult, result);
        
        instance = new PluginResource("menu/main.xml ");
        expResult = "menu/main.xml";
        result = instance.getPath();
        assertEquals(expResult, result);
        
        instance = new PluginResource(" menu/main.xml");
        expResult = "menu/main.xml";
        result = instance.getPath();
        assertEquals(expResult, result);
    }
    
}
