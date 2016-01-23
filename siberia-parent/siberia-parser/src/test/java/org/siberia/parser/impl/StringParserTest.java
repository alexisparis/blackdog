package org.siberia.parser.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 *
 * @author alexis
 */
public class StringParserTest extends TestCase
{   
    public StringParserTest(String testName)
    {   super(testName); }

    protected void setUp() throws Exception
    {   }

    protected void tearDown() throws Exception
    {   }

    public static Test suite()
    {   TestSuite suite = new TestSuite(StringParserTest.class);
        return suite;
    }

    public void testParse() throws Exception
    {
        String value         = null;
        Comparable expResult = null;
        Comparable result    = null;
        StringParser instance = new StringParser();
        
        value = "1";
        expResult = Boolean.TRUE;
        result = instance.parse(value);
        assertEquals(value, result);
        
        value = "ferfefeerg";
        expResult = Boolean.FALSE;
        result = instance.parse(value);
        assertEquals(value, result);
        
        value = Integer.toString(Integer.MAX_VALUE);
        expResult = Integer.MAX_VALUE;
        result = instance.parse(value);
        assertEquals(value, result);
        
        value = "0";
        expResult = Boolean.FALSE;
        result = instance.parse(value);
        assertEquals(value, result);
        
        value = "-1050";
        expResult = Boolean.FALSE;
        result = instance.parse(value);
        assertEquals(value, result);
        
        value = "";
        expResult = null;
        result = instance.parse(value);
        assertEquals(value, result);
        
        value = "-5.2";
        expResult = null;
        result = instance.parse(value);
        assertEquals(value, result);
        
        value = "500000.2";
        expResult = null;
        result = instance.parse(value);
        assertEquals(value, result);
        
        value = " ";
        expResult = null;
        result = instance.parse(value);
        assertEquals(value, result);
        
        value = "sdfsdf";
        expResult = null;
        result = instance.parse(value);
        assertEquals(value, result);
        
        value = Double.toString(Float.MAX_VALUE);
        expResult = null;
        result = instance.parse(value);
        assertEquals(value, result);
    }
}
