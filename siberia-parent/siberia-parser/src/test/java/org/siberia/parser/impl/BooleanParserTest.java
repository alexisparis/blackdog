package org.siberia.parser.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.*;
import org.siberia.parser.ParseException;
import org.siberia.parser.Parser;


/**
 *
 * @author alexis
 */
public class BooleanParserTest extends TestCase
{   
    public BooleanParserTest(String testName)
    {   super(testName); }

    protected void setUp() throws Exception
    {   }

    protected void tearDown() throws Exception
    {   }

    public static Test suite()
    {   TestSuite suite = new TestSuite(BooleanParserTest.class);
        return suite;
    }

    public void testParse() throws Exception
    {
        String value         = null;
        Comparable expResult = null;
        Comparable result    = null;
        BooleanParser instance = new BooleanParser();
        
        value = "true";
        expResult = Boolean.TRUE;
        result = instance.parse(value);
        assertEquals(expResult, result);
        
        value = "false";
        expResult = Boolean.FALSE;
        result = instance.parse(value);
        assertEquals(expResult, result);
        
        value = "TRUE";
        expResult = Boolean.TRUE;
        result = instance.parse(value);
        assertEquals(expResult, result);
        
        value = "FALSE";
        expResult = Boolean.FALSE;
        result = instance.parse(value);
        assertEquals(expResult, result);
        
        try
        {
            value = "";
            expResult = Boolean.FALSE;
            result = instance.parse(value);
            assertTrue(false);
        }
        catch(ParseException e)
        {   assertTrue(true); }
        
        try
        {
            value = " ";
            expResult = Boolean.FALSE;
            result = instance.parse(value);
            assertTrue(false);
        }
        catch(ParseException e)
        {   assertTrue(true); }
        
        try
        {
            value = "sdfsdf";
            expResult = Boolean.FALSE;
            result = instance.parse(value);
            assertTrue(false);
        }
        catch(ParseException e)
        {   assertTrue(true); }
    }
}
