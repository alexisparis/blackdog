/*
 * PropertiesProviderTest.java
 * JUnit based test
 *
 * Created on 6 septembre 2006, 22:30
 */

package org.siberia.properties.util;

import junit.framework.*;
import java.io.File;
import java.io.FileInputStream;
import org.siberia.parser.ParserRegistry;
import org.siberia.xml.JAXBPropertiesLoader;
import org.siberia.xml.schema.properties.Properties;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBException;

/**
 *
 * @author alexis
 */
public class PropertiesProviderTest extends TestCase
{
    
    public PropertiesProviderTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(PropertiesProviderTest.class);
        
        return suite;
    }

    public void testGetParserRegistry()
    {
//        System.out.println("getParserRegistry");
//        
//        ParserRegistry expResult = null;
//        ParserRegistry result = PropertiesProvider.getParserRegistry();
//        assertEquals(expResult, result);
//        
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    
    public void testGetProperty()
    {
        String dir = "src" + File.separator + "test" + File.separator + "java" + File.separator + 
                     "org" + File.separator + "siberia" + File.separator + "rc" + File.separator;
        Properties properties = null;
        JAXBPropertiesLoader jaxb = new JAXBPropertiesLoader();
        try
        {   properties = jaxb.loadProperties(new FileInputStream(dir + "properties.xml")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        Set<Properties> set = new HashSet<Properties>(1);
        set.add(properties);
        
        properties = new PropertiesMerger(set).merge();
        
        /* save it in tmp directory */
        File tempFile = null;
        try
        {   tempFile = File.createTempFile("properties", ".xml"); }
        catch(Exception e)
        {   assertTrue(false); }
        
        try
        {
            jaxb.saveProperties(properties, tempFile);
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        InputStream stream = null;
        try
        {   stream = new FileInputStream(tempFile); }
        catch(Exception e)
        {   assertTrue(false); } 
        
        Object o = null;
        
        o = PropertiesProvider.getProperty(stream, "test_string");
        
        assertTrue(o instanceof String);
        assertEquals("chainedecaractere", o);
        
        try
        {   stream.close();
            stream = new FileInputStream(tempFile); 
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        o = PropertiesProvider.getProperty(stream, "test_boolean");
        
        assertTrue(o instanceof Boolean);
        assertEquals(Boolean.TRUE, o);
        
        try
        {   stream.close();
            stream = new FileInputStream(tempFile); 
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        o = PropertiesProvider.getProperty(stream, "test_integer");
        
        assertTrue(o instanceof Integer);
        assertEquals(800, o);
        
        try
        {   stream.close();
            stream = new FileInputStream(tempFile); 
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        o = PropertiesProvider.getProperty(stream, "test_float");
        
        assertTrue(o instanceof Float);
        assertEquals(6.5f, o);
        
        try
        {   stream.close();
            stream = new FileInputStream(tempFile); 
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        o = PropertiesProvider.getProperty(stream, "test_double");
        
        assertTrue(o instanceof Double);
        assertEquals(90000.25632d, o);        
        
        try
        {   stream.close();
            stream = new FileInputStream(tempFile); 
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        o = PropertiesProvider.getProperty(stream, "blablabla");
        
        assertNull(o);
        
        try
        {   stream.close(); }
        catch(Exception e)
        {   assertTrue(false); }
        
        tempFile.delete();   
    }

    public void testGetProperties()
    {
        String dir = "src" + File.separator + "test" + File.separator + "java" + File.separator + 
                     "org" + File.separator + "siberia" + File.separator + "rc" + File.separator;
        Properties properties = null;
        JAXBPropertiesLoader jaxb = new JAXBPropertiesLoader();
        try
        {   properties = jaxb.loadProperties(new FileInputStream(dir + "properties.xml")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        Set<Properties> set = new HashSet<Properties>(1);
        set.add(properties);
        
        properties = new PropertiesMerger(set).merge();
        
        /* save it in tmp directory */
        File tempFile = null;
        try
        {   tempFile = File.createTempFile("properties", ".xml"); }
        catch(Exception e)
        {   assertTrue(false); }
        
        try
        {
            jaxb.saveProperties(properties, tempFile);
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        InputStream stream = null;
        try
        {   stream = new FileInputStream(tempFile); }
        catch(Exception e)
        {   assertTrue(false); } 
        
        Object[] objects = null;
        
        objects = PropertiesProvider.getProperties(stream, "test_string", "test_boolean", "test_float", "test_integer");
        
        assertEquals(4, objects.length);
        assertTrue(objects[0] instanceof String);
        assertEquals("chainedecaractere", objects[0]);
        assertTrue(objects[1] instanceof Boolean);
        assertEquals(Boolean.TRUE, objects[1]);
        assertTrue(objects[3] instanceof Integer);
        assertEquals(800, objects[3]);
        assertTrue(objects[2] instanceof Float);
        assertEquals(6.5f, objects[2]);
        
        try
        {   stream.close();
            stream = new FileInputStream(tempFile); 
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        objects = PropertiesProvider.getProperties(stream, "test_boolean", "test_integer", "test_string", "test_float");
        
        assertEquals(4, objects.length);
        assertTrue(objects[2] instanceof String);
        assertEquals("chainedecaractere", objects[2]);
        assertTrue(objects[0] instanceof Boolean);
        assertEquals(Boolean.TRUE, objects[0]);
        assertTrue(objects[1] instanceof Integer);
        assertEquals(800, objects[1]);
        assertTrue(objects[3] instanceof Float);
        assertEquals(6.5f, objects[3]);
        
        try
        {   stream.close();
            stream = new FileInputStream(tempFile); 
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        objects = PropertiesProvider.getProperties(stream, "blablabla", "test_integer", "test_string", "test_float");
        
        assertEquals(4, objects.length);
        assertTrue(objects[2] instanceof String);
        assertEquals("chainedecaractere", objects[2]);
        assertNull(objects[0]);
        assertTrue(objects[1] instanceof Integer);
        assertEquals(800, objects[1]);
        assertTrue(objects[3] instanceof Float);
        assertEquals(6.5f, objects[3]);
        
        try
        {   stream.close();
            stream = new FileInputStream(tempFile); 
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        objects = PropertiesProvider.getProperties(stream, "test_float", "test_integer", "test_string", "blablabla");
        
        assertEquals(4, objects.length);
        assertTrue(objects[2] instanceof String);
        assertEquals("chainedecaractere", objects[2]);
        assertNull(objects[3]);
        assertTrue(objects[1] instanceof Integer);
        assertEquals(800, objects[1]);
        assertTrue(objects[0] instanceof Float);
        assertEquals(6.5f, objects[0]);
        
        try
        {   stream.close();
            stream = new FileInputStream(tempFile); 
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        objects = PropertiesProvider.getProperties(stream, "blablabla", "bliblibli", "blobloblo", "blublublu");
        
        assertEquals(4, objects.length);
        assertNull(objects[0]);
        assertNull(objects[1]);
        assertNull(objects[2]);
        assertNull(objects[3]);
        
        try
        {   stream.close(); }
        catch(Exception e)
        {   assertTrue(false); }
        
        tempFile.delete(); 
    }

    public void testSetProperty()
    {
        
        String file = "src" + File.separator + "test" + File.separator + "java" + File.separator + 
                     "org" + File.separator + "siberia" + File.separator + "rc" + File.separator + "general_properties.xml";
        
        assertTrue(PropertiesProvider.setProperty(file, "test_string", "value"));
        
        try
        {   assertEquals("value", PropertiesProvider.getProperty(new FileInputStream(file), "test_string")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperty(file, "test_double", 852));
        
        try
        {   assertEquals(852.00, PropertiesProvider.getProperty(new FileInputStream(file), "test_double")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperty(file, "test_double", 853f));
        
        try
        {   assertEquals(853.00, PropertiesProvider.getProperty(new FileInputStream(file), "test_double")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperty(file, "test_double", 854d));
        
        try
        {   assertEquals(854.00, PropertiesProvider.getProperty(new FileInputStream(file), "test_double")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertFalse(PropertiesProvider.setProperty(file, "test_double", 1601));
        
        try
        {   assertEquals(854.00, PropertiesProvider.getProperty(new FileInputStream(file), "test_double")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperty(file, "test_integer", 815));
        
        try
        {   assertEquals(815, PropertiesProvider.getProperty(new FileInputStream(file), "test_integer")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperty(file, "test_integer", 417));
        
        try
        {   assertEquals(417, PropertiesProvider.getProperty(new FileInputStream(file), "test_integer")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertFalse(PropertiesProvider.setProperty(file, "test_integer", -417));
        
        try
        {   assertEquals(417, PropertiesProvider.getProperty(new FileInputStream(file), "test_integer")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperty(file, "test_boolean", Boolean.TRUE));
        
        try
        {   assertEquals(Boolean.TRUE, PropertiesProvider.getProperty(new FileInputStream(file), "test_boolean")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperty(file, "test_boolean", Boolean.FALSE));
        
        try
        {   assertEquals(Boolean.FALSE, PropertiesProvider.getProperty(new FileInputStream(file), "test_boolean")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperty(file, "test_boolean", true));
        assertTrue(PropertiesProvider.setProperty(file, "test_boolean", false));
        
        assertFalse(PropertiesProvider.setProperty(file, "test_boolean", "jkhfjkh"));
        
        try
        {   assertEquals(Boolean.FALSE, PropertiesProvider.getProperty(new FileInputStream(file), "test_boolean")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperty(file, "test_float", 45));
        
        try
        {   assertEquals(45f, PropertiesProvider.getProperty(new FileInputStream(file), "test_float")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperty(file, "test_float", 450));
        
        try
        {   assertEquals(450f, PropertiesProvider.getProperty(new FileInputStream(file), "test_float")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertFalse(PropertiesProvider.setProperty(file, "test_float", 800));
        
        try
        {   assertEquals(450f, PropertiesProvider.getProperty(new FileInputStream(file), "test_float")); }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperty(file, "test_float", 925));
        
        try
        {   assertEquals(925f, PropertiesProvider.getProperty(new FileInputStream(file), "test_float")); }
        catch(Exception e)
        {   assertTrue(false); }
    }

    public void testSetProperties()
    {
        
        String file = "src" + File.separator + "test" + File.separator + "java" + File.separator + 
                     "org" + File.separator + "siberia" + File.separator + "rc" + File.separator + "general_properties.xml";
        
        assertTrue(PropertiesProvider.setProperties(file,
                                                    new String[]{"test_string", "test_integer"},
                                                    new Object[]{"value", 412}));
        
        try
        {   Object[] os = PropertiesProvider.getProperties(new FileInputStream(file), "test_string", "test_integer");
            assertEquals("value", os[0]);
            assertEquals(412, os[1]);
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperties(file,
                                                    new String[]{"test_string", "test_integer"},
                                                    new Object[]{"value_2", 414}));
        
        try
        {   Object[] os = PropertiesProvider.getProperties(new FileInputStream(file), "test_string", "test_integer");
            assertEquals("value_2", os[0]);
            assertEquals(414, os[1]);
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertFalse(PropertiesProvider.setProperties(file,
                                                    new String[]{"test_string", "test_integer"},
                                                    new Object[]{"value_3", 4140}));
        
        try
        {   Object[] os = PropertiesProvider.getProperties(new FileInputStream(file), "test_string", "test_integer");
            assertEquals("value_2", os[0]);
            assertEquals(414, os[1]);
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertFalse(PropertiesProvider.setProperties(file,
                                                    new String[]{"test_string", "test_integer"},
                                                    new Object[]{"value_3", 4140}, false));
        
        try
        {   Object[] os = PropertiesProvider.getProperties(new FileInputStream(file), "test_string", "test_integer");
            assertEquals("value_3", os[0]);
            assertEquals(414, os[1]);
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperties(file,
                                                    new String[]{"test_string", "test_integer", "test_float"},
                                                    new Object[]{"value_4", 414, 415}, true));
        
        try
        {   Object[] os = PropertiesProvider.getProperties(new FileInputStream(file), "test_string", "test_integer", "test_float");
            assertEquals("value_4", os[0]);
            assertEquals(414, os[1]);
            assertEquals(415f, os[2]);
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertFalse(PropertiesProvider.setProperties(file,
                                                    new String[]{"test_string", "test_integer", "test_float"},
                                                    new Object[]{"value_5", 484, 850}, true));
        
        try
        {   Object[] os = PropertiesProvider.getProperties(new FileInputStream(file), "test_string", "test_integer", "test_float");
            assertEquals("value_4", os[0]);
            assertEquals(414, os[1]);
            assertEquals(415f, os[2]);
        }
        catch(Exception e)
        {   assertTrue(false); }
        
        assertTrue(PropertiesProvider.setProperties(file,
                                                    new String[]{"test_string", "test_integer", "test_float"},
                                                    new Object[]{"value_5", 484, 925}, true));
        
        try
        {   Object[] os = PropertiesProvider.getProperties(new FileInputStream(file), "test_string", "test_integer", "test_float");
            assertEquals("value_5", os[0]);
            assertEquals(484, os[1]);
            assertEquals(925f, os[2]);
        }
        catch(Exception e)
        {   assertTrue(false); }
    }
    
}
