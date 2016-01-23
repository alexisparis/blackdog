/*
 * PluginClassTest.java
 * JUnit based test
 *
 * Created on 5 septembre 2006, 21:22
 */

package org.siberia;

import junit.framework.*;

/**
 *
 * @author alexis
 */
public class PluginClassTest extends TestCase
{
    
    public PluginClassTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(PluginClassTest.class);
        
        return suite;
    }

    public void testGetPlugin()
    {
        PluginClass instance = null;
     
        instance = new PluginClass("siberia::org.siberia.Action");
        String expResult = "siberia";
        String result = instance.getPlugin();
        assertEquals(expResult, result);
        
        instance = new PluginClass("siberia:org.siberia.Action");
        expResult = null;
        result = instance.getPlugin();
        assertEquals(expResult, result);
        
        instance = new PluginClass("org.siberia.Action");
        expResult = null;
        result = instance.getPlugin();
        assertEquals(expResult, result);
        
        try
        {   instance = new PluginClass("siberia::");
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
        
        try
        {   instance = new PluginClass(null);
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
    }

    public void testGetClassName()
    {   
        PluginClass instance = null;
     
        instance = new PluginClass("siberia::org.siberia.Action");
        String expResult = "org.siberia.Action";
        String result = instance.getClassName();
        assertEquals(expResult, result);
        
        instance = new PluginClass("siberia:org.siberia.Action");
        expResult = "siberia:org.siberia.Action";
        result = instance.getClassName();
        assertEquals(expResult, result);
        
        instance = new PluginClass("org.siberia.Action");
        expResult = "org.siberia.Action";
        result = instance.getClassName();
        assertEquals(expResult, result);
    }
    
}
