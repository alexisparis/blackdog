package org.siberia.parser.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.siberia.parser.ParseException;


/**
 *
 * @author alexis
 */
public class DoubleParserTest extends TestCase
{   
    public DoubleParserTest(String testName)
    {   super(testName); }

    protected void setUp() throws Exception
    {   }

    protected void tearDown() throws Exception
    {   }

    public static Test suite()
    {   TestSuite suite = new TestSuite(DoubleParserTest.class);
        return suite;
    }

    public void testParse() throws Exception
    {
        String value         = null;
        Comparable expResult = null;
        Comparable result    = null;
        DoubleParser instance = new DoubleParser();
        
        value = "1.2";
        expResult = Boolean.TRUE;
        result = instance.parse(value);
        assertEquals(1.2d, result);
        
        value = "5";
        expResult = Boolean.FALSE;
        result = instance.parse(value);
        assertEquals(5d, result);
        
        value = Double.toString(Double.MAX_VALUE);
        expResult = Double.MAX_VALUE;
        result = instance.parse(value);
        assertEquals(expResult, result);
        
        value = "0";
        expResult = Boolean.FALSE;
        result = instance.parse(value);
        assertEquals(0d, result);
        
        value = "-1050";
        expResult = Boolean.FALSE;
        result = instance.parse(value);
        assertEquals(-1050d, result);
        
        try
        {
            value = "";
            expResult = null;
            result = instance.parse(value);
            assertTrue(false);
        }
        catch(ParseException e)
        {   assertTrue(true); }
        
        try
        {
            value = " ";
            expResult = null;
            result = instance.parse(value);
            assertTrue(false);
        }
        catch(ParseException e)
        {   assertTrue(true); }
        
        try
        {
            value = "sdfsdf";
            expResult = null;
            result = instance.parse(value);
            assertTrue(false);
        }
        catch(ParseException e)
        {   assertTrue(true); }
    }
}
