package org.siberia.parser.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.siberia.parser.ParseException;


/**
 *
 * @author alexis
 */
public class IntegerParserTest extends TestCase
{   
    public IntegerParserTest(String testName)
    {   super(testName); }

    protected void setUp() throws Exception
    {   }

    protected void tearDown() throws Exception
    {   }

    public static Test suite()
    {   TestSuite suite = new TestSuite(IntegerParserTest.class);
        return suite;
    }

    public void testParse() throws Exception
    {
        String value         = null;
        Comparable expResult = null;
        Comparable result    = null;
        IntegerParser instance = new IntegerParser();
        
        value = "1";
        expResult = Boolean.TRUE;
        result = instance.parse(value);
        assertEquals(1, result);
        
        value = "5";
        expResult = Boolean.FALSE;
        result = instance.parse(value);
        assertEquals(5, result);
        
        value = Integer.toString(Integer.MAX_VALUE);
        expResult = Integer.MAX_VALUE;
        result = instance.parse(value);
        assertEquals(expResult, result);
        
        value = "0";
        expResult = Boolean.FALSE;
        result = instance.parse(value);
        assertEquals(0, result);
        
        value = "-1050";
        expResult = Boolean.FALSE;
        result = instance.parse(value);
        assertEquals(-1050, result);
        
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
            value = "-5.2";
            expResult = null;
            result = instance.parse(value);
            assertTrue(false);
        }
        catch(ParseException e)
        {   assertTrue(true); }
        
        try
        {
            value = "500000.2";
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
            assertNotSame(1, result);
        }
        catch(ParseException e)
        {   assertTrue(true); }
        
        try
        {
            value = Double.toString(Float.MAX_VALUE);
            expResult = null;
            result = instance.parse(value);
            System.out.println("result : " + result);
            assertNotSame(1, result);
        }
        catch(ParseException e)
        {   assertTrue(true); }
    }
}
